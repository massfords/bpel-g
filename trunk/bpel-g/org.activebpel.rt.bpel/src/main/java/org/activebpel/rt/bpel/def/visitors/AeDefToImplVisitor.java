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

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.def.*;
import org.activebpel.rt.bpel.def.activity.*;
import org.activebpel.rt.bpel.def.activity.support.*;
import org.activebpel.rt.bpel.def.faults.IAeFaultMatchingStrategy;
import org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio.IAeMessageDataStrategyNames;
import org.activebpel.rt.bpel.impl.*;
import org.activebpel.rt.bpel.impl.activity.*;
import org.activebpel.rt.bpel.impl.activity.assign.AeCopyOperation;
import org.activebpel.rt.bpel.impl.activity.assign.IAeFrom;
import org.activebpel.rt.bpel.impl.activity.assign.IAeTo;
import org.activebpel.rt.bpel.impl.activity.assign.from.AeFromStrategyFactory;
import org.activebpel.rt.bpel.impl.activity.assign.to.AeToStrategyFactory;
import org.activebpel.rt.bpel.impl.activity.support.*;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

/**
 * <p>Visitor responsible for creating implementation objects from their definition
 * objects.</p>
 * <p/>
 * <p>A static convenience method called createProcess has been setup to easily
 * create a new process implementation.</p>
 */
public abstract class AeDefToImplVisitor implements IAeDefToImplVisitor {
    /**
     * Used in conjunction with the traversal object to traverse the object
     * model
     */
    protected IAeDefVisitor mTraversalVisitor;
    /**
     * The process id of the process we are creating an implemntation for.
     */
    protected long mProcessId;
    /**
     * The engine the created process will run inside of.
     */
    protected IAeBusinessProcessEngineInternal mEngine;
    /**
     * The process that we're creating implementations for
     */
    protected IAeBusinessProcessInternal mProcess;
    /**
     * Stores the stack of objects that we're visiting
     */
    protected final Stack<Object> mStack = new Stack<>();
    /**
     * Plan used to create the business process instance
     */
    protected final IAeProcessPlan mPlan;

    /**
     * collection of variables we've created
     */
    private final Collection<AeVariable> mVariables = new ArrayList<>();
    /**
     * collection of bpel objects we've created
     */
    private final Collection<IAeBpelObject> mBpelObjects = new ArrayList<>();
    /**
     * collection of partner links we've created
     */
    private final Collection<AePartnerLink> mPartnerLinks = new ArrayList<>();

    /**
     * Strategy for matching faults
     */
    private IAeFaultMatchingStrategy mFaultMatchingStrategy;
    /**
     * Strategy for terminating a scope
     */
    private IAeScopeTerminationStrategy mScopeTerminationStrategy;

    /**
     * impl for validating service messages
     */
    private IAeMessageValidator mMessageValidator;

    /**
     * Main entry point for implementation creation.
     *
     * @param aPid    The process Id to associate with the new process
     * @param aEngine The engine the process will run inside of.
     * @param aPlan   The plan we are visiting.
     * @return The new process implementation .
     */
    public static IAeBusinessProcess createProcess(long aPid, IAeBusinessProcessEngineInternal aEngine,
                                                   IAeProcessPlan aPlan) {
        IAeDefToImplVisitor def2impl = AeDefVisitorFactory.getInstance(aPlan.getProcessDef().getNamespace()).createImplVisitor(aPid, aEngine, aPlan);
        aPlan.getProcessDef().accept(def2impl);
        def2impl.reportObjects();
        return def2impl.getProcess();
    }

    /**
     * Ctor requires the process that you want to visit.
     *
     * @param aPid the process id to create an implmentation for
     */
    protected AeDefToImplVisitor(long aPid, IAeBusinessProcessEngineInternal aEngine, IAeProcessPlan aPlan) {
        mProcessId = aPid;
        mEngine = aEngine;
        mPlan = aPlan;
    }

    /**
     * Special constructor for creating dynamic objects from a currently
     * executing process. These objects include the children of a parallel
     * forEach as well as concurrent onEvents/onAlarms within a scope.
     *
     * @param aProcess
     * @param aParent
     */
    protected AeDefToImplVisitor(IAeBusinessProcessInternal aProcess, IAeBpelObject aParent) {
        setProcess(aProcess);
        push(aParent);
        mPlan = aProcess.getProcessPlan();
    }

    /**
     * Setter for traversal visitor
     *
     * @param aDefVisitor
     */
    public void setTraversalVisitor(IAeDefVisitor aDefVisitor) {
        mTraversalVisitor = aDefVisitor;
    }

    /**
     * Getter for traversal visitor
     */
    public IAeDefVisitor getTraversalVisitor() {
        if (mTraversalVisitor == null) {
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
    public void visit(AeProcessDef def) {
        AeBusinessProcess process = (AeBusinessProcess) mEngine.createProcess(mProcessId, mPlan);
        setProcess(process);
        process.setTerminationStrategy(getScopeTerminationStrategy());
        process.setFaultMatchingStrategy(getFaultMatchingStrategy());
        traverse(mPlan.getProcessDef(), process);
    }

    /**
     * Creates the scope implementation and then traverses it.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
     */
    public void visit(AeActivityScopeDef def) {
        AeActivityScopeImpl impl = new AeActivityScopeImpl(def, getActivityParent());
        impl.setTerminationStrategy(getScopeTerminationStrategy());
        impl.setFaultMatchingStrategy(getFaultMatchingStrategy());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Gets the namespace for the version of BPEL that we're processing.
     */
    protected String getBPELNamespace() {
        return getProcess().getBPELNamespace();
    }

    /**
     * Pushes the implementation onto the stack so it'll become the parent for
     * any other activities created during the execution of this method. Then the
     * def is visited which may call back into this class with other visit
     * methods. Finally, we pop from the stack to restore the previous parent.
     *
     * @param aDef
     * @param aImpl
     */
    protected void traverse(AeBaseDef aDef, Object aImpl) {
        // Add all BPEL objects to our lookup map
        if (aImpl instanceof IAeBpelObject)
            getBpelObjects().add((IAeBpelObject) aImpl);

        // No need to bother with stack if we have no parent object
        if (aImpl == null)
            aDef.accept(getTraversalVisitor());
        else {
            push(aImpl);
            aDef.accept(getTraversalVisitor());
            pop();
        }
    }

    /**
     * Pushes the activity onto the stack making it the current activity.
     *
     * @param aObj
     */
    protected void push(Object aObj) {
        mStack.push(aObj);
    }

    /**
     * Pops the activity from the stack.
     */
    protected void pop() {
        mStack.pop();
    }

    /**
     * Peeks at the current object on the stack.
     */
    protected Object peek() {
        return mStack.peek();
    }

    /**
     * Type safe peeker.
     */
    protected IAeActivityParent getActivityParent() {
        return (IAeActivityParent) peek();
    }

    /**
     * Type safe peeker.
     */
    protected AeActivityScopeImpl getScope() {
        return (AeActivityScopeImpl) peek();
    }

    /**
     * Type safe peeker for variable container
     */
    protected IAeVariableContainer getVariableContainer() {
        return (IAeVariableContainer) peek();
    }

    /**
     * Type safe peeker.
     */
    protected IAeCopyFromParent getCopyFromParent() {
        return (IAeCopyFromParent) peek();
    }

    /**
     * Type safe peeker.
     */
    protected IAeEventParent getMessageParent() {
        return (IAeEventParent) peek();
    }

    /**
     * Type safe peeker.
     */
    protected AeActivityIfImpl getActivityIf() {
        return (AeActivityIfImpl) peek();
    }

    /**
     * Type safe peeker.
     */
    protected AeActivityFlowImpl getFlow() {
        return (AeActivityFlowImpl) peek();
    }

    /**
     * Type safe peeker.
     */
    protected AeActivityImpl getActivity() {
        return (AeActivityImpl) peek();
    }

    /**
     * Type safe peeker.
     */
    protected AeCopyOperation getCopyOperation() {
        return (AeCopyOperation) mStack.peek();
    }

    /**
     * Type safe peeker.
     */
    protected AeActivityAssignImpl getAssign() {
        return (AeActivityAssignImpl) mStack.peek();
    }

    /**
     * Calls <code>accept</code> on the definition object. No need to create
     * an implementation object for the container here.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
     */
    public void visit(AeCorrelationSetsDef def) {
        traverse(def, null);
    }

    /**
     * Create an event handler and add it to the scope. We then traverse in order
     * to pick up any messages or alarms.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeEventHandlersDef)
     */
    public void visit(AeEventHandlersDef def) {
        AeEventHandlersContainer events = new AeEventHandlersContainer(def, getScope());
        getScope().setEventHandlersContainer(events);
        traverse(def, events);
    }

    /**
     * Calls <code>accept</code> on the definition object. No need to create
     * an implementation object for the container here. The scope/process
     * currently on the stack will have the AeFaultHandlersDef objects added to
     * it when they get visited.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
     */
    public void visit(AeFaultHandlersDef def) {
        traverse(def, null);
    }

    /**
     * Create the implementation object for the compensation handler container
     * and put it on the stack. The next activity visited will be added to the
     * compensation handler container.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
     */
    public void visit(AeCompensationHandlerDef def) {
        AeCompensationHandler compHandler = new AeCompensationHandler(def, getScope());
        getScope().setCompensationHandler(compHandler);
        traverse(def, compHandler);
    }

    /**
     * Calls <code>accept</code> on the definition object. No need to create
     * an implementation object for the container here. The scope/process
     * currently on the stack will have the AeVariableDef objects added to it
     * when they get visited.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariablesDef)
     */
    public void visit(AeVariablesDef def) {
        AeVariablesImpl variablesImpl = new AeVariablesImpl(def, getScope());
        getScope().setVariablesImpl(variablesImpl);
        traverse(def, variablesImpl);
    }

    /**
     * Creates the assign implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
     */
    public void visit(AeActivityAssignDef def) {
        AeActivityAssignImpl impl = new AeActivityAssignImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Creates the compensate implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
     */
    public void visit(AeActivityCompensateDef def) {
        AeActivityCompensateImpl impl = new AeActivityCompensateImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
     */
    public void visit(AeActivityCompensateScopeDef def) {
        AeActivityCompensateScopeImpl impl = new AeActivityCompensateScopeImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Creates the empty implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef)
     */
    public void visit(AeActivityEmptyDef def) {
        IAeActivity impl = new AeActivityEmptyImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Creates the flow implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
     */
    public void visit(AeActivityFlowDef def) {
        IAeActivity impl = new AeActivityFlowImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Creates the invoke implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
     */
    public void visit(AeActivityInvokeDef def) {
        AeActivityInvokeImpl impl = new AeActivityInvokeImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        impl.setMessageValidator(getMessageValidator());

        assignMessageDataProducer(impl, def);
        assignMessageDataConsumer(impl, def);

        traverse(def, impl);
    }

    /**
     * Creates the pick implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
     */
    public void visit(AeActivityPickDef def) {
        IAeActivity impl = new AeActivityPickImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Creates the receive implmentation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
     */
    public void visit(AeActivityReceiveDef def) {
        AeActivityReceiveImpl impl = new AeActivityReceiveImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        impl.setMessageValidator(getMessageValidator());

        assignMessageDataConsumer(impl, def);

        traverse(def, impl);
    }

    /**
     * Creates the reply implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
     */
    public void visit(AeActivityReplyDef def) {
        AeActivityReplyImpl impl = new AeActivityReplyImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        impl.setMessageValidator(getMessageValidator());

        assignMessageDataProducer(impl, def);

        traverse(def, impl);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
     */
    public void visit(AeActivitySuspendDef def) {
        IAeActivity impl = new AeActivitySuspendImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Creates the sequence implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
     */
    public void visit(AeActivitySequenceDef def) {
        IAeActivity impl = new AeActivitySequenceImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Creates the terminate implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
     */
    public void visit(AeActivityExitDef def) {
        IAeActivity impl = new AeActivityTerminateImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Creates the throw implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
     */
    public void visit(AeActivityThrowDef def) {
        IAeActivity impl = new AeActivityThrowImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Creates the wait implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
     */
    public void visit(AeActivityWaitDef def) {
        IAeActivity impl = new AeActivityWaitImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
     */
    public void visit(AeActivityForEachDef def) {
        IAeActivity impl = def.isParallel() ?
                new AeActivityForEachParallelImpl(def, getActivityParent()) :
                new AeActivityForEachImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef)
     */
    public void visit(AeForEachFinalDef def) {
        // no impl for final expression
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef)
     */
    public void visit(AeForEachStartDef aDef) {
        // no impl for start expression
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef)
     */
    public void visit(AeForEachBranchesDef def) {
        // no impl for branches
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef)
     */
    public void visit(AeForEachCompletionConditionDef def) {
        // no impl for completion condition
    }

    /**
     * Creates the <code>while</code> implementation and traverses.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
     */
    public void visit(AeActivityWhileDef def) {
        IAeActivity impl = new AeActivityWhileImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
     */
    public void visit(AeActivityRepeatUntilDef def) {
        IAeActivity impl = new AeActivityRepeatUntilImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
     */
    public void visit(AeActivityContinueDef def) {
        IAeActivity impl = new AeActivityContinueImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
     */
    public void visit(AeActivityBreakDef def) {
        IAeActivity impl = new AeActivityBreakImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * Create instance of fault handler implementation and add to the current scope's
     * fault handler container. Then traverse the def object so we'll add the
     * child activity to the fault.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
     */
    public void visit(AeCatchDef def) {
        AeFaultHandler fh = new AeFaultHandler(def, getScope());
        getScope().addFaultHandler(fh);
        traverse(def, fh);
    }

    /**
     * Create instance of alarm and add to the current parent which is either
     * a pick or an event handler for a scope.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
     */
    public void visit(AeOnAlarmDef def) {
        AeOnAlarm alarm = new AeOnAlarm(def, getMessageParent());
        getMessageParent().addAlarm(alarm);
        traverse(def, alarm);
    }

    /**
     * Create instance of message and add to the current parent which is either
     * a pick or an event handler for a scope.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
     */
    public void visit(AeOnMessageDef def) {
        AeOnMessage msg = new AeOnMessage(def, getMessageParent());
        msg.setMessageValidator(getMessageValidator());
        getMessageParent().addMessage(msg);

        assignMessageDataConsumer(msg, def);

        traverse(def, msg);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
     */
    public void visit(AeOnEventDef def) {
        visit((AeOnMessageDef) def);
    }

    /**
     * Create instance of variable and add to scope.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
     */
    public void visit(AeVariableDef def) {
        AeVariable var = new AeVariable(getVariableContainer(), def);
        getVariableContainer().addVariable(var);
        getVariables().add(var);
        traverse(def, var);
    }

    /**
     * Create instance of <code>catchAll</code> and add to scope.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchAllDef)
     */
    public void visit(AeCatchAllDef def) {
        AeDefaultFaultHandler catchAll = new AeDefaultFaultHandler(def, getScope());
        getScope().setDefaultFaultHandler(catchAll);
        traverse(def, catchAll);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
     */
    public void visit(AeAssignCopyDef def) {
        AeActivityAssignImpl assign = getAssign();
        AeCopyOperation copy = new AeCopyOperation(def, assign.getCopyOperationContext());
        assign.addCopyOperation(copy);
        traverse(def, copy);
    }

    /**
     * No implementation to create or traverse.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
     */
    public void visit(AeCorrelationDef def) {
        // no-op
    }

    /**
     * Create a link implementation and add it to the current flow. No further
     * traversal.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinkDef)
     */
    public void visit(AeLinkDef def) {
        AeLink link = new AeLink(def, getFlow());
        getFlow().addLink(link);
    }

    /**
     * No implementation or further traversal.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
     */
    public void visit(AePartnerDef def) {
        // no-op
    }

    /**
     * Create the <code>partnerLink</code> impl and add to the process.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
     */
    public void visit(AePartnerLinkDef def) {
        AePartnerLink plink = new AePartnerLink(getScope(), def);
        getScope().addPartnerLink(plink);
        getPartnerLinks().add(plink);
    }

    /**
     * Nothing to create for scope def, just traverse to visit all of its
     * children.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
     */
    public void visit(AeScopeDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
     */
    public void visit(AeMessageExchangesDef def) {
        // nothing to create here
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
     */
    public void visit(AeMessageExchangeDef def) {
        // nothing to create here and no children that need traversing
    }

    /**
     * Find the <code>link</code> that this <code>source</code> references and
     * add it to the <code>AeActivityImpl</code>'s collection of source links.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
     */
    public void visit(AeSourceDef def) {
        AeLink link = findLink(def.getLinkName());
        link.setTransitionConditionDef(def.getTransitionConditionDef());
        link.setSourceActivity(getActivity());
        getActivity().addSourceLink(link);
    }

    /**
     * Find the <code>link</code> that this <code>target</code> references and
     * add it to the <code>AeActivityImpl</code>'s collection of target links.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
     */
    public void visit(AeTargetDef def) {
        AeLink link = findLink(def.getLinkName());
        link.setTargetActivity(getActivity());
        getActivity().addTargetLink(link);
    }

    /**
     * Nothing to create directly, instead just visit the container so we'll hit
     * the <code>partnerLink</code>s individually.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinksDef)
     */
    public void visit(AePartnerLinksDef def) {
        traverse(def, null);
    }

    /**
     * Nothing to create or traverse here.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
     */
    public void visit(AePartnersDef def) {
    }

    /**
     * Nothing to create but we need to traverse so we'll hit the links.
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinksDef)
     */
    public void visit(AeLinksDef def) {
        traverse(def, null);
    }

    /**
     * Creates a correlation impl which handles initiating or validating the
     * correlations used for a wsio activity
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
     */
    public void visit(AeCorrelationsDef def) {
        IAeWSIOActivity parent = (IAeWSIOActivity) peek();

        if (parent instanceof AeActivityInvokeImpl) {
            // invoke uses the pattern attribute for correlations so the sets may
            // be initiated/validated using request or response data
            if (def.isRequestPatternUsed()) {
                IAeCorrelations requestCorrelationsImpl = new AeCorrelationsPatternImpl(def, parent, true);
                parent.setRequestCorrelations(requestCorrelationsImpl);
            }
            if (def.isResponsePatternUsed()) {
                IAeCorrelations responseCorrelationsImpl = new AeCorrelationsPatternImpl(def, parent, false);
                parent.setResponseCorrelations(responseCorrelationsImpl);
            }
        } else if (parent instanceof AeActivityReplyImpl) {
            parent.setResponseCorrelations(new AeCorrelationsImpl(def, parent));
        } else {
            AeIMACorrelations correlations = new AeIMACorrelations(def, parent);
            correlations.setFilter(getCorrelationsFilter());
            parent.setRequestCorrelations(correlations);
        }
        traverse(def, null);
    }

    /**
     * Returns a filter to use for IMA's when they queue and need to test for
     * conflictingReceives
     */
    protected abstract AeIMACorrelations.IAeCorrelationSetFilter getCorrelationsFilter();

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeImportDef)
     */
    public void visit(AeImportDef def) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeDocumentationDef)
     */
    public void visit(AeDocumentationDef aDef) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef)
     */
    public void visit(AeJoinConditionDef def) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef)
     */
    public void visit(AeTransitionConditionDef def) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
     */
    public void visit(AeActivityValidateDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef)
     */
    public void visit(AeExtensibleAssignDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionDef)
     */
    public void visit(AeExtensionDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionsDef)
     */
    public void visit(AeExtensionsDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
     */
    public void visit(AeFromPartsDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
     */
    public void visit(AeToPartsDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
     */
    public void visit(AeFromPartDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
     */
    public void visit(AeToPartDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
     */
    public void visit(AeSourcesDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
     */
    public void visit(AeTargetsDef def) {
        traverse(def, null);
    }

    /**
     * Creates the appropriate impl object to model the &lt;from&gt;
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
     */
    public void visit(AeFromDef def) {
        IAeFrom from = AeFromStrategyFactory.createFromStrategy(def);

        getCopyFromParent().setFrom(from);
        traverse(def, from);
    }

    /**
     * Creates the appropriate impl object to model the &lt;to&gt;
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
     */
    public void visit(AeToDef def) {
        IAeTo to = AeToStrategyFactory.createToStrategy(def);
        getCopyOperation().setTo(to);
        traverse(def, to);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeQueryDef)
     */
    public void visit(AeQueryDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeForDef)
     */
    public void visit(AeForDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeUntilDef)
     */
    public void visit(AeUntilDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(AeChildExtensionActivityDef)
     */
    public void visit(AeChildExtensionActivityDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
     */
    public void visit(AeExtensionActivityDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeConditionDef)
     */
    public void visit(AeConditionDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
     */
    public void visit(AeActivityIfDef def) {
        IAeActivity impl = new AeActivityIfImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
     */
    public void visit(AeIfDef def) {
        AeIf elseIf = new AeIf(def, getActivityIf());
        getActivityIf().addElseIf(elseIf);
        traverse(def, elseIf);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
     */
    public void visit(AeElseIfDef def) {
        AeElseIf elseIf = new AeElseIf(def, getActivityIf());
        getActivityIf().addElseIf(elseIf);
        traverse(def, elseIf);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
     */
    public void visit(AeElseDef def) {
        AeElse elseObj = new AeElse(def, getActivityIf());
        getActivityIf().setElse(elseObj);
        traverse(def, elseObj);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
     */
    public void visit(AeActivityRethrowDef def) {
        IAeActivity impl = new AeActivityRethrowImpl(def, getActivityParent());
        getActivityParent().addActivity(impl);
        traverse(def, impl);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef)
     */
    public void visit(AeRepeatEveryDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
     */
    public void visit(AeTerminationHandlerDef def) {
        AeTerminationHandler th = new AeTerminationHandler(def, getScope());
        getScope().setTerminationHandler(th);
        traverse(def, th);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLiteralDef)
     */
    public void visit(AeLiteralDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef)
     */
    public void visit(AeActivityOpaqueDef def) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionAttributeDef)
     */
    public void visit(AeExtensionAttributeDef aDef) {
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
     */
    public void visit(AeExtensionElementDef aDef) {
    }

    /**
     * @see org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor#visit(org.activebpel.rt.xml.def.AeBaseXmlDef)
     */
    public void visit(AeBaseXmlDef aDef) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the process.
     *
     * @param aProcess
     */
    protected void setProcess(IAeBusinessProcessInternal aProcess) {
        mProcess = aProcess;
    }

    /**
     * Gets the process instance.
     */
    public IAeBusinessProcessInternal getProcess() {
        return mProcess;
    }

    /**
     * Searches the stack for a flow starting from length - 2.
     *
     * @param aLinkName
     */
    private AeLink findLink(String aLinkName) {
        for (int i = mStack.size() - 2; i > 0; i--) {
            Object potentialLink = mStack.get(i);
            if (potentialLink instanceof AeActivityFlowImpl) {
                AeActivityFlowImpl flow = (AeActivityFlowImpl) potentialLink;
                AeLink link = flow.getLink(aLinkName);
                if (link != null) {
                    return link;
                }
            }
        }
        // not finding one should be a fatal error.
        throw new RuntimeException(AeMessages.getString("AeDefToImplVisitor.ERROR_0") + aLinkName); //$NON-NLS-1$
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefToImplVisitor#reportObjects()
     */
    public void reportObjects() {
        // BPEL objects
        for (IAeBpelObject obj : getBpelObjects()) {
            getProcess().addBpelObject(obj.getLocationPath(), obj);
        }
        getBpelObjects().clear();

        // variables
        for (AeVariable variable : getVariables()) {
            getProcess().addVariableMapping(variable);
        }
        getVariables().clear();

        // partner links
        for (AePartnerLink link : getPartnerLinks()) {
            getProcess().addPartnerLinkMapping(link);
        }
        getPartnerLinks().clear();
    }

    /**
     * Creates the message producer and assigns it on the wsio activity
     *
     * @param aImpl - an invoke or receive
     * @param aDef  - def provides the strategy
     */
    protected void assignMessageDataProducer(IAeMessageProducerParentAdapter aImpl, IAeMessageDataProducerDef aDef) {
        // Assign message data producer strategy.
        IAeMessageDataProducer messageDataProducer = createMessageProducer(aDef);
        aImpl.setMessageDataProducer(messageDataProducer);
    }

    /**
     * Creates a message producer object from the def
     *
     * @param aDef
     */
    protected IAeMessageDataProducer createMessageProducer(IAeMessageDataProducerDef aDef) {
        IAeMessageDataProducer messageDataProducer;

        String producerStrategy = aDef.getMessageDataProducerStrategy();

        if (IAeMessageDataStrategyNames.ELEMENT_VARIABLE.equals(producerStrategy) ||
                IAeMessageDataStrategyNames.MESSAGE_VARIABLE.equals(producerStrategy)) {
            messageDataProducer = new AeVariableMessageDataProducer();
        } else if (IAeMessageDataStrategyNames.TO_PARTS.equals(producerStrategy)) {
            messageDataProducer = new AeToPartsMessageDataProducer();
        } else {
            // TODO (MF) might want to consider throwing an Error or RuntimeException here if there is no strategy set.
            // This is something that we'll catch during static analysis but it's
            // possible that it could break as a result of some WSDL change
            messageDataProducer = new AeEmptyMessageDataProducer();
        }
        return messageDataProducer;
    }

    /**
     * Assigns the consumer strategy to the wsio activity
     *
     * @param aImpl
     * @param aDef
     */
    protected void assignMessageDataConsumer(IAeMessageConsumerParentAdapter aImpl, IAeMessageDataConsumerDef aDef) {
        // Assign message data consumer strategy.
        IAeMessageDataConsumer messageDataConsumer = createMessageConsumer(aDef);

        aImpl.setMessageDataConsumer(messageDataConsumer);
    }

    /**
     * Creaates a message consumer from the def
     *
     * @param aDef
     */
    protected IAeMessageDataConsumer createMessageConsumer(IAeMessageDataConsumerDef aDef) {
        IAeMessageDataConsumer messageDataConsumer;

        if (IAeMessageDataStrategyNames.MESSAGE_VARIABLE.equals(aDef.getMessageDataConsumerStrategy()) ||
                IAeMessageDataStrategyNames.ELEMENT_VARIABLE.equals(aDef.getMessageDataConsumerStrategy())) {
            messageDataConsumer = new AeVariableMessageDataConsumer();
        } else if (IAeMessageDataStrategyNames.FROM_PARTS.equals(aDef.getMessageDataConsumerStrategy())) {
            messageDataConsumer = new AeFromPartsMessageDataConsumer();
        } else {
            messageDataConsumer = new AeNoopMessageDataConsumer();
        }
        return messageDataConsumer;
    }

    /**
     * @return Returns the faultMatchingStrategy.
     */
    public IAeFaultMatchingStrategy getFaultMatchingStrategy() {
        return mFaultMatchingStrategy;
    }

    /**
     * @param aFaultMatchingStrategy The faultMatchingStrategy to set.
     */
    public void setFaultMatchingStrategy(IAeFaultMatchingStrategy aFaultMatchingStrategy) {
        mFaultMatchingStrategy = aFaultMatchingStrategy;
    }

    /**
     * @return Returns the scopeTerminationStrategy.
     */
    public IAeScopeTerminationStrategy getScopeTerminationStrategy() {
        return mScopeTerminationStrategy;
    }

    /**
     * @param aScopeTerminationStrategy The scopeTerminationStrategy to set.
     */
    public void setScopeTerminationStrategy(IAeScopeTerminationStrategy aScopeTerminationStrategy) {
        mScopeTerminationStrategy = aScopeTerminationStrategy;
    }

    /**
     * Setter for the message validator
     *
     * @param aMessageValidator
     */
    public void setMessageValidator(IAeMessageValidator aMessageValidator) {
        mMessageValidator = aMessageValidator;
    }

    /**
     * Getter for the message validator
     */
    public IAeMessageValidator getMessageValidator() {
        return mMessageValidator;
    }

    /**
     * @return Returns the bpelObjects.
     */
    public Collection<IAeBpelObject> getBpelObjects() {
        return mBpelObjects;
    }

    /**
     * @return Returns the partnerLinks.
     */
    public Collection<AePartnerLink> getPartnerLinks() {
        return mPartnerLinks;
    }

    /**
     * @return Returns the variables.
     */
    public Collection<AeVariable> getVariables() {
        return mVariables;
    }
}
