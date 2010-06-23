// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AeActivityDef.java,v 1.11 2006/10/18 23:11:15 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.support.AeJoinConditionDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourcesDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetDef;
import org.activebpel.rt.bpel.def.activity.support.AeTargetsDef;

/**
 * Definition for bpel base activity definition.
 */
public abstract class AeActivityDef extends AeNamedDef
{
   /** The 'suppressFailure' attribute. */
   private Boolean mSuppressFailure;
   /** The 'sources' def. */
   private AeSourcesDef mSourcesDef;
   /** The 'targets' def. */
   private AeTargetsDef mTargetsDef;
   /** The list of link names (cached). */
   private List mTargetLinkNames;
   /** Set of resources that this activity uses. Used in conjunction with serializable scopes and variable/partner link locking. */
   private Set mResourcesUsed;

   /**
    * The isolated scope that encloses this activity, or <code>null</code> if
    * an isolated scope does not enclose this activity.
    */
   private AeActivityScopeDef mIsolatedScope;

   /**
    * Default constructor
    */
   public AeActivityDef()
   {
      super();
   }

   /**
    * Gets the join condition as a string.
    */
   public String getJoinCondition()
   {
      String joinCondition = null;
      
      AeJoinConditionDef joinConditionDef = getJoinConditionDef();
      if (joinConditionDef != null)
      {
         joinCondition = joinConditionDef.getExpression();
      }

      return joinCondition;
   }

   /**
    * Gets the join condition for this activity.
    */
   public AeJoinConditionDef getJoinConditionDef()
   {
      AeJoinConditionDef joinConditionDef = null;

      if (getTargetsDef() != null)
      {
         joinConditionDef = getTargetsDef().getJoinConditionDef();
      }

      return joinConditionDef;
   }
   
   /**
    * Gets a list of the targets.
    */
   public List getTargetLinkNames()
   {
      if (mTargetLinkNames == null)
      {
         List list = new ArrayList();
         if (getTargetsDef() != null)
         {
            for (Iterator titer = getTargetsDef().getTargetDefs(); titer.hasNext(); )
            {
               AeTargetDef target = (AeTargetDef) titer.next();
               list.add(target.getLinkName());
            }
         }
         mTargetLinkNames = list;
      }
      return mTargetLinkNames;
   }

   /**
    * Accessor method to obtain suppressFailure property of this object.
    * 
    * @return suppressFailure flag (true="yes", false="no)
    */
   public Boolean getSuppressFailure()
   {
      return mSuppressFailure;
   }

   /**
    * Mutator method to set the suppressFailure property of this object.
    * 
    * @param aSuppressFailure (true="yes", false="no)
    */
   public void setSuppressFailure(Boolean aSuppressFailure)
   {
      mSuppressFailure = aSuppressFailure;
   }

   /**
    * Getter for the resources used by this activity that need to be locked.
    */
   public Set getResourcesUsed()
   {
      return mResourcesUsed;
   }

   /**
    * Setter for the resources used that need to be locked. 
    * @param aSet
    */
   public void setResourcesUsed(Set aSet)
   {
      mResourcesUsed = aSet;
   }

   /**
    * @return Returns the sourcesDef.
    */
   public AeSourcesDef getSourcesDef()
   {
      return mSourcesDef;
   }

   /**
    * @param aSourcesDef The sourcesDef to set.
    */
   public void setSourcesDef(AeSourcesDef aSourcesDef)
   {
      mSourcesDef = aSourcesDef;
   }

   /**
    * @return Returns the targetsDef.
    */
   public AeTargetsDef getTargetsDef()
   {
      return mTargetsDef;
   }

   /**
    * @param aTargetsDef The targetsDef to set.
    */
   public void setTargetsDef(AeTargetsDef aTargetsDef)
   {
      mTargetsDef = aTargetsDef;
   }

   /**
    * Returns true if the activity has at least one target.
    */
   public boolean hasTargets()
   {
      boolean rval = false;
      if (getTargetsDef() != null)
      {
         rval = getTargetsDef().getSize() > 0;
      }
      return rval;
   }
   
   /**
    * Returns true if the activity has at least one source.
    */
   public boolean hasSources()
   {
      boolean rval = false;
      if (getSourcesDef() != null)
      {
         rval = getSourcesDef().getSize() > 0;
      }
      return rval;
   }

   /**
    * Gets an iterator over the target defs in the activity.
    */
   public Iterator getTargetDefs()
   {
      Iterator iter = Collections.EMPTY_LIST.iterator();
      if (getTargetsDef() != null)
      {
         iter = getTargetsDef().getTargetDefs();
      }
      return iter;
   }

   /**
    * Gets an iterator over the source defs in the activity.
    */
   public Iterator getSourceDefs()
   {
      Iterator iter = Collections.EMPTY_LIST.iterator();
      if (getSourcesDef() != null)
      {
         iter = getSourcesDef().getSourceDefs();
      }
      return iter;
   }

   /**
    * Sets the isolated scope that encloses this activity.
    */
   public void setIsolatedScope(AeActivityScopeDef isolatedScope)
   {
      mIsolatedScope = isolatedScope;
   }

   /**
    * Returns the isolated scope that encloses this activity or
    * <code>null</code> if an isolated scope does not enclose this activity.
    */
   public AeActivityScopeDef getIsolatedScope()
   {
      return mIsolatedScope;
   }
}