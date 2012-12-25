//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityReplyValidator.java,v 1.5 2007/09/28 21:45:39 ppatruni Exp $
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

import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.message.AeMessagePartsMap;

/**
 * model provides validation for the reply activity
 */
public class AeActivityReplyValidator extends AeWSIOActivityValidator
{
   /** variable that we're replying with */
   private AeVariableValidator mVariableModel;

   /**
    * ctor
    * @param aDef
    */
   public AeActivityReplyValidator(AeActivityReplyDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the variable
    */
   public AeVariableValidator getVariable()
   {
      return mVariableModel;
   }
   
   /**
    * Validates:
    * 1. variable reference
    * @see org.activebpel.rt.bpel.def.validation.activity.AeWSIOActivityValidator#validate()
    */
   public void validate()
   {
      super.validate();

      // TODO (MF) Validate the variable or toParts against the outgoing message type.
      if (getDef().getToPartsDef() == null)
         mVariableModel = getVariableValidator(getDef().getVariable(), null, true, AeVariableValidator.VARIABLE_READ_WSIO);
      
      AeMessagePartsMap producerMap = getProducerMessagePartsMap();
      if (producerMap == null)
      {
         if (getDef().getFaultName() != null)
         {
            addTypeNotFoundError(ERROR_FAULT_NAME_NOT_FOUND, getDef().getFaultName(), getDefinition());
         }
         else
         {
            reportMessagePartsNotFound(mVariableModel, ERROR_OPERATION_OUT_NOT_FOUND);
         }
      }
      else
      {
         validateMessageProducerStrategy(producerMap, mVariableModel);
      }
   }

   /**
    * Getter for the def
    */
   public AeActivityReplyDef getDef()
   {
      return (AeActivityReplyDef) getDefinition();
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
}
 