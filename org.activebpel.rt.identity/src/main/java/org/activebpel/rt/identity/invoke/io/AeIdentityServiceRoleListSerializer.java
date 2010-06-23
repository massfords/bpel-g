//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.identity/src/org/activebpel/rt/identity/invoke/io/AeIdentityServiceRoleListSerializer.java,v 1.4 2008/02/02 19:44:32 PJayanetti Exp $
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
import org.activebpel.rt.identity.IAeIdentityRole;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *  Serializes a list of roles to ws message document.
 */
public class AeIdentityServiceRoleListSerializer extends AeIdentityServiceMessageSerializerBase
{
   /**
    * Role list.
    */
   private IAeIdentityRole mRoles[];
   
   /**
    * Ctor.
    * @param aRoles
    */
   public AeIdentityServiceRoleListSerializer(IAeIdentityRole aRoles[])   
   {
      mRoles = aRoles;
   }
   
   /**
    * Serializes a list of IAeIdentityRole objects into a roleList element
    * @see org.activebpel.rt.xml.AeXMLSerializerBase#serialize(org.w3c.dom.Element)
    */
   public Element serialize(Element aParentElement) throws AeException
   {
      Document document = createIdentityServiceNSDocument("roleList");  //$NON-NLS-1$
      if (mRoles != null)
      {
         Element rootEle = document.getDocumentElement();
         for (int i = 0; i < mRoles.length; i++)
         {
            createIdentityServiceNSElement(rootEle, "role", mRoles[i].getName()); //$NON-NLS-1$
         }
      }
      return document.getDocumentElement();
   }
//
}
