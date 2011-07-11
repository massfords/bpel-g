//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/ldap/AeLdifIdentitySearchProvider.java,v 1.4 2008/03/10 18:33:26 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider.ldap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.identity.provider.AeInMemorySearchProviderBase;
import org.activebpel.rt.util.AeUtil;


/**
 * LDIF based identity storage provider.
 */
public class AeLdifIdentitySearchProvider extends AeInMemorySearchProviderBase
{
   /** Possible values for a user object class in active-directory, novell eDirectory and OpenLDAP server. */
   private static Set USER_OBJECT_CLASSES = new HashSet();
   // initialize set
   static
   {  // todo (PJ) these values can be parameterized via config file.
      USER_OBJECT_CLASSES.add("person"); //$NON-NLS-1$
      USER_OBJECT_CLASSES.add("user"); //$NON-NLS-1$
      USER_OBJECT_CLASSES.add("inetorgperson"); //$NON-NLS-1$
      USER_OBJECT_CLASSES.add("organizationalperson"); //$NON-NLS-1$
   }

   /** Possible values for a group object class in active-directory, novell eDirectory and OpenLDAP server. */
   private static Set GROUP_OBJECT_CLASSES = new HashSet();
   // initialize set
   static
   {
      GROUP_OBJECT_CLASSES.add("group"); //$NON-NLS-1$
      GROUP_OBJECT_CLASSES.add("groupofuniquenames"); //$NON-NLS-1$
   }

   /**
    * Ctor.
    * @param aConfig
    */
   public AeLdifIdentitySearchProvider(Map aConfig)
   {
      super(aConfig);
   }

   /**
    * @see org.activebpel.rt.identity.provider.AeInMemorySearchProviderBase#load(java.io.File)
    */
   protected void load(File aFile) throws AeException
   {
      AeLdifFileReader reader = null;
      List ldifIdentities = new ArrayList();
      List ldifRoles = new ArrayList();
      Map ldifDnToRoleMap = new HashMap();
      // read file and collect users into users list and groups into groups map.
      try
      {
         reader = new AeLdifFileReader(aFile);
         reader.open();
         AeLdapEntry entry = null;
         while ((entry = reader.read()) != null)
         {
            if (isUser(entry))
            {
               ldifIdentities.add(new AeLdapIdentity( new AeLdapUser(entry) ));
            }
            else if (isGroup(entry))
            {
               AeLdapIdentityRole role = new AeLdapIdentityRole( new AeLdapGroup(entry) );
               ldifDnToRoleMap.put(entry.getDn(), role);
               ldifRoles.add(role);
            }
         }
      }
      catch(Exception e)
      {
         throw new AeException(e);
      }
      finally
      {
         if (reader != null)
         {
            reader.close();
         }
      }

      // add list of users as identity objects to in-memory collection.
      processEntries(ldifIdentities, ldifRoles, ldifDnToRoleMap);
   }

   /**
    * Adds the list of identities to the in memory collection and adds
    * the mappings for roles.
    * @param aIdentityList
    * @param aDnToRoleMap
    */
   protected void processEntries(List aIdentityList, List aRoleList, Map aDnToRoleMap)
   {
      // add list of users as identity objects to in-memory collection.
      for (int i = 0; i < aIdentityList.size(); i++)
      {
         AeLdapIdentity identity = (AeLdapIdentity) aIdentityList.get(i);

         // ldap entry may have n:1 mapping for principal name depending on the ldap server.
         if (AeUtil.notNullOrEmpty( identity.getLdapEntry().getProperty("userPrincipalName")) ) //$NON-NLS-1$
         {
            // active directory
            getPrincipalToIdentityMap().put(identity.getLdapEntry().getProperty("userPrincipalName"), identity); //$NON-NLS-1$
         }
         if (AeUtil.notNullOrEmpty( identity.getLdapEntry().getProperty("sAMAccountName")) ) //$NON-NLS-1$
         {
            // active directory via NT domain controller
            getPrincipalToIdentityMap().put(identity.getLdapEntry().getProperty("sAMAccountName"), identity); //$NON-NLS-1$
         }
         if (AeUtil.notNullOrEmpty( identity.getLdapEntry().getProperty("uid")) ) //$NON-NLS-1$
         {
            // open ldap etc.
            getPrincipalToIdentityMap().put(identity.getLdapEntry().getProperty("uid"), identity); //$NON-NLS-1$
         }

         // get list of group DNs this identity belongs to and add the role to the user
         List groupDNList = getRoleDnForIdentity(identity, aRoleList);
         for (int j = 0; j < groupDNList.size(); j++)
         {
            String dn = (String)groupDNList.get(j);
            AeLdapIdentityRole role = (AeLdapIdentityRole)aDnToRoleMap.get(dn);
            if (role != null)
            {
               identity.addRole(role);
               // add mapping
               addRoleToIdentityMapping(role, identity);
            }
         }
      }

      // add all roles the provider knows to an all role list.
      addRoleToList(aDnToRoleMap);
   }
   
   /**
    * Returns a list role DNs given a identity (user). Basically, lists roles by user. 
    * @param aIdentity
    * @param aRoleList
    * @return List containing DN string entries
    */
   protected List getRoleDnForIdentity(AeLdapIdentity aIdentity, List aRoleList)
   {
      // get list of group DNs this identity belongs to.
      // (note: does not support nested groups).
      // For MS-ActiveDirectory
      List groupDNList = aIdentity.getLdapEntry().getPropertyList("memberOf"); //$NON-NLS-1$
      if (AeUtil.isNullOrEmpty(groupDNList))
      {
         // check open-ldap format - by matching the 'uniqueMember' attribute.
         groupDNList = new ArrayList();            
         Iterator it = aRoleList.iterator();
         while (it.hasNext())
         {
            AeLdapIdentityRole role = (AeLdapIdentityRole) it.next();
            if (role.getLdapEntry().hasProperty("uniqueMember", aIdentity.getLdapEntry().getDn() ) ) //$NON-NLS-1$
            {
               groupDNList.add( role.getLdapEntry().getDn() );
            }
         }            
      }      
      return groupDNList;
   }
      
   /**
    * Returns true if the entry is a user
    * @param aEntry
    * @return true if object class is a person.
    */
   protected boolean isUser(AeLdapEntry aEntry)
   {
      return hasPropertyValue(aEntry, "objectClass", USER_OBJECT_CLASSES); //$NON-NLS-1$
   }

   /**
    * Returns true if the entry is a group
    * @param aEntry
    * @return true if object class is a group.
    */
   protected boolean isGroup(AeLdapEntry aEntry)
   {
      return hasPropertyValue(aEntry, "objectClass", GROUP_OBJECT_CLASSES); //$NON-NLS-1$
   }

   /**
    * Returns true of the LDIF entry has one of the values in the set for the named property.
    * @param aEntry
    * @param aPropertyName
    * @param aPossibleValues
    * @return true if aPropertyName has any one of the values given in the aPossibleValues set.
    */
   protected boolean hasPropertyValue(AeLdapEntry aEntry, String aPropertyName, Set aPossibleValues)
   {
      Iterator it = aPossibleValues.iterator();
      while ( it.hasNext() )
      {
         // check if property exist (case-insensitive)
         // (Some servers may use camel case such as inetOrgPerson while other may use lower case for objectClass inetorgperson.) 
         if (aEntry.hasProperty(aPropertyName, (String) it.next(), false) )
         {
            return true;
         }
      }
      return false;
   }
}
