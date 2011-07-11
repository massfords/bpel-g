package org.activebpel.services.jaxws;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.transform.stream.StreamSource;

import org.activebpel.rt.xml.AeXMLParserBase;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import bpelg.services.deploy.types.UndeploymentRequest;
import bpelg.services.urnresolver.types.AddMappingRequest;

public class AeDeploymentServiceIntegrationTest {

	AeProcessFixture pfix = new AeProcessFixture();

	@Before
	public void setUp() throws Exception {
		pfix.getResolver().addMapping(new AddMappingRequest().withName(
				"urn:x-vos:loancompany").withValue(
				"http://localhost:" + pfix.getCatalinaPort() + "/bpel-g/services/${urn.4}"));
	}

	@Test
	public void deployBPR() throws Exception {
		pfix.deploySingle(new File("src/test/resources/loanProcessCompleted.bpr"));
		pfix.deploySingle(new File("src/test/resources/loanApproval.bpr"));
		pfix.deploySingle(new File("src/test/resources/riskAssessment.bpr"));

		// run a simple test
		Document response = pfix.invoke(new File("src/test/resources/credInfo_Smith15001.xml"), 
				"http://localhost:8080/bpel-g/services/LoanProcessCompletedService");
		Document expectedNode = (Document) pfix.toNode(new StreamSource(new FileInputStream(
				"src/test/resources/expected-response.xml")));

		System.out.println(AeXMLParserBase.documentToString(response));

		pfix.assertXMLIgnorePrefix("failed to match", expectedNode, response);
		
		// some assertions for our filters
		
		// undeploy
		pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("loanProcessCompleted.bpr"));
		pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("loanApproval.bpr"));
		pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("riskAssessment.bpr"));
	}

//	@Test
	public void deployODE() throws Exception {
		pfix.deployAll(new File("src/test/resources/ode-project.zip"));
	}
}
