//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/preprocess/strategies/wsio/AeMessageDataSpec.java,v 1.1 2006/08/18 22:20:35 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio; 

import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.def.visitors.preprocess.strategies.AeBaseSpec;

/**
 * Describes how a wsio activity produces or consumes its message data
 */
public class AeMessageDataSpec extends AeBaseSpec
{
   /** constant for the bit that indicates the producer/consumer is using a message variable */
   public static final int MESSAGE_VARIABLE = 1;
   /** constant for the bit that indicates the producer/consumer is using an element variable */
   public static final int ELEMENT_VARIABLE = 2;
   /** constant for the bit that indicates the producer is using a toParts collection */
   public static final int TO_PARTS = 3;
   /** constant for the bit that indicates the consumer is using a toParts collection */
   public static final int FROM_PARTS = 4;
   
   /**
    * Creates the spec from the def
    * @param aDef
    */
   public static AeMessageDataSpec create(IAeMessageDataProducerDef aDef)
   {
      AeMessageDataSpec spec = new AeMessageDataSpec();
      AeVariableDef varDef = aDef.getMessageDataProducerVariable();

      setVariable(varDef, spec);
      
      if (aDef.getToPartsDef() != null)
      {
         spec.setToParts();
      }
      return spec;
   }
   
   /**
    * Creates the spec from the def
    * @param aDef
    */
   public static AeMessageDataSpec create(IAeMessageDataConsumerDef aDef)
   {
      AeMessageDataSpec spec = new AeMessageDataSpec();
      AeVariableDef varDef = aDef.getMessageDataConsumerVariable();

      setVariable(varDef, spec);
      
      if (aDef.getFromPartsDef() != null)
      {
         spec.setFromParts();
      }
      return spec;
   }

   /**
    * Sets the variable bits on the def
    * @param aVariableDef - def of the variable or null if not set on def
    * @param aSpec - the spec that will get the bits set on it
    */
   private static void setVariable(AeVariableDef aVariableDef, AeMessageDataSpec aSpec)
   {
      if (aVariableDef != null)
      {
         if (aVariableDef.isMessageType())
         {
            aSpec.setMessageVariable();
         }
         else if (aVariableDef.isElement())
         {
            aSpec.setElementVariable();
         }
      }
   }

   /**
    * Sets the message variable bit
    */
   public void setMessageVariable()
   {
      set(MESSAGE_VARIABLE);
   }
   
   /**
    * Sets the element variable bit
    */
   public void setElementVariable()
   {
      set(ELEMENT_VARIABLE);
   }
   
   /**
    * Sets the toParts bit
    */
   public void setToParts()
   {
      set(TO_PARTS);
   }
   
   /**
    * Sets the fromParts bit
    */
   public void setFromParts()
   {
      set(FROM_PARTS);
   }
}
 