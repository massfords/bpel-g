//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr.bsf/src/org/activebpel/rt/bpel/ext/expr/bsf/impl/AeBSFExpressionRunner.java,v 1.5 2006/11/01 17:04:24 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.bsf.impl;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionRunner;
import org.activebpel.rt.bpel.impl.expr.AeExprFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter;
import org.apache.bsf.BSFManager;

/**
 * Implements an expression runner that can execute expression written in multiple languages.  The
 * actual work is done by the BSF (Bean Scripting Framework).  Any language that can be run using the
 * BSF should work with this class.  Specific language implementations may leverage this class by
 * simply extending it and providing an implementation of <code>getEngineType</code> and (optionally)
 * <code>getTypeConverter</code>.
 */
public abstract class AeBSFExpressionRunner extends AeAbstractExpressionRunner
{
   /**
    * Default constructor.
    */
   public AeBSFExpressionRunner()
   {
   }

   /**
    * Must be implemented by subclasses.  This returns the BSF engine type, such as "jython" or "javascript".
    * 
    * @return Returns the engineType.
    */
   protected abstract String getEngineType();

   /**
    * @see org.activebpel.rt.bpel.impl.expr.AeAbstractExpressionRunner#doExecuteExpression(java.lang.String, org.activebpel.rt.bpel.impl.expr.IAeExpressionRunnerContext)
    */
   protected Object doExecuteExpression(String aExpression, IAeExpressionRunnerContext aContext)
         throws AeBpelException
   {
      try
      {
         BSFManager manager = new BSFManager();
         IAeExpressionTypeConverter typeConverter = createExpressionTypeConverter(aContext);
         IAeFunctionExecutionContext funcExecContext = new AeExprFunctionExecutionContext(aContext, typeConverter);

         AeBSFGenericExtensionFunctionBean bean1 = new AeBSFGenericExtensionFunctionBean(funcExecContext);
         manager.declareBean("bpel", bean1, AeBSFGenericExtensionFunctionBean.class); //$NON-NLS-1$
         AeBSFBpelExtensionFunctionBean bean2 = new AeBSFBpelExtensionFunctionBean(funcExecContext, IAeBPELConstants.BPWS_NAMESPACE_URI);
         manager.declareBean("bpws", bean2, AeBSFBpelExtensionFunctionBean.class); //$NON-NLS-1$
         AeBSFAbxExtensionFunctionBean bean3 = new AeBSFAbxExtensionFunctionBean(funcExecContext);
         manager.declareBean("abx", bean3, AeBSFAbxExtensionFunctionBean.class); //$NON-NLS-1$

         return manager.eval(getEngineType(), "(bpel)", 1, 1, aExpression); //$NON-NLS-1$
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
}
