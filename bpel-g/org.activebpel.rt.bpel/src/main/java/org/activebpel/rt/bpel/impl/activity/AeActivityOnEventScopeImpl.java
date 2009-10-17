//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityOnEventScopeImpl.java,v 1.7 2008/03/11 03:05:23 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeFaultFactory;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey;
import org.activebpel.rt.bpel.impl.AeProcessInfoEvent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeEnginePartnerLinkStrategy;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.activity.support.AeOnEvent;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * Special version of a scope that appears within a WS-BPEL onEvent.
 * The impl is created each time a message arrives within the onEvent.
 * The execution of this scope calls back into the scope in order to
 * consume the message. This is driven by the scope because the consumption
 * of the message may involve initiating partner links or correlation sets
 * that are defined by this scope. As such, the message consumption must
 * be deferred until the scope is ready to execute. Also, any errors consuming
 * the message must be handled first by this scope.  
 */
public class AeActivityOnEventScopeImpl extends AeActivityScopeImpl implements IAeMessageDispatcher
{
   /** message data received by the onEvent. This will remain set until the scope executes, at which time it is cleared. */
   private IAeMessageData mMessageData;
   /** context for the message data received by the onEvent. This will remain set until the scope executes, at which time it is cleared. */
   private IAeMessageContext mMessageContext;

   /**
    * Ctor
    * @param aActivityDef
    * @param aParent
    */
   public AeActivityOnEventScopeImpl(AeActivityScopeDef aActivityDef, AeOnEvent aParent)
   {
      super(aActivityDef, aParent);
   }

   /**
    * Overrides the base in order to check for a fault or consume the message
    * received by the onEvent. 
    * 
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      IAeMessageData messageData = getMessageData();
      
      setMessageContext(null);
      setMessageData(null);
      
      // if there is a fault, then the onEvent must have faulted with a conflictingRequest
      if (getFault() != null)
      {
         startTermination();
      }
      else
      {
         // no fault, grab the onEvent parent and install ourselves as the current scope
         // so any references to variables and correlation sets will be resolved through
         // us first.
         AeOnEvent onEvent = (AeOnEvent) getParent();
         onEvent.setCurrentScope(this);
         try
         {
            onEvent.extractMessageData(messageData);
            onEvent.setCurrentScope(null);
            super.execute();
         }
         catch(AeBpelException e)
         {
            AeProcessInfoEvent event = new AeProcessInfoEvent(getProcess().getProcessId(),
                  getLocationPath(), IAeProcessInfoEvent.ERROR_ON_EVENT_VALIDATION,
                  e.getFault().getFaultName().getLocalPart(), e.getMessage());
            getProcess().getEngine().fireInfoEvent(event);
            
            onEvent.setCurrentScope(null);
            triggerFaultHandling(e.getFault());
         }
         catch(Throwable t)
         {
            AeException.logError(t);
            onEvent.setCurrentScope(null);
            IAeFault fault = AeFaultFactory.getSystemErrorFault(t);
            triggerFaultHandling(fault);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }

   
   /**
    * Updates the partner link with any reply-to info from the message context
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl#initializePartnerLinks()
    */
   protected void initializePartnerLinks() throws AeBusinessProcessException
   {
      // init with any default state from the pdd
      super.initializePartnerLinks();
      
      // then init with any state from the message context
      AeOnEvent onEvent = (AeOnEvent) getParent();
      AePartnerLink plink = findPartnerLink(((AeOnEventDef)onEvent.getDefinition()).getPartnerLink());
      IAeEnginePartnerLinkStrategy plinkStrategy = getProcess().getEngine().getPartnerLinkStrategy();
      IAeProcessPlan plan = getProcess().getProcessPlan();
      plinkStrategy.updatePartnerLink(plink, plan, getMessageContext());
   }

   /**
    * @return Returns the messageData.
    */
   public IAeMessageData getMessageData()
   {
      return mMessageData;
   }

   /**
    * @param aMessageData The messageData to set.
    */
   public void setMessageData(IAeMessageData aMessageData)
   {
      mMessageData = aMessageData;
   }

   /**
    * @return Returns the messageContext.
    */
   public IAeMessageContext getMessageContext()
   {
      return mMessageContext;
   }

   /**
    * @param aMessageContext The messageContext to set.
    */
   public void setMessageContext(IAeMessageContext aMessageContext)
   {
      mMessageContext = aMessageContext;
   }
   
   /**
    * The child scope of the onEvent resolves the message exchange values prior to any 
    * enclosed scopes.
    *   
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher#getMessageExchangePathForOpenIMA()
    */
   public String getMessageExchangePathForOpenIMA() throws AeBusinessProcessException
   {
      AeOnEventDef onEventDef = getParentDef();
      String messageExchangePath = getMessageExchangePath(onEventDef.getMessageExchange());
      return messageExchangePath;
   }

   /**  
    * The child scope for the onEvent resolves the partner link prior to any enclosed scopes
    * 
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher#getPartnerLinkOperationImplKey()
    */
   public AePartnerLinkOpImplKey getPartnerLinkOperationImplKey()
   {
      AeOnEventDef onEventDef = getParentDef();
      String plinkName = onEventDef.getPartnerLink();
      AePartnerLink plink = findPartnerLink(plinkName);
      return new AePartnerLinkOpImplKey(plink, onEventDef.getOperation());
   }

   /**  
    * The onEvent impl always defers the update of the plink
    * 
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher#isPartnerLinkReadyForUpdate()
    */
   public boolean isPartnerLinkReadyForUpdate()
   {
      // TODO (MF) really only need to defer the update if it's a local partner link
      return false;
   }

   /**  
    * Records the fault locally and queues the scope. We still need to queue the scope through the
    * process since the scope may be isolated and require locks prior to executing its fault handlers
    * 
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher#onFault(org.activebpel.rt.bpel.IAeFault)
    */
   public void onFault(IAeFault aFault) throws AeBusinessProcessException
   {
      setFault(aFault);
      getProcess().queueObjectToExecute(this);
   }

   /**  
    * Records the message data and queues the scope. We still need to queue the scope through the
    * process since the scope may be isolated and require locks prior to executing.
    * 
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher#onMessage(org.activebpel.rt.message.IAeMessageData)
    */
   public void onMessage(IAeMessageData aMessage) throws AeBusinessProcessException
   {
      setMessageData(aMessage);
      getProcess().queueObjectToExecute(this);
   }

   /**
    * Gets the reference to the parent's def
    */
   private AeOnEventDef getParentDef()
   {
      AeOnEventDef onEventDef = (AeOnEventDef) getOnEventParent().getDefinition();
      return onEventDef;
   }

   /**
    * Getter for the onEvent parent.
    */
   protected AeOnEvent getOnEventParent()
   {
      return (AeOnEvent) getParent();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeMessageDispatcher#getTarget()
    */
   public IAeBpelObject getTarget()
   {
      return this;
   }
}