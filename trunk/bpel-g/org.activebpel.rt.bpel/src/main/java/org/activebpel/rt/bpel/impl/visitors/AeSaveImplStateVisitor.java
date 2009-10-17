// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeSaveImplStateVisitor.java,v 1.68 2008/03/11 03:05:23 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.AeBusinessProcessPropertyIO;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AeProcessImplState;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
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
import org.activebpel.rt.bpel.impl.activity.AeLoopActivity;
import org.activebpel.rt.bpel.impl.activity.IAeActivityLifeCycleAdapter;
import org.activebpel.rt.bpel.impl.activity.IAeExtensionLifecycleAdapter;
import org.activebpel.rt.bpel.impl.activity.support.AeBaseEvent;
import org.activebpel.rt.bpel.impl.activity.support.AeCompInfo;
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
import org.activebpel.rt.bpel.impl.activity.support.AeRepeatableOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeScopeSnapshot;
import org.activebpel.rt.bpel.impl.activity.support.AeWSBPELFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.storage.AeDurableReplySerializer;
import org.activebpel.rt.bpel.impl.storage.AeInboundReceiveSerializer;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Visit / traverse the impl to save state at each node.
 */
public class AeSaveImplStateVisitor extends AeImplTraversingVisitor implements IAeImplStateNames
{   
   /** Standard date format for human-readable date strings. */
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS z"); //$NON-NLS-1$

   /** Array of states that allow skipping the traversal of child activities. */
   private static final AeBpelState[] sSkipChildrenStatesArray = new AeBpelState[]
   {
      AeBpelState.DEAD_PATH,
      AeBpelState.INACTIVE,
      AeBpelState.QUEUED_BY_PARENT,
      AeBpelState.READY_TO_EXECUTE
   };

   /** Set of states that allow skipping the traversal of child activities. */
   private static final Set sSkipChildrenStates = Collections.unmodifiableSet(new HashSet(Arrays.asList(sSkipChildrenStatesArray)));

   /** Process state object to which implementation state is saved. */
   private AeProcessImplState mImplState;

   /** Maps objects to object ids. */
   private final Map mObjectIdMap = new HashMap();

   /** Next available object id. */
   private int mNextObjectId = 1;

   /** <code>true</code> if and only if serializing for persistence. */
   private boolean mForPersistence;

   /** flag to disable the skipping of child activities when writing state document */
   private boolean mSkipEnabled = true;

   /**
    * Ctor to set the process state object we'll use.
    *
    * @param aProcessState
    */
   public AeSaveImplStateVisitor(AeProcessImplState aProcessState)
   {
      setImplState(aProcessState);
   }

   /**
    * Get the process state object used by this visitor.
    *
    * @return AeProcessImplState
    */
   public AeProcessImplState getImplState()
   {
      if (mImplState == null)
      {
         setImplState(new AeProcessImplState());
      }

      return mImplState;
   }

   /**
    * Returns <code>true</code> if and only if serializing for persistence.
    */
   public boolean isForPersistence()
   {
      return mForPersistence;
   }

   /**
    * Sets the flag specifying whether we are serializing for persistence.
    *
    * @param aForPersistence
    */
   public void setForPersistence(boolean aForPersistence)
   {
      mForPersistence = aForPersistence;
   }

   /**
    * Set the process state object to be used by this visitor.
    *
    * @param aState The AeProcessImplState object to set.
    */
   public void setImplState(AeProcessImplState aState)
   {
      mImplState = aState;
   }

   /**
    * Appends base event attributes.
    *
    * @param aBaseEvent
    */
   protected void appendBaseEventAttributes(AeBaseEvent aBaseEvent)
   {
      boolean queued = aBaseEvent.isQueued();
      getImplState().appendAttribute(STATE_QUEUED, String.valueOf(queued));
   }

   /**
    * Appends serialization data for an <code>AeCompInfo</code> instance.
    *
    * @param aCompInfo
    */
   protected void appendCompInfo(AeCompInfo aCompInfo) throws AeBusinessProcessException
   {
      Integer id = getObjectId(aCompInfo);
      boolean seen = (id != null);

      if (!seen)
      {
         id = getNextObjectId();
         putObjectId(aCompInfo, id);
      }

      getImplState().appendElement(
         STATE_COMPINFO,
         new String[]
         {
            STATE_ID,               String.valueOf(id),
            STATE_SCOPE,            String.valueOf(aCompInfo.getScope().getLocationId()),
            STATE_HASCOORDINATIONS, String.valueOf(aCompInfo.isCoordinated()),
            STATE_COORDINATION_ID,  String.valueOf(aCompInfo.getCoordinationId())
         }
      );

      // comp infos that reference scopes with custom location paths should 
      // record the path as part of their state. The issue is that this scope 
      // might not exist when the process state gets restored so we'll need to 
      // recreate the scope from its location path, something that we couldn't 
      // do from its id alone.
      if (aCompInfo.getScope().hasCustomLocationPath())
      {
         getImplState().appendAttribute(STATE_SCOPE_LOCATION, aCompInfo.getScope().getLocationPath());
      }

      if (!seen)
      {
         boolean enabled = aCompInfo.isEnabled();

         getImplState().appendAttribute(STATE_ENABLED, String.valueOf(enabled));

         if (enabled)
         {
            getImplState().pushParent();

            try
            {
               AeScopeSnapshot snapshot = aCompInfo.getSnapshot();
               if (snapshot != null)
               {
                  appendScopeSnapshot(snapshot);
               }

               // Recursively save enclosed compensation info.
               for (Iterator i = aCompInfo.getEnclosedScopes().iterator(); i.hasNext(); )
               {
                  AeCompInfo enclosedScope = (AeCompInfo) i.next();
                  appendCompInfo(enclosedScope);
               }
            }
            finally
            {
               getImplState().popParent();
            }
         }
      }
   }

   /**
    * Appends a series of elements for the specified correlation properties.
    *
    * @param aProperties
    */
   protected void appendCorrelationProperties(Map aProperties)
   {
      // save each name/value pair
      for (Iterator iterNames = aProperties.keySet().iterator(); iterNames.hasNext(); )
      {
         QName partName = (QName)iterNames.next();
         Object value = aProperties.get(partName);

         getImplState().appendElement(
            STATE_PROPERTY,
            new String[]
            {
               STATE_NAME        , partName.getLocalPart(),
               STATE_NAMESPACEURI, partName.getNamespaceURI(),
               STATE_VALUE       , value.toString()
            }
         );
      }
   }

   /**
    * Appends a correlation set.
    *
    * @param aCorrelationSet The correlation set to append.
    */
   protected void appendCorrelationSet(AeCorrelationSet aCorrelationSet) throws AeBusinessProcessException
   {
      boolean initialized = aCorrelationSet.isInitialized();

      getImplState().appendElement(
         STATE_CORRSET,
         new String []
         {
            STATE_NAME   , aCorrelationSet.getName(),
            STATE_INIT   , String.valueOf(initialized),
            STATE_VERSION, String.valueOf(aCorrelationSet.getVersionNumber())
         }
      );

      // if not for persistence then give full location path rather than name
      if (! isForPersistence())
      {
         getImplState().appendAttribute(STATE_LOC, aCorrelationSet.getLocationPath());
      }


      if (aCorrelationSet.hasCustomLocationPath())
      {
         getImplState().appendAttribute(STATE_LOCATIONID, String.valueOf(aCorrelationSet.getLocationId()));
      }

      if (initialized)
      {
         getImplState().pushParent();

         try
         {
            Map properties;

            try
            {
               properties = aCorrelationSet.getPropertyValues();
            }
            catch (AeBpelException e)
            {
               throw new AeBusinessProcessException(AeMessages.getString("AeSaveImplStateVisitor.ERROR_8") + aCorrelationSet.getLocationPath(), e); //$NON-NLS-1$
            }

            if (properties.size() > 0)
            {
               appendCorrelationProperties(properties);
            }
         }
         finally
         {
            getImplState().popParent();
         }
      }
   }

   /**
    * Appends a subtree containing the process's create message.
    *
    * @param aProcess
    * @throws AeBusinessProcessException
    */
   protected void appendCreateMessage(AeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      AeInboundReceive createMessage = aProcess.getCreateMessage();
      if (createMessage != null)
      {
         appendInboundReceive(createMessage, STATE_CREATEMESSAGE);
      }
   }

   /**
    * Appends the inbound receive to the current element using the given element 
    * name
    * @param aMessage - message being saved
    * @param aElementName - name of the tag to use for the message element
    * @throws AeBusinessProcessException
    */
   private void appendInboundReceive(AeInboundReceive aMessage, String aElementName) throws AeBusinessProcessException
   {
      getImplState().appendElement(aElementName);
      getImplState().pushParent();
      // save reply id
      getImplState().appendAttribute(STATE_REPLY_ID, String.valueOf(aMessage.getReplyId()));
      try
      {
         AeInboundReceiveSerializer serializer = new AeInboundReceiveSerializer();
         serializer.setInboundReceive(aMessage);
         serializer.setTypeMapping(getImplState().getProcess().getEngine().getTypeMapping());

         AeFastElement createMessageElement = serializer.getInboundReceiveElement();
         getImplState().appendElement(createMessageElement);
      }
      finally
      {
         getImplState().popParent();
      }
   }

   /**
    * Appends process's list of open message activity info objects.
    * @param aProcess
    * @throws AeBusinessProcessException
    */
   protected void appendOpenMessageActivityInfo(AeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      Iterator it = aProcess.getOpenMessageActivityInfoList().iterator();
      while (it.hasNext())
      {
         AeOpenMessageActivityInfo info = (AeOpenMessageActivityInfo) it.next();
         getImplState().appendElement(STATE_OPEN_MESSAGE_ACTIVITY);
         getImplState().pushParent();
         try
         {
            getImplState().appendAttribute(STATE_PLINK_LOCATION, info.getPartnerLinkOpImplKey().getPartnerLinkLocationPath());
            getImplState().appendAttribute(STATE_OPERATION, info.getPartnerLinkOpImplKey().getOperation());
            if (info.getMessageExchangePath() != null)
            {
               getImplState().appendAttribute(STATE_MESSAGE_EXCHANGE, info.getMessageExchangePath());
            }            
            getImplState().appendAttribute(STATE_REPLY_ID, String.valueOf(info.getReplyId()));
            if (info.hasDurableReply())
            {               
               AeDurableReplySerializer ds = new AeDurableReplySerializer();
               ds.setDurableReplyInfo(info.getDurableReplyReceiver().getDurableReplyInfo());
               AeFastElement replyElement = ds.getDurableReplyInfoElement();
               getImplState().appendElement(replyElement);
            }
         }
         finally
         {
            getImplState().popParent();
         }         
      }
   }
   
   /**
    * Appends a subtree containing the process's execution queue.
    *
    * @param aProcess
    */
   protected void appendExecutionQueue(AeBusinessProcess aProcess)
   {
      getImplState().appendElement(STATE_QUEUE);
      getImplState().pushParent();

      try
      {
         List locationPaths = aProcess.getExecutionQueuePaths();

         for (Iterator i = locationPaths.iterator(); i.hasNext(); )
         {
            String locationPath = (String) i.next();
            getImplState().appendElement(STATE_QUEUEITEM, new String[] { STATE_LOC, locationPath });
         }
      }
      finally
      {
         getImplState().popParent();
      }
   }

   /**
    * Appends a fault.
    *
    * @param aFault
    * @throws AeBusinessProcessException
    */
   protected void appendFault(IAeFault aFault) throws AeBusinessProcessException
   {
      Integer id = getObjectId(aFault);
      boolean seen = (id != null);

      if (!seen)
      {
         id = getNextObjectId();
         putObjectId(aFault, id);

         AeFastElement faultElement = getImplState().getProcess().fastSerializeFault(aFault);
         getImplState().appendElement(faultElement);
      }
      else
      {
         getImplState().appendElement(STATE_FAULT);
      }

      getImplState().appendAttribute(STATE_ID, String.valueOf(id));
   }

   /**
    * Traverses through each extension and if if the extension element is an IAeExtensionLifecycleAdapter
    * then delegates state element creation to the extension.
    * @param aExtensions
    */
   protected void appendExtensionState(Collection aExtensions)
   {
      Element elem = AeXmlUtil.createElement(IAeActivityLifeCycleAdapter.EXTENSION_STATE_NAMESPACE, IAeActivityLifeCycleAdapter.EXTENSION_NAMESPACE_PREFIX, IAeActivityLifeCycleAdapter.EXTENSION_STATE_ELEMENT); 
      for(Iterator iter=aExtensions.iterator(); iter.hasNext(); )
      {
         Object obj = iter.next();
         if (obj instanceof IAeExtensionLifecycleAdapter)
            ((IAeExtensionLifecycleAdapter) obj).onSave(elem);
      }
      if (elem.hasChildNodes())
         getImplState().appendForeignNode(elem);
      
   }
   /**
    * Appends a partner link definition.
    *
    * @param aPartnerLink The partner link to save.
    */
   protected void appendPartnerLink(AePartnerLink aPartnerLink) throws AeBusinessProcessException
   {
      getImplState().appendElement(
         STATE_PLINK,
         new String []
         {
            STATE_NAME, aPartnerLink.getName(),
            STATE_VERSION, String.valueOf(aPartnerLink.getVersionNumber())
         }
      );

      if (isForPersistence())
      {
         // Use impl location id (instead of def's location id) to account for 
         // instance()s in forEach etc.
         getImplState().appendAttribute(STATE_LOCATIONID, String.valueOf(aPartnerLink.getLocationId()));
      }
      else
      {
         // Use impl location path (instead of def's location path) to account 
         // for instance()s in forEach etc.
         getImplState().appendAttribute(STATE_LOC, aPartnerLink.getLocationPath());
      }

      getImplState().pushParent();

      try
      {
         if (!AeUtil.isNullOrEmpty(aPartnerLink.getMyRole()))
         {
            getImplState().appendElement(STATE_ROLE);
            getImplState().pushParent();
            
            try
            {
               Node referenceData = AeXmlUtil.getFirstSubElement(aPartnerLink.getMyReference().toDocument());
               getImplState().appendForeignNode(referenceData);
            }
            finally
            {
               getImplState().popParent();
            }
         }

         if (!AeUtil.isNullOrEmpty(aPartnerLink.getPartnerRole()))
         {
            getImplState().appendElement(STATE_PROLE);
            getImplState().pushParent();
            try
            {
               Node referenceData = AeXmlUtil.getFirstSubElement(aPartnerLink.getPartnerReference().toDocument());
               getImplState().appendForeignNode(referenceData);
            }
            finally
            {
               getImplState().popParent();
            }
         }
      }
      finally
      {
         getImplState().popParent();
      }
   }

   /**
    * Appends variables and correlation sets from a scope snapshot.
    *
    * @param aSnapshot
    */
   protected void appendScopeSnapshot(AeScopeSnapshot aSnapshot) throws AeBusinessProcessException
   {
      for (Iterator i = aSnapshot.getVariables().iterator(); i.hasNext(); )
      {
         IAeVariable variable = (IAeVariable) i.next();
         appendVariable(variable);
      }

      for (Iterator i = aSnapshot.getCorrelationSets().iterator(); i.hasNext(); )
      {
         AeCorrelationSet correlationSet = (AeCorrelationSet) i.next();
         appendCorrelationSet(correlationSet);
      }

      for (Iterator i = aSnapshot.getPartnerLinks().iterator(); i.hasNext(); )
      {
         AePartnerLink partnerLink = (AePartnerLink) i.next();
         appendPartnerLink(partnerLink);
      }
   }

   /**
    * Appends a variable.
    *
    * @param aVariable The variable.
    */
   public void appendVariable(IAeVariable aVariable) throws AeBusinessProcessException
   {
      getImplState().appendElement(
         STATE_VAR,
         new String[]
         {
            STATE_NAME   , aVariable.getName(),
            STATE_DATA   , "no", //$NON-NLS-1$
            STATE_HASDATA, String.valueOf(aVariable.hasData()),
            STATE_HASATTACHMENTS, String.valueOf(aVariable.hasAttachments()),
            STATE_VERSION, String.valueOf(aVariable.getVersionNumber())
         }
      );
   }

   /**
    * Appends a subtree containing the process's variable locker.
    *
    * @param aProcess
    * @throws AeBusinessProcessException
    */
   protected void appendVariableLocker(AeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      getImplState().appendElement(STATE_VARIABLELOCKER);
      getImplState().pushParent();

      try
      {
         DocumentFragment variableLockerData = aProcess.getVariableLockerData();
         if (variableLockerData != null)
         {
            NodeList nodes = variableLockerData.getChildNodes();

            for (int i = 0; i < nodes.getLength(); ++i)
            {
               getImplState().appendForeignNode(nodes.item(i));
            }
         }
      }
      finally
      {
         getImplState().popParent();
      }
   }

   /**
    * Returns <code>true</code> if and only it is possible to skip the traversal
    * of the given activity's children.
    */
   protected boolean canSkipChildTraversal(AeAbstractBpelObject aImpl)
   {
      // do not skip scopes that are inactive if their compensation handlers
      // are executing (or faulting).
      boolean skip = false;
      if (isSkipEnabled())
      {
         // Don't skip if the bpel object has a fault associated with it.
         skip = isForPersistence() && aImpl.getFault() == null && (aImpl instanceof IAeActivityParent) && sSkipChildrenStates.contains(aImpl.getState());

         // special check to see if
         if (skip && aImpl instanceof AeActivityScopeImpl)
         {
            AeActivityScopeImpl scope = (AeActivityScopeImpl) aImpl;
            skip = !scope.isCompensating();
         }
      }
      return skip;
   }

   /**
    * Utility to lookup the correct XML element name for a given impl instance.
    *
    * @param aImpl The instance to look up.
    *
    * @return String
    */
   protected String getElementName(AeAbstractBpelObject aImpl)
   {
      if (aImpl instanceof AeLink)
         return STATE_LINK;
      else if (aImpl instanceof AeBusinessProcess)
         return STATE_ROOT;
      else
         return STATE_ACTY;
   }

   /**
    * Returns next available object id.
    */
   protected Integer getNextObjectId()
   {
      return new Integer(mNextObjectId++);
   }

   /**
    * Returns id for specified object.
    *
    * @param aObject
    */
   protected Integer getObjectId(Object aObject)
   {
      return (Integer) getObjectIdMap().get(aObject);
   }

   /**
    * Returns <code>Map</code> from objects to object ids.
    */
   protected Map getObjectIdMap()
   {
      return mObjectIdMap;
   }

   /**
    * Assign the specified id to the specified object.
    *
    * @param aObject
    * @param aObjectId
    */
   protected void putObjectId(Object aObject, Integer aObjectId)
   {
      getObjectIdMap().put(aObject, aObjectId);
   }

   /**
    * Converts a <code>Date</code> to a string representation of the date in
    * milliseconds. The empty string represents a null date.
    *
    * @param aDate
    * @return String
    */
   protected String toMillisString(Date aDate)
   {
      String result = ""; //$NON-NLS-1$

      if (aDate != null)
      {
         long millis = aDate.getTime();
         result = String.valueOf(millis);
      }

      return result;
   }

   /**
    * Converts a <code>Date</code> to a string representation of the date. The
    * empty string represents a null date.
    *
    * @param aDate
    * @return String
    */
   protected String toString(Date aDate)
   {
      String result = ""; //$NON-NLS-1$

      if (aDate != null)
      {
         result = DATE_FORMAT.format(aDate);
      }

      return result;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visitBase(org.activebpel.rt.bpel.impl.AeAbstractBpelObject)
    */
   protected void visitBase(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      // Append a new element for this implementation object.
      getImplState().appendElement(
         getElementName(aImpl),
         new String[]
         {
            STATE_STATE      , aImpl.getState().toString(),
            STATE_TERMINATING, String.valueOf(aImpl.isTerminating())
         }
      );

      // parallel forEach introduces the concept of multiple implementation 
      // objects getting created from a single definition. In this case, we need 
      // to persist the object's location path and location id since they will 
      // differ from what's in the definition.
      if (aImpl.hasCustomLocationPath())
      {
         getImplState().appendAttribute(STATE_LOCATIONID, String.valueOf(aImpl.getLocationId()));
         getImplState().appendLocationPathAttribute(aImpl.getLocationPath());
      }
      else if (isForPersistence() && aImpl.hasLocationId())
      {
         getImplState().appendAttribute(STATE_LOCATIONID, String.valueOf(aImpl.getLocationId()));
      }
      else
      {
         getImplState().appendLocationPathAttribute(aImpl.getLocationPath());
      }

      // Append nested elements for fault and nested implementation objects.
      getImplState().pushParent();

      try
      {
         // Persistence optimization: if this activity has child activities, and
         // we can restore the child activities' states from the parent's state,
         // then don't traverse the children.
         if (canSkipChildTraversal(aImpl))
         {
            // Indicate that we're skipping the children.
            getImplState().appendAttribute(STATE_SKIPCHILDREN, "true"); //$NON-NLS-1$
         }
         else
         {
            // if we can't skip our children, then it's possible that we have a 
            // fault.
            IAeFault fault = aImpl.getFault();
            if (fault != null)
            {
               appendFault(fault);
            }
            
            // save extensions state if the extension supports state saving 
            Collection extensions = aImpl.getExtensions();
            if (AeUtil.notNullOrEmpty(extensions))
            {
               appendExtensionState(extensions);
            }

            // If the scope is compensating, then we want all of its children 
            // saved since there could be another scope nested within this one 
            // that is also compensating.
            boolean savedSkipFlag = isSkipEnabled();
            if (aImpl instanceof AeActivityScopeImpl && ((AeActivityScopeImpl)aImpl).isCompensating())
            {
               // TODO (MF) Revisit to trim the state to only include data for the scope's compensation handler and its descendants
               setSkipEnabled(false);
            }

            super.visitBase(aImpl);

            setSkipEnabled(savedSkipFlag);
         }
      }
      finally
      {
         getImplState().popParent();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityCompensateImpl)
    */
   public void visit(AeActivityCompensateImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      int nextIndex = aImpl.getNextIndex();
      getImplState().appendAttribute(STATE_NEXTINDEX, String.valueOf(nextIndex));
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityReceiveImpl)
    */
   public void visit(AeActivityReceiveImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      boolean queued = aImpl.isQueued();
      getImplState().appendAttribute(STATE_QUEUED, String.valueOf(queued));
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl)
    */
   public void visit(AeActivityInvokeImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      getImplState().appendAttribute(STATE_QUEUED, String.valueOf(aImpl.isQueued()));
      getImplState().appendAttribute( STATE_ALARM_ID, String.valueOf( aImpl.getAlarmId() ) );
      // if no retries have been attempted don't write out attribute as 0 is 
      // default
      if(aImpl.getRetries() > 0)
      {
         getImplState().appendAttribute(STATE_RETRIES, String.valueOf(aImpl.getRetries()));
      }
      // save durable invoke transmission id information.
      getImplState().appendAttribute(STATE_TRANSMISSION_ID, String.valueOf( aImpl.getTransmissionId() ));      
      // save engine id.
      getImplState().appendAttribute(STATE_ENGINE_ID, String.valueOf( aImpl.getEngineId() ));

      // Save pending invoke journal entry id.
      getImplState().appendAttribute(STATE_JOURNAL_ID, String.valueOf(aImpl.getJournalId()));
   }
   
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityChildExtensionActivityImpl)
    */
   public void visit(AeActivityChildExtensionActivityImpl aImpl) throws AeBusinessProcessException 
   {
      if (aImpl.getLifeCycleAdapter() != null)
      {
         super.visit(aImpl);
         
         // save durable invoke transmission id information.
         getImplState().appendAttribute(STATE_TRANSMISSION_ID, String.valueOf( aImpl.getTransmissionId() ));      

         getImplState().pushParent();
         IAeActivityLifeCycleAdapter adapter = aImpl.getLifeCycleAdapter();
         Element elem = AeXmlUtil.createElement(IAeActivityLifeCycleAdapter.EXTENSION_STATE_NAMESPACE, IAeActivityLifeCycleAdapter.EXTENSION_NAMESPACE_PREFIX, IAeActivityLifeCycleAdapter.EXTENSION_STATE_ELEMENT); 
         adapter.save(elem);
         getImplState().appendForeignNode(elem);
         getImplState().popParent();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeAbstractImplVisitor#visitScope(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   protected void visitScope(AeActivityScopeImpl aImpl) throws AeBusinessProcessException
   {
      super.visitScope(aImpl);

      // Add additional scope-level attributes.
      boolean hasImplicitCompensationHandler = aImpl.hasImplicitCompensationHandler();
      boolean hasImplicitTerminationHandler  = aImpl.hasImplicitTerminationHandler();
      boolean hasImplicitFaultHandler        = aImpl.getFaultHandler() instanceof AeImplicitFaultHandler;
      boolean normalCompletion               = aImpl.isNormalCompletion();
      boolean hasCoordinations               = aImpl.hasCoordinations();
      boolean snapshotRecorded               = aImpl.isSnapshotRecorded();

      getImplState().appendAttributes(
         new String[]
         {
            STATE_HASIMPLICITCOMPENSATIONHANDLER, String.valueOf(hasImplicitCompensationHandler),
            STATE_HASIMPLICITTERMINATIONHANDLER,  String.valueOf(hasImplicitTerminationHandler),
            STATE_HASIMPLICITFAULTHANDLER       , String.valueOf(hasImplicitFaultHandler),
            STATE_HASCOORDINATIONS              , String.valueOf(hasCoordinations),
            STATE_NORMALCOMPLETION              , String.valueOf(normalCompletion),
            STATE_SNAPSHOTRECORDED              , String.valueOf(snapshotRecorded)
         }
      );

      IAeBpelObject faultHandler = aImpl.getFaultHandler();
      if (faultHandler != null)
      {
         String faultHandlerPath = faultHandler.getLocationPath();
         getImplState().appendAttribute(STATE_FAULTHANDLERPATH, faultHandlerPath);
      }

      getImplState().pushParent();

      try
      {
         // Add this scope's variables and correlation sets.
         AeScopeSnapshot snapshot = new AeScopeSnapshot();
         aImpl.recordScopeSnapshot(snapshot);
         appendScopeSnapshot(snapshot);

         // Save compensation info.
         if (aImpl.hasCompInfo())
         {
            appendCompInfo(aImpl.getCompInfo());
         }
      }
      finally
      {
         getImplState().popParent();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityOnEventScopeImpl)
    */
   public void visit(AeActivityOnEventScopeImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      
      if (aImpl.getMessageContext() != null)
      {
         // There is already code for serializing and deserializing an inbound 
         // receive. I have chosen to leverage that code for saving the state of 
         // the onEvent's message. We only need the context and data so I'll 
         // pass nulls for the other params to the ctor.
         getImplState().pushParent();
         try
         {
            AeInboundReceive inboundReceiveWrapper = new AeInboundReceive(null, null, aImpl.getProcess().getProcessPlan(), aImpl.getMessageData(), aImpl.getMessageContext());
            appendInboundReceive(inboundReceiveWrapper, STATE_ONEVENT_MESSAGE);
         }
         finally
         {
            getImplState().popParent();
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityWaitImpl)
    */
   public void visit(AeActivityWaitImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      boolean queued = aImpl.isQueued();
      getImplState().appendAttribute(STATE_QUEUED, String.valueOf(queued));
      getImplState().appendAttribute( STATE_ALARM_ID, String.valueOf( aImpl.getAlarmId() ) );
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityWhileImpl)
    */
   public void visit(AeActivityWhileImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      boolean queued = aImpl.isQueued();
      getImplState().appendAttribute(STATE_QUEUED, String.valueOf(queued));
      getImplState().appendAttribute( STATE_ALARM_ID, String.valueOf( aImpl.getAlarmId() ) );
      saveLoopControlFlag(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityRepeatUntilImpl)
    */
   public void visit(AeActivityRepeatUntilImpl aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      boolean queued = aImpl.isQueued();
      getImplState().appendAttribute(STATE_QUEUED, String.valueOf(queued));
      getImplState().appendAttribute( STATE_ALARM_ID, String.valueOf( aImpl.getAlarmId() ) );
      boolean firstIter = aImpl.isFirstIteration();
      getImplState().appendAttribute(STATE_FIRST_ITER, String.valueOf(firstIter));

      saveLoopControlFlag(aImpl);
   }

   /**
    * Saves the loop control flag which records whether the activity is 
    * currently terminating its children due to a continue or break activity 
    * having executed.
    * @param aImpl
    */
   protected void saveLoopControlFlag(AeLoopActivity aImpl)
   {
      int reason = aImpl.getEarlyTerminationReason();
      getImplState().appendAttribute(STATE_LOOP_TERMINATION_REASON, String.valueOf(reason));
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl)
    */
   public void visit(AeActivityForEachImpl aImpl)
         throws AeBusinessProcessException
   {
      super.visit(aImpl);

      saveForEachAttributes(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityForEachParallelImpl)
    */
   public void visit(AeActivityForEachParallelImpl aImpl)
         throws AeBusinessProcessException
   {
      visit((AeActivityForEachImpl)aImpl);

      int instanceValue = aImpl.getInstanceValue();
      getImplState().appendAttribute(STATE_INSTANCE_VALUE, String.valueOf(instanceValue));

      saveCompensatableScopes(aImpl);
   }

   /**
    * Saves the compensatable scopes for a dynamic scope parent
    * @param aImpl
    * @throws AeBusinessProcessException
    */
   protected void saveCompensatableScopes(IAeDynamicScopeParent aImpl) throws AeBusinessProcessException
   {
      // check the forEach to see if it has any compensatable scopes that need 
      // to be stored. compensatable scopes are scopes that were executed from 
      // a previous iteration of the forEach activity. This accounts for a 
      // forEach activity that is nested within a while loop.
      Collection compensatableScopes = aImpl.getCompensatableChildren();
      if (!AeUtil.isNullOrEmpty(compensatableScopes))
      {
         // push the impl again and visit its compensatableScopes
         getImplState().pushParent();

         try
         {
            // we're only interested in saving the state of scopes that are 
            // currently compensating. This is determined by checking the 
            // scope's compensation handler.
            for (Iterator iter = compensatableScopes.iterator(); iter.hasNext();)
            {
               AeActivityScopeImpl scope = (AeActivityScopeImpl) iter.next();
               if (scope.isCompensating())
               {
                  scope.accept(this);
                  getImplState().appendAttribute(STATE_SCOPE_COMPENSATING, "true"); //$NON-NLS-1$ 
               }
            }
         }
         finally
         {
            getImplState().popParent();
         }
      }
   }

   /**
    * Saves the common attributes used for serial and parallel forEach
    * @param aImpl
    */
   protected void saveForEachAttributes(AeActivityForEachImpl aImpl)
   {
      int counterValue = aImpl.getCounterValue();
      getImplState().appendAttribute(STATE_FOREACH_COUNTER, String.valueOf(counterValue));

      int startValue = aImpl.getStartValue();
      getImplState().appendAttribute(STATE_FOREACH_START, String.valueOf(startValue));

      int finalValue = aImpl.getFinalValue();
      getImplState().appendAttribute(STATE_FOREACH_FINAL, String.valueOf(finalValue));

      int completionCondition = aImpl.getCompletionCondition();
      getImplState().appendAttribute(STATE_FOREACH_COMPLETIONCONDITION, String.valueOf(completionCondition));

      int completionCount = aImpl.getCompletionCount();
      getImplState().appendAttribute(STATE_FOREACH_COMPLETIONCOUNT, String.valueOf(completionCount));

      saveLoopControlFlag(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.AeBusinessProcess)
    */
   public void visit(AeBusinessProcess aImpl) throws AeBusinessProcessException
   {
      // Handle the process as a scope first.
      visit((AeActivityScopeImpl) aImpl);

      // Add additional process-level attributes.
      Date endDate = aImpl.getEndDate();
      Date startDate = aImpl.getStartDate();

      getImplState().appendAttributes(
         new String[]
         {
            STATE_PROC               , aImpl.getName().toString(),
            STATE_PID                , String.valueOf(aImpl.getProcessId()),
            STATE_PROCESSSTATE       , String.valueOf(aImpl.getProcessState()),
            STATE_PROCESSSTATEREASON , String.valueOf(aImpl.getProcessStateReason()),
            STATE_PROCESSINITIATOR   , aImpl.getProcessInitiator(),
            STATE_ENDDATE            , toString(endDate),
            STATE_ENDDATEMILLIS      , toMillisString(endDate),
            STATE_STARTDATE          , toString(startDate),
            STATE_STARTDATEMILLIS    , toMillisString(startDate),
            STATE_MAXLOCATIONID      , String.valueOf(aImpl.getMaxLocationId()),
            STATE_COORDINATOR        , String.valueOf(aImpl.isCoordinator() ),
            STATE_PARTICIPANT        , String.valueOf(aImpl.isParticipant() ),
            STATE_NEXTVARIABLEID     , String.valueOf(aImpl.getNextVersionNumber()),
            STATE_EXITING            , String.valueOf(aImpl.isExiting()),
            STATE_DOC_VERSION        , IAeImplStateNames.STATE_DOC_CURRENT,
            STATE_INVOKE_ID          , String.valueOf(aImpl.getInvokeId()) ,
            STATE_ALARM_ID           , String.valueOf(aImpl.getAlarmId() ),
         }
      );

      getImplState().pushParent();

      try
      {
         appendCreateMessage(aImpl);
         appendExecutionQueue(aImpl);
         appendVariableLocker(aImpl);

         // Add the business process properties
         //
         appendBusinessProcessProperties(aImpl);

         // Add the faulting activity location paths
         //
         appendFaultingActivityLocationPaths(aImpl);

         // Add the open message activity list
         appendOpenMessageActivityInfo(aImpl);
      }
      finally
      {
         getImplState().popParent();
      }
   }

   /**
    * Append a business process property element node for each extension 
    * property.
    *
    * @param aImpl
    */
   protected void appendBusinessProcessProperties( AeBusinessProcess aImpl )
   {
      for (Iterator i = aImpl.getBusinessProcessPropertiesMap().entrySet().iterator(); i.hasNext(); )
      {
         Map.Entry entry = (Map.Entry) i.next();
         Object name = entry.getKey();
         Object value = entry.getValue();

         if ((name instanceof String) && (value instanceof String))
         {
            AeFastElement element = AeBusinessProcessPropertyIO.getBusinessProcessPropertyElement((String) name, (String) value);
            getImplState().appendElement(element);
         }
      }
   }

   /**
    * Append a faultingActivity element node for each faulting activity.
    *
    * @param aImpl
    */
   protected void appendFaultingActivityLocationPaths( AeBusinessProcess aImpl )
   {
      for( Iterator iter = aImpl.getFaultingActivityLocationPaths().iterator(); iter.hasNext(); )
      {
         String locationPath = (String)iter.next();
         AeFastElement faultingElement = new AeFastElement( STATE_FAULTING_ACTIVITY );
         faultingElement.setAttribute( STATE_LOC, locationPath );
         getImplState().appendElement( faultingElement );
      }
   }

   /**
    * Saves base handler information.
    *
    * @param aImpl
    * @throws AeBusinessProcessException
    */
   protected void saveFCTHandler(AeFCTHandler aImpl) throws AeBusinessProcessException
   {
      boolean hasCoordCompensator  = aImpl.hasCoordinatedCompensator();
      getImplState().appendAttribute(STATE_HASCOORDCOMPENSATOR, String.valueOf(hasCoordCompensator) );
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler)
    */
   public void visit(AeCompensationHandler aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      // Save compensation callback.
      IAeCompensationCallback callback = aImpl.getCallback();
      if (callback != null)
      {
         String callbackLocationPath = callback.getLocationPath();
         getImplState().appendAttribute(STATE_CALLBACK, callbackLocationPath);
         getImplState().appendAttribute(STATE_CALLBACK_COORDINATED, String.valueOf(callback.isCoordinated()) );
         getImplState().appendAttribute(STATE_CALLBACK_COORD_ID,  String.valueOf(callback.getCoordinationId()) );
      }

      // Save base handler information
      saveFCTHandler( (AeFCTHandler) aImpl);

      getImplState().pushParent();

      try
      {
         // Save compensation info.
         AeCompInfo compInfo = aImpl.getCompInfo();
         if (compInfo != null)
         {
            appendCompInfo(compInfo);
         }
      }
      finally
      {
         getImplState().popParent();
      }
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
      super.visit(aImpl);

      Boolean rawStatus = aImpl.getRawStatus();
      String status = (rawStatus != null) ? String.valueOf(rawStatus) : STATE_UNKNOWN;
      getImplState().appendAttribute(STATE_EVAL, status);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm)
    */
   public void visit(AeOnAlarm aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      getImplState().appendAttribute( STATE_ALARM_ID, String.valueOf( aImpl.getAlarmId() ) );
      // Add additional attributes.
      appendBaseEventAttributes(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeOnMessage)
    */
   public void visit(AeOnMessage aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);

      // Add additional attributes.
      appendBaseEventAttributes(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeOnEvent)
    */
   public void visit(AeOnEvent aImpl) throws AeBusinessProcessException
   {
      visit((AeOnMessage)aImpl);

      int instanceValue = aImpl.getInstanceValue();
      getImplState().appendAttribute(STATE_INSTANCE_VALUE, String.valueOf(instanceValue));
      getImplState().appendAttribute(STATE_INSTANCE_COUNT, String.valueOf(aImpl.getChildren().size()));

      saveCompensatableScopes(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeRepeatableOnAlarm)
    */
   public void visit(AeRepeatableOnAlarm aImpl) throws AeBusinessProcessException
   {
      visit((AeOnAlarm)aImpl);

      int instanceValue = aImpl.getInstanceValue();
      getImplState().appendAttribute(STATE_INSTANCE_VALUE, String.valueOf(instanceValue));
      getImplState().appendAttribute(STATE_INSTANCE_COUNT, String.valueOf(aImpl.getChildren().size()));
      getImplState().appendAttribute(STATE_INTERVAL, aImpl.getRepeatEveryDuration().toString());

      saveCompensatableScopes(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCoordinationContainer)
    */
   public void visit(AeCoordinationContainer aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      // save list of active coordination ids and state.
      Set set = aImpl.getRegisteredCoordinations();
      // save count.
      getImplState().appendAttribute(STATE_COORDINATION_COUNT, String.valueOf(set.size()));
      // save the list of ids.
      Iterator it = set.iterator();
      int index = 0;
      while (it.hasNext())
      {
         String id = (String) it.next();
         String state = aImpl.getState(id);
         String idName = STATE_COORDINATION_ID + String.valueOf(index);
         String stateName = STATE_COORD_STATE + String.valueOf(index);
         getImplState().appendAttribute(idName, id);
         getImplState().appendAttribute(stateName, state);
         index++;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCoordinatorCompensationHandler)
    */
   public void visit(AeCoordinatorCompensationHandler aImpl) throws AeBusinessProcessException
   {
      visit((AeCompensationHandler) aImpl);
      // save coordination info.
      String coordinationId = aImpl.getCoordinationId();
      getImplState().appendAttribute(STATE_COORDINATION_ID, coordinationId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeDefaultFaultHandler)
    */
   public void visit(AeDefaultFaultHandler aImpl) throws AeBusinessProcessException
   {
      super.visit(aImpl);
      // Save base handler information
      saveFCTHandler( (AeFCTHandler) aImpl);
      if (aImpl.isExecuting() && aImpl.getHandledFault() != null)
      {
         getImplState().pushParent();

         // Create and populate the 'handledFault' wrapper element.
         getImplState().appendElement(STATE_HANDLED_FAULT);
         getImplState().pushParent();
         appendFault(aImpl.getHandledFault());
         getImplState().popParent();

         getImplState().popParent();
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
      if ( aImpl.hasFaultVariable() )
      {
         getImplState().pushParent();
         try
         {
         appendVariable(aImpl.getFaultVariable());
      }
         finally
         {
            getImplState().popParent();
   }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeImplicitFaultHandler)
    */
   public void visit(AeImplicitFaultHandler aImpl) throws AeBusinessProcessException
   {
      visit((AeDefaultFaultHandler) aImpl);
   }

   /**
    * @return Returns the skipEnabled.
    */
   public boolean isSkipEnabled()
   {
      return mSkipEnabled;
   }

   /**
    * @param aSkipEnabled The skipEnabled to set.
    */
   public void setSkipEnabled(boolean aSkipEnabled)
   {
      mSkipEnabled = aSkipEnabled;
   }
}
