// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/process/IAeProcessWrapperMapCallback.java,v 1.1 2005/07/12 00:24:08 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.process;

/**
 * Defines the interface for an instance of {@link
 * org.activebpel.rt.bpel.server.engine.process.IAeProcessWrapperMap} to call back to
 * its owner.
 */
public interface IAeProcessWrapperMapCallback
{
   /**
    * Called by the process wrapper map when it is full.
    */
   public void notifyProcessWrapperMapFull();
}
