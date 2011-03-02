//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/IAeExpressionParser.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.def;

import org.activebpel.rt.AeException;


/**
 * This interface must be implemented by expression parsers.  There will be an implementation of this
 * interface for each expression language supported.
 */
public interface IAeExpressionParser
{
   /**
    * This method is called to parse a given expression into its parse result object.
    * 
    * @param aExpression
    * @throws AeException
    */
   public IAeExpressionParseResult parse(String aExpression) throws AeException;
}
