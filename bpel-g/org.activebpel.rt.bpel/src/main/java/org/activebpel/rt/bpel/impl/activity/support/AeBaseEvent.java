// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeBaseEvent.java,v 1.7 2006/12/14 23:00:27 mford Exp $
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
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.IAeEventParent;

/**
 * Base class for message and alarm events. 
 */
abstract public class AeBaseEvent extends AeActivityParent
{
   /** True if we have queued our receiver. */
   private boolean mQueued = false;
   
   /**
    * @param aDef
    * @param aParent
    */
   public AeBaseEvent(AeBaseDef aDef, IAeBpelObject aParent)
   {
      super(aDef, aParent);
   }

   /**
    * Overrides the base class by allowing the execute state to transition
    * to dead path and during that state transition we will dequeue our
    * receiver if queued.
    * @see org.activebpel.rt.bpel.impl.AeAbstractBpelObject#validateStateChange(org.activebpel.rt.bpel.impl.AeBpelState)
    */
   protected void validateStateChange(AeBpelState aNewState) throws AeBusinessProcessException
   {
      // always dequeue queued entries on a state change (terminate, deadpath, etc).
      if(isQueued())
      {
         dequeue();
         setQueued(false);
      }

      // Allow change from executing to dead path otherwise verify via base class
      if(! (AeBpelState.EXECUTING.equals(getState()) &&
            AeBpelState.DEAD_PATH.equals(aNewState)))
      {
         super.validateStateChange(aNewState);
      }
   }
      
   /**
    * This is an abstract method to dequeue our receiver if it has been queued 
    * derived classes should implement the actual dequeue code.
    * @throws AeBusinessProcessException
    */
   abstract protected void dequeue() throws AeBusinessProcessException;
   
   /**
    * Sets the event to active, notifies parent then queues the child activity
    * to execute. 
    * @throws AeBusinessProcessException
    */
   protected void executeChild() throws AeBusinessProcessException
   {
      // make sure that no race condition causes a timer or message to be
      // dispatched to us after we have been canceled
      ((IAeEventParent)getParent()).childActive(this);
      getProcess().queueObjectToExecute(mChild);
   }
   
   /**
    * Sets the event to not active and calls that we are complete.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childComplete(IAeBpelObject aChild)
      throws AeBusinessProcessException
   {
      // when our child is complete we are in turn completed
      objectCompleted();
   }

   /**
    * Returns true if at least one child activity is still active. This accounts for the possibility
    * that the event may have multiple children as is the case with ws-bpel 2.0's onEvent and onAlarm.
    */
   public boolean isActive()
   {
      for (Iterator iter = getChildrenForStateChange(); iter.hasNext();)
      {
         IAeBpelObject obj = (IAeBpelObject) iter.next();
         if (obj.getState() != AeBpelState.INACTIVE && !obj.getState().isFinal())
         {
            return true;
         }
      }
      return false;
   }
   
   /**
    * @return boolean True if the receiver has been queued.
    */
   public boolean isQueued()
   {
      return mQueued;
   }

   /**
    * @param aQueued Sets that we have a receiver queued if true.
    */
   public void setQueued(boolean aQueued)
   {
      mQueued = aQueued;
   }

}
