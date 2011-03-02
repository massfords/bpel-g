// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/IAePersistentLogger.java,v 1.4 2005/04/13 17:26:47 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;

import org.activebpel.rt.bpel.server.engine.IAeProcessLogger;

/**
 * Interface for handling logging in a persistent environment.  
 */
public interface IAePersistentLogger extends IAeProcessLogger
{
   /**
    * Returns the current log entry from the logger.
    * @param aProcessId
    */
   public IAeProcessLogEntry getLogEntry(long aProcessId);
}
