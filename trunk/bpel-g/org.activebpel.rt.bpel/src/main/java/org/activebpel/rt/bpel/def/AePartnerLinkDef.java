// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/AePartnerLinkDef.java,v 1.13 2008/01/22 03:13:38 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.def.visitors.IAeDefVisitor;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.IAePartnerLinkType;
import org.activebpel.rt.wsdl.def.IAeRole;

/**
 * Definition for bpel partner link.
 */
public class AePartnerLinkDef extends AeNamedDef
{
   /** The partner link type name. */
   private QName mPartnerLinkTypeName;
   /** The my role. */
   private String mMyRole;
   /** The partner role. */
   private String mPartnerRole;
   /** The partner link type. */
   private IAePartnerLinkType mPartnerLinkType;
   /** The initialize partner role flag. */
   private Boolean mInitializePartnerRole;

   /**
    * Default constructor
    */
   public AePartnerLinkDef()
   {
      super();
   }

   /**
    * @return Returns the partnerLinkType.
    */
   public IAePartnerLinkType getPartnerLinkType()
   {
      return mPartnerLinkType;
   }

   /**
    * @param aPartnerLinkType The partnerLinkType to set.
    */
   public void setPartnerLinkType(IAePartnerLinkType aPartnerLinkType)
   {
      mPartnerLinkType = aPartnerLinkType;
   }
   
   /**
    * Accessor method to obtain the Partner Link Type for this partner.
    * 
    * @return partner link type of the partner
    */
   public QName getPartnerLinkTypeName()
   {
      return mPartnerLinkTypeName;
   }

   /**
    * Mutator method to set the Partner Link Type for this partner.
    * 
    * @param aPartnerLinkType the Partner Link Type for this partner
    */
   public void setPartnerLinkTypeName(QName aPartnerLinkType)
   {
      mPartnerLinkTypeName = aPartnerLinkType;
   }

   /**
    * Accessor method to obtain my role for this partner link.
    * 
    * @return my role for this partner link
    */
   public String getMyRole()
   {
      return mMyRole;
   }
   
   /**
    * Mutator method to set myRole for this partner link.
    * 
    * @param aMyRole my role for this partner link
    */
   public void setMyRole(String aMyRole)
   {
      mMyRole = aMyRole;
   }
   
   /**
    * Getter for the myRole portType
    */
   public QName getMyRolePortType()
   {
      if (AeUtil.notNullOrEmpty(getMyRole()) && getPartnerLinkType() != null)
      {
         IAeRole role = getPartnerLinkType().findRole(getMyRole());
         if (role != null)
            return role.getPortType().getQName();
      }
      return null;
   }
   
   /**
    * Getter for the partnerRole portType
    */
   public QName getPartnerRolePortType()
   {
      if (AeUtil.notNullOrEmpty(getPartnerRole()) && getPartnerLinkType() != null)
      {
         return getPartnerLinkType().findRole(getPartnerRole()).getPortType().getQName();
      }
      return null;
   }

   /**
    * Accessor method to obtain the partner role for this partner link.
    * 
    * @return partner role for this partner link
    */
   public String getPartnerRole()
   {
      return mPartnerRole;
   }

   /**
    * Mutator method to set the partner role for this partner link.
    * 
    * @param aPartnerRole partner role for this partner link.
    */
   public void setPartnerRole(String aPartnerRole)
   {
      mPartnerRole = aPartnerRole;
   }

   /**
    * @return Returns the initializePartnerRole.
    */
   public Boolean getInitializePartnerRole()
   {
      return mInitializePartnerRole;
   }

   /**
    * @param aInitializePartnerRole The initializePartnerRole to set.
    */
   public void setInitializePartnerRole(Boolean aInitializePartnerRole)
   {
      mInitializePartnerRole = aInitializePartnerRole;
   }

   /**
    * @see org.activebpel.rt.bpel.def.AeBaseDef#accept(org.activebpel.rt.bpel.def.visitors.IAeDefVisitor)
    */
   public void accept(IAeDefVisitor aVisitor)
   {
      aVisitor.visit(this);
   }
}
