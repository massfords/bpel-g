//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeProcessCoordinationStub.java,v 1.6 2008/03/28 01:41:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.coord.AeCoordinationException;
import org.activebpel.rt.bpel.coord.IAeCoordinator;
import org.activebpel.wsio.IAeMessageAcknowledgeCallback;

/**
 *  Void implementation.
 */
public class AeProcessCoordinationStub implements IAeProcessCoordination
{
   /**
    *  Default ctor.
    */
   public AeProcessCoordinationStub()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#registerCoordinationId(long, java.lang.String, java.lang.String)
    */
   public void registerCoordinationId(long aProcessId, String aLocationPath, String aCoordinationId)
         throws AeCoordinationException
   {
      // no-op
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#deregisterCoordinationId(long, java.lang.String, java.lang.String, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void deregisterCoordinationId(long aProcessId, String aLocationPath, String aCoordinationId, IAeMessageAcknowledgeCallback aCallback, long aJournalId)
         throws AeCoordinationException
   {
      // no-op
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#activityFaulted(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void activityFaulted(long aProcessId, String aLocationPath, String aCoordinationId, IAeFault aFault, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId)
         throws AeCoordinationException
   {
      // no-op
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#installCompensationHandler(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.coord.IAeCoordinator, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void installCompensationHandler(long aProcessId, String aLocationPath, String aCoordinationId,
         IAeCoordinator aCoordinator, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
      // no-op
   }


   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#compensationCompletedCallback(long, java.lang.String, java.lang.String, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void compensationCompletedCallback(long aProcessId,
         String aLocationPath, String aCoordinationId, long aJournalId,
         IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId)
         throws AeCoordinationException
   {
      // no-op
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#compensationCompletedWithFaultCallback(long, java.lang.String, java.lang.String, org.activebpel.rt.bpel.IAeFault, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void compensationCompletedWithFaultCallback(long aProcessId,
         String aLocationPath, String aCoordinationId, IAeFault aFault,
         long aJournalId,
         IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId)
         throws AeCoordinationException
   {
      // no-op
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#compensateSubProcess(long, java.lang.String, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void compensateSubProcess(long aProcessId, String aCoordinationId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
      // no-op
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#cancelSubProcessCompensation(long, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void cancelSubProcessCompensation(long aProcessId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#cancelProcess(long, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void cancelProcess(long aProcessId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
      // no-op
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeProcessCoordination#subprocessCoordinationEnded(long, long, org.activebpel.wsio.IAeMessageAcknowledgeCallback, long)
    */
   public void subprocessCoordinationEnded(long aProcessId, long aJournalId, IAeMessageAcknowledgeCallback aCallback, long aCallbackJournalId) throws AeCoordinationException
   {
      // no-op
   }
}
