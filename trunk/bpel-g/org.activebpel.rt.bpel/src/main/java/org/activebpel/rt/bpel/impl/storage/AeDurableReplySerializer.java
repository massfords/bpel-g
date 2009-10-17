//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/storage/AeDurableReplySerializer.java,v 1.2 2006/07/10 16:32:47 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.storage;

import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.fastdom.AeFastText;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;
import org.activebpel.rt.util.AeUtil;

/**
 * Serialiazes an instance of IAeDurableReplyInfo class.
 *
 */
public class AeDurableReplySerializer implements IAeImplStateNames
{
   // LocationPath maybe needed at a higher level e.g. AeDurableQueingReplyReceiver.
   /**
    * Durable reply information
    */
   private IAeDurableReplyInfo mDurableReplyInfo;
   
   /**
    * Element representing the serialized contents of the durable reply. 
    */
   private AeFastElement mDurableReplyInfoElement;
   
   /**
    * Returns an instance of {@link
    * org.activebpel.rt.bpel.impl.fastdom.AeFastDocument} representing the
    * durable reply information.
    */
   public AeFastDocument getDurableReplyInfoDocument() throws AeBusinessProcessException
   {
      return new AeFastDocument(getDurableReplyInfoElement());
   }   

   /**
    * Returns an instance of {@link
    * org.activebpel.rt.bpel.impl.fastdom.AeFastElement} representing the
    * durable reply information.
    */
   public AeFastElement getDurableReplyInfoElement() throws AeBusinessProcessException
   {
      if (mDurableReplyInfoElement == null)
      {
         if (getDurableReplyInfo() == null)
         {
            throw new IllegalStateException(AeMessages.getString("AeDurableReplySerializer.MISSING_DATA")); //$NON-NLS-1$
         }

         mDurableReplyInfoElement = createDurableReplyInfoElement( getDurableReplyInfo() );
      }

      return mDurableReplyInfoElement;
   }

   /**
    * Serializes the inbound receive's durable reply data.
    * 
    * @param aReply
    */
   protected AeFastElement createDurableReplyInfoElement(IAeDurableReplyInfo aReply)
   { 
      AeFastElement result = new AeFastElement(STATE_DURABLE_REPLY);
      result.setAttribute(STATE_DURABLE_REPLY_TYPE, aReply.getType());
      // Serialize durable reply properties.
      Map properties = aReply.getProperties();
      if (properties != null)
      {
         for (Iterator i = properties.entrySet().iterator(); i.hasNext(); )
         {
            Map.Entry entry = (Map.Entry) i.next();
            String name = (String) entry.getKey();
            String value = AeUtil.getSafeString((String) entry.getValue());
            AeFastElement propertyElement = new AeFastElement( STATE_PROPERTY );
            propertyElement.setAttribute( STATE_NAME, name );
            AeFastText valueNode = new AeFastText( value );
            propertyElement.appendChild( valueNode );            
            result.appendChild(propertyElement);
         }
      }      
      return result;
   }  
   
   /**
    * @return Returns the durableReplyInfo.
    */
   protected IAeDurableReplyInfo getDurableReplyInfo()
   {
      return mDurableReplyInfo;
   }

   /**
    * @param aDurableReplyInfo The durableReplyInfo to set.
    */
   public void setDurableReplyInfo(IAeDurableReplyInfo aDurableReplyInfo)
   {
      mDurableReplyInfo = aDurableReplyInfo;
   }   
   
}
