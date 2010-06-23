//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeExtensionFunctionContext.java,v 1.11 2008/02/02 19:17:41 PJayanetti Exp $
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
import org.activebpel.rt.bpel.impl.function.attachment.AeCopyAllAttachmentsFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeCopyAttachmentFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeCreateAttachmentFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeGetAttachmentCountFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeGetAttachmentSizeFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeGetAttachmentPropertyFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeGetAttachmentTypeFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeRemoveAllAttachmentsFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeRemoveAttachmentFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeReplaceAttachmentFunction;

/**
 * A <code>FunctionContext</code> implementation that handles returning BPEL extension functions. Currently
 * supported function are:
 * 
 * <pre>
 * getProcessId() 
 * getProcessName()
 * getProcessInitiator()
 * getMyRoleProperty()
 * getAttachmentCount()
 * copyAttachment()
 * copyAllAttachments()
 * getAttachmentType()
 * getAttachmentProperty()
 * removeAttachment()
 * removeAllAttachments()
 * replaceAttachment()
 * getAttachmentSize()
 * createAttachment()
 * resolveURN() 
 * base64Encode()
 * getPlanExtensions()
 * </pre>
 */
public class AeExtensionFunctionContext extends AeAbstractFunctionContext
{
   /**
    * @see org.activebpel.rt.bpel.function.IAeFunctionContext#getFunction(java.lang.String) 
    * TODO: (JB) refractor into a function factory or chain of command pattern to return the objects
    */
   public IAeFunction getFunction(String aLocalName) throws AeUnresolvableException
   {
      if ( AeGetProcessIdFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeGetProcessIdFunction();
      }
      else if ( AeGetProcessNameFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeGetProcessNameFunction();
      }
      else if ( AeGetProcessInitiatorFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeGetProcessInitiatorFunction();
      }      
      else if ( AeGetMyRolePropertyFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeGetMyRolePropertyFunction();
      }
      else if ( AeGetAttachmentCountFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeGetAttachmentCountFunction();
      }
      else if ( AeCopyAttachmentFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeCopyAttachmentFunction();
      }
      else if ( AeCopyAllAttachmentsFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeCopyAllAttachmentsFunction();
      }
      else if ( AeGetAttachmentTypeFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeGetAttachmentTypeFunction();
      }
      else if ( AeGetAttachmentPropertyFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeGetAttachmentPropertyFunction();
      }
      else if ( AeRemoveAllAttachmentsFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeRemoveAllAttachmentsFunction();
      } 
      else if ( AeRemoveAttachmentFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeRemoveAttachmentFunction();
      } 
      else if ( AeReplaceAttachmentFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeReplaceAttachmentFunction();
      }
      else if ( AeGetAttachmentSizeFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeGetAttachmentSizeFunction();
      }
      else if ( AeCreateAttachmentFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeCreateAttachmentFunction();
      }
      else if ( AeResolveURNFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeResolveURNFunction();
      }
      else if ( AeBase64EncodeFunction.FUNCTION_NAME.equals(aLocalName) )
      {
         return new AeBase64EncodeFunction();
      }
      else if (AeGetPlanExtensions.FUNCTION_NAME.equals(aLocalName))
      {
         return new AeGetPlanExtensions();
      }
      else
      {
         throw new AeUnresolvableException(formatFunctionNotFoundErrorMsg(aLocalName));
      }
   }
}
