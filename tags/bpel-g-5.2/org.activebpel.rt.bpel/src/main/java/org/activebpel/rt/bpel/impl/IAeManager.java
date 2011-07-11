// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeManager.java,v 1.7 2008/03/11 03:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.impl.activity.IAeImplAdapter;


/**
 * Defines the methods that a manager must implement. The application architecture makes
 * use of managers to implement various pieces of the BPEL engine. By isolating the
 * logic in these various implementations (e.g. IAeQueueManager, IAeAlarmManager ...etc)
 * we're able to provide different implementations without affecting the engine code proper.
 */
public interface IAeManager
{
   /**
    * Returns the engine for this queue manager.
    */
   public IAeBusinessProcessEngineInternal getEngine();

   /**
    * Sets the engine for this queue manager.
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine);

   /**
    * Gets called once after the manager has been instantiated. If the manager runs
    * into any kind of fatal error during create then it should throw an exception which will 
    * halt the startup of the application.
    */
   public void create() throws Exception;
   
   /**
    * Called each time before the manager is going to start. 
    */
   public void prepareToStart() throws Exception;

   /**
    * Starts the manager running.
    */
   public void start() throws Exception;
   
   /**
    * Stops the manager. It may be restarted or destroyed from this point.  Note that this method
    * may be called by the engine even though a corresponding <code>start</code> method has not
    * yet been called.  This can happen when some other manager fails to start.
    */
   public void stop();
   
   /**
    * Destroys this instance of the manager. The manager should perform whatever cleanup 
    * work is necessary to shut down. 
    */
   public void destroy();
   
   /**
    * Used for the visitor pattern.
    * @param aVisitor
    */
   public void accept(IAeManagerVisitor aVisitor) throws Exception;
   
   /**
    * Provides a means for the engine to get an adapter from the manager.
    * @param aAdapterInterface
    */
   public IAeImplAdapter getAdapter(Class aAdapterInterface);
}
