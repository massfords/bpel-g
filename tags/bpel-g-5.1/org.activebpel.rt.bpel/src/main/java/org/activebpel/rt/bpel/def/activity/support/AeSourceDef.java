// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/AeSourceDef.java,v 1.6 2006/06/26 16:50:32 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity.support;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition base for all named definition elements
 */
public class AeSourceDef extends AeBaseDef
{
   /** The 'linkName' attribute. */
   private String mLinkName;
   /** The 'transitionCondition' child construct. */
   private AeTransitionConditionDef mTransitionConditionDef;

   /**
    * Default constructor
    */
   public AeSourceDef()
   {
      super();
   }

   /**
    * Accessor method to obtain link name of this object.
    * 
    * @return name of link
    */
   public String getLinkName()
   {
      return mLinkName;
   }

   /**
    * Mutator method to set the link name of this object.
    * 
    * @param aLinkName name of the link
    */
   public void setLinkName(String aLinkName)
   {
      mLinkName = aLinkName;
   }
   
   /**
    * Accessor method to obtain transitionCondition property of this object.
    * 
    * @return transitionCondition expression
    */
   public String getTransitionCondition()
   {
      if (getTransitionConditionDef() != null)
      {
         return getTransitionConditionDef().getExpression();
      }
      else
      {
         return null;
      }
   }

   /**
    * @return Returns the transitionConditionDef.
    */
   public AeTransitionConditionDef getTransitionConditionDef()
   {
      return mTransitionConditionDef;
   }

   /**
    * @param aTransitionConditionDef The transitionConditionDef to set.
    */
   public void setTransitionConditionDef(AeTransitionConditionDef aTransitionConditionDef)
   {
      mTransitionConditionDef = aTransitionConditionDef;
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
