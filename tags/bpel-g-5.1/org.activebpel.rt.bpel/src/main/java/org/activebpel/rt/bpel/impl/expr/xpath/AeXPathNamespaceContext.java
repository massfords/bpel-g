//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/xpath/AeXPathNamespaceContext.java,v 1.4 2006/09/27 19:58:41 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.expr.xpath;

import org.activebpel.rt.xml.IAeNamespaceContext;
import org.jaxen.NamespaceContext;

/**
 * Implements a namespace context that can be used during XPath expression execution.  It uses
 * the generic AE namespace context to do the actual translation.
 */
public class AeXPathNamespaceContext implements NamespaceContext
{
   /** The generic namespace context to use. */
   private IAeNamespaceContext mNamespaceContext;

   /**
    * Constructs an xpath namespace context from the generic namespace context.
    * 
    * @param aNamespaceContext
    */
   public AeXPathNamespaceContext(IAeNamespaceContext aNamespaceContext)
   {
      setNamespaceContext(aNamespaceContext);
   }

   /**
    * @see org.jaxen.NamespaceContext#translateNamespacePrefixToUri(java.lang.String)
    */
   public String translateNamespacePrefixToUri(String aPrefix)
   {
      return getNamespaceContext().resolvePrefixToNamespace(aPrefix);
   }

   /**
    * Getter for the namespace context.
    */
   protected IAeNamespaceContext getNamespaceContext()
   {
      return mNamespaceContext;
   }

   /**
    * Setter for the namespace context.
    * 
    * @param aContext
    */
   protected void setNamespaceContext(IAeNamespaceContext aContext)
   {
      mNamespaceContext = aContext;
   }
}
