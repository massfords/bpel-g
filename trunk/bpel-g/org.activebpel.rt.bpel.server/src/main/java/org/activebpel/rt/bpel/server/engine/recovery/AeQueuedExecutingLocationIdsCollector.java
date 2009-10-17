// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/AeQueuedExecutingLocationIdsCollector.java,v 1.1 2008/03/28 01:46:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AeBpelState;
import org.activebpel.rt.bpel.impl.AeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeAlarmReceiver;
import org.activebpel.rt.bpel.impl.activity.IAeMessageReceiverActivity;
import org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor;
import org.activebpel.rt.util.AeLongSet;

/**
 * Determines the location ids for activities that are currently executing by
 * visiting the tree of BPEL implementation objects.
 */
public class AeQueuedExecutingLocationIdsCollector extends AeImplTraversingVisitor
{
   /** The executing location ids set. */
   private AeLongSet mExecutingLocationIds;

   /**
    * Returns the executing location ids set.
    */
   protected AeLongSet getExecutingLocationIds()
   {
      return mExecutingLocationIds;
   }

   /**
    * Returns the location ids for activities that are currently executing in
    * the given process.
    *
    * @param aProcess
    */
   public AeLongSet getExecutingLocationIds(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      setExecutingLocationIds(new AeLongSet());
      
      if (aProcess instanceof AeBusinessProcess)
      {
         ((AeBusinessProcess) aProcess).accept(this);
      }

      return getExecutingLocationIds();
   }

   /**
    * Sets the executing location ids set.
    */
   protected void setExecutingLocationIds(AeLongSet aExecutingLocationIds)
   {
      mExecutingLocationIds = aExecutingLocationIds;
   }

   /**
    * Overrides method to save an activity's location id if the activity is
    * executing.
    * 
    * @see org.activebpel.rt.bpel.impl.visitors.AeImplTraversingVisitor#visitBase(org.activebpel.rt.bpel.impl.AeAbstractBpelObject)
    */
   protected void visitBase(AeAbstractBpelObject aImpl) throws AeBusinessProcessException
   {
      super.visitBase(aImpl);

      // If this activity is executing, then save its location id.
      if (aImpl.getState() == AeBpelState.EXECUTING && (aImpl instanceof IAeAlarmReceiver || aImpl instanceof IAeMessageReceiverActivity) )
      {
         getExecutingLocationIds().add(aImpl.getLocationId());
      }
   }
}
