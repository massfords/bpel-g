// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentHandler.java,v 1.19 2007/12/17 19:53:34 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.text.MessageFormat;
import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.bpel.server.logging.IAeLogWrapper;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *  Deploys an IAeDeploymentContainer.
 */
public class AeDeploymentHandler extends AeAbstractDeploymentHandler
{
   /** Deployment factory impl */
   protected IAeDeploymentFactory mFactory;
   
   /**
    * Constructor.
    * @param aLogger IAeLogWrapper impl. May be null.
    */
   public AeDeploymentHandler( IAeLogWrapper aLogger, IAeDeploymentFactory aFactory )
   {
      super( aLogger );
      mFactory = aFactory;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandler#deploy(org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer, org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger)
    */
   public synchronized void deploy( IAeDeploymentContainer aContainer, IAeDeploymentLogger aLogger ) throws AeException
   {
      setContainerName(aContainer.getShortName());
      setDeploymentLogger(aLogger);
      getDeploymentLogger().setContainerName(aContainer.getShortName());

      try
      {
         if( aContainer.isWsddDeployment() )
         {
            logInfo(AeMessages.getString("AeDeploymentHandler.0")); //$NON-NLS-1$
            doWsddDeployment( aContainer );
            logInfo(AeMessages.getString("AeDeploymentHandler.1") ); //$NON-NLS-1$
         }
         else
         {
            logInfo(AeMessages.getString("AeDeploymentHandler.2")); //$NON-NLS-1$
            doBpelDeployment( aContainer );
            logInfo(AeMessages.getString("AeDeploymentHandler.3") ); //$NON-NLS-1$
         }
      }
      finally
      {
         setDeploymentLogger(null);
         setContainerName(null);
      }
   }
   
   /**
    * Deploy all BPEL processes in the container.
    * @param aContainer
    * @throws AeException
    */
   protected void doBpelDeployment( IAeDeploymentContainer aContainer )
   throws AeException
   {
      try
      {
         if( isValid( aContainer ) )
         {
            try
            {
               deployCatalog( aContainer );
               deployPdefs( aContainer );
               deployBpel( aContainer );
               deployWebServices( aContainer );
            }
            catch (AeDeploymentException de)
            {
               getDeploymentLogger().addError(AeMessages.getString("AeDeploymentHandler.ERROR_4"), new Object[] { de.getLocalizedMessage() }, null); //$NON-NLS-1$
               if (de.getCause() != null)
               {
                  getDeploymentLogger().addError(AeMessages.getString("AeDeploymentHandler.ERROR_5"), new Object[] { de.getCause().getLocalizedMessage() }, null); //$NON-NLS-1$
               }
            }
         }
         else
         {
            logError( AeMessages.getString("AeDeploymentHandler.ERROR_6")); //$NON-NLS-1$
         }
      }
      catch (Throwable t)
      {
         getDeploymentLogger().addError(AeMessages.getString("AeDeploymentHandler.ERROR_7"), new Object[] { t.getLocalizedMessage() }, null); //$NON-NLS-1$
      }
   }
   
   /**
    * Deploy and web services to the web services container.
    * @param aContainer
    * @throws AeException
    */
   protected void doWsddDeployment( IAeDeploymentContainer aContainer )
   throws AeException
   {
      String wsddDom = aContainer.getWsddResource();
      logInfo( AeMessages.getString("AeDeploymentHandler.8") + wsddDom ); //$NON-NLS-1$
      Document wsddXml = aContainer.getResourceAsDocument( wsddDom );
      aContainer.setWsddData( wsddXml );
      deployWebServices( aContainer );
   }
   
   /**
    * Returns true if the container passes predeployment validation.
    * @param aContainer
    * @throws AeException
    */
   protected boolean isValid( IAeDeploymentContainer aContainer )
   throws AeException
   {
      getDeploymentLogger().resetWarningAndErrorFlags();
      getFactory().getValidationHandler().doPredeploymentValidation( aContainer, getDeploymentLogger() );
      return !getDeploymentLogger().hasErrors();      
   }
   
   /**
    * Deploy the catalog resources of the passed container.
    * @param aContainer
    * @throws AeException
    */
   protected void deployCatalog( IAeDeploymentContainer aContainer )
   throws AeException
   {
      logDebug( "Deploying Catalog" ); //$NON-NLS-1$
      getFactory().getCatalogDeployer().deployToCatalog( aContainer, getDeploymentLogger() );
   }
   
   /**
    * Deploy pdefs.
    * @param aContainer
    * @throws AeException
    */
   protected void deployPdefs( IAeDeploymentContainer aContainer )
   throws AeException
   {
      logDebug( "Deploying PDEFS" ); //$NON-NLS-1$
      getFactory().getPDefDeployer().deployPdefs( aContainer );
   }
   
   /**
    * Deploy BPEL.
    * @param aContainer
    * @throws AeException
    */
   protected void deployBpel( IAeDeploymentContainer aContainer )
   throws AeException
   {
      for( Iterator iter = aContainer.getPddResources().iterator(); iter.hasNext(); )
      {
         boolean success = false;
         String pddName = (String)iter.next();
         String shortName = AeUtil.getFilename(pddName);
         getDeploymentLogger().setPddName(shortName);
         try
         {
            IAeDeploymentSource source = aContainer.getDeploymentSource( pddName );
            logDebug( "Deploying BPEL for " + source.getProcessName() + " from " + pddName ); //$NON-NLS-1$ //$NON-NLS-2$
            getFactory().getBpelDeployer().deployBpel(source, getDeploymentLogger());

            if( !getDeploymentLogger().hasErrors() )
            {
               // Get the service info for undeployment
               IAeServiceDeploymentInfo[] services = getServiceInfo(source);
               aContainer.addServiceDeploymentInfo(services);
               
               if( getDeploymentLogger().hasWarnings() )
               {
                  logInfo(MessageFormat.format(AeMessages.getString("AeDeploymentHandler.4"), new Object[] {pddName})); //$NON-NLS-1$
               }
               getDeploymentLogger().addInfo(AeMessages.getString("AeDeploymentHandler.SUCCESSFULLY_DEPLOYED_PDD"), new Object[] {}, null); //$NON-NLS-1$
               success = true;
            }
            else
            {
               logError(MessageFormat.format(AeMessages.getString("AeDeploymentHandler.ERROR_8"), new Object[] {pddName})); //$NON-NLS-1$
            }
         }
         catch (Throwable t)
         {
            AeException.logError( t, t.getLocalizedMessage() );
            getDeploymentLogger().addError(AeMessages.getString("AeDeploymentHandler.ERROR_15"), new Object[] { pddName, t.getLocalizedMessage() }, null); //$NON-NLS-1$
         }
         finally
         {
            getDeploymentLogger().processDeploymentFinished(success);
         }

         getDeploymentLogger().resetWarningAndErrorFlags();
      }
      getDeploymentLogger().setPddName(null);
   }
   
   /**
    * Deploy web services.  The container must have a valid
    * deployment doc set as its wsddData.
    * @param aContainer
    * @throws AeException
    */
   protected void deployWebServices( IAeDeploymentContainer aContainer )
   throws AeException
   {
      ClassLoader wsLoader = aContainer.getWebServicesClassLoader();
      getFactory().getWebServicesDeployer().deployToWebServiceContainer( aContainer, wsLoader );
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandler#undeploy(org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer)
    */
   public synchronized void undeploy( IAeDeploymentContainer aContainer )
   {
      setContainerName(aContainer.getShortName());
      try
      {
         if( aContainer.isWsddDeployment() )
         {
            logInfo( AeMessages.getString("AeDeploymentHandler.16") ); //$NON-NLS-1$
            undeployWebServices( aContainer );
            logInfo( AeMessages.getString("AeDeploymentHandler.17") ); //$NON-NLS-1$
         }
         else
         {
            logInfo( AeMessages.getString("AeDeploymentHandler.18") ); //$NON-NLS-1$
            undeployWebServices( aContainer );      
            undeployBpel( aContainer );
            undeployCatalog( aContainer );
            undeployPdefs( aContainer );
            logInfo( AeMessages.getString("AeDeploymentHandler.19") ); //$NON-NLS-1$
         }
      }
      finally
      {
         setContainerName(null);
      }
   }
   
   /**
    * Undeploy the catalog resources of the passed container.
    * @param aContainer
    */
   protected void undeployCatalog( IAeDeploymentContainer aContainer )
   {
      try
      {
         logDebug( "Undeploying Catalog" ); //$NON-NLS-1$
         getFactory().getCatalogDeployer().undeployFromCatalog( aContainer );
      }
      catch (AeException e)
      {
         logError( AeMessages.getString("AeDeploymentHandler.ERROR_21"), e ); //$NON-NLS-1$
      }
   }
   
   /**
    * Undeploy pdefs.
    * @param aContainer
    */
   protected void undeployPdefs( IAeDeploymentContainer aContainer )
   {
      try
      {
         logDebug( "Undeploying PDEFs." ); //$NON-NLS-1$
         getFactory().getPDefDeployer().undeployPdefs( aContainer );
      }
      catch (AeException e)
      {
         logError( AeMessages.getString("AeDeploymentHandler.ERROR_23"), e ); //$NON-NLS-1$
      }
   }
   
   /**
    * Undeploy BPEL.
    * @param aContainer
    */
   protected void undeployBpel( IAeDeploymentContainer aContainer )
   {
      for( Iterator iter = aContainer.getPddResources().iterator(); iter.hasNext(); )
      {
         String pddName = (String)iter.next();
         try
         {
            IAeDeploymentSource source = aContainer.getDeploymentSource(pddName);
            logDebug( "Undeploying bpel: " + source.getProcessName() + " from " + pddName ); //$NON-NLS-1$ //$NON-NLS-2$
            getFactory().getBpelDeployer().undeployBpel( source );      
         }
         catch (AeException e)
         {
             logError(MessageFormat.format(AeMessages.getString("AeDeploymentHandler.ERROR_9"), new Object[] {pddName}), e); //$NON-NLS-1$
         }
      }
   }
   
   /**
    * Undeploy web services.
    * @param aContainer
    */
   protected void undeployWebServices( IAeDeploymentContainer aContainer )
   {
      try
      {
         for( Iterator iter = aContainer.getPddResources().iterator(); iter.hasNext(); )
         {
            String pddName = (String)iter.next();
            IAeDeploymentSource source = aContainer.getDeploymentSource(pddName);
            logDebug( "Undeploying web services: " + source.getProcessName() + " from " + pddName ); //$NON-NLS-1$ //$NON-NLS-2$

            // Get the service info for undeployment
            IAeServiceDeploymentInfo[] services = getServiceInfo(source);
            aContainer.addServiceDeploymentInfo(services);
         }
         
         getFactory().getWebServicesDeployer().undeployFromWebServiceContainer( aContainer );
      }
      catch (AeException e)
      {
         logError( AeMessages.getString("AeDeploymentHandler.ERROR_30"), e); //$NON-NLS-1$
      }
      
   }
   
   /**
    * Gets the service deployment info from a source
    * 
    * @param aSource
    * @throws AeDeploymentException
    */
   protected IAeServiceDeploymentInfo[] getServiceInfo(IAeDeploymentSource aSource) throws AeDeploymentException
   {
      // Get the service info
      Element pddElement = aSource.getProcessSourceElement();
      AeProcessDef processDef = aSource.getProcessDef();
      return AeServiceDeploymentUtil.getServices( processDef, pddElement );
   }
   
   /**
    * Accessor for deployment factory.
    */
   protected IAeDeploymentFactory getFactory()
   {
      return mFactory;
   }
}
