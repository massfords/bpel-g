// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeVariableSerializer.java,v 1.10 2007/05/24 00:54:32 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.fastdom.AeFastNode;
import org.activebpel.rt.bpel.impl.fastdom.AeFastText;
import org.activebpel.rt.bpel.impl.fastdom.AeForeignNode;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Serializes a variable to an <code>AeFastElement</code> or
 * <code>AeFastDocument</code>.
 */
public class AeVariableSerializer extends AeMessageDataSerializer implements IAeImplStateNames
{
   /** The variable to serialize. */
   private IAeVariable mVariable;

   /** The resulting serialization. */
   private AeFastElement mVariableElement;

   /**
    * Constructor.
    *
    * @param aTypeMapping The type mapping for simple types.
    */
   public AeVariableSerializer(AeTypeMapping aTypeMapping)
   {
      super(aTypeMapping);
   }

   /**
    * Serializes the specified variable to an <code>AeFastElement</code>.
    *
    * @param aVariable
    */
   protected AeFastElement createVariableElement(IAeVariable aVariable) throws AeBusinessProcessException
   {
      AeFastElement variableElement = new AeFastElement(STATE_VAR);
      boolean hasData = aVariable.hasData();
      boolean hasAttachments = aVariable.hasAttachments();

      variableElement.setAttribute(STATE_NAME   , aVariable.getName());
      variableElement.setAttribute(STATE_DATA   , "yes"); //$NON-NLS-1$
      variableElement.setAttribute(STATE_HASDATA, "" + hasData); //$NON-NLS-1$
      variableElement.setAttribute(STATE_HASATTACHMENTS, "" + hasAttachments); //$NON-NLS-1$
      variableElement.setAttribute(STATE_VERSION, "" + aVariable.getVersionNumber()); //$NON-NLS-1$

      if (hasData)
      {
         if (aVariable.isType())
         {
            variableElement.setAttribute(STATE_TYPE, "" + aVariable.getType()); //$NON-NLS-1$
            Object data = aVariable.getTypeData();
            AeFastNode child;
               
            if (data instanceof Node)
            {
               // Complex type.
               child = new AeForeignNode((Node) data);
            }
            else
            {
               // Simple type.
               child = new AeFastText(getTypeMapping().serialize(data));
            }

            if (hasAttachments) 
            {
               //Wrap value with an element tag
               AeFastElement valueElement = new AeFastElement(STATE_VALUE);
               valueElement.appendChild(child);
               variableElement.appendChild(valueElement);
            }
            else
            {
               variableElement.appendChild(child);
            }
         }
         else if (aVariable.isElement())
         {
            variableElement.setAttribute(STATE_ELEMENT, "" + aVariable.getElement()); //$NON-NLS-1$
            Element data = aVariable.getElementData();
            if (hasAttachments) 
            {
               //Wrap value with an element tag
               AeFastElement valueElement = new AeFastElement(STATE_VALUE);
               valueElement.appendChild(new AeForeignNode(data));
               variableElement.appendChild(valueElement);
            }
            else
            {
               variableElement.appendChild(new AeForeignNode(data));
            }
         }
         else if (aVariable.isMessageType())
         {
            variableElement.setAttribute(STATE_MESSAGETYPE, "" + aVariable.getMessageType()); //$NON-NLS-1$
            appendMessageDataParts(variableElement, aVariable.getMessageData());
         }
         else
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeVariableSerializer.ERROR_3")); //$NON-NLS-1$
         }
      }

      if (hasAttachments) 
      {
         appendMessageAttachmentItems(variableElement, aVariable.getAttachmentData());
      }

      return variableElement;
   }

   /**
    * Returns an <code>AeFastDocument</code> representing the variable.
    */
   public AeFastDocument getVariableDocument() throws AeBusinessProcessException
   {
      return new AeFastDocument(getVariableElement());
   }

   /**
    * Returns an <code>AeFastElement</code> representing the variable.
    */
   public AeFastElement getVariableElement() throws AeBusinessProcessException
   {
      if (mVariableElement == null)
      {
         if (mVariable == null)
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeVariableSerializer.ERROR_4")); //$NON-NLS-1$
         }

         mVariableElement = createVariableElement(mVariable);
      }

      return mVariableElement;
   }

   /**
    * Sets the variable to serialize.
    *
    * @param aVariable
    */
   public void setVariable(IAeVariable aVariable)
   {
      mVariable = aVariable;
      mVariableElement = null;
   }
}
