//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/invoke/io/AeIdentitySerializerBase.java,v 1.4 2008/02/17 21:54:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.identity.invoke.io;

import org.activebpel.rt.AeException;
import org.activebpel.rt.identity.IAeIdentity;
import org.activebpel.rt.identity.IAeIdentityProperty;
import org.activebpel.rt.identity.IAeIdentityRole;
import org.w3c.dom.Element;

/**
 * Base class that is needed to serialize a list of IAeIdentity objects
 */
public abstract class AeIdentitySerializerBase extends AeIdentityServiceMessageSerializerBase
{
   /**
    * Creates the element representing the IAeIdentity and add it to the parent element.
    * @param aParentElement
    * @param aIdentity
    */
   protected void createIdentityElement(Element aParentElement, IAeIdentity aIdentity) throws AeException
   {
      Element identityEle = createIdentityTypeNSElement(aParentElement, "identity", null ); //$NON-NLS-1$
      createIdentityTypeNSElement(identityEle, "id", aIdentity.getId() ); //$NON-NLS-1$
      createPropertyListElement(identityEle, aIdentity.getProperties());
      createRoleElements(identityEle, aIdentity.getRoles());
   }
   
   /**
    * Creates the element representing a list of IAeIdentityProperty and adds it to the parent element. 
    * @param aParentElement
    * @param aProperties
    */
   protected void createPropertyListElement(Element aParentElement, IAeIdentityProperty aProperties[]) throws AeException
   {
      Element propListEle = createIdentityTypeNSElement(aParentElement, "properties", null); //$NON-NLS-1$
      for (int i = 0; i < aProperties.length; i++)
      {
         createPropertyElement(propListEle, aProperties[i]);
      }
   }   
   
   /**
    * Creates the element representing the IAeIdentityProperty and adds it to the parent element. 
    * @param aParentElement
    * @param aProperty
    */
   protected void createPropertyElement(Element aParentElement, IAeIdentityProperty aProperty) throws AeException
   {
      Element propEle = createIdentityTypeNSElement(aParentElement, "property", aProperty.getValue()); //$NON-NLS-1$
      propEle.setAttribute("name", aProperty.getName()); //$NON-NLS-1$
   }
   
   /**
    * Creates the element representing a list of IAeIdentityRole objects and adds it to the parent element. 
    * @param aParentElement
    * @param aRoles
    */
   protected void createRoleElements(Element aParentElement, IAeIdentityRole aRoles[]) throws AeException
   {
      Element roleListEle = createIdentityTypeNSElement(aParentElement, "roles", null); //$NON-NLS-1$
      for (int i = 0; i < aRoles.length; i++)
      {
         createIdentityTypeNSElement(roleListEle, "role", aRoles[i].getName()); //$NON-NLS-1$
      }      
   }   
}
