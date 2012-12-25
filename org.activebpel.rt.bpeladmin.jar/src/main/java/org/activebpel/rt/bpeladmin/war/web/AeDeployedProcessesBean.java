// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeDeployedProcessesBean.java,v 1.1 2004/08/19 16:19:22 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import org.activebpel.rt.bpeladmin.war.AeEngineManagementFactory;

import bpelg.services.processes.types.GetProcessDeployments;
import bpelg.services.processes.types.ProcessDeployment;
import bpelg.services.processes.types.ProcessDeployments;

/**
 * Wraps the AeProcessDeploymentDetail array for the 
 * deployed processes listing.
 */
public class AeDeployedProcessesBean
{
   /** Deployed process details. */   
   protected final ProcessDeployments mDetails;
   /** Pointer to current index. */
   protected int mCurrentIndex;
   
   /**
    * Constructor.  Initializes the
    * deployment details array.
    */
   public AeDeployedProcessesBean()
   {
      mDetails = AeEngineManagementFactory.getProcessManager().getProcessDeployments(new GetProcessDeployments());      
   }
   
   /**
    * Size accessor.
    * @return The number of detail rows.
    */
   public int getDetailSize()
   {
      if( mDetails == null )
      {
         return 0;
      }
      return mDetails.getProcessDeployment().size();
   }
   
   /**
    * Indexed accessor.
    * @param aIndex
    */
   public ProcessDeployment getDetail( int aIndex )
   {
      setCurrentIndex( aIndex );
      return mDetails.getProcessDeployment().get(aIndex);
   }
   
   /**
    * Setter for the current index.
    * @param aIndex
    */
   protected void setCurrentIndex( int aIndex )
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
