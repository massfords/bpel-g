//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/IAeQueueColumns.java,v 1.9 2006/09/22 19:56:15 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql;

/**
 * Constants that define the Queue column names.
 */
public interface IAeQueueColumns
{
   public static final String DEADLINE = "Deadline"; //$NON-NLS-1$
   public static final String DEADLINE_MILLIS = "DeadlineMillis"; //$NON-NLS-1$
   public static final String PROCESS_NAME = "ProcessName"; //$NON-NLS-1$
   public static final String PROCESS_NAMESPACE = "ProcessNamespace"; //$NON-NLS-1$

   public static final String QUEUED_RECEIVE_ID = "QueuedReceiveId"; //$NON-NLS-1$
   public static final String PROCESS_ID = "ProcessId"; //$NON-NLS-1$
   public static final String LOCATION_PATH_ID = "LocationPathId"; //$NON-NLS-1$
   public static final String OPERATION = "Operation"; //$NON-NLS-1$
   public static final String PARTNER_LINK_NAME = "PartnerLinkName"; //$NON-NLS-1$
   public static final String PORT_TYPE_NAMESPACE = "PortTypeNamespace"; //$NON-NLS-1$
   public static final String PORT_TYPE_LOCALPART = "PortTypeLocalPart"; //$NON-NLS-1$
   public static final String CORRELATION_PROPERTIES = "CorrelationProperties"; //$NON-NLS-1$
   public static final String MATCH_HASH = "MatchHash"; //$NON-NLS-1$
   public static final String CORRELATE_HASH = "CorrelateHash"; //$NON-NLS-1$
   public static final String GROUP_ID = "GroupId"; //$NON-NLS-1$
   public static final String ALARM_ID = "AlarmId"; //$NON-NLS-1$
   public static final String PARTNER_LINK_ID = "PartnerLinkId"; //$NON-NLS-1$
   public static final String ALLOWS_CONCURRENCY = "AllowsConcurrency"; //$NON-NLS-1$
   
}
