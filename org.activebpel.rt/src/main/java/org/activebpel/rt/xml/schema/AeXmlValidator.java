//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeXmlValidator.java,v 1.6 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema; 

import java.util.Collections;
import java.util.Iterator;

import javax.wsdl.xml.WSDLLocator;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.AeXMLParserErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class for validating xml types (simple and complex) and elements
 */
public class AeXmlValidator
{
   /** wsdl def */
   private AeBPELExtendedWSDLDef mDef;
   /** Parser used for variable validation */ 
   private AeXMLParserBase mParser;
   /** used to serialize simple types to strings for validation */
   private AeTypeMapping mTypeMapping;
   
   /**
    * Ctor
    * @param aDef
    */
   public AeXmlValidator(AeBPELExtendedWSDLDef aDef, AeTypeMapping aTypeMapping)
   {
      setDef(aDef);
      setTypeMapping(aTypeMapping);
   }
   
   /**
    * Validates the simple type by creating a wrapper document with the root element name matching 
    * the aName param and and setting the xsi:type attribute equal to the expected type.
    * @param aName name of the simple type field, used to create a wrapper element for validation
    * @param aValue value being validated
    * @param aType name of the type, will become the value of xsi:type attribute
    * @return error message or null if valid
    * @throws AeException
    */
   public String validateSimpleType(String aName, Object aValue, QName aType) throws AeException
   {
      String value = getTypeMapping().serialize(aType, aValue);
      Document doc = declareSimpleType(aName, value, aType);
      return validateDocument(doc);
   }
   
   /**
    * Validates the complex type. The declared type passed in will be set on the document as its xsi:type
    * value.
    * @param aValue complex type being validated
    * @param aType name of the type, will become the value of the document element's xsi:type
    * @return error message or null if valid
    * @throws AeException
    */
   public String validateComplexType(Document aValue, QName aType) throws AeException
   {
      AeXmlUtil.declarePartType(aValue, aType);
      return validateDocument(aValue);
   }
   
   /**
    * Validates the complex type.
    * 
    * @param aValue
    * @param aType
    * @throws AeException
    */
   public String validateComplexType(Element aValue, QName aType) throws AeException
   {
      Document doc = AeXmlUtil.cloneElement(aValue).getOwnerDocument();
      return validateComplexType(doc, aType);
   }

   /**
    * Validates the element against its expected type
    * @param aValue
    * @param aElementName
    * @return error message or null if valid
    * @throws AeException
    */
   public String validateElement(Document aValue, QName aElementName) throws AeException
   {
      if (aValue != null && getDef() != null && elementNameMatchesData(aElementName, aValue))
      {
         return validateDocument(aValue);
      }
      Object[] args = {aElementName, AeXmlUtil.getElementType(aValue.getDocumentElement())};
      return AeMessages.format("AeXmlValidator.InvalidElement", args); //$NON-NLS-1$
   }
   
   /**
    * Method used to validate the data contents of an XML document.
    * @param aDoc document to be validated
    * @return error message or null if valid
    * @throws AeException
    */
   protected String validateDocument(Document aDoc) throws AeException
   {
      WSDLLocator locator = null;
      Iterator schemas = Collections.EMPTY_LIST.iterator();
      
      if (getDef() != null)
      {
         locator = getDef().getLocator();
         schemas = getDef().getSchemas();
      }
      
      getParser().setWSDLLocator(locator);
      getParser().validateDocument(aDoc, schemas);
      
      if (! getParser().hasParseWarnings())
      {
         return null;
      }
      else
      {
         Exception e = getParser().getErrorHandler().getParseException();
         if (e != null)
         {
            return e.getLocalizedMessage();
         }
         else
         {
            Object[] args = {AeXMLParserBase.documentToString(aDoc)};
            return AeMessages.format("AeXmlValidator.ValidationError", args); //$NON-NLS-1$
         }
      }
   }

   /**
    * Returns True if the element name matches the name is the data we are about to set,
    * and False if it does not.
    * @param aElementName XML qualified name of the element
    * @param aData The document which represents the data to be set
    */
   protected boolean elementNameMatchesData(QName aElementName, Document aData)
   {
      QName rootElementName = AeXmlUtil.getElementType(aData.getDocumentElement());
      boolean matched = false;
      if ( aElementName.equals(rootElementName) ||
           getDef().isCompatibleSGElement(aElementName, rootElementName))
      {
         matched = true;
      }
      return matched;
   }
    
   /**
    * This method is used to create and prepare a DOM for validation. We will obtain
    * the XML type information from the part and build the node tree based upon that.
    * The data will be added to this node for validation. If any required information
    * cannot be determined null will be returned for the document.
    *
    * @param aVarName The name of the var we are processing
    * @param aData The data for the part we wish to validate
    * @param aTypeName The XML qualified name of the simple type
    * @throws AeException
    */
   protected Document declareSimpleType(String aVarName, String aData, QName aTypeName) throws AeException
   {
      Document doc = null;

      if (aTypeName != null && aData != null)
      {
         doc = getParser().createDocument();
         Element docElement = doc.createElement(aVarName);
         doc.appendChild(docElement);

         // Set the schema and schema instance namespace
         docElement.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:ns", aTypeName.getNamespaceURI()); //$NON-NLS-1$
         docElement.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:xsi", IAeConstants.W3C_XML_SCHEMA_INSTANCE); //$NON-NLS-1$

         // Set the xsi:type attribute
         docElement.setAttributeNS(IAeConstants.W3C_XML_SCHEMA_INSTANCE, "xsi:type", "ns:" + aTypeName.getLocalPart()); //$NON-NLS-1$ //$NON-NLS-2$

         docElement.appendChild(doc.createTextNode(aData));
      }

      return doc;
   }

   /**
    * @return Returns the def.
    */
   protected AeBPELExtendedWSDLDef getDef()
   {
      return mDef;
   }

   /**
    * @param aDef The def to set.
    */
   protected void setDef(AeBPELExtendedWSDLDef aDef)
   {
      mDef = aDef;
   }

   /**
    * Returns parser for use in variable validation
    */
   protected AeXMLParserBase getParser()
   {
      if (mParser == null)
         mParser = new AeXMLParserBase(new AeXMLParserErrorHandler(false));
      
      return mParser;
   }
   
   /**
    * Getter for the type mapping
    */
   protected AeTypeMapping getTypeMapping()
   {
      return mTypeMapping;
   }
   
   /**
    * Setter for the type mapping
    * @param aTypeMapping
    */
   protected void setTypeMapping(AeTypeMapping aTypeMapping)
   {
      mTypeMapping = aTypeMapping;
   }
}
 