// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivityAssignDef.java,v 1.5 2006/06/26 16:50:32 mford Exp $
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
import org.activebpel.rt.bpel.def.activity.support.AeAssignCopyDef;
import org.activebpel.rt.bpel.def.activity.support.AeExtensibleAssignDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for bpel assign activity.
 */
public class AeActivityAssignDef extends AeActivityDef
{
   /** The assign's copy def children. */
   private List mCopies = new ArrayList();
   /** The assign's extensibleAssign children. */
   private List mExtensibleAssigns = new ArrayList();
   /** The assign's 'validate' attribute. */
   private boolean mValidate;

   /**
    * Default constructor
    */
   public AeActivityAssignDef()
   {
      super();
   }

   /**
    * Provides the ability to add a copy element to the assign element.
    *
    * @param aCopy copy element to be added
    */
   public void addCopyDef(AeAssignCopyDef aCopy)
   {
      mCopies.add(aCopy);
   }

   /**
    * Provide a list of the Copy objects for the user to iterate .
    *
    * @return iterator of AeAssignCopyDef objects
    */
   public Iterator getCopyDefs()
   {
      return mCopies.iterator();
   }

   /**
    * @return Returns the validate.
    */
   public boolean isValidate()
   {
      return mValidate;
   }

   /**
    * @param aValidate The validate to set.
    */
   public void setValidate(boolean aValidate)
   {
      mValidate = aValidate;
   }

   /**
    * @return Returns the extensibleAssigns.
    */
   public Iterator getExtensibleAssignDefs()
   {
      return mExtensibleAssigns.iterator();
   }

   /**
    * @param aDef The extensibleAssigns to set.
    */
   public void addExtensibleAssignDef(AeExtensibleAssignDef aDef)
   {
      mExtensibleAssigns.add(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
