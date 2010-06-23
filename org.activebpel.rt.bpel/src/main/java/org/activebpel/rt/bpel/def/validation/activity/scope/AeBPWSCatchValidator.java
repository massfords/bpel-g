//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeBPWSCatchValidator.java,v 1.1 2006/09/11 23:06:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import java.util.HashSet;
import java.util.Set;

import org.activebpel.rt.bpel.def.AeCatchDef;

/**
 * model provides the validation for the catchDef 
 */
public class AeBPWSCatchValidator extends AeBaseCatchValidator
{
   /** valid catch def patterns for BPWS */
   private static final Set BPWS_PATTERNS = new HashSet();

   static 
   {
      // catch w/ name only
      AeCatchSpec spec = new AeCatchSpec();
      spec.setFaultName();
      BPWS_PATTERNS.add(spec);
      
      // catch w/ variable only 
      spec = new AeCatchSpec();
      spec.setFaultVariable();
      BPWS_PATTERNS.add(spec);
      
      // catch w/ name and variable
      spec = new AeCatchSpec();
      spec.setFaultName();
      spec.setFaultVariable();
      BPWS_PATTERNS.add(spec);
   };
   
   /**
    * ctor
    * @param aDef
    */
   public AeBPWSCatchValidator(AeCatchDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.scope.AeBaseCatchValidator#getPatterns()
    */
   protected Set getPatterns()
   {
      return BPWS_PATTERNS;
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.scope.AeBaseCatchValidator#getPatternErrorMessage()
    */
   protected String getPatternErrorMessage()
   {
      return ERROR_BPWS_CATCH_PATTERN;
   }
}
 