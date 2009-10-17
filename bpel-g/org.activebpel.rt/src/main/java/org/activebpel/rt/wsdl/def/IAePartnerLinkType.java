// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/IAePartnerLinkType.java,v 1.3 2004/07/08 13:09:45 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def;

import java.util.Iterator;


/**
 * This interface represents a Partner Link Type element.  It contains
 * information about operations associated with this Partner Link Type.
 */
public interface IAePartnerLinkType
{
   /**
    * Add a Role element to this Partner Link Type.
    * @param aRole
    */
   public void addRole(IAeRole aRole);

   /**
    * Find a specific Role element given its Role name.
    * @param aName
    * @return IAeRole
    */
   public IAeRole findRole(String aName);

   /**
    * Get all the Roles defined for this Partner Link Type.
    * @return Iterator
    */
   public Iterator getRoleList();
   
   /**
    * Remove a Role that is associated with this Partner Link Type. 
    * @param aName
    * @return IAeRole
    */
   public IAeRole removeRole(String aName);

   /**
    * Get the name of this Partner Link Type.
    * @return String
    */
   public String getName();

   /**
    * Set the name for Partner Link Type.
    * @param aName
    */
   public void setName(String aName);

}
