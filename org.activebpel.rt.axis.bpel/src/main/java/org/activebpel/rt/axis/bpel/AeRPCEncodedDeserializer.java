//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeRPCEncodedDeserializer.java,v 1.10 2006/10/27 21:50:16 TZhang Exp $
/////////////////////////////////////////////////////////////////////////////
//             PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Text;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.apache.axis.Constants;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.utils.Mapping;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Form;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Custom deserializer for handling complex types over RPC. These types will be
 * deserialized into their literal xml, resolving any of the mulit-ref encoded
 * references along the way.
 * 
 * There are two issues to solve in deserializing custom types. The first is that
 * the resulting DOM must be valid according to the schema. The engine optionally
 * validates all variables by converting them to an xml stream and then re-parsing
 * them against the available schemas. The second issue is that the resulting DOM
 * must be compatible with jaxen, or the user's xpath expressions will not work.
 * It is possible to create a DOM which can be serialized and validated against the
 * schema - but still be invalid against jaxen. In order to work against jaxen, the
 * resulting DOM elements must be created with the proper namespaces with respect
 * to the element's type and the schema's elementFormDefault value. The latter
 * determines whether or not child elements in the schema must be within the same
 * namespace as their parent element. This is generally set to "qualified" but we
 * support either value since if ommitted, it defaults to "unqualified".
 */
public class AeRPCEncodedDeserializer extends DeserializerImpl
{
   /** Prefix used for namespaces */
   protected static final String PREFIX = "abpel-deser"; //$NON-NLS-1$
   
   /** Counter used to keep any new namespace prefixes we need to create unique */
   private int mPrefixCounter = 1;
   
   /** provides access to the schema types that we're deserializing */
   private IAeTypesContext mTypesContext;
   
   /** namespaces for attributes that we will skip during the deserialization process */
   protected static final Collection sNamespacesToSkipColl = new HashSet();
   
   static 
   {
      sNamespacesToSkipColl.addAll(Arrays.asList(Constants.URIS_SOAP_ENC));
      sNamespacesToSkipColl.addAll(Arrays.asList(Constants.URIS_SOAP_ENV));
      sNamespacesToSkipColl.add(Constants.URI_1999_SCHEMA_XSI);
      sNamespacesToSkipColl.add(Constants.URI_2000_SCHEMA_XSI);
   };
   
   /**
    * Constructor for deserializer.
    */
   public AeRPCEncodedDeserializer(IAeTypesContext aTypesContext)
   {
      setTypesContext(aTypesContext);
   }
   
   /**
    * @see org.apache.axis.encoding.Deserializer#onEndElement(java.lang.String, java.lang.String, org.apache.axis.encoding.DeserializationContext)
    */
   public void onEndElement(String aNamespace, String aLocalName,
                                  DeserializationContext aContext)
       throws SAXException
   {
      MessageElement msgElem = aContext.getCurElement();
      try
      {
         Document root = AeXmlUtil.newDocument();
         Element e = null;

         QName elementType = msgElem.getType();
         
         if (elementType == null)
         {
            // the MessageElement didn't have an xsi:type on it, it could be a Element
            // sent via RPC encoding (which is bad form) or it could be a malformed
            // RPC encoded message (which we'll try to recover from)
            
            // check for an element
            if (msgElem.getID() != null) 
            {
               if (msgElem.getParentNode() instanceof SOAPBody)
               {
                  SOAPBody body = (SOAPBody) msgElem.getParentNode();
                  NodeList children = body.getChildNodes();
                  if (children.item(0) instanceof RPCElement)
                  {
                     RPCElement operationElement = (RPCElement) children.item(0);
                     NodeList parts = operationElement.getChildNodes();
                     for(int i=0; elementType == null && i<parts.getLength(); i++)
                     {
                        MessageElement referrer = (MessageElement) parts.item(i);
                        String href = "#"+msgElem.getID(); //$NON-NLS-1$
                        if (href.equals(referrer.getHref()))
                        {
                           elementType = AeXmlUtil.getElementType(referrer);
                        }
                     }
                  }
               }
            }
            else
            {
               elementType = new QName(aNamespace, aLocalName);
            }
         }
         
         boolean isPartElement = isElementDeclaration(elementType);

         String elementName = elementType == null ? aLocalName : elementType.getLocalPart();
         String namespace = elementType == null ? "" : elementType.getNamespaceURI(); //$NON-NLS-1$
         
         String prefix = getNextPrefix();
         String prefixWithColon = isPartElement ? prefix + ":" : ""; //$NON-NLS-1$ //$NON-NLS-2$
         String rootNamespace = isPartElement ? namespace : ""; //$NON-NLS-1$
         
         // if the part is an element declaration, then we need to create the element
         // with a prefix so it'll be in the correct namespace.
         // in the case of complex types, we don't do this, instead opting for adding
         // an xsi-type element. 
         e = root.createElementNS(rootNamespace, prefixWithColon+elementName);

         if (AeUtil.notNullOrEmpty(prefixWithColon))
         {
            e.setAttributeNS(IAeBPELConstants.W3C_XMLNS, "xmlns:" + prefix, namespace); //$NON-NLS-1$
         }
         
         // TODO add junit test for simple and mixed content roots 
         // if we have text data associated with this message element
         if(! AeUtil.isNullOrEmpty(msgElem.getValue()))
            e.appendChild(e.getOwnerDocument().createTextNode(msgElem.getValue()));
         
         // add the namespaces from the SOAPEnvelope and Body. 
         // Apparently these aren't manifested in the child MessageElements
         addNSDeclarationsFromEnvelope(aContext, e);

         root.appendChild(e);
         addChildrenToElement(aContext, msgElem, elementType, null, e);
         value = root;
      }
      catch (Throwable t)
      {
         AeException.logError(t, AeMessages.getString("AeLiteralDeserializer.ERROR_10")); //$NON-NLS-1$
         if (t instanceof Exception)
            throw new SAXException((Exception)t);
         throw new SAXException(t.getLocalizedMessage());
      }
   }

   /**
    * Adds the namespace declarations from the SOAPEnvelope (including SOAPBody).
    * @param aContext
    * @param aElement
    * @throws SOAPException
    */
   protected void addNSDeclarationsFromEnvelope(DeserializationContext aContext, Element aElement) throws SOAPException
   {
      Map nsMap = new HashMap();
      MessageElement envelope = aContext.getEnvelope();
      if (envelope.namespaces != null)
      {
         addNamespaceMappings(nsMap, envelope);
      }
      MessageElement body = (MessageElement) ((SOAPEnvelope)envelope).getBody();
      if (body.namespaces != null)
      {
         addNamespaceMappings(nsMap, body);
      }
      
      for(Iterator it=nsMap.entrySet().iterator(); it.hasNext();)
      {
         Map.Entry entry = (Entry) it.next();
         Mapping mapping = (Mapping) entry.getValue();
         aElement.setAttributeNS(IAeBPELConstants.W3C_XMLNS, "xmlns:"+mapping.getPrefix(), mapping.getNamespaceURI()); //$NON-NLS-1$
      }
   }

   /**
    * Adds all of the ns mappings from the message element into a map
    * @param nsMap
    * @param aEnvelope
    */
   protected void addNamespaceMappings(Map nsMap, MessageElement aEnvelope)
   {
      for (Iterator it=aEnvelope.namespaces.iterator(); it.hasNext();)
      {
         Mapping mapping = (Mapping) it.next();
         // don't add any that we want to skip over, like SOAP or old XSI's
         if (!sNamespacesToSkipColl.contains(mapping.getNamespaceURI()))
         {
            nsMap.put(mapping.getPrefix(), mapping);
         }
      }
   }
   
   /**
    * Add any attributes found on the source message element to the target element.
    * We skip over attributes in the soap namespace as well as xsi:type.
    * @param aMessageElement source element with possible attributes
    * @param aElement target element for the attributes
    */
   protected void addAttributes(MessageElement aMessageElement, Element aElement)
   {
      Attributes attribs = aMessageElement.getAttributesEx();
      for(int i=0; i<attribs.getLength(); i++)
      {
         String ns = attribs.getURI(i);
         String localPart = attribs.getLocalName(i);
         String value = attribs.getValue(i);
         if (shouldCopyAttribute(ns, localPart))
         {
            aElement.setAttributeNS(ns, attribs.getQName(i), value);
         }
      }
      if (!AeUtil.isNullOrEmpty(aMessageElement.namespaces))
      {
         List nsList = getAllLocallyDeclaredNamespaces(aMessageElement);
         for(int i=0; i<nsList.size(); i++)
         {
            Mapping mapping = (Mapping) nsList.get(i);
            if (shouldCopyAttribute(mapping.getNamespaceURI(), "") && !mapping.getPrefix().startsWith(AeRPCEncodedSerializer.TYPE_PREFIX) && !mapping.getPrefix().startsWith(PREFIX)) //$NON-NLS-1$
            {
               if(AeUtil.isNullOrEmpty(mapping.getPrefix()))
                  aElement.setAttributeNS(IAeBPELConstants.W3C_XMLNS, "xmlns", mapping.getNamespaceURI()); //$NON-NLS-1$
               else
                  aElement.setAttributeNS(IAeBPELConstants.W3C_XMLNS, "xmlns:"+mapping.getPrefix(), mapping.getNamespaceURI()); //$NON-NLS-1$
            }
         }
      }
   }

   /**
    * Returns a list of all locally declared namespaces. The message element
    * contains a list of the namespaces that are currently in scope. We don't
    * want to blindly copy this list over each time since it'll result in 
    * the repetition of declarations from parent to child. Instead, the
    * child's list is taken and then all of the declarations from
    * the parent are removed, leaving just the declarations added in the child.
    * @param aMessageElement
    */
   protected List getAllLocallyDeclaredNamespaces(MessageElement aMessageElement)
   {
      List locallyDeclaredNamespaces = aMessageElement.namespaces != null? new ArrayList(aMessageElement.namespaces) : Collections.EMPTY_LIST;
      if (!locallyDeclaredNamespaces.isEmpty())
      {
         Node parent = aMessageElement.getParentNode();
         if (parent instanceof MessageElement)
         {
            if (!AeUtil.isNullOrEmpty(((MessageElement)parent).namespaces))
            {
               locallyDeclaredNamespaces.removeAll(((MessageElement)parent).namespaces);
            }
         }
      }
      return locallyDeclaredNamespaces;
   }
   
   /**
    * RPC Encoded format does not allow for attributes. That said, there are some
    * exceptions to the rule:
    * - xsi:nil is necessary to indicate null values.
    * - namespace declarations
    * - any other attributes from a namespace other than soap-encoding or soap-envelope
    * The latter is provided for users that ignore the restriction of no attributes
    * in RPC encoded messages.
    * @param aNamespaceURI value of the attributes namespace
    * @param aLocalPart value of the local part
    */
   protected boolean shouldCopyAttribute(String aNamespaceURI, String aLocalPart)
   {
      boolean shouldCopy = false;
      if (!sNamespacesToSkipColl.contains(aNamespaceURI))
      {
         if (AeUtil.isNullOrEmpty(aNamespaceURI))
         {
            // The href and id attributes are from axis
            shouldCopy = !("href".equals(aLocalPart) || "id".equals(aLocalPart)); //$NON-NLS-1$ //$NON-NLS-2$
         }
         else if (IAeBPELConstants.W3C_XML_SCHEMA_INSTANCE.equals(aNamespaceURI) && "type".equals(aLocalPart)) //$NON-NLS-1$
         {
            // don't want to copy xsi:type
         }
         else
         {
            shouldCopy = true;   
         }
      }
      return shouldCopy;
   }

   /**
    * Adds all of the child elements from the MessageElement to the element passed in.
    * @param aContext - used to get a reference to any elements that are referenced via href's
    * @param aMessageElement - the message element whose children we're adding
    * @param aElement - the new parent of the children
    */
   protected void addChildrenToElement(DeserializationContext aContext, MessageElement aMessageElement, QName aParentTypeName, ComplexType aParentType, Element aElement) throws AeException
   {
      addAttributes(aMessageElement, aElement);
      // we might no know the type of parent we're deserializing, depends on whether the inbound soap message had an xsi:type attribute
      QName parentType = aParentTypeName == null? aMessageElement.getType() : aParentTypeName;
      
      // flag used to avoid multiple attempts at discovering the parent's XMLType
      boolean attemptedToResolveParent = false;
      ComplexType parentComplexType = aParentType;
      // flag used to determine if local elements should be deserialized into the parent type's namespace
      boolean isSchemaQualified = aParentType!=null ? isSchemaQualified(aParentType.getSchema()) : false;
      // if the parent type is a soap array, then there is special handling for the child element names based on engine config
      boolean parentIsArray = AeSchemaUtil.isArray(parentType);
      for (Iterator it = aMessageElement.getChildElements(); it.hasNext();)
      {
         Object obj = it.next();
         if (!(obj instanceof Text) && obj instanceof MessageElement)
         {
            // If we have children then we're definitely dealing with a complex type.
            // Record the parent's complex type so we can determine whether its 
            // child elements need to be namespace qualified or not.
            if (parentComplexType == null && !attemptedToResolveParent && parentType != null)
            {
               attemptedToResolveParent = true;
               XMLType parentXmlType = getXMLType(parentType);
               parentComplexType = (ComplexType) parentXmlType;
               if (parentComplexType != null)
               {
                  Schema schema = parentComplexType.getSchema();
                  isSchemaQualified = isSchemaQualified(schema);
               }
            }
            
            MessageElement child = (MessageElement)obj;
            MessageElement dereferencedChildElement = null;
            
            if (child.getHref() == null)
               dereferencedChildElement = child;
            else
               dereferencedChildElement = (MessageElement) aContext.getObjectByRef(child.getHref());
            
            QName type = dereferencedChildElement.getType();
            
            String text = dereferencedChildElement.getValue();
            
            /**
             * There used to be a check here to see if we were deserializing
             * array elements. In that case the element name would be changed to
             * match the type of the element. 
             * Section 5.4.2 of the SOAP 1.1 spec:
             * 
             *    "Within an array value, element names are not significant for 
             *    distinguishing accessors. Elements may have any name. In practice, 
             *    elements will frequently be named so that their declaration in a 
             *    schema suggests or determines their type."
             */
            String elementName = child.getLocalName();
            if (parentIsArray && type != null)
            {
               // the name of the deserialized array item is configurable unless
               // the item is a schema element, in which case we must use the
               // local name of the type.
               elementName = type.getLocalPart();
            }
            
            ComplexType typeOfChild = null;
            String childNS = dereferencedChildElement.getNamespaceURI();
            
            if (AeUtil.isNullOrEmpty(childNS) && parentComplexType != null)
            {
               // In the case of Axis (and prob all other RPC-encoders), the childNS 
               // will always be "". In order to know for sure whether it should 
               // be ns qualified or not, we need to examine the ComplexType or 
               // its schema to see if either are defined as being ns qualified.
               ElementDecl elementDecl = AeBPELExtendedWSDLDef.findElement(parentComplexType, AeXmlUtil.getLocalName(child));
               if (elementDecl != null)
               {
                  if (elementDecl.getForm() == Form.Qualified || elementDecl.getForm() == null && isSchemaQualified )
                  {
                     childNS = elementDecl.getSchema().getTargetNamespace();
                  }
                  
                  if (elementDecl.getType() instanceof ComplexType)
                  {
                     typeOfChild = (ComplexType)elementDecl.getType();
                  }
               }
            }
            
            String prefixWithColon = ""; //$NON-NLS-1$
            if (AeUtil.notNullOrEmpty(childNS))
            {
               String prefix = getOrDeclarePrefix(aElement, childNS);
               if (AeUtil.notNullOrEmpty(prefix))
               {
                  // if the prefix comes back empty, it means we don't need it for
                  // the child element. This is probably because the parent has
                  // an xmlns declaration on it.
                  prefixWithColon = prefix + ":"; //$NON-NLS-1$
               }
            }

            Element multiRefElement = AeXmlUtil.addElementNS(aElement, childNS, prefixWithColon + elementName, text);
            addChildrenToElement(aContext, dereferencedChildElement, type, typeOfChild, multiRefElement);                        
         }
      }
   }

   /**
    * Returns true if the schema has elementFormDefault=qualified 
    * @param schema
    */
   private boolean isSchemaQualified(Schema schema)
   {
      return schema.getElementFormDefault() != null && schema.getElementFormDefault() == Form.Qualified;
   }
   
   /**
    * Gets the XMLType for the given QName. If the QName maps to an ElementDecl
    * then the element's type is returned.
    * 
    * @param parentType
    * @return can return null in cases where the type was not found which would indicate something wrong with the deployment.
    */
   protected XMLType getXMLType(QName parentType)
   {
      XMLType parentXmlType = getTypesContext().findType(parentType);
      if (parentXmlType == null)
      {
         ElementDecl parentElementDecl = getTypesContext().findElement(parentType);
         if (parentElementDecl != null)
         {
            parentXmlType = parentElementDecl.getType();
         }
      }
      return parentXmlType;
   }

   /**
    * Gets the prefix for the namespace using the element as its context. If the
    * prefix isn't found on the element or in any of its anscestors then a new prefix
    * will be declared on the element.
    *  
    * @param aElement
    * @param aNamespace
    */
   protected String getOrDeclarePrefix(Element aElement, String aNamespace)
   {
      String prefix = AeXmlUtil.getPrefixForNamespace(aElement, aNamespace);
      if (prefix == null)
      {
         prefix = getNextPrefix();
         aElement.setAttributeNS(IAeBPELConstants.W3C_XMLNS, "xmlns:"+prefix, aNamespace); //$NON-NLS-1$
      }
      return prefix;
   }
   
   /**
    * Returns true if the type passed in is an element declaration as opposed to a 
    * complex type.
    * @param aType
    */
   private boolean isElementDeclaration(QName aType) throws AeException
   {
      return getTypesContext().findElement(aType) != null;
   }

   /**
    * Gets the next prefix to use for a namespace declaration.
    */
   protected String getNextPrefix()
   {
      return PREFIX + mPrefixCounter++;
   }

   /**
    * Setter for the types context
    * @param typesContext
    */
   protected void setTypesContext(IAeTypesContext typesContext)
   {
      mTypesContext = typesContext;
   }

   /**
    * Getter for the types context
    */
   protected IAeTypesContext getTypesContext()
   {
      return mTypesContext;
   }
}
