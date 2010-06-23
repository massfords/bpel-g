//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeIMACorrelations.java,v 1.2 2008/02/17 21:37:09 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support; 

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.impl.AeCorrelationViolationException;
import org.activebpel.rt.bpel.impl.IAeBpelObject;

/**
 * Handles requests for correlation info needed when queuing the message 
 * receiver. This correlation info will be used to detect conflicting receives 
 * and also to add the message receiver to the message queue.
 */
public class AeIMACorrelations extends AeCorrelationsImpl implements IAeIMACorrelations
{
   /** 
    * filter used to include correlation set paths in the test for conflicting 
    * receives 
    */
   private IAeCorrelationSetFilter mFilter;
   
   /** 
    * Legacy implementation that includes all correlation sets as part of 
    * conflicting receive test. Applies to BPWS processes only 
    */
   public static final IAeCorrelationSetFilter ALL = new IAeCorrelationSetFilter()
   {
      /**
       * @see org.activebpel.rt.bpel.impl.activity.support.AeIMACorrelations.IAeCorrelationSetFilter#accept(org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet)
       */
      public boolean accept(AeCorrelationSet aCorrelationSet)
      {
         return true;
      }
   };
   
   /** 
    * Includes correlation sets that are intialized. Applies to WS-BPEL 
    * processes only. 
    */
   public static final IAeCorrelationSetFilter INITIALIZED = new IAeCorrelationSetFilter()
   {
      /**
       * @see org.activebpel.rt.bpel.impl.activity.support.AeIMACorrelations.IAeCorrelationSetFilter#accept(org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet)
       */
      public boolean accept(AeCorrelationSet aCorrelationSet)
      {
         return aCorrelationSet.isInitialized();
      }
   };
   
   /**
    * Ctor
    * @param aDef
    * @param aParent
    */
   public AeIMACorrelations(AeCorrelationsDef aDef, IAeBpelObject aParent)
   {
      super(aDef, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeIMACorrelations#getInitiatedProperties()
    */
   public Map getInitiatedProperties() throws AeCorrelationViolationException
   {
      HashMap correlation = new HashMap();
      for(Iterator iter = getInitiatedCorrelationDefs(); iter.hasNext(); )
      {
         AeCorrelationDef corrDef = (AeCorrelationDef)iter.next();
         AeCorrelationSet corrSet = getParent().findCorrelationSet(corrDef.getCorrelationSetName());
         if(corrSet.isInitialized() || corrDef.shouldAlreadyBeInitiated() )
         {
            correlation.putAll(corrSet.getPropertyValues());
         }
      }
      return correlation;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeIMACorrelations#getCSPathsForConflictingReceives()
    */
   public Set getCSPathsForConflictingReceives()
   {
      HashSet set = new HashSet();
      for(Iterator iter = getInitiatedCorrelationDefs(); iter.hasNext(); )
      {
         // The test for conflicting receives is based on duplicate receives
         // with the same plink/op/cs. The original release included ALL of
         // the correlationSets for the receive. The current code only 
         // includes correlation sets that were initiated at the time of the
         // receive's execution. It is possible that recalculating the 
         // key as part of a process restore could result in a conflict if the 
         // receive was queued with a join-style correlation set that has since 
         // been initiated. In order to prevent this, we will not include any
         // join style correlations as part of the conflicting receives. 
         // 
         // This example isn't a "real-world" case but here it is...
         // - R1 queues with CS1 and CS2. CS1 is initiated, CS2 is join style
         //   but is not initiated yet
         // - CS2 becomes initiated through an invoke or some receive
         // - R2 queues with CS1 and CS2. Since CS2 is initiated now, it becomes
         //   part of the key for R2 so R2 will not conflict with R1
         // - process state is saved
         // - process state gets restored
         // - code above recalculates conflictingReceive key for R1 and R2.
         //   Since CS2 is now initiated, it becomes part of the key for R1
         //   and causes a conflict.
         //
         // Note: The alternative to this is to store the full set of paths that
         //       the IMA was queued with as part of its process state. I didn't
         //       go with this approach since it would add a small amount of 
         //       data to the process state and it seems like such an unlikely
         //       thing to happen. If there were a more realistic use case then
         //       storing the data in the process state would be the right thing
         //       to do.
         AeCorrelationDef corrDef = (AeCorrelationDef)iter.next();
         if (!AeCorrelationDef.INITIATE_JOIN.equals(corrDef.getInitiate()))
         {
            AeCorrelationSet corrSet = getParent().findCorrelationSet(corrDef.getCorrelationSetName());
            if (getFilter().accept(corrSet))
               set.add(corrSet.getLocationPath());
         }
      }
      return set;
   }
   
   /**
    * Returns an iterator of {@link AeCorrelationDef} for use in the above 
    * methods.
    * 
    * This method exists to provide an override point for the onEvent impl.
    */
   protected Iterator getInitiatedCorrelationDefs()
   {
      return getCorrelationDefs();
   }
   
   /**
    * @return Returns the filter.
    */
   public IAeCorrelationSetFilter getFilter()
   {
      return mFilter;
   }

   /**
    * @param aFilter The filter to set.
    */
   public void setFilter(IAeCorrelationSetFilter aFilter)
   {
      mFilter = aFilter;
   }

   /**
    * Filters the correlation sets used to produce set of paths for 
    * conflictingReceives test.
    * 
    * When queuing a receive we need to detect that it doesn't conflict with any 
    * other queued receives. The test for a conflicting receive helps avoid the 
    * situation where more than one receive matches. While it is possible that 
    * more than one receive may match to a message, we will reject any receives 
    * that have the same partnerLink, operation, and correlation sets.
    * 
    * The code for an earlier release incorrectly included all correlation set 
    * paths instead of only including initiated correlation sets. In order to 
    * avoid legacy issues with the existing processes we will preserve the old 
    * behavior for the BPEL 1.1 processes. 
    */
   public static interface IAeCorrelationSetFilter
   {
      /**
       * Returns true if this correlation set should be included in the test for 
       * conflicting receives.
       * @param aCorrelationSet
       */
      public boolean accept(AeCorrelationSet aCorrelationSet);
   }
}
 