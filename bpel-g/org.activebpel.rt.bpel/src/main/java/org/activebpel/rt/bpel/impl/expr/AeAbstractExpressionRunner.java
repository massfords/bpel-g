//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/AeAbstractExpressionRunner.java,v 1.14 2008/02/17 21:37:09 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr;

import java.text.MessageFormat;
import java.util.Date;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeSchemaDate;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.activebpel.rt.xml.schema.AeSchemaDuration;
import org.activebpel.rt.xml.schema.AeSchemaTypeParseException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class defines some common functionality for all of our expression runners.
 */
public abstract class AeAbstractExpressionRunner implements IAeExpressionRunner
{
   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner#executeExpression(org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext, java.lang.String)
    */
   public Object executeExpression(IAeExpressionRunnerContext aContext, String aExpression)
         throws AeException
   {
      Object nativeRval = doExecuteExpression(aExpression, aContext);
      IAeExpressionTypeConverter typeConverter = createExpressionTypeConverter(aContext);
      Object convertedRval = typeConverter.convertToEngineType(nativeRval);
      return convertedRval;
   }
   
   /**
    * This method is called to do the actual execution of a join condition expression. The 
    * return value is whatever is returned from the 3rd party library being use for a specific 
    * language (saxon, rhino, etc).  This implementation simply calls the abstract doExecuteExpression
    * method, but subclasses may need to override it.
    * 
    * @param aExpression
    * @param aContext
    */
   protected Object doExecuteJoinConditionExpression(String aExpression,
         IAeExpressionRunnerContext aContext) throws AeBpelException
   {
      return doExecuteExpression(aExpression, aContext);
   }

   /**
    * This method is called to do the actual execution of the expression. The return value is whatever is
    * returned from the 3rd party library being use for a specific language (saxon, rhino, etc).
    * 
    * @param aExpression
    * @param aContext
    */
   protected abstract Object doExecuteExpression(String aExpression, IAeExpressionRunnerContext aContext)
         throws AeBpelException;

   /**
    * Returns a type converter specific to the expression runner implementation.
    */
   protected abstract IAeExpressionTypeConverter createExpressionTypeConverter(IAeExpressionRunnerContext aContext);

   /**
    * Converts a return value from <code>executeExpression</code> into a Java Boolean object.
    * This method can be overridden by language specific classes in order to be more strict
    * about the return type.
    * 
    * @param aNativeRval
    * @param aExpression
    * @param aContext
    * @throws AeException
    */
   protected Boolean doConvertToBoolean(Object aNativeRval, String aExpression,
         IAeExpressionRunnerContext aContext) throws AeException
   {
      if (aNativeRval instanceof Boolean)
      {
         return (Boolean) aNativeRval;
      }
      else if (aNativeRval instanceof String)
      {
         String str = (String) aNativeRval;
         if ("true".equalsIgnoreCase(str) || "false".equalsIgnoreCase(str)) //$NON-NLS-1$ //$NON-NLS-2$
         {
            return new Boolean("true".equalsIgnoreCase(str)); //$NON-NLS-1$
         }
      }
      else if (aNativeRval instanceof Integer)
      {
         int ival = ((Integer) aNativeRval).intValue();
         return new Boolean(ival > 0);
      }

      String msg = MessageFormat.format(
            AeMessages.getString("AeAbstractExpressionRunner.FAILED_TO_CONVERT_EXPRESSION_RVAL_ERROR"), //$NON-NLS-1$
            new Object [] { aExpression, aNativeRval } );
      throw new AeBpelException(msg, aContext.getFaultFactory().getInvalidExpressionValue(IAeFaultFactory.TYPE_BOOLEAN));
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner#executeBooleanExpression(org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext,
    *      java.lang.String)
    */
   public Boolean executeBooleanExpression(IAeExpressionRunnerContext aContext, String aExpression)
         throws AeException
   {
      Object obj = executeExpression(aContext, aExpression);
      return doConvertToBoolean(obj, aExpression, aContext);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner#executeJoinConditionExpression(org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext, java.lang.String)
    */
   public Boolean executeJoinConditionExpression(IAeExpressionRunnerContext aContext, String aExpression) throws AeException
   {
      Object nativeRval = doExecuteJoinConditionExpression(aExpression, aContext);
      IAeExpressionTypeConverter typeConverter = createExpressionTypeConverter(aContext);
      Object convertedRval = typeConverter.convertToEngineType(nativeRval);
      return doConvertToBoolean(convertedRval, aExpression, aContext);
   }

   /**
    * Converts a return value from <code>executeExpression</code> into a Java Date object.
    * This method can be overridden by language specific classes in order to be more strict
    * about the return type.
    * 
    * @param aNativeRval
    * @param aExpression
    * @param aContext
    * @throws AeException
    */
   protected Date doConvertToDeadline(Object aNativeRval, String aExpression,
         IAeExpressionRunnerContext aContext) throws AeException
   {
      if (aNativeRval instanceof Date)
         return (Date) aNativeRval;

      String deadline = doConvertToString(aNativeRval);
      try
      {
         AeSchemaDateTime dt = null;
         // Note - the deadline can be either xsd:date or xsd:dateTime according to the BPEL spec.
         if (deadline.indexOf("T") != -1) //$NON-NLS-1$
            dt = new AeSchemaDateTime(deadline);
         else
            dt = new AeSchemaDate(deadline);
         return dt.toDate();
      }
      catch (AeSchemaTypeParseException ex)
      {
         throw new AeBpelException(ex.getLocalizedMessage(), aContext.getFaultFactory().getInvalidExpressionValue(IAeFaultFactory.TYPE_DEADLINE), ex);
      }
   }
   
   /**
    * Converts a native return value to a string
    * @param aNativeRval
    */
   protected String doConvertToString(Object aNativeRval)
   {
      if (aNativeRval instanceof String)
      {
         return (String)aNativeRval;
      }
      else if (aNativeRval instanceof Element) 
      {
         return AeXmlUtil.getText((Element) aNativeRval);
      }
      else if (aNativeRval instanceof Node)
      {
         return ((Node)aNativeRval).getNodeValue();
      }
      return aNativeRval.toString();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner#executeDeadlineExpression(org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext, java.lang.String)
    */
   public Date executeDeadlineExpression(IAeExpressionRunnerContext aContext, String aExpression)
         throws AeException
   {
      Object deadlineObj = executeExpression(aContext, aExpression);
      return doConvertToDeadline(deadlineObj, aExpression, aContext);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner#executeDurationExpression(org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext, java.lang.String)
    */
   public AeSchemaDuration executeDurationExpression(IAeExpressionRunnerContext aContext, String aExpression)
         throws AeException
   {
      Object durationObj = executeExpression(aContext, aExpression);
      if (durationObj instanceof AeSchemaDuration)
         return (AeSchemaDuration) durationObj;

      String duration = doConvertToString(durationObj);
      try
      {
         return new AeSchemaDuration(duration);
      }
      catch (AeSchemaTypeParseException pe)
      {
         throw new AeBpelException(pe.getLocalizedMessage(), aContext.getFaultFactory().getInvalidExpressionValue(IAeFaultFactory.TYPE_DURATION), pe);
      }
   }
   
   /**
    * Throw a subLanguageExecutationFault when an exception is caught.
    * 
    * @throws AeBpelException
    */
   protected void throwSubLangExecutionFault(String aExpression, Throwable aThrowable,
         IAeExpressionRunnerContext aContext) throws AeBpelException
   {
      String msg = AeMessages.format("AeAbstractExpressionRunner.ExpressionFailedError", //$NON-NLS-1$
            new Object [] { aExpression, aThrowable.getLocalizedMessage() });
      IAeFault fault = aContext.getFaultFactory().getSubLanguageExecutionFault(aContext.getLanguageURI(),
            aThrowable, msg);
      throw new AeBpelException(msg, fault, aThrowable);
   }
}
