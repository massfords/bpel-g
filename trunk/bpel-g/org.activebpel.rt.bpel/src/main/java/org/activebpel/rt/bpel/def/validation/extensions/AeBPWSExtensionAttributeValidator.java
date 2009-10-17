// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeBPWSExtensionAttributeValidator.java,v 1.4 2008/03/20 16:01:32 dvilaverde Exp $
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
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;

/**
 * Class that validates an extension attribute def.
 */
public class AeBPWSExtensionAttributeValidator extends AeAbstractExtensionAttributeValidator
{
   /**
    * Constructor.
    * 
    * @param aDef
    */
   public AeBPWSExtensionAttributeValidator(AeExtensionAttributeDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if (AeUtil.isNullOrEmpty(getDef().getNamespace()))
      {
         getReporter().reportProblem(BPWS_UNEXPECTED_ATTRIBUTE_CODE, ERROR_UNEXPECTED_ATTRIBUTE, new String[] { getDef().getQualifiedName() },
               getDefinition());
      }
      else
      {
         Object[] args = { getDef().getNamespace(), getDef().getQualifiedName(),
               getDef().getParentXmlDef().getLocationPath() };
         String warningMessage = AeMessages.getString("AeBPWSExtensionAttributeValidator.ExtensibilityAttributeWarning"); //$NON-NLS-1$
         getReporter().reportProblem(BPWS_EXTENSIBILITY_ATTRIBUTE_CODE, warningMessage, args, getDefinition().getParentXmlDef());
      }
   }
}
