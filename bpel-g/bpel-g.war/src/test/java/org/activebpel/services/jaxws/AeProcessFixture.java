package org.activebpel.services.jaxws;

import bpelg.services.deploy.AeDeployer;
import bpelg.services.deploy.DeploymentService;
import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.DeploymentResponse.DeploymentInfo;
import bpelg.services.processes.AeProcessManager;
import bpelg.services.processes.ProcessManagerService;
import bpelg.services.urnresolver.AeURNResolver;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AeProcessFixture {
	AeXMLParserBase mParser = new AeXMLParserBase();
	AeDeployer mDeployer;
	AeURNResolver mResolver;
	AeProcessManager mProcessManager;

	protected AeURNResolver getResolver() throws MalformedURLException {
		if (mResolver == null) {
	    	String catalina_port = getCatalinaPort();
	    	URL url = new URL(
					"http://localhost:" + catalina_port + "/bpel-g/cxf/URNResolver?wsdl");
			Service svc = Service.create(url, new QName("urn:bpel-g:services:urn-resolver", "URNResolver"));
			mResolver = svc.getPort(AeURNResolver.class);
		}
		return mResolver;
	}
	
	protected String getCatalinaPort() {
    	String catalina_port = System.getProperty("CATALINA_PORT", "8080");
    	return catalina_port;
	}
	
	protected AeDeployer getDeployer() throws Exception {
		if (mDeployer == null) {
	    	String catalina_port = getCatalinaPort();
			DeploymentService ds = new DeploymentService(new URL(
					"http://localhost:" + catalina_port + "/bpel-g/cxf/DeploymentService?wsdl"));
			mDeployer = ds.getPort(AeDeployer.class);
		}
		return mDeployer;
	}
	
	protected AeProcessManager getProcessManager() throws Exception {
		if (mProcessManager == null) {
	    	String catalina_port = getCatalinaPort();
			mProcessManager = new ProcessManagerService(new URL(
					"http://localhost:" + catalina_port + "/bpel-g/cxf/ProcessManagerService?wsdl")).getPort(AeProcessManager.class);
		}
		return mProcessManager;
	}
	
	protected Document invoke(File aFile, String aEndpoint) throws Exception {
        Source request = new DOMSource(mParser.loadDocument(new FileInputStream(aFile), null));
		return invoke(request, aEndpoint);
	}

    protected Document invoke(Source request, String aEndpoint) throws Exception {
        return invoke(request, createDispatch(aEndpoint));
    }

    protected Document invoke(Source request, Dispatch<Source> aDispatch) throws Exception {
		Source response = aDispatch.invoke(request);
		Node actualNode = toNode(response);
		return (Document) actualNode;
	}
	
	protected Dispatch<Source> createDispatch(String aEndpoint) {
		String ns = "urn:bpel-g:generic";
		QName serviceName = new QName(ns, "DOCLitService");
		Service service = Service.create(serviceName);

		QName portName = new QName(ns, "DOCLitPortType");
		service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, aEndpoint);
		Dispatch<Source> dispatch = service.createDispatch(portName,
				Source.class, Service.Mode.PAYLOAD);
		return dispatch;
	}
	

	protected void deploySingle(File file) throws Exception {
		DeploymentResponse resp = deployAll(file);
		assertEquals(1, resp.getDeploymentInfo().size());
	}

	protected DeploymentResponse deployAll(File file) throws Exception {
		DeploymentResponse response = deploy(file);
		for(DeploymentInfo info : response.getDeploymentInfo()) {
			assertTrue(info.isDeployed());
			assertEquals(0, info.getNumberOfErrors());
		}
		return response;
	}

	protected DeploymentResponse deploy(File file) throws Exception {
		byte[] raw = IOUtils.toByteArray(new FileInputStream(file));
		DeploymentResponse response = getDeployer().deploy(file.getName(), raw);
		return response;
	}

	public Node toNode(Source aExpected) throws TransformerException {
		Node expectedNode;
		if (aExpected instanceof DOMSource) {
			expectedNode = ((DOMSource) aExpected).getNode();
		} else {
			DOMResult result = new DOMResult();
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.newTransformer().transform(aExpected, result);
			expectedNode = result.getNode();
		}
		return expectedNode;
	}

	public void assertXMLIgnorePrefix(String aMessage, Node aExpected,
			Node aActual) throws Exception {
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);

		Document expected = toDocument(aExpected);
		Document actual = toDocument(aActual);
		Diff diff = new Diff(expected, actual);
		diff.overrideDifferenceListener(new DifferenceListener() {
			public int differenceFound(Difference aDifference) {
				if (aDifference.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID)
					return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
				return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
			}

			public void skippedComparison(Node aControl, Node aTest) {
			}
		});
		XMLAssert.assertXMLEqual(diff, true);
	}

	/**
	 * Convenience method that converts an Element into a Document or returns
	 * the arg passed if already a Document
	 * 
	 * @param aDocOrElement
	 * @throws ParserConfigurationException
	 */
	public static Document toDocument(Node aDocOrElement) throws Exception {
		Document doc = null;
		if (aDocOrElement instanceof Element) {
			doc = AeXmlUtil.newDocument();
			Node node = doc.importNode(aDocOrElement, true);
			doc.appendChild(node);
		} else if (aDocOrElement instanceof Document)
			doc = (Document) aDocOrElement;
		else
			throw new IllegalArgumentException("expected a Document or Element");
		return doc;
	}

}
