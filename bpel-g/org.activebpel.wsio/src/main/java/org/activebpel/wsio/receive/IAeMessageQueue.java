//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/receive/IAeMessageQueue.java,v 1.7.4.1 2008/04/21 16:19:01 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio.receive; 

import java.rmi.RemoteException;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.w3c.dom.Document;

/**
 * Provides an interface for sending messages to the engine.
 */
public interface IAeMessageQueue
{
   /**
    * Delivers the message to the BPEL engine's message queue.
    * 
    * @param aData
    * @param aContext
    * @return IAeWebServiceResponse
    * @throws RemoteException
    * @throws AeRequestException
    */
   public IAeWebServiceResponse queueReceiveData(IAeWebServiceMessageData aData, IAeMessageContext aContext)
      throws RemoteException, AeRequestException;
   
   /**
    * Specialized version of queueReceiveData that accepts xml documents in lieu 
    * of our message container. The context must specify the full routing 
    * information of process qname and partner link OR specify only the service 
    * name. In either case, the missing information will be determined by 
    * introspecting the wsdl and matching the data to an operation.
    * 
    * @param aContext must contain the process qname &amp; partner link OR the service name, never both
    * @param aDocArray array of xml documents that make up the message parts for the operaiton invoked
    * @throws RemoteException
    * @throws AeRequestException
    */
   public IAeWebServiceResponse queueReceiveData(IAeMessageContext aContext, Document[] aDocArray) 
      throws RemoteException, AeRequestException;
   
   /**
    * Allows an externally invoked operation data to dispatch to a queued invoke.
    * @param aProcessId The id of the process expecting the response from the invoke
    * @param aLocationPath The path to the location awaiting the response
    * @param aMessageData The data we have received from invoke.
    * @param aProcessProperties Any string name/value pairs we received back from the invoke.
    * @deprecated Use org.activebpel.wsio.receive.IAeMessageQueue#queueInvokeData(long, java.lang.String, long, org.activebpel.wsio.IAeWebServiceMessageData, java.util.Map)
    */
   public void queueInvokeData(long aProcessId, String aLocationPath, IAeWebServiceMessageData aMessageData, Map aProcessProperties )
         throws RemoteException, AeRequestException;

   /**
    * Allows an externally invoked operation data to dispatch to a queued invoke.
    * @param aProcessId The id of the process expecting the response from the invoke
    * @param aLocationPath The path to the location awaiting the response
    * @param aTransmissionId The invoke's execution instance transmission id.
    * @param aMessageData The data we have received from invoke.
    * @param aProcessProperties Any string name/value pairs we received back from the invoke.
    */
   public void queueInvokeData(long aProcessId, String aLocationPath, long aTransmissionId, IAeWebServiceMessageData aMessageData, Map aProcessProperties )
         throws RemoteException, AeRequestException;
   
   /**
    * Allows an externally invoked operation fault to dispatch to a queued invoke.
    * @param aProcessId The process that's expecting the invoke response
    * @param aLocationPath The path to the location awaiting the response
    * @param aFaultName The fault name we received from invoke or null if not available
    * @param aFaultData The fault data we have received from invoke or null if not available
    * @param aProcessProperties Any string name/value pairs we received back from the invoke.
    * @deprecated Use org.activebpel.wsio.receive.IAeMessageQueue#queueInvokeFault(long, java.lang.String, long, javax.xml.namespace.QName, org.activebpel.wsio.IAeWebServiceMessageData, java.util.Map)
    */
   public void queueInvokeFault(long aProcessId, String aLocationPath, QName aFaultName, IAeWebServiceMessageData aFaultData, Map aProcessProperties)
         throws RemoteException, AeRequestException;

   /**
    * Allows an externally invoked operation fault to dispatch to a queued invoke.
    * @param aProcessId The process that's expecting the invoke response
    * @param aLocationPath The path to the location awaiting the response
    * @param aTransmissionId The invoke's execution instance transmission id. 
    * @param aFaultName The fault name we received from invoke or null if not available
    * @param aFaultData The fault data we have received from invoke or null if not available
    * @param aProcessProperties Any string name/value pairs we received back from the invoke.
    */
   public void queueInvokeFault(long aProcessId, String aLocationPath, long aTransmissionId, QName aFaultName, IAeWebServiceMessageData aFaultData, Map aProcessProperties)
      throws RemoteException, AeRequestException;
   
}
 