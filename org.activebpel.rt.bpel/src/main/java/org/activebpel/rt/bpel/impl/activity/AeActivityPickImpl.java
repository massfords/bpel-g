// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityPickImpl.java,v 1.15 2007/11/21 03:22:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.support.AeBaseEvent;
import org.activebpel.rt.bpel.impl.activity.support.AeEventHandlers;
import org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeOnMessage;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.util.AeUtil;

import java.util.Iterator;

/**
 * Implementation of the bpel pick activity.
 */
public class AeActivityPickImpl extends AeActivityImpl implements IAeEventParent
{
   /**
    * Container for the messages and alarms.
    */
   private AeEventHandlers mEvents = new AeEventHandlers();
   
   /** default constructor for activity */
   public AeActivityPickImpl(AeActivityPickDef aActivityDef, IAeActivityParent aParent)
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
    * @see org.activebpel.rt.bpel.impl.activity.IAeEventParent#addAlarm(org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm)
    */
   public void addAlarm(AeOnAlarm aAlarm)
   {
      mEvents.addAlarm(aAlarm);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.IAeEventParent#addMessage(org.activebpel.rt.bpel.impl.activity.support.AeOnMessage)
    */
   public void addMessage(AeOnMessage aMessage)
   {
      mEvents.addMessage(aMessage);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
    */
   public Iterator getChildrenForStateChange()
   {
      return AeUtil.join(mEvents.getAlarms(), mEvents.getMessages());
   }

   /**
    * Pick execution queues all child event objects to execute.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      for(Iterator iter=getChildrenForStateChange(); iter.hasNext(); )
      {
         IAeBpelObject bpelObject = (IAeBpelObject)iter.next();
         if (!bpelObject.getState().isFinal())
            getProcess().queueObjectToExecute(bpelObject); 
      }
   }

   /**
    * If not child dead path callback then handle by completing ourselves.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childComplete(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      // if this is not a dead path call back then set our execution as complete
      if( ! AeBpelState.DEAD_PATH.equals(aChild.getState()) )
         objectCompleted();
   }

   /**
    * Handles a child becoming active by setting the non-active children to dead paths.
    * @see org.activebpel.rt.bpel.impl.activity.IAeEventParent#childActive(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childActive(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      // just in case throw an exception here
      //
      if(aChild.getState().isFinal())
         throw new AeBusinessProcessException(AeMessages.getString("AeActivityPickImpl.ERROR_0")); //$NON-NLS-1$
         
      // loop through children for non-active children and set to dead path
      for(Iterator iter=getChildrenForStateChange(); iter.hasNext(); )
      {
         IAeBpelObject bpelObject = (IAeBpelObject)iter.next();
         if(bpelObject != aChild)
         {
	        if(bpelObject.getState().isFinal() || ((AeBaseEvent)bpelObject).isActive())
    	        throw new AeBusinessProcessException(AeMessages.getString("AeActivityPickImpl.ERROR_1")); //$NON-NLS-1$
            bpelObject.setState(AeBpelState.DEAD_PATH);
         }
      }
   }
}
