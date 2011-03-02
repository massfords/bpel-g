// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeEngineEvent.java,v 1.6 2006/10/20 14:41:25 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Date;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeEngineEvent;

/**
 * Implementation of bpel engine event.
 */
public class AeEngineEvent extends AeEvent implements IAeEngineEvent
{
   /** The process ID of the event being triggered. */
   private long mProcessID; 
   /** The ID of the event being triggered as defined in the interface. */
   private int mEventID;   
   /** The namespace qualified name of the process this event represents. */
   private QName mProcessName;

   /**
    * Constructor for BPEL engine event.
    * @param aProcessID The process ID of the event.
    * @param aEventID The event id of the event.
    * @param aProcessName The name of the process
    */
   public AeEngineEvent(long aProcessID, int aEventID, QName aProcessName)
   {
      super();
      
      mProcessID   = aProcessID;
      mEventID     = aEventID;
      mProcessName = aProcessName;
   }
   
   /**
    * Constructor for BPEL engine event.
    * 
    * @param aProcessID The process ID of the event.
    * @param aEventID The event id of the event.
    * @param aProcessName The name of the process
    * @param aTimestamp The event timestamp
    */
   public AeEngineEvent(long aProcessID, int aEventID, QName aProcessName, Date aTimestamp)
   {
      super(aTimestamp);
      
      mProcessID   = aProcessID;
      mEventID     = aEventID;
      mProcessName = aProcessName;
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeEngineEvent#getEventID()
    */
   public int getEventID()
   {
      return mEventID;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeEngineEvent#getPID()
    */
   public long getPID()
   {
      return mProcessID;
   }

   /**
    * @see org.activebpel.rt.bpel.IAeEngineEvent#getProcessName()
    */
   public QName getProcessName()
   {
      return mProcessName;
   }
}
