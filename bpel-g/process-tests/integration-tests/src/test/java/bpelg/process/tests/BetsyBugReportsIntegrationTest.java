package bpelg.process.tests;

import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.MessageType;
import bpelg.services.deploy.types.Msg;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ma21633
 *         Date: 12/21/12
 */
public class BetsyBugReportsIntegrationTest extends Assert {
    private final AeProcessFixture pfix = new AeProcessFixture();

    @Test
    public void test() throws Exception {
        //target/dependency/
//        String pathname = "../betsy-bug-reports/target/betsy-bug-reports-5.3-SNAPSHOT.jar";
        String pathname = "target/dependency/betsy-bug-reports.jar";
        DeploymentResponse response = pfix.deploy(new File(pathname));
        assertNotNull(response);

        Map<String,DeploymentResponse.DeploymentInfo> infos = new HashMap<String,DeploymentResponse.DeploymentInfo>();
        for(DeploymentResponse.DeploymentInfo info : response.getDeploymentInfo()) {
            infos.put(info.getName(), info);
        }

        assertStartError(infos.get("start-error.bpel.pdd"));
        assertDupeVar(infos.get("dupe-variable.bpel.pdd"));
    }

    private void assertStartError(DeploymentResponse.DeploymentInfo info) {
        assertFalse(info.isDeployed());
        boolean foundError = false;
        for(Msg m : info.getLog().getMsg()) {
            if (m.getType()== MessageType.ERROR) {
                foundError |= m.getValue().contains("start activity");
            }
        }
        assertTrue(foundError);
    }

    private void assertDupeVar(DeploymentResponse.DeploymentInfo info) {
        assertFalse(info.isDeployed());
        boolean foundError = false;
        for(Msg m : info.getLog().getMsg()) {
//            System.out.println(m.getValue());
            if (m.getType()== MessageType.ERROR) {
                foundError |= m.getValue().contains("A variable with name processCreated already exists");
            }
        }
        assertTrue(foundError);
    }
}
