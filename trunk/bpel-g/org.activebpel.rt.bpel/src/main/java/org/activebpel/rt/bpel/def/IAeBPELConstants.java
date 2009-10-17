// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/IAeBPELConstants.java,v 1.36 2007/11/15 22:31:11 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import javax.xml.namespace.QName;

import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;
import org.activebpel.rt.xml.def.IAeBaseXmlDefConstants;

/**
 * Contains the list of BPEL Constants.
 */
public interface IAeBPELConstants extends IAeBaseXmlDefConstants
{
   /** Active Endpoints namespace declaration. */
   public static final String AE_NAMESPACE_URI = "http://www.active-endpoints.com/2004/06/bpel/extensions/"; //$NON-NLS-1$

   /** The preferred prefix for AE extensions. */
   public static final String AE_EXTENSION_PREFIX = "ext"; //$NON-NLS-1$

   /** Active Endpoints 'allow process termination/compensation' extension namespace declaration. */
   public static final String AE_EXTENSION_NAMESPACE_URI_ALLOW_PROCESS_COORDINATION = "http://www.activebpel.org/2006/09/bpel/extension/process_coordination"; //$NON-NLS-1$
   /** Active Endpoints 'activity' extension namespace declaration. */
   public static final String AE_EXTENSION_NAMESPACE_URI_ACTIVITY = "http://www.activebpel.org/2006/09/bpel/extension/activity"; //$NON-NLS-1$
   /** Active Endpoints 'query handling' extension namespace declaration. */
   public static final String AE_EXTENSION_NAMESPACE_URI_QUERY_HANDLING = "http://www.activebpel.org/2006/09/bpel/extension/query_handling"; //$NON-NLS-1$

   /** BPEL4WS 1.1 namespace declaration */
   public static final String BPWS_NAMESPACE_URI = "http://schemas.xmlsoap.org/ws/2003/03/business-process/"; //$NON-NLS-1$

   /** BPEL4WS 1.1 Prefix. */
   public static final String BPWS_PREFIX = "bpws"; //$NON-NLS-1$

   /** WSBPEL 2.0 namespace declaration */
   public static final String WSBPEL_2_0_NAMESPACE_URI = IAeBPELExtendedWSDLConst.WSBPEL_2_0_NAMESPACE_URI;

   /** WSBPEL 2.0 abstract process namespace declaration */
   public static final String WSBPEL_2_0_ABSTRACT_NAMESPACE_URI = "http://docs.oasis-open.org/wsbpel/2.0/process/abstract"; //$NON-NLS-1$

   /** WSBPEL 2.0 Prefix. */
    public static final String WSBPEL_2_0_PREFIX = "bpel"; //$NON-NLS-1$

   /** BPEL extensions namespace declaration. */
   public static final String ABX_2_0_NAMESPACE_URI = "http://www.activebpel.org/2.0/bpel/extension"; //$NON-NLS-1$

   /** BPEL extensions namespace declaration. Used for functions */
   public static final String ABX_FUNCTIONS_NAMESPACE_URI = "http://www.activebpel.org/bpel/extension"; //$NON-NLS-1$

   /** Preferred prefix for ActiveBPEL extension namespace. */
   public static final String ABX_PREFIX = "abx"; //$NON-NLS-1$

   /** Preferred prefix for wsbpel abstract process namespace. */
   public static final String ABSTRACT_PROC_PREFIX = "absbpel"; //$NON-NLS-1$

   /** Default Schema NS */
   public static final String DEFAULT_SCHEMA_NS = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$

   public static final String ATTR_NO  = "no"; //$NON-NLS-1$
   public static final String ATTR_YES = "yes"; //$NON-NLS-1$

   // activity name constants
   public static final String TAG_ASSIGN = "assign"; //$NON-NLS-1$
   public static final String TAG_EMPTY = "empty"; //$NON-NLS-1$
   public static final String TAG_FLOW  = "flow"; //$NON-NLS-1$
   public static final String TAG_INVOKE = "invoke"; //$NON-NLS-1$
   public static final String TAG_PICK = "pick"; //$NON-NLS-1$
   public static final String TAG_REPLY = "reply"; //$NON-NLS-1$
   public static final String TAG_RECEIVE = "receive"; //$NON-NLS-1$
   public static final String TAG_SEQUENCE = "sequence"; //$NON-NLS-1$
   public static final String TAG_EXIT = "exit"; //$NON-NLS-1$
   public static final String TAG_THROW = "throw"; //$NON-NLS-1$
   public static final String TAG_WAIT = "wait"; //$NON-NLS-1$
   public static final String TAG_WHILE = "while"; //$NON-NLS-1$
   public static final String TAG_REPEAT_UNTIL = "repeatUntil"; //$NON-NLS-1$
   public static final String TAG_CONTINUE = "continue"; //$NON-NLS-1$
   public static final String TAG_BREAK = "break"; //$NON-NLS-1$
   public static final String TAG_FOREACH = "forEach"; //$NON-NLS-1$
   public static final String TAG_SUSPEND = "suspend"; //$NON-NLS-1$
   public static final String TAG_FOREACH_BRANCHES = "branches"; //$NON-NLS-1$
   public static final String TAG_FOREACH_COMPLETION_CONDITION = "completionCondition"; //$NON-NLS-1$
   public static final String TAG_VALIDATE = "validate"; //$NON-NLS-1$
   public static final String TAG_OPAQUE_ACTIVITY = "opaqueActivity"; //$NON-NLS-1$

   public static final String TAG_PROCESS = "process"; //$NON-NLS-1$
   public static final String TAG_TARGET_NAMESPACE = "targetNamespace"; //$NON-NLS-1$
   public static final String TAG_QUERY_LANGUAGE = "queryLanguage"; //$NON-NLS-1$
   public static final String TAG_EXPRESSION_LANGUAGE = "expressionLanguage"; //$NON-NLS-1$
   public static final String TAG_SUPPRESS_JOIN_FAILURE = "suppressJoinFailure"; //$NON-NLS-1$
   public static final String TAG_EXIT_ON_STANDARD_FAULT = "exitOnStandardFault"; //$NON-NLS-1$
   public static final String TAG_ENABLE_INSTANCE_COMPENSATION = "enableInstanceCompensation"; //$NON-NLS-1$
   public static final String TAG_ABSTRACT_PROCESS = "abstractProcess"; //$NON-NLS-1$
   public static final String TAG_ABSTRACT_PROCESS_PROFILE = "abstractProcessProfile"; //$NON-NLS-1$
   public static final String TAG_CREATE_TARGET_XPATH = "createTargetXPath"; //$NON-NLS-1$
   public static final String TAG_DISABLE_SELECTION_FAILURE = "disableSelectionFailure"; //$NON-NLS-1$

   public static final String TAG_PARTNER_LINK = "partnerLink"; //$NON-NLS-1$
   public static final String TAG_PARTNER_LINK_TYPE = "partnerLinkType"; //$NON-NLS-1$
   public static final String TAG_MY_ROLE = "myRole"; //$NON-NLS-1$
   public static final String TAG_PARTNER_ROLE = "partnerRole"; //$NON-NLS-1$
   public static final String TAG_INITIALIZE_PARTNER_ROLE = "initializePartnerRole"; //$NON-NLS-1$

   public static final String TAG_VARIABLE = "variable"; //$NON-NLS-1$
   public static final String TAG_MESSAGE_TYPE = "messageType"; //$NON-NLS-1$
   public static final String TAG_TYPE = "type"; //$NON-NLS-1$
   public static final String TAG_ELEMENT = "element"; //$NON-NLS-1$

   public static final String TAG_CORRELATION = "correlation"; //$NON-NLS-1$

   public static final String TAG_IMPORT = "import"; //$NON-NLS-1$
   public static final String TAG_EXTENSIONS = "extensions"; //$NON-NLS-1$
   public static final String TAG_EXTENSION = "extension"; //$NON-NLS-1$
   public static final String TAG_CORRELATIONS = "correlations"; //$NON-NLS-1$
   public static final String TAG_CORRELATION_SETS = "correlationSets"; //$NON-NLS-1$
   public static final String TAG_CORRELATION_SET = "correlationSet"; //$NON-NLS-1$
   public static final String TAG_FAULT_HANDLERS = "faultHandlers"; //$NON-NLS-1$
   public static final String TAG_EVENT_HANDLERS = "eventHandlers"; //$NON-NLS-1$
   public static final String TAG_VARIABLES = "variables"; //$NON-NLS-1$
   public static final String TAG_PARTNER_LINKS = "partnerLinks"; //$NON-NLS-1$

   public static final String TAG_PROPERTIES = "properties"; //$NON-NLS-1$

   public static final String TAG_ON_MESSAGE = "onMessage"; //$NON-NLS-1$
   public static final String TAG_ON_EVENT = "onEvent"; //$NON-NLS-1$
   public static final String TAG_ON_ALARM = "onAlarm"; //$NON-NLS-1$
   public static final String TAG_FAULT_VARIABLE = "faultVariable"; //$NON-NLS-1$
   public static final String TAG_FAULT_NAME = "faultName"; //$NON-NLS-1$
   public static final String TAG_FAULT_MESSAGE_TYPE = "faultMessageType"; //$NON-NLS-1$
   public static final String TAG_FAULT_ELEMENT = "faultElement"; //$NON-NLS-1$

   public static final String TAG_MESSAGE_EXCHANGES = "messageExchanges"; //$NON-NLS-1$
   public static final String TAG_MESSAGE_EXCHANGE = "messageExchange"; //$NON-NLS-1$

   public static final String TAG_MODEL_VERSION = "modelVersion"; //$NON-NLS-1$


   // standard attributes names of the copy definition
   public static final String TAG_SET        = "set"; //$NON-NLS-1$
   public static final String TAG_INITIATE   = "initiate"; //$NON-NLS-1$
   public static final String TAG_PATTERN    = "pattern"; //$NON-NLS-1$

   public static final String TAG_CREATE_INSTANCE = "createInstance"; //$NON-NLS-1$

   // standard attributes names of the throw activity definition
   public static final String TAG_ISOLATED = "isolated"; //$NON-NLS-1$

   public static final String TAG_CATCH = "catch"; //$NON-NLS-1$
   public static final String TAG_CATCH_ALL = "catchAll"; //$NON-NLS-1$
   public static final String TAG_COMPENSATION_HANDLER = "compensationHandler"; //$NON-NLS-1$
   public static final String TAG_INPUT_VARIABLE = "inputVariable"; //$NON-NLS-1$
   public static final String TAG_OUTPUT_VARIABLE = "outputVariable"; //$NON-NLS-1$
   public static final String TAG_IF = "if"; //$NON-NLS-1$
   public static final String TAG_ELSEIF = "elseif"; //$NON-NLS-1$
   public static final String TAG_ELSE = "else"; //$NON-NLS-1$
   public static final String TAG_CONDITION = "condition"; //$NON-NLS-1$
   public static final String TAG_IF_CONDITION = "if-condition"; //$NON-NLS-1$

   public static final String TAG_FOR   = "for"; //$NON-NLS-1$
   public static final String TAG_UNTIL = "until"; //$NON-NLS-1$

   public static final String TAG_EXTENSION_ACTIVITY = "extensionActivity"; //$NON-NLS-1$

   public static final String TAG_FOREACH_COUNTERNAME = "counterName"; //$NON-NLS-1$
   public static final String TAG_FOREACH_PARALLEL = "parallel"; //$NON-NLS-1$
   public static final String TAG_FOREACH_STARTCOUNTER = "startCounterValue"; //$NON-NLS-1$
   public static final String TAG_FOREACH_FINALCOUNTER = "finalCounterValue"; //$NON-NLS-1$
   public static final String TAG_FOREACH_BRANCH_COUNTCOMPLETED = "successfulBranchesOnly"; //$NON-NLS-1$

  // attributes tag names of a definition
   public static final String TAG_LINK_NAME = "linkName"; //$NON-NLS-1$

   public static final String TAG_COPY = "copy"; //$NON-NLS-1$
   public static final String TAG_EXTENSION_ASSIGN_OPERATION = "extensionAssignOperation"; //$NON-NLS-1$

  // standard element names of the activity definition
   public static final String TAG_TARGETS = "targets"; //$NON-NLS-1$
   public static final String TAG_SOURCES = "sources"; //$NON-NLS-1$
   public static final String TAG_TARGET = "target"; //$NON-NLS-1$
   public static final String TAG_SOURCE = "source"; //$NON-NLS-1$

   // attributes tag names of a definition
   public static final String TAG_TRANSITION_CONDITION = "transitionCondition"; //$NON-NLS-1$

   // attributes of a copy def
   public static final String TAG_KEEP_SRC_ELEMENT_NAME = "keepSrcElementName"; //$NON-NLS-1$
   public static final String TAG_IGNORE_MISSING_FROM_DATA = "ignoreMissingFromData"; //$NON-NLS-1$

   // standard element names of the copy definition
   public static final String TAG_FROM = "from"; //$NON-NLS-1$
   public static final String TAG_TO = "to"; //$NON-NLS-1$
   public static final String TAG_OPAQUE_FROM = "opaqueFrom"; //$NON-NLS-1$  (BPEL 2.0 abstract process only)

   public static final String TAG_LITERAL = "literal"; //$NON-NLS-1$

   // standard attributes names of the activity definition
   public static final String TAG_PART = "part"; //$NON-NLS-1$
   public static final String TAG_QUERY = "query"; //$NON-NLS-1$
   public static final String TAG_ENDPOINT_REFERENCE = "endpointReference"; //$NON-NLS-1$
   public static final String TAG_PROPERTY = "property"; //$NON-NLS-1$
   public static final String TAG_EXPRESSION = "expression"; //$NON-NLS-1$
   public static final String TAG_OPAQUE_ATTR = "opaque"; //$NON-NLS-1$

   public static final String TAG_SCOPE = "scope"; //$NON-NLS-1$

   public static final String TAG_JOIN_CONDITION   = "joinCondition"; //$NON-NLS-1$
   public static final String TAG_SUPPRESS_FAILURE = "suppressJoinFailure"; //$NON-NLS-1$
   public static final String TAG_NAME = "name"; //$NON-NLS-1$

   public static final String TAG_COMPENSATE = "compensate"; //$NON-NLS-1$
   public static final String TAG_COMPENSATE_SCOPE = "compensateScope"; //$NON-NLS-1$

   public static final String TAG_LINKS = "links"; //$NON-NLS-1$
   public static final String TAG_LINK  = "link"; //$NON-NLS-1$

   // standard attributes names of the activity definition
   public static final String TAG_PORT_TYPE = "portType"; //$NON-NLS-1$
   public static final String TAG_OPERATION = "operation"; //$NON-NLS-1$

   public static final String TAG_NAMESPACE = "namespace"; //$NON-NLS-1$
   public static final String TAG_LOCATION = "location"; //$NON-NLS-1$
   public static final String TAG_IMPORT_TYPE = "importType"; //$NON-NLS-1$

   public static final String TAG_MUST_UNDERSTAND = "mustUnderstand"; //$NON-NLS-1$

   public static final String TAG_FROM_PARTS = "fromParts"; //$NON-NLS-1$
   public static final String TAG_TO_PARTS = "toParts"; //$NON-NLS-1$
   public static final String TAG_FROM_PART = "fromPart"; //$NON-NLS-1$
   public static final String TAG_TO_PART = "toPart"; //$NON-NLS-1$
   public static final String TAG_TO_VARIABLE = "toVariable"; //$NON-NLS-1$
   public static final String TAG_FROM_VARIABLE = "fromVariable"; //$NON-NLS-1$

   public static final String TAG_RETHROW = "rethrow"; //$NON-NLS-1$

   public static final String TAG_REPEAT_EVERY = "repeatEvery"; //$NON-NLS-1$
   public static final String TAG_TERMINATION_HANDLER = "terminationHandler"; //$NON-NLS-1$

   // partner link epr service-ref
   public static final QName WS_BPEL_SERVICE_REF = new QName("http://docs.oasis-open.org/wsbpel/2.0/serviceref", "service-ref"); //$NON-NLS-1$ //$NON-NLS-2$

   /** The value of "expressionLanguage" for XPath (also the default for BPEL 1.1). */
   public static final String BPWS_XPATH_EXPR_LANGUAGE_URI = "http://www.w3.org/TR/1999/REC-xpath-19991116"; //$NON-NLS-1$
   /** The value of "expressionLanguage" for XPath (also the default for BPEL 2.0). */
   public static final String WSBPEL_EXPR_LANGUAGE_URI = "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath1.0"; //$NON-NLS-1$
   /** The value of "expressionLanguage" for XQuery (extension language by AEI). */
   public static final String XQUERY_EXPR_LANGUAGE_URI = "urn:active-endpoints:expression-language:xquery1.0"; //$NON-NLS-1$
   /** The value of "expressionLanguage" for JavaScript (extension language by AEI). */
   public static final String JAVASCRIPT_EXPR_LANGUAGE_URI = "urn:active-endpoints:expression-language:javascript1.5"; //$NON-NLS-1$
}
