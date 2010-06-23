//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/storage/AeDurableReplyDeserializer.java,v 1.2 2006/07/10 16:32:47 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.storage;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.reply.AeDurableReplyInfo;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AeDurableReplyDeserializer implements IAeImplStateNames
{
   /**
    * Durable reply information
    */
   private IAeDurableReplyInfo mDurableReplyInfo;
   
   /**
    * Element representing the serialized contents of the durable reply. 
    */
   private Element mDurableReplyInfoElement;

   /**
    * Deserializes the durable reply information.
    * 
    * @param aDurableReplyInfoElement
    * @throws AeBusinessProcessException
    */
   protected IAeDurableReplyInfo createDurableReplyInfo(Element aDurableReplyInfoElement) throws AeBusinessProcessException
   {
      String type = aDurableReplyInfoElement.getAttribute(STATE_DURABLE_REPLY_TYPE);
      if (AeUtil.isNullOrEmpty(type))
      {
         throw new IllegalStateException(AeMessages.getString("AeDurableReplyDeserializer.MISSING_DURABLE_REPLY_TYPE")); //$NON-NLS-1$         
      }
      // get the list of child property elements for the durable reply.
      Map properties = new HashMap();
      NodeList nl = aDurableReplyInfoElement.getElementsByTagName(STATE_PROPERTY);
      for (int i = 0; i < nl.getLength(); i++)
      {
         Element element = (Element) nl.item(i);
         String name = element.getAttribute(STATE_NAME);
         String value = AeXmlUtil.getText( element );
         properties.put(name, value);
      }
      return new AeDurableReplyInfo(type, properties);      
   }   
   
   protected void deserialize() throws AeBusinessProcessException
   {
      if (mDurableReplyInfo == null)
      {
         Element root = getDurableReplyInfoElement();
         if (root == null)
         {
            throw new IllegalStateException(AeMessages.getString("AeDurableReplyDeserializer.MISSING_DATA")); //$NON-NLS-1$
         }

         mDurableReplyInfo = createDurableReplyInfo(root);
      }
   }  
   
   /**
    * @return Returns the durableReplyInfo.
    */
   public IAeDurableReplyInfo getDurableReplyInfo() throws AeBusinessProcessException
   {
      deserialize();
      return mDurableReplyInfo;
   }

   /**
    * @param aDurableReplyInfo The durableReplyInfo to set.
    */
   protected void setDurableReplyInfo(IAeDurableReplyInfo aDurableReplyInfo)
   {
      mDurableReplyInfo = aDurableReplyInfo;
   }

   /**
    * @return Returns the durableReplyInfoElement.
    */
   protected Element getDurableReplyInfoElement()
   {
      return mDurableReplyInfoElement;
   }

   /**
    * @param aDurableReplyInfoElement The durableReplyInfoElement to set.
    */
   public void setDurableReplyInfoElement(Element aDurableReplyInfoElement)
   {
      mDurableReplyInfoElement = aDurableReplyInfoElement;
   }

}
