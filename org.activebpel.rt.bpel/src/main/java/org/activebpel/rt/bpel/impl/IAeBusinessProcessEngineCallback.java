// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeBusinessProcessEngineCallback.java,v 1.2 2006/10/18 23:18:53 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;

/**
 * Interface for the business processes call back into the engine .
 */
public interface IAeBusinessProcessEngineCallback
{

   /**
    * Signals that the process has ended. The engine will remove the process from the ProcessManager.
    *
    * @param aProcess process
    */
   public void processEnded(IAeBusinessProcess aProcess) throws AeBusinessProcessException;
}
