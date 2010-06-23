//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeAbstractLockManager.java,v 1.3 2005/02/08 15:33:10 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl; 

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;

/**
 * A reentrant implementation of the lock manager interface that uses reference
 * counts to determine when to free locks. The underlying locking mechanism is
 * left to be implemented through the <code>acquireLockInternal</code> and
 * <code>releaseLockInternal</code> methods.
 */
public abstract class AeAbstractLockManager extends AeManagerAdapter implements IAeLockManager
{
   /** Map of maps that tracks what threads own what locks. Outer map is thread to lock map. Inner map is simply lock to lock. */
   private Map mThreadsToLocks = new Hashtable();
   
   /**
    * Template method that attempts to acquire the lock for the specified timeout period.
    * 
    * @see org.activebpel.rt.bpel.impl.IAeLockManager#acquireLock(javax.xml.namespace.QName, int, int)
    */
   public final IAeLock acquireLock(QName aQName, int aLockId, int aTimeout) throws AeBusinessProcessException
   {
      AeLock lock = new AeLock(aQName, aLockId);
      
      if (threadOwnsLock(lock))
         return lock;
      
      boolean lockAcquired = false;
      int numTries = 0;
      long sleepInterval = getSleepInterval();
      long timeout = aTimeout * 1000;
      do 
      {
         numTries++;
         
         lockAcquired = acquireLockInternal(lock);
         
         if (!lockAcquired)
         {
            try { Thread.sleep(sleepInterval); } catch (InterruptedException ie) {/*Nothing to do.*/ }
         }
      } 
      while (!lockAcquired && (numTries * sleepInterval)< timeout);

      // If we failed to get the lock, throw an exception.
      if (!lockAcquired)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeAbstractLockManager.ERROR_0")); //$NON-NLS-1$
      }
      else
      {
         bindLock(lock);
      }
      return lock;
   }
   
   /**
    * If the reference count for the lock reaches zero then the impl here will 
    * call the <code>releaseLockInternal</code> method.
    * 
    * @see org.activebpel.rt.bpel.impl.IAeLockManager#releaseLock(org.activebpel.rt.bpel.impl.IAeLockManager.IAeLock)
    */
   public final void releaseLock(IAeLock aLock)
   {
      boolean noReferencesLeft = unbindLock(aLock);
      if (noReferencesLeft)
         releaseLockInternal(aLock);
   }
   
   /**
    * Gets the amount of time that the lock manager thread will sleep in between attempts
    * to acquire the lock.
    * 
    * @return amount of time to sleep in between attempts to acquire lock
    */
   protected long getSleepInterval()
   {
      return 1000;
   }
   
   /**
    * Called by <code>releaseLock</code> when the reference count for the lock
    * reaches zero.
    * 
    * @param aLock
    */
   protected abstract void releaseLockInternal(IAeLock aLock);

   /**
    * Allows for subclass to obtain lock in its own way.
    * 
    * @param aLock
    * @return true if the lock was acquired
    * @throws AeBusinessProcessException
    */
   protected abstract boolean acquireLockInternal(IAeLock aLock) throws AeBusinessProcessException;
   
   /**
    * Returns true if the calling Thread already owns this lock and increments
    * the reference counter for the lock if already owned.
    * 
    * @param aLock
    */
   protected boolean threadOwnsLock(IAeLock aLock)
   {
      Map locks = getLocksOwnedByThread(false);
      IAeLock lock = null;
      
      if (locks != null)
      {
         lock = (IAeLock) locks.get(aLock);
      }
      
      if (lock != null)
      {
         incrementReferenceCount(lock);
      }
      return lock != null;
   }

   /**
    * Gets the locks owned by thread, creating a map to store the locks
    * if the thread doesn't own any locks and the create flag is true.
    * 
    * @param aCreateIfNull flag controls creation of underlying map if none was found.
    * @return map of locks owned by thread or null if there were none and the flag is false
    */
   protected Map getLocksOwnedByThread(boolean aCreateIfNull)
   {
      Map locks = (Map) mThreadsToLocks.get(Thread.currentThread());
      if (locks == null && aCreateIfNull)
      {
         locks = new HashMap();
         mThreadsToLocks.put(Thread.currentThread(), locks);
      }
      return locks;
   }

   /**
    * Binds the lock to the current thread. Called when the lock has been acquired.
    * 
    * @param aLock
    */
   protected void bindLock(IAeLock aLock)
   {
      Map locks = getLocksOwnedByThread(true);
      AeLock lock = (AeLock) locks.get(aLock);
      if (lock != null)
      {
         // they already own the lock so increment the ref counter on the one found
         incrementReferenceCount(lock);
      }
      else
      {
         // they have just acquired the lock, store it in the map and increment the ref count
         locks.put(aLock, aLock);
         incrementReferenceCount(aLock);
      }
   }
   
   /**
    * Increments the reference count for this lock. Broken out here in order
    * to isolate the cast of the interface to the class.
    * 
    * @param aLock
    */
   protected void incrementReferenceCount(IAeLock aLock)
   {
      ((AeLock)aLock).increment();
   }
   
   /**
    * Decrements the reference count for this lock and returns true if the lock
    * is no longer being referenced. Broken out here in order to isolate the
    * cast of the interface to the class.
    * 
    * @param aLock
    * @return true if the lock is no longer being referenced.
    */
   protected boolean decrementReferenceCount(IAeLock aLock)
   {
      ((AeLock)aLock).decrement();
      return !((AeLock)aLock).isReferenced();
   }

   /**
    * Unbinds the lock from the current thread. If the reference count for the lock
    * goes to zero then the lock will be freed.
    * 
    * @param aLock
    * @return true if there are no more references to the underlying lock
    */
   protected boolean unbindLock(IAeLock aLock)
   {
      boolean noReferencesLeft = true;

      Map map = getLocksOwnedByThread(false);

      if (map == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeAbstractLockManager.ERROR_1")); //$NON-NLS-1$
      }

      IAeLock lock = (IAeLock) map.get(aLock);
      if (lock == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeAbstractLockManager.ERROR_1")); //$NON-NLS-1$
      }

      noReferencesLeft = decrementReferenceCount(lock);
      if (noReferencesLeft)
      {
         map.remove(aLock);
      }

      if (map.isEmpty())
      {
         mThreadsToLocks.remove(Thread.currentThread());
      }
      return noReferencesLeft;
   }
   
   /**
    * This class implements a simple lock object which contains both a QName and correlation
    * hash. It also has a reference counter which is used to keep track of the number of
    * references to the lock. When the reference count reaches zero then the lock can be
    * released.
    */
   public static class AeLock implements IAeLock
   {
      /** qname for the lock */
      private QName mQName;
      /** hash value of the lock */
      private int mHash;
      /** tracks the reference count for the lock. */
      private int mReferenceCount;

      /**
       * Constructs a lock object given a QName and a hash.
       * @param aQName A QName.
       * @param aHash A hash value of the object being locked.
       */
      public AeLock(QName aQName, int aHash)
      {
         mQName = aQName;
         mHash = aHash;
      }

      /**
       * @see java.lang.Object#equals(java.lang.Object)
       */
      public boolean equals(Object aLock)
      {
         AeLock lock2 = (AeLock) aLock;
         return getQName().equals(lock2.getQName()) && (getLockId() == lock2.getLockId());
      }

      /**
       * @see org.activebpel.rt.bpel.impl.IAeLockManager.IAeLock#getLockId()
       */
      public int getLockId()
      {
         return mHash;
      }

      /**
       * Getter for the qname property.
       */
      public QName getQName()
      {
         return mQName;
      }

      /**
       * Setter for the correlation hash property.
       */
      public void setHashcode(int aHash)
      {
         mHash = aHash;
      }

      /**
       * Setter for the qname property.
       */
      public void setQName(QName aName)
      {
         mQName = aName;
      }
      
      /**
       * @see java.lang.Object#hashCode()
       */
      public int hashCode()
      {
         return getQName().hashCode() + mHash;
      }

      /**
       * Increments the reference count. 
       */
      public void increment()
      {
         mReferenceCount++;
      }
      
      /**
       * Decrements the reference count.
       */
      public void decrement()
      {
         mReferenceCount--;
      }
      
      /**
       * Returns true if the counter has at least one reference.
       */
      public boolean isReferenced()
      {
         return mReferenceCount > 0;
      }
   }
}
 