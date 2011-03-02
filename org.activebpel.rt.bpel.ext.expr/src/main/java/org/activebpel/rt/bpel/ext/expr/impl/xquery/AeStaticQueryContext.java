//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/xquery/AeStaticQueryContext.java,v 1.3 2006/09/27 20:00:29 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.StaticQueryContext;

import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * This class extends the Saxon static query context in order to override certain
 * functionality, such as namespace/prefix lookups.
 */
public class AeStaticQueryContext extends StaticQueryContext
{
   /** The ActiveBPEL expression namespace context to use to resolve namespaces from prefixes. */
   private IAeNamespaceContext mNamespaceContext;

   /**
    * Constructs a static query context from the saxon configuration and the generic ActiveBPEL
    * namespace context.
    * 
    * @param aConfig
    * @param aNamespaceContext
    */
   public AeStaticQueryContext(Configuration aConfig, IAeNamespaceContext aNamespaceContext)
   {
      super(aConfig);
      setNamespaceContext(aNamespaceContext);
   }

   /**
    * Overrides method to lookup the namespace from within our own namespace context before
    * delegating to Saxon.
    * 
    * @see net.sf.saxon.query.StaticQueryContext#checkURIForPrefix(java.lang.String)
    */
   public String checkURIForPrefix(String aPrefix) 
   {
      // Check the query itself for the mapping first - only check our namespace context
      // if we can't find a mapping in the query.
      String rval = super.checkURIForPrefix(aPrefix);
      if (rval == null)
      {
         rval = getNamespaceContext().resolvePrefixToNamespace(aPrefix);
      }
      return rval;
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
