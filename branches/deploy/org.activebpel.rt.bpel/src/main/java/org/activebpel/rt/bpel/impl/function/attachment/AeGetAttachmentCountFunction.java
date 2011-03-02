//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/attachment/AeGetAttachmentCountFunction.java,v 1.3 2008/02/05 01:34:09 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function.attachment;

import java.util.List;

import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;

/**
 * Class representing the function used by expression evaluators to handle the BPEL -
 * getAttachmentCount(varname) function call.
 * <p>
 * <em>Description:</em> Returns the count of attachments associated with the named variable.
 * </p>
 * <em>Parameters:</em>
 * <ul>
 * <li>varname – string containing the name of the variable</li>
 * </ul>
 * <p>
 * <em>Return:</em> Integer which contains the count of attachments.
 * </p>
 */
public class AeGetAttachmentCountFunction extends AeAbstractAttachmentFunction
{
   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "getAttachmentCount"; //$NON-NLS-1$

   /**
    * Constructor.
    */
   public AeGetAttachmentCountFunction()
   {
      super(FUNCTION_NAME);
   }

   /**
    * Execution of XPath function.
    * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
   {
      Object result = null;

      // Validate that we have the proper number of arguments
      int numArgs = aArgs.size();
      if ( numArgs < 1 || numArgs > 1 )
         throwFunctionException(INVALID_PARAMS, getFunctionName());
      
      // Get the variable name from the first function argument
      IAeVariable variable = getVariable(aContext.getAbstractBpelObject(), getStringArg(aArgs,0));
     
      result = new Integer(variable.getAttachmentData().size());
      return result;
   }
}
