// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeDeployedServicesBean.java,v 1.2 2007/02/13 15:52:51 KPease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.bpel.server.admin.jmx.AeServiceDeploymentBean;
import org.activebpel.rt.bpeladmin.war.AeEngineManagementFactory;

/**
 * Wraps the AeServiceDeploymentInfo array for the deployed services listing.
 */
public class AeDeployedServicesBean
{
   /** Deployed service details. */   
   protected List<AeServiceDeploymentBean> mDetails;
   /** Pointer to current index. */
   protected int mCurrentIndex;
   
   /**
    * Constructor.  Initializes the service deployment details array.
    */
   public AeDeployedServicesBean()
   {
      List<AeServiceDeploymentBean> deployedServices = AeEngineManagementFactory.getBean().getDeployedServices();
      mDetails = new ArrayList(deployedServices);      
   }
   
   /**
    * Size accessor.
    * @return The number of detail rows.
    */
   public int getDetailSize()
   {
      if (mDetails == null)
         return 0;
      
      return mDetails.size();
   }
   
   /**
    * Indexed accessor.
    * @param aIndex
    */
   public AeServiceDeploymentBean getDetail(int aIndex)
   {
      setCurrentIndex(aIndex);
      return mDetails.get(aIndex);
   }
   
   /**
    * Setter for the current index.
    * @param aIndex
    */
   protected void setCurrentIndex(int aIndex)
   {
      mCurrentIndex = aIndex;
   }

   /**
    * Accessor for the current index.
    */
   public int getCurrentIndex()
   {
      return mCurrentIndex;
   }
}