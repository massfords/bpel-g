//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/attachment/AeCreateAttachmentFunction.java,v 1.5 2008/02/17 21:37:09 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function.attachment;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.attachment.IAeAttachmentItem;
import org.activebpel.rt.base64.Base64;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.AeWebServiceAttachment;

/**
 * Class representing the function used by expression evaluators to handle the BPEL -
 * createAttachment(varname, mimeType, encodedContent, [contentType]) function call.
 * <p>
 * <em>Description:</em> Creates a new attachment for the named variable.
 * </p>
 * <em>Parameters:</em>
 * <ul>
 * <li>varname string containing the name of the target variable</li>
 * <li>mimeType string value of the attachment content type eg. text/plain, image/jpeg ....</li>
 * <li>encodedContent string containing Base64 encoded content data of the attachment</li>
 * <li>Optional content id</li>
 * </ul>
 * <p>
 * <em>Return:</em> true if variable attachment was created successfully.
 * </p>
 * <p>
 * <em>Throws:</em> attachmentFault if there was a problem with passed attachment.
 * </p>
 */
public class AeCreateAttachmentFunction extends AeAbstractAttachmentFunction
{

   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "createAttachment"; //$NON-NLS-1$

   /**
    * Constructor.
    */
   public AeCreateAttachmentFunction()
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
      if ( numArgs < 3 || numArgs > 4)
         throwFunctionException(INVALID_PARAMS, getFunctionName());

      // Get the variable name from the first function argument
      String variableName = getStringArg(aArgs,0);

      // Get the mime type from the second function argument
      String mimeType = getStringArg(aArgs,1);

      if ( AeUtil.isNullOrEmpty(mimeType) )
      {
         throwFunctionException(MISSING_ATTACHMENT_MIME, getFunctionName());
      }

      // Get the attachment content from the third function argument
      String encodedContent = getStringArg(aArgs,2);
      if ( AeUtil.isNullOrEmpty(encodedContent) )
      {
         throwFunctionException(MISSING_ATTACHMENT_CONTENT, getFunctionName());
      }
 
      IAeBusinessProcessInternal process = aContext.getAbstractBpelObject().getProcess();

      long pid = process.getProcessId();
      Map attributes = new HashMap();
      attributes.put(AeMimeUtil.CONTENT_TYPE_ATTRIBUTE, mimeType);
      attributes.put(AeMimeUtil.CONTENT_ID_ATTRIBUTE, numArgs == 4 ? getStringArg(aArgs,3) : AeMimeUtil.AE_DEFAULT_INLINE_CONTENT_ID + pid);

      addVariableAttachment(variableName, attributes, encodedContent, aContext);

      result = Boolean.TRUE;
      return result;
   }
   
   /**
    * Adds given attachment to the variable(for the given variable name) and return the variable
    * @param aVariableName
    * @param aAttachmentProps
    * @param aContent
    * @param aContext
    * @throws AeFunctionCallException
    */
   public IAeAttachmentItem addVariableAttachment(String aVariableName, Map aAttachmentProps, String aContent, IAeFunctionExecutionContext aContext) throws AeFunctionCallException
   {
      IAeVariable variable = getVariable(aContext.getAbstractBpelObject(), aVariableName);
      IAeBusinessProcessInternal process = aContext.getAbstractBpelObject().getProcess();
      ByteArrayInputStream stream = new ByteArrayInputStream(Base64.decode(aContent));
      AeWebServiceAttachment wsAttachment = new AeWebServiceAttachment(stream, aAttachmentProps);
      String variablePath = variable.getLocationPath();
      try
      {
         return process.addVariableAttachment(variablePath, wsAttachment);
      }
      catch (AeBusinessProcessException ex)
      {
         throwFunctionException(CREATE_ATTACHMENT_FAILED, getFunctionName());
      }
      return null;
   }
}
