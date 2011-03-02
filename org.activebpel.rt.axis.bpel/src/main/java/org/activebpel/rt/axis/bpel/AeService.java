// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeService.java,v 1.4 2007/08/02 19:54:23 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel;

import org.activebpel.rt.axis.AeAxisEngineConfiguration;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.client.Service;

/**
 * This is a simple extension of the Axis Service object.
 */
public class AeService extends Service
{
   /** cached copy of the AxisClient to avoid creating one-off instances for each invoke */
   private static AxisClient sClient = null;
   
   /**
    * @see org.apache.axis.client.Service#getEngineConfiguration()
    */
   protected EngineConfiguration getEngineConfiguration() 
   {
      return new AeAxisEngineConfiguration();
   }
   
   /**
    * Overrides the base class to return a cached instance of the client.
    * 
    * @see org.apache.axis.client.Service#getAxisClient()
    */
   protected AxisClient getAxisClient() 
   {
      if (sClient == null)
      {
         synchronized(AeService.class)
         {
            if (sClient == null)
            {
               sClient = super.getAxisClient(); 
            }
         }
      }
      return sClient;
   }
}
