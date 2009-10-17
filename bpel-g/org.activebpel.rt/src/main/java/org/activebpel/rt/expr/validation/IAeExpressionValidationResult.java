//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/validation/IAeExpressionValidationResult.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.validation;

import java.util.List;

import org.activebpel.rt.expr.def.IAeExpressionParseResult;

/**
 * An instance of this interface is returned by expression validators.  This result
 * includes a list of error messages, if any, as well as a list of <code>AeVariableData</code>
 * and <code>AeVariableProperty</code> objects used by the expression.
 */
public interface IAeExpressionValidationResult
{
   /**
    * Returns list of information (INFO) messages found during parsing. This method should never return
    * null.  In cases where there are no errors, an empty list should be returned.
    */
   public List getInfoList();
   
   /**
    * Returns the list of errors found during parsing.  This method should never return
    * null.  In cases where there are no errors, an empty list should be returned.
    */
   public List getErrors();

   /**
    * Returns the list of warnings found during parsing.This method should never return
    * null.  In cases where there are no warnings, an empty list should be returned.
    */
   public List getWarnings();

   /**
    * Gets the result from parsing the expression.
    */
   public IAeExpressionParseResult getParseResult();
}
