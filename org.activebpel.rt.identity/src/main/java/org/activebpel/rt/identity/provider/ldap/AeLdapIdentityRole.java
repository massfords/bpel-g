//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/ldap/AeLdapIdentityRole.java,v 1.2 2007/06/14 14:59:45 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider.ldap;

import org.activebpel.rt.identity.AeIdentityRole;

/**
 * LDAP version of an identity role object.
 */
public class AeLdapIdentityRole extends AeIdentityRole
{

   /** Underlying LDAP group . */
   private AeLdapEntry mLdapEntry;

   /**
    * Ctor for identity role based on the LDAP group..
    * @param aLdapGroup
    */
   public AeLdapIdentityRole(AeLdapGroup aLdapGroup)
   {
      super( aLdapGroup.getDn(), aLdapGroup.getName() );
      setLdapEntry(aLdapGroup);
   }

   /**
    * @return the ldapEntry
    */
   protected AeLdapEntry getLdapEntry()
   {
      return mLdapEntry;
   }

   /**
    * @param aLdapEntry the aLdapEntry to set
    */
   protected void setLdapEntry(AeLdapEntry aLdapEntry)
   {
      mLdapEntry = aLdapEntry;
   }

}
