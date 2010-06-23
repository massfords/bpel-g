//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/attachment/AeRemoveAttachmentFunction.java,v 1.3.4.1 2008/04/21 16:09:43 ppatruni Exp $
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
 * removeAttachment(varname,itemNumber) function call.
 * <p>
 * <em>Description:</em> : Deletes the attachment of the named variable identified by the itemNumber.
 * </p>
 * <em>Parameters:</em>
 * <ul>
 * <li>varname string containing the name of the variable</li>
 * <li>itemNumber Item number of attachment (starting at one).</li>
 * </ul>
 * <p>
 * <em>Return:</em> true if variable attachment was deleted.
 * </p>
 * <p>
 * <em>Throws:</em> attachmentFault if there was a problem with the variable or the item number was out of
 * range</p>
 */
public class AeRemoveAttachmentFunction extends AeAbstractAttachmentFunction
{

   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "removeAttachment"; //$NON-NLS-1$

   /**
    * Constructor.
    */
   public AeRemoveAttachmentFunction()
   {
      super(FUNCTION_NAME);
   }

   /**
    * Execution of XPath function.
    * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
   {
      Object result = new Boolean(false);

      // Validate that we have the proper number of arguments
      int numArgs = aArgs.size();
      if ( numArgs < 2 || numArgs > 2 )
         throwFunctionException(INVALID_PARAMS, FUNCTION_NAME);

      // Get the variable name from the first function argument
      IAeVariable variable = getVariable(aContext.getAbstractBpelObject(), getStringArg(aArgs,0));
      // Get the attachment item number from the second function argument
      variable.getAttachmentData().remove(getItemIndex(getPositiveIntArg(aArgs,1)));
      result = Boolean.TRUE;
      return result;
   }
}
