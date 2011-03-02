//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/receive/IAeBPELReceiveHandler.java,v 1.2 2008/02/17 21:38:45 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.receive;


import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.w3c.dom.Document;

/**
 * Interface detailing the abstract methods required of pluggable receive handlers
 */
public interface IAeBPELReceiveHandler
{
   /**
    * Maps element data from a request into a AeMessageData object using
    * information in the process plan and message context. 
    * 
    * @param aPlan process plan for service
    * @param aContext MessageContext
    * @param aData Raw Input data
    * @return input converted to AeMessageData 
    */
   public IAeMessageData mapInputData(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, IAeWebServiceMessageData aData) throws AeBusinessProcessException;
   
   /**
    * Maps element data from a request into a AeMessageData object using
    * information in the process plan and message context. 
    * 
    * @param aPlan process plan for service
    * @param aContext MessageContext
    * @param aDocArray array of documents containing raw request data
    * @return AeMessageData mapped from input documents
    */
   public IAeMessageData mapInputData(IAeProcessPlan aPlan, AeExtendedMessageContext aContext, Document[] aDocArray) throws AeBusinessProcessException;
   
   /**
    * Maps the output data from the BPEL engine to a protocol-specific response
    * 
    * @param aContext the message context which contains the RPC objects
    * @param aResponse the output message from the BPEL engine
    * @throws AeBusinessProcessException
    */
   public IAeWebServiceResponse mapOutputData(IAeExtendedMessageContext aContext, IAeWebServiceResponse aResponse) throws AeBusinessProcessException; 
   
   /**
    * Maps fault data from the BPEL engine to a protocol-specific response
    * 
    * @param aContext
    * @param aResponse
    */
   public IAeWebServiceResponse mapFaultData(IAeExtendedMessageContext aContext, IAeWebServiceResponse aResponse) throws AeBusinessProcessException;
   
   /**
    * Performs upfront validation of input data according to the expectations of the receive handler
    * Method should throw an AeBusinessProcessException if it finds a problem with the input
    * 
    * @param aPlan
    * @param aContext
    * @param aDocArray
    * @throws AeBusinessProcessException
    */
   public void validateInputData(IAeProcessPlan aPlan, IAeExtendedMessageContext aContext, Document[] aDocArray) throws AeBusinessProcessException;

   /**
    * Performs upfront validation of input data according to the expectations of the receive handler
    * Method should throw an AeBusinessProcessException if it finds a problem with the input
    * 
    * @param aPlan
    * @param aContext
    * @param aData
    * @throws AeBusinessProcessException
    */
   public void validateInputData(IAeProcessPlan aPlan, IAeExtendedMessageContext aContext, IAeWebServiceMessageData aData) throws AeBusinessProcessException;
}
