// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Container for <code>correlationSets</code>. 
 */
public class AeCorrelationSetsDef extends AeBaseContainer
{
   /**
    * Default c'tor.
    */
   public AeCorrelationSetsDef()
   {
      super();
   }

   /**
    * Adds a correlation set to the collection.
    * @param aCorrelationSetDef
    */
   public void addCorrelationSetDef(AeCorrelationSetDef aCorrelationSetDef)
   {
      add(aCorrelationSetDef.getName(), aCorrelationSetDef);
   }

   /**
    * Gets the correlation set by name
    * @param aName
    */
   public AeCorrelationSetDef getCorrelationSetDef(String aName)
   {
      return (AeCorrelationSetDef) super.get(aName);
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);      
   }
}
