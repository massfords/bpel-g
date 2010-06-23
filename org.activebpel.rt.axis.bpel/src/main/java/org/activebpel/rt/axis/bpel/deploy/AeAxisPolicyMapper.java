// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/deploy/AeAxisPolicyMapper.java,v 1.3 2007/02/13 15:44:47 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.deploy;

import java.util.List;
import java.util.HashSet;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.server.deploy.IAePolicyMapper;
import org.activebpel.rt.bpel.server.deploy.IAeWsddConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Abstract class to help map Axis specific details of deploying policy for a web service.
 */
public abstract class AeAxisPolicyMapper implements IAePolicyMapper, IAeWsddConstants, IAePolicyConstants 
{
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerRequestHandlers(java.util.List)
    */
   abstract public List getServerRequestHandlers( List aPolicyList ) throws AeException;

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getServerResponseHandlers(java.util.List)
    */
   abstract public List getServerResponseHandlers( List aPolicyList ) throws AeException;
   
   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getClientRequestHandlers(java.util.List)
    */
   abstract public List getClientRequestHandlers( List aPolicyList ) throws AeException;

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getClientResponseHandlers(java.util.List)
    */
   abstract public List getClientResponseHandlers( List aPolicyList ) throws AeException;

   /**
    * @see org.activebpel.rt.bpel.server.deploy.IAePolicyMapper#getDeploymentHandler(java.util.List)
    */
   public String getDeploymentHandler(List aPolicyList) throws AeException
   {
      return null;
   }
   
   /**
    * Utility method for creating a NS aware handler element.
    * @param doc DOM Document
    * @param typeName handler type
    * @param handlerElements set of handler parameters 
    * @return ns aware dom element
    */
   protected Element createHandlerElement( Document doc, String typeName, HashSet handlerElements )
   {
      
         Element mHandler = doc.createElementNS( WSDD_NAMESPACE_URI, TAG_HANDLER );
         mHandler.setAttribute(IAePolicyConstants.TAG_TYPE_ATTR, typeName);
      
      return mHandler;
   }

   /**
    * Utility method for creating a NS aware parameter element.
    * @param doc DOM document
    * @param aName parameter name
    * @param aValue parameter value
    * @return ns aware dom element
    */
   protected Element createParameterElement( Document doc, String aName, String aValue )
   {
      Element element = doc.createElementNS( WSDD_NAMESPACE_URI, TAG_PARAMETER );
      element.setAttribute(IAePolicyConstants.TAG_NAME_ATTR, aName);
      element.setAttribute(IAePolicyConstants.TAG_VALUE_ATTR, aValue);
      return element;
   }

   
}
