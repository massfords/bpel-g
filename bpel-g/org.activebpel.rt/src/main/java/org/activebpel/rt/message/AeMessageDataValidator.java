//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/message/AeMessageDataValidator.java,v 1.2 2006/09/07 14:41:13 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.message; 

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.activebpel.rt.xml.schema.AeXmlValidator;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Document;

/**
 * Validates each of the message parts against the expected types from the Message's definition.
 */
public class AeMessageDataValidator
{
   /** boolean for allowing empty message parts */
   private boolean mRelaxedValidation;
   /** maps part names to their type info */
   private AeMessagePartsMap mMessageParts;
   
   /**
    * Ctor 
    * 
    * @param aRelaxedValidationFlag
    * @param aMessageParts
    */
   public AeMessageDataValidator(boolean aRelaxedValidationFlag, AeMessagePartsMap aMessageParts)
   {
      setRelaxedValidation(aRelaxedValidationFlag);
      setMessageParts(aMessageParts);
   }
   
   /**
    * Validates the message data passed in. 
    * @param aMessageData - message data to validate
    * @param aDef - def that defines the message and has any required schemas
    * @param aTypeMapping - type mapping to serialize any simple type values to strings for validation
    * @return error message or null if message is valid
    * @throws AeException
    */
   public String validate(IAeMessageData aMessageData, AeBPELExtendedWSDLDef aDef, AeTypeMapping aTypeMapping) throws AeException
   {
      // make sure it's the right message
      if (!AeUtil.compareObjects(getMessageType(), aMessageData.getMessageType()))
      {
         Object[] args = {getMessageType(), aMessageData.getMessageType()};
         return AeMessages.format("AeMessageDataValidator.WrongMessage", args); //$NON-NLS-1$
      }
      
      Message message = aDef.getMessage(getMessageType());
      
      // check for the wrong number of parts in the message
      if (!isRelaxedValidation() && aMessageData.getPartCount() != message.getParts().size())
      {
         Object[] args = {new Integer(message.getParts().size()), new Integer(aMessageData.getPartCount())};
         return AeMessages.format("AeMessageDataValidator.WrongPartCount", args); //$NON-NLS-1$
      }
      
      
      // nothing to validate if there are no parts
      if (aMessageData.getPartCount() == 0)
      {
         return null;
      }
      
      AeXmlValidator validator = new AeXmlValidator(aDef, aTypeMapping);
      
      for (Iterator iter=message.getParts().entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Entry) iter.next();
         String partName = (String)entry.getKey();
         Part part = (Part) entry.getValue();

         XMLType partType = getPartType(partName);
         Object data = aMessageData.getData(partName);
         
         if (data == null)
         {
            if (isRelaxedValidation())
            {
               continue;
            }
            else
            {
               return AeMessages.format("AeMessageDataValidator.NullPart", partName); //$NON-NLS-1$
            }
         }
         
         String errorMessage = null;
         
         if (part.getElementName() != null)
         {
            if (data instanceof Document)
            {
               errorMessage = validator.validateElement((Document) data, part.getElementName());
            }
            else
            {
               Object[] args = {part.getElementName(), data};
               errorMessage = AeMessages.format("AeMessageDataValidator.MissingElementDocument", args); //$NON-NLS-1$
            }
         }
         else if (AeXmlUtil.isComplexOrAny(partType))
         {
            if (data instanceof Document)
            {
               errorMessage = validator.validateComplexType((Document) data, part.getTypeName());
            }
            else
            {
               Object[] args = {part.getTypeName(), data};
               errorMessage = AeMessages.format("AeMessageDataValidator.MissingTypeDocument", args); //$NON-NLS-1$
            }
         }
         else
         {
            errorMessage = validator.validateSimpleType(part.getName(), data, part.getTypeName());
         }

         if (errorMessage != null)
         {
            Object[] args = {partName, errorMessage};
            return AeMessages.format("AeMessageDataValidator.InvalidPart", args); //$NON-NLS-1$
         }
      }
      return null;
   }

   /**
    * @return Returns the allowEmptyParts.
    */
   protected boolean isRelaxedValidation()
   {
      return mRelaxedValidation;
   }

   /**
    * @param aAllowEmptyParts The allowEmptyParts to set.
    */
   protected void setRelaxedValidation(boolean aAllowEmptyParts)
   {
      mRelaxedValidation = aAllowEmptyParts;
   }

   /**
    * Getter for the message name
    */
   protected QName getMessageType()
   {
      return getMessageParts().getMessageType();
   }

   /**
    * Gets the type for the given part name
    * @param aPartName
    */
   protected XMLType getPartType(String aPartName)
   {
      return getMessageParts().getPartInfo(aPartName).getXMLType();
   }

   /**
    * Getter for the message parts
    */
   protected AeMessagePartsMap getMessageParts()
   {
      return mMessageParts;
   }
   
   /**
    * Setter for the message parts
    * @param aMessageParts
    */
   protected void setMessageParts(AeMessagePartsMap aMessageParts)
   {
      mMessageParts = aMessageParts;
   }
}
 