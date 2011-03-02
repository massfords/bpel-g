//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivitySuspendImpl.java,v 1.6 2007/11/21 03:22:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.AeSuspendReason;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;
import org.activebpel.rt.util.AeUtil;

/**
 * Implementation of the suspend activity. Immediately suspends the process and
 * issues an alert with the optional variable to the alerting system.
 */
public class AeActivitySuspendImpl extends AeActivityImpl
{
   /**
    * Ctor takes the def
    * 
    * @param aDef
    * @param aParent
    */
   public AeActivitySuspendImpl(AeActivitySuspendDef aDef, IAeActivityParent aParent)
   {
      super(aDef, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
    */
   public void accept(IAeImplVisitor aVisitor)
         throws AeBusinessProcessException
   {
      aVisitor.visit(this);
   }
   
   /**
    * Extends base to suspend the process when we are ready to execute.
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#setState(org.activebpel.rt.bpel.impl.AeBpelState)
    */
   public void setState(AeBpelState aNewState)
         throws AeBusinessProcessException
   {
      super.setState(aNewState);
      
      // suspend the process if we are ready to execute
      if(aNewState.equals(AeBpelState.READY_TO_EXECUTE))
      {
         IAeVariable variable = null;
         String varName = getDef().getVariable();
         if (AeUtil.notNullOrEmpty(varName))
         {
            variable = findVariable(varName);
            if (!variable.hasData())
            {
               variable = null;
            }
         }
         
         getProcess().suspend(new AeSuspendReason(AeSuspendReason.SUSPEND_CODE_LOGICAL, getLocationPath(), variable));
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeExecutableQueueItem#execute()
    */
   public void execute() throws AeBusinessProcessException
   {
      super.execute();
      objectCompleted();
   }
   
   /**
    * Getter for the def
    */
   protected AeActivitySuspendDef getDef()
   {
      return (AeActivitySuspendDef) getDefinition();
   }
}
 