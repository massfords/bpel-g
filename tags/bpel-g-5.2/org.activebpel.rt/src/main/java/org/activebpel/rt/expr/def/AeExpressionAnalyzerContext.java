//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/expr/def/AeExpressionAnalyzerContext.java,v 1.1 2008/01/25 20:41:50 dvilaverde Exp $
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
 * An implementation of the expression util context.
 */
public class AeExpressionAnalyzerContext implements IAeExpressionAnalyzerContext
{
   /** The namespace context. */
   private IAeNamespaceContext mNamespaceContext;
   
   /**
    * Constructs an expression util context given the namespace context.
    * 
    * @param aNamespaceContext
    */
   public AeExpressionAnalyzerContext(IAeNamespaceContext aNamespaceContext)
   {
      setNamespaceContext(aNamespaceContext);
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext#getNamespaceContext()
    */
   public IAeNamespaceContext getNamespaceContext()
   {
      return mNamespaceContext;
   }

   /**
    * @param aNamespaceContext The namespaceContext to set.
    */
   protected void setNamespaceContext(IAeNamespaceContext aNamespaceContext)
   {
      mNamespaceContext = aNamespaceContext;
   }
}
