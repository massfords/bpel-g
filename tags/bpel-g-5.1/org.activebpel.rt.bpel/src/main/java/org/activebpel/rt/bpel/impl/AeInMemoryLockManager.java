// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeInMemoryLockManager.java,v 1.9 2005/04/15 13:27:00 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * This class implements a simple in-memory process def lock manager.
 */
public class AeInMemoryLockManager extends AeAbstractLockManager
{
   /** Contains all currently acquired locks. */
   private Set mLocks;

   /**
    * Constructs a new in-memory lock manager.
    * 
    * @param aConfig The configuration map for this manager.
    */
   public AeInMemoryLockManager(Map aConfig)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#create()
    */
   public void create() throws Exception
   {
      super.create();
      
      mLocks = new HashSet();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#stop()
    */
   public void stop()
   {
      mLocks.clear();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractLockManager#acquireLockInternal(org.activebpel.rt.bpel.impl.IAeLockManager.IAeLock)
    */
   protected boolean acquireLockInternal(IAeLock aLock)
         throws AeBusinessProcessException
   {
      synchronized (mLocks)
      {
         if (!mLocks.contains(aLock))
         {
            mLocks.add(aLock);
            return true;
         }
      }
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractLockManager#releaseLockInternal(org.activebpel.rt.bpel.impl.IAeLockManager.IAeLock)
    */
   protected void releaseLockInternal(IAeLock aLock)
   {
      synchronized (mLocks)
      {
         mLocks.remove(aLock);
      }
   }
}

