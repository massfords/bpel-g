// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/wsio/AeFromPartsValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.activity.wsio;

import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * Model that provides validation of the fromParts.
 */
public class AeFromPartsValidator extends AeBaseValidator
{

   /**
    * Constructor.
    */
   public AeFromPartsValidator(AeFromPartsDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      AeFromPartsDef fromPartsDef = (AeFromPartsDef) getDefinition();
      
      // Must have at least one fromPart
      if (!fromPartsDef.getFromPartDefs().hasNext())
         getReporter().reportProblem( BPEL_FROM_EMPTY_CONTAINER_CODE, ERROR_EMPTY_CONTAINER, new String[]{ fromPartsDef.getLocationPath() }, getDefinition() );
   }
}
