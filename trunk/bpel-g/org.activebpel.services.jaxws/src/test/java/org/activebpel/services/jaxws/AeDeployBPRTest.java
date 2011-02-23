package org.activebpel.services.jaxws;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
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

import org.activebpel.rt.base64.BASE64Encoder;
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
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import bpelg.services.admin.IAeAxisActiveBpelAdmin;
import bpelg.services.admin.types.AesDeployBprType;
import bpelg.services.admin.types.AesStringResponseType;

@Ignore
public class AeDeployBPRTest {

	AeXMLParserBase parser = new AeXMLParserBase();
	
	@Before
	public void setUp() throws Exception {
	    
	}

	@Test
	public void deployBPR() throws Exception {
		deploy("loanProcessCompleted.bpr");
		deploy("loanApproval.bpr");
		deploy("riskAssessment.bpr");

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
	}

	@Test
	public void deployODE() throws Exception {
		AesStringResponseType response = deploy("ode-project.zip");
		System.out.println(response.getResponse());
	}

	protected AesStringResponseType deploy(String aName)
			throws MalformedURLException, IOException, FileNotFoundException {
		Service service = Service
				.create(new URL(
						"http://localhost:8080/bpel-g/services/ActiveBpelAdmin"),
						new QName(
								"http://docs.active-endpoints/wsdl/activebpeladmin/2007/01/activebpeladmin.wsdl",
								"ActiveBpelAdmin"));

		byte[] raw = IOUtils.toByteArray(new FileInputStream(
				"src/test/resources/" + aName));
		String filedata = new BASE64Encoder().encode(raw);

		IAeAxisActiveBpelAdmin activeBpelAdmin = service
				.getPort(
						new QName(
								"http://docs.active-endpoints/wsdl/activebpeladmin/2007/01/activebpeladmin.wsdl",
								"ActiveBpelAdminPort"),
						IAeAxisActiveBpelAdmin.class);
		AesDeployBprType withBase64File = new AesDeployBprType()
				.withBprFilename(aName).withBase64File(filedata);
		AesStringResponseType response = activeBpelAdmin
				.deployBpr(withBase64File);
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
	public static Document toDocument(Node aDocOrElement)
			throws Exception {
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
