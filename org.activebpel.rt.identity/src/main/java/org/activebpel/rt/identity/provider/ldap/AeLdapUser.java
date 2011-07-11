//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/ldap/AeLdapUser.java,v 1.1 2007/03/26 19:30:23 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider.ldap;

/**
 * User object representing an LDAP objectPerson category.
 */
public class AeLdapUser extends AeLdapEntry
{
   /**
    * Contructs a AeLdapUser with the given the LDAP entry.
    * @param aOtherEntry LDAP entry.
    */
   public AeLdapUser(AeLdapEntry aOtherEntry)
   {
      super(aOtherEntry);
   }

   /**
    * Returns the email.
    * @return mail string.
    */
   public String getEmail()
   {
      return getProperty("mail"); //$NON-NLS-1$
   }
}
