// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityReceiveImpl.java,v 1.58 2008/03/20 19:13:59 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.List;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.IAeIMACorrelations;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.AeActivityReceiveConsumerContext;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * Implementation of the bpel receive activity.
 */
public class AeActivityReceiveImpl extends AeWSIOActivityImpl implements IAeMessageReceiverActivity, IAeWSIOActivity, IAeMessageDispatcher, IAeMessageConsumerParentAdapter
{
   /** True if we have queued our receiver */
   private boolean mQueued = false;
   
   /**
    * Constructor for receive activity.
    */
   public AeActivityReceiveImpl(AeActivityReceiveDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
   }

   /**
    * gets the plink def
    */
   public AePartnerLinkDef getPartnerLinkDef()
   {
      return getDef().getPartnerLinkDef();
   }

   /**
    * finds the partner link
    */
   public AePartnerLink findPartnerLink()
   {
      AePartnerLinkDef def = getPartnerLinkDef();
      if (def == null)
         return null;
      
      return findPartnerLink(getPartnerLinkDef().getName());
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * Implements receive execution logic.  The receive queues itself waiting for
    * message with the engine.  The engine will then call back the receive
    * when its message is ready.
    *
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      boolean okToQueue = AeMultiStartHelper.checkForMultiStartBehavior(this);

      if (okToQueue)
      {
         queueReceive();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity#requeue()
    */
   public void requeue() throws AeBusinessProcessException
   {
      if (isQueued())
      {
         dequeue();
         queueReceive();
      }
   }

   /**
    * Prepares the correlation sets for the activity and then queues the activity.
    */
   private void queueReceive() throws AeBusinessProcessException
   {
      setQueued(true);
      
      getProcess().queueMessageReceiver(this, getLocationId());
   }

   /**
    * Convenience method to avoid having to cast the definition
    */
   private AeActivityReceiveDef getDef()
   {
      return (AeActivityReceiveDef) getDefinition();
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher#getMessageExchangePathForOpenIMA()
    */
   public String getMessageExchangePathForOpenIMA() throws AeBusinessProcessException 
   {
      String messageExchangePath = findEnclosingScope().getMessageExchangePath(getDef().getMessageExchange());
      return messageExchangePath;
   }
   
   /**
    * Handle the receipt of our correlated message.
    * @see org.activebpel.rt.bpel.impl.IAeMessageReceiver#onMessage(org.activebpel.rt.message.IAeMessageData)
    */
   public void onMessage(IAeMessageData aMessage) throws AeBusinessProcessException
   {
      getProcess().removeReceiverKeyForConflictingReceives(this);

      setQueued(false);

      List policies = null;
      if (findPartnerLink() != null && findPartnerLink().getMyReference() != null)
         policies = findPartnerLink().getMyReference().getEffectivePolicies(getProcess().getProcessPlan());
      
      getMessageValidator().validateInbound(getProcess(), getDef(), aMessage, policies);

      if (getRequestCorrelations() != null)
         getRequestCorrelations().initiateOrValidate(aMessage, getDef().getConsumerMessagePartsMap());

      getMessageDataConsumer().consumeMessageData(aMessage, new AeActivityReceiveConsumerContext(this));
      
      // we are done
      objectCompleted();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher#onFault(org.activebpel.rt.bpel.IAeFault)
    */
   public void onFault(IAeFault aFault) throws AeBusinessProcessException
   {
      objectCompletedWithFault(aFault);
   }

   /**
    * One of our correlation sets has been initialized. If all of our sets are
    * initialized then we should queue ourselves.
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCorrelationListener#correlationSetInitialized(org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet)
    */
   public void correlationSetInitialized(AeCorrelationSet aSet) throws AeBusinessProcessException
   {
      aSet.removeCorrelationListener(this);

      if (AeMultiStartHelper.isCorrelatedDataAvailable(this))
      {
         queueReceive();
      }
   }

   /**
    * Overrides method to extend base in order to dequeue any queued receives. So if we are
    * terminating or completing with queued receives they should be dequeued.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#setState(org.activebpel.rt.bpel.impl.AeBpelState)
    */
   public void setState(AeBpelState aNewState) throws AeBusinessProcessException
   {
      dequeue();
      super.setState(aNewState);
   }

   /**
    * Dequeues the receive if it is queued.
    */
   protected void dequeue() throws AeBusinessProcessException
   {
      if (isQueued())
      {
         getProcess().dequeueMessageReceiver(this);
         setQueued(false);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity#getCorrelations()
    */
   public IAeIMACorrelations getCorrelations()
   {
      return (IAeIMACorrelations) getRequestCorrelations();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity#getPartnerLinkOperationImplKey()
    */
   public AePartnerLinkOpImplKey getPartnerLinkOperationImplKey()
   {
      return new AePartnerLinkOpImplKey(findPartnerLink(getDef().getPartnerLink()), getDef().getOperation());
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity#canCreateInstance()
    */
   public boolean canCreateInstance()
   {
      return getDef().isCreateInstance();
   }

   /**
    * Getter for the queued flag
    */
   public boolean isQueued()
   {
      return mQueued;
   }

   /**
    * Setter for the queued flag
    * @param aB
    */
   public void setQueued(boolean aB)
   {
      mQueued = aB;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity#isConcurrent()
    */
   public boolean isConcurrent()
   {
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher#isPartnerLinkReadyForUpdate()
    */
   public boolean isPartnerLinkReadyForUpdate()
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity#createDispatcher(org.activebpel.wsio.receive.IAeMessageContext)
    */
   public IAeMessageDispatcher createDispatcher(IAeMessageContext aMessageContext)
   {
      return this;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher#getTarget()
    */
   public IAeBpelObject getTarget()
   {
      return this;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageConsumerParentAdapter#getMessageDataConsumerDef()
    */
   public IAeMessageDataConsumerDef getMessageDataConsumerDef()
   {
      return getDef();
   }
}
