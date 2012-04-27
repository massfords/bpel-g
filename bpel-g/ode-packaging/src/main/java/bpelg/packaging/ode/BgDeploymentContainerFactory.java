package bpelg.packaging.ode;

import bpelg.services.deploy.MissingResourcesException;
import bpelg.services.deploy.UnhandledException;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.AeNewDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainerFactory;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;

import java.io.File;

public class BgDeploymentContainerFactory implements IAeDeploymentContainerFactory {

    @Override
    public IAeDeploymentContainer createDeploymentContainer(AeNewDeploymentInfo info, IAeDeploymentLogger logger)
            throws AeException, MissingResourcesException, UnhandledException {
        try {
            return new BgDeploymentContainer(new File(info.getTempURL().getFile()), logger);
        } catch (MissingResourcesException e) {
            throw e;
        } catch (Exception e) {
            throw new UnhandledException(e.getMessage(), e);
        }
    }

    @Override
    public IAeDeploymentContainer createUndeploymentContainer(AeNewDeploymentInfo info)
            throws AeException {
        try {
            return new BgDeploymentContainer(new File(info.getTempURL().getFile()), null);
        } catch (Exception e) {
            throw new AeException(e);
        }
    }
}
