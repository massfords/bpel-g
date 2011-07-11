//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/attachment/AeGetAttachmentPropertyFunction.java,v 1.2.8.1 2008/04/21 16:09:43 ppatruni Exp $
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

/**
 * Class representing the function used by expression evaluators to handle the BPEL -
 * getAttachmentProperty(varname,itemNumber,propertyName) function call.
 * <p>
 * <em>Description:</em> : Returns the value of the name property of the attachment item.
 * </p>
 * <em>Parameters:</em>
 * <ul>
 * <li>varname string containing the name of the variable</li>
 * <li>itemNumber Item number of attachment (starting at one).</li>
 * <li>propertyName name of the attachment header property.</li>
 * </ul>
 * <p>
 * <em>Return:</em> String which contains the value of the attachment property, return "" if the property is
 * not set.
 * </p>
 * <p>
 * <em>Throws:</em> attachmentFault if there was a problem with passed attachment.
 * </p>AeGetAttachmentSizeFunction
 */
public class AeGetAttachmentPropertyFunction extends AeAbstractAttachmentFunction
{
   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "getAttachmentProperty"; //$NON-NLS-1$

   /**
    * Constructor.
    */
   public AeGetAttachmentPropertyFunction()
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
      if ( numArgs != 3 )
         throwFunctionException(INVALID_PARAMS, getFunctionName());

      // Get the variable name from the first function argument
      String variableName = getStringArg(aArgs,0);
      // Get the attachment item number from the second function argument
      IAeAttachmentItem item = getAttachment(aContext, variableName, getPositiveIntArg(aArgs,1));

      // Get the header property name from the third function argument
      String propertyName = getStringArg(aArgs,2);
      
      result = item.getHeader(propertyName);

      if ( result == null )
         throwFunctionException(NULL_RESULT_ERROR, getFunctionName());

      return result;
   }
}
