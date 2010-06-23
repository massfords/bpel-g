//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/links/AeSourceValidator.java,v 1.2 2006/09/25 01:34:39 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.links; 

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for the source element
 */
public class AeSourceValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeSourceValidator(AeSourceDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.expressions.AeBaseExpressionValidator#validate()
    */
   public void validate()
   {
      super.validate();
      getProcessValidator().getLinkValidator().addSource(getDef(), (AeActivityDef) getParentActivityModel().getDefinition());
   }

   /**
    * Getter for the src def
    */
   protected AeSourceDef getDef()
   {
      return (AeSourceDef) getDefinition();
   }
}
 