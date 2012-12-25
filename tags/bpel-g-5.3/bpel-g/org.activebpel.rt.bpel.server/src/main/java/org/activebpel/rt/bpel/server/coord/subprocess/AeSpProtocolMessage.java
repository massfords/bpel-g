//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/AeSpProtocolMessage.java,v 1.3 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess;

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.server.coord.AeProtocolMessage;

/**
 * 
 */
public class AeSpProtocolMessage extends AeProtocolMessage implements IAeSpProtocolMessage
{
   /** journal id to mark as done when the message is processed by the recipient. */
   private long mJournalId = IAeProcessManager.NULL_JOURNAL_ID;
   
   /**
    * Target process id.
    */
   private long mProcessId;
   
   /** Source process id. */
   private long mSourceProcessId;
   
   /**
    * Message target location path.
    */
   private String mLocationPath;

   /**
    * Constructs given signal, coordination id and target pid and location path.
    * @param aSignal
    * @param aCoordinationId
    * @param aPid
    * @param aLocationPath
    * @param aJournalId
    * @param aSourceProcessId
    */
   public AeSpProtocolMessage(String aSignal, String aCoordinationId, long aPid, String aLocationPath, long aJournalId, long aSourceProcessId)
   {      
      this(aSignal, aCoordinationId, null, aPid, aLocationPath, aJournalId, aSourceProcessId);
   }
   
   /**
    * Construts given signal, coordination id, fault, and target pid and location path. 
    * @param aSignal
    * @param aCoordinationId
    * @param aFault
    * @param aPid
    * @param aLocationPath
    * @param aJournalId
    * @param aSourceProcessId
    */
   public AeSpProtocolMessage(String aSignal, String aCoordinationId, IAeFault aFault, 
            long aPid, String aLocationPath, long aJournalId, long aSourceProcessId)
   {
      super(aSignal, aCoordinationId, aFault);
      setProcessId(aPid);
      setLocationPath(aLocationPath);
      setJournalId(aJournalId);
      setSourceProcessId(aSourceProcessId);
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.coord.subprocess.IAeSpProtocolMessage#getProcessId()
    */
   public long getProcessId()
   {
      return mProcessId;
   }

   /**
    * Sets the process id.
    * @param aProcessId
    */
   protected void setProcessId(long aProcessId)
   {
      mProcessId = aProcessId;
   }
   
   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.server.coord.subprocess.IAeSpProtocolMessage#getLocationPath()
    */
   public String getLocationPath()
   {
      return mLocationPath;
   }

   /**
    * Sets the location path.
    * @param aLocationPath
    */
   protected void setLocationPath(String aLocationPath)
   {
      mLocationPath = aLocationPath;
   }

   /**
    * @see org.activebpel.rt.bpel.server.coord.subprocess.IAeSpProtocolMessage#getJournalId()
    */
   public long getJournalId()
   {
      return mJournalId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.coord.subprocess.IAeSpProtocolMessage#setJournalId(long)
    */
   public void setJournalId(long aJournalId)
   {
      mJournalId = aJournalId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.coord.subprocess.IAeSpProtocolMessage#getSourceProcessId()
    */
   public long getSourceProcessId()
   {
      return mSourceProcessId;
   }
   
   /**
    * Setter for the source process id
    * @param aSourceProcessId
    */
   protected void setSourceProcessId(long aSourceProcessId)
   {
      mSourceProcessId = aSourceProcessId;
   }
}
