//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/handlers/AeBpelHandler.java,v 1.59 2008/02/27 17:55:58 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.handlers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.axis.AeHandler;
import org.activebpel.rt.axis.AeMutableServiceDesc;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeFault;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders;
import org.activebpel.rt.bpel.impl.reply.IAeDurableReplyInfo;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.IAeWsddConstants;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.receive.AeExtendedMessageContext;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceAttachment;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.handlers.soap.SOAPService;
import org.w3c.dom.Element;

/**
 * The base BPEL handler for web services under an Axis framework.
 */
public abstract class AeBpelHandler extends AeHandler implements IAePolicyConstants
{
   /** key for wsdl object added to the context */
   protected static final String AE_CONTEXT_KEY_WSDL_OUTPUT = "ae.wsdl.output"; //$NON-NLS-1$
   
   /** Key for property which holds the process QName. */
   private static final String PROCESS_ENTRY = "org.activebpel.rt.axis.ProcessEntry"; //$NON-NLS-1$

   /** Key for durable reply info property */
   public static final String DURABLE_REPLY_PROPERTY = "AeDurableReplyInfo"; //$NON-NLS-1$   
   
   /** Identifier tag used for process name element */
   public static final String PROCESS_NAME_TAG = IAeWsddConstants.TAG_PROCESS_NAME;
   /** Identifier tag used for process namespace element */
   public static final String PROCESS_NAMESPACE_TAG = IAeWsddConstants.TAG_PROCESS_NS;
   /** Identifier tag used for partner link element */
   public static final String PARTNER_LINK_TAG = IAeWsddConstants.TAG_PARTNER_LINK;
   /** Identifier tag used for partner link element */
   public static final String PARTNER_LINK_ID_TAG = IAeWsddConstants.TAG_PARTNER_LINK_ID;
   /** Transport URL property */
   public static final String TRANS_URL_TAG = MessageContext.TRANS_URL;  

   /**
    * Initialize the service description by loading parameters
    * in order to obtain the deployment plan for it.
    * @see org.apache.axis.providers.BasicProvider#initServiceDesc(org.apache.axis.handlers.soap.SOAPService, org.apache.axis.MessageContext)
    */
   public void initServiceDesc(SOAPService aService, MessageContext aContext) throws AxisFault
   {
      // The context will be null when axis is simply requesting a listing of
      // all web services as opposed to a particular service's wsdl.
      // We need to defer our true init until a context is available since we
      // use the context to fixup the locations of wsdl imports.
      // In JBoss 4.0, the context is not null at this point, but rather empty.
      String transportURL = null;

      //------------------------------------------------------------------------
      // allow the code to execute - which will set up the internal structure
      // and initialize the operations (ie: for wsdl listing page)
      // however - defer setting init flag to true unless we have a transport
      // url from the message context
      //------------------------------------------------------------------------

      if( aContext != null )
      {
         transportURL = (String) aContext.getProperty(MessageContext.TRANS_URL);
      }

      try
      {
         AeMutableServiceDesc serviceDesc = createServiceDescriptor();
         serviceDesc.setName(aService.getName());
         aService.setServiceDescription(serviceDesc);

         // default namespace
         String namespace = (String)aService.getOption(PROCESS_NAMESPACE_TAG);
         if (namespace == null)
            throw new AxisFault(AeMessages.getString("AeBpelHandler.ERROR_5")); //$NON-NLS-1$
         serviceDesc.setDefaultNamespace(namespace);

         // process name
         QName process = getProcessName(aService);
         setProcessName(serviceDesc, process);

         // partner link definition
         AePartnerLinkDef plinkDef = getPartnerLinkDef(aService);
         QName plinkType = plinkDef.getPartnerLinkTypeName();

         AeWsdlReference wsdlReference = new AeWsdlReference(serviceDesc, plinkDef.getLocationPath(),
               process, plinkType, transportURL);
         wsdlReference.init();
         serviceDesc.setWsdlReference( wsdlReference );

         aService.setServiceDescription( serviceDesc );

         // only set the init flag to true if we got a transport url
         if( AeUtil.notNullOrEmpty( transportURL ) )
         {
            serviceDesc.setInitialized(true);
         }
      }
      catch (Exception ex)
      {
         AeException.logError(ex, AeMessages.getString("AeBpelHandler.ERROR_13")); //$NON-NLS-1$
         throw AxisFault.makeFault(ex);
      }
   }

   /**
    * Override base implementation so that we can generate the WSDL each time it is requested.
    * Otherwise Axis will cache the generated WSDL and it may be different, depending on where
    * the initial request came from (e.g. localhost vs. IP addr)
    * 
    * @param aContext the message context for this request
    * @throws AxisFault
    */
   public void generateWSDL(MessageContext aContext) throws AxisFault
   {
      try
      {
         // Get the current copy of our ServiceDesc impl
         AeMutableServiceDesc serviceDesc = (AeMutableServiceDesc)aContext.getService().getServiceDescription();
         
         // Create a cloned copy of the WSDL reference with the current transport URL to be used for WSDL gen
         String transportURL = (String)aContext.getProperty(MessageContext.TRANS_URL);
         AeWsdlReference wsdlRef = new AeWsdlReference((AeWsdlReference)serviceDesc.getWsdlReference(), transportURL);
         
         // Init the wsdl ref, and set it as current copy in ServiceDesc
         wsdlRef.init();
         serviceDesc.setWsdlReference(wsdlRef);
         
         // Now go ahead and generate the WSDL
         generateWSDL(aContext, wsdlRef.getWsdlDef());
      }
      catch (AeException ex)
      {
         AeException.logError(ex, AeMessages.getString("AeBpelHandler.ERROR_13")); //$NON-NLS-1$
         throw AxisFault.makeFault(ex);
      }
   }
   
   /**
    * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
    */
   public void invoke(MessageContext aContext) throws AxisFault
   {
      try
      {
         // The expected input data for AeSOAPReceiveHandler is a single document containing
         // the entire SOAP envelope from the request
         QName envName = new QName(IAeConstants.SOAP_NAMESPACE_URI, "Envelope"); //$NON-NLS-1$
         AeWebServiceMessageData data = new AeWebServiceMessageData(envName);
         data.getMessageData().put(javax.xml.soap.SOAPEnvelope.class.getName(), aContext.getCurrentMessage().getSOAPEnvelope());
         
         // Add Soap Message Attachments
         data.setAttachments(AeAttachmentUtil.soap2wsioAttachments(aContext.getCurrentMessage(), aContext.getUsername()));
         
         // Invoke the BPEL engine
         AeExtendedMessageContext context = getExtendedContext(aContext);
         IAeWebServiceResponse wsResponse = AeEngineFactory.getEngine().queueReceiveData(context, data);
         aContext.setPastPivot(true);
         
         if (wsResponse == null)
         {
            return;
         }
         else if (wsResponse.getMessageData() != null)
         {
            aContext.setResponseMessage(getSOAPMessage(wsResponse));
         }
      }
      catch (AeBpelException ae)
      {
         AeException.logError(ae, ae.getLocalizedMessage());
         IAeFault fault = ae.getFault();
         if (fault != null)
            throw new AxisFault(fault.getFaultName(), fault.getInfo(), null, null);
         else
            throw AxisFault.makeFault(ae);
      }
      catch (Exception ex)
      {
         AeException.logError(ex, ex.getLocalizedMessage());
         throw AxisFault.makeFault(ex);
      }
   }
   
   /**
    * Creates and populates the AeExtendedMessageContext with properties from the Axis message context
    * 
    * @param aContext
    * @return the context
    */
   public AeExtendedMessageContext getExtendedContext(MessageContext aContext) throws AeBusinessProcessException
   {
      ServiceDesc serviceDesc = aContext.getService().getServiceDescription();
      String plinkLoc = ((AePartnerLinkDef)serviceDesc.getProperty(PARTNER_LINK_ENTRY)).getLocationPath();
      
      // Grab our deserialized addressing headers
      IAeAddressingHeaders wsa = (IAeAddressingHeaders) aContext.getProperty(IAeAddressingHeaders.AE_WSA_HEADERS_PROPERTY);
      if (wsa == null)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeBpelHandler.0")); //$NON-NLS-1$
      }
      
      // Create the extended message context
      AeExtendedMessageContext context = new AeExtendedMessageContext();
      context.setReceiveHandler(getReceiveHandler());
      context.setWsAddressingHeaders(wsa);
      context.setPartnerLink(plinkLoc);
      context.setPrincipal(aContext.getUsername());
      context.setProcessName(getProcessName(serviceDesc));
      context.setTransportUrl((String) aContext.getProperty(MessageContext.TRANS_URL));
      context.setMappedHeaders((Element) aContext.getProperty(AE_CONTEXT_MAPPED_PROPERTIES));
      context.setEncodingStyle(aContext.getEncodingStyle());
      context.setDurableReplyInfo((IAeDurableReplyInfo) aContext.getProperty(DURABLE_REPLY_PROPERTY));
      context.setSubject((Subject) aContext.getProperty(Subject.class.getName()));
      
      context.setProperty(MessageContext.class.getName(), aContext);
      context.setProperty(Use.class.getName(), getUse());
      
      return context;
   }
   
   /**
    * Setter for the process qname.
    * @param aService
    * @param aProcessQName
    */
   protected void setProcessName(ServiceDesc aService, QName aProcessQName)
   {
      aService.setProperty(PROCESS_ENTRY, aProcessQName);
   }

   /**
    * Gets the qname for the process that this service targets.
    * @param aService
    */
   protected QName getProcessName(ServiceDesc aService)
   {
      return (QName) aService.getProperty(PROCESS_ENTRY);
   }

   /**
    * Gets the deployment plan for this service.
    * @param aServiceDesc
    */
   protected IAeProcessDeployment getDeploymentPlan(ServiceDesc aServiceDesc) throws AeBusinessProcessException
   {
      IAeProcessDeployment deploymentPlan = AeEngineFactory.getDeploymentProvider().findCurrentDeployment(getProcessName(aServiceDesc));
      return deploymentPlan;
   }


   /**
    * Creates a service descriptor, and allow handler ability to initialize.
    */
   protected AeMutableServiceDesc createServiceDescriptor()
   {
      AeMutableServiceDesc serviceDesc = new AeMutableServiceDesc();
      serviceDesc.setUse( getUse() );
      serviceDesc.setStyle( getStyle() );
      return serviceDesc;
   }
   
   /**
    * Gets the QName for the BPEL process from the SOAPService
    * @param aService
    * @throws AxisFault
    */
   protected QName getProcessName(SOAPService aService) throws AxisFault
   {
      String namespace = (String) aService.getOption(PROCESS_NAMESPACE_TAG);
      if (namespace == null)
         throw new AxisFault(AeMessages.getString("AeBpelHandler.ERROR_5")); //$NON-NLS-1$

      String processName = (String) aService.getOption(PROCESS_NAME_TAG);
      if (processName == null)
         throw new AxisFault(AeMessages.getString("AeBpelHandler.ERROR_6")); //$NON-NLS-1$
      
      return new QName(namespace, processName);
   }

   /**
    * Gets the partner link definition for this receive
    * @param aService
    * @throws AxisFault
    */
   protected AePartnerLinkDef getPartnerLinkDef(SOAPService aService) throws AxisFault
   {
      String partnerLinkName = (String) aService.getOption(PARTNER_LINK_TAG);
      if (partnerLinkName == null)
         throw new AxisFault(AeMessages.getString("AeBpelHandler.ERROR_7")); //$NON-NLS-1$
      String partnerLinkIdStr = (String) aService.getOption(PARTNER_LINK_ID_TAG);
      if (partnerLinkIdStr == null)
         throw new AxisFault(AeMessages.getString("AeBpelHandler.ERROR_7")); //$NON-NLS-1$
      int partnerLinkId = Integer.parseInt(partnerLinkIdStr);
      AePartnerLinkDefKey plDefKey = new AePartnerLinkDefKey(partnerLinkName, partnerLinkId);

      // Get the copy of our ServiceDesc impl
      AeMutableServiceDesc serviceDesc = (AeMutableServiceDesc)aService.getServiceDescription();
      IAeProcessDeployment deployment = null;
      try
      {
         deployment = getDeploymentPlan(serviceDesc);
      }
      catch (AeBusinessProcessException ex)
      {
      }
      if (deployment == null)
         throw new AxisFault(AeMessages.getString("AeBpelHandler.ERROR_8")); //$NON-NLS-1$

      return deployment.getProcessDef().findPartnerLink(plDefKey);
   }
   
   /**
    * @see org.activebpel.rt.axis.bpel.handlers.AeBpelHandler#getUse()
    */
   protected abstract Use getUse();
   
   /**
    * @see org.activebpel.rt.axis.bpel.handlers.AeBpelHandler#getStyle()
    */
   protected abstract Style getStyle();
   
   /**
    * @return protocol binding name to determine receive handler
    */
   public abstract String getReceiveHandler();
   
   /**
    * Creates a multi part SOAP message when there are attachments present in the outbound response, otherwise returns a single soap envelope
    * @param aWsResponse
    * @return
    * @throws SOAPException
    * @throws AeWsAttachmentException
    */
   private Message getSOAPMessage(IAeWebServiceResponse aWsResponse) throws SOAPException
   {
      // The output data from AeSOAPReceiveHandler is a single document containing 
      // the entire SOAP envelope for the response
      javax.xml.soap.SOAPEnvelope resEnv = (javax.xml.soap.SOAPEnvelope) aWsResponse.getMessageData().getMessageData().get(javax.xml.soap.SOAPEnvelope.class.getName());
      
      Message soapMessage = new Message(resEnv);
      
      List attachments = aWsResponse.getMessageData().getAttachments();
      if ((attachments != null) && (attachments.size() > 0))
      {
         // Attachments need to be added to the the soap message.
         
         // Need to add SOAPAction header otherwise Axis rejects the transmission of a multipart message
         MimeHeaders hd = soapMessage.getMimeHeaders();
         hd.addHeader("SOAPAction", "");   //$NON-NLS-1$//$NON-NLS-2$
         
         try
         {
            // Create the SOAP Message attachments
            for(Iterator attachmentItr = attachments.iterator(); attachmentItr.hasNext();)
            {
               IAeWebServiceAttachment attachment = (IAeWebServiceAttachment)attachmentItr.next();
                
               AttachmentPart soapAttachment = (AttachmentPart)soapMessage.createAttachmentPart();
               String mimeType = attachment.getMimeType();
               soapAttachment.setContent(attachment.getContent(),mimeType);
               soapAttachment.setContentId(attachment.getContentId());
               // add attachment headers
               Map headers = attachment.getMimeHeaders();
               for (Iterator i = headers.entrySet().iterator(); i.hasNext();)
               {
                  Entry pair = (Entry)i.next();
                  soapAttachment.addMimeHeader((String)pair.getKey(), (String)pair.getValue());
               }
               soapMessage.addAttachmentPart(soapAttachment);
            }
            
         }
         finally
         {
            // Clean up temporary files for attachments.
            for (Iterator i = attachments.iterator(); i.hasNext(); )
            {
               AeCloser.close(((IAeWebServiceAttachment) i.next()).getContent());
            }
         }
         
         // Save changes to the message we just populated
         soapMessage.saveChanges();
      }
      return soapMessage;
   }
}
