// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentHandlerFactory.java,v 1.3 2007/04/24 00:45:52 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.activebpel.rt.bpel.server.logging.IAeLogWrapper;

/**
 * Factory for creating new deployment handlers.
 * In this impl, the handlers ARE NOT thread safe and <code>newInstance</code>
 * should be called for each deployment.  
 */
public class AeDeploymentHandlerFactory implements IAeDeploymentHandlerFactory
{
   /**
    * Deployment factory used by the handler impl.
    */
   protected IAeDeploymentFactory mDeploymentFactory;
   
   /**
    * Constructor.
    * @param aParams Any aeEngineConfig params.
    */
   public AeDeploymentHandlerFactory( Map aParams ) throws Exception
   {
      Map deployerParams = (Map)aParams.get("DeploymentFactory"); //$NON-NLS-1$
      String deploymentFactoryImpl = (String)deployerParams.get("Class"); //$NON-NLS-1$
      Class deployerFactoryImplClass = Class.forName(deploymentFactoryImpl);
      Constructor xTor = deployerFactoryImplClass.getConstructor( new Class[]{Map.class} );
      mDeploymentFactory = (IAeDeploymentFactory)xTor.newInstance( new Object[]{deployerParams} );
   }
   
   /**
    * Accessor for the deployment factory impl.
    */
   public IAeDeploymentFactory getDeploymentFactory()
   {
      return mDeploymentFactory;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandlerFactory#getWebServicesDeployer()
    */
   public IAeWebServicesDeployer getWebServicesDeployer()
   {
      return getDeploymentFactory().getWebServicesDeployer();  
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandlerFactory#newInstance(org.activebpel.rt.bpel.server.logging.IAeLogWrapper)
    */
   public IAeDeploymentHandler newInstance(IAeLogWrapper aLogWrapper)
   {
      return new AeDeploymentHandler( aLogWrapper, getDeploymentFactory() );
   }
}
