//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeQSWSDLHandler.java,v 1.2 2006/04/13 21:33:42 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.axis.bpel.deploy.AeResourceProvider;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Constants;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.transport.http.QSWSDLHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Custom version of Query String Handler for serving up WSDL needed to work around
 * issue in current Axis implementation. Axis implementation was making call to get 
 * service names which internally get the service descriptors (overkill for the need).
 * The real problem is that while this method is getting the service descs it tries to
 * fixup the imports and if the import refers to a location that this handler is serving 
 * up we get into a nasty dependency. We actually only need to take over the getDeployedServiceNames
 * method, but because it was private in Axis, we had to take over a little more.
 */
public class AeQSWSDLHandler extends QSWSDLHandler
{
   /**
    * Override method call so we don't get errors during reflection invocation of method.
    * @see org.apache.axis.transport.http.QSHandler#invoke(org.apache.axis.MessageContext)
    */
   public void invoke(MessageContext msgContext) throws AxisFault
   {
      super.invoke(msgContext);
   }

   /**
    * Updates the soap:address locations for all ports in the WSDL using the URL from the request as the base
    * portion for the updated locations, ensuring the WSDL returned to the client contains the correct
    * location URL.
    * @param wsdlDoc the WSDL as a DOM document
    * @param msgContext the current Axis JAX-RPC message context
    * @throws AxisFault if we fail to obtain the list of deployed service names from the server config
    */
   protected void updateSoapAddressLocationURLs(Document wsdlDoc, MessageContext msgContext) throws AxisFault
   {
      Set deployedServiceNames;
      try
      {
         deployedServiceNames = getDeployedServiceNames(msgContext);
      }
      catch (ConfigurationException ce)
      {
         throw new AxisFault("Failed to determine deployed service names.", ce); //$NON-NLS-1$
      }

      NodeList wsdlPorts = wsdlDoc.getDocumentElement().getElementsByTagNameNS(Constants.NS_URI_WSDL11, "port"); //$NON-NLS-1$
      if (wsdlPorts != null)
      {
         String endpointURL = getEndpointURL(msgContext);
         String baseEndpointURL = endpointURL.substring(0, endpointURL.lastIndexOf("/") + 1); //$NON-NLS-1$
         for (int i = 0; i < wsdlPorts.getLength(); i++)
         {
            Element portElem = (Element)wsdlPorts.item(i);
            Node portNameAttrib = portElem.getAttributes().getNamedItem("name"); //$NON-NLS-1$
            if (portNameAttrib == null)
               continue;
            
            String portName = portNameAttrib.getNodeValue();
            NodeList soapAddresses = portElem.getElementsByTagNameNS(Constants.URI_WSDL11_SOAP, "address"); //$NON-NLS-1$
            if (soapAddresses == null || soapAddresses.getLength() == 0)
               soapAddresses = portElem.getElementsByTagNameNS(Constants.URI_WSDL12_SOAP, "address"); //$NON-NLS-1$
            
            if (soapAddresses != null)
            {
               for (int j = 0; j < soapAddresses.getLength(); j++)
               {
                  Element addressElem = (Element)soapAddresses.item(j);
                  Node addressLocationAttrib = addressElem.getAttributes().getNamedItem("location"); //$NON-NLS-1$
                  if (addressLocationAttrib == null)
                     continue;
                  
                  String addressLocation = addressLocationAttrib.getNodeValue();
                  String addressServiceName = addressLocation.substring(addressLocation.lastIndexOf("/") + 1); //$NON-NLS-1$
                  String newServiceName = getNewServiceName(deployedServiceNames, addressServiceName, portName);
                  if (newServiceName != null)
                  {
                     String newAddressLocation = baseEndpointURL + newServiceName;
                     addressLocationAttrib.setNodeValue(newAddressLocation);
                     log.debug("Setting soap:address location values in WSDL for port " + //$NON-NLS-1$
                           portName + " to: " + //$NON-NLS-1$
                           newAddressLocation);
                  }
                  else
                  {
                     log.debug("For WSDL port: " + portName + ", unable to match port name or the last component of " + //$NON-NLS-1$ //$NON-NLS-2$
                                 "the SOAP address url with a " + //$NON-NLS-1$
                                 "service name deployed in server-config.wsdd.  Leaving SOAP address: " + //$NON-NLS-1$
                                 addressLocation + " unmodified."); //$NON-NLS-1$
                  }
               }
            }
         }
      }
   }

   /**
    * Returns new service name given current deployed services and endpoint/port name. 
    * @param deployedServiceNames
    * @param currentServiceEndpointName
    * @param portName
    */
   private String getNewServiceName(Set deployedServiceNames, String currentServiceEndpointName, String portName)
   {
      String endpointName = null;
      if (deployedServiceNames.contains(currentServiceEndpointName))
         endpointName = currentServiceEndpointName;
      else if (deployedServiceNames.contains(portName))
         endpointName = portName;
      return endpointName;
   }

   /**
    * This is the only method from the Axis impl that needs to be taken over. This class
    * would be much smaller if the base method was not private.
    * @param msgContext
    * @throws ConfigurationException
    */
   private Set getDeployedServiceNames(MessageContext msgContext) throws ConfigurationException
   {
      Set serviceNames = new HashSet();

      EngineConfiguration config = msgContext.getAxisEngine().getConfig();
      if (config instanceof AeResourceProvider)
      {
         WSDDService[] services = ((AeResourceProvider)config).getMyDeployment().getServices();
         for (int i = 0; i < services.length; i++)
            serviceNames.add(services[i].getServiceDesc().getName());
      }
      else
      {
         for (Iterator iter = config.getDeployedServices(); iter.hasNext();)
            serviceNames.add(((ServiceDesc)iter.next()).getName());
      }

      return serviceNames;
   }
}