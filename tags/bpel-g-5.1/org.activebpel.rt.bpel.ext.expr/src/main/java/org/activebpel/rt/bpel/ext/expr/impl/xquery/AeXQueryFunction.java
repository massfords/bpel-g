//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/xquery/AeXQueryFunction.java,v 1.4 2006/10/12 20:22:12 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import java.util.ArrayList;
import java.util.List;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionTool;
import net.sf.saxon.expr.FunctionCall;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.ValueRepresentation;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.AnyItemType;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.type.TypeHierarchy;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.Value;

import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;

/**
 * This class wraps a generic ActiveBPEL expression function into something that the Saxon XQuery processor
 * can use.
 */
public class AeXQueryFunction extends FunctionCall
{
   /** The generic function to delegate work to. */
   private IAeFunction mFunction;
   /** The expression context to use during function execution. */
   private IAeFunctionExecutionContext mFunctionExecutionContext;

   /**
    * Constructs an xquery function from a generic expression function.
    * 
    * @param aFunction
    * @param aFunctionExecutionContext
    */
   public AeXQueryFunction(IAeFunction aFunction, IAeFunctionExecutionContext aFunctionExecutionContext)
   {
      setFunction(aFunction);
      setFunctionExecutionContext(aFunctionExecutionContext);
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
   public ItemType getItemType(TypeHierarchy arg0)
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
    * Overrides method to delegate the work to the generic ActiveBPEL function.
    * 
    * @see net.sf.saxon.expr.Expression#evaluateItem(net.sf.saxon.expr.XPathContext)
    */
   public Item evaluateItem(XPathContext aContext) throws XPathException
   {
      try
      {
         List args = new ArrayList();
         for (int i = 0; i < argument.length; i++)
         {
            ValueRepresentation valRep = ExpressionTool.eagerEvaluate(argument[i], aContext);
            Item item = Value.asItem(valRep);
            Object val = getFunctionExecutionContext().getTypeConverter().convertToEngineType(item);
            args.add(val);
         }

         // Call the function and convert the result to something Saxon will like.
         Object obj = getFunction().call(getFunctionExecutionContext(), args);
         obj = getFunctionExecutionContext().getTypeConverter().convertToExpressionType(obj);
         if (obj == null)
            return null;

         // Note: if all simple types were represented internally by instances of IAeSchemaType, then this 
         // step would not be necessary.
         Value actual = Value.convertJavaObjectToXPath(obj, SequenceType.ANY_SEQUENCE, aContext.getController().getConfiguration());
         // If the 'actual' is null, then just return the String value of the raw data.
         if (actual == null)
            return new StringValue(obj.toString());

         return actual.itemAt(0);
      }
      catch (AeExpressionException ee)
      {
         throw ee;
      }
      catch (Throwable fe)
      {
         throw new XPathException(fe.getLocalizedMessage(), fe) {};
      }
   }

   /**
    * @return Returns the function.
    */
   protected IAeFunction getFunction()
   {
      return mFunction;
   }

   /**
    * @param aFunction The function to set.
    */
   protected void setFunction(IAeFunction aFunction)
   {
      mFunction = aFunction;
   }

   /**
    * @return Returns the functionExecutionContext.
    */
   protected IAeFunctionExecutionContext getFunctionExecutionContext()
   {
      return mFunctionExecutionContext;
   }

   /**
    * @param aFunctionExecutionContext The functionExecutionContext to set.
    */
   protected void setFunctionExecutionContext(IAeFunctionExecutionContext aFunctionExecutionContext)
   {
      mFunctionExecutionContext = aFunctionExecutionContext;
   }
}
