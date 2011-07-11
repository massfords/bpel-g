// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeMasterPolicyMapper.java,v 1.9 2008/01/14 21:27:46 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Policy mapper that calls each configured mapper in turn to provide a complete set of Handlers for policy
 * assertions
 */
public class AeMasterPolicyMapper implements IAePolicyMapper, IAeWsddConstants
{
   private static final String WSA_HEADER_HANDLER = "proc:org.activebpel.rt.axis.bpel.handlers.AeWsaHeaderHandler"; //$NON-NLS-1$
   
   private List<IAePolicyMapper> mMappers;

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerRequestHandlers(java.util.List)
    */
   public List<Element> getServerRequestHandlers(List<Element> aPolicyList) throws AeException
   {
      List<Element> handlers = new ArrayList<Element>();

      if (AeUtil.notNullOrEmpty(aPolicyList))
      {
         for (Iterator<IAePolicyMapper> it = mMappers.iterator(); it.hasNext();)
         {
        	 IAePolicyMapper policyMapper = it.next();
            try
            {
               handlers.addAll(policyMapper.getServerRequestHandlers(aPolicyList));
            }
            catch (Exception e)
            {
               throw new AeException(
                     AeMessages.getString("AePolicyMapper.Error_0") + policyMapper.getClass().getName(), e); //$NON-NLS-1$
            }
         }
      }
      
      // Add the WSA Must-understand handler to all bpel processes
      Document doc = AeXmlUtil.newDocument();
      Element handler = doc.createElementNS( WSDD_NAMESPACE_URI, TAG_HANDLER );
      handler.setAttribute(IAePolicyConstants.TAG_TYPE_ATTR, WSA_HEADER_HANDLER);
      handlers.add(handler);
            
      return handlers;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerResponseHandlers(java.util.List)
    */
   public List<Element> getServerResponseHandlers(List<Element> aPolicyList) throws AeException
   {
      List<Element> handlers = new ArrayList<Element>();

      if (AeUtil.notNullOrEmpty(aPolicyList))
      {
         for (Iterator<IAePolicyMapper> it = mMappers.iterator(); it.hasNext();)
         {
        	 IAePolicyMapper policyMapper = it.next();
            try
            {
               handlers.addAll(policyMapper.getServerResponseHandlers(aPolicyList));
            }
            catch (Exception e)
            {
               throw new AeException(
                     AeMessages.getString("AePolicyMapper.Error_0") + policyMapper.getClass().getName(), e); //$NON-NLS-1$
            }
         }
      }

      return handlers;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerResponseHandlers(java.util.List)
    */
   public List<Element> getClientRequestHandlers(List<Element> aPolicyList) throws AeException
   {
      List<Element> handlers = new ArrayList<Element>();

      if (AeUtil.notNullOrEmpty(aPolicyList))
      {
         for (Iterator<IAePolicyMapper> it = mMappers.iterator(); it.hasNext();)
         {
        	 IAePolicyMapper policyMapper = it.next();
            try
            {
               handlers.addAll(policyMapper.getClientRequestHandlers(aPolicyList));
            }
            catch (Exception e)
            {
               throw new AeException(
                     AeMessages.getString("AePolicyMapper.Error_0") + policyMapper.getClass().getName(), e); //$NON-NLS-1$
            }
         }
      }

      return handlers;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getClientResponseHandlers(java.util.List)
    */
   public List<Element> getClientResponseHandlers(List<Element> aPolicyList) throws AeException
   {
      List<Element> handlers = new ArrayList<Element>();

      if (AeUtil.notNullOrEmpty(aPolicyList))
      {
         for (Iterator<IAePolicyMapper> it = mMappers.iterator(); it.hasNext();)
         {
        	 IAePolicyMapper policyMapper = it.next();
            try
            {
               handlers.addAll(policyMapper.getClientResponseHandlers(aPolicyList));
            }
            catch (Exception e)
            {
               throw new AeException(
                     AeMessages.getString("AePolicyMapper.Error_0") + policyMapper.getClass().getName(), e); //$NON-NLS-1$
            }
         }
      }
      return handlers;
   }

   /**
    * 
    * Overrides method to get service parameters from policy 
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServiceParameters(java.util.List)
    */
   public List<Element> getServiceParameters(List<Element> aPolicyList) throws AeException
   {
      List<Element> handlers = new ArrayList<Element>();

      if (AeUtil.notNullOrEmpty(aPolicyList))
      {
         for (Iterator<IAePolicyMapper> it = mMappers.iterator(); it.hasNext();)
         {
        	 IAePolicyMapper policyMapper = it.next();
            try
            {
               handlers.addAll(policyMapper.getServiceParameters(aPolicyList));
            }
            catch (Exception e)
            {
               throw new AeException(
                     AeMessages.getString("AePolicyMapper.Error_0") + policyMapper.getClass().getName(), e); //$NON-NLS-1$
            }
         }
      }

      return handlers;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getCallProperties(java.util.List)
    */
   public Map<String, Object> getCallProperties(List<Element> aPolicyList) throws AeException
   {
      Map<String, Object> map = new HashMap<String, Object>();
      
      if (AeUtil.notNullOrEmpty(aPolicyList))
      {
         for (Iterator<IAePolicyMapper> it = mMappers.iterator(); it.hasNext();)
         {
        	IAePolicyMapper policyMapper = it.next();
            try
            {
               Map<String, Object> policyMap = policyMapper.getCallProperties(aPolicyList);
               if (policyMap != null)
                    map.putAll(policyMap);
            }
            catch (Exception e)
            {
               throw new AeException(
                     AeMessages.getString("AePolicyMapper.Error_0") + policyMapper.getClass().getName(), e); //$NON-NLS-1$
            }
         }
      }
      return map;
   }

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getDeploymentHandler(java.util.List)
    */
   public String getDeploymentHandler(List<Element> aPolicyList) throws AeException
   {
      String handler = null;
      if (AeUtil.notNullOrEmpty(aPolicyList))
      {
         for (Iterator<IAePolicyMapper> it = mMappers.iterator(); it.hasNext();)
         {
            IAePolicyMapper policyMapper = it.next();
            try
            {
               String newhandler = policyMapper.getDeploymentHandler(aPolicyList);
               if (!AeUtil.isNullOrEmpty(newhandler))
               {
                  if (!AeUtil.isNullOrEmpty(handler) && !newhandler.equals(handler))
                  {
                     // throw an exception if more than one comes back
                     String[] args = new String[] {handler, newhandler};
                     throw new AeException(AeMessages.format("AeMasterPolicyMapper.0", args)); //$NON-NLS-1$
                  }
                  else
                  {
                     handler = newhandler;
                  }
               }
            }
            catch (Exception e)
            {
               throw new AeException(
                     AeMessages.getString("AePolicyMapper.Error_0") + policyMapper.getClass().getName(), e); //$NON-NLS-1$
            }
         }
      }
      return handler;
   }
   
   /**
    * @param aMapper
 	*/
   public void setMappers(List<IAePolicyMapper> aMapper) {
	   mMappers = aMapper;
   }
}
