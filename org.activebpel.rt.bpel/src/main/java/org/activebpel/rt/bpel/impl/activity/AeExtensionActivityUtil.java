// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeExtensionActivityUtil.java,v 1.3 2008/03/11 03:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.reply.AeExtensionActivityDurableInfo;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;
import org.activebpel.rt.bpel.impl.reply.IAeExtensionReplyReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.AeMessageAcknowledgeException;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;
import org.activebpel.wsio.receive.AeMessageContext;

/**
 * Utility methods for use in the extension activities.
 */
public class AeExtensionActivityUtil
{
   /**
    * Queue some receive data onto the engine for extension activities.
    * 
    * @param aBpelObject
    * @param aMessageData
    * @param aMessageContext
    * @param aOneway
    * @throws AeBusinessProcessException
    */
   public static void queueReceiveData(IAeBpelObject aBpelObject, IAeMessageData aMessageData, AeMessageContext aMessageContext, boolean aOneway, long aTransmissionId) throws AeBusinessProcessException
   {
      IAeExtensionReplyReceiver replyReceiver = null;
      IAeBusinessProcessEngineInternal engine = aBpelObject.getProcess().getEngine();
      if (!aOneway)
      {
         long pid = aBpelObject.getProcess().getProcessId();
         String locationPath = aBpelObject.getLocationPath();

         IAeDurableReplyInfo durableInfo = new AeExtensionActivityDurableInfo(pid, locationPath, aTransmissionId);

         replyReceiver = (IAeExtensionReplyReceiver) engine.getTransmissionTracker().getDurableReplyFactory().createReplyReceiver(IAeTransmissionTracker.NULL_TRANSREC_ID, durableInfo);
         replyReceiver.setEngine(engine);
      }
      IAeMessageAcknowledgeCallback callback = new AeTransmissionAcknowledged(aBpelObject.getProcess().getEngine().getTransmissionTracker(), aTransmissionId);
      engine.queueReceiveData(aMessageData, replyReceiver, aMessageContext, callback, true);
   }
   
   
   /**
    * Marks the message as having been transmitted when it is received by the 
    * b4p impl process. 
    */
   protected static class AeTransmissionAcknowledged implements IAeMessageAcknowledgeCallback
   {
      /** transmission id */
      private long mTransmissionId;
      /** tracker */
      private IAeTransmissionTracker mTracker;
      
      /**
       * Ctor
       * @param aTracker
       * @param aTransmissionId
       */
      public AeTransmissionAcknowledged(IAeTransmissionTracker aTracker, long aTransmissionId)
      {
         setTransmissionId(aTransmissionId);
         setTracker(aTracker);
      }

      /**
       * @see org.activebpel.wsio.IAeMessageAcknowledgeCallback#onAcknowledge(java.lang.String)
       */
      public void onAcknowledge(String aMessageId) throws AeMessageAcknowledgeException
      {
         recordTransmission(aMessageId);
      }

      /**
       * @see org.activebpel.wsio.IAeMessageAcknowledgeCallback#onNotAcknowledge(java.lang.Throwable)
       */
      public void onNotAcknowledge(Throwable aReason)
      {
         recordTransmission(null);
      }
      
      /**
       * Records that the message was transmitted successfully
       * @param aMessageId
       */
      protected void recordTransmission(String aMessageId)
      {
         try
         {
            getTracker().add(getTransmissionId(), aMessageId, IAeTransmissionTracker.TRANSMITTED_STATE);
         }
         catch (AeException e)
         {
            e.logError();
         }
      }

      /**
       * @return the transmissionId
       */
      protected long getTransmissionId()
      {
         return mTransmissionId;
      }

      /**
       * @param aTransmissionId the transmissionId to set
       */
      protected void setTransmissionId(long aTransmissionId)
      {
         mTransmissionId = aTransmissionId;
      }

      /**
       * @return the tracker
       */
      protected IAeTransmissionTracker getTracker()
      {
         return mTracker;
      }

      /**
       * @param aTracker the tracker to set
       */
      protected void setTracker(IAeTransmissionTracker aTracker)
      {
         mTracker = aTracker;
      }
   }
}
