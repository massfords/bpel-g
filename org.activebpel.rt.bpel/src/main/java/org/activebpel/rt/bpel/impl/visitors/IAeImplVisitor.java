// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/visitors/IAeImplVisitor.java,v 1.17 2007/10/24 16:23:45 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.visitors;

import org.activebpel.rt.bpel.AeBusinessProcessException;
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
 * Visitor interface specification for BPEL Implementation classes.
 */
public interface IAeImplVisitor
{
   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityAssignImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityCompensateImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityCompensateScopeImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityEmptyImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityRethrowImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityFlowImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityInvokeImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityPickImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityReceiveImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityReplyImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivitySuspendImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityScopeImpl aImpl) throws AeBusinessProcessException;
   
   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityOnEventScopeImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeBusinessProcess aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivitySequenceImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityTerminateImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityThrowImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityValidateImpl aImpl) throws AeBusinessProcessException;
   
   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityWaitImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityForEachImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityForEachParallelImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityWhileImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityRepeatUntilImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityContinueImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    * @throws AeBusinessProcessException
    */
   public void visit(AeActivityBreakImpl aImpl) throws AeBusinessProcessException;

   /////////  Activity Support

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeOnAlarm aImpl) throws AeBusinessProcessException;
   
   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeRepeatableOnAlarm aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeOnMessage aImpl) throws AeBusinessProcessException;
   
   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeOnEvent aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeCompensationHandler aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeImplicitCompensationHandler aImpl) throws AeBusinessProcessException;
   
   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeImplicitTerminationHandler aImpl) throws AeBusinessProcessException;
   
   /**
    * Visits the specified implmentation object
    * @param aImpl
    */
   public void visit(AeTerminationHandler aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeDefaultFaultHandler aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeFaultHandler aImpl) throws AeBusinessProcessException;
   
   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeWSBPELFaultHandler aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeImplicitFaultHandler aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeEventHandlersContainer aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeLink aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeCoordinationContainer aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeCoordinatorCompensationHandler aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityIfImpl aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeElseIf aImpl) throws AeBusinessProcessException;

   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeElse aImpl) throws AeBusinessProcessException;
   
   /**
    * Visits the specified implementation object
    * @param aImpl
    */
   public void visit(AeActivityChildExtensionActivityImpl aImpl) throws AeBusinessProcessException;
}
