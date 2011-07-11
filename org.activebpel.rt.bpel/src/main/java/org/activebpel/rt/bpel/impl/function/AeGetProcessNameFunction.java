//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeGetProcessNameFunction.java,v 1.3 2007/09/04 15:51:33 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function;

import java.util.List;

import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;


/**
 * Class representing the XPath function used by the expression evaluator to handle 
 * the BPEL extension function call getProcessName().
 */
public class AeGetProcessNameFunction extends AeAbstractBpelFunction
{
   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "getProcessName"; //$NON-NLS-1$

   /**
    * Constructor.
    */
   public AeGetProcessNameFunction()
   {
       super(FUNCTION_NAME);
   }
   
   /**
    * Execution of XPath getProcessId function. 
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
   {
      return aContext.getAbstractBpelObject().getProcess().getName().getLocalPart();
   }
}
