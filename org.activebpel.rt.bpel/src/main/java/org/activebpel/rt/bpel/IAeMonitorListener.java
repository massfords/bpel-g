// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeMonitorListener.java,v 1.2 2007/12/31 15:35:15 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

/**
 * Interface for engine listeners.
 */
public interface IAeMonitorListener
{
   /** Monitor event sent when a process faults or is suspended because of a fault */
   public static final int MONITOR_PROCESS_FAULT = 100;
   /** Monitor event sent when a process is loaded */
   public static final int MONITOR_PROCESS_LOADED = 101;
   /** Monitor event sent when a process is loaded */
   public static final int MONITOR_PROCESS_LOAD_TIME = 102;
   /** Monitor event sent when work manager scheduled item has started */
   public static final int MONITOR_WM_START_TIME = 103;
   /** Monitor event sent with time to acquire database connection */
   public static final int MONITOR_DB_CONN_TIME = 104;
   /** Monitor event sent when correlated message is discarded */
   public static final int MONITOR_CORR_MSG_DISCARD = 105;
   /** Monitor event sent when a message is validated */
   public static final int MONITOR_MESSAGE_VALIDATION_TIME = 106;

   /** EventData code used in MONITOR_PROCESS_FAULT to signal process ended in fault state */
   public static final long EVENT_DATA_PROCESS_FAULTED = 0;
   /** EventData code used in MONITOR_PROCESS_FAULT to signal process suspended because it was about to fault */
   public static final long EVENT_DATA_PROCESS_FAULTING = 1;

   /** EventData code used in MONITOR_PROCESS_LOADED to signal process loaded from cache */
   public static final long EVENT_DATA_PROCESS_LOAD_FROM_CACHE = 0;
   /** EventData code used in MONITOR_PROCESS_LOADED to signal process loaded from database */
   public static final long EVENT_DATA_PROCESS_LOAD_FROM_DB = 1;
   
   /**
    * Method called to notify listeners a monitor event has occurred.
    * @param aMonitorID The Id of the monitor event 
    * @param aMonitorData The data which is specific to the event id
    */
   public void handleMonitorEvent(int aMonitorID, long aMonitorData);
}