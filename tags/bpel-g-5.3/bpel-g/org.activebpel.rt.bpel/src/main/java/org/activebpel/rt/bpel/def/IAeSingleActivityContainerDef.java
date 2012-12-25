// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/IAeSingleActivityContainerDef.java,v 1.3 2006/06/26 16:50:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

/**
 * Interface for all containers of single activities.
 */
public interface IAeSingleActivityContainerDef extends IAeActivityContainerDef
{
   /**
    * Obtains the current activity associated with this activity container.
    * @return an activity associated with this object
    * @see AeActivityDef
    */
   public AeActivityDef getActivityDef();

   /**
    * Set the activity to execute in this activity container.
    * @param aActivity activity to set
    * @see AeActivityDef
    */
   public void setActivityDef(AeActivityDef aActivity);

}
