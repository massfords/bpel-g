// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeAbstractProcessManager.java,v 1.2 2007/05/24 00:50:18 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.activebpel.rt.AeException;

/**
 * Implements a simple in-memory process manager.
 */
public abstract class AeAbstractProcessManager extends AeManagerAdapter implements IAeProcessManager
{
   public static final String CONFIG_DEBUG = "Debug"; //$NON-NLS-1$

   /** <code>true</code> if and only if showing debug output. */
   private static boolean sDebug = false;

   /** Process purged listeners */
   private Set<IAeProcessPurgedListener> mProcessPurgedListeners = new CopyOnWriteArraySet<IAeProcessPurgedListener>();

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#addProcessPurgedListener(org.activebpel.rt.bpel.impl.IAeProcessPurgedListener)
    */
   public void addProcessPurgedListener(IAeProcessPurgedListener aListener)
   {
      getProcessPurgedListeners().add(aListener);
   }

   /**
    * Writes debugging output.
    */
   public void debug(String aMessage)
   {
      if (isDebug())
      {
         System.out.println(aMessage);
      }
   }

   /**
    * Writes formatted debugging output.
    */
   public void debug(String aPattern, long aArgument)
   {
      debug(aPattern, new Object[] { new Long(aArgument) });
   }

   /**
    * Writes formatted debugging output.
    */
   public void debug(String aPattern, Object[] aArguments)
   {
      if (isDebug()) // test for debugging before formatting
      {
         debug(MessageFormat.format(aPattern, aArguments));
      }
   }

   /**
    * Notifies all process purged listeners that the given process has been
    * purged by this process manager.
    *
    * @param aProcessId
    */
   protected void fireProcessPurged(long aProcessId)
   {
      for (Iterator i = getProcessPurgedListeners().iterator(); i.hasNext(); )
      {
         try
         {
            ((IAeProcessPurgedListener) i.next()).processPurged(aProcessId);
         }
         catch (Throwable t)
         {
            // Just log exception from listeners, because we should not let them
            // impact us in any way.
            AeException.logError(t);
         }
      }
   }

   /**
    * Returns the process purged listeners for this process manager.
    */
   protected Set<IAeProcessPurgedListener> getProcessPurgedListeners()
   {
      return mProcessPurgedListeners;
   }

   /**
    * @return <code>true</code> if and only if the process manager is in debug
    * mode.
    */
   public static boolean isDebug()
   {
      return sDebug;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessManager#removeProcessPurgedListener(org.activebpel.rt.bpel.impl.IAeProcessPurgedListener)
    */
   public void removeProcessPurgedListener(IAeProcessPurgedListener aListener)
   {
      getProcessPurgedListeners().remove(aListener);
   }
}
