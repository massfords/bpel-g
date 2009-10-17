// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeDeploymentHandler.java,v 1.5 2006/02/24 16:37:30 EWittmann Exp $
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
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;

/**
 * Course grained deployment interface.
 */
public interface IAeDeploymentHandler
{
   /**
    * Deploy container resources to the system.
    * 
    * @param aContainer
    * @param aLogger
    * @throws AeException
    */
   public void deploy(IAeDeploymentContainer aContainer, IAeDeploymentLogger aLogger) throws AeException;
   
   /**
    * Undeploy container resources from the system.
    * 
    * @param aContainerS
    * @throws AeException
    */
   public void undeploy( IAeDeploymentContainer aContainerS ) throws AeException;
}
