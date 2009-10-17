//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/transreceive/AePersistentTransmissionTracker.java,v 1.3 2007/01/25 21:38:12 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.transreceive;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.server.engine.storage.AePersistentStoreFactory;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeTransmissionTrackerStorage;
import org.activebpel.rt.util.AeLongSet;

/**
 * Implements the persistent version of a transmission manager which is 
 * required for durable invokes and durable replies.
 *
 */
public class AePersistentTransmissionTracker extends AeInMemoryTransmissionTracker
{

   /** reference to the storage. */
   private IAeTransmissionTrackerStorage mStorage;
   
   /**
    * Default ctor.
    * @param aConfig
    */
   public AePersistentTransmissionTracker(Map aConfig) throws AeException
   {
      super(aConfig);
      setStorage(AePersistentStoreFactory.getInstance().getTransmissionTrackerStorage());
   }
   
    /**
     * @return Returns the storage.
     */
    protected IAeTransmissionTrackerStorage getStorage()
    {
       return mStorage;
    }

    /**
     * @param aStorage The storage to set.
     */
    protected void setStorage(IAeTransmissionTrackerStorage aStorage)
    {
       mStorage = aStorage;
    }    

    /** 
     * Overrides method to return id from the storage layer.
     * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getNextId()
     */
    public long getNextId()
    {
       try
       {
          return getStorage().getNextTransmissionId();
       }
       catch(Exception e)
       {
          AeException.logError(e);
          return IAeTransmissionTracker.NULL_TRANSREC_ID;
       }
    }
    
    /** 
     * Overrides method to return entry from storage layer. 
     * @see org.activebpel.rt.bpel.server.transreceive.AeInMemoryTransmissionTracker#getEntry(long)
     */
    protected AeTransmissionTrackerEntry getEntry(long aTransmissionId) throws AeStorageException
    {
       return getStorage().get(aTransmissionId);
    }
    
    /**
     * @see org.activebpel.rt.bpel.server.transreceive.AeInMemoryTransmissionTracker#addEntry(org.activebpel.rt.bpel.server.transreceive.AeTransmissionTrackerEntry)
     */
    protected void addEntry(AeTransmissionTrackerEntry aEntry) throws AeStorageException
    {
       getStorage().add(aEntry);
    }
    
    /**
     * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#update(long, int)
     */
    public void update(long aTransmissionId, int aState) throws AeException
    {
       AeTransmissionTrackerEntry entry = new AeTransmissionTrackerEntry(aTransmissionId, aState);
       getStorage().update(entry);
    }
    
    /**
     * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#remove(long)
     */
    public void remove(long aTransmissionId) throws AeException
    {
       getStorage().remove(aTransmissionId);
    }    
    
    /**
     * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#remove(org.activebpel.rt.util.AeLongSet)
     */
    public void remove(AeLongSet aTransmissionIds) throws AeException
    {
       getStorage().remove(aTransmissionIds);
    }    
}
