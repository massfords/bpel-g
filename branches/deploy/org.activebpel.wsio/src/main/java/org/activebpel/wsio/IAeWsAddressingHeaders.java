//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/IAeWsAddressingHeaders.java,v 1.3 2008/02/17 22:01:32 mford Exp $
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
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;


/**
 * Represents the values of a set of WS-Addressing header elements
 * from a SOAP Envelope
 */
public interface IAeWsAddressingHeaders extends Serializable
{
   /** Header element names */ 
   public static final String WSA_RELATES_TO = "RelatesTo"; //$NON-NLS-1$
   public static final String WSA_MESSAGE_ID = "MessageID"; //$NON-NLS-1$
   public static final String WSA_ACTION = "Action"; //$NON-NLS-1$
   public static final String WSA_FAULT_TO = "FaultTo"; //$NON-NLS-1$
   public static final String WSA_REPLY_TO = "ReplyTo"; //$NON-NLS-1$
   public static final String WSA_FROM = "From"; //$NON-NLS-1$
   public static final String WSA_TO = "To"; //$NON-NLS-1$
   public static final String WSA_RECIPIENT = "Recipient"; //$NON-NLS-1$   
   public static final String WSA_ADDRESS = "Address"; //$NON-NLS-1$
   public static final String ABX_CONVERSATION_ID = "conversationId"; //$NON-NLS-1$
   
   /**
    * @return Returns the messageId URI as a string.
    */
   public String getMessageId();

   /**
    * @param aMessageId The messageId URI string to set.
    */
   public void setMessageId(String aMessageId);

   /**
    * @return Returns the Action URI.
    */
   public String getAction();

   /**
    * @param aAction The Action URI to set.
    */
   public void setAction(String aAction);

   /**
    * @return Returns the FaultTo endpoint.
    */
   public IAeWebServiceEndpointReference getFaultTo();

   /**
    * @param aFaultTo The FaultTo endpoint to set.
    */
   public void setFaultTo(IAeWebServiceEndpointReference aFaultTo);

   /**
    * @return Returns the From endpoint.
    */
   public IAeWebServiceEndpointReference getFrom();

   /**
    * @param aFrom The From endpoint to set.
    */
   public void setFrom(IAeWebServiceEndpointReference aFrom);

   /**
    * @return Returns collection of message id's related to this message, 
    * indexed by the wsa RelationshipType QName.
    */
   public Map getRelatesTo();
   
   /**
    * @param aRelatesTo The collection of RelationshipType QName/MessageId pairs to set.
    */
   public void setRelatesTo(Map aRelatesTo);

   /**
    * Adds a MessageId to the RelatesTo collection, identified by 
    * the qualified name of the relationship type
    * @param aMessageId message id of related message 
    * @param aRelationship relationship type as defined by wsa
    */
   public void addRelatesTo(String aMessageId, QName aRelationship);

   /**
    * @return Returns the MessageId for a given RelationshipType QName.
    */
   public String getRelatesTo(QName aRelation);
      
   /**
    * @return Returns the ReplyTo endpoint.
    */
   public IAeWebServiceEndpointReference getReplyTo();

   /**
    * @param aReplyTo The ReplyTo endpoint to set.
    */
   public void setReplyTo(IAeWebServiceEndpointReference aReplyTo);

   /**
    * @return Returns address of the To destination.
    */
   public String getTo();

   /**
    * @param aAddress Sets the value of the To address.
    */
   public void setTo(String aAddress);
   
   /**
    * @return Returns additional elements (non-WSA) to serialize as headers
    */
   public List getReferenceProperties();
   
   /**
    * @param aNamespace The WSA namespace to use.
    */
   public void setSourceNamespace(String aNamespace);
   
   /**
    * @returns the WSA namespace to use.
    */
   public String getSourceNamespace();
   
   /**
    * Gets the endpoint reference that was invoked
    */
   public IAeWebServiceEndpointReference getRecipient();

   /**
    * Sets the Recipient as an endpoint reference. 
    */
   public void setRecipient(IAeWebServiceEndpointReference aEndpoint);
   
   /**
    * Sets the value for the abx:conversationId
    * @param aId
    */
   public void setConversationId(String aId);
   
   /**
    * Gets the value of the abx:conversationId 
    */
   public String getConversationId();
}