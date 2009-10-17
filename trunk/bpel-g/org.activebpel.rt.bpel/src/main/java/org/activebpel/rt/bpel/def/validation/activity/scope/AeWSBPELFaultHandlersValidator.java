//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeWSBPELFaultHandlersValidator.java,v 1.3 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import org.activebpel.rt.bpel.def.AeFaultHandlersDef;

/**
 * model provides validation for the WSBPEL faultHandlers def
 */
public class AeWSBPELFaultHandlersValidator extends AeFaultHandlersValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeWSBPELFaultHandlersValidator(AeFaultHandlersDef aDef)
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
      getReporter().reportProblem(WSBPEL_ILLEGAL_FH_CONSTRUCTS_CODE, ERROR_ILLEGAL_FH_CONSTRUCTS, null, baseCatch.getDefinition());
   }
}
 