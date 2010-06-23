// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr/src/org/activebpel/rt/bpel/ext/expr/impl/xquery/AeWSBPELXQueryVariableResolver.java,v 1.3 2007/11/01 18:29:44 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.ext.expr.impl.xquery;

import java.util.List;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.VariableDeclaration;
import net.sf.saxon.query.GlobalVariableDefinition;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.variables.VariableResolver;

import org.activebpel.rt.bpel.ext.expr.AeMessages;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver;

/**
 * A WS-BPEL Xquery variable resolver.  This class resolves XQuery variables (of the form $bpelVarName)
 * to XQuery expressions that, when executed, return the value of the named BPEL variable.
 */
public class AeWSBPELXQueryVariableResolver implements VariableResolver
{
   /** The function exec context. */
   private IAeFunctionExecutionContext mFunctionExecutionContext;
   /** Variable resolver. */
   private IAeExpressionRunnerVariableResolver mVariableResolver;

   /**
    * Constructor.
    *
    * @param aContext
    */
   public AeWSBPELXQueryVariableResolver(IAeFunctionExecutionContext aContext, IAeExpressionRunnerVariableResolver aVariableResolver)
   {
      setFunctionExecutionContext(aContext);
      setVariableResolver(aVariableResolver);
   }

   /**
    * @see net.sf.saxon.variables.VariableResolver#hasVariable(int, java.lang.String, java.lang.String)
    */
   public boolean hasVariable(int aNameCode, String aUri, String aLocal)
   {
      return getVariableResolver().variableExists(aLocal);
   }

   /**
    * @see net.sf.saxon.variables.VariableResolver#resolve(int, java.lang.String, java.lang.String)
    */
   public VariableDeclaration resolve(int aNameCode, String aUri, String aLocal)
   {
      // Note: don't need to pass the URI here, because we already know it is empty
      return createGlobalVariableDef(aNameCode, aLocal);
   }

   /**
    * Creates a global variable definition for the given namecode + local name.
    *
    * @param aNameCode
    * @param aVariableName
    */
   private GlobalVariableDefinition createGlobalVariableDef(int aNameCode, String aVariableName)
   {
      GlobalVariableDefinition globalVarDef = new GlobalVariableDefinition();
      globalVarDef.setNameCode(aNameCode);
      globalVarDef.setIsParameter(true);
      globalVarDef.setVariableName(aVariableName);
      globalVarDef.setRequiredType(SequenceType.SINGLE_ITEM);

      Expression expression = createVariableExpression(aVariableName);
      globalVarDef.setValueExpression(expression);

      return globalVarDef;
   }

   /**
    * Create the expression that will be executed for the variable reference.
    *
    * @param aVariableName
    */
   protected Expression createVariableExpression(String aVariableName)
   {
        IAeFunction getVarFun = new AeWSBPELXQueryVariableResolverFunctionAdapter(aVariableName);
        AeXQueryFunction xfunc = new AeXQueryFunction(getVarFun, getFunctionExecutionContext());
        xfunc.setArguments(new Expression[]{});
        return xfunc;
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

  /**
   * @return variable resolver.
   */
   protected IAeExpressionRunnerVariableResolver getVariableResolver()
   {
      return mVariableResolver;
   }

   /**
    * Sets the variable resolver.
    * @param aVariableResolver
    */
   protected void setVariableResolver(IAeExpressionRunnerVariableResolver aVariableResolver)
   {
      mVariableResolver = aVariableResolver;
   }


   /**
    * Adopts a IAeExpressionRunnerVariableResolver into a IAeFunction.
    */
   class AeWSBPELXQueryVariableResolverFunctionAdapter implements IAeFunction
   {
      /** Variable to resolve. */
      private String mVariableName;

      /**
       * ctor.
       * @param aVariableName
       */
      protected AeWSBPELXQueryVariableResolverFunctionAdapter(String aVariableName)
      {
         mVariableName = aVariableName;
      }

      /**
       *
       * Overrides method to return variable data via IAeExpressionRunnerVariableResolver.
       * @see org.activebpel.rt.bpel.function.IAeFunction#call(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, java.util.List)
       */
      public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
      {
         Object result = null;
         try
         {
            result = getVariableResolver().getVariableData(mVariableName);
         }
         catch(Exception e)
         {
            throw new AeFunctionCallException(e);
         }
         if (result != null)
         {
            return result;
         }
         else
         {
           throw new AeExpressionException( new AeBpelException(
                  AeMessages.format("AeWSBPELXQueryVariableResolver.VARIABLE_NOT_INITIALIZED_ERROR", mVariableName), getFunctionExecutionContext().getFaultFactory().getUninitializedVariable() ) ); //$NON-NLS-1$
         }
      }
   }
}
