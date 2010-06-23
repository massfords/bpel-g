//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeOnMessageBaseValidator.java,v 1.7 2008/02/17 21:37:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.message.AeMessagePartsMap;

public abstract class AeOnMessageBaseValidator extends AeWSIOActivityValidator
{
   /** variable that is being received */
   protected AeVariableValidator mVariable;

   /**
    * Ctor accepts the def
    * @param aDef
    */
   public AeOnMessageBaseValidator(AeBaseDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeWSIOActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      AeMessagePartsMap consumerMap = getConsumerMessagePartsMap();
      AeVariableValidator variable = getVariable();
      validateMessageConsumerStrategy(consumerMap, variable);
   }

   /**
    * Gets the variable for the onMessage
    * aMode indicates the read/write usage of the variable
    */
   protected AeVariableValidator getVariable()
   {
      if (mVariable == null)
      {
         mVariable = resolveVariable(); 
      }
      return mVariable;
   }
   
   /**
    * Resolves the variable
    */
   protected abstract AeVariableValidator resolveVariable();
   
   
   /**
    * Getter for the def
    */
   private IAeReceiveActivityDef getDef()
   {
      return (IAeReceiveActivityDef) getDefinition();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAePartnerLinkOperationUser#getOperation()
    */
   public String getOperation()
   {
      return getDef().getOperation();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAePartnerLinkOperationUser#getPortType()
    */
   public QName getPortType()
   {
      return getDef().getPortType();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAePartnerLinkOperationUser#isMyRole()
    */
   public boolean isMyRole()
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.IAePartnerLinkOperationUser#isPartnerRole()
    */
   public boolean isPartnerRole()
   {
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validateNCName(boolean)
    */
   protected void validateNCName(boolean aRequired)
   {
      // side effect of extending wsio activity, onMessage doesn't have a name
   }
}
 