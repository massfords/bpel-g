// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeManagerAdapter.java,v 1.5 2008/03/11 03:05:22 mford Exp $
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
 * A base class for managers that don't want to have to implement all of the manager methods.
 */
public abstract class AeManagerAdapter implements IAeManager
{
   /** The engine for this manager. */
   private IAeBusinessProcessEngineInternal mEngine;

   /**
    * @see org.activebpel.rt.bpel.impl.IAeQueueManager#getEngine()
    */
   public IAeBusinessProcessEngineInternal getEngine()
   {
      return mEngine;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#setEngine(org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal)
    */
   public void setEngine(IAeBusinessProcessEngineInternal aEngine)
   {
      mEngine = aEngine;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#create()
    */
   public void create() throws Exception
   {
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#prepareToStart()
    */
   public void prepareToStart() throws Exception
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#start()
    */
   public void start() throws Exception
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#stop()
    */
   public void stop()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#destroy()
    */
   public void destroy()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#accept(org.activebpel.rt.bpel.impl.IAeManagerVisitor)
    */
   public void accept(IAeManagerVisitor aVisitor) throws Exception
   {
      aVisitor.visit(this);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#getAdapter(java.lang.Class)
    */
   public IAeImplAdapter getAdapter(Class aAdapterInterface)
   {
      if (aAdapterInterface.isAssignableFrom(getClass()))
         return (IAeImplAdapter) this;
      return null;
   }
}
