// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/IAeDefTraverser.java,v 1.19 2007/11/15 22:31:11 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

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
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefTraverser;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;

/**
 * Traversal interface for use in conjunction with BPEL definition object
 * visitation. Each traverse method below accepts a definition object and
 * a visitor object. The method's responsibility is to decide how to traverse the
 * given definition object so each of its child objects (if any) will get
 * visited. 
 */
public interface IAeDefTraverser extends IAeBaseXmlDefTraverser
{
   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeProcessDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityAssignDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityCompensateDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityCompensateScopeDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityEmptyDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityFlowDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityInvokeDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityPickDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityReceiveDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityReplyDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityScopeDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeVariablesDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeVariableDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeCorrelationSetsDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeCorrelationSetDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeFaultHandlersDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeCatchDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeCompensationHandlerDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeEventHandlersDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeOnAlarmDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeOnMessageDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeOnEventDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivitySequenceDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityExitDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityThrowDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityWaitDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
   /**
    * Traverses the definition objects, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * 
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityForEachDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
   /**
    * Traverses the definition objects, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * 
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeForEachFinalDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
   /**
    * Traverses the definition objects, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * 
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeForEachStartDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition objects, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * 
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeForEachBranchesDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition objects, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * 
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeForEachCompletionConditionDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityWhileDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityRepeatUntilDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityContinueDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityBreakDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivitySuspendDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeCatchAllDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeAssignCopyDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeCorrelationDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeLinkDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AePartnerDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AePartnerLinkDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeScopeDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeMessageExchangesDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeMessageExchangeDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeSourcesDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeSourceDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeTargetsDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeTargetDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AePartnerLinksDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AePartnersDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeLinksDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeCorrelationsDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeFromDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeToDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeQueryDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeImportDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityValidateDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeExtensibleAssignDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeExtensionsDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeExtensionDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeFromPartsDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeToPartsDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeFromPartDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeToPartDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeTransitionConditionDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeJoinConditionDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeForDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeUntilDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeExtensionActivityDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeChildExtensionActivityDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityIfDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeConditionDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeElseIfDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeIfDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeElseDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityRethrowDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeRepeatEveryDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeTerminationHandlerDef aDef, IAeBaseXmlDefVisitor aVisitor);

   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeLiteralDef aDef, IAeBaseXmlDefVisitor aVisitor);
   
   /**
    * Traverses the definition object, calling <code>accept</code> on each
    * of the object's child objects that can be visited.
    * @param aDef
    * @param aVisitor
    */
   public void traverse(AeActivityOpaqueDef  aDef, IAeBaseXmlDefVisitor aVisitor);
}
