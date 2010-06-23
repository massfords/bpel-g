// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/expr/functions/AeFunctionValidatorFactory.java,v 1.2 2008/02/17 21:37:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.expr.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.expr.AeExpressionLanguageUtil;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidator;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;

/**
 * Provides lookup of validator given a function qname and BPEL namespace.
 */
public class AeFunctionValidatorFactory implements IAeFunctionValidatorFactory
{
   /** BPEL namespace to Map (String -> Map). Nested map is: (QName -> IAeFunctionValidator) */
   private Map mNamespaceMap = Collections.synchronizedMap(new HashMap());
   
   /**
    * C'tor
    */
   public AeFunctionValidatorFactory()
   {
      getNamespaceMap().put(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, getWSBPELFunctions());
      getNamespaceMap().put(IAeBPELConstants.BPWS_NAMESPACE_URI, getBPWSFunctions());
      getNamespaceMap().put(IAeBPELConstants.WSBPEL_2_0_ABSTRACT_NAMESPACE_URI, getWSBPELFunctions());
   }

   /**
    * @see org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory#getValidator(java.lang.String, javax.xml.namespace.QName)
    */
   public IAeFunctionValidator getValidator(String aBpelNamespace, QName aQName)
   {
      IAeFunctionValidator validator = null;
      Map functionsMap = (Map) getNamespaceMap().get(aBpelNamespace);
      if (functionsMap != null)
      {
         validator = (IAeFunctionValidator) functionsMap.get(aQName);
      }
      return validator;
   }
   
   /**
    * Register a collection of function validators.
    * @param aBpelNamespace - namespace that these validators are valid for.
    * @param aValidatorMap - Map (QName -> IAeFunctionValidator)
    */
   public void registerValidators(String aBpelNamespace, Map aValidatorMap)
   {
      for (Iterator iter = aValidatorMap.entrySet().iterator(); iter.hasNext();)
      {
         Entry item = (Entry) iter.next();
         registerValidator(aBpelNamespace, (QName) item.getKey(), (IAeFunctionValidator) item.getValue());
      }
   }
   
   /**
    * Register a collection of function validators to all bpel namespaces.
    * @param aValidatorMap
    */
   public void registerValidators(Map aValidatorMap)
   {
      for (Iterator iter = getNamespaceMap().keySet().iterator(); iter.hasNext();)
      {
         String key = String.valueOf(iter.next());
         registerValidators(key, aValidatorMap);
      }
   }
   
   /**
    * Register a single function validator to the given bpel namespace.
    * @param aBpelNamespace
    * @param aFunctionQName
    * @param aValidator
    */
   public void registerValidator(String aBpelNamespace, QName aFunctionQName, IAeFunctionValidator aValidator)
   {
      Map updateMap = (Map) getNamespaceMap().get(aBpelNamespace);
      if (!updateMap.containsKey(aFunctionQName))
      {
         updateMap.put(aFunctionQName, aValidator);
      }
   }
   
   /**
    * Register a single function validator to all bpel namespaces.
    * @param aFunctionQName
    * @param aValidator
    */
   public void registerValidator(QName aFunctionQName, IAeFunctionValidator aValidator)
   {
      for (Iterator iter = getNamespaceMap().keySet().iterator(); iter.hasNext();)
      {
         String key = String.valueOf(iter.next());
         registerValidator(key, aFunctionQName, aValidator);
      }
   }
   
   /**
    * Common functions used by both BPEL 1.1 and 2.0
    */
   protected Map getCommonFunctions()
   {
      Map map = new HashMap();
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "getProcessId"), new AeGetProcessIdFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "getProcessName"), new AeGetProcessNameFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "resolveURN"), new AeResolveURNFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "getMyRoleProperty"), new AeGetMyRolePropertyFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "getAttachmentCount"), new AeGetAttachmentCountFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "copyAttachment"), new AeCopyAttachmentFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "replaceAttachment"), new AeReplaceAttachmentFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "removeAttachment"), new AeRemoveAttachmentFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "copyAllAttachments"), new AeCopyAllAttachmentsFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "removeAllAttachments"), new AeRemoveAllAttachmentsFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "getAttachmentType"), new AeGetAttachmentTypeFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "getAttachmentProperty"), new AeGetAttachmentPropertyFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "getAttachmentSize"), new AeGetAttachmentSizeFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "createAttachment"), new AeCreateAttachmentFunctionValidator()); //$NON-NLS-1$
      map.put(new QName(IAeBPELConstants.ABX_FUNCTIONS_NAMESPACE_URI, "base64Encode"), new AeBase64EncodeFunctionValidator()); //$NON-NLS-1$
      return map;
   }

   /**
    * BPEL 2.0 functions
    */
   protected Map getWSBPELFunctions()
   {
      Map map = new HashMap();
      // common functions
      map.putAll(getCommonFunctions());
      // add all WSBPEL functions
      map.put(AeExpressionLanguageUtil.VAR_PROPERTY_FUNC_BPEL20, new AeGetVariablePropertyFunctionValidator());
      map.put(AeExpressionLanguageUtil.DO_XSL_TRANSFORM_FUNC_BPEL20, new AeDoXslTransformFunctionValidator());
      
      // add all BPWS functions, but report warning if they're found
      map.put(AeExpressionLanguageUtil.VAR_DATA_FUNC, new AeBPWSFunctionUsedInWSBPELValidator( new AeGetVariableDataFunctionValidator()));
      map.put(AeExpressionLanguageUtil.VAR_PROPERTY_FUNC, new AeBPWSFunctionUsedInWSBPELValidator( new AeGetVariablePropertyFunctionValidator() ));
      map.put(AeExpressionLanguageUtil.LINK_STATUS_FUNC, new AeBPWSFunctionUsedInWSBPELValidator( new AeGetLinkStatusFunctionValidator()));
      return map;
   }
   
   /**
    * BPEL 1.1 functions
    */
   protected Map getBPWSFunctions()
   {
      Map map = new HashMap();
      // common functions
      map.putAll(getCommonFunctions());
      // add all BPWS functions
      map.put(AeExpressionLanguageUtil.VAR_DATA_FUNC, new AeGetVariableDataFunctionValidator());
      map.put(AeExpressionLanguageUtil.VAR_PROPERTY_FUNC, new AeGetVariablePropertyFunctionValidator());
      map.put(AeExpressionLanguageUtil.LINK_STATUS_FUNC, new AeGetLinkStatusFunctionValidator());
      
      // add all WSBPEL functions, but report errors if they're found
      map.put(AeExpressionLanguageUtil.DO_XSL_TRANSFORM_FUNC_BPEL20, new AeWSBPELFunctionUsedInBPWSValidator(new AeDoXslTransformFunctionValidator()));
      map.put(AeExpressionLanguageUtil.VAR_PROPERTY_FUNC_BPEL20, new AeWSBPELFunctionUsedInBPWSValidator(new AeGetVariablePropertyFunctionValidator()));
      return map;
   }
   
   /**
    * @return Returns the namespaceMap.
    */
   protected Map getNamespaceMap()
   {
      return mNamespaceMap;
   }

   /**
    * @param aNamespaceMap the namespaceMap to set
    */
   protected void setNamespaceMap(Map aNamespaceMap)
   {
      mNamespaceMap = aNamespaceMap;
   }

}
