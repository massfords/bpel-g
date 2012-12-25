// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/IAeExtensionActivityDef.java,v 1.1 2006/09/25 01:34:39 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity;

/**
 * Marker interface for identifying AE provided extension activities.
 */
public interface IAeExtensionActivityDef
{

   /**
    * Returns true if the engine understands this extension activity.
    */
   public boolean isUnderstood();

   /**
    * Returns the namespace of this extension activity.
    */
   public String getNamespace();
}
