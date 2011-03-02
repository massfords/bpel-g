//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/validation/AeExpressionValidationResult.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.validation;

import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.expr.def.IAeExpressionParseResult;

/**
 * Implements a simple expression validation result object. This implementation of the
 * <code>IAeExpressionValidationResult</code> interface is basically just a container for the various lists
 * required by the interface.
 */
public class AeExpressionValidationResult implements IAeExpressionValidationResult
{
   /** List of info messages. */
   private List mInfoList;
   /** The list of errors. */
   private List mErrors;
   /** The list of warnings. */
   private List mWarnings;
   /** Results of the parsing, will be null if the expression wasn't parsed */
   private IAeExpressionParseResult mParseResult;

   /**
    * Default constructor.
    */
   public AeExpressionValidationResult()
   {
      setInfoList(new LinkedList());
      setErrors(new LinkedList());
      setWarnings(new LinkedList());
   }
   
   /**
    * @return Returns the infoList.
    */
   public List getInfoList()
   {
      return mInfoList;
   }

   /**
    * @param aInfoList The infoList to set.
    */
   public void setInfoList(List aInfoList)
   {
      mInfoList = aInfoList;
   }

   /**
    * @see org.activebpel.rt.expr.validation.IAeExpressionValidationResult#getErrors()
    */
   public List getErrors()
   {
      return mErrors;
   }

   /**
    * Setter for the errors.
    * @param aErrors
    */
   protected void setErrors(List aErrors)
   {
      mErrors = aErrors;
   }

   /**
    * Adds an info to the info list.
    * @param aInfo
    */
   public void addInfo(String aInfo)
   {
      getInfoList().add(aInfo);
   }

   /**
    * Adds a list of info messages to the internal info list.
    * @param aInfoList
    */
   public void addInfo(List aInfoList)
   {
      getInfoList().addAll(aInfoList);
   }   
   
   /**
    * Adds an error to the list of errors.
    * @param aError
    */
   public void addError(String aError)
   {
      getErrors().add(aError);
   }

   /**
    * Adds a list of errors to the internal error list.
    * @param aErrors
    */
   public void addErrors(List aErrors)
   {
      getErrors().addAll(aErrors);
   }

   /**
    * Adds a warning to the list of errors.
    * @param aWarning
    */
   public void addWarning(String aWarning)
   {
      getWarnings().add(aWarning);
   }

   /**
    * Gets the list of warnings.
    */
   public List getWarnings()
   {
      return mWarnings;
   }

   /**
    * Sets the list of warnings.
    * 
    * @param aWarnings
    */
   protected void setWarnings(List aWarnings)
   {
      mWarnings = aWarnings;
   }

   /**
    * @see org.activebpel.rt.expr.validation.IAeExpressionValidationResult#getParseResult()
    */
   public IAeExpressionParseResult getParseResult()
   {
      return mParseResult;
   }
   
   /**
    * Setter for the parse result
    * @param aResult
    */
   public void setParseResult(IAeExpressionParseResult aResult)
   {
      mParseResult = aResult;
   }

}
