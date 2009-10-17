//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/preprocess/strategies/wsio/AeBaseMessageDataStrategyMatcher.java,v 1.1 2006/09/11 23:06:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio; 

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;

/**
 * Base class for message data consumer and producer strategy matchers.  
 */
public abstract class AeBaseMessageDataStrategyMatcher implements IAeMessageDataStrategyMatcher
{
   /** map of producer specs to strategy names */
   private Map mProducerMap = new HashMap();
   /** map of consumer specs to strategy names */
   private Map mConsumerMap = new HashMap();
   
   /**
    * Ctor inits the maps 
    */
   public AeBaseMessageDataStrategyMatcher()
   {
      initMaps();
   }

   /**
    * Loads the map with all of the valid patterns
    */
   protected void initMaps()
   {
      AeMessageDataSpec spec = new AeMessageDataSpec();
      spec.setMessageVariable();
      getProducerMap().put(spec, IAeMessageDataStrategyNames.MESSAGE_VARIABLE);
      getConsumerMap().put(spec, IAeMessageDataStrategyNames.MESSAGE_VARIABLE);
   }

   /**
    * Getter for the map
    */
   protected Map getProducerMap()
   {
      return mProducerMap;
   }

   /**
    * Getter for the map
    */
   protected Map getConsumerMap()
   {
      return mConsumerMap;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio.IAeMessageDataStrategyMatcher#getProducerStrategy(org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef)
    */
   public String getProducerStrategy(IAeMessageDataProducerDef aDef)
   {
      return (String) getProducerMap().get(AeMessageDataSpec.create(aDef));
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio.IAeMessageDataStrategyMatcher#getConsumerStrategy(org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef)
    */
   public String getConsumerStrategy(IAeMessageDataConsumerDef aDef)
   {
      return (String) getConsumerMap().get(AeMessageDataSpec.create(aDef));
   }
}
 