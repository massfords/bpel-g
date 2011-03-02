//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeExtensionActivityValidator.java,v 1.2 2006/09/25 01:34:39 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.extensions; 

import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator;

/**
 * Model provides validation for an extension activity element.
 */
public class AeExtensionActivityValidator extends AeActivityValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeExtensionActivityValidator(AeExtensionActivityDef aDef)
   {
      super(aDef);
   }
   
   /**
    * Returns the definition.
    */
   protected AeExtensionActivityDef getDef()
   {
      return (AeExtensionActivityDef) getDefinition();
   }
}
 