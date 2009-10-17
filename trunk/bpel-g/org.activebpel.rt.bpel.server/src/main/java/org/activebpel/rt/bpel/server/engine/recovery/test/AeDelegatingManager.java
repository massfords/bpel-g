// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/test/AeDelegatingManager.java,v 1.2 2008/03/11 03:09:29 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.test;

import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeManager;
import org.activebpel.rt.bpel.impl.IAeManagerVisitor;
import org.activebpel.rt.bpel.impl.activity.IAeImplAdapter;

/**
 * Implements a manager that delegates all method calls to an underlying
 * delegate manager.
 */
public class AeDelegatingManager implements IAeManager
{
   /** The underlying delegate manager. */
   private final IAeManager mBaseManager;

   /**
    * Constructs a manager that delegates all method calls to the given base
    * manager.
    *
    * @param aBaseManager
    */
   public AeDelegatingManager(IAeManager aBaseManager)
   {
      mBaseManager = aBaseManager;
   }

   /**
    * Returns the base process manager.
    */
   protected IAeManager getBaseManager()
   {
      return mBaseManager;
   }

   /*======================================================================
    * org.activebpel.rt.bpel.impl.IAeManager methods
    *======================================================================
    */

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#accept(org.activebpel.rt.bpel.impl.IAeManagerVisitor)
    */
   public void accept(IAeManagerVisitor aVisitor) throws Exception
   {
      getBaseManager().accept(aVisitor);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#create()
    */
   public void create() throws Exception
   {
      getBaseManager().create();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#destroy()
    */
   public void destroy()
   {
      getBaseManager().destroy();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#getEngine()
    */
   public IAeBusinessProcessEngineInternal getEngine()
   {
      return getBaseManager().getEngine();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#prepareToStart()
    */
   public void prepareToStart() throws Exception
   {
      getBaseManager().prepareToStart();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#setEngine(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine)
   {
      getBaseManager().setEngine(aEngine);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#start()
    */
   public void start() throws Exception
   {
      getBaseManager().start();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#stop()
    */
   public void stop()
   {
      getBaseManager().stop();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#getAdapter(java.lang.Class)
    */
   public IAeImplAdapter getAdapter(Class aAdapterInterface)
   {
      return getBaseManager().getAdapter(aAdapterInterface);
   }
}
