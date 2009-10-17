// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/resource/AeInMemoryResourceStats.java,v 1.2 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog.resource;


/**
 *  Tracks cache performance.
 */
public class AeInMemoryResourceStats implements IAeResourceStats
{
   /** Total cache reads. */
   protected int mTotalReads;
   /** Total disk reads. */
   protected int mDiskReads;   

   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceStats#getDiskReads()
    */
   public int getDiskReads()
   {
      return mDiskReads;
   }

   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceStats#getTotalReads()
    */
   public int getTotalReads()
   {
      return mTotalReads;
   }

   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceStats#logDiskRead()
    */
   public void logDiskRead()
   {
      mDiskReads++;
   }

   /**
    * @see org.activebpel.rt.bpel.server.catalog.resource.IAeResourceStats#logTotalRead()
    */
   public void logTotalRead()
   {
      mTotalReads++;
   }
}
