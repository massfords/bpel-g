// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/IAeCorrelationListener.java,v 1.2 2004/07/08 13:10:00 ewittmann Exp $
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

/**
 * Callback interface for reporting that the correlation set has been initialized.
 * This interface is used by receives and pick's onMessage in order to avoid 
 * those activities queuing themselves prior to their data being available. 
 */
public interface IAeCorrelationListener
{
   /**
    * callback method invoked by the correlation set once it's initialized. 
    */
   public void correlationSetInitialized(AeCorrelationSet aSet) throws AeBusinessProcessException;
}
