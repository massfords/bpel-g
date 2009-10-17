//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/from/AeFromStrategyFactory.java,v 1.4.4.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.from; 


import java.util.Map;

import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.io.readers.def.IAeFromStrategyNames;
import org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationComponentFactory;
import org.activebpel.rt.bpel.impl.activity.assign.IAeFrom;

/**
 * Factory for creating the from def based on its strategy name. This factory
 * assumes that the defs passed to it have already been adorned with their 
 * specific strategy name.
 */
public class AeFromStrategyFactory extends AeCopyOperationComponentFactory
{
   /** singleton instance of the factory */
   private static final AeFromStrategyFactory SINGLETON = new AeFromStrategyFactory();
   
   /**
    * protected ctor to force factory method usage 
    */
   protected AeFromStrategyFactory()
   {
      super();
   }
   
   /**
    * Populates the map with the strategy name to &lt;from&gt; strategery
    *  
    * @see org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationComponentFactory#initMap()
    */
   protected void initMap()
   {
      Map map = getMap();
      map.put(IAeFromStrategyNames.FROM_EXPRESSION, AeFromExpression.class);
      map.put(IAeFromStrategyNames.FROM_LITERAL, AeFromLiteral.class);
      map.put(IAeFromStrategyNames.FROM_PARTNER_LINK, AeFromPartnerLink.class);
      map.put(IAeFromStrategyNames.FROM_PROPERTY_ELEMENT, AeFromPropertyElement.class);
      map.put(IAeFromStrategyNames.FROM_PROPERTY_MESSAGE, AeFromPropertyMessage.class);
      map.put(IAeFromStrategyNames.FROM_PROPERTY_TYPE, AeFromPropertyType.class);
      map.put(IAeFromStrategyNames.FROM_VARIABLE_ELEMENT, AeFromVariableElement.class);
      map.put(IAeFromStrategyNames.FROM_VARIABLE_ELEMENT_QUERY, AeFromVariableElementWithQuery.class);
      map.put(IAeFromStrategyNames.FROM_VARIABLE_MESSAGE, AeFromVariableMessage.class);
      map.put(IAeFromStrategyNames.FROM_VARIABLE_MESSAGE_PART, AeFromVariableMessagePart.class);
      map.put(IAeFromStrategyNames.FROM_VARIABLE_MESSAGE_PART_QUERY, AeFromVariableMessagePartWithQuery.class);
      map.put(IAeFromStrategyNames.FROM_VARIABLE_TYPE, AeFromVariableType.class);
      map.put(IAeFromStrategyNames.FROM_VARIABLE_TYPE_QUERY, AeFromVariableTypeWithQuery.class);
      map.put(IAeFromStrategyNames.FROM_EXTENSION, AeFromExtension.class);
   }
   
   /**
    * Creates the IAeFrom instance from the def object's strategy hint.
    * 
    * @param aDef
    */
   public static IAeFrom createFromStrategy(AeFromDef aDef)
   {
      return (IAeFrom) SINGLETON.create(aDef);
   }
}
 