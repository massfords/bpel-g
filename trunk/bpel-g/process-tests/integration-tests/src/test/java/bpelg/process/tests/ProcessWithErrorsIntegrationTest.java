package bpelg.process.tests;

import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.Msg;
import org.activebpel.services.jaxws.AeProcessFixture;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * @author markford
 *         Date: 4/13/12
 */
public class ProcessWithErrorsIntegrationTest extends Assert {

    private final AeProcessFixture pfix = new AeProcessFixture();

    @Test
    public void test() throws Exception {
        DeploymentResponse response = pfix.deploy(new File("target/dependency/process-with-errors.jar"));
        assertNotNull(response);
        DeploymentResponse.DeploymentInfo info = response.getDeploymentInfo().get(0);
        assertFalse(info.isDeployed());
        for(Msg m : info.getLog().getMsg()) {
            System.out.println(m.getType());
            System.out.println(m.getValue());
        }
        assertEquals(1, info.getNumberOfWarnings());
        assertEquals(6, info.getNumberOfErrors());
    }

    /*
    Tests to add:
    - deploy.xml
    -- missing process
    -- misnamed partner link
    -- duplicate service endpoint
    -- missing invoke plink (warning?)
    -- missing receive endpoint plink (fatal)

    - process
    -- static analysis failure
     */
}
