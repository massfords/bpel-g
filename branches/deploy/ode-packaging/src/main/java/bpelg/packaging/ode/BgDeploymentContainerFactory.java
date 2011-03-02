package bpelg.packaging.ode;

import java.io.File;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.AeNewDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainerFactory;

public class BgDeploymentContainerFactory implements IAeDeploymentContainerFactory {

    @Override
    public IAeDeploymentContainer createDeploymentContainer(AeNewDeploymentInfo aInfo)
            throws AeException {
        try {
            return new BgDeploymentContainer(new File(aInfo.getTempURL().getFile()));
        } catch (Exception e) {
            throw new AeException(e);
        }
    }

    @Override
    public IAeDeploymentContainer createUndeploymentContainer(AeNewDeploymentInfo aInfo)
            throws AeException {
        try {
            return new BgDeploymentContainer(new File(aInfo.getTempURL().getFile()));
        } catch (Exception e) {
            throw new AeException(e);
        }
    }
}
