//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDelegatingWebServiceDeployer.java,v 1.5.4.1 2008/04/24 14:47:01 KPease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeWSDLPolicyHelper;
import org.activebpel.rt.bpel.server.engine.AeDelegatingHandlerFactory;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;

/**
 * WebServiceDeployer that delegates to the target deployer based on the deployment type 
 * specified on the container or overridden by endpoint policy
 */
public class AeDelegatingWebServiceDeployer extends AeDelegatingHandlerFactory implements IAeWebServicesDeployer
{
   /**
    * Constructor that takes a configuration map
    * 
    * @param aConfig
    * @throws AeException
    */
   public AeDelegatingWebServiceDeployer(Map aConfig) throws AeException
   {
      super(aConfig);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer#deployToWebServiceContainer(org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer, java.lang.ClassLoader)
    */
   public void deployToWebServiceContainer(IAeDeploymentContainer aContainer, ClassLoader aLoader) throws AeException
   {
      if (aContainer.isWsddDeployment())
      {
         // non-bpel axis service
         IAeWebServicesDeployer deployer = (IAeWebServicesDeployer) getDelegate(aContainer.getDeploymentType());
         deployer.deployToWebServiceContainer(aContainer, aLoader);
      }
      else
      {
         IAeServiceDeploymentInfo[] services = aContainer.getServiceDeploymentInfo();
         if (services != null)
         {
            for (int i = 0; i < services.length; i++)
            {
               resolveServicePolicies(services[i]);
               IAeWebServicesDeployer deployer = getServiceDeployer(services[i], aContainer.getDeploymentType());
               deployer.deployToWebServiceContainer(services[i], aLoader);
            }
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer#undeployFromWebServiceContainer(org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer)
    */
   public void undeployFromWebServiceContainer(IAeDeploymentContainer aContainer) throws AeException
   {
      if (aContainer.isWsddDeployment())
      {
         // non-bpel axis service
         IAeWebServicesDeployer deployer = (IAeWebServicesDeployer) getDelegate(aContainer.getDeploymentType());
         deployer.undeployFromWebServiceContainer(aContainer);
      }
      else
      {
         IAeServiceDeploymentInfo[] services = aContainer.getServiceDeploymentInfo();
         if (services != null)
         {
            for (int i = 0; i < services.length; i++)
            {
               IAeWebServicesDeployer deployer = getServiceDeployer(services[i], aContainer.getDeploymentType());
               deployer.undeployFromWebServiceContainer(services[i]);
            }
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer#deployToWebServiceContainer(org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo, java.lang.ClassLoader)
    */
   public void deployToWebServiceContainer(IAeServiceDeploymentInfo aService, ClassLoader aLoader) throws AeException
   {
      IAeWebServicesDeployer deployer = getServiceDeployer(aService, null);
      deployer.deployToWebServiceContainer(aService, aLoader);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer#undeployFromWebServiceContainer(org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo)
    */
   public void undeployFromWebServiceContainer(IAeServiceDeploymentInfo aService) throws AeException
   {
      IAeWebServicesDeployer deployer = getServiceDeployer(aService, null);
      deployer.undeployFromWebServiceContainer(aService);
   }
   
   /**
    * Returns the handler specified by the deployer type, unless overridden by policy on the service
    * 
    * @param aService
    * @param aDeployerType
    * @return deployer for service
    * @throws AeException
    */
   protected IAeWebServicesDeployer getServiceDeployer(IAeServiceDeploymentInfo aService, String aDeployerType) throws AeException
   {
      IAeWebServicesDeployer delegate = (IAeWebServicesDeployer) getDelegate(aDeployerType);
      
      if (aService == null)
         return delegate;
      
      if (AeUtil.isNullOrEmpty(aService.getPolicies()))
         return delegate;
      
      String policytype = AeEngineFactory.getPolicyMapper().getDeploymentHandler(aService.getPolicies());
      if (!AeUtil.isNullOrEmpty(policytype))
      {
         delegate = (IAeWebServicesDeployer) getDelegate(policytype);
      }
      
      return delegate;
   }
   
   /**
    * Resolves policy references for service deployment
    * 
    * @param aService
    * @throws AeException
    */
   protected void resolveServicePolicies(IAeServiceDeploymentInfo aService) throws AeException
   {
      if (!AeUtil.isNullOrEmpty(aService.getPolicies()))
      {
         IAeContextWSDLProvider wsdlProvider = AeEngineFactory.getDeploymentProvider().findCurrentDeployment(aService.getProcessQName());
         List policies = AeWSDLPolicyHelper.resolvePolicyReferences(wsdlProvider, aService.getPolicies());
         if (!AeUtil.isNullOrEmpty(policies))
         {
            aService.getPolicies().clear();
            aService.getPolicies().addAll(policies);
         }
      }
   }

}
