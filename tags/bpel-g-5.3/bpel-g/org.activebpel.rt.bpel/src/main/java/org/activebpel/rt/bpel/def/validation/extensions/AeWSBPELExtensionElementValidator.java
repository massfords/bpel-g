// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeWSBPELExtensionElementValidator.java,v 1.8 2008/03/26 13:59:37 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.extensions;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.validation.IAeExtensionUsageAdapter;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.IAeExtensionObject;


/**
 * Class that validates an extension attribute def.
 */
public class AeWSBPELExtensionElementValidator extends AeAbstractExtensionElementValidator
{
   /**
    * Constructor.
    * 
    * @param aDef
    */
   public AeWSBPELExtensionElementValidator(AeExtensionElementDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();

      String extNamespace = getDef().getElementQName().getNamespaceURI();
      if (IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI.equals(extNamespace))
      {
         String [] args = new String [] { getDef().getElementQName().getLocalPart() };
         String error = AeMessages.getString("AeWSBPELExtensionElementValidator.AddedBPELConstructAsExtensibilityElement"); //$NON-NLS-1$
         getReporter().reportProblem(WSBPEL_EXTELEM_BPEL_AT_INVALID_LOCATION_CODE, error, args, getDefinition());
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
