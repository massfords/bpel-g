//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/deploy/AeHttpPolicyMapper.java,v 1.1 2008/03/28 17:56:08 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http.deploy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.server.deploy.IAePolicyMapper;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Creates http options from Http Transport mapping assertions All policies are optional and provided to allow
 * users to override the values set on the http client for a particular invoke Example:
 * 
 * <pre>
 *    &lt;abp:HTTPTransportOptions httpSocketTimeout=&quot;10000&quot; httpTcpNoDelyt=&quot;true&quot; redirectWithGET=&quot;true&quot;&gt;
 * </pre>
 */
public class AeHttpPolicyMapper implements IAePolicyMapper, IAePolicyConstants
{

   /**
    * Default Constructor.
    * @param aConfig
    */
   public AeHttpPolicyMapper(Map aConfig)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getCallProperties(java.util.List)
    */
   public Map getCallProperties(List aPolicyList) throws AeException
   {
      HashMap map = new HashMap();

      for (Iterator it = aPolicyList.iterator(); it.hasNext();)
      {
         Element aPolicyElement = (Element)it.next();

         // Extract from the policy HTTP Transport Options if any
         NodeList children = aPolicyElement.getElementsByTagNameNS(IAeConstants.ABP_NAMESPACE_URI, TAG_HTTP_TRANSPORT_OPTIONS);
         for (int i = 0, len = children.getLength(); i < len; i++)
         {

            Element assertion = (Element)children.item(i);

            // Socket timeout
            String value = assertion.getAttribute(ATTR_HTTP_SOCKET_TIMEOUT);
            if ( !AeUtil.isNullOrEmpty(value) )
            {
               map.put(ATTR_HTTP_SOCKET_TIMEOUT, new Integer(AeUtil.parseInt(value, 0)));
            }

            // TCP_NO_DELAY
            value = assertion.getAttribute(ATTR_HTTP_TCP_NODELAY);
            if ( !AeUtil.isNullOrEmpty(value) )
            {
               map.put(ATTR_HTTP_TCP_NODELAY, new Boolean(AeUtil.toBoolean(value)));
            }

            // REDIRECT_WITH_GET
            value = assertion.getAttribute(ATTR_HTTP_REDIRECT_WITH_GET);
            if ( !AeUtil.isNullOrEmpty(value) )
            {
               map.put(ATTR_HTTP_REDIRECT_WITH_GET, new Boolean(AeUtil.toBoolean(value)));
            }

            // XML data mime types - all mime types to be treated as xml data
            NodeList mimetypechildren = assertion.getElementsByTagNameNS(IAeConstants.ABP_NAMESPACE_URI, TAG_HTTP_MIME_TYPE);
            if ( mimetypechildren != null )
            {
               Set xmlmimetypes = new TreeSet();
               for (int j = 0, xlen = mimetypechildren.getLength(); j < xlen; j++)
               {
                  Element mimetype = (Element)mimetypechildren.item(j);
                  xmlmimetypes.add(AeXmlUtil.getText(mimetype).toLowerCase());
               }
               if ( xmlmimetypes.size() > 0 )
                  map.put(TAG_HTTP_XML_TYPES, xmlmimetypes);
            }
         }
      }
      return map;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getClientRequestHandlers(java.util.List)
    */
   public List getClientRequestHandlers(List aPolicyList) throws AeException
   {
      return Collections.EMPTY_LIST;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getClientResponseHandlers(java.util.List)
    */
   public List getClientResponseHandlers(List aPolicyList) throws AeException
   {
      return Collections.EMPTY_LIST;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getDeploymentHandler(java.util.List)
    */
   public String getDeploymentHandler(List aPolicyList) throws AeException
   {
      return null;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerRequestHandlers(java.util.List)
    */
   public List getServerRequestHandlers(List aPolicyList) throws AeException
   {
      return Collections.EMPTY_LIST;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerResponseHandlers(java.util.List)
    */
   public List getServerResponseHandlers(List aPolicyList) throws AeException
   {
      return Collections.EMPTY_LIST;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServiceParameters(java.util.List)
    */
   public List getServiceParameters(List aPolicyList) throws AeException
   {

      return Collections.EMPTY_LIST;
   }

}
