// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/IAeLocationVersionSet.java,v 1.1 2004/08/13 20:42:02 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.util.Iterator;

/**
 * Defines interface for a set of objects identified by location id and version
 * number.
 */
public interface IAeLocationVersionSet
{
   /**
    * Adds the specified location id and version number to the set.
    *
    * @param aLocationId
    * @param aVersionNumber
    */
   public void add(long aLocationId, int aVersionNumber);

   /**
    * Returns <code>true</code> if and only if the set contains the specified
    * location id and version number.
    *
    * @param aLocationId
    * @param aVersionNumber
    */
   public boolean contains(long aLocationId, int aVersionNumber);

   /**
    * Returns <code>true</code> if and only if the set contains the location id
    * and version number in the specified entry.
    *
    * @param aEntry
    */
   public boolean contains(IEntry aEntry);

   /**
    * Returns an <code>Iterator</code> over the entries in the set, where
    * entries are instances of <code>IAeLocationVersionEntry</code>.
    */
   public Iterator iterator();

   /**
    * Defines the interface for an entry in an <code>IAeLocationVersionSet</code>.
    */
   public interface IEntry
   {
      /**
       * Returns the location id.
       */
      public long getLocationId();

      /**
       * Returns the version number.
       */
      public int getVersionNumber();
   }
}
