//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeNoOpFunctionCall.java,v 1.2 2006/09/07 15:11:45 EWittmann Exp $
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
import net.sf.saxon.expr.FunctionCall;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.AnyItemType;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.type.TypeHierarchy;


/**
 * A no-op XQuery function call.  This is used for simple static validation of the XQuery syntax, as
 * well as for parsing the expression to extract the list of functions.
 */
public class AeNoOpFunctionCall extends FunctionCall
{
   /** The function's namespace. */
   private String mNamespace;
   /** The function name. */
   private String mName;
   
   /**
    * Creates a no op function call with the given namespace and name.  Arguments will be set on the
    * object later.
    * 
    * @param aNamespace
    * @param aName
    */
   public AeNoOpFunctionCall(String aNamespace, String aName)
   {
      setNamespace(aNamespace);
      setName(aName);
   }
   
   /**
    * @see net.sf.saxon.expr.FunctionCall#checkArguments(net.sf.saxon.expr.StaticContext)
    */
   protected void checkArguments(StaticContext aEnv) throws XPathException
   {
   }

   /**
    * @see net.sf.saxon.expr.ComputedExpression#computeCardinality()
    */
   protected int computeCardinality()
   {
      return StaticProperty.ALLOWS_ONE;
   }

   /**
    * @see net.sf.saxon.expr.Expression#getItemType(net.sf.saxon.type.TypeHierarchy)
    */
   public ItemType getItemType(TypeHierarchy aArg)
   {
      return AnyItemType.getInstance();
   }
   
   /**
    * Overrides method to prevent pre-evaluation.
    * 
    * @see net.sf.saxon.expr.FunctionCall#preEvaluate(net.sf.saxon.expr.StaticContext)
    */
   public Expression preEvaluate(StaticContext env) throws XPathException
   {
      return this;
   }

   /**
    * @see net.sf.saxon.expr.Expression#evaluateItem(net.sf.saxon.expr.XPathContext)
    */
   public Item evaluateItem(XPathContext aContext) throws XPathException
   {
      return null;
   }
   
   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * @param aName The name to set.
    */
   protected void setName(String aName)
   {
      mName = aName;
   }

   /**
    * @return Returns the namespace.
    */
   public String getNamespace()
   {
      return mNamespace;
   }
   
   /**
    * @param aNamespace The namespace to set.
    */
   protected void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }
}
