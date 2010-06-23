//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpServiceManager.java,v 1.2 2008/03/26 15:42:56 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeConfigChangeListener;
import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpel.impl.AeManagerAdapter;
import org.activebpel.rt.http.handler.IAeHttpHandler;


import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;

/**
 * An Http Service Manager
 */
public class AeHttpServiceManager extends AeManagerAdapter implements IAeHttpServiceManager, IAeConfigChangeListener
{

   /** http service config data. */
   private AeHttpConfig mHttpConfig;

   private AeHttpSendHandlerBase mInitialHandler;

   /** the cached http connection pool manager */
   private static MultiThreadedHttpConnectionManager sHttpConnectionManager;

   /** timeout thread responsible for closing idle connections */
   private static IdleConnectionTimeoutThread sConnectionTimeoutThread;

   /**
    * Constructor The constructor for the http manager which receives a configuration map as input.
    * @param aConfig The engine configuration map.
    */
   public AeHttpServiceManager(Map aConfig)
   {
      super();
      mHttpConfig = new AeHttpConfig(aConfig);
   }

   /**
    * @see org.activebpel.rt.http.IAeHttpServiceManager#send(org.activebpel.rt.http.AeHttpRequest)
    */
   public AeHttpResponse send(AeHttpRequest aRequest) throws AeException
   {
      AeHttpResponse response = getHandler(aRequest).handle();
      if ( response == null )
         throw new AeException(AeMessages.format("AeHttpServiceManager.ERROR_InvalidResponse", new Object[] { aRequest.getMethodName() })); //$NON-NLS-1$

      return response;
   }

   /**
    * @return the defaultHandler
    * @throws AeException
    */
   private IAeHttpHandler getHandler(AeHttpRequest aRequest) throws AeException
   {
      IAeHttpHandler handler = mInitialHandler.start(aRequest);
      if ( handler == null )
         throw new AeException(AeMessages.format("AeHttpServiceManager.ERROR_NoHandlerConfigured", new Object[] { aRequest.getMethodName() })); //$NON-NLS-1$

      return handler;
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeConfigChangeListener#updateConfig(org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig)
    */
   public void updateConfig(IAeUpdatableEngineConfig aConfig)
   {
      AeHttpConfig httpConfig = AeHttpConfig.getFromConfig(aConfig);
      setHttpConfig(httpConfig);
   }

   /**
    * @return the httpConnectionManager
    */
   protected static HttpConnectionManager getHttpConnectionManager()
   {
      return sHttpConnectionManager;
   }

   /**
    * @return the connectionTimeoutThread
    */
   protected static IdleConnectionTimeoutThread getConnectionTimeoutThread()
   {
      return sConnectionTimeoutThread;
   }

   /**
    * @see org.activebpel.rt.http.IAeHttpServiceManager#getHttpConfig()
    */
   public AeHttpConfig getHttpConfig()
   {
      return mHttpConfig;
   }

   /**
    * @param aHttpConfig the httpConfig to set
    */
   protected void setHttpConfig(AeHttpConfig aHttpConfig)
   {
      mHttpConfig = aHttpConfig;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#start()
    */
   public void start() throws Exception
   {
      getHttpConfig().initDefaults();

      // Create a MultiThreadedHttpConnectionManager and Idle connection monitor.
      // This connection manager will be used for all HttpClient instances.

      sHttpConnectionManager = new MultiThreadedHttpConnectionManager();

      sConnectionTimeoutThread = new IdleConnectionTimeoutThread();
      sConnectionTimeoutThread.addConnectionManager(sHttpConnectionManager);

      getHttpConfig().configureConnection();

      // setup handler chain
      mInitialHandler = getHttpConfig().initHandlerChain();

      // start monitoring connections
      sConnectionTimeoutThread.start();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeManagerAdapter#stop()
    */
   public void stop()
   {
      if ( sHttpConnectionManager != null )
      {
         sHttpConnectionManager.shutdown();
         sHttpConnectionManager = null;
      }

      if ( sConnectionTimeoutThread != null )
      {
         sConnectionTimeoutThread.shutdown();
         sConnectionTimeoutThread.interrupt();
         sConnectionTimeoutThread = null;
      }
   }

}
