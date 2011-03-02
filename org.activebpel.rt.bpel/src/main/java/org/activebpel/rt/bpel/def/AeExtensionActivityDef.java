// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def;

import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Models the 'extensionActivity' bpel construct introduced in WS-BPEL 2.0.
 */
public class AeExtensionActivityDef extends AeActivityDef implements IAeSingleActivityContainerDef
{
   /** The extensionActivity construct must have a single activity child. */
   private AeActivityDef mActivity;

   /**
    * Default c'tor.
    */
   public AeExtensionActivityDef()
   {
      super();
   }

   /**
    * @return Returns the activity.
    */
   public AeActivityDef getActivityDef()
   {
      return mActivity;
   }

   /**
    * @param aActivity The activity to set.
    */
   public void setActivityDef(AeActivityDef aActivity)
   {
      mActivity = aActivity;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.IAeActivityContainerDef#replaceActivityDef(org.activebpel.rt.bpel.def.AeActivityDef, org.activebpel.rt.bpel.def.AeActivityDef)
    */
   public void replaceActivityDef(AeActivityDef aOldActivityDef, AeActivityDef aNewActivityDef)
   {
      setActivityDef(aNewActivityDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
   
   /**
    * Override this method in order to ask the child activity def for
    * its sources.  This change was made for the AeCheckStartActivityVisitor,
    * which determines whether activities are properly linked to start
    * activities.  In BPEL 2.0, the extensionActivity is just a wrapper
    * and so doesn't have the sources def.  The child of the extension
    * activity has the sources and targets.
    * 
    * @see org.activebpel.rt.bpel.def.AeActivityDef#getSourcesDef()
    */
   public AeSourcesDef getSourcesDef()
   {
      if (getActivityDef().hasSources())
         return getActivityDef().getSourcesDef();
      else
         return super.getSourcesDef();
   }
   
   /**
    * Override this method in order to ask the child activity def for
    * its targets.  This change was made for the AeCheckStartActivityVisitor,
    * which determines whether activities are properly linked to start
    * activities.  In BPEL 2.0, the extensionActivity is just a wrapper
    * and so doesn't have the targets def.  The child of the extension
    * activity has the sources and targets.
    * 
    * @see org.activebpel.rt.bpel.def.AeActivityDef#getTargetsDef()
    */
   public AeTargetsDef getTargetsDef()
   {
      if (getActivityDef().hasTargets())
         return getActivityDef().getTargetsDef();
      else
         return super.getTargetsDef();
   }
}
