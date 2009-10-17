// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeActivityParent.java,v 1.2 2004/07/08 13:09:57 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.IAeActivity;

/**
 * Describes objects that can be a parent of an activity.
 */
public interface IAeActivityParent extends IAeBpelObject
{
   /**
    * Adds the activity to this parent. The method implies that
    * there may be more than 1 activity as a child of this parent.
    * This is a side effect of deciding to have a single interface
    * to handle cases with a single child and those with multiple
    * children.
    */
   public void addActivity(IAeActivity aActivity);
}
