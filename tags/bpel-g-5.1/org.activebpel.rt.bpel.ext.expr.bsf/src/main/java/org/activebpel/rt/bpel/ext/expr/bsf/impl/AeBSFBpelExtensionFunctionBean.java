//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.ext.expr.bsf/src/org/activebpel/rt/bpel/ext/expr/bsf/impl/AeBSFBpelExtensionFunctionBean.java,v 1.5 2006/09/27 20:01:33 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.ext.expr.bsf.impl;

import java.util.ArrayList;
import java.util.List;

import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;

/**
 * This class is used to provide convenient access to the BPEL extension functions.  It
 * is simply a bean class that exposes the BPEL extension functions as methods.
 */
public class AeBSFBpelExtensionFunctionBean extends AeBSFAbstractExtensionFunctionBean
{
   /** The namespace of this function bean. */
   private String mNamespace;
   
   /**
    * Constructs the bean with the given function context.
    * 
    * @param aFunctionExecutionContext
    * @param aNamespace
    */
   public AeBSFBpelExtensionFunctionBean(IAeFunctionExecutionContext aFunctionExecutionContext, String aNamespace)
   {
      super(aFunctionExecutionContext);
      setNamespace(aNamespace);
   }

   /**
    * @see org.activebpel.rt.bpel.ext.expr.bsf.impl.AeBSFAbstractExtensionFunctionBean#getNamespace()
    */
   protected String getNamespace()
   {
      return mNamespace;
   }

   /**
    * A 1 arg version of bpws:getVariableData().
    * 
    * @param aArg1
    */
   public Object getVariableData(Object aArg1)
   {
      return getVariableData(aArg1, null, null);
   }

   /**
    * A 2 arg version of bpws:getVariableData().
    * 
    * @param aArg1
    * @param aArg2
    */
   public Object getVariableData(Object aArg1, Object aArg2)
   {
      return getVariableData(aArg1, aArg2, null);
   }

   /**
    * A 3 arg version of bpws:getVariableData().
    * 
    * @param aArg1
    * @param aArg2
    * @param aArg3
    */
   public Object getVariableData(Object aArg1, Object aArg2, Object aArg3)
   {
      List args = new ArrayList();
      if (aArg1 != null)
         args.add(aArg1);
      if (aArg2 != null)
         args.add(aArg2);
      if (aArg3 != null)
         args.add(aArg3);
      return callFunction("getVariableData", args); //$NON-NLS-1$
   }

   /**
    * An implementation of bpws:getVariableProperty().
    * 
    * @param aArg1
    * @param aArg2
    */
   public Object getVariableProperty(Object aArg1, Object aArg2)
   {
      try
      {
         List args = new ArrayList();
         args.add(aArg1);
         args.add(aArg2);
         return callFunction("getVariableProperty", args); //$NON-NLS-1$
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   /**
    * An implementation of bpws:getLinkStatus().
    * 
    * @param aArg1
    */
   public Object getLinkStatus(Object aArg1)
   {
      try
      {
         List args = new ArrayList();
         args.add(aArg1);
         return callFunction("getLinkStatus", args); //$NON-NLS-1$
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * @param aNamespace The namespace to set.
    */
   protected void setNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }
}
