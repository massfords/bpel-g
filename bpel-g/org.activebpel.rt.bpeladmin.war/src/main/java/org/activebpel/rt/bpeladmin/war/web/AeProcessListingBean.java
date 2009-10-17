// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeProcessListingBean.java,v 1.15 2007/09/28 19:53:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.util.Date;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpeladmin.war.AeMessages;

/**
 * Controls the filtering/display of active process details.
 */
public class AeProcessListingBean extends AeAbstractListingBean
{
   /** Selected process state. */
   protected int mState = AeProcessFilter.STATE_ANY;

   /** Selected process creation start date. */
   protected Date mCreateStartDate;

   /** Selected process creation end date. */
   protected Date mCreateEndDate;

   /** Select process QName. */
   protected QName mQName;

   /** Row details. */
   protected AeProcessInstanceDetailWrapper[] mDetails;

   /** Optional suffix for the total row count. */
   protected String mTotalRowCountSuffix;

   /** Selected process completion start date. */
   protected Date mCompleteStartDate;

   /** Selected process completion end date. */
   protected Date mCompleteEndDate;
   
   /** Advanced query for filtering. */
   protected String mAdvancedQuery = ""; //$NON-NLS-1$

   /**
    * Default constructor.
    */
   public AeProcessListingBean()
   {
   }

   /**
    * Returns true if any state is selected.
    */
   public boolean isActiveState()
   {
      return mState == AeProcessFilter.STATE_ANY;
   }

   /**
    * Returns true if running state is selected.
    */
   public boolean isRunningState()
   {
      return mState == AeProcessFilter.STATE_RUNNING;
   }

   /**
    * Returns true if complete state is selected.
    */
   public boolean isCompleteState()
   {
      return mState == AeProcessFilter.STATE_COMPLETED;
   }
   
   /**
    * Returns true if compensatable state is selected
    */
   public boolean isCompensatableState()
   {
      return mState == AeProcessFilter.STATE_COMPENSATABLE;
   }

   /**
    * Returns true if faulted state is selected.
    */
   public boolean isFaultedState()
   {
      return mState == AeProcessFilter.STATE_FAULTED;
   }
   
   /**
    * Return ture if the suspended state is selected.
    */
   public boolean isSuspendedState()
   {
      return mState == AeProcessFilter.STATE_SUSPENDED;
   }
   
   /**
    * Return ture if the suspended state (due to uncaught fault) is selected.
    */
   public boolean isSuspendedFaultingState()
   {
      return mState == AeProcessFilter.STATE_SUSPENDED_FAULTING;
   }
   
   /**
    * Return ture if the suspended state (due to suspend activity) is selected.
    */
   public boolean isSuspendedProgrammaticState()
   {
      return mState == AeProcessFilter.STATE_SUSPENDED_PROGRAMMATIC;
   }

   /**
    * Return ture if the suspended state (due to suspend activity) is selected.
    */
   public boolean isSuspendedManualState()
   {
      return mState == AeProcessFilter.STATE_SUSPENDED_MANUAL;
   }

   /**
    * Returns <code>true</code> if and only if the suspended state (due to
    * invoke recovery) is selected.
    */
   public boolean isSuspendedInvokeRecoveryState()
   {
      return mState == AeProcessFilter.STATE_SUSPENDED_INVOKE_RECOVERY;
   }

   /**
    * Setter for the state property.
    * @param aState
    */
   public void setState( int aState )
   {
      mState = aState;
   }

   /**
    * Setter for the process creation start date.
    * 
    * @param aDate
    */
   public void setCreateStartDate( Date aDate )
   {
      mCreateStartDate = aDate;
   }

   /**
    * Getter for process creation start date property.
    */
   public Date getCreateStartDate()
   {
      return mCreateStartDate;
   }

   /**
    * Setter for the process creation end date.
    * @param aDate
    */
   public void setCreateEndDate( Date aDate )
   {
      mCreateEndDate = aDate;
   }

   /**
    * Getter for process creation end date property.
    */
   public Date getCreateEndDate()
   {
      return mCreateEndDate;
   }

   /**
    * Setter for process selection qname.  If ns is empty, only
    * local part will be used.
    * @param aQName
    */
   public void setQname( String aQName )
   {
      mQName = AeWebUtil.toQName( aQName );
   }

   /**
    * Accessor for qname.
    */
   public String getQname()
   {
      return AeWebUtil.toString( mQName );
   }

   /**
    * If the arg value is true (this is sort of a bean hack), then
    * the bean will select the relevant processes based on the
    * filter information.
    * @param aValue
    */
   public void setFinished( boolean aValue )
   {
      if( aValue )
      {
         AeProcessFilter filter = createFilter();

         try
         {

            AeProcessListResult results = getAdmin().getProcessList(filter);
            AeProcessInstanceDetail[] details = results.getRowDetails();

            if( details != null && details.length > 0 )
            {
               int length = details.length < mRowCount ? details.length : mRowCount;
               AeProcessInstanceDetailWrapper[] wrappers = new AeProcessInstanceDetailWrapper[length];

               for( int i = 0; i < length; i++ )
               {
                  wrappers[i] = wrapDetail(details[i]);
               }

               mDetails = wrappers;
               setTotalRowCount( results.getTotalRowCount() );
               updateNextPageStatus();
               setRowsDisplayed( mDetails.length );

               // Display "+" after row count if the row count wasn't completed.
               setTotalRowCountSuffix(results.isCompleteRowCount() ? "" : "+"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
               setNextPage( false );
            }
         }
         catch (AeException e)
         {
            // on error clear the currently displayed list
            mDetails = new AeProcessInstanceDetailWrapper[0];
            // display message from the root of the exception trace which is probably the most meaningful info
            setStatusDetail(AeMessages.format("AeProcessListingBean.ERROR_3", e.getRootRootCause().getLocalizedMessage())); //$NON-NLS-1$
         }
      }
   }

   /**
    * @return the filter to use for the listing.
    */
   protected AeProcessFilter createFilter()
   {
      AeProcessFilter filter = new AeProcessFilter();
      filter.setListStart( getRowStart() );
      filter.setMaxReturn( getRowCount() );
      filter.setProcessState( mState );
      filter.setProcessName( mQName );
      filter.setAdvancedQuery( getAdvancedQuery() );
      filter.setProcessCreateStart( getCreateStartDate() );
      filter.setProcessCreateEnd( getCreateEndDate() );
      filter.setProcessCompleteStart( getCompleteStartDate() );
      filter.setProcessCompleteEnd( getCompleteEndDate() );
      return filter;
   }

   /**
    * Wraps the process instance detail using the process instance detail wrapper.
    * 
    * @param aDetail
    */
   protected AeProcessInstanceDetailWrapper wrapDetail(AeProcessInstanceDetail aDetail)
   {
      return new AeProcessInstanceDetailWrapper(aDetail);
   }
   
   /**
    * Returns the process wrapper identified by the passed index.
    * @param aIndex
    */
   public AeProcessInstanceDetailWrapper getInstanceDetail( int aIndex )
   {
      return mDetails[aIndex];
   }

   /**
    * Accessor for detail array size.
    */
   public int getInstanceDetailSize()
   {
      if( mDetails == null )
      {
         return 0;
      }
      return mDetails.length;
   }

   /**
    * Returns true if there are detail rows to view.
    */
   public boolean isPopulated()
   {
      return getInstanceDetailSize() > 0;
   }

   /**
    * @see org.activebpel.rt.bpeladmin.war.web.AeAbstractListingBean#isEmpty()
    */
   public boolean isEmpty()
   {
      return !isPopulated();
   }

   /**
    * Sets the suffix used to indicate additional information about the total row count.
    */
   public void setTotalRowCountSuffix(String aTotalRowCountSuffix)
   {
      mTotalRowCountSuffix = aTotalRowCountSuffix;
   }

   /**
    * Returns the suffix used to indicate additional information about the total row count.
    */
   public String getTotalRowCountSuffix()
   {
      return (mTotalRowCountSuffix == null) ? "" : mTotalRowCountSuffix; //$NON-NLS-1$
   }

   /**
    * Setter for the process completion start date.
    * 
    * @param aDate
    */
   public void setCompleteStartDate( Date aDate )
   {
      mCompleteStartDate = aDate;
   }

   /**
    * Getter for process completion start date property.
    */
   public Date getCompleteStartDate()
   {
      return mCompleteStartDate;
   }

   /**
    * Setter for the process completion end date.
    * 
    * @param aDate
    */
   public void setCompleteEndDate( Date aDate )
   {
      mCompleteEndDate = aDate;
   }

   /**
    * Getter for process completion end date property.
    */
   public Date getCompleteEndDate()
   {
      return mCompleteEndDate;
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
}
