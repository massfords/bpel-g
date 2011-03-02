//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/attachment/AeGetAttachmentSizeFunction.java,v 1.3.8.1 2008/04/21 16:09:43 ppatruni Exp $
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
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.util.AeMimeUtil;

/**
 * Class representing the function used by expression evaluators to handle the BPEL -
 * getAttachmentSize(varname,itemNumber) function call.
 * <p>
 * <em>Description:</em> : Returns the length of the passed attachment.
 * </p>
 * <em>Parameters:</em>
 * <ul>
 * <li>varname string containing the name of the variable</li>
 * <li>itemNumber Item number of attachment (starting at one).</li>
 * </ul>
 * <p>
 * <em>Return:</em> long which contains the length of the attachment.
 * </p>
 * <p>
 * <em>Throws:</em> attachmentFault if there was a problem with passed attachment.</p>
 */
public class AeGetAttachmentSizeFunction extends AeAbstractAttachmentFunction
{
   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "getAttachmentSize"; //$NON-NLS-1$

   /**
    * Constructor.
    */
   public AeGetAttachmentSizeFunction()
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
      if ( numArgs != 2 )
         throwFunctionException(INVALID_PARAMS, getFunctionName());
      
      // Get the variable name from the first function argument
      String variableName = getStringArg(aArgs,0);
      // Get the variable attachment item number from the second function argument
      IAeAttachmentItem item = getAttachment(aContext, variableName, getPositiveIntArg(aArgs,1));
      
      result = item.getHeader(AeMimeUtil.AE_SIZE_ATTRIBUTE);
      
      if ( result == null )
         throwFunctionException(NULL_RESULT_ERROR, getFunctionName());

      return result;
   }
}
