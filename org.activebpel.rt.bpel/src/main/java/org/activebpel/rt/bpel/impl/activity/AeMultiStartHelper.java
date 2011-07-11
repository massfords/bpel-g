// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeMultiStartHelper.java,v 1.14 2006/10/26 13:52:59 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.IAeIMACorrelations;

/**
 * A Helper class to check if the <code>receive</code> or <code>onMessage</code>
 * that is about to execute is part of a multi-start pattern. If the process
 * defines multiple start points and the activity executing isn't the one that's
 * starting the process then it should wait until the start activity has finished
 * executing and initialized the correlation sets. After this happens, the non-starting
 * activity will be notified via a callback that it can queue itself for execution.
 */
public class AeMultiStartHelper
{
   /**
    * If the activity is a create instance then we need to check to see if we
    * were the activity that kicked off the process. If not, then we cannot queue
    * ourselves until our correlated sets are ready. Instead, we'll wait for the
    * starting activity to initialize the correlation sets and call us back to proceed.
    */
   public static boolean checkForMultiStartBehavior(IAeMessageReceiverActivity aActivity)
      throws AeBusinessProcessException
   {
      boolean okToQueue = true; 
      if (aActivity.getProcess().isStartMessageAvailable() && aActivity.canCreateInstance())
      {
         if ( ! isReceiveDataQueued(aActivity) )
         {
            // if the start message doesn't match what we're looking for, then there
            // is no reason for us to get queued. 
            //  
            okToQueue = false;
            
            if ( ! isCorrelatedDataAvailable(aActivity) )  
            {
               // In a multi-start setup, the createInstances must use the same
               // correlation sets. 
               // As such, the facts that... 
               //    1)our data wasn't present
               //    2)our cs aren't initialized
               // ...means that some other receive/onMessage is going to be starting
               // the process and I should wait for them before queing myself.
               // ergo, add self as listener to each of the cs (make this happen 
               // in the is check)
               setupCorrelationListener(aActivity);
            }
         }
      }
      return okToQueue;
   }
   
   /**
    * Returns true if the receive's data is queued by the process. 
    */
   private static boolean isReceiveDataQueued(IAeMessageReceiverActivity aActivity) throws AeBusinessProcessException
   {
      return aActivity.getProcess().isReceiveDataQueued(aActivity.getPartnerLinkOperationImplKey());
   }

   /**
    * Returns true if all of the receive's correlation sets are initialized.
    * If this method returns false, then the caller should add themselves as
    * a listener on the correlation set to get a callback when it gets initialized
    */
   public static boolean isCorrelatedDataAvailable(IAeMessageReceiverActivity aActivity) throws AeBusinessProcessException
   {
      boolean correlatedDataAvailable = true;
      IAeIMACorrelations receiveCorrelations = aActivity.getCorrelations();
      if (receiveCorrelations != null)
      {
         for(Iterator iter = receiveCorrelations.getCorrelationDefs(); iter.hasNext() && correlatedDataAvailable; )
         {
            AeCorrelationDef corrDef = (AeCorrelationDef)iter.next();
            AeCorrelationSet corrSet = aActivity.findCorrelationSet(corrDef.getCorrelationSetName());
            if(!corrSet.isInitialized())
            {
               correlatedDataAvailable = false;            
            }
         }
      }
      return correlatedDataAvailable;
   }
   
   /**
    * Walks the correlation sets and adds self as a listener on each of the 
    * correlation sets that aren't initialized.
    */
   private static void setupCorrelationListener(IAeMessageReceiverActivity aActivity) throws AeBusinessProcessException
   {
      for(Iterator iter = aActivity.getCorrelations().getCorrelationDefs(); iter.hasNext(); )
      {
         AeCorrelationDef corrDef = (AeCorrelationDef)iter.next();
         AeCorrelationSet corrSet = aActivity.findCorrelationSet(corrDef.getCorrelationSetName());
         if(!corrSet.isInitialized())
         {
            corrSet.addCorrelationListener(aActivity);
         }
      }
   }
}
