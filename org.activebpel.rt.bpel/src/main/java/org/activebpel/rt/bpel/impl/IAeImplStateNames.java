// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeImplStateNames.java,v 1.50 2008/03/28 01:41:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

/**
 * Tag and attributes names for XML serialization produced by
 * <code>AeProcessImplState</code> and read by
 * <code>AeRestoreImplState</code>.
 */
public interface IAeImplStateNames
{
   public static final String STATE_DOC_VERSION           = "stateDocVersion"; //$NON-NLS-1$
   public static final String STATE_DOC_CURRENT           = "3.0"; //$NON-NLS-1$
   public static final String STATE_DOC_1_0               = "1.0"; //$NON-NLS-1$
   
   public static final String STATE_ACTY                  = "bpelObject" ; //$NON-NLS-1$
   public static final String STATE_ADDRESS               = "address" ; //$NON-NLS-1$
   public static final String STATE_ATTACHMENT            = "attachment" ; //$NON-NLS-1$
   public static final String STATE_ATTACHMENT_HEADER     = "header" ; //$NON-NLS-1$
   public static final String STATE_CALLBACK              = "callback" ; //$NON-NLS-1$
   public static final String STATE_COMPINFO              = "compInfo" ; //$NON-NLS-1$
   public static final String STATE_CORRSET               = "correlationSet" ; //$NON-NLS-1$
   public static final String STATE_CREATEMESSAGE         = "createMessage" ; //$NON-NLS-1$
   public static final String STATE_DATA                  = "dataIncluded" ; //$NON-NLS-1$
   public static final String STATE_PARTNERENDPOINTREFERENCE = "embeddedEndpointReference" ; //$NON-NLS-1$
   public static final String STATE_ELEMENT               = "element" ; //$NON-NLS-1$
   public static final String STATE_ENABLED               = "enabled" ; //$NON-NLS-1$
   public static final String STATE_ENDDATE               = "endDate" ; //$NON-NLS-1$
   public static final String STATE_ENDDATEMILLIS         = "endDateMillis" ; //$NON-NLS-1$
   public static final String STATE_EPREF                 = "endPointReference" ; //$NON-NLS-1$
   public static final String STATE_ERROR                 = "error" ; //$NON-NLS-1$
   public static final String STATE_EVAL                  = "evaluated" ; //$NON-NLS-1$
   public static final String STATE_EXITING               = "exiting" ; //$NON-NLS-1$
   public static final String STATE_FAULT                 = "fault" ; //$NON-NLS-1$
   public static final String STATE_FAULTHANDLERPATH      = "faultHandlerPath" ; //$NON-NLS-1$
   public static final String STATE_FAULTING_ACTIVITY     = "faultingActivity"; //$NON-NLS-1$
   public static final String STATE_FIRST_ITER            = "firstIteration" ; //$NON-NLS-1$
   public static final String STATE_FOREACH_COUNTER       = "counterValue"; //$NON-NLS-1$
   public static final String STATE_INSTANCE_VALUE        = "instanceValue"; //$NON-NLS-1$
   public static final String STATE_INSTANCE_COUNT        = "instanceCount"; //$NON-NLS-1$
   public static final String STATE_INTERVAL              = "interval"; //$NON-NLS-1$
   public static final String STATE_SCOPE_COMPENSATING    = "compensating";  //$NON-NLS-1$
   public static final String STATE_FOREACH_COMPLETIONCONDITION = "completionCondition"; //$NON-NLS-1$
   public static final String STATE_FOREACH_COMPLETIONCOUNT = "completionCount"; //$NON-NLS-1$
   public static final String STATE_FOREACH_START         = "startValue"; //$NON-NLS-1$
   public static final String STATE_FOREACH_FINAL         = "finalValue"; //$NON-NLS-1$
   public static final String STATE_GID                   = "gid" ; //$NON-NLS-1$
   public static final String STATE_HASATTACHMENTS        = "hasAttachments" ; //$NON-NLS-1$
   public static final String STATE_HASDATA               = "hasData" ; //$NON-NLS-1$
   public static final String STATE_HASIMPLICITCOMPENSATIONHANDLER = "hasImplicitCompensationHandler" ; //$NON-NLS-1$
   public static final String STATE_HASIMPLICITTERMINATIONHANDLER = "hasImplicitTerminationHandler" ; //$NON-NLS-1$
   public static final String STATE_HASIMPLICITFAULTHANDLER = "hasImplicitFaultHandler" ; //$NON-NLS-1$
   public static final String STATE_HASELEMENTDATA        = "hasElementData"; //$NON-NLS-1$
   public static final String STATE_HASMESSAGEDATA        = "hasMessageData" ; //$NON-NLS-1$
   public static final String STATE_ID                    = "id" ; //$NON-NLS-1$
   public static final String STATE_INBOUNDRECEIVE        = "inboundReceive" ; //$NON-NLS-1$
   public static final String STATE_INIT                  = "initialized" ; //$NON-NLS-1$
   public static final String STATE_LINK                  = "link" ; //$NON-NLS-1$
   public static final String STATE_LOC                   = "locationPath" ; //$NON-NLS-1$
   public static final String STATE_LOCATIONID            = "locationId" ; //$NON-NLS-1$
   public static final String STATE_LOOP_TERMINATION_REASON = "loopControlFlag" ; //$NON-NLS-1$
   public static final String STATE_MAXLOCATIONID         = "maxLocationId" ; //$NON-NLS-1$
   public static final String STATE_MESSAGETYPE           = "messagetype" ; //$NON-NLS-1$
   public static final String STATE_NEXTVARIABLEID        = "nextVariableId";  //$NON-NLS-1$
   public static final String STATE_MESSAGECONTEXT        = "messageContext" ; //$NON-NLS-1$
   public static final String STATE_REFPROPS              = "referenceProperties" ; //$NON-NLS-1$
   public static final String STATE_MAPPED_HEADERS        = "mappedHeaders"; //$NON-NLS-1$
   public static final String STATE_WSAHEADERS            = "wsaHeaders" ; //$NON-NLS-1$
   public static final String STATE_MESSAGEDATA           = "messageData" ; //$NON-NLS-1$
   public static final String STATE_MESSAGE_EXCHANGE      = "messageExchange"; //$NON-NLS-1$
   public static final String STATE_MYENDPOINTREFERENCE   = "myEndpointReference" ; //$NON-NLS-1$
   public static final String STATE_NAME                  = "name" ; //$NON-NLS-1$
   public static final String STATE_NAMESPACEURI          = "namespaceURI" ; //$NON-NLS-1$
   public static final String STATE_NEXTINDEX             = "nextIndex" ; //$NON-NLS-1$
   public static final String STATE_NORMALCOMPLETION      = "normalCompletion" ; //$NON-NLS-1$
   public static final String STATE_ONEVENT_MESSAGE       = "onEventMessage" ; //$NON-NLS-1$
   public static final String STATE_OPERATION             = "operation"; //$NON-NLS-1$
   public static final String STATE_PART                  = "part" ; //$NON-NLS-1$
   public static final String STATE_PID                   = "pid" ; //$NON-NLS-1$
   public static final String STATE_PLINK                 = "partnerLink" ; //$NON-NLS-1$
   public static final String STATE_PLINK_LOCATION        = "partnerLinkLoc" ; //$NON-NLS-1$
   public static final String STATE_PORTTYPE              = "portType" ; //$NON-NLS-1$
   public static final String STATE_PRINCIPAL             = "principal" ; //$NON-NLS-1$
   public static final String STATE_PROC                  = "process" ; //$NON-NLS-1$
   public static final String STATE_PROCESSNAME           = "processName" ; //$NON-NLS-1$
   public static final String STATE_PROCESSPROPERTY       = "processProperty"; //$NON-NLS-1$
   public static final String STATE_PROCESSSTATE          = "processState" ; //$NON-NLS-1$
   public static final String STATE_PROCESSSTATEREASON    = "processStateReason" ; //$NON-NLS-1$
   public static final String STATE_PROLE                 = "partnerRole" ; //$NON-NLS-1$
   public static final String STATE_PROCESSINITIATOR      = "processInitiator" ; //$NON-NLS-1$
   public static final String STATE_PROPERTY              = "property" ; //$NON-NLS-1$
   public static final String STATE_QUEUE                 = "queue" ; //$NON-NLS-1$
   public static final String STATE_QUEUED                = "queued" ; //$NON-NLS-1$
   public static final String STATE_QUEUEITEM             = "queueItem" ; //$NON-NLS-1$
   public static final String STATE_REASON                = "reason" ; //$NON-NLS-1$
   public static final String STATE_REPLY                 = "reply" ; //$NON-NLS-1$
   public static final String STATE_REPLYWAITING          = "replyWaiting" ; //$NON-NLS-1$
   public static final String STATE_RETHROWABLE           = "rethrowable"; //$NON-NLS-1$
   public static final String STATE_RETRIES               = "retries" ; //$NON-NLS-1$
   public static final String STATE_ROLE                  = "myRole" ; //$NON-NLS-1$
   public static final String STATE_ROOT                  = "processState" ; //$NON-NLS-1$
   public static final String STATE_SCOPE                 = "scope" ; //$NON-NLS-1$
   public static final String STATE_SCOPE_LOCATION        = "scopeLocation" ; //$NON-NLS-1$
   public static final String STATE_SERVICENAME           = "serviceName" ; //$NON-NLS-1$
   public static final String STATE_SERVICEPORT           = "servicePort" ; //$NON-NLS-1$
   public static final String STATE_SKIPCHILDREN          = "skipChildren" ; //$NON-NLS-1$
   public static final String STATE_SOURCE                = "source" ; //$NON-NLS-1$
   public static final String STATE_STARTDATE             = "startDate" ; //$NON-NLS-1$
   public static final String STATE_STARTDATEMILLIS       = "startDateMillis" ; //$NON-NLS-1$
   public static final String STATE_STATE                 = "currentState" ; //$NON-NLS-1$
   public static final String STATE_SUSPENDABLE           = "suspendable"; //$NON-NLS-1$
   public static final String STATE_TERMINATING           = "terminating"; //$NON-NLS-1$
   public static final String STATE_TYPE                  = "type"; //$NON-NLS-1$
   public static final String STATE_UNKNOWN               = "unknown" ; //$NON-NLS-1$
   public static final String STATE_VALUE                 = "value" ; //$NON-NLS-1$
   public static final String STATE_VAR                   = "variable" ; //$NON-NLS-1$
   public static final String STATE_VARIABLELOCKER        = "variableLocker" ; //$NON-NLS-1$
   public static final String STATE_SNAPSHOTRECORDED      = "snapshotRecorded"; //$NON-NLS-1$
   public static final String STATE_VERSION               = "version" ; //$NON-NLS-1$
   public static final String STATE_COORDINATOR           = "coordinator" ; //$NON-NLS-1$
   public static final String STATE_PARTICIPANT           = "participant" ; //$NON-NLS-1$
   public static final String STATE_HASCOORDINATIONS      = "hasCoordinations" ; //$NON-NLS-1$
   public static final String STATE_COORDINATION_ID       = "coordId" ; //$NON-NLS-1$
   public static final String STATE_COORDINATION_COUNT    = "coordCount" ; //$NON-NLS-1$   
   public static final String STATE_COORD_STATE           = "coordState" ; //$NON-NLS-1$
   public static final String STATE_CALLBACK_COORDINATED  = "callbackCoordinated" ; //$NON-NLS-1$
   public static final String STATE_CALLBACK_COORD_ID     = "callbackCoordId" ; //$NON-NLS-1$   
   public static final String STATE_HASCOORDCOMPENSATOR   = "hasCoordCompensator" ; //$NON-NLS-1$
   public static final String STATE_HANDLED_FAULT         = "handledFault"; //$NON-NLS-1$
   
   public static final String STATE_DURABLE_REPLY          = "durableReply" ; //$NON-NLS-1$
   public static final String STATE_DURABLE_REPLY_TYPE     = "type" ; //$NON-NLS-1$
   public static final String STATE_OPEN_MESSAGE_ACTIVITY  = "openMessageActivity" ; //$NON-NLS-1$
   public static final String STATE_TRANSMISSION_ID        = "transmissionId" ; //$NON-NLS-1$
   public static final String STATE_REPLY_ID               = "replyId" ; //$NON-NLS-1$
   public static final String STATE_ENGINE_ID              = "eid" ; //$NON-NLS-1$   
   public static final String STATE_INVOKE_ID              = "invokeId" ; //$NON-NLS-1$
   public static final String STATE_ALARM_ID               = "alarmId" ; //$NON-NLS-1$
   public static final String STATE_JOURNAL_ID             = "jid" ; //$NON-NLS-1$
}
