//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/receive/AeAbstractReceiveHandler.java,v 1.5 2008/02/27 17:55:04 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.receive;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.impl.IAeReceiveHandler;
import org.activebpel.rt.bpel.impl.addressing.AeAddressingHeaders;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;
import org.activebpel.rt.bpel.impl.reply.IAeReplyReceiver;
import org.activebpel.rt.bpel.impl.reply.IAeTransmissionTracker;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.security.AeSecurityException;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.AeMessageContext;
import org.activebpel.wsio.receive.IAeMessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Common base class for implementations of IAeReceiveHandler
 */
public abstract class AeAbstractReceiveHandler implements IAeReceiveHandler, IAeBPELReceiveHandler
{
   /** key for wsdl object added to the context */
   protected static final String AE_CONTEXT_KEY_WSDL_OUTPUT = "ae.wsdl.output"; //$NON-NLS-1$

   /**
    * @see org.activebpel.rt.bpel.impl.IAeReceiveHandler#handleReceiveData(org.activebpel.wsio.IAeWebServiceMessageData, org.activebpel.wsio.receive.IAeMessageContext)
    */
   public IAeWebServiceResponse handleReceiveData(IAeWebServiceMessageData aData, IAeMessageContext aContext) throws AeBusinessProcessException
   {
      AeExtendedMessageContext context = AeExtendedMessageContext.convertToExtended(aContext);
      IAeProcessPlan plan = getProcessPlan(context);
      updateContextWithPlanInfo(plan, context);
      authorizeRequest(plan, context);
      validateInputData(plan, context, aData);
      IAeMessageData data = mapInputData(plan, context, aData);
      return invokeProcessEngine(plan, context, data);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeReceiveHandler#handleReceiveData(org.w3c.dom.Document[], org.activebpel.wsio.receive.IAeMessageContext)
    */
   public IAeWebServiceResponse handleReceiveData(Document[] aDocArray, IAeMessageContext aContext) throws AeBusinessProcessException
   {
      AeExtendedMessageContext context = AeExtendedMessageContext.convertToExtended(aContext);
      IAeProcessPlan plan = getProcessPlan(context);
      updateContextWithPlanInfo(plan, context);
      authorizeRequest(plan, context);
      validateInputData(plan, context, aDocArray);
      IAeMessageData data = mapInputData(plan, context, aDocArray);
      return invokeProcessEngine(plan, context, data);
   }
   
   /**
    * Updates the process name if not already set and the service name is null
    * 
    * @param aPlan
    * @param aContext
    */
   protected void updateContextWithPlanInfo(IAeProcessPlan aPlan, AeExtendedMessageContext aContext) throws AeBusinessProcessException
   {
      if (AeUtil.isNullOrEmpty(aContext.getProcessName()) && AeUtil.isNullOrEmpty(aContext.getServiceName()))
      {
         aContext.setProcessName(aPlan.getProcessDef().getQName());
      }
   }
   
   /**
    * Authorizes a service request based on the subject on the message context and the allowedRoles
    * 
    * @param aPlan
    * @param aContext
    * @throws AeBusinessProcessException
    */
   protected void authorizeRequest(IAeProcessPlan aPlan, AeExtendedMessageContext aContext) throws AeBusinessProcessException
   {
      // Get the subject
      Subject subject = aContext.getSubject();
      
      if (subject != null)
      {
         try
         {
            if (!AeEngineFactory.getSecurityProvider().authorize(subject, aContext))
            {
               String[] msgstr = { aContext.getPrincipal(), aContext.getOperation(), aContext.getServiceName()};
               throw new AeBusinessProcessException(AeMessages.format("AeAbstractReceiveHandler.0", msgstr)); //$NON-NLS-1$
            }
         }
         catch (AeSecurityException e)
         {
            throw new AeBusinessProcessException(e.getLocalizedMessage(), e);
         }
      }
   }
   
   /**
    * Invokes the process engine 
    * 
    * @param aPlan
    * @param aContext
    * @param aData
    * @return response from engine, mapped to protocol-specific response
    */
   protected IAeWebServiceResponse invokeProcessEngine(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, IAeMessageData aData) throws AeBusinessProcessException
   {
      IAeAddressingHeaders wsa = AeAddressingHeaders.convert(aContext.getWsAddressingHeaders());
      wsa.setRecipient(getMyRoleEndpoint(aPlan, aContext));
      aContext.setWsAddressingHeaders(wsa);

      IAeDurableReplyInfo durableInfo = aContext.getDurableReplyInfo();
      if (durableInfo != null)
      {
         IAeReplyReceiver receiver = AeEngineFactory.getTransmissionTracker().getDurableReplyFactory().createReplyReceiver(IAeTransmissionTracker.NULL_TRANSREC_ID, durableInfo);
         AeEngineFactory.getEngine().queueReceiveData(aPlan, aData, receiver, aContext);
         return null;
      }
      else
      {
         IAeWebServiceResponse response = AeEngineFactory.getEngine().queueReceiveData(aPlan, aContext, aData);
         // map the output message or fault
         if (response == null)
            return null;
         else if (response.getErrorCode() != null)
            return mapFaultData(aContext, response);
         else if (response.getMessageData() != null)
            return mapOutputData(aContext, response);
         else
            return response; 
      }
   }
   
   /**
    * Gets the plan used to route the incoming message based on the information in the context.
    *
    * @param aContext
    * @throws AeBusinessProcessException
    */
   protected IAeProcessPlan getProcessPlan(AeMessageContext aContext) throws AeBusinessProcessException
   {
      return AeEngineFactory.getEngine().getProcessPlan(aContext);
   }

   /**
    * Gets the deployment plan for this service.
    * 
    * @param aPlan
    * @throws AeBusinessProcessException
    */
   protected IAeProcessDeployment getDeploymentPlan(IAeProcessPlan aPlan) throws AeBusinessProcessException
   {
      IAeProcessDeployment deploymentPlan = AeEngineFactory.getDeploymentProvider().findCurrentDeployment(aPlan.getProcessDef().getQName());
      return deploymentPlan;
   }
   
   /**
    * Returns the partner link definition 
    * 
    * @param aPlan
    * @param aContext
    * @return the partner link definition
    */
   protected AePartnerLinkDef getPartnerLinkDef(IAeProcessPlan aPlan, IAeMessageContext aContext)
   {
      return aPlan.getProcessDef().findPartnerLink(aContext.getPartnerLink());
   }

   /**
    * Gets the myRole endpoint reference for the service, updated with
    * information contained within the extended message context.
    * 
    * @param aPlan
    * @param aContext
    * @return an updated myRole endpoint reference
    * @throws AeBusinessProcessException
    */
   protected IAeEndpointReference getMyRoleEndpoint(IAeProcessPlan aPlan, IAeExtendedMessageContext aContext) throws AeBusinessProcessException
   {
      QName processName = aPlan.getProcessDef().getQName();
      AePartnerLinkDef def = getPartnerLinkDef(aPlan, aContext);
      try
      {
         IAeProcessDeployment deployment = getDeploymentPlan(aPlan);
         IAeEndpointReference myEndpoint =  AeEngineFactory.getPartnerAddressing().getMyRoleEndpoint(deployment, def, processName, null);
         if (!AeUtil.isNullOrEmpty(aContext.getTransportUrl()))
         {
            myEndpoint.setAddress(aContext.getTransportUrl());
         }
         if (!AeUtil.isNullOrEmpty(aContext.getServiceName()))
         {
            myEndpoint.setServiceName(new QName(def.getMyRolePortType().getNamespaceURI(), aContext.getServiceName()));
            myEndpoint.setServicePort(aContext.getServiceName() + "Port"); //$NON-NLS-1$            
         }
         // Get any mapped message headers attached by our handler
         Element headers = aContext.getMappedHeaders();
         if (headers != null) 
         {
            if (headers.getAttribute("passthrough").equals("true")) //$NON-NLS-1$ //$NON-NLS-2$
            {
               // add elements to message context to be passedthrough as headers on subsequent invokes
               NodeList children = headers.getChildNodes();
               for (int i = 0;i<children.getLength();i++) 
               {
                  Node child = children.item(i);
                  if (child.getNodeType() == Node.ELEMENT_NODE)
                     aContext.addReferenceProperty((Element) child);
               }
            }
            headers.setAttribute("operation", aContext.getOperation()); //$NON-NLS-1$
            myEndpoint.addExtensibilityElement(headers);
         }
         return myEndpoint;
      }
      catch (AeException ae)
      {
         throw new AeBusinessProcessException(ae.getLocalizedMessage(), ae);
      }
   }
   
   /**
    * Helper method to get the QName for the port type
    * 
    * @param aPlan
    * @param aContext
    * @return portType qname
    */
   protected QName getMyRolePortTypeQName(IAeProcessPlan aPlan, IAeMessageContext aContext)
   {
      AePartnerLinkDef plDef = aPlan.getProcessDef().findPartnerLink(aContext.getPartnerLink());
      AePartnerLinkDefKey plDefKey = new AePartnerLinkDefKey(plDef);

      return aPlan.getMyRolePortType(plDefKey);
   }
}
