//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/AeInMemorySearchProviderBase.java,v 1.7.4.1 2008/04/28 21:56:48 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.identity.AeIdentityException;
import org.activebpel.rt.identity.AeMessages;
import org.activebpel.rt.identity.IAeIdentity;
import org.activebpel.rt.identity.IAeIdentityRole;
import org.activebpel.rt.identity.search.AeIdentitySearchException;
import org.activebpel.rt.util.AeUtil;

/**
 * A search provider base for simple file based formats such as the tomcat-users
 * and LDIF file formats. The implementations initially load all the content
 * into memory on start up.
 */
public abstract class AeInMemorySearchProviderBase extends AeIdentitySearchProviderBase
{
   /** Map containing rolename to identities.*/
   private Map mRoleToIdentitySetMap = new HashMap();
   /** Map of principal name to identities. */
   private Map mPrincipalToIdentityMap = new HashMap();
   /** List of all roles provider knows. */
   private List mRoles = new ArrayList();

   /**
    * ctor.
    * @param aConfig
    */
   protected AeInMemorySearchProviderBase(Map aConfig)
   {
      super(aConfig);
   }

   /**
    * Returns file location path to data file.
    */
   protected String getFilename()
   {
      return (String) getConfig().get(AeIdentityFileConfig.FILE_NAME_KEY);
   }

   /**
    * Overrides method to load contents from file.
    * @see org.activebpel.rt.identity.provider.IAeIdentitySearchProvider#initialize()
    */
   public void initialize() throws AeException
   {
      String filename = AeUtil.replaceAntStyleParams(getFilename(), null);
      
      if (AeUtil.isNullOrEmpty(filename))
      {
         throw new AeIdentityException(AeIdentityException.CONFIGURATION_EXCEPTION, AeMessages.getString("AeInMemorySearchProviderBase.FILE_REQUIRED")); //$NON-NLS-1$
      }
      File file = null;
      // check with the class loader to see if name resource exists.
      URL resourceUrl = getClass().getResource(filename);
      if (resourceUrl != null)
      {
         file = new File (resourceUrl.getFile());
      }
      else
      {
         // assume absolute file path is specified instead of a class resource name.
         file = new File(filename);
      }

      if (!file.isFile())
      {
         String msg = AeMessages.format("AeInMemorySearchProviderBase.FILE_NOT_FOUND", file.getAbsolutePath()); //$NON-NLS-1$
         throw new AeIdentityException(AeIdentityException.CONFIGURATION_EXCEPTION, msg); 
      }
      // load the contents.
      try
      {
         load(file);
      }
      catch(Exception e)
      {
         throw new AeIdentityException(AeIdentityException.CONFIGURATION_EXCEPTION, e.getMessage(),e);
      }
      if (getPrincipalToIdentityMap().size() == 0)
      {
         throw new AeIdentityException(AeIdentityException.CONFIGURATION_EXCEPTION, AeMessages.format("AeInMemorySearchProviderBase.ENTRIES_NOT_FOUND", filename)); //$NON-NLS-1$
      }
         
   }

   /**
    * @see org.activebpel.rt.identity.provider.AeIdentitySearchProviderBase#getIdentityByPrincipal(java.lang.String)
    */
   protected IAeIdentity getIdentityByPrincipal(String aPrincipalName) throws AeIdentitySearchException
   {
      IAeIdentity identity = (IAeIdentity) getPrincipalToIdentityMap().get(aPrincipalName);
      return identity;
   }

   /**
    * @see org.activebpel.rt.identity.provider.AeIdentitySearchProviderBase#getIdentitiesByRole(java.lang.String)
    */
   protected Set getIdentitiesByRole(String aRoleName) throws AeIdentitySearchException
   {
      Set identitiesByRoleSet = (Set) getRoleToIdentitySetMap().get( aRoleName );
      if (identitiesByRoleSet == null)
      {
         identitiesByRoleSet = Collections.EMPTY_SET;
      }
      return identitiesByRoleSet;
   }

   /**
    * Called during the initialization to load content into memory.
    * @param aFile
    * @throws Exception
    */
   protected abstract void load(File aFile) throws Exception;


   /**
    * Adds a mapping entry for role name to identity.
    * @param aRole
    * @param aIdentity
    */
   protected void addRoleToIdentityMapping(IAeIdentityRole aRole, IAeIdentity aIdentity)
   {
      Set identitiesByRoleSet = (Set) getRoleToIdentitySetMap().get( aRole.getName() );
      if (identitiesByRoleSet == null)
      {
         identitiesByRoleSet = new LinkedHashSet();
         getRoleToIdentitySetMap().put( aRole.getName() , identitiesByRoleSet );
      }
      identitiesByRoleSet.add(aIdentity);
   }
   
   /**
    * Add all roles to a role list.
    * @param aDnToRoleMap
    */
   protected void addRoleToList(Map aDnToRoleMap)
   {
      for (Iterator iter = aDnToRoleMap.entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry)iter.next();
         mRoles.add(entry.getValue());
      }
   }
   
   /**
    * @return the principa;ToIdentityMap
    */
   protected Map getPrincipalToIdentityMap()
   {
      return mPrincipalToIdentityMap;
   }

   /**
    * @return the roleToIdentitySetMap
    */
   protected Map getRoleToIdentitySetMap()
   {
      return mRoleToIdentitySetMap;
   }

   /**
    * @return all roles the proviser knows.
    */
   protected List getRoles()
   {
      return mRoles;
   }

   /**
    * Finds all of the roles that the provider knows about. 
    * Overrides method to 
    * @see org.activebpel.rt.identity.provider.IAeIdentitySearchProvider#findRoles()
    */
   public IAeIdentityRole[] findRoles() throws AeIdentitySearchException
   {
      List list = getRoles();
      IAeIdentityRole[] allRoles = new IAeIdentityRole[list.size()];
      list.toArray(allRoles);
      
      return allRoles; 
   }
   
}
