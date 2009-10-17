// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/activity/AeActivityCompensateDef.java,v 1.8 2006/12/14 22:39:18 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeScopeDef;
import org.activebpel.rt.bpel.def.IAeFCTHandlerDef;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for bpel compensate activity.
 */
public class AeActivityCompensateDef extends AeActivityDef
{
   /**
    * Default constructor
    */
   public AeActivityCompensateDef()
   {
      super();
   }

   /**
    * Walks up the parent hierarchy until it comes across a catch/catchAll or compensationHandler
    * and then it returns that object's enclosing scope.
    */
   public AeScopeDef findRootScopeForCompensation()
   {
      boolean foundFCTDef = false;
      for ( AeBaseDef parent = getParent(); parent != null; parent = parent.getParent())
      {
         // skip over scopes til u find a catch/catchAll or compensation handler
         if (foundFCTDef && parent instanceof AeScopeDef )
         {
            return (AeScopeDef) parent;
         }
         // we found a catch/catchAll
         else if (parent instanceof IAeFCTHandlerDef)
         {
            foundFCTDef = true;
         }
      }

      // should never get here unless the process is invalid, at which point we
      // should have caught it during static analysis.
      return null ;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeActivityDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
