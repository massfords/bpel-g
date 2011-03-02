//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/coord/subprocess/dispatchers/AeCompensateSubprocess.java,v 1.1 2008/03/28 01:46:19 mford Exp $
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
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.activity.support.AeParticipantCompensator;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 * Compensates the subprocess
 */
public class AeCompensateSubprocess extends AeDurableSpCoordinationDispatcher
{
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
   public AeCompensateSubprocess(IAeProcessManager aProcessManager,
         long aProcessId, long aJournalId,
         IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId,
         String aLocationPath, String aCoordinationId)
   {
      super(aProcessManager, aProcessId, aJournalId, aCallback, aCallbackJournalId,
            aLocationPath, aCoordinationId);
   }

   /**
    * @see org.activebpel.rt.bpel.server.coord.subprocess.AeDurableSpCoordinationDispatcher#dispatchBehavior(org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal)
    */
   protected void dispatchBehavior(IAeBusinessProcessInternal aProcess)
         throws AeBusinessProcessException
   {
      AeParticipantCompensator comp = new AeParticipantCompensator(getCoordinationId(), getProcessManager().getEngine());
      aProcess.compensate(comp);
   }

   /**
    * @see org.activebpel.rt.bpel.server.coord.subprocess.AeDurableSpCoordinationDispatcher#journalDispatchBehavior()
    */
   protected long journalDispatchBehavior()
   {
      return getProcessManager().journalCompensateSubprocess(getProcessId(), getCoordinationId());
   }
} 