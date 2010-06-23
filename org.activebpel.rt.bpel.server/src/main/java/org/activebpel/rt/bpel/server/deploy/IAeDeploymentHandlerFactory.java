// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeDeploymentHandlerFactory.java,v 1.2 2007/04/24 00:45:52 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.bpel.server.logging.IAeLogWrapper;

/**
 * Factory interface for creating deployment handlers.
 */
public interface IAeDeploymentHandlerFactory
{
   /**
    * Create a new IAeDeploymentHandler interface.
    * @param aLogWrapper
    */
   public IAeDeploymentHandler newInstance( IAeLogWrapper aLogWrapper );
   
   /**
    * Returns the web service deployer.
    */
   public IAeWebServicesDeployer getWebServicesDeployer();
}
