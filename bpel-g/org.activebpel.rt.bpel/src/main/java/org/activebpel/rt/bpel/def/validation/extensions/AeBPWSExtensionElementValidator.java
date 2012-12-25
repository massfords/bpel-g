// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeBPWSExtensionElementValidator.java,v 1.4 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.extensions;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.xml.def.AeExtensionElementDef;

/**
 * Class that validates an extension attribute def.
 */
public class AeBPWSExtensionElementValidator extends AeAbstractExtensionElementValidator
{
   /**
    * Constructor.
    * 
    * @param aDef
    */
   public AeBPWSExtensionElementValidator(AeExtensionElementDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();

      QName extQName = getDef().getElementQName();
      Object[] args = { extQName.getNamespaceURI(), extQName.getLocalPart(),
            getDefinition().getParentXmlDef().getLocationPath() };
      String warningMessage = null;
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(extQName.getNamespaceURI()))
      {
         warningMessage = AeMessages.getString("AeBPWSExtensionElementValidator.AddedBPELConstructAsExtensibilityElement"); //$NON-NLS-1$
      }
      else
      {
         warningMessage = AeMessages.getString("AeBPWSExtensionElementValidator.ExtensibilityElementWarning"); //$NON-NLS-1$
      }
      getReporter().reportProblem(BPWS_EXTENSIBILITY_ELEMENT_CODE, warningMessage, args, getDefinition());
   }
}
