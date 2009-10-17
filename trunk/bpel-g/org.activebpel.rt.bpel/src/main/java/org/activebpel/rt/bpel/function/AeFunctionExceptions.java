//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/AeFunctionExceptions.java,v 1.8.4.3 2008/04/21 16:09:43 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.function;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;

/**
 * Common function exceptions
 *
 * <p>TODO: (JB) leverage this for other functions</p>
 */
public class AeFunctionExceptions
{
   public static final IAeFunctionException UNRESOLVED_VARIABLE = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.UNRESOLVED_VARIABLE_ERROR", args)); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException UNSUPPORTED_LANG_URI = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.UNSUPPORTED_LANG_URI", args)); //$NON-NLS-1$
      }
   };   
   
   public static final IAeFunctionException INVALID_PARAMS = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.INCORRECT_PARAMS_TO_FUNCTION_ERROR", args)); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException EXPECT_STRING_ARGUMENT = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.EXPECT_STRING_ARGUMENT", args)); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException EXPECT_LONG_ARGUMENT = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.EXPECT_LONG_ARGUMENT", args)); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException EXPECT_VALID_ARGUMENT = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.EXPECT_VALID_ARGUMENT", args)); //$NON-NLS-1$
      }
   };   
   
   public static final IAeFunctionException EXPECT_POSITIVE_INT_ARGUMENT = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.EXPECT_POSITIVE_INT_ARGUMENT", args)); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException NULL_RESULT_ERROR = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.ERROR_EVALUATING_FUNCTION", args),new NullPointerException()); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException VARIABLE_NOT_INITIALIZED = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeExpressionException(
               new AeBpelException(
                     AeMessages.format("AeFunctionExceptions.VARIABLE_NOT_INITIALIZED_ERROR", args[0]), ((IAeFunctionExecutionContext)args[1]).getFaultFactory().getUninitializedVariable())); //$NON-NLS-1$
     
      }
   };
   
   public static final IAeFunctionException INVALID_ATTACHMENT_INDEX = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.ATTACHMENT_NUMBER_ERROR", args)); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException ATTACHMENT_INDEX_INVALID_NUMBER = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.ATTACHMENT_INDEX_INVALID_NUMBER_ERROR", args)); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException INVALID_ATTACHMENT_OBJECT_TYPE = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.INVALID_ATTACHMENT_OBJECT_TYPE", args)); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException MISSING_ATTACHMENT_MIME = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.MISSING_ATTACHMENT_MIME", args)); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException MISSING_ATTACHMENT_CONTENT = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.MISSING_ATTACHMENT_CONTENT", args)); //$NON-NLS-1$
      }
   };
   
   public static final IAeFunctionException CREATE_ATTACHMENT_FAILED = new IAeFunctionException()
   {
      public void error(Object[] args) throws AeFunctionCallException
      {
         throw new AeFunctionCallException(AeMessages.format(
               "AeFunctionExceptions.CREATE_ATTACHMENT_FAILED", args)); //$NON-NLS-1$
      }
   };
   
   public static void throwFunctionException(IAeFunctionException aError, String arg) throws AeFunctionCallException
   {
      Object[] args = { arg };
      throwFunctionException(aError, args);
   }

   public static void throwFunctionException(IAeFunctionException aError, Object[] args) throws AeFunctionCallException
   {
      aError.error(args);
   }

   protected interface IAeFunctionException
   {
      /**
       * 
       * @param args
       * @throws AeFunctionCallException
       */
      public void error(Object[] args) throws AeFunctionCallException;
   }
}
