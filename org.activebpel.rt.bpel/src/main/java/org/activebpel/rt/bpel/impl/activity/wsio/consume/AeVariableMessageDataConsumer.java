// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/wsio/consume/AeVariableMessageDataConsumer.java,v 1.9 2008/04/02 01:34:56 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.wsio.consume;

import java.util.Iterator;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.message.AeMessagePartTypeInfo;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Implements a message data producer that copies data to a variable.
 */
public class AeVariableMessageDataConsumer extends AeAbstractMessageDataConsumer
{
   /**
    * Constructs a variable message data consumer for the given
    * context.
    *
    */
   public AeVariableMessageDataConsumer()
   {
      super();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumer#consumeMessageData(org.activebpel.rt.message.IAeMessageData, org.activebpel.rt.bpel.impl.activity.wsio.consume.IAeMessageDataConsumerContext)
    */
   public void consumeMessageData(IAeMessageData aMessageData, IAeMessageDataConsumerContext aContext) throws AeBusinessProcessException
   {
      IAeVariable variable = aContext.getVariable();

      // By the time we get here, we know that this is either a message type
      // variable or an element variable.
      if (variable.isMessageType())
      {
         // might need the type mapping
         AeTypeMapping typeMapping = aContext.getBpelObject().getProcess().getEngine().getTypeMapping();

         AeMessagePartsMap map = aContext.getMessageConsumerDef().getConsumerMessagePartsMap();
         for (Iterator it = aMessageData.getPartNames(); it.hasNext();)
         {
            String part = (String) it.next();
            Object data = aMessageData.getData(part);
            if (data instanceof String)
            {
               AeMessagePartTypeInfo typeInfo = map.getPartInfo(part);
               // type info could be null for an invalid message.
               // in this case, the process may throw a fault when the validation kicks in later.
               if (typeInfo != null)
               {
                  Object converted = typeMapping.deserialize(typeInfo.getXMLType(), (String)data);
                  aMessageData.setData(part, converted);
               }
            }
            else if (data instanceof Document)
            {
               // Touch the nodes so they're fully expanded and not using a 
               // "deferred" implementation. I noticed that the deferred impl
               // was causing some expressions to not resolve correctly.
               AeXmlUtil.touchXmlNodes((Node)data);
            }
         }
         variable.setMessageData(aMessageData);
      }
      else
      {
         try
         {
            String partName = (String) aMessageData.getPartNames().next();
            Document doc = (Document) aMessageData.getData(partName); 
            variable.setElementData(doc.getDocumentElement());
            
            if (aMessageData.hasAttachments())
               variable.setAttachmentData(aMessageData.getAttachmentContainer());
         }
         catch (Throwable t)
         {
            // Note: The only way that we should fault here is if validation is
            // turned off and we are consuming an incomplete message.
            //
            if (t instanceof AeBusinessProcessException)
            {
               throw (AeBusinessProcessException) t;
            }
            
            throw new AeBusinessProcessException(t.getLocalizedMessage(), t);
         }
      }
   }
}
