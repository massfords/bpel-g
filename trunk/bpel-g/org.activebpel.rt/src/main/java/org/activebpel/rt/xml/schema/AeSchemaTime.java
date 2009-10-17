// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaTime.java,v 1.2 2006/09/07 14:41:12 ewittmann Exp $
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
 * This class represents a time as defined by the xsd:time datatype. An object of this type can be 
 * constructed from an xsd:time formatted string, or from a Date or Calendar java object.
 */
public class AeSchemaTime extends AeSchemaDateTime
{
   /** A regular expression for matching schema dateTime strings. */
   private static Pattern INPUT_PATTERN = Pattern.compile("([0-9]{2}):([0-9]{2}):([0-9]{2})(\\.([0-9]*))?(Z|(([\\+\\-])([0-9]{2}):([0-9]{2})))?"); //$NON-NLS-1$
   /** The output format to use for toString(). */
   private static String OUTPUT_PATTERN = "{0,number,00}:{1,number,00}:{2,number,00}{3,choice,0#|0<.{3,number,000}}Z"; //$NON-NLS-1$

   /**
    * Creates a schema time object given a xsd:time formatted String.  This string is 
    * typically gotten from a value found in an XML document.
    * 
    * @param aSchemaTimeStr A time formatted in XSD format (subset of ISO 8601).
    */
   public AeSchemaTime(String aSchemaTimeStr)
   {
      super(aSchemaTimeStr);
   }

   /**
    * Creates a schema time object from a java Date object.
    * 
    * @param aDate
    */
   public AeSchemaTime(Date aDate)
   {
      super(aDate);
      // This causes the internal time structures to be re-computed
      getCalendar().getTimeInMillis();
      // Now normalize all of the date fields
      getCalendar().set(Calendar.YEAR, 2004);
      getCalendar().set(Calendar.MONTH, 0);
      getCalendar().set(Calendar.DAY_OF_MONTH, 1);
   }

   /**
    * Creates a schema date object from a java Calendar object.
    * 
    * @param aCalendar
    */
   public AeSchemaTime(Calendar aCalendar)
   {
      super(aCalendar);
      // This causes the internal time structures to be re-computed
      getCalendar().getTimeInMillis();
      // Now normalize all of the date fields
      getCalendar().set(Calendar.YEAR, 2004);
      getCalendar().set(Calendar.MONTH, 0);
      getCalendar().set(Calendar.DAY_OF_MONTH, 1);
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
      String hourStr = aMatcher.group(1);
      String minuteStr = aMatcher.group(2);
      String secondStr = aMatcher.group(3);
      String fracSecondStr = aMatcher.group(5);
      if (AeUtil.isNullOrEmpty(fracSecondStr))
         fracSecondStr = "0"; //$NON-NLS-1$
      String tzStr = aMatcher.group(6);
      boolean isUTC = (AeUtil.isNullOrEmpty(tzStr)) || ("Z".equals(tzStr)); //$NON-NLS-1$
      String tzHr = null;
      String tzMin = null;
      char tzDir = '+';
      if (!isUTC)
      {
         tzDir = aMatcher.group(8).charAt(0);
         tzHr = aMatcher.group(9);
         tzMin = aMatcher.group(10);
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
            new Integer(getCalendar().get(Calendar.HOUR_OF_DAY)),
            new Integer(getCalendar().get(Calendar.MINUTE)),
            new Integer(getCalendar().get(Calendar.SECOND)),
            new Integer(getCalendar().get(Calendar.MILLISECOND))
      };
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#getYear()
    */
   public int getYear()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#setYear(int)
    */
   public void setYear(int aYear)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#getMonth()
    */
   public int getMonth()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#setMonth(int)
    */
   public void setMonth(int aMonth)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#getDay()
    */
   public int getDay()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeSchemaDateTime#setDay(int)
    */
   public void setDay(int aDay)
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
