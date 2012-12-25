//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/IAeExpressionRunner.java,v 1.4 2006/11/06 23:41:25 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr;

import java.util.Date;

import org.activebpel.rt.AeException;
import org.activebpel.rt.xml.schema.AeSchemaDuration;

/**
 * This interface should be implemented in order to provide an implementation of an
 * expression runner.  An expression runner is capable of executing an expression in 
 * a particular expression language.  Examples of expression runners would be XPath
 * and XQuery expression runners.  By default, BPEL 1.0 compliant engines must be able
 * to execute XPath 1.0 expressions, so there will be at least one implementation of
 * this interface (for XPath 1.0 expressions).
 * 
 * Note that, currently, a new instance of an expression runner will be created every
 * time one is needed.  So at the moment, expression runners do not need to be thread
 * safe, since they will not be reused.
 */
public interface IAeExpressionRunner
{
   /**
    * This method is called to execute an expression.  The result of an expression will
    * be some sort of object.  The result could be a Boolean, a List, a Document, etc...
    * @param aContext The expression execution context
    * @param aExpression The expression to execute.
    * 
    * @throws AeException
    */
   public Object executeExpression(IAeExpressionRunnerContext aContext, String aExpression) throws AeException;

   /**
    * Executes a boolean expression.  A boolean expression is simply an expression evaluates to a 
    * boolean.
    * @param aContext The expression execution context
    * @param aExpression The expression to execute.
    * 
    * @throws AeException
    */
   public Boolean executeBooleanExpression(IAeExpressionRunnerContext aContext, String aExpression) throws AeException;
   
   /**
    * Executes a deadline expression.  A deadline expression is an expression that evaluates to a 
    * BPEL deadline.  A BPEL deadline is defined as a xml schema 'date' or 'dateTime' type.
    * 
    * @param aContext The expression execution context
    * @param aExpression The expression to execute.
    * @throws AeException
    */
   public Date executeDeadlineExpression(IAeExpressionRunnerContext aContext, String aExpression) throws AeException;

   /**
    * Executes a duration expression.  A duration expression is an expression that evaluates to a
    * BPEL duration.  A BPEL duration is defined as the xml schema type 'duration'.  See the xml
    * schema specification for more details on how to format a duration.
    * 
    * @param aContext The expression execution context
    * @param aExpression The expression to execute.
    * @throws AeException
    */
   public AeSchemaDuration executeDurationExpression(IAeExpressionRunnerContext aContext, String aExpression) throws AeException;

   /**
    * Executes a join condition expression.  A join condition expression is an expression 
    * that evaluates to a boolean and conforms to some restrictions that are outlined in the 
    * specification.
    * 
    * @param aContext The expression execution context
    * @param aExpression The expression to execute.
    * @throws AeException
    */
   public Boolean executeJoinConditionExpression(IAeExpressionRunnerContext aContext, String aExpression) throws AeException;
}
