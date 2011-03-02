// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefToImplVisitor.java,v 1.50.4.1 2008/04/21 16:09:44 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCatchAllDef;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.AeExtensionDef;
import org.activebpel.rt.bpel.def.AeExtensionsDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.bpel.def.AeImportDef;
import org.activebpel.rt.bpel.def.AeMessageExchangeDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.AePartnerDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinksDef;
import org.activebpel.rt.bpel.def.AePartnersDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.def.activity.AeActivityAssignDef;
import org.activebpel.rt.bpel.def.activity.AeActivityBreakDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityContinueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityExitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityFlowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.activity.AeActivityIfDef;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef;
import org.activebpel.rt.bpel.def.activity.AeActivityThrowDef;
import org.activebpel.rt.bpel.def.activity.AeActivityValidateDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef;
import org.activebpel.rt.bpel.def.activity.support.AeForDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef;
import org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinksDef;
import org.activebpel.rt.bpel.def.activity.support.AeLiteralDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.activity.support.AeQueryDef;
import org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeUntilDef;
import org.activebpel.rt.bpel.def.faults.IAeFaultMatchingStrategy;
import org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio.IAeMessageDataStrategyNames;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.AeVariable;
import org.activebpel.rt.bpel.impl.AeVariablesImpl;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeMessageValidator;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.activity.AeActivityAssignImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityBreakImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityCompensateImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityCompensateScopeImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityContinueImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityEmptyImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityFlowImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityForEachImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityForEachParallelImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityIfImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityInvokeImpl;
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
import org.activebpel.rt.bpel.impl.activity.AeActivityWaitImpl;
import org.activebpel.rt.bpel.impl.activity.AeActivityWhileImpl;
import org.activebpel.rt.bpel.impl.activity.IAeCopyFromParent;
import org.activebpel.rt.bpel.impl.activity.IAeEventParent;
import org.activebpel.rt.bpel.impl.activity.IAeMessageConsumerParentAdapter;
import org.activebpel.rt.bpel.impl.activity.IAeMessageProducerParentAdapter;
import org.activebpel.rt.bpel.impl.activity.IAeScopeTerminationStrategy;
import org.activebpel.rt.bpel.impl.activity.IAeVariableContainer;
import org.activebpel.rt.bpel.impl.activity.IAeWSIOActivity;
import org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeFrom;
import org.activebpel.rt.bpel.impl.activity.assign.IAeTo;
import org.activebpel.rt.bpel.impl.activity.assign.from.AeFromStrategyFactory;
import org.activebpel.rt.bpel.impl.activity.assign.to.AeToStrategyFactory;
import org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationsImpl;
import org.activebpel.rt.bpel.impl.activity.support.AeCorrelationsPatternImpl;
import org.activebpel.rt.bpel.impl.activity.support.AeDefaultFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeElse;
import org.activebpel.rt.bpel.impl.activity.support.AeElseIf;
import org.activebpel.rt.bpel.impl.activity.support.AeEventHandlersContainer;
import org.activebpel.rt.bpel.impl.activity.support.AeFaultHandler;
import org.activebpel.rt.bpel.impl.activity.support.AeIMACorrelations;
import org.activebpel.rt.bpel.impl.activity.support.AeIf;
import org.activebpel.rt.bpel.impl.activity.support.AeLink;
import org.activebpel.rt.bpel.impl.activity.support.AeOnAlarm;
import org.activebpel.rt.bpel.impl.activity.support.AeOnMessage;
import org.activebpel.rt.bpel.impl.activity.support.AeTerminationHandler;
import org.activebpel.rt.bpel.impl.activity.support.IAeCorrelations;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.AeFromPartsMessageDataConsumer;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.AeNoopMessageDataConsumer;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.AeVariableMessageDataConsumer;
import org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumer;
import org.activebpel.rt.bpel.impl.activity.wsio.produce.AeEmptyMessageDataProducer;
import org.activebpel.rt.bpel.impl.activity.wsio.produce.AeToPartsMessageDataProducer;
import org.activebpel.rt.bpel.impl.activity.wsio.produce.AeVariableMessageDataProducer;
import org.activebpel.rt.bpel.impl.activity.wsio.produce.IAeMessageDataProducer;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;

/**
 * <p>Visitor responsible for creating implementation objects from their definition
 * objects.</p>
 *
 * <p>A static convenience method called createProcess has been setup to easily
 * create a new process implementation.</p>
 */
public abstract class AeDefToImplVisitor implements IAeDefToImplVisitor
{
   /**
    * Used in conjunction with the traversal object to traverse the object
    * model
    */
   protected IAeDefVisitor mTraversalVisitor;
   /** The process id of the process we are creating an implemntation for. */
   protected long mProcessId;
   /** The engine the created process will run inside of. */
   protected IAeBusinessProcessEngineInternal mEngine;
   /** The process that we're creating implmentations for */
   protected IAeBusinessProcessInternal mProcess;
   /** Stores the stack of objects that we're visiting */
   protected Stack mStack = new Stack();
   /** Plan used to create the business process instance */
   protected IAeProcessPlan mPlan;

   /** collection of variables we've created */
   private Collection mVariables = new ArrayList();
   /** collection of bpel objects we've created */
   private Collection mBpelObjects = new ArrayList();
   /** collection of partner links we've created */
   private Collection mPartnerLinks = new ArrayList();

   /** Strategy for matching faults */
   private IAeFaultMatchingStrategy mFaultMatchingStrategy;
   /** Strategy for terminating a scope */
   private IAeScopeTerminationStrategy mScopeTerminationStrategy;

   /** impl for validating service messages */
   private IAeMessageValidator mMessageValidator;

   /**
    * Main entry point for implementation creation.
    *
    * @param aPid The process Id to associate with the new process
    * @param aEngine The engine the process will run inside of.
    * @param aPlan The plan we are visiting.
    * @return The new process implementation .
    */
   public static IAeBusinessProcess createProcess(long aPid, IAeBusinessProcessEngineInternal aEngine,
         IAeProcessPlan aPlan)
   {
      IAeDefToImplVisitor def2impl = AeDefVisitorFactory.getInstance(aPlan.getProcessDef().getNamespace()).createImplVisitor(aPid, aEngine, aPlan);
      aPlan.getProcessDef().accept(def2impl);
      def2impl.reportObjects();
      return def2impl.getProcess();
   }

   /**
    * Ctor requires the process that you want to visit.
    * @param aPid the process id to create an implmentation for
    */
   protected AeDefToImplVisitor(long aPid, IAeBusinessProcessEngineInternal aEngine, IAeProcessPlan aPlan)
   {
      mProcessId = aPid;
      mEngine = aEngine;
      mPlan = aPlan;
   }

   /**
    * Special constructor for creating dynamic objects from a currently
    * executing process. These objects include the children of a parallel
    * forEach as well as concurrent onEvents/onAlarms within a scope.
    * @param aProcess
    * @param aParent
    */
   protected AeDefToImplVisitor(IAeBusinessProcessInternal aProcess, IAeBpelObject aParent)
   {
      setProcess(aProcess);
      push(aParent);
      mPlan = aProcess.getProcessPlan();
   }

   /**
    * Setter for traversal visitor
    * @param aDefVisitor
    */
   public void setTraversalVisitor(IAeDefVisitor aDefVisitor)
   {
      mTraversalVisitor = aDefVisitor;
   }

   /**
    * Getter for traversal visitor
    */
   public IAeDefVisitor getTraversalVisitor()
   {
      if (mTraversalVisitor == null)
      {
         setTraversalVisitor(createTraverser());
      }
      return mTraversalVisitor;
   }

   /**
    * Creates the def traverser
    */
   protected abstract IAeDefVisitor createTraverser();

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      AeBusinessProcess process = (AeBusinessProcess) mEngine.createProcess(mProcessId, mPlan);
      setProcess(process);
      process.setTerminationStrategy(getScopeTerminationStrategy());
      process.setFaultMatchingStrategy(getFaultMatchingStrategy());
      traverse(mPlan.getProcessDef(), process);
   }

   /**
    * Creates the scope implementation and then traverses it.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      AeActivityScopeImpl impl = new AeActivityScopeImpl(aDef, getActivityParent());
      impl.setTerminationStrategy(getScopeTerminationStrategy());
      impl.setFaultMatchingStrategy(getFaultMatchingStrategy());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Gets the namespace for the version of BPEL that we're processing.
    */
   protected String getBPELNamespace()
   {
      return getProcess().getBPELNamespace();
   }

   /**
    * Pushes the implementation onto the stack so it'll become the parent for
    * any other activities created during the execution of this method. Then the
    * def is visited which may call back into this class with other visit
    * methods. Finally, we pop from the stack to restore the previous parent.
    * @param aDef
    * @param aImpl
    */
   protected void traverse(AeBaseDef aDef, Object aImpl)
   {
      // Add all BPEL objects to our lookup map
      if (aImpl instanceof IAeBpelObject)
         getBpelObjects().add(aImpl);

      // No need to bother with stack if we have no parent object
      if (aImpl == null)
         aDef.accept(getTraversalVisitor());
      else
      {
         push(aImpl);
         aDef.accept(getTraversalVisitor());
         pop();
      }
   }

   /**
    * Pushes the activity onto the stack making it the current activity.
    * @param aObj
    */
   protected void push(Object aObj)
   {
      mStack.push(aObj);
   }

   /**
    * Pops the activity from the stack.
    */
   protected void pop()
   {
      mStack.pop();
   }

   /**
    * Peeks at the current object on the stack.
    */
   protected Object peek()
   {
      return mStack.peek();
   }

   /**
    * Type safe peeker.
    */
   protected IAeActivityParent getActivityParent()
   {
      return (IAeActivityParent) peek();
   }

   /**
    * Type safe peeker.
    */
   protected AeActivityScopeImpl getScope()
   {
      return (AeActivityScopeImpl) peek();
   }

   /**
    * Type safe peeker for variable container
    */
   protected IAeVariableContainer getVariableContainer()
   {
      return (IAeVariableContainer) peek();
   }

   /**
    * Type safe peeker.
    */
   protected IAeCopyFromParent getCopyFromParent()
   {
      return (IAeCopyFromParent) peek();
   }

   /**
    * Type safe peeker.
    */
   protected IAeEventParent getMessageParent()
   {
      return (IAeEventParent) peek();
   }

   /**
    * Type safe peeker.
    */
   protected AeActivityIfImpl getActivityIf()
   {
      return (AeActivityIfImpl) peek();
   }

   /**
    * Type safe peeker.
    */
   protected AeActivityFlowImpl getFlow()
   {
      return (AeActivityFlowImpl) peek();
   }

   /**
    * Type safe peeker.
    */
   protected AeActivityImpl getActivity()
   {
      return (AeActivityImpl) peek();
   }

   /**
    * Type safe peeker.
    */
   protected AeCopyOperation getCopyOperation()
   {
      return (AeCopyOperation) mStack.peek();
   }

   /**
    * Type safe peeker.
    */
   protected AeActivityAssignImpl getAssign()
   {
      return (AeActivityAssignImpl) mStack.peek();
   }

   /**
    * Calls <code>accept</code> on the definition object. No need to create
    * an implementation object for the container here.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
    */
   public void visit(AeCorrelationSetsDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * Create an event handler and add it to the scope. We then traverse in order
    * to pick up any messages or alarms.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeEventHandlersDef)
    */
   public void visit(AeEventHandlersDef aDef)
   {
      AeEventHandlersContainer events = new AeEventHandlersContainer(aDef, getScope());
      getScope().setEventHandlersContainer(events);
      traverse(aDef, events);
   }

   /**
    * Calls <code>accept</code> on the definition object. No need to create
    * an implementation object for the container here. The scope/process
    * currently on the stack will have the AeFaultHandlersDef objects added to
    * it when they get visited.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
    */
   public void visit(AeFaultHandlersDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * Create the implementation object for the compensation handler container
    * and put it on the stack. The next activity visited will be added to the
    * compensation handler container.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void visit(AeCompensationHandlerDef aDef)
   {
      AeCompensationHandler compHandler = new AeCompensationHandler(aDef, getScope());
      getScope().setCompensationHandler(compHandler);
      traverse(aDef, compHandler);
   }

   /**
    * Calls <code>accept</code> on the definition object. No need to create
    * an implementation object for the container here. The scope/process
    * currently on the stack will have the AeVariableDef objects added to it
    * when they get visited.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariablesDef)
    */
   public void visit(AeVariablesDef aDef)
   {
      AeVariablesImpl variablesImpl = new AeVariablesImpl(aDef, getScope());
      getScope().setVariablesImpl(variablesImpl);
      traverse(aDef, variablesImpl);
   }

   /**
    * Creates the assign implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
    */
   public void visit(AeActivityAssignDef aDef)
   {
      AeActivityAssignImpl impl = new AeActivityAssignImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Creates the compensate implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
    */
   public void visit(AeActivityCompensateDef aDef)
   {
      AeActivityCompensateImpl impl = new AeActivityCompensateImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef aDef)
   {
      AeActivityCompensateScopeImpl impl = new AeActivityCompensateScopeImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Creates the empty implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef)
    */
   public void visit(AeActivityEmptyDef aDef)
   {
      IAeActivity impl = new AeActivityEmptyImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Creates the flow implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
    */
   public void visit(AeActivityFlowDef aDef)
   {
      IAeActivity impl = new AeActivityFlowImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Creates the invoke implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      AeActivityInvokeImpl impl = new AeActivityInvokeImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      impl.setMessageValidator(getMessageValidator());

      assignMessageDataProducer(impl, aDef);
      assignMessageDataConsumer(impl, aDef);

      traverse(aDef, impl);
   }

   /**
    * Creates the pick implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
    */
   public void visit(AeActivityPickDef aDef)
   {
      IAeActivity impl = new AeActivityPickImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Creates the receive implmentation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      AeActivityReceiveImpl impl = new AeActivityReceiveImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      impl.setMessageValidator(getMessageValidator());

      assignMessageDataConsumer(impl, aDef);

      traverse(aDef, impl);
   }

   /**
    * Creates the reply implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef aDef)
   {
      AeActivityReplyImpl impl = new AeActivityReplyImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      impl.setMessageValidator(getMessageValidator());

      assignMessageDataProducer(impl, aDef);

      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
    */
   public void visit(AeActivitySuspendDef aDef)
   {
      IAeActivity impl = new AeActivitySuspendImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Creates the sequence implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
    */
   public void visit(AeActivitySequenceDef aDef)
   {
      IAeActivity impl = new AeActivitySequenceImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Creates the terminate implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
    */
   public void visit(AeActivityExitDef aDef)
   {
      IAeActivity impl = new AeActivityTerminateImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Creates the throw implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
    */
   public void visit(AeActivityThrowDef aDef)
   {
      IAeActivity impl = new AeActivityThrowImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Creates the wait implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
      IAeActivity impl = new AeActivityWaitImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
    */
   public void visit(AeActivityForEachDef aDef)
   {
      IAeActivity impl = aDef.isParallel() ?
               new AeActivityForEachParallelImpl(aDef, getActivityParent()) :
                  new AeActivityForEachImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef)
    */
   public void visit(AeForEachFinalDef aDef)
   {
      // no impl for final expression
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef)
    */
   public void visit(AeForEachStartDef aDef)
   {
      // no impl for start expression
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
    */
   public void visit(AeForEachBranchesDef aDef)
   {
      // no impl for branches
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef)
    */
   public void visit(AeForEachCompletionConditionDef aDef)
   {
      // no impl for completion condition
   }

   /**
    * Creates the <code>while</code> implementation and traverses.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef aDef)
   {
      IAeActivity impl = new AeActivityWhileImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
    */
   public void visit(AeActivityRepeatUntilDef aDef)
   {
      IAeActivity impl = new AeActivityRepeatUntilImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
    */
   public void visit(AeActivityContinueDef aDef)
   {
      IAeActivity impl = new AeActivityContinueImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
    */
   public void visit(AeActivityBreakDef aDef)
   {
      IAeActivity impl = new AeActivityBreakImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * Create instance of fault handler implementation and add to the current scope's
    * fault handler container. Then traverse the def object so we'll add the
    * child activity to the fault.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void visit(AeCatchDef aDef)
   {
      AeFaultHandler fh = new AeFaultHandler(aDef, getScope());
      getScope().addFaultHandler(fh);
      traverse(aDef, fh);
   }

   /**
    * Create instance of alarm and add to the current parent which is either
    * a pick or an event handler for a scope.
    *
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      AeOnAlarm alarm = new AeOnAlarm(aDef, getMessageParent());
      getMessageParent().addAlarm(alarm);
      traverse(aDef, alarm);
   }

   /**
    * Create instance of message and add to the current parent which is either
    * a pick or an event handler for a scope.

    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      AeOnMessage msg = new AeOnMessage(aDef, getMessageParent());
      msg.setMessageValidator(getMessageValidator());
      getMessageParent().addMessage(msg);

      assignMessageDataConsumer(msg, aDef);

      traverse(aDef, msg);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      visit((AeOnMessageDef)aDef);
   }

   /**
    * Create instance of variable and add to scope.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
    */
   public void visit(AeVariableDef aDef)
   {
      AeVariable var = new AeVariable(getVariableContainer(), aDef);
      getVariableContainer().addVariable(var);
      getVariables().add(var);
      traverse(aDef, var);
   }

   /**
    * Create instance of <code>catchAll</code> and add to scope.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchAllDef)
    */
   public void visit(AeCatchAllDef aDef)
   {
      AeDefaultFaultHandler catchAll = new AeDefaultFaultHandler(aDef, getScope());
      getScope().setDefaultFaultHandler(catchAll);
      traverse(aDef, catchAll);
   }
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
    */
   public void visit(AeAssignCopyDef aDef)
   {
      AeActivityAssignImpl assign = (AeActivityAssignImpl) getAssign();
      AeCopyOperation copy = new AeCopyOperation(aDef, assign.getCopyOperationContext());
      assign.addCopyOperation(copy);
      traverse(aDef, copy);
   }

   /**
    * No implementation to create or traverse.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
    */
   public void visit(AeCorrelationDef aDef)
   {
      // no-op
   }

   /**
    * Create a link implementation and add it to the current flow. No further
    * traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinkDef)
    */
   public void visit(AeLinkDef aDef)
   {
      AeLink link = new AeLink(aDef, getFlow());
      getFlow().addLink(link);
   }

   /**
    * No implementation or further traversal.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
    */
   public void visit(AePartnerDef aDef)
   {
      // no-op
   }

   /**
    * Create the <code>partnerLink</code> impl and add to the process.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void visit(AePartnerLinkDef aDef)
   {
      AePartnerLink plink = new AePartnerLink(getScope(), aDef);
      getScope().addPartnerLink(plink);
      getPartnerLinks().add(plink);
   }

   /**
    * Nothing to create for scope def, just traverse to visit all of its
    * children.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
    */
   public void visit(AeScopeDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
    */
   public void visit(AeMessageExchangesDef aDef)
   {
      // nothing to create here
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
    */
   public void visit(AeMessageExchangeDef aDef)
   {
      // nothing to create here and no children that need traversing
   }

   /**
    * Find the <code>link</code> that this <code>source</code> references and
    * add it to the <code>AeActivityImpl</code>'s collection of source links.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
    */
   public void visit(AeSourceDef aDef)
   {
      AeLink link = findLink(aDef.getLinkName());
      link.setTransitionConditionDef(aDef.getTransitionConditionDef());
      link.setSourceActivity(getActivity());
      getActivity().addSourceLink(link);
   }

   /**
    * Find the <code>link</code> that this <code>target</code> references and
    * add it to the <code>AeActivityImpl</code>'s collection of target links.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
    */
   public void visit(AeTargetDef aDef)
   {
      AeLink link = findLink(aDef.getLinkName());
      link.setTargetActivity(getActivity());
      getActivity().addTargetLink(link);
   }

   /**
    * Nothing to create directly, instead just visit the container so we'll hit
    * the <code>partnerLink</code>s individually.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinksDef)
    */
   public void visit(AePartnerLinksDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * Nothing to create or traverse here.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
    */
   public void visit(AePartnersDef aDef)
   {
   }

   /**
    * Nothing to create but we need to traverse so we'll hit the links.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinksDef)
    */
   public void visit(AeLinksDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * Creates a correlation impl which handles initiating or validating the
    * correlations used for a wsio activity
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void visit(AeCorrelationsDef aDef)
   {
      IAeWSIOActivity parent = (IAeWSIOActivity) peek();

      if (parent instanceof AeActivityInvokeImpl)
      {
         // invoke uses the pattern attribute for correlations so the sets may
         // be initiated/validated using request or response data
         if (aDef.isRequestPatternUsed())
         {
            IAeCorrelations requestCorrelationsImpl = new AeCorrelationsPatternImpl(aDef, parent, true);
            parent.setRequestCorrelations(requestCorrelationsImpl);
         }
         if (aDef.isResponsePatternUsed())
         {
            IAeCorrelations responseCorrelationsImpl = new AeCorrelationsPatternImpl(aDef, parent, false);
            parent.setResponseCorrelations(responseCorrelationsImpl);
         }
      }
      else if (parent instanceof AeActivityReplyImpl)
      {
         parent.setResponseCorrelations(new AeCorrelationsImpl(aDef, parent));
      }
      else
      {
         AeIMACorrelations correlations = new AeIMACorrelations(aDef, parent);
         correlations.setFilter(getCorrelationsFilter());
         parent.setRequestCorrelations(correlations);
      }
      traverse(aDef, null);
   }

   /**
    * Returns a filter to use for IMA's when they queue and need to test for
    * conflictingReceives
    */
   protected abstract AeIMACorrelations.IAeCorrelationSetFilter getCorrelationsFilter();

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeImportDef)
    */
   public void visit(AeImportDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
    */
   public void visit(AeDocumentationDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
    */
   public void visit(AeJoinConditionDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
    */
   public void visit(AeTransitionConditionDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
    */
   public void visit(AeActivityValidateDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef)
    */
   public void visit(AeExtensibleAssignDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionDef)
    */
   public void visit(AeExtensionDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionsDef)
    */
   public void visit(AeExtensionsDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
    */
   public void visit(AeFromPartsDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
    */
   public void visit(AeToPartsDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
    */
   public void visit(AeFromPartDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
    */
   public void visit(AeToPartDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
    */
   public void visit(AeSourcesDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
    */
   public void visit(AeTargetsDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * Creates the appropriate impl object to model the &lt;from&gt;
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   public void visit(AeFromDef aDef)
   {
      IAeFrom from = AeFromStrategyFactory.createFromStrategy(aDef);
   
      getCopyFromParent().setFrom(from);
      traverse(aDef, from);
   }

   /**
    * Creates the appropriate impl object to model the &lt;to&gt;
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
    */
   public void visit(AeToDef aDef)
   {
      IAeTo to = AeToStrategyFactory.createToStrategy(aDef);
      getCopyOperation().setTo(to);
      traverse(aDef, to);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
    */
   public void visit(AeQueryDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
    */
   public void visit(AeForDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
    */
   public void visit(AeUntilDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
    */
   public void visit(AeExtensionActivityDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
    */
   public void visit(AeConditionDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
      IAeActivity impl = new AeActivityIfImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
      AeIf elseIf = new AeIf(aDef, getActivityIf());
      getActivityIf().addElseIf(elseIf);
      traverse(aDef, elseIf);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      AeElseIf elseIf = new AeElseIf(aDef, getActivityIf());
      getActivityIf().addElseIf(elseIf);
      traverse(aDef, elseIf);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
      AeElse elseObj = new AeElse(aDef, getActivityIf());
      getActivityIf().setElse(elseObj);
      traverse(aDef, elseObj);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
    */
   public void visit(AeActivityRethrowDef aDef)
   {
      IAeActivity impl = new AeActivityRethrowImpl(aDef, getActivityParent());
      getActivityParent().addActivity(impl);
      traverse(aDef, impl);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
    */
   public void visit(AeRepeatEveryDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
    */
   public void visit(AeTerminationHandlerDef aDef)
   {
      AeTerminationHandler th = new AeTerminationHandler(aDef, getScope());
      getScope().setTerminationHandler(th);
      traverse(aDef, th);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLiteralDef)
    */
   public void visit(AeLiteralDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
    */
   public void visit(AeActivityOpaqueDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
    */
   public void visit(AeExtensionAttributeDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public void visit(AeExtensionElementDef aDef)
   {
   }

   /**
    * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeBaseXmlDef)
    */
   public void visit(AeBaseXmlDef aDef)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * Sets the process.
    *
    * @param aProcess
    */
   protected void setProcess(IAeBusinessProcessInternal aProcess)
   {
      mProcess = aProcess;
   }

   /**
    * Gets the process instance.
    */
   public IAeBusinessProcessInternal getProcess()
   {
      return mProcess;
   }

   /**
    * Searches the stack for a flow starting from length - 2.
    * @param aLinkName
    */
   private AeLink findLink(String aLinkName)
   {
      for (int i=mStack.size()-2; i>0; i--)
      {
         Object potentialLink = mStack.get(i);
         if (potentialLink instanceof AeActivityFlowImpl)
         {
            AeActivityFlowImpl flow = (AeActivityFlowImpl) potentialLink;
            AeLink link = flow.getLink(aLinkName);
            if (link != null)
            {
               return link;
            }
         }
      }
      // not finding one should be a fatal error.
      throw new RuntimeException(AeMessages.getString("AeDefToImplVisitor.ERROR_0")+aLinkName); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefToImplVisitor#reportObjects()
    */
   public void reportObjects()
   {
      // BPEL objects
      for (Iterator iter = getBpelObjects().iterator(); iter.hasNext();)
      {
         IAeBpelObject obj = (IAeBpelObject) iter.next();
         getProcess().addBpelObject(obj.getLocationPath(), obj);
      }
      getBpelObjects().clear();

      // variables
      for (Iterator iter = getVariables().iterator(); iter.hasNext();)
      {
         AeVariable variable = (AeVariable) iter.next();
         getProcess().addVariableMapping(variable);
      }
      getVariables().clear();

      // partner links
      for (Iterator iter = getPartnerLinks().iterator(); iter.hasNext();)
      {
         AePartnerLink plink = (AePartnerLink) iter.next();
         getProcess().addPartnerLinkMapping(plink);
      }
      getPartnerLinks().clear();
   }

   /**
    * Creates the message producer and assigns it on the wsio activity
    * @param aImpl - an invoke or receive
    * @param aDef - def provides the strategy
    */
   protected void assignMessageDataProducer(IAeMessageProducerParentAdapter aImpl, IAeMessageDataProducerDef aDef)
   {
      // Assign message data producer strategy.
      IAeMessageDataProducer messageDataProducer = createMessageProducer(aDef);
      aImpl.setMessageDataProducer(messageDataProducer);
   }

   /**
    * Creates a message producer object from the def
    * @param aDef
    */
   protected IAeMessageDataProducer createMessageProducer(IAeMessageDataProducerDef aDef)
   {
      IAeMessageDataProducer messageDataProducer;

      String producerStrategy = aDef.getMessageDataProducerStrategy();

      if (IAeMessageDataStrategyNames.ELEMENT_VARIABLE.equals(producerStrategy) ||
            IAeMessageDataStrategyNames.MESSAGE_VARIABLE.equals(producerStrategy))
      {
         messageDataProducer = new AeVariableMessageDataProducer();
      }
      else if (IAeMessageDataStrategyNames.TO_PARTS.equals(producerStrategy))
      {
         messageDataProducer = new AeToPartsMessageDataProducer();
      }
      else
      {
         // TODO (MF) might want to consider throwing an Error or RuntimeException here if there is no strategy set.
         // This is something that we'll catch during static analysis but it's
         // possible that it could break as a result of some WSDL change
         messageDataProducer = new AeEmptyMessageDataProducer();
      }
      return messageDataProducer;
   }

   /**
    * Assigns the consumer strategy to the wsio activity
    * @param aImpl
    * @param aDef
    */
   protected void assignMessageDataConsumer(IAeMessageConsumerParentAdapter aImpl, IAeMessageDataConsumerDef aDef)
   {
      // Assign message data consumer strategy.
      IAeMessageDataConsumer messageDataConsumer = createMessageConsumer(aDef);

      aImpl.setMessageDataConsumer(messageDataConsumer);
   }

   /**
    * Creaates a message consumer from the def
    * @param aDef
    */
   protected IAeMessageDataConsumer createMessageConsumer(IAeMessageDataConsumerDef aDef)
   {
      IAeMessageDataConsumer messageDataConsumer;

      if (IAeMessageDataStrategyNames.MESSAGE_VARIABLE.equals(aDef.getMessageDataConsumerStrategy()) ||
            IAeMessageDataStrategyNames.ELEMENT_VARIABLE.equals(aDef.getMessageDataConsumerStrategy()))
      {
         messageDataConsumer = new AeVariableMessageDataConsumer();
      }
      else if (IAeMessageDataStrategyNames.FROM_PARTS.equals(aDef.getMessageDataConsumerStrategy()))
      {
         messageDataConsumer = new AeFromPartsMessageDataConsumer();
      }
      else
      {
         messageDataConsumer = new AeNoopMessageDataConsumer();
      }
      return messageDataConsumer;
   }

   /**
    * @return Returns the faultMatchingStrategy.
    */
   public IAeFaultMatchingStrategy getFaultMatchingStrategy()
   {
      return mFaultMatchingStrategy;
   }

   /**
    * @param aFaultMatchingStrategy The faultMatchingStrategy to set.
    */
   public void setFaultMatchingStrategy(IAeFaultMatchingStrategy aFaultMatchingStrategy)
   {
      mFaultMatchingStrategy = aFaultMatchingStrategy;
   }

   /**
    * @return Returns the scopeTerminationStrategy.
    */
   public IAeScopeTerminationStrategy getScopeTerminationStrategy()
   {
      return mScopeTerminationStrategy;
   }

   /**
    * @param aScopeTerminationStrategy The scopeTerminationStrategy to set.
    */
   public void setScopeTerminationStrategy(IAeScopeTerminationStrategy aScopeTerminationStrategy)
   {
      mScopeTerminationStrategy = aScopeTerminationStrategy;
   }

   /**
    * Setter for the message validator
    * @param aMessageValidator
    */
   public void setMessageValidator(IAeMessageValidator aMessageValidator)
   {
      mMessageValidator = aMessageValidator;
   }

   /**
    * Getter for the message validator
    */
   public IAeMessageValidator getMessageValidator()
   {
      return mMessageValidator;
   }

   /**
    * @return Returns the bpelObjects.
    */
   public Collection getBpelObjects()
   {
      return mBpelObjects;
   }

   /**
    * @return Returns the partnerLinks.
    */
   public Collection getPartnerLinks()
   {
      return mPartnerLinks;
   }

   /**
    * @return Returns the variables.
    */
   public Collection getVariables()
   {
      return mVariables;
   }
}
