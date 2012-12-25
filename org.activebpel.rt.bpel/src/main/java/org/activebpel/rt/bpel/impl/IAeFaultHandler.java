// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.IAeFault;

/**
 * An interface for fault handlers.
 */
public interface IAeFaultHandler extends IAeActivityParent, IAeFCTHandler
{
   /**
    * Called to set the fault on the fault handler.
    * 
    * @param aHandledFault The handledFault to set.
    */
   public void setHandledFault(IAeFault aHandledFault);

   /**
    * Gets the fault that this handler is handling.
    */
   public IAeFault getHandledFault();
}
