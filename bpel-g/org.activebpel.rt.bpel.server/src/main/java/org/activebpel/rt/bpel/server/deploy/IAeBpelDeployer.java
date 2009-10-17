// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeBpelDeployer.java,v 1.1 2004/09/17 20:59:52 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter;

/**
 * Interface for deployers that deploy/undeploy BPEL to the ActiveBPEL
 * engine.
 */
public interface IAeBpelDeployer
{
   
   /**
    * Deploy all BPEL processes to the ActiveBPEL engine.
    * @param aDeployment 
    * @param aReporter Reports any validation issues.
    * @throws AeException
    */
   public void deployBpel( IAeDeploymentSource aDeployment, IAeBaseErrorReporter aReporter )
   throws AeException;
   
   /**
    * Remove all BPEL processes in the container from the ActiveBPEL engine.
    * @param aDeployment
    * @throws AeException
    */
   public void undeployBpel( IAeDeploymentSource aDeployment )
   throws AeException;
}
