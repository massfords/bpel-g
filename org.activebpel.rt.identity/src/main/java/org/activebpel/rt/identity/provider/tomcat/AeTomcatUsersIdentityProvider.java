//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/provider/tomcat/AeTomcatUsersIdentityProvider.java,v 1.6 2008/02/27 19:35:29 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.provider.tomcat;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.identity.AeIdentity;
import org.activebpel.rt.identity.AeIdentityProperty;
import org.activebpel.rt.identity.AeIdentityRole;
import org.activebpel.rt.identity.IAeIdentityRole;
import org.activebpel.rt.identity.provider.AeInMemorySearchProviderBase;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Identity storage search provider based on the Jakarta Tomcat
 * tomcat-users.xml realm.
 */
public class AeTomcatUsersIdentityProvider extends AeInMemorySearchProviderBase
{
   /** Tomcat-users dom. */
   private Document mDocument;

   /**
    * Ctor.
    * @param aConfig
    */
   public AeTomcatUsersIdentityProvider(Map aConfig)
   {
      super(aConfig);
   }

   /**
    * @see org.activebpel.rt.identity.provider.AeInMemorySearchProviderBase#load(java.io.File)
    */
   protected void load(File aFile) throws AeException
   {
      setDocument( AeXmlUtil.toDoc(aFile, null) );
      try
      {
         Map roleMap = new HashMap();
         Iterator userEleIter = AeXPathUtil.selectNodes(getDocument(), "//user").iterator(); //$NON-NLS-1$
         while( userEleIter.hasNext() )
         {
            AeIdentity identity = createIdentity((Element) userEleIter.next());
            getPrincipalToIdentityMap().put(identity.getId(), identity);
            IAeIdentityRole[] roles = identity.getRoles();
            // update rolename-to-identitiesList map.
            for (int i = 0; i < roles.length; i++)
            {
               addRoleToIdentityMapping(roles[i], identity);
               roleMap.put(roles[i].getName(), roles[i]);
            }
         }
         // add all roles the provider knows to an all role list.
         addRoleToList(roleMap);
      }
      catch(Exception e)
      {
         AeException aex = new AeException(e);
         throw aex;
      }
   }

   /**
    * Creates <code>AeIdentity</code> given tomcat user element node.
    * @param aUserElement tomcat user element.
    * @return AeIdentity representing the user.
    */
   protected AeIdentity createIdentity(Element aUserElement)
   {
      // Tomcat files use the attribute 'name' for the username.
      // However, for ActiveBPEL 4.x release, the identity service 
      // xml file (incorrectly) assumed the 'username' attribute to have the username.
      // To keep the format compatible with the tomcatusers xml file,
      // we first check for the 'name' attribute, and if that is not
      // available, then look for the ae 4.x 'username' attribute
      
      String username = aUserElement.getAttribute("name"); //$NON-NLS-1$
      if (AeUtil.isNullOrEmpty(username))
      {
         username = aUserElement.getAttribute("username"); //$NON-NLS-1$
      }
      AeIdentity identity =  new AeIdentity(username, username);
      identity.setProperty(new AeIdentityProperty("email",  aUserElement.getAttribute("email"))) ; //$NON-NLS-1$  //$NON-NLS-2$
      Iterator roleNamesIterator = AeUtil.toList(aUserElement.getAttribute("roles"),",").iterator(); //$NON-NLS-1$ //$NON-NLS-2$
      while ( roleNamesIterator.hasNext() )
      {
         String roleName =  (String) roleNamesIterator.next();
         // Tomcat role id and role name are the same.
         identity.addRole( new AeIdentityRole(roleName) );
      }
      return identity;
   }
   
   /**
    * @return the document
    */
   protected Document getDocument()
   {
      return mDocument;
   }

   /**
    * @param aDocument the document to set
    */
   protected void setDocument(Document aDocument)
   {
      mDocument = aDocument;
   }

}
