//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/IAeQueueSQLKeys.java,v 1.5 2006/01/03 20:34:57 EWittmann Exp $
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
 * Constants for the Queue storage SQL keys (keys into the SQLConfig object).
 */
public interface IAeQueueSQLKeys
{
   /** The SQL statement key for deleting an alarm. */
   public static final String DELETE_ALARM = "DeleteAlarm"; //$NON-NLS-1$
   /** The SQL statement key for deleting an alarm by its group id. */
   public static final String DELETE_ALARMS_IN_GROUP = "DeleteAlarmsByGroup"; //$NON-NLS-1$
   /** The SQL statement key for inserting an alarm. */
   public static final String INSERT_ALARM = "InsertAlarm"; //$NON-NLS-1$
   /** The SQL statement key for getting the list of alarms. */
   public static final String GET_ALARMS = "GetAlarms"; //$NON-NLS-1$

   /** The SQL statement key for getting queued receives. */
   public static final String GET_QUEUED_RECEIVE = "GetQueuedReceive"; //$NON-NLS-1$
   /** The SQL statement key for deleting queued receives by queued receive ID. */
   public static final String DELETE_QUEUED_RECEIVE_BYID = "DeleteQueuedReceiveById"; //$NON-NLS-1$
   /** The SQL statement key for deleting queued receives by location ID. */
   public static final String DELETE_QUEUED_RECEIVES_BY_LOCID = "DeleteQueuedReceiveByLocId"; //$NON-NLS-1$
   /** The SQL statement key for deleting queued receives. */
   public static final String DELETE_QUEUED_RECEIVE = "DeleteQueuedReceive"; //$NON-NLS-1$
   /** The SQL statement key for deleting queued receives by queued receive ID. */
   public static final String DELETE_QUEUED_RECEIVES_BY_GROUP = "DeleteQueuedReceivesByGroup"; //$NON-NLS-1$
   /** The SQL statement key for inserting queued receives. */
   public static final String INSERT_QUEUED_RECEIVE = "InsertQueuedReceive"; //$NON-NLS-1$
   /** The SQL statement key for getting correlated receives. */
   public static final String GET_CORRELATED_RECEIVES = "GetCorrelatedReceives"; //$NON-NLS-1$
}
