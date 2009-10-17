// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeAlarmListingBean.java,v 1.7 2007/04/24 17:28:17 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.util.Date;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.list.AeAlarmExt;
import org.activebpel.rt.bpel.impl.list.AeAlarmFilter;
import org.activebpel.rt.bpel.impl.list.AeAlarmListResult;

/**
 *  Wraps the details for the Message Receivers Queue listing.
 */
public class AeAlarmListingBean extends AeAbstractListingBean
{
   /** Alarms to display. */   
   protected AeAlarmExt[] mAlarms;
   /** Process id. */
   protected long mProcessId = AeAlarmFilter.NULL_ID;
   /** Select process QName. */
   protected QName mQName;
   /** The current row being processed. */
   protected int mCurrentIndex;
   /** Selected start date. */
   protected Date mStartDate;
   /** Selected end date. */
   protected Date mEndDate;
   /** Current status */
   protected String mStatus;

   /**
    * Constructor.  
    */
   public AeAlarmListingBean()
   {
      super();
   }
   
   /**
    * @see org.activebpel.rt.war.tags.IAeErrorAwareBean#addError(java.lang.String, java.lang.String, java.lang.String)
    */
   public void addError(String aPropertyName, String aValue,
         String aErrorMessage)
   {
      super.addError(aPropertyName, aValue, aErrorMessage);
   }

   /**
    * Setting this flag to true signals that the bean should update its state 
    * based on the filter parameters.
    * @param aUpdateFlag
    */
   public void setFinished( boolean aUpdateFlag )
   {
      if( aUpdateFlag )
      {
         AeAlarmFilter filter = new AeAlarmFilter();
         filter.setProcessId( mProcessId );
         filter.setProcessName( mQName );
         filter.setListStart( getRowStart() );
         filter.setMaxReturn( getRowCount() );
         updateStartDate( filter );
         updateEndDate( filter );
         
         AeAlarmListResult results = getAdmin().getAlarms( filter );
            
         if( !results.isEmpty() )
         { 
            setTotalRowCount( results.getTotalRowCount() );
            updateNextPageStatus();
            mAlarms = results.getResults();
            for( int i = 0 ; i < results.getResults().length; i++ )
            {
               AeAlarmExt alarm = results.getResults()[i];
               String location = results.getLocationPath( alarm.getPathId() );
               alarm.setLocation(location);
            }
            setRowsDisplayed( mAlarms.length );
         }
         else
         {
            setNextPage( false );
         }
      }
   }
   
   /**
    * Update the filter start date.
    * @param filter
    */
   protected void updateStartDate( AeAlarmFilter filter )
   {
      filter.setAlarmFilterStart( getStartDate() );  
   }
   
   /**
    * Update the filter end date.
    * @param filter
    */
   protected void updateEndDate( AeAlarmFilter filter )
   {
      filter.setAlarmFilterEnd( getEndDate() );  
   }
   
   /**
    * Indexed accessor for an alarm.
    * @param aIndex
    * @return The AeAlarmExt mapped to the index.
    */
   public AeAlarmExt getAlarmInstance( int aIndex )
   {
      return mAlarms[ aIndex ];          
   }
   
   /**
    * Accessor for the number of the receivers.
    */
   public int getAlarmInstanceSize()
   {
      if( mAlarms == null )
      {
         return 0;
      }
      else
      {
         return mAlarms.length;
      }
   }

   /**
    * Setter for the process id.
    * @param aId A process id.
    */
   public void setProcessId( String aId )
   {
      try
      {
         mProcessId = Long.parseLong(aId);
      }
      catch( NumberFormatException e )
      {
         // todo - error handling
      }
   }
   
   /**
    * Getter for the process id.
    */
   public String getProcessId()
   {
      if( mProcessId == AeAlarmFilter.NULL_ID )
      {
         return ""; //$NON-NLS-1$
      }
      else
      {
         return String.valueOf(mProcessId);
      }
   }
   
   /**
    * Setter for the start date.
    * 
    * @param aDate
    */
   public void setStartDate( Date aDate )
   {
      mStartDate = aDate;
   }
   
   /**
    * Getter for start date property.
    */
   public Date getStartDate()
   {
      return mStartDate;
   }
   
   /**
    * Setter for the end date.
    * 
    * @param aDate
    */
   public void setEndDate( Date aDate )
   {
      mEndDate = aDate;
   }
   
   /**
    * Getter for end date property.
    */
   public Date getEndDate()
   {
      return mEndDate;
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
    * Returns true if there are detail rows to view. 
    */
   public boolean isPopulated()
   {
      return mAlarms != null && mAlarms.length > 0;
   }
   
   /**
    * @see org.activebpel.rt.bpeladmin.war.web.AeAbstractListingBean#isEmpty()
    */
   public boolean isEmpty()
   {
      return !isPopulated();
   }
}
