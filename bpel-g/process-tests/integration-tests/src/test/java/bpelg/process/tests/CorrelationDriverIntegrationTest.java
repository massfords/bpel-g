package bpelg.process.tests;

import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.UndeploymentRequest;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.util.Collections;

/**
 * @author markford
 *         Date: 4/25/12
 */
public class CorrelationDriverIntegrationTest extends Assert {
    private static final AeProcessFixture pfix = new AeProcessFixture();

    @BeforeClass
    public static void deployProcess() throws Exception {
        // deploy
        DeploymentResponse response = pfix.deployAll(new File("target/dependency/correlation-test.jar"));
        DeploymentResponse.DeploymentInfo info = response.getDeploymentInfo().get(0);
        assertNotNull(info.getLog());
        System.out.println(info.getLog());
    }

    @AfterClass
    public static void undeployProcess() throws Exception {
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("correlation-test.jar"));
    }

    @Test
    public void test() throws Exception {

        String request =
                "<requestForQuote xmlns='http://www.example.org/correlation/'>" +
                        "    <customerId>junit</customerId>" +
                        "    <productId>100</productId>" +
                        "    <quantity>5</quantity>" +
                        "</requestForQuote>";

        // invoke the service and get our quote back
        Document confirmation = pfix.invoke(new StreamSource(new StringReader(request)),
                "http://localhost:8080/bpel-g/services/driverService");
        assertNotNull(confirmation);

        boolean confirmed = AeXPathUtil.selectBoolean(confirmation, "/ns:confirmation/ns:accept",
                Collections.singletonMap("ns", "http://www.example.org/correlation/"));
        assertTrue(confirmed);
    }
}
