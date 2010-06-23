//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/schema/AeSchemaDay.java,v 1.2 2006/09/07 14:41:12 ewittmann Exp $
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
 * Schema type for calendar field: gDay 
 */
public class AeSchemaDay extends AeAbstractTZBasedSchemaType
{
   /** A regular expression for matching schema day strings. */
   private static Pattern INPUT_PATTERN = Pattern.compile("---([0-9]{2})(Z|(([+-])([0-9]{2}):([0-9]{2})))?"); //$NON-NLS-1$
   /** The output pattern. */
   private static String OUTPUT_PATTERN = "---{0,number,00}{1}"; //$NON-NLS-1$

   /** The day. */
   private int mDay;

   /**
    * Creates a schema day object.
    * 
    * @param aDay
    * @param aTimezoneOffset The timezone offset (in minutes from UTC)
    */
   public AeSchemaDay(int aDay, int aTimezoneOffset)
   {
      setDay(aDay);
      setTimeZone(new SimpleTimeZone(aTimezoneOffset * 60000, "")); //$NON-NLS-1$
   }
   
   /**
    * Ctor takes string: ---DD where DD is 1-31
    * @param aValue
    */
   public AeSchemaDay(String aValue)
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
      String dayStr = aMatcher.group(1);
      setDay(Integer.parseInt(dayStr));

      boolean isUTC = (AeUtil.isNullOrEmpty(aMatcher.group(2))) || ("Z".equals(aMatcher.group(2))); //$NON-NLS-1$
      if (!isUTC)
      {
         char tzDir = aMatcher.group(4).charAt(0);
         String tzHr = aMatcher.group(5);
         String tzMin = aMatcher.group(6);
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
      return "xsd:gDay"; //$NON-NLS-1$
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getOutputPatternArguments()
    */
   protected Object[] getOutputPatternArguments()
   {
      return new Object[] { new Integer(getDay()), formatTimeZone() };
   }

   /**
    * @see org.activebpel.rt.xml.schema.AeAbstractPatternBasedSchemaType#getOutputPattern()
    */
   protected String getOutputPattern()
   {
      return OUTPUT_PATTERN;
   }

   /**
    * @return Returns the day.
    */
   public int getDay()
   {
      return mDay;
   }

   /**
    * @param aDay The day to set.
    */
   protected void setDay(int aDay)
   {
      mDay = aDay;
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaType#accept(org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor)
    */
   public void accept(IAeSchemaTypeVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
 