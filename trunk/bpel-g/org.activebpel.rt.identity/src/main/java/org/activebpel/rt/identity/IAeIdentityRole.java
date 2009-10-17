//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/IAeIdentityRole.java,v 1.2 2008/02/17 21:54:48 mford Exp $
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
 * Identity role.
 */
public interface IAeIdentityRole
{
   /**
    * Unique id of role.
    * @return role id.
    */
   public String getId();
   
   /**
    *  Returns role.
    * @return name of role
    */
   public String getName();
   
   /**
    * Compares the role to another.
    * @param other
    * @return true if the roles are the same.
    */
   public boolean equals(Object other);
   
   /** 
    * @return Hashcode for this role.
    */
   public int hashCode();
   
   /**
    * String representation of this role. 
    */
   public String toString();
   
}
