// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/AeWorkEvent.java,v 1.5 2005/02/10 23:00:43 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work;

import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkItem;

import org.activebpel.rt.bpel.server.AeMessages;

/**
 * Event that gets fired to a WorkListener to report progress on a Work object. 
 */
public class AeWorkEvent implements WorkEvent
{
   /** type of event */
   private int mType;
   /** WorkItem that event relates to */
   private WorkItem mWorkItem;
   /** exception, if any, that was a result of the work's execution */
   private WorkException mException;
   
   /**
    * Creates a work event for the specified work object and type.  
    */
   public AeWorkEvent(WorkItem aWorkItem, int aType, WorkException aException)
   {
      mType = aType;
      mException = aException;

      if (aException != null && aType != WorkEvent.WORK_COMPLETED)
         throw new IllegalArgumentException(AeMessages.getString("AeWorkEvent.ERROR_0")); //$NON-NLS-1$
   }

   /**
    * @see commonj.work.WorkEvent#getType()
    */
   public int getType()
   {
      return mType;
   }

   /**
    * @see commonj.work.WorkEvent#getException()
    */
   public WorkException getException()
   {
      return mException;
   }

   /**
    * @see commonj.work.WorkEvent#getWorkItem()
    */
   public WorkItem getWorkItem()
   {
      return mWorkItem;
   }
}