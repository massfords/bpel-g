//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/IAeExpressionAnalyzerContext.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
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
 * This interface is passed to the various methods of the IAevExpressionUtil interface to provide
 * contextual information for those activities.
 */
public interface IAeExpressionAnalyzerContext
{
   /**
    * Gets the namespace context.
    */
   public IAeNamespaceContext getNamespaceContext();
}
