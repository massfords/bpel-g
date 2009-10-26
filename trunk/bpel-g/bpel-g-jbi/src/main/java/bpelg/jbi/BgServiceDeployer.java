package bpelg.jbi;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer;

public class BgServiceDeployer implements IAeWebServicesDeployer {
    
    // FIXME need to impl this class

    @Override
    public void deployToWebServiceContainer(IAeDeploymentContainer aContainer, ClassLoader aLoader)
            throws AeException {
    }

    @Override
    public void deployToWebServiceContainer(IAeServiceDeploymentInfo aService, ClassLoader aLoader)
            throws AeException {
    }

    @Override
    public void undeployFromWebServiceContainer(IAeDeploymentContainer aContainer)
            throws AeException {
    }

    @Override
    public void undeployFromWebServiceContainer(IAeServiceDeploymentInfo aService)
            throws AeException {
    }

}
