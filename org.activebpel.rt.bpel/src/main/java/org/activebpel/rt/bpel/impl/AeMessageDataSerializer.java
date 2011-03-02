// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeMessageDataSerializer.java,v 1.15 2008/02/17 21:37:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.attachment.AeStoredAttachmentItem;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.fastdom.AeFastNode;
import org.activebpel.rt.bpel.impl.fastdom.AeFastText;
import org.activebpel.rt.bpel.impl.fastdom.AeForeignNode;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;

/**
 * Serializes a message data object to an <code>AeFastElement</code> or
 * <code>AeFastDocument</code>.
 */
public class AeMessageDataSerializer implements IAeImplStateNames
{
   /** Type mapping for simple types. */
   private AeTypeMapping mTypeMapping;

   /** The message data to serialize. */
   private IAeMessageData mMessageData;

   /** The resulting serialization. */
   private AeFastElement mMessageDataElement;

   /**
    * Constructor.
    *
    * @param aTypeMapping The type mapping for simple types.
    */
   public AeMessageDataSerializer(AeTypeMapping aTypeMapping)
   {
      mTypeMapping = aTypeMapping;
   }

   /**
    * Appends elements for message data parts to specified parent element.
    *
    * @param aParentElement
    * @param aMessageData
    */
   protected void appendMessageDataParts(AeFastElement aParentElement, IAeMessageData aMessageData)
   {
      for (Iterator i = aMessageData.getPartNames(); i.hasNext(); )
      {
         String name = (String) i.next();
         Object value = aMessageData.getData(name);

         AeFastElement partElement = new AeFastElement(STATE_PART);
         partElement.setAttribute(STATE_NAME, name);

         AeFastNode dataElement;

         if (value instanceof Document)
         {
            dataElement = new AeForeignNode(((Document) value).getDocumentElement());
         }
         else
         {
            String str = getTypeMapping().serialize(value);
            dataElement = new AeFastText(str);
         }

         partElement.appendChild(dataElement);
         aParentElement.appendChild(partElement);
      }
   }
   
   /**
    * Appends elements for Attachment items to specified parent element.
    * 
    * @param aParentElement
    * @param aAttachmentContainer
    */
   protected void appendMessageAttachmentItems(AeFastElement aParentElement, IAeAttachmentContainer aAttachmentContainer)
   {
      // Append all attribute items as elements with a token value attribute and headers as child elements
      for (Iterator i = aAttachmentContainer.getAttachmentItems(); i.hasNext(); )
      {
         AeStoredAttachmentItem item = (AeStoredAttachmentItem) i.next();
         long attachmentId = item.getAttachmentId();
         long groupId = item.getGroupId();
         long processId = item.getProcessId();
      
         AeFastElement itemElement = new AeFastElement(STATE_ATTACHMENT);
         itemElement.setAttribute(STATE_ID, String.valueOf(attachmentId));
         itemElement.setAttribute(STATE_GID, String.valueOf(groupId));
         itemElement.setAttribute(STATE_PID, String.valueOf(processId));
         
         for (Iterator pairs = item.getHeaders().entrySet().iterator(); pairs.hasNext();)
         {
            Map.Entry pair = (Map.Entry)pairs.next();
            AeFastElement pairElement = new AeFastElement(STATE_ATTACHMENT_HEADER);
            pairElement.setAttribute(STATE_NAME, (String)pair.getKey());
            AeFastText value = new AeFastText((String)pair.getValue());
            pairElement.appendChild(value);
            itemElement.appendChild(pairElement);
         }
         
         aParentElement.appendChild(itemElement);
      }
   }

   /**
    * Serializes the specified message data object to an
    * <code>AeFastElement</code>.
    *
    * @param aMessageData
    */
   protected AeFastElement createMessageDataElement(IAeMessageData aMessageData)
   {
      AeFastElement messageDataElement = new AeFastElement(STATE_MESSAGEDATA);

      QName messageType = aMessageData.getMessageType();
      messageDataElement.setAttribute(STATE_NAME, messageType.getLocalPart());
      messageDataElement.setAttribute(STATE_NAMESPACEURI, messageType.getNamespaceURI());

      appendMessageDataParts(messageDataElement, aMessageData);
      
      if (aMessageData.hasAttachments())
         appendMessageAttachmentItems(messageDataElement, aMessageData.getAttachmentContainer());
      
      return messageDataElement;
   }

   /**
    * Returns an <code>AeFastDocument</code> representing the message data object.
    */
   public AeFastDocument getMessageDataDocument()
   {
      return new AeFastDocument(getMessageDataElement());
   }

   /**
    * Returns an <code>AeFastElement</code> representing the message data object.
    */
   public AeFastElement getMessageDataElement()
   {
      if (mMessageDataElement == null)
      {
         if (mMessageData == null)
         {
            throw new IllegalStateException(AeMessages.getString("AeMessageDataSerializer.ERROR_0")); //$NON-NLS-1$
         }

         mMessageDataElement = createMessageDataElement(mMessageData);
      }

      return mMessageDataElement;
   }

   /**
    * Sets the message data object to serialize.
    *
    * @param aMessageData
    */
   public void setMessageData(IAeMessageData aMessageData)
   {
      mMessageData = aMessageData;
      mMessageDataElement = null;
   }

   /**
    * @return the type mapping for simple types.
    */
   protected AeTypeMapping getTypeMapping()
   {
      return mTypeMapping;
   }
}
