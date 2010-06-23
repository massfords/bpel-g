// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeEventHandlersContainer.java,v 1.16 2006/09/22 19:52:38 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.IAeEventParent;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Models the <code>eventHandlers</code> in a <code>scope</code> activity.
 */
public class AeEventHandlersContainer extends AeAbstractBpelObject implements IAeEventParent
{
   /**
    * provides storage for the messages and alarms.
    */
   private AeEventHandlers mEvents = new AeEventHandlers();

   /**
    * @param aDef
    * @param aParent
    */
   public AeEventHandlersContainer(AeEventHandlersDef aDef, IAeBpelObject aParent)
   {
      super(aDef, aParent);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept( IAeImplVisitor aVisitor ) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }
   
   /**
    * Installs event objects with the engine's event and alarm services.
    */
   public void execute() throws AeBusinessProcessException
   {
      // nothing happens in the execute. The scope will have already
      // installed the alarms and messages for the event container.
      // This is done externally to the container's execute to handle
      // the case of process level events. In this case, we want the 
      // event handlers to execute AFTER the create instance activity 
      // but BEFORE any other activities have had a chance to execute.
   }

   /**
    * Disables all of the events (alarms/messages) within this container. This
    * will prevent them from becoming active and allow the scope to complete.
    * If the event is already active, there is no change, the scope will need
    * to wait for the events to complete.
    */
   public void completeEvents() throws AeBusinessProcessException
   {
      if (!getState().isFinal())
      {
         // disable any events which are not in progress
         disableEvents(getChildrenForStateChange());

         // check whether any events are still active
         checkForActiveEvents();
      }
   }

   /**
    * Scans the messages and alarms looking for any active events. If there are none
    * then the parent scope is notified that there are no more events to wait for. 
    */
   private void checkForActiveEvents() throws AeBusinessProcessException
   {
      if (noActiveEvents(getChildrenForStateChange()))
      {
         // if we have no active events then transition to completed state
         setState(AeBpelState.FINISHED);
         
         eventsCompleted();
      }
   }

   /**
    * Notifies the scope that our events have completed.
    * @throws AeBusinessProcessException
    */
   protected void eventsCompleted() throws AeBusinessProcessException
   {
      getScope().eventsCompleted();
   }

   /**
    * Getter for the parent scope
    */
   protected AeActivityScopeImpl getScope()
   {
      return ((AeActivityScopeImpl) getParent());
   }

   /**
    * Returns true if there are NO active events in the iterator.
    * @param aIterator
    * @return true if all of the events are INACTIVE. False if there is at 
    *          least 1 active event.
    */
   private boolean noActiveEvents(Iterator aIterator)
   {
      while(aIterator.hasNext())
      {
         AeBaseEvent event = (AeBaseEvent) aIterator.next();
         if (event.isActive())
         {
            return false;
         }
      }
      return true;
   }
   
   /**
    * Disables all the non-active events in the Iterator by setting them to dead path. 
    * @param aIterator
    */
   private void disableEvents(Iterator aIterator) throws AeBusinessProcessException
   {
      while(aIterator.hasNext())
      {
         AeBaseEvent event = (AeBaseEvent) aIterator.next();
         if (! event.isActive())
         {
            // don't set events state to dead path if it already fired (for example alarm)
            if(! event.getState().isFinal())
            {
               // used to be setting state to DEAD_PATH but since that propagates to its children, the children were 
               // getting an illegal state change from FINISHED to DEAD_PATH
               event.setState(AeBpelState.FINISHED);
            }
         }
         else if (event.isQueued())
         {
            event.dequeue();
         }
      }
   }

   /**
    * Installs the alarms with the engine's timer service
    */
   public void installAlarms() throws AeBusinessProcessException
   {
      for (Iterator it = getEventHandlers().getAlarms(); it.hasNext();)
      {
         AeOnAlarm alarm = (AeOnAlarm) it.next();
         getProcess().queueObjectToExecute(alarm);
      }
   }

   /**
    * Installs the events with the engine's event queue.
    */
   public void installMessages() throws AeBusinessProcessException
   {
      for (Iterator it = getEventHandlers().getMessages(); it.hasNext();)
      {
         AeOnMessage message = (AeOnMessage) it.next();
         getProcess().queueObjectToExecute(message);
      }
   }
   
   /**
    * Adds the alarm to the list.
    * @see org.activebpel.rt.bpel.impl.activity.IAeEventParent#addAlarm(org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm)
    */
   public void addAlarm(AeOnAlarm aAlarm)
   {
      getEventHandlers().addAlarm(aAlarm);
   }

   /**
    * Adds the message to the list.
    * @see org.activebpel.rt.bpel.impl.activity.IAeEventParent#addMessage(org.activebpel.rt.bpel.impl.activity.support.AeOnMessage)
    */
   public void addMessage(AeOnMessage aMessage)
   {
      getEventHandlers().addMessage(aMessage);
   }
   
   /**
    * Getter for the event storage object.
    */
   public AeEventHandlers getEventHandlers()
   {
      return mEvents;
   }
   
   /**
    * Called by the engine when a message or alarm has finished executing.
    * If the scope is waiting for us then we need to walk the events and see 
    * if there are any left to complete. 
    */
   public void childComplete(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      if (aChild.getState() != AeBpelState.DEAD_PATH)
      {
         if (aChild instanceof AeOnMessage)
         {
            getScope().eventCompleted((AeOnMessage) aChild);
         }
         else
         {
            getScope().alarmCompleted((AeOnAlarm) aChild);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return AeUtil.join(
         getEventHandlers().getAlarms(),
         getEventHandlers().getMessages());
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeEventParent#childActive(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childActive(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      // does nothing as events are checked directly when scope finishes      
   }
}
