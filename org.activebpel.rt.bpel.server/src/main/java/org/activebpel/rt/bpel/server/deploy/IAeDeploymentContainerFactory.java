package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.AeException;

public interface IAeDeploymentContainerFactory {

    /**
     * Create and configure the <code>IAeDeploymentContainer</code>for
     * deployment.
     * 
     * @param aInfo
     * @throws AeException
     */
    public IAeDeploymentContainer createDeploymentContainer(AeNewDeploymentInfo aInfo)
            throws AeException;

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
