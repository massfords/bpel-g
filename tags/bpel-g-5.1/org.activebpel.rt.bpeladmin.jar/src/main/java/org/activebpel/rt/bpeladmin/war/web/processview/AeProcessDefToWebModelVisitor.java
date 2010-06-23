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
public class AeProcessDefToWebModelVisitor extends AeProcessDefToWebVisitorBase
{
   /** Used in conjunction with the traversal object to traverse the object model */
   protected IAeDefVisitor mTraversalVisitor;

   /** Visual model used to represent a business process impl. */
   private AeBpelProcessObject mBpelProcessModel;

   /** Stack for the current visual model parent. */
   private Stack mStack;

   /** Stack for the holding links on per Flow basis. */
   private Stack mLinkContainerStack;

   /** List of links visited */
   private List mLinksList;

   /** Process def */
   private AeProcessDef mProcessDef;

   /**
    * Ctor.
    * @param aProcessDef process definition.
    */
   public AeProcessDefToWebModelVisitor(AeProcessDef aProcessDef)
   {
      setProcessDef(aProcessDef);
      mStack = new Stack();
      mLinkContainerStack = new Stack();
      init();
   }

   /**
    * Build the web visual model by visiting the process definition.
    */
   public void startVisiting()
   {
      visit(getProcessDef());
   }

   /**
    * @return Returns the processDef.
    */
   protected AeProcessDef getProcessDef()
   {
      return mProcessDef;
   }
   /**
    * @param aProcessDef The processDef to set.
    */
   protected void setProcessDef(AeProcessDef aProcessDef)
   {
      mProcessDef = aProcessDef;
   }

   /**
    * Creates the traversal and visitor object we're using to visit the object
    * model.
    */
   protected void init()
   {
      mTraversalVisitor = new AeTraversalVisitor(new AeDefTraverser(), this);
   }

   /**
    * Returns list containt AeLinkImpl objects that were visited.
    */
   protected List getLinksList()
   {
      if (mLinksList == null)
      {
         mLinksList = new ArrayList();
      }
      return mLinksList;
   }

   /**
    * @return Returns the bpelProcess.
    */
   public AeBpelProcessObject getBpelProcessModel()
   {
      return mBpelProcessModel;
   }

   /**
    * Sets the process visual model.
    * @param aBpelProcess The bpelProcess to set.
    */
   protected void setBpelProcessModel(AeBpelProcessObject aBpelProcess)
   {
      mBpelProcessModel = aBpelProcess;
   }

   /**
    * Pops the current visual model parent from the stack.
    */
   protected AeBpelObjectBase popModel()
   {
      return (AeBpelObjectBase) mStack.pop();
   }

   /**
    * Pushes the current visual model parent to the stack.
    */
   protected void pushModel(AeBpelObjectBase aBpelObject)
   {
      mStack.push(aBpelObject);
   }

   /**
    * Returns the current model from the stack via peek.
    */
   protected AeBpelObjectBase getModel()
   {
      return (AeBpelObjectBase) mStack.peek();
   }

   /**
    * Type safe peek at the current stack.
    * @return bpel activity model.
    */
   protected AeBpelActivityObject getActivityModel()
   {
      return (AeBpelActivityObject) mStack.peek();
   }

   /**
    * Creates a new map and pushes it into the links container stack. This is normally
    * done when a flow is visited.
    */
   protected void pushLinksContainer()
   {
      Map map = new HashMap();
      mLinkContainerStack.push(map);
   }

   /**
    * Pops the map containing the links in the current flow.
    * @return map containing links in the current flow.
    */
   protected Map popLinksContainer()
   {
      return (Map) mLinkContainerStack.pop();
   }

   /**
    * @return the map containing links in the current flow.
    */
   protected Map getLinksContainerMap()
   {
      return (Map) mLinkContainerStack.peek();
   }

   /**
    * Adds a link to the process model.
    * @param aLinkModel
    */
   protected void addLink(AeBpelLinkObject aLinkModel)
   {
      getLinksList().add(aLinkModel);
      getLinksContainerMap().put(aLinkModel.getName(), aLinkModel);
      // add to the root process model.
      getBpelProcessModel().addLink(aLinkModel);
      getBpelProcessModel().addWebModel(aLinkModel);
   }

   /**
    * Returns the link given its name. The link is searched by name by walking up the Flow
    * hierarchy.
    * @param aName name of link.
    * @return link if found or null otherwise.
    */
   protected AeBpelLinkObject getLink(String aName)
   {
      AeBpelLinkObject rVal = null;
      int size = mLinkContainerStack.size();
      for(int i=size-1; i >=0 && rVal==null; i--)
      {
         rVal = (AeBpelLinkObject) ((Map)mLinkContainerStack.get(i)).get(aName);
      }
      return rVal;
   }

   /**
    * Convenience method that returns the def name if available otherwise returns empty string.
    * @param aDef bpel definition
    * @return name of bpel object or empty string if not available.
    */
   protected String getDefName(AeBaseXmlDef aDef)
   {
      String rVal = ""; //$NON-NLS-1$
      if (aDef instanceof AeNamedDef)
      {
         AeNamedDef namedDef = (AeNamedDef)aDef;
         rVal = namedDef.getName();
      }
      return rVal;
   }

   /**
    * Adds the visiual model to the current parent and initializes the visual model with the
    * definition name and location path.
    * @param aDef def model.
    * @param aBpelObject visual model
    */
   protected void initModel(AeBaseXmlDef aDef, AeBpelObjectBase aBpelObject)
   {
      aBpelObject.setLocationPath(aDef.getLocationPath());
      aBpelObject.setName( getDefName(aDef) );
      // FIXMEPJ process object model was being visited twice and getting its state overwritten. Should only visit once.
      if (AeUtil.isNullOrEmpty(aBpelObject.getState()))
         setState(aDef, aBpelObject);
      addModel(aBpelObject);
   }

   /**
    * Adds the given visual model to the current parent.
    * @param aBpelObject
    */
   protected void addModel(AeBpelObjectBase aBpelObject)
   {
      if (aBpelObject != getBpelProcessModel() && getModel() instanceof AeBpelObjectContainer)
      {
         // add child
         ( (AeBpelObjectContainer)getModel()).addChild(aBpelObject);
         getBpelProcessModel().addWebModel(aBpelObject);
      }
   }

   /**
    * Sets visual model state given its corresponding def.
    * @param aDef bpel definition.
    * @param aBpelObject web visual model.
    */
   protected void setState(AeBaseXmlDef aDef, AeBpelObjectBase aBpelObject)
   {
      // no-op. subclasses which uses the state document may override this method to reflect the current state.
   }

   /**
    * Sets process visual model state given its corresponding def.
    * @param aDef bpel process definition.
    * @param aBpelObject web visual model.
    */
   protected void setState(AeProcessDef aDef, AeBpelProcessObject aBpelObject)
   {
      // no-op. subclasses which uses the state document may override this method to reflect the current state.
   }

   /**
    * Sets link visual model state given its corresponding def.
    * @param aDef bpel link definition.
    * @param aBpelObject web visual model.
    */
   protected void setState(AeLinkDef aDef, AeBpelLinkObject aBpelObject)
   {
      // no-op. subclasses which uses the state document may override this method to reflect the current state.
   }

   /**
    * Pushes the visual model onto the stack so it will be the parent for any
    * other activities created during the execution of this method. Then the
    * def is visited which may call back into this class with other visit methods.
    * Finally, we pop from the stack to restore the previous parent.
    * @param aDef
    * @param aVisualModel
    */
   protected void traverse(AeBaseXmlDef aDef, AeBpelObjectBase aVisualModel)
   {
      // No need to bother with stack if we have no parent object
      if (aVisualModel == null)
      {
         aDef.accept(mTraversalVisitor);
      }
      else
      {
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
    * @param aModel
    */
   protected void checkEmpty(AeBpelObjectContainer aModel)
   {
     // TODO (PJ) remove this method when defect wrt visting empty def is fixed.
      if (aModel.size() == 0 && aModel.getParent() != null)
      {
         ((AeBpelObjectContainer)aModel.getParent()).removeChild(aModel);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      super.visit(aDef);
      AeBpelProcessObject bpelProcess = new AeBpelProcessObject(aDef, getDefName(aDef), aDef.getLocationPath());
      setBpelProcessModel(bpelProcess);
      setState(aDef, bpelProcess);
      traverse(aDef, bpelProcess);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef)
    */
   public void visit(AeActivityAssignDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_ASSIGN, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef)
    */
   public void visit(AeActivityCompensateDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_COMPENSATE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef)
    */
   public void visit(AeActivityCompensateScopeDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(isBpelVersion11() ? IAeBPELConstants.TAG_COMPENSATE : IAeBPELConstants.TAG_COMPENSATE_SCOPE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef)
    */
   public void visit(AeActivityEmptyDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_EMPTY, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef)
    */
   public void visit(AeActivityFlowDef aDef)
   {
      // create a new map (and push it into the stack) to hold the links in this container.
      pushLinksContainer();
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_FLOW, aDef);
      traverse(aDef, model);
      // pop the links container map.
      popLinksContainer();
   }

   /**
    * Overrides method to
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_INVOKE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
    */
   public void visit(AeActivityPickDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_PICK, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_RECEIVE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_REPLY, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
    */
   public void visit(AeActivitySuspendDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_SUSPEND, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
    */
   public void visit(AeActivityScopeDef aDef)
   {
      AeBpelScopeObject model = new AeBpelScopeObject(aDef);
      if (isPeopleActivity(aDef))
      {
         model.setIconName("peopleActivity"); //$NON-NLS-1$
         model.setTagName("People"); //$NON-NLS-1$
      }
      traverse(aDef, model);
   }

   /**
    * Returns true if the scope is a people activity container.
    * @param aDef people actitvity scope
    * @return true if scope has extension attribute identfying it as a people activity.
    */
   protected boolean isPeopleActivity(AeActivityScopeDef aDef)
   {
      Iterator it = aDef.getExtensionAttributeDefs().iterator();
      while (it.hasNext() )
      {
         AeExtensionAttributeDef attrib = (AeExtensionAttributeDef) it.next();
         if ("http://www.activebpel.org/2006/09/bpel/extension/peopleActivity".equals( attrib.getNamespace()) //$NON-NLS-1$
               && "people-activity".equals(AeXmlUtil.extractLocalPart(attrib.getQualifiedName())) ) //$NON-NLS-1$
         {
            return true;
         }
      }
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
    */
   public void visit(AeActivityContinueDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_CONTINUE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
    */
   public void visit(AeActivityBreakDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_BREAK, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetDef)
    */
   public void visit(AeCorrelationSetDef aDef)
   {
      AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_CORRELATION_SET, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchDef)
    */
   public void visit(AeCatchDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_CATCH, aDef);
      traverse(aDef, model);
      model.setName( getLocalName(aDef.getFaultName()) );
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCatchAllDef)
    */
   public void visit(AeCatchAllDef aDef)
   {
      // catchAll
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_CATCH_ALL, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeTerminationHandlerDef)
    */
   public void visit(AeTerminationHandlerDef aDef)
   {
      AeBpelTerminationHandlerObject model = new AeBpelTerminationHandlerObject(aDef);
      traverse(aDef, model);
      checkEmpty(model);
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariableDef)
    */
   public void visit(AeVariableDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_VARIABLE, aDef);
      model.setDisplayOutlineOnly(true);
      getBpelProcessModel().addVariablePath(model.getLocationPath());
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeVariablesDef)
    */
   public void visit(AeVariablesDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_VARIABLES, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeEventHandlersDef)
    */
   public void visit(AeEventHandlersDef aDef)
   {
      AeBpelEventHandlersObject model = new AeBpelEventHandlersObject(aDef);
      traverse(aDef, model);
      checkEmpty(model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCompensationHandlerDef)
    */
   public void visit(AeCompensationHandlerDef aDef)
   {
      AeBpelCompensationHandlerObject model = new AeBpelCompensationHandlerObject(aDef);
      traverse(aDef, model);
      checkEmpty(model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationSetsDef)
    */
   public void visit(AeCorrelationSetsDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_CORRELATION_SETS, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef)
    */
   public void visit(AeFromPartDef aDef)
   {
      AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_FROM_PART, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
      //override name with part
      model.setName(aDef.getPart());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef)
    */
   public void visit(AeFromPartsDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_FROM_PARTS, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartDef)
    */
   public void visit(AeToPartDef aDef)
   {
      AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_TO_PART, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
      //override name with part
      model.setName(aDef.getPart());

   }
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef)
    */
   public void visit(AeToPartsDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_TO_PARTS, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeFaultHandlersDef)
    */
   public void visit(AeFaultHandlersDef aDef)
   {
      AeBpelFaultHandlersObject model = new AeBpelFaultHandlersObject(aDef);
      traverse(aDef, model);
      checkEmpty(model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangesDef)
    */
   public void visit(AeMessageExchangesDef aDef)
   {
      if (!aDef.isImplict() && aDef.getMessageExchangeDefs().hasNext() )
      {
         AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_MESSAGE_EXCHANGES, aDef);
         model.setDisplayOutlineOnly(true);
         traverse(aDef, model);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeMessageExchangeDef)
    */
   public void visit(AeMessageExchangeDef aDef)
   {
      AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_MESSAGE_EXCHANGE, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
      model.setName(aDef.getName());
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_ON_MESSAGE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      if (isBpelVersion11())
      {
         visit( (AeOnMessageDef) aDef);
      }
      else
      {
         AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_ON_EVENT, IAeBPELConstants.TAG_ON_MESSAGE, aDef);
         traverse(aDef, model);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_ON_ALARM, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef)
    */
   public void visit(AeActivitySequenceDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_SEQUENCE, aDef);
      traverse(aDef, model);
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityExitDef)
    */
   public void visit(AeActivityExitDef aDef)
   {
      String tag = isBpelVersion11() ? IAeBpelLegacyConstants.TAG_TERMINATE : IAeBPELConstants.TAG_EXIT;
      AeBpelActivityObject model = new AeBpelActivityObject(tag, IAeBpelLegacyConstants.TAG_TERMINATE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef)
    */
   public void visit(AeActivityThrowDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_THROW, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef)
    */
   public void visit(AeActivityRethrowDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_RETHROW, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef)
    */
   public void visit(AeActivityValidateDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_VALIDATE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_WAIT, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_WHILE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef)
    */
   public void visit(AeActivityRepeatUntilDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_REPEAT_UNTIL, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
    */
   public void visit(AeActivityForEachDef aDef)
   {
      AeBpelActivityObject model = new AeBpelActivityObject(IAeBPELConstants.TAG_FOREACH, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.AeActivityIfDef)
    */
   public void visit(AeActivityIfDef aDef)
   {
      // Note: BPEL 1.1 <switch> is mapped to a BPEL 2.x <if> activity.
      // The first BPEL 1.1 <case> construct is now part of the BPEL 2.x <if>.
      // Create the 'container' <switch>
      AeBpelActivityObject model = null;
      if ( isBpelVersion11() )
      {
         model = new AeBpelActivityObject(IAeBpelLegacyConstants.TAG_SWITCH, aDef);
         traverse(aDef, model);
      }
      else
      {
         model = new AeBpelActivityObject(IAeBPELConstants.TAG_IF, IAeBpelLegacyConstants.TAG_SWITCH, aDef);
         // the controller type of <if> is not the same as the tag name 'if' since it
         // conflicts with a  <if> definition [which is  a choice part (child) of the main if-else-elsif container].
         //
         // this model is basically the container model to hold the if, elseif and else choice parts.
         model.setControllerType("ifelse"); //$NON-NLS-1$
         traverse(aDef, model);
      }
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
      String tag = isBpelVersion11() ? IAeBpelLegacyConstants.TAG_CASE : IAeBPELConstants.TAG_IF;
      AeBpelObjectContainer model = new AeBpelObjectContainer(tag, IAeBpelLegacyConstants.TAG_CASE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      // Note: BPEL 1.1 <case> activities are  mapped to a BPEL 2.x <elseif> activity,
      // except for the very 1st <case> construct found in the <switch> - which is mapped in the  BPEL 2.x <if>.
      String tag = isBpelVersion11() ? IAeBpelLegacyConstants.TAG_CASE : IAeBPELConstants.TAG_ELSEIF;
      AeBpelObjectContainer model = new AeBpelObjectContainer(tag, IAeBpelLegacyConstants.TAG_CASE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeElseDef)
    */
   public void visit(AeElseDef aDef)
   {
      // Note: BPEL 1.1 <otherwise> activities are  mapped to a BPEL 2.x <else> activity,
      String tag = isBpelVersion11() ? IAeBpelLegacyConstants.TAG_OTHERWISE : IAeBPELConstants.TAG_ELSE;
      AeBpelObjectContainer model = new AeBpelObjectContainer(tag, IAeBpelLegacyConstants.TAG_OTHERWISE, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerDef)
    */
   public void visit(AePartnerDef aDef)
   {
      AeBpelObjectBase model = new AeBpelObjectBase(IAeBpelLegacyConstants.TAG_PARTNER, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void visit(AePartnerLinkDef aDef)
   {
      // TODO: refactor common code to a base class.
      AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_PARTNER_LINK, aDef);
      model.setDisplayOutlineOnly(true);      
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeScopeDef)
    */
   public void visit(AeScopeDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef)
    */
   public void visit(AeAssignCopyDef aDef)
   {
      AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_COPY, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef)
    */
   public void visit(AeCorrelationDef aDef)
   {
      AeBpelObjectBase model = new AeBpelObjectBase(IAeBPELConstants.TAG_CORRELATION, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinkDef)
    */
   public void visit(AeLinkDef aDef)
   {
      // create new link model.
      AeBpelLinkObject linkModel = new AeBpelLinkObject(aDef);
      linkModel.setLocationPath(aDef.getLocationPath());
      linkModel.setName( getDefName(aDef) );
      setState(aDef, linkModel);
      addLink(linkModel);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
    */
   public void visit(AeSourceDef aDef)
   {
      // find the link.
      AeBpelLinkObject linkModel = getLink(aDef.getLinkName());
      linkModel.setCondition(aDef.getTransitionCondition());
      // add the link and associate it with the source.
      getActivityModel().addChild(linkModel);
      getActivityModel().addSourceLink(linkModel);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetDef)
    */
   public void visit(AeTargetDef aDef)
   {
      // find the link and associate it with the target.
      AeBpelLinkObject linkModel = getLink(aDef.getLinkName());
      getActivityModel().addTargetLink(linkModel);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnerLinksDef)
    */
   public void visit(AePartnerLinksDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_PARTNER_LINKS, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AePartnersDef)
    */
   public void visit(AePartnersDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBpelLegacyConstants.TAG_PARTNERS, aDef);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeLinksDef)
    */
   public void visit(AeLinksDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void visit(AeCorrelationsDef aDef)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(IAeBPELConstants.TAG_CORRELATIONS, aDef);
      model.setDisplayOutlineOnly(true);
      traverse(aDef, model);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef)
    */
   public void visit(AeSourcesDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebVisitorBase#visit(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef)
    */
   public void visit(AeTargetsDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      QName elemName = aDef.getElementName();
      // icon name is same as element name. e.g. peopleActivity.png.
      String iconName = elemName.getLocalPart();
      AeBpelActivityObject model = new AeBpelActivityObject(elemName.getLocalPart(), iconName, aDef);
      IAeXmlDefGraphNodeAdapter adapter = null; 
      if (aDef.getExtensionObject() != null)
      {
         adapter = (IAeXmlDefGraphNodeAdapter) aDef.getExtensionObject().getAdapter(IAeXmlDefGraphNodeAdapter.class);
         if (adapter != null)
         {
            iconName = adapter.getIcon() != null ? adapter.getIcon() : iconName;
            model.setIconName(iconName);
            model.setAdapter(adapter);
         }
      }
      traverse(aDef, model);

      if (null != adapter && null != adapter.getTreeNode())
      {
         pushModel(model);
         buildTree(adapter.getTreeNode(), adapter);
         popModel();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeExtensionActivityDef)
    */
   public void visit(AeExtensionActivityDef aDef)
   {
      traverse(aDef, null);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.xml.def.AeExtensionElementDef)
    */
   public void visit(AeExtensionElementDef aDef)
   {
      if (aDef.getExtensionObject() != null)
      {
         IAeXmlDefGraphNodeAdapter adapter = (IAeXmlDefGraphNodeAdapter) aDef.getExtensionObject().getAdapter(IAeXmlDefGraphNodeAdapter.class);
         if (adapter != null)
            buildTree(adapter.getTreeNode(), adapter);
      }
   }

   /**
    * Builds the visual model given the graph root node.
    * @param aNode
    */
   protected void buildTree(IAeXmlDefGraphNode aNode, IAeXmlDefGraphNodeAdapter aAdapter)
   {
      List children = aNode.getChildren();
      AeBpelObjectBase visualModel = null;
      AeBaseXmlDef def = aNode.getDef();

      // Default icon if unknowActivity.png for canvas and unknownActivity.gif for outline on web console
      String icon = "unknownActivity"; //$NON-NLS-1$
      if (AeUtil.notNullOrEmpty(aNode.getIcon()))
      {
         icon = aNode.getIcon();
      }
      if (children.size() > 0)
      {
         visualModel = new AeBpelObjectContainer(aNode.getName(), icon, def, aNode.isDisplayOutlineOnly());
      }
      else
      {
         visualModel = new AeBpelObjectBase(aNode.getName(), icon, def, aNode.getDisplayName(), def.getLocationPath(), aNode.isDisplayOutlineOnly());
      }
      visualModel.setAdapter(aAdapter);
      // init model parameters and add it to the parent.
      initModel(def, visualModel);
      visualModel.setName(aNode.getDisplayName());
      visualModel.setLocationPath(def.getLocationPath());
      pushModel(visualModel);
      Iterator it = children.iterator();
      while (it.hasNext())
      {
         IAeXmlDefGraphNode child = (IAeXmlDefGraphNode) it.next();
         buildTree(child, aAdapter);
      }
      popModel();
   }

}
