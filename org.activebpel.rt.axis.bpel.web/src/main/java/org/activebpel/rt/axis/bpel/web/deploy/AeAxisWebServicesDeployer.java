// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel.web/src/org/activebpel/rt/axis/bpel/web/deploy/AeAxisWebServicesDeployer.java,v 1.10 2008/02/17 21:33:30 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.web.deploy;

import java.util.Map;

import org.activebpel.rt.axis.bpel.deploy.AeAxisWebServicesDeployerBase;
import org.activebpel.rt.axis.bpel.web.AeProcessEngineServlet;
import org.apache.axis.server.AxisServer;

/**
 * WebServicesDeployer impl that deploys web services to Axis running
 * under a servlet. 
 */
public class AeAxisWebServicesDeployer extends AeAxisWebServicesDeployerBase
{

   /**
    * Constructor.
    */
   public AeAxisWebServicesDeployer()
   {
      super(null);
   }   
   
   /**
    * Constructor.
    * @param aConfig
    */
   public AeAxisWebServicesDeployer(Map aConfig)
   {
      super(aConfig);
   }   
   
   /**
    * Implements method by returning the axis server associated with the process engine servlet. 
    * @see org.activebpel.rt.axis.bpel.deploy.AeAxisWebServicesDeployerBase#getAxisServer()
    */
   protected AxisServer getAxisServer()
   {
      return AeProcessEngineServlet.getAxisServer();
   }
}
