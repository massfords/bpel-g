//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/policy/IAePolicyReference.java,v 1.1 2007/07/27 18:08:54 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def.policy;

import javax.wsdl.extensions.ExtensibilityElement;
import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;

/**
 * Interface for classes representing a wsp:PolicyReference extension element
 * within a WSDL definition
 */
public interface IAePolicyReference extends ExtensibilityElement
{
   public static final String URI_ATTRIBUTE = "URI"; //$NON-NLS-1$
   public static final String POLICY_REFERENCE_ELEMENT = "PolicyReference"; //$NON-NLS-1$
   public static final QName POLICY_REF_QNAME = new QName(IAeConstants.WSP_NAMESPACE_URI, POLICY_REFERENCE_ELEMENT);
   
   /**
    * @return the fully qualified reference URI pointing to this policy
    */
   public String getReferenceURI();

   /**
    * Sets the fully qualified reference URI
    */
   public void setReferenceURI(String aURI);
   
   /**
    * Returns the target namespace part of the fully qualified URI
    * 
    * For example, the namespace URI of "http://www.fabrikam123.com/policies#RmPolicy" 
    * is "http://www.fabrikam123.com/policies"
    * 
    * A null or empty URI indicates that the policy is defined within the target namespace of the document
    */
   public String getNamespaceURI();

   /**
    * @param aNamespaceURI the baseURI to set
    */
   public void setNamespaceURI(String aNamespaceURI);

   /**
    * Returns the policy Id within the namespace URI
    * For example, the referenceId of "http://www.fabrikam123.com/policies#RmPolicy" is "RmPolicy"
    */
   public String getReferenceId();

   /**
    * @param aId the Id to set
    */
   public void setReferenceId(String aId);
   
   /**
    * Returns true if the policy is defined within the same target namespace as the reference
    */
   public boolean isLocalReference();
   
   /**
    * @return the target namespace of the document that contains this reference
    */
   public String getTargetNamespace();
}