// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/storage/AeInboundReceiveDeserializer.java,v 1.17 2008/03/28 01:41:27 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.bpel.impl.AeBusinessProcessPropertyIO;
import org.activebpel.rt.bpel.impl.AeEndpointReference;
import org.activebpel.rt.bpel.impl.AeMessageDataDeserializer;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.addressing.AeAddressingHeaders;
import org.activebpel.rt.bpel.impl.addressing.AeWsAddressingFactory;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingDeserializer;
import org.activebpel.rt.bpel.impl.queue.AeInboundReceive;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.activebpel.wsio.IAeWsAddressingHeaders;
import org.activebpel.wsio.receive.AeMessageContext;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Deserializes an inbound receive from its serialization.
 */
public class AeInboundReceiveDeserializer implements IAeImplStateNames
{
   /** The inbound receive once deserialized. */
   private AeInboundReceive mInboundReceive;

   /** The inbound receive's serialization. */
   private Element mInboundReceiveElement;

   /** The process plan to use to deserialize the inbound receive. */
   private IAeProcessPlan mProcessPlan;

   /** The type mapping to use to deserialize simple types. */
   private AeTypeMapping mTypeMapping;
   
   /** The durable reply info once deserialized. (if available). */
   private IAeDurableReplyInfo mDurableReplyInfo;

   /**
    * Creates the correlation map for the incoming receive.  
    *
    * @param aPartnerLinkOpKey
    * @param aPlan
    * @param aData
    * @param aContext
    */
   private Map createCorrelationMap(AePartnerLinkOpKey aPartnerLinkOpKey, IAeProcessPlan aPlan,
         IAeMessageData aData, IAeMessageContext aContext) throws AeBusinessProcessException
   {
      Collection names = aPlan.getCorrelatedPropertyNames(aPartnerLinkOpKey);
      Map map = new HashMap();

      // Note: aData would only be null if an inbound receive had no message data.  This only
      // happens when saving the state of an AeActivityOnEventScopeImpl.  For details, see 
      // AeSaveImplStateVisitor::visit(AeActivityOnEventScopeImpl aImpl). 
      if (aData != null)
      {
         for (Iterator i = names.iterator(); i.hasNext(); )
         {
            QName name = (QName) i.next();
            AeMessagePartsMap messagePartsMap = aPlan.getProcessDef().getMessageForCorrelation(aPartnerLinkOpKey);
            IAePropertyAlias alias = aPlan.getProcessDef().getPropertyAliasForCorrelation(messagePartsMap, name);
            QName type = AeWSDLDefHelper.getProperty(aPlan, name).getTypeName();
            Object value = AeXPathHelper.getInstance(aPlan.getProcessDef().getNamespace()).extractCorrelationPropertyValue(alias, aData, getTypeMapping(), type);
   
            map.put(name, value);
         }
      }

      String convId = aContext.getWsAddressingHeaders().getConversationId(); 
      if (convId != null)
      {
         // add engine managed correlation property to the set
         map.put(IAePolicyConstants.CONVERSATION_ID_HEADER, convId);
      }
      
      return map;
   }

   /**
    * Creates an instance of {@link org.activebpel.rt.bpel.impl.queue.AeInboundReceive} from its
    * serialization.
    * 
    * @param aInboundReceiveElement
    * @throws AeBusinessProcessException
    */
   protected AeInboundReceive createInboundReceive(Element aInboundReceiveElement) throws AeBusinessProcessException
   {
      // Deserialize the message context and message data from the input XML
      // element.
      Map childElementsMap = getChildElementsMap(aInboundReceiveElement);
      AeMessageContext context = createMessageContext(getElement(childElementsMap, STATE_MESSAGECONTEXT));
      IAeMessageData data = createMessageData(getElement(childElementsMap, STATE_MESSAGEDATA));
 
      mDurableReplyInfo = createDurableReplyInfo(getElement(childElementsMap, STATE_DURABLE_REPLY));
      // Verify that caller provided process plan.
      IAeProcessPlan plan = getProcessPlan();
      if (plan == null)
      {
         throw new IllegalStateException(AeMessages.getString("AeInboundReceiveDeserializer.ERROR_0")); //$NON-NLS-1$
      }

      String partnerLink = context.getPartnerLink();
      AePartnerLinkDef plDef = plan.getProcessDef().findPartnerLink(partnerLink);
      AePartnerLinkOpKey plOpKey = new AePartnerLinkOpKey(plDef, context.getOperation());

      // Reconstruct correlation map.
      Map correlationMap = createCorrelationMap(plOpKey, plan, data, context);
      boolean replyWaiting = "true".equalsIgnoreCase( aInboundReceiveElement.getAttribute(STATE_REPLYWAITING)); //$NON-NLS-1$
      // Create inbound receive (with out the durable reply info)
      AeInboundReceive result = new AeInboundReceive(plOpKey, correlationMap, plan, data, context);
      result.setOneway(!replyWaiting);
      return result;
   }
   
   /**
    * Creates the durable reply properties.
    * 
    * @param aDurableReplyInfoElement
    * @throws AeBusinessProcessException
    */
   protected IAeDurableReplyInfo createDurableReplyInfo(Element aDurableReplyInfoElement) throws AeBusinessProcessException
   {
      IAeDurableReplyInfo info = null;
      if (aDurableReplyInfoElement != null)
      {
         AeDurableReplyDeserializer deserializer = new AeDurableReplyDeserializer();
         deserializer.setDurableReplyInfoElement(aDurableReplyInfoElement);
         info = deserializer.getDurableReplyInfo();
      }
      return info;
   }

   /**
    * Creates an instance of {@link
    * org.activebpel.wsio.IAeWebServiceEndpointReference} from its
    * serialization.
    * 
    * @param aEndpointReferenceElement
    */
   protected IAeWebServiceEndpointReference createEndpointReference(Element aEndpointReferenceElement) throws AeBusinessProcessException
   {
      AeEndpointReference result = null;

      if (aEndpointReferenceElement != null)
      {
         result = new AeEndpointReference();
         result.setReferenceData(AeXmlUtil.getFirstSubElement(aEndpointReferenceElement));
      }

      return result;
   }

   /**
    * Creates an instance of {@link
    * org.activebpel.wsio.IAeWsAddressingHeaders} from its
    * serialization.
    * 
    * @param aWsaElement
    */
   protected IAeWsAddressingHeaders createWsaHeaders(Element aWsaElement) throws AeBusinessProcessException
   {
      AeAddressingHeaders result = new AeAddressingHeaders(IAeConstants.WSA_NAMESPACE_URI);

      if (aWsaElement != null)
      {
         IAeAddressingDeserializer writer = AeWsAddressingFactory.getInstance().getDeserializer(IAeConstants.WSA_NAMESPACE_URI);
         writer.deserializeHeaders(AeXmlUtil.getFirstSubElement(aWsaElement), result);
      }
      
      return result;
   }
   
   /**
    * Deserializes the inbound receive's message context from its serialization.
    * 
    * @param aMessageContextElement
    * @throws AeBusinessProcessException
    */
   protected AeMessageContext createMessageContext(Element aMessageContextElement) throws AeBusinessProcessException
   {
      AeMessageContext result = null;

      if (aMessageContextElement != null)
      {
         Map map = getChildElementsMap(aMessageContextElement);
         
         result = new AeMessageContext();
         
         // deserialize complete set of WSA headers
         IAeWsAddressingHeaders wsa = createWsaHeaders(getElement(map, STATE_WSAHEADERS));
         
         // Versions prior to 3.0.2 only serialized the recipient and ReplyTo endpoints from the message context
         // In the event that we needed to restore the message context prior to 
         // the update using the engine's partner link strategy, we would lose
         // addressing information for MessageID and conversationID elements.
         
         // check for legacy myRole endpoint element.  
         if (getElement(map, STATE_MYENDPOINTREFERENCE) != null)
         {
            IAeWebServiceEndpointReference myEndpointReference = createEndpointReference(getElement(map, STATE_MYENDPOINTREFERENCE));
            wsa.setRecipient(myEndpointReference);
         }
         
         // check for legacy partner endpoint element from replyTo 
         if (getElement(map, STATE_PARTNERENDPOINTREFERENCE) != null)
         {
            IAeWebServiceEndpointReference embeddedEndpointReference = createEndpointReference(getElement(map, STATE_PARTNERENDPOINTREFERENCE));
            wsa.setReplyTo(embeddedEndpointReference);
         }
         
         result.setWsAddressingHeaders(wsa);
         
         // Deserialize attributes.
         result.setOperation(aMessageContextElement.getAttribute(STATE_OPERATION));
         result.setPartnerLink(aMessageContextElement.getAttribute(STATE_PLINK));
         result.setPrincipal(aMessageContextElement.getAttribute(STATE_PRINCIPAL));
         result.setProcessVersion(aMessageContextElement.getAttribute(STATE_VERSION));

         // Deserialize process properties.
         NodeList nodes = aMessageContextElement.getChildNodes();
         Map properties = result.getBusinessProcessProperties();

         for (int i = nodes.getLength(); --i >= 0; )
         {
            Node node = nodes.item(i);

            if ((node instanceof Element) && STATE_PROCESSPROPERTY.equals(node.getNodeName()))
            {
               AeBusinessProcessPropertyIO.extractBusinessProcessProperty((Element) node, properties);            
            }
         }
         
         // Deserialize reference properties
         Element refprops = getElement(map, STATE_REFPROPS);
         if (refprops != null)
         {
            NodeList props = refprops.getChildNodes();
            for (int i = 0; i < props.getLength(); i++)
            {
               if (props.item(i).getNodeType() == Node.ELEMENT_NODE)
               {
                  result.addReferenceProperty((Element) props.item(i));
               }
            }
         }
         
         restoreMappedHeaders(map, wsa);
      }
      
      return result;
   }

   /**
    * Restores mapped headers from the map
    * @param aMap
    * @param aHeaders
    */
   protected void restoreMappedHeaders(Map aMap, IAeWsAddressingHeaders aHeaders)
   {
      // If the inbound receive came in with mapped headers, then it's likely that the bpel process is expecting to find them there when it executes. 
      // deserialize any optional mapped headers
      Element mappedHeaders = getElement(aMap, STATE_MAPPED_HEADERS);
      if (mappedHeaders != null)
      {
         AeEndpointReference epr = new AeEndpointReference();
         if (aHeaders.getRecipient() != null)
            epr.setReferenceData(aHeaders.getRecipient());
            
         NodeList props = mappedHeaders.getChildNodes();
         List extElements = new ArrayList();
         extElements.addAll(AeUtil.toList(epr.getExtensibilityElements()));
         for (int i = 0; i < props.getLength(); i++)
         {
            if (props.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
               extElements.add(props.item(i));
            }
         }
         
         epr.setExtensibilityElements(extElements);
         aHeaders.setRecipient(epr);
      }
   }

   /**
    * Deserializes the inbound receive's message data from its serialization.
    * 
    * @param aMessageDataElement
    * @throws AeBusinessProcessException
    */
   protected IAeMessageData createMessageData(Element aMessageDataElement) throws AeBusinessProcessException
   {
      IAeMessageData result = null;

      if (aMessageDataElement != null)
      {
         AeMessageDataDeserializer deserializer = new AeMessageDataDeserializer();
         deserializer.setMessageDataElement(aMessageDataElement);

         result = deserializer.getMessageData();
      }

      return result;
   }

   /**
    * Deserializes a {@link javax.xml.namespace.QName} from the specified
    * element.
    * 
    * @param aQNameElement
    */
   protected QName createQName(Element aQNameElement)
   {
      QName result = null;

      if (aQNameElement != null)
      {
         String localPart = aQNameElement.getAttribute(STATE_NAME);
         String namespace = aQNameElement.getAttribute(STATE_NAMESPACEURI);

         result = new QName(namespace, localPart);
      }

      return result;
   }

   /**
    * Sets output variables.
    */
   protected void deserialize() throws AeBusinessProcessException
   {
      if (mInboundReceive == null)
      {
         Element root = getInboundReceiveElement();
         if (root == null)
         {
            throw new IllegalStateException(AeMessages.getString("AeInboundReceiveDeserializer.ERROR_1")); //$NON-NLS-1$
         }

         mInboundReceive = createInboundReceive(root);
      }
   }

   /**
    * Returns a {@link java.util.Map} from tag names to child elements.
    * 
    * @param aElement
    */
   protected Map getChildElementsMap(Element aElement)
   {
      NodeList nodes = aElement.getChildNodes();
      Map result = new HashMap(nodes.getLength());

      for (int i = 0; i < nodes.getLength(); ++i)
      {
         Node node = nodes.item(i);

         if (node instanceof Element)
         {
            result.put(node.getNodeName(), node);
         }
      }

      return result;
   }

   /**
    * Returns the child element for the specified tag name from a {@link
    * java.util.Map} of tag names to child elements.
    * 
    * @param aChildElementsMap
    * @param aTagName
    */
   protected Element getElement(Map aChildElementsMap, String aTagName)
   {
      return (Element) aChildElementsMap.get(aTagName);
   }

   /**
    * Returns the process plan to use to deserialize the inbound receive.
    */
   protected IAeProcessPlan getProcessPlan()
   {
      return mProcessPlan;
   }

   /**
    * Returns the inbound receive deserialized from the serialization that was
    * set with {@link #setInboundReceiveElement}.
    */
   public AeInboundReceive getInboundReceive() throws AeBusinessProcessException
   {
      deserialize();

      return mInboundReceive;
   }

   /**
    * @return Returns the deserialized durableReplyInfo. If the inbound receive is one way or 
    * if the reply was not durable, then this method returns <code>null</code>.
    */
   public IAeDurableReplyInfo getDurableReplyInfo() throws AeBusinessProcessException
   {
      deserialize();
      return mDurableReplyInfo;
   }

   /**
    * Returns the inbound receive serialization to use.
    */
   protected Element getInboundReceiveElement()
   {
      return mInboundReceiveElement;
   }

   /**
    * Returns type mapping to use to deserialize simple types.
    */
   protected AeTypeMapping getTypeMapping()
   {
      return mTypeMapping;
   }

   /**
    * Resets all output variables.
    */
   protected void reset()
   {
      mInboundReceive = null;
   }

   /**
    * Sets the process plan to use to deserialize the inbound receive.
    *
    * @param aProcessPlan
    */
   public void setProcessPlan(IAeProcessPlan aProcessPlan)
   {
      reset();

      mProcessPlan = aProcessPlan;
   }

   /**
    * Sets the inbound receive serialization to use.
    *
    * @param aInboundReceiveElement
    */
   public void setInboundReceiveElement(Element aInboundReceiveElement)
   {
      reset();

      mInboundReceiveElement = aInboundReceiveElement;
   }

   /**
    * Sets the type mapping to use to deserialize simple types.
    *
    * @param aTypeMapping
    */
   public void setTypeMapping(AeTypeMapping aTypeMapping)
   {
      reset();

      mTypeMapping = aTypeMapping;
   }
}
