// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/process/AeProcessWrapperMap.java,v 1.6 2006/06/16 18:39:59 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.process;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.AePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager;
import org.activebpel.work.AeAbstractWork;

import commonj.work.Work;
import commonj.work.WorkManager;

/**
 * Maps process ids to process wrappers.
 */
public class AeProcessWrapperMap implements IAeProcessWrapperMap
{
   /** The process manager that owns this process wrapper map. */
   private final IAePersistentProcessManager mProcessManager;

   /** Maps process ids to instances of {@link org.activebpel.rt.bpel.server.engine.process.AeProcessWrapper}. */
   private final Map<Long,AeProcessWrapper> mInMemoryMap = new HashMap<Long,AeProcessWrapper>();

   /** Callback interface to the map owner. */
   private IAeProcessWrapperMapCallback mCallback;
   
   /** next internal id for process wrapper created with empty id */
   private long mNextTempId = -1L;

   /**
    * Constructs process wrapper map for the specified process manager.
    *
    * @param aProcessManager
    */
   public AeProcessWrapperMap(IAePersistentProcessManager aProcessManager)
   {
      mProcessManager = aProcessManager;

      if (aProcessManager instanceof IAeProcessWrapperMapCallback)
      {
         mCallback = (IAeProcessWrapperMapCallback) aProcessManager;
      }
   }

   /**
    * Calls the callback object's {@link
    * org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMapCallback#notifyProcessWrapperMapFull()}
    * method.
    */
   protected void callNotifyProcessWrapperMapFull()
   {
      final IAeProcessWrapperMapCallback callback = getCallback();
      if (callback != null)
      {
         // Call the callback in a separate thread to avoid potential deadlocks
         // (see defect 1151, "Dead lock in persistence layer"). By the time we
         // get here, this thread has locked the process wrapper map. The
         // callback will then try to lock a process wrapper. This may deadlock
         // with another thread that has already locked the same process
         // wrapper and is waiting to lock the process wrapper map.
         Work work = new AeAbstractWork()
         {
            public void run()
            {
               try
               {
                  callback.notifyProcessWrapperMapFull();
               }
               catch (Throwable t)
               {
                  AeException.logError(t, AeMessages.getString("AeProcessWrapperMap.ERROR_RUN_NOTIFY")); //$NON-NLS-1$
               }
            }
         };

        AeEngineFactory.getBean(WorkManager.class).schedule(work);
      }
   }

   /**
    * Writes formatted debugging output.
    */
   protected void debug(String aPattern, long aArgument)
   {
      debug(aPattern, new Object[] { new Long(aArgument) });
   }

   /**
    * Writes formatted debugging output.
    */
   protected void debug(String aPattern, Object[] aArguments)
   {
      if (isDebug())
      {
         System.out.println(MessageFormat.format(aPattern, aArguments));
      }
   }

   /**
    * Returns the callback object.
    */
   protected IAeProcessWrapperMapCallback getCallback()
   {
      return mCallback;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMap#getCurrentWrapper(long)
    */
   public synchronized AeProcessWrapper getCurrentWrapper(long aProcessId)
   {
      return getInMemoryMap().get(aProcessId);
   }

   /**
    * Returns the effective process limit, which is the greater of the
    * configured maximum process count or the number of processes required for
    * recovery.
    */
   protected int getEffectiveProcessLimit()
   {
      return getProcessManager().getEffectiveProcessLimit();
   }

   /**
    * Accessor for in-memory map of process instances.
    */
   protected Map<Long,AeProcessWrapper> getInMemoryMap()
   {
      return mInMemoryMap;
   }

   /**
    * Returns the process manager that owns this process wrapper map.
    */
   protected IAePersistentProcessManager getProcessManager()
   {
      return mProcessManager;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMap#getWrapper()
    */
   public synchronized AeProcessWrapper getWrapper()
   {
      return getWrapper(mNextTempId--);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMap#getWrapper(long)
    */
   public synchronized AeProcessWrapper getWrapper(long aProcessId)
   {
      AeProcessWrapper wrapper = getInMemoryMap().get(aProcessId);
      if (wrapper == null)
      {
         // Wait until there are fewer than the maximum number of processes.
         waitUntilFewerThan(getEffectiveProcessLimit());

         // If we haven't created one for this process yet then create a brand new wrapper.
         if((wrapper = getInMemoryMap().get(aProcessId)) == null)
         {
            wrapper = new AeProcessWrapper(aProcessId);
            getInMemoryMap().put(aProcessId, wrapper);
         }
      }

      // Add a reference until next call to releaseWrapper().
      wrapper.incrementCount();
      return wrapper;
   }

   /**
    * @return <code>true</code> if and only if the process manager is in debug
    * mode.
    */
   protected static boolean isDebug()
   {
      return AePersistentProcessManager.isDebug();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMap#releaseWrapper(org.activebpel.rt.bpel.server.engine.process.AeProcessWrapper)
    */
   public synchronized void releaseWrapper(AeProcessWrapper aWrapper)
   {
      // If there are no more references to the wrapper, then discard it.
      if (aWrapper.decrementCount() == 0)
      {
         // This method is synchronized, so that getWrapper() won't return a
         // wrapper just after we remove it from the map here. In other words,
         // getWrapper() must only return wrappers that are in the map.
         getInMemoryMap().remove(aWrapper.getProcessId());

         // Notify waitUntilFewerThan().
         notifyAll();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMap#setCallback(org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMapCallback)
    */
   public void setCallback(IAeProcessWrapperMapCallback aCallback)
   {
      mCallback = aCallback;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMap#waitUntilEmpty()
    */
   public void waitUntilEmpty()
   {
      waitUntilFewerThan(1);
   }

   /**
    * Waits until there are fewer than the specified number of processes.
    */
   protected synchronized void waitUntilFewerThan(int aCount)
   {
      if (getInMemoryMap().size() >= aCount)
      {
         // Notify the callback, and perhaps it will release some entries.
         callNotifyProcessWrapperMapFull();

         debug("waitUntilFewerThan({0,number,0}): waiting", aCount); //$NON-NLS-1$

         while (getInMemoryMap().size() >= aCount)
         {
            try
            {
               wait();
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
         }

         debug("waitUntilFewerThan({0,number,0}): resumed", aCount); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMap#setProcessWrapperProcess(org.activebpel.rt.bpel.server.engine.process.AeProcessWrapper, org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public synchronized void setProcessWrapperProcess(AeProcessWrapper aWrapper, IAeBusinessProcess aProcess)
   {
      getInMemoryMap().remove(aWrapper.getProcessId());
      aWrapper.setProcess(aProcess);
      getInMemoryMap().put(aWrapper.getProcessId(), aWrapper);
   }
}
