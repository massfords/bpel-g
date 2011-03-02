// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeProcessInfoEvent.java,v 1.13 2008/03/11 03:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

/**
 * Interface for process information events.
 *
 * Events are reported to listeners via handleProcessInfoEvent().
 */
public interface IAeProcessInfoEvent extends IAeBaseProcessEvent
{
   // Information events
   // TODO move message format id's to message formatter
   /** Start value for information event range. */
   public static final int INFO_EVENTS = 1000 ;

   // Simulation info events.
   /** Alarm has fired info event. */
   public static final int INFO_ON_ALARM = 1001 ;
   /** Wait has completed info event. */
   public static final int INFO_WAIT = 1002 ;
   /** Join condition info event. */
   public static final int INFO_JOIN = 1003 ;
   /** While info event. */
   public static final int INFO_WHILE = 1004 ;
   /** Case info event. */
   public static final int INFO_CASE = 1005 ;
   /** Link transition info event. */
   public static final int INFO_LINK_XTN = 1006 ;

   // ****** Note: Keep all of the forEach event id's together
   /** forEach start value info event. */
   public static final int INFO_FOREACH_START_VALUE = 1007;
   /** forEach final value info event. */
   public static final int INFO_FOREACH_FINAL_VALUE = 1008;
   /** forEach completionCondition value info event. */
   public static final int INFO_FOREACH_COMPLETION_CONDITION_VALUE = 1009;
   /** forEach completionCondition met info event. */
   public static final int INFO_FOREACH_COMPLETION_CONDITION_MET = 1010;
   // ****** See note above

   /** activity is being terminated as a result of a break, continue, or completionCondition */
   public static final int INFO_EARLY_TERMINATION = 1011 ;

   /** activity is waiting to acquire variable lock */
   public static final int INFO_WAITING_FOR_LOCK = 1012;

   /** event id to signal that the process's compensation handler is running */
   public static final int INFO_PROCESS_COMPENSATION_STARTED = 1013;
   /** event id to signal that the process's compensation handler is finished */
   public static final int INFO_PROCESS_COMPENSATION_FINISHED = 1014;
   /** event id to signal that the process's compensation handler faulted */
   public static final int INFO_PROCESS_COMPENSATION_FAULTED = 1015;
   /** event id to signal that the process's compensation handler terminated */
   public static final int INFO_PROCESS_COMPENSATION_TERMINATED = 1016;

   /** Repeat until info event. */
   public static final int INFO_REPEAT_UNTIL = 1017;
   /** If info event. */
   public static final int INFO_IF = 1018;
   /** ElseIf info event. */
   public static final int INFO_ELSE_IF = 1019;

   // Generic info events.
   /** Generic info event format. */
   public static final int GENERIC_INFO_EVENT = 1500 ;


   // Warning events
   /** Start value for warning event range. */
   public static final int WARN_EVENTS = 2000 ;


   // Error events
   /** Start value for error event range. */
   public static final int ERROR_EVENTS = 3000 ;

   /** Unrecognized engine event ID sent by engine. */
   public static final int ERROR_EVENT_NOT_HANDLED = 3001 ;

   /** Attempt to restart running thread/activity. */
   public static final int ERROR_RESTART_ACTIVITY = 3002 ;

   /** Error executing copy operation in assign activity. */
   public static final int ERROR_ASSIGN_ACTIVITY = 3003 ;
   
   /** Data sent to an onEvent was invalid and caused child scope to fault */
   public static final int ERROR_ON_EVENT_VALIDATION = 3004;

   /** The end of the line... */
   public static final int LAST_ERROR_EVENT_ID = 4000 ;


   /** Format ID for (unhandeled) Event ID and Ancillary Info. */
   public static final int EVENT_ID_AND_INFO_FMT = 4001 ;
}
