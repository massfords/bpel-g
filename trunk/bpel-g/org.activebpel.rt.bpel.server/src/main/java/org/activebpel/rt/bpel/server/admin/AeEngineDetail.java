//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/AeEngineDetail.java,v 1.3 2007/08/15 21:02:07 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin;

import java.io.Serializable;
import java.util.Date;

/**
 * This class contains details about a currently running engine.
 */
public class AeEngineDetail implements Serializable
{
   /** The current engine state. */
   protected int mState;
   /** The current monitor status. */
   protected int mMonitorStatus;
   /** The error message if the state of the engine is "ERROR". */
   protected String mErrorMessage;
   /** The current engine's start time (null if stopped). */
   protected Date mStartTime;

   /**
    * Default constructor.
    */
   public AeEngineDetail()
   {
      mErrorMessage = ""; //$NON-NLS-1$
   }

   /**
    * Getter for the engine state property.
    */
   public int getState()
   {
      return mState;
   }
   
   /**
    * Setter for the engine state.
    * 
    * @param aEngineState The engine state.
    */
   public void setState(int aEngineState)
   {
      mState = aEngineState;
   }

   /**
    * Getter for the monitor status property.
    */
   public int getMonitorStatus()
   {
      return mMonitorStatus;
   }
   
   /**
    * Setter for the monitor status.
    * 
    * @param aMonitorStatus The monitor status
    */
   public void setMonitorStatus(int aMonitorStatus)
   {
      mMonitorStatus = aMonitorStatus;
   }

   /**
    * Getter for the engine start time property.
    */
   public Date getStartTime()
   {
      return mStartTime;
   }
   
   /**
    * Setter for the engine start time property.
    * 
    * @param aStartTime The engine start time.
    */
   public void setStartTime(Date aStartTime)
   {
      mStartTime = aStartTime;
   }

   /**
    * Getter for the engine error message property.
    */
   public String getErrorMessage()
   {
      return mErrorMessage;
   }

   /**
    * Setter for the engine error message property.
    */
   public void setErrorMessage(String aErrorMessage)
   {
      mErrorMessage = aErrorMessage;
   }
}