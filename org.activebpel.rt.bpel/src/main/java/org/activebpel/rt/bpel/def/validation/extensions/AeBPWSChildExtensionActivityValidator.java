//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeBPWSChildExtensionActivityValidator.java,v 1.2 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2004-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.extensions;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;

/**
 * Class that validates an child extension activity def.
 */
public class AeBPWSChildExtensionActivityValidator extends AeAbstractChildExtensionActivityValidator
{

   /**
    * Constructor
    * @param aBaseDef
    */
   public AeBPWSChildExtensionActivityValidator(AeChildExtensionActivityDef aBaseDef)
   {
      super(aBaseDef);
      // TODO Auto-generated constructor stub
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();

      QName extQName = getDef().getElementName();
      Object[] args = { extQName.getNamespaceURI(), extQName.getLocalPart(),
            getDefinition().getParentXmlDef().getLocationPath() };
      String warningMessage = null;
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(extQName.getNamespaceURI()))
      {
         warningMessage = AeMessages.getString("AeBPWSChildExtensionActivityValidator.AddedBPELConstructAsExtensibilityActivity"); //$NON-NLS-1$
      }
      else
      {
         warningMessage = AeMessages.getString("AeBPWSChildExtensionActivityValidator.ExtensibilityActivityWarning"); //$NON-NLS-1$
      }
      getReporter().reportProblem(BPWS_EXTENSIBILITY_ACTIVITY_CODE, warningMessage, args, getDefinition());
   }
}
