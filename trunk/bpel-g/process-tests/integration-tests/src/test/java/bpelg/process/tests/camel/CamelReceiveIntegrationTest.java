package bpelg.process.tests.camel;

import bpelg.services.deploy.types.UndeploymentRequest;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;

/**
 * @author markford
 *         Date: 4/7/12
 */
public class CamelReceiveIntegrationTest extends Assert {
    private AeProcessFixture pfix = new AeProcessFixture();
    private CamelFixture cfix;

    @Before
    public void setUp() throws Exception {
        cfix = new CamelFixture("netty:udp://localhost:60300?sync=false");
        cfix.start();
        // deploy
        pfix.deploySingle(new File("target/dependency/camel-receive.jar"));
    }

    @Test
    public void test() throws Exception {

        // prepare payload
        String body = "<createProcess xmlns='http://www.example.org/simple/'>" +
                "   <message>hello from junit</message>" +
                "</createProcess>";

        // send message via udp
        // use our same context here to get a ref to an endpoint that targets the input for the process
        cfix.send("netty:udp://localhost:60200?sync=false", body);

        // assert response is received - hopefully it'll complete w/in 20 seconds
        cfix.assertIsSatisfied();

        // assert response payload
        String response = (String) cfix.getReceivedPayload(0);
        assertNotNull(response);
        Node doc = pfix.toNode(new StreamSource(new StringReader(response)));
        assertEquals("hello from junit", AeXPathUtil.selectText(doc, "//*[local-name() = 'message']", null));
    }

    @After
    public void tearDown() throws Exception {
        cfix.stop();
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("camel-receive.jar"));
    }
}
