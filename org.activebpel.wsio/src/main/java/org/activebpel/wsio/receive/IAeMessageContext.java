//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/receive/IAeMessageContext.java,v 1.12 2007/01/26 22:43:51 KPease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio.receive;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.activebpel.wsio.IAeWsAddressingHeaders;

/**
 * Provides the context for an inbound message to the bpel engine.
 */
public interface IAeMessageContext extends Serializable
{
   /**
    * Gets the name of the process being invoked.
    * Should return a null value if getServiceName returns the
    * process service name.
    */
   public QName getProcessName();

   /**
    * Return the service name of the process being invoked.
    * Should return a null value if getProcessName returns
    * the process QName.
    */
   public String getServiceName();

   /**
    * Gets the name of the partner link that the request arrived on.
    */
   public String getPartnerLink();

   /**
    * Gets the name of the operation that the request invoked.
    */
   public String getOperation();

   /**
    * Gets the authenticated principal value or null if the request wasn't authenticated.
    */
   public String getPrincipal();

   /**
    * Gets the embedded endpoint reference that should be used for any asynchronous replies
    * on this partner link. This is an optional value and is only used if the endpoint
    * reference source for the partner link is declared as "invoker" in the PDD.
    * @deprecated Deprecated. Use  getPartnerEndpointReference() instead.
    */
   public IAeWebServiceEndpointReference getEmbeddedEndpointReference();

   /**
    * Gets the embedded endpoint reference that should be used for any asynchronous replies
    * on this partner link. This is an optional value and is only used if the endpoint
    * reference source for the partner link is declared as "invoker" in the PDD.
    * @deprecated Deprecated. Use  getWsAddressingHeaders().getReplyTo() instead.
    */
   public IAeWebServiceEndpointReference getPartnerEndpointReference();

   /**
    * Gets the endpoint reference that was invoked or null if not set.
    * @deprecated Deprecated. Use  getWsAddressingHeaders().getRecipient() instead.
    */
   public IAeWebServiceEndpointReference getMyEndpointReference();

   /**
    * Gets the version of the process that the message is targeting. A null or
    * empty string indicates that the message should be routed to the current
    * version of the process.
    */
   public String getProcessVersion();

   /**
    * Accessor for name/value properties that will be passed to the business
    * process instance.
    */
   public Map getBusinessProcessProperties();

   /**
    * Gets an Iterator of all reference property elements associated with the inbound message.
    * @return Iterator for reference property elements.
    */
   public Iterator getReferenceProperties();
   
   /**
    * Gets collected WS-Addressing headers associated with the inbound message.
    * @return container for WSA headers.
    */
   public IAeWsAddressingHeaders getWsAddressingHeaders();
   
   /**
    * @return name of the intended receive handler
    */
   public String getReceiveHandler();
}
