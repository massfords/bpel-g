//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/AeWsAddressingHeaders.java,v 1.4 2008/02/17 22:01:32 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 *  Holder for the values from a set of WS-Addressing Headers
 */
public class AeWsAddressingHeaders implements IAeWsAddressingHeaders
{
   /** Action URI */
   private String mAction;
   /** Message ID URI */
   private String mMessageId;
   /** Collection of RelationshipType QName/MessageId pairs */
   private HashMap mRelatesTo = new HashMap();
   /** From endpoint */
   private IAeWebServiceEndpointReference mFrom;
   /** ReplyTo endpoint */
   private IAeWebServiceEndpointReference mReplyTo;
   /** FaultTo endpoint */
   private IAeWebServiceEndpointReference mFaultTo;
   /** Endpoint containing To address URI */
   private IAeWebServiceEndpointReference mTo;
   /** List of additional header elements to serialize */
   private List mReferenceProperties;
   /** WSA Namespace URI */ 
   private String mNamespace;   
   /** WSA To URI */ 
   private String mToURI;   
   /** abx:conversationId */
   private String mConversationId;
   
   /**
    * Constructor 
    * @param aNamespace WS-Addressing namespace URI for this instance
    */
   public AeWsAddressingHeaders(String aNamespace)
   {
      setSourceNamespace(aNamespace);
   }
   
   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getMessageId()
    */
   public String getMessageId()
   {
      return mMessageId;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setMessageId(java.lang.String)
    */
   public void setMessageId(String aMessageId)
   {
      mMessageId = aMessageId;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getAction()
    */
   public String getAction()
   {
      return mAction;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setAction(java.lang.String)
    */
   public void setAction(String aAction)
   {
      mAction = aAction;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getFaultTo()
    */
   public IAeWebServiceEndpointReference getFaultTo()
   {
      return mFaultTo;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setFaultTo(org.activebpel.wsio.IAeWebServiceEndpointReference)
    */
   public void setFaultTo(IAeWebServiceEndpointReference aFaultTo)
   {
      mFaultTo = aFaultTo;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getFrom()
    */
   public IAeWebServiceEndpointReference getFrom()
   {
      return mFrom;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setFrom(org.activebpel.wsio.IAeWebServiceEndpointReference)
    */
   public void setFrom(IAeWebServiceEndpointReference aFrom)
   {
      mFrom = aFrom;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getRelatesTo()
    */
   public Map getRelatesTo()
   {
      return mRelatesTo;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getRelatesTo(javax.xml.namespace.QName)
    */
   public String getRelatesTo(QName aRelation)
   {
      return (String) mRelatesTo.get(aRelation);
   }
   
   
   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setRelatesTo(java.util.Map)
    */
   public void setRelatesTo(Map aRelatesTo)
   {
      mRelatesTo = (HashMap) aRelatesTo;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#addRelatesTo(java.lang.String, javax.xml.namespace.QName)
    */
   public void addRelatesTo(String aMessageId, QName aRelation)
   {
      getRelatesTo().put(aRelation, aMessageId);
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getReplyTo()
    */
   public IAeWebServiceEndpointReference getReplyTo()
   {
      return mReplyTo;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setReplyTo(org.activebpel.wsio.IAeWebServiceEndpointReference)
    */
   public void setReplyTo(IAeWebServiceEndpointReference aReplyTo)
   {
      mReplyTo = aReplyTo;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getTo()
    */
   public String getTo()
   {
      return mToURI;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setTo(java.lang.String)
    */
   public void setTo(String aTo)
   {
      mToURI = aTo;
   }
   
   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setSourceNamespace(java.lang.String)
    */
   public void setSourceNamespace(String aNamespace)
   {
      mNamespace = aNamespace;      
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getSourceNamespace()
    */
   public String getSourceNamespace()
   {
      return mNamespace;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getRecipient()
    */
   public IAeWebServiceEndpointReference getRecipient()
   {
      return mTo;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setRecipient(org.activebpel.wsio.IAeWebServiceEndpointReference)
    */
   public void setRecipient(IAeWebServiceEndpointReference aEndpoint)
   {
      mTo = aEndpoint;
   }
   
   
   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getReferenceProperties()
    */
   public List getReferenceProperties()
   {
      if (mReferenceProperties == null)
      {
         mReferenceProperties = new ArrayList();
      }
      return mReferenceProperties;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#setConversationId(java.lang.String)
    */
   public void setConversationId(String aId)
   {
      mConversationId = aId;
   }

   /**
    * @see org.activebpel.wsio.IAeWsAddressingHeaders#getConversationId()
    */
   public String getConversationId()
   {
      return mConversationId;
   }

}
