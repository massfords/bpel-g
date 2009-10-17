//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/receive/AeExtendedMessageContext.java,v 1.2 2007/02/13 15:26:59 kpease Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.receive;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;
import org.activebpel.wsio.receive.AeMessageContext;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.w3c.dom.Element;

/**
 * Extended Message Context supporting the additional information required 
 * by our pluggable receive handlers such as those for SOAP messages, including WSRM
 */
public class AeExtendedMessageContext extends AeMessageContext implements IAeExtendedMessageContext
{
   private IAeDurableReplyInfo mDurableReplyInfo;
   private Element mMappedHeaders;
   private String mTransportUrl;
   private String mEncodingStyle;
   private HashMap mProperties;
   private Subject mSubject;

   /**
    * Default c-tor
    */
   public AeExtendedMessageContext()
   {
      super();
   }
   
   /**
    * Copy constructor from IAeMessageContext
    * @param aContext
    */
   public AeExtendedMessageContext(IAeMessageContext aContext)
   {
      super(aContext);
   }

   /**
    * Full Copy constructor
    * @param aContext
    */
   public AeExtendedMessageContext(IAeExtendedMessageContext aContext)
   {
      super(aContext);
      setDurableReplyInfo(aContext.getDurableReplyInfo());
      setTransportUrl(aContext.getTransportUrl());
      setMappedHeaders(aContext.getMappedHeaders());
      setEncodingStyle(aContext.getEncodingStyle());
      getProperties().putAll(aContext.getProperties());
      setSubject(aContext.getSubject());
   }
   
   /**
    * Converts to an instance of this class
    * @param aContext
    */
   public static AeExtendedMessageContext convertToExtended(IAeMessageContext aContext)
   {
      if (aContext instanceof AeExtendedMessageContext)
      {
         return (AeExtendedMessageContext) aContext;
      }
      else
      {
         return new AeExtendedMessageContext(aContext);
      }
   }
   
   /**
    * @return the encodingStyle
    */
   public String getEncodingStyle()
   {
      return mEncodingStyle;
   }

   /**
    * @param aEncodingStyle the encodingStyle to set
    */
   public void setEncodingStyle(String aEncodingStyle)
   {
      mEncodingStyle = aEncodingStyle;
   }
   
   /**
    * @param aDurableReplyInfo the durableReplyInfo to set
    */
   public void setDurableReplyInfo(IAeDurableReplyInfo aDurableReplyInfo)
   {
      mDurableReplyInfo = aDurableReplyInfo;
   }

   /**
    * @param aMappedHeaders the mappedHeaders to set
    */
   public void setMappedHeaders(Element aMappedHeaders)
   {
      mMappedHeaders = aMappedHeaders;
   }

   /**
    * @param aTransportUrl the transportUrl to set
    */
   public void setTransportUrl(String aTransportUrl)
   {
      mTransportUrl = aTransportUrl;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext#getDurableReplyInfo()
    */
   public IAeDurableReplyInfo getDurableReplyInfo()
   {
      return mDurableReplyInfo;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext#getMappedHeaders()
    */
   public Element getMappedHeaders()
   {
      return mMappedHeaders;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext#getTransportUrl()
    */
   public String getTransportUrl()
   {
      return mTransportUrl;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext#getProperty(java.lang.Object)
    */
   public Object getProperty(Object aKey)
   {
      if (mProperties == null)
      {
         return null;
      }
      
      return mProperties.get(aKey);
   }
   
   /**
    * sets a property
    * @param aKey
    * @param aValue
    */
   public void setProperty(Object aKey, Object aValue)
   {
      getProperties().put(aKey, aValue);
   }
   
   /**
    * @return Map of properties
    */
   public Map getProperties()
   {
      if (mProperties == null)
      {
         mProperties = new HashMap();
      }
      return mProperties;
   }

   /**
    * @return the subject
    */
   public Subject getSubject()
   {
      return mSubject;
   }

   /**
    * @param aSubject the subject to set
    */
   public void setSubject(Subject aSubject)
   {
      mSubject = aSubject;
   }

}
