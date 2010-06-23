// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeCatalogDeployer.java,v 1.1 2006/07/18 20:05:33 ckeller Exp $
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
 * Handles deployments to catalog.
 */
public interface IAeCatalogDeployer
{
   /**
    * Deploy wsdl in deployment container to the catalog.
    * @param aContainer
    * @param aLogger
    * @throws AeException
    */
   public void deployToCatalog( IAeDeploymentContainer aContainer, IAeDeploymentLogger aLogger )
   throws AeException;
   
   /**
    * Remove wsdl in the deployment container from the catalog.
    * @param aContainer
    * @throws AeException
    */
   public void undeployFromCatalog( IAeDeploymentContainer aContainer )
   throws AeException;

}
