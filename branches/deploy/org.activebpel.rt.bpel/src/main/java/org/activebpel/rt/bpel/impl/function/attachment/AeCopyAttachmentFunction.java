//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/attachment/AeCopyAttachmentFunction.java,v 1.3.4.1 2008/04/21 16:09:43 ppatruni Exp $
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
 * copyAttachment(fromVarname, fromItemNumber, toVarname) 
 * <p>
 * <ul>
 * <li>fromVarname string containing the name of the source variable.</li>
 * <li>fromItemNumber item number of the source attachment.</li>
 *  <li>toVarname string containing the name of the target variable.</li>
 * </ul>
 * </p>
 * <em>Throws:</em> attachmentFault if there was a problem with passed attachment.
 */
public class AeCopyAttachmentFunction extends AeAbstractAttachmentFunction
{

   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "copyAttachment"; //$NON-NLS-1$

   /**
    * Constructor.
    */
   public AeCopyAttachmentFunction()
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
      if (numArgs != 3)
         throwFunctionException(INVALID_PARAMS, getFunctionName());

      // Get the from variable name from the first function argument
      // Get the from item number from the second function argument
      // Get the to variable name from the third function argument
      IAeAttachmentItem fromItem = getAttachment(aContext, getStringArg(aArgs,0), getPositiveIntArg(aArgs,1));
      IAeVariable toVariable = getVariable(aContext.getAbstractBpelObject(), getStringArg(aArgs,2));
      result =  new Boolean(toVariable.getAttachmentData().add(fromItem));
      return result;
   }
}
