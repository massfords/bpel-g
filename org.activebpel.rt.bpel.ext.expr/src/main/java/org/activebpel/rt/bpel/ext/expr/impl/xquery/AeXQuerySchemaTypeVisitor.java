// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/xquery/AeXQuerySchemaTypeVisitor.java,v 1.1 2006/09/07 15:11:45 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.AnyURIValue;
import net.sf.saxon.value.Base64BinaryValue;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.DateValue;
import net.sf.saxon.value.DurationValue;
import net.sf.saxon.value.GDayValue;
import net.sf.saxon.value.GMonthDayValue;
import net.sf.saxon.value.GMonthValue;
import net.sf.saxon.value.GYearMonthValue;
import net.sf.saxon.value.GYearValue;
import net.sf.saxon.value.HexBinaryValue;
import net.sf.saxon.value.TimeValue;
import net.sf.saxon.value.Value;

import org.activebpel.rt.xml.schema.AeSchemaAnyURI;
import org.activebpel.rt.xml.schema.AeSchemaBase64Binary;
import org.activebpel.rt.xml.schema.AeSchemaDate;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.activebpel.rt.xml.schema.AeSchemaDay;
import org.activebpel.rt.xml.schema.AeSchemaDuration;
import org.activebpel.rt.xml.schema.AeSchemaHexBinary;
import org.activebpel.rt.xml.schema.AeSchemaMonth;
import org.activebpel.rt.xml.schema.AeSchemaMonthDay;
import org.activebpel.rt.xml.schema.AeSchemaTime;
import org.activebpel.rt.xml.schema.AeSchemaYear;
import org.activebpel.rt.xml.schema.AeSchemaYearMonth;
import org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor;

/**
 * A schema type visitor that will convert a schema type object to a Saxon Value.
 */
public class AeXQuerySchemaTypeVisitor implements IAeSchemaTypeVisitor
{
   /** The Saxon Value. */
   private Value mExpressionValue;

   /**
    * Default c'tor.
    */
   public AeXQuerySchemaTypeVisitor()
   {
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaAnyURI)
    */
   public void visit(AeSchemaAnyURI aSchemaType)
   {
      Value value = new AnyURIValue(aSchemaType.getURI());
      setExpressionValue(value);
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaDate)
    */
   public void visit(AeSchemaDate aSchemaType)
   {
      Value value = new DateValue(aSchemaType.getCalendar(), aSchemaType.getTimezoneOffset());
      setExpressionValue(value);
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaDateTime)
    */
   public void visit(AeSchemaDateTime aSchemaType)
   {
      Value value = new DateTimeValue(aSchemaType.getCalendar(), true);
      setExpressionValue(value);
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaDay)
    */
   public void visit(AeSchemaDay aSchemaType)
   {
      Value value = new GDayValue((byte) aSchemaType.getDay(), aSchemaType.getTimezoneOffset());
      setExpressionValue(value);
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaDuration)
    */
   public void visit(AeSchemaDuration aSchemaType)
   {
      Value value = new DurationValue(!aSchemaType.isNegative(), aSchemaType.getYears(), 
            aSchemaType.getMonths(), aSchemaType.getDays(), aSchemaType.getHours(), aSchemaType.getMinutes(),
            aSchemaType.getSeconds(), aSchemaType.getMilliseconds() * 1000);
      setExpressionValue(value);
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaMonth)
    */
   public void visit(AeSchemaMonth aSchemaType)
   {
      Value value = new GMonthValue((byte) aSchemaType.getMonth(), aSchemaType.getTimezoneOffset());
      setExpressionValue(value);
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaMonthDay)
    */
   public void visit(AeSchemaMonthDay aSchemaType)
   {
      Value value = new GMonthDayValue((byte) aSchemaType.getMonth(), (byte) aSchemaType.getDay(), aSchemaType.getTimezoneOffset());
      setExpressionValue(value);
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaTime)
    */
   public void visit(AeSchemaTime aSchemaType)
   {
      Value value = new TimeValue((byte) aSchemaType.getHour(), (byte) aSchemaType.getMinute(),
            (byte) aSchemaType.getSecond(), aSchemaType.getMillisecond() * 1000, aSchemaType.getTimezoneOffset());
      setExpressionValue(value);
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaYear)
    */
   public void visit(AeSchemaYear aSchemaType)
   {
      Value value = new GYearValue(aSchemaType.getYear(), aSchemaType.getTimezoneOffset());
      setExpressionValue(value);
   }

   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaYearMonth)
    */
   public void visit(AeSchemaYearMonth aSchemaType)
   {
      Value value = new GYearMonthValue(aSchemaType.getYear(), (byte) aSchemaType.getMonth(), aSchemaType.getTimezoneOffset());
      setExpressionValue(value);
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaBase64Binary)
    */
   public void visit(AeSchemaBase64Binary aSchemaType)
   {
      try
      {
         Value value = new Base64BinaryValue(aSchemaType.toString());
         setExpressionValue(value);
      }
      catch (XPathException ex)
      {
         // If we have a valid AeSchemaBase64Binary instance, then it shouldn't be a problem
         // converting it to the Saxon object.  But throw a runtime exception just in case.
         throw new RuntimeException(ex);
      }
   }
   
   /**
    * @see org.activebpel.rt.xml.schema.IAeSchemaTypeVisitor#visit(org.activebpel.rt.xml.schema.AeSchemaHexBinary)
    */
   public void visit(AeSchemaHexBinary aSchemaType)
   {
      try
      {
         Value value = new HexBinaryValue(aSchemaType.toString());
         setExpressionValue(value);
      }
      catch (XPathException ex)
      {
         // If we have a valid AeSchemaHexBinary instance, then it shouldn't be a problem
         // converting it to the Saxon object.  But throw a runtime exception just in case.
         throw new RuntimeException(ex);
      }
   }

   /**
    * @return Returns the expressionValue.
    */
   public Value getExpressionValue()
   {
      return mExpressionValue;
   }

   /**
    * @param aExpressionValue The expressionValue to set.
    */
   protected void setExpressionValue(Value aExpressionValue)
   {
      mExpressionValue = aExpressionValue;
   }

}
