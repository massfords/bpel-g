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

import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Wraps the AeServiceDeploymentInfo array for the deployed services listing.
 */
public class AeDeployedServicesBean
{
   /** Deployed service details. */   
   protected IAeServiceDeploymentInfo[] mDetails;
   /** Pointer to current index. */
   protected int mCurrentIndex;
   
   /**
    * Constructor.  Initializes the service deployment details array.
    */
   public AeDeployedServicesBean()
   {
      mDetails = AeEngineFactory.getEngineAdministration().getDeployedServices();      
   }
   
   /**
    * Size accessor.
    * @return The number of detail rows.
    */
   public int getDetailSize()
   {
      if (mDetails == null)
         return 0;
      
      return mDetails.length;
   }
   
   /**
    * Indexed accessor.
    * @param aIndex
    */
   public IAeServiceDeploymentInfo getDetail(int aIndex)
   {
      setCurrentIndex(aIndex);
      return mDetails[aIndex];
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