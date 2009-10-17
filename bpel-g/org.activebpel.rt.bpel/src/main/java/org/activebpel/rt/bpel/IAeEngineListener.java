// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeEngineListener.java,v 1.6 2006/01/19 21:17:42 ckeller Exp $
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
 * Interface for engine listeners.
 */
public interface IAeEngineListener
{
   /**
    * Handle an event fired by the BPEL Engine.
    * @param aEvent The event to handle.
    * @return boolean indicating whether to suspend the process the engine event is for during STARTED handling. True means suspend. 
    */
   public boolean handleEngineEvent(IAeEngineEvent aEvent);
   
   /**
    * Handle an alert event fired by the BPEL Engine.
    * @param aEvent
    */
   public void handleAlert(IAeEngineAlert aEvent);
}
