//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/AeAbstractImplVisitor.java,v 1.4 2007/11/27 02:49:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.activity.AeActivityAssignImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityBreakImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityChildExtensionActivityImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityCompensateImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityCompensateScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityContinueImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityEmptyImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityFlowImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityForEachParallelImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityIfImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityOnEventScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityPickImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityReceiveImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityRepeatUntilImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityReplyImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityRethrowImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivitySequenceImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivitySuspendImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityTerminateImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityThrowImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityValidateImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityWaitImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityWhileImpl;
import org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeCoordinationContainer;
import org.activebpel.rt.bpel.impl.activity.support.AeCoordinatorCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeDefaultFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeElse;
import org.activebpel.rt.bpel.impl.activity.support.AeElseIf;
import org.activebpel.rt.bpel.impl.activity.support.AeEventHandlersContainer;
import org.activebpel.rt.bpel.impl.activity.support.AeFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeImplicitCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeImplicitFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeImplicitTerminationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeLink;
import org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeOnEvent;
import org.activebpel.rt.bpel.impl.activity.support.AeOnMessage;
import org.activebpel.rt.bpel.impl.activity.support.AeRepeatableOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeTerminationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeWSBPELFaultHandler;

/**
 *  Base class for impl visitors
 */
public abstract class AeAbstractImplVisitor implements IAeImplVisitor
{
   /**
    * No-op here
    * @param aObject
    */
   protected void visitBase(AeAbstractBpelObject aObject) throws AeBusinessProcessException
   {
   }
   
   /**
    * Calls visitBase
    * @param aImpl
    * @throws AeBusinessProcessException
    */
   protected void visitScope(AeActivityScopeImpl aImpl) throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityAssignImpl)
    */
   public void visit(AeActivityAssignImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityCompensateImpl)
    */
   public void visit(AeActivityCompensateImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityCompensateScopeImpl)
    */
   public void visit(AeActivityCompensateScopeImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityEmptyImpl)
    */
   public void visit(AeActivityEmptyImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityRethrowImpl)
    */
   public void visit(AeActivityRethrowImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityFlowImpl)
    */
   public void visit(AeActivityFlowImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl)
    */
   public void visit(AeActivityInvokeImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityPickImpl)
    */
   public void visit(AeActivityPickImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityReceiveImpl)
    */
   public void visit(AeActivityReceiveImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityReplyImpl)
    */
   public void visit(AeActivityReplyImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivitySuspendImpl)
    */
   public void visit(AeActivitySuspendImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl)
    */
   public void visit(AeActivityScopeImpl aImpl)
         throws AeBusinessProcessException
   {
      visitScope(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityOnEventScopeImpl)
    */
   public void visit(AeActivityOnEventScopeImpl aImpl)
         throws AeBusinessProcessException
   {
      visitScope(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.AeBusinessProcess)
    */
   public void visit(AeBusinessProcess aImpl) throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivitySequenceImpl)
    */
   public void visit(AeActivitySequenceImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityTerminateImpl)
    */
   public void visit(AeActivityTerminateImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityThrowImpl)
    */
   public void visit(AeActivityThrowImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityValidateImpl)
    */
   public void visit(AeActivityValidateImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityWaitImpl)
    */
   public void visit(AeActivityWaitImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl)
    */
   public void visit(AeActivityForEachImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityForEachParallelImpl)
    */
   public void visit(AeActivityForEachParallelImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityWhileImpl)
    */
   public void visit(AeActivityWhileImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityRepeatUntilImpl)
    */
   public void visit(AeActivityRepeatUntilImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityContinueImpl)
    */
   public void visit(AeActivityContinueImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityBreakImpl)
    */
   public void visit(AeActivityBreakImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm)
    */
   public void visit(AeOnAlarm aImpl) throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeRepeatableOnAlarm)
    */
   public void visit(AeRepeatableOnAlarm aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeOnMessage)
    */
   public void visit(AeOnMessage aImpl) throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeOnEvent)
    */
   public void visit(AeOnEvent aImpl) throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler)
    */
   public void visit(AeCompensationHandler aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeImplicitCompensationHandler)
    */
   public void visit(AeImplicitCompensationHandler aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeImplicitTerminationHandler)
    */
   public void visit(AeImplicitTerminationHandler aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeTerminationHandler)
    */
   public void visit(AeTerminationHandler aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeDefaultFaultHandler)
    */
   public void visit(AeDefaultFaultHandler aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeFaultHandler)
    */
   public void visit(AeFaultHandler aImpl) throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeWSBPELFaultHandler)
    */
   public void visit(AeWSBPELFaultHandler aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeImplicitFaultHandler)
    */
   public void visit(AeImplicitFaultHandler aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeEventHandlersContainer)
    */
   public void visit(AeEventHandlersContainer aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeLink)
    */
   public void visit(AeLink aImpl) throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCoordinationContainer)
    */
   public void visit(AeCoordinationContainer aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeCoordinatorCompensationHandler)
    */
   public void visit(AeCoordinatorCompensationHandler aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityIfImpl)
    */
   public void visit(AeActivityIfImpl aImpl) throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeElseIf)
    */
   public void visit(AeElseIf aImpl) throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.support.AeElse)
    */
   public void visit(AeElse aImpl) throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor#visit(org.activebpel.rt.bpel.impl.activity.AeActivityChildExtensionActivityImpl)
    */
   public void visit(AeActivityChildExtensionActivityImpl aImpl)
         throws AeBusinessProcessException
   {
      visitBase(aImpl);
   }
}
 