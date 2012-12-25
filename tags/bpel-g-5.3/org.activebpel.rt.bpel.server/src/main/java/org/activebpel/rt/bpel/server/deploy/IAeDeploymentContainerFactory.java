package org.activebpel.rt.bpel.server.deploy;

import bpelg.services.deploy.MissingResourcesException;
import bpelg.services.deploy.UnhandledException;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;

public interface IAeDeploymentContainerFactory {

    /**
     * Create and configure the <code>IAeDeploymentContainer</code>for
     * deployment.
     * 
     * @param aInfo
     * @throws AeException
     */
    public IAeDeploymentContainer createDeploymentContainer(AeNewDeploymentInfo aInfo, IAeDeploymentLogger logger)
            throws AeException, MissingResourcesException, UnhandledException;

    /**
     * Create and configure the <code>IAeDeploymentContainer</code>for
     * undeployment.
     * 
     * @param aInfo
     * @throws AeException
     */
    public IAeDeploymentContainer createUndeploymentContainer(AeNewDeploymentInfo aInfo)
            throws AeException;

}
