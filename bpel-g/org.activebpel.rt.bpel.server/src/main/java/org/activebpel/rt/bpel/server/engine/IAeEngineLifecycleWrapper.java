//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/IAeEngineLifecycleWrapper.java,v 1.1 2005/06/20 20:14:34 TWinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine;

import org.activebpel.rt.AeException;

/**
 * Interface for controlling interactions with the BPEL engine from the 
 * servlet entry point.
 */
public interface IAeEngineLifecycleWrapper
{
   /**
    * Initialize the engine and any required resources.
    * @throws AeException
    */
   public void init() throws AeException;
   
   /**
    * Kick off the sequence that is responsible for starting the engine.
    * NOTE: this call may return immediately without the engine having
    * actually been started.
    * @throws AeException
    */
   public void start() throws AeException;
   
   /**
    * Stop the engine and release any associated resources.
    * @throws AeException
    */
   public void stop() throws AeException;

}
