// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeWSBPELDefMessagePartsMapVisitor.java,v 1.4 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import javax.wsdl.Message;
import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataConsumerDef;
import org.activebpel.rt.bpel.def.activity.IAeMessageDataProducerDef;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;

/**
 * Assigns {@link org.activebpel.rt.message.AeMessagePartsMap} info to web
 * service activities.
 */
public class AeWSBPELDefMessagePartsMapVisitor extends AeAbstractDefMessagePartsMapVisitor
{
   /**
    * Constructs the visitor with the given WSDL provider.
    *
    * @param aWSDLProvider
    */
   public AeWSBPELDefMessagePartsMapVisitor(IAeContextWSDLProvider aWSDLProvider)
   {
      super(aWSDLProvider);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      QName portType = getPartnerRolePortType(aDef);
      String operation = aDef.getOperation();
      String locationPath = aDef.getLocationPath();
   
      if (aDef.getProducerMessagePartsMap() == null)
      {
         AeMessagePartsMap inputMap = createInputMessagePartsMap(portType, operation, null, null, locationPath);
         aDef.setProducerMessagePartsMap(inputMap);
      }
   
      if (aDef.getConsumerMessagePartsMap() == null)
      {
         AeMessagePartsMap outputMap = createOutputMessagePartsMap(portType, operation, null, null, locationPath);
         aDef.setConsumerMessagePartsMap(outputMap);
      }
   
      super.visit(aDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeChildExtensionActivityDef)
    */
   public void visit(AeChildExtensionActivityDef aDef)
   {
      if (aDef.getExtensionObject() != null)
      {
         // producer 
         IAeMessageDataProducerDef producerDef = (IAeMessageDataProducerDef) aDef.getExtensionObject().getAdapter(IAeMessageDataProducerDef.class);
         if (producerDef != null)
         {
            AeMessagePartsMap map = createInputMessagePartsMap(producerDef.getProducerPortType(), producerDef.getProducerOperation(), null, null, aDef.getLocationPath());
            producerDef.setProducerMessagePartsMap(map);
         }
         
         // consumer
         IAeMessageDataConsumerDef consumerDef = (IAeMessageDataConsumerDef) aDef.getExtensionObject().getAdapter(IAeMessageDataConsumerDef.class);
         if (consumerDef != null)
         {
            AeMessagePartsMap map = createOutputMessagePartsMap(consumerDef.getConsumerPortType(), consumerDef.getConsumerOperation(), null, null, aDef.getLocationPath());
            consumerDef.setConsumerMessagePartsMap(map);
         }
      }
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefMessagePartsMapVisitor#createFaultMessagePartsMap(javax.xml.namespace.QName, java.lang.String, javax.xml.namespace.QName, java.lang.String)
    */
   protected AeMessagePartsMap createFaultMessagePartsMap(QName aPortType, String aOperation, QName aFaultName, String aLocationPath)
   {
      Message message = AeWSDLDefHelper.getFaultMessage(getWSDLProvider(), aPortType, aOperation, aFaultName);
      return (message == null) ? null : createMessagePartsMap(message, aLocationPath);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefMessagePartsMapVisitor#createInputMessagePartsMap(javax.xml.namespace.QName, java.lang.String, javax.xml.namespace.QName, javax.xml.namespace.QName, java.lang.String)
    */
   protected AeMessagePartsMap createInputMessagePartsMap(QName aPortType, String aOperation, QName aRequestMessageType, QName aResponseMessageType, String aLocationPath)
   {
      Message message = AeWSDLDefHelper.getInputMessage(getWSDLProvider(), aPortType, aOperation);
      return (message == null) ? null : createMessagePartsMap(message, aLocationPath);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefMessagePartsMapVisitor#createOutputMessagePartsMap(javax.xml.namespace.QName, java.lang.String, javax.xml.namespace.QName, javax.xml.namespace.QName, java.lang.String)
    */
   protected AeMessagePartsMap createOutputMessagePartsMap(QName aPortType, String aOperation, QName aRequestMessageType, QName aResponseMessageType, String aLocationPath)
   {
      Message message = AeWSDLDefHelper.getOutputMessage(getWSDLProvider(), aPortType, aOperation);
      return (message == null) ? null : createMessagePartsMap(message, aLocationPath);
   }
}
