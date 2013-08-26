//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeProcessDefToWebModelVisitor.java,v 1.24 2008/02/29 23:44:38 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.AeCatchAllDef;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetsDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AeEventHandlersDef;
import org.activebpel.rt.bpel.def.AeExtensionActivityDef;
import org.activebpel.rt.bpel.def.AeFaultHandlersDef;
import org.activebpel.rt.bpel.def.AeMessageExchangeDef;
import org.activebpel.rt.bpel.def.AeMessageExchangesDef;
import org.activebpel.rt.bpel.def.AeNamedDef;
import org.activebpel.rt.bpel.def.AePartnerDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinksDef;
import org.activebpel.rt.bpel.def.AePartnersDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.AeTerminationHandlerDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.AeVariablesDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
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
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinksDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartDef;
import org.activebpel.rt.bpel.def.activity.support.AeToPartsDef;
import org.activebpel.rt.bpel.def.io.IAeBpelLegacyConstants;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeExtensionAttributeDef;
import org.activebpel.rt.xml.def.AeExtensionElementDef;
import org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNode;
import org.activebpel.rt.xml.def.graph.IAeXmlDefGraphNodeAdapter;

/**
 * Visitor responsible for building the web visual model by visiting the BPEL definitions.
 */
public class AeProcessDefToWebModelVisitor extends AeProcessDefToWebVisitorBase {
    /**
     * Used in conjunction with the traversal object to traverse the object model
     */
    protected IAeDefVisitor mTraversalVisitor;

    /**
     * Visual model used to represent a business process impl.
     */
    private AeBpelProcessObject mBpelProcessModel;

    /**
     * Stack for the current visual model parent.
     */
    private final Stack<AeBpelObjectBase> mStack;

    /**
     * Stack for the holding links on per Flow basis.
     */
    private final Stack<Map<String, AeBpelLinkObject>> mLinkContainerStack;

    /**
     * List of links visited
     */
    private List<AeBpelLinkObject> mLinksList;

    /**
     * Process def
     */
    private AeProcessDef mProcessDef;

    /**
     * Ctor.
     *
     * @param aProcessDef process definition.
     */
    public AeProcessDefToWebModelVisitor(AeProcessDef aProcessDef) {
        setProcessDef(aProcessDef);
        mStack = new Stack<>();
        mLinkContainerStack = new Stack<>();
        init();
    }

    /**
     * Build the web visual model by visiting the process definition.
     */
    public void startVisiting() {
        visit(getProcessDef());
    }

    /**
     * @return Returns the processDef.
     */
    protected AeProcessDef getProcessDef() {
        return mProcessDef;
    }

    /**
     * @param aProcessDef The processDef to set.
     */
    protected void setProcessDef(AeProcessDef aProcessDef) {
        mProcessDef = aProcessDef;
    }

    /**
     * Creates the traversal and visitor object we're using to visit the object
     * model.
     */
    protected void init() {
        mTraversalVisitor = new AeTraversalVisitor(new AeDefTraverser(), this);
    }

    /**
     * Returns list containt AeLinkImpl objects that were visited.
     */
    protected List<AeBpelLinkObject> getLinksList() {
        if (mLinksList == null) {
            mLinksList = new ArrayList<>();
        }
        return mLinksList;
    }

    /**
     * @return Returns the bpelProcess.
     */
    public AeBpelProcessObject getBpelProcessModel() {
        return mBpelProcessModel;
    }

    /**
     * Sets the process visual model.
     *
     * @param aBpelProcess The bpelProcess to set.
     */
    protected void setBpelProcessModel(AeBpelProcessObject aBpelProcess) {
        mBpelProcessModel = aBpelProcess;
    }

    /**
     * Pops the current visual model parent from the stack.
     */
    protected AeBpelObjectBase popModel() {
        return mStack.pop();
    }

    /**
     * Pushes the current visual model parent to the stack.
     */
    protected void pushModel(AeBpelObjectBase aBpelObject) {
        mStack.push(aBpelObject);
    }

    /**
     * Returns the current model from the stack via peek.
     */
    protected AeBpelObjectBase getModel() {
        return mStack.peek();
    }

    /**
     * Type safe peek at the current stack.
     *
     * @return bpel activity model.
     */
    protected AeBpelActivityObject getActivityModel() {
        return (AeBpelActivityObject) mStack.peek();
    }

    /**
     * Creates a new map and pushes it into the links container stack. This is normally
     * done when a flow is visited.
     */
    protected void pushLinksContainer() {
        Map<String, AeBpelLinkObject> map = new HashMap<>();
        mLinkContainerStack.push(map);
    }

    /**
     * Pops the map containing the links in the current flow.
     *
     * @return map containing links in the current flow.
     */
    protected Map popLinksContainer() {
        return mLinkContainerStack.pop();
    }

    /**
     * @return the map containing links in the current flow.
     */
    protected Map<String, AeBpelLinkObject> getLinksContainerMap() {
        return mLinkContainerStack.peek();
    }

    /**
     * Adds a link to the process model.
     *
     * @param aLinkModel
     */
    protected void addLink(AeBpelLinkObject aLinkModel) {
        getLinksList().add(aLinkModel);
        getLinksContainerMap().put(aLinkModel.getName(), aLinkModel);
        // add to the root process model.
        getBpelProcessModel().addLink(aLinkModel);
        getBpelProcessModel().addWebModel(aLinkModel);
    }

    /**
     * Returns the link given its name. The link is searched by name by walking up the Flow
     * hierarchy.
     *
     * @param aName name of link.
     * @return link if found or null otherwise.
     */
    protected AeBpelLinkObject getLink(String aName) {
        AeBpelLinkObject rVal = null;
        int size = mLinkContainerStack.size();
        for (int i = size - 1; i >= 0 && rVal == null; i--) {
            rVal = mLinkContainerStack.get(i).get(aName);
        }
        return rVal;
    }

    /**
     * Convenience method that returns the def name if available otherwise returns empty string.
     *
     * @param aDef bpel definition
     * @return name of bpel object or empty string if not available.
     */
    protected String getDefName(AeBaseXmlDef aDef) {
        String rVal = ""; //$NON-NLS-1$
        if (aDef instanceof AeNamedDef) {
            AeNamedDef namedDef = (AeNamedDef) aDef;
            rVal = namedDef.getName();
        }
        return rVal;
    }

    /**
     * Adds the visiual model to the current parent and initializes the visual model with the
     * definition name and location path.
     *
     * @param aDef        def model.
     * @param aBpelObject visual model
     */
    protected void initModel(AeBaseXmlDef aDef, AeBpelObjectBase aBpelObject) {
        aBpelObject.setLocationPath(aDef.getLocationPath());
        aBpelObject.setName(getDefName(aDef));
        // FIXMEPJ process object model was being visited twice and getting its state overwritten. Should only visit once.
        if (AeUtil.isNullOrEmpty(aBpelObject.getState()))
            setState(aDef, aBpelObject);
        addModel(aBpelObject);
    }

    /**
     * Adds the given visual model to the current parent.
     *
     * @param aBpelObject
     */
    protected void addModel(AeBpelObjectBase aBpelObject) {
        if (aBpelObject != getBpelProcessModel() && getModel() instanceof AeBpelObjectContainer) {
            // add child
            ((AeBpelObjectContainer) getModel()).addChild(aBpelObject);
            getBpelProcessModel().addWebModel(aBpelObject);
        }
    }

    /**
     * Sets visual model state given its corresponding def.
     *
     * @param aDef        bpel definition.
     * @param aBpelObject web visual model.
     */
    protected void setState(AeBaseXmlDef aDef, AeBpelObjectBase aBpelObject) {
        // no-op. subclasses which uses the state document may override this method to reflect the current state.
    }

    /**
     * Sets process visual model state given its corresponding def.
     *
     * @param aDef        bpel process definition.
     * @param aBpelObject web visual model.
     */
    protected void setState(AeProcessDef aDef, AeBpelProcessObject aBpelObject) {
        // no-op. subclasses which uses the state document may override this method to reflect the current state.
    }

    /**
     * Sets link visual model state given its corresponding def.
     *
     * @param aDef        bpel link definition.
     * @param aBpelObject web visual model.
     */
    protected void setState(AeLinkDef aDef, AeBpelLinkObject aBpelObject) {
        // no-op. subclasses which uses the state document may override this method to reflect the current state.
    }

    /**
     * Pushes the visual model onto the stack so it will be the parent for any
     * other activities created during the execution of this method. Then the
     * def is visited which may call back into this class with other visit methods.
     * Finally, we pop from the stack to restore the previous parent.
     *
     * @param aDef
     * @param aVisualModel
     */
    protected void traverse(AeBaseXmlDef aDef, AeBpelObjectBase aVisualModel) {
        // No need to bother with stack if we have no parent object
        if (aVisualModel == null) {
            aDef.accept(mTraversalVisitor);
        } else {
            // init model parameters and add it to the parent.
            initModel(aDef, aVisualModel);
            pushModel(aVisualModel);
            aDef.accept(mTraversalVisitor);
            popModel();
        }
    }

    /**
     * Checks to see if the given container model is empty and if so, the model
     * is removed from the current hierarchy.
     *
     * @param aModel
     */
    protected void checkEmpty(AeBpelObjectContainer aModel) {
        // TODO (PJ) remove this method when defect wrt visting empty def is fixed.
        if (aModel.size() == 0 && aModel.getParent() != null) {
            (aModel.getParent()).removeChild(aModel);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
     */
    public void visit(AeProcessDef def) {
        super.visit(def);
        AeBpelProcessObject bpelProcess = new AeBpelProcessObject(def, getDefName(def), def.getLocationPath());
        setBpelProcessModel(bpelProcess);
        setState(def, bpelProcess);
        traverse(def, bpelProcess);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
     */
    public void visit(AeActivityAssignDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_ASSIGN, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
     */
    public void visit(AeActivityCompensateDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_COMPENSATE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
     */
    public void visit(AeActivityCompensateScopeDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(isBpelVersion11() ? IAeBPELConstants.TAG_COMPENSATE : IAeBPELConstants.TAG_COMPENSATE_SCOPE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef)
     */
    public void visit(AeActivityEmptyDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_EMPTY, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
     */
    public void visit(AeActivityFlowDef def) {
        // create a new map (and push it into the stack) to hold the links in this container.
        pushLinksContainer();
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_FLOW, def);
        traverse(def, model);
        // pop the links container map.
        popLinksContainer();
    }

    /**
     * Overrides method to
     *
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
     */
    public void visit(AeActivityInvokeDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_INVOKE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
     */
    public void visit(AeActivityPickDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_PICK, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
     */
    public void visit(AeActivityReceiveDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_RECEIVE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
     */
    public void visit(AeActivityReplyDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_REPLY, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
     */
    public void visit(AeActivitySuspendDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_SUSPEND, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
     */
    public void visit(AeActivityScopeDef def) {
        AeBpelScopeObject model = new AeBpelScopeObject(def);
        if (isPeopleActivity(def)) {
            model.setIconName("peopleActivity"); //$NON-NLS-1$
            model.setTagName("People"); //$NON-NLS-1$
        }
        traverse(def, model);
    }

    /**
     * Returns true if the scope is a people activity container.
     *
     * @param aDef people actitvity scope
     * @return true if scope has extension attribute identfying it as a people activity.
     */
    protected boolean isPeopleActivity(AeActivityScopeDef aDef) {
        Iterator it = aDef.getExtensionAttributeDefs().iterator();
        while (it.hasNext()) {
            AeExtensionAttributeDef attrib = (AeExtensionAttributeDef) it.next();
            if ("http://www.activebpel.org/2006/09/bpel/extension/peopleActivity".equals(attrib.getNamespace()) //$NON-NLS-1$
                    && "people-activity".equals(AeXmlUtil.extractLocalPart(attrib.getQualifiedName()))) //$NON-NLS-1$
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
     */
    public void visit(AeActivityContinueDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_CONTINUE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
     */
    public void visit(AeActivityBreakDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_BREAK, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetDef)
     */
    public void visit(AeCorrelationSetDef aDef) {
        AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_CORRELATION_SET, aDef);
        model.setDisplayOutlineOnly(true);
        traverse(aDef, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
     */
    public void visit(AeCatchDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_CATCH, def);
        traverse(def, model);
        model.setName(getLocalName(def.getFaultName()));
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchAllDef)
     */
    public void visit(AeCatchAllDef def) {
        // catchAll
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_CATCH_ALL, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
     */
    public void visit(AeTerminationHandlerDef def) {
        AeBpelTerminationHandlerObject model = new AeBpelTerminationHandlerObject(def);
        traverse(def, model);
        checkEmpty(model);
    }


    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
     */
    public void visit(AeVariableDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_VARIABLE, def);
        model.setDisplayOutlineOnly(true);
        getBpelProcessModel().addVariablePath(model.getLocationPath());
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariablesDef)
     */
    public void visit(AeVariablesDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_VARIABLES, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeEventHandlersDef)
     */
    public void visit(AeEventHandlersDef def) {
        AeBpelEventHandlersObject model = new AeBpelEventHandlersObject(def);
        traverse(def, model);
        checkEmpty(model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
     */
    public void visit(AeCompensationHandlerDef def) {
        AeBpelCompensationHandlerObject model = new AeBpelCompensationHandlerObject(def);
        traverse(def, model);
        checkEmpty(model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
     */
    public void visit(AeCorrelationSetsDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_CORRELATION_SETS, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
     */
    public void visit(AeFromPartDef def) {
        AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_FROM_PART, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
        //override name with part
        model.setName(def.getPart());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
     */
    public void visit(AeFromPartsDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_FROM_PARTS, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
    }


    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
     */
    public void visit(AeToPartDef def) {
        AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_TO_PART, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
        //override name with part
        model.setName(def.getPart());

    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
     */
    public void visit(AeToPartsDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_TO_PARTS, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
     */
    public void visit(AeFaultHandlersDef def) {
        AeBpelFaultHandlersObject model = new AeBpelFaultHandlersObject(def);
        traverse(def, model);
        checkEmpty(model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
     */
    public void visit(AeMessageExchangesDef def) {
        if (!def.isImplict() && def.getMessageExchangeDefs().hasNext()) {
            AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_MESSAGE_EXCHANGES, def);
            model.setDisplayOutlineOnly(true);
            traverse(def, model);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
     */
    public void visit(AeMessageExchangeDef def) {
        AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_MESSAGE_EXCHANGE, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
        model.setName(def.getName());
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
     */
    public void visit(AeOnMessageDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_ON_MESSAGE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
     */
    public void visit(AeOnEventDef def) {
        if (isBpelVersion11()) {
            visit((AeOnMessageDef) def);
        } else {
            AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_ON_EVENT, IAeBPELConstants.TAG_ON_MESSAGE, def);
            traverse(def, model);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
     */
    public void visit(AeOnAlarmDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_ON_ALARM, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
     */
    public void visit(AeActivitySequenceDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_SEQUENCE, def);
        traverse(def, model);
    }


    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
     */
    public void visit(AeActivityExitDef def) {
        String tag = isBpelVersion11() ? IAeBpelLegacyConstants.TAG_TERMINATE : IAeBPELConstants.TAG_EXIT;
        AeBpelActivityObject model = new AeBpelActivityObject(tag, IAeBpelLegacyConstants.TAG_TERMINATE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
     */
    public void visit(AeActivityThrowDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_THROW, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
     */
    public void visit(AeActivityRethrowDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_RETHROW, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
     */
    public void visit(AeActivityValidateDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_VALIDATE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
     */
    public void visit(AeActivityWaitDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_WAIT, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
     */
    public void visit(AeActivityWhileDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_WHILE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
     */
    public void visit(AeActivityRepeatUntilDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_REPEAT_UNTIL, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
     */
    public void visit(AeActivityForEachDef def) {
        AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_FOREACH, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
     */
    public void visit(AeActivityIfDef def) {
        // Note: BPEL 1.1 <switch> is mapped to a BPEL 2.x <if> activity.
        // The first BPEL 1.1 <case> construct is now part of the BPEL 2.x <if>.
        // Create the 'container' <switch>
        AeBpelActivityObject model = null;
        if (isBpelVersion11()) {
            model = new AeBpelActivityObject(IAeBpelLegacyConstants.TAG_SWITCH, def);
            traverse(def, model);
        } else {
            model = new AeBpelActivityObject(IAeBPELConstants.TAG_IF, IAeBpelLegacyConstants.TAG_SWITCH, def);
            // the controller type of <if> is not the same as the tag name 'if' since it
            // conflicts with a  <if> definition [which is  a choice part (child) of the main if-else-elsif container].
            //
            // this model is basically the container model to hold the if, elseif and else choice parts.
            model.setControllerType("ifelse"); //$NON-NLS-1$
            traverse(def, model);
        }
    }


    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
     */
    public void visit(AeIfDef def) {
        String tag = isBpelVersion11() ? IAeBpelLegacyConstants.TAG_CASE : IAeBPELConstants.TAG_IF;
        AeBpelObjectContainer model = new AeBpelObjectContainer(tag, IAeBpelLegacyConstants.TAG_CASE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
     */
    public void visit(AeElseIfDef def) {
        // Note: BPEL 1.1 <case> activities are  mapped to a BPEL 2.x <elseif> activity,
        // except for the very 1st <case> construct found in the <switch> - which is mapped in the  BPEL 2.x <if>.
        String tag = isBpelVersion11() ? IAeBpelLegacyConstants.TAG_CASE : IAeBPELConstants.TAG_ELSEIF;
        AeBpelObjectContainer model = new AeBpelObjectContainer(tag, IAeBpelLegacyConstants.TAG_CASE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
     */
    public void visit(AeElseDef def) {
        // Note: BPEL 1.1 <otherwise> activities are  mapped to a BPEL 2.x <else> activity,
        String tag = isBpelVersion11() ? IAeBpelLegacyConstants.TAG_OTHERWISE : IAeBPELConstants.TAG_ELSE;
        AeBpelObjectContainer model = new AeBpelObjectContainer(tag, IAeBpelLegacyConstants.TAG_OTHERWISE, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
     */
    public void visit(AePartnerDef def) {
        AeBpelObjectBase model = new AeBpelObjectBase(IAeBpelLegacyConstants.TAG_PARTNER, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
     */
    public void visit(AePartnerLinkDef def) {
        // TODO: refactor common code to a base class.
        AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_PARTNER_LINK, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
     */
    public void visit(AeScopeDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
     */
    public void visit(AeAssignCopyDef def) {
        AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_COPY, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
     */
    public void visit(AeCorrelationDef def) {
        AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_CORRELATION, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinkDef)
     */
    public void visit(AeLinkDef def) {
        // create new link model.
        AeBpelLinkObject linkModel = new AeBpelLinkObject(def);
        linkModel.setLocationPath(def.getLocationPath());
        linkModel.setName(getDefName(def));
        setState(def, linkModel);
        addLink(linkModel);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
     */
    public void visit(AeSourceDef def) {
        // find the link.
        AeBpelLinkObject linkModel = getLink(def.getLinkName());
        linkModel.setCondition(def.getTransitionCondition());
        // add the link and associate it with the source.
        getActivityModel().addChild(linkModel);
        getActivityModel().addSourceLink(linkModel);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
     */
    public void visit(AeTargetDef def) {
        // find the link and associate it with the target.
        AeBpelLinkObject linkModel = getLink(def.getLinkName());
        getActivityModel().addTargetLink(linkModel);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinksDef)
     */
    public void visit(AePartnerLinksDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_PARTNER_LINKS, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
     */
    public void visit(AePartnersDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBpelLegacyConstants.TAG_PARTNERS, def);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinksDef)
     */
    public void visit(AeLinksDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
     */
    public void visit(AeCorrelationsDef def) {
        AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_CORRELATIONS, def);
        model.setDisplayOutlineOnly(true);
        traverse(def, model);
    }

    /**
     * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
     */
    public void visit(AeSourcesDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
     */
    public void visit(AeTargetsDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(AeChildExtensionActivityDef)
     */
    public void visit(AeChildExtensionActivityDef def) {
        QName elemName = def.getElementName();
        // icon name is same as element name. e.g. peopleActivity.png.
        String iconName = elemName.getLocalPart();
        AeBpelActivityObject model = new AeBpelActivityObject(elemName.getLocalPart(), iconName, def);
        IAeXmlDefGraphNodeAdapter adapter = null;
        if (def.getExtensionObject() != null) {
            adapter = (IAeXmlDefGraphNodeAdapter) def.getExtensionObject().getAdapter(IAeXmlDefGraphNodeAdapter.class);
            if (adapter != null) {
                iconName = adapter.getIcon() != null ? adapter.getIcon() : iconName;
                model.setIconName(iconName);
                model.setAdapter(adapter);
            }
        }
        traverse(def, model);

        if (null != adapter && null != adapter.getTreeNode()) {
            pushModel(model);
            buildTree(adapter.getTreeNode(), adapter);
            popModel();
        }
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
     */
    public void visit(AeExtensionActivityDef def) {
        traverse(def, null);
    }

    /**
     * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
     */
    public void visit(AeExtensionElementDef aDef) {
        if (aDef.getExtensionObject() != null) {
            IAeXmlDefGraphNodeAdapter adapter = (IAeXmlDefGraphNodeAdapter) aDef.getExtensionObject().getAdapter(IAeXmlDefGraphNodeAdapter.class);
            if (adapter != null)
                buildTree(adapter.getTreeNode(), adapter);
        }
    }

    /**
     * Builds the visual model given the graph root node.
     *
     * @param aNode
     */
    protected void buildTree(IAeXmlDefGraphNode aNode, IAeXmlDefGraphNodeAdapter aAdapter) {
        List<IAeXmlDefGraphNode> children = aNode.getChildren();
        AeBpelObjectBase visualModel = null;
        AeBaseXmlDef def = aNode.getDef();

        // Default icon if unknowActivity.png for canvas and unknownActivity.gif for outline on web console
        String icon = "unknownActivity"; //$NON-NLS-1$
        if (AeUtil.notNullOrEmpty(aNode.getIcon())) {
            icon = aNode.getIcon();
        }
        if (children.size() > 0) {
            visualModel = new AeBpelObjectContainer(aNode.getName(), icon, def, aNode.isDisplayOutlineOnly());
        } else {
            visualModel = new AeBpelObjectBase(aNode.getName(), icon, def, aNode.getDisplayName(), def.getLocationPath(), aNode.isDisplayOutlineOnly());
        }
        visualModel.setAdapter(aAdapter);
        // init model parameters and add it to the parent.
        initModel(def, visualModel);
        visualModel.setName(aNode.getDisplayName());
        visualModel.setLocationPath(def.getLocationPath());
        pushModel(visualModel);
        Iterator<IAeXmlDefGraphNode> it = children.iterator();
        while (it.hasNext()) {
            buildTree(it.next(), aAdapter);
        }
        popModel();
    }

}
