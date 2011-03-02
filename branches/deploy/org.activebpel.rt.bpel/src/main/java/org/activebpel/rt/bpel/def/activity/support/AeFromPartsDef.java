// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.activity.support;

import java.util.Iterator;

import org.activebpel.rt.bpel.def.AeBaseContainer;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Models the BPEL 2.0 'fromParts' construct.
 */
public class AeFromPartsDef extends AeBaseContainer
{
   /**
    * Default c'tor.
    */
   public AeFromPartsDef()
   {
      super();
   }

   /**
    * Adds a fromPart def to the container.
    * 
    * @param aDef
    */
   public void addFromPartDef(AeFromPartDef aDef)
   {
      add(aDef);
   }
   
   /**
    * Gets an iterator over all of the fromPart defs.
    */
   public Iterator getFromPartDefs()
   {
      return getValues();
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
