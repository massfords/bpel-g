//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AeEngineLifecycleWrapper.java,v 1.4.2.1 2008/04/21 16:12:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import commonj.timers.TimerListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.config.AeDefaultEngineConfiguration;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileInfo;
import org.activebpel.rt.bpel.server.deploy.scanner.IAeDeploymentFileHandler;
import org.activebpel.rt.bpel.server.engine.config.AeFileBasedEngineConfig;
import org.activebpel.rt.bpel.server.logging.IAeLogWrapper;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.timer.AeAbstractTimerWork;

/**
 * ActiveBPEL <code>IAeEngineHandler</code> impl.
 */
public class AeEngineLifecycleWrapper implements IAeEngineLifecycleWrapper
{
   /** Initial delay before initiating start sequence. */
   protected long mInitialDelay;
   /** Platform logging. */
   protected IAeLogWrapper mLog;
   /** The file handler. */
   protected IAeDeploymentFileHandler mFileHandler;
   /** The directory of the servlet context dir where we are running */
   protected static File mContextPath;
   
   /**
    * Constructor.
    * @param aFileHandler
    * @param aInitialDelay
    */
   public AeEngineLifecycleWrapper(IAeLogWrapper aLog, IAeDeploymentFileHandler aFileHandler, long aInitialDelay)
   {
      mLog = aLog;
      mFileHandler = aFileHandler;
      mInitialDelay = aInitialDelay;
   }
   
   /**
    * Returns the constext path wher the servlet is executing
    */
   public static File getServletContext()
   {
      return mContextPath;
   }

   /**
    * Sets the context path where the servlet is executing
    * @param aContextPath
    */
   public static void setServletContext(File aContextPath)
   {
      mContextPath = aContextPath;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeEngineLifecycleWrapper#init()
    */
   public void init() throws AeException
   {
      AeEngineFactory.preInit(loadEngineConfig());
      AeEngineFactory.init();
      logInfo( "********** " + getConfigDescription() + AeMessages.getString("AeEngineLifecycleWrapper.1") ); //$NON-NLS-1$ //$NON-NLS-2$
   }
   
   protected String getConfigDescription()
   {
      return AeEngineFactory.getEngineAdministration().getEngineConfig().getDescription();
   }
   
   /**
    * Load the engine configuration.
    * @throws AeException
    */
   protected IAeEngineConfiguration loadEngineConfig() throws AeException
   {
      InputStream in = null;
      try
      {
         File engineConfigFile = loadConfigFile();
         in = new FileInputStream( engineConfigFile );
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         AeFileBasedEngineConfig engineConfig = new AeFileBasedEngineConfig(engineConfigFile,cl);
         AeFileBasedEngineConfig.loadConfig( engineConfig, in, cl );
         return engineConfig;
      }
      catch( Exception e )
      {
         throw new AeException(e);
      }
      finally
      {
         AeCloser.close( in );
      }
   }

   /**
    * Attempt to load the engine config file using the name of 
    * the file specified by the "engine.config" init param.
    * If the file cannot be located, load the default version
    * (aeEngineConfig.xml) from the classpath.
    * @throws AeException 
    */
   protected File loadConfigFile() throws AeException
   {
      File configFile = AeDeploymentFileInfo.getEngineConfigFile();
      
      if( !configFile.isFile() )
      {
         logError(MessageFormat.format(AeMessages.getString("AeEngineLifecycleWrapper.ERROR_0"), //$NON-NLS-1$
                                       new Object[] {configFile.getPath()}), null );

         URL configResource = AeUtil.findOnClasspath( AeDefaultEngineConfiguration.DEFAULT_CONFIG_FILE, getClass() );
         if( configResource == null )
         {
            throw new AeException(MessageFormat.format(AeMessages.getString("AeEngineLifecycleWrapper.ERROR_2"), //$NON-NLS-1$
                                                       new Object[] {AeDefaultEngineConfiguration.DEFAULT_CONFIG_FILE}));
         }
         configFile = new File(configResource.getFile());
      }
      logInfo(MessageFormat.format(AeMessages.getString("AeEngineLifecycleWrapper.3"), new Object[] {configFile.getPath()})); //$NON-NLS-1$
      return configFile; 
   }
   
   
   /**
    * Proceed with the engine start sequence. The start routine kicks off a timer to delay actual start.  
    * @throws AeException
    */
   public void start() throws AeException
   {
      long delay = getInitialDelay();

      if (delay <= 0)
      {
         doStart();
      }
      else
      {
         // We need to schedule the start after the preset delay interval
         TimerListener timerWork = new AeAbstractTimerWork()
         {
            public void run()
            {
               doStart();
            }
         };
      
         AeEngineFactory.getTimerManager().schedule(timerWork, getInitialDelay());
      }
   }
   
   /**
    * Start the server if the storage is ready. 
    */
   protected void doStart()
   {
      if (AeEngineFactory.isEngineStorageReady())
      {
         boolean startScanning = true;

         // if the initial deployments fail then
         // something is wrong with the bpr dir
         try
         {
            doInitialDeployments();
         }
         catch( Throwable t )
         {
            startScanning = false;
            AeException.logError( t, AeMessages.getString("AeEngineLifecycleWrapper.ERROR_3") ); //$NON-NLS-1$
         }
         
         startBpelEngine();
         
         // don't scan if initial deployments failed
         if( startScanning )
         {
            startScanning();
         }
      }
   } 
   
   /**
    * Process any deployments necessary before starting the BPEL engine.
    */
   protected void doInitialDeployments()
   {
      mFileHandler.handleInitialDeployments();
   }

   /**
    * Start the BPEL engine.
    */
   protected void startBpelEngine()
   {
      try
      {
         AeEngineFactory.getEngine().start();
         logInfo( "********** " + getConfigDescription() + AeMessages.getString("AeEngineLifecycleWrapper.9") ); //$NON-NLS-1$ //$NON-NLS-2$
      }
      catch( AeException ae )
      {
         ae.logError();
      }
   }
   
   /**
    * Start the directory scanner.
    */
   protected void startScanning()
   {
      mFileHandler.startScanning();
   }

   /**
    * Convenience log for information messages.
    * @param aMessage
    */
   protected void logInfo( String aMessage )
   {
      if( mLog != null )
      {
         mLog.logInfo( aMessage );
      }
   }
   
   /**
    * Convenience log for error messages.
    * @param aMessage
    * @param aProblem
    */
   protected void logError( String aMessage, Throwable aProblem )
   {
      if( mLog != null )
      {
         mLog.logError( aMessage, aProblem );
      }
   }
   
   /**
    * Stop the engine and release any associated resources.
    */
   public void stop()
   {
      String description = getConfigDescription();
      logInfo( description + AeMessages.getString("AeEngineLifecycleWrapper.10")); //$NON-NLS-1$

      try
      {
         mFileHandler.stopScanning();
         AeEngineFactory.getEngine().shutDown();
         logInfo( description + AeMessages.getString("AeEngineLifecycleWrapper.11")); //$NON-NLS-1$
      }
      catch (Exception e)
      {
         // should never happen
         logError( MessageFormat.format("Error shutting down {0}", new Object[] {description}), e); //$NON-NLS-1$
      }
   }
   
   /**
    * Returns the initialDelay to be used during start.
    */
   protected long getInitialDelay()
   {
      return mInitialDelay;
   }
}