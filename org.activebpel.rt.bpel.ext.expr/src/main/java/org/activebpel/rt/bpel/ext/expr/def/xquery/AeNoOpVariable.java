// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeNoOpVariable.java,v 1.1 2006/09/07 15:11:45 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.def.xquery;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.QName;

import net.sf.saxon.Configuration;
import net.sf.saxon.expr.Container;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionTool;
import net.sf.saxon.expr.Optimizer;
import net.sf.saxon.expr.PromotionOffer;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.AnyItemType;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.type.TypeHierarchy;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.Value;

/**
 * An implementation of an Expression that is used only for parsing for validation/analysis.  This is
 * an object that will exist in the parsed XQuery AST, but is not meant for execution.
 */
public class AeNoOpVariable implements Expression
{
   /** The variable's namespace. */
   private String mNamespaceURI;
   /** The variable's name. */
   private String mName;

   /**
    * Construct the no-op variable given its qualified name.
    * 
    * @param aNamespaceURI
    * @param aName
    */
   public AeNoOpVariable(String aNamespaceURI, String aName)
   {
      setNamespaceURI(aNamespaceURI);
      setName(aName);
   }

   /**
    * @see net.sf.saxon.expr.Expression#getImplementationMethod()
    */
   public int getImplementationMethod()
   {
      return EVALUATE_METHOD;
   }

   /**
    * @see net.sf.saxon.expr.Expression#simplify(net.sf.saxon.expr.StaticContext)
    */
   public Expression simplify(StaticContext aEnv) throws XPathException
   {
      return this;
   }

   /**
    * @see net.sf.saxon.expr.Expression#typeCheck(net.sf.saxon.expr.StaticContext, net.sf.saxon.type.ItemType)
    */
   public Expression typeCheck(StaticContext aEnv, ItemType aContextItemType) throws XPathException
   {
      return this;
   }

   /**
    * @see net.sf.saxon.expr.Expression#optimize(net.sf.saxon.expr.Optimizer, net.sf.saxon.expr.StaticContext, net.sf.saxon.type.ItemType)
    */
   public Expression optimize(Optimizer aOpt, StaticContext aEnv, ItemType aContextItemType) throws XPathException
   {
      return this;
   }

   /**
    * @see net.sf.saxon.expr.Expression#promote(net.sf.saxon.expr.PromotionOffer)
    */
   public Expression promote(PromotionOffer aOffer) throws XPathException
   {
      return this;
   }

   /**
    * @see net.sf.saxon.expr.Expression#getSpecialProperties()
    */
   public int getSpecialProperties()
   {
      return 0;
   }

   /**
    * @see net.sf.saxon.expr.Expression#getCardinality()
    */
   public int getCardinality()
   {
      return StaticProperty.ALLOWS_ONE;
   }

   /**
    * @see net.sf.saxon.expr.Expression#getItemType(net.sf.saxon.type.TypeHierarchy)
    */
   public ItemType getItemType(TypeHierarchy aTh)
   {
      return AnyItemType.getInstance();
   }

   /**
    * @see net.sf.saxon.expr.Expression#getDependencies()
    */
   public int getDependencies()
   {
      return 0;
   }

   /**
    * @see net.sf.saxon.expr.Expression#iterateSubExpressions()
    */
   public Iterator iterateSubExpressions()
   {
      return Collections.EMPTY_LIST.iterator();
   }

   /**
    * @see net.sf.saxon.expr.Expression#getParentExpression()
    */
   public Container getParentExpression()
   {
      return null;
   }

   /**
    * @see net.sf.saxon.expr.Expression#evaluateItem(net.sf.saxon.expr.XPathContext)
    */
   public Item evaluateItem(XPathContext aContext) throws XPathException
   {
      Value actual = Value.convertJavaObjectToXPath(getQName(), SequenceType.ANY_SEQUENCE, aContext.getController().getConfiguration());
      return actual.itemAt(0);
   }

   /**
    * @see net.sf.saxon.expr.Expression#iterate(net.sf.saxon.expr.XPathContext)
    */
   public SequenceIterator iterate(XPathContext aContext) throws XPathException
   {
      return null;
   }

   /**
    * @see net.sf.saxon.expr.Expression#effectiveBooleanValue(net.sf.saxon.expr.XPathContext)
    */
   public boolean effectiveBooleanValue(XPathContext aContext) throws XPathException
   {
      return true;
   }

   /**
    * @see net.sf.saxon.expr.Expression#evaluateAsString(net.sf.saxon.expr.XPathContext)
    */
   public String evaluateAsString(XPathContext aContext) throws XPathException
   {
      return ""; //$NON-NLS-1$
   }

   /**
    * @see net.sf.saxon.expr.Expression#process(net.sf.saxon.expr.XPathContext)
    */
   public void process(XPathContext aContext) throws XPathException
   {
   }

   /**
    * @see net.sf.saxon.expr.Expression#display(int, java.io.PrintStream, net.sf.saxon.Configuration)
    */
   public void display(int aLevel, PrintStream aOut, Configuration aConfig)
   {
      aOut.println(ExpressionTool.indent(aLevel) + "variable(" + getQName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * @see net.sf.saxon.expr.Expression#checkPermittedContents(net.sf.saxon.type.SchemaType, net.sf.saxon.expr.StaticContext, boolean)
    */
   public void checkPermittedContents(SchemaType aParentType, StaticContext aEnv, boolean aWhole) throws XPathException
   {
   }

   /**
    * @return Returns the namespaceURI.
    */
   public String getNamespaceURI()
   {
      return mNamespaceURI;
   }

   /**
    * @param aNamespaceURI The namespaceURI to set.
    */
   protected void setNamespaceURI(String aNamespaceURI)
   {
      mNamespaceURI = aNamespaceURI;
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
    * Convenience for getting the variable's qualified name.
    */
   public QName getQName()
   {
      return new QName(getNamespaceURI(), getName());
   }
}
