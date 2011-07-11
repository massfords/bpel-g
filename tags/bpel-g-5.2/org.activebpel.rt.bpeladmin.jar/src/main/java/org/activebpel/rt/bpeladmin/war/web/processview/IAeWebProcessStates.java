// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/IAeWebProcessStates.java,v 1.4 2007/09/28 19:53:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

/**
 * Interface which defines the process states used by the web visual model.
 */
public interface IAeWebProcessStates
{
   /** Current process state for Loaded */
   public static final String PROCESS_STATE_LOADED = "loaded";  //$NON-NLS-1$
   
   /** Current process state for Running */
   public static final String PROCESS_STATE_RUNNING = "running";  //$NON-NLS-1$
   
   /** Current process state for Suspended */
   public static final String PROCESS_STATE_SUSPENDED_ACTIVITY = "suspendedActivity";  //$NON-NLS-1$
   
   /** Current process state for Suspended */
   public static final String PROCESS_STATE_SUSPENDED_FAULTING = "suspendedFaulting";  //$NON-NLS-1$
   
   /** Current process state for Suspended */
   public static final String PROCESS_STATE_SUSPENDED_MANUAL = "suspendedManual";  //$NON-NLS-1$
   
   /** Current process state for Suspended */
   public static final String PROCESS_STATE_SUSPENDED_INVOKE_RECOVERY = "suspendedInvokeRecovery";  //$NON-NLS-1$
   
   /** Current process state for Suspended */
   public static final String PROCESS_STATE_SUSPENDED = "suspended";  //$NON-NLS-1$
   
   /** Current process state for Completed */
   public static final String PROCESS_STATE_COMPLETED = "completed";  //$NON-NLS-1$
   
   /** Current process state for Compensatable */
   public static final String PROCESS_STATE_COMPENSATABLE = "compensatable"; //$NON-NLS-1$
   
   /** Current process state for Faulted */
   public static final String PROCESS_STATE_FAULTED = "faulted";  //$NON-NLS-1$
   
   /** Current process state for Unknown */
   public static final String PROCESS_STATE_UNKNOWN = "unknown";  //$NON-NLS-1$
}
