//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/ldap/AeLdapIdentity.java,v 1.2 2007/11/20 20:45:03 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider.ldap;

import java.util.Iterator;

import org.activebpel.rt.identity.AeIdentity;
import org.activebpel.rt.identity.AeIdentityProperty;
import org.activebpel.rt.util.AeUtil;

/**
 * LDAP version of an identity object.
 */
public class AeLdapIdentity extends AeIdentity
{
   /** Underlying LDAP user. */
   private AeLdapEntry mLdapEntry;

   /**
    * Ctor for LDAP user.
    * @param aLdapUser
    */
   public AeLdapIdentity(AeLdapUser aLdapUser)
   {
      // Note: options for name are LDAP attributes cn,displayName, or givenName + sn.
      // For this implementation, use CN (which is not always the same as displayName or firstName+lastName).
      super( aLdapUser.getDn(), aLdapUser.getCn() );
      setLdapEntry(aLdapUser);
      // copy over LDAP attributes that were returned as part of LDAP query 'returningAttributes' list.
      Iterator propNames = getLdapEntry().propertyNames();
      while( propNames.hasNext() )
      {
         String propName = (String) propNames.next();
         String propValue = getLdapEntry().getProperty( propName );
         if (AeUtil.notNullOrEmpty(propValue))
         {
            setProperty(new AeIdentityProperty(propName, propValue));
         }
      }
      // add well known 'name' property
      setProperty(new AeIdentityProperty("name", aLdapUser.getCn())) ; //$NON-NLS-1$
      // add well known 'email' property
      setProperty(new AeIdentityProperty("email", aLdapUser.getEmail() )) ; //$NON-NLS-1$
   }

   /**
    * @return the ldapEntry
    */
   public AeLdapEntry getLdapEntry()
   {
      return mLdapEntry;
   }

   /**
    * @param aLdapEntry the ldapEntry to set
    */
   protected void setLdapEntry(AeLdapEntry aLdapEntry)
   {
      mLdapEntry = aLdapEntry;
   }
}
