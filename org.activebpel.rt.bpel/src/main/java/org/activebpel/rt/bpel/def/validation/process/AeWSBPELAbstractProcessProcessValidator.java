//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/process/AeWSBPELAbstractProcessProcessValidator.java,v 1.3 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.process;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.validation.IAeValidationContext;
import org.activebpel.rt.util.AeUtil;

/**
 * Process def validator for ws-bpel 2.x abstract processes.
 *
 */
public class AeWSBPELAbstractProcessProcessValidator extends AeWSBPELProcessValidator
{

   /**
    * Default ctor.
    * @param aContext
    * @param aDef
    */
   public AeWSBPELAbstractProcessProcessValidator(IAeValidationContext aContext, AeProcessDef aDef)
   {
      super(aContext, aDef);
   }

   /**
    * Overrides method to check for abstractProcessProfile attribute.
    * @see org.activebpel.rt.bpel.def.validation.process.AeWSBPELProcessValidator#validate()
    */
   public void validate()
   {
      super.validate();
      if ( AeUtil.isNullOrEmpty( getProcessDef().getAbstractProcessProfile() ) )
      {
         getReporter().reportProblem(WSBPEL_PROFILE_REQUIRED_CODE, AeMessages.getString("AeWSBPELAbstractProcessProcessValidator.ProfileRequired"), null, //$NON-NLS-1$
               getDefinition());
      }
   }
}
