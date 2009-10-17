// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/IAeValidationDefs.java,v 1.54 2007/09/28 21:45:38 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.validation;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeMessages;


/**
 * Interface definition for error reporting, primarily for validation errors.
 */
public interface IAeValidationDefs
{   
   /** Empty QName object with an empty selection localPart */
   public static final QName EMPTY_QNAME = new QName("", "(none)");  //$NON-NLS-1$ //$NON-NLS-2$

   /////////////////////////////////////////////////////////////////////////
   // Error, Warning and Information codes.  These are defined here for use
   //  by validation logic.  The AEV package can cross-reference these codes
   //  to localizable strings.
   //
   // {0}, {1}, etc., used for substitution placeholder where validate()
   //  method will insert specifics.
   /////////////////////////////////////////////////////////////////////////

   ///////////////////////////////
   // Error codes - add as needed.
   ///////////////////////////////

   /** Error message for a compensateScope activity that is missing its target attribute value */
   public final static String ERROR_COMPENSATE_SCOPE_EMPTY = AeMessages.getString("IAeValidationDefs.CompensateScopeMissingTarget"); //$NON-NLS-1$
   /** Error message for a validate activity that is missing its list of variables to validate */
   public final static String ERROR_EMPTY_VALIDATE = AeMessages.getString("IAeValidationDefs.EmptyValidate"); //$NON-NLS-1$
   /** Error message for a copy operation that will result in mismatchedAssignmentFailure at runtime */
   public final static String ERROR_MISMATCHED_ASSIGNMENT_FAILURE = AeMessages.getString("IAeValidationDefs.MismatchedAssignmentFailure"); //$NON-NLS-1$

   public final static String FIELD_UNDEFINED = "(none)" ; //$NON-NLS-1$
   public final static String EXCEPTION_DURING_VALIDATION = AeMessages.getString("IAeValidationDefs.5") ; //$NON-NLS-1$

   /** Error for a null strategy for a copy operation's from */
   public final static String ERROR_UNSUPPORTED_COPYOP_FROM = AeMessages.getString("IAeValidationDefs.UnsupportedCopyOperation.From") ; //$NON-NLS-1$
   /** Error for a null strategy for a copy operation's to */
   public final static String ERROR_UNSUPPORTED_COPYOP_TO = AeMessages.getString("IAeValidationDefs.UnsupportedCopyOperation.To") ; //$NON-NLS-1$
   /** Error message for assigning to a plink that doesn't define a partnerRole */
   public final static String ERROR_PLINK_ASSIGN_TO = AeMessages.getString("IAeValidationDefs.Plink.To.NoPartnerRole"); //$NON-NLS-1$
   /** Error message for assigning from a plink that doesn't have a myRole */
   public final static String ERROR_PLINK_ASSIGN_FROM_MYROE = AeMessages.getString("IAeValidationDefs.Plink.From.NoMyRole"); //$NON-NLS-1$
   /** Error message for assigning from a plink that doesn't have a partnerRole */
   public final static String ERROR_PLINK_ASSIGN_FROM_PARTNERROE = AeMessages.getString("IAeValidationDefs.Plink.From.NoPartnerRole"); //$NON-NLS-1$
   /** Error message for nesting an isolated scope */
   public final static String ERROR_NESTED_ISOLATED_SCOPE = AeMessages.getString("IAeValidationDefs.NestedIsolatedScope"); //$NON-NLS-1$
   /** 
    * Error message for an isolated scope within an FCT handler that compensates 
    * an isolated scope. 
    */
   public final static String ERROR_NESTED_ISOLATED_SCOPE_FCT_SOURCE = AeMessages.getString("IAeValidationDefs.NestedIsolatedScope_source"); //$NON-NLS-1$
   /** 
    * Error message for an isolated scope that is being compensated from an
    * isolated scope within an FCT handler. 
    */
   public final static String ERROR_NESTED_ISOLATED_SCOPE_FCT_TARGET = AeMessages.getString("IAeValidationDefs.NestedIsolatedScope_target"); //$NON-NLS-1$
   /** Warning message for a bpws isolated scope that is not a leaf scope */
   public final static String WARNING_BPWS_SERIALIZABLE_LEAF = AeMessages.getString("IAeValidationDefs.BPWS.IsolatedScope"); //$NON-NLS-1$

   public final static String ERROR_TO_EXPR_FORMAT_INVALID = AeMessages.getString("IAeValidationDefs.InvalidExpressionToSpec"); //$NON-NLS-1$
   
   /**
    * Non-abstract processes need at least one activity (either a Pick or a Receive) with the 
    * createInstance flag set to true.
    */
   public final static String ERROR_NO_CREATE = AeMessages.getString("IAeValidationDefs.6") ; //$NON-NLS-1$
   
   /**
    * A required field is missing a value.
    * 
    * Arg 0 is the field name.
    */
   public final static String ERROR_FIELD_MISSING = AeMessages.getString("IAeValidationDefs.7") ; //$NON-NLS-1$
   
   /**
    * A required activity is missing from a container.
    * 
    * Arg 0 is the container name.
    */
   public final static String ERROR_ACTIVITY_MISSING = AeMessages.getString("IAeValidationDefs.8") ; //$NON-NLS-1$
   
   /** A forEach, onEvent, onAlarm requires a child scope */
   public static final String ERROR_REQUIRES_SCOPE_CHILD = AeMessages.getString("IAeValidationDefs.RequiresScopeChild") ; //$NON-NLS-1$
   
   /** Implicit variables are not allowed to have explicit declarations */
   public static final String ERROR_IMPLICIT_VARIABLE_EXPLICITLY_DEFINED = AeMessages.getString("IAeValidationDefs.ImplicitVariableDefined") ; //$NON-NLS-1$ 
   
   /**
    * Assign: must have at least one Copy.
    */
   public final static String ERROR_NO_COPY = AeMessages.getString("IAeValidationDefs.9") ; //$NON-NLS-1$

   /**
    * Assign: from can only be opaque in abstract process.
    */   
   public final static String ERROR_OPAQUE_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.10") ; //$NON-NLS-1$
   
   /**
    * OpaqueAcitity: OpaqueAcitity can be only in abstract process.
    */   
   public final static String ERROR_OPAQUE_ACTIVITY_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.OpaqueActivityNotAllowed") ; //$NON-NLS-1$
   

   /**
    * Switch: must have at least one Case.
    */
   public final static String SWITCH_MISSING_CASE = AeMessages.getString("IAeValidationDefs.SwitchMissingCase") ; //$NON-NLS-1$

   /**
    * If: must have at least one condition.
    */
   public final static String IF_MISSING_CONDITION = AeMessages.getString("IAeValidationDefs.IfMissingCondition") ; //$NON-NLS-1$
   
   /**
    * Pick: must have at least one onMessage.
    */
   public final static String ERROR_NO_ONMESSAGE = AeMessages.getString("IAeValidationDefs.12") ; //$NON-NLS-1$
   
   /**
    * Pick: if more than one onMessage, then there should be no onAlarm.
    */
   public final static String ERROR_ALARM_ON_CREATEINSTANCE = AeMessages.getString("IAeValidationDefs.13") ; //$NON-NLS-1$
   
   /**
    * If more than one Create Instance, their correlation sets need to match.
    */
   public final static String ERROR_CS_MISMATCH = AeMessages.getString("IAeValidationDefs.14") ; //$NON-NLS-1$
   
   /**
    * Pick: if more than one onMessage, then createInstance should be true.
    */
   public final static String ERROR_MULT_ONMSG_CREATE = AeMessages.getString("IAeValidationDefs.15") ; //$NON-NLS-1$
   
   /**
    * Link definition is invalid.
    * 
    * Arg 0 is the link name. 
    */
   public final static String ERROR_BAD_LINK = AeMessages.getString("IAeValidationDefs.16") ; //$NON-NLS-1$
   
   /**
    * Link has more than one source activity.
    * 
    * Arg 0 is the link name. 
    */
   public final static String ERROR_MULTI_SRC_LINK = AeMessages.getString("IAeValidationDefs.70") ; //$NON-NLS-1$
   
   /**
    * Link has more than one target activity.
    * 
    * Arg 0 is the link name. 
    */
   public final static String ERROR_MULTI_TARGET_LINK = AeMessages.getString("IAeValidationDefs.71") ; //$NON-NLS-1$
   
   /**
    * Link boundary crossing error.
    * 
    * Arg 0 is the link name.
    * Arg 1 is the boundary type.
    */
   public final static String ERROR_LINK_CROSSING = AeMessages.getString("IAeValidationDefs.17") ; //$NON-NLS-1$
   
   /**
    * Bad Link from Scope to a child target activity.
    * 
    * Arg 0 is the link name.
    */
   public final static String ERROR_SCOPE_LINK = AeMessages.getString("IAeValidationDefs.BAD_LINK_ERROR"); //$NON-NLS-1$
   
   /**
    * Link is part of an invalid graph cycle.
    * 
    * Arg 0 is the link name.
    */
   public final static String ERROR_LINK_CYCLE = AeMessages.getString("IAeValidationDefs.18") ; //$NON-NLS-1$

   /**
    * Terminate is not valid in an abstract process.
    */
   public final static String ERROR_TERM_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.19") ; //$NON-NLS-1$
   
   /**
    * Type specification for variable is missing.
    * 
    * Arg 0 is the variable name.
    */
   public final static String ERROR_VAR_HAS_NO_TYPE = AeMessages.getString("IAeValidationDefs.20") ; //$NON-NLS-1$
   
   /**
    * Partner link is unresolved and can't be found
    * 
    * Arg 0 is the partner link name.
    */
   public final static String ERROR_PARTNER_LINK_NOT_FOUND = AeMessages.getString("IAeValidationDefs.21") ; //$NON-NLS-1$
   
   /**
    * Partner link has no my role
    * 
    * Arg 0 is the partner link name.
    */
   public final static String ERROR_PARTNER_LINK_MISSING_MYROLE = AeMessages.getString("IAeValidationDefs.22") ; //$NON-NLS-1$
   
   /**
    * Partner link has no partner role
    * 
    * Arg 0 is the partner link name.
    */
   public final static String ERROR_PARTNER_LINK_MISSING_PARTNERROLE = AeMessages.getString("IAeValidationDefs.23") ; //$NON-NLS-1$
   
   /**
    * Partner link has no myrole
    * 
    * Arg 0 is the partner link name.
    * Arg 1 is the partner link type role.
    * Arg 2 is the portType for role.
    * Arg 3 is the portType for operation.
    */
   public final static String ERROR_PORTTYPE_MISMATCH = AeMessages.getString("IAeValidationDefs.24") ; //$NON-NLS-1$
   
   /**
    * Scope is unresolved.
    * 
    * Arg 0 is the compensate activity's scope.
    */
   public final static String ERROR_SCOPE_NOT_FOUND = AeMessages.getString("IAeValidationDefs.25"); //$NON-NLS-1$
   
   /**
    * More than one Scope with same name.
    * 
    * Arg 0 is the compensate activity's target scope.
    */
   public final static String TOO_MANY_SCOPES_FOUND = AeMessages.getString("IAeValidationDefs.DUPLICATE_SCOPE"); //$NON-NLS-1$

   /**
    * Variable is unresolved and can't be found.
    * 
    * Arg 0 is the variable name.
    */
   public final static String ERROR_VAR_NOT_FOUND = AeMessages.getString("IAeValidationDefs.26") ; //$NON-NLS-1$
   
   /**
    * A message part is not declared and can't be found.
    * 
    * Arg 0 is the part name.
    * Arg 1:2 is the message type's QName nsURI:localPart.
    */
   public final static String ERROR_VAR_PART_NOT_FOUND = AeMessages.getString("IAeValidationDefs.27") ; //$NON-NLS-1$

   /**
    * A property alias references a message part that doesn't exist
    * 
    * Arg 0:1 message qname
    * Arg 2:3 property qname
    * Arg 4 part name
    */
   public final static String ERROR_PROPERTY_ALIAS_BAD_PART = AeMessages.getString("IAeValidationDefs.PropertyAliasBadPart") ; //$NON-NLS-1$

   
   /**
    * A specified correlation set is unresolved and can't be found.
    * 
    * Arg 0 is the name of the correlation set.
    */
   public final static String ERROR_CORR_SET_NOT_FOUND = AeMessages.getString("IAeValidationDefs.28") ; //$NON-NLS-1$
   
   /**
    * A partner link is missing partner link type.
    * 
    * Arg 0 is the partner link's name.
    */
   public final static String ERROR_PARTNER_LINK_HAS_NO_TYPE = AeMessages.getString("IAeValidationDefs.29") ; //$NON-NLS-1$
   
   /**
    * A partner link type is unresolved and can't be found.
    * 
    * Arg 0:1 is the partner link type's QName nsURI:localPart.
    */
   public final static String ERROR_PARTNER_LINK_TYPE_NOT_FOUND = AeMessages.getString("IAeValidationDefs.30") ; //$NON-NLS-1$
   
   /**
    * A specified role is unresolved and can't be found.
    * 
    * Arg 0 is the role's name.
    * Arg 1 is the partner link type local name.
    */
   public final static String ERROR_ROLE_NOT_FOUND = AeMessages.getString("IAeValidationDefs.31") ; //$NON-NLS-1$
   
   /** 
    * Plink w/o partner role has initializePartnerRole attribute 
    * Arg 0 is the plink's name.
    */
   public final static String ERROR_INIT_PARTNER_ROLE_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.InitializePartnerRoleNotAllowed") ; //$NON-NLS-1$

   /**
    * A specified role has no port type assignment.
    * 
    * Arg 0 is the role's name.
    * Arg 1 is the partner link type local name.
    */
   public final static String ERROR_ROLE_HAS_NO_PORTTYPE = AeMessages.getString("IAeValidationDefs.32") ; //$NON-NLS-1$
   
   /**
    * A specified port type is unresolved and can't be found.
    * 
    * Arg 0:1 is the port type's QName nsURI:localPart.
    */
   public final static String ERROR_PORT_TYPE_NOT_FOUND = AeMessages.getString("IAeValidationDefs.33") ; //$NON-NLS-1$
   
   /** 
    * An element variable is being used for message data consumption or production where
    * a message variable is required 
    */
   public static final String ERROR_ELEMENT_VARIABLE_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.ElementVariableNotAllowed") ; //$NON-NLS-1$
   
   /** 
    * The wrong element variable is being used to send/receive message data.
    * 
    * Arg 0 operation
    *     1:2 actual element variable qname
    *     3:4 expected message qname
    *     5:6 expected element qname
    */
   public static final String ERROR_WRONG_ELEMENT_VARIABLE = AeMessages.getString("IAeValidationDefs.WrongElementVariable") ; //$NON-NLS-1$

   /** 
    * The wrong message variable is being used to send/receive message data.
    * 
    * Arg 0 operation
    *     1:2 actual msg variable qname
    *     3:4 expected message qname
    */
   public static final String ERROR_WRONG_MESSAGE_VARIABLE = AeMessages.getString("IAeValidationDefs.WrongMessageVariable") ; //$NON-NLS-1$

   /** A message variable is required (bpws 1.1 error message) */
   public static final String ERROR_MESSAGE_VARIABLE_REQUIRED = AeMessages.getString("IAeValidationDefs.MessageVariableRequired") ; //$NON-NLS-1$
   
   /** wsio activities cannot have simple/complex type variables for their message data consumption/production */
   public static final String ERROR_TYPE_VARIABLE_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.TypeVariableNotAllowed") ; //$NON-NLS-1$
   
   /** wsio activities that consumes messages does not have a valid strategy for consuming the message data */
   public static final String ERROR_MESSAGE_CONSUMER_STRATEGY = AeMessages.getString("IAeValidationDefs.MessageConsumerStrategyNotSet"); //$NON-NLS-1$
   /** wsio activities that produces messages does not have a valid strategy for producing the message data */
   public static final String ERROR_MESSAGE_PRODUCER_STRATEGY = AeMessages.getString("IAeValidationDefs.MessageProducerStrategyNotSet"); //$NON-NLS-1$
   
   /** 
    * wsio activities that consumes messages does not have a valid strategy for consuming the message data
    * Arg {0}:{1} message type
    *     {2} - operation  
    */
   public static final String ERROR_EMPTY_MESSAGE_CONSUMER_STRATEGY_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.EmptyMessageConsumerStrategyNotAllowed"); //$NON-NLS-1$

   /** 
    * wsio activities that consumes messages does not have a valid strategy for producing the message data
    * Arg {0}:{1} message type
    *     {2} - operation  
    */
   public static final String ERROR_EMPTY_MESSAGE_PRODUCER_STRATEGY_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.EmptyMessageProducerStrategyNotAllowed"); //$NON-NLS-1$

   /**
    * A specified operation with given input and is unresolved and can't be found.
    * 
    * Arg 0 is the operation's name.
    * Arg 1:2 is the QName of the invalid input message type.
    * Arg 3:4 is the QName of the invalid output message type
    */
   public final static String ERROR_OPERATION_INOUT_NOT_FOUND = AeMessages.getString("IAeValidationDefs.34") ; //$NON-NLS-1$
   
   /**
    * A specified operation with given input is unresolved and can't be found.
    * 
    * Arg 0 is the operation's name.
    * Arg 1:2 is the QName of the invalid input message type.
    */
   public final static String ERROR_OPERATION_IN_NOT_FOUND = AeMessages.getString("IAeValidationDefs.35") ; //$NON-NLS-1$

   /**
    * A specified operation with given output is unresolved and can't be found.
    * 
    * Arg 0 is the operation's name.
    * Arg 1:2 is the QName of the invalid output message type
    */
   public final static String ERROR_OPERATION_OUT_NOT_FOUND = AeMessages.getString("IAeValidationDefs.36") ; //$NON-NLS-1$
   
   /**
    * A specified operation for the given port type cannot be found
    * 
    * Arg 0 is the operation's name.
    * Arg 1:2 is the QName of the port type
    */
   public final static String ERROR_OPERATION_NOT_FOUND = AeMessages.getString("IAeValidationDefs.OperationNotFound") ; //$NON-NLS-1$

   /** A correlation pattern is only allowed to be used on an invoke */
   public static final String ERROR_CORRELATION_PATTERN_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.PatternNotAllowed"); //$NON-NLS-1$
   
   /** A correlation pattern is required for an invoke */
   public static final String ERROR_CORRELATION_PATTERN_REQUIRED = AeMessages.getString("IAeValidationDefs.PatternRequired"); //$NON-NLS-1$

   /**
    * A correlation pattern is set to OUT or OUT-IN, but the invoke is a one-way
    * 
    */
   public final static String ERROR_CORRELATION_OUT_PATTERN_MISMATCH = AeMessages.getString("IAeValidationDefs.38") ; //$NON-NLS-1$
   
   /** A correlation pattern value is not valid. */
   public static final String ERROR_CORRELATION_INVALID_PATTERN = AeMessages.getString("IAeValidationDefs.InvalidPattern");  //$NON-NLS-1$

   /**
    * A specified property is unresolved and can't be found.
    * 
    * Arg 0:1 is the property's QName nsURI:localPart.
    */
   public final static String ERROR_PROP_NOT_FOUND = AeMessages.getString("IAeValidationDefs.39") ; //$NON-NLS-1$
   
   /**
    * A specified message type is unresolved and can't be found.
    * 
    * Arg 0:1 is the message type's QName nsURI:localPart.
    */
   public final static String ERROR_MSG_SPEC_NOT_FOUND = AeMessages.getString("IAeValidationDefs.40") ; //$NON-NLS-1$

   /**
    * A specified element is unresolved and can't be found.
    * 
    * Arg 0:1 is the element's QName nsURI:localPart.
    */
   public final static String ERROR_ELEMENT_SPEC_NOT_FOUND = AeMessages.getString("IAeValidationDefs.41") ; //$NON-NLS-1$
   
   /**
    * A specified type specification is unresolved and can't be found.
    * 
    * Arg 0:1 is the type specification's QName nsURI:localPart.
    */
   public final static String ERROR_TYPE_SPEC_NOT_FOUND = AeMessages.getString("IAeValidationDefs.42") ; //$NON-NLS-1$
   
   /**
    * One or more property aliases for a variable are undefined.
    * 
    * Arg 0 is the variable name.
    * Arg 1 is the variable's type name.
    * Arg 2 is a comma-delimited list of the names of properties that are missing aliases.
    */
   public final static String ERROR_CORRELATION_PROP_ALIASES_NOT_FOUND = 
      AeMessages.getString("IAeValidationDefs.0") ; //$NON-NLS-1$
      
   /**
    * Correlation set has no properties
    * 
    * Arg 0 is the correlation set name.
    */
   public final static String ERROR_CORR_SET_PROPS_NOT_FOUND =
      AeMessages.getString("IAeValidationDefs.45") ; //$NON-NLS-1$
      
   /**
    * The propertyAlias used for correlation is for a complex type but it doesn't specify a query. 
    *  This will fail at runtime unless the type is an "anyType" and is a simple type at runtime. 
    * 
    * Arg 0:1 message qname
    * Arg 2:3 property qname
    * Arg 4 part
    */
   public final static String ERROR_NO_QUERY_FOR_PROP_ALIAS = 
      AeMessages.getString("IAeValidationDefs.MissingPropertyAliasQuery") ; //$NON-NLS-1$

   /**
    * An invalid expression (unexpected error) was encountered. Arg 0 is the invalid expression. Arg 1 is the reason.
    */
   public final static String ERROR_INVALID_EXPRESSION = AeMessages.getString("IAeValidationDefs.INVALID_EXPRESSION"); //$NON-NLS-1$

   public final static String ERROR_EMPTY_START_EXPRESSION = AeMessages.getString("IAeValidationDefs.EMPTY_START_EXPRESSION"); //$NON-NLS-1$
   public final static String ERROR_EMPTY_FINAL_EXPRESSION = AeMessages.getString("IAeValidationDefs.EMPTY_FINAL_EXPRESSION"); //$NON-NLS-1$
   public final static String ERROR_EMPTY_COMPLETION_CONDITION_EXPRESSION = AeMessages.getString("IAeValidationDefs.EMPTY_COMPLETION_CONDITION_EXPRESSION"); //$NON-NLS-1$

   /**
    * An invalid XPath expression was encountered. Arg 0 is the invalid expression. Arg 1 is the reason.
    */
   public final static String ERROR_INVALID_XPATH = AeMessages.getString("AeXPathExpressionValidator.INVALID_XPATH"); //$NON-NLS-1$

   /** signals an illegal part attribute for a copy operation's from/to */
   public final static String ERROR_PART_USAGE = AeMessages.getString("IAeValidationDefs.ERROR_PART_USAGE"); //$NON-NLS-1$
   
   /**
    * Variable Type Mismatch for an Activity (i.e., the variable used doesn't match the type 
    * needed by the activity.
    * 
    * Arg 0 is the variable name.
    * Arg 1:2 is the QName of the variable's type.
    * Arg 3:4 is the QName of the activity's required type.
    */
   public final static String ERROR_VAR_TYPE_MISMATCH = AeMessages.getString("IAeValidationDefs.49") ; //$NON-NLS-1$
   
   /** 
    * Variable type mismatch where reference was to a message part but the variable def is not a message
    * 
    *  Arg 0 = var name
    *  Arg 1:2 = var type Qname
    *  Arg 3 = message part
    */
   public static final String ERROR_VAR_TYPE_MISMATCH_MESSAGE = AeMessages.getString("IAeValidationDefs.ExpectedMessageType"); //$NON-NLS-1$

   /** 
    * Variable type mismatch where reference was to an element but the variable def is not an element
    * 
    *  Arg 0 = var name
    *  Arg 1:2 = var type Qname
    */
   public static final String ERROR_VAR_TYPE_MISMATCH_ELEMENT = AeMessages.getString("IAeValidationDefs.ExpectedElement"); //$NON-NLS-1$

   /**
    * A fault name is unresolved and can't be found.
    * 
    * Arg 0 is the fault name.
    */
   public final static String ERROR_FAULT_NAME_NOT_FOUND = AeMessages.getString("IAeValidationDefs.50") ; //$NON-NLS-1$
   
   /**
    * A fault handler has not activity.
    */
   public final static String ERROR_EMPTY_FAULT_HANDLER = AeMessages.getString("IAeValidationDefs.51") ; //$NON-NLS-1$
   
   /** A container is missing its required activity */
   // TODO (MF) use this error message in place of other more specific ones
   public static final String ERROR_EMPTY_CONTAINER = AeMessages.getString("IAeValidationDefs.EmptyContainer"); //$NON-NLS-1$
   
   /** Error for a compensation handler on the root scope of a FCT handler */
   public static final String ERROR_ROOT_SCOPE_FCT_HANDLER = AeMessages.getString("IAeValidationDefs.RootScopeFCTHandler"); //$NON-NLS-1$

   /**
    * Error message for illegal catch pattern for BPWS
    */
   public final static String ERROR_BPWS_CATCH_PATTERN = AeMessages.getString("IAeValidationDefs.BPWS_CATCH_PATTERN" ); //$NON-NLS-1$

   /**
    * Error message for illegal catch pattern for WSBPEL
    */
   public final static String ERROR_WSBPEL_CATCH_PATTERN = AeMessages.getString("IAeValidationDefs.WSBPEL_CATCH_PATTERN" ); //$NON-NLS-1$

   /**
    * Error message for illegal fault handler constructs in WSBPEL.
    */
   public final static String ERROR_ILLEGAL_FH_CONSTRUCTS = AeMessages.getString("IAeValidationDefs.ERROR_ILLEGAL_FH_CONSTRUCTS" ); //$NON-NLS-1$
   
   /**
    *  A fault handler catch a standard BPEL fault while the exitOnStandardFault is set to yes in scope/process.
    */
   public final static String ERROR_ILLEGAL_CATCH_FOR_EXIT_ON_STD_FAULT = AeMessages.getString("IAeValidationDefs.ERROR_ILLEGAL_CATCH_FOR_EXIT_ON_STD_FAULT" ); //$NON-NLS-1$
      
   /**
    * A compensate activity is in the wrong place.
    * 
    * fixme this error message needs to be broken into a 2.0 and a 1.1 version
    */
   public final static String ERROR_MISPLACED_COMPENSATE = AeMessages.getString("IAeValidationDefs.52") ; //$NON-NLS-1$

   /**
    * A compensate activity is in the wrong place.
    */
   public final static String ERROR_MISPLACED_RETHROW = AeMessages.getString("IAeValidationDefs.MisplacedRethrow") ; //$NON-NLS-1$

   /**
    * A part type specification is unresolved or can't be found.
    * 
    * Arg 0 is partname
    * Arg 1:2 is the message type specification's QName nsURI:localPart.
    * Arg 3 Exception message
    */
   public final static String ERROR_DISCOVERING_PART_TYPE_SPECS = AeMessages.getString("IAeValidationDefs.53") ; //$NON-NLS-1$

   /**
    * The object name is invalid, ie: not a valid NCName.
    * 
    * Arg 0 The invalid name
    */
   public final static String ERROR_INVALID_NAME = AeMessages.getString("IAeValidationDefs.69") ; //$NON-NLS-1$
   
   /**
    * No validator could be found for the expression language.
    * 
    * Arg 0 - the expression language.
    */
   public final static String ERROR_NO_VALIDATOR_FOR_LANGUAGE = AeMessages.getString("IAeValidationDefs.NO_VALIDATOR_FOR_EXPRLANG_ERROR"); //$NON-NLS-1$
   
   /** Error when we encounter multiple children but only expected one */
   public static final String ERROR_MULTIPLE_CHILDREN_FOUND = AeMessages.getString("IAeValidationDefs.MultipleChildrenError"); //$NON-NLS-1$
   
   /** Error when an empty 'eventHandlers' construct is found. */
   public static final String ERROR_EMPTY_EVENT_HANDLER = AeMessages.getString("IAeValidationDefs.MissingEventHandlerError"); //$NON-NLS-1$
   
   /**
    * A fault variable MUST be an element or messageType
    * 
    * Arg 0 is the fault variable name.
    */
   public static final String ERROR_FAULT_TYPE = AeMessages.getString("IAeValidationDefs.FaultType") ; //$NON-NLS-1$
   
   /** A fault variable MUST be a messageType for BPWS 1.1 */
   public static final String ERROR_FAULT_MESSAGETYPE_REQUIRED = AeMessages.getString("IAeValidationDefs.FaultMessageTypeRequired"); //$NON-NLS-1$
   
   /////////////////////////////////
   // Warning codes - add as needed.
   /////////////////////////////////

   public final static String WARNING_TEST = "WarningTest" ; //$NON-NLS-1$

   /**
    * A defined partner link is never used.
    * 
    * Arg 0 is the partner link name.
    */
   public final static String WARNING_PARTNER_LINK_NOT_USED = AeMessages.getString("IAeValidationDefs.56") ; //$NON-NLS-1$

   /**
    * A defined variable is never used.
    * 
    * Arg 0 is the variable name.
    */
   public final static String WARNING_VARIABLE_NOT_USED = AeMessages.getString("IAeValidationDefs.57") ; //$NON-NLS-1$

   /**
    * A defined variable is never read.
    * 
    * Arg 0 is the variable name.
    */
   public final static String WARNING_VARIABLE_NOT_READ = AeMessages.getString("IAeValidationDefs.VariableNotRead") ; //$NON-NLS-1$

   /**
    * A defined variable is never written to.
    * 
    * Arg 0 is the variable name.
    */
   public final static String WARNING_VARIABLE_NO_INIT = AeMessages.getString("IAeValidationDefs.VariableImproperIO") ; //$NON-NLS-1$

   /**
    * A defined variable is never written to.
    * 
    * Arg 0 is the variable name.
    */
   public final static String WARNING_VARIABLE_NOT_WRITTEN_TO = AeMessages.getString("IAeValidationDefs.VariableNotWrittenTo") ; //$NON-NLS-1$

   
   /** A message variable can only be initialized from a variable of the same message type */
   public final static String WARNING_INVALID_MESSAGE_VARIABLE_INIT = AeMessages.getString("IAeValidationDefs.InvalidMessageVariableInit"); //$NON-NLS-1$
   
   /**
    * A defined correlation set is never used.
    * 
    * Arg 0 is the correlation set name.
    */
   public final static String WARNING_CORR_SET_NOT_USED = AeMessages.getString("IAeValidationDefs.58") ; //$NON-NLS-1$

   /**
    * A defined variable is never used.
    * 
    * Arg 0 is extension.
    */
   public final static String WARNING_EXTENSION_NOT_USED = AeMessages.getString("IAeValidationDefs.ExtensionNotUsedError") ; //$NON-NLS-1$
   
   /**
    * Link not used.
    * 
    * Arg 0 is the link name. 
    */
   public final static String WARNING_LINK_NOT_USED = AeMessages.getString("IAeValidationDefs.UnusedLinkWarning"); //$NON-NLS-1$

   /**
    * Import is missing it's location specifier.
    */
   public final static String WARNING_MISSING_IMPORT_LOCATION = AeMessages.getString("IAeValidationDefs.59") ; //$NON-NLS-1$
   
   /**
    * Namespace has an invalid location specifier.
    * 
    * Arg 0 is the namespace prefix.
    */
   public final static String WARNING_INVALID_IMPORT_LOCATION = AeMessages.getString("IAeValidationDefs.60") ; //$NON-NLS-1$
   
   /**
    * Receive: no correlation set assigned and the Receive is not a create instance.
    */
   public final static String WARNING_NO_CORR_SET_NO_CREATE = AeMessages.getString("IAeValidationDefs.61") ; //$NON-NLS-1$

   /**
    * A fault name is unresolved and can't be found.
    * 
    * Arg 0 is the fault name.
    */
   public final static String WARN_FAULT_NAME_NOT_CAUGHT = AeMessages.getString("IAeValidationDefs.62") ; //$NON-NLS-1$
   
   /**
    * Enabled instance compensation set to No - compensation handler invalid.
    */
   public final static String WARN_COMPENSATION_HANDLER_NOT_ENABLED = AeMessages.getString("IAeValidationDefs.63") ; //$NON-NLS-1$
   
   /**
    * Missing single quotes to specify a constant variable name.
    * 
    * Arg 1 is the variable name.
    */
   public final static String WARN_NON_CONST_VARNAME = AeMessages.getString("IAeValidationDefs.64") ; //$NON-NLS-1$

   /**
    * A variable type has been overloaded within a scope definition
    * 
    * Arg 0 is the variable name.
    */
   public final static String WARNING_VARIABLE_TYPE_OVERLOADED = AeMessages.getString("IAeValidationDefs.65") ; //$NON-NLS-1$

   /**
    * A variable has supplied a query expression without a required part name
    * 
    * Arg 0 is the variable name.
    */
   public final static String WARNING_VARIABLE_PART_REQUIRED = AeMessages.getString("IAeValidationDefs.66") ; //$NON-NLS-1$

   /**
    * A variable has supplied a query expression for a simple type variable
    * 
    * Arg 0 is the variable name.
    */
   public final static String WARNING_VARIABLE_QUERY_NOT_SUPPORTED = AeMessages.getString("IAeValidationDefs.67") ; //$NON-NLS-1$
   
   /**
    * Invalid literal found in join conditio expression.
    * 
    * Arg 0 is the invalid literal.
    */
   public static final String INVALID_LITERAL_IN_JOIN_CONDITION = AeMessages.getString("IAeValidationDefs.75"); //$NON-NLS-1$

   /**
    * Link in joinCondition could not be found.
    * 
    * Arg 0 is the link name.
    * Arg 1 is the joinCondition expression 
    */
   public static final String NO_LINK_FOUND_FOR_JOIN_CONDITION = AeMessages.getString("IAeValidationDefs.76"); //$NON-NLS-1$
   
   /**
    * Invalid match between number of link names and number of getLinkStatus function calls.
    * 
    * Arg 0 is the joinCondition expression 
    */
   public static final String INVALID_JOIN_CONDITION = AeMessages.getString("IAeValidationDefs.77"); //$NON-NLS-1$
   
   /**
    * Found a getLinkStatus with no args.
    * 
    * Arg 0 is the entire joinCondition expression.
    */
   public static final String EMPTY_GET_LINK_STATUS_FUNCTION = AeMessages.getString("IAeValidationDefs.72"); //$NON-NLS-1$

   /**
    * Invalid nesting of functions inside joinCondition expression.
    * 
    * Arg 0 is the entire joinCondition expression.
    */
   public static final String INVALID_NESTING_IN_LINK_STATUS_FUNCTION = AeMessages.getString("IAeValidationDefs.73"); //$NON-NLS-1$
   
   /**
    * Invalid arg present in getLinkStatus function of joinCondition.
    * 
    * Arg 0 is the joinCondition expression.
    */
   public static final String INVALID_ARG_IN_LINK_STATUS_FUNCTION = AeMessages.getString("IAeValidationDefs.74"); //$NON-NLS-1$
   
   /**
    * A non-persistent process uses subprocess invoke protocol.
    * 
    * Arg 0 is partner link name.
    */
   public final static String WARNING_NONPERSISTENT_SUBPROCESS_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.NONPERSIST_SUBPROCESS_NOT_ALLOWED") ; //$NON-NLS-1$

   /**
    * A non-persistent process uses retry policies.
    * 
    * Arg 0 is partner link name.
    */
   public final static String ERROR_NONPERSISTENT_RETRYPOLICY_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.NONPERSIST_RETRY_POLICY_NOT_ALLOWED") ; //$NON-NLS-1$
   
   /**
    * A non-persistent process has BPEL constructs (such as OnMessage) which are not allowed.
    * 
    * Arg 0 is BPEL constuct name.
    */   
   public final static String ERROR_NONPERSISTENT_ACTIVITY_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.NONPERSIST_ACTIVITY_NOT_ALLOWED") ; //$NON-NLS-1$

   /**
    * A non-persistent process has multiple BPEL Receive activities which is not allowed.
    * 
    * Arg 0 is number of receive activities found.
    */   
   public final static String ERROR_NONPERSISTENT_MULTIPLE_RECEIVES_NOT_ALLOWED = AeMessages.getString("IAeValidationDefs.NONPERSIST_MULTIPLE_RECEIVES_NOT_ALLOWED") ; //$NON-NLS-1$

   /**
    * A non-persistent process does not have a createInstance receive or pick.
    */      
   public final static String ERROR_NONPERSISTENT_CREATE_INSTANCE_NOT_FOUND = AeMessages.getString("IAeValidationDefs.NONPERSIST_CREATE_INSTANCE_NOT_FOUND") ; //$NON-NLS-1$
   
   /**
    * The extension was not understood, but the declaration of the extension indicated mustUnderstand=='yes'.
    */
   public final static String ERROR_DID_NOT_UNDERSTAND_EXTENSION = AeMessages.getString("IAeValidationDefs.UnknownExtensionError"); //$NON-NLS-1$
   
   /**
    * An extension was found but the namespace was not declared in the list of extensions.
    */
   public final static String ERROR_UNDECLARED_EXTENSION = AeMessages.getString("IAeValidationDefs.UndeclaredExtensionError"); //$NON-NLS-1$
   
   /**
    * An attribute was found but was not read.
    */
   public final static String ERROR_UNEXPECTED_ATTRIBUTE = AeMessages.getString("IAeValidationDefs.UnexpectedAttribute"); //$NON-NLS-1$
   
   /**
    * An invalid literal was found.
    */
   public final static String ERROR_INVALID_LITERAL = AeMessages.getString("IAeValidationDefs.InvalidLiteral"); //$NON-NLS-1$
   
   /**
    * A xsi:schemaLocation attribute was found on the literal.
    */
   public final static String WARNING_SCHEMA_LOCATION_IN_LITERAL = AeMessages.getString("IAeValidationDefs.XsiSchemaLocationFoundInLiteral"); //$NON-NLS-1$
   
   /**
    * Missing import for a reference. 
    */
   public final static String WARNING_MISSING_IMPORT = AeMessages.getString("IAeValidationDefs.MissingImport"); //$NON-NLS-1$
   
   // Information codes - add as needed.
   /////////////////////////////////////

   public final static String INFO_TEST = "InfoTest" ; //$NON-NLS-1$

}
