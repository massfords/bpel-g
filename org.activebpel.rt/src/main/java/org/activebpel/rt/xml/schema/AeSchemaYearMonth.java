//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaYearMonth.java,v 1.2 2006/09/07 14:41:12 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml.schema; 

import java.util.SimpleTimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activebpel.rt.util.AeUtil;


/**
 * Schema type for calendar field gYearMonth 
 */
public class AeSchemaYearMonth extends AeAbstractTZBasedSchemaType
{
   /** A regular expression for matching schema year-month strings. */
   private static Pattern INPUT_PATTERN = Pattern.compile("(-?)([0-9]{4})-([0-9]{2})(Z|(([+-])([0-9]{2}):([0-9]{2})))?"); //$NON-NLS-1$
   /** The output pattern. */
   private static String OUTPUT_PATTERN = "{0,number,0000}-{1,number,00}{2}"; //$NON-NLS-1$

   /** The year. */
   private int mYear;
   /** The month. */
   private int mMonth;

   /**
    * Creates the schema yearMonth object.
    * 
    * @param aYear
    * @param aMonth
    * @param aTimezoneOffset The timezone offset (in minutes from UTC)
    */
   public AeSchemaYearMonth(int aYear, int aMonth, int aTimezoneOffset)
   {
      setYear(aYear);
      setMonth(aMonth);
      setTimeZone(new SimpleTimeZone(aTimezoneOffset * 60000, "")); //$NON-NLS-1$
   }
   
   /**
    * Ctor takes year/month string: CCCC-MM
    * @param aValue
    */
   public AeSchemaYearMonth(String aValue)
   {
      super(aValue);
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
      String dirStr = aMatcher.group(1);
      String yearStr = aMatcher.group(2);
      String monthStr = aMatcher.group(3);
      setYear(Integer.parseInt(yearStr));
      setMonth(Integer.parseInt(monthStr));
      if ("-".equals(dirStr)) //$NON-NLS-1$
         setYear(getYear() * -1);

      boolean isUTC = (AeUtil.isNullOrEmpty(aMatcher.group(4))) || ("Z".equals(aMatcher.group(4))); //$NON-NLS-1$
      if (!isUTC)
      {
         char tzDir = aMatcher.group(6).charAt(0);
         String tzHr = aMatcher.group(7);
         String tzMin = aMatcher.group(8);
         setTimeZone(createTimeZone(tzHr, tzMin, tzDir));
      }
      else
      {
         setTimeZone(sUTCTimeZone);
      }
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getSchemaTypeName()
    */
   protected String getSchemaTypeName()
   {
      return "xsd:gYearMonth"; //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getOutputPatternArguments()
    */
   protected Object[] getOutputPatternArguments()
   {
      return new Object[] { new Integer(getYear()), new Integer(getMonth()), formatTimeZone() };
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getOutputPattern()
    */
   protected String getOutputPattern()
   {
      return OUTPUT_PATTERN;
   }

   /**
    * @return Returns the month.
    */
   public int getMonth()
   {
      return mMonth;
   }

   /**
    * @param aMonth The month to set.
    */
   protected void setMonth(int aMonth)
   {
      mMonth = aMonth;
   }

   /**
    * @return Returns the year.
    */
   public int getYear()
   {
      return mYear;
   }

   /**
    * @param aYear The year to set.
    */
   protected void setYear(int aYear)
   {
      mYear = aYear;
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaType#accept(org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor)
    */
   public void accept(IAeSchemaTypeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
