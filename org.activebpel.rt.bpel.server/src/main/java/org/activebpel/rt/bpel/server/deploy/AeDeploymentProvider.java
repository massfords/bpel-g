// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeploymentProvider.java,v 1.20 2007/11/02 16:02:04 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;

/**
 * This class manages the deployment plans defined for BPEL processes.
 */
public class AeDeploymentProvider extends AeAbstractDeploymentProvider
{
   /** The deployment plans which are currently deployed */
   private HashMap mDeploymentPlans = new HashMap();

   /**
    * Constructor for the deployment provider.
    */
   public AeDeploymentProvider(Map aMap)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.server.IAeDeploymentProvider#findCurrentDeployment(javax.xml.namespace.QName)
    */
   public IAeProcessDeployment findCurrentDeployment(QName aProcessName) throws AeBusinessProcessException
   {
      return (IAeProcessDeployment)mDeploymentPlans.get(aProcessName);
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAePlanManager#findCurrentPlan(javax.xml.namespace.QName)
    */
   public IAeProcessPlan findCurrentPlan(QName aProcessName) throws AeBusinessProcessException
   {
      return findCurrentDeployment(aProcessName);
   }

   /**
    * @see org.activebpel.rt.bpel.server.IAeDeploymentProvider#addDeploymentPlan(org.activebpel.rt.bpel.server.IAeProcessDeployment)
    */
   public void addDeploymentPlan(IAeProcessDeployment aDeploymentPlan)
   {
      mDeploymentPlans.put(aDeploymentPlan.getProcessDef().getQName(), aDeploymentPlan);
      
      AeProcessDeployment deployment = (AeProcessDeployment) aDeploymentPlan;
      
      AeServiceMap.getInstance().addServiceData(deployment.getServiceInfos());
   }

   /**
    * @see org.activebpel.rt.bpel.server.IAeDeploymentProvider#getDeployedPlans()
    */
   public Iterator getDeployedPlans()
   {
      synchronized(mDeploymentPlans)
      {
         return new ArrayList(mDeploymentPlans.values()).iterator();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.IAeDeploymentProvider#removeDeploymentPlan(javax.xml.namespace.QName)
    */
   public void removeDeploymentPlan(QName aProcessName)
   {
      mDeploymentPlans.remove( aProcessName );
      AeServiceMap.getInstance().processUndeployed(aProcessName);
   }

   /**
    * @see org.activebpel.rt.bpel.server.IAeDeploymentProvider#findDeploymentPlan(long, javax.xml.namespace.QName)
    */
   public IAeProcessDeployment findDeploymentPlan(long aProcessId, QName aProcessName) throws AeBusinessProcessException
   {
      return (IAeProcessDeployment) mDeploymentPlans.get(aProcessName);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.IAeDeploymentProvider#getRoutingInfoByServiceName(java.lang.String)
    */
   public AeRoutingInfo getRoutingInfoByServiceName(String aServiceName) throws AeBusinessProcessException
   {
      AeRoutingInfo routingInfo = null;

      IAeServiceDeploymentInfo data = AeServiceMap.getInstance().getServiceData(aServiceName);
      if (data != null)
      {
         IAeProcessDeployment deployment = findCurrentDeployment(data.getProcessQName());
         
         if (deployment != null)
         {
            routingInfo = new AeRoutingInfo(deployment, data);
         }
      }
      
      if (routingInfo == null)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeDeploymentProvider.NO_PLAN_FOR_SERVICE", aServiceName)); //$NON-NLS-1$
      }
      
      return routingInfo;
   }
}
