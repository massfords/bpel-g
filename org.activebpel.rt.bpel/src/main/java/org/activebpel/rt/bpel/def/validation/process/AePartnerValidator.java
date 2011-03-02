//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/process/AePartnerValidator.java,v 1.2 2006/09/11 23:06:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.process; 

import java.util.Iterator;

import org.activebpel.rt.bpel.def.AePartnerDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * Validates the partner def
 */
public class AePartnerValidator extends AeBaseValidator
{

   /**
    * ctor takes the partner def
    * @param aDef
    */
   public AePartnerValidator(AePartnerDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Getter for the def
    */
   protected AePartnerDef getDef()
   {
      return (AePartnerDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      // check the partner links listed by this partner.
      //
      for ( Iterator iter = getDef().getPartnerLinks() ; iter.hasNext() ; )
      {
         String name = (String)iter.next();
         // validates that the plink exists and records a reference to it
         getPartnerLinkValidator(name, true);
      }
   }
}
 