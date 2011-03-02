//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/IAeExpressionParserContext.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.expr.def;

import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * This interface defines a context used by expression parsers.  This context will provide all of the
 * objects and data necessary for parsing expressions.
 */
public interface IAeExpressionParserContext
{
   /**
    * Gets the namespace context configured for this parser context.
    */
   public IAeNamespaceContext getNamespaceContext();
}
