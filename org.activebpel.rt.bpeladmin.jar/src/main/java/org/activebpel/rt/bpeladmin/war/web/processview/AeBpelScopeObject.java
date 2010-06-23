//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/processview/AeBpelScopeObject.java,v 1.2 2006/07/25 17:56:38 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.processview;

import java.util.List;

import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;

/**
 * Represents a web visual model for a BPEL scope activity.
 */
public class AeBpelScopeObject extends AeBpelActivityObject
{

   /**
    * Ctor.
    * @param aTagName tag name
    * @param aDef scope or process tag name.
    */
   protected AeBpelScopeObject(String aTagName, AeBaseDef aDef)
   {
      super(aTagName, aDef);
   }

   /**
    * Ctor.
    * @param aDef scope definition.
    */
   public AeBpelScopeObject(AeActivityScopeDef aDef)
   {
      this(AeActivityScopeDef.TAG_SCOPE, aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpeladmin.war.web.processview.AeBpelObjectBase#findPartnerLink(java.lang.String)
    */
   public AeBpelObjectBase findPartnerLink(String aPartnerLinkName)
   {
      AeBpelObjectBase  rVal = getPartnerLink(aPartnerLinkName);
      if (rVal != null)
      {
         return rVal;
      }
      else
      {
         return super.findPartnerLink(aPartnerLinkName);
      }
   }    
   
   /**
    * Returns the partnerlink contained in this scope or null if not found.
    * @param aPartnerLinkName
    * @return partner link.
    */
   protected AeBpelObjectBase getPartnerLink(String aPartnerLinkName)
   {
      AeBpelObjectBase rVal = null;
      List partnerLinks = getChildren("partnerLinks");//$NON-NLS-1$
      if (partnerLinks.size() > 0)
      {
         List partners = ( (AeBpelObjectContainer)partnerLinks.get(0)).getChildren();
         for (int i = 0; i < partners.size(); i++)
         {
            AeBpelObjectBase partnerLink = (AeBpelObjectBase) partners.get(i);
            if (partnerLink.getName().equals(aPartnerLinkName))
            {
               rVal = partnerLink;
               break;
            }
          }         
      }
      return rVal;
   }   
}
