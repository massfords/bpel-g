//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/process/AeWSBPELProcessValidator.java,v 1.5 2008/03/20 16:01:32 dvilaverde Exp $
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
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.validation.IAeValidationContext;
import org.activebpel.rt.bpel.def.validation.extensions.AeExtensionValidator;

/**
 * WS-BPEL 2.x version of the process def validator.
 */
public class AeWSBPELProcessValidator extends AeProcessValidator
{
   /**
    * Ctor.
    * @param aContext
    * @param aDef
    */
   public AeWSBPELProcessValidator(IAeValidationContext aContext, AeProcessDef aDef)
   {
      super(aContext, aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.IAeValidator#validate()
    */
   public void validate()
   {
      if (getProcessDef().isCreateTargetXPath() || getProcessDef().isDisableSelectionFailure())
      {
         AeExtensionValidator extValidator = findExtensionValidator(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_QUERY_HANDLING);
         processExtensionValidator(extValidator, true, IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_QUERY_HANDLING);
      }

      super.validate();
      
      if (isExtensionInUse())
      {
         getReporter().reportProblem(WSBPEL_BPEL_2_0_EXTENSION_USED_CODE,
               AeMessages.getString("AeWSBPELProcessValidator.ExtensionActivitiesInUse"), //$NON-NLS-1$
               null, 
               getDefinition());
      }
   }

   /**
    * Returns true if there are any extensions in use in the process.
    */
   protected boolean isExtensionInUse()
   {
      return getProcessDef().getExtensionDefs().hasNext();
   }
}
