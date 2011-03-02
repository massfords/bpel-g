//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/def/xquery/AeAbstractXQueryParseResult.java,v 1.2 2008/01/25 21:28:25 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.def.xquery;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.saxon.Configuration;
import net.sf.saxon.expr.Binding;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.FunctionCall;
import net.sf.saxon.expr.VariableReference;
import net.sf.saxon.instruct.GlobalParam;
import net.sf.saxon.value.DecimalValue;
import net.sf.saxon.value.IntegerValue;
import net.sf.saxon.value.StringValue;

import org.activebpel.rt.bpel.def.expr.AeAbstractExpressionParseResult;
import org.activebpel.rt.expr.def.AeScriptFuncDef;
import org.activebpel.rt.expr.def.AeScriptVarDef;
import org.activebpel.rt.expr.def.IAeExpressionParserContext;

/**
 * A base implementation of a parse result for the xquery language.
 */
public abstract class AeAbstractXQueryParseResult extends AeAbstractExpressionParseResult
{
   /** The parsed xquery expression. */
   private Expression mXQueryExpression;
   /** The saxon Configuration object. */
   private Configuration mConfiguration;
   /** The cached set of functions. */
   private Set mFunctions;
   /** The cached set of variables. */
   private Set mVariables;

   /**
    * Creates the xquery parse result.
    * 
    * @param aExpression
    * @param aXQueryExpression
    * @param aConfiguration
    * @param aParserContext
    */
   public AeAbstractXQueryParseResult(String aExpression, Expression aXQueryExpression, Configuration aConfiguration,
         IAeExpressionParserContext aParserContext)
   {
      super(aExpression, aParserContext);
      setXQueryExpression(aXQueryExpression);
      setConfiguration(aConfiguration);
   }

   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionParseResult#getFunctions()
    */
   public Set getFunctions()
   {
      if (mFunctions == null)
         mFunctions = extractFunctions(getXQueryExpression(), getConfiguration(), null);

      return mFunctions;
   }
   
   /**
    * @see org.activebpel.rt.expr.def.IAeExpressionParseResult#getVariableReferences()
    */
   public Set getVariableReferences()
   {
      if (mVariables == null)
         mVariables = extractVariables(getXQueryExpression(), getConfiguration());

      return mVariables;
   }

   /**
    * Extracts the function declarations from the given Expression object.  Returns a list
    * of AeScriptFuncDef objects.
    * 
    * @param aExpression
    * @param aConfig
    * @param aParentFunc
    */
   protected Set extractFunctions(Expression aExpression, Configuration aConfig, AeScriptFuncDef aParentFunc)
   {
      Set set = new LinkedHashSet();
      AeScriptFuncDef parentFunc = aParentFunc;
      
      // If the expression is a function call, then extract it and add it to the list.
      if (aExpression instanceof FunctionCall)
      {
         FunctionCall fc = (FunctionCall) aExpression;
         AeScriptFuncDef funcDef = null;
         if (fc instanceof AeNoOpFunctionCall)
         {
            AeNoOpFunctionCall noopFunc = (AeNoOpFunctionCall) fc;
            funcDef = new AeScriptFuncDef(noopFunc.getNamespace(), noopFunc.getName());
            extractArgsIntoFunction(aExpression, funcDef);
         }
         else
         {
            funcDef = new AeScriptFuncDef(null, fc.getDisplayName(aConfig.getNamePool()));
            extractArgsIntoFunction(aExpression, funcDef);
         }
         funcDef.setParent(parentFunc);
         set.add(funcDef);
         parentFunc = funcDef;
      }

      // Now process all of the children.
      for (Iterator iter = aExpression.iterateSubExpressions(); iter.hasNext(); )
      {
         Expression exp = (Expression) iter.next();
         set.addAll(extractFunctions(exp, aConfig, parentFunc));
      }
      return set;
   }
   
   /**
    * Extracts the variable references from the given Expression object.  Returns a list
    * of AeScriptVarDef objects.
    * 
    * @param aExpression
    * @param aConfig
    */
   protected Set extractVariables(Expression aExpression, Configuration aConfig)
   {
      Set set = new LinkedHashSet();
      
      // If the expression is a variable reference, then extract it and add it to the list.
      if (aExpression instanceof VariableReference)
      {
         VariableReference varRef = (VariableReference) aExpression;
         // Extracts the bpel variable from the var reference.  Note: this will return null
         // if the variable is not a bpel variable (but is instead a FLOWR variable or a global
         // variable).
         AeNoOpVariable var = getBpelVariable(varRef);
         if (var != null)
         {
            // Note that param two is null because we cannot reliably reconstruct the 'query' 
            // portion of the variable reference the way we can in XPath (due to the nature of
            // the Saxon AST).
            set.add(new AeScriptVarDef(var.getQName(), null));
         }
      }

      // Now process all of the children.
      for (Iterator iter = aExpression.iterateSubExpressions(); iter.hasNext(); )
      {
         Expression exp = (Expression) iter.next();
         set.addAll(extractVariables(exp, aConfig));
      }

      return set;
   }
   
   /**
    * Gets the BPEL variable from the variable reference.  If this variable reference does not
    * reference a BPEL variable (if instead it references a FLOWR variable or a global variable)
    * then this method will return null.
    * 
    * @param aVarReference
    */
   protected AeNoOpVariable getBpelVariable(VariableReference aVarReference)
   {
      Binding binding = aVarReference.getBinding();
      if (binding instanceof GlobalParam)
      {
         Expression varExpression = ((GlobalParam) binding).getSelectExpression();
         if (varExpression instanceof AeNoOpVariable)
         {
            return (AeNoOpVariable) varExpression;
         }
      }
      return null;
   }
   
   /**
    * This method extracts the arguments from the given expression and adds them as 
    * arguments to the function def object.  For non-literal arguments this method
    * simply adds the __EXPRESSION__ token as the argument.
    * 
    * @param aExpression
    * @param aFuncDef
    */
   protected void extractArgsIntoFunction(Expression aExpression, AeScriptFuncDef aFuncDef)
   {
      for (Iterator iter = aExpression.iterateSubExpressions(); iter.hasNext(); )
      {
         Expression exp = (Expression) iter.next();
         if (exp instanceof StringValue)
         {
            aFuncDef.getArgs().add(((StringValue) exp).getStringValue());
         }
         else if (exp instanceof IntegerValue)
         {
            aFuncDef.getArgs().add(new Integer((int)((IntegerValue) exp).getDoubleValue()));
         }
         else if (exp instanceof DecimalValue)
         {
            aFuncDef.getArgs().add(new Double(((DecimalValue) exp).getDoubleValue()));
         }
         else
         {
            aFuncDef.getArgs().add(AeScriptFuncDef.__EXPRESSION__);
         }
      }
   }

   /**
    * @return Returns the xQueryExpression.
    */
   public Expression getXQueryExpression()
   {
      return mXQueryExpression;
   }
   
   /**
    * @param aQueryExpression The xQueryExpression to set.
    */
   protected void setXQueryExpression(Expression aQueryExpression)
   {
      mXQueryExpression = aQueryExpression;
   }
   
   /**
    * @return Returns the configuration.
    */
   protected Configuration getConfiguration()
   {
      return mConfiguration;
   }
   
   /**
    * @param aConfiguration The configuration to set.
    */
   protected void setConfiguration(Configuration aConfiguration)
   {
      mConfiguration = aConfiguration;
   }
}
