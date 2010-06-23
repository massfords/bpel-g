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
   private static final int PROCESS_ID_BLOCK_SIZE      = 100;
   private static final int JOURNAL_ID_BLOCK_SIZE      = 100;
   private static final int COORDINATION_PK_BLOCK_SIZE = 100;
   private static final int COORDINATION_ID_BLOCK_SIZE = 100;
   private static final int QUEUED_RECEIVE_ID_BLOCK_SIZE = 100;
   private static final int TRANSMISSION_ID_BLOCK_SIZE = 100;
   private static final int ATTACHMENT_GROUP_ID_BLOCK_SIZE = 100;
   private static final int ATTACHMENT_ITEM_ID_BLOCK_SIZE = 100;
   

   /** The global engine id counter. */
   public static final AeCounter ENGINE_ID_COUNTER           = new AeCounter("EngineId"); //$NON-NLS-1$

   /** The global process id counter. */
   public static final AeCounter PROCESS_ID_COUNTER          = new AeCounter("ProcessId", PROCESS_ID_BLOCK_SIZE); //$NON-NLS-1$

   /** The global queued receive id counter. */
   public static final AeCounter QUEUED_RECEIVE_ID_COUNTER   = new AeCounter("QueuedReceiveId", QUEUED_RECEIVE_ID_BLOCK_SIZE); //$NON-NLS-1$

   /** The global hash collision counter. */
   public static final AeCounter HASH_COLLISION_COUNTER      = new AeCounter("HashCollisions"); //$NON-NLS-1$

   /** The lock hash collision counter. */
   public static final AeCounter LOCK_HASH_COLLISION_COUNTER = new AeCounter("LockHashCollisions"); //$NON-NLS-1$

   /** The global journal id counter. */
   public static final AeCounter JOURNAL_ID_COUNTER          = new AeCounter("JournalId", JOURNAL_ID_BLOCK_SIZE); //$NON-NLS-1$

   /** The coordination primary key. */
   public static final AeCounter COORDINATION_PK_COUNTER     = new AeCounter("CoordinationPk", COORDINATION_PK_BLOCK_SIZE); //$NON-NLS-1$
   
   /** The coordination id counter. */
   public static final AeCounter COORDINATION_ID_COUNTER     = new AeCounter("CoordinationId", COORDINATION_ID_BLOCK_SIZE); //$NON-NLS-1$

   /** A SQL counter for the license id. */
   public static final AeCounter LICENSE_ID_COUNTER          = new AeCounter("LicenseId"); //$NON-NLS-1$
   
   /** The coordination id counter. */
   public static final AeCounter TRANSMISSION_ID_COUNTER     = new AeCounter("TransmissionId", TRANSMISSION_ID_BLOCK_SIZE); //$NON-NLS-1$
   
   /** A SQL counter for attachment groups. */
   public static final AeCounter ATTACHMENT_GROUP_ID_COUNTER = new AeCounter("AttachmentGroupId",ATTACHMENT_GROUP_ID_BLOCK_SIZE); //$NON-NLS-1$
   
   /** A SQL counter for attachment item ids. */
   public static final AeCounter ATTACHMENT_ITEM_ID_COUNTER  = new AeCounter("AttachmentItemId",ATTACHMENT_ITEM_ID_BLOCK_SIZE); //$NON-NLS-1$
   

   /** The persistent counter store. */
   private static IAeCounterStore sCounterStore;

   /** Name of this counter. */
   private final String mCounterName;

   /** Block size for grabbing values from database. */
   private int mBlockSize;

   /** Next value in the current block for this counter. */
   private long mNextValue;

   /** First value past the current block of values for this counter. */
   private long mEndOfValues;

   /**
    * Constructs counter with default block size of <code>1</code>.
    *
    * @param aCounterName
    */
   public AeCounter(String aCounterName)
   {
      this(aCounterName, 1);
   }

   /**
    * Constructs counter with specified block size.
    *
    * @param aCounterName
    * @param aBlockSize
    */
   public AeCounter(String aCounterName, int aBlockSize)
   {
      mCounterName = aCounterName;
      mBlockSize = aBlockSize;
   }

   /**
    * Returns block size for this counter.
    */
   protected int getBlockSize()
   {
      return mBlockSize;
   }

   /**
    * Returns source for counter values.
    */
   protected static IAeCounterStore getCounterStore() throws AeStorageException
   {
      if (sCounterStore == null)
      {
         sCounterStore = AePersistentStoreFactory.getInstance().getCounterStore();
      }

      return sCounterStore;
   }

   /**
    * Returns this counter's name.
    *
    * @return String
    */
   protected String getName()
   {
      return mCounterName;
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
         return getCounterStore().getNextValues(getName(), getBlockSize());
      }
      catch (RemoteException e)
      {
         throw new AeStorageException(AeMessages.format("AeCounter.ERROR_0", getName()), e); //$NON-NLS-1$
      }
   }
}
