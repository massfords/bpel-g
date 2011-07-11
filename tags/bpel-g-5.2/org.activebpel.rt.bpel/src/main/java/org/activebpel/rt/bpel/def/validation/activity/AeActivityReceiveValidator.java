//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityReceiveValidator.java,v 1.6 2008/03/20 16:01:32 dvilaverde Exp $
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

import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.message.AeMessagePartsMap;

/**
 * model for validating the receive activity
 */
public class AeActivityReceiveValidator extends AeWSIOActivityValidator
{
   /** variable that we're receiving */
   private AeVariableValidator mVariableModel;
   
   /**
    * ctor
    * @param aDef
    */
   public AeActivityReceiveValidator(AeActivityReceiveDef aDef)
   {
      super(aDef);
   }

   /**
    * Validates:
    * 1. if create instance, record with process
    * 2. if not create instance, issue warning if no correlation
    * @see org.activebpel.rt.bpel.def.validation.activity.AeWSIOActivityValidator#validate()
    */
   public void validate()
   {
      mVariableModel = getVariableValidator(getDef().getVariable(), null, true, AeVariableValidator.VARIABLE_WRITE_WSIO);

      super.validate();

      if (getDef().isCreateInstance())
      {
         getProcessValidator().addCreateInstance(this);
      }
      else if (!getDef().getCorrelationList().hasNext())
      {
         getReporter().reportProblem( BPEL_RCV_NO_CORR_SET_NO_CREATE_CODE,
               WARNING_NO_CORR_SET_NO_CREATE,
               new String[] { AeActivityReceiveDef.TAG_RECEIVE },
               getDefinition());
      }

      AeMessagePartsMap consumerMap = getConsumerMessagePartsMap();
      validateMessageConsumerStrategy(consumerMap, mVariableModel);
   }
   
   /**
    * Getter for the variable
    */
   public AeVariableValidator getVariable()
   {
      return mVariableModel;
   }
   
   /**
    * Getter for the def
    */
   protected AeActivityReceiveDef getDef()
   {
      return (AeActivityReceiveDef) getDefinition();
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
 