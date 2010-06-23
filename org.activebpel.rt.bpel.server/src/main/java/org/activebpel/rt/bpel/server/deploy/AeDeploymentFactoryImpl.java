// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentFactoryImpl.java,v 1.6 2008/01/22 17:11:18 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.validate.IAeValidationHandler;
import org.activebpel.rt.util.AeUtil;

/**
 * Default IAeDeploymentFactory impl.  Gets all of its deployer impls
 * from the config map.  None of its deployers should contain any
 * state info (as they are all treated like singletons).
 */
public class AeDeploymentFactoryImpl implements IAeDeploymentFactory
{
   // deployer xml constants
   private static final String PDEF_DEPLOYER         = "PdefDeployer"; //$NON-NLS-1$
   private static final String CATALOG_DEPLOYER      = "CatalogDeployer"; //$NON-NLS-1$
   private static final String WEB_SERVICES_DEPLOYER = "WebServicesDeployer"; //$NON-NLS-1$
   private static final String BPEL_DEPLOYER         = "BpelDeployer"; //$NON-NLS-1$
   private static final String VALIDATION_HANDLER    = "ValidationHandler"; //$NON-NLS-1$
   
   /** Validation handler. */
   protected IAeValidationHandler mValidationHandler;
   /** Partner definition deployer. */
   protected IAePdefDeployer mPdefDeployer;
   /** Wsdl catalog deployer. */
   protected IAeCatalogDeployer mCatalogDeployer;
   /** Web services deployer. */   
   protected IAeWebServicesDeployer mWebServicesDeployer;
   /** BPEL process deployer. */
   protected IAeBpelDeployer mBpelDeployer;

   /**
    * Constructor.
    * @param aConfig AeEngine config params map.
    * @throws AeException
    */
   public AeDeploymentFactoryImpl( Map aConfig ) throws AeException
   {
      if (AeUtil.isNullOrEmpty(aConfig))
      {
         throw new AeException(AeMessages.getString("AeDeploymentFactoryImpl.ERROR_0")); //$NON-NLS-1$
      }

      initializeDeployers( aConfig );
   }
   
   /**
    * Initialize the individual deployers.
    * @param aConfig
    * @throws AeException
    */
   protected void initializeDeployers( Map aConfig ) throws AeException
   {
      mPdefDeployer = (IAePdefDeployer)AeDeployerUtil.createDeployer(PDEF_DEPLOYER, aConfig);   
      mCatalogDeployer = (IAeCatalogDeployer)AeDeployerUtil.createDeployer(CATALOG_DEPLOYER, aConfig);               
      mWebServicesDeployer = (IAeWebServicesDeployer)AeDeployerUtil.createDeployer(WEB_SERVICES_DEPLOYER, aConfig);
      mBpelDeployer = (IAeBpelDeployer)AeDeployerUtil.createDeployer(BPEL_DEPLOYER, aConfig );
      
      mValidationHandler = (IAeValidationHandler)AeDeployerUtil.createDeployer(VALIDATION_HANDLER, aConfig );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentFactory#getBpelDeployer()
    */
   public IAeBpelDeployer getBpelDeployer()
   {
      return mBpelDeployer;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentFactory#getPDefDeployer()
    */
   public IAePdefDeployer getPDefDeployer()
   {
      return mPdefDeployer;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentFactory#getCatalogDeployer()
    */
   public IAeCatalogDeployer getCatalogDeployer()
   {
      return mCatalogDeployer;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentFactory#getWebServicesDeployer()
    */
   public IAeWebServicesDeployer getWebServicesDeployer()
   {
      return mWebServicesDeployer;
   }
      
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentFactory#getValidationHandler()
    */
   public IAeValidationHandler getValidationHandler()
   {
      return mValidationHandler;
   }
}
