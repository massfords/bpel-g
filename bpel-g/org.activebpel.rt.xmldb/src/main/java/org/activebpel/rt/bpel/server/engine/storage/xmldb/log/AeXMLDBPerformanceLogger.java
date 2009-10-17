//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/log/AeXMLDBPerformanceLogger.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import sun.misc.Queue;

/**
 * A performance logger for logging timing info for XMLDB queries and inserts.
 */
@SuppressWarnings("restriction")
public class AeXMLDBPerformanceLogger implements IAeXMLDBPerformanceLogger
{
   /** The <code>sInstance</code> is the singleton instance. */
   private static IAeXMLDBPerformanceLogger sInstance;

   /**
    * This is the singleton accessor.
    */
   public static IAeXMLDBPerformanceLogger getInstance()
   {
      return sInstance;
   }

   /**
    * Initializes the performance logger singleton.
    * 
    * @param aLogDir
    */
   public static void init(String aLogDir)
   {
      if (aLogDir != null && new File(aLogDir).isDirectory())
      {
         sInstance = new AeXMLDBPerformanceLogger(aLogDir);
      }
      else
      {
         sInstance = new AeNullXMLDBPerformanceLogger();
      }
   }

   /** The location of the log file. */
   private File mLogFile;
   /** The <code>mQueue2</code> field. */
   private Queue mQueue = new Queue();
   /** The <code>mWriter</code> field. */
   private PrintWriter mWriter;

   /**
    * Constructs the performance logger - it will log to a file that will be placed in the
    * given directory.
    */
   private AeXMLDBPerformanceLogger(String aLogDir)
   {
      try
      {
         setLogFile(new File(aLogDir, "xmldb-perf.log")); //$NON-NLS-1$
         setWriter(new PrintWriter(new FileOutputStream(getLogFile(), true)));

         Thread thread = new Thread(new Runnable()
            {
               public void run()
               {
                  try
                  {
                     boolean done = false;
                     while (!done)
                     {
                        AeLogEntry entry = (AeLogEntry) getQueue().dequeue();
                        writeLogEntry(entry);
                     }
                  }
                  catch (InterruptedException ex)
                  {
                     ex.printStackTrace();
                  }
               }
            });
         thread.setDaemon(true);
         thread.setPriority(Thread.MIN_PRIORITY);
         thread.start();
      }
      catch (FileNotFoundException ex)
      {
         ex.printStackTrace();
      }
   }

   protected void writeLogEntry(AeLogEntry aEntry)
   {
      PrintWriter writer = getWriter();

      writer.print(aEntry.mTiming1);
      writer.print('\t');
      writer.print(aEntry.mTiming2);
      writer.print('\t');
      writer.print(aEntry.mQuery);
      writer.println(""); //$NON-NLS-1$
      writer.flush();
   }

   public void logXQueryTime(String aQuery, long aQueryTime, long aHandlerTime)
   {
      getQueue().enqueue(new AeLogEntry(aQuery, aQueryTime, aHandlerTime));
   }

   public void logInsertTime(String aInsert, long aInsertTime)
   {
      getQueue().enqueue(new AeLogEntry(aInsert, aInsertTime, -1));
   }
   
   /**
    * @return Returns the logFile.
    */
   protected File getLogFile()
   {
      return mLogFile;
   }
   /**
    * @param aLogFile The logFile to set.
    */
   protected void setLogFile(File aLogFile)
   {
      mLogFile = aLogFile;
   }

   /**
    * @return Returns the queue2.
    */
   protected Queue getQueue()
   {
      return mQueue;
   }

   /**
    * @param aQueue2 The queue2 to set.
    */
   protected void setQueue(Queue aQueue2)
   {
      mQueue = aQueue2;
   }
   
   /**
    * Inner struct class to hold log info.
    */
   protected static class AeLogEntry
   {
      public String mQuery;
      public long mTiming1;
      public long mTiming2;

      public AeLogEntry(String aQuery, long aTiming1, long aTiming2)
      {
         mQuery = aQuery.replace('\n', ' ');
         mTiming1 = aTiming1;
         mTiming2 = aTiming2;
      }
   }
   
   /**
    * @return Returns the writer.
    */
   protected PrintWriter getWriter()
   {
      return mWriter;
   }
   
   /**
    * @param aWriter The writer to set.
    */
   protected void setWriter(PrintWriter aWriter)
   {
      mWriter = aWriter;
   }
}
