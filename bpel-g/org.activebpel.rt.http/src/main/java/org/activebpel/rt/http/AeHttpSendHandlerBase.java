//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpSendHandlerBase.java,v 1.5 2008/03/28 17:55:40 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAePolicyConstants;

import org.activebpel.rt.http.handler.AeHttpDeleteHandler;
import org.activebpel.rt.http.handler.AeHttpGetHandler;
import org.activebpel.rt.http.handler.AeHttpPostHandler;
import org.activebpel.rt.http.handler.AeHttpPutHandler;
import org.activebpel.rt.http.handler.IAeHttpHandler;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * An abstract base class for HttpClient based HTTP senders
 * @see AeHttpServiceManager
 * @see AeHttpPostHandler
 * @see AeHttpGetHandler
 * @see AeHttpDeleteHandler
 * @see AeHttpPutHandler
 */
public abstract class AeHttpSendHandlerBase implements IAeHttpHandler, IAePolicyConstants
{

   /** the current http request information */
   private AeHttpRequest mHttpRequest;

   /** the next method handler in the chain of handlers */
   private AeHttpSendHandlerBase mSuccessor;

   /**
    * Constructor
    * @param aSuccessor
    */
   public AeHttpSendHandlerBase(AeHttpSendHandlerBase aSuccessor)
   {
      mSuccessor = aSuccessor;
   }

   /**
    * Start point of the chain, called by client or pre-node. Call handle() on this node, and decide whether
    * to continue the chain. If the next node is not null and this node did not handle the request, call
    * start() on next node to handle request.
    * @param aRequest the request parameter
    */
   public final IAeHttpHandler start(AeHttpRequest aRequest)
   {
      mHttpRequest = aRequest;
      IAeHttpHandler handler = null;

      boolean foundHandler = canHandle(aRequest);

      if ( foundHandler )
         return this;

      if ( mSuccessor != null && !foundHandler )
         handler = mSuccessor.start(aRequest);

      return handler;
   }

   /**
    * @return the httpRequest
    */
   protected AeHttpRequest getHttpRequest()
   {
      return mHttpRequest;
   }

   /**
    * @return the httpConfig
    * @throws AeException 
    */
   private AeHttpConfig getHttpConfig() throws AeException
   {
      return AeHttpRequest.getHttpServiceManager().getHttpConfig();
   }

   /**
    * Deliver request to peer consume and return the response
    * @param aMethod
    * @throws AeException
    */
   protected AeHttpResponse deliver(HttpMethod aMethod) throws AeException
   {
      try
      {
         // Create and configure the http client
         HttpClient httpClient = createClient(getHttpRequest().getOverrideProperties());

         // configure method overrides
         configureHttpMethod(aMethod, getHttpRequest().getHeaders());

         // deliver http transport
         int returnCode = httpClient.executeMethod(aMethod);

         AeHttpResponse response = new AeHttpResponse(returnCode, aMethod,getHttpRequest().getOverrideProperties());
         if ( response.isRedirect() )
         {
            URI redirectLocation = new URI((String)response.getHeaders().get(AeMimeUtil.REDIRECT_LOCATION_ATTRIBUTE), true);
            if ( getHttpRequest().isRedirectWithGET() && !(aMethod instanceof GetMethod) )
            {
               response = AeHttpRequest.getHttpServiceManager().send(getHttpRequest().createGETRedirectRequest(redirectLocation));
            }
            else
            {
               aMethod.setURI(redirectLocation);
               response = redirect(aMethod);
            }
         }
         else
            response.consume();

         return response;
      }
      catch (AeHttpException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new AeException(e);
      }
   }

   abstract protected AeHttpResponse redirect(HttpMethod aMethod) throws AeException;

   /**
    * @param aUri
    * @param aParams the LinkedHashMap of url parameters. values are a list to support duplicates.
    * @return the resolved url
    * @throws AeException
    */
   protected static String formUrl(String aUri, Map aParams, boolean aEncode) throws AeException
   {
      StringBuffer url = new StringBuffer(aUri);
      try
      {
         // Append optional params
         String delim = aUri.charAt(aUri.length() - 1) == '?' ? "" : "?"; //$NON-NLS-1$ //$NON-NLS-2$
         for (Iterator iterator = aParams.keySet().iterator(); iterator.hasNext();)
         {
            String paramName = (String)iterator.next();
            List valueList = (List)aParams.get(paramName);
            for (Iterator valueItr = valueList.iterator(); valueItr.hasNext();)
            {
               String value = (String)valueItr.next();
               url.append(delim).append(paramName);
               if ( AeUtil.notNullOrEmpty(value) )
                  url.append("=").append(aEncode ? AeUTF8Util.urlEncode(value) : value); //$NON-NLS-1$
               delim = "&"; //$NON-NLS-1$
            }
         }
         return url.toString();
      }
      catch (UnsupportedEncodingException ex)
      {
         throw new AeException(AeMessages.format("AeHttpSendHandlerBase.ERROR_UrlEncode", new Object[] { url.toString() }), ex); //$NON-NLS-1$
      }
   }

   /**
    * Create and configure an http client object
    * @return the http client with default configuration updated with the http service manager engine
    *         configurations
    * @throws AeException 
    */
   protected HttpClient createClient(Map aOverrideProperties) throws AeException
   {
      HttpClient client = new HttpClient(AeHttpServiceManager.getHttpConnectionManager());
      getHttpConfig().configureClient(client, aOverrideProperties);
      return client;
   }

   /**
    * Convenience method to configure an http method object
    * @param aMethod
    * @param aHeaders
    */
   protected void configureHttpMethod(HttpMethod aMethod, Map aHeaders)
   {
      // set http headers
      for (Iterator hItr = aHeaders.entrySet().iterator(); hItr.hasNext();)
      {
         Map.Entry header = (Map.Entry)hItr.next();
         aMethod.setRequestHeader((String)header.getKey(), (String)header.getValue());
      }
   }
}
