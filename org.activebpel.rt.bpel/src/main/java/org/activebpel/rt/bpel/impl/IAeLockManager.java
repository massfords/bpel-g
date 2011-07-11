// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeLockManager.java,v 1.4 2004/12/30 22:28:15 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * A generic lock manager that provides a means of acquiring and releasing locks
 * based on a unique resource name and hash value.
 */
public interface IAeLockManager extends IAeManager
{
   /**
    * Called when a lock needs to be acquired. Should always be used in conjunction
    * with the releaseLock method in a finally block.
    * 
    * @param aQName Qualified name of the key
    * @param aLockId id of the value we're trying to lock (should be unique w/in the qname provided)
    * @param aTimeout max number of seconds to wait to acquire lock
    * @return IAeLock The lock that was acquired, should be used in the call to release lock.
    * @throws AeBusinessProcessException
    */
   public IAeLock acquireLock(QName aQName, int aLockId, int aTimeout) throws AeBusinessProcessException;

   /**
    * Called to release a lock that it had previously acquired for a qname and hash value.
    * 
    * @param aLock 
    */
   public void releaseLock(IAeLock aLock);
   
   /**
    * Value returned from the acquire lock call. 
    */
   public interface IAeLock 
   {
      /**
       * Gets the qname of the lock that was acquired.
       */
      public QName getQName();
      
      /**
       * Gets the value of the hashcode of the lock that was acquired
       */
      public int getLockId();
   }
}
