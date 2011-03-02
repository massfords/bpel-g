//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeRoutingInfo.java,v 1.3 2007/09/02 17:17:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;

/**
 * Simple container for returning the routing information for a request.  
 */
public class AeRoutingInfo
{
   /** the deployment we'll route to */
   private IAeProcessDeployment mDeployment;
   /** the service being hit */
   private IAeServiceDeploymentInfo mServiceData;
   
   /**
    * Ctor
    * 
    * @param aDeployment
    * @param aData
    */
   public AeRoutingInfo(IAeProcessDeployment aDeployment, IAeServiceDeploymentInfo aData)
   {
      setDeployment(aDeployment);
      setServiceData(aData);
   }
   
   /**
    * @return Returns the deployment.
    */
   public IAeProcessDeployment getDeployment()
   {
      return mDeployment;
   }
   
   /**
    * @param aDeployment The deployment to set.
    */
   public void setDeployment(IAeProcessDeployment aDeployment)
   {
      mDeployment = aDeployment;
   }
   
   /**
    * @return Returns the serviceData.
    */
   public IAeServiceDeploymentInfo getServiceData()
   {
      return mServiceData;
   }
   
   /**
    * @param aServiceData The serviceData to set.
    */
   public void setServiceData(IAeServiceDeploymentInfo aServiceData)
   {
      mServiceData = aServiceData;
   }
   
   /**
    * Returns true if the service operation is implemented by this process
    * @param aPortType
    * @param aOperation
    */
   public boolean isImplemented(QName aPortType, String aOperation)
   {
      AePartnerLinkDef plinkDef = getDeployment().getProcessDef().findPartnerLink(getServiceData().getPartnerLinkDefKey());
      QName portTypeQName = plinkDef.getMyRolePortType();
      if (aPortType.equals(portTypeQName))
      {
         AeImplementsOperationVisitor visitor = new AeImplementsOperationVisitor(aOperation, plinkDef);
         getDeployment().getProcessDef().accept(visitor);
         return visitor.isFound();
      }
      return false;
   }
}
 