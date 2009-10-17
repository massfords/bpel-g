// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeRPCEncodedSerializer.java,v 1.15 2007/07/18 23:47:17 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.DeserializerFactory;
import javax.xml.rpc.encoding.SerializerFactory;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.ser.ElementSerializer;
import org.apache.axis.utils.Messages;
import org.exolab.castor.xml.schema.ComplexType;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Custom serializer for axis that handles complex types over rpc. The base class,
 * <code>ElementSerializer</code> wasn't handling checking to see if we were
 * encoding the types via multiRef.
 */
public class AeRPCEncodedSerializer extends ElementSerializer
{

   /** prefix used when setting the xsi:type on the serialized multiRef element. 
    * The AeLiteralDeserializer will skip over namespace declaration with this prefix
    * to avoid the namespace declaration getting propagated when it's not needed. */
   public static final String TYPE_PREFIX = "abpel-ser"; //$NON-NLS-1$

   /** context for schema types resolution */
   private IAeTypesContext mTypesContext;

   /** serializer factory used for just-in-time type mapping registration */
   private SerializerFactory mSerializerFactory = null;

   /** deserializer factory used for just-in-time type mapping registration */
   private DeserializerFactory mDeserializerFactory = null;

   /** prefix for type assignment unique */
   private int mPrefixTypeCount = 0;
   
   /**
    * Constructor
    * @param aTypesContext
    */
   public AeRPCEncodedSerializer(IAeTypesContext aTypesContext)
   {
      mTypesContext = aTypesContext;
   }

   /**
    * @see org.apache.axis.encoding.Serializer#serialize(javax.xml.namespace.QName, org.xml.sax.Attributes, java.lang.Object, org.apache.axis.encoding.SerializationContext)
    */
   public void serialize(QName aName, Attributes aAttributes, Object aValue, SerializationContext aContext)
      throws IOException
   {
      try
      {
         if (!(aValue instanceof Document || aValue instanceof AeElementHolder))
            throw new IOException(Messages.getMessage("cantSerialize01")); //$NON-NLS-1$

         boolean encoded = "multiRef".equals(aName.getLocalPart()); //$NON-NLS-1$

         // step 1: write the multiRef element out
         Element element = getElement(aValue);
         QName type = getType(aValue, element);
         XMLType xmlType = getXMLType(type);

         Attributes attribs = null;
         boolean xsiTypeRequired = false;
      
         if (xmlType instanceof ComplexType && AeSchemaUtil.isArray(((ComplexType)xmlType)))
         {
            // set the array type instead
            // note: no need to hold onto the return value, the call to get will
            //       cache the prefix within the context.
            aContext.getPrefixForURI(Constants.URI_SOAP11_ENC, TYPE_PREFIX + mPrefixTypeCount++);
            attribs = aContext.setTypeAttribute(aAttributes, IAeBPELExtendedWSDLConst.SOAP_ARRAY);
            xsiTypeRequired = true;
            
            /*
             * TODO (MF) revisit arrayType when Castor supports extension elements within Schema object
             * 
             * The soapenc:arrayType attribute is not strictly required by the
             * SOAP encoding rules. It would be nice to add it to the array
             * but the problem is that I don't know what the type of the array
             * is in all cases. 
             * 
             * The wsdl:arrayType attribute in the schema is not present in the
             * Castor's ComplexType for the array. They seem to skip over it
             * entirely. This attribute would provide info on the child elements.
             *
             * AttributesImpl attributes = new AttributesImpl(attribs);
             * attributes.addAttribute(Constants.URI_SOAP11_ENC, "arrayType", 
             *             soapEncPrefix + ":arrayType", "CDATA", ?????);
             * attribs = attributes;  
             */
         }
         else if (AeUtil.isNullOrEmpty(element.getNamespaceURI()) && type != null)
         {
            aContext.getPrefixForURI(type.getNamespaceURI(), TYPE_PREFIX + mPrefixTypeCount++);
            attribs = aContext.setTypeAttribute(aAttributes, type);
            xsiTypeRequired = true;
         }

         if (attribs == null && encoded)
         {
            // If we're encoded, then we need to include the attributes passed to
            // us since they'll include the soap-encoding directive as well as
            // the id attribute which is used to match the multiRef element to
            // an href that points to it.
            attribs = aAttributes;
         }
         
         // copy all attribs (including namespace assignments)
         NamedNodeMap attrMap = element.getAttributes();
         if(attrMap != null && attrMap.getLength() > 0)
         {
            AttributesImpl attrs = attribs == null? new AttributesImpl() : new AttributesImpl(attribs);
            for(int i =0; i < attrMap.getLength(); ++i)
            {
               Node node = attrMap.item(i); 
               if(attribs == null || AeUtil.isNullOrEmpty(attribs.getValue(node.getNamespaceURI(), node.getLocalName())))
                  attrs.addAttribute(node.getNamespaceURI(), node.getLocalName(), node.getNodeName(), "CDATA", node.getNodeValue() ); //$NON-NLS-1$
            }
            attribs = attrs;
         }

         // serialize the element
         if (!xsiTypeRequired)
         {
            // if we don't know what the type is then we should avoid writing
            // the wrong xsi:type attribute. This is how that's accomplished.
            aContext.setWriteXMLType(null);
         }

         aContext.startElement(aName, attribs);

         // If the type is complex we need to recurse into child nodes
         if(xmlType instanceof ComplexType || hasChildElements(element) )
         {
            ComplexType complexType = null;
            // could be an anyType
            if(xmlType instanceof ComplexType)
               complexType = (ComplexType)xmlType;
            // step two: write all of my children out
            NodeList children = element.getChildNodes();
            for (int i = 0; i < children.getLength(); i++)
            {
               Node child = children.item(i);
               if (child instanceof Element)
               {
                  writeDOMElement(aContext, complexType, (Element) child, encoded);
               }
               //-----------------------------------------------------------------
               // The remaining cases are for text nodes and comments
               //-----------------------------------------------------------------
               else if (child instanceof CDATASection)
               {
                  aContext.writeString("<![CDATA["); //$NON-NLS-1$
                  aContext.writeString(((Text) child).getData());
                  aContext.writeString("]]>"); //$NON-NLS-1$
               }
               else if (child instanceof Comment)
               {
                  aContext.writeString("<!--"); //$NON-NLS-1$
                  aContext.writeString(((CharacterData) child).getData());
                  aContext.writeString("-->"); //$NON-NLS-1$
               }
               else if (child instanceof Text)
               {
                  aContext.writeSafeString(((Text) child).getData());
               }
            }
         }
         else
         {
            // simple type, just write out the contents
            aContext.writeSafeString(AeXmlUtil.getText(element));
         }

         aContext.endElement();
      }
      catch(Throwable t)
      {
         AeException.logError(t, t.getLocalizedMessage());
         if (t instanceof IOException)
            throw (IOException)t;
         else
            throw new IOException(t.getLocalizedMessage());
      }
   }

   /**
    * Writes the Element to the context or delegates to <code>serializeComplexType</code>
    * if the element has child nodes.
    * 
    * @param aContext
    * @param aComplexType
    * @param aChildElement
    * @param aEncodedFlag
    * @throws IOException
    */
   protected void writeDOMElement(SerializationContext aContext, ComplexType aComplexType, Element aChildElement, boolean aEncodedFlag) throws IOException
   {
      // If the child is a complex type itself, then serialize it with its
      // own serializer
      
      if (aComplexType == null || !aEncodedFlag)
      {
         aContext.setWriteXMLType(null);
         aContext.writeDOMElement(aChildElement);
      }
      else if (hasChildElements(aChildElement))
      {
         serializeComplexType(aContext, aComplexType, aChildElement);
      }
      else
      {
         QName type = null;
         // TODO (MF) This is a workaround for a bug in AttributesImpl where you get a NPE testing for the existence of a variable by QName
         // We will not attempt to add an xsi:type to an element that already has attributes.
         // The code in Axis's SerializationContext class was generating a NullPointerException
         // in org.xml.sax.helpers.AttributesImpl.getIndex. The problem is that the element in question
         // contained attributes with a null namespace and the getIndex does an equals()
         // on the namespace to see if it matches the one you're searching for.
         // Since RPC-Encoding doesn't strictly support attributes on elements, it doesn't
         // seem too bad of a workaround.
         if (aChildElement.getAttributes().getLength()==0)
         {
            ElementDecl elementDecl = AeBPELExtendedWSDLDef.findElement(aComplexType, AeXmlUtil.getLocalName(aChildElement));
            if (elementDecl != null)
            {
               XMLType childType = elementDecl.getType();
               if (childType != null)
               {
                  String typeName = childType.getName();
                  // if the typeName is null then this type is probably an anonymous child type declared inline
                  // this type might be derived from another type - either simple or complex, walk the base
                  // type to see if that has a name.
                  while(typeName == null && (childType = childType.getBaseType()) != null)
                  {
                     typeName = childType.getName();
                  }
                  Schema schema = childType.getSchema();
                  String tns = schema.getTargetNamespace();
   
                  // Castor has an issue where the schema for the root simple types
                  // has a null tns. The assumption then is that if we found a
                  // child type that's a simple type and its schema has null tns,
                  // assume it's the w3 schema.
                  // There is a separate issue where AnyType is created with the 
                  // wrong schema ref so we have to check for that as well.
                  if (childType.isAnyType() || (AeUtil.isNullOrEmpty(tns) && childType.isSimpleType()))
                     tns = IAeBPELConstants.W3C_XML_SCHEMA;
                     
                  if (AeUtil.notNullOrEmpty(tns) && AeUtil.notNullOrEmpty(typeName))
                     type = new QName(tns, typeName);
               }
            }
         }
         
         aContext.setWriteXMLType(type);

         // else, the child is a simple type
         aContext.writeDOMElement(aChildElement);
      }
   }

   /**
    * Serializes the complex type to the context.
    * @param aContext - current serialization context
    * @param aParentComplexType - the parent of the type we're serializing
    * @param aElement - the instance of the complex type we need to serialize
    */
   protected void serializeComplexType(
      SerializationContext aContext,
      ComplexType aParentComplexType,
      Element aElement)
      throws IOException
   {
      QName childQName = null;
      if (AeSchemaUtil.isArray(aParentComplexType))
      {
         childQName = getType(null, aElement);
      }
      else
      {
         // first attempt to get the type from the element itself
         childQName = AeXmlUtil.getXSIType(aElement);
         if (childQName == null)
         {
            // it didn't have one. Attempt to get the name from the parent type or
            // its base type
            ElementDecl childDeclaration = AeBPELExtendedWSDLDef.findElement(aParentComplexType, AeXmlUtil.getLocalName(aElement));
            if (childDeclaration != null && AeUtil.notNullOrEmpty(childDeclaration.getType().getName()))
            {
               childQName = getTypeQName(childDeclaration);
            }
            else
            {
               childQName = AeXmlUtil.getElementType(aElement);
            }
         }
      }
      TypeMapping tm = aContext.getTypeMapping();
      tm.register(AeElementHolder.class, childQName, 
               getSerializerFactory(), getDeserializerFactory());
      AeElementHolder childValue = new AeElementHolder(childQName, aElement);
      // in the case of rpc-literal, we want to include the namespace of the element we're serializing
      // use AeUtil.getSafeString here since that ns could be null (if the case is rpc encoded) .
      aContext.serialize(new QName(AeUtil.getSafeString(aElement.getNamespaceURI()), AeXmlUtil.getLocalName(aElement)), null, childValue);
   }
   
   /**
    * Gets the type QName from the element declaration.
    * @param aElementDecl
    */
   protected QName getTypeQName(ElementDecl aElementDecl)
   {
      String namespace = aElementDecl.getType().getSchema().getTargetNamespace();
      return new QName(namespace, aElementDecl.getType().getName());
   }

   /**
    * Getter for the literal serializer factory with lazy instantiation.
    */
   protected SerializerFactory getSerializerFactory()
   {
      if (mSerializerFactory == null)
         mSerializerFactory = new AeRPCEncodedSerializerFactory(this);
      return mSerializerFactory;
   }

   /**
    * Getter for the literal serializer factory with lazy instantiation.
    */
   protected DeserializerFactory getDeserializerFactory()
   {
      if (mDeserializerFactory == null)
      {
         // this will never get used since we're not deserializing here but it's
         // part of adding to the type mapping interface so i don't have much of
         // a choice.
         mDeserializerFactory = new AeNoOpDeserializerFactory();
      }
      return mDeserializerFactory;
   }
   
   private static class AeNoOpDeserializerFactory implements DeserializerFactory
   {
      /**
       * @see javax.xml.rpc.encoding.DeserializerFactory#getDeserializerAs(java.lang.String)
       */
      public Deserializer getDeserializerAs(String mechanismType)
      {
         throw new UnsupportedOperationException();
      }

      /**
       * @see javax.xml.rpc.encoding.DeserializerFactory#getSupportedMechanismTypes()
       */
      public Iterator getSupportedMechanismTypes()
      {
         return Collections.EMPTY_LIST.iterator();
      }

   }

   /**
    * Returns true if the child passed in has child elements. 
    * @param aChild
    */
   protected boolean hasChildElements(Element aChild)
   {
      NodeList nl = aChild.getChildNodes();
      for (int i = 0; nl.item(i) != null; i++)
      {
         if (nl.item(i) instanceof Element)
            return true;
      }
      return false;
   }

   /**
    * Gets the XMLType for the QName passed in.
    * @param aType
    */
   protected XMLType getXMLType(QName aType) throws IOException
   {
      XMLType xmlType;
      ElementDecl elementDecl = mTypesContext.findElement(aType);
      // have to search for the element first or we'll get an error
      if (elementDecl != null)
         xmlType = elementDecl.getType();
      else
         xmlType = mTypesContext.findType(aType);
      return xmlType;
   }

   /**
    * Gets the element from the value passed in which must be either a <code>Document</code>
    * or an <code>AeElementHolder</code>
    * @param aValue
    */
   protected Element getElement(Object aValue)
   {
      if (aValue instanceof Document)
         return ((Document) aValue).getDocumentElement();
      else
         return ((AeElementHolder) aValue).getElement();
   }

   /**
    * Extracts the type from the element.
    * @param aValue either an <code>Element</code> or an <code>AeElementHolder</code>
    * @param aElement
    */
   private QName getType(Object aValue, Element aElement)
   {
      if (aValue instanceof AeElementHolder)
         return ((AeElementHolder)aValue).getType();
      String typeValue =
         aElement.getAttributeNS(
            IAeBPELConstants.W3C_XML_SCHEMA_INSTANCE,
            "type"); //$NON-NLS-1$
      String typeNamespace =
         AeXmlUtil.getNamespaceForPrefix(
            aElement,
            AeXmlUtil.extractPrefix(typeValue));
      String typeLocalPart = AeXmlUtil.extractLocalPart(typeValue);
      if (AeUtil.isNullOrEmpty(typeValue))
      {
         typeNamespace = aElement.getNamespaceURI();
         typeLocalPart = AeXmlUtil.extractLocalPart(aElement.getNodeName());
      }
      QName type = new QName(typeNamespace, typeLocalPart);
      return type;
   }
}
