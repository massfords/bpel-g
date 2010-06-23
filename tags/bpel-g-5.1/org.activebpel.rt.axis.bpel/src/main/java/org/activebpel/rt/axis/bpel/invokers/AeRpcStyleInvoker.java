//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/invokers/AeRpcStyleInvoker.java,v 1.13 2008/01/11 19:39:27 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.invokers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.Message;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.axis.bpel.AeTypeMappingHelper;
import org.activebpel.rt.axis.ser.AeCallTypeMapper;
import org.activebpel.rt.axis.ser.AeSimpleValueWrapper;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.AeBindingUtils;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.message.RPCParam;
import org.w3c.dom.Document;

/**
 * Calls an rpc style endpoint.
 */
public class AeRpcStyleInvoker extends AeSOAPInvoker
{
   /**
    * @see org.activebpel.rt.axis.bpel.invokers.IAeInvoker#invoke(org.activebpel.rt.axis.bpel.invokers.AeAxisInvokeContext)
    */
   public void invoke( AeAxisInvokeContext aContext ) throws AeException, RemoteException
   {
      invokeRpcCall( aContext );
   }
   
   /**
    * Calls an rpc style endpoint on behalf of passed invoke context object.
    * @param aContext
    * @throws AeBusinessProcessException
    */
   protected void invokeRpcCall( AeAxisInvokeContext aContext ) throws AeException, RemoteException
   {
      Use use = getUse(aContext);
      addOperationToCall(aContext, use);
      setupOperationQName(aContext);

      // Highly unlikely this will ever be null, only situation would be if process got deleted in time it took to perform invoke
      IAeContextWSDLProvider wsdlProvider = AeEngineFactory.getDeploymentProvider().findDeploymentPlan(aContext.getInvoke().getProcessId(), aContext.getInvoke().getProcessName());
      if (wsdlProvider == null)
         return;

      // register type mappings
      AeBPELExtendedWSDLDef inputMessageDef = AeWSDLDefHelper.getWSDLDefinitionForMsg(wsdlProvider, aContext.getOperation().getInput().getMessage().getQName());
      AeBPELExtendedWSDLDef outputMessageDef = null;
      
      if(aContext.getOperation().getOutput() != null)
      {
         outputMessageDef = AeWSDLDefHelper.getWSDLDefinitionForMsg(wsdlProvider, aContext.getOperation().getOutput().getMessage().getQName());
      }

      AeCallTypeMapper typeMapper = new AeCallTypeMapper( aContext.getCall() );
      AeTypeMappingHelper typeMappingHelper = new AeTypeMappingHelper(inputMessageDef, outputMessageDef, aContext.getOperation());
      typeMappingHelper.setEncoded(aContext.getCall().getOperation().getUse() == Use.ENCODED);
      typeMappingHelper.registerTypes(typeMapper);
      
      // Format the message into the call parameters
      ArrayList list = new ArrayList();
      Map messageData  = getMessageData(aContext);
      List outboundAttachments = addAttachments(aContext);
      AeWebServiceMessageData outputMsg = null;
      Object obj;
      try
      { 
         Message input = aContext.getOperation().getInput().getMessage();
         for (Iterator iter = aContext.getCall().getOperation().getAllInParams().iterator(); iter.hasNext();)
         {
            String partName = ((ParameterDesc) iter.next()).getName();
            
            Part part = input.getPart(partName);
            Object data = messageData.get(partName);
            if (data instanceof Document && AeBPELExtendedWSDLDef.isXsiTypeRequired(part, (Document) data))
            {
               AeXmlUtil.declarePartType((Document)data, part.getTypeName());
            }      
            if (inputMessageDef.isDerivedSimpleType(part))
            {
             	AeSimpleValueWrapper wrapper = new AeSimpleValueWrapper(data);
             	RPCParam param = new RPCParam(part.getName(), wrapper);
             	list.add( param );
            }
            else if (part.getElementName() != null)
            {    
             	Document doc = (Document)data;
             	QName elementName = AeXmlUtil.getElementType(doc.getDocumentElement());
             	RPCParam param = new RPCParam(elementName, data);
             	list.add(param);
            }    
            else
            {
               list.add(data);
            }         
         }
         
         if ( aContext.getInvoke().isOneWay() )
            aContext.getCall().getOperation().setMep(OperationType.ONE_WAY);

         // execute the endpoint
         obj = aContext.getCall().invoke(list.toArray());
         
         // outputMsg will be created for request/response only
         outputMsg = createOutputMessageData(aContext);
      }
      finally
      {
         closeAttachmentStreams(outboundAttachments);
      }
      
      receiveAttachments(aContext,outputMsg);
        
      if (!aContext.getInvoke().isOneWay())
      {
         if (obj != null)
         {
            //
            // obj is equal to the value of the first part of the message. If there
            // are other parts to the message then we need to walk all of the output
            // params in order to read them
            // 
            List output = aContext.getCall().getOperation().getAllOutParams();
            
            if (output.size() > 0)
            {
               // set the first part
               ParameterDesc desc = (ParameterDesc) output.get(0);
               outputMsg.setData(desc.getName(), AeTypeMappingHelper.fixPart(aContext.getOperation().getOutput().getMessage().getPart(desc.getName()), obj));
               
               // Now walk all of the other parts (if any) and set their types on the
               // output message. 
               for (int i=1; i<output.size(); i++)
               {
                  desc = (ParameterDesc) output.get(i);
                  obj = aContext.getCall().getOutputParams().get( desc.getQName() );
                  outputMsg.setData(desc.getName(), AeTypeMappingHelper.fixPart(aContext.getOperation().getOutput().getMessage().getPart(desc.getName()), obj));
               }
            }
         }
         
         // read any parts from the header
         extractPartsFromHeader(aContext, outputMsg);
      }
      
      // Return the message to the awaiting callback 
      aContext.getResponse().setMessageData( outputMsg );
   }

   /**
    * Determines if the RPC invoke is encoded or literal by examining the binding
    * for the operation being invoked.
    *
    * @param aContext
    * @throws AeBusinessProcessException
    */
   protected Use getUse(AeAxisInvokeContext aContext) throws AeBusinessProcessException
   {
      Use useObj = null;

      BindingOperation bop = aContext.getBindingOperation();
      BindingInput input = bop.getBindingInput();

      // if there are extensibility elements, look to see if they specify
      // use="encoded" or use="literal"
      // only checking the input for the operation, not supporting mixed
      // use values in the operation, meaning encoded for input and literal for
      // output
      UnknownExtensibilityElement[] elements = AeBindingUtils.getUnknownExtensibilityElementsByName(input, AeBindingUtils.SOAP_BODY); 
      if (elements != null && elements.length > 0)
      {
         String use = elements[0].getElement().getAttribute("use"); //$NON-NLS-1$
         if ( Use.ENCODED_STR.equals(use) )
            useObj = Use.ENCODED;
         else if ( Use.LITERAL_STR.equals(use) )
            useObj = Use.LITERAL;
      }

      return useObj;
   }

   /**
    * Sets the operation descriptor for the call.
    * Note copied from service handler setup, since it couldn't be reused with its arguments.
    *
    * @param aContext The context for the invoke
    * @param aUseFlag Either Use.ENCODED or Use.LITERAL or null, if null we default to encoded.
    */
   protected void addOperationToCall(AeAxisInvokeContext aContext, Use aUseFlag)
   {
      // Create operation so that we are displayed in list of allowed methods. Need
      // to set a method in the operation or Axis will complain.
      OperationDesc operation = new OperationDesc();
      if ( aUseFlag != null )
      {
         operation.setUse(aUseFlag);
      }
      else
      {
         operation.setUse(Use.ENCODED);
      }
      operation.setName(aContext.getOperation().getName());

      addParamsToOperation(operation, aContext, true);
      
      if ( aContext.getOperation().getOutput() != null )
      {
         addParamsToOperation(operation, aContext, false);

         Output out = aContext.getOperation().getOutput();
         Message msg = out.getMessage();
         if ( msg.getParts().size() > 0 )
         {
            Part part = (Part)msg.getParts().values().iterator().next();

            if ( part.getTypeName() != null )
            {
               operation.setReturnType(part.getTypeName());
            }
            else
            {
               operation.setReturnType(part.getElementName());
            }
         }
         operation.setMep(OperationType.REQUEST_RESPONSE);
      }
      else
      {
         operation.setMep(OperationType.ONE_WAY);
      }
      
      // Axis 1.4 requires the return type be set on any call that sets parameters, even on one-way calls
      if (operation.getNumParams() > 0 && AeUtil.isNullOrEmpty(operation.getReturnType()))
      {
         // we'll set it to xs:any if we could not determine it above
         operation.setReturnType(org.apache.axis.Constants.XSD_ANY);
      }
      
      aContext.getCall().setOperation(operation);
   }

   /**
    * Adds all parameters for the given message to the given operation.
    * Note copied from service handler setup, since it couldn't be reused with its arguments.
    *
    * @param aOperation The operation we will be adding parameters for
    * @param aContext The context for the invoke
    * @param aIsInput flag indicating if this is an input message (true)
    */
   protected void addParamsToOperation(OperationDesc aOperation, AeAxisInvokeContext aContext, boolean aIsInput)
   {
      // if we're doing the input, then we should respect any parameterOrder attribute that was
      // set on the operation.
      List paramOrder = aIsInput ? AeBPELExtendedWSDLDef.getParameterOrder(aContext.getOperation()) : null;

      Message msg = aIsInput ? aContext.getOperation().getInput().getMessage() : aContext.getOperation().getOutput().getMessage();
      List partsList = msg.getOrderedParts(paramOrder);
      for (Iterator iter = partsList.iterator(); iter.hasNext();)
      {
         // Get the next part and obtain the return type for it (may be type or element)
         Part part = (Part)iter.next();
         QName typeName = part.getElementName();
         if ( typeName == null )
            typeName = part.getTypeName();

         ParameterDesc param = new ParameterDesc();
         param.setMode(aIsInput ? ParameterDesc.IN : ParameterDesc.OUT);
         param.setName(part.getName());
         param.setTypeQName(typeName);      
         // check to see if the param is destined for the input or output headers
         param.setInHeader(aIsInput && aContext.isInputHeader(part.getName()));
         param.setOutHeader(!aIsInput && aContext.isOutputHeader(part.getName()));
         
         aOperation.addParameter(param);
      }
   }

   /**
    * Setup the correct invoke operation qname if described in input binding.
    * @param aContext context for the invoke
    */
   protected void setupOperationQName(AeAxisInvokeContext aContext)
   {
      BindingInput bIn = aContext.getBindingOperation().getBindingInput();
      if ( bIn != null )
      {
         UnknownExtensibilityElement[] elements = AeBindingUtils.getUnknownExtensibilityElementsByName(bIn, AeBindingUtils.SOAP_BODY); 
         if (elements != null && elements.length > 0)
         {
            String ns = elements[0].getElement().getAttribute("namespace"); //$NON-NLS-1$
            if ( !AeUtil.isNullOrEmpty(ns) )
               aContext.getCall().setOperationName(new QName(ns, aContext.getOperation().getName()));
         }
      }
   }
}
