// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeWsioPolicyMapper.java,v 1.2 2008/02/04 21:17:21 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.AeWSDLPolicyHelper;
import org.activebpel.rt.bpel.impl.AeTimeoutPolicy;
import org.activebpel.rt.bpel.server.deploy.IAePolicyMapper;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Maps assertions into call properties for the following:
 * 
 * Engine Managed Correlation
 * Web Service Timeout
 * 
 */
public class AeWsioPolicyMapper implements IAePolicyMapper 
{
   /**
    * Default Constructor.
    * @param aConfig
    */
   public AeWsioPolicyMapper(Map aConfig)
   {
            
   }   
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerRequestHandlers(java.util.List)
    */
   public List getServerRequestHandlers( List aPolicyList ) throws AeException
   {
      return Collections.EMPTY_LIST;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerResponseHandlers(java.util.List)
    */
   public List getServerResponseHandlers( List aPolicyList )
   throws AeException
   {
      return Collections.EMPTY_LIST;
   }
   
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getClientRequestHandlers(java.util.List)
    */
   public List getClientRequestHandlers( List aPolicyList )
   throws AeException
   {
      return Collections.EMPTY_LIST;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getClientResponseHandlers(java.util.List)
    */
   public List getClientResponseHandlers( List aPolicyList )
   throws AeException
   {
      return getServerRequestHandlers(aPolicyList);
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServiceParameters(java.util.List)
    */
   public List getServiceParameters(List aPolicyList) throws AeException
   {
      return Collections.EMPTY_LIST;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getCallProperties(java.util.List)
    */
   public Map getCallProperties(List aPolicyList) throws AeException
   {
      Map map = new HashMap();
      if (AeUtil.notNullOrEmpty(aPolicyList))
      {
         // Examine the list of policy assertions to determine the request handlers
         for (Iterator it = aPolicyList.iterator(); it.hasNext();) 
         {
            Element policy = (Element) it.next();
            // Engine managed correlation
            NodeList children = policy.getElementsByTagNameNS(IAeConstants.ABP_NAMESPACE_URI, IAePolicyConstants.TAG_ASSERT_MANAGED_CORRELATION);
            if (children.getLength() > 0)
            {
               // Add a conversationId header QName
               map.put(IAePolicyConstants.TAG_ASSERT_MANAGED_CORRELATION, IAePolicyConstants.CONVERSATION_ID_HEADER); 
            }
            
            // Map principal as header
            children = policy.getElementsByTagNameNS(IAeConstants.ABP_NAMESPACE_URI, IAePolicyConstants.TAG_ASSERT_MAP_PROCESS_INTIATOR);
            if (children.getLength() > 0)
            {
               // Add a principal header QName
               map.put(IAePolicyConstants.TAG_ASSERT_MAP_PROCESS_INTIATOR, IAePolicyConstants.PRINCIPAL_HEADER); 
            }             
         }
         
         // See if a timeout policy has been configured, otherwise use the engine value
         int timeout = AeEngineFactory.getEngineConfig().getWebServiceInvokeTimeout();
         Element timeoutPolicy = AeWSDLPolicyHelper.getPolicyElement(aPolicyList, AeTimeoutPolicy.TIMEOUT_ID);
         if (timeoutPolicy != null)
            timeout = AeTimeoutPolicy.getTimeoutValue(timeoutPolicy);
         map.put(AeTimeoutPolicy.TAG_ASSERT_TIMEOUT, String.valueOf(timeout));
      }
      return map;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getDeploymentHandler(java.util.List)
    */
   public String getDeploymentHandler(List aPolicyList) throws AeException
   {
      return null;
   }
}
