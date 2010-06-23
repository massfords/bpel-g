// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/xquery/AeXQueryTypeMapper.java,v 1.2 2006/11/01 19:38:10 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.sf.saxon.functions.Component;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.AnyURIValue;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.Base64BinaryValue;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.DateValue;
import net.sf.saxon.value.DecimalValue;
import net.sf.saxon.value.DurationValue;
import net.sf.saxon.value.GDayValue;
import net.sf.saxon.value.GMonthDayValue;
import net.sf.saxon.value.GMonthValue;
import net.sf.saxon.value.GYearMonthValue;
import net.sf.saxon.value.GYearValue;
import net.sf.saxon.value.HexBinaryValue;
import net.sf.saxon.value.IntegerValue;
import net.sf.saxon.value.TimeValue;

import org.activebpel.rt.AeException;
import org.activebpel.rt.xml.schema.AeSchemaAnyURI;
import org.activebpel.rt.xml.schema.AeSchemaBase64Binary;
import org.activebpel.rt.xml.schema.AeSchemaDate;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.activebpel.rt.xml.schema.AeSchemaDay;
import org.activebpel.rt.xml.schema.AeSchemaDuration;
import org.activebpel.rt.xml.schema.AeSchemaHexBinary;
import org.activebpel.rt.xml.schema.AeSchemaMonth;
import org.activebpel.rt.xml.schema.AeSchemaMonthDay;
import org.activebpel.rt.xml.schema.AeSchemaTime;
import org.activebpel.rt.xml.schema.AeSchemaYear;
import org.activebpel.rt.xml.schema.AeSchemaYearMonth;

/**
 * This class is used to convert Saxon AtomicValue instances to Jave/Ae objects.
 */
public class AeXQueryTypeMapper
{
   /** The singleton instance. */
   private static AeXQueryTypeMapper sInstance = new AeXQueryTypeMapper();
   
   /** The map of Saxon types to specific convert methods. */
   private Map mMethodMap;

   /**
    * Private c'tor.
    */
   private AeXQueryTypeMapper()
   {
      setMethodMap(new HashMap());
      createMapping(Type.ANY_URI, "convertAnyURI"); //$NON-NLS-1$
      createMapping(Type.BASE64_BINARY, "convertBase64Binary"); //$NON-NLS-1$
      createMapping(Type.DATE, "convertDate"); //$NON-NLS-1$
      createMapping(Type.DATE_TIME, "convertDateTime"); //$NON-NLS-1$
      createMapping(Type.DURATION, "convertDuration"); //$NON-NLS-1$
      createMapping(Type.G_DAY, "convertGDay"); //$NON-NLS-1$
      createMapping(Type.G_MONTH, "convertGMonth"); //$NON-NLS-1$
      createMapping(Type.G_MONTH_DAY, "convertGMonthDay"); //$NON-NLS-1$
      createMapping(Type.G_YEAR, "convertGYear"); //$NON-NLS-1$
      createMapping(Type.G_YEAR_MONTH, "convertGYearMonth"); //$NON-NLS-1$
      createMapping(Type.HEX_BINARY, "convertHexBinary"); //$NON-NLS-1$
      createMapping(Type.TIME, "convertTime"); //$NON-NLS-1$
   }
   
   /**
    * Creates a mapping from a Saxon type to a Method to use to do the conversion.
    * 
    * @param aType
    * @param aMethodName
    */
   protected void createMapping(int aType, String aMethodName)
   {
      Object key = new Integer(aType);
      Method value = getMethod(aMethodName);
      getMethodMap().put(key, value);
   }

   /**
    * Convenience method to get the method with the given name.
    * 
    * @param aMethodName
    */
   protected Method getMethod(String aMethodName)
   {
      try
      {
         return AeXQueryTypeMapper.class.getMethod(aMethodName, new Class [] { AtomicValue.class });
      }
      catch (Exception ex)
      {
         throw new RuntimeException("Failed to find method: " + aMethodName); //$NON-NLS-1$
      }
   }

   /**
    * Returns true if this type mapper has a mapping for the given AtomicValue.
    * 
    * @param aValue
    */
   public static boolean canConvert(AtomicValue aValue)
   {
      Object key = new Integer(aValue.getItemType(null).getPrimitiveType());
      return sInstance.getMethodMap().containsKey(key);
   }

   /**
    * Converts a Saxon atomic value to a Java object.  Note that users of this class should call
    * canConvert prior to calling convert.  If canConvert returns false, then convert should not
    * be called (otherwise it will throw an IllegalArgumentException).
    * 
    * @param aValue
    */
   public static Object convert(AtomicValue aValue)
   {
      Object rval = null;

      AeXQueryTypeMapper mapper = sInstance;
      Object key = new Integer(aValue.getItemType(null).getPrimitiveType());
      Method method = (Method) mapper.getMethodMap().get(key);
      if (method == null)
         throw new IllegalArgumentException();
      Object [] args = new Object[] { aValue };
      try
      {
         rval = method.invoke(mapper, args);
      }
      catch (Exception ex)
      {
         throw new IllegalArgumentException(ex.getLocalizedMessage());
      }
      return rval;
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertAnyURI(AtomicValue aValue)
   {
      return new AeSchemaAnyURI(((AnyURIValue) aValue).getStringValue());
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertBase64Binary(AtomicValue aValue)
   {
      return new AeSchemaBase64Binary(((Base64BinaryValue) aValue).getStringValue());
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertDate(AtomicValue aValue)
   {
      return new AeSchemaDate(((DateValue) aValue).getCalendar());
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertDateTime(AtomicValue aValue)
   {
      return new AeSchemaDateTime(((DateTimeValue) aValue).getCalendar());
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertDuration(AtomicValue aValue)
   {
      DurationValue durationValue = (DurationValue) aValue;
      try
      {
         boolean isNegative = false;
         int years = (int) ((IntegerValue) durationValue.getComponent(Component.YEAR)).longValue();
         if (years < 0)
            isNegative = true;
         years = Math.abs(years);
         int months = (int) Math.abs(((IntegerValue) durationValue.getComponent(Component.MONTH)).longValue());
         int days = (int) Math.abs(((IntegerValue) durationValue.getComponent(Component.DAY)).longValue());
         int hours = (int) Math.abs(((IntegerValue) durationValue.getComponent(Component.HOURS)).longValue());
         int minutes = (int) Math.abs(((IntegerValue) durationValue.getComponent(Component.MINUTES)).longValue());
         BigDecimal secondsBD = ((DecimalValue) durationValue.getComponent(Component.SECONDS)).getValue();
         // Convert from seconds to millis.
         // TODO (EPW) There is a potential loss of precision here.
         BigDecimal millisBD = secondsBD.multiply(BigDecimal.valueOf(1000L));
         int millis = Math.abs(millisBD.intValue());
         int seconds = millis / 1000;
         millis = millis % 1000;

         return new AeSchemaDuration(isNegative, years, months, days, hours, minutes, seconds, millis);
      }
      catch (XPathException ex)
      {
         AeException.logError(ex);
         return new AeSchemaDuration((durationValue).getStringValue());
      }
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertGDay(AtomicValue aValue)
   {
      GDayValue dayVal = (GDayValue) aValue;
      return new AeSchemaDay(dayVal.getDay(), dayVal.getTimezoneInMinutes());
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertGMonth(AtomicValue aValue)
   {
      GMonthValue monthValue = (GMonthValue) aValue;
      return new AeSchemaMonth(monthValue.getMonth(), monthValue.getTimezoneInMinutes());
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertGMonthDay(AtomicValue aValue)
   {
      GMonthDayValue monthDayValue = (GMonthDayValue) aValue;
      return new AeSchemaMonthDay(monthDayValue.getMonth(), monthDayValue.getDay(), monthDayValue.getTimezoneInMinutes());
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertGYear(AtomicValue aValue)
   {
      GYearValue yearValue = (GYearValue) aValue;
      return new AeSchemaYear(yearValue.getYear(), yearValue.getTimezoneInMinutes());
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertGYearMonth(AtomicValue aValue)
   {
      GYearMonthValue yearMonthValue = (GYearMonthValue) aValue;
      return new AeSchemaYearMonth(yearMonthValue.getYear(), yearMonthValue.getMonth(), yearMonthValue.getTimezoneInMinutes());
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertHexBinary(AtomicValue aValue)
   {
      return new AeSchemaHexBinary(((HexBinaryValue) aValue).getStringValue());
   }

   /**
    * Converts a Saxon native type to its corresponding Java/Ae type.
    * 
    * @param aValue
    */
   public Object convertTime(AtomicValue aValue)
   {
      return new AeSchemaTime(((TimeValue) aValue).getCalendar());
   }

   /**
    * @return Returns the methodMap.
    */
   public Map getMethodMap()
   {
      return mMethodMap;
   }

   /**
    * @param aMethodMap The methodMap to set.
    */
   public void setMethodMap(Map aMethodMap)
   {
      mMethodMap = aMethodMap;
   }
}
