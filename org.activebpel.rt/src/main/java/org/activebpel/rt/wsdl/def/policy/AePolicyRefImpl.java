//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/wsdl/def/policy/AePolicyRefImpl.java,v 1.2 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.wsdl.def.policy;

import javax.xml.namespace.QName;

import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementation representing a wsp:PolicyReference extensibility element in a WSDL document
 */
public class AePolicyRefImpl implements IAePolicyReference
{
   private static final String ID_SEPARATOR = "#"; //$NON-NLS-1$
   
   private String mBaseURI;
   private String mReferenceId;
   private Boolean mRequired;
   private QName mElementType = POLICY_REF_QNAME;
   private String mTargetNamespace;

   /**
    * No-arg constructor
    */
   public AePolicyRefImpl()
   {
      
   }
   
   /**
    * Create a new instance from an element
    * @param aElement
    */
   public AePolicyRefImpl(Element aElement)
   {
      setReferenceURI(aElement.getAttribute(IAePolicyReference.URI_ATTRIBUTE)); 
      Document owner = aElement.getOwnerDocument();
      if (owner != null)
      {
         // get the targetNamespace attribute from the owner document in case
         // we need to lookup the wsdl for a local reference 
         setTargetNamespace(owner.getDocumentElement().getAttribute("targetNamespace")); //$NON-NLS-1$
      }
   }
   
   /**
    * @see org.activebpel.rt.wsdl.def.policy.IAePolicyReference#getNamespaceURI()
    */
   public String getNamespaceURI()
   {
      return mBaseURI;
   }

   /**
    * @see org.activebpel.rt.wsdl.def.policy.IAePolicyReference#getReferenceId()
    */
   public String getReferenceId()
   {
      return mReferenceId;
   }

   /**
    * @see org.activebpel.rt.wsdl.def.policy.IAePolicyReference#getReferenceURI()
    */
   public String getReferenceURI()
   {
      return getNamespaceURI() + ID_SEPARATOR + getReferenceId();
   }

   /**
    * @see org.activebpel.rt.wsdl.def.policy.IAePolicyReference#setNamespaceURI(java.lang.String)
    */
   public void setNamespaceURI(String aBaseURI)
   {
      mBaseURI = aBaseURI;
   }

   /**
    * @see org.activebpel.rt.wsdl.def.policy.IAePolicyReference#setReferenceId(java.lang.String)
    */
   public void setReferenceId(String aId)
   {
      mReferenceId = aId;
   }

   /**
    * @see org.activebpel.rt.wsdl.def.policy.IAePolicyReference#setReferenceURI(java.lang.String)
    */
   public void setReferenceURI(String aURI)
   {
      if (aURI == null)
      {
         setNamespaceURI(null);
         setReferenceId(null);
         return;
      }
      
      int index = aURI.indexOf(ID_SEPARATOR);
      if (index == 0)
      {
         setNamespaceURI(null);
         setReferenceId(aURI.substring(index + 1));
      }
      if (index == -1)
      {
         setNamespaceURI(null);
         setReferenceId(aURI);
      }
      else
      {
         setNamespaceURI(aURI.substring(0, index));
         setReferenceId(aURI.substring(index + 1));
      }
   }

   /**
    * @see javax.wsdl.extensions.ExtensibilityElement#getElementType()
    */
   public QName getElementType()
   {
      return mElementType;
   }

   /**
    * @see javax.wsdl.extensions.ExtensibilityElement#getRequired()
    */
   public Boolean getRequired()
   {
      return mRequired;
   }

   /**
    * @see javax.wsdl.extensions.ExtensibilityElement#setElementType(javax.xml.namespace.QName)
    */
   public void setElementType(QName aElementType)
   {
      mElementType = aElementType;
   }

   /**
    * @see javax.wsdl.extensions.ExtensibilityElement#setRequired(java.lang.Boolean)
    */
   public void setRequired(Boolean aRequired)
   {
      mRequired = aRequired;
   }

   /**
    * @see org.activebpel.rt.wsdl.def.policy.IAePolicyReference#isLocalReference()
    */
   public boolean isLocalReference()
   {
      return AeUtil.isNullOrEmpty(getNamespaceURI());
   }

   /**
    * @see org.activebpel.rt.wsdl.def.policy.IAePolicyReference#getTargetNamespace()
    */
   public String getTargetNamespace()
   {
      return mTargetNamespace;
   }
   
   /**
    * @param aNamespace
    */
   public void setTargetNamespace(String aNamespace)
   {
      mTargetNamespace = aNamespace;
   }
}
