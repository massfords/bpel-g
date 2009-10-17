//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeXQueryStaticQueryContext.java,v 1.2 2006/09/27 20:00:29 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.def.xquery;

import net.sf.saxon.Configuration;
import net.sf.saxon.query.StaticQueryContext;

import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * A simple class used during static XQuery expression syntax validation. This class is necessary to resolve
 * unknown prefixes to their corresponding namespaces. For static syntax validation, it is not important what
 * the prefix maps to, as long as it maps to something. The resulting resolved URI will then be passed into
 * the function library to bind a prefixed function to a function impl. During static syntax validation, we
 * install a function library that always returns a no-op XQuery function. Therefore, the
 */
public class AeXQueryStaticQueryContext extends StaticQueryContext
{
   /** A namespace context to use for resolving namespaces. */
   private IAeNamespaceContext mNamespaceContext;

   /**
    * Simple constructor for the custom static query context.
    * @param aConfig
    */
   public AeXQueryStaticQueryContext(Configuration aConfig, IAeNamespaceContext aNamespaceContext)
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
      String rval = getNamespaceContext().resolvePrefixToNamespace(aPrefix);
      if (AeUtil.isNullOrEmpty(rval))
      {
         rval = super.checkURIForPrefix(aPrefix);
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

