// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeAlarmFilter.java,v 1.3 2005/07/12 00:17:04 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.io.Serializable;
import java.util.Date;

import javax.xml.namespace.QName;

/**
 * Wraps selection criteria for selecting message receivers
 * off of the queue.
 */
public class AeAlarmFilter extends AeListingFilter implements Serializable
{
   /** No selection criteria specified. */
   public static final AeAlarmFilter NULL_FILTER = new AeAlarmFilter();
   /** Default 'null' value for process id. */
   public static final long NULL_ID = -1;
   
   /** Process id. */
   private long mProcessId = NULL_ID;
   /** Specifies the process creation starting date range to be included in the results */
   private Date mAlarmFilterStart;
   /** Specifies the process creation ending date range to be included in the results */
   private Date mAlarmFilterEnd;
   /** The namespace qualified name of process we are looking for. */
   private QName mProcessName;
   
   /**
    * Constructor.
    */
   public AeAlarmFilter()
   {
      super();
   }
   
   /**
    * Accessor for process id.
    */
   public long getProcessId()
   {
      return mProcessId;
   }
   
   /**
    * Returns true if the process id criteria has not been set.
    */
   public boolean isNullProcessId()
   {
      return mProcessId == NULL_ID;
   }

   /**
    * Setter for the process id.
    * @param aId
    */
   public void setProcessId(long aId)
   {
      mProcessId = aId;
   }
   
   /**
    * Returns the alarm filter end date (or null if not set).
    */
   public Date getAlarmFilterEnd()
   {
      return mAlarmFilterEnd;
   }

   /**
    * Returns the alarm filter start date (or null if not set).
    */
   public Date getAlarmFilterStart()
   {
      return mAlarmFilterStart;
   }

   /**
    * Sets the alarm filter end date (or null if not set).
    */
   public void setAlarmFilterEnd(Date aDate)
   {
      mAlarmFilterEnd = aDate;
   }

   /**
    * Sets the alarm filter start date (or null if not set).
    */
   public void setAlarmFilterStart(Date aDate)
   {
      mAlarmFilterStart = aDate;
   }

   /**
    * Get the name of the process we are filtering on, null if none.
    */
   public QName getProcessName()
   {
      return mProcessName;
   }

   /**
    * Sets the name of the process we are filtering on, null if none.
    */
   public void setProcessName(QName aName)
   {
      mProcessName = aName;
   }

}
