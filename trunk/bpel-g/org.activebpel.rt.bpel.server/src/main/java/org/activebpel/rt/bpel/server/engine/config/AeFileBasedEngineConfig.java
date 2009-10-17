//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/config/AeFileBasedEngineConfig.java,v 1.6 2005/06/08 13:30:31 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.MessageFormat;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.AeDefaultEngineConfiguration;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeCloser;

/**
 * Extension of <code>AeDefaultEngineConfiguratio</code> that
 * is capable of receiving change notification from a <code>AeDirectoryScanner</code>.
 * Allow for config file changes made externally (not throug console) to be
 * detected.
 */
public class AeFileBasedEngineConfig extends AeDefaultEngineConfiguration
{
   // Error message constants.
   private static final String SAVE_ERROR = AeMessages.getString("AeFileBasedEngineConfig.ERROR_0"); //$NON-NLS-1$
   private static final String UPDATE_ERROR = AeMessages.getString("AeFileBasedEngineConfig.ERROR_1"); //$NON-NLS-1$
      
   /** Object lock for preventing duplicate updates. */
   private Object mLock = new Object();
   /** The file we are listening on. */
   protected String mFilePath;
   /** The last modified time of the config file. */
   protected long mLastModified;
   /** The class loader */
   protected ClassLoader mClassLoader;
   
   /**
    * Constructor.
    * @param aConfigFile
    * @param aClassLoader
    */
   public AeFileBasedEngineConfig( File aConfigFile, ClassLoader aClassLoader )
   {
      mFilePath = aConfigFile.getPath();
      // TODO Z! check exception handling with jar resource
      mLastModified = new File(mFilePath).lastModified();
      mClassLoader = aClassLoader;
   }

   /**
    * @see org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig#update()
    */
   public void update()
   {
      synchronized(mLock)
      {
         Writer w = null;
         try
         {
            File file = new File(mFilePath);
            w = new FileWriter( file );
            // it's possible that this save will cause the 'updateBecauseFileChanged'
            // method to be called (from a scan) before the last modified time has been updated,
            // so make sure that method locks on the mLock
            save(w);
            mLastModified = file.lastModified();
         }
         catch( Exception ae )
         {
            AeException.logError( ae, MessageFormat.format(SAVE_ERROR, mFilePath) );
         }
         finally
         {
            AeCloser.close( w );
         }
         super.update();
      }
   }
   
   /**
    * Update the engine config file settings due to a change in the file.
    */
   public void updateBecauseFileChanged()
   {
      // sync on the lock to prevent duplicate 
      // update calls after file has been 
      // saved (via above update method call)
      // but before last modified has been
      // set to new value
      synchronized( mLock )
      {
         File file = new File( mFilePath );

         if( mLastModified != file.lastModified() )
         {
            InputStream in = null;
            try
            {
               in = new FileInputStream( file );
               loadConfig( this, in, mClassLoader );
               super.update();
            }
            catch( IOException io )
            {
               AeException.logError( io, MessageFormat.format( UPDATE_ERROR, mFilePath) );
            }
            finally
            {
               AeCloser.close(in);
            }
         }
      }
   }
}
