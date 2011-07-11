// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/IAeMultipleActivityContainerDef.java,v 1.3 2006/06/26 16:50:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.Iterator;

/**
 * Interface for all containers of mutliple activities.  
 */
public interface IAeMultipleActivityContainerDef extends IAeActivityContainerDef
{
   /**
    * Adds an activity definition to the list of activities to execute.
    * @param aActivity the link to be added.
    */
   public void addActivityDef(AeActivityDef aActivity);

   /**
    * Provide a list of the activity elements for the user to iterate .
    * @return iterator of AeActivityDef objects
    */
   public Iterator getActivityDefs();
}
