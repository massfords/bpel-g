//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis/src/org/activebpel/rt/axis/AeHandler.java,v 1.30 2007/12/11 22:28:42 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis;

import java.util.HashMap;
import java.util.Iterator;

import javax.wsdl.Binding;
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeWSDLException;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.IAeBPELExtendedWSDLConst;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.constants.Style;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.providers.BasicProvider;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The base handler for web services under an Axis framework.
 */
public abstract class AeHandler extends BasicProvider
{
   /** Key for property which holds the AeBPELExtendedWSDLDef this service operates against */
   public static final String WSDL_DEF_ENTRY = "org.activebpel.rt.axis.WsdlDefEntry"; //$NON-NLS-1$
   
   /** Key for property which specifies the AePartnerLinkDef which this service operates against*/
   public static final String PARTNER_LINK_ENTRY = "org.activebpel.rt.axis.PartnerLinkEntry";  //$NON-NLS-1$

   /** Key for property which holds the port type QName which service operates against*/
   public static final String PORT_TYPE_ENTRY = "org.activebpel.rt.axis.PortTypeEntry"; //$NON-NLS-1$

   /** Document using in creating elements for the extensibility element */
   private Document mDocument; 

   /**
    * Generates the WSDL for this provider, using current WSDL definition
    * @see org.apache.axis.Handler#generateWSDL(org.apache.axis.MessageContext)
    */
   public void generateWSDL(MessageContext aContext) throws AxisFault
   {
      // Load the WSDL Document from the file specified in the service description
      AeBPELExtendedWSDLDef def = getExtendedWSDLDef(aContext.getService().getServiceDescription());
      if (def == null)
         throw new AxisFault(AeMessages.getString("AeHandler.ERROR_2")); //$NON-NLS-1$

      generateWSDL(aContext, def);
   }
   
   /**
    * Generates the WSDL for this provider, given an AeBPELExtendedWSDLDef object.
    * @param aContext the message context 
    */
   protected void generateWSDL(MessageContext aContext, AeBPELExtendedWSDLDef aDef) throws AxisFault
   {
      try
      {
         ServiceDesc serviceDesc = aContext.getService().getServiceDescription();
         // synchronizing on the def object here because we're going to remove its
         // bindings and add them back in using our endpoint url for the service and such.
         synchronized(aDef)
         {
            // make sure the soap namespace for wsdl extensions is mapped at the def layer as some tools (eclipse wst), have problems otherwise
            if( aDef.getWSDLDef().getPrefix( IAeBPELExtendedWSDLConst.SOAP_NAMESPACE ) == null )
               aDef.getWSDLDef().addNamespace(createPrefix(aDef, IAeBPELExtendedWSDLConst.SOAP_PREFIX_DEFAULT), IAeBPELExtendedWSDLConst.SOAP_NAMESPACE); 
            
            // Remove any bindings specified in the original WSDL and allow inheriting class to set
            aDef.getBindings().clear();
            addBindingSpecification(serviceDesc);
   
            // Remove any services specified in the original WSDL and allow inheriting class to set
            aDef.getServices().clear();
            String transportURL = (String)aContext.getProperty(MessageContext.TRANS_URL);
            addServiceSpecification(serviceDesc, transportURL);
            
            // ensure that there is a ns prefix for the target namespace
            // we need to do this because wsdl4j code expects a prefix for the 
            // port namespace uri (which we explicitly set to be the the target namespace of the def)
            if( aDef.getWSDLDef().getPrefix( aDef.getTargetNamespace() ) == null )
               aDef.getWSDLDef().addNamespace(createPrefix(aDef, "aetns"), aDef.getTargetNamespace()); //$NON-NLS-1$
   
            // Set the document in the message context to be retrieved by the Axis servlet
            aContext.setProperty("WSDL", aDef.write()); //$NON-NLS-1$
         }
      }
      catch (AeException e)
      {
         throw AxisFault.makeFault(e);
      }
   }
   
   /**
    * Generate a suitable prefix for the target namespace.
    * @param aDef
    * @param aBase
    * TODO (CK) use addnamespacemapping from aeextendedwsdldef and change so it reuses prefixes for namespaecs if avaiable fix other calls
    */
   protected String createPrefix( AeBPELExtendedWSDLDef aDef, String aBase )
   {
      int index = 0;
      String generatedPrefix = aBase;
      while( aDef.getWSDLDef().getNamespace( generatedPrefix ) != null )
      {
         generatedPrefix = generatedPrefix+(index++);
      }
      return generatedPrefix;
   }
   
   /**
    * Getter for the port type qname
    * @param aServiceDesc
    */
   protected QName getPortTypeQName(ServiceDesc aServiceDesc)
   {
      return (QName) aServiceDesc.getProperty(PORT_TYPE_ENTRY);
   }
   
   /**
    * Setter for the port type qname
    * @param aServiceDesc
    * @param aQName
    */
   protected void setPortTypeQName(ServiceDesc aServiceDesc, QName aQName)
   {
      aServiceDesc.setProperty(PORT_TYPE_ENTRY, aQName);
   }
   
   /**
    * Convenience method for getting the extended wsdl def from the service desc.
    */
   protected AeBPELExtendedWSDLDef getExtendedWSDLDef(ServiceDesc aServiceDesc)
   {
      return (AeBPELExtendedWSDLDef)aServiceDesc.getProperty(WSDL_DEF_ENTRY);
   }
   
   /**
    * Sets the extended wsdl def on the service desc.
    * @param aServiceDesc
    * @param aDef
    */
   protected void setExtendedWSDLDef(ServiceDesc aServiceDesc, AeBPELExtendedWSDLDef aDef) throws AeWSDLException
   {
      aServiceDesc.setProperty(WSDL_DEF_ENTRY, aDef);
   }

   /**
    * Creates the WSDL binding specification for the provider.
    * @param aServiceDesc the service description
    */
   protected void addBindingSpecification(ServiceDesc aServiceDesc)
   {
      Definition def = getExtendedWSDLDef(aServiceDesc).getWSDLDef();

      // Get the portType from the WSDL definition
      PortType portType = def.getPortType(getPortTypeQName(aServiceDesc));

      // Create the binding element for the handler
      Binding binding = def.createBinding();
      binding.setUndefined(false);
      binding.setPortType(portType);
      binding.setQName(new QName(def.getTargetNamespace(), aServiceDesc.getName() + "Binding")); //$NON-NLS-1$

      // Create the DOM element which represents the SOAP binding ext element and add to binding
      Element element = getDocument().createElementNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "binding"); //$NON-NLS-1$
      element.setPrefix(def.getPrefix( IAeBPELExtendedWSDLConst.SOAP_NAMESPACE ));
      element.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "style", aServiceDesc.getStyle().getName()); //$NON-NLS-1$
      element.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "transport", IAeBPELExtendedWSDLConst.SOAP_HTTP_TRANSPORT); //$NON-NLS-1$
      binding.addExtensibilityElement(createSoapExtElement("binding", element)); //$NON-NLS-1$
      def.addBinding(binding);

      // Create declarations for all allowed operations
      for (Iterator iter=portType.getOperations().iterator(); iter.hasNext();)
      {
         Operation operation=(Operation)iter.next();
         if (aServiceDesc.getAllowedMethods().contains(operation.getName()))
            binding.addBindingOperation(createBindingOperation(aServiceDesc, def, portType, operation));
      }
   }

   /**
    * Creates the WSDL service specification for the provider.
    * @param aServiceDesc the service description
    * @param aTransportURL the URL which the request is issued on
    */
   protected void addServiceSpecification(ServiceDesc aServiceDesc, String aTransportURL)
   {
      Definition def = getExtendedWSDLDef(aServiceDesc).getWSDLDef();

      // Create the binding element for the RPC handler
      Service service = def.createService();
      service.setQName(new QName(def.getTargetNamespace(), aServiceDesc.getName()));
      def.addService(service);

      Port port = def.createPort();
      port.setName(aServiceDesc.getName() + "Port"); //$NON-NLS-1$
      QName bindingName = new QName(def.getTargetNamespace(), aServiceDesc.getName() + "Binding"); //$NON-NLS-1$
      Binding portBinding = def.getBinding(bindingName);
      port.setBinding(portBinding);

      // Create the DOM element which represents the SOAP binding ext element and add to binding
      Element element = getDocument().createElementNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "address"); //$NON-NLS-1$
      element.setPrefix(def.getPrefix( IAeBPELExtendedWSDLConst.SOAP_NAMESPACE ));
      element.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "location", aTransportURL); //$NON-NLS-1$
      port.addExtensibilityElement(createSoapExtElement("address", element)); //$NON-NLS-1$

      service.addPort(port);
   }

   /**
    * Creates a binding operation to be added to a binding.
    * @param aServiceDesc the service description
    * @param aDef the WSDL definition
    * @param aPortType the portType for the operation
    * @param aOperation the operation we are creating the binding operation for.
    */
   protected BindingOperation createBindingOperation(ServiceDesc aServiceDesc, Definition aDef, PortType aPortType, Operation aOperation)
   {
      boolean isRPCStyle = Style.RPC_STR.equals(aServiceDesc.getStyle().getName());
      
      // Create the operation to be added to the binding
      BindingOperation bindingOp = aDef.createBindingOperation();
      bindingOp.setOperation(aOperation);
      bindingOp.setName(aOperation.getName());
      
      // Create the DOM element which represents the SOAP operation ext element and add to binding
      Element element = getDocument().createElementNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "operation"); //$NON-NLS-1$
      element.setPrefix(aDef.getPrefix( IAeBPELExtendedWSDLConst.SOAP_NAMESPACE ));
      element.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "soapAction", ""); //$NON-NLS-1$ //$NON-NLS-2$
      element.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "style", aServiceDesc.getStyle().getName()); //$NON-NLS-1$
      bindingOp.addExtensibilityElement(createSoapExtElement("operation", element)); //$NON-NLS-1$
      
      if (aOperation.getInput() != null)
      {
         // Create the SOAP body ext element and add it to the operation
         Element bodyElement = createBodyElement(aServiceDesc, aDef, getDocument(), aPortType, isRPCStyle);
         BindingInput bindingInput = aDef.createBindingInput();
         bindingInput.addExtensibilityElement(createSoapExtElement("body", bodyElement)); //$NON-NLS-1$
         bindingOp.setBindingInput(bindingInput);
      }
      
      if (aOperation.getOutput() != null)
      {
         // Create the SOAP body ext element and add it to the operation
         Element bodyElement = createBodyElement(aServiceDesc, aDef, getDocument(), aPortType, isRPCStyle);
         BindingOutput bindingOutput = aDef.createBindingOutput();
         bindingOutput.addExtensibilityElement(createSoapExtElement("body", bodyElement)); //$NON-NLS-1$
         bindingOp.setBindingOutput(bindingOutput);
      }
      
      for (Iterator faultIter=aOperation.getFaults().values().iterator(); faultIter.hasNext();)
      {
         Fault fault = (Fault)faultIter.next();
         BindingFault bindingFault = aDef.createBindingFault();
         bindingFault.setName(fault.getName());
      
         // Create the DOM element which represents the SOAP fault operation ext element
         Element faultElement = getDocument().createElementNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "fault"); //$NON-NLS-1$
         faultElement.setPrefix(aDef.getPrefix( IAeBPELExtendedWSDLConst.SOAP_NAMESPACE ));
         faultElement.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "use", aServiceDesc.getUse().getName()); //$NON-NLS-1$
         faultElement.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "name", fault.getName()) ; //$NON-NLS-1$
      
         // RPC style bindings should declare encoding specifications
         if (isRPCStyle)
            faultElement.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "encodingStyle", aServiceDesc.getUse().getEncoding()); //$NON-NLS-1$
      
         // Create the SOAP fault ext element and add it to the operation
         bindingFault.addExtensibilityElement(createSoapExtElement("fault", faultElement)); //$NON-NLS-1$
         bindingOp.addBindingFault(bindingFault);
      }
      
      return bindingOp;
   }

   /**
    * Creates a SOAP body to be added as an extensibility element of our WSDL. 
    * @param aServiceDesc the service descriptor
    * @param aDoc the DOM used for creating the element
    * @param aDef the wsdl def
    * @param aPortType provides the namespace for the body element in the event that we're producing RPC Literal bindings
    * @param aIsRPCStyle flag indicating if this is RPC style as opposed to document style binding
    */
   protected Element createBodyElement(ServiceDesc aServiceDesc, Definition aDef, Document aDoc, PortType aPortType, boolean aIsRPCStyle)
   {
      // Create the DOM element which represents the SOAP input/output operation ext element
      Element bodyElement = aDoc.createElementNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "body"); //$NON-NLS-1$
      bodyElement.setPrefix(aDef.getPrefix( IAeBPELExtendedWSDLConst.SOAP_NAMESPACE ));
      String use = aServiceDesc.getUse().getName();
      bodyElement.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "use", use); //$NON-NLS-1$
      
      // RPC style bindings should declare encoding specifications
      if (aIsRPCStyle)
      {
         bodyElement.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "encodingStyle", aServiceDesc.getUse().getEncoding()); //$NON-NLS-1$
         
         if ("literal".equals(use)) //$NON-NLS-1$
         {
            bodyElement.setAttributeNS(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, "namespace", aPortType.getQName().getNamespaceURI()); //$NON-NLS-1$
         }
      }
      
      return bodyElement;
   }

   /**
    * Helper method to create a SOAP extensibility element given the extensibility name
    * and DOM Element which is the basis for the extensibility element.
    * @param aName the name of the extensibility element
    * @param aElement the basis for the extensibility element
    */
   protected ExtensibilityElement createSoapExtElement(String aName, Element aElement)
   {
      // Create the SOAP fault ext element and add it to the operation
      UnknownExtensibilityElement extElement = new UnknownExtensibilityElement();
      extElement.setElement(aElement);
      extElement.setElementType(new QName(IAeBPELExtendedWSDLConst.SOAP_NAMESPACE, aName));

      return extElement;
   }
   
   /**
    * Returns a document object used to create elements used for extensibility elements.
    */
   protected Document getDocument()
   {
      if (mDocument == null)
         mDocument = new AeXMLParserBase().createDocument();
         
      return mDocument;
   }

   /**
    * Maps the message data contained in the data map to the given input message.
    * The data map specifies a Part to SOAPBodyElement relationship, in which 
    * the SOAPBodyElement contains the actual data which needs to be mapped.
    * @param aServiceDesc the service description for the service provider
    * @param aInputMsg the input message to be populated with data
    * @param aDataMap map to store part to element mappings
    * @throws Exception
    */
   protected void mapInputData(ServiceDesc aServiceDesc, IAeMessageData aInputMsg, HashMap aDataMap) throws Exception
   {
      AeBPELExtendedWSDLDef def = getExtendedWSDLDef(aServiceDesc);
      for (Iterator iter=aDataMap.keySet().iterator(); iter.hasNext();)
      {
         // Get the Part from the data map and determine the part type
         Part part = (Part)iter.next();
         XMLType type = null;
         boolean complex = false;
         if (part.getTypeName() != null)
         {
            type = def.findType(part.getTypeName());
            complex = AeXmlUtil.isComplexOrAny(type);
         }
         else if (part.getElementName() != null)
         {
            complex = true;
            ElementDecl element = def.findElement(part.getElementName());
            if (element != null) 
               type = element.getType(); 
         }

         Document doc = (Document)aDataMap.get(part);
         if (type == null || complex)
         {
            // if part declared as type then make sure the root is the part name 
            // with no namespace (per WS-I BP 1)
            if (part.getTypeName() != null)
            {
               Element root = doc.getDocumentElement();
               if(! AeUtil.compareObjects(part.getName(), root.getLocalName())  ||
                  ! AeUtil.isNullOrEmpty(root.getNamespaceURI()))
               {
                  doc = AeXmlUtil.createMessagePartTypeDocument(part.getName(), root);
               }
            }
            aInputMsg.setData(part.getName(), doc);
         }
         else 
         {
            // Concatenate all text nodes to get the data value
            aInputMsg.setData(part.getName(), AeXmlUtil.getText(doc.getDocumentElement()));
         }
      }
   }
}