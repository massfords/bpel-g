//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/invoke/AeEngineAdminInvokeHandler.java,v 1.2 2008/02/02 19:23:26 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.invoke;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.server.admin.invoke.io.AeEngineAdminMessageSerializerBase;
import org.activebpel.rt.bpel.server.admin.invoke.io.AeGetProcessDetailSerializer;
import org.activebpel.rt.bpel.server.admin.invoke.io.AeGetProcessStateSerializer;
import org.activebpel.rt.bpel.server.admin.invoke.io.AesProcessTypeDeserializer;
import org.activebpel.rt.bpel.server.admin.invoke.io.IAeEngineAdminMessageIOConstants;
import org.activebpel.rt.bpel.server.engine.AeBpelEngine;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.wsio.invoke.AeWSIInvokeHandlerBase;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.invoke.AeInvokeResponse;
import org.w3c.dom.Document;

/**
 * <p>
 * A custom invoke handler that implements the following operations
 * of the ActiveBPELAdmin (http://docs.active-endpoints/wsdl/activebpeladmin/2007/01/activebpeladmin.wsdl).
 * </p>
 * <p>IAeAxisActiveBpelAdmin PortType</p>
 * <ul>
 *    <li>SuspendProcess</li>
 *    <li>ResumeProcess</li>
 *    <li>GetProcessState</li>
 *    <li>GetProcessDetail</li>
 * </ul>
 */
public class AeEngineAdminInvokeHandler extends AeWSIInvokeHandlerBase
{
   /** The suspend process operation input QName. */
   private static final QName SUSPEND_PROCESS_MESSAGE = new QName(IAeEngineAdminMessageIOConstants.ENGINE_ADMIN_WSDL_NS, "suspendProcessRequest"); //$NON-NLS-1$
   /** Resume process operation input QName. */
   private static final QName RESUME_PROCESS_MESSAGE  = new QName(IAeEngineAdminMessageIOConstants.ENGINE_ADMIN_WSDL_NS, "resumeProcessRequest"); //$NON-NLS-1$
   /** The GetProcessState process operation input QName. */
   private static final QName GET_PROCESS_STATE_REQ_MESSAGE  = new QName(IAeEngineAdminMessageIOConstants.ENGINE_ADMIN_WSDL_NS, "getProcessStateRequest"); //$NON-NLS-1$
   /** The GetProcessState process operation ouput QName. */
   private static final QName GET_PROCESS_STATE_RES_MESSAGE  = new QName(IAeEngineAdminMessageIOConstants.ENGINE_ADMIN_WSDL_NS, "getProcessStateResponse"); //$NON-NLS-1$
   /** The GetProcessDetail process operation input QName. */
   private static final QName GET_PROCESS_DETAIL_REQ_MESSAGE  = new QName(IAeEngineAdminMessageIOConstants.ENGINE_ADMIN_WSDL_NS, "getProcessDetailRequest"); //$NON-NLS-1$
   /** The GetProcessDetail process operation ouput QName. */
   private static final QName GET_PROCESS_DETAIL_RES_MESSAGE  = new QName(IAeEngineAdminMessageIOConstants.ENGINE_ADMIN_WSDL_NS, "getProcessDetailResponse"); //$NON-NLS-1$

   /**
    * Ctor.
    */
   public AeEngineAdminInvokeHandler()
   {
   }

   /**
    * Convenience method to set the output response data.
    * @param aResponse
    * @param aOutputMessageType
    * @param aPartName
    * @param aSerializer
    * @throws Exception
    */
   protected void setResponseData(AeInvokeResponse aResponse, QName aOutputMessageType, String aPartName,
         AeEngineAdminMessageSerializerBase aSerializer) throws Exception
   {
      setResponseData(aResponse, aOutputMessageType, aPartName, aSerializer.serialize().getOwnerDocument());
   }

   /**
    * Implements the SuspendProcess WSDL operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void suspendProcess(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      compareExpectedMessageType(aMessageData, SUSPEND_PROCESS_MESSAGE, "SuspendProcess"); //$NON-NLS-1$
      AesProcessTypeDeserializer des = new AesProcessTypeDeserializer(extractMessagePartDocument(aMessageData, "input")); //$NON-NLS-1$
      long pid = des.getProcessId();
      AeEngineFactory.getEngine().suspendProcess(pid);
   }

   /**
    * Implements the ResumeProcess WSDL operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void resumeProcess(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      compareExpectedMessageType(aMessageData, RESUME_PROCESS_MESSAGE, "ResumeProcess"); //$NON-NLS-1$
      AesProcessTypeDeserializer des = new AesProcessTypeDeserializer(extractMessagePartDocument(aMessageData, "input")); //$NON-NLS-1$
      long pid = des.getProcessId();
      AeEngineFactory.getEngine().resumeProcess(pid);
   }

   /**
    * Implements the 'GetProcessState' operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void getProcessState(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      compareExpectedMessageType(aMessageData, GET_PROCESS_STATE_REQ_MESSAGE, "GetProcessState"); //$NON-NLS-1$
      AesProcessTypeDeserializer des = new AesProcessTypeDeserializer(extractMessagePartDocument(aMessageData, "input")); //$NON-NLS-1$
      long pid = des.getProcessId();
      Document doc = AeEngineFactory.getEngine().getProcessState(pid);
      AeGetProcessStateSerializer ser = new AeGetProcessStateSerializer( doc );
      setResponseData(aResponse, GET_PROCESS_STATE_RES_MESSAGE, "output", ser); //$NON-NLS-1$
   }

   /**
    * Implements the getProcessDetail() operation.
    * @param aMessageData
    * @param aResponse
    * @throws Exception
    */
   public void getProcessDetail(IAeWebServiceMessageData aMessageData, AeInvokeResponse aResponse) throws Exception
   {
      compareExpectedMessageType(aMessageData, GET_PROCESS_DETAIL_REQ_MESSAGE, "GetProcessDetail"); //$NON-NLS-1$
      AesProcessTypeDeserializer des = new AesProcessTypeDeserializer(extractMessagePartDocument(aMessageData, "input")); //$NON-NLS-1$
      long pid = des.getProcessId();
      AeProcessInstanceDetail detail = ((AeBpelEngine)AeEngineFactory.getEngine()).getProcessInstanceDetails(pid);
      AeGetProcessDetailSerializer ser = new AeGetProcessDetailSerializer( detail );
      setResponseData(aResponse, GET_PROCESS_DETAIL_RES_MESSAGE, "output", ser); //$NON-NLS-1$
   }

}
