// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Implementation of the bpel repeatUntil activity.
 */
public class AeActivityRepeatUntilImpl extends AeActivityWhileImpl
{
   /** Flag indicating if this is the first iteration. */
   private boolean mFirstIteration = true;

   /**
    * Constructs the repeatUntil impl object.
    *
    * @param aActivityDef
    * @param aParent
    */
   public AeActivityRepeatUntilImpl(AeActivityRepeatUntilDef aActivityDef, IAeActivityParent aParent)
   {
      super(aActivityDef, aParent);
   }


   /**
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityWhileImpl#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      if (mFirstIteration)
      {
         mFirstIteration = false;

         // Queue the activity to execute
         getChild().setState(AeBpelState.INACTIVE);
         getProcess().queueObjectToExecute(getChild());
      }
      else
      {
         initAlarmIterations();

         AeConditionDef conditionDef = ((AeActivityRepeatUntilDef) getDefinition()).getConditionDef();
         boolean isConditionTrue = executeBooleanExpression(conditionDef);

         // Generate engine info event for debug.
         getProcess().getEngine().fireEvaluationEvent(getProcess().getProcessId(),
               conditionDef.getExpression(), IAeProcessInfoEvent.INFO_REPEAT_UNTIL, getLocationPath(),
               Boolean.toString(isConditionTrue));

         if(isConditionTrue)
         {
            // condition is true so we are done
            objectCompleted();
         }
         else
         {
            // queue the activity to execute
            getChild().setState(AeBpelState.INACTIVE);
            getProcess().queueObjectToExecute(getChild());
         }
      }
   }

   /**
    * @return Returns the firstIteration.
    */
   public boolean isFirstIteration()
   {
      return mFirstIteration;
   }

   /**
    * @param aFirstIteration The firstIteration to set.
    */
   public void setFirstIteration(boolean aFirstIteration)
   {
      mFirstIteration = aFirstIteration;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }
}
