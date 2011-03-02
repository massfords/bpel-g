// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaDateTime.java,v 1.5.4.1 2008/04/21 16:05:13 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activebpel.rt.util.AeUtil;


/**
 * This class represents a time as defined by the xsd:dateTime datatype.  An object of this type
 * can be constructed from an xsd:dateTime formatted string, or from a Date or Calendar java
 * object.
 */
public class AeSchemaDateTime extends AeAbstractTZBasedSchemaType implements Serializable
{
   /** A regular expression for matching schema dateTime strings. */
   private static Pattern INPUT_PATTERN = Pattern.compile("(-?)([1-9]*[0-9]{4})-([0-9]{2})-([0-9]{2})T([0-9]{2}):([0-9]{2}):([0-9]{2})(\\.([0-9]*))?(Z|(([\\+\\-])([0-9]{2}):([0-9]{2})))?"); //$NON-NLS-1$
   /** The output format to use for toString(). */
   private static String OUTPUT_PATTERN = "{0}{1,number,0000}-{2,number,00}-{3,number,00}T{4,number,00}:{5,number,00}:{6,number,00}{7,choice,0#|0<.{7,number,000}}Z"; //$NON-NLS-1$

   /** A Gregorian Calendar object - holds all of the actual time and date info. */
   protected GregorianCalendar mCalendar;

   /**
    * Simple constructor - creates a schema dateTime object with a default GregorianCalendar
    * representing the current time.
    */
   protected AeSchemaDateTime()
   {
      setCalendar(new GregorianCalendar());
   }

   /**
    * Creates a schema dateTime object given a xsd:dateTime formatted String.  This string is
    * typically gotten from a value found in an XML document.
    *
    * @param aSchemaDateTime A date formatted in XSD format (subset of ISO 8601).
    */
   public AeSchemaDateTime(String aSchemaDateTime)
   {
      super(aSchemaDateTime);
   }

   /**
    * Creates a schema dateTime object from a java Date object.
    *
    * @param aDate
    */
   public AeSchemaDateTime(Date aDate)
   {
      this();
      getCalendar().setTimeZone(sUTCTimeZone);
      // Work around a GNU Classpath bug: the GNU Calendar implementation
      // doesn't recalcuate its fields after setting the time zone, so always
      // set the time after the time zone.
      getCalendar().setTime(aDate);
   }

   /**
    * Creates a schema dateTime object from a java Calendar object.
    *
    * @param aCalendar
    */
   public AeSchemaDateTime(Calendar aCalendar)
   {
      setCalendar(new GregorianCalendar());
      initFromCalendar(aCalendar);
   }

   /**
    * Creates a schema dateTime object from a utc milliseconds.
    * @param aUtcMillis
    */
   public AeSchemaDateTime(long aUtcMillis)
   {
      this( new Date(aUtcMillis) );
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getInputPattern()
    */
   protected Pattern getInputPattern()
   {
      return INPUT_PATTERN;
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#processMatcher(java.util.regex.Matcher)
    */
   protected void processMatcher(Matcher aMatcher)
   {
      boolean isDateNegative = "-".equals(aMatcher.group(1)); //$NON-NLS-1$
      String yearStr = aMatcher.group(2);
      String monthStr = aMatcher.group(3);
      String dayStr = aMatcher.group(4);
      String hourStr = aMatcher.group(5);
      String minuteStr = aMatcher.group(6);
      String secondStr = aMatcher.group(7);
      String fracSecondStr = aMatcher.group(9);
      if (AeUtil.isNullOrEmpty(fracSecondStr))
         fracSecondStr = "0"; //$NON-NLS-1$
      String tzStr = aMatcher.group(10);
      boolean isUTC = (AeUtil.isNullOrEmpty(tzStr)) || ("Z".equals(tzStr)); //$NON-NLS-1$
      String tzHr = null;
      String tzMin = null;
      char tzDir = '+';
      if (!isUTC)
      {
         tzDir = aMatcher.group(12).charAt(0);
         tzHr = aMatcher.group(13);
         tzMin = aMatcher.group(14);
      }

      int year = new Integer(yearStr).intValue();
      int era = GregorianCalendar.AD;
      if (isDateNegative)
      {
         era = GregorianCalendar.BC;
      }
      int millis = 0;
      if (fracSecondStr != null)
      {
         millis = (int) (1000F * new Float("0." + fracSecondStr).floatValue()); //$NON-NLS-1$
      }
      TimeZone tz = createTimeZone(tzHr, tzMin, tzDir);
      setTimeZone(tz);

      setCalendar(new GregorianCalendar());
      getCalendar().setTimeZone(tz);
      getCalendar().set(Calendar.ERA, era);
      getCalendar().set(Calendar.YEAR, year);
      getCalendar().set(Calendar.MONTH, new Integer(monthStr).intValue() - 1);
      getCalendar().set(Calendar.DAY_OF_MONTH, new Integer(dayStr).intValue());
      getCalendar().set(Calendar.HOUR_OF_DAY, new Integer(hourStr).intValue());
      getCalendar().set(Calendar.MINUTE, new Integer(minuteStr).intValue());
      getCalendar().set(Calendar.SECOND, new Integer(secondStr).intValue());
      getCalendar().set(Calendar.MILLISECOND, millis);
      // Need to update the internal Calendar data structures by asking for the time in millis
      // because they are not updated until some internal data is queried.
      long time = getCalendar().getTimeInMillis();
      // Now set the time zone to UTC - the next time someone asks for any calendar field, the
      // internal data structures will be updated.
      getCalendar().setTimeZone(sUTCTimeZone);
      setTimeZone(sUTCTimeZone);
      // Work around a GNU Classpath bug: the GNU Calendar implementation
      // doesn't recalcuate its fields after setting the time zone, so always
      // set the time after the time zone.
      getCalendar().setTimeInMillis(time);
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getSchemaTypeName()
    */
   protected String getSchemaTypeName()
   {
      return "xsd:dateTime"; //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getOutputPatternArguments()
    */
   protected Object[] getOutputPatternArguments()
   {
      return new Object [] {
            (getCalendar().get(Calendar.ERA) == GregorianCalendar.BC) ? "-" : "", //$NON-NLS-1$ //$NON-NLS-2$
            new Integer(getCalendar().get(Calendar.YEAR)),
            new Integer(getCalendar().get(Calendar.MONTH) + 1),
            new Integer(getCalendar().get(Calendar.DAY_OF_MONTH)),
            new Integer(getCalendar().get(Calendar.HOUR_OF_DAY)),
            new Integer(getCalendar().get(Calendar.MINUTE)),
            new Integer(getCalendar().get(Calendar.SECOND)),
            new Integer(getCalendar().get(Calendar.MILLISECOND))
      };
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getOutputPattern()
    */
   protected String getOutputPattern()
   {
      return OUTPUT_PATTERN;
   }

   /**
    * Gets the date time as a java Calendar.
    */
   public GregorianCalendar getCalendar()
   {
      return mCalendar;
   }

   /**
    * Initializes the internal state of the schema dateTime object from a java Calendar object.
    *
    * @param aCalendar
    */
   protected void initFromCalendar(Calendar aCalendar)
   {
      // Clone the calendar and convert it to UTC.
      Calendar cal = (Calendar) aCalendar.clone();
      long millis = cal.getTimeInMillis();
      cal.setTimeZone(sUTCTimeZone);
      // Work around a GNU Classpath bug: the GNU Calendar implementation
      // doesn't recalcuate its fields after setting the time zone, so always
      // set the time after the time zone.
      cal.setTimeInMillis(millis);

      // Create a new calendar and set it to UTC.
      getCalendar().setTimeZone(sUTCTimeZone);
      getCalendar().set(Calendar.YEAR, cal.get(Calendar.YEAR));
      getCalendar().set(Calendar.MONTH, cal.get(Calendar.MONTH));
      getCalendar().set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH));
      getCalendar().set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
      getCalendar().set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
      getCalendar().set(Calendar.SECOND, cal.get(Calendar.SECOND));
      getCalendar().set(Calendar.MILLISECOND, cal.get(Calendar.MILLISECOND));
      getCalendar().set(Calendar.ERA, cal.get(Calendar.ERA));
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObject)
   {
      if(aObject instanceof AeSchemaDateTime)
      {
         AeSchemaDateTime dt1 = this;
         AeSchemaDateTime dt2 = (AeSchemaDateTime) aObject;
         return dt1.getCalendar().equals(dt2.getCalendar());
      }
      else
      {
         return false;
      }
   }

   /**
    * Return the stored date.
    * @return java.util.Date
    */
   public Date toDate()
   {
      return getCalendar().getTime();
   }

   /**
    * Gets the year.
    */
   public int getYear()
   {
      return getCalendar().get(Calendar.YEAR);
   }

   /**
    * Sets the year.  This will also set the era if the year is negative.
    *
    * @param aYear
    */
   public void setYear(int aYear)
   {
      int year = aYear;
      if (aYear < 0)
      {
         getCalendar().set(Calendar.ERA, GregorianCalendar.BC);
         year = Math.abs(aYear);
      }
      else
      {
         getCalendar().set(Calendar.ERA, GregorianCalendar.AD);
      }
      getCalendar().set(Calendar.YEAR, year);
   }

   /**
    * Gets the month.
    */
   public int getMonth()
   {
      return getCalendar().get(Calendar.MONTH) + 1;
   }

   /**
    * Sets the month (1 for January, etc...).
    *
    * @param aMonth
    */
   public void setMonth(int aMonth)
   {
      getCalendar().set(Calendar.MONTH, aMonth - 1);
   }

   /**
    * Gets the day.
    */
   public int getDay()
   {
      return getCalendar().get(Calendar.DAY_OF_MONTH);
   }

   /**
    * Sets the day (of the month).
    *
    * @param aDay
    */
   public void setDay(int aDay)
   {
      getCalendar().set(Calendar.DAY_OF_MONTH, aDay);
   }

   /**
    * Gets the hour. &amp;lparen;24 hour clock$rparen;
    */
   public int getHour()
   {
      return getCalendar().get(Calendar.HOUR_OF_DAY);
   }

   /**
    * Sets the hour of the day.  &amp;lparen;24 hour clock, 0 for midnight, 12 for noon$rparen;
    *
    * @param aHour
    */
   public void setHour(int aHour)
   {
      getCalendar().set(Calendar.HOUR_OF_DAY, aHour);
   }

   /**
    * Gets the minute.
    */
   public int getMinute()
   {
      return getCalendar().get(Calendar.MINUTE);
   }

   /**
    * Sets the minute.
    *
    * @param aMinute
    */
   public void setMinute(int aMinute)
   {
      getCalendar().set(Calendar.MINUTE, aMinute);
   }

   /**
    * Gets the second.
    */
   public int getSecond()
   {
      return getCalendar().get(Calendar.SECOND);
   }

   /**
    * Sets the second.
    *
    * @param aSecond
    */
   public void setSecond(int aSecond)
   {
      getCalendar().set(Calendar.SECOND, aSecond);
   }

   /**
    * Gets the millis.
    */
   public int getMillisecond()
   {
      return getCalendar().get(Calendar.MILLISECOND);
   }

   /**
    * Sets the millis.
    *
    * @param aMillisecond
    */
   public void setMillisecond(int aMillisecond)
   {
      getCalendar().set(Calendar.MILLISECOND, aMillisecond);
   }

   /**
    * Adds the given duration to this dateTime.
    *
    * @param aSchemaDuration
    */
   public void addDuration(AeSchemaDuration aSchemaDuration)
   {
      getCalendar().add(Calendar.YEAR, aSchemaDuration.getYears());
      getCalendar().add(Calendar.MONTH, aSchemaDuration.getMonths());
      getCalendar().add(Calendar.DAY_OF_YEAR, aSchemaDuration.getDays());

      getCalendar().add(Calendar.HOUR_OF_DAY, aSchemaDuration.getHours());
      getCalendar().add(Calendar.MINUTE, aSchemaDuration.getMinutes());
      getCalendar().add(Calendar.SECOND, aSchemaDuration.getSeconds());
      getCalendar().add(Calendar.MILLISECOND, aSchemaDuration.getMilliseconds());
   }

   /**
    * @param aCalendar The calendar to set.
    */
   protected void setCalendar(GregorianCalendar aCalendar)
   {
      mCalendar = aCalendar;
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaType#accept(org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor)
    */
   public void accept(IAeSchemaTypeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
