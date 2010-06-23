// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/rdebug/server/AeBreakpointList.java,v 1.1 2004/12/02 00:01:45 JPerrotto Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.rdebug.server;

import java.io.Serializable;


/**
 * Wrapper for the list of breakpoints sent by the designer during remote debugging.
 */
public class AeBreakpointList implements Serializable
{
   /** The total number of rows in the list of breakpoints. */
   private int mTotalRowCount;
   
   /** The detail rows for the list of breakpoints. */
   private AeBreakpointInstanceDetail[] mRowDetails;

   /**
    * No-arg constructor
    */
   public AeBreakpointList()
   {
   }

   /**
    * Returns the row details of the breakpoint definitions.
    */
   public AeBreakpointInstanceDetail[] getRowDetails()
   {
      return mRowDetails;
   }

   /**
    * Returns the total number of rows containing breakpoint definitions.
    */
   public int getTotalRowCount()
   {
      return mTotalRowCount;
   }
   
   /**
    * Sets the detail list in the breakpoint set.
    * @param aDetails breakpoint list details to be set
    */
   public void setRowDetails(AeBreakpointInstanceDetail[] aDetails)
   {
      mRowDetails = aDetails;
   }

   /**
    * Sets the total row count for the breakpoint list.
    * @param aTotalRows total rows to be set
    */
   public void setTotalRowCount(int aTotalRows)
   {
      mTotalRowCount = aTotalRows;
   }
}
