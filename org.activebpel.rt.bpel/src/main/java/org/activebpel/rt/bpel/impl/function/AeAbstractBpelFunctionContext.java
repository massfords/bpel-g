//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeAbstractBpelFunctionContext.java,v 1.1 2006/08/17 19:59:26 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function;

import org.activebpel.rt.bpel.function.AeUnresolvableException;
import org.activebpel.rt.bpel.function.IAeFunction;

/**
 * A <code>IAeFunctionContext</code> implementation that handles returning
 * common BPEL functions.
 * 
 * Currently supported functions are:
 * 
 * <pre>getVariableProperty</pre> 
 */
public abstract class AeAbstractBpelFunctionContext extends AeAbstractFunctionContext
{
   // Constant for getVariableProperty bpel function. */
   public static final String GET_VARIABLE_PROPERTY = "getVariableProperty"; //$NON-NLS-1$

   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionContext#getFunction(java.lang.String)
    */
   public IAeFunction getFunction(String aLocalName) throws AeUnresolvableException
   {
      if (GET_VARIABLE_PROPERTY.equals(aLocalName))
      {
         return new AeGetVariablePropertyFunction();
      }
      else
      {
         throw new AeUnresolvableException(formatFunctionNotFoundErrorMsg(aLocalName));
      }
   }
}
