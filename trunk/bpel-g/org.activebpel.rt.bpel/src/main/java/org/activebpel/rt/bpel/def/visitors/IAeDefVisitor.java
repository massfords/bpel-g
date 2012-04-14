// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/IAeDefVisitor.java,v 1.18 2007/10/12 16:09:48 ppatruni Exp $
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
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;

/**
 * Visitor interface for BPEL Definition classes.
 */
public interface IAeDefVisitor extends IAeBaseXmlDefVisitor
{
   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeProcessDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityAssignDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityCompensateDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityCompensateScopeDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityEmptyDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityFlowDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityInvokeDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityPickDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityReceiveDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityReplyDef def);
   
   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivitySuspendDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityScopeDef def);
   
   /**
    * Visits the continue definition.
    * @param def
    */
   public void visit(AeActivityContinueDef def);
   
   /**
    * Visits the break definition.
    * @param def
    */
   public void visit(AeActivityBreakDef def);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeCorrelationSetDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeCatchDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeCatchAllDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeVariableDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeVariablesDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeEventHandlersDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeCompensationHandlerDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeCorrelationSetsDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeFaultHandlersDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeOnMessageDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeOnEventDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeOnAlarmDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivitySequenceDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityExitDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityThrowDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityWaitDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityWhileDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityRepeatUntilDef def);
   
   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityForEachDef def);
   
   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeForEachCompletionConditionDef def);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeForEachStartDef aDef);
   
   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeForEachFinalDef def);
   
   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeForEachBranchesDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AePartnerDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AePartnerLinkDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeScopeDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeMessageExchangesDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeMessageExchangeDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeAssignCopyDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeCorrelationDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeLinkDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeSourceDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeTargetDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AePartnerLinksDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AePartnersDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeLinksDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeCorrelationsDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeFromDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeToDef def);
      
   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeQueryDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeImportDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityValidateDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeExtensibleAssignDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeExtensionsDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeExtensionDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeFromPartsDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeToPartsDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeFromPartDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeToPartDef def);
   
   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeSourcesDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeTargetsDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeTransitionConditionDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeJoinConditionDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeForDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeUntilDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeExtensionActivityDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeChildExtensionActivityDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityIfDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeIfDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeElseIfDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeElseDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeConditionDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityRethrowDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeRepeatEveryDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeTerminationHandlerDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeLiteralDef def);

   /**
    * Visits the specified type of definition object.
    * @param def
    */
   public void visit(AeActivityOpaqueDef def);
}
