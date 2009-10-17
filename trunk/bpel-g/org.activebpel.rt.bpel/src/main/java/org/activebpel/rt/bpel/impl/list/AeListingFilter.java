//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeListingFilter.java,v 1.5 2006/01/31 20:14:13 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list; 

import java.io.Serializable;
import java.util.Date;

import org.activebpel.rt.util.AeDate;

/**
 * Base class for filtering operations.
 */
public class AeListingFilter implements IAeListingFilter, Serializable
{
   /** The maximum set of results to be returned by this call, 0 is unlimited */
   private int mMaxReturn;
   /** The row number to start fetching results if a max return size is specified. */
   private int mListStart;
   
   /**
    * Convenience method that returns true if the value passed in is within the
    * range of rows that we're looking.  
    * @param aRowCount
    */
   public boolean isWithinRange(int aRowCount)
   {
      if (aRowCount > getListStart())
      {
         // figure out how many rows have actually been returned
         // at this point
         int actualNumberReturned = aRowCount - getListStart();

         // we've skipped passed the number of rows we're supposed to
         // we'll accept this row if it's less than our max or if our
         // max is 0
         return getMaxReturn() == 0 || actualNumberReturned <= getMaxReturn();
      }
      return false;
   }
   
   /**
    * Returns the maximum number of results to be returned.
    */
   public int getMaxReturn()
   {
      return mMaxReturn;
   }

   /**
    * Sets the maximum number of results to be returned.
    * @param aMaxReturn maximum results to be returned.
    */
   public void setMaxReturn(int aMaxReturn)
   {
      mMaxReturn = aMaxReturn;
   }

   /**
    * Returns the starting position from which to return results.
    */
   public int getListStart()
   {
      return mListStart;
   }

   /**
    * Sets the starting position from which to return results.
    * @param aListStart starting position from which to return results
    */
   public void setListStart(int aListStart)
   {
      mListStart = aListStart;
   }

   /**
    * Returns the start of the day following the specified date.
    */
   protected Date getNextDay(Date aDate)
   {
      return AeDate.getStartOfNextDay(aDate);
   }
}
 