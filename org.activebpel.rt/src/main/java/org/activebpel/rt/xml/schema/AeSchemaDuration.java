//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaDuration.java,v 1.4.14.1 2008/04/21 16:05:13 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activebpel.rt.util.AeUtil;


/**
 * This class represents a schema duration (xs:duration). A duration exists as a number of years, months,
 * days, hours, minutes, and seconds in the format... <br/>
 * <br/>
 * <code>PnYnMnDTnHnMnS</code><br/>
 * ...where n is some integer.
 */
public class AeSchemaDuration extends AeAbstractPatternBasedSchemaType
{
   /** Pattern that matches a duration. */
   private static Pattern INPUT_PATTERN = Pattern.compile("(-?P)([0-9]+Y)?([0-9]+M)?([0-9]+D)?(T([0-9]+H)?([0-9]+M)?([0-9]+(\\.[0-9]+)?S)?)?"); //$NON-NLS-1$
   /** The output pattern to use for toString(). */
   private static String OUTPUT_PATTERN = "{0}P{1,number,#}Y{2,number,#}M{3,number,#}DT{4,number,#}H{5,number,#}M{6,number,#}{7,choice,0#|0<.{7,number,000}}S"; //$NON-NLS-1$

   /** A flag indicating if this duration is negative. */
   private boolean mNegative;
   /** Number of years in the duration. */
   private int mYears;
   /** Number of months in the duration. */
   private int mMonths;
   /** Number of days in the duration. */
   private int mDays;
   /** Number of hours in the duration. */
   private int mHours;
   /** Number of minutes in the duration. */
   private int mMinutes;
   /** Number of seconds in the duration. */
   private int mSeconds;
   /** Number of millis in the duration. */
   private int mMilliseconds;

   /**
    * Construct a duration from the given xs:duration formatted string.
    * 
    * @param aDurationString
    */
   public AeSchemaDuration(String aDurationString)
   {
      super(aDurationString);
   }

   /**
    * Constructs a schema duration initialized to 0.
    */
   public AeSchemaDuration()
   {
   }

   /**
    * Constructs a schema duration with the given duration parameters.
    * 
    * @param aIsNegativeDuration
    * @param aYears
    * @param aMonths
    * @param aDays
    * @param aHours
    * @param aMinutes
    * @param aSeconds
    * @param aMilliseconds
    */
   public AeSchemaDuration(boolean aIsNegativeDuration, int aYears, int aMonths, int aDays, int aHours,
         int aMinutes, int aSeconds, int aMilliseconds)
   {
      setNegative(aIsNegativeDuration);
      setYears(aYears);
      setMonths(aMonths);
      setDays(aDays);
      setHours(aHours);
      setMinutes(aMinutes);
      setSeconds(aSeconds);
      setMilliseconds(aMilliseconds);
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
      String p = aMatcher.group(1);
      setNegative(p.startsWith("-")); //$NON-NLS-1$
      setYears(extractInt(aMatcher.group(2)));
      setMonths(extractInt(aMatcher.group(3)));
      setDays(extractInt(aMatcher.group(4)));
      setHours(extractInt(aMatcher.group(6)));
      setMinutes(extractInt(aMatcher.group(7)));
      setSeconds(extractSeconds(aMatcher.group(8)));
      setMilliseconds(extractMillis(aMatcher.group(9)));
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getSchemaTypeName()
    */
   protected String getSchemaTypeName()
   {
      return "xsd:duration"; //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getOutputPatternArguments()
    */
   protected Object[] getOutputPatternArguments()
   {
      return new Object [] {
            isNegative() ? "-" : "", //$NON-NLS-1$ //$NON-NLS-2$
            new Integer(getYears()),
            new Integer(getMonths()),
            new Integer(getDays()),
            new Integer(getHours()),
            new Integer(getMinutes()),
            new Integer(getSeconds()),
            new Integer(getMilliseconds())
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
    * Extracts the integer portion of a component of the duration.  
    * 
    * @param aString
    */
   protected int extractInt(String aString)
   {
      if (AeUtil.isNullOrEmpty(aString))
      {
         return 0;
      }
      else
      {
         return Integer.parseInt(aString.substring(0, aString.length() - 1));
      }
   }

   /**
    * Extracts the seconds from group 7 of the regular expression match.  This match could be simply
    * 'nS' in which case n will be returned, or it could be 'n1.n2S' in which case n1 will be returned.
    * 
    * @param aString
    */
   protected int extractSeconds(String aString)
   {
      if (AeUtil.isNullOrEmpty(aString))
      {
         return 0;
      }
      else
      {
         int idx = aString.indexOf('.');
         if (idx != -1)
         {
            return Integer.parseInt(aString.substring(0, idx));
         }
         else
         {
            return extractInt(aString);
         }
      }
   }

   /**
    * Extracts the millis information from group 7 of the regular expression match.  
    * 
    * @param aString
    */
   protected int extractMillis(String aString)
   {
      if (AeUtil.isNullOrEmpty(aString))
      {
         return 0;
      }
      else
      {
         return (int) (1000F * new Float("0" + aString).floatValue()); //$NON-NLS-1$
      }
   }

   /**
    * @return Returns the days.
    */
   public int getDays()
   {
      return mDays;
   }

   /**
    * @param aDays The days to set.
    */
   public void setDays(int aDays)
   {
      mDays = aDays;
   }

   /**
    * @return Returns the hours.
    */
   public int getHours()
   {
      return mHours;
   }

   /**
    * @param aHours The hours to set.
    */
   public void setHours(int aHours)
   {
      mHours = aHours;
   }

   /**
    * @return Returns the milliseconds.
    */
   public int getMilliseconds()
   {
      return mMilliseconds;
   }

   /**
    * @param aMilliseconds The milliseconds to set.
    */
   public void setMilliseconds(int aMilliseconds)
   {
      mMilliseconds = aMilliseconds;
   }

   /**
    * @return Returns the minutes.
    */
   public int getMinutes()
   {
      return mMinutes;
   }

   /**
    * @param aMinutes The minutes to set.
    */
   public void setMinutes(int aMinutes)
   {
      mMinutes = aMinutes;
   }

   /**
    * @return Returns the months.
    */
   public int getMonths()
   {
      return mMonths;
   }

   /**
    * @param aMonths The months to set.
    */
   public void setMonths(int aMonths)
   {
      mMonths = aMonths;
   }

   /**
    * @return Returns the seconds.
    */
   public int getSeconds()
   {
      return mSeconds;
   }

   /**
    * @param aSeconds The seconds to set.
    */
   public void setSeconds(int aSeconds)
   {
      mSeconds = aSeconds;
   }

   /**
    * @return Returns the years.
    */
   public int getYears()
   {
      return mYears;
   }

   /**
    * @param aYears The years to set.
    */
   public void setYears(int aYears)
   {
      mYears = aYears;
   }
   
   /**
    * @return Returns the negative.
    */
   public boolean isNegative()
   {
      return mNegative;
   }
   
   /**
    * @param aNegative The negative to set.
    */
   public void setNegative(boolean aNegative)
   {
      mNegative = aNegative;
   }
   
   /**
    * Returns true if the none of the duration's values are greater than zero
    */
   public boolean isZero()
   {
      return getYears() == 0 &&
             getMonths() == 0 &&
             getDays() == 0 &&
             getHours() == 0 &&
             getMinutes() == 0 &&
             getSeconds() == 0 &&
             getMilliseconds() == 0;
   }

   /**
    * Converts this duration to an absolute deadline, using the current time as the starting
    * point.
    */
   public Date toDeadline()
   {
      Calendar cal = Calendar.getInstance();
      int modifier = isNegative() ? -1 : 1;
      cal.add(Calendar.YEAR, getYears() * modifier);
      cal.add(Calendar.MONTH, getMonths() * modifier);
      cal.add(Calendar.DAY_OF_YEAR, getDays() * modifier);
      cal.add(Calendar.HOUR_OF_DAY, getHours() * modifier);
      cal.add(Calendar.MINUTE, getMinutes() * modifier);
      cal.add(Calendar.SECOND, getSeconds() * modifier);
      cal.add(Calendar.MILLISECOND, getMilliseconds() * modifier);
      return cal.getTime();
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaType#accept(org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor)
    */
   public void accept(IAeSchemaTypeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
