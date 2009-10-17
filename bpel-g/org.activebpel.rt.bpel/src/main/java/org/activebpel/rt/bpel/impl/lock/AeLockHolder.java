// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/lock/AeLockHolder.java,v 1.2 2004/07/08 13:10:00 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.lock;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Impl of the lock holder interface that allows multiple owners */
public class AeLockHolder
{
   /** Current owners of the lock */
   private Set mOwners = new HashSet();
   /** True if the lock holder is exclusive meaning that only 1 owner is allowed */
   private boolean mExclusive = false;

   /**
    * @param aOwnerPath
    */
   public void add(String aOwnerPath)
   {
      mOwners.add(aOwnerPath);
   }

   /**
    * Returns <code>true</code> if and only if this lock holder has no owners.
    */
   public boolean isEmpty()
   {
      return mOwners.isEmpty();
   }

   /**
    * Returns <code>true</code> if and only if the lock is exclusive.
    */
   public boolean isExclusive()
   {
      return mExclusive;
   }

   /**
    * @param aOwnerPath
    */
   public void remove(String aOwnerPath)
   {
      mOwners.remove(aOwnerPath);
   }

   /**
    * @param aFlag
    */
   public void setExclusive(boolean aFlag)
   {
      mExclusive = aFlag;
   }

   /**
    * Returns <code>true</code> if and only if additionals owners can be added to this lock holder.
    */
   public boolean canAdd()
   {
      return isExclusive()? isEmpty() : true;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object o)
   {
      if (!(o instanceof AeLockHolder))
      {
         return false;
      }

      AeLockHolder other = (AeLockHolder) o;

      if (isExclusive() != other.isExclusive())
      {
         return false;
      }

      return getOwners().equals(other.getOwners());
   }

   /**
    * Returns the set of lock owners.
    */
   public Set getOwners()
   {
      return Collections.unmodifiableSet(mOwners);
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return (isExclusive() ? 1 : 0)
           + getOwners().hashCode()
           ;
   }
}
