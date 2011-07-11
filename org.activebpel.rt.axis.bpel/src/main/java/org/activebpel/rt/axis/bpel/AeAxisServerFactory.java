//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/AeAxisServerFactory.java,v 1.2 2007/08/02 19:54:23 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel;

import java.net.URL;
import java.util.logging.Logger;

import org.activebpel.rt.axis.AeAxisEngineConfiguration;
import org.activebpel.rt.axis.bpel.deploy.AeResourceProvider;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.apache.axis.AxisFault;
import org.apache.axis.server.AxisServer;
/**
 *   Factory class to return the static Axis server to either the AxisServlet (http)
 *   or JMS listener (or any other listener type for that matter) 
**/
public class AeAxisServerFactory
{

   /* the default axis server configuration file name */
   private static final String DEFAULT_AXIS_SERVER_CONFIG = "ae-server-config.wsdd"; //$NON-NLS-1$
 
   /* the default axis client configuration file name */
   private static final String DEFAULT_AXIS_CLIENT_CONFIG = "ae-client-config.wsdd"; //$NON-NLS-1$

   /* the axis server instance */
   private static AxisServer sAxisServer = null;
   
   /** for deployment logging purposes */
   private static final Logger log = Logger.getLogger("ActiveBPEL"); //$NON-NLS-1$
   
   /**
    *  Get AxisServer instance
    *  Initialize the Axis Server if necessary.
    */
   public static AxisServer getAxisServer() throws AxisFault 
   {
      if (sAxisServer == null)
      {
         initAxisServer();
      }
      return sAxisServer;
   }   
   
   /**
    * Initialize the Axis Server.  Short return if already initialized
    * @throws AxisFault
    */
   private static synchronized void initAxisServer() throws AxisFault
   {
      if (sAxisServer == null)
      {
         // find the global config file in classpath
         URL resource = AeUtil.findOnClasspath( DEFAULT_AXIS_SERVER_CONFIG, AeAxisServerFactory.class );
         
         // if no configuration for axis has been found then throw an exception
         if (resource == null)
         {
            throw new AxisFault(AeMessages.getString("AeAxisServerFactory.ERROR_14") +  DEFAULT_AXIS_SERVER_CONFIG); //$NON-NLS-1$
         }
   
         // construct a configuration using our own provider so it can create 
         // our deployment type which is aware of classloader issues
         AeResourceProvider serverConfiguration = new AeResourceProvider(resource);
         sAxisServer = new AxisServer(serverConfiguration);
         log.info(AeMessages.getString("AeAxisServerFactory.15")); //$NON-NLS-1$
         
         // Initialize the axis client server
         AeAxisEngineConfiguration.loadConfig(DEFAULT_AXIS_CLIENT_CONFIG);
         log.info(AeMessages.getString("AeAxisServerFactory.16")); //$NON-NLS-1$
      }
   }
}
