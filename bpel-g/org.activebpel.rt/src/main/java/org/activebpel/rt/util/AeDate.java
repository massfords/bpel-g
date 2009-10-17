// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeDate.java,v 1.4
// 2006/06/26 16:46:44 mford Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.activebpel.rt.xml.schema.AeSchemaDate;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.activebpel.rt.xml.schema.AeSchemaTime;
import org.activebpel.rt.xml.schema.AeSchemaTypeParseException;

/**
 * Utility class for date parsing.
 */
public class AeDate
{
   /** Indicates the date was parsed using a pattern which contained date and time. */
   public final static int DATETIME = 0;

   /** Indicates the date was parsed using a pattern which contained only the date. */
   public final static int DATE = 1;

   /** Indicates the date was parsed using a pattern which contained only the time. */
   public final static int TIME = 2;

   /** List of common date time format patterns. */
   private static List sPatterns = new ArrayList();

   static
   {
      sPatterns.add(new AeDate("yyyy/MM/dd hh:mm:ss a", DATETIME)); // 2006/01/27 2:54:30 PM //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy/MM/dd hh:mm a", DATETIME)); // 2006/01/27 2:54 PM //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy/MM/dd HH:mm:ss", DATETIME)); // 2006/01/27 14:54:30 //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy/MM/dd HH:mm", DATETIME)); // 2006/01/27 14:54 //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy/MM/dd", DATE)); // 2006/01/27 //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy-MM-dd hh:mm:ss a", DATETIME)); // 2006-01-27 2:54:30 PM //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy-MM-dd hh:mm a", DATETIME)); // 2006-01-27 2:54 PM //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy-MM-dd HH:mm:ss", DATETIME)); // 2006-01-27 14:54:30 //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy-MM-dd HH:mm", DATETIME)); // 2006-01-27 14:54 //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy-MM-dd", DATE)); // 2006-01-27 //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy.MM.dd hh:mm:ss a", DATETIME)); // 2006.01.27 2:54:30 PM //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy.MM.dd hh:mm a", DATETIME)); // 2006.01.27 2:54 PM //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy.MM.dd HH:mm:ss", DATETIME)); // 2006.01.27 14:54:30 //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy.MM.dd HH:mm", DATETIME)); // 2006.01.27 14:54 //$NON-NLS-1$
      sPatterns.add(new AeDate("yyyy.MM.dd", DATE)); // 2006.01.27 //$NON-NLS-1$
   }

   /**
    * Date format type. E.g. DATETIME, DATE or TIME.
    */
   private int mType;

   /**
    * Date format pattern.
    */
   private String mPattern;

   /**
    * Parsed Date object.
    */
   private Date mDate;

   /**
    * Default ctor
    * 
    * @param aPattern date format pattern.
    * @param aPatternType date format pattern type.
    */
   public AeDate(String aPattern, int aPatternType)
   {
      this(null, aPattern, aPatternType);
   }

   /**
    * Constructs an AeDate object with the given date, pattern and type.
    * 
    * @param aPattern date format pattern.
    * @param aPatternType date format pattern type.
    */
   public AeDate(Date aDate, String aPattern, int aPatternType)
   {
      mDate = aDate;
      mPattern = aPattern;
      mType = aPatternType;
   }

   /**
    * @return Returns the date.
    */
   public Date getDate()
   {
      return mDate;
   }

   /**
    * @return Returns the pattern.
    */
   public String getPattern()
   {
      return mPattern;
   }

   /**
    * @return Returns the type.
    */
   public int getType()
   {
      return mType;
   }

   /**
    * Parses and returns a AeDate object given a date and time as a string.
    * 
    * @param aDateTime date string
    * @return Parsed date or null if the string date format is not supported.
    */
   public static AeDate parse(String aDateTime)
   {
      AeDate rVal = null;
      if (AeUtil.notNullOrEmpty(aDateTime))
      {
         for (int i = 0; i < sPatterns.size(); i++)
         {
            AeDate datePattern = (AeDate) sPatterns.get(i);
            Date date = parseDate(aDateTime, datePattern.getPattern());
            if (date != null)
            {
               rVal = new AeDate(date, datePattern.getPattern(), datePattern.getType());
               break;
            }
         }
         // try ISO8601 date and time format
         try
         {
            Date date = (new AeSchemaDateTime(aDateTime)).toDate();
            rVal = new AeDate(date, null, DATETIME);
         }
         catch (AeSchemaTypeParseException e)
         {
            // ignore
         }
         if (rVal == null)
         {
            // try ISO8601 date only format
            try
            {
               Date date = (new AeSchemaDate(aDateTime)).toDate();
               rVal = new AeDate(date, null, DATE);
            }
            catch (AeSchemaTypeParseException e)
            {
               // ignore
            }
         }
         if (rVal == null)
         {
            // try ISO8601 time only format
            try
            {
               Date date = (new AeSchemaTime(aDateTime)).toDate();
               rVal = new AeDate(date, null, TIME);
            }
            catch (AeSchemaTypeParseException e)
            {
               // ignore
            }
         }
      }
      return rVal;
   }

   /**
    * Parses and returns a Date object given a date and time as a string.
    * 
    * @param aDateTime date string
    * @return Parsed date or null if the string date format is not supported.
    */
   public static Date parseDate(String aDateTime)
   {
      AeDate aeDate = parse(aDateTime);
      if (aeDate != null)
      {
         return aeDate.getDate();
      }
      else
      {
         return null;
      }
   }

   /**
    * Parses and returns a Date object given a date and time as a string as well as the date format pattern.
    * 
    * @param aDateTime date string
    * @param aDateTimeFormatPattern format pattern
    * @return Parsed date or null if the string date format is not supported.
    */
   public static Date parseDate(String aDateTime, String aDateTimeFormatPattern)
   {
      Date rVal = null;
      if (AeUtil.notNullOrEmpty(aDateTime))
      {
         DateFormat dateFormat = AeUtil.notNullOrEmpty(aDateTimeFormatPattern) ? new SimpleDateFormat(
               aDateTimeFormatPattern) : new SimpleDateFormat();
         try
         {
            rVal = dateFormat.parse(aDateTime);
         }
         catch (ParseException e)
         {
            // ignore
         }
      }
      return rVal;
   }

   /**
    * Returns a date object with the time set to 00:00 (start of the day).
    * 
    * @param aDate
    */
   public static Date getStartOfDay(Date aDate)
   {
      Date rVal = null;
      if (aDate != null)
      {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(aDate);
         calendar.clear(Calendar.HOUR);
         calendar.clear(Calendar.MINUTE);
         calendar.clear(Calendar.SECOND);
         calendar.clear(Calendar.MILLISECOND);
         rVal = calendar.getTime();
      }
      return rVal;
   }

   /**
    * Returns a date object with the time set to 23:59 (end of the day).
    * 
    * @param aDate
    */
   public static Date getEndOfDay(Date aDate)
   {
      Date rVal = null;
      if (aDate != null)
      {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(aDate);
         calendar.set(Calendar.HOUR, 23);
         calendar.set(Calendar.MINUTE, 59);
         calendar.set(Calendar.SECOND, 59);
         calendar.set(Calendar.MILLISECOND, 999);
         rVal = calendar.getTime();
      }
      return rVal;
   }

   /**
    * Adds one day to the given date object. The time is unchanged.
    * 
    * @param aDate
    */
   public static Date getNextDay(Date aDate)
   {
      Date rVal = null;
      if (aDate != null)
      {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(aDate);
         calendar.add(Calendar.DATE, 1);
         rVal = calendar.getTime();
      }
      return rVal;
   }

   /**
    * Substracts one day from the given date object. The time is unchanged.
    * 
    * @param aDate
    */
   public static Date getPreviousDay(Date aDate)
   {
      Date rVal = null;
      if (aDate != null)
      {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(aDate);
         calendar.add(Calendar.DATE, -1);
         rVal = calendar.getTime();
      }
      return rVal;
   }

   /**
    * Returns the start of the day following the specified date.
    */
   public static Date getStartOfNextDay(Date aDate)
   {
      return getStartOfDay(getNextDay(aDate));
   }
}
