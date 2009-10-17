// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeProcessPruningBean.java,v 1.11 2006/05/26 16:08:47 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.text.MessageFormat;
import java.util.Date;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.util.AeDate;

/**
 * Bean for driving display of process pruning portion of storage page.
 */
public class AeProcessPruningBean extends AePruningBean
{
   /** Prune date property. */
   private Date mPruneDate;

   /**
    * Default constructor.
    */
   public AeProcessPruningBean()
   {
   }

   /**
    * Returns the prune date property.
    */
   public Date getPruneDate()
   {
      return mPruneDate;
   }

   /**
    * Returns a filter for processes that completed on or before the prune
    * date.
    */
   protected AeProcessFilter getPruneProcessFilter() throws AeException
   {
      AeProcessFilter filter = new AeProcessFilter();
      filter.setMaxReturn(0); // just want a count without results
      filter.setProcessState(AeProcessFilter.STATE_COMPLETED_OR_FAULTED);
      Date compensatedDate = compensateForFilterAdjustment( getPruneDate() );
      filter.setProcessCompleteEnd( compensatedDate );
      return filter;
   }
   
   /**
    * In the normal course of filter processing, the complete date is adjusted
    * to the start of the following day. Here the completed by date is adjusted 
    * to the start of the previous day to compensate.
    *
    * @param aDate
    */
   protected Date compensateForFilterAdjustment( Date aDate )
   {
      Date result = null;
      
      if (aDate != null)
      {
         result = AeDate.getStartOfDay(AeDate.getPreviousDay(aDate));
      }
      return result;
   }
   
   /**
    * Marshalls the params into the filter and then deletes the processes from storage.
    */
   protected void executeDelete()
   {
      if (getPruneDate() != null)
      {
         try
         {
            int n = getAdmin().removeProcesses(getPruneProcessFilter());

            String pattern = AeMessages.getString("AeProcessPruningBean.0"); //$NON-NLS-1$
            Object[] args = {new Integer(n)};
            setStatusDetail(MessageFormat.format(pattern, args));

            // If the request succeeds, then clear the prune date.
            setPruneDate(null);
         }
         catch (AeException e)
         {
            AeException.logError(e, AeMessages.getString("AeProcessPruningBean.ERROR_1")); //$NON-NLS-1$

            String message = e.getLocalizedMessage();
            setStatusDetail((message != null) ? message : AeMessages.getString("AeProcessPruningBean.2")); //$NON-NLS-1$
         }
      }
   }

   /**
    * Marshalls the params into the filter and then reports how many processes matched
    * the criteria. 
    */
   protected void executeQuery()
   {
      if (getPruneDate() != null)
      {
         setPrunePending(true);

         try
         {
            int numProcesses = getAdmin().getProcessCount(getPruneProcessFilter());

            String pattern = AeMessages.getString("AeProcessPruningBean.3"); //$NON-NLS-1$
            Object[] args = {new Integer(numProcesses)};
            setStatusDetail(MessageFormat.format(pattern, args));
            setPruneValid(numProcesses > 0);
         }
         catch (AeException e)
         {
            AeException.logError(e, AeMessages.getString("AeProcessPruningBean.ERROR_4")); //$NON-NLS-1$
            setStatusDetail(AeMessages.getString("AeProcessPruningBean.5")); //$NON-NLS-1$
         }
      }
   }

   /**
    * Sets the prune date.
    */
   public void setPruneDate(Date aPruneDate)
   {
      mPruneDate = aPruneDate;
   }
}
