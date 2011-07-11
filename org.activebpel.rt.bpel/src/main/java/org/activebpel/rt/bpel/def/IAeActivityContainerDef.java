// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/IAeActivityContainerDef.java,v 1.4 2007/09/12 02:48:11 mford Exp $
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
 * Interface for all containers of activities.  Note this is not meant to be
 * implemented directly, but you should use the derived interfaces
 * IAeMultipleActivityContainerDef or IAeSingleActivityContainerDef.
 */
public interface IAeActivityContainerDef
{
   /**
    * Replaces an old activity with a new one.
    * 
    * @param aOldActivityDef
    * @param aNewActivityDef
    */
   public void replaceActivityDef(AeActivityDef aOldActivityDef, AeActivityDef aNewActivityDef);

}
