//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/function/IAeFunctionExecutionContext.java,v 1.8 2008/02/15 17:42:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.function;

import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.IAeFaultFactory;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter;
import org.activebpel.rt.xml.IAeNamespaceContext;

/**
 * This context is passed to a function when it is executed.
 */
public interface IAeFunctionExecutionContext
{
   /**
    * Returns the namespace context to use when executing an expression.
    */
   public IAeNamespaceContext getNamespaceContext();

   /**
    * Returns the function factory to use when executing an expression.
    */
   public IAeFunctionFactory getFunctionFactory();

   /**
    * Returns the object to use as the evaluation context.
    */
   public Object getEvaluationContext();

   /**
    * Returns the abstract bpel object that will be used during expression execution.
    */
   // TODO (EPW) see where this is used and replace with an interface
   public AeAbstractBpelObject getAbstractBpelObject();

   /**
    * Provides getter for the fault factory
    */
   public IAeFaultFactory getFaultFactory();

   /**
    * Returns the namespace of the BPEL process that this expression is contained within.
    */
   public String getBpelNamespace();

   /**
    * Returns the expression language-specific type converter.
    */
   public IAeExpressionTypeConverter getTypeConverter();
}
