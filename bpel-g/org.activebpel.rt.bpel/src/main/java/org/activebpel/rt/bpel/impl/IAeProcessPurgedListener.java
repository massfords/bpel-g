// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeProcessPurgedListener.java,v 1.1 2007/05/08 18:58:46 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

/**
 * Defines interface for listeners that are notified when a process is purged by
 * the process manager.
 */
public interface IAeProcessPurgedListener
{
   /**
    * Notifies the listener that the given process has been purged by the
    * process manager.
    *
    * @param aProcessId
    */
   public void processPurged(long aProcessId);
}
