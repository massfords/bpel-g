// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeProcessInstanceDetail.java,v 1.10 2007/02/16 20:01:19 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.io.Serializable;
import java.util.Date;

import javax.xml.namespace.QName;

/**
 * JavaBean for holding some data for a single process instance
 */
public class AeProcessInstanceDetail implements Serializable
{
   /** name of the process */
   private QName mName;
   /** id of the process */
   private long mProcessId;
   /** date/time the process was started */
   private Date mStarted;
   /** date/time the process was ended */
   private Date mEnded;
   /** current state of the process */
   private int mState;
   /** reason for state of the process */
   private int mStateReason;
   
   /**
    * No-arg constructor
    */
   public AeProcessInstanceDetail()
   {
   }
   
   /**
    * Getter for the process name
    */
   public QName getName()
   {
      return mName;
   }

   /**
    * Getter for the process id
    */
   public long getProcessId()
   {
      return mProcessId;
   }

   /**
    * Getter for the start date
    */
   public Date getStarted()
   {
      return mStarted;
   }
   
   /**
    * Setter for the name
    * @param aName
    */
   public void setName(QName aName)
   {
      mName = aName;
   }

   /**
    * Setter for the process id
    * @param aId
    */
   public void setProcessId(long aId)
   {
      mProcessId = aId;
   }

   /**
    * Setter for the date the process started
    * @param aDate
    */
   public void setStarted(Date aDate)
   {
      mStarted = aDate;
   }
   
   /**
    * Gets the state of the process
    */
   public int getState()
   {
      return mState;
   }

   /**
    * Setter for the state of the process
    * @param aProcessState
    */
   public void setState(int aProcessState)
   {
      mState = aProcessState;
   }
   
   /**
    * Gets the state reason code of the process
    */
   public int getStateReason()
   {
      return mStateReason;
   }

   /**
    * Setter for the state reason code of the process
    * @param aReasonCode
    */
   public void setStateReason(int aReasonCode)
   {
      mStateReason = aReasonCode;
   }
   
   /**
    * Setter for the end date.
    */
   public void setEnded(Date ended)
   {
      mEnded = ended;
   }

   /**
    * Getter for the end date.
    */
   public Date getEnded()
   {
      return mEnded;
   }
}
