//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/expr/AeExpressionLanguageUtil.java,v 1.14 2008/01/25 21:01:18 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.expr;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.impl.function.attachment.AeCopyAllAttachmentsFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeCopyAttachmentFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeCreateAttachmentFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeGetAttachmentPropertyFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeGetAttachmentTypeFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeRemoveAllAttachmentsFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeRemoveAttachmentFunction;
import org.activebpel.rt.bpel.impl.function.attachment.AeReplaceAttachmentFunction;
import org.activebpel.rt.expr.def.AeScriptFuncDef;

/**
 * This class has some static convenience methods for doing various tasks related to expressions
 * and expression languages.
 */
public class AeExpressionLanguageUtil
{
   /** The ActiveBPEL extension function used to add a variable attachment */
   public final static QName ATTACHMENT_COPY_FUNC = new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, AeCopyAttachmentFunction.FUNCTION_NAME);
   /** The ActiveBPEL extension function used to replace a variable attachment */
   public final static QName ATTACHMENT_REPLACE_FUNC = new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, AeReplaceAttachmentFunction.FUNCTION_NAME);
   /** The ActiveBPEL extension function used to remove a variable attachment */
   public final static QName ATTACHMENT_REMOVE_FUNC = new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, AeRemoveAttachmentFunction.FUNCTION_NAME);
   /** The ActiveBPEL extension function used to copy a variable attachment */
   public final static QName ATTACHMENT_COPY_ALL_FUNC = new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, AeCopyAllAttachmentsFunction.FUNCTION_NAME);
   /** The ActiveBPEL extension function used to remove all variable attachments */
   public final static QName ATTACHMENT_REMOVE_ALL_FUNC = new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, AeRemoveAllAttachmentsFunction.FUNCTION_NAME);
   /** The ActiveBPEL extension function used to get a variable attachment MIME TYPE */
   public final static QName ATTACHMENT_GET_VAR_TYPE_FUNC = new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, AeGetAttachmentTypeFunction.FUNCTION_NAME);
   /** The ActiveBPEL extension function used to get a variable attachment property */
   public final static QName ATTACHMENT_GET_VAR_PROP_FUNC = new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, AeGetAttachmentPropertyFunction.FUNCTION_NAME);
   /** The ActiveBPEL extension function used to create a variable attachment */
   public final static QName ATTACHMENT_CREATE_FUNC = new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, AeCreateAttachmentFunction.FUNCTION_NAME);
   
    
   
   /** The BPEL extension function used retrieve a variable property */
   public final static String VAR_PROPERTY_FUNC_NAME = "getVariableProperty"; //$NON-NLS-1$
   /** The BPEL extension function used retrieve variable data */
   public final static String VAR_DATA_FUNC_NAME = "getVariableData"; //$NON-NLS-1$
   /** The BPEL extension function used retrieve variable data */
   public final static String LINK_STATUS_FUNC_NAME = "getLinkStatus"; //$NON-NLS-1$
   
   
   /** The fully qualified name of the getVariableProperty function. */
   public final static QName VAR_PROPERTY_FUNC = new QName(IAeBPELConstants.BPWS_NAMESPACE_URI, VAR_PROPERTY_FUNC_NAME);
   /** The fully qualified name of the getVariableProperty function. */
   public final static QName MYROLE_PROPERTY_FUNC = new QName(IAeBPELConstants.ABX_NAMESPACE_URI, "getMyRoleProperty"); //$NON-NLS-1$
   /** The fully qualified name of the getVariableData function. */
   public final static QName VAR_DATA_FUNC = new QName(IAeBPELConstants.BPWS_NAMESPACE_URI, VAR_DATA_FUNC_NAME);
   /** The fully qualified name of the getLinkStatus function. */
   public final static QName LINK_STATUS_FUNC = new QName(IAeBPELConstants.BPWS_NAMESPACE_URI, LINK_STATUS_FUNC_NAME);

   /** The fully qualified name of the getVariableProperty function. */
   public final static QName VAR_PROPERTY_FUNC_BPEL20 = new QName(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, VAR_PROPERTY_FUNC_NAME);
   /** The fully qualified name of the getVariableProperty function. */
   public final static QName DO_XSL_TRANSFORM_FUNC_BPEL20 = new QName(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, "doXslTransform"); //$NON-NLS-1$

   /**
    * Returns true if the function is a bpws:getVariableData function.
    *
    * @param aFunction
    */
   public static boolean isVarDataFunction(AeScriptFuncDef aFunction)
   {
      return VAR_DATA_FUNC.equals(aFunction.getQName());
   }

   /**
    * Returns true if the function is a bpws:getVariableProperty function.
    *
    * @param aFunction
    */
   public static boolean isVarPropertyFunction(AeScriptFuncDef aFunction)
   {
      return VAR_PROPERTY_FUNC.equals(aFunction.getQName()) || VAR_PROPERTY_FUNC_BPEL20.equals(aFunction.getQName());
   }

   /**
    * Returns true if the function is an attachment function.
    *
    * @param aFunction
    */
   public static boolean isAttachmentFunction(AeScriptFuncDef aFunction)
   {
      return ATTACHMENT_COPY_FUNC.equals(aFunction.getQName()) ||
             ATTACHMENT_REPLACE_FUNC.equals(aFunction.getQName()) ||
             ATTACHMENT_REMOVE_FUNC.equals(aFunction.getQName()) ||
             ATTACHMENT_COPY_ALL_FUNC.equals(aFunction.getQName()) ||
             ATTACHMENT_REMOVE_ALL_FUNC.equals(aFunction.getQName()) ||
             ATTACHMENT_GET_VAR_TYPE_FUNC.equals(aFunction.getQName()) ||
             ATTACHMENT_GET_VAR_PROP_FUNC.equals(aFunction.getQName()) ||
             ATTACHMENT_CREATE_FUNC.equals(aFunction.getQName());
   }
   
   /**
    * Returns true if the function is a abx:getMyRoleProperty function.
    *
    * @param aFunction
    */
   public static boolean isMyRolePropertyFunction(AeScriptFuncDef aFunction)
   {
      return MYROLE_PROPERTY_FUNC.equals(aFunction.getQName());
   }


   /**
    * Returns true if the function is a bpws:getLinkStatus function.
    *
    * @param aFunction
    */
   public static boolean isLinkStatusFunction(AeScriptFuncDef aFunction)
   {
      return LINK_STATUS_FUNC.equals(aFunction.getQName());
   }
   
   /**
    * Returns true if the function is a bpel:doXslTransform function.
    * 
    * @param aFunction
    */
   public static boolean isDoXslTransformFunction(AeScriptFuncDef aFunction)
   {
      return DO_XSL_TRANSFORM_FUNC_BPEL20.equals(aFunction.getQName());
   }
}
