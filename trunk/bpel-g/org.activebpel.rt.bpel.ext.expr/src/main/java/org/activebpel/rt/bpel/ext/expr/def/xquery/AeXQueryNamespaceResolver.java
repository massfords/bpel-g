// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeXQueryNamespaceResolver.java,v 1.2 2006/09/27 20:00:29 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.xquery;

import java.util.Collections;
import java.util.Iterator;

import net.sf.saxon.om.NamespaceResolver;

import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * An implementation of a Saxon NamespaceResolver.
 */
public class AeXQueryNamespaceResolver implements NamespaceResolver
{
   /** The namespace context. */
   private IAeNamespaceContext mNamespaceContext;

   /**
    * Constructor given a NS context.
    * 
    * @param aNamespaceContext
    */
   public AeXQueryNamespaceResolver(IAeNamespaceContext aNamespaceContext)
   {
      setNamespaceContext(aNamespaceContext);
   }
   
   /**
    * @see net.sf.saxon.om.NamespaceResolver#getURIForPrefix(java.lang.String, boolean)
    */
   public String getURIForPrefix(String aPrefix, boolean aUseDefault)
   {
      return getNamespaceContext().resolvePrefixToNamespace(aPrefix);
   }

   /**
    * @see net.sf.saxon.om.NamespaceResolver#iteratePrefixes()
    */
   public Iterator iteratePrefixes()
   {
      // There is no equivalent of this functinality in IAeNamespaceContext, so just return an 
      // empty iterator.
      return Collections.EMPTY_LIST.iterator();
   }

   /**
    * @return Returns the namespaceContext.
    */
   protected IAeNamespaceContext getNamespaceContext()
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
