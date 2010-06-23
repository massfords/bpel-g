// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/factory/IAeWorkManagerFactory.java,v 1.1 2007/07/31 20:54:25 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work.factory;

import commonj.work.WorkManager;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.work.input.IAeInputMessageWorkManager;

/**
 * Defines the interface for the factory that provides the CommonJ
 * <code>WorkManager</code> and the {@link IAeInputMessageWorkManager} (input
 * message work manager) to the engine.
 */
public interface IAeWorkManagerFactory
{
   /**
    * Initializes the work manager factory from the engine configuration.
    *
    * @param aConfigMap
    */
   public void init(Map aConfigMap) throws AeException;

   /**
    * Returns the CommonJ <code>WorkManager</code> for scheduling CommonJ
    * <code>Work</code>.
    */
   public WorkManager getWorkManager() throws AeException;

   /**
    * Returns <code>true</code> if and only if the work manager returned by
    * {@link #getWorkManager()} is our internal implementation.
    */
   public boolean isInternalWorkManager() throws AeException;

   /**
    * Returns the {@link IAeInputMessageWorkManager} for scheduling input
    * message work.
    */
   public IAeInputMessageWorkManager getInputMessageWorkManager() throws AeException;
}
