// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAePolicyMapper.java,v 1.3 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.w3c.dom.Element;

/**
 * Handle the platform specific details of deploying policy for a web service.
 */
public interface IAePolicyMapper
{

   /**
    * Maps policy assertions to service parameter definitions.
    * @param aPolicyList A list of policies to be mapped to the service
    * @return List of parameter definitions
    * @throws AeException
    */
   public List<Element> getServiceParameters( List<Element> aPolicyList )
   throws AeException;
      
   /**
    * Maps sender side policy assertions to handler chain definitions.
    * @param aPolicyList A list of policies to be mapped to the service request flow
    * @return List of handler definitions
    * @throws AeException
    */
   public List<Element> getClientRequestHandlers( List<Element> aPolicyList )
   throws AeException;

   /**
    * Maps receiver side policy assertions to handler defintions.
    * @param aPolicyList A list of policies to be mapped to the service request flow
    * @return List of handler definitions
    * @throws AeException
    */
   public List<Element> getClientResponseHandlers( List<Element> aPolicyList )
   throws AeException;
   
   
   /**
    * Maps receiver side policy assertions to wsdd deployment document.
    * @param aPolicyList A list of policies to be mapped to the service request flow
    * @return List of handler definitions
    * @throws AeException
    */
   public List<Element> getServerRequestHandlers( List<Element> aPolicyList )
   throws AeException;

   /**
    * Maps sender side policy assertions to wsdd deployment document.
    * @param aPolicyList A list of policies to be mapped to the service response flow
    * @return List of handler definitions
    * @throws AeException
    */
   public List<Element> getServerResponseHandlers( List<Element> aPolicyList )
   throws AeException;
   
   /**
    * Maps client policy assertions to property name/value pairs.
    * @param aPolicyList A list of policies to be mapped
    * @return Map of property name/value pairs
    * @throws AeException
    */
   public Map<String,String> getCallProperties( List<Element> aPolicyList )
   throws AeException;
   
   /**
    * Determines the appropriate deployment handler key from policy assertions.
    * If no specific deployment handler is determined from policies, implementations
    * should return null.
    * 
    * @param aPolicyList
    * @return deployment handler key
    * @throws AeException
    */
   public String getDeploymentHandler(List<Element> aPolicyList) throws AeException;
   
}
