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

import java.util.Iterator;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Container for partnerLinks. Broken out as a separate class so we can visit it.
 */
public class AePartnerLinksDef extends AeBaseContainer implements IAePartnerLinkParentDef
{
   /**
    * Default c'tor.
    */
   public AePartnerLinksDef()
   {
      super();
   }

   /**
    * Adds a new partnerLinkDef to the collection.
    * @param aPartnerLink
    */
   public void addPartnerLinkDef(AePartnerLinkDef aPartnerLink)
   {
      add(aPartnerLink.getName(), aPartnerLink);
   }
   
   /**
    * Gets a single partner link by its name.
    * 
    * @param aPartnerLinkName
    */
   public AePartnerLinkDef getPartnerLinkDef(String aPartnerLinkName)
   {
      return (AePartnerLinkDef) get(aPartnerLinkName);
   }
   
   /**
    * Gets an iterator over the list of all partner link defs.
    */
   public Iterator getPartnerLinkDefs()
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
