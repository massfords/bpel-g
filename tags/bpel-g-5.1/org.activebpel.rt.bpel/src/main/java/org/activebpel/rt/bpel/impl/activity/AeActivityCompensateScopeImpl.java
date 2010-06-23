// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl.activity;

import java.util.List;

import org.activebpel.rt.bpel.def.activity.AeActivityCompensateScopeDef;
import org.activebpel.rt.bpel.impl.IAeActivityParent;

/**
 * The compensateScope activity gets called from a fault handler or from within an
 * already executing compensation handler. The role of the compensationScope activity
 * is to identify the scope that is getting compensated and then to
 * queue the identified scope's compensation handlers.
 */
public class AeActivityCompensateScopeImpl extends AeActivityCompensateImpl
{
   /** 
    * Constructor for activity.
    * 
    * @param aActivityDef
    * @param aParent
    */
   public AeActivityCompensateScopeImpl(AeActivityCompensateScopeDef aActivityDef, IAeActivityParent aParent)
   {
      this(aActivityDef, aParent, false);
   }

   /**
    * Constructor for activity.
    * 
    * @param aActivityDef activity definition
    * @param aParent enclosing scope or fault handler
    */
   public AeActivityCompensateScopeImpl(AeActivityCompensateScopeDef aActivityDef, IAeActivityParent aParent,
         boolean aMatchCoordinated)
   {
      super(aActivityDef, aParent, aMatchCoordinated);
   }

   /**
    * Gets the name of the scope that we're targeting for compensation.
    */
   protected String getScopeNameForCompensation()
   {
      AeActivityCompensateScopeDef def = (AeActivityCompensateScopeDef) getDefinition();
      return def.getTarget();
   }

   /**
    * Overrides in order to return only scopes matching the 'target' scope name.
    * 
    * @see org.activebpel.rt.bpel.impl.activity.AeActivityCompensateImpl#getMatchingScopes()
    */
   protected List getMatchingScopes()
   {
      String scopeName = getScopeNameForCompensation();
      return getCompInfo().getEnclosedInfoByScopeName(scopeName);
   }
}
