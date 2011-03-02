// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeIf.java,v 1.2 2006/11/13 17:06:35 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity.support;

import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.impl.activity.AeActivityIfImpl;

/**
 * An implementation object for the first boolean clause of an if activity.
 */
public class AeIf extends AeElseIf
{
   /**
    * Constructs the if object.
    * 
    * @param aDef
    * @param aParent
    */
   public AeIf(AeIfDef aDef, AeActivityIfImpl aParent)
   {
      super(aDef, aParent);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeElseIf#fireEvalEvent(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef, boolean)
    */
   protected void fireEvalEvent(AeElseIfDef aDef, boolean aResult)
   {
      int eventId = IAeProcessInfoEvent.INFO_IF;
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(getProcess().getBPELNamespace()))
         eventId = IAeProcessInfoEvent.INFO_CASE;

      getProcess().getEngine().fireEvaluationEvent(getProcess().getProcessId(),
            aDef.getConditionDef().getExpression(), eventId, getLocationPath(),
            Boolean.toString(aResult));
   }
}
