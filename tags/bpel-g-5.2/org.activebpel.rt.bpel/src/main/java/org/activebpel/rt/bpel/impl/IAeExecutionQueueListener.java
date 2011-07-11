// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeExecutionQueueListener.java,v 1.1 2004/07/31 00:33:32 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

/**
 * Defines interface for listening to <code>AeExecutionQueue</code> events.
 */
public interface IAeExecutionQueueListener
{
   /**
    * Handles execution queue event.
    *
    * @param aEvent
    */
   public void handleExecutionQueueEvent(IAeExecutionQueueEvent aEvent);
}
