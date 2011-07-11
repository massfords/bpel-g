// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/receive/AeDefaultReceiveHandler.java,v 1.5 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.receive;

import java.util.Iterator;
import java.util.Map;

import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessageDataFactory;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.impl.AeDataConverter;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.AeOperationSignatureMatcher;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.XMLType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Receive handler that simply invokes the BPEL engine. 
 */
public class AeDefaultReceiveHandler extends AeAbstractReceiveHandler 
{
   /**
    * Simple conversion from IAeWebServiceMessageData to AeMessageData
    * 
    * @see org.activebpel.rt.bpel.server.engine.receive.IAeBPELReceiveHandler#mapInputData(org.activebpel.rt.bpel.impl.IAeProcessPlan, org.activebpel.rt.bpel.server.engine.receive.AeExtendedMessageContext, org.activebpel.wsio.IAeWebServiceMessageData)
    */
   public IAeMessageData mapInputData(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, IAeWebServiceMessageData aData) throws AeBusinessProcessException
   {
      return AeDataConverter.convert(aData);
   }
   
   /**
    * Maps body element data from a SOAP request into a AeMessageData object using
    * information in the process plan and message context. 
    * 
    * Will attempt to determine the operation based on a signature match.     
    * Updates the message context with the operation name.
    * 
    * @param aPlan process plan for service
    * @param aContext MessageContext
    * @param aDocArray array of documents containing raw request data
    * @return AeMessageData mapped from input documents
    * @throws AeBusinessProcessException
    */
   public IAeMessageData mapInputData(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, Document[] aDocArray) throws AeBusinessProcessException
   {
      // Find the WSDL def for service
      QName portTypeQName = getMyRolePortTypeQName(aPlan, aContext);
      AeBPELExtendedWSDLDef portTypeDef = AeWSDLDefHelper.getWSDLDefinitionForPortType(aPlan, portTypeQName);

      // Match the signature to an operation
      AeOperationSignatureMatcher opFinder = new AeOperationSignatureMatcher(portTypeDef, portTypeQName, aContext.getOperation(), aDocArray);
      if (!opFinder.foundMatch())
      {
         throw new AeBusinessProcessException(AeMessages.getString("AeBPELReceiveHandler.0")); //$NON-NLS-1$
      }
      // update the operation on the context
      aContext.setOperation(opFinder.getOperationName());
      
      Operation operation = opFinder.getOperation();
      if (operation.getOutput() != null)
      {
         AeBPELExtendedWSDLDef outputMessageDef = AeWSDLDefHelper.getWSDLDefinitionForMsg(aPlan, operation.getOutput().getMessage().getQName());
         if (outputMessageDef == null)
         {
            // this could happen if user replaces existing wsdl with new incompatible
            // wsdl version but DOES NOT expire the existing service
            throw new AeBusinessProcessException( AeMessages.format("AeDefaultReceiveHandler.MSG_NOT_FOUND", operation.getOutput().getMessage().getQName())); //$NON-NLS-1$
         }
         aContext.setProperty(AE_CONTEXT_KEY_WSDL_OUTPUT, outputMessageDef);
      }
      
      // create the input data
      Map partAsKeyMap = opFinder.getPartsMap();
      IAeMessageData inputMsg = AeMessageDataFactory.instance().createMessageData(opFinder.getMessageName()); 
      for (Iterator iter=partAsKeyMap.keySet().iterator(); iter.hasNext();)
      {
         // Get the Part from the data map and determine the part type
         Part part = (Part)iter.next();
         XMLType type = null;
         boolean complex = false;
         if (part.getTypeName() != null)
         {
            try
            {
               type = portTypeDef.findType(part.getTypeName());
            }
            catch (AeException ex)
            {
               throw new AeBusinessProcessException(ex.getLocalizedMessage(), ex);
            }
            complex = AeXmlUtil.isComplexOrAny(type);
         }
         else if (part.getElementName() != null)
         {
            complex = true;
            ElementDecl element = portTypeDef.findElement(part.getElementName());
            if (element != null) 
               type = element.getType(); 
         }

         Document doc = (Document)partAsKeyMap.get(part);
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
            inputMsg.setData(part.getName(), doc);
         }
         else 
         {
            // Concat all text nodes to get the data value
            inputMsg.setData(part.getName(), AeXmlUtil.getText(doc.getDocumentElement()));
         }
      }      
      
      return inputMsg;
   }
   

   /**
    * Returns response from engine as-is
    * 
    * @see org.activebpel.rt.bpel.server.engine.receive.AeAbstractReceiveHandler#mapFaultData(org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext, org.activebpel.wsio.IAeWebServiceResponse)
    */
   public IAeWebServiceResponse mapFaultData(IAeExtendedMessageContext aContext, IAeWebServiceResponse aResponse) throws AeBusinessProcessException
   {
      return aResponse;
   }

   /**
    * Returns response from engine as-is
    * @see org.activebpel.rt.bpel.server.engine.receive.IAeBPELReceiveHandler#mapOutputData(org.activebpel.rt.bpel.server.engine.receive.IAeExtendedMessageContext, org.activebpel.wsio.IAeWebServiceResponse)
    */
   public IAeWebServiceResponse mapOutputData(IAeExtendedMessageContext aContext, IAeWebServiceResponse aResponse) throws AeBusinessProcessException
   {
      return aResponse;
   }

   /**
    * Always succeeds.
    * 
    * Issues with the part data contained in the document array will be detected 
    * we map the input data to AeMessageData.
    * 
    * @see org.activebpel.rt.bpel.server.engine.receive.AeAbstractReceiveHandler#validateInputData(org.activebpel.rt.bpel.impl.IAeProcessPlan, IAeExtendedMessageContext, org.w3c.dom.Document[])
    */
   public void validateInputData(IAeProcessPlan aPlan, IAeExtendedMessageContext aContext, Document[] aDocArray) throws AeBusinessProcessException
   {
      // do nothing
   }

   /**
    * Always succeeds. 
    * 
    * @see org.activebpel.rt.bpel.server.engine.receive.AeAbstractReceiveHandler#validateInputData(org.activebpel.rt.bpel.impl.IAeProcessPlan, IAeExtendedMessageContext, org.w3c.dom.Document[])
    */
   public void validateInputData(IAeProcessPlan aPlan, IAeExtendedMessageContext aContext, IAeWebServiceMessageData aData) throws AeBusinessProcessException
   {
      // do nothing
   }

}
