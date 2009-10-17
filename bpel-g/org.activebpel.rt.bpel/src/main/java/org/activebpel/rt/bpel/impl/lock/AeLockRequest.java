// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/lock/AeLockRequest.java,v 1.2 2004/07/08 13:10:00 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.lock;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Describes the request for a variable locking. The request is executed through
 * the acquireLock() method. If the lock is able to be acquired immediately then
 * lock holders will be installed for the locked variables. If not, then the
 * request is added to the failed request collection in the outter class for
 * an attempt at a later time.
 */
public abstract class AeLockRequest
{
   protected final AeVariableLocker mVariableLocker;
   /** The set of variable paths that we want to lock */
   protected Set mVariablesToLock;
   /** The callback that gets used if we can't fulfill the request immediately */
   protected IAeVariableLockCallback mCallback;
   /** The path of the object that will be the owner of the lock */
   protected String mOwner;

   /**
    * Creates a lock request with all of its required data.
    * @param aVariablesToLock - The set of variable paths that we want to lock
    * @param aOwner - The path of the object that will be the owner of the lock
    * @param aCallback - The callback that gets used if we can't fulfill the request immediately
    */
   public AeLockRequest(AeVariableLocker aVariableLocker, Set aVariablesToLock, String aOwner, IAeVariableLockCallback aCallback)
   {
      mVariableLocker = aVariableLocker;
      mVariablesToLock = aVariablesToLock;
      mCallback = aCallback;
      mOwner = aOwner;
   }

   /**
    * Returns the object that's trying to own the lock on the variables.
    */
   public String getOwner()
   {
      return mOwner;
   }

   /** Returns true if all of the requested variables can be immediately locked */
   protected abstract boolean canLock();

   /** Returns true if the request is for an exclusive lock */
   public abstract boolean isExclusiveRequest();

   /**
    * Template method that dictates the structure of acquiring a lock. First we
    * see if it's possible to lock all of the variables in question. If so,
    * the locks are added otherwise we add ourselves to the failed requests collection.
    */
   protected boolean acquireLock()
   {
      boolean immediatelyAvailable = canLock();

      if (immediatelyAvailable)
      {
         addLockHolders();
      }
      else
      {
         mVariableLocker.addLockRequest(getOwner(), this);
      }

      return immediatelyAvailable;
   }

   /**
    * Walks the set of variables to lock and adds the owner as a lock
    * holder for each of the variables. If there is no lock holder installed
    * for the variable then one is created.
    */
   protected void addLockHolders()
   {
      for (Iterator iter = mVariablesToLock.iterator(); iter.hasNext();)
      {
         String variablePath = (String) iter.next();
         mVariableLocker.addLockHolder(variablePath, getOwner(), isExclusiveRequest());
      }
   }

   /** Getter for the callback */
   public IAeVariableLockCallback getCallback()
   {
      return mCallback;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object o)
   {
      if (!(o instanceof AeLockRequest))
      {
         return false;
      }

      AeLockRequest other = (AeLockRequest) o;

      if (isExclusiveRequest() != other.isExclusiveRequest())
      {
         return false;
      }

      if ((getCallback() == null) && (other.getCallback() != null))
      {
         return false;
      }

      return getVariablesToLock().equals(other.getVariablesToLock())
          && getCallback().equals(other.getCallback())
          && getOwner().equals(other.getOwner())
          ;
   }

   /**
    * Returns set of paths of variables to lock.
    */
   public Set getVariablesToLock()
   {
      return Collections.unmodifiableSet(mVariablesToLock);
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return getVariablesToLock().hashCode()
           + ((getCallback() != null) ? getCallback().hashCode() : 0)
           + getOwner().hashCode()
           + (isExclusiveRequest() ? 1 : 0)
           ;
   }
}
