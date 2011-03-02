// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/graph/AeGraphImageServlet.java,v 1.11 2008/02/17 21:43:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.graph;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.graph.AeGraphProperties;
import org.activebpel.rt.util.AeUtil;

/**
 * Servlet responsible for drawing the BPEL graph on a off screen graphics context
 * and serving the rendered image content.
 */
public class AeGraphImageServlet extends HttpServlet
{
   /** Key used to look up the http servlet session for the model cache. */
   private static String CACHE_MODEL_SESSION_KEY = "org.activebpel.rt.bpeladmin.war.web.graph.AeProcessImageModelCache";  //$NON-NLS-1$

   /**
    * Overrides method to ready imaging properties such as the buffered image's color depth.
    * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
    */
   public void init(ServletConfig aConfig) throws ServletException
   {
      super.init(aConfig);

      // to make it easier override the properties set in the graph.properties file,
      // check and see if there is a corresponding property as part of the servlet's initial parameter.
      AeGraphProperties graphProperties = AeGraphProperties.getInstance();

      for (Iterator i = graphProperties.keySet().iterator(); i.hasNext(); )
      {
         String key = (String) i.next();
         String value = aConfig.getInitParameter(key);
         if (!AeUtil.isNullOrEmpty(value))
         {
            graphProperties.setProperty(key, value);
         }
      }
   }

   /**
    * Servlet entry for the request and response.
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   public void service(HttpServletRequest aRequest, HttpServletResponse aResponse) throws IOException, ServletException
   {
      // Wrap the HTTP response in an instance of IAeGraphImageResponse.
      AeGraphImageServletResponse response = new AeGraphImageServletResponse(aResponse);

      // Construct the graph image driver that sends the image to the response.
      AeGraphImageDriver driver = createDriver(response);

      try
      {
         // The servlet may render the graph based on one of the following parameters:
         // 1) pid : Active Process ID, or
         // 2) pdid: Deployed process (definition) id index, or,
         // 3) planId: Deployed process (definition) plan Id.
         AeGraphImageParameters params = new AeGraphImageParameters();
         params.mProcessId = getProcessId(aRequest);
         params.mDeploymentProcessId = getDeployedProcessId(aRequest);
         params.mPlanId = getDeployedProcessPlanId(aRequest);
         params.mPart = getPartId(aRequest);
         params.mPivotPath = aRequest.getParameter("pivot"); //$NON-NLS-1$
         params.mGridRow = getIntParam(aRequest, "r", -1);//$NON-NLS-1$
         params.mGridColumn = getIntParam(aRequest, "c", -1);//$NON-NLS-1$
         params.mTileWidth = getIntParam(aRequest, "w", -1);//$NON-NLS-1$
         params.mTileHeight = getIntParam(aRequest, "h", -1);//$NON-NLS-1$
         params.mSessionId = aRequest.getParameter("sid"); //$NON-NLS-1$

         AeProcessViewCache cache = getProcessViewCache(aRequest.getSession(true));

         driver.sendGraphImage(params, cache);
      }
      catch (Throwable t)
      {
         driver.sendErrorImageStream();
         String info = AeMessages.format("AeGraphImageServlet.error",new Object[] { String.valueOf(t.getLocalizedMessage()) } ); //$NON-NLS-1$
         AeException.logWarning(info);
      }
   }

   /**
    * Returns driver to send the graph image to the given response.
    */
   protected AeGraphImageDriver createDriver(IAeGraphImageResponse aResponse)
   {
      return new AeGraphImageDriver(aResponse);
   }

   /**
    * Returns the process view cache associated with the given HTTP session.
    */
   protected AeProcessViewCache getProcessViewCache(HttpSession aHttpSession)
   {
      AeProcessViewCache cache;
      // get model cache from http session
      synchronized(aHttpSession)
      {
         cache = (AeProcessViewCache) aHttpSession.getAttribute(CACHE_MODEL_SESSION_KEY);
         if (cache == null)
         {
            cache = new AeProcessViewCache();
            aHttpSession.setAttribute(CACHE_MODEL_SESSION_KEY, cache);
         }
      }  // end synch (httpSession)
      return cache;
   }

   /**
    * Returns the process id obtained from the http request 'pid' parameter.
    * @param aRequest
    * @return process id or -1 if not given.
    */
   protected long getProcessId(HttpServletRequest aRequest)
   {
      return getIntParam(aRequest, "pid", -1);//$NON-NLS-1$
   }

   /**
    * Returns the process deployed id index obtained from the http request 'pdid' parameter.
    * @param aRequest
    * @return deployed process id or -1 if not given.
    */
   protected int getDeployedProcessId(HttpServletRequest aRequest)
   {
      return getIntParam(aRequest, "pdid", -1);//$NON-NLS-1$
   }

   /**
    * Returns the process deployed plan id obtained from the http request 'planid' parameter.
    * @param aRequest
    * @return deployed process plan id or -1 if not given.
    */
   protected int getDeployedProcessPlanId(HttpServletRequest aRequest)
   {
      return getIntParam(aRequest, "planid", -1);//$NON-NLS-1$
   }

   /**
    * Returns the process part id obtained from the http request 'part' parameter.
    * @param aRequest
    * @return part id or -1 if not given.
    */
   protected int getPartId(HttpServletRequest aRequest)
   {
      return getIntParam(aRequest, "part", -1);//$NON-NLS-1$
   }

   /**
    * Convinience method to return a parameter as an integer value.
    * @param aRequest
    * @param aParamName
    * @param aDefaultValue
    * @return int param value.
    */
   protected int getIntParam(HttpServletRequest aRequest, String aParamName, int aDefaultValue)
   {
      int rval = AeUtil.parseInt(aRequest.getParameter(aParamName), aDefaultValue);
      return rval;
   }

   /**
    * Wraps an <code>HttpServletResponse</code> object in an
    * {@link IAeGraphImageResponse} for a graph image driver.
    */
   protected static class AeGraphImageServletResponse implements IAeGraphImageResponse
   {
      private final HttpServletResponse mHttpResponse;

      /**
       * Constructs an <code>HttpServletResponse</code> wrapper.
       *
       * @param aHttpResponse
       */
      public AeGraphImageServletResponse(HttpServletResponse aHttpResponse)
      {
         mHttpResponse = aHttpResponse;
      }

      /**
       * Overrides method to add a header to the HTTP response.
       *
       * @see org.activebpel.rt.bpeladmin.war.web.graph.IAeGraphImageResponse#addHeader(java.lang.String, java.lang.String)
       */
      public void addHeader(String aHeaderName, String aHeaderValue)
      {
         mHttpResponse.addHeader(aHeaderName, aHeaderValue);
      }

      /**
       * Overrides method to return the HTTP response output stream.
       *
       * @see org.activebpel.rt.bpeladmin.war.web.graph.IAeGraphImageResponse#getOutputStream()
       */
      public OutputStream getOutputStream() throws IOException
      {
         return mHttpResponse.getOutputStream();
      }

      /**
       * Overrides method to set the HTTP response content type.
       *
       * @see org.activebpel.rt.bpeladmin.war.web.graph.IAeGraphImageResponse#setContentType(java.lang.String)
       */
      public void setContentType(String aContentType)
      {
         mHttpResponse.setContentType(aContentType);
      }
   }
}
