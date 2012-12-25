//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/queue/IAeQueueElements.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confIDential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbIDden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.queue;


/**
 * Constants that define the Queue element names.
 */
public interface IAeQueueElements
{
   public static final String DEADLINE = "Deadline"; //$NON-NLS-1$

   public static final String QUEUED_RECEIVE_ID = "QueuedReceiveID"; //$NON-NLS-1$
   public static final String PROCESS_ID = "ProcessID"; //$NON-NLS-1$
   public static final String LOCATION_PATH_ID = "LocationPathID"; //$NON-NLS-1$
   public static final String GROUP_ID = "GroupID"; //$NON-NLS-1$
   public static final String OPERATION = "Operation"; //$NON-NLS-1$
   public static final String PARTNER_LINK_NAME = "PartnerLinkName"; //$NON-NLS-1$
   public static final String PARTNER_LINK_ID = "PartnerLinkID"; //$NON-NLS-1$
   public static final String PORT_TYPE = "PortType"; //$NON-NLS-1$
   public static final String CORRELATION_PROPERTIES = "CorrelationProperties"; //$NON-NLS-1$
   public static final String MATCH_HASH = "MatchHash"; //$NON-NLS-1$
   public static final String CORRELATE_HASH = "CorrelateHash"; //$NON-NLS-1$
   public static final String PROCESS_NAME = "ProcessName"; //$NON-NLS-1$
   public static final String ALARM_ID = "AlarmID"; //$NON-NLS-1$
   public static final String ALLOWS_CONCURRENCY = "AllowsConcurrency"; //$NON-NLS-1$
}
