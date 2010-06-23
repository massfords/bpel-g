// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/providers/AeBpelPolicyProvider.java,v 1.2 2006/09/26 15:15:07 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.providers;

import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.axis.bpel.handlers.AeBpelDocumentHandler;
import org.activebpel.rt.axis.bpel.handlers.AeBpelHandler;
import org.activebpel.rt.axis.bpel.handlers.IAePolicyHandler;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentException;
import org.activebpel.rt.bpel.server.deploy.IAeWsddConstants;
import org.activebpel.rt.util.AeUtil;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.deployment.wsdd.WSDDService;

/**
 * Defines a provider for the BPEL Policy invocations.
 */
public class AeBpelPolicyProvider extends WSDDProvider
{
   /**
    * @see org.apache.axis.deployment.wsdd.WSDDProvider#newProviderInstance(org.apache.axis.deployment.wsdd.WSDDService, org.apache.axis.EngineConfiguration)
    */
   public Handler newProviderInstance(WSDDService service, EngineConfiguration registry) throws Exception
   {
      String handlerClass = service.getParameter(IAePolicyConstants.PARAM_HANDLER_CLASS);
      if (AeUtil.isNullOrEmpty(handlerClass))
      {
         AeDeploymentException.logError(null, AeMessages.format("AeBpelPolicyProvider.0", service.getServiceDesc().getName())); //$NON-NLS-1$
         return new AeBpelDocumentHandler();
      }
      IAePolicyHandler handler = (IAePolicyHandler) Class.forName(handlerClass).newInstance();
      handler.init(service, registry); 
      return (AeBpelHandler) handler;
   }

   /**
    * @see org.apache.axis.deployment.wsdd.WSDDProvider#getName()
    */
   public String getName()
   {
      return IAeWsddConstants.NAME_POLICY_BINDING;
   }
}
