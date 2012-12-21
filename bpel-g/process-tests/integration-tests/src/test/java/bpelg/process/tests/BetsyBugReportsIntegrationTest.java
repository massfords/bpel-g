package bpelg.process.tests;

import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.MessageType;
import bpelg.services.deploy.types.Msg;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * @author ma21633
 *         Date: 12/21/12
 */
public class BetsyBugReportsIntegrationTest extends Assert {
    private final AeProcessFixture pfix = new AeProcessFixture();

    @Test
    public void test() throws Exception {
        String pathname = "../betsy-bug-reports/target/betsy-bug-reports-5.3-SNAPSHOT.jar";
        DeploymentResponse response = pfix.deploy(new File(pathname));
        assertNotNull(response);
        DeploymentResponse.DeploymentInfo info = response.getDeploymentInfo().get(0);
        assertFalse(info.isDeployed());
        boolean foundError = false;
        for(Msg m : info.getLog().getMsg()) {
            if (m.getType()== MessageType.ERROR) {
                foundError |= m.getValue().contains("start activity");
            }
        }
        assertTrue(foundError);
    }

}
