// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeVariableDeserializer.java,v 1.21 2007/08/01 18:17:34 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.text.MessageFormat;
import java.util.List;

import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeBusinessProcessEngine;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Restores the state of a variable from a serialized variable document.
 */
public class AeVariableDeserializer implements IAeImplStateNames
{
   /** The engine we are running inside of, supplys type mapper. */
   protected IAeBusinessProcessEngine mEngine;

   /**
    * Constructs the deserializer with an engine since engine has type mapping info.
    * @param aEngine the engine associated with derializer for mapping usages.
    */
   public AeVariableDeserializer(IAeBusinessProcessEngine aEngine)
   {
      mEngine = aEngine;
   }

   /** The serialized variable document. */
   private Document mDocument;

   /** The variable to restore. */
   private IAeVariable mVariable;

   /**
    * Returns the serialized variable document.
    */
   protected Document getDocument()
   {
      return mDocument;
   }

   /**
    * Returns element data from the serialized variable document.
    */
   protected Element getElementData() throws AeBusinessProcessException
   {
      // Return the first child of the document root.
      return (Element) AeXmlUtil.getFirstSubElement(getDocument().getDocumentElement());
   }

   /**
    * Returns message data from the serialized variable document.
    *
    * @param aVariable the variable to get message data for
    */
   protected IAeMessageData getMessageData(IAeVariable aVariable) throws AeBusinessProcessException
   {
      AeMessageDataDeserializer deserializer = new AeMessageDataDeserializer();
      deserializer.setMessageDataElement(getDocument().getDocumentElement());
      deserializer.setMessageType(aVariable.getMessageType());
      deserializer.setTypeMapping(getTypeMapping());
      deserializer.setVariable(aVariable);
      return deserializer.getMessageData();
   }
   
   /**
    * Returns type data from the serialized variable document relative to element
    * @param aElement
    */
   protected Object getTypeData(Element aElement) throws AeBusinessProcessException
   {
      Object data = AeXmlUtil.getFirstSubElement(aElement);
         
      // If the data is not an element (for a complex type), then the data is
      // the string serialization of a simple type.
      if (data == null)
      {
         data = AeXmlUtil.getText(aElement);
      }
      
      return data;
   }

   /**
    * Returns the variable to restore.
    */
   protected IAeVariable getVariable()
   {
      return mVariable;
   }

   /**
    * Returns the variable version number from the serialized variable
    * document.
    *
    * @return boolean
    * @throws AeBusinessProcessException
    */
   protected int getVersionNumber() throws AeBusinessProcessException
   {
      Attr attr = (Attr) selectSingleNode(getDocument(), "/*/@" + STATE_VERSION); //$NON-NLS-1$
      String version = attr.getValue();

      try
      {
         return Integer.parseInt(version);
      }
      catch (NumberFormatException e)
      {
         throw new AeBusinessProcessException(MessageFormat.format(AeMessages.getString("AeVariableDeserializer.ERROR_1"), new Object[] {version}), e); //$NON-NLS-1$
      }
   }

   /**
    * Returns <code>true</code> if and only if the serialized variable document
    * has data to restore.
    *
    * @return boolean
    * @throws AeBusinessProcessException
    */
   protected boolean hasData() throws AeBusinessProcessException
   {
      Element root = getDocument().getDocumentElement();
      return !root.getTagName().equals(STATE_VAR) || "true".equals( root.getAttribute(STATE_HASDATA)) ; //$NON-NLS-1$
   }
   
   /**
    * Returns <code>true</code> if and only if the serialized variable document
    * has attachments to restore.
    *
    * @return boolean
    * @throws AeBusinessProcessException
    */
   protected boolean hasAttachments() throws AeBusinessProcessException
   {
      Element root = getDocument().getDocumentElement();
      return  root.getTagName().equals(STATE_VAR) && "true".equals(root.getAttribute(STATE_HASATTACHMENTS)); //$NON-NLS-1$
   }
   
   /**
    * Restores the variable from the serialized variable document.
    *
    * @throws AeBusinessProcessException
    */
   public void restoreVariable() throws AeBusinessProcessException
   {
      IAeVariable variable = getVariable();
      
      Element root = getDocument().getDocumentElement();
     
      if (!hasData())
      {
         variable.clear();
      }
      else if (variable.isType())
      {
         Object data = getTypeData(root);
         
         if (hasAttachments())
         {
            // Unwrap 'value' tag
            data = getTypeData((Element)data);
         }

         // If the data is a simple string, then it is a serialized simple type.
         if (data instanceof String)
         {
            data = getTypeMapping().deserialize(variable.getDefinition().getXMLType(), (String) data);
         }

         variable.setTypeData(data);
      }
      else if (variable.isElement())
      {
         // The element data needs to be in its own document
         Document document = AeXmlUtil.newDocument();
         Node elementNode = getElementData();

         if (hasAttachments())
         {
            // Unwrap 'value' tag
            elementNode = AeXmlUtil.getFirstSubElement(elementNode);
         }

         Element element = (Element) document.importNode(elementNode, true);
         document.appendChild(element);
         variable.setElementData(element);
      }
      else
      {
         IAeMessageData messageData = getMessageData(variable);
         variable.setMessageData(messageData);
      }
      
      if (hasAttachments() && !(variable.isMessageType() && hasData()))
      {
         IAeAttachmentContainer container = new AeMessageDataDeserializer().createAttachmentData(root);
         variable.setAttachmentData(container);
      }
      
      // Restore the version number *after* setting the variable data, because
      // setting the variable data updates the version number.
      restoreVersionNumber();
   }
   
   /**
    * Restore the version number from the serialized data.
    * @throws AeBusinessProcessException
    */
   protected void restoreVersionNumber() throws AeBusinessProcessException
   {
      getVariable().setVersionNumber(getVersionNumber());
   }

   /**
    * Restores the variable from the specified serialized variable document.
    *
    * @param aDocument
    * @throws AeBusinessProcessException
    */
   public void restoreVariable(Document aDocument) throws AeBusinessProcessException
   {
      setDocument(aDocument);
      restoreVariable();
   }

   /**
    * Selects a single node by XPath. Returns <code>null</code> if there is no
    * matching node.
    *
    * @param aNode The node to select from.
    * @param aPath The XPath expression.
    * @return Node The matching node.
    * @throws AeBusinessProcessException
    */
   protected static Node selectOptionalNode(Node aNode, String aPath) throws AeBusinessProcessException
   {
      List nodes;

      try
      {
         XPath xpath = new DOMXPath(aPath);
         nodes = xpath.selectNodes(aNode);
      }
      catch (JaxenException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeVariableDeserializer.ERROR_0") + aPath, e); //$NON-NLS-1$
      }

      // Check for no more than 1 matching node.
      if (nodes.size() > 1)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeVariableDeserializer.ERROR_9") + aPath); //$NON-NLS-1$
      }

      return (nodes.size() == 0) ? null : (Node) nodes.get(0);
   }

   /**
    * Select a single node by XPath.
    *
    * @param aNode The node to select from.
    * @param aPath The XPath expression.
    * @return Node The matching node.
    * @throws AeBusinessProcessException
    */
   protected static Node selectSingleNode(Node aNode, String aPath) throws AeBusinessProcessException
   {
      List nodes;

      try
      {
         XPath xpath = new DOMXPath(aPath);
         nodes = xpath.selectNodes(aNode);
      }
      catch (JaxenException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeVariableDeserializer.ERROR_0") + aPath, e); //$NON-NLS-1$
      }

      // Check for exactly 1 matching node.
      if (nodes.size() == 0)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeVariableDeserializer.ERROR_11") + aPath); //$NON-NLS-1$
      }

      if (nodes.size() > 1)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeVariableDeserializer.ERROR_9") + aPath); //$NON-NLS-1$
      }

      return (Node) nodes.get(0);
   }

   /**
    * Sets the serialized variable document to use.
    *
    * @param aDocument
    */
   protected void setDocument(Document aDocument)
   {
      mDocument = aDocument;
   }

   /**
    * Sets the variable to restore.
    *
    * @param aVariable
    */
   public void setVariable(IAeVariable aVariable)
   {
      mVariable = aVariable;
   }

   /** Helper method to get type mapping from engine. */
   protected AeTypeMapping getTypeMapping()
   {
      return mEngine.getTypeMapping();
   }
}
