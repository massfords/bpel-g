// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeBpelDeployer.java,v 1.7 2005/06/08 13:30:31 EWittmann Exp $
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
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * IAeBpelDeployer impl.
 */
public class AeBpelDeployer implements IAeBpelDeployer
{
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeBpelDeployer#deployBpel(org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource, org.activebpel.rt.bpel.def.validation.IAeBaseErrorReporter)
    */
   public void deployBpel(IAeDeploymentSource aSource, IAeBaseErrorReporter aReporter ) throws AeException
   {
      IAeProcessDeployment deployment = create(aSource);
      AeDeploymentHandlerFactory handlerFactory = (AeDeploymentHandlerFactory)AeEngineFactory.getDeploymentHandlerFactory();
      handlerFactory.getDeploymentFactory().getValidationHandler().doDeploymentValidation(
            aSource.getPddLocation(), deployment, aReporter);
      if( !aReporter.hasErrors() )
      {
         AeEngineFactory.getDeploymentProvider().addDeploymentPlan( deployment );
      }
   }
   
   /**
    * Create the process deployment.
    * @param aSource
    * @throws AeDeploymentException
    */
   public IAeProcessDeployment create( IAeDeploymentSource aSource )
   throws AeDeploymentException
   {
      return AeProcessDeploymentFactory.getInstance().newInstance(aSource );
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeBpelDeployer#undeployBpel(org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource)
    */
   public void undeployBpel(IAeDeploymentSource aDeployment) throws AeException
   {
      AeEngineFactory.getDeploymentProvider().removeDeploymentPlan( aDeployment.getProcessName() );
   }
}
