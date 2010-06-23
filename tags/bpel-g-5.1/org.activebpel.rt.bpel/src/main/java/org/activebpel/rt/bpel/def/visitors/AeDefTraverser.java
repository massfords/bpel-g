// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeDefTraverser.java,v 1.31 2007/11/15 22:31:11 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeCatchAllDef;
import org.activebpel.rt.bpel.def.AeCatchDef;
import org.activebpel.rt.bpel.def.AeCompensationHandlerDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
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
import org.activebpel.rt.bpel.def.IAeForUntilParentDef;
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
import org.activebpel.rt.bpel.def.adapter.IAeExtensionTraverserAdapter;
import org.activebpel.rt.xml.def.IAeExtensionObject;
import org.activebpel.rt.xml.def.visitors.AeBaseXmlDefTraverser;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;

/**
 * The external iterator for the IAeBaseXmlDefVisitor interface. Provides the
 * logic for traversing the BPEL definition objects.
 */
public class AeDefTraverser extends AeBaseXmlDefTraverser implements IAeDefTraverser
{
   /**
    * Default c'tor.
    */
   public AeDefTraverser()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeProcessDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeProcessDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getExtensionsDef(), aVisitor);
      callAccept(aDef.getImportDefs(), aVisitor);
      callAccept(aDef.getPartnerLinksDef(), aVisitor);
      callAccept(aDef.getPartnersDef(), aVisitor);
      callAccept(aDef.getMessageExchangesDef(), aVisitor);
      callAccept(aDef.getVariablesDef(), aVisitor);
      callAccept(aDef.getCorrelationSetsDef(), aVisitor);
      callAccept(aDef.getFaultHandlersDef(), aVisitor);
      callAccept(aDef.getCompensationHandlerDef(), aVisitor);
      callAccept(aDef.getEventHandlersDef(), aVisitor);
      callAccept(aDef.getTerminationHandlerDef(), aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityScopeDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      AeScopeDef scope = aDef.getScopeDef();
      scope.accept(aVisitor);
   }

   /**
    * Walk each of the <code>AeCorrelationSetDef</code> and call accept.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeCorrelationSetsDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeCorrelationSetsDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      // @todo these classes should impl common interface as per chris's
      // original idea.
      callAccept(aDef.getValues(), aVisitor);
   }

   /**
    * Walk each of the <code>AeOnMessageDef</code> and
    * <code>AeOnAlarmDef</code> and call accept.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeEventHandlersDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeEventHandlersDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getOnEventDefs(), aVisitor);
      callAccept(aDef.getAlarmDefs(), aVisitor);
   }

   /**
    * Walk all of the <code>AeFaultHandlerDef</code> as well as the
    * <code>AeDefaultFaultHandlerDef</code> and call accept
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeFaultHandlersDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeFaultHandlersDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getCatchDefs(), aVisitor);
      callAccept(aDef.getCatchAllDef(), aVisitor);
   }

   /**
    * Calls accept on the single child of the compensation handler
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeCompensationHandlerDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeCompensationHandlerDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * Walk all of the <code>AeVariableDef</code> and call accept.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeVariablesDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeVariablesDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getValues(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeCorrelationSetDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeCorrelationSetDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * Calls <code>accept</code> on the child activity for the fault
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeCatchDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeCatchDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
      // ws-bpel has a fault variable def
      callAccept(aDef.getFaultVariableDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeVariableDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeVariableDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getFromDef(), aVisitor);
   }

   /**
    * Calls <code>accept</code> on the child activity for the alarm.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeOnAlarmDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      // Note: Check for overrides if you're changing this code.
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseForAndUntilDefs(aDef, aVisitor);
      callAccept(aDef.getRepeatEveryDef(), aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * Calls <code>accept</code> on the correlations and the child activity for
    * the message.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeOnMessageDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getCorrelationsDef(), aVisitor);
      callAccept(aDef.getFromPartsDef(), aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeOnEventDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverse((AeOnMessageDef) aDef, aVisitor);
   }

   /**
    * Calls <code>accept</code> on the <code>AeAssignCopyDef</code>
    * children.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityAssignDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityAssignDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getCopyDefs(), aVisitor);
      callAccept(aDef.getExtensibleAssignDefs(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityCompensateDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityCompensateDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityCompensateScopeDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityEmptyDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityEmptyDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityOpaqueDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivitySuspendDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityContinueDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityBreakDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * Walk all of the <code>AeLinkDef</code> and <code>AeActivityDef</code>
    * and call <code>accept</code>
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityFlowDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityFlowDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getLinksDef(), aVisitor);
      callAccept(aDef.getActivityDefs(), aVisitor);
   }

   /**
    * Walks all of the <code>AeInvokeDef</code> and calls <code>accept</code>.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityInvokeDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getCorrelationsDef(), aVisitor);
      if (aDef.getImplicitScopeDef() != null)
      {
         AeScopeDef implicitScopeDef = aDef.getImplicitScopeDef().getScopeDef();
         callAccept(implicitScopeDef.getCatchDefs(), aVisitor);
         callAccept(implicitScopeDef.getCatchAllDef(), aVisitor);
         callAccept(implicitScopeDef.getCompensationHandlerDef(), aVisitor);
      }
      callAccept(aDef.getToPartsDef(), aVisitor);
      callAccept(aDef.getFromPartsDef(), aVisitor);
   }

   /**
    * Walk the <code>AeOnMessageDef</code> and <code>AeOnAlarmDef</code> and
    * call <code>accept</code>.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityPickDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityPickDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getOnMessageDefs(), aVisitor);
      callAccept(aDef.getAlarmDefs(), aVisitor);
   }

   /**
    * Walks all of the <code>AeCorrelationDef</code> and calls
    * <code>accept</code>.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityReceiveDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getCorrelationsDef(), aVisitor);
      callAccept(aDef.getFromPartsDef(), aVisitor);
   }

   /**
    * Walks all of the <code>AeCorrelationDef</code> and calls
    * <code>accept</code>.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityReplyDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getCorrelationsDef(), aVisitor);
      callAccept(aDef.getToPartsDef(), aVisitor);
   }

   /**
    * Walk all of the child activities and call <code>accept</code>.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivitySequenceDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivitySequenceDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getActivityDefs(), aVisitor);
   }

   /**
    * Traverse terminate for inbound links
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityExitDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityExitDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityThrowDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityThrowDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityWaitDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      traverseForAndUntilDefs(aDef, aVisitor);
   }

   /**
    * Call <code>accept</code> on the child activity.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityWhileDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getConditionDef(), aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityRepeatUntilDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityRepeatUntilDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      // different from while - traverse the children in a different order
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getConditionDef(), aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityForEachDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getStartDef(), aVisitor);
      callAccept(aDef.getFinalDef(), aVisitor);
      callAccept(aDef.getCompletionCondition(), aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeForEachFinalDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeForEachFinalDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeForEachStartDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeForEachStartDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeForEachBranchesDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeForEachBranchesDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeForEachCompletionConditionDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeForEachCompletionConditionDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getBranches(), aVisitor);
   }

   /**
    * Call <code>accept</code> on the child activity.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeCatchAllDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeCatchAllDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * Calls <code>accept</code> on the From and To
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeAssignCopyDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getFromDef(), aVisitor);
      callAccept(aDef.getToDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeFromDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeFromDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getLiteralDef(), aVisitor);
      callAccept(aDef.getQueryDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeToDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeToDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getQueryDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeQueryDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeQueryDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
   }

   /**
    * Walks all of the <code>correlation</code> objects and calls accept.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeCorrelationsDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeCorrelationsDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getValues(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeCorrelationDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeLinkDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeLinkDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AePartnerDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AePartnerDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AePartnerLinkDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AePartnerLinkDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeMessageExchangesDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeMessageExchangesDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getMessageExchangeDefs(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeMessageExchangeDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeMessageExchangeDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeExtensibleAssignDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeExtensionDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeExtensionDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeExtensionsDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeExtensionsDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getExtensionDefs(), aVisitor);
   }

   /**
    * Calls <code>accept</code> on the fault handler, compensation handler,
    * variable container, correlationset container, event handler, and the child
    * activity.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeScopeDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeScopeDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getPartnerLinksDef(), aVisitor);
      callAccept(aDef.getMessageExchangesDef(), aVisitor);
      callAccept(aDef.getVariablesDef(), aVisitor);
      callAccept(aDef.getCorrelationSetsDef(), aVisitor);
      callAccept(aDef.getFaultHandlersDef(), aVisitor);
      callAccept(aDef.getCompensationHandlerDef(), aVisitor);
      callAccept(aDef.getEventHandlersDef(), aVisitor);
      callAccept(aDef.getTerminationHandlerDef(), aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * Walk all of the child partnerLinks and call <code>accept</code>.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AePartnerLinksDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AePartnerLinksDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getValues(), aVisitor);
   }

   /**
    * Walk all of the child partners and call <code>accept</code>.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AePartnersDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AePartnersDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getValues(), aVisitor);
   }

   /**
    * Walk all of the child links and call <code>accept</code>.
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeLinksDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeLinksDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getLinkDefs(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeSourcesDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeSourcesDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getSourceDefs(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeSourceDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeSourceDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getTransitionConditionDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeTargetsDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeTargetsDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getJoinConditionDef(), aVisitor);
      callAccept(aDef.getTargetDefs(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeTargetDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeTargetDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeImportDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeImportDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityValidateDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityValidateDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeFromPartsDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeFromPartsDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getFromPartDefs(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeToPartsDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeToPartsDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getToPartDefs(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeFromPartDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeFromPartDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeToPartDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeToPartDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeJoinConditionDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeTransitionConditionDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeTransitionConditionDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeForDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeForDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeUntilDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeUntilDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeChildExtensionActivityDef aDef, IAeBaseXmlDefVisitor aVisitor)
   { 
      IAeExtensionObject extObj = aDef.getExtensionObject();
      if ( extObj != null )
      {
         IAeExtensionTraverserAdapter adapter = (IAeExtensionTraverserAdapter)extObj.getAdapter(IAeExtensionTraverserAdapter.class);
         if ( adapter != null )
         {
            IAeDefTraverser traverser = adapter.createTraverser();
            traverser.traverse(aDef, aVisitor);
            return;
         }
      }
      callAccept(aDef.getOrderedDefs().iterator(), aVisitor);
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeExtensionActivityDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeExtensionActivityDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityIfDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityIfDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
      callAccept(aDef.getIfDef(), aVisitor);
      callAccept(aDef.getElseIfDefs(), aVisitor);
      callAccept(aDef.getElseDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeConditionDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeConditionDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeElseDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeElseDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeElseIfDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getConditionDef(), aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeIfDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeIfDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getConditionDef(), aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.AeActivityRethrowDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeActivityRethrowDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      traverseSourceAndTargetLinks(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeRepeatEveryDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeRepeatEveryDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.AeTerminationHandlerDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeTerminationHandlerDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      traverseDocumentationDefs(aDef, aVisitor);
      traverseExtensionDefs(aDef, aVisitor);
      callAccept(aDef.getActivityDef(), aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefTraverser#traverse(org.activebpel.rt.bpel.def.activity.support.AeLiteralDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
    */
   public void traverse(AeLiteralDef aDef, IAeBaseXmlDefVisitor aVisitor)
   {
      // literals cannot have documentation or extensions
   }

   /**
    * Traverse for and until constructs.
    * 
    * @param aDef
    * @param aVisitor
    */
   protected void traverseForAndUntilDefs(IAeForUntilParentDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      callAccept(aDef.getForDef(), aVisitor);
      callAccept(aDef.getUntilDef(), aVisitor);
   }

   /**
    * @param aDef
    * @param aVisitor
    */
   protected void traverseSourceAndTargetLinks(AeActivityDef aDef,
         IAeBaseXmlDefVisitor aVisitor)
   {
      callAccept(aDef.getTargetsDef(), aVisitor);
      callAccept(aDef.getSourcesDef(), aVisitor);
   }
}
