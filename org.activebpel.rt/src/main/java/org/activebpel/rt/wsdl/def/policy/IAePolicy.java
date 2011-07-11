//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/policy/IAePolicy.java,v 1.1 2007/07/27 18:08:54 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def.policy;

import javax.wsdl.extensions.ElementExtensible;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.w3c.dom.Element;

/**
 * Interface for classes that represent a wsp:Policy extensibility element in 
 * a WSDL document
 */
public interface IAePolicy extends ExtensibilityElement, ElementExtensible 
{
   public static final String WSU_ID_ATTRIBUTE = "Id"; //$NON-NLS-1$
   public static final String POLICY_ELEMENT = "Policy"; //$NON-NLS-1$
   public static final QName POLICY_QNAME = new QName(IAeConstants.WSP_NAMESPACE_URI, POLICY_ELEMENT);
   
   /**
    * @return the wsu:Id relative to the base URI
    */
   public String getReferenceId();

   /**
    * @param aId the wsu:Id to set
    */
   public void setReferenceId(String aId);

   /**
    * @return the wsp:Policy element
    */
   public Element getPolicyElement();

   /**
    * @param aPolicy the policy element to set
    */
   public void setPolicyElement(Element aPolicy);
   
}