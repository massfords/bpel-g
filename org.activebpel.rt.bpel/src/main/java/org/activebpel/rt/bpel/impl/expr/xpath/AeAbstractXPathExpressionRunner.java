// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/expr/xpath/AeAbstractXPathExpressionRunner.java,v 1.2 2007/11/01 18:23:52 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.expr.xpath;

import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionRunner;
import org.activebpel.rt.bpel.impl.expr.AeExprFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerVariableResolver;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;
import org.jaxen.FunctionContext;
import org.jaxen.NamespaceContext;
import org.jaxen.VariableContext;

/**
 * A base class for implementations of xpath expression runners.
 */
public abstract class AeAbstractXPathExpressionRunner extends AeAbstractExpressionRunner
{
   /**
    * Default constructor.
    */
   public AeAbstractXPathExpressionRunner()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionRunner#doExecuteExpression(java.lang.String, org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext)
    */
   protected Object doExecuteExpression(String aExpression, IAeExpressionRunnerContext aContext)
         throws AeBpelException
   {
      try
      {
         IAeExpressionTypeConverter typeConverter = createExpressionTypeConverter(aContext);
         IAeFunctionExecutionContext funcExecContext = new AeExprFunctionExecutionContext(aContext, typeConverter);
         
         NamespaceContext xpathNSContext = new AeXPathNamespaceContext(aContext.getNamespaceContext());
         FunctionContext xpathFuncContext = new AeXPathFunctionContext(funcExecContext);
         VariableContext xpathVarContext = createVariableContext(funcExecContext, aContext.getVariableResolver());

         AeXPathHelper xpathHelper = AeXPathHelper.getInstance(aContext.getBpelNamespace());
         return xpathHelper.executeXPathExpression(aExpression, aContext.getEvaluationContext(),
               xpathFuncContext, xpathVarContext, xpathNSContext);
      }
      catch (AeBpelException ex)
      {
         throw ex;
      }
      catch (AeExpressionException ee)
      {
         throw ee.getWrappedException();
      }
      catch (Throwable t)
      {
         throwSubLangExecutionFault(aExpression, t, aContext);
         return null; // Will never get here - the above call will always throw.
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionRunner#doExecuteJoinConditionExpression(java.lang.String, org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext)
    */
   protected Object doExecuteJoinConditionExpression(String aExpression, IAeExpressionRunnerContext aContext) throws AeBpelException
   {
      try
      {
         IAeExpressionTypeConverter typeConverter = createExpressionTypeConverter(aContext);
         IAeFunctionExecutionContext funcExecContext = new AeExprFunctionExecutionContext(aContext, typeConverter);
         
         NamespaceContext xpathNSContext = new AeXPathNamespaceContext(aContext.getNamespaceContext());
         FunctionContext xpathFuncContext = new AeXPathFunctionContext(funcExecContext);
         VariableContext xpathVarContext = createJoinConditionVariableContext(funcExecContext);

         AeXPathHelper xpathHelper = AeXPathHelper.getInstance(aContext.getBpelNamespace());
         return xpathHelper.executeXPathExpression(aExpression, aContext.getEvaluationContext(),
               xpathFuncContext, xpathVarContext, xpathNSContext);
      }
      catch (AeBpelException ex)
      {
         throw ex;
      }
      catch (AeExpressionException ee)
      {
         throw ee.getWrappedException();
      }
      catch (Throwable t)
      {
         throwSubLangExecutionFault(aExpression, t, aContext);
         return null; // Will never get here - the above call will always throw.
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionRunner#createExpressionTypeConverter(org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext)
    */
   protected IAeExpressionTypeConverter createExpressionTypeConverter(IAeExpressionRunnerContext aContext)
   {
      AeXPathHelper xpathHelper = AeXPathHelper.getInstance(aContext.getBpelNamespace());
      return new AeXPathExpressionTypeConverter(xpathHelper);
   }

   /**
    * Base class does not supply a variable context (BPEL4WS version).
    * 
    * @param aContext
    * @param aVariableResolver
    */
   protected abstract VariableContext createVariableContext(IAeFunctionExecutionContext aContext, IAeExpressionRunnerVariableResolver aVariableResolver);

   /**
    * Base class does not supply a variable context (BPEL4WS version).
    * 
    * @param aContext
    */
   protected abstract VariableContext createJoinConditionVariableContext(IAeFunctionExecutionContext aContext);
}
