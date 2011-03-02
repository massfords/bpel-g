// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/IAeCopyStrategyFactory.java,v 1.1 2006/12/14 15:10:37 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity.assign;

import org.activebpel.rt.bpel.impl.AeBpelException;

/**
 * An interface for copy operation strategy factories.
 */
public interface IAeCopyStrategyFactory
{
   /**
    * Called to create a strategy.
    * 
    * @param aCopyOperation
    * @param aFromData
    * @param aToData
    * @throws AeBpelException
    */
   public IAeCopyStrategy createStrategy(IAeCopyOperation aCopyOperation, Object aFromData, Object aToData)
         throws AeBpelException;
}
