//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeBPWSFaultHandlersValidator.java,v 1.3 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.util.AeUtil;

/**
 * model provides validation for the BPWS faultHandlers def
 */
public class AeBPWSFaultHandlersValidator extends AeFaultHandlersValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeBPWSFaultHandlersValidator(AeFaultHandlersDef aDef)
   {
      super(aDef);
   }
      
   /**
    * 
    * Overrides method to 
    * @see org.activebpel.rt.bpel.def.validation.activity.scope.AeFaultHandlersValidator#reportDuplicateCatch(org.activebpel.rt.bpel.def.validation.activity.scope.AeBaseCatchValidator)
    */
   protected void reportDuplicateCatch(AeBaseCatchValidator baseCatch)
   {
      getReporter().reportProblem(BPWS_ILLEGAL_FH_CONSTRUCTS_CODE, ERROR_ILLEGAL_FH_CONSTRUCTS, null, baseCatch.getDefinition());
   }
   
   /**
    * Overrides method to
    * @see org.activebpel.rt.bpel.def.validation.activity.scope.AeFaultHandlersValidator#createSpecValidator(org.activebpel.rt.bpel.def.validation.activity.scope.AeBaseCatchValidator)
    */
   protected IAeCatchValidatorSpec createSpecValidator(AeBaseCatchValidator baseCatch)
   {
      return new AeBPWSCatchValidatorSpec(baseCatch.getDef());
   }
   
   /**
    * Nested class to create a object of AeBPWSCatchValidatorSpec for comparing the two 
    * catch element. Extend AeBaseCatchValidatorSpec to handle BPWS specific faultVaraible. 
    */
   protected static class AeBPWSCatchValidatorSpec extends AeBaseCatchValidatorSpec
   {

      /**
       * ctor
       * @param aDef
       */
      public AeBPWSCatchValidatorSpec(AeCatchDef aDef)
      {
         super(aDef);
      }
      
      /**
       * Equality is determined by comparing the AeCatchSpec of the fault handler and if the 
       * spec is equal then compare the faultName, faultVariable, falutElementName and faultMessageType.
       * @see java.lang.Object#equals(java.lang.Object)
       */    
      public boolean equals(Object aObject)
      {
         if ( aObject == null || !(aObject instanceof AeBPWSCatchValidatorSpec))
            return false;

         AeBPWSCatchValidatorSpec otherSpec = (AeBPWSCatchValidatorSpec)aObject;         
         return super.equals(aObject) && AeUtil.compareObjects(getCatchDef().getFaultVariable(), otherSpec.getCatchDef().getFaultVariable());
      }

      /**
       * @see java.lang.Object#hashCode()
       */
      public int hashCode()
      {
         return super.hashCode() + AeUtil.getSafeString(getCatchDef().getFaultVariable()).hashCode();  
      }
   }
      
}
 