// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAePdefDeployer.java,v 1.1 2004/09/17 20:59:52 PCollins Exp $
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

/**
 * Handles the details of deploying/undeploying partner definitions.
 */
public interface IAePdefDeployer
{

   /**
    * Deploy any partner definitions in the deployment container.
    * @param aContainer
    * @throws AeException
    */
   public void deployPdefs( IAeDeploymentContainer aContainer )
   throws AeException;
   
   /**
    * Undeploy any partner definitions in the deployment container.
    * @param aContainer
    * @throws AeException
    */
   public void undeployPdefs( IAeDeploymentContainer aContainer )
   throws AeException;

}
