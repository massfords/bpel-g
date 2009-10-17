//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeOnMessageValidator.java,v 1.5 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity; 


import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.IAeActivityCreateInstanceDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.util.AeUtil;

/**
 * model provides validation for the onMessage part of a pick
 */
public class AeOnMessageValidator extends AeOnMessageBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeOnMessageValidator(AeOnMessageDef aDef)
   {
      super(aDef);
   }
   
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeOnMessageBaseValidator#resolveVariable()
    */
   protected AeVariableValidator resolveVariable()
   {
      // Avoid calling getVariableValidator() if there is no variable specified
      // since variable can be null for ws-bpel if it's an empty message. 

      if (AeUtil.notNullOrEmpty(getDef().getVariable()))
         return getVariableValidator(getDef().getVariable(), AeOnMessageDef.TAG_VARIABLE, true, AeVariableValidator.VARIABLE_WRITE_WSIO); 

      return null;
   }

   /**
    * Getter for the def
    */
   protected AeOnMessageDef getDef()
   {
      return (AeOnMessageDef) getDefinition();
   }

   /**
    * Validates:
    * 1. variable exists
    * 2. warns if not create instance and no correlation
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      // calling getVariable() will report an error if the variable is not found
      getVariable();
      
      // check for corr set if parent is a Pick and not a create instance
      //
      boolean isCreateInstance = false;
      AeBaseDef parent = getDef().getParent();
      if ( parent instanceof IAeActivityCreateInstanceDef )
      {
         isCreateInstance = ((IAeActivityCreateInstanceDef)getDef().getParent()).isCreateInstance();
      }

      if ( !isCreateInstance && !getDef().getCorrelationDefs().hasNext())
      {
         getReporter().reportProblem( BPEL_MSG_NO_CORR_SET_NO_CREATE_CODE,
                                    WARNING_NO_CORR_SET_NO_CREATE,
                                    new String[] { AeOnMessageDef.TAG_ON_MESSAGE },
                                    getDef() );
      }
   }
}
 