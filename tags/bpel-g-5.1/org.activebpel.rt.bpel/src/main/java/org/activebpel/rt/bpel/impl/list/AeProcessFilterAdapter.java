// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeProcessFilterAdapter.java,v 1.10 2008/02/14 20:52:34 mford Exp $
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
import java.util.HashMap;

import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.util.AeUtil;

/**
 * Wraps the AeProcessFilter to encapsulate selection criteria.
 * Since this version is only used for the AeInMemoryProcessManager,
 * we will get the entire collection of processes in the system.
 */
public class AeProcessFilterAdapter
{
   /** Process filter instance. */
   protected AeProcessFilter mFilter;
   /** Current row. */
   protected int mCurrentRow;
   /** Mapping of filter state to process states */
   private static HashMap sMappings = new HashMap();
   
   static
   {
      sMappings.put( new Integer(AeProcessFilter.STATE_COMPLETED), new Integer(IAeBusinessProcess.PROCESS_COMPLETE) );
      sMappings.put( new Integer(AeProcessFilter.STATE_RUNNING), new Integer(IAeBusinessProcess.PROCESS_RUNNING) );
      sMappings.put( new Integer(AeProcessFilter.STATE_FAULTED), new Integer(IAeBusinessProcess.PROCESS_FAULTED) );
      sMappings.put( new Integer(AeProcessFilter.STATE_SUSPENDED), new Integer(IAeBusinessProcess.PROCESS_SUSPENDED) );
      sMappings.put( new Integer(AeProcessFilter.STATE_SUSPENDED_FAULTING), new Integer(IAeBusinessProcess.PROCESS_SUSPENDED) );
      sMappings.put( new Integer(AeProcessFilter.STATE_SUSPENDED_PROGRAMMATIC), new Integer(IAeBusinessProcess.PROCESS_SUSPENDED) );
      sMappings.put( new Integer(AeProcessFilter.STATE_SUSPENDED_MANUAL), new Integer(IAeBusinessProcess.PROCESS_SUSPENDED) );
      sMappings.put( new Integer(AeProcessFilter.STATE_SUSPENDED_INVOKE_RECOVERY), new Integer(IAeBusinessProcess.PROCESS_SUSPENDED) );
      sMappings.put( new Integer(AeProcessFilter.STATE_COMPENSATABLE), new Integer(IAeBusinessProcess.PROCESS_COMPENSATABLE) );
   }
   
   /**
    * Constructor.
    * @param aFilter The process filter instance.
    */
   public AeProcessFilterAdapter( AeProcessFilter aFilter )
   {
      mFilter = aFilter;
   }

   /**
    * Returns true if the process is selectable based on the
    * filter criteria.
    * @param aProcess
    */
   public boolean accept( IAeBusinessProcess aProcess )
   {
      boolean match = true;
      if( getFilter() != null )
      {
         if( isStartRowOrAbove() )
         {
            match = isMatch( aProcess );
         }
         else
         {
            match = false;
         }
         incrementCurrentRow();
      }
      return match;
   }

   /**
    * Increments the current row count.
    */
   protected void incrementCurrentRow()
   {
      mCurrentRow++;
   }

   /**
    * Returns true if the current row should be examined.
    */
   protected boolean isStartRowOrAbove()
   {
      return mCurrentRow >= getFilter().getListStart();
   }

   /**
    * Returns true if the process meets the filter selection
    * criteria.
    * @param aProcess
    */
   protected boolean isMatch( IAeBusinessProcess aProcess )
   {
      return isPIDMatch(aProcess) &&
             isStateMatch( aProcess ) &&
             isQNameMatch( aProcess ) &&
             isAfterCreationStartDate( aProcess ) &&
             isBeforeCreationEndDate( aProcess) &&
             isAfterCompletionStartDate( aProcess ) &&
             isBeforeCompletionEndDate( aProcess );
   }
   
   /**
    * Returns true if we're filtering on the PID and this process has a PID within
    * our range.
    * @param aProcess
    */
   protected boolean isPIDMatch(IAeBusinessProcess aProcess)
   {
      if (getFilter().getProcessIdRange() != null)
      {
         long pid = aProcess.getProcessId();
         return getFilter().getProcessIdRange()[0] <= pid && pid <= getFilter().getProcessIdRange()[1];
      }
      return true;
   }
   
   /**
    * Return the int representing the process state from the process filter
    * state.
    * @param aFilter
    */
   protected static int getRequestedState( AeProcessFilter aFilter )
   {
      Integer key = new Integer( aFilter.getProcessState() );
      return ((Integer)sMappings.get( key )).intValue();
   }

   /**
    * Compare the process state with the filter state settings.
    * @param aProcess
    * @return boolean True if the state settings are a match.
    */
   protected boolean isStateMatch( IAeBusinessProcess aProcess )
   {
      // issue here is that we are comparing state ints from
      // AeProcessFilter against state ints from IAeBusinessProcess
      // @todo - replace with enum
      if( getFilter().getProcessState() == AeProcessFilter.STATE_ANY )
      {
         return true;
      }

      if ( getFilter().getProcessState() == AeProcessFilter.STATE_COMPLETED_OR_FAULTED )
      {
         return (aProcess.getProcessState() == IAeBusinessProcess.PROCESS_COMPLETE) || 
                (aProcess.getProcessState() == IAeBusinessProcess.PROCESS_FAULTED);
      }
      
      return aProcess.getProcessState() == getRequestedState( getFilter() );
   }

   /**
    * Compare the process state QName with the filter settings.
    * @param aProcess
    * @return boolean True if the QName is a match.
    */
   protected boolean isQNameMatch( IAeBusinessProcess aProcess )
   {
      if( getFilter().getProcessName() != null )
      {
         return isNamespaceMatch( aProcess ) && isLocalNameMatch( aProcess );
      }
      else
      {
         return true;
      }
   }

   /**
    * Returns true if filter has no ns criteria otherwise returns
    * true if the ns strings match.
    * @param aProcess
    */
   protected boolean isNamespaceMatch( IAeBusinessProcess aProcess )
   {
      if( !AeUtil.isNullOrEmpty( getFilter().getProcessName().getNamespaceURI() ) )
      {
         return AeUtil.compareObjects(
            getFilter().getProcessName().getNamespaceURI(),
            aProcess.getName().getNamespaceURI() );
      }
      else
      {
         return true;
      }
   }

   /**
    * Returns true if the local part for the filter and the process
    * are a match.
    * @param aProcess
    */
   protected boolean isLocalNameMatch( IAeBusinessProcess aProcess )
   {
      return getFilter().getProcessName().getLocalPart().equals(
                                          aProcess.getName().getLocalPart() );
   }

   /**
    * Returns true if the filter has no creation start date criteria,
    * otherwise, returns true if the process start date is after the filter
    * creation start date.
    * @param aProcess
    */
   protected boolean isAfterCreationStartDate( IAeBusinessProcess aProcess )
   {
      Date processDate = aProcess.getStartDate();
      Date filterDate = getFilter().getProcessCreateStart();

      return (filterDate == null) || !processDate.before(filterDate);
   }

   /**
    * Returns true if the filter has no creation end date criteria, otherwise,
    * returns true if the process start date is before the filter creation end
    * date.
    * @param aProcess
    */
   protected boolean isBeforeCreationEndDate( IAeBusinessProcess aProcess )
   {
      Date processDate = aProcess.getStartDate();
      Date filterDate = getFilter().getProcessCreateEndNextDay();

      return (filterDate == null) || processDate.before(filterDate);
   }

   /**
    * Returns <code>true</code> if the filter has no completion date start criterion.
    * If criteria is set it returns <code>false</code> if the process has no end date. 
    * Otherwise, returns <code>true</code> if the process end date is after the
    * filter completion start date.
    *
    * @param aProcess
    */
   protected boolean isAfterCompletionStartDate(IAeBusinessProcess aProcess)
   {
      Date filterDate = getFilter().getProcessCompleteStart();
      boolean accept = filterDate == null;
      if(! accept)
      {
         Date processDate = aProcess.getEndDate();
         accept = processDate != null && !processDate.before(filterDate);
      }
      return accept;
   }

   /**
    * Returns <code>true</code> if the filter has no completion end date criterion.
    * If criteria is set it returns <code>false</code> if the process has no end date. 
    * Otherwise, returns <code>true</code> if the process end date is before
    * the filter completion end date.
    *
    * @param aProcess
    */
   protected boolean isBeforeCompletionEndDate(IAeBusinessProcess aProcess)
   {
      Date filterDate = getFilter().getProcessCompleteEndNextDay();
      boolean accept = filterDate == null;
      if(! accept)
      {
         Date processDate = aProcess.getEndDate();
         accept = (processDate != null) && processDate.before(filterDate);
      }
      return accept;
   }

   /**
    * Getter for the process filter.
    */
   protected AeProcessFilter getFilter()
   {
      return mFilter;
   }
}
