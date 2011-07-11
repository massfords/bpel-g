// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/IAeBpelClassConstants.java,v 1.13 2008/02/29 18:20:43 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io;

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
import org.activebpel.rt.bpel.def.activity.AeActivityOpaqueDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
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
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.AeDocumentationDef;

/**
 * Defines all of the class constants used by IAeBpelDefWriters, 
 * IAeBpelDefReaders, and the corresponding registries.
 * <br />
 * This is used in place of Class.forName where we are constrained by
 * current obfuscation.
 */
public interface IAeBpelClassConstants
{

   public static final Class<? extends AeBaseXmlDef> ASSIGN_COPY_CLASS             = AeAssignCopyDef.class;
   public static final Class<? extends AeBaseXmlDef> ASSIGN_EXTENSIBLE_ASSIGN_CLASS= AeExtensibleAssignDef.class;
   public static final Class<? extends AeBaseXmlDef> ASSIGN_TO_CLASS               = AeToDef.class;
   public static final Class<? extends AeBaseXmlDef> ASSIGN_FROM_CLASS             = AeFromDef.class;
   public static final Class<? extends AeBaseXmlDef> LITERAL_CLASS                 = AeLiteralDef.class;
   public static final Class<? extends AeBaseXmlDef> CORRELATION_CLASS             = AeCorrelationDef.class;
   public static final Class<? extends AeBaseXmlDef> CORRELATIONS_CLASS            = AeCorrelationsDef.class;
   public static final Class<? extends AeBaseXmlDef> CORRELATION_SET_CLASS         = AeCorrelationSetDef.class;
   public static final Class<? extends AeBaseXmlDef> CORRELATION_SETS_CLASS        = AeCorrelationSetsDef.class;
   public static final Class<? extends AeBaseXmlDef> CATCH_ALL_CLASS               = AeCatchAllDef.class;
   public static final Class<? extends AeBaseXmlDef> DOCUMENTATION_CLASS           = AeDocumentationDef.class;
   public static final Class<? extends AeBaseXmlDef> EVENT_HANDLERS_CLASS          = AeEventHandlersDef.class;
   public static final Class<? extends AeBaseXmlDef> IMPORT_CLASS                  = AeImportDef.class;
   public static final Class<? extends AeBaseXmlDef> EXTENSIONS_CLASS              = AeExtensionsDef.class;
   public static final Class<? extends AeBaseXmlDef> EXTENSION_CLASS               = AeExtensionDef.class;
   public static final Class<? extends AeBaseXmlDef> CATCH_CLASS                   = AeCatchDef.class;
   public static final Class<? extends AeBaseXmlDef> FAULT_HANDLERS_CLASS          = AeFaultHandlersDef.class;
   public static final Class<? extends AeBaseXmlDef> FOR_CLASS                     = AeForDef.class;
   public static final Class<? extends AeBaseXmlDef> LINK_CLASS                    = AeLinkDef.class;
   public static final Class<? extends AeBaseXmlDef> LINKS_CLASS                   = AeLinksDef.class;
   public static final Class<? extends AeBaseXmlDef> MESSAGE_EXCHANGES_CLASS       = AeMessageExchangesDef.class;
   public static final Class<? extends AeBaseXmlDef> MESSAGE_EXCHANGE_CLASS        = AeMessageExchangeDef.class;
   public static final Class<? extends AeBaseXmlDef> ON_ALARM_CLASS                = AeOnAlarmDef.class;
   public static final Class<? extends AeBaseXmlDef> ON_MESSAGE_CLASS              = AeOnMessageDef.class;
   public static final Class<? extends AeBaseXmlDef> ON_EVENT_CLASS                = AeOnEventDef.class;
   public static final Class<? extends AeBaseXmlDef> PARTNER_CLASS                 = AePartnerDef.class;
   public static final Class<? extends AeBaseXmlDef> PARTNERS_CLASS                = AePartnersDef.class;
   public static final Class<? extends AeBaseXmlDef> PARTNER_LINK_CLASS            = AePartnerLinkDef.class;
   public static final Class<? extends AeBaseXmlDef> PARNTER_LINKS_CLASS           = AePartnerLinksDef.class;
   public static final Class<? extends AeBaseXmlDef> PROCESS_CLASS                 = AeProcessDef.class;
   public static final Class<? extends AeBaseXmlDef> SCOPE_CLASS                   = AeScopeDef.class;
   public static final Class<? extends AeBaseXmlDef> COMPENSATION_HANDLER_CLASS    = AeCompensationHandlerDef.class;
   public static final Class<? extends AeBaseXmlDef> SOURCES_CLASS                 = AeSourcesDef.class;
   public static final Class<? extends AeBaseXmlDef> TARGETS_CLASS                 = AeTargetsDef.class;
   public static final Class<? extends AeBaseXmlDef> SOURCE_CLASS                  = AeSourceDef.class;
   public static final Class<? extends AeBaseXmlDef> TARGET_CLASS                  = AeTargetDef.class;
   public static final Class<? extends AeBaseXmlDef> VARIABLE_CLASS                = AeVariableDef.class;
   public static final Class<? extends AeBaseXmlDef> VARIABLES_CLASS               = AeVariablesDef.class;
   public static final Class<? extends AeBaseXmlDef> FROM_PARTS_CLASS              = AeFromPartsDef.class;
   public static final Class<? extends AeBaseXmlDef> TO_PARTS_CLASS                = AeToPartsDef.class;
   public static final Class<? extends AeBaseXmlDef> FROM_PART_CLASS               = AeFromPartDef.class;
   public static final Class<? extends AeBaseXmlDef> TO_PART_CLASS                 = AeToPartDef.class;
   public static final Class<? extends AeBaseXmlDef> JOIN_CONDITION_CLASS          = AeJoinConditionDef.class;
   public static final Class<? extends AeBaseXmlDef> TRANSITION_CONDITION_CLASS    = AeTransitionConditionDef.class;
   public static final Class<? extends AeBaseXmlDef> UNTIL_CLASS                   = AeUntilDef.class;
   public static final Class<? extends AeBaseXmlDef> IF_CLASS                      = AeIfDef.class;
   public static final Class<? extends AeBaseXmlDef> ELSEIF_CLASS                  = AeElseIfDef.class;
   public static final Class<? extends AeBaseXmlDef> ELSE_CLASS                    = AeElseDef.class;
   public static final Class<? extends AeBaseXmlDef> CONDITION_CLASS               = AeConditionDef.class;
   public static final Class<? extends AeBaseXmlDef> REPEAT_EVERY_CLASS            = AeRepeatEveryDef.class;
   public static final Class<? extends AeBaseXmlDef> TERMINATION_HANDLER_CLASS     = AeTerminationHandlerDef.class;
   public static final Class<? extends AeBaseXmlDef> PARTNER_LINKS_CLASS           = AePartnerLinksDef.class;

   public static final Class<? extends AeBaseXmlDef> ACTIVITY_ASSIGN_CLASS     = AeActivityAssignDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_COMPENSATE_CLASS = AeActivityCompensateDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_COMPENSATE_SCOPE_CLASS = AeActivityCompensateScopeDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_EMPTY_CLASS      = AeActivityEmptyDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_FLOW_CLASS       = AeActivityFlowDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_INVOKE_CLASS     = AeActivityInvokeDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_IF_CLASS         = AeActivityIfDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_PICK_CLASS       = AeActivityPickDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_SCOPE_CLASS      = AeActivityScopeDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_SEQUENCE_CLASS   = AeActivitySequenceDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_OPAQUE_CLASS     = AeActivityOpaqueDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_TERMINATE_CLASS  = AeActivityExitDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_EXIT_CLASS       = AeActivityExitDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_THROW_CLASS      = AeActivityThrowDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_REPLY_CLASS      = AeActivityReplyDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_RECEIVE_CLASS    = AeActivityReceiveDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_WAIT_CLASS       = AeActivityWaitDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_WHILE_CLASS      = AeActivityWhileDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_REPEAT_UNTIL_CLASS = AeActivityRepeatUntilDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_RETHROW_CLASS    = AeActivityRethrowDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_CONTINUE_CLASS   = AeActivityContinueDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_BREAK_CLASS      = AeActivityBreakDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_FOREACH_CLASS    = AeActivityForEachDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_FOREACH_START    = AeForEachStartDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_FOREACH_FINAL    = AeForEachFinalDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_FOREACH_COMPLETION_CONDITION = AeForEachCompletionConditionDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_FOREACH_BRANCHES = AeForEachBranchesDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_SUSPEND_CLASS    = AeActivitySuspendDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_VALIDATE_CLASS   = AeActivityValidateDef.class;
   public static final Class<? extends AeBaseXmlDef> ACTIVITY_CHILDEXTENSION_CLASS    = AeChildExtensionActivityDef.class;

   public static final Class<? extends AeBaseXmlDef> EXTENSION_ACTIVITY_CLASS  = AeExtensionActivityDef.class;
}
