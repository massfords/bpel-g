//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeHTMLFormatter.java,v 1.7 2007/07/26 18:17:49 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.text.CharacterIterator;
import java.text.NumberFormat;
import java.text.StringCharacterIterator;
import java.util.StringTokenizer;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;

/**
 * Utility class for massaging strings to appear in HTML
 */
public class AeHTMLFormatter
{
   /**
    * Formats an XML string into one that will render properly in HTML.  It does this
    * by replacing &lt; and &gt; characters with their equivalent HTML entities. and
    * also escapes ampersands with &amp;.
    * 
    * @param aString
    */
   public static String formatXMLString(String aString)
   {
      // check for null or empty and do a short return
      if (AeUtil.isNullOrEmpty(aString))
         return ""; //$NON-NLS-1$

      StringBuffer buff = new StringBuffer();
      synchronized (buff)
      {
         CharacterIterator i = new StringCharacterIterator(aString.trim());
         for (char ch = i.first(); ch != CharacterIterator.DONE; ch = i.next())
         {
            if (ch == '<')
            {
               buff.append("&lt;"); //$NON-NLS-1$
            }
            else if (ch == '>')
            {
               buff.append("&gt;"); //$NON-NLS-1$
            }
            else if (ch == '&')
            {
               buff.append("&amp;"); //$NON-NLS-1$
            }
            else
            {
               buff.append(ch);
            }
         }
      }
      return buff.toString();      
   }
   
   /**
    * Convert a Java multi-line string into a JavaScript compatible composite string.
    * @param aString - String to convert
    * @return converted string
    */
   public static String formatJavascriptString(String aString)
   {
      // check for null or empty and do a short return
      if (AeUtil.isNullOrEmpty(aString))
         return "''"; //$NON-NLS-1$

      // change any tokens that don't work in javascript or xml
      String str = aString.replace('\'', '\"');
      StringBuffer buff = new StringBuffer();
      StringTokenizer tokenizer = new StringTokenizer(str, "\n\r"); //$NON-NLS-1$
      while (tokenizer.hasMoreTokens())
      {
         String token = tokenizer.nextToken();
         buff.append("'"); //$NON-NLS-1$
         buff.append(formatXMLString(token));
         buff.append("\\n'"); //$NON-NLS-1$
         if (tokenizer.hasMoreTokens())
         {
            buff.append(" +\n"); //$NON-NLS-1$
         }
      }
      return buff.toString();
   }

   /**
    * Formats a number.
    *
    * @param aValue
    * @param aNumberFormat
    * @throws AeException
    */
   public static String formatNumber(Object aValue, NumberFormat aNumberFormat) throws AeException
   {
      // the property value, as a Number object. 
      Number number = null;
      if (aValue != null && (aValue instanceof Number)  )
      {
         // property value is a number
         number = (Number) aValue;
      }
      else if (aValue != null && (aValue instanceof String) && !AeUtil.isNullOrEmpty( (String) aValue) )
      {         
         String s = (String) aValue;
         // perform a simple test to if this is a number. (defect #785).
         if (!Character.isDigit( s.charAt(0) ))
         {
            // if this is not a number, then return the string.
            return s;
         }
         // the value is a non-empty string - so try and parse the number.
         try
         {
            number = aNumberFormat.parse(s);
         }
         catch(Exception e)
         {
            String err = AeMessages.format("AeHTMLFormatter.ERROR_FormatNumber", s); //$NON-NLS-1$
            throw new AeException(err);
         }
      }
      else         
      {
         // Property not found in bean or it is null or empty (for strings).
         // Return empty string to be displayed
         return "";  //$NON-NLS-1$
      }     
      
      // At this point, we have a valid Number object. Simply format it and return the string.
      if ((number instanceof Double) || (number instanceof Float))
      {
         return aNumberFormat.format(number.doubleValue());
      }
      else
      {
         return aNumberFormat.format(number.longValue());
      }
   }   
}
