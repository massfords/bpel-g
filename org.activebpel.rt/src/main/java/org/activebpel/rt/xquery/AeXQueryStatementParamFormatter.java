// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xquery/AeXQueryStatementParamFormatter.java,v 1.1 2008/03/19 16:35:56 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xquery;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;

/**
 * Formats an XQuery parameter based on its data type.
 */
class AeXQueryStatementParamFormatter
{
   /** Map of java Class to IAeXQueryStatementParamFormatter. */
   private Map mFormatters = new HashMap();

   /**
    * C'tor.
    */
   public AeXQueryStatementParamFormatter()
   {
      mFormatters.put(String.class, STRING_FORMATTER);
      mFormatters.put(QName.class, QNAME_FORMATTER);
      mFormatters.put(AeSchemaDateTime.class, DATETIME_FORMATTER);
      mFormatters.put(Date.class, DATE_FORMATTER);
      mFormatters.put(Boolean.class, BOOLEAN_FORMATTER);
      mFormatters.put(AeXQueryStatementCriteria.class, CRITERIA_FORMATTER);
      mFormatters.put(Integer.class, SIMPLE_FORMATTER);
      mFormatters.put(Long.class, SIMPLE_FORMATTER);
      mFormatters.put(Float.class, SIMPLE_FORMATTER);
      mFormatters.put(Double.class, SIMPLE_FORMATTER);
      mFormatters.put(BigInteger.class, SIMPLE_FORMATTER);
   }

   /**
    * Formats the given XQuery statement parameter value.
    *
    * @param aValue
    */
   public String formatParameterValue(Object aValue)
   {
      if (aValue == null)
         throw new AssertionError(AeMessages.getString("AeXQueryStatementParamFormatter.InvalidNullParamValue")); //$NON-NLS-1$

      IAeXQueryStatementParamFormatter formatter = (IAeXQueryStatementParamFormatter) mFormatters.get(aValue.getClass());
      if (formatter == null)
         throw new AssertionError(AeMessages.format("AeXQueryStatementParamFormatter.NoFormatterError", aValue.getClass().getName())); //$NON-NLS-1$

      return formatter.format(aValue);
   }

   /**
    * All param formatters implement this interface.
    */
   private static interface IAeXQueryStatementParamFormatter
   {
      /**
       * Formats the given value - the resulting String should be in
       * a format suitable for inserting into the XQuery.
       *
       * @param aValue
       */
      public String format(Object aValue);
   }

   /**
    * Formatter for String data.
    */
   private static IAeXQueryStatementParamFormatter STRING_FORMATTER = new IAeXQueryStatementParamFormatter()
   {
      /**
       * @see org.activebpel.rt.xquery.AeXQueryStatementParamFormatter.IAeXQueryStatementParamFormatter#format(java.lang.Object)
       */
      public String format(Object aValue)
      {
         String pattern = "''{0}''"; //$NON-NLS-1$
         if (((String) aValue).contains("'")) //$NON-NLS-1$
            pattern = "\"{0}\""; //$NON-NLS-1$
         return MessageFormat.format(pattern, new Object[] { aValue });
      }
   };

   /**
    * Formatter for AeSchemaDateTime values.
    */
   private static IAeXQueryStatementParamFormatter DATETIME_FORMATTER = new IAeXQueryStatementParamFormatter()
   {
      /**
       * @see org.activebpel.rt.xquery.AeXQueryStatementParamFormatter.IAeXQueryStatementParamFormatter#format(java.lang.Object)
       */
      public String format(Object aValue)
      {
         AeSchemaDateTime dateTime = (AeSchemaDateTime) aValue;
         String pattern = "xsd:dateTime(''{0}'')"; //$NON-NLS-1$
         return MessageFormat.format(pattern, new Object[] { dateTime.toString() });
      }
   };

   /**
    * Formatter for Date values.
    */
   private static IAeXQueryStatementParamFormatter DATE_FORMATTER = new IAeXQueryStatementParamFormatter()
   {
      /**
       * @see org.activebpel.rt.xquery.AeXQueryStatementParamFormatter.IAeXQueryStatementParamFormatter#format(java.lang.Object)
       */
      public String format(Object aValue)
      {
         Date date = (Date) aValue;
         AeSchemaDateTime dateTime = new AeSchemaDateTime(date);
         String pattern = "xsd:dateTime(''{0}'')"; //$NON-NLS-1$
         return MessageFormat.format(pattern, new Object[] { dateTime.toString() });
      }
   };

   /**
    * Formatter for QName values.
    */
   private static IAeXQueryStatementParamFormatter QNAME_FORMATTER = new IAeXQueryStatementParamFormatter()
   {
      /**
       * @see org.activebpel.rt.xquery.AeXQueryStatementParamFormatter.IAeXQueryStatementParamFormatter#format(java.lang.Object)
       */
      public String format(Object aValue)
      {
         QName qname = (QName) aValue;
         String pattern = "QName(''{0}'', ''{1}'')"; //$NON-NLS-1$
         return MessageFormat.format(pattern, new Object[] { qname.getNamespaceURI(), qname.getLocalPart() });
      }
   };

   /**
    * Formatter for boolean values.
    */
   private static IAeXQueryStatementParamFormatter BOOLEAN_FORMATTER = new IAeXQueryStatementParamFormatter()
   {
      /**
       * @see org.activebpel.rt.xquery.AeXQueryStatementParamFormatter.IAeXQueryStatementParamFormatter#format(java.lang.Object)
       */
      public String format(Object aValue)
      {
         Boolean bool = (Boolean) aValue;
         String pattern = "{0}()"; //$NON-NLS-1$
         return MessageFormat.format(pattern, new Object[] { bool });
      }
   };

   /**
    * Formatter for boolean values.
    */
   private static IAeXQueryStatementParamFormatter CRITERIA_FORMATTER = new IAeXQueryStatementParamFormatter()
   {
      /**
       * @see org.activebpel.rt.xquery.AeXQueryStatementParamFormatter.IAeXQueryStatementParamFormatter#format(java.lang.Object)
       */
      public String format(Object aValue)
      {
         AeXQueryStatementCriteria criteria = (AeXQueryStatementCriteria) aValue;
         return AeUtil.getSafeString(criteria.getCriteriaString());
      }
   };
   
   /**
    * Formatter for simple types that can be formatted successfully using a simple toString().
    */
   private static IAeXQueryStatementParamFormatter SIMPLE_FORMATTER = new IAeXQueryStatementParamFormatter()
   {
      /**
       * @see org.activebpel.rt.xquery.AeXQueryStatementParamFormatter.IAeXQueryStatementParamFormatter#format(java.lang.Object)
       */
      public String format(Object aValue)
      {
         return String.valueOf(aValue);
      }
   };
}
