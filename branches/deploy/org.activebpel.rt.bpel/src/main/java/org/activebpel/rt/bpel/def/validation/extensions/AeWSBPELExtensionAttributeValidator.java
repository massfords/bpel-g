// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/extensions/AeWSBPELExtensionAttributeValidator.java,v 1.9 2008/03/26 13:59:37 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.extensions;

import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.validation.IAeExtensionUsageAdapter;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.IAeExtensionObject;

/**
 * Class that validates an extension attribute def.
 */
public class AeWSBPELExtensionAttributeValidator extends AeAbstractExtensionAttributeValidator
{
   /**
    * Constructor.
    * 
    * @param aDef
    */
   public AeWSBPELExtensionAttributeValidator(AeExtensionAttributeDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();
      // If the parent of extension attribute is a people activity then the attributes will not be qualified
      // so ignore validation when the parent is AeChildExtensionActivityDef
      // sample People Activity element in process
      // <b4p:peopleActivity inputVariable="inputVar" isSkipable="no" name="testPeopleActivity" outputVariable="outputVar"/>
      if (getDef().getParentXmlDef() instanceof AeChildExtensionActivityDef)
         return;
         
      String extNamespace = getDef().getNamespace();
      if (AeUtil.isNullOrEmpty(extNamespace))
      {
         getReporter().reportProblem(WSBPEL_UNEXPECTED_ATTRIBUTE_CODE, 
                                    ERROR_UNEXPECTED_ATTRIBUTE, 
                                    new String[] { getDef().getQualifiedName() },
                                    getDefinition());
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
