//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/IAeIdentity.java,v 1.3.4.1 2008/04/21 16:16:25 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity;


/**
 * Identity interface.
 */
public interface IAeIdentity
{
   /**
    * Accessor to the Indentity's id.
    * @return Returns id string.
    */
   public String getId();

   /**
    * Name of identity (usually common or fullname).
    * @return name of identity.
    */
   public String getName();

   /**
    * Returns a list of roles this identity assumes.
    * @return list of <code>IAeIdentityRole</code>.
    */
   public IAeIdentityRole[] getRoles();

   /**
    * Returns list property names.
    * @return list of properties
    */
   public IAeIdentityProperty[] getProperties();

   /**
    * Returns property or <code>null</code> if not found.
    * @param aName
    * @return identity property or <code>null</code>.
    */
   public IAeIdentityProperty getProperty(String aName);

   /**
    * Checks to see if the identity has the given role.
    * @param aRoleName
    * @return true if in role.
    */
   public boolean hasRole(String aRoleName);

   /**
    * Compares this identity to another.
    * @param other
    * @return true if the identities are the same.
    */
   public boolean equals(Object other);

   /**
    * @return Hashcode for this identity.
    */
   public int hashCode();

   /**
    * String representation of this identity.
    */
   public String toString();
}
