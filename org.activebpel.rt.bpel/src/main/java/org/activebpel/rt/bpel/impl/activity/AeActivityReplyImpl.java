// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityReplyImpl.java,v 1.41 2008/02/29 04:09:35 mford Exp $
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
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.activity.wsio.produce.AeActivityReplyProducerContext;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.message.IAeMessageData;

/**
 * Implementation of the bpel reply activity.
 */
public class AeActivityReplyImpl extends AeWSIOActivityImpl implements IAeMessageProducerParentAdapter
{
   /** default constructor for activity */
   public AeActivityReplyImpl(AeActivityReplyDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   /**
    * Convenience method to avoid casting of definition.
    */
   private AeActivityReplyDef getDef()
   {
      return (AeActivityReplyDef) getDefinition();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      IAeMessageData inputMessage = getMessageDataProducer().produceMessageData(new AeActivityReplyProducerContext(this));
      AePartnerLink plink = findPartnerLink(getDef().getPartnerLink());

      // Validate only if we know the outgoing message type. In particular, we
      // won't know the outgoing message type if we are replying with a fault
      // that was not defined as part of the WSDL operation.
      // TODO (MF) When static analysis is updated, then remove this check. 
      if (getDef().getProducerMessagePartsMap() != null)
      {
         List policies = null;
         if (plink != null && plink.getMyReference() != null)
            policies = plink.getMyReference().getEffectivePolicies(getProcess().getProcessPlan());
         getMessageValidator().validateOutbound(getProcess(), getDef(), inputMessage, policies);
      }

      // get the correlation sets we are creating via input and initiate
      if (getResponseCorrelations() != null)
         getResponseCorrelations().initiateOrValidate(inputMessage, getDef().getProducerMessagePartsMap());

      // Queue our invocation of the partner operation we will be called back when it is done
      // note that we may be called back via onMessage or onFault before this returns
      String messageExchangePath = findEnclosingScope().getMessageExchangePath(getDef().getMessageExchange());
      AePartnerLinkOpImplKey plOpKey = new AePartnerLinkOpImplKey(plink, getDef().getOperation());
      getProcess().queueReply(inputMessage, getDef().getFaultName(), plOpKey, messageExchangePath);

      // fixme (MF-3.1) introduce a callback so we can be sure that the reply was successful.                   
      objectCompleted();
   }
   
   /** 
    * Overrides method to remove the open IMA before changing state to COMPLETED.
    * 
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#exceptionManagementCompleteActivity()
    */
   public void exceptionManagementCompleteActivity() throws AeBusinessProcessException
   {
      // Remove the open IMA from the process open IMA list.
      AePartnerLink plink = findPartnerLink(getDef().getPartnerLink());
      AePartnerLinkOpImplKey plOpKey = new AePartnerLinkOpImplKey(plink, getDef().getOperation());
      String messageExchangePath = findEnclosingScope().getMessageExchangePath(getDef().getMessageExchange());
      
      getProcess().removeReply(plOpKey, messageExchangePath);

      super.exceptionManagementCompleteActivity();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageProducerParentAdapter#getMessageDataProducerDef()
    */
   public IAeMessageDataProducerDef getMessageDataProducerDef()
   {
      return getDef();
   }
}
