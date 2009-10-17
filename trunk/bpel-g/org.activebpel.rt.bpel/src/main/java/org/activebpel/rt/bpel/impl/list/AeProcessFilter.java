// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeProcessFilter.java,v 1.14 2007/10/01 20:34:00 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.util.Date;

import javax.xml.namespace.QName;

/**
 * Specifies a filter object used in the selection of process which exist on
 * a BPEL engine.
 */
public class AeProcessFilter extends AeListingFilter
{
   /** Used to specify filter for process state to all processes, running and completed. */
   public final static int STATE_ANY = 0;
   /** Used to specify filter for process state to only running processes. */
   public final static int STATE_RUNNING = 1;
   /** Used to specify filter for process state to only completed processes. */
   public final static int STATE_COMPLETED = 2;
   /** Used to specify filter for process state to only faulted processes. */
   public final static int STATE_FAULTED = 3;
   /** Used to specify filter for process state to completed or faulted processes. */
   public final static int STATE_COMPLETED_OR_FAULTED = 4;
   /** Used to specify filter for suspended process state. */
   public static final int STATE_SUSPENDED = 5;
   /** Used to specify filter for processes that have been suspended due to uncaught fault. */
   public static final int STATE_SUSPENDED_FAULTING = 6;
   /** Used to specify filter for processes that have been suspended due to suspend activity. */
   public static final int STATE_SUSPENDED_PROGRAMMATIC = 7;
   /** Used to specify filter for processes that have been suspended by user manually. */
   public static final int STATE_SUSPENDED_MANUAL = 8;
   /** Used to specify filter for processes that have completed their normal execution and are eligible for compensation */
   public static final int STATE_COMPENSATABLE = 9;
   /** Used to specify filter for process that have been suspended due to a pending invoke during process recovery. */
   public static final int STATE_SUSPENDED_INVOKE_RECOVERY = 10;
   /** Used to specify filter for process state to only running and suspended processes. */
   public final static int STATE_RUNNING_OR_SUSPENDED = 11;

   /** The namespace qualified name of process we are looking for. */
   private QName mProcessName;
   /** The process group we are looking for. */
   private String mProcessGroup;
   /** The flag inidicating that results should hide the system process group. */
   private boolean mHideSystemProcessGroup;
   /** The state of the process on the server we are looking for. */
   private int mProcessState;
   /** Specifies the process creation starting date range to be included in the results */
   private Date mProcessCreateStart;
   /** Specifies the process creation ending date range to be included in the results */
   private Date mProcessCreateEnd;
   /** Specifies the process completion starting date range to be included in the results */
   private Date mProcessCompleteStart;
   /** Specifies the process completion ending date range to be included in the results */
   private Date mProcessCompleteEnd;
   /** Specifies an advanced query for filtering results for future extension. */
   private String mAdvancedQuery;
   /** Specifies a planId of processes we are looking for */
   private int mPlanId;
   /** Specifies a mDeletableDate (currentDeleteDate - retentionDays) for this process. */
   private Date mDeletableDate;
   /** Specifies a delete processId range for the processes to be deleted. */
   private long[] mProcessIdRange;

   /**
    * Default constructor.
    */
   public AeProcessFilter()
   {
   }

   /**
    * Constructor which takes as input a process name we would like to filter by.
    * @param aProcessName the process name we want to filter by
    */
   public AeProcessFilter(QName aProcessName)
   {
      setProcessName(aProcessName);
   }

   /**
    * Returns the namespace qualified process name to be used in the filter spec.
    * A value of null indicates all process names are to be considered.
    */
   public QName getProcessName()
   {
      return mProcessName;
   }

   /**
    * Sets the namespace qualified process name to be used in the filter spec.
    * A value of null indicates all process names are to be considered.
    * @param aName the process name to be set.
    */
   public void setProcessName(QName aName)
   {
      mProcessName = aName;
   }

   /**
    * Returns the process state to be used in the filter spec.
    */
   public int getProcessState()
   {
      return mProcessState;
   }

   /**
    * Sets the process state to be used in the filter spec.
    * @param aState the state to be set for filtering
    */
   public void setProcessState(int aState)
   {
      mProcessState = aState;
   }

   /**
    * Returns the process creation start date from which to return results.
    * A value of null indicates we should consider all processes since start of server.
    */
   public Date getProcessCreateStart()
   {
      return mProcessCreateStart;
   }

   /**
    * Sets the process creation start date from which to return results.
    * A value of null indicates we should consider all processes since start of server.
    * @param aDate starting date to be set
    */
   public void setProcessCreateStart(Date aDate)
   {
      mProcessCreateStart = aDate;
   }

   /**
    * Returns the process creation end date from which to return results.
    * A value of null indicates we should consider all processes up until current date.
    */
   public Date getProcessCreateEnd()
   {
      return mProcessCreateEnd;
   }

   /**
    * Returns the start of the day after the process creation end date.
    */
   public Date getProcessCreateEndNextDay()
   {
      return getNextDay(getProcessCreateEnd());
   }

   /**
    * Sets the process creation end date from which to return results.
    * A value of null indicates we should consider all processes up until current date.
    * @param aDate ending date to be set
    */
   public void setProcessCreateEnd(Date aDate)
   {
      mProcessCreateEnd = aDate;
   }

   /**
    * Returns the process completion start date from which to return results.
    * A value of null indicates we should consider all processes since start of server.
    */
   public Date getProcessCompleteStart()
   {
      return mProcessCompleteStart;
   }

   /**
    * Sets the process completion start date from which to return results.
    * A value of null indicates we should consider all processes since start of server.
    * @param aDate starting date to be set
    */
   public void setProcessCompleteStart(Date aDate)
   {
      mProcessCompleteStart = aDate;
   }

   /**
    * Returns the process completion end date from which to return results.
    * A value of null indicates we should consider all processes up until current date.
    */
   public Date getProcessCompleteEnd()
   {
      return mProcessCompleteEnd;
   }

   /**
    * Returns the start of the day after the process completion end date.
    */
   public Date getProcessCompleteEndNextDay()
   {
      return getNextDay(getProcessCompleteEnd());
   }

   /**
    * Sets the process completion end date from which to return results.
    * A value of null indicates we should consider all processes up until current date.
    * @param aDate ending date to be set
    */
   public void setProcessCompleteEnd(Date aDate)
   {
      mProcessCompleteEnd = aDate;
   }
   
   /**
    * @return Returns the advancedQuery.
    */
   public String getAdvancedQuery()
   {
      return mAdvancedQuery;
   }
   
   /**
    * @param aAdvancedQuery The advancedQuery to set.
    */
   public void setAdvancedQuery(String aAdvancedQuery)
   {
      mAdvancedQuery = aAdvancedQuery;
   }
   
   /**
    * WS Workaround, overrides method to avoid bean issue when trying to install as service input. 
    * @see org.activebpel.rt.bpel.impl.list.AeListingFilter#setListStart(int)
    */
   public void setListStart(int aListStart)
   {
      super.setListStart(aListStart);
   }

   /**
    * WS Workaround, overrides method to avoid bean issue when trying to install as service input. 
    * @see org.activebpel.rt.bpel.impl.list.AeListingFilter#setMaxReturn(int)
    */
   public void setMaxReturn(int aMaxReturn)
   {
      super.setMaxReturn(aMaxReturn);
   }

   /**
    * WS Workaround, overrides method to avoid bean issue when trying to install as service input. 
    * @see org.activebpel.rt.bpel.impl.list.IAeListingFilter#getListStart()
    */
   public int getListStart()
   {
      return super.getListStart();
   }

   /**
    * WS Workaround, overrides method to avoid bean issue when trying to install as service input. 
    * @see org.activebpel.rt.bpel.impl.list.IAeListingFilter#getMaxReturn()
    */
   public int getMaxReturn()
   {
      return super.getMaxReturn();
   }
      
   /**
    * @return the planId
    */
   public int getPlanId()
   {
      return mPlanId;
   }

   /**
    * @param aPlanId the planId to set
    */
   public void setPlanId(int aPlanId)
   {
      mPlanId = aPlanId;
   }

   /**
    * @return the mDeletableDate
    */
   public Date getDeletableDate()
   {
      return mDeletableDate;
   }

   /**
    * @param aDeletableDate the aDeletableDate to set
    */
   public void setDeletableDate(Date aDeletableDate)
   {
      mDeletableDate = aDeletableDate;
   }
   
   /**
    * @return the mProcessIdRange
    */
   public long[] getProcessIdRange()
   {
      return mProcessIdRange;
   }

   /**
    * @param aProcessIdRange the delete range to set
    */
   public void setProcessIdRange(long[] aProcessIdRange)
   {
      mProcessIdRange = aProcessIdRange;
   }

   /**
    * @return the processGroup
    */
   public String getProcessGroup()
   {
      return mProcessGroup;
   }

   /**
    * @param aProcessGroup the processGroup to set
    */
   public void setProcessGroup(String aProcessGroup)
   {
      mProcessGroup = aProcessGroup;
   }

   /**
    * @return the hideSystemProcessGroup indicator
    */
   public boolean isHideSystemProcessGroup()
   {
      return mHideSystemProcessGroup;
   }

   /**
    * @param aHideSystemProcessGroup the hideSystemProcessGroup to set
    */
   public void setHideSystemProcessGroup(boolean aHideSystemProcessGroup)
   {
      mHideSystemProcessGroup = aHideSystemProcessGroup;
   }
}
