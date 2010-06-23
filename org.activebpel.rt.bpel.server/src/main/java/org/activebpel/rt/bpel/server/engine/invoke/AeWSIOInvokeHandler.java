// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AeWSIOInvokeHandler.java,v 1.4 2008/02/17 21:38:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.xml.namespace.QName;

import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.AeWSDLPolicyHelper;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.impl.AeEndpointReference;
import org.activebpel.rt.bpel.impl.addressing.AeAddressingHeaders;
import org.activebpel.rt.bpel.impl.addressing.IAeAddressingHeaders;
import org.activebpel.rt.bpel.impl.queue.AeInvoke;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.addressing.IAePartnerAddressing;
import org.activebpel.rt.bpel.server.deploy.IAePolicyMapper;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.AeBindingUtils;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.AeWsAddressingException;
import org.activebpel.wsio.IAeWebServiceEndpointReference;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Base class for invoke handlers making calls that require WSDL, Policy and WS-Addressing support 
 */
public abstract class AeWSIOInvokeHandler implements IAeInvokeHandler, IAePolicyConstants
{
   public static final String STYLE_RPC = "rpc"; //$NON-NLS-1$
   public static final String STYLE_DOCUMENT = "document"; //$NON-NLS-1$

   /**
    * Sets up the context for an invoke, resolving WSDL definitions for the service and SOAP binding operation,
    * resolves policy references, and ws-addressing for the target endpoint.
    * 
    * @param aContext
    * @param aInvokeQueueObject
    * @param aQueryData
    * @throws AeBusinessProcessException
    */
   protected void setupInvokeContext(AeInvokeContext aContext, IAeInvoke aInvokeQueueObject, String aQueryData) throws AeBusinessProcessException
   {
      aContext.setInvoke(aInvokeQueueObject);

      AeAddressHandlingType addressHandling = AeAddressHandlingType.getByName(aQueryData);
      aContext.setAddressType(addressHandling);

      IAeEndpointReference endpointReference = getEndpointReference(aInvokeQueueObject);
      aContext.setEndpoint(endpointReference);
      
      AeBPELExtendedWSDLDef wsdlDef = getWsdlDef(aInvokeQueueObject, endpointReference);
      Service wsdlService = getServiceObject(wsdlDef, endpointReference);
      aContext.setService(wsdlService);

      // The service name can be null in some circumstances. Specifically, if the addressHandling type is
      // URN or URL, then we'll attempt to use the address found in the wsa:Address
      // as opposed to the soap:address in the service.
      if ( wsdlService == null && addressHandling == AeAddressHandlingType.SERVICE )
         throw new AeBusinessProcessException(AeMessages.getString("AeInvokeHandler.ERROR_0")); //$NON-NLS-1$

      PortType portType = getPortType(wsdlService, endpointReference, aInvokeQueueObject);
      Operation operation = portType.getOperation(aInvokeQueueObject.getOperation(), null, null);
      aContext.setOperation(operation);
      if ( operation == null )
         throw new AeBusinessProcessException(AeMessages.format("AeInvokeHandler.ERROR_MISSING_OPERATION", aInvokeQueueObject.getOperation())); //$NON-NLS-1$
               
      // get the implicit replyTo 
      IAeEndpointReference replyTo = updateReplyToEndpoint(aInvokeQueueObject);
      aContext.setReplyTo(replyTo);
      
      // Resolve endpoint and wsdl policies
      List wsdlPolicy = getWsdlPolicies(wsdlDef, aInvokeQueueObject, endpointReference);
      aContext.setPolicyList(wsdlPolicy);
      // map policy to call properties
      Map callProperties = getPolicyDrivenProperties(wsdlPolicy);
      aContext.setCallProperties(callProperties);

      String url = getEndpointUrl(wsdlService, endpointReference, aContext.getAddressType());
      endpointReference.setAddress(url);
      String soapAction = getSoapAction(wsdlService, operation.getName(), endpointReference);
      aContext.setSoapAction(soapAction);
      
      // Setup a holder for WS-Addressing info
      if (isWsaMandatory(endpointReference, aContext.getReplyTo()))
      {
         // Update the endpoint with WS-Addressing info
         IAeAddressingHeaders wsaHeaders = new AeAddressingHeaders(endpointReference.getSourceNamespace());
         wsaHeaders.setAction(soapAction);
         wsaHeaders.setTo(url);
         wsaHeaders.setReplyTo(aContext.getReplyTo());
         IAePartnerAddressing pa = AeEngineFactory.getPartnerAddressing();
         IAeEndpointReference newEndpoint = pa.updateEndpointHeaders(wsaHeaders, endpointReference);
         aContext.setEndpoint(newEndpoint);
      }
      
      // handle the principal header mapping policy
      handlePrincipalHeader(aInvokeQueueObject, aContext);
      
      if (wsdlService != null)
      {
         // list of input parts to add as headers
         // TODO (MF) get list of faults to look for in response soap headers
         BindingOperation bop = getBindingOperation(wsdlService, operation.getName(), endpointReference);
         aContext.setBindingOperation(bop);
         BindingInput input = bop.getBindingInput();
         Message inputMessage = operation.getInput().getMessage();
         
         Collection inputHeaderParts = AeBindingUtils.getPartsForHeader(input, inputMessage.getQName());
         aContext.setInputHeaderParts(inputHeaderParts);
         
         if (operation.getOutput() != null)
         {
            Collection outputHeaderParts = AeBindingUtils.getPartsForHeader(bop.getBindingOutput(), operation.getOutput().getMessage().getQName());
            aContext.setOutputHeaderParts(outputHeaderParts);
         }
      }
   }

   /**
    * Extract the <code>IAeEndpointReference</code> from the
    * <code>IAeInvoke</code> object.
    * @param aInvoke
    */
   protected IAeEndpointReference getEndpointReference(IAeInvoke aInvoke)
   {
      return ((AeInvoke)aInvoke).getPartnerReference();
   }
   
   /**
    * Extract the wsa:ReplyTo <code>IAeWebServiceEndpointReference</code> from the
    * <code>IAeInvoke</code> object and see if we need to include engine managed correlation headers.
    * @param aInvoke
    */
   protected IAeEndpointReference updateReplyToEndpoint(IAeInvoke aInvoke) throws AeBusinessProcessException
   {
      IAeEndpointReference myRef = ((AeInvoke)aInvoke).getMyReference();
      if (myRef == null)
      {
         return null;
      }
      IAeEndpointReference replyTo = new AeEndpointReference();
      replyTo.setReferenceData(myRef);

      // we don't want to transmit our policy info in the replyTo
      replyTo.clearPolicies();
      
      return replyTo;
   }
   
   /**
    * Find the matching <code>javax.wsdl.Service</code> service object.
    * @param aDef
    * @param aEndpointRef
    */
   protected Service getServiceObject(AeBPELExtendedWSDLDef aDef, IAeWebServiceEndpointReference aEndpointRef) throws AeBusinessProcessException
   {
      Service service = null;
      if ( aEndpointRef.getServiceName() != null )
      {
         if ( aDef != null )
         {
            service = (Service)aDef.getServices().get(aEndpointRef.getServiceName());
         }
         // TODO (MF) if we were given a service name but couldn't find it, we should generate a warning message.
         //       we may still be able to do the invoke but w/o the binding info we might be missing out
         //       on adding some required info like the soap action.
      }
      return service;
   }
   
   /**
    * Mandatory WSA information should be added automatically if
    *    1. we have a ReplyTo address from the partner endpoint reference properties --OR--
    *    2. we have headers required for engine managed correlation in the default reply to ref props
    * TODO: Use the 2006 WSA SOAP Binding spec to decide this in the future
    */
   protected boolean isWsaMandatory(IAeWebServiceEndpointReference aPartnerEndpoint, IAeWebServiceEndpointReference aMyEndpoint) throws AeBusinessProcessException
   {
      try
      {
         IAeAddressingHeaders wsaHeaders = new AeAddressingHeaders(aPartnerEndpoint.getSourceNamespace());
         wsaHeaders.setReferenceProperties(aPartnerEndpoint.getReferenceProperties());
         return (wsaHeaders.getReplyTo() != null || AeUtil.notNullOrEmpty(aMyEndpoint.getReferenceProperties()));
      }
      catch (AeWsAddressingException e)
      {
         throw new AeBusinessProcessException(e.getLocalizedMessage(), e);
      }
   }
   
   /**
    * Extract the endpoint url string from the service.
    * @param aWsdlService
    * @param aEndpointRef
    * @param aAddressHandlingType
    * @throws AeBusinessProcessException
    */
   protected String getEndpointUrl( Service aWsdlService, IAeWebServiceEndpointReference aEndpointRef, AeAddressHandlingType aAddressHandlingType ) throws AeBusinessProcessException
   {
      // get the port we are executin on the service and get the url
      String url = ""; //$NON-NLS-1$

      if ( aAddressHandlingType == AeAddressHandlingType.SERVICE )
      {
         Port port = aWsdlService.getPort(aEndpointRef.getServicePort());
         if ( port == null )
         {
            Object[] args = { aEndpointRef.getServiceName(), aEndpointRef.getServicePort() };
            throw new AeBusinessProcessException(AeMessages.format("AeInvokeHandler.MissingPort", args)); //$NON-NLS-1$
         }

         List extList = port.getExtensibilityElements();
         for (int i = 0; extList != null && i < extList.size(); i++)
         {
            Object obj = extList.get(i);

            if ( obj instanceof UnknownExtensibilityElement )
            {
               UnknownExtensibilityElement sop = (UnknownExtensibilityElement)obj;
               if ( "address".equals(sop.getElement().getLocalName()) ) //$NON-NLS-1$
               {
                  url = sop.getElement().getAttribute("location"); //$NON-NLS-1$
                  break;
               }
            }
         }
      }
      else if ( aAddressHandlingType == AeAddressHandlingType.ADDRESS )
      {
         url = aEndpointRef.getAddress();
      }

      // in either case, send the url through the urn mapping facility to see if
      // it maps to another url.
      return AeEngineFactory.getURNResolver().getURL(url);
   }

   /**
    * Determine the call style (RPC or Document).
    *
    * @param aWsdlService
    * @param aOperationName
    * @param aEndpointRef
    * @throws AeBusinessProcessException
    */
   protected String getRequestStyle( Service aWsdlService, String aOperationName, IAeWebServiceEndpointReference aEndpointRef ) throws AeBusinessProcessException
   {
      if ( aWsdlService == null )
      {
         return STYLE_DOCUMENT;
      }

      String requestStyle = null;

      Port port = aWsdlService.getPort(aEndpointRef.getServicePort());
      Binding binding = port.getBinding();
      List extList = binding.getExtensibilityElements();

      for (int i = 0; extList != null && i < extList.size(); i++)
      {
         Object obj = extList.get(i);

         if ( obj instanceof UnknownExtensibilityElement )
         {
            UnknownExtensibilityElement sop = (UnknownExtensibilityElement)obj;
            if ( "binding".equals(sop.getElement().getLocalName()) ) //$NON-NLS-1$
            {
               String style = sop.getElement().getAttribute("style"); //$NON-NLS-1$
               if (!AeUtil.isNullOrEmpty(style))
               {
                  requestStyle = style;
               }
               break;
            }
         }
      }

      // find out if document or rpc and get the soap action from binding/operation
      BindingOperation bop = getBindingOperation(aWsdlService, aOperationName, aEndpointRef);
      extList = bop.getExtensibilityElements();
      for (int i = 0; extList != null && i < extList.size(); i++)
      {
         Object obj = extList.get(i);

         if ( obj instanceof UnknownExtensibilityElement )
         {
            UnknownExtensibilityElement sop = (UnknownExtensibilityElement)obj;
            if ( "operation".equals(sop.getElement().getLocalName()) ) //$NON-NLS-1$
            {
               String style = sop.getElement().getAttribute("style"); //$NON-NLS-1$
               if (!AeUtil.isNullOrEmpty(style))
               {
                  requestStyle = style;
               }
            }
         }
      }
      
      return requestStyle;
   }

   /**
    * Look for the value of a soapAction attribute on the binding operation. 
    *  
    * @param aWsdlService
    * @param aOperationName
    * @param aEndpointRef
    * @throws AeBusinessProcessException
    */
   protected String getSoapAction(Service aWsdlService, String aOperationName, IAeWebServiceEndpointReference aEndpointRef) throws AeBusinessProcessException
   {
      String soapAction = null; 
      if ( aWsdlService != null )
      {
         // get the soap action from binding/operation
         BindingOperation bop = getBindingOperation(aWsdlService, aOperationName, aEndpointRef);
         List extList = bop.getExtensibilityElements();
         for (int i = 0; extList != null && i < extList.size(); i++)
         {
            Object obj = extList.get(i);

            if ( obj instanceof UnknownExtensibilityElement )
            {
               UnknownExtensibilityElement sop = (UnknownExtensibilityElement)obj;
               if ( "operation".equals(sop.getElement().getLocalName()) ) //$NON-NLS-1$
               {
                  soapAction = sop.getElement().getAttribute("soapAction"); //$NON-NLS-1$
                  break;
               }
            }
         }
      }
      return soapAction;
   }

   /**
    * Get the <code>BindingOperation</code> from the service object.
    * @param aWsdlService
    * @param aOperationName
    * @param aEndpointRef
    * @throws AeBusinessProcessException
    */
   protected BindingOperation getBindingOperation(Service aWsdlService, String aOperationName, IAeWebServiceEndpointReference aEndpointRef) throws AeBusinessProcessException
   {
      Port port = aWsdlService.getPort(aEndpointRef.getServicePort());
      Binding binding = port.getBinding();
      BindingOperation operation;
      if (binding == null || (operation = binding.getBindingOperation( aOperationName, null, null)) == null)
      {
         Object[] args = { aEndpointRef.getServiceName(), aEndpointRef.getServicePort() };
         throw new AeBusinessProcessException(AeMessages.format("AeInvokeHandler.MissingBinding", args)); //$NON-NLS-1$
      }
      return operation;
   }

   /**
    * Get the <code>PortType</code> that we're invoking.
    * @param aWsdlService
    * @param aEndpointRef
    * @param aInvoker
    * @throws AeBusinessProcessException
    */
   protected PortType getPortType(Service aWsdlService, IAeWebServiceEndpointReference aEndpointRef, IAeInvoke aInvoker) throws AeBusinessProcessException
   {
      PortType portType = null;
      if ( aWsdlService != null )
      {
         // get the operation and build the body elements
         Port port = aWsdlService.getPort(aEndpointRef.getServicePort());
         if ( port == null )
         {
            Object[] args = { aEndpointRef.getServiceName().getNamespaceURI(), aEndpointRef.getServiceName().getLocalPart(), aEndpointRef.getServicePort() };
            throw new AeBusinessProcessException(AeMessages.format("AeInvokeHandler.MissingPort", args)); //$NON-NLS-1$
         }
         if ( port.getBinding() == null )
         {
            throw new AeBusinessProcessException(AeMessages.format("AeInvokeHandler.MissingBinding", aEndpointRef.getServicePort())); //$NON-NLS-1$
         }
         portType = port.getBinding().getPortType();
      }
      else
      {
         // TODO (RN) May want to revisit this to pass the IAeProcessDeployment instead of looking it up
         IAeContextWSDLProvider wsdlProvider = AeEngineFactory.getDeploymentProvider().findDeploymentPlan(aInvoker.getProcessId(), aInvoker.getProcessName());
         if (wsdlProvider != null)
         {
            AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForPortType(wsdlProvider, aInvoker.getPortType());
            if ( def != null )
               portType = def.getPortType(aInvoker.getPortType());
            else
               throw new AeBusinessProcessException(AeMessages.format("AeInvokeHandler.NoDefForPortType", aInvoker.getPortType())); //$NON-NLS-1$
         }
      }

      return portType;
   }

   /**
    * Extracts the message data from a fault details element.
    * @param aWsdlFault
    * @param aFirstDetailElement
    */
   protected AeWebServiceMessageData extractMessageData(Fault aWsdlFault, Element aFirstDetailElement)
   {
      AeWebServiceMessageData data = new AeWebServiceMessageData(aWsdlFault.getMessage().getQName());
      String partName = (String)aWsdlFault.getMessage().getParts().keySet().iterator().next();
      if ( isSimpleType(aFirstDetailElement) )
      {
         data.setData(partName, AeXmlUtil.getText(aFirstDetailElement));
      }
      else
      {
         Document doc = AeXmlUtil.newDocument();
         Element details = (Element)doc.importNode(aFirstDetailElement, true);
         doc.appendChild(details);
         data.setData(partName, doc);
      }
      return data;
   }

   /**
    * Helper method that checks to see if the passed elements data is a simple type
    * or complex type.
    * @param aElement The element to check the contents of.
    */
   protected boolean isSimpleType(Element aElement)
   {
      boolean simple = false;
      // TODO Simple check for now, a complex type will have attributes and/or child elements.
      if ( AeUtil.isNullOrEmpty(aElement.getNamespaceURI()) && AeXmlUtil.getFirstSubElement(aElement) == null )
      {
         simple = true;
         if ( aElement.hasAttributes() )
         {
            NamedNodeMap attrs = aElement.getAttributes();
            for (int i = 0; i < attrs.getLength(); ++i)
            {
               String nsURI = attrs.item(i).getNamespaceURI();
               if(! IAeBPELConstants.W3C_XMLNS.equals(nsURI) &&
                  ! IAeBPELConstants.W3C_XML_SCHEMA_INSTANCE.equals(nsURI))
               {
                  simple = false;
                  break;
               }
            }
         }
      }
      return simple;
   }
   
   /**
    * Get policy driven call properties.
    * @param aPolicyList
    */
   protected Map getPolicyDrivenProperties( List aPolicyList ) throws AeBusinessProcessException
   {
      try {
         // Map policy assertions to call properties
         if (!AeUtil.isNullOrEmpty(aPolicyList)) 
         {
            // get the main policy mapper
            IAePolicyMapper mapper = AeEngineFactory.getPolicyMapper();
            // get Client Request properties
            return mapper.getCallProperties(aPolicyList);
         }
         else
         {
            return Collections.EMPTY_MAP;
         }
      } 
      catch (Throwable t) 
      {
         throw new AeBusinessProcessException(t.getLocalizedMessage(),t); 
      }
   }
   
   /**
    * Gets all of the policy attachments from the endpoint and wsdl definitions that may
    * be required for the invoke
    *
    * @param aDef
    * @param aInvoke
    * @param aEndpoint
    * @throws AeBusinessProcessException
    */
   protected List getWsdlPolicies(AeBPELExtendedWSDLDef aDef, IAeInvoke aInvoke, IAeEndpointReference aEndpoint) throws AeBusinessProcessException
   {
      IAeContextWSDLProvider wsdlProvider = AeEngineFactory.getDeploymentProvider().findDeploymentPlan(aInvoke.getProcessId(), aInvoke.getProcessName());
      return AeWSDLPolicyHelper.getEffectiveOperationPolicies(wsdlProvider, aEndpoint, aInvoke.getPortType(), aInvoke.getOperation(), null);
   }
   
   /**
    * Gets the WSDL def for an invoke.
    * 
    * If the endpoint includes a service name, we'll lookup the wsdl for the service.
    * If no service, we'll lookup the wsdl for the invoke's portType 
    * 
    * @param aInvoke
    * @param aEndpointRef
    * @throws AeBusinessProcessException
    */
   protected AeBPELExtendedWSDLDef getWsdlDef(IAeInvoke aInvoke, IAeWebServiceEndpointReference aEndpointRef) throws AeBusinessProcessException
   {
      AeBPELExtendedWSDLDef def = null;
      if ( aEndpointRef.getServiceName() != null )
      {
         // they have a service name, see if we can find the wsdl for it
         def = AeWSDLDefHelper.getWSDLDefinitionForService( AeEngineFactory.getCatalog(), aEndpointRef.getServiceName() );
         // if not global wsdl check the context wsdl for service
         if(def == null)
         {
            IAeContextWSDLProvider wsdlProvider = AeEngineFactory.getDeploymentProvider().findDeploymentPlan(aInvoke.getProcessId(), aInvoke.getProcessName());
            if (wsdlProvider != null)
               def = AeWSDLDefHelper.getWSDLDefinitionForService( wsdlProvider, aEndpointRef.getServiceName() );
         }
      }
      else 
      {
         IAeContextWSDLProvider wsdlProvider = AeEngineFactory.getDeploymentProvider().findDeploymentPlan(aInvoke.getProcessId(), aInvoke.getProcessName());
         if (wsdlProvider != null)
            def = AeWSDLDefHelper.getWSDLDefinitionForPortType(wsdlProvider, aInvoke.getPortType());
      }
      return def;
   }
   
   /**
    * If the process initiator mapping header policy is in effect, we will add a header as a 
    * reference property to the endpoint.
    * 
    * If the header already exists, we will leave the existing header untouched
    * to allow for overrides through an assign to the partner endpoint
    * 
    * @param aInvoke
    * @param aContext
    * @throws AeBusinessProcessException
    */
   protected void handlePrincipalHeader(IAeInvoke aInvoke, AeInvokeContext aContext) throws AeBusinessProcessException
   {
      // check for principal header policy 
      QName principalName = (QName) aContext.getCallProperties().get(IAePolicyConstants.TAG_ASSERT_MAP_PROCESS_INTIATOR);
      if (!AeUtil.isNullOrEmpty(principalName))
      {
         IAeEndpointReference endpoint = aContext.getEndpoint();
         Element principalHeader = null;
         // look for existing principal header
         for (Iterator it = endpoint.getReferenceProperties().iterator(); it.hasNext();)
         {
            Element header = (Element) it.next();
            QName headerName = new QName(header.getNamespaceURI(), header.getLocalName());
            if (IAePolicyConstants.PRINCIPAL_HEADER.equals(headerName))
            {
               principalHeader = header;
               break;
            }
         }
         // not found, create a new element
         if (principalHeader == null)
         {
            String principal = aInvoke.getProcessInitiator();
            if (AeUtil.isNullOrEmpty(principal))
            {
               principal = IAePolicyConstants.ANONYMOUS_PRINCIPAL;
            }
            principalHeader = AeXmlUtil.createElement(principalName.getNamespaceURI(), "abx", principalName.getLocalPart()); //$NON-NLS-1$
            AeXmlUtil.addText(principalHeader, principal);
            endpoint.getReferenceProperties().add(principalHeader);
         }
      }
   }
}
