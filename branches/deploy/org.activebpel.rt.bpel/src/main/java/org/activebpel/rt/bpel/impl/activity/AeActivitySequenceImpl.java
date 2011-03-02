// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivitySequenceImpl.java,v 1.15 2007/11/21 03:22:17 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import java.util.ArrayList;
import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Implementation of the bpel sequence activity.
 */
public class AeActivitySequenceImpl extends AeActivityImpl implements IAeActivityParent
{
   /** activities to execute in sequence */
   private ArrayList mActivities = new ArrayList();
   
   /** constructor for sequence activity */
   public AeActivitySequenceImpl(AeActivitySequenceDef aActivityDef, IAeActivityParent aParent)
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
   
   /** Adds an activity definition to the list of activities to execute */
   public void addActivity(IAeActivity aActivity)
   {
      mActivities.add(aActivity);
   }

   /** returns an iterator of activity objects to be executed in sequence */
   public Iterator getChildrenForStateChange()
   {
      return mActivities.iterator();
   }
   
   /**
    * Kicks off the sequence running, we'll queue the first eligible child to
    * execute
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      queueNextChild();
   }

   /**
    * Queues the next child to execute or completes if all of the child objects
    * have either executed or reached dead path
    */
   private void queueNextChild() throws AeBusinessProcessException
   {
      // Queues the first activity to execute
      IAeBpelObject child = getNextObject();
      if (child != null)
      {
         getProcess().queueObjectToExecute(child);
      }
      else
      {
         objectCompleted();
      }
   }

   /**
    * The completed child is either FINISHED or DEAD_PATH. We don't care which 
    * case it is, as long as the child complete is the one that we queued to execute.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
    */
   public void childComplete(IAeBpelObject aChild) throws AeBusinessProcessException
   {
      queueNextChild();
   }
   
   /**
    * Walks the child array looking for the first non-final object to execute
    */
   protected IAeBpelObject getNextObject()
   {
      for (int i=0; i<mActivities.size(); i++)
      {
         IAeBpelObject nextObject = (IAeBpelObject)mActivities.get(i);
         if ( ! nextObject.getState().isFinal())
         {
            return nextObject;
         }
      }
      return null;
   }
}
