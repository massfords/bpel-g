// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeAbstractExtensionAttributeValidator.java,v 1.3 2007/09/26 02:21:03 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.extensions;

import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;

/**
 * Base class for validating an extension attribute def.
 */
public abstract class AeAbstractExtensionAttributeValidator extends AeBaseValidator
{
   /**
    * Constructor.
    * 
    * @param aBaseDef
    */
   public AeAbstractExtensionAttributeValidator(AeExtensionAttributeDef aBaseDef)
   {
      super(aBaseDef);
   }
   
   /**
    * Convenience method for getting the def already cast.
    */
   protected AeExtensionAttributeDef getDef()
   {
      return (AeExtensionAttributeDef) getDefinition();
   }
}
