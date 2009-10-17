// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/deploy/AeAxisWebServicesDeployerBase.java,v 1.5 2008/02/17 21:29:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.deploy;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.AeWsdlReferenceTracker;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDUndeployment;
import org.apache.axis.server.AxisServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * WebServicesDeployer impl that deploys web services to Axis. 
 */
public abstract class AeAxisWebServicesDeployerBase extends AeAxisBase implements IAeWebServicesDeployer
{
   /**
    * Constructor.
    * @param aConfig
    */
   public AeAxisWebServicesDeployerBase(Map aConfig)
   {
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer#deployToWebServiceContainer(org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer, java.lang.ClassLoader)
    */
   public void deployToWebServiceContainer(IAeDeploymentContainer aContainer, ClassLoader aLoader) throws AeException
   {
      Document wsddDoc = null;
      if (aContainer.isWsddDeployment())
      {
         wsddDoc = aContainer.getWsddData();
      }
      else
      {
         wsddDoc = createWsdd(aContainer.getServiceDeploymentInfo());
      }
      deployToWebServiceContainer(wsddDoc, aLoader);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer#undeployFromWebServiceContainer(org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer)
    */
   public void undeployFromWebServiceContainer(IAeDeploymentContainer aContainer) throws AeException
   {
      Document wsddDoc = null;
      if (aContainer.isWsddDeployment())
      {
         wsddDoc = aContainer.getWsddData();
      }
      else
      {
         wsddDoc = createWsddForUndeployment(aContainer.getServiceDeploymentInfo());
      }
      undeployFromWebServiceContainer(wsddDoc);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer#deployToWebServiceContainer(org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo, java.lang.ClassLoader)
    */
   public void deployToWebServiceContainer(IAeServiceDeploymentInfo aService, ClassLoader aLoader) throws AeException
   {
      Document wsddDoc = createWsdd(new IAeServiceDeploymentInfo[] {aService});
      deployToWebServiceContainer(wsddDoc, aLoader);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer#undeployFromWebServiceContainer(org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo)
    */
   public void undeployFromWebServiceContainer(IAeServiceDeploymentInfo aService) throws AeException
   {
      Document wsddDoc = createWsddForUndeployment(new IAeServiceDeploymentInfo[] {aService});
      undeployFromWebServiceContainer(wsddDoc);
   }
   
   /**
    * Creates a wsdd service undeployment descriptor based on service names only.
    * 
    * @param aServices
    * @return wsdd document
    * @throws AeDeploymentException
    */   
   protected Document createWsddForUndeployment(IAeServiceDeploymentInfo[] aServices) throws AeDeploymentException
   {
      AeWsddBuilder builder = new AeWsddBuilder();
      for (int i = 0; i < aServices.length; i++)
      {
         IAeServiceDeploymentInfo serviceData = aServices[i];
         builder.addServiceElement(serviceData.getServiceName(), null);
      }
      return builder.getWsddDocument();      
   }

   /**
    * Creates a wsdd service deployment descriptor
    * 
    * @param aServices
    * @return wsdd document
    * @throws AeDeploymentException
    */
   protected Document createWsdd(IAeServiceDeploymentInfo[] aServices) throws AeDeploymentException
   {
      AeWsddBuilder builder = new AeWsddBuilder();
      for (int i = 0; i < aServices.length; i++)
      {
         IAeServiceDeploymentInfo serviceData = aServices[i];
         if( serviceData.isRPCEncoded() )
         {
            builder.addRpcService( serviceData );
         }
         else if (serviceData.isRPCLiteral())
         {
            builder.addRpcLiteralService( serviceData );
         }
         else if (serviceData.isMessageService())
         {
            builder.addMsgService( serviceData );
         }
         else if (serviceData.isPolicyService())
         {
            builder.addPolicyService( serviceData );
         }
         else if (!serviceData.isExternalService())
         {  
            AeException.logWarning( AeMessages.format("AeAxisWebServicesDeployerBase.UNKNOWN_ROLE_IN_WSDD", serviceData.getServiceName()) ); //$NON-NLS-1$
         }
      }
      
      return builder.getWsddDocument();
   }
   
   /**
    * @see org.activebpel.rt.axis.bpel.deploy.AeAxisBase#deployToAxis(java.lang.ClassLoader, org.w3c.dom.Document, org.w3c.dom.Document)
    */
   protected void deployToAxis(
      ClassLoader aClassLoader,
      Document aAxisDoc,
      Document aAdminDoc)
      throws Exception
   {
      // remember old classloader
      ClassLoader previous = Thread.currentThread().getContextClassLoader();

      try
      {
         Thread.currentThread().setContextClassLoader(aClassLoader);
         EngineConfiguration config = getAxisServer().getConfig();
         if (config instanceof WSDDEngineConfiguration) 
         {
             WSDDDeployment deployment =
                 ((WSDDEngineConfiguration)config).getDeployment();
             new AeBprDeployment(aAxisDoc.getDocumentElement()).deployToRegistry(deployment);
             // TODO --- may need client deployment here
         }

         refreshAndSave();
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(previous);
      }
   }
   
   /**
    * Update global options and configuration.
    * @throws Exception
    */
   protected void refreshAndSave() throws Exception
   {
      getAxisServer().refreshGlobalOptions();
      getAxisServer().saveConfiguration();
   }

   /**
    * @see org.activebpel.rt.axis.bpel.deploy.AeAxisBase#undeployFromAxis(org.w3c.dom.Document, org.w3c.dom.Document)
    */
   protected void undeployFromAxis(Document aAxisDoc, Document aAdminDoc)
      throws Exception
   {
      Element undeployEl = aAxisDoc.getDocumentElement();
      
      EngineConfiguration config = getAxisServer().getConfig();

      if (config instanceof WSDDEngineConfiguration) 
      {
         new WSDDUndeployment(undeployEl).undeployFromRegistry(
            ((WSDDEngineConfiguration)config).getDeployment());
         
         removeWsdlReferences( aAxisDoc );
      }

      refreshAndSave();
   }
   
   /**
    * Once the services have been removed, remove any <code>IAeWsdlRefereces</code>
    * that were associated with those services.
    * @param aWsdd
    */
   protected void removeWsdlReferences( Document aWsdd )
   {
      NodeList services = aWsdd.getElementsByTagName( "service" ); //$NON-NLS-1$
      for( int i = 0; i < services.getLength(); i++ )
      {
         String serviceName = ((Element)services.item(i)).getAttribute("name"); //$NON-NLS-1$
         AeWsdlReferenceTracker.unregisterReference( serviceName );
      }
   }
   
   /**
    * Accessor for Axis server.
    */
   abstract protected AxisServer getAxisServer();
}
