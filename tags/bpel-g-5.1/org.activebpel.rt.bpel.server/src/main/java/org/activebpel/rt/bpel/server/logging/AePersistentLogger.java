// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AePersistentLogger.java,v 1.9 2005/04/13 17:26:47 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager;
import org.activebpel.rt.util.AeSequenceReader;
import org.activebpel.rt.util.AeUtil;

/**
 * Persistent version of the logger. Maintains a StringBuffer for events that
 * are being fired by a process. When the process gets saved to the db, the
 * process manager will pull the available log information and persist it there.
 *
 * Requesting the full log from this class is simply a pass through to the
 * persistent process manager which in turn will pull it from the db.
 */
public class AePersistentLogger extends AeInMemoryProcessLogger implements IAePersistentLogger
{
   /**
    * Constructor for the log.
    * @param aConfig
    */
   public AePersistentLogger(Map aConfig)
   {
      super(aConfig);
   }

   /**
    * @see org.activebpel.rt.bpel.server.logging.IAePersistentLogger#getLogEntry(long)
    */
   public IAeProcessLogEntry getLogEntry(long aProcessId)
   {
      return new AeProcessLogEntry(aProcessId);
   }

   /**
    * Gets the available log from memory
    * @param aProcessId
    */
   protected String getAvailableLog(long aProcessId)
   {
      StringBuffer sb = getBuffer(aProcessId, false);
      return sb == null ? null : sb.toString();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeProcessLogger#getAbbreviatedLog(long)
    */
   public String getAbbreviatedLog(long aProcessId) throws Exception
   {
      String retLog = ""; //$NON-NLS-1$
      String existingLog = ((IAePersistentProcessManager)getEngine().getProcessManager()).getProcessLog(aProcessId);
      String availableLog = getAvailableLog(aProcessId);
      if(existingLog != null)
         retLog = existingLog;
      if(availableLog != null)
         retLog += availableLog;
      return retLog;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeProcessLogger#getFullLog(long)
    */
   public Reader getFullLog(long aProcessId) throws Exception
   {
      IAePersistentProcessManager pm = (IAePersistentProcessManager) getEngine().getProcessManager();
      Reader reader = pm.dumpLog(aProcessId);
      String availableLog = getAvailableLog(aProcessId);
      if (AeUtil.isNullOrEmpty(availableLog))
         return reader;
      else
         return new AeSequenceReader(reader, new StringReader(availableLog));
   }

   /**
    * Overridden here because we're not closing the logs at all so there's no point
    * in determining whether the event is a close event or not.
    * @see org.activebpel.rt.bpel.server.logging.AeInMemoryProcessLogger#isCloseEvent(org.activebpel.rt.bpel.IAeProcessEvent)
    */
   protected boolean isCloseEvent(IAeProcessEvent aEvent)
   {
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.server.logging.AeInMemoryProcessLogger#appendToLog(long, java.lang.String)
    */
   protected void appendToLog(long aPid, String line)
   {
      if (AeUtil.notNullOrEmpty(line))
      {
         StringBuffer sb = getBuffer(aPid, true);
         synchronized(sb)
         {
            super.appendToLog(aPid, line);
            // There is a race condition between appendToLog() and extractLogEntry().
            // It's possible that they'll execute concurrently due to an out of band request
            // for a logging statement (such as the process was migrated to another plan).
            // If extractLogEntry() executes first then the StringBuffer that we're both
            // synch'ing on will have been cleared and removed from the map. It's no
            // bother that it was cleared since its data will be written to the db, but
            // it's removal from the map is a problem since the statement we'll have
            // appended in this method will never make it into the log. The solution is
            // to always add the buffer back into the map here.
            getBufferMap().put(aPid, sb);
         }
      }
   }

   /**
    * Struct used to model the log entry
    */
   private class AeProcessLogEntry implements IAeProcessLogEntry
   {
      /** process id for the entry */
      private long mProcessId;
      /** The {@link java.lang.StringBuffer} for this log entry. */
      private StringBuffer mLogBuffer;
      /** contents of the log */
      private String mLog;
      /** number of lines in the log */
      private int mLineCount = -1;

      /**
       * Creates the entry with its data
       * @param aProcessId
       */
      protected AeProcessLogEntry(long aProcessId)
      {
         mProcessId = aProcessId;
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeProcessLogEntry#getLineCount()
       */
      public int getLineCount()
      {
         if (mLineCount == -1)
         {
            char[] cdata = getLog().toCharArray();
            mLineCount = 0;
            for (int i = 0; i < cdata.length; i++)
            {
               if (cdata[i] == '\n')
               {
                  mLineCount++;
               }
            }
         }
         return mLineCount;
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeProcessLogEntry#getLog()
       */
      public String getLog()
      {
         if (mLog == null)
         {
            // Grab a snapshot of the buffer to persist.
            mLog = getLogBuffer().toString();
         }

         return mLog;
      }

      /**
       * Returns {@link java.lang.StringBuffer} for this log entry.
       */
      protected StringBuffer getLogBuffer()
      {
         if (mLogBuffer == null)
         {
            mLogBuffer = getBuffer(getProcessId(), false);

            if (mLogBuffer == null)
            {
               // Use an empty StringBuffer as a placeholder for an empty log.
               mLogBuffer = new StringBuffer();
            }
         }

         return mLogBuffer;
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeProcessLogEntry#getProcessId()
       */
      public long getProcessId()
      {
         return mProcessId;
      }

      /**
       * @see org.activebpel.rt.bpel.server.logging.IAeProcessLogEntry#clearFromLog()
       */
      public void clearFromLog()
      {
         int nPersisted = getLog().length();

         // Delete the portion of the buffer that was persisted.
         if (nPersisted > 0)
         {
            StringBuffer sb = getLogBuffer();

            synchronized (sb)
            {
               sb.delete(0, nPersisted);

               if (sb.length() == 0)
               {
                  getBufferMap().remove(getProcessId());
               }
            }
         }
      }
   }
}
