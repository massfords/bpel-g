// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AeInMemoryProcessLogger.java,v 1.9 2007/02/16 14:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Map;

import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.config.IAeConfigChangeListener;
import org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.server.engine.AeBPELProcessEventFormatter;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.IAeProcessLogger;
import org.activebpel.rt.util.AeLongMap;
import org.activebpel.rt.util.AeUtil;

/**
 * Maintains a StringBuffer of formatted process events for each process created
 * and executed by the engine. This class is intended to be a base class from which
 * to either persist the process log to a file or db.  
 */
public class AeInMemoryProcessLogger implements IAeProcessLogger, IAeConfigChangeListener
{
   /** Reference to the engine that we're listening to. */
   private IAeBusinessProcessEngineInternal mEngine;

   /** maps the process id to the string buffer */
   protected AeLongMap mPidToBuffer = new AeLongMap(new Hashtable());
  
   /** used to filter out some log events */
   protected IAeLoggingFilter mFilter = null;

   /**
    * Constructor. There are no params read from the config map at this level.
    * @param aConfig
    */
   public AeInMemoryProcessLogger(Map aConfig)
   {
      updateConfig( AeEngineFactory.getEngineConfig().getUpdatableEngineConfig() );
      AeEngineFactory.getEngineConfig().getUpdatableEngineConfig().addConfigChangeListener( this );
   }

   /**
    * @see org.activebpel.rt.bpel.IAeProcessListener#handleProcessEvent(org.activebpel.rt.bpel.IAeProcessEvent)
    */
   public boolean handleProcessEvent(IAeProcessEvent aEvent)
   {
      if (getLoggingFilter().accept(aEvent))
      {
         String line = formatEvent(aEvent);
         try
         {
            appendToLog(aEvent.getPID(), line);
            if (isCloseEvent(aEvent))
               closeLog(aEvent.getPID());
         }
         catch (Throwable t)
         {
            t.printStackTrace();
         }
      }
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeProcessListener#handleProcessInfoEvent(org.activebpel.rt.bpel.IAeProcessInfoEvent)
    */
   public void handleProcessInfoEvent(IAeProcessInfoEvent aEvent)
   {
      if (getLoggingFilter().accept(aEvent))
      {
         String line = formatEvent(aEvent);
         appendToLog(aEvent.getPID(), line);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeProcessLogger#getAbbreviatedLog(long)
    */
   public String getAbbreviatedLog(long aPid) throws Exception
   {
      String log = ""; //$NON-NLS-1$
      StringBuffer buffer = getBuffer(aPid, false);
      if (buffer != null)
      {
         log = buffer.toString();
      }
      
      return log;
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeProcessLogger#getFullLog(long)
    */
   public Reader getFullLog(long aProcessId) throws Exception
   {
      return new StringReader(getAbbreviatedLog(aProcessId));
   }

   /**
    * Appends the formatted line to the process's StringBuffer
    * @param aPid
    * @param aLine
    */
   protected void appendToLog(long aPid, String aLine)
   {
      if (AeUtil.notNullOrEmpty(aLine))
      {
         StringBuffer buffer = getBuffer(aPid, true);
         synchronized(buffer)
         {
            buffer.append(aLine);
            buffer.append("\r\n"); //$NON-NLS-1$
         }
      }
   }

   /**
    * Gets the buffer for the specified process.
    * @param aPid
    * @param aCreateIfNotFound
    */
   protected StringBuffer getBuffer(long aPid, boolean aCreateIfNotFound)
   {
      StringBuffer buffer = (StringBuffer) getBufferMap().get(aPid);
      if (buffer == null && aCreateIfNotFound)
      {
         synchronized(getBufferMap())
         {
            buffer = (StringBuffer) getBufferMap().get(aPid);
            if (buffer == null)
            {
               buffer = new StringBuffer();
               getBufferMap().put(aPid, buffer);
            }
         }
      }
      return buffer;
   }

   /**
    * Returns true if the event signals that the process has ended
    * @param aEvent
    */
   protected boolean isCloseEvent(IAeProcessEvent aEvent)
   {
      return "/process".equals(aEvent.getNodePath()) && (aEvent.getEventID() == IAeProcessEvent.EXECUTE_COMPLETE || aEvent.getEventID() == IAeProcessEvent.EXECUTE_FAULT);  //$NON-NLS-1$
   }

   /**
    * Getter for the buffer map.
    */
   protected AeLongMap getBufferMap()
   {
      return mPidToBuffer;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAeProcessLogger#setEngine(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine)
   {
      mEngine = aEngine;
   }
   
   /**
    * Getter for the engine.
    */
   protected IAeBusinessProcessEngineInternal getEngine()
   {
      return mEngine;
   }


   /** Default impl does nothing. Called when the process completes and no more log events will be fired. */
   protected void closeLog(long aPid) throws IOException
   {
   }

   /**
    * Formats the event using the MessageFormat based <code>AeBPELProcessEventFormatter</code>.
    * @param aEvent
    */
   protected String formatEvent(IAeProcessEvent aEvent)
   {
      return AeBPELProcessEventFormatter.getInstance().formatEvent( aEvent );
   }

   /**
    * Formats the event using the MessageFormat based <code>AeBPELProcessEventFormatter</code>.
    * @param aEvent
    */
   protected String formatEvent(IAeProcessInfoEvent aEvent)
   {
      return AeBPELProcessEventFormatter.getInstance().formatEvent( aEvent );
   }
   
   /**
    * @see org.activebpel.rt.bpel.config.IAeConfigChangeListener#updateConfig(org.activebpel.rt.bpel.config.IAeUpdatableEngineConfig)
    */
   public void updateConfig(IAeUpdatableEngineConfig aConfig)
   {
      setLoggingFilter(AeLoggingFilter.getLoggingFilter(aConfig.getLoggingFilter()));
      if (getLoggingFilter().isEnabled())
      {
         AeEngineFactory.getEngine().addProcessListener( this );
      }
      else
      {
         AeEngineFactory.getEngine().removeProcessListener( this );
      }
   }
   
   /**
    * Getter for the logging filter.
    */
   protected IAeLoggingFilter getLoggingFilter()
   {
      return mFilter;
   }
   
   /**
    * Setter for the logging filter
    * @param aFilter
    */
   protected void setLoggingFilter(IAeLoggingFilter aFilter)
   {
      mFilter = aFilter;
   }
}
