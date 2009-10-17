// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeDeploymentFactory.java,v 1.2 2006/07/18 20:05:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.bpel.server.deploy.validate.IAeValidationHandler;

/**
 * Factory interface for obtaining individual ActiveBpel deployers and
 * validators.
 */
public interface IAeDeploymentFactory
{
   /**
    * Provides access to the predeployment and deployment
    * validators.
    */
   public IAeValidationHandler getValidationHandler();   
   
   /**
    * Accessor for the deployer responsible for deploying BPEL processes
    * to the engine.
    */
   public IAeBpelDeployer getBpelDeployer();
   
   /**
    * Accessor for the deployer responsible for deploying entries
    * from a BPR resource catalog.
    */
   public IAeCatalogDeployer getCatalogDeployer();
   
   /**
    * Accessor for the deployer responsible for deploying partner 
    * definitions.
    */
   public IAePdefDeployer getPDefDeployer();
   
   /**
    * Accessor for the deployer responsible for deploying web services
    * to the web services container.
    */
   public IAeWebServicesDeployer getWebServicesDeployer();

}
