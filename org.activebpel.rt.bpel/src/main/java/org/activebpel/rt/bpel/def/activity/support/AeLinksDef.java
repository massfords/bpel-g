// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.activity.support;

import java.util.Iterator;

import org.activebpel.rt.bpel.def.AeBaseContainer;
import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Container for the links within a Flow. Using this container as opposed to 
 * a collection so it can be visited.
 */
public class AeLinksDef extends AeBaseContainer
{
   /**
    * Default c'tor.
    */
   public AeLinksDef()
   {
      super();
   }
   
   /**
    * Adds a link to the collection.
    * @param aDef
    */
   public void addLinkDef(AeLinkDef aDef)
   {
      super.add(aDef);
   }

   /**
    * Gets the iterator for the collection of links.
    */
   public Iterator getLinkDefs()
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
