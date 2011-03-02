// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaDate.java,v 1.4.4.1 2008/04/21 16:05:13 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activebpel.rt.util.AeUtil;

/**
 * This class represents a date as defined by the xsd:date datatype.  An object of this type
 * can be constructed from an xsd:date formatted string, or from a Date or Calendar java 
 * object.<br/>
 * <br/>
 * Note: if an xsd:date formatted string is used to construct this object, and that date string
 * contains the optional timezone offset (e.g. +05:00), there is the potential for the resulting
 * date to be rolled back to the previous day.  This is because this object assumes 00:00:00 as
 * the "time" for the given date.  It then converts the date to UTC, which may roll the date
 * back one day.
 */
public class AeSchemaDate extends AeSchemaDateTime
{
   /** A regular expression for matching schema date strings. */
   private static Pattern INPUT_PATTERN = Pattern.compile("(-?)([1-9]*[0-9]{4})-([0-9]{2})-([0-9]{2})(Z|(([\\+\\-])([0-9]{2}):([0-9]{2})))?"); //$NON-NLS-1$
   /** The output format to use for toString(). */
   private static String OUTPUT_PATTERN = "{0}{1,number,0000}-{2,number,00}-{3,number,00}"; //$NON-NLS-1$

   /**
    * Creates a schema date object given a xsd:date formatted String.  This string is 
    * typically gotten from a value found in an XML document.
    * 
    * @param aSchemaDateStr A date formatted in XSD format (subset of ISO 8601).
    */
   public AeSchemaDate(String aSchemaDateStr)
   {
      super(aSchemaDateStr);
   }

   /**
    * Creates a schema date object from a java Date object.
    * 
    * @param aDate
    */
   public AeSchemaDate(Date aDate)
   {
      super(aDate);
      // This causes the internal time structures to be re-computed
      getCalendar().getTimeInMillis();
      // Now 0 out all of the time fields
      getCalendar().set(Calendar.HOUR_OF_DAY, 0);
      getCalendar().set(Calendar.MINUTE, 0);
      getCalendar().set(Calendar.SECOND, 0);
      getCalendar().set(Calendar.MILLISECOND, 0);
   }

   /**
    * Creates a schema date object from a java Calendar object.
    * 
    * @param aCalendar
    */
   public AeSchemaDate(Calendar aCalendar)
   {
      super(aCalendar);
      // This causes the internal time structures to be re-computed
      getCalendar().getTimeInMillis();
      // Now 0 out all of the time fields
      getCalendar().set(Calendar.HOUR_OF_DAY, 0);
      getCalendar().set(Calendar.MINUTE, 0);
      getCalendar().set(Calendar.SECOND, 0);
      getCalendar().set(Calendar.MILLISECOND, 0);
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#getInputPattern()
    */
   protected Pattern getInputPattern()
   {
      return INPUT_PATTERN;
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#processMatcher(java.util.regex.Matcher)
    */
   protected void processMatcher(Matcher aMatcher)
   {
      boolean isDateNegative = "-".equals(aMatcher.group(1)); //$NON-NLS-1$
      String yearStr = aMatcher.group(2);
      String monthStr = aMatcher.group(3);
      String dayStr = aMatcher.group(4);
      String tzStr = aMatcher.group(5);
      boolean isUTC = (AeUtil.isNullOrEmpty(tzStr)) || ("Z".equals(tzStr)); //$NON-NLS-1$
      String tzHr = null;
      String tzMin = null;
      char tzDir = '+';
      if (!isUTC)
      {
         tzDir = aMatcher.group(7).charAt(0);
         tzHr = aMatcher.group(8);
         tzMin = aMatcher.group(9);
      }

      int era = GregorianCalendar.AD;
      if (isDateNegative)
         era = GregorianCalendar.BC;

      TimeZone tz = createTimeZone(tzHr, tzMin, tzDir);
      setTimeZone(tz);

      setCalendar(new GregorianCalendar());
      getCalendar().setTimeZone(tz);
      getCalendar().set(Calendar.ERA, era);
      getCalendar().set(Calendar.YEAR, new Integer(yearStr).intValue());
      getCalendar().set(Calendar.MONTH, new Integer(monthStr).intValue() - 1); // month is 0 based
      getCalendar().set(Calendar.DAY_OF_MONTH, new Integer(dayStr).intValue());
      getCalendar().set(Calendar.HOUR_OF_DAY, 0);
      getCalendar().set(Calendar.MINUTE, 0);
      getCalendar().set(Calendar.SECOND, 0);
      getCalendar().set(Calendar.MILLISECOND, 0);
      // Need to update the internal Calendar data structures by asking for the time in millis
      // because they are not updated until some internal data is queried.
      long millis = getCalendar().getTimeInMillis();
      // Now set the time zone to UTC - the next time someone asks for any calendar field, the 
      // internal data structures will be updated.
      getCalendar().setTimeZone(sUTCTimeZone);
      setTimeZone(sUTCTimeZone);
      // Work around a GNU Classpath bug: the GNU Calendar implementation
      // doesn't recalcuate its fields after setting the time zone, so always
      // set the time after the time zone.
      getCalendar().setTimeInMillis(millis);
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#getOutputPattern()
    */
   protected String getOutputPattern()
   {
      return OUTPUT_PATTERN;
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#getOutputPatternArguments()
    */
   protected Object[] getOutputPatternArguments()
   {
      return new Object [] {
            (getCalendar().get(Calendar.ERA) == GregorianCalendar.BC) ? "-" : "", //$NON-NLS-1$ //$NON-NLS-2$
            new Integer(getCalendar().get(Calendar.YEAR)),
            new Integer(getCalendar().get(Calendar.MONTH) + 1),
            new Integer(getCalendar().get(Calendar.DAY_OF_MONTH))
      };
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#getHour()
    */
   public int getHour()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#setHour(int)
    */
   public void setHour(int aHour)
   {
      throw new UnsupportedOperationException();
   }


   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#getMinute()
    */
   public int getMinute()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#setMinute(int)
    */
   public void setMinute(int aMinute)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#getSecond()
    */
   public int getSecond()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#setSecond(int)
    */
   public void setSecond(int aSecond)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#getMillisecond()
    */
   public int getMillisecond()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaType#accept(org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor)
    */
   public void accept(IAeSchemaTypeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
