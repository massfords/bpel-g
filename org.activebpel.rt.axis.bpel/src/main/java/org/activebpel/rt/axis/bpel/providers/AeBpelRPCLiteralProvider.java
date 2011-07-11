// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/providers/AeBpelRPCLiteralProvider.java,v 1.2 2005/10/05 18:51:30 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.providers;

import org.activebpel.rt.bpel.server.deploy.IAeWsddConstants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDProvider;
import org.apache.axis.deployment.wsdd.WSDDService;

/**
 * Defines a provider for the BPEL RPC invocations.
 */
public class AeBpelRPCLiteralProvider extends WSDDProvider
{
   /**
    * @see org.apache.axis.deployment.wsdd.WSDDProvider#newProviderInstance(org.apache.axis.deployment.wsdd.WSDDService, org.apache.axis.EngineConfiguration)
    */
   public Handler newProviderInstance(WSDDService service, EngineConfiguration registry) throws Exception
   {
      return new org.activebpel.rt.axis.bpel.handlers.AeBpelRPCLiteralHandler();
   }

   /**
    * @see org.apache.axis.deployment.wsdd.WSDDProvider#getName()
    */
   public String getName()
   {
      return IAeWsddConstants.NAME_RPC_LIT_BINDING;
   }
}
