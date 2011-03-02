// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeBaseProcessEvent.java,v 1.2 2006/11/13 22:42:18 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl;

import java.util.Date;

import org.activebpel.rt.bpel.IAeBaseProcessEvent;
import org.activebpel.rt.util.AeUtil;

/**
 * A base class impl for process events.
 */
public abstract class AeBaseProcessEvent extends AeEvent implements IAeBaseProcessEvent
{
   /** The process ID of the event being triggered. */
   private long mProcessID;
   /** The path of the bpel object triggering the event (xpath) */
   private String mNodePath;
   /** The ID of the event being triggered as defined in the interface. */
   private int mEventID;
   /** The Fault associated with this event, if any. */
   private String mFaultName;
   /** Extra info registered wih the event by the triggerer. */
   private String mAncillaryInfo;

   /**
    * Constructs the process event.
    * 
    * @param aPID
    * @param aPath
    * @param aEventID
    * @param aFault
    * @param aInfo
    */
   public AeBaseProcessEvent(long aPID, String aPath, int aEventID, String aFault, String aInfo)
   {
      super();
      
      setProcessID(aPID);
      setNodePath(aPath);
      setEventID(aEventID);
      setFaultName(aFault);
      setAncillaryInfo(AeUtil.getSafeString(aInfo));
   }
   
   /**
    * Constructs the process event (with the given timestamp).
    * 
    * @param aPID
    * @param aPath
    * @param aEventID
    * @param aFault
    * @param aInfo
    * @param aTimestamp
    */
   public AeBaseProcessEvent(long aPID, String aPath, int aEventID, String aFault, String aInfo, Date aTimestamp)
   {
      super(aTimestamp);
      
      setProcessID(aPID);
      setNodePath(aPath);
      setEventID(aEventID);
      setFaultName(aFault);
      setAncillaryInfo(AeUtil.getSafeString(aInfo));
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBaseProcessEvent#getNodePath()
    */
   public String getNodePath()
   {
      return mNodePath;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBaseProcessEvent#getEventID()
    */
   public int getEventID()
   {
      return mEventID;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBaseProcessEvent#getFaultName()
    */
   public String getFaultName()
   {
      return mFaultName;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBaseProcessEvent#getAncillaryInfo()
    */
   public String getAncillaryInfo()
   {
      return mAncillaryInfo;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeBaseProcessEvent#getPID()
    */
   public long getPID()
   {
      return mProcessID;
   }

   /**
    * @param aProcessID The processID to set.
    */
   protected void setProcessID(long aProcessID)
   {
      mProcessID = aProcessID;
   }

   /**
    * @param aAncillaryInfo The ancillaryInfo to set.
    */
   protected void setAncillaryInfo(String aAncillaryInfo)
   {
      mAncillaryInfo = aAncillaryInfo;
   }

   /**
    * @param aEventID The eventID to set.
    */
   protected void setEventID(int aEventID)
   {
      mEventID = aEventID;
   }

   /**
    * @param aFaultName The faultName to set.
    */
   protected void setFaultName(String aFaultName)
   {
      mFaultName = aFaultName;
   }

   /**
    * @param aNodePath The nodePath to set.
    */
   protected void setNodePath(String aNodePath)
   {
      mNodePath = aNodePath;
   }
}
