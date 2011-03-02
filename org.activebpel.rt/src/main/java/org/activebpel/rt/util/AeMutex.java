// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeMutex.java,v 1.2 2006/05/01 19:47:50 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import org.activebpel.rt.AeMessages;

/**
 * Implements mutual exclusion monitor that supports reentrant locking.
 */
public class AeMutex
{
   /** <code>true</code> if and only if debug capabilities enabled. */
   private static boolean sDebug = false;
   
   /** The next available mutex id. */
   private static int sNextMutexId = 1;

   /** The internal id for this mutex. Used by {@link #toString()}. */
   private final int mMutexId = getNextMutexId();

   /**
    * The ownership count, which is the number of times that the owner must
    * call {@link #releaseMutex()} to relinquish ownership. The count is
    * <code>0</code> if no thread owns the mutex.
    */
   private int mOwnershipCount = 0;

   /**
    * The thread that owns this mutex or <code>null</code> if no thread owns
    * the mutex.
    */
   private Thread mOwner = null;

   /** Diagnostic stack trace. */
   private AeMutexStackTrace mStackTrace;

   /**
    * Acquires exclusive ownership of this mutex, blocking if necessary until
    * the current owner relinquishes ownership.
    */
   public synchronized void acquireMutex()
   {
       // Wait while any other thread has ownership.
       while ((mOwnershipCount > 0) && (mOwner != Thread.currentThread()))
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

       // Increment ownership count.
       if (++mOwnershipCount == 1)
       {
          // We just acquired ownership.
          mOwner = Thread.currentThread();

          // Set diagnostic stack trace.
          if (isDebug())
          {
             setStackTrace(new AeMutexStackTrace(toString() + " acquired by " + mOwner)); //$NON-NLS-1$
          }
       }
   }

   /**
    * Returns next available mutex id.
    */
   protected static synchronized int getNextMutexId()
   {
      return sNextMutexId++;
   }

   /**
    * Returns the current ownership count.
    */
   public int getOwnershipCount()
   {
      return mOwnershipCount;
   }

   /**
    * Returns debug flag.
    */
   protected static boolean isDebug()
   {
      return sDebug;
   }

   /**
    * Prints diagnostic stack trace, if we have one.
    */
   protected void printStackTrace()
   {
      if (isDebug() && (mStackTrace != null))
      {
         mStackTrace.printStackTrace();
      }
   }

   /**
    * Releases ownership of this mutex.
    */
   public synchronized void releaseMutex()
   {
      if (mOwnershipCount <= 0)
      {
         // Reset it to a sane value.
         mOwnershipCount = 0;

         // Show last valid release stack trace.
         printStackTrace();

         throw new IllegalStateException(AeMessages.getString("AeMutex.ERROR_UNBALANCED_RELEASE")); //$NON-NLS-1$
      }

      if (mOwner != Thread.currentThread())
      {
         // Show owner's acquisition stack trace.
         printStackTrace();

         throw new IllegalStateException(AeMessages.getString("AeMutex.ERROR_ILLEGAL_RELEASE")); //$NON-NLS-1$
      }
      
      // Decrement ownership count.
      if (--mOwnershipCount == 0)
      {
         // We just relinquished ownership.
         mOwner = null;
         notifyAll();

         // Update diagnostic stack trace.
         if (isDebug())
         {
            setStackTrace(new AeMutexStackTrace(toString() + " released by " + Thread.currentThread())); //$NON-NLS-1$
         }
      }
   }

   /**
    * Sets debug flag.
    */
   public static void setDebug(boolean aDebug)
   {
      sDebug = aDebug;
   }

   /**
    * Sets diagnostic stack trace.
    */
   protected void setStackTrace(AeMutexStackTrace aStackTrace)
   {
      mStackTrace = aStackTrace;
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return "AeMutex[" + mMutexId + "]"; //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Wraps a message and stack trace together.
    */
   protected static class AeMutexStackTrace extends Throwable
   {
      public AeMutexStackTrace(String aMessage)
      {
         super(aMessage);
      }
   }
}