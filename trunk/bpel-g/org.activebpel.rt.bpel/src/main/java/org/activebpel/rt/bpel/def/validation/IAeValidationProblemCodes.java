// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/IAeValidationProblemCodes.java,v 1.1 2008/03/20 16:00:00 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation;

/**
 * Contains the list of BPEL Error code csonstants.
 */
public interface IAeValidationProblemCodes
{
   /** namespace for severity and codes files */
   public static String SEVERITY_NAMESPACE = "urn:activebpel:validator:severity";                     //$NON-NLS-1$
   
   /** Codes used by all validators */
   public static String BPEL_CORR_ABSOLUTE_PATH_SYNTAX_CODE = "BPEL_CORR_ABSOLUTE_PATH_SYNTAX";       //$NON-NLS-1$
   public static String BPEL_MSG_ABSOLUTE_PATH_SYNTAX_CODE = "BPEL_MSG_ABSOLUTE_PATH_SYNTAX";         //$NON-NLS-1$
   public static String BPEL_COMPLEX_ABSOLUTE_PATH_SYNTAX_CODE = "BPEL_COMPLEX_ABSOLUTE_PATH_SYNTAX"; //$NON-NLS-1$
   
   public static String BPEL_SEQ_ACTIVITY_MISSING_CODE = "BPEL_SEQ_ACTIVITY_MISSING";                 //$NON-NLS-1$
   public static String BPEL_ACTIVITY_MISSING_CODE = "BPEL_ACTIVITY_MISSING";                         //$NON-NLS-1$
   public static String BPEL_ELSE_ACTIVITY_MISSING_CODE = "BPEL_ELSE_ACTIVITY_MISSING";               //$NON-NLS-1$
   public static String BPEL_ELSEIF_ACTIVITY_MISSING_CODE = "BPEL_ELSEIF_ACTIVITY_MISSING";           //$NON-NLS-1$
   public static String BPEL_FLOW_ACTIVITY_MISSING_CODE = "BPEL_FLOW_ACTIVITY_MISSING";               //$NON-NLS-1$

   public static String BPEL_ALARM_ON_CREATEINSTANCE_CODE = "BPEL_ALARM_ON_CREATEINSTANCE";           //$NON-NLS-1$
   
   public static String BPEL_ASSIGNCOPY_MISSING_FROM_TO_CODE = "BPEL_ASSIGNCOPY_MISSING_FROM_TO";     //$NON-NLS-1$

   public static String BPEL_BAD_LINK_CODE = "BPEL_BAD_LINK";                                         //$NON-NLS-1$

   public static String BPWS_BPEL_1_1_EXT_ACTIVITY_USED_CODE = "BPWS_BPEL_1_1_EXT_ACTIVITY_USED";     //$NON-NLS-1$

   public static String WSBPEL_BPEL_2_0_EXTENSION_USED_CODE = "WSBPEL_BPEL_2_0_EXTENSION_USED";       //$NON-NLS-1$

   public static String WSBPEL_EXTACT_BPEL_AT_INVALID_LOCATION_CODE = "WSBPEL_EXTACT_BPEL_AT_INVALID_LOCATION";   //$NON-NLS-1$
   public static String WSBPEL_EXTELEM_BPEL_AT_INVALID_LOCATION_CODE = "WSBPEL_EXTELEM_BPEL_AT_INVALID_LOCATION"; //$NON-NLS-1$

   public static String BPWS_ILLEGAL_FH_CONSTRUCTS_CODE = "BPWS_ILLEGAL_FH_CONSTRUCTS";               //$NON-NLS-1$

   public static String BPEL_BPWS_MESSAGE_EXCHANGE_CODE = "BPEL_BPWS_MESSAGE_EXCHANGE";               //$NON-NLS-1$
   
   public static String BPWS_SERIALIZABLE_LEAF_CODE = "BPWS_SERIALIZABLE_LEAF";                       //$NON-NLS-1$
   public static String BPWS_UNEXPECTED_ATTRIBUTE_CODE = "BPWS_UNEXPECTED_ATTRIBUTE";                 //$NON-NLS-1$

   public static String BPEL_CATCH_INVALID_CODE = "BPEL_CATCH_INVALID";                               //$NON-NLS-1$

   public static String BPEL_CHECK_START_ACTIVITY_CODE = "BPEL_CHECK_START_ACTIVITY";                 //$NON-NLS-1$
   
   public static String BPEL_COMPENSATE_SCOPE_EMPTY_CODE = "BPEL_COMPENSATE_SCOPE_EMPTY";             //$NON-NLS-1$

   public static String BPWS_COMPENSATION_HANDLER_NOT_ENABLED_CODE = "BPWS_COMPENSATION_HANDLER_NOT_ENABLED"; //$NON-NLS-1$

   public static String BPEL_CORR_SET_MISMATCH_CODE = "BPEL_CORR_SET_MISMATCH";                       //$NON-NLS-1$

   public static String BPEL_CORR_SET_NOT_FOUND_CODE = "BPEL_CORR_SET_NOT_FOUND";                     //$NON-NLS-1$

   public static String BPEL_CORR_SET_NOT_USED_CODE = "BPEL_CORR_SET_NOT_USED";                       //$NON-NLS-1$

   public static String BPEL_CORR_SET_PROPS_NOT_FOUND_CODE = "BPEL_CORR_SET_PROPS_NOT_FOUND";         //$NON-NLS-1$

   public static String BPEL_CORRELATION_PATTERN_NOT_ALLOWED_CODE = "BPEL_CORRELATION_PATTERN_NOT_ALLOWED"; //$NON-NLS-1$
   
   public static String BPEL_CORRELATION_PATTERN_REQUIRED_CODE = "BPEL_CORRELATION_PATTERN_REQUIRED"; //$NON-NLS-1$

   public static String BPEL_DISCOVERING_PART_TYPE_SPECS_CODE = "BPEL_DISCOVERING_PART_TYPE_SPECS";   //$NON-NLS-1$

   public static String BPEL_ELEMENT_VARIABLE_NOT_ALLOWED_CODE = "BPEL_ELEMENT_VARIABLE_NOT_ALLOWED"; //$NON-NLS-1$

   public static String BPEL_EMPTY_COMPLETION_CONDITION_EXPRESSION_CODE = "BPEL_EMPTY_COMPLETION_CONDITION_EXPRESSION"; //$NON-NLS-1$

   public static String BPEL_COMP_EMPTY_CONTAINER_CODE = "BPEL_COMP_EMPTY_CONTAINER";                 //$NON-NLS-1$
   public static String BPEL_TERM_EMPTY_CONTAINER_CODE = "BPEL_TERM_EMPTY_CONTAINER";                 //$NON-NLS-1$
   public static String BPEL_FROM_EMPTY_CONTAINER_CODE = "BPEL_FROM_EMPTY_CONTAINER";                 //$NON-NLS-1$
   public static String BPEL_TO_EMPTY_CONTAINER_CODE = "BPEL_TO_EMPTY_CONTAINER";                     //$NON-NLS-1$

   public static String BPEL_EMPTY_EVENT_HANDLER_CODE = "BPEL_EMPTY_EVENT_HANDLER";                   //$NON-NLS-1$
   
   public static String BPEL_CATCHALL_EMPTY_FAULT_HANDLER_CODE = "BPEL_CATCHALL_EMPTY_FAULT_HANDLER"; //$NON-NLS-1$
   public static String BPEL_EMPTY_FAULT_HANDLER_CODE = "BPEL_EMPTY_FAULT_HANDLER";                   //$NON-NLS-1$

   public static String BPEL_EMPTY_FINAL_EXPRESSION_CODE = "BPEL_EMPTY_FINAL_EXPRESSION";             //$NON-NLS-1$
   
   public static String BPEL_EMPTY_MESSAGE_STRATEGY_CODE = "BPEL_EMPTY_MESSAGE_STRATEGY";             //$NON-NLS-1$

   public static String BPEL_EMPTY_START_EXPRESSION_CODE = "BPEL_EMPTY_START_EXPRESSION";             //$NON-NLS-1$

   public static String BPEL_EMPTY_VALIDATE_CODE = "BPEL_EMPTY_VALIDATE";                             //$NON-NLS-1$

   public static String BPEL_MISPLACED_RETHROW_CODE = "BPEL_MISPLACED_RETHROW";                       //$NON-NLS-1$

   public static String BPEL_EXCEPTION_DURING_VALIDATION_CODE = "BPEL_EXCEPTION_DURING_VALIDATION";   //$NON-NLS-1$

   public static String BPEL_EXPRESSION_ERROR_CODE = "BPEL_EXPRESSION_ERROR";                         //$NON-NLS-1$
   public static String BPEL_EXPRESSION_INFO_CODE = "BPEL_EXPRESSION_INFO";                           //$NON-NLS-1$
   public static String BPEL_EXPRESSION_WARNING_CODE = "BPEL_EXPRESSION_WARNING";                     //$NON-NLS-1$

   public static String BPWS_EXTENSIBILITY_ACTIVITY_CODE = "BPWS_EXTENSIBILITY_ACTIVITY";             //$NON-NLS-1$

   public static String BPWS_EXTENSIBILITY_ATTRIBUTE_CODE = "BPWS_EXTENSIBILITY_ATTRIBUTE";           //$NON-NLS-1$

   public static String BPWS_EXTENSIBILITY_ELEMENT_CODE = "BPWS_EXTENSIBILITY_ELEMENT";               //$NON-NLS-1$

   public static String BPEL_EXTENSION_NOT_UNDERSTOOD_CODE = "BPEL_EXTENSION_NOT_UNDERSTOOD";         //$NON-NLS-1$
   public static String BPEL_EXTENSION_NOT_USED_CODE = "BPEL_EXTENSION_NOT_USED";                     //$NON-NLS-1$

   public static String BPWS_FAULT_MESSAGETYPE_REQUIRED_CODE = "BPWS_FAULT_MESSAGETYPE_REQUIRED";     //$NON-NLS-1$
   public static String BPEL_FAULT_NAME_NOT_CAUGHT_CODE = "BPEL_FAULT_NAME_NOT_CAUGHT";               //$NON-NLS-1$

   public static String BPEL_FIELD_MISSING_CODE = "BPEL_FIELD_MISSING";                               //$NON-NLS-1$
   public static String BPEL_FOREACH_FIELD_MISSING_CODE = "BPEL_FOREACH_FIELD_MISSING";               //$NON-NLS-1$
   public static String BPEL_WSIO_FIELD_MISSING_CODE = "BPEL_WSIO_FIELD_MISSING";                     //$NON-NLS-1$
   public static String BPEL_THROW_FIELD_MISSING_CODE = "BPEL_THROW_FIELD_MISSING";                   //$NON-NLS-1$
   public static String BPEL_WAIT_FIELD_MISSING_CODE = "BPEL_WAIT_FIELD_MISSING";                     //$NON-NLS-1$
   public static String BPEL_LOOP_FIELD_MISSING_CODE = "BPEL_LOOP_FIELD_MISSING";                     //$NON-NLS-1$
   public static String BPEL_ALARM_FIELD_MISSING_CODE = "BPEL_ALARM_FIELD_MISSING";                   //$NON-NLS-1$
   public static String BPEL_ELSEIF_FIELD_MISSING_CODE = "BPEL_ELSEIF_FIELD_MISSING";                 //$NON-NLS-1$

   public static String WSBPEL_ILLEGAL_CATCH_FOR_EXIT_ON_STD_FAULT_CODE = "WSBPEL_ILLEGAL_CATCH_FOR_EXIT_ON_STD_FAULT";                //$NON-NLS-1$

   public static String BPEL_FOREACH_IMPLICIT_VARIABLE_EXPLICITLY_DEFINED_CODE = "BPEL_FOREACH_IMPLICIT_VARIABLE_EXPLICITLY_DEFINED";  //$NON-NLS-1$
   public static String BPEL_EVENT_IMPLICIT_VARIABLE_EXPLICITLY_DEFINED_CODE = "BPEL_EVENT_IMPLICIT_VARIABLE_EXPLICITLY_DEFINED";      //$NON-NLS-1$

   public static String BPEL_IMPORT_TYPE_INVALID_URI_CODE = "BPEL_IMPORT_TYPE_INVALID_URI";           //$NON-NLS-1$

   public static String BPEL_IMPORT_TYPE_NOT_ABSOLUTE_CODE = "BPEL_IMPORT_TYPE_NOT_ABSOLUTE";         //$NON-NLS-1$

   public static String BPEL_INIT_PARTNER_ROLE_NOT_ALLOWED_CODE = "BPEL_INIT_PARTNER_ROLE_NOT_ALLOWED"; //$NON-NLS-1$

   public static String WSBPEL_INVALID_ALARM_CHILD_CODE = "WSBPEL_INVALID_ALARM_CHILD";               //$NON-NLS-1$

   public static String BPEL_INVALID_EXPRESSION_CODE = "BPEL_INVALID_EXPRESSION";                     //$NON-NLS-1$

   public static String WSBPEL_INVALID_FAULT_TYPE_CODE = "WSBPEL_INVALID_FAULT_TYPE";                 //$NON-NLS-1$

   public static String BPEL_INVALID_LITERAL_CODE = "BPEL_INVALID_LITERAL";                           //$NON-NLS-1$

   public static String BPEL_INVALID_LOOP_LOCATION_CODE = "BPEL_INVALID_LOOP_LOCATION";               //$NON-NLS-1$

   public static String BPEL_INVALID_MESSAGE_VARIABLE_INIT_CODE = "BPEL_INVALID_MESSAGE_VARIABLE_INIT"; //$NON-NLS-1$

   public static String BPEL_INVALID_NAME_CODE = "BPEL_INVALID_NAME";                                 //$NON-NLS-1$

   public static String WSBPEL_INVALID_TO_FORMAT_EXPRESSION_CODE = "WSBPEL_INVALID_TO_FORMAT_EXPRESSION"; //$NON-NLS-1$

   public static String BPEL_INVALID_VARIABLE_TYPE_CODE = "BPEL_INVALID_VARIABLE_TYPE";               //$NON-NLS-1$
   
   public static String BPEL_CORR_INVALID_XPATH_CODE = "BPEL_CORR_INVALID_XPATH";                     //$NON-NLS-1$
   public static String BPEL_TYPE_INVALID_XPATH_CODE = "BPEL_TYPE_INVALID_XPATH";                     //$NON-NLS-1$
   public static String BPEL_ELEMENT_INVALID_XPATH_CODE = "BPEL_ELEMENT_INVALID_XPATH";               //$NON-NLS-1$
   public static String BPEL_MSG_PART_INVALID_XPATH_CODE = "BPEL_MSG_PART_INVALID_XPATH";             //$NON-NLS-1$
   public static String BPEL_COMPLEX_INVALID_XPATH_CODE = "BPEL_COMPLEX_INVALID_XPATH";               //$NON-NLS-1$

   public static String BPEL_LINK_CROSSING_CODE = "BPEL_LINK_CROSSING";                               //$NON-NLS-1$

   public static String BPEL_LINK_CYCLE_CODE = "BPEL_LINK_CYCLE";                                     //$NON-NLS-1$

   public static String BPEL_LINK_NOT_USED_CODE = "BPEL_LINK_NOT_USED";                               //$NON-NLS-1$

   public static String BPEL_MESSAGE_STRATEGY_CODE = "BPEL_MESSAGE_STRATEGY";                         //$NON-NLS-1$

   public static String BPEL_MESSAGE_VARIABLE_CODE = "BPEL_MESSAGE_VARIABLE"; //$NON-NLS-1$

   public static String BPEL_MISMATCHED_ASSIGNMENT_FAILURE_CODE = "BPEL_MISMATCHED_ASSIGNMENT_FAILURE"; //$NON-NLS-1$

   public static String BPEL_MISPLACED_COMPENSATE_CODE = "BPEL_MISPLACED_COMPENSATE";                 //$NON-NLS-1$

   public static String BPEL_MISSING_CONDITION_CODE = "BPEL_MISSING_CONDITION";                       //$NON-NLS-1$

   public static String BPEL_MISSING_EXPRESSION_PART_CODE = "BPEL_MISSING_EXPRESSION_PART";           //$NON-NLS-1$

   public static String WSBPEL_VARIABLE_MISSING_IMPORT_CODE = "WSBPEL_VARIABLE_MISSING_IMPORT";       //$NON-NLS-1$
   public static String WSBPEL_PARTNERLINK_MISSING_IMPORT_CODE = "WSBPEL_PARTNERLINK_MISSING_IMPORT"; //$NON-NLS-1$

   public static String BPEL_CORR_MISSING_PROPERTY_ALIAS_CODE = "BPEL_CORR_MISSING_PROPERTY_ALIAS";         //$NON-NLS-1$
   public static String BPEL_ELEMENT_MISSING_PROPERTY_ALIAS_CODE = "BPEL_ELEMENT_MISSING_PROPERTY_ALIAS";   //$NON-NLS-1$
   public static String BPEL_MSG_MISSING_PROPERTY_ALIAS_CODE = "BPEL_MSG_MISSING_PROPERTY_ALIAS";           //$NON-NLS-1$
   public static String BPEL_TYPE_MISSING_PROPERTY_ALIAS_CODE = "BPEL_TYPE_MISSING_PROPERTY_ALIAS";         //$NON-NLS-1$

   public static String BPEL_MISSING_ROLE_CODE = "BPEL_MISSING_ROLE";                                 //$NON-NLS-1$

   public static String BPEL_MULTI_SRC_LINK_CODE = "BPEL_MULTI_SRC_LINK";                             //$NON-NLS-1$
   public static String BPEL_MULTI_TARGET_LINK_CODE = "BPEL_MULTI_TARGET_LINK";                       //$NON-NLS-1$

   public static String BPEL_MULTIPLE_CHILDREN_FOUND_CODE = "BPEL_MULTIPLE_CHILDREN_FOUND";           //$NON-NLS-1$

   public static String WSBPEL_NESTED_ISOLATED_SCOPE_CODE = "WSBPEL_NESTED_ISOLATED_SCOPE";           //$NON-NLS-1$

   public static String WSBPEL_NESTED_ISOLATED_SCOPE_FCT_SOURCE_CODE = "WSBPEL_NESTED_ISOLATED_SCOPE_FCT_SOURCE"; //$NON-NLS-1$
   public static String WSBPEL_NESTED_ISOLATED_SCOPE_FCT_TARGET_CODE = "WSBPEL_NESTED_ISOLATED_SCOPE_FCT_TARGET"; //$NON-NLS-1$

   public static String BPEL_NO_COPY_CODE = "BPEL_NO_COPY";                                           //$NON-NLS-1$

   public static String BPEL_EVENT_NO_CORR_SET_NO_CREATE_CODE = "BPEL_EVENT_NO_CORR_SET_NO_CREATE";   //$NON-NLS-1$
   public static String BPEL_RCV_NO_CORR_SET_NO_CREATE_CODE = "BPEL_RCV_NO_CORR_SET_NO_CREATE";       //$NON-NLS-1$
   public static String BPEL_MSG_NO_CORR_SET_NO_CREATE_CODE = "BPEL_MSG_NO_CORR_SET_NO_CREATE";       //$NON-NLS-1$

   public static String BPEL_NO_CREATE_CODE = "BPEL_NO_CREATE";                                       //$NON-NLS-1$

   public static String BPEL_NO_IMPORT_TYPE_CODE = "BPEL_NO_IMPORT_TYPE";                             //$NON-NLS-1$

   public static String BPEL_NO_ONMESSAGE_CODE = "BPEL_NO_ONMESSAGE";                                 //$NON-NLS-1$

   public static String BPEL_NO_QUERY_FOR_PROP_ALIAS_CODE = "BPEL_NO_QUERY_FOR_PROP_ALIAS";           //$NON-NLS-1$

   public static String BPEL_NON_STANDARD_EXPRESSION_LANGUAGE_CODE = "BPEL_NON_STANDARD_EXPRESSION_LANGUAGE";  //$NON-NLS-1$

   public static String BPEL_OPAQUE_ACTIVITY_NOT_ALLOWED_CODE = "BPEL_OPAQUE_ACTIVITY_NOT_ALLOWED";            //$NON-NLS-1$
   public static String BPEL_FROM_OPAQUE_ACTIVITY_NOT_ALLOWED_CODE = "BPEL_FROM_OPAQUE_ACTIVITY_NOT_ALLOWED";  //$NON-NLS-1$

   public static String BPEL_OPERATION_INOUT_NOT_FOUND_CODE = "BPEL_OPERATION_INOUT_NOT_FOUND";       //$NON-NLS-1$

   public static String BPEL_OPERATION_NOT_FOUND_CODE = "BPEL_OPERATION_NOT_FOUND";                   //$NON-NLS-1$

   public static String BPEL_PARTNER_LINK_NOT_FOUND_CODE = "BPEL_PARTNER_LINK_NOT_FOUND";             //$NON-NLS-1$

   public static String BPEL_PARTNER_LINK_NOT_USED_CODE = "BPEL_PARTNER_LINK_NOT_USED";               //$NON-NLS-1$

   public static String BPEL_PATTERN_INVALID_CODE = "BPEL_PATTERN_INVALID";                           //$NON-NLS-1$

   public static String BPEL_PLINK_ASSIGN_FROM_MYROLE_CODE = "BPEL_PLINK_ASSIGN_FROM_MYROLE";         //$NON-NLS-1$

   public static String BPEL_PLINK_ASSIGN_FROM_PARTNERROLE_CODE = "BPEL_PLINK_ASSIGN_FROM_PARTNERROLE"; //$NON-NLS-1$

   public static String BPEL_PLINK_ASSIGN_TO_CODE = "BPEL_PLINK_ASSIGN_TO";                           //$NON-NLS-1$

   public static String BPEL_PORTTYPE_MISMATCH_CODE = "BPEL_PORTTYPE_MISMATCH";                       //$NON-NLS-1$

   public static String WSBPEL_PROFILE_REQUIRED_CODE = "WSBPEL_PROFILE_REQUIRED";                     //$NON-NLS-1$

   public static String BPEL_PROPERTY_ALIAS_BAD_PART_CODE = "BPEL_PROPERTY_ALIAS_BAD_PART";           //$NON-NLS-1$

   public static String WSBPEL_REQUIRES_SCOPE_CHILD_CODE = "WSBPEL_REQUIRES_SCOPE_CHILD";             //$NON-NLS-1$
   public static String BPEL_REQUIRES_SCOPE_CHILD_CODE = "BPEL_REQUIRES_SCOPE_CHILD";                 //$NON-NLS-1$

   public static String BPEL_ROLE_HAS_NO_PORTTYPE_CODE = "BPEL_ROLE_HAS_NO_PORTTYPE";                 //$NON-NLS-1$

   public static String BPEL_ROLE_NOT_FOUND_CODE = "BPEL_ROLE_NOT_FOUND";                             //$NON-NLS-1$

   public static String BPEL_ROOT_SCOPE_FCT_HANDLER_CODE = "BPEL_ROOT_SCOPE_FCT_HANDLER";             //$NON-NLS-1$

   public static String BPEL_SCHEMA_LOCATION_IN_LITERAL_CODE = "BPEL_SCHEMA_LOCATION_IN_LITERAL";     //$NON-NLS-1$

   public static String BPEL_SCOPE_LINK_CODE = "BPEL_SCOPE_LINK";                                     //$NON-NLS-1$

   public static String BPEL_SCOPE_NOT_FOUND_CODE = "BPEL_SCOPE_NOT_FOUND";                           //$NON-NLS-1$

   public static String BPEL_TERM_NOT_ALLOWED_CODE = "BPEL_TERM_NOT_ALLOWED";                         //$NON-NLS-1$

   public static String BPEL_TOO_MANY_SCOPES_FOUND_CODE = "BPEL_TOO_MANY_SCOPES_FOUND";               //$NON-NLS-1$

   public static String BPEL_TYPE_NOT_FOUND_CODE = "BPEL_TYPE_NOT_FOUND";                             //$NON-NLS-1$

   public static String BPEL_UNDECLARED_EXTENSION_CODE = "BPEL_UNDECLARED_EXTENSION";                 //$NON-NLS-1$

   public static String BPEL_UNSUPPORTED_COPYOP_FROM_CODE = "BPEL_UNSUPPORTED_COPYOP_FROM";           //$NON-NLS-1$

   public static String BPEL_UNSUPPORTED_COPYOP_TO_CODE = "BPEL_UNSUPPORTED_COPYOP_TO";               //$NON-NLS-1$

   public static String BPEL_UNSUPPORTED_EXPRESSION_LANGUAGE_CODE = "BPEL_UNSUPPORTED_EXPRESSION_LANGUAGE"; //$NON-NLS-1$

   public static String BPEL_VAR_HAS_NO_TYPE_CODE = "BPEL_VAR_HAS_NO_TYPE";                           //$NON-NLS-1$

   public static String BPEL_VAR_NOT_FOUND_CODE = "BPEL_VAR_NOT_FOUND";                               //$NON-NLS-1$
   public static String BPEL_FROM_VAR_NOT_FOUND_CODE = "BPEL_FROM_VAR_NOT_FOUND";                     //$NON-NLS-1$
   public static String BPEL_TO_VAR_NOT_FOUND_CODE = "BPEL_TO_VAR_NOT_FOUND";                         //$NON-NLS-1$
   public static String BPEL_FROM_PART_VAR_NOT_FOUND_CODE = "BPEL_FROM_PART_VAR_NOT_FOUND";           //$NON-NLS-1$
   public static String BPEL_TO_PART_VAR_NOT_FOUND_CODE = "BPEL_TO_PART_VAR_NOT_FOUND";               //$NON-NLS-1$

   public static String BPEL_FROM_PART_VAR_PART_NOT_FOUND_CODE = "BPEL_FROM_PART_VAR_PART_NOT_FOUND"; //$NON-NLS-1$
   public static String BPEL_TO_PART_VAR_PART_NOT_FOUND_CODE = "BPEL_TO_PART_VAR_PART_NOT_FOUND";     //$NON-NLS-1$
   public static String BPEL_MSG_PART_VAR_PART_NOT_FOUND_CODE = "BPEL_MSG_PART_VAR_PART_NOT_FOUND";   //$NON-NLS-1$

   public static String BPEL_ELEMENT_VAR_TYPE_MISMATCH_CODE = "BPEL_ELEMENT_VAR_TYPE_MISMATCH";       //$NON-NLS-1$
   public static String BPEL_MSG_PART_VAR_TYPE_MISMATCH_CODE = "BPEL_MSG_PART_VAR_TYPE_MISMATCH";     //$NON-NLS-1$

   public static String BPEL_VARIABLE_USAGE_CODE = "BPEL_VARIABLE_USAGE"; //$NON-NLS-1$

   public static String BPEL_WRONG_ELEMENT_VARIABLE_CODE = "BPEL_WRONG_ELEMENT_VARIABLE"; //$NON-NLS-1$

   public static String BPEL_WRONG_MESSAGE_VARIABLE_CODE = "BPEL_WRONG_MESSAGE_VARIABLE"; //$NON-NLS-1$

   public static String WSBPEL_ILLEGAL_FH_CONSTRUCTS_CODE = "WSBPEL_ILLEGAL_FH_CONSTRUCTS"; //$NON-NLS-1$
   
   public static String BPEL_WSBPEL_MESSAGE_EXCHANGE_CODE = "BPEL_WSBPEL_MESSAGE_EXCHANGE"; //$NON-NLS-1$
   
   public static String WSBPEL_UNEXPECTED_ATTRIBUTE_CODE = "WSBPEL_UNEXPECTED_ATTRIBUTE"; //$NON-NLS-1$

   public static String BPEL_XPATH_VALIDATION_EXCEPTION_CODE = "BPEL_XPATH_VALIDATION_EXCEPTION"; //$NON-NLS-1$

}
