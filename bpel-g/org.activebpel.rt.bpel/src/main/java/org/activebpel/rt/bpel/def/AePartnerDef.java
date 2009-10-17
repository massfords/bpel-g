// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AePartnerDef.java,v 1.8 2006/06/26 16:50:26 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;

/**
 * Definition for bpel partner.
 */
public class AePartnerDef extends AeNamedDef implements IAePartnerLinkParentDef
{
   /** List of strings which are the names of the associated partner links. */
   private List mPartnerLinks = new ArrayList();

   /**
    * Default constructor
    */
   public AePartnerDef()
   {
      super();
   }

   /**
    * Returns an iterator for the list of partnerLink names (String) associated
    * with this partner.
    */
   public Iterator getPartnerLinks()
   {
      return mPartnerLinks.iterator();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.IAePartnerLinkParentDef#addPartnerLinkDef(org.activebpel.rt.bpel.def.AePartnerLinkDef)
    */
   public void addPartnerLinkDef(AePartnerLinkDef aPartnerLink)
   {
      addPartnerLink(aPartnerLink.getName());
   }

   /**
    * Adds a partnerLink to the list associated with this partner.
    * @param aPartnerLink
    */
   public void addPartnerLink(String aPartnerLink)
   {
      mPartnerLinks.add(aPartnerLink);
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
