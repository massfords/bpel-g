// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAePlanManager.java,v 1.3 2004/10/08 14:01:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.IAeProcessPlan;

/**
 * Provides a mechanism to map/cache a process QName to its description which
 * contains the required information to dispatch an inbound request to a
 * specific process instance or create a new process. 
 */
public interface IAePlanManager
{
   /**
    * Looks up the process plan for a process by its QName. 
    * @param aProcessName
    * @throws AeBusinessProcessException if the plan is not found
    */
   public IAeProcessPlan findCurrentPlan(QName aProcessName) throws AeBusinessProcessException;
}
