//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeWSBPELChildExtensionActivityValidator.java,v 1.5 2008/03/26 13:59:37 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.extensions;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.validation.IAeExtensionUsageAdapter;
import org.activebpel.rt.xml.def.IAeExtensionObject;

/**
 * Class to validate a child extension activity def
 */
public class AeWSBPELChildExtensionActivityValidator extends AeAbstractChildExtensionActivityValidator
{
   /**
    * Constructor.
    * @param aBaseDef
    */
   public AeWSBPELChildExtensionActivityValidator(AeChildExtensionActivityDef aBaseDef)
   {
      super(aBaseDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      String extNamespace = getDef().getNamespace();
      if (IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI.equals(extNamespace))
      {
         String [] args = new String [] { getDef().getElementName().getLocalPart() };
         String error = AeMessages.getString("AeWSBPELChildExtensionActivityValidator.AddedBPELConstructAsExtensibilityActivity"); //$NON-NLS-1$
         getReporter().reportProblem(WSBPEL_EXTACT_BPEL_AT_INVALID_LOCATION_CODE, error, args, getDefinition());
      }
      else
      {
         IAeExtensionObject extensionObject = getDef().getExtensionObject();
         // If a IAeExtensionUsageAdapter is available, skip the extension validation
         // check here - it will happen later when we use the IAeExtensionUsageAdapter.
         if (extensionObject == null || extensionObject.getAdapter(IAeExtensionUsageAdapter.class) == null)
         {
            AeExtensionValidator extensionValidator = findExtensionValidator(extNamespace);
            processExtensionValidator(extensionValidator, getDef().isUnderstood(), extNamespace);
         }
      }

      IAeExtensionObject extObject = getDef().getExtensionObject();
      doExtensionObjectValidation(extObject);
   }
}
