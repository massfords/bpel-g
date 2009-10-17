// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeProcessListener.java,v 1.4 2004/12/02 00:06:42 JPerrotto Exp $
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
 * Interface for process listeners.
 */
public interface IAeProcessListener
{
   /**
    * Handle an event fired by the BPEL Engine for a process.
    * @param aEvent The event to handle.
    * @return boolean true if suspend needed, otherwise false.
    */
   public boolean handleProcessEvent(IAeProcessEvent aEvent);
   
   /**
    * Log an information message for the process.
    * @param aEvent The information event to handle.
    */
   public void handleProcessInfoEvent(IAeProcessInfoEvent aEvent);
}
