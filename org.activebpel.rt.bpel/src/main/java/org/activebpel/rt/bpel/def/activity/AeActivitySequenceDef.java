// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivitySequenceDef.java,v 1.7 2007/09/12 02:48:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.IAeMultipleActivityContainerDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for bpel sequence activity.
 */
public class AeActivitySequenceDef extends AeActivityDef implements IAeMultipleActivityContainerDef
{
   // standard elements of the activity definition
   private List mActivities = new ArrayList();

   /**
    * Default constructor
    */
   public AeActivitySequenceDef()
   {
      super();
   }

   /**
    * Adds an activity definition to the list of activities to execute.
    * 
    * @param aActivity activity to be added
    */
   public void addActivityDef(AeActivityDef aActivity)
   {
      mActivities.add(aActivity);
   }

   /**
    * @see org.activebpel.rt.bpel.def.IAeActivityContainerDef#replaceActivityDef(org.activebpel.rt.bpel.def.AeActivityDef, org.activebpel.rt.bpel.def.AeActivityDef)
    */
   public void replaceActivityDef(AeActivityDef aOldActivityDef, AeActivityDef aNewActivityDef)
   {
      AeDefUtil.replaceActivityDef(mActivities, aOldActivityDef, aNewActivityDef);
   }
   
   /**
    * Returns an iterator of activity definitions to iterate over.
    * 
    * @return iterator over activity list
    */
   public Iterator getActivityDefs()
   {
      return mActivities.iterator();
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
