//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/preprocess/strategies/wsio/AeWSBPELMessageDataProducerStrategyMatcher.java,v 1.2 2006/09/11 23:06:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio; 


/**
 * Contains the valid strategy patterns for WS-BPEL 2.0 
 */
public class AeWSBPELMessageDataProducerStrategyMatcher extends AeBaseMessageDataStrategyMatcher
{
   /**
    * Ctor 
    */
   public AeWSBPELMessageDataProducerStrategyMatcher()
   {
      super();
   }
   
   /**
    * Loads the map with all of the valid patterns
    */
   protected void initMaps()
   {
      super.initMaps();
      
      // element variant
      AeMessageDataSpec spec = new AeMessageDataSpec();
      spec.setElementVariable();
      getProducerMap().put(spec, IAeMessageDataStrategyNames.ELEMENT_VARIABLE);
      getConsumerMap().put(spec, IAeMessageDataStrategyNames.ELEMENT_VARIABLE);

      // toPart variant
      spec = new AeMessageDataSpec();
      spec.setToParts();
      getProducerMap().put(spec, IAeMessageDataStrategyNames.TO_PARTS);

      // fromPart variant
      spec = new AeMessageDataSpec();
      spec.setFromParts();
      getConsumerMap().put(spec, IAeMessageDataStrategyNames.FROM_PARTS);

      // empty message variant
      spec = new AeMessageDataSpec();
      getProducerMap().put(spec, IAeMessageDataStrategyNames.EMPTY_MESSAGE);
      getConsumerMap().put(spec, IAeMessageDataStrategyNames.EMPTY_MESSAGE);
   }
}
 