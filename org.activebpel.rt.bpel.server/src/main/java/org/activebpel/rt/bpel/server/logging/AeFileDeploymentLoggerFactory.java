//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AeFileDeploymentLoggerFactory.java,v 1.9 2005/06/13 17:54:06 PCollins Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;

/**
 * Factory for returning instances of the deployment logger that write to a file. 
 */
public class AeFileDeploymentLoggerFactory implements IAeDeploymentLoggerFactory
{
   /** file that we're writing to */
   private File mFile;

   /** pattern to use when printing date/time stamp in log file */
   private String mDatePattern = AeMessages.getString("AeFileDeploymentLoggerFactory.0"); //$NON-NLS-1$
   
   /**
    * Creates a new factory with the init parameters from the map.
    * @param aMap
    */
   public AeFileDeploymentLoggerFactory(Map aMap)
   {
      initLogFile(aMap);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLoggerFactory#getDeploymentLog()
    */
   public String[] getDeploymentLog()
   {
      List results = new ArrayList();
      
      if (mFile.isFile())
      {
         BufferedReader reader = null;
         try
         {
            reader = new BufferedReader( new FileReader(mFile) );
            String line = null;
            while( (line = reader.readLine()) != null )
            {
               results.add( line );         
            }
         }
         catch (IOException e)
         {
            AeException.logError( e, AeMessages.getString("AeFileDeploymentLoggerFactory.ERROR_1") + mFile ); //$NON-NLS-1$
            return new String[] {AeMessages.getString("AeFileDeploymentLoggerFactory.ERROR_1") + mFile }; //$NON-NLS-1$
         }
         finally
         {
            AeCloser.close( reader );
         }
      }
      return (String[]) results.toArray( new String[ results.size() ] );
   }

   /**
    * Create the log directory and the log file object.
    * @param aParams
    */
   protected void initLogFile( Map aParams )
   {
      File dir = new File( new File(AeEngineFactory.getEngineConfig().getLoggingBaseDir()), "deployment-logs" ); //$NON-NLS-1$
      dir.mkdirs();
      mFile = new File( dir, "aeDeployment.log" ); //$NON-NLS-1$
      mFile.delete();
   }

   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLoggerFactory#createLogger()
    */
   public IAeDeploymentLogger createLogger()
   {
      return new AeFileLogger();
   }
   
   /**
    * Writes the log contents out to the file
    * @param aLogContents
    */
   protected synchronized void write(String aLogContents)
   {
      PrintWriter pw = null;
      try
      {
         pw = new PrintWriter(new FileWriter(mFile, true));
         pw.println(aLogContents);
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         AeCloser.close(pw);
      }
   }
   
   /**
    * Impl of the logger that writes to a file once the log is closed. 
    */
   protected class AeFileLogger extends AeUnstructuredDeploymentLog
   {
      /** buffer that we keep */
      private StringWriter mStringWriter;
      /** used to format the dates/times of the log statements */
      private SimpleDateFormat mDateFormat = new SimpleDateFormat(mDatePattern);
      
      /**
       * ctor for the file logger 
       */
      public AeFileLogger()
      {
         mStringWriter = new StringWriter();
         setWriter(new PrintWriter(mStringWriter));
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.AeDeploymentLog#writeMessage(java.lang.String)
       */
      protected void writeMessage(String aMessage)
      {
         super.writeMessage(mDateFormat.format(new Date()) + " " + aMessage); //$NON-NLS-1$
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger#close()
       */
      public void close()
      {
         String log = mStringWriter.toString();
         if (!AeUtil.isNullOrEmpty(log))
         {
            write(log);
         }
      }
   }
}
 