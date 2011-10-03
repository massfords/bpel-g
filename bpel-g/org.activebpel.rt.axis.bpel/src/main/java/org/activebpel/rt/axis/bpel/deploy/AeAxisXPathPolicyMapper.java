// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/deploy/AeAxisXPathPolicyMapper.java,v 1.4 2006/09/15 21:25:09 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.IAePolicyConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates Policy Deployment for a XPath mapping assertions 
 */
public class AeAxisXPathPolicyMapper extends AeAxisPolicyMapper
{
   public static final String HANDLER_XPATH_RECEIVER = "proc:".concat(org.activebpel.rt.axis.bpel.handlers.AeXPathReceiveHandler.class.getName()); //$NON-NLS-1$
   
    /** 
	 * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerRequestHandlers(java.util.List)
	 */
   public List<Element> getServerRequestHandlers( List<Element> aPolicyList )
   throws AeException
   {
      List<Element> handlers = new ArrayList<Element>();
      
      // Examine the list of policy assertions to determine the request handlers
      for (Iterator<Element> it = aPolicyList.iterator(); it.hasNext();) 
      {
         Element policy = it.next();
         NodeList children = policy.getElementsByTagNameNS(IAeConstants.ABP_NAMESPACE_URI, IAePolicyConstants.TAG_ASSERT_XPATH_RECEIVE);
         for (int i=0, len=children.getLength(); i < len; i++)
         {
            Node assertion = children.item(i);
            Element handler = createHandlerElement(policy.getOwnerDocument(), HANDLER_XPATH_RECEIVER , null);
            NodeList params = assertion.getChildNodes();
            for (int j = 0; j < params.getLength(); j++) {
               Node param = params.item(j);
               if (param.getNodeType() != Node.ELEMENT_NODE)
                  continue;
               Element elem = (Element) param;
               String name = elem.getAttribute(IAePolicyConstants.TAG_NAME_ATTR);
               String value = elem.getAttribute(IAePolicyConstants.TAG_VALUE_ATTR);
               handler.appendChild(createParameterElement(policy.getOwnerDocument(), name , value ));
            }
            handlers.add(handler);
         }
      }
      return handlers;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerResponseHandlers(java.util.List)
    */
   public List<Element> getServerResponseHandlers( List<Element> aPolicyList )
   throws AeException
   {
      return Collections.<Element>emptyList();
   }
   
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getClientRequestHandlers(java.util.List)
    */
   public List<Element> getClientRequestHandlers( List<Element> aPolicyList )
   throws AeException
   {
      return Collections.<Element>emptyList();
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getClientResponseHandlers(java.util.List)
    */
   public List<Element> getClientResponseHandlers( List<Element> aPolicyList )
   throws AeException
   {
      return getServerRequestHandlers(aPolicyList);
   }

   /**
    * Overrides method to return service deployment parameters  
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServiceParameters(java.util.List)
    */
   public List<Element> getServiceParameters(List<Element> aPolicyList) throws AeException
   {
      return Collections.<Element>emptyList();
   }

   /**
    * Overrides method to map policies to name/value pairs 
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getCallProperties(java.util.List)
    */
   public Map<String, Object> getCallProperties(List<Element> aPolicyList) throws AeException
   {
      Map<String, Object> map = new HashMap<String, Object>();
      // Examine the list of policy assertions to determine the request handlers
      for (Iterator<Element> it = aPolicyList.iterator(); it.hasNext();) 
      {
         Element policy = it.next();
         NodeList children = policy.getElementsByTagNameNS(IAeConstants.ABP_NAMESPACE_URI, IAePolicyConstants.TAG_ASSERT_XPATH_RECEIVE);
         for (int i=0, len=children.getLength(); i < len; i++)
         {
            map.put(IAePolicyConstants.XPATH_QUERY_SOURCE, IAePolicyConstants.XPATH_QUERY_SOURCE_CONTEXT);
            Map<String, String> handlerParams = new HashMap<String, String>();
            Element assertion = (Element) children.item(i);
            NodeList params = assertion.getChildNodes();
            for (int j = 0; j < params.getLength(); j++) 
            {
               Node param = params.item(j);
               if (param.getNodeType() != Node.ELEMENT_NODE)
                  continue;
               Element elem = (Element) param;
               String name = elem.getAttribute(IAePolicyConstants.TAG_NAME_ATTR);
               String value = elem.getAttribute(IAePolicyConstants.TAG_VALUE_ATTR);
               handlerParams.put(name, value);
            }
            map.put(IAePolicyConstants.XPATH_QUERY_PARAMS, handlerParams);
         }
      }
      return map;
   }
   
}
