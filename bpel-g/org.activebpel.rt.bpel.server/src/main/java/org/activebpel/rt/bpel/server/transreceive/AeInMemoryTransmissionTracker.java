//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/transreceive/AeInMemoryTransmissionTracker.java,v 1.4 2008/03/11 03:09:29 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.transreceive;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeLongSet;
import org.activebpel.wsio.invoke.IAeTransmission;

/**
 * In memory implementation of the transmission tracker. 
 */
public class AeInMemoryTransmissionTracker extends AeNoopTransmissionTracker
{
   /** In memory map of entries. */
   private Map mEntries;
   
   /** Next transmission id. */
   private long mNextId;
   
   /**
    * Default constructor.
    */
   public AeInMemoryTransmissionTracker(Map aConfig) throws AeException
   {
      super(aConfig);
      mEntries = new HashMap();
      mNextId = 0;
   }   
      
   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getNextId()
    */
   public synchronized long getNextId()
   {
      mNextId++;
      return mNextId;
   }

   /** 
    * @return Returns entries map.0
    */
   protected Map getEntries()
   {
      return mEntries;
   }
   
   /** 
    * Convinience method the access an entry given its id.
    * @param aTransmissionId
    *
    */
   protected AeTransmissionTrackerEntry getEntry(long aTransmissionId) throws AeException
   {
      AeTransmissionTrackerEntry entry = (AeTransmissionTrackerEntry) getEntries().get( new Long(aTransmissionId) );
      return entry;
   }
   
   /**
    * Convinience method to add a new entry.
    * @param aEntry
    */
   protected void addEntry(AeTransmissionTrackerEntry aEntry) throws AeException
   {
      getEntries().put(new Long(aEntry.getTransmissionId()), aEntry); 
   }
      
   /**
    * Adds the message id with the given state. This method returns a unique
    * transmission id.
    * @param aTransmissionId
    * @param aMessageId Invoke handler dependent message id.
    * @param aState transmitted or received state.
    * @throws AeException
    */
   public void add(long aTransmissionId, String aMessageId, int aState) throws AeException
   {
      AeTransmissionTrackerEntry entry = new AeTransmissionTrackerEntry(aTransmissionId, aState, aMessageId);
      addEntry(entry);
   }
   
   /**
    * Returns true if the given transmission id and state already exists.
    * @param aTransmissionId transmission id.
    * @return true if id exists.
    * @throws AeException
    */
   public boolean exists(long aTransmissionId) throws AeException
   {
      return getEntry(aTransmissionId) != null;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#exists(long, int)
    */
   public boolean exists(long aTransmissionId, int aState) throws AeException
   {
      AeTransmissionTrackerEntry entry = getEntry(aTransmissionId);
      return (entry != null && entry.getState() == aState);
   }
   
   /**
    * Updates the state given transmission id.
    * @param aTransmissionId transmission id.
    * @param aState transmitted or received state.
    * @throws AeException
    */
   public void update(long aTransmissionId, int aState) throws AeException
   {
      AeTransmissionTrackerEntry entry = getEntry(aTransmissionId);
      if (entry != null)
      {
         entry.setState(aState);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getState(long)
    */
   public int getState(long aTransmissionId) throws AeException
   {
      AeTransmissionTrackerEntry entry = getEntry(aTransmissionId);
      if (entry != null)
      {
         return entry.getState();
      }
      else
      {
         return IAeTransmissionTracker.NULL_STATE;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#getMessageId(long)
    */
   public String getMessageId(long aTransmissionId) throws AeException
   {
      AeTransmissionTrackerEntry entry = getEntry(aTransmissionId);
      if (entry != null)
      {
         return entry.getMessageId();
      }
      else
      {
         return null;
      }
   }
   
   /**
    * Removed the entry associated with the given id.
    * @param aTransmissionId
    * @throws AeException
    */
   public void remove(long aTransmissionId) throws AeException
   {
      getEntries().remove( new Long(aTransmissionId) );
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#remove(org.activebpel.rt.util.AeLongSet)
    */
   public void remove(AeLongSet aTransmissionIds) throws AeException
   {
      Iterator it = aTransmissionIds.iterator();
      while ( it.hasNext() )
      {
         getEntries().remove( (Long) it.next() );
      }
   }
   
   
   /**
    * @see org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker#isTransmitted(long)
    */
   public boolean isTransmitted(long aTxId) throws AeException
   {
      // Check if this invoke has already been (reliably) delivered based on the existence of 
      // the transmission id in the storage layer.
      // Perform this check only if the transmission id is positive (persistent/durable invoke transmission id)
      return (aTxId > IAeTransmissionTracker.NULL_TRANSREC_ID) && exists( aTxId );
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.transreceive.AeNoopTransmissionTracker#assignTransmissionId(org.activebpel.wsio.invoke.IAeTransmission, long, int)
    */
   public void assignTransmissionId(IAeTransmission aTransmission, long aProcessId, int aLocationId) throws AeException
   { 
      // Assign a new id only if a durable/persistent id  (positive #) has not already been assigned.
      if (aTransmission.getTransmissionId() <= IAeTransmissionTracker.NULL_TRANSREC_ID)
      {      
         // get the next tranmission id.
         long txId = getNextId();
         // set the tx id in the process state
         aTransmission.setTransmissionId( txId );
         // journal this action.
         AeEngineFactory.getEngine().getProcessManager().journalInvokeTransmitted(aProcessId, aLocationId, txId);
      }
   }   
}
