// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeAbstractListingBean.java,v 1.2 2004/09/21 02:38:32 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

/**
 *  Provides common methods for displaying "scrollable" rows on web page.
 */
public abstract class AeAbstractListingBean extends AeAbstractAdminBean
{
   /** Number of rows to display. */
   protected int mRowCount = 20;
   /** Start offset. */
   protected int mRowStart;
   /** Indicates if there are more rows to view. */
   protected boolean mNextPage;
   /** Total number of rows available (not necessarily on this page though). */
   protected int mTotalRows;
   /** Number of rows that actually displayed.  Will be less than or equal to mRowCount. */
   protected int mRowsDisplayed;
   
   
   /**
    * Set the row start offset.
    * @param aStart The starting offset.
    */
   public void setRowStart( int aStart )
   {
      mRowStart = aStart;
   }
   
   /**
    * Accessor of the row start offset.
    */
   public int getRowStart()
   {
      return mRowStart;
   }
   
   /**
    * Set the maximum number of rows to return.
    * Default value is 10.
    * @param aCount The max number of rows to return.
    */
   public void setRowCount( int aCount )
   {
      mRowCount = aCount;
   }
   
   /**
    * Accessor for the max number of rows to return.
    */
   public int getRowCount()
   {
      return mRowCount;
   }
   
   /**
    * Getter for displayed rows start.
    */
   public int getDisplayStart()
   {
      return mRowStart + 1;
   }
   
   /**
    * Getter for displayed rows end.
    */
   public int getDisplayEnd()
   {
      return mRowStart + getRowsDisplayed();
   }
   
   /**
    * Returns true if there is a next page.
    */
   public boolean isNextPage()
   {
      return mNextPage;
   }
   
   /**
    * Determine if there are more rows to display than are visible on the
    * current page.
    */
   protected void updateNextPageStatus()
   {
      setNextPage( getTotalRowCount() > (getRowCount()+getRowStart()) );
   }

   /**
    * Setter for the next page property.
    * @param aValue
    */
   public void setNextPage( boolean aValue )
   {
      mNextPage = aValue;
   }
   
   /**
    * Returns true if there is a previous page.
    */
   public boolean isPreviousPage()
   {
      return mRowStart > 0;
   }
   
   /**
    * Returns the next page offset.
    */
   public int getNextPageOffset()
   {
      return mRowStart + mRowCount;
   }
   
   /**
    * Returns the previous page offset.
    */
   public int getPreviousPageOffset()
   {
      return Math.max( (mRowStart) - mRowCount, 0 );
   }
   
   /**
    * Getter for total row count.
    */
   public int getTotalRowCount()
   {
      return mTotalRows;
   }
   
   /**
    * Setter for the total row count.
    * @param aTotal
    */
   public void setTotalRowCount( int aTotal )
   {
      mTotalRows = aTotal;
   }
   
   /**
    * Indicates if this listing is empty.
    */
   abstract public boolean isEmpty();
   
   /**
    * Setter for the number of rows that are actually displayed.
    * @param aRows The actual number of rows that are displayed.
    */
   protected void setRowsDisplayed( int aRows )
   {
      mRowsDisplayed = aRows;    
   }
   
   /**
    * Return the actual number of rows displayed.
    */
   protected int getRowsDisplayed()
   {
      return mRowsDisplayed;
   }
}
