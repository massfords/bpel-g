//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/to/AeToStrategyFactory.java,v 1.5.4.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign.to; 

import java.util.Map;

import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.io.readers.def.IAeToStrategyNames;
import org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationComponentFactory;
import org.activebpel.rt.bpel.impl.activity.assign.IAeTo;

/**
 * Factory for creating strategies for implementing the &lt;to&gt; portion of a copy
 * operation.
 */
public class AeToStrategyFactory extends AeCopyOperationComponentFactory
{
   /** singleton instance */
   private static final AeToStrategyFactory SINGLETON = new AeToStrategyFactory();
   
   /**
    * private ctor to enforce singleton
    */
   private AeToStrategyFactory()
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperationComponentFactory#initMap()
    */
   protected void initMap()
   {
      Map map = getMap();

      map.put(IAeToStrategyNames.TO_PARTNER_LINK, AeToPartnerLink.class);
      map.put(IAeToStrategyNames.TO_PROPERTY_ELEMENT, AeToPropertyElement.class);
      map.put(IAeToStrategyNames.TO_PROPERTY_MESSAGE, AeToPropertyMessage.class);
      map.put(IAeToStrategyNames.TO_PROPERTY_TYPE, AeToPropertyType.class);
      map.put(IAeToStrategyNames.TO_VARIABLE_ELEMENT, AeToVariableElement.class);
      map.put(IAeToStrategyNames.TO_VARIABLE_ELEMENT_QUERY, AeToVariableElementWithQuery.class);
      map.put(IAeToStrategyNames.TO_VARIABLE_MESSAGE, AeToVariableMessage.class);
      map.put(IAeToStrategyNames.TO_VARIABLE_MESSAGE_PART, AeToVariableMessagePart.class);
      map.put(IAeToStrategyNames.TO_VARIABLE_MESSAGE_PART_QUERY, AeToVariableMessagePartWithQuery.class);
      map.put(IAeToStrategyNames.TO_VARIABLE_TYPE, AeToVariableType.class);
      map.put(IAeToStrategyNames.TO_VARIABLE_TYPE_QUERY, AeToVariableTypeWithQuery.class);
      // Note: ToExpression Should never get created because we create specific strategies depending
      // on the structure of the expression (ToVariableMessagePart, ToVariableMessagePartQuery, 
      // ToVariableType, ToVariableElement, or ToVariableElementQuery).  This is done by the
      // visitor that sets the strategy on the def.
      map.put(IAeToStrategyNames.TO_EXPRESSION, null);
      map.put(IAeToStrategyNames.TO_EXTENSION, AeToExtension.class);
   }
   
   /**
    * Creates the strategy used to determine the location of the data for the copy operation.
    * @param aDef
    */
   public static IAeTo createToStrategy(AeToDef aDef)
   {
      return (IAeTo) SINGLETON.create(aDef);
   }
}
 