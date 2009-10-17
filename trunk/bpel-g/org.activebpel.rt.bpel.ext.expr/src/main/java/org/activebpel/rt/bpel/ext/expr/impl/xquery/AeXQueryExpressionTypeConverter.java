//$Header$
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import java.util.List;

import net.sf.saxon.dom.ElementOverNodeInfo;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NamespaceIterator;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.NamespaceIterator.NamespaceNodeImpl;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.ObjectValue;
import net.sf.saxon.value.Value;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.ext.expr.AeMessages;
import org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionTypeConverter;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.IAeSchemaType;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An implementation of a type converter for Saxon XQuery.  This class implements the rules outlined
 * in Saxon regarding how to convert from Saxon types to Java types (there, and back again).
 */
public class AeXQueryExpressionTypeConverter extends AeAbstractExpressionTypeConverter
{
   /**
    * @see org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionTypeConverter#convertToExpressionType(java.lang.Object)
    */
   public Object convertToExpressionType(Object aEngineType)
   {
      if (aEngineType instanceof Document)
      {
         return ((Document) aEngineType).getDocumentElement();
      }
      else if (aEngineType instanceof IAeSchemaType)
      {
         AeXQuerySchemaTypeVisitor typeVisitor = new AeXQuerySchemaTypeVisitor();
         ((IAeSchemaType) aEngineType).accept(typeVisitor);
         return typeVisitor.getExpressionValue();
      }
      else
      {
         return super.convertToExpressionType(aEngineType);
      }
   }

   /**
    * Note that the return value from an XQuery expression will always be a Document that Saxon
    * generated for the result.  This Document contains a sequence of values which are either 
    * atomic types or elements.  The AeXQueryTypedExpressionResult class is used to iterate through
    * theose sequence values and return a List of converted Objects.
    * 
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter#convertToEngineType(java.lang.Object)
    */
   public Object convertToEngineType(Object aExpressionType)
   {
      // Is this the end result of an expression?
      if (aExpressionType instanceof AeXQueryExpressionResult)
      {
         Document doc = ((AeXQueryExpressionResult) aExpressionType).getDocument();
         List list = AeXQueryTypedExpressionResult.createResultList(doc);
         if (list.size() == 1)
            return list.get(0);
         else if (list.size() == 0)
            return null;
         else
            return list;
      }
      else if (aExpressionType instanceof Item)
      {
         return convertFromSaxonType((Item) aExpressionType);
      }
      else if (aExpressionType == null)
      {
         return aExpressionType;
      }
      else
      {
         // This case should never happen, since this method will only be called either to convert
         // the end result of an Expression or an argument to a custom function.  In the former case,
         // the result will always be a AeXQueryExpressionResult, in the latter case it will always
         // be a Item.
         throw new IllegalArgumentException(AeMessages.format("AeXQueryExpressionTypeConverter.UnexpectedXQueryType", aExpressionType.getClass())); //$NON-NLS-1$
      }
   }

   /**
    * Converts from an internal Saxon type to an Engine type (Java/Ae type).  For example, this method
    * will convert a net.sf.saxon.value.DateTimeValue object to an AeSchemaDateTime object.  It will
    * convert a net.sf.saxon.value.Boolean to a java.lang.Boolean.
    * 
    * @param aExpressionType
    */
   protected Object convertFromSaxonType(Item aExpressionType)
   {
      // The ObjectValue could be an IAeAttachmentItem. This won't be useful for
      // any purpose other than passing it to another function call.
      if (aExpressionType instanceof ObjectValue)
      {
         ObjectValue value = (ObjectValue) aExpressionType;
         return value.getObject();
      }
      if (aExpressionType instanceof AtomicValue)
      {
         AtomicValue value = (AtomicValue) aExpressionType;
         if (AeXQueryTypeMapper.canConvert(value))
         {
            return AeXQueryTypeMapper.convert(value);
         }
      }
      
      // Wrap any NodeInfo objects in a dom Node wrapper. There's a special check
      // here for NodeWrapper since it already implements the Node interface for us
      // so no wrapping is necessary.
      if (aExpressionType instanceof NodeInfo && !(aExpressionType instanceof NodeWrapper))
      {
         NodeOverNodeInfo nonInfo = NodeOverNodeInfo.wrap((NodeInfo) aExpressionType);
         if (nonInfo instanceof ElementOverNodeInfo)
            return toDOM((ElementOverNodeInfo) nonInfo);
         return nonInfo;
      }

      try
      {
         return Value.convert(aExpressionType);
      }
      catch (XPathException ex)
      {
         throw new RuntimeException(ex);
      }
   }
   
   /**
    * Converts the Saxon NodeInfo wrapper into a regular DOM element. This is done
    * because the Saxon impl does not provide namespace declarations through the
    * regular DOM getAttribute calls. This functionality is expected by the DOM
    * providers since we use it when copying elements or executing expressions.
    * @param aInfo
    */
   protected Element toDOM(ElementOverNodeInfo aInfo)
   {
      Document doc = AeXmlUtil.newDocument();
      toDOM(doc, aInfo);
      Element dom = doc.getDocumentElement();
      return dom;
   }
   
   /**
    * Converts the NodeInfo into a DOM and adds it to the node provided.
    * @param aNode
    * @param aInfo
    */
   protected void toDOM(Node aNode, ElementOverNodeInfo aInfo)
   {
      Element dom = AeXmlUtil.addElementNS(aNode, aInfo.getNamespaceURI(), aInfo.getNodeName());
      
      // copy all of the attributes over
      NamedNodeMap attribs = aInfo.getAttributes();
      for(int i=0; i<attribs.getLength(); i++)
      {
         Attr attr = (Attr) attribs.item(i);
         if (AeUtil.notNullOrEmpty(attr.getNamespaceURI()))
         {
            dom.setAttributeNS(attr.getNamespaceURI(), attr.getNodeName(), attr.getNodeValue());
         }
         else
         {
            dom.setAttribute(attr.getNodeName(), attr.getNodeValue());
         }
      }
      
      // copy all of the ns decls over
      addNamespaceDecls(dom, aInfo.getUnderlyingNodeInfo());
      
      // copy all of the children over
      NodeList nl = aInfo.getChildNodes();
      int length = nl.getLength();
      for(int i=0; i<length; i++)
      {
         Node node = nl.item(i);
         if (node instanceof ElementOverNodeInfo)
         {
            toDOM(dom, (ElementOverNodeInfo)node);
         }
         else
         {
            dom.appendChild(dom.getOwnerDocument().importNode(node, false));
         }
      }
   }

   /**
    * Adds all of the declared namespaces on the dom element.
    * @param aDom
    * @param aInfo
    */
   private void addNamespaceDecls(Element aDom, NodeInfo aInfo)
   {
      AxisIterator iter = aInfo.iterateAxis(Axis.NAMESPACE);
      for(NamespaceIterator.NamespaceNodeImpl nsItem = (NamespaceNodeImpl) iter.next(); iter.current() != null; nsItem = (NamespaceNodeImpl) iter.next())
      {
         String prefix = nsItem.getDisplayName();
         String nsURI = nsItem.getStringValue();
         
         // skip it if it's the xml ns or if the element already has the ns decl
         if (!IAeConstants.W3C_XML_NAMESPACE.equals(nsURI) && !aDom.hasAttributeNS(IAeConstants.W3C_XMLNS, prefix))
         {
            if ("".equals(prefix)) //$NON-NLS-1$
               aDom.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns", nsURI); //$NON-NLS-1$
            else
               aDom.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:"+prefix, nsURI); //$NON-NLS-1$
               
         }
      }
   }
}
