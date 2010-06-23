//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/dispatchers/AeCancelSubprocessCompensation.java,v 1.1 2008/03/28 01:46:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.coord.subprocess.dispatchers; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeCompensatableActivity;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Sends the cancel subprocess compensation signal
 */
public class AeCancelSubprocessCompensation extends AeDurableSpCoordinationDispatcher
{
   /**
    * Ctor
    * @param aProcessManager
    * @param aProcessId
    * @param aJournalId
    * @param aCallback
    * @param aCallbackJournalId
    */
   public AeCancelSubprocessCompensation(IAeProcessManager aProcessManager,
         long aProcessId, long aJournalId,
         IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId)
   {
      super(aProcessManager, aProcessId, aJournalId, aCallback, aCallbackJournalId,
            null, null);
   }

   /**
    * @see org.activebpel.rt.bpel.server.coord.subprocess.AeDurableSpCoordinationDispatcher#dispatchBehavior(org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal)
    */
   protected void dispatchBehavior(IAeBusinessProcessInternal aProcess)
         throws AeBusinessProcessException
   {
      IAeCompensatableActivity compensatableActivity = (IAeCompensatableActivity) aProcess;
      compensatableActivity.terminateCompensationHandler();
   }

   /**
    * @see org.activebpel.rt.bpel.server.coord.subprocess.AeDurableSpCoordinationDispatcher#journalDispatchBehavior()
    */
   protected long journalDispatchBehavior()
   {
      return getProcessManager().journalCancelSubprocessCompensation(getProcessId());
   }
} 