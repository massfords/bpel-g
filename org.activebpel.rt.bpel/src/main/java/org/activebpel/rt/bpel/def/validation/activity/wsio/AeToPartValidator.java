//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/activity/wsio/AeToPartValidator.java,v 1.8 2008/03/20 16:01:32 dvilaverde Exp $
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
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.def.validation.AeBaseValidator;
import org.activebpel.rt.bpel.def.validation.AeVariableValidator;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.IAeExtensionObject;

/**
 * model provides validation for the toPart def
 */
public class AeToPartValidator extends AeBaseValidator
{
   /**
    * ctor
    * @param aDef
    */
   public AeToPartValidator(AeToPartDef aDef)
   {
      super(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.validation.AeBaseValidator#validate()
    */
   public void validate()
   {
      super.validate();

      // get a ref to the fromPart's enclosing message consumer def
      AeBaseXmlDef producerDef = getDefinition().getParentXmlDef().getParentXmlDef();
      IAeMessageDataProducerDef def = null;
      if (producerDef instanceof AeChildExtensionActivityDef)
      {
         IAeExtensionObject extObject = ((AeChildExtensionActivityDef) producerDef).getExtensionObject();
         if (extObject != null)
            def = (IAeMessageDataProducerDef) extObject.getAdapter(IAeMessageDataProducerDef.class);
         
         // if the extension activity wants to participate in validation and 
         // execution of the toParts then they need to provide a producer def
         if(def == null)
            return;
      }
      else
      {
         def = (IAeMessageDataProducerDef) producerDef;
      }
      
      if (def.getProducerMessagePartsMap() != null)
      {
         // Note: if the parts are null, then an error will be reported elsewhere
         AeMessagePartsMap map = def.getProducerMessagePartsMap();
         String partName = getDef().getPart();
         validateMessagePart(map, partName);
      }
      
      // validate the fromVariable attribute using the proper scoping rules
      String fromVariableName = getDef().getFromVariable();
      AeVariableValidator variable = getVariableValidator(fromVariableName, AeToPartDef.TAG_FROM_VARIABLE, true, AeVariableValidator.VARIABLE_READ_WSIO);
      if (variable == null)
      {
         getReporter().reportProblem( BPEL_TO_PART_VAR_NOT_FOUND_CODE,
                                    ERROR_VAR_NOT_FOUND,
                                    new String[] { fromVariableName },
                                    getDefinition());
      }
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
         getReporter().reportProblem( BPEL_TO_PART_VAR_PART_NOT_FOUND_CODE,
                                       ERROR_VAR_PART_NOT_FOUND, new String[] {aPartName,
                                       getNSPrefix(aMap.getMessageType().getNamespaceURI()),
                                       aMap.getMessageType().getLocalPart()}, getDefinition());
      }
   }
   
   /**
    * Getter for the toPart def
    */
   protected AeToPartDef getDef()
   {
      return (AeToPartDef) getDefinition();
   }
}
 