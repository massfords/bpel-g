//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/receive/AeMessageContext.java,v 1.15 2008/02/17 22:01:32 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio.receive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.wsio.AeMessages;
import org.activebpel.wsio.AeWsAddressingHeaders;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.activebpel.wsio.IAeWsAddressingConstants;
import org.activebpel.wsio.IAeWsAddressingHeaders;
import org.w3c.dom.Element;


/**
 * Provides a context for an incoming message to the bpel engine.
 */
public class AeMessageContext implements IAeMessageContext
{
   // error constants
   private static final String CANNOT_SET_SERVICENAME = "AeMessageContext.ERROR_2"; //$NON-NLS-1$
   private static final String CANNOT_SET_PROCESSNAME = "AeMessageContext.ERROR_1"; //$NON-NLS-1$

   /** name of the process being hit */
   private QName mProcessName;
   /** the service name. */
   private String mServiceName;
   /** name of the partner link being hit */
   private String mPartnerLink;
   /** name of the operation being invoked */
   private String mOperation;
   /** authenticated principal or null if none */
   private String mPrincipal;
   /** version number that the message is intended for or null for the current version */
   private String mProcessVersion;
   /** container for the business process properties */
   private Map mBusinessProcessProperties = new HashMap();
   /** container for the message reference properties */
   private List mRefProps = new ArrayList();
   /** Collected WS-Addressing Headers from inbound message */
   private IAeWsAddressingHeaders mWsaHeaders;
   /** Name of the intended receive side handler */
   private String mReceiveHandler;

   /**
    * No-arg ctor
    */
   public AeMessageContext()
   {
   }

   /**
    * Creates a message context with the minimum information required for message delivery.
    *
    * @param aPartnerLink
    * @param aOperation
    */
   public AeMessageContext(String aPartnerLink, String aOperation)
   {
      setPartnerLink(aPartnerLink);
      setOperation(aOperation);
   }

   /**
    * Copy constructor
    * @param aContext
    */
   public AeMessageContext(IAeMessageContext aContext)
   {
      Map map = aContext.getBusinessProcessProperties();
      if (map != null)
         mBusinessProcessProperties.putAll(map);

      setWsAddressingHeaders(aContext.getWsAddressingHeaders());
      setOperation(aContext.getOperation());
      setPartnerLink(aContext.getPartnerLink());
      setPrincipal(aContext.getPrincipal());
      setProcessName(aContext.getProcessName());
      setProcessVersion(aContext.getProcessVersion());
      setServiceName(aContext.getServiceName());
   }

   /**
    * Convenience method for converting the message context from the interface
    * to our impl. Uses the copy constructor if the interface is not our impl.
    *
    * @param aContext
    */
   public static AeMessageContext convert(IAeMessageContext aContext)
   {
      if (aContext instanceof AeMessageContext)
         return (AeMessageContext) aContext;
      else
         return new AeMessageContext(aContext);
   }

   /**
    * @see org.activebpel.wsio.receive.IAeMessageContext#getProcessName()
    */
   public QName getProcessName()
   {
      return mProcessName;
   }

   /**
    * @see org.activebpel.wsio.receive.IAeMessageContext#getOperation()
    */
   public String getOperation()
   {
      return mOperation;
   }

   /**
    * @see org.activebpel.wsio.receive.IAeMessageContext#getPrincipal()
    */
   public String getPrincipal()
   {
      return mPrincipal;
   }

   /**
    * @deprecated Replaced with getPartnerEndpointReference() method.
    * @see org.activebpel.wsio.receive.IAeMessageContext#getEmbeddedEndpointReference()
    */
   public IAeWebServiceEndpointReference getEmbeddedEndpointReference()
   {
      return getPartnerEndpointReference();
   }

   /**
    * @deprecated Replaced with getWsAddressingHeaders.getReplyTo() method.    
    * @see org.activebpel.wsio.receive.IAeMessageContext#getPartnerEndpointReference()
   */ 
   public IAeWebServiceEndpointReference getPartnerEndpointReference()
   {
      return getWsAddressingHeaders().getReplyTo();
   }

   /**
    * @deprecated Replaced with getWsAddressingHeaders.getRecipient() method.    
    * @see org.activebpel.wsio.receive.IAeMessageContext#getMyEndpointReference()
   */ 
   public IAeWebServiceEndpointReference getMyEndpointReference()
   {
      return getWsAddressingHeaders().getRecipient();
   }
  
   /**
    * @deprecated please use getWsAddressingHeaders().setRecipient() instead
    * @param aRef The endpoint to set.
   */
   public void setMyEndpointReference(IAeWebServiceEndpointReference aRef)
   {
      getWsAddressingHeaders().setRecipient(aRef);
   }
  
   /**
    * @deprecated use getWsAddressingHeaders().setReplyTo() instead
    * @param aEndpoint The endpoint to set.
    */
   public void setEmbeddedEndpointReference(IAeWebServiceEndpointReference aEndpoint)
   {
      setPartnerEndpointReference(aEndpoint);
   }

   /**
    * @deprecated please use getWsAddressingHeaders().setReplyTo() instead
    * Sets the partner (embedded) endpoint reference.
    * @param aEndpoint
    */
   public void setPartnerEndpointReference(IAeWebServiceEndpointReference aEndpoint)
   {
      getWsAddressingHeaders().setReplyTo(aEndpoint);
   }

   /**
    * @param aOperation The operation to set.
    */
   public void setOperation(String aOperation)
   {
      mOperation = aOperation;
   }

   /**
    * @param aPrincipal The principal to set.
    */
   public void setPrincipal(String aPrincipal)
   {
      mPrincipal = aPrincipal;
   }

   /**
    * @param aProcessName The processName to set.
    */
   public void setProcessName(QName aProcessName)
   {
      if( getServiceName() != null )
      {
         throw new IllegalArgumentException( AeMessages.format( CANNOT_SET_PROCESSNAME, getServiceName() ) ); 
      }
      mProcessName = aProcessName;
   }

   /**
    * @see org.activebpel.wsio.receive.IAeMessageContext#getProcessVersion()
    */
   public String getProcessVersion()
   {
      return mProcessVersion;
   }

   /**
    * Setter for the process version.
    *
    * @param aProcessVersion
    */
   public void setProcessVersion(String aProcessVersion)
   {
      mProcessVersion = aProcessVersion;
   }

   /**
    * @see org.activebpel.wsio.receive.IAeMessageContext#getBusinessProcessProperties()
    */
   public Map getBusinessProcessProperties()
   {
      return mBusinessProcessProperties;
   }

   /**
    * Setter for properties that will be passed to the business process instance.
    * @param aName
    * @param aValue
    */
   public void setBusinessProcessProperty( String aName, String aValue )
   {
      getBusinessProcessProperties().put( aName, aValue );
   }

   /**
    * @see org.activebpel.wsio.receive.IAeMessageContext#getServiceName()
    */
   public String getServiceName()
   {
      return mServiceName;
   }

   /**
    * Set the service name.
    * @param aServiceName
    */
   public void setServiceName( String aServiceName )
   {
      if( aServiceName != null && getProcessName() != null )
      {
         throw new IllegalArgumentException( AeMessages.format(CANNOT_SET_SERVICENAME, getProcessName().toString() ) ); 
      }
      mServiceName = aServiceName;
   }

   /**
    * Implements method to add a reference property for this endpoint reference.
    */
   public void addReferenceProperty(Element aRefElement)
   {
      if (mRefProps == null)
         mRefProps = new ArrayList();
      mRefProps.add(aRefElement);
   }

   /**
    * Gets an Iterator of all reference property elements.
    * @return Iterator for reference property elements.
    */
   public Iterator getReferenceProperties()
   {
      return mRefProps != null ? mRefProps.iterator() : null;

   }

   /**
    * Setter for the list of reference property elements.
    * @param aList
    */
   public void setReferenceProperties(List aList)
   {
      mRefProps = aList;
   }

   /**
    * @see org.activebpel.wsio.receive.IAeMessageContext#getPartnerLink()
    */
   public String getPartnerLink()
   {
      return mPartnerLink;
   }

   /**
    * @param aPartnerLink The partnerLink to set.
    */
   public void setPartnerLink(String aPartnerLink)
   {
      mPartnerLink = aPartnerLink;
   }

   /**
    * @return Returns the wsaHeaders.
    */
   public IAeWsAddressingHeaders getWsAddressingHeaders()
   {
      if (mWsaHeaders == null)
      {
         setWsAddressingHeaders(new AeWsAddressingHeaders(IAeWsAddressingConstants.WSA_NAMESPACE_URI));
      }
      return mWsaHeaders;
   }

   /**
    * @param aWsaHeaders The wsaHeaders to set.
    */
   public void setWsAddressingHeaders(IAeWsAddressingHeaders aWsaHeaders)
   {
      mWsaHeaders = aWsaHeaders;
   }

   /**
    * @param aReceiveHandler
    */
   public void setReceiveHandler(String aReceiveHandler)
   {
      mReceiveHandler = aReceiveHandler;
   }
   
   /**
    * @see org.activebpel.wsio.receive.IAeMessageContext#getReceiveHandler()
    */
   public String getReceiveHandler()
   {
      return mReceiveHandler;
   }
}
