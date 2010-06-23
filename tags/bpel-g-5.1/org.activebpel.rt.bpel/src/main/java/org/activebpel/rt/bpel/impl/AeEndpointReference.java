// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeEndpointReference.java,v 1.41 2008/03/28 01:41:50 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLPolicyHelper;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.impl.endpoints.AeEndpointFactory;
import org.activebpel.rt.bpel.impl.endpoints.IAeEndpointDeserializer;
import org.activebpel.rt.bpel.impl.endpoints.IAeEndpointFactory;
import org.activebpel.rt.bpel.impl.endpoints.IAeEndpointSerializer;
import org.activebpel.rt.util.AeCompareXML;
import org.activebpel.rt.util.AeSOAPElementUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.activebpel.wsio.IAeWsAddressingConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The implementation of endpoint references for invoking and allowing
 * partner link invocations.
 */
public class AeEndpointReference implements IAeEndpointReference
{
   /** factory that gives us a means to parse endpoint references from xml */
   public static IAeEndpointFactory sEndpointFactory = new AeEndpointFactory();
   /** namespace we're using for identifying the credentials embedded in the endpoint properties */
   private static final String CREDENTIALS_NAMESPACE = "http://active-endpoints/endpoint-credentials"; //$NON-NLS-1$
   /** username qname */
   private static final QName CREDENTIALS_USER = new QName(CREDENTIALS_NAMESPACE, "username"); //$NON-NLS-1$
   /** password qname */
   private static final QName CREDENTIALS_PW = new QName( CREDENTIALS_NAMESPACE, "password"); //$NON-NLS-1$
   /** conversation id qname */
   private static final QName CONVERSATION_ID = new QName(IAeConstants.ABX_NAMESPACE_URI, "conversationId"); //$NON-NLS-1$
   /** SOAP mustUnderstand attribute */
   private static final String MUST_UNDERSTAND_ATTRIBUTE = "mustUnderstand"; //$NON-NLS-1$
   /** SOAP actor attribute */
   private static final String ACTOR_ATTRIBUTE = "actor"; //$NON-NLS-1$

   /** Required URI address of the endpoint reference. */
   private String mAddress;
   /** Optional list of properties required to identify resource. */
   private Map mProperties;
   /** Optional port type of the reference. */
   private QName mPortType;
   /** Optional specification of WSDL service element which defines endpoint. */
   private QName mServiceName;
   /** Optional name of service port used in conjunction with service name. */
   private String mServicePort;
   /** Optional list of policy elements used by endpoint. */
   private List mPolicies;
   /** Optional list of resolved policy elements used by endpoint. */
   private transient List mResolvedPolicies;
   /** Port type used to resolve policies cached on this instance */
   private QName mPolicyPortType;
   /** Operation used to resolve policies cached on this instance*/
   private String mPolicyOperation;
   /** The namespace of the endpoint's source xml or default if not set */
   private String mNamespace = IAeConstants.WSA_NAMESPACE_URI;
   /** List of extensibility element. */
   private List mExtElements;
   /** Optional list of reference property elements used by endpoint. */
   private List mRefProps = new ArrayList();
   /** Internal document used for importing reference properties, policies, etc */
   private Element mMetadata;

  /**
    * Default constructor for endpoint reference.
    */
   public AeEndpointReference()
   {
   }

   /**
    * Sets the data which defines the endpoint reference based upon the
    * given document fragment.
    * @param aRef An endpoint reference from which we are extracting the definition
    */
   public void setReferenceData(IAeWebServiceEndpointReference aRef)
   {
      try
      {
         // Clear any previous data we may be holding
         clearReferenceData();
         
         if (aRef == null)
         {
            return;
         }
         
         // set the source namespace
         mNamespace = aRef.getSourceNamespace();
         
         // Set the address and port for the reference
         if (AeUtil.isNullOrEmpty(aRef.getAddress()))
         {
            setAddress(getAnonymousRole());         
         }
         else
         {
            setAddress(aRef.getAddress());
         }
         
         mPortType = aRef.getPortType();
   
         // Set the service information
         mServiceName = aRef.getServiceName();
         mServicePort = aRef.getServicePort();
   
         // Add any properties the source endpoint reference contains
         for (Iterator iter=aRef.getProperties().keySet().iterator(); iter.hasNext();)
         {
            Object key = iter.next();
            addProperty((QName)key, (String)aRef.getProperties().get(key));
         }
   
         // Add reference property elements
         for (Iterator it = aRef.getReferenceProperties().iterator(); it.hasNext();) {
            addReferenceProperty((Element) it.next());
         }
   
         // Add any properties the source endpoint reference contains
         if(! AeUtil.isNullOrEmpty(aRef.getPolicies()))
         {
            for (Iterator iter=aRef.getPolicies().iterator(); iter.hasNext();)
               addPolicyElement((Element)iter.next());
         }
   
         // Add any extensibility elements the source endpoint reference contains
         for (Iterator iter=aRef.getExtensibilityElements(); iter.hasNext();) {
               addExtensibilityElement((Element)iter.next());
         }
      }
      catch (ConcurrentModificationException cme)
      {
         // this situation arises if partner links are not properly scoped and
         // you have 2 concurrent receives trying to update the same instance.
         // this is bad process design, but unfortunately we do not have a reliable 
         // of detecting this situation ahead of time without implementing some kind of flow analysis.
         throw new RuntimeException(AeMessages.getString("AeEndpointReference.0"));  //$NON-NLS-1$
      }
   }

   /**
    * Updates the data which defines the endpoint reference based upon the
    * given document fragment.
    * @param aRef An endpoint reference from which we are extracting the definition
    */
   public void updateReferenceData(IAeWebServiceEndpointReference aRef)
   {
      // Set the address and port for the reference
      setAddress(aRef.getAddress());
      mPortType = aRef.getPortType();

      // Set the service information
      mServiceName = aRef.getServiceName();
      mServicePort = aRef.getServicePort();

      // Add any properties the source endpoint reference contains
      for (Iterator iter=aRef.getProperties().keySet().iterator(); iter.hasNext();)
      {
         Object key = iter.next();
         addProperty((QName)key, (String)aRef.getProperties().get(key));
      }

      // Add reference property elements
      if( !AeUtil.isNullOrEmpty(aRef.getReferenceProperties()))
      {
         clearReferenceProperties();
         for (Iterator it = aRef.getReferenceProperties().iterator(); it.hasNext();) 
         {
            addReferenceProperty((Element) it.next());
         }
      }

      // Add any properties the source endpoint reference contains
      if(! AeUtil.isNullOrEmpty(aRef.getPolicies()))
      {
         clearPolicies();
         for (Iterator iter=aRef.getPolicies().iterator(); iter.hasNext();)
            addPolicyElement((Element)iter.next());
      }

      // Add any extensibility elements the source endpoint reference contains
      if( aRef.getExtensibilityElements().hasNext())
      {
         setExtensibilityElements(new ArrayList());
         for (Iterator iter=aRef.getExtensibilityElements(); iter.hasNext();) 
         {
            addExtensibilityElement((Element)iter.next());
         }
      }
   }


   /**
    * Sets the data which defines the endpoint reference based upon the
    * given document fragment.
    * @param aData An element which defines an endpoint reference
    */
   public void setReferenceData(Element aData) throws AeBusinessProcessException
   {
      // Clear any previous data we may be holding
      clearReferenceData();

      IAeEndpointDeserializer parser = sEndpointFactory.getDeserializer(aData.getNamespaceURI());
      parser.deserializeEndpoint(aData, this);
   }

   /**
    * Updates the data which defines the endpoint reference based upon the
    * given document fragment.
    * @param aData An element which defines an endpoint reference
    */
   public void updateReferenceData(Element aData) throws AeBusinessProcessException
   {
      IAeEndpointDeserializer parser = sEndpointFactory.getDeserializer(aData.getNamespaceURI());
      parser.deserializeEndpoint(aData, this);
   }

   /**
    * @return the URI for the wsa anonymous role
    */
   private String getAnonymousRole()
   {
      return mNamespace + IAeWsAddressingConstants.WSA_ANONYMOUS_ROLE;
   }

   /**
    * Returns the current representation of the endpoint reference as a document.
    */
   public Document toDocument()
   {
      IAeEndpointSerializer writer = sEndpointFactory.getSerializer(mNamespace);
      return writer.serializeEndpoint(this);
   }
   
   /**
    * @see java.lang.Object#clone()
    */
   public Object clone()
   {
      AeEndpointReference newRef = new AeEndpointReference();
      try
      {
         newRef.setReferenceData(toDocument().getDocumentElement());
         newRef.setProperties(new HashMap(getProperties()));
      }
      catch (AeBusinessProcessException ex)
      {
         AeException.logError(ex);
      }
      return newRef;
   }

   /**
    * Returns the URI address of the endpoint reference.
    */
   public String getAddress()
   {
      return AeUtil.getSafeString(mAddress);
   }

   /**
    * Returns the list of policies if any were specified. The list returned is
    * unmodifiable, and proper API should be used to modify the underlying collection.
    */
   public List getPolicies()
   {
      if (mPolicies == null)
         mPolicies = new ArrayList();
      return Collections.unmodifiableList(mPolicies);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeEndpointReference#clearPolicies()
    */
   public void clearPolicies()
   {
      mResolvedPolicies = null;
      if (mPolicies != null)
         mPolicies.clear();
   }

   /**
    * @see org.activebpel.rt.bpel.IAeEndpointReference#getEffectivePolicies(org.activebpel.rt.wsdl.IAeContextWSDLProvider)
    */
   public List getEffectivePolicies(IAeContextWSDLProvider aWSDLProvider)
   {
      return getEffectivePolicies(aWSDLProvider, null, null);
   }
   
   /**
    * Resolve policies if 
    * a) they haven't been resolved previously OR
    * b) the policies were resolved for a specific invoke that does not match the current request
    * 
    * @see org.activebpel.rt.bpel.IAeEndpointReference#getEffectivePolicies(org.activebpel.rt.wsdl.IAeContextWSDLProvider, javax.xml.namespace.QName, java.lang.String)
    */
   public List getEffectivePolicies(IAeContextWSDLProvider aWSDLProvider, QName aPortType, String aOperation)
   {
      if (mResolvedPolicies == null || !AeUtil.compareObjects(mPolicyPortType, aPortType) || !AeUtil.compareObjects(mPolicyOperation, aOperation)) 
      {
         mResolvedPolicies = AeWSDLPolicyHelper.getEffectiveOperationPolicies(aWSDLProvider, this, aPortType, aOperation, null);
         mPolicyPortType = aPortType;
         mPolicyOperation = aOperation;
      }
      return (mResolvedPolicies != null ? mResolvedPolicies : Collections.EMPTY_LIST);
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeEndpointReference#findPolicyElements(org.activebpel.rt.wsdl.IAeContextWSDLProvider, java.lang.String)
    */
   public List findPolicyElements(IAeContextWSDLProvider aWSDLProvider, String aPolicyName)
   {
      return findPolicyElements(aWSDLProvider, null, null, aPolicyName);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeEndpointReference#findPolicyElements(org.activebpel.rt.wsdl.IAeContextWSDLProvider, javax.xml.namespace.QName, java.lang.String, java.lang.String)
    */
   public List findPolicyElements(IAeContextWSDLProvider aWSDLProvider, QName aPortType, String aOperation,
         String aPolicyTagName)
   {
      List results = findPolicyElements(aWSDLProvider, aPortType, aOperation, new QName(IAeConstants.ABP_NAMESPACE_URI, aPolicyTagName));
      if (AeUtil.isNullOrEmpty(results))
         results = findPolicyElements(aWSDLProvider, aPortType, aOperation, new QName(IAeConstants.ABPEL_POLICY_NS, aPolicyTagName));
      
      return results;
   }   
   
   /**
    * Finds the policy elements for the given policy QName.  
    * @param aWSDLProvider The WSDL provider to use for resolving references
    * @param aPolicyName The policy QName
    */
   public List findPolicyElements(IAeContextWSDLProvider aWSDLProvider, QName aPortType, String aOperation, QName aPolicyName)
   {
      List results = new ArrayList();
      for(Iterator iter=getEffectivePolicies(aWSDLProvider, aPortType, aOperation).iterator(); iter.hasNext(); )
      {
         Element policyEl = (Element)iter.next();
         NodeList children = policyEl.getElementsByTagNameNS(aPolicyName.getNamespaceURI(), aPolicyName.getLocalPart());

         for(int i=0, len=children.getLength(); i < len; ++i)
            results.add(children.item(i));
      }
      
      return results;
   }
   
   /**
    * Returns the primary port type for the endpoint reference. This value
    * may be null.
    */
   public QName getPortType()
   {
      return mPortType;
   }

   /**
    * Returns the map of properties if any were specified.
    */
   public Map getProperties()
   {
      return (mProperties == null ? Collections.EMPTY_MAP : mProperties);
   }

   /**
    * Returns the name of the WSDL file which contains the definition of the
    * service element. This value may be null.
    */
   public QName getServiceName()
   {
      return mServiceName;
   }

   /**
    * Returns the port name of the service element. This value may be null.
    */
   public String getServicePort()
   {
      return mServicePort;
   }

   /**
    * Clears any reference specification data we may have set, if any.
    */
   protected void clearReferenceData()
   {
      setAddress(getAnonymousRole());
      mProperties  = null;
      mPortType    = null;
      mServicePort = null;
      mServiceName = null;
      mPolicies    = null;
      mResolvedPolicies = null;
      mExtElements = null;
      mRefProps = new ArrayList();
      mMetadata = null;
   }

   /**
    * Equality is determined by comparing all of the member data for the ER.
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObject)
   {
      if (aObject instanceof AeEndpointReference)
      {
         AeEndpointReference other = (AeEndpointReference) aObject;

         if ( ! ( AeUtil.compareObjects(other.getAddress(), getAddress()) &&
                  AeUtil.compareObjects(other.mProperties, mProperties) &&
                  AeUtil.compareObjects(other.mPortType, mPortType) &&
                  AeUtil.compareObjects(other.mServicePort, mServicePort) &&
                  AeUtil.compareObjects(other.mServiceName, mServiceName) &&
                  AeUtil.compareObjects(other.mPolicies, mPolicies) ) )
            return false;

         // Compare extensibility elements if any.
         if ( other.getExtensibilityElementsList().size() != getExtensibilityElementsList().size() )
            return false;

         AeCompareXML compare = new AeCompareXML();
         Iterator otherIter = other.getExtensibilityElements();
         Iterator thisIter = getExtensibilityElements();
         while ( otherIter.hasNext() )
         {
            Element otherElem = (Element)otherIter.next();
            Element thisElem =  (Element)thisIter.next();
            if ( ! compare.compareBothElements(otherElem, thisElem, "/") ) //$NON-NLS-1$
               return false;
         }
         return true;
      }
      return super.equals(aObject);
   }

   /**
    * Uses the address for the hashcode or the super's impl if the address is null.
    * The address can be null during the initial creation of the object, or when
    * the the ER is dynamic and the assign that populates it hasn't executed yet,
    * or during simulation.
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      if (mAddress != null)
         return mAddress.hashCode();
      return super.hashCode();
   }

   /**
    * Adds a property definition to the endpoint reference.
    * @param aKey the key for the property
    * @param aValue the value for the property
    */
   public void addProperty(QName aKey, String aValue)
   {
      if (mProperties == null)
         mProperties = new HashMap();

      mProperties.put(aKey, aValue);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeEndpointReference#getUsername()
    */
   public String getUsername()
   {
      return (String) getProperties().get(CREDENTIALS_USER);
   }

   /**
    * @see org.activebpel.rt.bpel.IAeEndpointReference#getPassword()
    */
   public String getPassword()
   {
      return (String) getProperties().get(CREDENTIALS_PW);
   }

   /**
    * @param aString
    */
   public void setAddress(String aString)
   {
      mAddress = aString;
   }

   /**
    * @param aName
    */
   public void setPortType(QName aName)
   {
      mPortType = aName;
   }

   /**
    * @param aMap
    */
   public void setProperties(Map aMap)
   {
      mProperties = aMap;
   }

   /**
    * @param aName
    */
   public void setServiceName(QName aName)
   {
      mServiceName = aName;
   }

   /**
    * @param aString
    */
   public void setServicePort(String aString)
   {
      mServicePort = aString;
   }

   /**
    * Setter for the source namespace that this endpoint was created from. This is
    * used if the endpoint gets serialized in order to preserve the original format.
    * @param aNamespace
    */
   public void setSourceNamespace(String aNamespace)
   {
      mNamespace = aNamespace;
   }

   /**
    * Get all the extensibility elements.
    * @return List extensibility elements.
    */
   private List getExtensibilityElementsList()
   {
      return mExtElements != null ? mExtElements : Collections.EMPTY_LIST;
   }

   /**
    * @see org.activebpel.wsio.IAeWebServiceEndpointReference#getExtensibilityElements()
    */
   public Iterator getExtensibilityElements()
   {
      return getExtensibilityElementsList().iterator();
   }

   /**
    * Setter for the list of extensibility elements.
    * @param aList
    */
   public void setExtensibilityElements(List aList)
   {
      mExtElements = aList;
   }

   /**
    * Add an extensibility element.
    * @param aExtElement the extensibility element to be added
    */
   public void addExtensibilityElement(Element aExtElement)
   {
      if (getExtensibilityElementsList() == Collections.EMPTY_LIST)
         setExtensibilityElements(new ArrayList());

      getExtensibilityElementsList().add(cloneElement(aExtElement));
   }

   /**
    * Implements method to add a policy for this endpoint reference.
    * @see org.activebpel.rt.bpel.IAeEndpointReference#addPolicyElement(org.w3c.dom.Element)
    */
   public void addPolicyElement(Element aPolicyElement)
   {
      if (mPolicies == null)
         mPolicies = new ArrayList();
      mPolicies.add(cloneElement(aPolicyElement));
   }

   /**
    * Implements method to add a reference property for this endpoint reference.
    * @see org.activebpel.rt.bpel.IAeEndpointReference#addReferenceProperty(org.w3c.dom.Element)
    */
   public void addReferenceProperty(Element aRefElement)
   {
      if (mRefProps == null)
         mRefProps = new ArrayList();

      // create a clone of the element to detach from original parent
      Element element = cloneElement(aRefElement);
      
      // backward compatibility support for credentials passed in reference properties
      QName elementName = new QName(element.getNamespaceURI(), element.getLocalName());
      if (CREDENTIALS_USER.equals(elementName))
      {
         addProperty(elementName, AeXmlUtil.getText(element));
      }
      else if (CREDENTIALS_PW.equals(elementName))
      {
         addProperty(elementName, AeXmlUtil.getText(element));
      }
      else if (CONVERSATION_ID.equals(elementName))
      {
         // grab the conversation id and set it as a property for easy access
         addProperty(elementName, AeXmlUtil.getText(element));
      }
      
      // Certain SOAPHeaderElement implementations handle these attributes 
      // separately from the other attributes and frequently cause problems when 
      // serialized to and from SOAPHeaders
      String actor = element.getAttribute(ACTOR_ATTRIBUTE);
      if (!AeUtil.isNullOrEmpty(actor))
      {
         element.removeAttribute(ACTOR_ATTRIBUTE);
         element.setAttributeNS(IAeConstants.SOAP_NAMESPACE_URI, ACTOR_ATTRIBUTE, actor);
      }
      String mustUnderstand = element.getAttribute(MUST_UNDERSTAND_ATTRIBUTE);
      if (!AeUtil.isNullOrEmpty(mustUnderstand))
      {
         element.removeAttribute(MUST_UNDERSTAND_ATTRIBUTE);
         element.setAttributeNS(IAeConstants.SOAP_NAMESPACE_URI, MUST_UNDERSTAND_ATTRIBUTE, mustUnderstand);
      }
      mRefProps.add(element);
   }
   
   /**
    * Creates a detached copy of the element as a DOM element
    * 
    * @param aElement
    * @return copy of element
    */
   protected Element cloneElement(Element aElement)
   {
      if (aElement instanceof SOAPElement)
      {
         return AeSOAPElementUtil.convertToDOM((SOAPElement) aElement, getMetadataElement());
      }
      else
      {
         return AeXmlUtil.cloneElement(aElement, getMetadataElement().getOwnerDocument());         
      }
   }

   /**
    * Gets an Iterator of all reference property elements.
    * @return Iterator for reference property elements.
    */
   public List getReferenceProperties()
   {
      return mRefProps;
   }

   /**
    * Setter for the list of reference property elements.
    */
   public void clearReferenceProperties()
   {
      // clear old list
      getReferenceProperties().clear();
   }

   /** 
    * Overrides method to 
    * @see org.activebpel.wsio.IAeWebServiceEndpointReference#getSourceNamespace()
    */
   public String getSourceNamespace()
   {
      return mNamespace;
   }
   
   /**
    * @return the internal metadata element used as a parent for reference parameters, policy, etc
    */
   public Element getMetadataElement()
   {
      if (mMetadata == null)
      {
         Document doc = AeXmlUtil.newDocument();
         mMetadata = AeXmlUtil.addElementNS(doc, getSourceNamespace(), "Metadata"); //$NON-NLS-1$
         mMetadata.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:wsa", getSourceNamespace()); //$NON-NLS-1$
         mMetadata.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:wsp", IAeConstants.WSP_NAMESPACE_URI); //$NON-NLS-1$
         mMetadata.setAttributeNS(IAeConstants.W3C_XMLNS, "xmlns:abp", IAeConstants.ABP_NAMESPACE_URI); //$NON-NLS-1$
      }
      return mMetadata;
   }
}