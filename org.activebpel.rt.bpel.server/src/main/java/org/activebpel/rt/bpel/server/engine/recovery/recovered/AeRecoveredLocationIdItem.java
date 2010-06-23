// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/recovered/AeRecoveredLocationIdItem.java,v 1.3 2007/11/15 21:06:53 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.recovered;

/**
 * Base class for recovered items that match by location id.
 */
public abstract class AeRecoveredLocationIdItem implements IAeRecoveredItem
{
   /** The process id. */
   private final long mProcessId;

   /** The location id. */
   private final int mLocationId;

   /**
    * Constructs a recovered item that matches by location id.
    */
   protected AeRecoveredLocationIdItem(long aProcessId, int aLocationId)
   {
      mProcessId = aProcessId;
      mLocationId = aLocationId;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aOther)
   {
      if (aOther instanceof AeRecoveredLocationIdItem)
      {
         AeRecoveredLocationIdItem other = (AeRecoveredLocationIdItem) aOther;
         return other.getLocationId() == getLocationId() &&
                other.isRemoval() == isRemoval();
      }
      return false;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.recovered.IAeRecoveredItem#getLocationId()
    */
   public int getLocationId()
   {
      return mLocationId;
   }

   /**
    * Returns the process id.
    */
   protected long getProcessId()
   {
      return mProcessId;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return getLocationId();
   }
}