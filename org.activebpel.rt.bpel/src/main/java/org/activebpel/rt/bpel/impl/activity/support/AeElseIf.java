// $Header$
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity.support;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.impl.activity.AeActivityIfImpl;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

/**
 * Implements the 'elseif' child of the 'if' activity.
 */
public class AeElseIf extends AeElse
{
   /**
    * Constructs the else if object with the given def and parent.
    * 
    * @param aDef
    * @param aParent
    */
   public AeElseIf(AeElseIfDef aDef, AeActivityIfImpl aParent)
   {
      super(aDef, aParent);
   }

   /**
    * Returns true if our expression evaluates to true. Always true for otherwise.
    */
   public boolean isEvalTrue() throws AeBusinessProcessException
   {
      AeElseIfDef def = (AeElseIfDef) getDefinition();
      boolean result = executeBooleanExpression(def.getConditionDef());

      // Generate engine info event for debug.
      fireEvalEvent(def, result);

      return result;
   }

   /**
    * Fire the evaluation event.
    * 
    * @param aDef
    * @param aResult
    */
   protected void fireEvalEvent(AeElseIfDef aDef, boolean aResult)
   {
      int eventId = IAeProcessInfoEvent.INFO_ELSE_IF;
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(getProcess().getBPELNamespace()))
         eventId = IAeProcessInfoEvent.INFO_CASE;

      getProcess().getEngine().fireEvaluationEvent(getProcess().getProcessId(),
            aDef.getConditionDef().getExpression(), eventId, getLocationPath(),
            Boolean.toString(aResult));
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }
}
