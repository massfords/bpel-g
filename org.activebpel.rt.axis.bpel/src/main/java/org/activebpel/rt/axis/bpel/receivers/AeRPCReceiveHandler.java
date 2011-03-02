//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/receivers/AeRPCReceiveHandler.java,v 1.3 2007/08/30 11:00:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.receivers;


import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.axis.bpel.AeTypeMappingHelper;
import org.activebpel.rt.axis.ser.AeMessageContextTypeMapper;
import org.activebpel.rt.axis.ser.AeSimpleValueWrapper;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessageDataFactory;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.server.engine.receive.AeExtendedMessageContext;
import org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.AeMessageContext;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.constants.Use;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Receive handler for RPC (Encoded and Literal) SOAP bindings
 */
public class AeRPCReceiveHandler extends AeAxisReceiveHandler
{
   /**
    * No-arg constructor
    */
   public AeRPCReceiveHandler()
   {

   }
   
   /**
    * Extracts the parts from the message and populates the IAeMessageData. For rpc-encoding
    * each part should be accessible by its part name in the WSDL.
    *  
    * Input data type: soapenv:Envelope
    * Output data type: soapenv:Envelope
    * 
    * @see org.activebpel.rt.bpel.server.engine.receive.AeDefaultReceiveHandler#mapInputData(org.activebpel.rt.bpel.impl.IAeProcessPlan, org.activebpel.rt.bpel.server.engine.receive.AeExtendedMessageContext, org.w3c.dom.Document[])
    */
   protected IAeMessageData mapInputFromSOAP(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, SOAPEnvelope aEnv) throws AeBusinessProcessException
   {
      String opName = null;
      try
      {
         // Get the WSDL definition for the service 
         QName portTypeQName = getMyRolePortTypeQName(aPlan, aContext);
         AeBPELExtendedWSDLDef defFromService = AeWSDLDefHelper.getWSDLDefinitionForPortType(aPlan, portTypeQName);
         
         // For an RPC SOAP request there should only be a single body element   
         if (aEnv.getBodyElements().size() < 1)
            throw new AeBusinessProcessException(AeMessages.getString("AeBpelRPCHandler.ERROR_0")); //$NON-NLS-1$

         MessageContext axisContext = (MessageContext) aContext.getProperty(MessageContext.class.getName());
         
         // Get the request body from the SOAP envelope
         RPCElement reqBody = findFirstRPCElement(aEnv);
   
         // Find the proper operation based on the SOAP request operation
         Operation operation = null;
         PortType portType = defFromService.getPortType(portTypeQName);
         for (Iterator iter=portType.getOperations().iterator(); iter.hasNext() && operation == null;)
         {
            Operation op = (Operation)iter.next();
            if (op.getName().equals(reqBody.getMethodName()))
               operation = op;
         }         
         
         // This is an error if we could not find the operation
         if (operation == null)
            throw new AeBusinessProcessException(AeMessages.format("AeBpelRPCHandler.ERROR_1", reqBody.getMethodName())); //$NON-NLS-1$
   
         opName = operation.getName();
         
         if (AeUtil.isNullOrEmpty(aContext.getOperation()))
         {
            aContext.setOperation(operation.getName());            
         }
         
         // Create the input message and fill in the parameter data for the request
         QName inMsg = operation.getInput().getMessage().getQName();
         IAeMessageData inputMsg = AeMessageDataFactory.instance().createMessageData(inMsg);
         
         // get the def that has the messages
         AeBPELExtendedWSDLDef inputMessageDef = AeWSDLDefHelper.getWSDLDefinitionForMsg(aPlan, operation.getInput().getMessage().getQName());
         if( inputMessageDef == null )
         {
            // this could happen if user replaces existing wsdl with new incompatible
            // wsdl version but DOES NOT expire the existing service
            throw new AeBusinessProcessException( AeMessages.format("AeBpelRPCHandler.ERROR_7", inMsg)); //$NON-NLS-1$
         }
         AeBPELExtendedWSDLDef outputMessageDef = null;
         
         if (operation.getOutput() != null)
         {
            outputMessageDef = AeWSDLDefHelper.getWSDLDefinitionForMsg(aPlan, operation.getOutput().getMessage().getQName());
            if (outputMessageDef == null)
            {
               // this could happen if user replaces existing wsdl with new incompatible
               // wsdl version but DOES NOT expire the existing service
               throw new AeBusinessProcessException( AeMessages.format("AeBpelRPCHandler.ERROR_7", operation.getOutput().getMessage().getQName())); //$NON-NLS-1$
            }
            aContext.setProperty(AE_CONTEXT_KEY_WSDL_OUTPUT, outputMessageDef);
         }
   
         AeMessageContextTypeMapper msgContextTypeMapper = new AeMessageContextTypeMapper(axisContext);
         AeTypeMappingHelper typeMappingHelper = new AeTypeMappingHelper(inputMessageDef, outputMessageDef, operation);
         typeMappingHelper.setEncoded((Use) aContext.getProperty(Use.class.getName()) == Use.ENCODED);
         typeMappingHelper.registerTypes(msgContextTypeMapper); 
         
         extractMessageParts(reqBody, operation, inputMsg);
         
         return inputMsg;
      }
      catch (Exception e)
      {
         AeException.logError(e, AeMessages.format("AeBpelRPCHandler.ERROR_3", opName)); //$NON-NLS-1$
         throw new AeBusinessProcessException(e.getLocalizedMessage());
      }
   }

   /**
    * Finds the first RPC element.  If ws-security is used, there
    * may be reference elements in addition to the body
    * @param aEnv
    * @throws AxisFault
    * @return RPCElement or null if none found
    */
   private RPCElement findFirstRPCElement(SOAPEnvelope aEnv) throws AxisFault
   {
      Vector bodyElements = aEnv.getBodyElements();
      RPCElement reqBody = null;
      for (Iterator it = bodyElements.iterator(); it.hasNext();)
      {
         Object element = it.next();
         if (element instanceof RPCElement)
         {
            reqBody = (RPCElement) element;
            break;
         }
      }
      return reqBody;
   }

   /**
    * The message data within the response will have a type of soapenv:Envelope and a single 
    * element containing the SOAPEnvelope document. 
    *
    * @see org.activebpel.rt.bpel.server.engine.receive.AeDefaultReceiveHandler#mapOutputData(org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext, org.activebpel.wsio.IAeWebServiceResponse)
    */
   public IAeWebServiceResponse mapOutputData(IAeExtendedMessageContext aContext, IAeWebServiceResponse aResponse) throws AeBusinessProcessException
   {
      try
      {
         MessageContext axisContext = (MessageContext) aContext.getProperty(MessageContext.class.getName());
         SOAPEnvelope reqEnv = axisContext.getRequestMessage().getSOAPEnvelope();
         RPCElement reqBody = findFirstRPCElement(reqEnv);
   
         // If we don't have a response message, make sure we set one up
         // with the appropriate versions of SOAP and Schema
         SOAPEnvelope resEnv = (SOAPEnvelope) createEnvelope();
   
         // Setup the response message body based on information in the request body
         RPCElement resBody = new RPCElement(reqBody.getMethodName() + "Response"); //$NON-NLS-1$
         resBody.setPrefix(reqBody.getPrefix());
         resBody.setNamespaceURI(reqBody.getNamespaceURI());
         resBody.setEncodingStyle(aContext.getEncodingStyle());
         
         // Get the definition for the output message type
         IAeWebServiceMessageData outputMsg = aResponse.getMessageData();
         AeBPELExtendedWSDLDef def = (AeBPELExtendedWSDLDef) aContext.getProperty(AE_CONTEXT_KEY_WSDL_OUTPUT);
         if (def == null)
         {
            IAeProcessPlan plan = getProcessPlan(AeMessageContext.convert(aContext));
            def = AeWSDLDefHelper.getWSDLDefinitionForMsg(plan, outputMsg.getMessageType());
         }
         
         Message outputMessage = def.getMessage(outputMsg.getMessageType());

         // Loop through all parts for the ouput message and add an output param to the response body
         for (Iterator iter=outputMessage.getOrderedParts(null).iterator(); iter.hasNext();)
         {
            Part part = (Part) iter.next();
            Object partData = outputMsg.getMessageData().get(part.getName());
            
            // if it's a derived simple type, then add a wrapper so it'll hit our serializer
            if (!(partData instanceof Document) && def.isDerivedSimpleType(part))
            {
               partData = new AeSimpleValueWrapper(partData);
            }

            // if it's a Document and a type, make sure it has an xsi:type value
            if (partData instanceof Document && AeBPELExtendedWSDLDef.isXsiTypeRequired(part, (Document)partData))
            {
               AeXmlUtil.declarePartType((Document)partData, part.getTypeName());
            }
            
            // the part name will be namespace qualified only if it's an element
            QName partQName = null;
            if (part.getElementName() == null)
            {
               partQName = new QName("", part.getName());  //$NON-NLS-1$ 
            }
            else
            {
               Document doc = (Document) partData;
               partQName = AeXmlUtil.getElementType(doc.getDocumentElement());               
            }
   
            RPCParam rpcParam = new RPCParam(partQName, partData);
            resBody.addParam(rpcParam);

            // The MessageContext will determine the current operation being invoked when
            // it deserializes the message. However, if there are no parts then 
            // no deserialization took place. As a result, I trigger the deserialization
            // here to ensure that the operation is set since I need it to set the param descriptor 
            if (aContext.getOperation() == null)
            {
               reqBody.deserialize();
            }

            if (aContext.getOperation() != null)
            {
               rpcParam.setParamDesc(axisContext.getOperation().getParamByQName(new QName("", part.getName()))); //$NON-NLS-1$
            }
         }
         // Add the response body to the response envelope
         resEnv.addBodyElement(resBody);
         
         mapResponseAddressing(resEnv, aContext.getWsAddressingHeaders());
         
         return createWsResponse(aResponse, resEnv);         
      }
      catch (Exception ex)
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeBpelRPCHandler.ERROR_6"), ex); //$NON-NLS-1$
      }
   }

   /**
    * Extracts the parts from the message and populates the IAeMessageData. For rpc-encoding
    * each part should be accessible by its part name in the WSDL.
    * 
    * @param aReqBody
    * @param aOperation
    * @param aInputMsg
    * @throws SAXException
    * @throws AxisFault
    */
   protected void extractMessageParts(RPCElement aReqBody, Operation aOperation, IAeMessageData aInputMsg) throws SAXException, AxisFault
   {
      List paramOrder = AeBPELExtendedWSDLDef.getParameterOrder(aOperation);
      
      // The parts in the order that we're expecting them
      List parts = aOperation.getInput().getMessage().getOrderedParts(paramOrder);
      
      // The rpc params from the request. This call will trigger deserialization 
      // of the message
      List rpcParams = aReqBody.getParams();
      
      // The assumption is that the two lists are in synch. If not, then we should
      // catch in validation.
      
      int i=0;
      for (Iterator iter=parts.iterator(); iter.hasNext(); i++)
      {
         Part part = (Part) iter.next();
         String partName = part.getName();
         RPCParam param = aReqBody.getParam(partName);
         
         if (param == null && i < rpcParams.size())
         {
            // try getting it by offset if we couldn't find it by name
            param = (RPCParam) rpcParams.get(i);
         }
         
         // if the part is missing, we'll add it as a null to the messageData object. If the user has validation
         // turned on then this will fault in the engine. If validation is turned off, then the message will
         // go through. Doesn't seem right to fault here for a missing part if we allow validation to be turned off.
         Object value = param != null? param.getObjectValue() : null;
         aInputMsg.setData(partName, AeTypeMappingHelper.fixPart(part, value));
      }
   }
   
}
