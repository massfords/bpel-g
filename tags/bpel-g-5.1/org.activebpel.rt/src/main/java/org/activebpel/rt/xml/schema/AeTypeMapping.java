// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeTypeMapping.java,v 1.7 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.exolab.castor.xml.schema.XMLType;

/**
 * Supports type mappings for standard xsd to java types.
 * 
 * Standard mappings from WSDL to Java
 *  xsd:base64Binary  byte[] 
 *  xsd:boolean  boolean 
 *  xsd:byte  byte 
 *  xsd:date   java.util.Date 
 *  xsd:dateTime  java.util.Calendar 
 *  xsd:time   java.util.Time 
 *  xsd:decimal  java.math.BigDecimal 
 *  xsd:double  double 
 *  xsd:float  float 
 *  xsd:hexBinary  byte[] 
 *  xsd:int int 
 *  xsd:integer java.math.BigInteger 
 *  xsd:long  long 
 *  xsd:QName  javax.xml.namespace.QName 
 *  xsd:short  short 
 *  xsd:string java.lang.String 
 *
 * TODO Should have an interface and a factory for this.
 * TODO Need to handle non primitive types too (unsignedInt, etc)
 */
public class AeTypeMapping
{
   /** Java to schema mappings. */
   protected HashMap mJava2SchemaMappings = new HashMap();
   /** Schema to java mappings. */
   protected HashMap mSchema2JavaMappings = new HashMap();
   /** The default mapper if we don't have a specific one. */
   protected IAeTypeMapper defaultMapper = new AeBasicMapper();
   
   /** supported simple type Set */
   private static Set sSimpleTypes;

   public static final QName XSD_ANYURI = new QName(IAeConstants.W3C_XML_SCHEMA, "anyURI"); //$NON-NLS-1$
   public static final QName XSD_BASE64_BINARY = new QName(IAeConstants.W3C_XML_SCHEMA, "base64Binary"); //$NON-NLS-1$
   public static final QName XSD_BOOLEAN = new QName(IAeConstants.W3C_XML_SCHEMA, "boolean"); //$NON-NLS-1$
   public static final QName XSD_BYTE = new QName(IAeConstants.W3C_XML_SCHEMA, "byte"); //$NON-NLS-1$
   public static final QName XSD_DATE = new QName(IAeConstants.W3C_XML_SCHEMA, "date"); //$NON-NLS-1$
   public static final QName XSD_DATETIME = new QName(IAeConstants.W3C_XML_SCHEMA, "dateTime"); //$NON-NLS-1$
   public static final QName XSD_TIME = new QName(IAeConstants.W3C_XML_SCHEMA, "time"); //$NON-NLS-1$
   public static final QName XSD_DURATION = new QName(IAeConstants.W3C_XML_SCHEMA, "duration"); //$NON-NLS-1$
   public static final QName XSD_DECIMAL = new QName(IAeConstants.W3C_XML_SCHEMA, "decimal"); //$NON-NLS-1$
   public static final QName XSD_DOUBLE = new QName(IAeConstants.W3C_XML_SCHEMA, "double"); //$NON-NLS-1$
   public static final QName XSD_FLOAT = new QName(IAeConstants.W3C_XML_SCHEMA, "float"); //$NON-NLS-1$
   public static final QName XSD_HEX_BINARY = new QName(IAeConstants.W3C_XML_SCHEMA, "hexBinary"); //$NON-NLS-1$
   public static final QName XSD_INT = new QName(IAeConstants.W3C_XML_SCHEMA, "int"); //$NON-NLS-1$
   public static final QName XSD_INTEGER = new QName(IAeConstants.W3C_XML_SCHEMA, "integer"); //$NON-NLS-1$
   public static final QName XSD_LONG = new QName(IAeConstants.W3C_XML_SCHEMA, "long"); //$NON-NLS-1$
   public static final QName XSD_QNAME = new QName(IAeConstants.W3C_XML_SCHEMA, "QName"); //$NON-NLS-1$
   public static final QName XSD_SHORT = new QName(IAeConstants.W3C_XML_SCHEMA, "short"); //$NON-NLS-1$
   public static final QName XSD_STRING = new QName(IAeConstants.W3C_XML_SCHEMA, "string"); //$NON-NLS-1$
   public static final QName XSD_YEARMONTH = new QName(IAeConstants.W3C_XML_SCHEMA, "gYearMonth"); //$NON-NLS-1$
   public static final QName XSD_YEAR = new QName(IAeConstants.W3C_XML_SCHEMA, "gYear"); //$NON-NLS-1$
   public static final QName XSD_MONTHDAY = new QName(IAeConstants.W3C_XML_SCHEMA, "gMonthDay"); //$NON-NLS-1$
   public static final QName XSD_DAY = new QName(IAeConstants.W3C_XML_SCHEMA, "gDay"); //$NON-NLS-1$
   public static final QName XSD_MONTH = new QName(IAeConstants.W3C_XML_SCHEMA, "gMonth"); //$NON-NLS-1$
   public static final QName XSD_NORMALIZED_STRING = new QName(IAeConstants.W3C_XML_SCHEMA, "normalizedString"); //$NON-NLS-1$
   public static final QName XSD_TOKEN = new QName(IAeConstants.W3C_XML_SCHEMA, "token"); //$NON-NLS-1$
   public static final QName XSD_UNSIGNED_BYTE = new QName(IAeConstants.W3C_XML_SCHEMA, "unsignedByte"); //$NON-NLS-1$
   public static final QName XSD_POSITIVE_INTEGER = new QName(IAeConstants.W3C_XML_SCHEMA, "positiveInteger"); //$NON-NLS-1$
   public static final QName XSD_NEGATIVE_INTEGER = new QName(IAeConstants.W3C_XML_SCHEMA, "negativeInteger"); //$NON-NLS-1$
   public static final QName XSD_NON_NEGATIVE_INTEGER = new QName(IAeConstants.W3C_XML_SCHEMA, "nonNegativeInteger"); //$NON-NLS-1$
   public static final QName XSD_NON_POSITIVE_INTEGER = new QName(IAeConstants.W3C_XML_SCHEMA, "nonPositiveInteger"); //$NON-NLS-1$
   public static final QName XSD_UNSIGNED_INT = new QName(IAeConstants.W3C_XML_SCHEMA, "unsignedInt"); //$NON-NLS-1$
   public static final QName XSD_UNSIGNED_LONG = new QName(IAeConstants.W3C_XML_SCHEMA, "unsignedLong"); //$NON-NLS-1$
   public static final QName XSD_UNSIGNED_SHORT = new QName(IAeConstants.W3C_XML_SCHEMA, "unsignedShort"); //$NON-NLS-1$
   public static final QName XSD_NAME = new QName(IAeConstants.W3C_XML_SCHEMA, "Name"); //$NON-NLS-1$
   public static final QName XSD_NCNAME = new QName(IAeConstants.W3C_XML_SCHEMA, "NCName"); //$NON-NLS-1$
   public static final QName XSD_LANGUAGE = new QName(IAeConstants.W3C_XML_SCHEMA, "language"); //$NON-NLS-1$
   public static final QName XSD_ID = new QName(IAeConstants.W3C_XML_SCHEMA, "ID"); //$NON-NLS-1$
   public static final QName XSD_IDREF = new QName(IAeConstants.W3C_XML_SCHEMA, "IDREF"); //$NON-NLS-1$
   public static final QName XSD_IDREFS = new QName(IAeConstants.W3C_XML_SCHEMA, " IDREFS"); //$NON-NLS-1$
   public static final QName XSD_ENTITY = new QName(IAeConstants.W3C_XML_SCHEMA, " ENTITY"); //$NON-NLS-1$
   public static final QName XSD_ENTITIES = new QName(IAeConstants.W3C_XML_SCHEMA, "ENTITIES"); //$NON-NLS-1$
   public static final QName XSD_NOTATION = new QName(IAeConstants.W3C_XML_SCHEMA, " NOTATION"); //$NON-NLS-1$
   public static final QName XSD_NMTOKENS = new QName(IAeConstants.W3C_XML_SCHEMA, " NMTOKENS"); //$NON-NLS-1$

   /**
    * Statically load the simple type Set.
    */
   static {
      sSimpleTypes = new HashSet();
      sSimpleTypes.add(AeTypeMapping.XSD_ANYURI);
      sSimpleTypes.add(AeTypeMapping.XSD_BASE64_BINARY);
      sSimpleTypes.add(AeTypeMapping.XSD_BOOLEAN);
      sSimpleTypes.add(AeTypeMapping.XSD_BYTE);
      sSimpleTypes.add(AeTypeMapping.XSD_DATE);
      sSimpleTypes.add(AeTypeMapping.XSD_DATETIME);
      sSimpleTypes.add(AeTypeMapping.XSD_DAY);
      sSimpleTypes.add(AeTypeMapping.XSD_DECIMAL);
      sSimpleTypes.add(AeTypeMapping.XSD_DOUBLE);
      sSimpleTypes.add(AeTypeMapping.XSD_DURATION);
      sSimpleTypes.add(AeTypeMapping.XSD_ENTITIES);
      sSimpleTypes.add(AeTypeMapping.XSD_ENTITY);
      sSimpleTypes.add(AeTypeMapping.XSD_FLOAT);
      sSimpleTypes.add(AeTypeMapping.XSD_HEX_BINARY);
      sSimpleTypes.add(AeTypeMapping.XSD_ID);
      sSimpleTypes.add(AeTypeMapping.XSD_IDREF);
      sSimpleTypes.add(AeTypeMapping.XSD_IDREFS);
      sSimpleTypes.add(AeTypeMapping.XSD_INT);
      sSimpleTypes.add(AeTypeMapping.XSD_INTEGER);
      sSimpleTypes.add(AeTypeMapping.XSD_LANGUAGE);
      sSimpleTypes.add(AeTypeMapping.XSD_LONG);
      sSimpleTypes.add(AeTypeMapping.XSD_MONTH);
      sSimpleTypes.add(AeTypeMapping.XSD_MONTHDAY);
      sSimpleTypes.add(AeTypeMapping.XSD_NAME);
      sSimpleTypes.add(AeTypeMapping.XSD_NCNAME);
      sSimpleTypes.add(AeTypeMapping.XSD_NEGATIVE_INTEGER);
      sSimpleTypes.add(AeTypeMapping.XSD_NMTOKENS);
      sSimpleTypes.add(AeTypeMapping.XSD_NON_NEGATIVE_INTEGER);
      sSimpleTypes.add(AeTypeMapping.XSD_NON_POSITIVE_INTEGER);
      sSimpleTypes.add(AeTypeMapping.XSD_NORMALIZED_STRING);
      sSimpleTypes.add(AeTypeMapping.XSD_NOTATION);
      sSimpleTypes.add(AeTypeMapping.XSD_POSITIVE_INTEGER);
      sSimpleTypes.add(AeTypeMapping.XSD_QNAME);
      sSimpleTypes.add(AeTypeMapping.XSD_SHORT);
      sSimpleTypes.add(AeTypeMapping.XSD_STRING);
      sSimpleTypes.add(AeTypeMapping.XSD_TIME);
      sSimpleTypes.add(AeTypeMapping.XSD_TOKEN);
      sSimpleTypes.add(AeTypeMapping.XSD_UNSIGNED_BYTE);
      sSimpleTypes.add(AeTypeMapping.XSD_UNSIGNED_INT);
      sSimpleTypes.add(AeTypeMapping.XSD_UNSIGNED_LONG);
      sSimpleTypes.add(AeTypeMapping.XSD_UNSIGNED_SHORT);
      sSimpleTypes.add(AeTypeMapping.XSD_YEAR);
      sSimpleTypes.add(AeTypeMapping.XSD_YEARMONTH);
   }
   
   /**
    * Returns true if the QName supplied is a supported SimpleType, otherwise false.
    * 
    * @param aSimpleType
    */
   public static boolean isSimpleTypeSupported(QName aSimpleType) 
   {
      return sSimpleTypes.contains(aSimpleType);
   }
   
   /**
    * Ctor for aType mapping populates the mappings.
    */
   public AeTypeMapping()
   {
      mJava2SchemaMappings.put(AeSchemaBase64Binary.class, new AeBase64BinaryMapper());
      mSchema2JavaMappings.put(XSD_BASE64_BINARY, new AeBase64BinaryMapper());

      mJava2SchemaMappings.put(Boolean.class, new AeBooleanMapper());
      mSchema2JavaMappings.put(XSD_BOOLEAN, new AeBooleanMapper());

      mJava2SchemaMappings.put(Byte.class, new AeByteMapper());
      mSchema2JavaMappings.put(XSD_BYTE, new AeByteMapper());

      mJava2SchemaMappings.put(AeSchemaDate.class, new AeDateMapper());
      mSchema2JavaMappings.put(XSD_DATE, new AeDateMapper());

      mJava2SchemaMappings.put(AeSchemaDateTime.class, new AeDateTimeMapper());
      mJava2SchemaMappings.put(Date.class, new AeDateTimeMapper());
      mSchema2JavaMappings.put(XSD_DATETIME, new AeDateTimeMapper());

      mJava2SchemaMappings.put(AeSchemaTime.class, new AeTimeMapper());
      mSchema2JavaMappings.put(XSD_TIME, new AeTimeMapper());

      mJava2SchemaMappings.put(AeSchemaDuration.class, new AeDurationMapper());
      mSchema2JavaMappings.put(XSD_DURATION, new AeDurationMapper());
      
      mJava2SchemaMappings.put(BigDecimal.class, new AeBigDecimalMapper());
      mSchema2JavaMappings.put(XSD_DECIMAL, new AeBigDecimalMapper());

      mJava2SchemaMappings.put(Double.class, new AeDoubleMapper());
      mSchema2JavaMappings.put(XSD_DOUBLE, new AeDoubleMapper());

      mJava2SchemaMappings.put(Float.class, new AeFloatMapper());
      mSchema2JavaMappings.put(XSD_FLOAT, new AeFloatMapper());

      mJava2SchemaMappings.put(AeSchemaHexBinary.class, new AeHexBinaryMapper());
      mSchema2JavaMappings.put(XSD_HEX_BINARY, new AeHexBinaryMapper());

      mJava2SchemaMappings.put(Integer.class, new AeIntMapper());
      mSchema2JavaMappings.put(XSD_INT, new AeIntMapper());

      mJava2SchemaMappings.put(BigInteger.class, new AeBigIntegerMapper());
      mSchema2JavaMappings.put(XSD_INTEGER, new AeBigIntegerMapper());

      mJava2SchemaMappings.put(Long.class, new AeLongMapper());
      mSchema2JavaMappings.put(XSD_LONG, new AeLongMapper());

      mJava2SchemaMappings.put(QName.class, new AeQNameMapper());
      mSchema2JavaMappings.put(XSD_QNAME, new AeQNameMapper());

      mJava2SchemaMappings.put(Short.class, new AeShortMapper());
      mSchema2JavaMappings.put(XSD_SHORT, new AeShortMapper());

      mJava2SchemaMappings.put(AeSchemaAnyURI.class, new AeAnyURIMapper());
      mSchema2JavaMappings.put(XSD_ANYURI, new AeAnyURIMapper());

      mJava2SchemaMappings.put(AeSchemaYearMonth.class, new AeSchemaYearMonthMapper());
      mSchema2JavaMappings.put(XSD_YEARMONTH, new AeSchemaYearMonthMapper());

      mJava2SchemaMappings.put(AeSchemaYear.class, new AeSchemaYearMapper());
      mSchema2JavaMappings.put(XSD_YEAR, new AeSchemaYearMapper());

      mJava2SchemaMappings.put(AeSchemaMonthDay.class, new AeSchemaMonthDayMapper());
      mSchema2JavaMappings.put(XSD_MONTHDAY, new AeSchemaMonthDayMapper());
      
      mJava2SchemaMappings.put(AeSchemaDay.class, new AeSchemaDayMapper());
      mSchema2JavaMappings.put(XSD_DAY, new AeSchemaDayMapper());
      
      mJava2SchemaMappings.put(AeSchemaMonth.class, new AeSchemaMonthMapper());
      mSchema2JavaMappings.put(XSD_MONTH, new AeSchemaMonthMapper());
   }
   
   /**
    * Returns the XSI type QName for the given object, or null if we do not handle this type.
    * @param aObj The object we are looking to find the QName of
    */
   public QName getXSIType(Object aObj)
   {
      if (aObj != null)
      {
         IAeTypeMapper mapper = (IAeTypeMapper)mJava2SchemaMappings.get(aObj.getClass());
         if (mapper != null)
         {
            for (Iterator iter=mSchema2JavaMappings.keySet().iterator(); iter.hasNext();)
            {
               Object key = iter.next();
               if (mapper.getClass().equals(mSchema2JavaMappings.get(key).getClass()))
                   return (QName)key;
            }
         }
      }
      
      return null;
   }
   
   /**
    * Serializes a schema object to a string.
    * @param aObj The object to serialize as string 
    * @return String representing object.
    */
   public String serialize(Object aObj)
   {
      if(aObj != null)
      {
         IAeTypeMapper mapper = (IAeTypeMapper)mJava2SchemaMappings.get(aObj.getClass());
         if(mapper == null)
            mapper = defaultMapper;
         return mapper.serialize(aObj);
      }
      return ""; //$NON-NLS-1$
   }
   
   /**
    * Serializes a schema object to a string using the type as a hint.
    * 
    * @param aType The type of the schema object.
    * @param aObj The object to serialize.
    * @return String representing object.
    */
   public String serialize(QName aType, Object aObj)
   {
      if (aObj != null)
      {
         IAeTypeMapper mapper = (IAeTypeMapper) mSchema2JavaMappings.get(aType);
         if (mapper == null)
            return serialize(aObj);
         return mapper.serialize(aObj);
      }
      return ""; //$NON-NLS-1$
   }
   
   /**
    * Deserializes a schema object from a string.
    * @param aType The qname of the schema type
    * @param aObj The object ti be deserialized
    * @return The Object in java form
    */
   public Object deserialize(QName aType, String aObj)
   {
      if(aType != null && aObj != null)
      {
         IAeTypeMapper mapper = (IAeTypeMapper)mSchema2JavaMappings.get(aType);
         // TODO check for base type mapping
         if (mapper == null)
            mapper = defaultMapper;
         return mapper.deserialize(aObj);
      }
      return aObj;
   }
   
   /**
    * Deserializes a schema object from a string and will check for base type
    * deserializers if specific one is not avaiable.
    * @param aType The xml type of the schema type
    * @param aObj The object ti be deserialized
    * @return The Object in java form
    */
   public Object deserialize(XMLType aType, String aObj)
   {
      if(aObj != null)
      {
         QName typeName = new QName(aType.getSchema().getSchemaNamespace(), aType.getName());
         IAeTypeMapper mapper = (IAeTypeMapper)mSchema2JavaMappings.get(typeName);
         while(mapper == null && aType.getBaseType() != null)
         {
            aType = aType.getBaseType();
            typeName = new QName(aType.getSchema().getSchemaNamespace(), aType.getName());
            mapper = (IAeTypeMapper)mSchema2JavaMappings.get(typeName);
         }
         if(mapper == null)
            mapper = defaultMapper;
         return mapper.deserialize(aObj);
      }
      return aObj;
   }
}

/**
 * Interface which all type mappers should adhere to.
 */
interface IAeTypeMapper
{
   /**
    * Serializes a schema object to a string.
    * @param aObj The object being serialized
    * @return The string representing the object
    */
   public String serialize(Object aObj);

   /**
    * Deserializes a java object from a string.
    * @param aObj The string being deserialized
    * @return The object representing the schema type
    */
   public Object deserialize(String aObj);
}

/**
 * Simple object to string handler.
 */
class AeBasicMapper implements IAeTypeMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#serialize(java.lang.Object)
    */
   public String serialize(Object aObj)
   {
      if(aObj != null)
         return aObj.toString();
      return ""; //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return aObj;
   }
}

/**
 * Handles xsd boolean types.
 */
class AeBooleanMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      if ("1".equals(aObj)) //$NON-NLS-1$
      {
         return Boolean.TRUE;
      }
      else
      {
         return Boolean.valueOf(aObj);
      }
   }
}

/**
 * Handles xsd integer types.
 */
class AeBigIntegerMapper extends AeBigDecimalMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return ((BigDecimal)super.deserialize(aObj)).toBigInteger();
   }
}

/**
 * Handles xsd byte types.
 */
class AeByteMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new Byte(aObj);
   }
}

/**
 * Handles base64Binary types.
 */
class AeBase64BinaryMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.AeBasicMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aArg)
   {
      return new AeSchemaBase64Binary(aArg);
   }
}

/**
 * Handles xsd date types.
 */
class AeDateMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#serialize(java.lang.Object)
    */
   public String serialize(Object aObj)
   {
      if (aObj instanceof AeSchemaDate)
         return aObj.toString();
      if (aObj instanceof AeSchemaDateTime)
         return new AeSchemaDate(((AeSchemaDateTime) aObj).toDate()).toString();
      // Note: the following two cases should never happen, but they're easy to handle just in case.
      if (aObj instanceof Calendar)
         return new AeSchemaDate((Calendar) aObj).toString();
      if (aObj instanceof Date)
         return new AeSchemaDate((Date) aObj).toString();

      return super.serialize(aObj);
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      try
      {
         return new AeSchemaDate(aObj);
      }
      catch (Exception e)
      {
         // Also accept xsd:dateTime formatted strings.  This code was added for the following use case:
         //  1) javascript expression of the form "new Date(95, 2, 10)" - returns a java.util.Date object
         //  2) we serialize the Date object to a String formatted as an xsd:dateTime (because we don't
         //     necessarily know the type of the variable we are assigning to)
         //  3) we deserialize the value here but we KNOW we want an xsd:date
         try
         {
            return new AeSchemaDate(new AeSchemaDateTime(aObj).toDate());
         }
         catch (Exception e2)
         {
         }
         // TODO (MF) I removed the log statement to avoid generating error messages when setting variables via remote debugger. Restore once the designer code changes. 
         return new AeSchemaDate(new Date());
      }
   }
}

/**
 * Handles xsd date types.
 */
class AeDateTimeMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#serialize(java.lang.Object)
    */
   public String serialize(Object aObj)
   {
      if (aObj instanceof AeSchemaDateTime)
         return aObj.toString();
      // Note: the following two cases should never happen, but they're easy to handle just in case.
      if (aObj instanceof Calendar)
         return new AeSchemaDateTime((Calendar) aObj).toString();
      if (aObj instanceof Date)
         return new AeSchemaDateTime((Date) aObj).toString();
      return super.serialize(aObj);
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      try
      {
         return new AeSchemaDateTime(aObj);
      }
      catch (Exception e)
      {
         // This is unlikely - should only happen if a badly formatted xsd:dateTime is sent.
         // TODO (MF) I removed the log statement to avoid generating error messages when setting variables via remote debugger. Restore once the designer code changes. 
         return new AeSchemaDateTime(new Date());
      }
   }
}

/**
 * Handles xsd date types.
 */
class AeTimeMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#serialize(java.lang.Object)
    */
   public String serialize(Object aObj)
   {
      if(aObj instanceof AeSchemaTime)
         return aObj.toString();
      // Note: the following two cases should never happen, but they're easy to handle just in case.
      if (aObj instanceof Calendar)
         return new AeSchemaTime((Calendar) aObj).toString();
      if (aObj instanceof Date)
         return new AeSchemaTime((Date) aObj).toString();
      return super.serialize(aObj);
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      try
      {
         return new AeSchemaTime(aObj);
      }
      catch (Exception e)
      {
         // This is unlikely - should only happen if a badly formatted xsd:time is sent.
         // TODO (MF) I removed the log statement to avoid generating error messages when setting variables via remote debugger. Restore once the designer code changes. 
         return new AeSchemaTime(new Date());
      }
   }
}

/**
 * Handles xsd anyURI types.
 */
class AeAnyURIMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new AeSchemaAnyURI(aObj);
   }
}

class AeSchemaYearMonthMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.AeBasicMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new AeSchemaYearMonth(aObj);
   }
}

class AeSchemaYearMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.AeBasicMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new AeSchemaYear(aObj);
   }
}

class AeSchemaMonthDayMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.AeBasicMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new AeSchemaMonthDay(aObj);
   }
}

class AeSchemaDayMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.AeBasicMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new AeSchemaDay(aObj);
   }
}

class AeSchemaMonthMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.AeBasicMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new AeSchemaMonth(aObj);
   }
}

/**
 * Handles xsd duration types.
 */
class AeDurationMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      try
      {
         return new AeSchemaDuration(aObj);
      }
      catch (Exception e)
      {
         // This is unlikely - should only happen if a badly formatted xsd:duration is sent.
         // TODO (MF) I removed the log statement to avoid generating error messages when setting variables via remote debugger. Restore once the designer code changes. 
         return new AeSchemaTime(new Date());
      }
   }
}

/**
 * Handles xsd double types.
 */
class AeDoubleMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      try
      {
         return new Double(aObj);
      }
      catch (Exception ex)
      {
         // TODO (MF) I removed the log statement to avoid generating error messages when setting variables via remote debugger. Restore once the designer code changes. 
         return new Double(0);
      }
   }
}

/**
 * Handles xsd float types.
 */
class AeFloatMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      try
      {
         return new Float(aObj);
      }
      catch (Exception ex)
      {
         // TODO (MF) I removed the log statement to avoid generating error messages when setting variables via remote debugger. Restore once the designer code changes. 
         return new Float(0);
      }
   }
}

/**
 * Handles xsd decimal types.
 */
class AeBigDecimalMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      try
      {
         return new BigDecimal(aObj);
      }
      catch (Exception ex)
      {
         // TODO (MF) I removed the log statement to avoid generating error messages when setting variables via remote debugger. Restore once the designer code changes. 
         return new BigDecimal(0);
      }
   }
}

/**
 * Handles hexBinary types.
 */
class AeHexBinaryMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.AeBasicMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aArg)
   {
      return new AeSchemaHexBinary(aArg);
   }
}

/**
 * Handles xsd int types.
 */
class AeIntMapper extends AeDoubleMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new Integer(((Double)super.deserialize(aObj)).intValue());
   }
}

/**
 * Handles xsd long types.
 */
class AeLongMapper extends AeDoubleMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new Long(((Double)super.deserialize(aObj)).longValue());
   }
}

/**
 * Handles xsd QName types.
 */
class AeQNameMapper extends AeBasicMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new QName(aObj);
   }
}

/**
 * Handles xsd QName types.
 */
class AeShortMapper extends AeDoubleMapper
{
   /**
    * @see org.activebpel.rt.xml.schema.IAeTypeMapper#deserialize(java.lang.String)
    */
   public Object deserialize(String aObj)
   {
      return new Short(((Double)super.deserialize(aObj)).shortValue());
   }
}
