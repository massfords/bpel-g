//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/links/AeLinkValidator.java,v 1.2 2006/09/25 01:34:39 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.links; 

import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * model provides validation for the link
 */
public class AeLinkValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeLinkValidator(AeLinkDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the link def
    */
   protected AeLinkDef getDef()
   {
      return (AeLinkDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      validateNCName(true);
      getProcessValidator().getLinkValidator().addLink(getDef());
      
      // fixme 2.0 only, warning if link not used
   }
}
 