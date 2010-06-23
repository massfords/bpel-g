//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeJoinVisitor.java,v 1.4 2006/09/27 00:36:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;

/**
 * Visits each correlation and records whether the correlation is an initiate="yes" on the correlationSet.
 * If a correlationSet can be initiated from multiple activities then we need to take this into consideration
 * when routing an inbound receive since the cs may or may not be populated when the IMA enters the queue.
 */
public class AeJoinVisitor extends AeAbstractDefVisitor
{
   /** maps the correlationSetDef to a object which describes its usage */
   private Map mCSToUsageMap = new HashMap();

   /**
    * no arg ctor 
    */
   public AeJoinVisitor()
   {
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      super.visit(aDef);
      
      // walk all of the correlationSet's we've encountered and mark those that have multiple initiate points w/ the "join" flag
      for (Iterator iter = getUsageMap().entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry) iter.next();
         AeCorrelationSetDef csDef = (AeCorrelationSetDef) entry.getKey();
         AeCorrelationSetUsage usage = (AeCorrelationSetUsage) entry.getValue();
         
         if (usage.isJoin())
         {
            csDef.setJoinStyle(true);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
    */
   public void visit(AeCorrelationDef aDef)
   {
      super.visit(aDef);
      
      if (aDef.isInitiate())
      {
         if (aDef.getParent().getParent() instanceof AeOnEventDef)
         {
            addCorrelationInitiation(AeDefUtil.findCorrSetByName(aDef.getCorrelationSetName(), ((AeOnEventDef)aDef.getParent().getParent()).getContext()));
         }
         else
         {
            addCorrelationInitiation(AeDefUtil.findCorrSetByName(aDef.getCorrelationSetName(), aDef));
         }
      }
   }

   /**
    * Getter for the usage map
    */
   protected Map getUsageMap()
   {
      return mCSToUsageMap;
   }
   
   /**
    * Records the initiation of the correlationSet. If there is already an initiation
    * recorded for this correlationSet then that usage is marked as being a "join".
    * @param aDef
    */
   protected void addCorrelationInitiation(AeCorrelationSetDef aDef)
   {
      AeCorrelationSetUsage usage = (AeCorrelationSetUsage) getUsageMap().get(aDef);
      if (usage == null)
      {
         usage = new AeCorrelationSetUsage();
         getUsageMap().put(aDef, usage);
      }
      else
      {
         usage.addInitiateYes();
      }
   }

   /**
    * simple struct for maintaining the usage info for a correlationSet.
    */
   private static class AeCorrelationSetUsage
   {
      // TODO (MF) the strategy for this visitor doesn't take into consideration whether multiple initiation points for a correlationSet are possible through the process execution. For example, the multiple correlation initiate points may be mutually exclusive through some switch/case nesting or other constructs. 

      /** number of correlation's that are initiating this set */
      private boolean mJoin;
      
      /**
       * ctor
       */
      public AeCorrelationSetUsage()
      {
      }
      
      /**
       * Increments the "yes" counter
       */
      public void addInitiateYes()
      {
         mJoin = true;
      }
      
      /**
       * Returns true if the correlationSet is part of a "join" strategy whereby more than one
       * activity may initiate the correlationSet.
       */
      public boolean isJoin()
      {
         return mJoin;
      }
   }

}
 