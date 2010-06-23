//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeXQueryStaticFunctionLibrary.java,v 1.3 2006/09/27 20:00:29 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.def.xquery;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.functions.FunctionLibrary;
import net.sf.saxon.trans.XPathException;

import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * Implements a function library for use during XQuery syntax validation.  This function
 * library does not contain any real functions - it will return a simple no-op function
 * for all function lookups.
 */
public class AeXQueryStaticFunctionLibrary implements FunctionLibrary
{
   /** A namespace context. */
   private IAeNamespaceContext mNamespaceContext;

   /**
    * Constructor.
    * 
    * @param aNamespaceContext
    */
   public AeXQueryStaticFunctionLibrary(IAeNamespaceContext aNamespaceContext)
   {
      setNamespaceContext(aNamespaceContext);
   }

   /**
    * @see net.sf.saxon.functions.FunctionLibrary#isAvailable(int, java.lang.String, java.lang.String, int)
    */
   public boolean isAvailable(int aFingerprint, String aUri, String aLocal, int arity)
   {
      return true;
   }

   /**
    * @see net.sf.saxon.functions.FunctionLibrary#bind(int, java.lang.String, java.lang.String, net.sf.saxon.expr.Expression[])
    */
   public Expression bind(int aNameCode, String aUri, String aLocal, Expression[] aStaticArgs)
         throws XPathException
   {
      AeNoOpFunctionCall func = new AeNoOpFunctionCall(aUri, aLocal);
      func.setArguments(aStaticArgs);
      return func;
   }

   /**
    * @see net.sf.saxon.functions.FunctionLibrary#copy()
    */
   public FunctionLibrary copy()
   {
      return new AeXQueryStaticFunctionLibrary(getNamespaceContext());
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
