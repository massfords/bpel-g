// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AeFileLogger.java,v 1.8 2005/02/08 15:36:04 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.IAeProcessLogger;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUnsynchronizedCharArrayWriter;
import org.activebpel.rt.util.AeUtil;

/**
 * File based logging. Writes the contents of the process log to a file once the
 * process completes.
 */
public class AeFileLogger extends AeInMemoryProcessLogger
{
   /** max size of a log permitted to be kept in memory before being written to disk. */
   private static final int MAX_LOG_SIZE = 1024*128;
   
   /**
    * Ctor takes a map of options of the config file.
    */
   public AeFileLogger( Map aConfig )
   {
      super(aConfig);
      String value = (String) aConfig.get("DeleteFilesOnStartup"); //$NON-NLS-1$

      boolean deleteFileOnStartup = true; 

      if (!AeUtil.isNullOrEmpty(value))
         deleteFileOnStartup = Boolean.valueOf(value).booleanValue();
         
      if (deleteFileOnStartup)
      {
         deleteLogFiles();
      }
   }

   /**
    * Deletes the existing log files on startup. This is only called during construction
    * of the logger if the "DeleteFilesOnStartup" entry is set to "true" in the config
    * or if not present at all.
    */
   protected void deleteLogFiles()
   {
      File dir = new File(AeEngineFactory.getEngineConfig().getLoggingBaseDir(), "process-logs/"); //$NON-NLS-1$
      File[] files = dir.listFiles(new FileFilter()
      {

         public boolean accept(File aFile)
         {
            return aFile.isFile() && aFile.getName().endsWith(".log"); //$NON-NLS-1$
         }
      });
      for (int i = 0; files != null && i < files.length; i++)
      {
         files[i].delete();
      }
   }
   
   

   /**
    * Reads the file into a String
    * @param raf
    * @throws IOException
    */
   protected String read(RandomAccessFile raf) throws IOException
   {
      StringBuffer log = new StringBuffer((int)raf.length());
      String line = null;
      while ((line = raf.readLine()) != null)
      {
         log.append(line);
         log.append('\n');
      }
      return log.toString();
   }
   
   /**
    * Writes the contents of the process's log to a file and removes it from the 
    * buffer map.
    * @see org.activebpel.rt.bpel.server.logging.AeInMemoryProcessLogger#closeLog(long)
    */
   protected void closeLog(long aPid) throws IOException
   {
      writeToFile(aPid);
      getBufferMap().remove(aPid);
   }

   
   /**
    * Gets the file for the process's log. 
    * @param aPid
    */
   protected File getFile(long aPid) throws IOException
   {
      File file = new File(AeEngineFactory.getEngineConfig().getLoggingBaseDir(), "process-logs/"+aPid+".log"); //$NON-NLS-1$ //$NON-NLS-2$
      file.getParentFile().mkdirs();
      return file;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeProcessLogger#getAbbreviatedLog(long)
    */
   public String getAbbreviatedLog(long aProcessId) throws Exception
   {
      writeToFile(aProcessId);
      File file = getFile(aProcessId);
      if (file.length() < (1024 * 512))
      {
         // we'll allow them to read 1/2 meg into memory
         return readLogFileIntoString(file);
      }
      else
      {
         return readAbbreviatedLog(file);
      }
   }
   
   /**
    * Reads the head and tail of the log file. The amount of lines for the head
    * and tail is determined by the engine config.
    * @param aFile
    */
   private String readAbbreviatedLog(File aFile) throws IOException
   {
      AeUnsynchronizedCharArrayWriter writer = new AeUnsynchronizedCharArrayWriter();
      RandomAccessFile raf = null;
      try
      {
         raf = new RandomAccessFile(aFile, "r"); //$NON-NLS-1$
         int headLimit = AeEngineFactory.getEngineConfig().getIntegerEntry("Logging.Head", IAeProcessLogger.DEFAULT_HEAD); //$NON-NLS-1$
         int tailLimit = AeEngineFactory.getEngineConfig().getIntegerEntry("Logging.Tail", IAeProcessLogger.DEFAULT_TAIL); //$NON-NLS-1$
         
         // read the head
         read(raf, writer, headLimit);
         
         // move the cursor to the tail
         boolean moved = seekToTail(raf, tailLimit);
         
         if (moved)
         {
            // write the delim
            writer.write(SNIP);
         }
         
         // read the tail
         read(raf, writer, Integer.MAX_VALUE);
         
      }
      finally
      {
         AeCloser.close(raf);
      }
      
      return writer.toString();
   }

   /**
    * Moves the file pointer to where we need to be to read the tail portion
    * of the log.
    * @param aRandomAccessFile
    * @param aTailLimit
    * @throws IOException
    */
   private boolean seekToTail(RandomAccessFile aRandomAccessFile, int aTailLimit) throws IOException
   {
      // the sweetspot is the point near the end of the file where we'll be 
      // able to read the required number of lines for our tail.
      // i'm estimating 120 chars per log line
      boolean moved = false; 
      long sweetspot = aRandomAccessFile.length() - (aTailLimit * 120);
      if (sweetspot > aRandomAccessFile.getFilePointer())
      {
         aRandomAccessFile.seek(sweetspot);
         aRandomAccessFile.readLine();
         moved = true;
      }
      return moved;
   }

   /**
    * Reads at most aLimit number of chars from the file and writes them to the
    * writer
    * @param aRandomAccessFile
    * @param aWriter
    * @param aLimit
    * @throws IOException
    */
   private void read(RandomAccessFile aRandomAccessFile, AeUnsynchronizedCharArrayWriter aWriter,
                              int aLimit) throws IOException
   {
      String line;
      int count = 0;
      while (count++ < aLimit && (line = aRandomAccessFile.readLine()) != null)
      {
         aWriter.write(line);
         aWriter.write('\n');
      }
   }
   
   

   /**
    * Returns the whole log as a string
    */
   private String readLogFileIntoString(File aFile)
   {
      Reader reader = null;
      AeUnsynchronizedCharArrayWriter writer = new AeUnsynchronizedCharArrayWriter();
      try
      {
         reader = new FileReader(aFile);
         char[] buff = new char[1024*128];
         int read;
         while ((read = reader.read(buff)) != -1)
            writer.write(buff, 0, read);
      }
      catch (IOException e)
      {
         AeCloser.close(reader);
      }
      return writer.toString();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeProcessLogger#getFullLog(long)
    */
   public Reader getFullLog(long aProcessId) throws Exception
   {
      writeToFile(aProcessId);
      return new FileReader(getFile(aProcessId));
   }
   
   /**
    * Writes the contents of the log's memory buffer to disk. This is done when
    * the process closes, buffer gets too big, or if someone is requesting the abbreviated or
    * full log.
    * @param aProcessId
    * @throws IOException
    */
   protected void writeToFile(long aProcessId) throws IOException
   {
      StringBuffer sb = getBuffer(aProcessId, false);
      if (sb != null)
      {
         synchronized(sb)
         {
            Writer w = null;
            try
            {
               w = new FileWriter(getFile(aProcessId), true);
               w.write(sb.toString());
               sb.setLength(0);
            }
            finally
            {
               AeCloser.close(w);
            }
         }
      }
   }
   /**
    * @see org.activebpel.rt.bpel.server.logging.AeInMemoryProcessLogger#appendToLog(long, java.lang.String)
    */
   protected void appendToLog(long aPid, String line)
   {
      if (!AeUtil.isNullOrEmpty(line))
      {
         StringBuffer sb = getBuffer(aPid, true);
         synchronized(sb)
         {
            sb.append(line);
            sb.append('\n');

            if (sb.length() > MAX_LOG_SIZE)
            {
               try
               {
                  writeToFile(aPid);
               }
               catch (IOException e)
               {
                  AeException.logError(e, AeMessages.getString("AeFileLogger.ERROR_8")); //$NON-NLS-1$
                  // eat the exception
               }
            }
         }
      }
   }

}
