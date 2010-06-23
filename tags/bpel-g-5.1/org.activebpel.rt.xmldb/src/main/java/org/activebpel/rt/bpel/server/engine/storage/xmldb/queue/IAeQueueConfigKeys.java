//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/queue/IAeQueueConfigKeys.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.queue;

/**
 * An interface that simply holds some static constants which are the names of keys in the XMLDB config
 * object.
 */
public interface IAeQueueConfigKeys
{
   /** Delete alarm query statment key. */
   public static final String DELETE_ALARM = "DeleteAlarm"; //$NON-NLS-1$
   /** Delete alarm query statment key. */
   public static final String DELETE_ALARMS_BY_GROUPID = "DeleteAlarmsByGroupId"; //$NON-NLS-1$
   /** Insert alarm query statment key. */
   public static final String INSERT_ALARM = "InsertAlarm"; //$NON-NLS-1$
   /** Get all alarms query statment key. */
   public static final String GET_ALARMS = "GetAlarms"; //$NON-NLS-1$
   /** Get alarms by filter criteria query statment key. */
   public static final String GET_ALARMS_FILTERED = "GetAlarmsFiltered"; //$NON-NLS-1$

   /** Insert queued receive query statment key. */
   public static final String INSERT_QUEUED_RECEIVE = "InsertQueuedReceive"; //$NON-NLS-1$
   /** Get a list of correlated receives query statement key. */
   public static final String GET_CORRELATED_RECEIVES = "GetCorrelatedReceives"; //$NON-NLS-1$
   /** Get a list of conflicting receives query statement key. */
   public static final String GET_CONFLICTING_RECEIVES = "GetConflictingReceives"; //$NON-NLS-1$
   /** Get a queued receive query statement key. */
   public static final String GET_QUEUED_RECEIVE = "GetQueuedReceive"; //$NON-NLS-1$
   /** Get a filtered list of queued receives query statement key. */
   public static final String GET_QUEUED_RECEIVES_FILTERED = "GetQueuedReceivesFiltered"; //$NON-NLS-1$
   /** Delete a single queued receive by id statement key. */
   public static final String DELETE_QUEUED_RECEIVE_BYID = "DeleteQueuedReceiveById"; //$NON-NLS-1$
   /** Delete all queued receives in group statement key. */
   public static final String DELETE_QUEUED_RECEIVES_BY_GROUP = "DeleteQueuedReceivesByGroup"; //$NON-NLS-1$
}
