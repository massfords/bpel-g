package bpelg.process.tests;

import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.MessageType;
import bpelg.services.deploy.types.Msg;
import bpelg.services.deploy.types.UndeploymentRequest;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * @author markford
 *         Date: 4/26/12
 */
public class InvalidContainerIntegrationTest extends Assert {
    private final AeProcessFixture pfix = new AeProcessFixture();

    @Test
    public void test() throws Exception {
        DeploymentResponse response = pfix.deploy(new File("target/dependency/invalid-container.jar"));
        assertNotNull(response);
        DeploymentResponse.DeploymentInfo info = response.getDeploymentInfo().get(0);
        assertTrue(info.isDeployed());
        for (Msg m : info.getLog().getMsg()) {
            System.out.println(m.getType() + ": " + m.getValue());
        }
        for (Msg m : response.getMsg()) {
            System.out.println(m.getType() + ": " + m.getValue());
        }
        assertEquals(1, response.getMsg().size());
        assertEquals(MessageType.WARNING, response.getMsg().get(0).getType());
        assertTrue(response.getMsg().get(0).getValue().startsWith("Extra bpel file without an entry in deploy.xml:"));
    }

    @After
    public void undeployProcess() throws Exception {
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("invalid-container.jar"));
    }
}
