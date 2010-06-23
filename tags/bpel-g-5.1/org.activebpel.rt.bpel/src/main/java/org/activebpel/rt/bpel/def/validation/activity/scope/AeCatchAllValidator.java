//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeCatchAllValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import org.activebpel.rt.bpel.def.AeCatchAllDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model for validating the catchAll def
 */
public class AeCatchAllValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeCatchAllValidator(AeCatchAllDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the def
    */
   protected AeCatchAllDef getDef()
   {
      return (AeCatchAllDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if ( getDef().getActivityDef() == null )
         getReporter().reportProblem( BPEL_CATCHALL_EMPTY_FAULT_HANDLER_CODE, ERROR_EMPTY_FAULT_HANDLER, null, getDefinition() );
   }
}
 