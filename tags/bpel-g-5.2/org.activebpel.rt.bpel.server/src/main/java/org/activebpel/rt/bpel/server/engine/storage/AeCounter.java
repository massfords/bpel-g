// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeCounter.java,v 1.10 2007/04/23 23:38:57 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.rmi.RemoteException;

import org.activebpel.rt.bpel.server.AeMessages;

/**
 * Implements persistent counters.
 */
public class AeCounter
{
   /** The persistent counter store. */
   private IAeCounterStore mCounterStore;

   /** Name of this counter. */
   private String mCounterName;

   /** Block size for grabbing values from database. */
   private int mBlockSize = 100;

   /** Next value in the current block for this counter. */
   private long mNextValue;

   /** First value past the current block of values for this counter. */
   private long mEndOfValues;

   /**
    * Returns source for counter values.
    */
   public IAeCounterStore getCounterStore() 
   {
      return mCounterStore;
   }
   
   public void setCounterStore(IAeCounterStore aStore) {
       mCounterStore = aStore;
   }

   /**
    * Returns next value for this counter.
    *
    * @return long
    * @throws AeStorageException
    */
   public synchronized long getNextValue() throws AeStorageException
   {
      // If the counter has reached the end of the current block of values,
      // then grab a new block of values from the counter values source.
      if (mNextValue >= mEndOfValues)
      {
	      mNextValue = getNextValues();
	      mEndOfValues = mNextValue + getBlockSize();
      }

      return mNextValue++;
   }

   /**
    * Returns next block of values from the counter values source.
    *
    * @return long first new value in block
    * @throws AeStorageException
    */
   protected long getNextValues() throws AeStorageException
   {
      try
      {
         return getCounterStore().getNextValues(getCounterName(), getBlockSize());
      }
      catch (RemoteException e)
      {
         throw new AeStorageException(AeMessages.format("AeCounter.ERROR_0", getCounterName()), e); //$NON-NLS-1$
      }
   }

    public String getCounterName() {
        return mCounterName;
    }
    
    public void setCounterName(String aCounterName) {
        mCounterName = aCounterName;
    }
    
    public int getBlockSize() {
        return mBlockSize;
    }
    
    public void setBlockSize(int aBlockSize) {
        mBlockSize = aBlockSize;
    }
}
