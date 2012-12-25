// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/child/IAeChildWorkCompletedListener.java,v 1.2 2007/06/20 19:40:06 kroe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work.child;

import commonj.work.WorkItem;

/**
 * Defines interface for listeners to be notified when a work item completes.
 */
public interface IAeChildWorkCompletedListener
{
   /**
    * Called when the <code>Work</code> associated with the given
    * <code>WorkItem</code> completes.
    *
    * @param aWorkItem
    */
   public void workCompleted(WorkItem aWorkItem);
}
