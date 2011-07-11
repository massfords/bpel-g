// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/IAeCompensationCallback.java,v 1.8 2006/06/26 16:50:37 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;


/**
 * Interface for the invoker of a scope's compensation method. Provides a callback
 * method for the scope to invoke when the compensation is complete.
 */
public interface IAeCompensationCallback
{
   /**
    * Called by the scope when its compensation is complete.
    * @param aCompHandler
    */
   public void compensationComplete(AeCompensationHandler aCompHandler) throws AeBusinessProcessException;

   /**
    * Called by the scope when its compensation was interrupted by a fault.
    * @param aCompHandler
    * @param aFault
    */
   public void compensationCompleteWithFault(AeCompensationHandler aCompHandler, IAeFault aFault) throws AeBusinessProcessException;

   /**
    * Callback method that indicates the compensationHandler has been terminated.
    * @param aCompHandler
    */
   public void compensationTerminated(AeCompensationHandler aCompHandler) throws AeBusinessProcessException;

   /**
    * Returns the location path for the callback object. This will be used to
    * identify the callback object in the event that the process is asked to
    * persist itself.
    */
   public String getLocationPath();

   /**
    * Returns true if this is a coordinated comp info. This is normally used
    * during saving and restoring state information.
    * @return true if this is a coordinated comp info.
    */
   public boolean isCoordinated();

   /**
    * Returns the coordinationId if this is a coordinated comp info.
    * This information is normally used during state save/restore procedures.
    * @return the coordinationId if this is a coordinated comp info.
    */
   public String getCoordinationId();

}
