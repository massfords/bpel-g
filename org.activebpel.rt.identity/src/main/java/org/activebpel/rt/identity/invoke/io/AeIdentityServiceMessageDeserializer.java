//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/invoke/io/AeIdentityServiceMessageDeserializer.java,v 1.6 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.invoke.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.identity.IAeIdentityServiceConstants;
import org.activebpel.rt.identity.search.AeIdentityQuery;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.xml.AeXMLDeserializerBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Deserializer for all incoming messages.
 */
public class AeIdentityServiceMessageDeserializer extends AeXMLDeserializerBase
{
   /**
    * Default Ctor.
    * @param aDocument
    */
   public AeIdentityServiceMessageDeserializer(Document aDocument)
   {
      setElement(aDocument.getDocumentElement());
   }

   /**
    * @see org.activebpel.rt.xml.AeXMLDeserializerBase#initNamespaceMap(java.util.Map)
    */
   protected void initNamespaceMap(Map aMap)
   {
      aMap.put(IAeIdentityServiceConstants.IDENTITY_TYPES_PREFIX, IAeIdentityServiceConstants.IDENTITY_TYPES_NS);
      aMap.put(IAeIdentityServiceConstants.IDENTITY_SERVICE_PREFIX, IAeIdentityServiceConstants.IDENTITY_SERVICE_NS);
   } 
   
   /**
    * Returns the node local named prefixed with the service NS prefix.
    * @param aLocalName
    */
   protected String getServicePrefixedName(String aLocalName)
   {
      return IAeIdentityServiceConstants.IDENTITY_SERVICE_PREFIX + ":" + aLocalName; //$NON-NLS-1$
   }

   /**
    * Returns the node local named prefixed with the identity NS prefix.
    * @param aLocalName
    */
   protected String getPrefixedName(String aLocalName)
   {
      return IAeIdentityServiceConstants.IDENTITY_TYPES_PREFIX + ":" + aLocalName; //$NON-NLS-1$
   }
   
   /**
    * Returns the principal name for a findRolesByPrincipal request principalNameInput message.
    * @return principal name or null if not found.
    */
   public String getPrincipalName()
   {
      return getText(getElement(), "//" + getServicePrefixedName("principalName")); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Returns the roleName for findIdentitiesByRole operation roleNameInput message type.
    * @return role name.
    */
   public String getRoleName()
   {
      return getText(getElement(), "//" + getServicePrefixedName("roleName")); //$NON-NLS-1$ //$NON-NLS-2$
   }
   
   /**
    * Gets the list of queries from the message.
    */
   public List getQueries()
   {
      List list = new ArrayList();
      List queryElements = AeXPathUtil.selectNodesIgnoreException(getElement(), "//aeid:identityQuery", getNamespaceMap()); //$NON-NLS-1$
      for (Iterator it = queryElements.iterator(); it.hasNext();)
      {
         Element e = (Element) it.next();
         AeIdentityQuery query = (AeIdentityQuery) getQuery(e);
         list.add(query);
      }
      return list;
   }

   /**
    * Returns the first query found in the message.
    */
   public AeIdentityQuery getQuery()
   {
      Element e = (Element) AeXPathUtil.selectSingleNodeIgnoreException(getElement(), "//aeid:identityQuery[1]", getNamespaceMap()); //$NON-NLS-1$
      return getQuery(e);
   }
   
   /**
    * Gets the query from the given element
    * @param aElement
    */
   protected AeIdentityQuery getQuery(Element aElement)
   {
      AeIdentityQuery  rval = new AeIdentityQuery();
      String includeDirective = "include"; //$NON-NLS-1$
      String excludeDirective = "exclude"; //$NON-NLS-1$
      
      // get list of roles to include
      List includeRoles = getRoleList(aElement, includeDirective);
      rval.includeRoles(includeRoles);

      List excludeRoles = getRoleList(aElement, excludeDirective);
      rval.excludeRoles(excludeRoles);

      // list of principals to include.
      List includePrincipals = getPrincipalList(aElement, includeDirective);
      rval.includePrincipals(includePrincipals);

      // exclude list
      List excludePrincipals = getPrincipalList(aElement, excludeDirective);
      rval.excludePrincipals(excludePrincipals);
      return rval;
   }

   /**
    * Gets the list of roles to include or exclude in the query
    * @param aElement
    * @param aDirective - value for the directive, either "include" or "exclude"
    */
   private List getRoleList(Element aElement, String aDirective)
   {
      return getIdentityQueryValues(aElement, aDirective, "group", "role");  //$NON-NLS-1$//$NON-NLS-2$
   }

   /**
    * Gets the list of principals to include or exclude in the query
    * @param aElement
    * @param aDirective - value for the directive, either "include" or "exclude"
    */
   private List getPrincipalList(Element aElement, String aDirective)
   {
      return getIdentityQueryValues(aElement, aDirective, "user", "principal");  //$NON-NLS-1$//$NON-NLS-2$
   }

   /**
    * Extracts values from the identity query.
    * @param aElement
    * @param aDirective - value for the directive, either "include" or "exclude"
    * @param aNodeName - name of the node. One of (user, group)
    * @param aLegacyNodeName - name of the legacy node. One of (principal, role)
    */
   private List getIdentityQueryValues(Element aElement, String aDirective, String aNodeName, String aLegacyNodeName)
   {
      List roles = getIdentityQueryValues(aElement, aDirective, aNodeName);
      if (roles.isEmpty())
      {
         roles = getIdentityQueryValues(aElement, aDirective, aLegacyNodeName);
      }
      return roles;
   }

   /**
    * Extracts the specified nodes from the identity query.
    * @param aElement 
    * @param aDirective - value for the directive, either "include" or "exclude"
    * @param aNodeName - name of the node. One of (user, group, principal, role)
    */
   private List getIdentityQueryValues(Element aElement, String aDirective, String aNodeName)
   {
      return getTextList(aElement, getPrefixedName(aDirective) + "/" + getPrefixedName(aNodeName) ); //$NON-NLS-1$
   }
}
