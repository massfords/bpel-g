//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/wsio/AeFromPartValidator.java,v 1.7 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation.activity.wsio; 

import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.bpel.def.validation.activity.AeWSIOActivityValidator;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.IAeExtensionObject;

/**
 * moddel provides validation for the fromPart def
 */
public class AeFromPartValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeFromPartValidator(AeFromPartDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      // get a ref to the fromPart's enclosing message consumer def
      AeBaseXmlDef consumerDef = getDefinition().getParentXmlDef().getParentXmlDef();
      IAeMessageDataConsumerDef def = null;
      if (consumerDef instanceof AeChildExtensionActivityDef)
      {
         IAeExtensionObject extObject = ((AeChildExtensionActivityDef) consumerDef).getExtensionObject();
         if (extObject != null)
            def = (IAeMessageDataConsumerDef)extObject.getAdapter(IAeMessageDataConsumerDef.class);

         // if the extension activity wants to participate in validation and 
         // execution of the fromParts then they need to provide a consumer def
         if (def == null)
            return;
      }
      else
      {
         def = (IAeMessageDataConsumerDef) consumerDef;
      }

      if (def.getConsumerMessagePartsMap() != null)
      {
         // Note: if the parts are null, then an error will be reported elsewhere
         AeMessagePartsMap map = def.getConsumerMessagePartsMap();
         String partName = getDef().getPart();
         validateMessagePart(map, partName);
      }
      
      // validate the toVariable attribute using the proper scoping rules
      AeWSIOActivityValidator wsioParent = (AeWSIOActivityValidator) getAnscestor(AeWSIOActivityValidator.class);
      if (wsioParent != null)
      {
         String toVariableName = getDef().getToVariable();
         AeVariableValidator variable = wsioParent.resolveFromPartVariable(toVariableName);
         if (variable == null)
         {
            getReporter().reportProblem( BPEL_FROM_PART_VAR_NOT_FOUND_CODE,
                                       ERROR_VAR_NOT_FOUND,
                                       new String[] { toVariableName },
                                       getDefinition());
         }
      }
      
      super.validate();
   }

   /**
    * Validates that the part exists on the message part map
    * @param aMap
    * @param aPartName
    */
   protected void validateMessagePart(AeMessagePartsMap aMap, String aPartName)
   {
      if (aMap.getPartInfo(aPartName) == null)
      {
         getReporter().reportProblem(BPEL_FROM_PART_VAR_PART_NOT_FOUND_CODE,
                                    ERROR_VAR_PART_NOT_FOUND, new String[] {aPartName,
                                    getNSPrefix(aMap.getMessageType().getNamespaceURI()),
                                    aMap.getMessageType().getLocalPart()}, getDefinition());
      }
   }
   
   /**
    * Getter for the fromPart def
    */
   protected AeFromPartDef getDef()
   {
      return (AeFromPartDef) getDefinition();
   }
}
 