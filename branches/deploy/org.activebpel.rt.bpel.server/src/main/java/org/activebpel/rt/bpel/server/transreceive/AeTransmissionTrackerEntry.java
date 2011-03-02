//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/transreceive/AeTransmissionTrackerEntry.java,v 1.1 2006/05/24 23:16:35 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.transreceive;

/**
 * Entry containing transmission id, state and associated message id.
 *
 */
public class AeTransmissionTrackerEntry
{
   /** Entry id */
   private long mTransmissionId;
   /** Entry state */
   private int mState;
   /** Optional message id. */
   private String mMessageId;

   /**
    * Constructs an entry given entry id and state.
    * @param aTransmissionId
    * @param aState
    */
   public AeTransmissionTrackerEntry(long aTransmissionId, int aState)
   {
      this(aTransmissionId, aState, null);
   }
   
   /**
    * Constructs entry given id, state and message id.
    * @param aTransmissionId
    * @param aState
    * @param aMessageId
    */
   public AeTransmissionTrackerEntry(long aTransmissionId, int aState, String aMessageId)
   {
      mTransmissionId = aTransmissionId;
      mState = aState;
      mMessageId = aMessageId;
   }

   /**
    * @return Returns the messageId.
    */
   public String getMessageId()
   {
      return mMessageId;
   }
     
   /**
    * @param aMessageId The messageId to set.
    */
   public void setMessageId(String aMessageId)
   {
      mMessageId = aMessageId;
   }

   /**
    * @param aState The state to set.
    */
   public void setState(int aState)
   {
      mState = aState;
   }

   /**
    * @return Returns the state.
    */
   public int getState()
   {
      return mState;
   }

   /**
    * @return Returns the transmissionId.
    */
   public long getTransmissionId()
   {
      return mTransmissionId;
   }

}
