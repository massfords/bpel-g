// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeAbstractTZBasedSchemaType.java,v 1.1 2006/09/07 14:41:12 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml.schema;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;


/**
 * A base class for time-based schema types (types that have a Timezone component).
 */
public abstract class AeAbstractTZBasedSchemaType extends AeAbstractPatternBasedSchemaType
{
   /** A standard time zone defined for UTC. */
   protected static SimpleTimeZone sUTCTimeZone = new SimpleTimeZone(0, "UTC"); //$NON-NLS-1$


   /** The Timezone. */
   private TimeZone mTimeZone;

   /**
    * Default c'tor.
    */
   public AeAbstractTZBasedSchemaType()
   {
      super();
      setTimeZone(sUTCTimeZone);
   }
   
   /**
    * Constructs the TZ-based schema type.
    * 
    * @param aValue
    */
   public AeAbstractTZBasedSchemaType(String aValue)
   {
      super(aValue);
   }

   /**
    * Creates a <code>TimeZone</code> object from a given set of UTC offset information (hours, 
    * minutes, and direction).
    * 
    * @param aTZHours The offset hours.
    * @param aTZMinutes The offset minutes.
    * @param aTZDirection The offset direction.
    * @return A new time zone.
    */
   protected TimeZone createTimeZone(String aTZHours, String aTZMinutes, char aTZDirection)
   {
      if (aTZHours == null)
      {
         return sUTCTimeZone;
      }
      // Get number of hours in the offset
      int offset = new Integer(aTZHours).intValue();
      // Convert to minutes
      offset *= 60;
      // Add offset minutes
      offset += new Integer(aTZMinutes).intValue();
      // Convert to millis
      offset *= 60000;
      if (aTZDirection == '-')
      {
         offset *= -1;
      }
      return new SimpleTimeZone(offset, ""); //$NON-NLS-1$
   }

   /**
    * @return Returns the timeZone.
    */
   public TimeZone getTimeZone()
   {
      return mTimeZone;
   }

   /**
    * @param aTimeZone The timeZone to set.
    */
   protected void setTimeZone(TimeZone aTimeZone)
   {
      mTimeZone = aTimeZone;
   }

   /**
    * Gets the timezone offset (in minutes).
    */
   public int getTimezoneOffset()
   {
      return getTimezoneOffsetMillis() / 60000;
   }
   
   /**
    * Gets the timezone offset (in milliseconds).
    */
   public int getTimezoneOffsetMillis()
   {
      // We need to get the timezone offset, which could be different depending on what day we are
      // talking about (because of Daylight Savings Time).  The Schema spec seems to indicate that
      // Dec 31, 1972 should be used in certain circumstances - so we are using that day here to get
      // the offset.  This can be overridden by subclasses to do something more appropriate.
      return getTimeZone().getOffset(GregorianCalendar.AD, 1972, 11, 31, Calendar.SUNDAY, 0);
   }

   /**
    * Formats the current value of TimeZone in schema format.  Note that if the timezone offset is
    * found to be 0, that would indicate UTC, in which case this method will return "Z".
    * 
    * Examples:
    *   +05:00
    *   -07:30
    *   +13:00
    */
   protected String formatTimeZone()
   {
      int offsetMillis = getTimezoneOffsetMillis();
      if (offsetMillis == 0)
      {
         return "Z"; //$NON-NLS-1$
      }
      else
      {
         // Convert to minutes
         int offset = Math.abs(offsetMillis) / 60000;
         int hours = offset / 60;
         int minutes = offset % 60;
         
         Object [] args = new Object [] { (offsetMillis < 0) ? "-" : "+", //$NON-NLS-1$ //$NON-NLS-2$
               new Integer(hours), new Integer(minutes) };
         
         return MessageFormat.format("{0}{1,number,00}:{2,number,00}", args); //$NON-NLS-1$
      }
   }
}
