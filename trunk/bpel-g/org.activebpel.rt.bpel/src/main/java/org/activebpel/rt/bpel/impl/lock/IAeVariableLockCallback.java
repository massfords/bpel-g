// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/lock/IAeVariableLockCallback.java,v 1.2 2004/07/08 13:10:00 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.lock;

import org.activebpel.rt.bpel.AeBusinessProcessException;

/**
 * Callback interface for reporting that an object's request for locking
 * of one or more variables has been successful.
 */
public interface IAeVariableLockCallback
{
   /**
    * This method gets called by the AeVariableLocker once all of the variables
    * have been locked on behalf of the requesting object.
    */
   public void variableLocksAcquired(String aOwnerPath) throws AeBusinessProcessException;
}
