//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/scope/AeWSBPELTerminationHandlerValidator.java,v 1.2 2006/10/30 22:47:27 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.scope; 

import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.validation.extensions.AeExtensionValidator;

/**
 * Model provides validation for the terminationHandler def.
 */
public class AeWSBPELTerminationHandlerValidator extends AeTerminationHandlerValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeWSBPELTerminationHandlerValidator(AeTerminationHandlerDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      // Is this an extension termination handler?  (process level termination handler)
      if (getDef().getParent() instanceof AeProcessDef)
      {
         AeExtensionValidator extensionValidator = findExtensionValidator(IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ALLOW_PROCESS_COORDINATION);
         processExtensionValidator(extensionValidator, true, IAeBPELConstants.AE_EXTENSION_NAMESPACE_URI_ALLOW_PROCESS_COORDINATION);
      }
   }
}
 