package bpelg.packaging.ode;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.AeNewDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainerFactory;

import java.io.File;

public class BgDeploymentContainerFactory implements IAeDeploymentContainerFactory {

    @Override
    public IAeDeploymentContainer createDeploymentContainer(AeNewDeploymentInfo info)
            throws AeException {
        try {
            return new BgDeploymentContainer(new File(info.getTempURL().getFile()));
        } catch (Exception e) {
            throw new AeException(e);
        }
    }

    @Override
    public IAeDeploymentContainer createUndeploymentContainer(AeNewDeploymentInfo info)
            throws AeException {
        try {
            return new BgDeploymentContainer(new File(info.getTempURL().getFile()));
        } catch (Exception e) {
            throw new AeException(e);
        }
    }
}
