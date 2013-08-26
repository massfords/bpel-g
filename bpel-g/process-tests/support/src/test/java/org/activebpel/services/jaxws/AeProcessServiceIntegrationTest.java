package org.activebpel.services.jaxws;

import bpelg.services.deploy.types.UndeploymentRequest;
import bpelg.services.processes.types.ProcessFilterType;
import bpelg.services.processes.types.ProcessList;
import bpelg.services.processes.types.ProcessStateFilterValueType;
import bpelg.services.urnresolver.types.AddMappingRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

@Ignore
public class AeProcessServiceIntegrationTest {
    final AeProcessFixture pfix = new AeProcessFixture();

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
        pfix.invoke(new File("src/test/resources/credInfo_Smith15001.xml"),
                "http://localhost:8080/bpel-g/services/LoanProcessCompletedService");

        // we should have some processes in our list
        ProcessList list = pfix.getProcessManager().getProcesses(new ProcessFilterType().withProcessState(ProcessStateFilterValueType.Completed));
        assertEquals(2, list.getTotalRowCount());
    }

    @After
    public void tearDown() throws Exception {
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("loanProcessCompleted.bpr"));
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("loanApproval.bpr"));
        pfix.getDeployer().undeploy(new UndeploymentRequest().withDeploymentContainerId("riskAssessment.bpr"));
    }

}
