// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityInvokeImpl.java,v 1.65 2008/03/11 03:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeInvokeActivity;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeAlarmReceiver;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeMessageReceiver;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.activity.support.AeInvokeRetryPolicy;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.AeActivityInvokeConsumerContext;
import org.activebpel.rt.bpel.impl.activity.wsio.produce.AeActivityInvokeProducerContext;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;

/**
 * Implementation of the bpel invoke activity.
 */
public class AeActivityInvokeImpl extends AeWSIOActivityImpl implements IAeInvokeActivity, IAeMessageReceiver, IAeAlarmReceiver, IAeMessageConsumerParentAdapter, IAeMessageProducerParentAdapter
{
   /** Boolean indicating that the invoke has been queued to be tried at a later time. */
   private boolean mQueued;

   /** contains the code and state for implementing retries for invokes */
   private AeInvokeRetryPolicy mRetryPolicy = new AeInvokeRetryPolicy(this);

   /**
    * Invoke transmission id used in durable invokes.
    */
   private long mTransmissionId = IAeTransmissionTracker.NULL_TRANSREC_ID;

   /**
    * Id of the engine where the invoke was executed.
    */
   private int mEngineId;

   /** Alarm execution instance reference id. */
   private int mAlarmId;

   /** Pending invoke journal entry id. */
   private long mJournalId = IAeProcessManager.NULL_JOURNAL_ID;

   /** default constructor for activity */
   public AeActivityInvokeImpl(AeActivityInvokeDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
      setAlarmId(-1);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      // if we are executing in a loop make sure to clear the state of retries
      setRetries(0);
      setQueued(false);

      // Set the engine id. This id will be reset when state is final.
      setEngineId( getProcess().getEngine().getEngineId() );

      // Journal the pending invoke.
      long journalId = getProcess().getEngine().getProcessManager().journalInvokePending(getProcess().getProcessId(), getLocationId());
      setJournalId(journalId);
      
      getProcess().assignTransmissionId(this);

      IAeMessageData msgData = getInputMessageData();

      // initiates any correlation sets that are defined to initiate with OUTBOUND data
      if (getRequestCorrelations() != null)
      {
         getRequestCorrelations().initiateOrValidate(msgData, getDef().getProducerMessagePartsMap());
      }
      
      // Try the invoke...
      queueInvoke(msgData);
   }

   /**
    * Queues our invocation of the partner operation. We will be called back
    * when it is done. Note that we may be called back via
    * {@link #onMessage(IAeMessageData)} or {@link #onFault(IAeFault)} before
    * this method returns.
    *
    * @throws AeBusinessProcessException
    */
   public void queueInvoke(IAeMessageData aMessageData) throws AeBusinessProcessException
   {
      IAeMessageData data = aMessageData == null? getInputMessageData() : aMessageData;
          
      AePartnerLink partnerLink = findPartnerLink(getDef().getPartnerLink());
      AePartnerLinkOpImplKey aePartnerLinkOpImplKey = new AePartnerLinkOpImplKey(partnerLink, getDef().getOperation());

      getProcess().queueInvoke(this, data, partnerLink, aePartnerLinkOpImplKey);
   }

   /**
    * Overrides method to reset transmission id.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#terminate()
    */
   public void terminate() throws AeBusinessProcessException
   {
      resetTransmissionId();
      super.terminate();
   }

   /**
    * Gets the data for the invoke. If there is a variable defined but not initialized
    * then you'll get an exception.
    */
   private IAeMessageData getInputMessageData() throws AeBusinessProcessException
   {
      IAeMessageData inputMessage = getMessageDataProducer().produceMessageData(new AeActivityInvokeProducerContext(this));

      List policies = null;
      AePartnerLink plink = findPartnerLink(getDef().getPartnerLink());
      if (plink != null && plink.getPartnerReference() != null)
      {
         policies = plink.getPartnerReference().getEffectivePolicies(getProcess().getProcessPlan(), getDef().getProducerPortType(), getDef().getProducerOperation());
      }
      
      getMessageValidator().validateOutbound(getProcess(), getDef(), inputMessage, policies);

      return inputMessage;
   }

   /**
    * Convenience method to avoid having to cast the definition object in order
    * to access methods provided by invoke def.
    */
   private AeActivityInvokeDef getDef()
   {
      AeActivityInvokeDef def = (AeActivityInvokeDef)getDefinition();
      return def;
   }

   /**
    * Handle the receipt of our output message.
    * @see org.activebpel.rt.bpel.impl.IAeMessageReceiver#onMessage(org.activebpel.rt.message.IAeMessageData)
    */
   public void onMessage(IAeMessageData aMessage) throws AeBusinessProcessException
   {
      if (getState().isFinal())
      {
         return;
      }
      if (aMessage != null)
      {
         List policies = null; 
         AePartnerLink plink = findPartnerLink(getDef().getPartnerLink());
         if (plink != null && plink.getPartnerReference() != null)
            policies = plink.getPartnerReference().getEffectivePolicies(getProcess().getProcessPlan(), getDef().getConsumerPortType(), getDef().getConsumerOperation());
         
         getMessageValidator().validateInbound(getProcess(), getDef(), aMessage, policies);

         // initiates any correlation sets that are defined to initiate with INBOUND data
         if (getResponseCorrelations() != null)
            getResponseCorrelations().initiateOrValidate(aMessage, getDef().getConsumerMessagePartsMap());

         getMessageDataConsumer().consumeMessageData(aMessage, new AeActivityInvokeConsumerContext(this));
      }

      // we are done
      objectCompleted();
   }

   /**
    * Convenience method for getting the output variable. If none is defined, then
    * we return null
    */
   public IAeVariable getOutputVariable()
   {
      if (AeUtil.notNullOrEmpty(getDef().getOutputVariable()))
         return findVariable(getDef().getOutputVariable());
      else
         return null;
   }

   /**
    * Handle the receipt of a fault from our invoke.
    * @see org.activebpel.rt.bpel.impl.IAeMessageReceiver#onFault(org.activebpel.rt.bpel.IAeFault)
    */
   public void onFault(IAeFault aFault) throws AeBusinessProcessException
   {
      if (!getState().isFinal())
      {
         if(! getRetryPolicy().reschedule(aFault))
         {
            objectCompletedWithFault(aFault);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.IAeInvokeActivity#isOneWay()
    */
   public boolean isOneWay()
   {
      return getDef().getConsumerMessagePartsMap() == null;
   }

   /**
    * Implements method by dispatching an invoke retry after marking ourselves as dequeued
    * and incrementing the count of times we have attempted retries.
    * @see org.activebpel.rt.bpel.impl.IAeAlarmReceiver#onAlarm()
    */
   public void onAlarm() throws AeBusinessProcessException
   {
      // if for some reason we are not queued then this alarm is in error
      if(isQueued())
      {
         // TODO (ck) add an info event to log that we are retrying
         setQueued(false);
         setRetries(getRetries() + 1);

         // retry the invoke...
         queueInvoke(getInputMessageData());
      }
   }

   /**
    * Overrides method to extend base in order to reset the activity and dequeue
    * any queued alarm. We have to override both this method and
    * {@link #setFaultedState(IAeFault)} in order to catch all transitions to
    * final states.
    *
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityImpl#setState(org.activebpel.rt.bpel.impl.AeBpelState)
    */
   public void setState(AeBpelState aNewState) throws AeBusinessProcessException
   {
      if (aNewState.isFinal())
      {
         reset();
      }
      dequeue();
      super.setState(aNewState);
   }

   /**
    * Overrides method to extend base in order to reset the activity and dequeue
    * any queued alarm. We have to override both this method and
    * {@link #setState(AeBpelState)} in order to catch all transitions to final
    * states.
    *
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityImpl#setFaultedState(org.activebpel.rt.bpel.IAeFault)
    */
   public void setFaultedState(IAeFault aFault) throws AeBusinessProcessException
   {
      reset();
      super.setFaultedState(aFault);
   }

   /**
    * Resets the state of this activity.
    */
   protected void reset() throws AeBusinessProcessException
   {
      // reset durable invoke data.
      resetTransmissionId();
      // reset engine id
      resetEngineId();
      // reset journal id
      resetJournalId();

      dequeue();
   }

   /**
    * Dequeues the alarm from the process.
    */
   protected void dequeue() throws AeBusinessProcessException
   {
      if (isQueued())
      {
         getProcess().dequeueAlarm(this);
         setQueued(false);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAlarmReceiver#getGroupId()
    */
   public int getGroupId()
   {
      return getLocationId();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAlarmReceiver#isQueued()
    */
   public boolean isQueued()
   {
      return mQueued;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAlarmReceiver#isQueued()
    */
   public void setQueued(boolean aQueued)
   {
      mQueued = aQueued;
   }

   /**
    * @return Returns the retries.
    */
   public int getRetries()
   {
      return getRetryPolicy().getRetries();
   }

   /**
    * @param aRetries The retries to set.
    */
   public void setRetries(int aRetries)
   {
      getRetryPolicy().setRetries(aRetries);
   }

   /**
    * @return Returns the transmission Id.
    */
   public long getTransmissionId()
   {
      return mTransmissionId;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeInvokeActivity#setTransmissionId(long)
    */
   public void setTransmissionId(long aTransmissionId)
   {
      mTransmissionId = aTransmissionId;
   }

   /**
    * Resets the transmission information and prepare for the next invoke execution.
    */
   private void resetTransmissionId()
   {
      // if the previous tx id is persistent/durable (ie. a positive number), then add it to
      // current invoke tx id collection so that the corresponding entries in the transmision
      // tracker can be deleted.
      if (getTransmissionId() > IAeTransmissionTracker.NULL_TRANSREC_ID)
      {
         getProcess().getEngine().getProcessManager().transmissionIdDone(getProcess().getProcessId(), getTransmissionId() );
      }
      setTransmissionId(IAeTransmissionTracker.NULL_TRANSREC_ID);
   }

   /**
    * @return Returns the engineId.
    */
   public int getEngineId()
   {
      return mEngineId;
   }

   /**
    * @param aEngineId The engineId to set.
    */
   public void setEngineId(int aEngineId)
   {
      mEngineId = aEngineId;
   }

   /**
    * Resets the engine id.
    */
   private void resetEngineId()
   {
      setEngineId(IAeBusinessProcessEngineInternal.NULL_ENGINE_ID);
   }

   /**
    * Getter for the retry policy
    */
   protected AeInvokeRetryPolicy getRetryPolicy()
   {
      return mRetryPolicy;
   }

   /**
    * @return Returns the alarmId.
    */
   public int getAlarmId()
   {
      return mAlarmId;
   }

   /**
    * @param aAlarmId The alarmId to set.
    */
   public void setAlarmId(int aAlarmId)
   {
      mAlarmId = aAlarmId;
   }

   /**
    * Returns the pending invoke journal entry id.
    */
   public long getJournalId()
   {
      return mJournalId;
   }

   /**
    * Sets the pending invoke journal entry id.
    */
   public void setJournalId(long aJournalId)
   {
      mJournalId = aJournalId;
   }

   /**
    * Marks the pending invoke journal entry done and resets the pending invoke
    * journal entry id.
    */
   protected void resetJournalId()
   {
      if (getJournalId() != IAeProcessManager.NULL_JOURNAL_ID)
      {
         getProcess().getEngine().getProcessManager().journalEntryDone(getProcess().getProcessId(), getJournalId());
      }

      setJournalId(IAeProcessManager.NULL_JOURNAL_ID);
   }

   /**
    * @return <code>true</code> if and only if this is a pending invoke
    */
   public boolean isPending()
   {
      return isExecuting() && !isQueued();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageProducerParentAdapter#getMessageDataProducerDef()
    */
   public IAeMessageDataProducerDef getMessageDataProducerDef()
   {
      return getDef();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageConsumerParentAdapter#getMessageDataConsumerDef()
    */
   public IAeMessageDataConsumerDef getMessageDataConsumerDef()
   {
      return getDef();
   }
}
