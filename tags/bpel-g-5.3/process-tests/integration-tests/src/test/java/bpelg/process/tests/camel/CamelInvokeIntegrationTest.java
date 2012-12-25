package bpelg.process.tests.camel;

import bpelg.services.deploy.types.UndeploymentRequest;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;

/**
 * @author markford
 *         Date: 4/7/12
 */
public class CamelInvokeIntegrationTest extends Assert {
    private final AeProcessFixture pfix = new AeProcessFixture();
    private CamelFixture cfix;

    @Before
    public void setUp() throws Exception {
        cfix = new CamelFixture("netty:udp://localhost:60300?sync=false");
        cfix.start();
        // deploy
        pfix.deploySingle(new File("target/dependency/camel-invoke.jar"));
    }

    @Test
    public void test() throws Exception {

        // prepare payload
        String body = "<createProcess xmlns='http://www.example.org/camel-invoke/'>" +
                "   <message>hello from junit</message>" +
                "</createProcess>";

        // send message via soap
        Document responseDoc = pfix.invoke(new StreamSource(new StringReader(body)),
                "http://localhost:8080/bpel-g/services/camelInvokeService");

        // assert response is received - hopefully it'll complete w/in 20 seconds
        cfix.assertIsSatisfied();

        // assert response payload
        String notification = cfix.getReceivedPayload(0);
        assertNotNull(notification);
        Node notificationDoc = pfix.toNode(new StreamSource(new StringReader(notification)));
        assertEquals("hello from junit", AeXPathUtil.selectText(notificationDoc, "//*[local-name() = 'message']", null));

        // assert the notification is the same as our response
        pfix.assertXMLIgnorePrefix("notification differs from response", responseDoc, notificationDoc);
    }

    @After
    public void tearDown() throws Exception {
        cfix.stop();
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("camel-invoke.jar"));
    }
}
