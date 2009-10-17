/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import java.io.Serializable;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Describes the interface used for interacting with business processes. This
 * extends {@link org.activebpel.wsio.IAeWebServiceEndpointReference} to add
 * convenience methods but should not add additional data.
 */
public interface IAeEndpointReference extends IAeWebServiceEndpointReference, Serializable
{
   /**
    * Returns the current representation of the endpoint reference as a document
    * fragment.
    */
   public Document toDocument();
   
   /**
    * Clears any policy references which this endpoint is holding.
    */
   public void clearPolicies();

   /**
    * Returns a {@link List} of resolved policy elements if any were specified.
    * 
    * If service information is provided on the endpoint, Resolves WSDL policy attached 
    * to the service and binding as well as direct references on the endpoint itself
    * 
    * @param aWSDLProvider The WSDL provider used to resolve references 
    */
   public List getEffectivePolicies(IAeContextWSDLProvider aWSDLProvider);

   /**
    * Returns a {@link List} of resolved policy elements for an invoke 
    * if any were specified.
    * 
    * If service information is provided on the endpoint, Resolves WSDL policy attached 
    * to the service and binding as well as direct references on the endpoint itself
    * 
    * WSDL Policy attachments at the operation and message level are also resolved
    * 
    * @param aWSDLProvider The WSDL provider used to resolve references 
    */
   public List getEffectivePolicies(IAeContextWSDLProvider aWSDLProvider, QName aPortType, String aOperation);
   
   /**
    * Returns a {@link List} of {@link Element} objects which match the given policy tag name.
    * An empty list will be returned if not matches are found.
    * @param aPolicyTagName The name of the 
    */
   public List findPolicyElements(IAeContextWSDLProvider aWSDLProvider, String aPolicyTagName);

   /**
    * Returns a {@link List} of {@link Element} objects which match the given policy tag name.
    * An empty list will be returned if not matches are found.
    * @param aPolicyTagName The name of the 
    */
   public List findPolicyElements(IAeContextWSDLProvider aWSDLProvider, QName aPortType, String aOperation, String aPolicyTagName);
   
   /**
    * Sets the data which defines the endpoint reference based upon the
    * given element.
    * @param aData An element which defines an endpoint reference
    */
   public void setReferenceData(Element aData) throws AeBusinessProcessException;

   /**
    * Sets the data which defines the endpoint reference based upon the
    * given endpoint reference.
    * @param aReference the endpoint reference
    */
   public void setReferenceData(IAeWebServiceEndpointReference aReference);

   /**
    * Updates the data which defines the endpoint reference based upon the
    * given element.
    * @param aData An element which defines an endpoint reference
    */
   public void updateReferenceData(Element aData) throws AeBusinessProcessException;

   /**
    * Updates the data which defines the endpoint reference based upon the
    * given endpoint reference.
    * @param aReference An endpoint reference
    */
   public void updateReferenceData(IAeWebServiceEndpointReference aReference);

   /**
    * Add an extensibility element.
    * @param aExtElement the extensibility element to be added
    */
   public void addExtensibilityElement(Element aExtElement);

   /**
    * Add an Policy element.
    * @param aPolicyElement the policy element to be added
    */
   public void addPolicyElement(Element aPolicyElement);

   /**
    * Adds a property to the reference properties
    * @param key
    * @param aString
    */
   public void addProperty(QName key, String aString);

   /**
    * Setter for the address
    * @param aString
    */
   public void setAddress(String aString);

   /**
    * Setter for the porttype
    * @param aName
    */
   public void setPortType(QName aName);

   /**
    * Setter for the service port
    * @param portName
    */
   public void setServicePort(String portName);

   /**
    * Setter for the service name
    * @param aName
    */
   public void setServiceName(QName aName);

   /**
    * Setter for the source namespace that this endpoint was created from. This is
    * used if the endpoint gets serialized in order to preserve the original format.
    * @param aNamespace
    */
   public void setSourceNamespace(String aNamespace);

   /**
    * Adds a property to the reference properties
    * @param aElement
    */
   public void addReferenceProperty(Element aElement);

   /**
    * Gets an Iterator of all reference property elements.
    * @return Iterator for reference property elements.
    */
   public List getReferenceProperties();
   
   /**
    * Called to clone the endpoint reference.
    */
   public Object clone();
}