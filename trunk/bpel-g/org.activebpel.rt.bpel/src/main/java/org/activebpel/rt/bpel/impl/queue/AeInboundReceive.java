// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/queue/AeInboundReceive.java,v 1.18 2006/12/14 15:11:22 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.queue;

import commonj.timers.Timer;

import java.util.Date;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * Models the data received from a partner.   
 */
public class AeInboundReceive extends AeCorrelatedReceive
{
   /** The process plan for the inbound receive. */
   private IAeProcessPlan mProcessPlan;
   /** Message data of message queue entry */
   private IAeMessageData mMessageData;
   /** id given by the queue manager, currently only used for unmatched receives */
   private String mQueueId;
   /** The context that for the inbound message */
   private IAeMessageContext mContext;
   /** The date when this inbound receive should expire (if it hasn't been dispatched yet). */
   private Date mTimeoutDate;
   /** The Timer that has been created to timeout this inbound receive at the above timeout date. */
   private Timer mTimeoutTimer;
   /** Indicates if the inbound receive is one-way or a two-way (request-response). */
   private boolean mOneway;
   /** Reply receiver for two way messages. */
   private IAeReplyReceiver mReplyReceiver;
   /** Dd that is assigned to the corresponding reply for two-way messages. */
   private long mReplyId = 0; 
   
   /**
    * Creates an inbound receive.
    * 
    * @param aPlinkOpKey
    * @param aCorrelation
    * @param aProcPlan
    * @param aData
    * @param aContext
    */
   public AeInboundReceive(AePartnerLinkOpKey aPlinkOpKey, Map aCorrelation, IAeProcessPlan aProcPlan, IAeMessageData aData,
         IAeMessageContext aContext)
   {
      this(aPlinkOpKey, aCorrelation, aProcPlan, aData, aContext, null);
   }   
   
   /**
    * Creates an inbound receive.
    * 
    * @param aPlinkOpKey
    * @param aCorrelation
    * @param aProcPlan
    * @param aData
    * @param aContext
    * @param aReplyReceiver
    */
   public AeInboundReceive(AePartnerLinkOpKey aPlinkOpKey, Map aCorrelation, IAeProcessPlan aProcPlan, IAeMessageData aData,
         IAeMessageContext aContext, IAeReplyReceiver aReplyReceiver)
   {
      super(aPlinkOpKey, aProcPlan.getProcessDef().getQName(), aCorrelation);

      mContext = aContext;
      setProcessPlan(aProcPlan);
      setMessageData(aData);
      setTimeoutDate(null);
      setTimeoutTimer(null);
      setReplyReceiver(aReplyReceiver);
      setOneway(aReplyReceiver == null);
   }
       
   /**
    * @return Returns the replyId.
    */
   public long getReplyId()
   {
      return mReplyId;
   }

   /**
    * @param aReplyId The replyId to set.
    */
   public void setReplyId(long aReplyId)
   {
      // Sets a reply id. The replyId is a unique id that is assigned once the inbound receive has been
      // journaled and matched to a process. (In AeBusinessProcessEngine::CreateProcessWithMessage and in
      // AeBusinessProcessEngine:journalDispatchReceiveData(..). This id is typically used by the
      // AeReply queueu object.
      mReplyId = aReplyId;
   }

   /**
    * @return Returns true the inbound message is oneway. Returns false if the messge is waiting for a response.
    */
   public boolean isOneway()
   {
      return mOneway;
   }

   /**
    * @param aOneway The oneway to set.
    */
   public void setOneway(boolean aOneway)
   {
      mOneway = aOneway;
   }

   /**
    * Getter for the message's context.
    */
   public IAeMessageContext getContext()
   {
      return mContext;
   }
   
   /**
    * Getter for the message data
    */
   public IAeMessageData getMessageData()
   {
      return mMessageData;
   }

   /**
    * Getter for the queue id, a unique id assigned to this queue object.
    */
   public String getQueueId()
   {
      return mQueueId;
   }

   /**
    * Setter for the message data.
    * @param aData
    */
   public void setMessageData(IAeMessageData aData)
   {
      mMessageData = aData;
   }

   /**
    * Setter for the queue id.
    * @param aString
    */
   public void setQueueId(String aString)
   {
      mQueueId = aString;
   }

   /**
    * Getter for the process plan.
    */
   public IAeProcessPlan getProcessPlan()
   {
      return mProcessPlan;
   }

   /**
    * Setter for the process plan.
    */
   public void setProcessPlan(IAeProcessPlan aPlan)
   {
      mProcessPlan = aPlan;
   }

   /**
    * Convenience method that gets the process qname
    */
   public QName getProcessName()
   {
      return getProcessPlan().getProcessDef().getQName();
   }

   /**
    * @return Returns the timeoutDate.
    */
   public Date getTimeoutDate()
   {
      return mTimeoutDate;
   }

   /**
    * @param aTimeoutDate The timeoutDate to set.
    */
   public void setTimeoutDate(Date aTimeoutDate)
   {
      mTimeoutDate = aTimeoutDate;
   }

   /**
    * @return Returns the timeoutTimer.
    */
   public Timer getTimeoutTimer()
   {
      return mTimeoutTimer;
   }

   /**
    * @param aTimeoutTimer The timeoutTimer to set.
    */
   public void setTimeoutTimer(Timer aTimeoutTimer)
   {
      mTimeoutTimer = aTimeoutTimer;
   }
   
   /**
    * Returns true if the reply is durable.
    */
   public boolean isReplyDurable()
   {
      return getReplyReceiver() != null && getReplyReceiver().getDurableReplyInfo() != null;
   }

   /**
    * @return Returns the replyReceiver if available or <code>null</code> otherwise (for example, for one-way messages).
    */
   public IAeReplyReceiver getReplyReceiver()
   {
      return mReplyReceiver;
   }

   /**
    * @param aReplyReceiver The replyReceiver to set.
    */
   public void setReplyReceiver(IAeReplyReceiver aReplyReceiver)
   {
      mReplyReceiver = aReplyReceiver;
   }
   
   /**
    * Gets the port type
    */
   public QName getPortType()
   {
      return getProcessPlan().getMyRolePortType(getPartnerLinkOperationKey());
   }
}
