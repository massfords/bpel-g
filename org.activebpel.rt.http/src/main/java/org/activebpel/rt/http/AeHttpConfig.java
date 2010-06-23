//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpConfig.java,v 1.3 2008/02/17 21:54:15 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;

import org.activebpel.rt.config.AeConfigurationUtil;
import org.activebpel.rt.http.handler.AeHttpPostHandler;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpConnectionParams;

/**
 * Convenience wrapper around the engine http service configuration to get and set well known properties.
 */
public class AeHttpConfig
{

   // ---------------- Configuration entry keys ------------------------------
   /** Configuration entry key name for http service. */
   public static String HTTP_SERVICE_MANAGER_ENTRY = "HttpServiceManager"; //$NON-NLS-1$

   /** Configuration entry key name for http service method handlers. */
   public static String HTTP_METHOD_HANDLERS_ENTRY = "MethodHandlers"; //$NON-NLS-1$

   public static final String IDLE_CONNECTION_TIMEOUT_ENTRY = "IdleConnectionTimeout"; //$NON-NLS-1$

   public static final String IDLE_CONNECTION_SWEEP_INTERVAL_ENTRY = "IdleConnectionSweepinterval"; //$NON-NLS-1$

   public static final String DEFAULT_MAX_CONNECTIONS_ENTRY = "DefaultMaxConnections"; //$NON-NLS-1$

   public static final String DEFAULT_MAX_CONNECTIONS_PER_HOST_ENTRY = "DefaultMaxConnectionsPerHost"; //$NON-NLS-1$

   public static final String CONTENT_CHARSET_ENTRY = "ContentCharset"; //$NON-NLS-1$

   public static final String LINGER_ENTRY = "Linger"; //$NON-NLS-1$

   /** Configuration entry key name for Http GET method handler */
   public static final String GET_ENTRY = "GET"; //$NON-NLS-1$

   /** Configuration entry key name for Http POST method */
   public static final String POST_ENTRY = "POST"; //$NON-NLS-1$

   /** Configuration entry key name for Http PUT method */
   public static final String PUT_ENTRY = "PUT"; //$NON-NLS-1$

   /** Configuration entry key name for Http DELETE method */
   public static final String DELETE_ENTRY = "DELETE"; //$NON-NLS-1$

   /** Configuration entry key name for Http TRACE method */
   public static final String TRACE_ENTRY = "TRACE"; //$NON-NLS-1$

   /** Configuration entry key name for Http OPTIONS method */
   public static final String OPTIONS_ENTRY = "OPTIONS"; //$NON-NLS-1$

   // -------- Configuration default values --------------------------
   private static String[][] defaults = new String[][] { { IDLE_CONNECTION_TIMEOUT_ENTRY, "5000" }, //$NON-NLS-1$
         { IDLE_CONNECTION_SWEEP_INTERVAL_ENTRY, "3000" }, //$NON-NLS-1$
         { DEFAULT_MAX_CONNECTIONS_ENTRY, "500" }, //$NON-NLS-1$
         { DEFAULT_MAX_CONNECTIONS_PER_HOST_ENTRY, "100" }, //$NON-NLS-1$
         { CONTENT_CHARSET_ENTRY, AeUTF8Util.UTF8_ENCODING },
         { LINGER_ENTRY, "-1" } //$NON-NLS-1$
   };

   private static String[][] defaultMethodHandlers = new String[][] { { GET_ENTRY, "org.activebpel.rt.http.handler.AeHttpGetHandler" }, //$NON-NLS-1$
         { POST_ENTRY, "org.activebpel.rt.http.handler.AeHttpPostHandler" }, //$NON-NLS-1$
         { PUT_ENTRY, "org.activebpel.rt.http.handler.AeHttpPutHandler" }, //$NON-NLS-1$
         { OPTIONS_ENTRY, "org.activebpel.rt.http.handler.AeHttpOptionsHandler" }, //$NON-NLS-1$
         { TRACE_ENTRY, "org.activebpel.rt.http.handler.AeHttpTraceHandler" }, //$NON-NLS-1$
         { DELETE_ENTRY, "org.activebpel.rt.http.handler.AeHttpDeleteHandler" } //$NON-NLS-1$
   };

   /**
    * Fallback handler to use when the chain is not configured in the engine configuration.
    */
   protected final AeHttpSendHandlerBase FALLBACK_HTTP_HANDLER = new AeHttpPostHandler(null);

   /** Configuration entry key name for http service method handlers. */
   public static String DEFAULT_ATTACHMENT_ID = "aerest-1"; //$NON-NLS-1$

   /** Prefix of temp attachment file name. */
   public static String TEMP_FILE_PREFIX = "aerest_"; //$NON-NLS-1$

   /** Postfix of temp attachment file name. */
   public static String TEMP_FILE_POSTFIX = ".bin"; //$NON-NLS-1$

   /** Parent entry containing http service config entry settings. */
   private Map mMap;

   /**
    * Constructor
    */
   public AeHttpConfig(Map aHttpConfig)
   {
      setMap(aHttpConfig);
   }

   /**
    * Initialize the http method handlers (Chain of Responsibilities). The order of responsibilities is in the
    * order of the entries in the engine config file
    * @return the head of the chain handler.
    * @throws AeException
    */
   public AeHttpSendHandlerBase initHandlerChain() throws AeException
   {

      AeHttpSendHandlerBase predecessor = null;
      if ( getMap().containsKey(HTTP_METHOD_HANDLERS_ENTRY) )
      {
         List chain = new LinkedList();
         Map handlers = (Map)getMap().get(HTTP_METHOD_HANDLERS_ENTRY);
         for (Iterator iterator = handlers.keySet().iterator(); iterator.hasNext();)
         {

            String method = (String)iterator.next();
            String clazz = ((String)handlers.get(method));
            // add in reverse order
            chain.add(0, clazz);
         }

         for (Iterator iterator = chain.iterator(); iterator.hasNext();)
         {
            String methodHandlerClass = (String)iterator.next();
            try
            {
               Class clazz = Class.forName(methodHandlerClass);
               ArrayList classList = new ArrayList();
               classList.add(AeHttpSendHandlerBase.class);

               Constructor cons = clazz.getConstructor((Class[])classList.toArray(new Class[classList.size()]));
               Object[] intArgs;
               if ( predecessor == null )
                  intArgs = new Object[] { null };
               else
                  intArgs = new Object[] { predecessor };
               predecessor = (AeHttpSendHandlerBase)cons.newInstance(intArgs);
            }
            catch (Throwable ex)
            {
               throw new AeException(AeMessages.getString("AeHttpConfig.ERROR_CoR"), ex); //$NON-NLS-1$
            }
         }

      }
      if ( predecessor == null )
      {
         predecessor = FALLBACK_HTTP_HANDLER;

         AeException.logWarning(AeMessages.format(
               "AeHttpConfig.WARNING_Handler_Not_Configured", new Object[] { HTTP_METHOD_HANDLERS_ENTRY, FALLBACK_HTTP_HANDLER.getClass().getName() })); //$NON-NLS-1$
      }
      return predecessor;
   }

   /**
    * Initialize config map with defaults
    */
   protected void initDefaults()
   {

      for (int i = 0; i < defaults.length; i++)
      {
         String[] entry = defaults[i];

         if ( !getMap().containsKey(entry[0]) )
         {
            getMap().put(entry[0], entry[1]);
         }
      }

      // default method handlers
      if ( !getMap().containsKey(HTTP_METHOD_HANDLERS_ENTRY) )
         getMap().put(HTTP_METHOD_HANDLERS_ENTRY, new LinkedHashMap());

      Map handlers = ((Map)getMap().get(HTTP_METHOD_HANDLERS_ENTRY));
      for (int i = 0; i < defaultMethodHandlers.length; i++)
      {
         String[] entry = defaultMethodHandlers[i];

         if ( !handlers.containsKey(entry[0]) )
         {
            handlers.put(entry[0], entry[1]);
         }
      }
   }

   /**
    * Extracts the http config values from the engine config
    * @param aEngineConfig
    */
   public static AeHttpConfig getFromConfig(IAeUpdatableEngineConfig aEngineConfig)
   {
      Map httpServiceMap = getHttpMapEntryCopy(aEngineConfig);
      Map tmp = new HashMap();
      tmp.putAll(httpServiceMap);
      return new AeHttpConfig(tmp);
   }

   /**
    * Sets the http config values on the engine config
    * @param aConfig
    * @param aEngineConfig
    */
   public static void setOnConfig(AeHttpConfig aConfig, IAeUpdatableEngineConfig aEngineConfig)
   {
      Map httpServiceMap = getHttpMapEntry(aEngineConfig);
      httpServiceMap.clear();
      httpServiceMap.putAll(aConfig.getMap());
   }

   /**
    * @return the config
    */
   public Map getMap()
   {
      if ( mMap == null )
         mMap = new LinkedHashMap();
      return mMap;
   }

   /**
    * @param aConfig the config to set
    */
   public void setMap(Map aConfig)
   {
      mMap = aConfig;
   }

   /**
    * Returns a deep copy of the config entry map containing http service settings.
    * @return copy of the http service.
    */
   protected static Map getHttpMapEntryCopy(IAeUpdatableEngineConfig aEngineConfig)
   {
      Map httpServiceMap = getHttpMapEntry(aEngineConfig);
      Map tmp = new HashMap();
      AeConfigurationUtil.copyEntries(httpServiceMap, tmp);
      return tmp;
   }

   /**
    * Returns map containing http service settings.
    */
   protected static Map getHttpMapEntry(IAeUpdatableEngineConfig aConfig)
   {
      // TODO (MF) The exact path into the config should be passed to the manager when it loads so we don't
      // get out of sync
      // Map path: //CustomManagers/HttpServiceManager
      Map customManagersMap = aConfig.getMapEntry(IAeEngineConfiguration.CUSTOM_MANAGERS_ENTRY, true);
      Map httpServiceMap = aConfig.getMapEntry(HTTP_SERVICE_MANAGER_ENTRY, true, customManagersMap);
      return httpServiceMap;
   }

   protected void configureConnection() throws AeException
   {
      AeHttpServiceManager.getHttpConnectionManager().getParams().setDefaultMaxConnectionsPerHost(
            AeUtil.getNumeric((String)getMap().get(DEFAULT_MAX_CONNECTIONS_PER_HOST_ENTRY)));

      AeHttpServiceManager.getHttpConnectionManager().getParams()
            .setMaxTotalConnections(AeUtil.getNumeric((String)getMap().get(DEFAULT_MAX_CONNECTIONS_ENTRY)));

      int sweepInterval = AeUtil.getNumeric((String)getMap().get(IDLE_CONNECTION_SWEEP_INTERVAL_ENTRY));
      AeHttpServiceManager.getConnectionTimeoutThread().setTimeoutInterval(sweepInterval);

      int idleTimeout = AeUtil.getNumeric((String)getMap().get(IDLE_CONNECTION_TIMEOUT_ENTRY));
      AeHttpServiceManager.getConnectionTimeoutThread().setConnectionTimeout(idleTimeout);

   }

   /**
    * Convenience method to configure an http client object
    * @param aClient
    * @param aOverrideProperties
    */
   protected void configureClient(HttpClient aClient, Map aOverrideProperties)
   {
      
      // TODO (JB) HTTP add cookie and proxy support for soap

      // Client connection timeout - timeout for initial connection
      Object connectionTimeout = aOverrideProperties.get(IAePolicyConstants.ATTR_HTTP_CLIENT_CONNECTION_TIMEOUT);
      if ( connectionTimeout != null )
      {
         aClient.getHttpConnectionManager().getParams().setConnectionTimeout(((Integer)connectionTimeout).intValue());
      }

      // Connection manager timeout - the timeout value for allocation of connections from the pool
      Object connectionManagerTimeout = aOverrideProperties.get(IAePolicyConstants.ATTR_HTTP_CLIENT_CONNECTION_TIMEOUT);
      if ( connectionManagerTimeout != null )
      {
         aClient.getParams().setConnectionManagerTimeout((((Long)connectionManagerTimeout).longValue()));
      }

      // Preemptive
      String preemptive = (String)aOverrideProperties.get(IAePolicyConstants.TAG_ASSERT_AUTH_PREEMPTIVE);
      if ( AeUtil.notNullOrEmpty(preemptive) )
      {
         aClient.getParams().setAuthenticationPreemptive("true".equalsIgnoreCase(preemptive)); //$NON-NLS-1$
      }

      // socket timeout - timeout for blocking reads
      Object socketTimeout = aOverrideProperties.get(IAePolicyConstants.ATTR_HTTP_SOCKET_TIMEOUT);
      if ( socketTimeout != null )
      {
         aClient.getParams().setSoTimeout(((Integer)socketTimeout).intValue());
      }

      // TCP_NODELAY
      Object tcpNoDelay = aOverrideProperties.get(IAePolicyConstants.ATTR_HTTP_TCP_NODELAY);
      if ( tcpNoDelay != null )
      {
         aClient.getParams().setBooleanParameter(HttpConnectionParams.TCP_NODELAY, ((Boolean)tcpNoDelay).booleanValue());
      }

      // Credentials
      String user = (String)aOverrideProperties.get(IAePolicyConstants.TAG_ASSERT_AUTH_USER);
      if ( AeUtil.notNullOrEmpty(user) )
      {
         // Authentication required, pass our credentials to HttpClient.
         aClient.getState().setCredentials(AuthScope.ANY,
               new UsernamePasswordCredentials(user, (String)aOverrideProperties.get(IAePolicyConstants.TAG_ASSERT_AUTH_PASSWORD)));
      }

   }
}
