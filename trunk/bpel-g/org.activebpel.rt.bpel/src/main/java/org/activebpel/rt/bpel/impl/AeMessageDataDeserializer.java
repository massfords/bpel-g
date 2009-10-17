// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeMessageDataDeserializer.java,v 1.17 2008/02/17 21:37:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.attachment.AeAttachmentContainer;
import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessageDataFactory;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.impl.attachment.AeStoredAttachmentItem;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.exolab.castor.xml.schema.XMLType;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Constructs a message data object from serialized message data.
 * @todo should pass type mapper even though this is just for transient fault messages
 */
public class AeMessageDataDeserializer implements IAeImplStateNames
{
   /** The XML <code>Element</code> containing the serialized message data. */
   private Element mMessageDataElement;

   /** The message type specifying the message data to produce. */
   private QName mMessageType;

   /** The resulting message data object. */
   private IAeMessageData mMessageData;

   /** Optional variable to use for part type definitions. */
   private IAeVariable mVariable;

   /** Optional type mapping to use to deserialize simple types. */
   private AeTypeMapping mTypeMapping;

   /**
    * Default constructor.
    */
   public AeMessageDataDeserializer()
   {
   }

   /**
    * Constructs a message data object from the specified serialized message
    * data and message type.
    */
   protected IAeMessageData createMessageData(Element aMessageDataElement, QName aMessageType) throws AeBusinessProcessException
   {
      List partElements = selectNodes(aMessageDataElement, "./" + STATE_PART); //$NON-NLS-1$
      IAeMessageData messageData = AeMessageDataFactory.instance().createMessageData(aMessageType);

      for (Iterator i = partElements.iterator(); i.hasNext(); )
      {
         Element partElement = (Element) i.next();
         String name = partElement.getAttribute(STATE_NAME);
         Object value;

         Element data = AeXmlUtil.getFirstSubElement(partElement);
         if (data != null)
         {
            value = createPartDocument(data);
         }
         else
         {
            value = AeXmlUtil.getText(partElement);
         }

         messageData.setData(name, value);
      }

      // If there is a variable to supply type definitions and a type mapping
      // to deserialize simple types, then fix parts that are simple types.
      if ((getVariable() != null) && (getTypeMapping() != null))
      {
         AeVariableDef variableDef = getVariable().getDefinition();
         AeTypeMapping typeMapping = getTypeMapping();

         for (Iterator i = messageData.getPartNames(); i.hasNext(); )
         {
            String name = (String) i.next();
            Object value = messageData.getData(name);

            if (value instanceof String && AeUtil.notNullOrEmpty((String)value))
            {
               XMLType type = variableDef.getPartType(name);
               if (type.getName() != null)
               {
                  value = typeMapping.deserialize(type, (String) value);
                  messageData.setData(name, value);
               }
            }
         }
      }
      
      // Deserialize attachments and add them to the message.
      messageData.setAttachmentContainer(createAttachmentData(aMessageDataElement));

      return messageData;
   }

   /**
    * constructs attachments from the specified serialized message data and add them to the specified attachment container
    * @param aMessageDataElement
    * @throws AeBusinessProcessException
    */
   protected IAeAttachmentContainer createAttachmentData(Element aMessageDataElement) throws AeBusinessProcessException
   {
      IAeAttachmentContainer container = new AeAttachmentContainer();
      List attachmentElements = selectNodes(aMessageDataElement, "./" + STATE_ATTACHMENT); //$NON-NLS-1$
     
      for (Iterator i = attachmentElements.iterator(); i.hasNext(); )
      {
         // deserialize attachment item
         Element attachmentElement = (Element) i.next();
         long attachmentId = Long.parseLong(attachmentElement.getAttribute(STATE_ID));
         long groupId = Long.parseLong(attachmentElement.getAttribute(STATE_GID));
         long processId = Long.parseLong(attachmentElement.getAttribute(STATE_PID));
         
         // deserialize headers
         Map headers = new HashMap();
         NodeList headerNodes = attachmentElement.getElementsByTagName(STATE_ATTACHMENT_HEADER);
      
         for( int h = 0; h < headerNodes.getLength(); h++ )
         {
            Element header = (Element)headerNodes.item(h);
            headers.put(header.getAttribute(STATE_NAME), AeXmlUtil.getText(header).trim());
         }  

         // Create attachment item and add it to the container.
         AeStoredAttachmentItem item = new AeStoredAttachmentItem();
         item.setAttachmentId(attachmentId);
         item.setGroupId(groupId);
         item.setProcessId(processId);
         item.setHeaders(headers);

         container.add(item);
      }

      return container;
   }

   /**
    * Returns a new part <code>Document</code> with the specified data.
    *
    * @param aPartData The root of the part data.
    */
   protected Document createPartDocument(Element aPartData)
   {
      Document document = AeXmlUtil.newDocument();
      Element root = (Element) document.importNode(aPartData, true);
      document.appendChild(root);
      return document;
   }

   /**
    * Returns a message data object constructed from the serialized message
    * data and message type specified with <code>setMessageDataElement</code>
    * and <code>setMessageType</code>.
    */
   public IAeMessageData getMessageData() throws AeBusinessProcessException
   {
      if (mMessageData == null)
      {
         if (mMessageDataElement == null)
         {
            throw new AeBusinessProcessException(AeMessages.getString("AeMessageDataDeserializer.ERROR_1")); //$NON-NLS-1$
         }

         mMessageData = createMessageData(mMessageDataElement, getMessageType());
      }

      return mMessageData;
   }

   /**
    * Returns optional type mapping to use to deserialize simple types.
    */
   protected AeTypeMapping getTypeMapping()
   {
      return mTypeMapping;
   }

   /**
    * Returns optional variable to use for part type definitions.
    */
   protected IAeVariable getVariable()
   {
      return mVariable;
   }

   /**
    * Returns the message type specifying the message data to produce.
    */
   protected QName getMessageType()
   {
      if (mMessageType == null)
      {
         String localPart = mMessageDataElement.getAttribute(STATE_NAME);
         String namespace = mMessageDataElement.getAttribute(STATE_NAMESPACEURI);

         mMessageType = new QName(namespace, localPart);
      }

      return mMessageType;
   }

   /**
    * Resets all output variables.
    */
   protected void reset()
   {
      mMessageData = null;
   }

   /**
    * Selects nodes by XPath.
    *
    * @param aNode The node to select from.
    * @param aPath The XPath expression.
    * @return List The list of matching nodes.
    * @throws AeBusinessProcessException
    */
   protected static List selectNodes(Node aNode, String aPath) throws AeBusinessProcessException
   {
      try
      {
         XPath xpath = new DOMXPath(aPath);
         return xpath.selectNodes(aNode);
      }
      catch (JaxenException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeMessageDataDeserializer.ERROR_2") + aPath, e); //$NON-NLS-1$
      }
   }

   /**
    * Sets the XML <code>Element</code> containing the serialized message data.
    *
    * @param aMessageDataElement
    */
   public void setMessageDataElement(Element aMessageDataElement)
   {
      reset();

      mMessageDataElement = aMessageDataElement;
   }

   /**
    * Sets the message type specifying the message data to produce.
    *
    * @param aMessageType
    */
   public void setMessageType(QName aMessageType)
   {
      reset();

      mMessageType = aMessageType;
   }
   
   /**
    * Sets optional type mapping to use to deserialize simple types.
    */
   public void setTypeMapping(AeTypeMapping aTypeMapping)
   {
      reset();

      mTypeMapping = aTypeMapping;
   }

   /**
    * Sets optional variable to use for part type definitions.
    */
   public void setVariable(IAeVariable aVariable)
   {
      reset();

      mVariable = aVariable;
   }
}
