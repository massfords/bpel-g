// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/AePartnerLinkTypeImpl.java,v 1.7 2006/06/26 16:46:43 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

/**
 * This class represents an implementation mapping to a Partner Link Type
 * extension within a WSDL document. It contains information about operations
 * associated with this Partner Link Type element.
 */
public class AePartnerLinkTypeImpl implements ExtensibilityElement, IAePartnerLinkType, IAeBPELExtendedWSDLConst
{
   // The QName of this extension element.
   private QName mElementType;

   // Indicates if the semantics of this extension are required.
   private Boolean mRequired;

   // The name of this Partner Link Type.
   private String mName;

   // List of Role's associated with this Partner Link Type.
   private Map mRolesMap = new HashMap();

   /**
    * Constructor.  This contructor is generally used for modeling a new Partner
    * Link extension element.
    */
   public AePartnerLinkTypeImpl()
   {
      setElementType(new QName(PARTNER_LINK_NAMESPACE, PARTNER_LINK_TYPE_TAG));
   }

   /**
    * Add a Role element to this Partner Link Type.
    * @param aRole
    */
   public void addRole(IAeRole aRole)
   {
      mRolesMap.put((String) aRole.getName(), (IAeRole) aRole);
   }

   /**
    * Find a specific Role element given its Role name.
    * @param aName
    * @return IAeRole
    */
   public IAeRole findRole(String aName)
   {
      return (IAeRole) mRolesMap.get(aName);
   }
 
   /**
    * Get all the Roles defined for this Partner Link Type.
    * @return Iterator
    */
   public Iterator getRoleList()
   {
      return mRolesMap.values().iterator();
   }
   
   /**
    * Remove a Role that is associated with this Partner Link Type.  Returns the
    * removed Role if found, otherwise null is returned.
    * @param aName
    * @return IAeRole
    */
   public IAeRole removeRole(String aName)
   {
      return (IAeRole)mRolesMap.remove(aName);
   }
   
   /**
    * Get the name of this Partner Link Type.
    * @return String
    */
   public String getName()
   {
      return mName;
   }

   /**
    * Set the name for Partner Link Type.
    * @param aName
    */
   public void setName(String aName)
   {
      mName = aName;
   }

   /**
    * Get the QName of this Partner Link Type element.
    * @return QName
    */
   public QName getElementType()
   {
      return mElementType;
   }

   /**
    * Set the QName of this Partner Link Type element.
    * @param aElementType
    */
   public void setElementType(QName aElementType)
   {
      mElementType = aElementType;
   }

   /**
    * Set whether or not the semantics of this extension are required.
    * @param aRequired
    */
   public void setRequired(Boolean aRequired)
   {
      mRequired = aRequired;
   }

   /**
    * Get whether or not the semantics of this extension are required.
    * @return Boolean
    */
   public Boolean getRequired()
   {
      return mRequired;
   }
}
