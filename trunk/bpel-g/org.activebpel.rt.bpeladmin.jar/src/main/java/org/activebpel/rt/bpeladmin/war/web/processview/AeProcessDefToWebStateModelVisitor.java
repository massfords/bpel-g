//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeProcessDefToWebStateModelVisitor.java,v 1.17 2008/02/17 21:43:06 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.util.HashSet;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCorrelationsDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.support.AeLinkDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.io.IAeBpelLegacyConstants;
import org.activebpel.rt.bpel.def.util.AeLocationPathUtils;
import org.activebpel.rt.bpel.def.visitors.AeDefVisitorFactory;
import org.activebpel.rt.bpel.def.visitors.AeDynamicInstancePathBuilder;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Visitor responsible for building the web visual model and its state by visiting the BPEL definitions.
 */
public class AeProcessDefToWebStateModelVisitor extends AeProcessDefToWebModelVisitor
{
   /** Non-activity models that should not include default states.*/
   protected static final Set SKIP_STATE_PROPERTY = new HashSet();
   static
   {
      SKIP_STATE_PROPERTY.add(IAeBPELConstants.TAG_VARIABLE);
      SKIP_STATE_PROPERTY.add(IAeBPELConstants.TAG_VARIABLES);
      SKIP_STATE_PROPERTY.add(IAeBPELConstants.TAG_PARTNER_LINKS);
      SKIP_STATE_PROPERTY.add(IAeBPELConstants.TAG_PARTNER_LINK);
      SKIP_STATE_PROPERTY.add(IAeBpelLegacyConstants.TAG_PARTNERS);
      SKIP_STATE_PROPERTY.add(IAeBPELConstants.TAG_CORRELATIONS);
      SKIP_STATE_PROPERTY.add(IAeBPELConstants.TAG_CORRELATION);
      SKIP_STATE_PROPERTY.add(IAeBPELConstants.TAG_CORRELATION_SET);
      SKIP_STATE_PROPERTY.add(IAeBPELConstants.TAG_CORRELATION_SETS);
      SKIP_STATE_PROPERTY.add(IAeBPELConstants.TAG_COPY);
   }
   /** Indicates if a ParallelForEach should be flattened to a single instance. */
   private boolean mForEachParallelSingleInstance;

   /** Location path of the currently selected ForEachParallel tree. */
   private String mPivotPath = null;

   /**
    * Flag that indicates visits to a concurrent container.
    */
   private int mVisitingCocurrentContainerCount;

   /**
    * Ctor.
    * @param aProcessDef process definition.
    * @param aStateDoc state xml document.
    * @param aForEachParallelSingleInstance
    */
   public AeProcessDefToWebStateModelVisitor(AeProcessDef aProcessDef, Document aStateDoc, boolean aForEachParallelSingleInstance)
   {
      super(aProcessDef);
      setStateDocument(aStateDoc);
      setForEachParallelSingleInstance(aForEachParallelSingleInstance);
   }

   /**
    * @return Returns the forEachParallelSingleInstance.
    */
   protected boolean isForEachParallelSingleInstance()
   {
      return mForEachParallelSingleInstance;
   }

   /**
    * @param aForEachParallelSingleInstance The forEachParallelSingleInstance to set.
    */
   protected void setForEachParallelSingleInstance(boolean aForEachParallelSingleInstance)
   {
      mForEachParallelSingleInstance = aForEachParallelSingleInstance;
   }

   /**
    * @return Returns the pivotPath.
    */
   public String getPivotPath()
   {
      return mPivotPath;
   }

   /**
    * @param aPivotPath The pivotPath to set.
    */
   public void setPivotPath(String aPivotPath)
   {
      mPivotPath = aPivotPath;
   }

   /**
    * Increases the reference count when a concurrent container is visited.
    */
   protected void incVisitingCocurrentContainerCount()
   {
      mVisitingCocurrentContainerCount++;
   }

   /**
    * Decreases the reference count when leaving a concurrent container.
    */
   protected void decVisitingCocurrentContainerCount()
   {
      mVisitingCocurrentContainerCount--;
   }

   /**
    * @return true if the current container visit reference count is greater than zero.
    */
   protected boolean isChildofConcurrentContainer()
   {
      return mVisitingCocurrentContainerCount > 0;
   }

   /**
    * Returns the state instance count for concurrent onEvent or onAlarm.
    * @param aDef
    * @return instance count value or 0 if not found.
    */
   protected int getInstanceCount(AeBaseDef aDef)
   {
      int instanceCount = 0;
      try
      {
         Element ele = selectSingleElement(aDef);
         if (ele != null && AeUtil.notNullOrEmpty( ele.getAttribute(IAeImplStateNames.STATE_INSTANCE_COUNT) ) )
         {
            instanceCount = Integer.parseInt( ele.getAttribute(IAeImplStateNames.STATE_INSTANCE_COUNT) );
         }
      }
      catch(Exception e)
      {
         AeException.logError(e, e.getMessage());
      }
      return instanceCount;
   }

   /**
    * Overrides method to account for duplicate visits in forEach parallel or concurrent onEvent/onAlarm containers.
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebModelVisitor#visit(org.activebpel.rt.bpel.def.AeCorrelationsDef)
    */
   public void visit(AeCorrelationsDef aDef)
   {
      // visit only if not already visited.
      if (getBpelProcessModel() != null && getBpelProcessModel().getWebModel(aDef.getLocationPath()) == null)
      {
         super.visit(aDef);
      }
   }

   /**
    * Overrides method to account for concurrent onAlarms instances.
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebModelVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      // get the instance counter from the state document.
      int instanceCount = getInstanceCount(aDef);
      if (instanceCount < 1 || isBpelVersion11() )
      {
         // no non-concurrent version
         super.visit(aDef);
      }
      else
      {
         AeActivityScopeDef childScopeDef = aDef.getChildScope();
         visitConcurrentDef(aDef, childScopeDef, instanceCount, IAeBPELConstants.TAG_ON_EVENT, IAeBPELConstants.TAG_ON_MESSAGE);
      }
   }

   /**
    *
    * Overrides method to
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebModelVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      // get the instance counter from the state document.
      int instanceCount = getInstanceCount(aDef);
      if (instanceCount < 1 || isBpelVersion11() )
      {
         // no non-concurrent version
         super.visit(aDef);
      }
      else
      {
         AeActivityScopeDef childScopeDef = (AeActivityScopeDef) aDef.getActivityDef();
         visitConcurrentDef(aDef, childScopeDef, instanceCount, IAeBPELConstants.TAG_ON_ALARM, IAeBPELConstants.TAG_ON_ALARM);
      }
   }


   /**
    * Common entry point for concurrent onEvent or onAlarm def.
    * @param aContainerDef
    * @param aChildScopePrototypeDef
    * @param aInstanceCount
    * @param aContainerTagName
    * @param aContainerIconName
    */
   protected void visitConcurrentDef(AeBaseDef aContainerDef, AeActivityScopeDef aChildScopePrototypeDef, int aInstanceCount, String aContainerTagName, String aContainerIconName)
   {
      AeBpelObjectContainer model = new AeBpelObjectContainer(aContainerTagName, aContainerIconName, aContainerDef);
      buildParallelScopeContainer(aContainerDef, aChildScopePrototypeDef, model, 1, aInstanceCount);
   }

   /**
    * Overrides method to handle parallel for each.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
    */
   public void visit(AeActivityForEachDef aDef)
   {
      // if the forEach activity is parallel, then create (scope) models for each instance
      // of the parallel forEach.
      if (aDef.isParallel())
      {
         // get the instance counter start and final values from the state document.
         int forEachStart = 1;
         int forEachFinal = 1;
         int forEachInstance = 1;
         try
         {
            Element ele = selectSingleElement(aDef);
            if (ele != null)
            {
               forEachStart = Integer.parseInt( ele.getAttribute(IAeImplStateNames.STATE_FOREACH_START) );
               forEachFinal = Integer.parseInt( ele.getAttribute(IAeImplStateNames.STATE_FOREACH_FINAL) );
               forEachInstance = Integer.parseInt( ele.getAttribute(IAeImplStateNames.STATE_INSTANCE_VALUE) );
            }
         }
         catch(Exception e)
         {
            AeException.logError(e, e.getMessage());
         }

         int instanceStartValue = AeLocationPathUtils.getStartInstanceValue(forEachInstance, forEachStart, forEachFinal);
         int instanceFinalValue = AeLocationPathUtils.getFinalInstanceValue(forEachInstance, forEachStart, forEachFinal);

         // create forEach container model and initialize it with child instance scopes.
         if (instanceStartValue != -1 && instanceFinalValue != -1 && instanceStartValue <= instanceFinalValue)
         {
            AeBpelActivityObject forEachModel = new AeBpelActivityObject(AeActivityForEachDef.TAG_FOREACH, aDef);
            AeActivityScopeDef childScopeDef = aDef.getChildScope();
            buildParallelScopeContainer(aDef, childScopeDef, forEachModel, instanceStartValue, instanceFinalValue);
            return;
         }
      }
      // not a parallel forEach (or the loop was never executed) - let the base class handle the creation and traversal.
      super.visit(aDef);

   }

   /**
    * Visits and builds the "parallel instance" container (forEach, onEvent, onAlarm) and its child scope instances.
    * @param aContainerDef
    * @param aChildScopePrototypeDef
    * @param aContainerModel
    * @param aStartValue
    * @param aFinalValue
    */
   protected void buildParallelScopeContainer(AeBaseDef aContainerDef, AeActivityScopeDef aChildScopePrototypeDef,
         AeBpelObjectContainer aContainerModel, int aStartValue, int aFinalValue)
   {
      incVisitingCocurrentContainerCount();
      // if counter start value is -1, then the child scopes are not available.
      if (aStartValue == -1 || aChildScopePrototypeDef == null)
      {
         // TODO (PJ): build traverse or peek ahead to build links instead of traversing all the children.
         // call traverse so that the links are traversed and built.
         traverse(aContainerDef, aContainerModel);
         // remove child scope
         aContainerModel.getChildren().remove(aContainerModel.getChildren().size() - 1 );
      }
      else
      {
         initModel(aContainerDef, aContainerModel);
         pushModel(aContainerModel);

         // The name will be used for each instance of scopes that are created.
         String scopeName = getDefName(aChildScopePrototypeDef);

         //
         // if single instance, then include only a single child scope instance instead of all instances.
         //
         if (isForEachParallelSingleInstance())
         {
            // initialize the default instance counter to the first child.
            int instanceCount = aStartValue;
            if ( AeUtil.notNullOrEmpty( getPivotPath() ) )
            {
               // if the pivot path is given, then select scope instance that is part of the pivot path.
               StringBuffer scopeInstancePath = new StringBuffer();
               String scopeLocationPath = aChildScopePrototypeDef.getLocationPath();
               for (int i = aStartValue; i <= aFinalValue; i++)
               {
                  scopeInstancePath.setLength(0);
                  scopeInstancePath.append(scopeLocationPath);
                  // create new location paths based on the current instance counter value
                  AeDynamicInstancePathBuilder.appendInstancePredicate(scopeInstancePath, i);
                  // check to see if the pivot path falls under the child scope's path.
                  if ( getPivotPath().startsWith( scopeInstancePath.toString() ) )
                  {
                     instanceCount = i;
                     break;
                  }
               } // for
            } // end if pivotPath != null
            // add single child scope instance and all its children.
            buildParallelScopeChild(aContainerDef, aContainerModel, aChildScopePrototypeDef, scopeName, instanceCount);
         }
         else
         {
            // not single instance i.e. add all scope instance children
            for (int i = aStartValue; i <= aFinalValue; i++)
            {
               buildParallelScopeChild(aContainerDef, aContainerModel, aChildScopePrototypeDef, scopeName, i);
            } // for
         }

         // pop visual container model.
         popModel();

      }// end if else.

      decVisitingCocurrentContainerCount();
   }

   /**
    * Build a single child scope instance of a parallel forEach or concurrent onAlarm/onEvent.
    * @param aContainerDef
    * @param aContainerModel
    * @param aChildScopeDef
    * @param aChildName
    * @param aInstanceCount
    */
   protected void buildParallelScopeChild(AeBaseDef aContainerDef, AeBpelObjectContainer aContainerModel,
         AeActivityScopeDef aChildScopeDef, String aChildName, int aInstanceCount)
   {
      // create new location paths based on the current instance counter value
      buildInstanceLocationPaths(aContainerDef.getLocationPath(), aChildScopeDef, aInstanceCount);
      // add children (scope and all its children).
      super.traverse(aContainerDef, null);
      if (aContainerModel.getChildren().size() > 0)
      {
         // append the instance counter value to the instance scope's name.
         AeBpelScopeObject childScopeModel = (AeBpelScopeObject) aContainerModel.getChildren().get(aContainerModel.getChildren().size() - 1 );
         childScopeModel.setName(aChildName + "[" + aInstanceCount + "]");   //$NON-NLS-1$//$NON-NLS-2$
      }
   }

   /**
    * Builds the location path for a parallel forEach (or concurrent onAlarm/onEvent) scope child and all its children.
    * @param aBaseLocationPath
    * @param aChildScopeDef
    * @param aInstanceCount
    */
   protected void buildInstanceLocationPaths(String aBaseLocationPath, AeActivityScopeDef aChildScopeDef, int aInstanceCount)
   {
      // create new location paths based on the current instance counter value
      // using the def path visitor.
      IAePathSegmentBuilder segmentBuilder = AeDefVisitorFactory.getInstance(getProcessDef().getNamespace()).createPathSegmentBuilder();
      AeDynamicInstancePathBuilder pathBuilder = new AeDynamicInstancePathBuilder(segmentBuilder, aChildScopeDef);
      pathBuilder.setInstanceValue(aInstanceCount);
      pathBuilder.setPath(aBaseLocationPath);
      aChildScopeDef.accept(pathBuilder);
   }

   /**
    * Overrides method to associate the state document with the process visual model.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      super.visit(aDef);
      getBpelProcessModel().setStateDoc(getStateDocument());

      // check for process's coordinator and participant flags.
      try
      {
         // find the process element in the state document.
         Element ele = selectElement(IAeImplStateNames.STATE_PROCESSSTATE);
         // get value for coordinator attr.
         String s = ele.getAttribute(IAeImplStateNames.STATE_COORDINATOR);
         getBpelProcessModel().setCoordinator( "true".equalsIgnoreCase(s )); //$NON-NLS-1$
         // get value for participant attr.
         s = ele.getAttribute(IAeImplStateNames.STATE_PARTICIPANT);
         getBpelProcessModel().setParticipant( "true".equalsIgnoreCase(s )); //$NON-NLS-1$
      }
      catch(Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
      }
   }

   /**
    * Overrides method to set the BPEL visual model's currentState based on the state document.
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebModelVisitor#setState(org.activebpel.rt.xml.def.AeBaseXmlDef, org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase)
    */
   protected void setState(AeBaseXmlDef aDef, AeBpelObjectBase aBpelObject)
   {
      try
      {
         // find the element in the state document for the given definition.
         Element ele = selectSingleElement(aDef);
         if (ele != null)
         {
            // set the current state.
            String state = ele.getAttribute(IAeImplStateNames.STATE_STATE);
            aBpelObject.setState(state); // currentState
         }
         else if (!SKIP_STATE_PROPERTY.contains(aBpelObject.getTagName()) )
         {
            // assign inactive state by default except for non-activity type items such as variables.
            aBpelObject.setState(AeBpelState.INACTIVE.toString());
         }
      }
      catch(Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
      }
   }

   /**
    * Overrides method to set the process's state.
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebModelVisitor#setState(org.activebpel.rt.bpel.def.AeProcessDef, org.activebpel.rt.bpeladmin.war.web.processview.AeBpelProcessObject)
    */
   protected void setState(AeProcessDef aDef, AeBpelProcessObject aBpelObject)
   {
      // set activity and the process level state
      try
      {
         // find the element in the state document which corresponds to the process.
         Element ele = selectSingleElement(aDef, IAeImplStateNames.STATE_PROCESSSTATE);
         if (ele != null)
         {
            // set the current activity  state.
            String state = ele.getAttribute(IAeImplStateNames.STATE_STATE);
            aBpelObject.setState(state); // currentState

            // set process state code.
            int processState = Integer.parseInt(ele.getAttribute(IAeImplStateNames.STATE_PROCESSSTATE));
            aBpelObject.setProcessState(processState);
            int stateReason = Integer.parseInt(ele.getAttribute(IAeImplStateNames.STATE_PROCESSSTATEREASON));
            aBpelObject.setProcessStateReason(stateReason);
         }
      }
      catch(Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
      }
   }

   /**
    *
    * Overrides method to set the link's state.
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeProcessDefToWebModelVisitor#setState(org.activebpel.rt.bpel.def.activity.support.AeLinkDef, org.activebpel.rt.bpeladmin.war.web.processview.AeBpelLinkObject)
    */
   protected void setState(AeLinkDef aDef, AeBpelLinkObject aBpelObject)
   {
      try
      {
         // element in the state document which corressponds to the link.
         Element ele = selectSingleElement(aDef, IAeImplStateNames.STATE_LINK);
         if (ele != null)
         {
            String eval = ele.getAttribute(IAeImplStateNames.STATE_EVAL);
            aBpelObject.setStatus(eval);
         }
      }
      catch(Exception e)
      {
         AeException.logError(e, e.getLocalizedMessage());
      }
   }
}
