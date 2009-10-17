// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeProcessCompensationCallbackWrapper.java,v 1.4 2006/11/09 16:28:58 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.bpel.impl.AeProcessInfoEvent;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;

/**
 * wrapper used to ensure that the process transitions from running to completed
 * when the process level compensation is complete.
 */
public class AeProcessCompensationCallbackWrapper implements
      IAeCompensationCallback
{
   /** our delegate */
   private IAeCompensationCallback mDelegate;
   
   /**
    * ctor accepts our delegate callback reference
    * @param aCallback
    */
   public AeProcessCompensationCallbackWrapper(IAeCompensationCallback aCallback)
   {
      setDelegate(aCallback);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback#compensationComplete(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler)
    */
   public void compensationComplete(AeCompensationHandler aCompHandler) throws AeBusinessProcessException
   {
      getDelegate().compensationComplete(aCompHandler); 
      setProcessState(aCompHandler, IAeBusinessProcess.PROCESS_COMPLETE, IAeProcessInfoEvent.INFO_PROCESS_COMPENSATION_FINISHED);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback#compensationCompleteWithFault(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler, org.activebpel.rt.bpel.IAeFault)
    */
   public void compensationCompleteWithFault(AeCompensationHandler aCompHandler, IAeFault aFault) throws AeBusinessProcessException
   {
      getDelegate().compensationCompleteWithFault(aCompHandler, aFault);
      setProcessState(aCompHandler, IAeBusinessProcess.PROCESS_FAULTED, IAeProcessInfoEvent.INFO_PROCESS_COMPENSATION_FAULTED);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback#compensationTerminated(org.activebpel.rt.bpel.impl.activity.support.AeCompensationHandler)
    */
   public void compensationTerminated(AeCompensationHandler aCompHandler) throws AeBusinessProcessException
   {
      getDelegate().compensationTerminated(aCompHandler);
      // As per CK/Defect1558 - change state to Faulted instead of Completed.
      setProcessState(aCompHandler, IAeBusinessProcess.PROCESS_FAULTED, IAeProcessInfoEvent.INFO_PROCESS_COMPENSATION_TERMINATED);
   }
   
   /**
    * Fires an event to report the state change for the process level compensation handler and then
    * sets the process state to complete once the compensation is done.
    * @param aCompHandler
    * @param aProcessState
    * @param aProcessInfoState
    */
   protected void setProcessState(AeCompensationHandler aCompHandler, int aProcessState, int aProcessInfoState)
   {
      IAeBusinessProcessInternal process = aCompHandler.getProcess();
      AeProcessInfoEvent event = new AeProcessInfoEvent(process.getProcessId(), process.getLocationPath(), aProcessInfoState);
      process.getEngine().fireInfoEvent(event);
      process.setProcessState(aProcessState);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback#getLocationPath()
    */
   public String getLocationPath()
   {
      return getDelegate().getLocationPath();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback#isCoordinated()
    */
   public boolean isCoordinated()
   {
      return getDelegate().isCoordinated();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.IAeCompensationCallback#getCoordinationId()
    */
   public String getCoordinationId()
   {
      return getDelegate().getCoordinationId();
   }

   /**
    * @return Returns the delegate.
    */
   protected IAeCompensationCallback getDelegate()
   {
      return mDelegate;
   }

   /**
    * @param aDelegate The delegate to set.
    */
   protected void setDelegate(IAeCompensationCallback aDelegate)
   {
      mDelegate = aDelegate;
   }
}
 