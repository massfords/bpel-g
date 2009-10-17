//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeListResult.java,v 1.4 2005/02/19 00:43:56 KRoe Exp $
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base class for list results.
 */
public class AeListResult implements Serializable
{
   /** The total number of rows which match the process list request. */
   protected int mTotalRowCount;

   /** <code>true</code> if and only if <code>mTotalRowCount</code> is the true total row count. */
   protected boolean mCompleteRowCount = true;

   /** Container for the results. */
   protected List mResults = new ArrayList();

   /**
    * Default constructor for WebLogic bean serializer.
    */
   public AeListResult()
   {
   }

   /**
    * Constructor.
    * 
    * @param aTotalRowCount
    * @param aResults
    * @param aCompleteRowCount
    */
   protected AeListResult(int aTotalRowCount, Collection aResults, boolean aCompleteRowCount)
   {
      setTotalRowCount(aTotalRowCount);
      getResultsInternal().addAll(aResults);
      setCompleteRowCount(aCompleteRowCount);
   }

   /**
    * Returns the total number of rows which match the filter set used to
    * obtain the results. May actually be larger than the detail list of processes.
    */
   public int getTotalRowCount()
   {
      return mTotalRowCount;
   }

   /**
    * Sets the total the total number of rows which match the filter.
    * May actually be larger than the detail list of processes.
    */
   public void setTotalRowCount(int aTotalRowCount)
   {
      mTotalRowCount = aTotalRowCount;
   }

   /**
    * Returns <code>true</code> if and only if <code>getTotalRowCount()</code> reports
    * the true total row count.
    */
   public boolean isCompleteRowCount()
   {
      return mCompleteRowCount;
   }

   /**
    * Sets flag indicating whether {@link #getTotalRowCount()}returns
    * the true total row count.
    */
   public void setCompleteRowCount(boolean aCompleteRowCount)
   {
      mCompleteRowCount = aCompleteRowCount;
   }

   /**
    * Return true if there are no results in the listing.
    */
   public boolean isEmpty()
   {
      return getResultsInternal().isEmpty();
   }

   /**
    * Accessor for results container.
    */
   protected List getResultsInternal()
   {
      return mResults;
   }
}