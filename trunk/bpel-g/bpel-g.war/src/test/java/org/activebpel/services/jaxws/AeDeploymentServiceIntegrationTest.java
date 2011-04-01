package org.activebpel.services.jaxws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import bpelg.services.deploy.AeDeployer;
import bpelg.services.deploy.DeploymentService;
import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.DeploymentResponse.DeploymentInfo;
import bpelg.services.deploy.types.UndeploymentRequest;
import bpelg.services.urnresolver.AeURNResolver;
import bpelg.services.urnresolver.types.AddMappingRequest;

public class AeDeploymentServiceIntegrationTest {

	AeXMLParserBase parser = new AeXMLParserBase();
	AeDeployer deployer;

	@Before
	public void setUp() throws Exception {
    	String catalina_port = System.getProperty("CATALINA_PORT", "8080");
    	URL url = new URL(
				"http://localhost:" + catalina_port + "/bpel-g/cxf/URNResolver?wsdl");
		Service svc = Service.create(url, new QName("urn:bpel-g:services:urn-resolver", "URNResolver"));
		AeURNResolver resolver = svc.getPort(AeURNResolver.class);
		resolver.addMapping(new AddMappingRequest().withName(
				"urn:x-vos:loancompany").withValue(
				"http://localhost:" + catalina_port + "/bpel-g/services/${urn.4}"));

		DeploymentService ds = new DeploymentService(new URL(
				"http://localhost:" + catalina_port + "/bpel-g/cxf/DeploymentService?wsdl"));
		deployer = ds.getPort(AeDeployer.class);
	}

	@Test
	public void deployBPR() throws Exception {
		deploySingle("loanProcessCompleted.bpr");
		deploySingle("loanApproval.bpr");
		deploySingle("riskAssessment.bpr");

		// run a simple test
		String ns = "urn:bpel-g:generic";
		QName serviceName = new QName(ns, "DOCLitService");
		Service service = Service.create(serviceName);

		QName portName = new QName(ns, "DOCLitPortType");
		service.addPort(portName, SOAPBinding.SOAP11HTTP_BINDING,
				"http://localhost:8080/bpel-g/services/LoanProcessCompletedService");
		Dispatch<Source> dispatch = service.createDispatch(portName,
				Source.class, Service.Mode.PAYLOAD);
		Source request = new DOMSource(parser.loadDocument(new FileInputStream(
				"src/test/resources/credInfo_Smith15001.xml"), null));
		Source response = dispatch.invoke(request);
		Node expectedNode = toNode(new StreamSource(new FileInputStream(
				"src/test/resources/expected-response.xml")));
		Node actualNode = toNode(response);

		System.out.println(AeXMLParserBase.documentToString(actualNode));

		assertXMLIgnorePrefix("failed to match", expectedNode, actualNode);
		
		// undeploy
		deployer.undeploy(new UndeploymentRequest().withDeploymentContainerId("loanProcessCompleted.bpr"));
		deployer.undeploy(new UndeploymentRequest().withDeploymentContainerId("loanApproval.bpr"));
		deployer.undeploy(new UndeploymentRequest().withDeploymentContainerId("riskAssessment.bpr"));
	}

	protected void deploySingle(String name) throws Exception {
		DeploymentResponse resp = deployAll(name);
		assertEquals(1, resp.getDeploymentInfo().size());
	}

	@Test
	public void deployODE() throws Exception {
		deployAll("ode-project.zip");
	}

	protected DeploymentResponse deployAll(String name) throws Exception {
		DeploymentResponse response = deploy(name);
		for(DeploymentInfo info : response.getDeploymentInfo()) {
			assertTrue(info.isDeployed());
			assertEquals(0, info.getNumberOfErrors());
		}
		return response;
	}

	protected DeploymentResponse deploy(String aName) throws Exception {
		byte[] raw = IOUtils.toByteArray(new FileInputStream(
				"src/test/resources/" + aName));
		DeploymentResponse response = deployer.deploy(aName, raw);
		return response;
	}

	public static Node toNode(Source aExpected) throws TransformerException {
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

	public static void assertXMLIgnorePrefix(String aMessage, Node aExpected,
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
