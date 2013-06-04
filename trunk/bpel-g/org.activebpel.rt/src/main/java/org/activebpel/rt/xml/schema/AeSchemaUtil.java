//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaUtil.java,v 1.18 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.util.AeUnsynchronizedCharArrayWriter;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.exolab.castor.xml.Namespaces;
import org.exolab.castor.xml.schema.*;
import org.exolab.castor.xml.schema.writer.SchemaWriter;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.io.StringReader;
import java.util.*;

/**
 * A utility class that contains static methods used to manipulate XML schemas.
 */
public class AeSchemaUtil
{
   /* Some QName decls of schema simple types. */
   public static final QName sInt = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "int"); //$NON-NLS-1$ 
   public static final QName sLong = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "long"); //$NON-NLS-1$
   public static final QName sBoolean = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "boolean"); //$NON-NLS-1$
   public static final QName sString = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "string"); //$NON-NLS-1$
   public static final QName sQName = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "QName"); //$NON-NLS-1$
   public static final QName sDateTime = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "dateTime"); //$NON-NLS-1$
   public static final QName sBase64Binary = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "base64Binary"); //$NON-NLS-1$

   /** Available built-in XML schema simple types (localPart names). */
   public final static String[] BUILT_IN_SIMPLE_TYPES = new String[]
   {    
      "anyURI", "base64Binary", "boolean", "byte", "date", "dateTime",        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
      "decimal", "double", "duration", "ENTITIES", "ENTITY", "float",         //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
      "gDay", "gMonth", "gMonthDay", "gYear", "gYearMonth", "hexBinary",      //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
      "ID", "IDREF", "IDREFS", "int", "integer", "language", "long",          //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
      "Name", "NCName", "negativeInteger", "NMTOKENS", "nonNegativeInteger",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
      "nonPositiveInteger", "normalizedString", "NOTATION", "NMTOKEN",        //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      "positiveInteger", "QName", "short", "string", "time", "token",         //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$//$NON-NLS-6$
      "unsignedByte", "unsignedInt", "unsignedLong", "unsignedShort"          //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
   };

   /**
    * This method merges all of the schema types, elements, namespaces, attribute groups, from the two passed
    * in schemas into a single schema. 
    * 
    * @todo Alternatively, change the wsdl def object to hold a list of schemas rather than one schema per target namespace.
    * 
    * @param aSchema1
    * @param aSchema2
    */
   public static Schema mergeSchemas(Schema aSchema1, Schema aSchema2) throws SchemaException
   {
      if ( !AeUtil.compareObjects(aSchema1.getTargetNamespace(), aSchema2.getTargetNamespace()) )
      {
         throw new SchemaException(AeMessages.getString("AeSchemaUtil.ERROR_0")); //$NON-NLS-1$
      }
      String targetNS = aSchema2.getTargetNamespace();

      // Copy over the complex types
      Enumeration e = aSchema2.getComplexTypes();
      while (e.hasMoreElements())
      {
         ComplexType ct = (ComplexType)e.nextElement();
         // Must move the complex type from schema2 to schema1
         ct.setSchema(aSchema1);
         if ( aSchema1.getComplexType(ct.getName()) == null )
         {
            aSchema1.addComplexType(ct);
         }
      }

      // Copy over the element declarations
      e = aSchema2.getElementDecls();
      while (e.hasMoreElements())
      {
         ElementDecl ed = (ElementDecl)e.nextElement();
         if ( aSchema1.getElementDecl(ed.getName()) == null )
         {
            aSchema1.addElementDecl(ed);
         }
      }

      // Copy over the simple types
      e = aSchema2.getSimpleTypes();
      while (e.hasMoreElements())
      {
         SimpleType st = (SimpleType)e.nextElement();
         // Must move the simple type from schema2 to schema1
         st.setSchema(aSchema1);
         if ( aSchema1.getSimpleType(st.getName(), targetNS) == null )
         {
            aSchema1.addSimpleType(st);
         }
      }

      // Copy over the imports
      e = aSchema2.getImportedSchema();
      while (e.hasMoreElements())
      {
         Schema importedSchema = (Schema)e.nextElement();
         if ( aSchema1.getImportedSchema(importedSchema.getTargetNamespace()) == null )
         {
            aSchema1.addImportedSchema(importedSchema);
         }
      }

      // Copy over all attribute groups
      e = aSchema2.getAttributeGroups();
      while (e.hasMoreElements())
      {
         AttributeGroupDecl ag = (AttributeGroupDecl)e.nextElement();
         if ( aSchema1.getAttributeGroup(ag.getName()) == null )
         {
            aSchema1.addAttributeGroup(ag);
         }
      }

      // Copy over all namespaces
      Namespaces namespaces = aSchema2.getNamespaces();
      e = namespaces.getLocalNamespacePrefixes();
      while (e.hasMoreElements())
      {
         String prefix = (String)e.nextElement();
         String ns = namespaces.getNamespaceURI(prefix);
         if ( aSchema1.getNamespace(prefix) == null )
         {
            aSchema1.addNamespace(prefix, ns);
         }
      }

      // Copy over all model groups
      e = aSchema2.getModelGroups();
      while (e.hasMoreElements())
      {
         ModelGroup mg = (ModelGroup)e.nextElement();
         if ( aSchema1.getModelGroup(mg.getName()) == null )
         {
            aSchema1.addModelGroup(mg);
         }
      }

      return aSchema1;
   }

   /**
    * Given a schema, returns a String which is the schema serialized as a string. This method is primarily a
    * debugging tool - it should be used as the detail formatter for a Schema object in Eclipse. That is the
    * reason for the extra formatting flag: the resulting text from the SchemaWriter is reparsed in order to
    * re-serialize with indentation turned on.
    * @param aSchema The schema to serialize
    * @param aUseIndentation True if the result should be indented
    */
   public static String serializeSchema(Schema aSchema, boolean aUseIndentation)
   {
      try
      {
         AeUnsynchronizedCharArrayWriter charWriter = new AeUnsynchronizedCharArrayWriter();
         SchemaWriter schemaWriter = new SchemaWriter(charWriter);
         schemaWriter.write(aSchema);

         String schemaStr = new String(charWriter.toCharArray());

         if ( aUseIndentation )
         {
            AeXMLParserBase parser = new AeXMLParserBase();
            parser.setNamespaceAware(false);
            parser.setValidating(false);
            Document doc = parser.loadDocument(new InputSource(new StringReader(schemaStr)), null);
            schemaStr = AeXMLParserBase.documentToString(doc, true);
         }

         return schemaStr;
      }
      catch (Exception e)
      {
         AeException.logError(e);
         return e.getLocalizedMessage();
      }
   }

   /**
    * Returns true if the type is derived from a soap encoded Array
    * @param aType
    */
   public static boolean isArray(XMLType aType)
   {
      XMLType type = getRootType(aType);
      return IAeBPELExtendedWSDLConst.SOAP_ARRAY.getLocalPart().equals(type.getName())
            && IAeBPELExtendedWSDLConst.SOAP_ARRAY.getNamespaceURI().equals(
                  type.getSchema().getTargetNamespace());
   }

   /**
    * Returns true if the qname is the soap encoded array type.
    * @param aType
    */
   public static boolean isArray(QName aType)
   {
      return IAeBPELExtendedWSDLConst.SOAP_ARRAY.equals(aType);
   }

   /**
    * Gets the base type for the type passed in, walking up to the root type.
    * @param aType
    */
   public static XMLType getRootType(XMLType aType)
   {
      XMLType type = aType;
      while (type.getBaseType() != null)
      {
         type = type.getBaseType();
      }
      return type;
   }
   
   /**
    * Gets the base type name for the type passed in, walking up to the root type.
    * 
    * @param aSimpleType
    */
   public static QName getRootTypeName(XMLType aSimpleType)
   {
      XMLType type = getRootType(aSimpleType);
      return new QName( type.getSchema().getSchemaNamespace(), type.getName() );
   }

   /**
    * Gets the namespace uri needed to qualify a reference to an element defined by this decl
    * @param aElementDecl
    */
   public static String getNamespaceURI(ElementDecl aElementDecl)
   {
      Schema tgtSchema = null;
      // If element is top level or is a reference, we must qualify it
      if ( aElementDecl.getParent() == aElementDecl.getSchema() )
         tgtSchema = aElementDecl.getSchema();
      else if ( aElementDecl.isReference() )
         tgtSchema = aElementDecl.getReference().getSchema();

      // If no target schema yet, check the element form default settings
      if ( tgtSchema == null )
      {
         Form elementForm = aElementDecl.getForm();
         if ( elementForm == null )
            elementForm = aElementDecl.getSchema().getElementFormDefault();

         // If no element form specified or we found an element form and
         // it is unqualified, then no prefix is required.
         if ( elementForm != null && elementForm.isQualified() )
            tgtSchema = aElementDecl.getSchema();
      }

      if ( tgtSchema != null )
         return tgtSchema.getTargetNamespace();
      else
         return null;
   }

   /**
    * Gets the namespace uri needed to qualify a reference to the attribute defined by this decl
    * @param aAttributeDecl
    */
   public static String getNamespaceURI(AttributeDecl aAttributeDecl)
   {
      Schema tgtSchema = null;
      // If attribute is top level or is a reference, we must qualify it
      if ( aAttributeDecl.getParent() == aAttributeDecl.getSchema() )
         tgtSchema = aAttributeDecl.getSchema();
      else if ( aAttributeDecl.isReference() )
         tgtSchema = aAttributeDecl.getReference().getSchema();

      // If no target schema yet, check the attribute form default settings
      if ( tgtSchema == null )
      {
         Form attrForm = aAttributeDecl.getForm();
         if ( attrForm == null )
            attrForm = aAttributeDecl.getSchema().getAttributeFormDefault();

         // If no attribute form specified or we found an attribute form and
         // it is unqualified, then no prefix is required.
         if ( attrForm != null && attrForm.isQualified() )
            tgtSchema = aAttributeDecl.getSchema();
      }

      if ( tgtSchema != null )
         return tgtSchema.getTargetNamespace();
      else
         return null;
   }

   
    /**
    * Gets the substitution group level for specified group head and group member. If the member element
    * does not belong to the group, level = -1, If two elements belong to a group but in the same level, level =
    * 0, If the member element is in the head element group, level = n, where n = 1, 2, 3...
    * @param aHeadElement
    * @param aMemberElement
    * @return a substitution group level
    */
    public static int getSubstitutionGroupLevel(ElementDecl aHeadElement, ElementDecl aMemberElement)
   {
      // if any of schema elements is null, return -1.
      if ( aHeadElement == null || aMemberElement == null )
         return -1;
      
      QName headQName = getQNameForElementDecl(aHeadElement);
      QName memberQName = getQNameForElementDecl(aMemberElement);
      // if two schema elements are the same, return 0.
      if ( AeUtil.compareObjects(headQName, memberQName) )
         return 0;

      String headGroupName = aHeadElement.getSubstitutionGroup();
      String memberGroupName = aMemberElement.getSubstitutionGroup();
      if ( AeUtil.compareObjects(headGroupName, memberGroupName) )
         return 0;
   
      int level = 0;
      while (true)
      {
         memberGroupName = aMemberElement.getSubstitutionGroup();
         if ( memberGroupName == null )
            return -1;
         else
         {
            Schema currentSchema = aMemberElement.getSchema();
            int idx = memberGroupName.indexOf(":"); //$NON-NLS-1$
            if ( idx > 0 )
            {
               String prefix = memberGroupName.substring(0, idx);
               String localpart = memberGroupName.substring(idx + 1);
               String ns = currentSchema.getNamespace(prefix);
               String tns = currentSchema.getTargetNamespace();
               if ( ns.equals(tns) )
               {
                  aMemberElement = currentSchema.getElementDecl(localpart);
               }
               else
               {
                  Schema impSchema = aMemberElement.getSchema().getImportedSchema(ns);
                  aMemberElement = impSchema.getElementDecl(localpart);
               }
            }
            else
            {
               aMemberElement = currentSchema.getElementDecl(memberGroupName);
            }
            memberQName = getQNameForElementDecl(aMemberElement);
            level++;
         }

         if ( memberQName.equals(headQName) )
         {
            return level;
         }
      }
   }

    /**
     * Gets QName for a given elementDecl
     * @param aElementDecl
     */
   public static QName getQNameForElementDecl(ElementDecl aElementDecl)
   {
      if ( aElementDecl == null )
         return null;
      String elementNS = getNamespaceURI(aElementDecl);
      String localName = aElementDecl.getName();
      QName qName = new QName(elementNS, localName);
      return qName;
   }
   
   /**
    * Walks the list of schemas looking for types that are derived from the given
    * complex type.
    * @param aSchemas
    * @param aComplexType
    */
   public static List<ComplexType> findDerivedTypes(List aSchemas, ComplexType aComplexType)
   {
      // list that we'll return
      List<ComplexType> list = new ArrayList<>();
      
      // keep a set of the schemas we've processed to avoid recursing forever
      Set<Schema> alreadyProcessed = new HashSet<>();

       for (Object s : aSchemas) {
           Schema schema = (Schema) s;
           // process the schema if u can add it to the set
           if (alreadyProcessed.add(schema)) {
               // call will populate list with all of the derived types
               findDerivedTypes(schema, aComplexType, list);

               // check for imports in this schema
               for (Enumeration enoom = schema.getImportedSchema(); enoom.hasMoreElements(); ) {
                   Schema imported = (Schema) enoom.nextElement();
                   // check to see if the imported schema has already been processed.
                   if (alreadyProcessed.add(imported)) {
                       findDerivedTypes(imported, aComplexType, list);
                   }
               }
           }
       }
      
      return list;
   }
   
   /**
    * Finds all of the global complex types in the schema that are derived from 
    * the given type.
    * @param aSchema
    * @param aType
    * @param aList
    */
   protected static void findDerivedTypes(Schema aSchema, ComplexType aType, List<ComplexType> aList)
   {
      for(Enumeration enoom = aSchema.getComplexTypes(); enoom.hasMoreElements();)
      {
         ComplexType complexType = (ComplexType) enoom.nextElement();
         if (aType != complexType && isTypeDerivedFromType(complexType, aType))
         {
            aList.add(complexType);
         }
      }
   }

   /**
    * Finds all elements that are SG elements for the given element. 
    * @param aSchemas
    * @param aElement
    */
   public static List<ElementDecl> findSubstitutionGroupElements(List aSchemas, ElementDecl aElement)
   {
      // the list we're going to return
      List<ElementDecl> list = new ArrayList<>();

      // set to avoid recursing forever
      Set<Schema> alreadyProcessed = new HashSet<>();
      
      // walk all of the schemas
       for (Object s : aSchemas) {
           Schema schema = (Schema) s;
           // check to see if we've already processed this schema
           if (alreadyProcessed.add(schema)) {
               // call will populate the list with all of the sg elements
               findSubstitutionGroupElements(schema, aElement, list);

               // check for imports in this schema
               for (Enumeration enoom = schema.getImportedSchema(); enoom.hasMoreElements(); ) {
                   Schema imported = (Schema) enoom.nextElement();
                   // check to see if we've already seen the import
                   if (alreadyProcessed.add(imported)) {
                       findSubstitutionGroupElements(imported, aElement, list);
                   }
               }
           }
       }
      
      return list;
   }
   
   /**
    * Finds all of the global elements that are SG elements for the given
    * element.
    * @param aSchema
    * @param aElement
    * @param aList
    */
   protected static void findSubstitutionGroupElements(Schema aSchema, ElementDecl aElement, List<ElementDecl> aList)
   {
      for(Enumeration enoom = aSchema.getElementDecls(); enoom.hasMoreElements();)
      {
         ElementDecl element = (ElementDecl) enoom.nextElement();
         if (aElement != element && getSubstitutionGroupLevel(aElement, element) > 0)
         {
            aList.add(element);
         }
      }
   }

   /**
    * Returns true if the first type is derived from the second type
    * @param aType
    * @param aPossibleBase
    */
   public static boolean isTypeDerivedFromType(ComplexType aType, ComplexType aPossibleBase)
   {
      ComplexType type = aType;
      while(type.getBaseType() != null && type.getBaseType().isComplexType())
      {
         if (type.getBaseType() == aPossibleBase)
         {
            return true;
         }
         else
         {
            type = (ComplexType) type.getBaseType();
         }
      }
      
      return false;
   }

   /**
    * Finds the passed data type derived from the passed base type
    * @param aWsdlDef The wsdl def that should contain the type information
    * @param aDataType The data type to determine
    * @param aBaseType The base type which we want to know if the data type is derived from
    * @return true if it is derived false otherwise
    */
   public static boolean isDataDerivedFromType(AeBPELExtendedWSDLDef aWsdlDef, QName aDataType, QName aBaseType)
   {
      // see if we are derived type
      boolean isDerived = false;
      try
      {
         XMLType xType= aWsdlDef.findType(aDataType);
         while(xType != null)
         {
            QName qType = new QName(xType.getSchema().getTargetNamespace(), xType.getName()); 
            if(qType.equals(aBaseType))
            {
               isDerived = true;
               break;
            }
            else
               xType = xType.getBaseType();
         }
      }
      catch (AeException ex)
      {
         // ignore error assume type is invalid
      }
      return isDerived;
   }

}
