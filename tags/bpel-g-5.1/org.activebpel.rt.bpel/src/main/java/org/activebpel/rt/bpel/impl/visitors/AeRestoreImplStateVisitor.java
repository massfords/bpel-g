// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeRestoreImplStateVisitor.java,v 1.71 2008/03/11 03:05:23 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.util.AeLocationPathUtils;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.AeBusinessProcessPropertyIO;
import org.activebpel.rt.bpel.impl.AeConflictingReceiveException;
import org.activebpel.rt.bpel.impl.AeMessageDataDeserializer;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey;
import org.activebpel.rt.bpel.impl.AeVariable;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeDynamicScopeParent;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.activity.AeActivityChildExtensionActivityImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityCompensateImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityForEachParallelImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityOnEventScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityReceiveImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityRepeatUntilImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityWaitImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityWhileImpl;
import org.activebpel.rt.bpel.impl.activity.AeDynamicScopeCreator;
import org.activebpel.rt.bpel.impl.activity.AeLoopActivity;
import org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleAdapter;
import org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter;
import org.activebpel.rt.bpel.impl.activity.IAeVariableContainer;
import org.activebpel.rt.bpel.impl.activity.support.AeBaseEvent;
import org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeCoordinationContainer;
import org.activebpel.rt.bpel.impl.activity.support.AeCoordinatorCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet;
import org.activebpel.rt.bpel.impl.activity.support.AeDefaultFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeFCTHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeImplicitCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeImplicitFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeLink;
import org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeOnEvent;
import org.activebpel.rt.bpel.impl.activity.support.AeOnMessage;
import org.activebpel.rt.bpel.impl.activity.support.AeOpenMessageActivityInfo;
import org.activebpel.rt.bpel.impl.activity.support.AeParticipantCompensator;
import org.activebpel.rt.bpel.impl.activity.support.AeProcessCompensationCallbackWrapper;
import org.activebpel.rt.bpel.impl.activity.support.AeRepeatableOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeWSBPELFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyFactory;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.bpel.impl.storage.AeDurableReplyDeserializer;
import org.activebpel.rt.bpel.impl.storage.AeFaultDeserializer;
import org.activebpel.rt.bpel.impl.storage.AeInboundReceiveDeserializer;
import org.activebpel.rt.bpel.impl.storage.AeRestoreImplState;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeSchemaDuration;
import org.activebpel.wsio.receive.AeMessageContext;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Visits a tree of BPEL implementation objects to restore the state from an
 * instance of <code>AeRestoreImplState</code>.
 */
public class AeRestoreImplStateVisitor extends AeBaseRestoreVisitor
{
   /**
    * Maps compensation callback location paths to the compensation handlers
    * that need them.
    */
   private final Map mCompensationCallbackOwnersMap = new HashMap();
   
   /** Flag to indicate if any of the scopes that were visited had any sub process invokes (coordinations). */
   private boolean mScopeHadCoordination = false;
   
   /** visitor used to set deadpath state on skipped children */
   private static final IAeImplVisitor DEADPATH_VISITOR = new AeDeadPathVisitor();

   /**
    * Constructor.
    *
    * @param aImplState The state object from which to restore state.
    */
   public AeRestoreImplStateVisitor(AeRestoreImplState aImplState)
   {
      super(aImplState);
   }

   /**
    * Creates the process's create message from its serialization, if the
    * process still has a create message.
    *
    * @param aImpl
    * @return AeInboundReceive
    * @throws AeBusinessProcessException
    */
   protected AeInboundReceive createCreateMessage(AeBusinessProcess aImpl) throws AeBusinessProcessException
   {
      Element implElement = getImplState().getElement(aImpl);
      String xpath = "./" + STATE_CREATEMESSAGE; //$NON-NLS-1$
      Element createMessageElement = (Element) selectOptionalNode(implElement, xpath, "Error restoring create message"); //$NON-NLS-1$

      return (createMessageElement == null) ? null : createCreateMessage(aImpl, createMessageElement);
   }

   /**
    * Creates the process's create message from its serialization.
    *
    * @param aImpl
    * @param aElement
    * @return AeInboundReceive
    * @throws AeBusinessProcessException
    */
   protected AeInboundReceive createCreateMessage(IAeBusinessProcessInternal aImpl, Element aElement) throws AeBusinessProcessException
   {
      long replyId = getAttributeLong(aElement, STATE_REPLY_ID);
      Element root = AeXmlUtil.getFirstSubElement(aElement);
      
      AeInboundReceive createMessage;

      if (root.getTagName() == IAeImplStateNames.STATE_MESSAGEDATA)
      {
         // This is the legacy case. We used to synthesize the inbound receive
         // from just the partner link, operation, and message data.
         String partnerLink = getAttribute(aElement, STATE_PLINK);
         String operation = getAttribute(aElement, STATE_OPERATION);
         
         // Deserialize the message data.
         AeMessageDataDeserializer deserializer = new AeMessageDataDeserializer();
         deserializer.setMessageDataElement(root);

         IAeMessageData messageData = deserializer.getMessageData();

         IAeMessageContext context = new AeMessageContext(partnerLink, operation);

         AeProcessDef processDef = aImpl.getProcessDefinition();
         AePartnerLinkDef plDef = processDef.findPartnerLink(partnerLink);
         AePartnerLinkOpKey plOpKey = new AePartnerLinkOpKey(plDef, context.getOperation());

         // Construct the create message with enough attributes to allow the
         // process to match a message receiver to the create message: partner
         // link, port type, and operation (see
         // AeMessageReceiver.matches(AeCorrelatedReceive aCorrelatedReceive)).
         createMessage = new AeInboundReceive(
            plOpKey,
            null, // correlation map
            aImpl.getProcessPlan(),
            messageData,
            context);
      }
      else
      {
         AeInboundReceiveDeserializer deserializer = new AeInboundReceiveDeserializer();
         deserializer.setInboundReceiveElement(root);
         deserializer.setProcessPlan(aImpl.getProcessPlan());
         deserializer.setTypeMapping(aImpl.getEngine().getTypeMapping());

         createMessage = deserializer.getInboundReceive();
         boolean replyWaiting = !createMessage.isOneway();
         //create reply receiver.         
         if (replyWaiting)
         {
            IAeReplyReceiver replyReceiver = null;
            IAeDurableReplyFactory factory = aImpl.getEngine().getTransmissionTracker().getDurableReplyFactory();
            replyReceiver = factory.createReplyReceiver(createMessage.getReplyId(), deserializer.getDurableReplyInfo());
            createMessage.setReplyReceiver(replyReceiver);
         }         
      }
      createMessage.setReplyId(replyId);
      return createMessage;
   }

   /**
    * Creates the fault object for the specified implementation
    * object, if the fault exists.
    *
    * @param aImpl
    * @return IAeFault
    * @throws AeBusinessProcessException
    */
   protected IAeFault createFault(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      Element implElement = getImplState().getElement(aImpl);
      String xpath = "./" + STATE_FAULT; //$NON-NLS-1$
      Element faultElement = (Element) selectOptionalNode(implElement, xpath, "Error restoring fault"); //$NON-NLS-1$

      return (faultElement == null) ? null : createFault(faultElement);
   }


   /**
    * Looks for extension element state in the state element at this bpel object's location path.
    * If an extension element state is found then it traverses each extension, and delegates restoring
    * of state to the extension that is an IAeExtensionLifecycleAdapter.  
    * @param aImpl
    * @throws AeBusinessProcessException 
    */
   protected void restoreExtensionState(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      Element elem = AeXmlUtil.findSubElement(getImplState().getElement(aImpl), IAeActivityLifeCycleAdapter.EXTENSION_STATE_ELEMENT); 
      if (elem == null)
         return;
      
      Collection extensions = aImpl.getExtensions();
      if (extensions == null)
         return;
      
      for(Iterator iter=extensions.iterator(); iter.hasNext(); )
      {
         Object obj = iter.next();
         if (obj instanceof IAeExtensionLifecycleAdapter)
         {
            ((IAeExtensionLifecycleAdapter) obj).onRestore(elem);
         }
      }
   }
   /**
    * Creates an instance of <code>IAeFault</code> from its serialization.
    *
    * @param aElement
    * @return AeFault
    * @throws AeBusinessProcessException
    */
   protected IAeFault createFault(Element aElement) throws AeBusinessProcessException
   {
      int id = getAttributeInt(aElement, STATE_ID);
      IAeFault fault = (IAeFault) getObject(id);

      if (fault == null)
      {
         // Haven't seen this object id yet. Create the fault object.
         AeFaultDeserializer deserializer = new AeFaultDeserializer();
         deserializer.setFaultElement(aElement);
         deserializer.setProcess(getImplState().getProcess());

         fault = deserializer.getFault();

         // Save the fault object for future references to its object id.
         putIdObject(id, fault);
      }

      return fault;
   }

   /**
    * Restores an instance of <code>AeBaseEvent</code>.
    *
    * @param aBaseEvent
    * @throws AeBusinessProcessException
    */
   protected void restoreBaseEvent(AeBaseEvent aBaseEvent) throws AeBusinessProcessException
   {
      Element element = getImplState().getElement(aBaseEvent);
      boolean queued = getAttributeBoolean(element, STATE_QUEUED);

      aBaseEvent.setQueued(queued);
   }

   /**
    * Restores a process's partner links.
    *
    * @param aScope The scope implementation object.
    */
   protected static void restoreScopePartnerLinks(Element aScopeElement, AeActivityScopeImpl aScope) throws AeBusinessProcessException
   {
      // TODO (EPW) could share some logic with AeRestoreCompInfoVisitor::createCompInfoPartnerLinks (same with variables and corr. sets too)
      String xpath = "./" + STATE_PLINK; //$NON-NLS-1$
      List elements = selectNodes(aScopeElement, xpath, "Error restoring scope partner links"); //$NON-NLS-1$

      for (Iterator i = elements.iterator(); i.hasNext(); )
      {
         Element element = (Element) i.next();
         String name = getAttribute(element, STATE_NAME);
         AePartnerLink plink = aScope.findPartnerLink(name);
         
         restorePartnerLink(element, plink);
      }
   }

   /**
    * Restores variables.
    *
    * @param aElement The <code>Element</code> that owns the variable elements.
    * @param aVariableContainer The scope that owns the variables to restore.
    */
   protected static void restoreVariables(Element aElement, IAeVariableContainer aVariableContainer) throws AeBusinessProcessException
   {
      String xpath = "./" + STATE_VAR; //$NON-NLS-1$
      List elements = selectNodes(aElement, xpath, "Error restoring scope variables"); //$NON-NLS-1$
   
      for (Iterator i = elements.iterator(); i.hasNext(); )
      {
         Element element = (Element) i.next();
         String name = getAttribute(element, STATE_NAME);
         IAeVariable variable = aVariableContainer.findVariable(name);
   
         if (variable != null)
         {
            // cast to AeVariable because we need to access the process
            restoreVariable(element, (AeVariable) variable);
         }
      }
   }

   /**
    * Retores the list of open message activities.
    * @param aProcess
    * @throws AeBusinessProcessException
    */
   protected void restoreOpenMessageActivityList(AeBusinessProcess aProcess, Element aProcessElement) throws AeBusinessProcessException
   {
      String xpath = "./" + STATE_OPEN_MESSAGE_ACTIVITY; //$NON-NLS-1$
      List elements = selectNodes(aProcessElement, xpath, "Error restoring open message activity list"); //$NON-NLS-1$
      for (Iterator i = elements.iterator(); i.hasNext(); )
      {
         Element element = (Element) i.next();
         String plinkLocation = getAttribute(element, STATE_PLINK_LOCATION);
         AePartnerLink partnerLink = null;
         if (AeUtil.isNullOrEmpty(plinkLocation))
         {
            String partnerLinkName = getAttribute(element, STATE_PLINK);
            partnerLink = aProcess.findPartnerLink(partnerLinkName);
         }
         else
         {
            partnerLink = aProcess.findProcessPartnerLink(plinkLocation);
         }

         String operation = getAttribute(element, STATE_OPERATION);
         String messageExchangePath = null;
         String s = getAttribute(element, STATE_MESSAGE_EXCHANGE);
         
         if (AeUtil.notNullOrEmpty(s))
         {
            messageExchangePath = s;
         }
         long replyId = getAttributeLong(element, STATE_REPLY_ID);
         
         AePartnerLinkOpImplKey plOpImplKey = new AePartnerLinkOpImplKey(partnerLink, operation);
         
         AeOpenMessageActivityInfo info = new AeOpenMessageActivityInfo(plOpImplKey, messageExchangePath, replyId);
         // create durable reply info. (if available)
         xpath = "./" + STATE_DURABLE_REPLY; //$NON-NLS-1$
         Element durableReplyEle = (Element)selectOptionalNode(element, xpath, "Error restoring durableReply from openMessageActivity"); //$NON-NLS-1$
         if (durableReplyEle != null)
         {
            AeDurableReplyDeserializer des = new AeDurableReplyDeserializer();
            des.setDurableReplyInfoElement(durableReplyEle);
            IAeReplyReceiver replyReceiver = aProcess.getEngine().getTransmissionTracker().getDurableReplyFactory().createReplyReceiver(-1, des.getDurableReplyInfo() );
            info.setDurableReplyReceiver( replyReceiver );
         }
         // add to process list
         aProcess.addOpenMessageActivityInfo(info);
      }
      
   }
   
   /**
    * Restore any business process properties.
    *
    * @param aProcess
    * @throws AeBusinessProcessException
    */
   protected void restoreBusinessProcessProperties( AeBusinessProcess aProcess ) throws AeBusinessProcessException
   {
      try
      {
         Collection extensionPropertyElements = getImplState().getBusinessProcessPropertiesElements();
         if( extensionPropertyElements != null )
         {
            Map properties = new HashMap();
            for( Iterator iter = extensionPropertyElements.iterator(); iter.hasNext(); )
            {
               Element propertyExtEl = (Element)iter.next();
               AeBusinessProcessPropertyIO.extractBusinessProcessProperty(propertyExtEl, properties );
            }

            aProcess.addBusinessProcessProperties( properties );
         }
      }
      catch( AeException ae )
      {
         // should only happen if there is some problem selecting the
         // extension property nodes
         throw new AeBusinessProcessException( ae.getLocalizedMessage(), ae );
      }
   }
   
   /**
    * Restore any faulting activity location paths.
    *
    * @param aProcess
    * @throws AeBusinessProcessException
    */
   protected void restoreFaultingActivityLocationPaths( AeBusinessProcess aProcess ) throws AeBusinessProcessException
   {
      try
      {
         Collection faultingActivityElements = getImplState().getFaultingActivityElements();
         if( faultingActivityElements != null )
         {
            for( Iterator iter = faultingActivityElements.iterator(); iter.hasNext(); )
            {
               Element faultingActivityEl = (Element)iter.next();
               String locationPath = faultingActivityEl.getAttribute( STATE_LOC );
               aProcess.getFaultingActivityLocationPaths().add( locationPath );
            }
         }
      }
      catch( AeException ae )
      {
         // should only happen if there is some problem selecting the
         // faulting activity nodes
         throw new AeBusinessProcessException( ae.getLocalizedMessage(), ae );
      }
   }

   /**
    * Restores a scope's correlation sets.
    *
    * @param aScopeElement The <code>Element</code> that owns the correlation set elements.
    * @param aScope The scope that owns the correlation sets.
    */
   protected static void restoreScopeCorrelationSets(Element aScopeElement, AeActivityScopeImpl aScope) throws AeBusinessProcessException
   {
      String xpath = "./" + STATE_CORRSET; //$NON-NLS-1$
      List elements = selectNodes(aScopeElement, xpath, "Error restoring scope correlation sets"); //$NON-NLS-1$

      for (Iterator i = elements.iterator(); i.hasNext(); )
      {
         Element element = (Element) i.next();
         String name = getAttribute(element, STATE_NAME);
         AeCorrelationSet correlationSet = aScope.findCorrelationSet(name);

         if (correlationSet != null)
         {
            restoreCorrelationSet(element, correlationSet);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visitBase(org.activebpel.rt.bpel.impl.AeAbstractBpelObject)
    */
   protected void visitBase(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      Element element = getImplState().getElement(aImpl);
      
      if (aImpl.hasCustomLocationPath())
      {
         aImpl.setLocationId(getAttributeInt(element, STATE_LOCATIONID));
         AeBusinessProcess process = (AeBusinessProcess) aImpl.getProcess();
         process.addBpelObject(aImpl.getLocationPath(), aImpl);
      }

      AeBpelState state = AeBpelState.forName(getAttribute(element, STATE_STATE));
      if (state == null)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeRestoreImplStateVisitor.ERROR_29")); //$NON-NLS-1$
      }

      aImpl.setRawState(state);

      boolean terminating = getAttributeBoolean(element, STATE_TERMINATING);
      aImpl.setTerminating(terminating);

      // Restore fault.
      aImpl.setFault(createFault(aImpl));

      // restore HI state
      restoreExtensionState(aImpl);
      // Make sure business process can locate default and implicit objects.
      getImplState().getProcess().addBpelObject(aImpl.getLocationPath(), aImpl);

      boolean skipChildren = getAttributeBoolean(element, STATE_SKIPCHILDREN);
      if (!skipChildren)
      {
         super.visitBase(aImpl);
      }
      else
      {
         if(aImpl.hasCustomLocationPath())
         {
            // visit the locations which are not saved in the document to ensure that their ids are correct
            aImpl.accept(new AeRegisterLocationVisitor((AeBusinessProcess) aImpl.getProcess()));
         }
         
         // if this is a deadpath then propagate state
         if (state == AeBpelState.DEAD_PATH)
         {
            // Propagate dead path state to the children that we're skipping.
            aImpl.accept(DEADPATH_VISITOR);
         }
      }
   }
   
   /**
    * Visitor for setting the node and all of its children to dead path. This is used
    * in cases where a node's children were skipped due to the node being deadpath.
    * The visitor will set the raw state of each object to DEADPATH to avoid any
    * proproegration of events.
    */
   protected static class AeDeadPathVisitor extends AeImplTraversingVisitor
   {
      /**
       * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visitBase(org.activebpel.rt.bpel.impl.AeAbstractBpelObject)
       */
      protected void visitBase(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
      {
         super.visitBase(aImpl);
         
         if (aImpl instanceof AeLink)
         {
            // since the parent for the link (source) is a deadpath we need to set the links to false 
            ((AeLink)aImpl).setRawStatus(false);
         }
         else
         {
            // only need to set the state of the non-link bpel objects
            aImpl.setRawState(AeBpelState.DEAD_PATH);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityCompensateImpl)
    */
   public void visit(AeActivityCompensateImpl aImpl) throws AeBusinessProcessException
   {
      String nextIndex = getImplState().getAttribute(aImpl, STATE_NEXTINDEX);
      if (!AeUtil.isNullOrEmpty(nextIndex))
      {
         aImpl.setNextIndex(Integer.parseInt(nextIndex));
      }

      // We may have already traversed a compensation handler that needs to
      // call this compensate activity.
      AeCompensationHandler handler = (AeCompensationHandler) mCompensationCallbackOwnersMap.remove(aImpl.getLocationPath());
      if (handler != null)
      {
         handler.setCallback(aImpl);
      }

      super.visit(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityReceiveImpl)
    */
   public void visit(AeActivityReceiveImpl aImpl) throws AeBusinessProcessException
   {
      boolean queued = "true".equals(getImplState().getAttribute(aImpl, STATE_QUEUED)); //$NON-NLS-1$
      aImpl.setQueued(queued);
      super.visit(aImpl);
      if (aImpl.isQueued())
      {
         AeBusinessProcess process = (AeBusinessProcess) aImpl.getProcess();
         try
         {
            process.addReceiverKeyForConflictingReceives(aImpl);
         }
         catch (AeConflictingReceiveException e)
         {
            // Log any exceptions but don't fail the restore. There shouldn't be
            // any exceptions from this call since it's just restoring the
            // state of the process. 
            e.logError();
         }
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityChildExtensionActivityImpl)
    */
   public void visit(AeActivityChildExtensionActivityImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      
      // restore durable invoke tx id information.
      long id = Long.parseLong( getImplState().getAttribute(aImpl, STATE_TRANSMISSION_ID));
      aImpl.setTransmissionId(id);

      Element elem = null;
      try 
      {
         elem = getImplState().getElement(aImpl);
      }
      catch(AeBusinessProcessException ex)
      {
         return;
      }
      aImpl.getLifeCycleAdapter().restore(elem);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl)
    */
   public void visit(AeActivityInvokeImpl aImpl) throws AeBusinessProcessException
   {
      boolean queued = "true".equals(getImplState().getAttribute(aImpl, STATE_QUEUED)); //$NON-NLS-1$
      aImpl.setQueued(queued);
      String retries = getImplState().getAttribute(aImpl, STATE_RETRIES);
      if(AeUtil.notNullOrEmpty(retries))
      {
         aImpl.setRetries(Integer.parseInt(retries));
      }
      // restore durable invoke tx id information.
      long id = Long.parseLong( getImplState().getAttribute(aImpl, STATE_TRANSMISSION_ID));
      aImpl.setTransmissionId(id);

      // restore engine id.
      int engineId = Integer.parseInt( getImplState().getAttribute(aImpl, STATE_ENGINE_ID));
      aImpl.setEngineId(engineId);

      int alarmId = Integer.parseInt( getImplState().getAttribute(aImpl, STATE_ALARM_ID));
      aImpl.setAlarmId(alarmId);

      // Restore pending invoke journal entry id.
      long journalId = Long.parseLong(getImplState().getAttribute(aImpl, STATE_JOURNAL_ID));
      aImpl.setJournalId(journalId);
      
      super.visit(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeAbstractImplVisitor#visitScope(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   protected void visitScope(AeActivityScopeImpl aImpl)
         throws AeBusinessProcessException
   {
      Element element = getImplState().getElement(aImpl);

      boolean hasImplicitCompensationHandler = "true".equals(getImplState().getAttribute(aImpl, STATE_HASIMPLICITCOMPENSATIONHANDLER)); //$NON-NLS-1$
      boolean hasImplicitTerminationHandler  = "true".equals(getImplState().getAttribute(aImpl, STATE_HASIMPLICITTERMINATIONHANDLER)); //$NON-NLS-1$
      boolean hasImplicitFaultHandler        = "true".equals(getImplState().getAttribute(aImpl, STATE_HASIMPLICITFAULTHANDLER)); //$NON-NLS-1$
      boolean normalCompletion               = "true".equals(getImplState().getAttribute(aImpl, STATE_NORMALCOMPLETION)); //$NON-NLS-1$
      boolean hasCoordinations               = "true".equals(getImplState().getAttribute(aImpl, STATE_HASCOORDINATIONS)); //$NON-NLS-1$
      boolean snapshotRecorded               = "true".equals(getImplState().getAttribute(aImpl, STATE_SNAPSHOTRECORDED)); //$NON-NLS-1$
      String faultHandlerPath                = getAttribute(element, STATE_FAULTHANDLERPATH);
      
      aImpl.setNormalCompletion(normalCompletion);
      aImpl.setSnapshotRecorded(snapshotRecorded);
      
      if (hasImplicitCompensationHandler)
      {
         // Calling getCompensationHandler() will create the implicit
         // compensation handler if necessary. Create the implicit compensation
         // handler now, so that traverse() will see it.
         aImpl.getCompensationHandler();
      }
      
      if (hasImplicitTerminationHandler)
      {
         // Calling getTerminationHandler() will create the implicit
         // termination handler if necessary. Create the implicit termination
         // handler now, so that traverse() will see it.
         aImpl.getTerminationHandler();
      }

      if (hasImplicitFaultHandler)
      {
         // The implicit fault handler's fault will be restored when the fault
         // handler is traversed.
         aImpl.setFaultHandler(aImpl.createImplicitFaultHandler(null));
      }
      else if (AeUtil.notNullOrEmpty(faultHandlerPath))
      {
         // One of our faultHandlers is executing. Set that instance on the scope.
         for (Iterator iter = aImpl.getFaultHandlersIterator(); iter.hasNext();)
         {
            AeDefaultFaultHandler fh = (AeDefaultFaultHandler) iter.next();
            if (fh.getLocationPath().equals(faultHandlerPath))
            {
               aImpl.setFaultHandler(fh);
               break;
            }
         }
      }
      
      if (hasCoordinations)
      {
         // calling getCoordinationContainer creates the container.
         // this container will be visited by this traverser (via getChildrenForStateChange() )
         aImpl.getCoordinationContainer();
         
         // make a note that a scope had a coordinated invoke.
         // this flag will be use to set the process's isCoordinating state (for legacy compatibility).
         mScopeHadCoordination = true;
      }

      // Restore the scope's variables and correlation sets.
      if (aImpl.getVariableContainer() != null)
         restoreVariables(element, aImpl.getVariableContainer());
      restoreScopeCorrelationSets(element, aImpl);
      restoreScopePartnerLinks(element, aImpl);

      // the comp info for this impl is restored by another visitor
      super.visitScope(aImpl);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityOnEventScopeImpl)
    */
   public void visit(AeActivityOnEventScopeImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      // look for an inbound receive that needs to be restored.
      // this will only be here for isolated scopes instances that
      // were not able to execute immediately.
      Element implElement = getImplState().getElement(aImpl);
      String xpath = "./" + STATE_ONEVENT_MESSAGE; //$NON-NLS-1$
      Element messageElement = (Element) selectOptionalNode(implElement, xpath, "Error restoring onEvent message"); //$NON-NLS-1$

      AeInboundReceive inboundReceive = (messageElement == null) ? null : createCreateMessage(aImpl.getProcess(), messageElement);
      
      if (inboundReceive != null)
      {
         aImpl.setMessageContext(inboundReceive.getContext());
         aImpl.setMessageData(inboundReceive.getMessageData());
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityWaitImpl)
    */
   public void visit(AeActivityWaitImpl aImpl) throws AeBusinessProcessException
   {
      boolean queued = "true".equals(getImplState().getAttribute(aImpl, STATE_QUEUED)); //$NON-NLS-1$
      aImpl.setQueued(queued);
      int alarmId = Integer.parseInt( getImplState().getAttribute(aImpl, STATE_ALARM_ID));
      aImpl.setAlarmId(alarmId);

      super.visit(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityWhileImpl)
    */
   public void visit(AeActivityWhileImpl aImpl) throws AeBusinessProcessException
   {
      boolean queued = "true".equals(getImplState().getAttribute(aImpl, STATE_QUEUED)); //$NON-NLS-1$
      aImpl.setQueued(queued);
      int alarmId = Integer.parseInt( getImplState().getAttribute(aImpl, STATE_ALARM_ID));
      aImpl.setAlarmId(alarmId);
      
      restoreLoopControl(aImpl);      
      super.visit(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityRepeatUntilImpl)
    */
   public void visit(AeActivityRepeatUntilImpl aImpl) throws AeBusinessProcessException
   {
      boolean queued = "true".equals(getImplState().getAttribute(aImpl, STATE_QUEUED)); //$NON-NLS-1$
      aImpl.setQueued(queued);
      boolean firstIter = "true".equals(getImplState().getAttribute(aImpl, STATE_FIRST_ITER)); //$NON-NLS-1$
      aImpl.setFirstIteration(firstIter);      
      int alarmId = Integer.parseInt( getImplState().getAttribute(aImpl, STATE_ALARM_ID));
      aImpl.setAlarmId(alarmId);
      
      restoreLoopControl(aImpl);
      
      super.visit(aImpl);
   }
   
   /**
    * Restores the loop control flag which tells us if the loop is terminating its
    * children because of a continue or break activity having executed w/in it.
    * @param aImpl
    */
   protected void restoreLoopControl(AeLoopActivity aImpl) throws AeBusinessProcessException
   {
      // the loop termination flag tells us if the loop is terminating its children
      // because of a continue or break activity.
      String loopFlag = getImplState().getAttribute(aImpl, STATE_LOOP_TERMINATION_REASON);
      aImpl.setEarlyTerminationReason(Integer.parseInt(loopFlag));
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl)
    */
   public void visit(AeActivityForEachImpl aImpl)
         throws AeBusinessProcessException
   {
      restoreForEachAttributes(aImpl);
      
      super.visit(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityForEachParallelImpl)
    */
   public void visit(AeActivityForEachParallelImpl aImpl)
         throws AeBusinessProcessException
   {
      restoreForEachAttributes(aImpl);
      Element element = getImplState().getElement(aImpl);
      int instanceValue = getAttributeInt(element, STATE_INSTANCE_VALUE);
      aImpl.setInstanceValue(instanceValue);
      
      // If the counterValue is less than the finalValue then the forEach didn't execute
      // any scopes due to detecting a completion condition error. The forEach will evaluate
      // its start, final, and completionCondition expressions prior to creating any child
      // scopes. If it detects that there won't be enough iterations to meet the completionCondition
      // then the scope will fault and the counter value will never be set to the finalValue+1
      if (aImpl.getCounterValue() != -1 && aImpl.getCounterValue() > aImpl.getFinalValue() 
            &&
            // ensure that the start value is not greater than the final value
            // or there won't be anything to restore
            aImpl.getStartValue() <= aImpl.getFinalValue())
      {
         // The process for restoring a parallel forEach's state involves visiting
         // the forEach's state element and then creating one child scope for each
         // of the iterations that had executed the last time the forEach was persisted.
         // The forEach maintains an instance value which is used to create unique
         // location paths for each of its child scopes. 
         // We need to restore the forEach's instance value to the value it was at
         // when the forEach executed and created its children. This way, we'll 
         // create child scopes with the same instance paths that they had the last
         // time they'd been in memory. 
         int startInstance = AeLocationPathUtils.getStartInstanceValue(instanceValue, aImpl.getStartValue(), aImpl.getFinalValue());
         int finalInstance = AeLocationPathUtils.getFinalInstanceValue(instanceValue, aImpl.getStartValue(), aImpl.getFinalValue());
         List scopes = AeDynamicScopeCreator.create(false, aImpl, startInstance, finalInstance);
         aImpl.getChildren().addAll(scopes);
      }
      
      super.visit(aImpl);

      restoreCompensatingScopes(element);
   }

   /**
    * Restores any dynamic scopes that may have been compensating while the 
    * process state was saved.
    * @param aElement
    * @throws AeBusinessProcessException
    */
   protected void restoreCompensatingScopes(Element aElement) throws AeBusinessProcessException
   {
      // The dynamic scope container may contain child elements representing 
      // scopes that had their state saved since they were in the middle of 
      // compensating when the process was saved. The code below restores these 
      // scopes and their children.
      String xpath = "./"+STATE_ACTY+"[@" + STATE_SCOPE_COMPENSATING + "='true']"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      List compensatingScopes = selectNodes(aElement, xpath, "Error restoring compensating scopes within dynamic scope container"); //$NON-NLS-1$
      for (Iterator iter = compensatingScopes.iterator(); iter.hasNext();)
      {
         Element scopeElement = (Element) iter.next();
         String scopeLocation = scopeElement.getAttribute(STATE_LOC);
         AeActivityScopeImpl scope = restoreScopeInstance(scopeLocation, false);
         scope.accept(this);
      }
   }
   
   /**
    * Reads the standard attributes for the forEach activity, whether its serial or parallel
    * @param aImpl
    * @throws AeBusinessProcessException
    */
   protected void restoreForEachAttributes(AeActivityForEachImpl aImpl) throws AeBusinessProcessException
   {
      restoreLoopControl(aImpl);
      
      Element element = getImplState().getElement(aImpl);
      int counterValue = getAttributeInt(element, STATE_FOREACH_COUNTER);
      int startValue = getAttributeInt(element, STATE_FOREACH_START);
      int finalValue = getAttributeInt(element, STATE_FOREACH_FINAL);
      int completionCondition = getAttributeInt(element, STATE_FOREACH_COMPLETIONCONDITION);
      int completionCount = getAttributeInt(element, STATE_FOREACH_COMPLETIONCOUNT);
      
      aImpl.setCounterValue(counterValue);
      aImpl.setStartValue(startValue);
      aImpl.setFinalValue(finalValue);
      aImpl.setCompletionCondition(completionCondition);
      aImpl.setCompletionCount(completionCount);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.AeBusinessProcess)
    */
   public void visit(AeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      // Restore process fields.
      Element element = getImplState().getElement(aProcess);
      int processState = getAttributeInt(element, STATE_PROCESSSTATE);
      int processStateReason = getAttributeInt(element, STATE_PROCESSSTATEREASON);
      Date endDate = toDate(getAttribute(element, STATE_ENDDATEMILLIS));
      Date startDate = toDate(getAttribute(element, STATE_STARTDATEMILLIS));
      int maxLocationId = getAttributeInt(element, STATE_MAXLOCATIONID);
      // Note: coordinator and participant flags are new for AE 2.1
      boolean coordinator = getAttributeBoolean(element, STATE_COORDINATOR);
      boolean participant = getAttributeBoolean(element, STATE_PARTICIPANT);
      boolean exiting = getAttributeBoolean(element, STATE_EXITING);
      // process initiator
      String initiator = getAttribute(element, STATE_PROCESSINITIATOR);
      // record the version of the state that we're reading in case there's any modifications we need to make to the process post read
      getImplState().setStateVersion(getAttribute(element, STATE_DOC_VERSION));
      
      int nextVariableId = getAttributeInt(element, STATE_NEXTVARIABLEID);
      if (nextVariableId == -1)
      {
         // This accounts for processes saved prior to the introduction of the 
         // variable id. We'll walk all of the variables in the state document 
         // and record the highest variable version number.
         String xpath = "//" + STATE_VAR + "/@" + STATE_VERSION; //$NON-NLS-1$ //$NON-NLS-2$
         List nodes = selectNodes(element.getOwnerDocument(), xpath, "Error selecting variable versions"); //$NON-NLS-1$
         nextVariableId = 0;
         for (Iterator iter = nodes.iterator(); iter.hasNext();)
         {
            Attr attr = (Attr) iter.next();
            nextVariableId = Math.max(nextVariableId, Integer.parseInt(attr.getNodeValue()));
         }
         // add one to move to a unique value
         nextVariableId += 1;
      }
      // restore invoke id.      
      long invokeId = getAttributeLong(element, STATE_INVOKE_ID);      
      int alarmId = getAttributeInt(element, STATE_ALARM_ID);
      
      aProcess.setProcessState(processState);
      aProcess.setProcessStateReason(processStateReason);
      aProcess.setEndDate(endDate);
      aProcess.setStartDate(startDate);
      aProcess.setMaxLocationId(maxLocationId);
      aProcess.setNextVersionNumber(nextVariableId);
      aProcess.setExiting(exiting);
      aProcess.setProcessInitiator(initiator);

      aProcess.setCreateMessage(createCreateMessage(aProcess));
      aProcess.setInvokeId(invokeId);
      aProcess.setAlarmId(alarmId);
      
      if(((AeProcessDef)aProcess.getDefinition()).containsSerializableScopes())
      {
         Node variableLockerData = getImplState().getVariableLockerData();
         aProcess.setVariableLockerData(variableLockerData);
      }

      // Restore any business process properties
      restoreBusinessProcessProperties(aProcess);
      
      // Restore any faulting activity location paths
      restoreFaultingActivityLocationPaths(aProcess);

      // Now handle the process as a scope.
      visit((AeActivityScopeImpl) aProcess);
      
      // Restore open message activity list
      restoreOpenMessageActivityList(aProcess, element);

      // Scope visit needs to happen before we set the location paths for the process as they are dependent
      // upon impl objects created in the scope visitation.
      boolean suspended = isSuspended(processState);
      List locationPaths = getImplState().getExecutionQueuePaths();
      aProcess.setExecutionQueue(suspended, locationPaths);
      
      // Set the coordinator flag.
      // Visiting the scopes would have also set a flag to indicate that at least one of the 
      // scopes had a coordinated invoke. Use this for legacy/backward compatibility.      
      aProcess.setCoordinator( coordinator || mScopeHadCoordination);
      
      // Set the participant flag. For legacy compatibility (pre 2.1), we should
      // also check the business properties.
      participant = participant || AeUtil.notNullOrEmpty(aProcess.getBusinessProcessProperty(IAeCoordinating.WSCOORD_ID)); 
      aProcess.setParticipant(participant);
   }
   
   /**
    * Returns true if the process is in a suspended state.
    * 
    * @param aProcessState
    */
   protected boolean isSuspended( int aProcessState )
   {
      return aProcessState == IAeBusinessProcess.PROCESS_SUSPENDED;
   }
   
   /** 
    * Restore the attributes common to fault and compensation handlers.
    * @param aImpl
    * @throws AeBusinessProcessException
    */
   protected void restoreFCTHandler(AeFCTHandler aImpl) throws AeBusinessProcessException
   {
      boolean hasCoordCompensator = "true".equals(getImplState().getAttribute(aImpl, STATE_HASCOORDCOMPENSATOR)); //$NON-NLS-1$
      if (hasCoordCompensator)
      {
         // creates the implicit compensate activity which will be 
         // returned as part of getChildrenForStateChange - hence
         // will be visited by this state restorer.
         aImpl.getCoordinatedActivityCompensator();
      }
   }   
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler)
    */
   public void visit(AeCompensationHandler aImpl) throws AeBusinessProcessException
   {
      // Restore base
      restoreFCTHandler(aImpl);
      
      // Restore compensation callback.
      String callbackLocationPath = getImplState().getAttribute(aImpl, STATE_CALLBACK);
      if (!AeUtil.isNullOrEmpty(callbackLocationPath))
      {         
         // check to see if this is a callback for a coordinated compensator.
         // (instead of a normal compensate activity i.e. AeActivityCompensateImpl)
         boolean coordinated = "true".equals(getImplState().getAttribute(aImpl, STATE_CALLBACK_COORDINATED)); //$NON-NLS-1$
         String coordinationId = getImplState().getAttribute(aImpl, STATE_CALLBACK_COORD_ID);
         
         IAeCompensationCallback callback = null;
         if (coordinated)
         {
            // create a coordinated compensator.
            callback = new AeParticipantCompensator(coordinationId, getImplState().getProcess().getEngine() );

            // if it's the compensation handler for the process, then wrap the callback
            // to ensure that the process's state changes when the compensation is complete
            if (aImpl.getParent() == aImpl.getProcess())
            {
               callback = new AeProcessCompensationCallbackWrapper(callback);
            }
         }
         else
         {
            callback = (IAeCompensationCallback) getImplState().getProcess().findBpelObject(callbackLocationPath);
         }

         if (callback == null)
         {
            // If we couldn't find the callback object, then it's probably an
            // implicit compensate activity that hasn't been traversed yet. Put
            // this compensation handler into a map keyed by callback location,
            // so that visit(AeActivityCompensateImpl aImpl) can set the
            // callback object later.
            mCompensationCallbackOwnersMap.put(callbackLocationPath, aImpl);
         }
         else
         {
            aImpl.setCallback(callback);
         }
      }
      
      // the comp info for this impl is restored by another visitor

      super.visit(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeImplicitCompensationHandler)
    */
   public void visit(AeImplicitCompensationHandler aImpl) throws AeBusinessProcessException
   {
      visit((AeCompensationHandler) aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeLink)
    */
   public void visit(AeLink aImpl) throws AeBusinessProcessException
   {
      String status = getImplState().getAttribute(aImpl, STATE_EVAL);

      if (STATE_UNKNOWN.equals(status))
      {
         aImpl.clearStatus();
      }
      else
      {
         aImpl.setRawStatus("true".equals(status)); //$NON-NLS-1$
      }

      super.visit(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm)
    */
   public void visit(AeOnAlarm aImpl) throws AeBusinessProcessException
   {
      restoreBaseEvent(aImpl);
      int alarmId = Integer.parseInt( getImplState().getAttribute(aImpl, STATE_ALARM_ID));
      aImpl.setAlarmId(alarmId);
      super.visit(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeOnMessage)
    */
   public void visit(AeOnMessage aImpl) throws AeBusinessProcessException
   {
      restoreBaseEvent(aImpl);
      super.visit(aImpl);
      
      if (aImpl.isQueued())
      {
         AeBusinessProcess process = (AeBusinessProcess) aImpl.getProcess();
         try
         {
            process.addReceiverKeyForConflictingReceives(aImpl);
         }
         catch (AeConflictingReceiveException e)
         {
            // Log any exceptions but don't fail the restore. There shouldn't be
            // any exceptions from this call since it's just restoring the
            // state of the process. 
            e.logError();
         }
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeOnEvent)
    */
   public void visit(AeOnEvent aImpl) throws AeBusinessProcessException
   {
      Element element = getImplState().getElement(aImpl);
      if (aImpl.isConcurrent())
      {
         restoreEventHandlerDynamicScopes(aImpl, element);
         restoreCompensatingScopes(element);
      }

      visit((AeOnMessage)aImpl);
   }

   /**
    * Restores the dynamic scope children for the parent
    * @param aImpl
    * @param aElement
    */
   protected void restoreEventHandlerDynamicScopes(IAeDynamicScopeParent aImpl, Element aElement)
   {
      int instanceValue = getAttributeInt(aElement, STATE_INSTANCE_VALUE);
      aImpl.setInstanceValue(instanceValue);
      int instanceCount = getAttributeInt(aElement, STATE_INSTANCE_COUNT);
      if (instanceCount > 0)
      {
         // there are children to restore
         int startValue = instanceValue - instanceCount;
         int finalValue = instanceValue - 1;
         List scopes = AeDynamicScopeCreator.create(false, aImpl, startValue, finalValue );
         aImpl.getChildren().addAll(scopes);
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeRepeatableOnAlarm)
    */
   public void visit(AeRepeatableOnAlarm aImpl) throws AeBusinessProcessException
   {
      Element element = getImplState().getElement(aImpl);
      if (aImpl.isConcurrent())
      {
         restoreEventHandlerDynamicScopes(aImpl, element);
         restoreCompensatingScopes(element);
      }
      
      String repeatEveryDuration = getImplState().getAttribute(aImpl, STATE_INTERVAL);
      AeSchemaDuration duration = new AeSchemaDuration(repeatEveryDuration);
      aImpl.setRepeatEveryDuration(duration);

      visit((AeOnAlarm)aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCoordinationContainer)
    */
   public void visit(AeCoordinationContainer aImpl) throws AeBusinessProcessException
   {
      // get count.
      int count = getAttributeInt(getImplState().getElement(aImpl), STATE_COORDINATION_COUNT);
      // get each coord id and state and restore it.
      for (int index = 0; index < count; index++)
      {  
         String idName = STATE_COORDINATION_ID + String.valueOf(index);
         String stateName = STATE_COORD_STATE + String.valueOf(index);
         String id = getImplState().getAttribute(aImpl, idName);
         String state = getImplState().getAttribute(aImpl, stateName);
         aImpl.restoreCoordination(id, state);
      }
      super.visit(aImpl);
   }

   /** 
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCoordinatorCompensationHandler)
    */
   public void visit(AeCoordinatorCompensationHandler aImpl) throws AeBusinessProcessException
   {
      visit((AeCompensationHandler) aImpl);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeDefaultFaultHandler)
    */
   public void visit(AeDefaultFaultHandler aImpl) throws AeBusinessProcessException
   {
      // restore base handler information
      restoreFCTHandler( (AeFCTHandler) aImpl);
      
      super.visit(aImpl);

      // Restore the 'handled' fault after traversing so that 'already seen'
      // objects are handled properly.
      Element implElement = getImplState().getElement(aImpl);
      String xpath = "./" + STATE_HANDLED_FAULT; //$NON-NLS-1$
      Element handledFaultElement = (Element) selectOptionalNode(implElement, xpath, "Error restoring handled fault"); //$NON-NLS-1$

      if (handledFaultElement != null)
      {
         Element faultElem = AeXmlUtil.getFirstSubElement(handledFaultElement);
         IAeFault fault = createFault(faultElem);
         if (fault != null)
         {
            aImpl.setHandledFault(fault);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeFaultHandler)
    */
   public void visit(AeFaultHandler aImpl) throws AeBusinessProcessException
   {
      visit((AeDefaultFaultHandler) aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeWSBPELFaultHandler)
    */
   public void visit(AeWSBPELFaultHandler aImpl) throws AeBusinessProcessException
   {
      visit((AeFaultHandler) aImpl);
      restoreVariables(getImplState().getElement(aImpl), aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeImplicitFaultHandler)
    */
   public void visit(AeImplicitFaultHandler aImpl) throws AeBusinessProcessException
   {
      visit((AeDefaultFaultHandler) aImpl);
   }
}
