//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeTypeMappingHelper.java,v 1.8 2006/09/20 22:04:30 TZhang Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.ser.AeSimpleSerializerFactory;
import org.activebpel.rt.axis.ser.AeSimpleValueWrapper;
import org.activebpel.rt.axis.ser.IAeTypeMapper;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.ser.SimpleDeserializerFactory;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper class for registering custom serializers/deserializers for
 * sending/receiving SOAP messages using the RPC/encoded style.
 */
public class AeTypeMappingHelper implements IAeTypesContext
{
   /** maps the qname to the tuple of info */
   private Map mQNameToTuple;

   /** def that contains the input message for the operation*/
   private AeBPELExtendedWSDLDef mInputMessageDef;

   /** def that contains the output message for the operation*/
   private AeBPELExtendedWSDLDef mOutputMessageDef;

   /** operation that we're either receiving or invoking */
   private Operation mOperation;
   
   /** cached deserializer factory, we can reuse this for all mappings */
   private DeserializerFactory mDeserializerFactory;

   /** cached serializer factory, we can reuse this for all mappings */
   private SerializerFactory mSerializerFactory;
   
   /** flag that indicates if we're encoded or literal */
   private boolean mEncoded = true;

   /**
    * Creates the type mapper with the input message, the output message, and the 
    * operation being invoked or received.
    * 
    * @param aInputMessageDef
    * @param aOutputMessageDef
    * @param aOperation
    */
   public AeTypeMappingHelper(AeBPELExtendedWSDLDef aInputMessageDef, AeBPELExtendedWSDLDef aOutputMessageDef, Operation aOperation) throws AeException
   {
      setInputMessageDef(aInputMessageDef);
      setOutputMessageDef(aOutputMessageDef);
      setOperation(aOperation);
   }
   
   /**
    * Setter for the encoded flag
    * 
    * @param aEncodedFlag
    */
   public void setEncoded(boolean aEncodedFlag)
   {
      mEncoded = aEncodedFlag;
   }
   
   /**
    * Getter for the encoded flag
    */
   public boolean isEncoded()
   {
      return mEncoded;
   }
   
   /**
    * Builds the list of types that need to get registered for the operation and 
    * then registers them on the type mapper provided. 
    * 
    * @param aTypeMapper
    */
   public void registerTypes(IAeTypeMapper aTypeMapper)
   {
      AeTypeMappingTuple[] mappings = getTypeMappings();
      if (mappings != null)
      {
         // add the entries to the type mapping
         for (int i = 0; i < mappings.length; i++)
         {
            aTypeMapper.register(getClass(mappings[i]), mappings[i].getType(),
                  getSerializerFactory(mappings[i]),
                  getDeserializerFactory(mappings[i]));
         }
      }
   }

   /**
    * Gets the class used to identify the type being serialized. This will work
    * in conjunction with the type name to select the proper serializer to use.
    * 
    * @param aMapping
    */
   protected Class getClass(AeTypeMappingTuple aMapping)
   {
      if (aMapping.isDerivedSimpleType())
         return AeSimpleValueWrapper.class;
      else
         return Document.class;
   }

   /**
    * Gets the serializer factory to use for the mapping.
    * 
    * @param aTuple
    */
   protected SerializerFactory getSerializerFactory(AeTypeMappingTuple aTuple)
   {
      SerializerFactory factory = null;
      if (aTuple.isDerivedSimpleType())
      {
         factory = getSimpleSerializerFactory(aTuple.getType());
      }
      else
      {
         factory = getComplexSerializerFactory();
      }
      return factory;
   }

   /**
    * Gets the deserializer factory to use for the mapping
    * 
    * @param aTuple
    */
   protected DeserializerFactory getDeserializerFactory(
         AeTypeMappingTuple aTuple)
   {
      DeserializerFactory factory = null;
      if (aTuple.isDerivedSimpleType())
      {
         factory = new SimpleDeserializerFactory(String.class, aTuple.getType());
      }
      else
      {
         factory = getComplexDeserializerFactory();
      }
      return factory;
   }

   /**
    * Registers the types required for the specified operation.
    * 
    * @return array of mappings or null if none of the types in the input/output
    *         message require special serialization/deserialization
    */
   public AeTypeMappingTuple[] getTypeMappings()
   {
      identifyTypeMappings(getOperation().getInput().getMessage(), true);
      if (getOperation().getOutput() != null)
      {
         identifyTypeMappings(getOperation().getOutput().getMessage(), false);
      }

      AeTypeMappingTuple[] tuples = null;
      if (hasMappings())
      {
         tuples = new AeTypeMappingTuple[getMap().size()];
         getMap().values().toArray(tuples);
      }
      return tuples;
   }
   
   /**
    * @see org.activebpel.rt.axis.bpel.IAeTypesContext#findElement(javax.xml.namespace.QName)
    */
   public ElementDecl findElement(QName aElementName)
   {
      // we don't want to misreport the soapenc:Array QName as an element
      // there is an Array element within the soapenc schema so w/o this
      // check we'd find that element decl.
      if (AeSchemaUtil.isArray(aElementName))
         return null;
      
      ElementDecl decl = findElement(getInputMessageDef(), aElementName);

      if (decl == null && getOutputMessageDef() != null)
      {
         decl = findElement(getOutputMessageDef(), aElementName); 
      }
      return decl;
   }
   
   /**
    * Searches the def for the element, eating any exceptions.
    * 
    * @param aDef
    * @param aElementName
    */
   protected ElementDecl findElement(AeBPELExtendedWSDLDef aDef, QName aElementName)
   {
      if (aElementName == null)
         return null;
      
      ElementDecl decl = null;
      decl = aDef.findElement(aElementName);

      return decl;
   }

   /**
    * @see org.activebpel.rt.axis.bpel.IAeTypesContext#findType(javax.xml.namespace.QName)
    */
   public XMLType findType(QName aTypeName)
   {
      XMLType type = findType(getInputMessageDef(), aTypeName);
      if (type == null && getOutputMessageDef() != null)
      {
         type = findType(getOutputMessageDef(), aTypeName);
      }
      return type;
   }
   
   /**
    * Searches the wsdl def for the specified type, eating any exceptions.
    * 
    * @param aDef
    * @param aTypeName
    */
   protected XMLType findType(AeBPELExtendedWSDLDef aDef, QName aTypeName)
   {
      if (aTypeName == null)
         return null;
      XMLType type = null;
      try
      {
         type = aDef.findType(aTypeName);
      }
      catch (AeException e)
      {
         // eat the exception
      }
      return type;
   }
   
   /**
    * Walks the parts for the message and records any type mapping information
    * that we need for complex types, schema elements, or derived simple types.
    * 
    * @param aMessage The input or output message we're processing
    * @param aInputFlag
    */
   protected void identifyTypeMappings(Message aMessage, boolean aInputFlag)
   {
      AeBPELExtendedWSDLDef def = aInputFlag ? getInputMessageDef() : getOutputMessageDef();
      
      List parts = aMessage.getOrderedParts(null);
      for (Iterator iter = parts.iterator(); iter.hasNext();)
      {
         AeTypeMappingTuple tuple = null;
         Part part = (Part) iter.next();
         if (def.isArray(part))
         {
            tuple = getOrCreateTuple(IAeBPELExtendedWSDLConst.SOAP_ARRAY);
            // I need to register our handler to intercept the type as
            // an array or as its real type. It'll be an array coming in 
            // from the outside world but it'll be the type when we're
            // sending the message over the wire.
            tuple = getOrCreateTuple(getPartType(part));
         }
         else if (def.isComplexEncodedType(part))
         {
            tuple = getOrCreateTuple(getPartType(part));
         }
         else if (def.isDerivedSimpleType(part))
         {
            tuple = getOrCreateTuple(getPartType(part));
            tuple.setDerivedSimpleType(true);
         }
      }
   }

   /**
    * Gets the QName of the part's underlying type which will either be from
    * <code>getElementName()</code> or from <code>getTypeName()</code>
    * 
    * @param aPart
    */
   protected QName getPartType(Part aPart)
   {
      QName type = aPart.getElementName();
      if (type == null)
         type = aPart.getTypeName();
      return type;
   }

   /**
    * Gets or creates the type mapping object from the map
    * 
    * @param aName
    */
   protected AeTypeMappingTuple getOrCreateTuple(QName aName)
   {
      AeTypeMappingTuple tuple = (AeTypeMappingTuple) getMap().get(aName);
      if (tuple == null)
      {
         tuple = new AeTypeMappingTuple(aName);
         getMap().put(aName, tuple);
      }
      return tuple;
   }

   /**
    * Getter for the map, does lazy loading
    */
   protected Map getMap()
   {
      if (mQNameToTuple == null)
      {
         mQNameToTuple = new HashMap();
      }
      return mQNameToTuple;
   }

   /**
    * Returns true if there are entries in the map
    */
   protected boolean hasMappings()
   {
      return mQNameToTuple != null && mQNameToTuple.size() > 0;
   }

   /**
    * @return Returns the def.
    */
   protected AeBPELExtendedWSDLDef getInputMessageDef()
   {
      return mInputMessageDef;
   }

   /**
    * @param aDef The def to set.
    */
   protected void setInputMessageDef(AeBPELExtendedWSDLDef aDef)
   {
      mInputMessageDef = aDef;
   }

   /**
    * @return Returns the def.
    */
   protected AeBPELExtendedWSDLDef getOutputMessageDef()
   {
      return mOutputMessageDef;
   }

   /**
    * @param aDef The def to set.
    */
   protected void setOutputMessageDef(AeBPELExtendedWSDLDef aDef)
   {
      mOutputMessageDef = aDef;
   }


   /**
    * @return Returns the operation.
    */
   protected Operation getOperation()
   {
      return mOperation;
   }

   /**
    * @param aOperation
    *           The operation to set.
    */
   protected void setOperation(Operation aOperation)
   {
      mOperation = aOperation;
   }

   /**
    * @return Returns the DeserializerFactory.
    */
   protected DeserializerFactory getComplexDeserializerFactory()
   {
      if (mDeserializerFactory == null)
      {
         if (isEncoded())
            mDeserializerFactory = new AeRPCEncodedDeserializerFactory(this);
         else
            mDeserializerFactory = new AeRPCLiteralDeserializerFactory(this);
      }
      return mDeserializerFactory;
   }

   /**
    * @return Returns the SerializerFactory.
    */
   protected SerializerFactory getComplexSerializerFactory()
   {
      if (mSerializerFactory == null)
      {
         if (isEncoded())
            mSerializerFactory = new AeRPCEncodedSerializerFactory(this);
         else
            mSerializerFactory = AeRPCLiteralSerializerFactory.getInstance();
      }
      return mSerializerFactory;
   }

   /**
    * @return Returns the simpleSerializerFactory.
    */
   protected AeSimpleSerializerFactory getSimpleSerializerFactory(QName aType)
   {
      return new AeSimpleSerializerFactory(aType);
   }

   /**
    * If the part is a type and the value is a document then this method will
    * change the root element of the document so that its name matches the name
    * of the part. This is required by the BPEL spec.
    * 
    * @param aPart The wsdl part
    * @param aValue The value for the part
    * @return Object the same as the value passed in (although the root element may have changed)
    */
   public static Object fixPart(Part aPart, Object aValue)
   {
      if (aPart.getTypeName() != null && aValue instanceof Document)
      {
         Document doc = (Document) aValue;
         Element oldRoot = doc.getDocumentElement();
         Element newRoot = AeXmlUtil.createMessagePartTypeElement(doc, aPart.getName(), oldRoot);
         doc.removeChild(oldRoot);
         doc.appendChild(newRoot);
      }
      
      return aValue;
   }
}