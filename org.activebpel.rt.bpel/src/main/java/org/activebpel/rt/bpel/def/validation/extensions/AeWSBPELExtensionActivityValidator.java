// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeWSBPELExtensionActivityValidator.java,v 1.2 2006/12/14 22:49:08 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.extensions;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.IAeExtensionActivityDef;

/**
 * WSBPEL version of an extension activity validator.
 */
public class AeWSBPELExtensionActivityValidator extends AeExtensionActivityValidator
{
   /**
    * Constructor.
    * 
    * @param aDef
    */
   public AeWSBPELExtensionActivityValidator(AeExtensionActivityDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.activity.AeActivityValidator#validate()
    */
   public void validate()
   {
      AeActivityDef activityDef = getDef().getActivityDef();
      if (activityDef != null)
      {
         IAeExtensionActivityDef extActivity = (IAeExtensionActivityDef) activityDef;
         AeExtensionValidator extensionValidator = findExtensionValidator(extActivity.getNamespace());
         processExtensionValidator(extensionValidator, extActivity.isUnderstood(), extActivity.getNamespace());
      }
      
      super.validate();
   }
}
