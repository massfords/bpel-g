// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel.web/src/org/activebpel/rt/axis/bpel/web/AeProcessEngineServlet.java,v 1.37.2.1 2008/04/21 16:06:50 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002, 2003, 2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.web;

import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.bpel.AeAxisServerFactory;
import org.activebpel.rt.util.AeUTF8Util;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AxisServlet;

/**
 * The process engine servlet starts up the bpel server, as well as the axis 
 * server.  It is automatically loaded on startup as part of the web.xml for 
 * the tomcat deployment.  After it starts up the server it spawns a thread 
 * which listens for business process archive deployments.
 */
public class AeProcessEngineServlet extends AxisServlet
{

   /**
     * 
     */
    private static final long serialVersionUID = -8165149089539255494L;
/** The axis server */
   protected static AxisServer mAxisServer = null;

   /**
    * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
    */
   public void init(ServletConfig aConfig) throws ServletException
   {
      log.info(AeMessages.getString("AeProcessEngineServlet.12")); //$NON-NLS-1$
      try
      {
         // set the axis server context for base static code
         aConfig.getServletContext().setAttribute(ATTR_AXIS_ENGINE, getEngine());
      }
      catch (Exception e)
      {
         log.error(AeMessages.getString("AeProcessEngineServlet.13"), e); //$NON-NLS-1$
         throw new ServletException(e);
      }
      
      super.init(aConfig);
   }
   
   /**
    * @see org.apache.axis.transport.http.AxisServlet#reportAvailableServices(javax.servlet.http.HttpServletResponse, java.io.PrintWriter, javax.servlet.http.HttpServletRequest)
    */
   protected void reportAvailableServices(HttpServletResponse aResponse,
         PrintWriter aWriter, HttpServletRequest aRequest)
         throws ConfigurationException, AxisFault
   {
      // handle utf-8 chars in the wsdl listing
	  aResponse.setCharacterEncoding( AeUTF8Util.UTF8_ENCODING );
      super.reportAvailableServices(aResponse, aWriter, aRequest);
   }
   
   /**
    * Overrides so we can create our own axis engine configuration.
    * @see org.apache.axis.transport.http.AxisServletBase#getEngine()
    */
   public AxisServer getEngine() throws AxisFault 
   {
      return AeAxisServerFactory.getAxisServer();
   }
   
   /**
    * Accessor for the Axis server.
    */
   public static AxisServer getAxisServer() 
   {
      try 
      {
          return AeAxisServerFactory.getAxisServer();
      }
      catch (AxisFault af)
      {
          AeException.logError(af);
          return null;
      }
   }

}

