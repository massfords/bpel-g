package org.activebpel.services.jaxws;

import bpelg.services.deploy.AeDeployer;
import bpelg.services.deploy.DeploymentService;
import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.DeploymentResponse.DeploymentInfo;
import bpelg.services.preferences.AePreferences;
import bpelg.services.preferences.PreferencesService;
import bpelg.services.processes.AeProcessManager;
import bpelg.services.processes.ProcessManagerService;
import bpelg.services.urnresolver.AeURNResolver;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.*;
import org.junit.Assert;
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
import java.util.List;

public class AeProcessFixture extends Assert {
    private AeXMLParserBase parser = new AeXMLParserBase();
    private AeDeployer deployer;
    private AeURNResolver resolver;
	private AeProcessManager processManager;
    private AePreferences preferencesService;

    public AePreferences getPreferencesService() throws Exception {
        if (preferencesService == null) {
            String catalina_port = getCatalinaPort();
            preferencesService = new PreferencesService(new URL(
                    "http://localhost:" + catalina_port + "/bpel-g/cxf/PreferencesService?wsdl")).getPort(AePreferences.class);
        }
        return preferencesService;
    }

    public AeURNResolver getResolver() throws MalformedURLException {
		if (resolver == null) {
	    	String catalina_port = getCatalinaPort();
	    	URL url = new URL(
					"http://localhost:" + catalina_port + "/bpel-g/cxf/URNResolver?wsdl");
			Service svc = Service.create(url, new QName("urn:bpel-g:services:urn-resolver", "URNResolver"));
			resolver = svc.getPort(AeURNResolver.class);
		}
		return resolver;
	}
	
	public String getCatalinaPort() {
    	String catalina_port = System.getProperty("CATALINA_PORT", "8080");
    	return catalina_port;
	}
	
	public AeDeployer getDeployer() throws Exception {
		if (deployer == null) {
	    	String catalina_port = getCatalinaPort();
			DeploymentService ds = new DeploymentService(new URL(
					"http://localhost:" + catalina_port + "/bpel-g/cxf/DeploymentService?wsdl"));
			deployer = ds.getPort(AeDeployer.class);
		}
		return deployer;
	}
	
	public AeProcessManager getProcessManager() throws Exception {
		if (processManager == null) {
	    	String catalina_port = getCatalinaPort();
			processManager = new ProcessManagerService(new URL(
					"http://localhost:" + catalina_port + "/bpel-g/cxf/ProcessManagerService?wsdl")).getPort(AeProcessManager.class);
		}
		return processManager;
	}
	
	public Document invoke(File file, String endpoint) throws Exception {
        Source request = new DOMSource(parser.loadDocument(new FileInputStream(file), null));
		return invoke(request, endpoint);
	}

    public Document invoke(Source request, String endpoint) throws Exception {
        return invoke(request, createDispatch(endpoint));
    }

    public Document invoke(Source request, Dispatch<Source> dispatch) throws Exception {
		Source response = dispatch.invoke(request);
		Node actualNode = toNode(response);
		return (Document) actualNode;
	}
	
	public Dispatch<Source> createDispatch(String endpoint) {
		String ns = "urn:bpel-g:generic";
		QName serviceName = new QName(ns, "DOCLitService");
		Service service = Service.create(serviceName);

		QName portName = new QName(ns, "DOCLitPortType");
		service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING, endpoint);
		Dispatch<Source> dispatch = service.createDispatch(portName,
				Source.class, Service.Mode.PAYLOAD);
		return dispatch;
	}
	

	public DeploymentResponse deploySingle(File file) throws Exception {
		DeploymentResponse resp = deployAll(file);
        List<DeploymentInfo> infos = resp.getDeploymentInfo();
        try {
            assertEquals(1, infos.size());
        } finally {
            List<DeploymentInfo> deploymentInfo = resp.getDeploymentInfo();
            if (!deploymentInfo.isEmpty() && deploymentInfo.get(0).getNumberOfErrors() != 0) {
                System.out.println(resp.getDeploymentInfo().get(0).getLog());
            }
        }
        return resp;
	}

	public DeploymentResponse deployAll(File file) throws Exception {
		DeploymentResponse response = deploy(file);
		for(DeploymentInfo info : response.getDeploymentInfo()) {
			assertTrue(info.isDeployed());
			assertEquals(0, info.getNumberOfErrors());
		}
		return response;
	}

	public DeploymentResponse deploy(File file) throws Exception {
		byte[] raw = IOUtils.toByteArray(new FileInputStream(file));
		DeploymentResponse response = getDeployer().deploy(file.getName(), raw);
		return response;
	}

	public Node toNode(Source expected) throws TransformerException {
		Node expectedNode;
		if (expected instanceof DOMSource) {
			expectedNode = ((DOMSource) expected).getNode();
		} else {
			DOMResult result = new DOMResult();
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.newTransformer().transform(expected, result);
			expectedNode = result.getNode();
		}
		return expectedNode;
	}

	public void assertXMLIgnorePrefix(String message, Node expected,
			Node actual) throws Exception {
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);

		Document expectedDoc = toDocument(expected);
		Document actualDoc = toDocument(actual);
		Diff diff = new Diff(expectedDoc, actualDoc);
		diff.overrideDifferenceListener(new DifferenceListener() {
			public int differenceFound(Difference aDifference) {
				if (aDifference.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID)
					return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
				return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
			}

			public void skippedComparison(Node aControl, Node aTest) {
			}
		});
		XMLAssert.assertXMLEqual(message, diff, true);
	}

	/**
	 * Convenience method that converts an Element into a Document or returns
	 * the arg passed if already a Document
	 * 
	 * @param docOrElement
	 * @throws ParserConfigurationException
	 */
	public static Document toDocument(Node docOrElement) throws Exception {
		Document doc = null;
		if (docOrElement instanceof Element) {
			doc = AeXmlUtil.newDocument();
			Node node = doc.importNode(docOrElement, true);
			doc.appendChild(node);
		} else if (docOrElement instanceof Document)
			doc = (Document) docOrElement;
		else
			throw new IllegalArgumentException("expected a Document or Element");
		return doc;
	}

}
