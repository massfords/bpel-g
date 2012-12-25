//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/dispatchers/AeCoordinationCompleted.java,v 1.1 2008/03/28 01:46:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess.dispatchers; 

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.IAeCoordinator;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Signals that the coordination has completed, potentially with a fault.
 */
public class AeCoordinationCompleted extends AeDurableSpCoordinationDispatcher
{
   /** coordinator will be set if the coordination completed normally and is compensatable */
   private IAeCoordinator mCoordinator;
   /** fault will be set if the coordination completed with a fault */
   private IAeFault mFault;

   /**
    * Ctor
    * @param aProcessManager
    * @param aProcessId
    * @param aJournalId
    * @param aCallback
    * @param aCallbackJournalId
    * @param aLocationPath
    * @param aCoordinationId
    */
   public AeCoordinationCompleted(IAeProcessManager aProcessManager, long aProcessId,
         long aJournalId, IAeMessageAcknowledgeCallback aCallback,
         long aCallbackJournalId, String aLocationPath, String aCoordinationId)
   {
      super(aProcessManager, aProcessId, aJournalId, aCallback, aCallbackJournalId,
            aLocationPath, aCoordinationId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.coord.subprocess.AeDurableSpCoordinationDispatcher#dispatchBehavior(org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal)
    */
   protected void dispatchBehavior(IAeBusinessProcessInternal aProcess)
   {
      aProcess.coordinatedActivityCompleted(getLocationPath(), getCoordinationId(), getCoordinator(), getFault());
   }

   /**
    * @see org.activebpel.rt.bpel.server.coord.subprocess.AeDurableSpCoordinationDispatcher#journalDispatchBehavior()
    */
   protected long journalDispatchBehavior()
   {
      return getProcessManager().journalCoordinatedActivityCompleted(getProcessId(), getLocationPath(), getCoordinationId(), getFault());
   }

   /**
    * @return the fault
    */
   public IAeFault getFault()
   {
      return mFault;
   }

   /**
    * @param aFault the fault to set
    */
   public void setFault(IAeFault aFault)
   {
      mFault = aFault;
   }

   /**
    * @return the coordinator
    */
   public IAeCoordinator getCoordinator()
   {
      return mCoordinator;
   }

   /**
    * @param aCoordinator the coordinator to set
    */
   public void setCoordinator(IAeCoordinator aCoordinator)
   {
      mCoordinator = aCoordinator;
   }
} 