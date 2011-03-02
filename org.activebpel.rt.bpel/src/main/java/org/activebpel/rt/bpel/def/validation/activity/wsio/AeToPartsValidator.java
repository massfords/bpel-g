// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/wsio/AeToPartsValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.activity.wsio;

import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;

/**
 * Model that provides validation of toParts.
 */
public class AeToPartsValidator extends AeBaseValidator
{
   /**
    * Constructor.
    */
   public AeToPartsValidator(AeToPartsDef aDef)
   {
      super(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      AeToPartsDef toPartsDef = (AeToPartsDef) getDefinition();
      
      // Must have at least one toPart
      if (!toPartsDef.getToPartDefs().hasNext())
         getReporter().reportProblem( BPEL_TO_EMPTY_CONTAINER_CODE, ERROR_EMPTY_CONTAINER, new String[]{ toPartsDef.getLocationPath() }, getDefinition() );
   }
}
