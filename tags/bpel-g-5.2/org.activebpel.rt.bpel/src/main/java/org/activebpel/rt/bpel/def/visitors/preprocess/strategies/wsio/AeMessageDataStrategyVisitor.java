//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/preprocess/strategies/wsio/AeMessageDataStrategyVisitor.java,v 1.3 2007/11/15 02:24:40 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio; 

import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;

/**
 * Visits each invoke and reply in order to set the strategy hint on the def
 */
public class AeMessageDataStrategyVisitor extends AeAbstractDefVisitor
{
   /** matcher determines if the invoke/reply conform to one of the prescribed patterns */
   private IAeMessageDataStrategyMatcher mMatcher;
   
   /**
    * Ctor accepts the matcher
    * @param aMatcher
    */
   public AeMessageDataStrategyVisitor(IAeMessageDataStrategyMatcher aMatcher)
   {
      setMatcher(aMatcher);
      setTraversalVisitor( new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      determineProducerStrategy(aDef);
      determineConsumerStrategy(aDef);
      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef aDef)
   {
      determineProducerStrategy(aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      determineConsumerStrategy(aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      determineConsumerStrategy(aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      determineConsumerStrategy(aDef);
      super.visit(aDef);
   }

   /**
    * Determines the strategy for the message data producer and sets it 
    * on the def
    * @param aDef
    */
   protected void determineProducerStrategy(IAeMessageDataProducerDef aDef)
   {
      // pass the def to the strategy matcher (as an interface)
      // set the strategy from the matcher on the def
      String strategy = getMatcher().getProducerStrategy(aDef);
      aDef.setMessageDataProducerStrategy(strategy);
   }

   /**
    * Determines the strategy for the message data producer and sets it 
    * on the def
    * @param aDef
    */
   protected void determineConsumerStrategy(IAeMessageDataConsumerDef aDef)
   {
      // pass the def to the strategy matcher (as an interface)
      // set the strategy from the matcher on the def
      String strategy = getMatcher().getConsumerStrategy(aDef);
      aDef.setMessageDataConsumerStrategy(strategy);
   }

   /**
    * @return Returns the matcher.
    */
   public IAeMessageDataStrategyMatcher getMatcher()
   {
      return mMatcher;
   }

   /**
    * @param aMatcher The matcher to set.
    */
   public void setMatcher(IAeMessageDataStrategyMatcher aMatcher)
   {
      mMatcher = aMatcher;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      if (aDef.getExtensionObject() != null)
      {
         // producer 
         IAeMessageDataProducerDef producerDef = (IAeMessageDataProducerDef) aDef.getExtensionObject().getAdapter(IAeMessageDataProducerDef.class);
         if (producerDef != null)
         {
            determineProducerStrategy(producerDef);
         }
         
         // consumer
         IAeMessageDataConsumerDef consumerDef = (IAeMessageDataConsumerDef) aDef.getExtensionObject().getAdapter(IAeMessageDataConsumerDef.class);
         if (consumerDef != null)
         {
            determineConsumerStrategy(consumerDef);
         }
      }
      super.visit(aDef);
   }
}
 