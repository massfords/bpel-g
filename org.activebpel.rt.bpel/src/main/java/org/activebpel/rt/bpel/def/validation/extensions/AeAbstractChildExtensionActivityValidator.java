//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeAbstractChildExtensionActivityValidator.java,v 1.1 2007/10/12 16:09:48 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.extensions;

import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator;

/**
 * Base class for validating child extension activity def
 */
public class AeAbstractChildExtensionActivityValidator extends AeActivityValidator
{

   /**
    * Constructor.
    * @param aBaseDef
    */
   public AeAbstractChildExtensionActivityValidator(AeChildExtensionActivityDef aBaseDef)
   {
      super(aBaseDef);
   }
   
   /**
    * Convenience method for getting the def already cast.
    */
   protected AeChildExtensionActivityDef getDef()
   {
      return (AeChildExtensionActivityDef) getDefinition();
   }

}
