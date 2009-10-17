// $Header$
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
import org.activebpel.rt.bpel.def.IAeConditionParentDef;
import org.activebpel.rt.bpel.def.IAeSingleActivityContainerDef;
import org.activebpel.rt.bpel.def.activity.support.AeConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for bpel if/switch activity.
 */
public class AeActivityIfDef extends AeActivityDef implements IAeSingleActivityContainerDef, IAeConditionParentDef
{
   /** A container for the condition and activity children of the if activity. */
   private AeIfDef mIfDef;
   /** The list of 'elseif' constructs in this if. */
   private List mElseIfs = new ArrayList();
   /** The final else child. */
   private AeElseDef mElse;

   /**
    * Default constructor
    */
   public AeActivityIfDef()
   {
      super();
   }

   /**
    * @return Returns the activity.
    */
   public AeActivityDef getActivityDef()
   {
      if (getIfDef() == null)
         return null;
      return getIfDef().getActivityDef();
   }

   /**
    * @param aActivity The activity to set.
    */
   public void setActivityDef(AeActivityDef aActivity)
   {
      if (getIfDef() == null)
         setIfDef(new AeIfDef());
      getIfDef().setActivityDef(aActivity);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.IAeActivityContainerDef#replaceActivityDef(org.activebpel.rt.bpel.def.AeActivityDef, org.activebpel.rt.bpel.def.AeActivityDef)
    */
   public void replaceActivityDef(AeActivityDef aOldActivityDef, AeActivityDef aNewActivityDef)
   {
      setActivityDef(aNewActivityDef);
   }

   /**
    * @return Returns the condition.
    */
   public AeConditionDef getConditionDef()
   {
      if (getIfDef() == null)
         return null;
      return getIfDef().getConditionDef();
   }

   /**
    * @param aCondition The condition to set.
    */
   public void setConditionDef(AeConditionDef aCondition)
   {
      if (getIfDef() == null)
         setIfDef(new AeIfDef());
      getIfDef().setConditionDef(aCondition);
   }

   /**
    * @return Returns the else.
    */
   public AeElseDef getElseDef()
   {
      return mElse;
   }

   /**
    * @param aElse The else to set.
    */
   public void setElseDef(AeElseDef aElse)
   {
      mElse = aElse;
   }
   
   /**
    * Adds an elseif construct to the def.
    * 
    * @param aElseIf
    */
   public void addElseIfDef(AeElseIfDef aElseIf)
   {
      mElseIfs.add(aElseIf);
   }
   
   /**
    * Gets an iterator over all the AeElseIfDefs.
    */
   public Iterator getElseIfDefs()
   {
      return mElseIfs.iterator();
   }

   /**
    * @return Returns the ifDef.
    */
   public AeIfDef getIfDef()
   {
      return mIfDef;
   }

   /**
    * @param aIfDef The ifDef to set.
    */
   public void setIfDef(AeIfDef aIfDef)
   {
      mIfDef = aIfDef;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
