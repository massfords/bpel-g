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

import java.io.File;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.bpel.AeAxisServerFactory;
import org.activebpel.rt.bpel.config.AeDefaultEngineConfiguration;
import org.activebpel.rt.bpel.server.catalog.AeCatalogEvent;
import org.activebpel.rt.bpel.server.catalog.IAeCatalogListener;
import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileHandler;
import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileInfo;
import org.activebpel.rt.bpel.server.deploy.scanner.IAeDeploymentFileHandler;
import org.activebpel.rt.bpel.server.engine.AeEngineLifecycleWrapper;
import org.activebpel.rt.bpel.server.engine.IAeEngineLifecycleWrapper;
import org.activebpel.rt.bpel.server.logging.IAeLogWrapper;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
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
public class AeProcessEngineServlet extends AxisServlet implements IAeCatalogListener
{
   /////////////////////////////////////////////////////////////////////////////
   // Init param keys
   /////////////////////////////////////////////////////////////////////////////
   /** Deployment directory init param key. */
   public static final String BPR_DIR_PARAM       = "deployment.directory"; //$NON-NLS-1$
   /** Engine config file name init param key. */
   public static final String ENGINE_CONFIG_PARAM = "engine.config"; //$NON-NLS-1$
   /** Scan interval init param config. */
   public static final String SCAN_INTERVAL_PARAM = "scan.interval"; //$NON-NLS-1$
   /** Initial scan delay init param config. */
   public static final String SCAN_DELAY_PARAM    = "scan.delay"; //$NON-NLS-1$
   /** Staging directory init param. */
   public static final String STAGING_DIR_PARAM   = "staging.directory"; //$NON-NLS-1$
   /** Servlet home init param key. */
   public static final String SERVLET_HOME_PARAM  = "servlet.home"; //$NON-NLS-1$
   
   /////////////////////////////////////////////////////////////////////////////
   // Default values for init params
   /////////////////////////////////////////////////////////////////////////////
   
   /** the default servlet home */
   private static final String DEFAULT_HOME = "catalina.home"; //$NON-NLS-1$
   
   /** Default staging directory: work (relative to deployment directory). */
   private static final String DEFAULT_STAGING_DIR = "work"; //$NON-NLS-1$
   
   /** Default deployment directory: bpr (relative to server.home init param or catalina.home if none is specified). */
   private static final String DEFAULT_DEPLOYMENT_DIR = "bpr"; //$NON-NLS-1$
   
   /** The default scan interval */
   protected static final long DEFAULT_INTERVAL = 30000;

   /** Default amount of time to wait before scanning starts. */
   protected static final long DEFAULT_DELAY = 15000;
   
   /////////////////////////////////////////////////////////////////////////////
   // Member data
   /////////////////////////////////////////////////////////////////////////////
   /** for deployment logging purposes */
   protected static final Logger log = Logger.getLogger("ActiveBPEL"); //$NON-NLS-1$

   /** AeLoggerWrapper impl for deployment logging. */
   protected static final AeTomcatLogger sLogger = new AeTomcatLogger();

   /** The axis server */
   protected static AxisServer mAxisServer = null;

   /** The deployment handler. */
   protected static IAeDeploymentFileHandler mDeploymentHandler = null;
   
   /** Engine handler impl. */   
   protected IAeEngineLifecycleWrapper mEngineHandler;
   
   /** Key used to get the context tempdir where our servlet is running */
   protected static final String SERVLET_CONTEXT = "javax.servlet.context.tempdir"; //$NON-NLS-1$
   
   /**
    * Getter for the static deployment handler.
    */
   public static IAeDeploymentFileHandler getDeploymentHandler()
   {
      return mDeploymentHandler;
   }

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
         initFileUtil( aConfig );
         mEngineHandler = createEngineHandler( aConfig );
         mEngineHandler.init();
         mEngineHandler.start();
      }
      catch (Exception e)
      {
         log.log(Level.SEVERE, AeMessages.getString("AeProcessEngineServlet.13"), e); //$NON-NLS-1$
         throw new ServletException(e);
      }
      
      super.init(aConfig);
   }
   
   /**
    * Initialize the static contents of the <code>AeActiveBpelFileUtil</code> class.
    * @param aConfig
    * @throws AeException
    */
   protected void initFileUtil( ServletConfig aConfig ) throws AeException
   {
      File servletHome = initServletHome(aConfig);
      File deploymentDir = getFile( servletHome, BPR_DIR_PARAM, DEFAULT_DEPLOYMENT_DIR, aConfig );
      File stagingDir = getFile( servletHome, STAGING_DIR_PARAM, DEFAULT_STAGING_DIR, aConfig );
      String configFileName = getEngineConfigFileName(aConfig);

      AeDeploymentFileInfo.setConfigFileName( configFileName );
      AeDeploymentFileInfo.setDeploymentDirectory( deploymentDir.getPath() );
      AeDeploymentFileInfo.setStagingDirectory( stagingDir.getPath() );
   }

   /**
    * Returns the servlet context path where are servlet is executing from
    * @param aConfig
    */
   protected File getServletContextPath( ServletConfig aConfig)
   {
      try
      {
         return (File)aConfig.getServletContext().getAttribute(SERVLET_CONTEXT);
      }
      catch (Exception e)
      {
         AeException.logError(e);
         return null;
      }
   }
   
   /**
    * Create the <code>IAeEngineHandler</code> impl.
    * @param aConfig
    * @throws AeException
    */
   protected IAeEngineLifecycleWrapper createEngineHandler( ServletConfig aConfig ) throws AeException
   {
      long initialDelay = getLongValue( aConfig, SCAN_DELAY_PARAM, DEFAULT_DELAY );
      IAeDeploymentFileHandler fileHandler = createFileHandler( aConfig );
      AeEngineLifecycleWrapper handler = new AeEngineLifecycleWrapper(sLogger, fileHandler, initialDelay);
      AeEngineLifecycleWrapper.setServletContext(getServletContextPath(aConfig));
      
      return handler;
   }
   
   /**
    * Create the <code>IAeDeploymentFileHandler</code> impl.
    * @param aConfig
    */
   protected IAeDeploymentFileHandler createFileHandler( ServletConfig aConfig )
   {
      if (mDeploymentHandler == null)
      {
         long scanInterval = getLongValue( aConfig, SCAN_INTERVAL_PARAM, DEFAULT_INTERVAL );
         mDeploymentHandler = new AeDeploymentFileHandler( sLogger, scanInterval );
      }
      return mDeploymentHandler;
   }
   
   /**
    * Return the long value for an init param.
    * @param aConfig
    * @param aParamName
    * @param aDefaultValue
    */
   protected long getLongValue( ServletConfig aConfig, String aParamName, long aDefaultValue )
   {
      long retVal = aDefaultValue;
      String longValue = aConfig.getInitParameter( aParamName );
      if( !AeUtil.isNullOrEmpty(longValue) )
      {
         try
         {
            retVal = Long.parseLong( longValue );
         }
         catch( NumberFormatException nfe )
         {
            
         }
      }
      return retVal;
   }
   

   /**
    * Initialize the server home from the "servlet.home" init
    * param.  Defaults to "catalina.home" if none is specified.
    * This value is used as an environment property lookup key.
    * @param aConfig
    * @throws AeException Thrown if there is no corresponding environment property for the servlet.home param.
    */
   protected File initServletHome( ServletConfig aConfig ) throws AeException
   {
      // extract the server home value (defaults to "catalina.home"
      // if none is specified
      String servletHomePath = aConfig.getInitParameter( SERVLET_HOME_PARAM );
      if( AeUtil.isNullOrEmpty(servletHomePath) )
      {
         servletHomePath = System.getProperty(DEFAULT_HOME);
      }
      else
      {
         servletHomePath = AeUtil.replaceAntStyleParams( servletHomePath, System.getProperties() );
      }
      File servletHome = new File(servletHomePath);
      log.fine("servlet.home="+servletHomePath); //$NON-NLS-1$
      return servletHome;
   }
      
   /**
    * Convenience method for creating files.
    * @param aServletHome Any files that are not absolute will be resolved relative to this file.
    * @param aParamKey The init param key.
    * @param aDefaultPath The default value if no init param is specified.
    * @param aConfig The <code>ServletConfig</code>.
    */
   protected File getFile( File aServletHome, String aParamKey, String aDefaultPath, ServletConfig aConfig )
   {
      String filePath = aConfig.getInitParameter( aParamKey );
      if( AeUtil.isNullOrEmpty(filePath) )
      {
         filePath = aDefaultPath;
      }
      else
      {
         filePath = AeUtil.replaceAntStyleParams( filePath, System.getProperties() );
      }
      File file = new File( filePath );
      if( !file.isAbsolute() )
      {
         file = new File( aServletHome, filePath );
      }
      return file;
   }
   
   /**
    * Initialize the engine config file name from the "engine.config"
    * init param.  Defaults to "aeEngineConfig.xml" if none is
    * specified.
    * @param aConfig
    */
   protected String getEngineConfigFileName(ServletConfig aConfig)
   {
      // get the name of the engine config file default value is aeEngineConfig.xml
      String engineConfigFileName = aConfig.getInitParameter( ENGINE_CONFIG_PARAM );
      if( AeUtil.isNullOrEmpty(engineConfigFileName) )
      {
         engineConfigFileName = AeDefaultEngineConfiguration.DEFAULT_CONFIG_FILE;
      }
      log.fine("engine.config="+engineConfigFileName); //$NON-NLS-1$
      return engineConfigFileName;
   }
   
   
   /**
    * Signal the <code></code> and set us as not initialized.
    * 
    * @see javax.servlet.Servlet#destroy()
    */
   public void destroy() 
   {
      super.destroy();
      try
      {
         mEngineHandler.stop();
      }
      catch( AeException ae )
      {
         ae.logError();
      }
   }
   

   /**
    * @see org.apache.axis.transport.http.AxisServlet#reportAvailableServices(javax.servlet.http.HttpServletResponse, java.io.PrintWriter, javax.servlet.http.HttpServletRequest)
    */
   protected void reportAvailableServices(HttpServletResponse aResponse,
         PrintWriter aWriter, HttpServletRequest aRequest)
         throws ConfigurationException, AxisFault
   {
      encodeResponse( aResponse );
      super.reportAvailableServices(aResponse, aWriter, aRequest);
   }
   
   /**
    * Set character encoding to UTF-8.
    * @param aResponse
    */
   protected void encodeResponse( HttpServletResponse aResponse )
   {
      // handle utf-8 chars in the wsdl listing
      aResponse.setCharacterEncoding( AeUTF8Util.UTF8_ENCODING );
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

   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogListener#onDeployment(org.activebpel.rt.bpel.server.catalog.AeCatalogEvent)
    */
   public void onDeployment(AeCatalogEvent aEvent)
   {
      log.fine(AeMessages.getString("AeProcessEngineServlet.19") + aEvent.getLocationHint() ); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogListener#onDuplicateDeployment(org.activebpel.rt.bpel.server.catalog.AeCatalogEvent)
    */
   public void onDuplicateDeployment(AeCatalogEvent aEvent)
   {
      log.warning(MessageFormat.format(AeMessages.getString("AeProcessEngineServlet.0"), //$NON-NLS-1$
                                       new Object[] {aEvent.getLocationHint()}));
   }

   /**
    * @see org.activebpel.rt.bpel.server.catalog.IAeCatalogListener#onRemoval(org.activebpel.rt.bpel.server.catalog.AeCatalogEvent)
    */
   public void onRemoval(AeCatalogEvent aEvent)
   {
      log.fine( AeMessages.getString("AeProcessEngineServlet.22") + aEvent.getLocationHint() ); //$NON-NLS-1$
   }
   
   /**
    * Logger wrapper impl for Tomcat.
    */
   protected static class AeTomcatLogger implements IAeLogWrapper
   {
      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeLogWrapper#logDebug(java.lang.String)
       */
      public void logDebug(String aMessage)
      {
         log.fine( aMessage );
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeLogWrapper#logError(java.lang.String, java.lang.Throwable)
       */
      public void logError(String aMessage, Throwable aProblem)
      {
         log.severe( aMessage );
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeLogWrapper#logInfo(java.lang.String)
       */
      public void logInfo(String aMessage)
      {
         log.info( aMessage );
      }
   }
}

