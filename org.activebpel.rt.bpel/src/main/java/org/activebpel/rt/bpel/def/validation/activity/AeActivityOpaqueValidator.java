//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/AeActivityOpaqueValidator.java,v 1.3 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;

/**
 * Model for validating ws-bpel 2.x abstract process's opaque activity.
 */
public class AeActivityOpaqueValidator extends AeActivityValidator
{

   /**
    * Ctor.
    * @param aDef
    */
   public AeActivityOpaqueValidator(AeActivityOpaqueDef aDef)
   {
      super(aDef);
   }
   
   /** 
    * Overrides method to report error if the opaque activity is not part of the abstract process namespace. 
    * @see org.activebpel.rt.bpel.def.validation.IAeValidator#validate()
    */
   public void validate()
   {
      super.validate();
      if (!IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI.equals(getBpelNamespace()) )
      {
         getReporter().reportProblem( BPEL_OPAQUE_ACTIVITY_NOT_ALLOWED_CODE, 
                                       ERROR_OPAQUE_ACTIVITY_NOT_ALLOWED, 
                                       null, 
                                       (AeActivityOpaqueDef)getDefinition() );         
      }
   }

}
