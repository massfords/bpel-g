//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeGetProcessInitiatorFunction.java,v 1.1 2008/02/02 19:17:41 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function;

import java.util.List;

import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;

/**
 * Class representing the XPath function used by the expression evaluator to handle
 * the BPEL extension function call getProcessInitiator().
 */
public class AeGetProcessInitiatorFunction extends AeAbstractBpelFunction
{
   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "getProcessInitiator"; //$NON-NLS-1$
   
   /**
    * Constructor.
    */   
   public AeGetProcessInitiatorFunction()
   {
      super(FUNCTION_NAME);
   }

   /**
    * Overrides method to return process intiator.
    * @see org.activebpel.rt.bpel.function.IAeFunction#call(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, java.util.List)
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
   {
      return aContext.getAbstractBpelObject().getProcess().getProcessInitiator();
   }
}