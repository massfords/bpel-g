//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/attachment/AeReplaceAttachmentFunction.java,v 1.2 2007/09/04 15:51:33 jbik Exp $
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

import org.activebpel.rt.attachment.IAeAttachmentItem;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;

/**
 * Class representing the function used by expression evaluators to handle the BPEL -
 * replaceAttachment(fromVarname, fromItemNumber,toVarname, toItemNumber) function call.
 * <p>
 * <em>Description:</em> Replaces the attachment of the named variable.
 * </p>
 * <em>Parameters:</em>
 * <ul>
 * <li>fromVarname string containing the name of the variable with the replacement attachment</li>
 * <li>fromItemNumber Item number of the replacement attachment (starting at one).</li>
 * <li>toVarname string containing the name of the variable with the target attachment to be replaced</li>
 * <li>toItemNumber Item number of the target replacement attachment (starting at one).</li>
 * </ul>
 * <p>
 * <em>Return:</em> true if variable attachment was replaced.
 * </p>
 * <p>
 * <em>Throws:</em> attachmentFault if there was a problem with passed attachment.
 * </p>
 */
public class AeReplaceAttachmentFunction extends AeAbstractAttachmentFunction
{

   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "replaceAttachment"; //$NON-NLS-1$

   /**
    * Constructor.
    */
   public AeReplaceAttachmentFunction()
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
      if ( numArgs != 4 )
         throwFunctionException(INVALID_PARAMS, getFunctionName());

      // Get the from variable name from the first function argument
      // Get the from item number from the second function argument
      IAeAttachmentItem fromItem = getAttachment(aContext, getStringArg(aArgs,0), getPositiveIntArg(aArgs,1));

      // Get the to variable name from the third function argument
      // Get the to item index from the fourth function argument
      IAeVariable toVariable = getVariable(aContext.getAbstractBpelObject(), getStringArg(aArgs,2));
      toVariable.getAttachmentData().set(getItemIndex(getPositiveIntArg(aArgs,3)), fromItem);
      result = Boolean.TRUE;
      return result;
   }
}
