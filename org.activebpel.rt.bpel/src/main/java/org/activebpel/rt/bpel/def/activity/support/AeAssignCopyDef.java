// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/support/AeAssignCopyDef.java,v 1.11 2006/11/16 23:34:12 mford Exp $
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
import org.activebpel.rt.bpel.def.IAeFromParentDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Holds individual copy operations of an assign activity
 */
public class AeAssignCopyDef extends AeBaseDef implements IAeFromParentDef
{
   /** The copy's 'from' construct. */
   private AeFromDef mFrom;
   /** The copy's 'to' construct. */
   private AeToDef mTo;
   /** The copy's 'keepSrcElementName' attribute. */
   private boolean mKeepSrcElementName;
   /** The copy's 'ignoreMissingFromData' attribute  */
   private boolean mIgnoreMissingFromData;
   
   /**
    * Default constructor
    */
   public AeAssignCopyDef()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public final void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }

   /**
    * Accessor method to obtain the From assignment part of the Copy activity.
    * 
    * @return the From assignment object
    */
   public final AeFromDef getFromDef()
   {
      return mFrom;
   }

   /**
    * Mutator method to set the From assignment part of the Copy activity.
    * 
    * @param aFrom the From part of the Copy activity
    */
   public final void setFromDef(AeFromDef aFrom)
   {
      mFrom = aFrom;
   }

   /**
    * Accessor method to obtain the To assignment part of the Copy activity.
    * 
    * @return the To assignment object
    */
   public final AeToDef getToDef()
   {
      return mTo;
   }

   /**
    * Mutator method to set the To assignment part of the Copy activity.
    * 
    * @param aTo the To part of the Copy activity
    */
   public final void setToDef(AeToDef aTo)
   {
      mTo = aTo;
   }

   /**
    * @return Returns the keepSrcElementName.
    */
   public final boolean isKeepSrcElementName()
   {
      return mKeepSrcElementName;
   }

   /**
    * @param aKeepSrcElementName The keepSrcElementName to set.
    */
   public final void setKeepSrcElementName(boolean aKeepSrcElementName)
   {
      mKeepSrcElementName = aKeepSrcElementName;
   }

   /**
    * @return the ignoreMissingFromData
    */
   public final boolean isIgnoreMissingFromData()
   {
      return mIgnoreMissingFromData;
   }

   /**
    * @param aIgnoreMissingFromData the ignoreMissingFromData to set
    */
   public void setIgnoreMissingFromData(boolean aIgnoreMissingFromData)
   {
      mIgnoreMissingFromData = aIgnoreMissingFromData;
   }
}
