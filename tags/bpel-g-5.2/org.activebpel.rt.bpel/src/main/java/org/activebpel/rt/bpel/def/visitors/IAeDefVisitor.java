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
    * @param aDef
    */
   public void visit(AeProcessDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityAssignDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityCompensateDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityCompensateScopeDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityEmptyDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityFlowDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityInvokeDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityPickDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityReceiveDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityReplyDef aDef);
   
   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivitySuspendDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityScopeDef aDef);
   
   /**
    * Visits the continue definition.
    * @param aDef
    */
   public void visit(AeActivityContinueDef aDef);
   
   /**
    * Visits the break definition.
    * @param aDef
    */
   public void visit(AeActivityBreakDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeCorrelationSetDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeCatchDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeCatchAllDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeVariableDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeVariablesDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeEventHandlersDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeCompensationHandlerDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeCorrelationSetsDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeFaultHandlersDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeOnMessageDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeOnEventDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeOnAlarmDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivitySequenceDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityExitDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityThrowDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityWaitDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityWhileDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityRepeatUntilDef aDef);
   
   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityForEachDef aDef);
   
   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeForEachCompletionConditionDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeForEachStartDef aDef);
   
   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeForEachFinalDef aDef);
   
   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeForEachBranchesDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AePartnerDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AePartnerLinkDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeScopeDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeMessageExchangesDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeMessageExchangeDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeAssignCopyDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeCorrelationDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeLinkDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeSourceDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeTargetDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AePartnerLinksDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AePartnersDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeLinksDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeCorrelationsDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeFromDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeToDef aDef);
      
   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeQueryDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeImportDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityValidateDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeExtensibleAssignDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeExtensionsDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeExtensionDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeFromPartsDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeToPartsDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeFromPartDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeToPartDef aDef);
   
   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeSourcesDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeTargetsDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeTransitionConditionDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeJoinConditionDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeForDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeUntilDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeExtensionActivityDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeChildExtensionActivityDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityIfDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeIfDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeElseIfDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeElseDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeConditionDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityRethrowDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeRepeatEveryDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeTerminationHandlerDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeLiteralDef aDef);

   /**
    * Visits the specified type of definition object.
    * @param aDef
    */
   public void visit(AeActivityOpaqueDef aDef);
}
