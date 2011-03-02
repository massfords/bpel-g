//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/IAeWebServiceEndpointReference.java,v 1.6 2006/09/26 15:03:07 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * Partner endpoint reference data.
 */
public interface IAeWebServiceEndpointReference extends Serializable
{
   /**
    * Returns the list of policy elements if any were specified. 
    */
   public List getPolicies();

   /**
    * Returns an Iterator of extensibility elements defined here.
    * @return Iterator the extensibility elements.
    */
   public Iterator getExtensibilityElements();

   /**
    * Returns the name of the WSDL file which contains the definition of the
    * service element. This value may be null.
    */
   public QName getServiceName();

   /**
    * Returns the port name of the service element. This value may be null.
    */
   public String getServicePort();
   
   /**
    * Returns the username to set on the call object or null if not set
    */
   public String getUsername();
   
   /**
    * Returns the password to set on the call object or null if not set
    */
   public String getPassword();
   
   /**
    * Returns the map of properties if any were specified. 
    */
   public Map getProperties();

   /**
    * Returns the address for the endpoint.
    */
   public String getAddress();

   /**
    * Returns the port type for the endpoint.
    */
   public QName getPortType();
   
   /**
    * Gets an Iterator of all reference property elements.
    * @return Iterator for reference property elements.
    */
   public List getReferenceProperties();
   
   /**
    * @return the wsa namespace uri
    */
   public String getSourceNamespace();
   
}
