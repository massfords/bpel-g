// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/lock/AeVariableLocker.java,v 1.8.4.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.lock;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

/**
 * Provides a locking facility for use in controlling access to variables when
 * there are serializable scopes in the process. Serializable scopes require
 * exclusive access to variables such that no other activity can read or write
 * the variable data. This locking facility supports exclusive locks as well as
 * shared locks. Shared locks are obtained by activities that exist outside the
 * context of a serializable scope prior to their execution. These shared locks
 * will serve to prevent an exlusive lock from being obtained but allow other
 * shared locks to be obtained on the same variable. An exclusive lock does not
 * allow more than one owner and will not be installed unless it's the only lock
 * holder for the variable.
 *
 * If an activity is unable to obtain the lock it requires (either shared or
 * exclusive) then it will not enter its ready to execute state until it receives
 * a callback from this class via the IAeVariableLockCallback interface.
 */
public class AeVariableLocker implements IAeVariableLocker
{
   /** Map of variable paths to their lock holders */
   private Map mLockedPaths = new HashMap();

   /** Map of the failed lock requests that are awaiting an unlock to continue */
   private Map mFailedLockRequests = new HashMap();

   /** Map of the owner paths to the variables that they have locked. */
   private Map mOwnersToVariablesLocked = new HashMap();

   /**
    * Returns true if all of the variables in the set were able to be
    * locked. If one or more were already locked by a previous call then the
    * method will return false and the callback instance we be notified when
    * the variables have been locked.
    * @param aSetOfVariablePaths The location paths of the variables you want to lock
    * @param aOwnerXPath The location path of the caller
    * @param aCallback The callback object that gets notified when the lock is
    *                  acquired if it's not immediately available
    */
   public synchronized boolean addExclusiveLock(Set aSetOfVariablePaths, String aOwnerXPath, IAeVariableLockCallback aCallback)
   {
      if (AeUtil.isNullOrEmpty(aSetOfVariablePaths))
         return true;

      AeLockRequest request = new AeExclusiveLockRequest(this, aSetOfVariablePaths, aOwnerXPath, aCallback);
      return request.acquireLock();
   }

   /**
    * Returns true if all of the variables in the set were able to be
    * locked. Shared locks will be denied only if an exclusive lock is present
    * on the variable being requested.
    * @param aSetOfVariablePaths The location paths of the variables you want to lock
    * @param aOwnerXPath The location path of the caller
    * @param aCallback The callback object that gets notified when the lock is
    *                  acquired if it's not immediately available
    */
   public synchronized boolean addSharedLock(Set aSetOfVariablePaths, String aOwnerXPath, IAeVariableLockCallback aCallback)
   {
      if (AeUtil.isNullOrEmpty(aSetOfVariablePaths))
         return true;

      AeSharedLockRequest request = new AeSharedLockRequest(this, aSetOfVariablePaths, aOwnerXPath, aCallback);
      return request.acquireLock();
   }

   /**
    * Called by an activity once it has finished executing or has faulted. This unlocks
    * all of the variables in the set and will possibly result in notifying
    * one or more of the waiting callbacks.
    * @param aOwner - the owner of the lock
    */
   public synchronized void releaseLocks(String aOwner) throws AeBusinessProcessException
   {
      Set set = (Set) mOwnersToVariablesLocked.remove(aOwner);
      
      // TODO (MF) look into avoiding acquiring locks if object in final state which is what led me here in the first place
      mFailedLockRequests.remove(aOwner);

      if (set != null)
      {
         removeLocks(set, aOwner);
         retryFailedRequests();
      }
   }

   /**
    * Walks the set of variable paths being unlocked and removes them
    * from their lock holders. If the removal causes a lock holder to become
    * empty then it is removed from the map of locked variables.
    * @param aSetOfVariablePaths - A set of variable location paths
    * @param aOwner - location path for the lock owner being removed
    */
   private void removeLocks(Set aSetOfVariablePaths, String aOwner)
   {
      for (Iterator iter = aSetOfVariablePaths.iterator(); iter.hasNext();)
      {
         String variablePath = (String) iter.next();
         AeLockHolder lockHolder = getLockHolder(variablePath);
         // should never be null
         lockHolder.remove(aOwner);
         if (lockHolder.isEmpty())
         {
            mLockedPaths.remove(variablePath);
         }
      }
   }

   /**
    * Retries all of the existing failed requests. This gets called each time
    * an activity releases its locks.
    */
   private void retryFailedRequests() throws AeBusinessProcessException
   {
      if (!mFailedLockRequests.isEmpty())
      {
         Map map = new HashMap(mFailedLockRequests);
         mFailedLockRequests.clear();
         for (Iterator iter = map.values().iterator(); iter.hasNext();)
         {
            AeLockRequest request = (AeLockRequest) iter.next();
            if (request.acquireLock())
            {
               request.getCallback().variableLocksAcquired(request.getOwner());
            }
         }
      }
   }

   /**
    * Gets the lock holder for the path specified.
    * @param aVariablePath - path to the variable
    */
   AeLockHolder getLockHolder(String aVariablePath)
   {
      return (AeLockHolder) mLockedPaths.get(aVariablePath);
   }

   /**
    * Adds a lock holder.
    *
    * @param aVariablePath
    * @param aOwnerPath
    * @param aExclusive
    */
   void addLockHolder(String aVariablePath, String aOwnerPath, boolean aExclusive)
   {
      // Add variable->owner mapping (via AeLockHolder).
      AeLockHolder lockHolder = (AeLockHolder) mLockedPaths.get(aVariablePath);

      if (lockHolder == null)
      {
         lockHolder = new AeLockHolder();
         mLockedPaths.put(aVariablePath, lockHolder);
      }

      lockHolder.add(aOwnerPath);
      lockHolder.setExclusive(aExclusive);

      // Add owner->variable mapping.
      Set variablePaths = (Set) mOwnersToVariablesLocked.get(aOwnerPath);

      if (variablePaths == null)
      {
         variablePaths = new HashSet();
         mOwnersToVariablesLocked.put(aOwnerPath, variablePaths);
      }

      variablePaths.add(aVariablePath);
   }

   /**
    * Adds a lock request to the set of lock requests to retry.
    *
    * @param aOwnerPath
    * @param aLockRequest
    */
   void addLockRequest(String aOwnerPath, AeLockRequest aLockRequest)
   {
      // Note that this blows away any existing lock request by this owner.
      mFailedLockRequests.put(aOwnerPath, aLockRequest);
   }

   /**
    * Clears all locks (for deserialization).
    */
   void clearLocks()
   {
      mLockedPaths.clear();
      mOwnersToVariablesLocked.clear();
   }

   /**
    * Clears all outstanding lock requests (for deserialization).
    */
   void clearRequests()
   {
      mFailedLockRequests.clear();
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object o)
   {
      if (!(o instanceof AeVariableLocker))
      {
         return false;
      }

      AeVariableLocker other = (AeVariableLocker) o;

      return mLockedPaths.equals(other.mLockedPaths)
          && mFailedLockRequests.equals(other.mFailedLockRequests)
          && mOwnersToVariablesLocked.equals(other.mOwnersToVariablesLocked)
          ;
   }

   /**
    * Returns set of locked variable paths (for serialization).
    */
   Set getLockedPaths()
   {
      return Collections.unmodifiableSet(mLockedPaths.keySet());
   }

   /**
    * Returns collection of outstanding lock requests (for serialization).
    */
   Collection getLockRequests()
   {
      return Collections.unmodifiableCollection(mFailedLockRequests.values());
   }

   /**
    * Returns serialization of this variable locker suitable for input to
    * <code>setLockerData</code>.
    *
    * @param aCallback which callback to serialize lock requests for
    * @return DocumentFragment the serialized locker data
    */
   public synchronized DocumentFragment getLockerData(IAeVariableLockCallback aCallback) throws AeBusinessProcessException
   {
      AeLockerSerializer serializer = new AeLockerSerializer(this, aCallback);

      try
      {
         return serializer.serialize();
      }
      catch (AeException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeVariableLocker.ERROR_0"), e); //$NON-NLS-1$
      }
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return mLockedPaths.hashCode()
           + mFailedLockRequests.hashCode()
           + mOwnersToVariablesLocked.hashCode()
           ;
   }

   /**
    * Returns <code>true</code> if and only if the specified variable path is locked.
    *
    * @param aVariablePath
    */
   boolean isLocked(String aVariablePath)
   {
      return mLockedPaths.containsKey(aVariablePath);
   }

   /**
    * Sets this variable locker's data from a serialization produced by
    * <code>getLockerData</code>.
    *
    * @param aNode the serialized locker data
    * @param aCallback which callback to use when reconstructing lock requests
    */
   public synchronized void setLockerData(Node aNode, IAeVariableLockCallback aCallback) throws AeBusinessProcessException
   {
      AeLockerDeserializer deserializer = new AeLockerDeserializer(this, aCallback);

      try
      {
         deserializer.deserialize(aNode);
      }
      catch (AeException e)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeVariableLocker.ERROR_1"), e); //$NON-NLS-1$
      }
   }
}
