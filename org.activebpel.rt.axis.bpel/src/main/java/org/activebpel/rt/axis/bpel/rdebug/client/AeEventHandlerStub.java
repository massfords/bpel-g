// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/rdebug/client/AeEventHandlerStub.java,v 1.18 2007/01/25 16:57:22 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.rdebug.client;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.xml.namespace.QName;

import org.activebpel.rt.axis.bpel.AeMessages;
import org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandler;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.apache.axis.AxisFault;
import org.apache.axis.NoEndPointException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.JavaUtils;
import org.w3c.dom.Document;

/**
 * The client side code used to interface with the remote debug services.
 */
 public class AeEventHandlerStub extends Stub implements IAeEventHandler
{
   // The indexes into the operation array
   private final static int ENGINE_EVENT_ID  = 0;
   private final static int PROCESS_EVENT_ID = 1;
   private final static int ENGINE_ALERT_ID = 2;
   private final static int PROCESS_INFO_EVENT_ID = 3;
   private final static int BREAKPOINT_EVENT_ID = 4;   
   // The number of operations
   private final static int OPERATION_COUNT = 5;

   static OperationDesc[] sOperations;

   static 
   {
      sOperations = new OperationDesc[OPERATION_COUNT];

      OperationDesc oper = new OperationDesc();
      oper.setName("engineEventHandler"); //$NON-NLS-1$
      oper.addParameter(new QName("", "contextId"),   AeSchemaUtil.sLong,  long.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "processId"),   AeSchemaUtil.sLong,  long.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "eventType"),   AeSchemaUtil.sInt,   int.class,  ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "processName"), AeSchemaUtil.sQName, QName.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "timestamp"),   AeSchemaUtil.sDateTime, Calendar.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.setReturnType(AeSchemaUtil.sBoolean);
      oper.setReturnClass(boolean.class);
      oper.setReturnQName(new QName("", "engineEventHandlerReturn")); //$NON-NLS-1$ //$NON-NLS-2$
      oper.setStyle(Style.RPC);
      oper.setUse(Use.ENCODED);
      sOperations[ENGINE_EVENT_ID] = oper;

      oper = new OperationDesc();
      oper.setName("processEventHandler"); //$NON-NLS-1$
      oper.addParameter(new QName("", "contextId"), AeSchemaUtil.sLong, long.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "processId"), AeSchemaUtil.sLong, long.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "path"), AeSchemaUtil.sString, String.class,  ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "eventType"), AeSchemaUtil.sInt, int.class,  ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "faultName"), AeSchemaUtil.sString, String.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "text"), AeSchemaUtil.sString, String.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "name"), AeSchemaUtil.sQName, QName.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "timestamp"),   AeSchemaUtil.sDateTime, Calendar.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.setReturnType(AeSchemaUtil.sBoolean);
      oper.setReturnClass(boolean.class);
      oper.setReturnQName(new QName("", "processEventHandlerReturn")); //$NON-NLS-1$ //$NON-NLS-2$
      oper.setStyle(Style.RPC);
      oper.setUse(Use.ENCODED);
      sOperations[PROCESS_EVENT_ID] = oper;
      
      oper = new OperationDesc();
      oper.setName("processInfoEventHandler"); //$NON-NLS-1$
      oper.addParameter(new QName("", "contextId"), AeSchemaUtil.sLong, long.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "processId"), AeSchemaUtil.sLong, long.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "path"), AeSchemaUtil.sString, String.class,  ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "eventType"), AeSchemaUtil.sInt, int.class,  ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "faultName"), AeSchemaUtil.sString, String.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "text"), AeSchemaUtil.sString, String.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "timestamp"),   AeSchemaUtil.sDateTime, Calendar.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
      oper.setStyle(Style.RPC);
      oper.setUse(Use.ENCODED);
      sOperations[PROCESS_INFO_EVENT_ID] = oper;
      
      oper = new OperationDesc();
      oper.setName("engineAlertHandler"); //$NON-NLS-1$
      oper.addParameter(new QName("", "contextId"),   AeSchemaUtil.sLong,  long.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "processId"),   AeSchemaUtil.sLong,  long.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "eventType"),   AeSchemaUtil.sInt,   int.class,  ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "processName"), AeSchemaUtil.sQName, QName.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "location"), AeSchemaUtil.sString, String.class,  ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "details"), AeSchemaUtil.sString, String.class,  ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "timestamp"),   AeSchemaUtil.sDateTime, Calendar.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
      oper.setStyle(Style.RPC);
      oper.setUse(Use.ENCODED);
      sOperations[ENGINE_ALERT_ID] = oper;      

      oper = new OperationDesc();
      oper.setName("breakpointEventHandler"); //$NON-NLS-1$
      oper.addParameter(new QName("", "contextId"),   AeSchemaUtil.sLong,  long.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "processId"),   AeSchemaUtil.sLong,  long.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "path"), AeSchemaUtil.sString, String.class,  ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "processName"), AeSchemaUtil.sQName, QName.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      oper.addParameter(new QName("", "timestamp"), AeSchemaUtil.sDateTime, Calendar.class, ParameterDesc.IN, false, false); //$NON-NLS-1$ //$NON-NLS-2$
      // Axis 1.4 requires the return type be set on any call that sets parameters, even on one-way calls
      oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
      oper.setStyle(Style.RPC);
      oper.setUse(Use.ENCODED);
      sOperations[BREAKPOINT_EVENT_ID] = oper;
   }

   /**
    * Default contructor.
    * @throws AxisFault
    */
   public AeEventHandlerStub() throws AxisFault
   {
      this(null);
   }

   /**
    * Contructor which take endpoint URL and Service object.
    * @param aEndpointURL the endpoint to initialize with
    * @param aService the service to use for initialization
    * @throws AxisFault
    */
   public AeEventHandlerStub(URL aEndpointURL, Service aService) throws AxisFault
   {
      this(aService);
      super.cachedEndpoint = aEndpointURL;
   }

   /**
    * Constructor which takes a Service object to initialze with.
    * @param aService the service to use for initialization
    * @throws AxisFault
    */
   public AeEventHandlerStub(Service aService) throws AxisFault
   {
      if (aService == null)
         super.service = new Service();
      else
         super.service = aService;
   }

   /**
    * Creates the base remote procedure call object.
    * @throws RemoteException
    */
   private Call createCall() throws RemoteException
   {
      try
      {
         Call call = (Call) super.service.createCall();
         if (super.maintainSessionSet)
            call.setMaintainSession(super.maintainSession);

         if (super.cachedUsername != null)
            call.setUsername(super.cachedUsername);

         if (super.cachedPassword != null)
            call.setPassword(super.cachedPassword);

         if (super.cachedEndpoint != null)
            call.setTargetEndpointAddress(super.cachedEndpoint);

         if (super.cachedTimeout != null)
            call.setTimeout(super.cachedTimeout);

         if (super.cachedPortName != null)
            call.setPortName(super.cachedPortName);

         Enumeration keys = super.cachedProperties.keys();
         while (keys.hasMoreElements())
         {
            String key = (String) keys.nextElement();
            call.setProperty(key, super.cachedProperties.get(key));
         }
         return call;
      }
      catch (Throwable th)
      {
         throw new AxisFault(AeMessages.getString("AeEventHandlerStub.ERROR_36"), th); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandler#engineEventHandler(long, long, int, javax.xml.namespace.QName, java.util.Date)
    */
   public boolean engineEventHandler(long aContextId, long aProcessId, int aEventType, QName aProcessName,
         Date aTimestamp) throws RemoteException
   {
      if (super.cachedEndpoint == null)
         throw new NoEndPointException();

      Call call = createCall();
      call.setOperation(sOperations[ENGINE_EVENT_ID]);
      call.setUseSOAPAction(true);
      call.setSOAPActionURI(""); //$NON-NLS-1$
      call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
      call.setOperationName(new QName("urn:AeRemoteDebugServices", "engineEventHandler")); //$NON-NLS-1$ //$NON-NLS-2$

      setRequestHeaders(call);
      setAttachments(call);
      Calendar cal = Calendar.getInstance();
      cal.setTime(aTimestamp);
      Object resp = call.invoke(new Object[] { new Long(aContextId), new Long(aProcessId),
            new Integer(aEventType), aProcessName, cal });

      if (resp instanceof RemoteException)
         throw (RemoteException) resp;
      
      try
      {
         extractAttachments(call);
         return ((Boolean) resp).booleanValue();
      }
      catch (Exception _exception)
      {
         return ((Boolean) JavaUtils.convert(resp, boolean.class)).booleanValue();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandler#engineAlertHandler(long, long, int, javax.xml.namespace.QName, java.lang.String, javax.xml.namespace.QName, org.w3c.dom.Document, java.util.Date)
    */
   public void engineAlertHandler(long aContextId, long aProcessId, int aEventType, QName aProcessName,
         String aLocationPath, QName aFaultName, Document aDetails, Date aTimestamp) throws RemoteException
   {
      if (super.cachedEndpoint == null)
         throw new NoEndPointException();

      Call call = createCall();
      call.setOperation(sOperations[ENGINE_ALERT_ID]);
      call.setUseSOAPAction(true);
      call.setSOAPActionURI(""); //$NON-NLS-1$
      call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
      call.setOperationName(new QName("urn:AeRemoteDebugServices", "engineAlertHandler")); //$NON-NLS-1$ //$NON-NLS-2$
      
      String serializedXmlDetails = ""; //$NON-NLS-1$
      if (aDetails != null)
         serializedXmlDetails = AeXMLParserBase.documentToString(aDetails);

      setRequestHeaders(call);
      setAttachments(call);
      Calendar cal = Calendar.getInstance();
      cal.setTime(aTimestamp);
      Object resp = call.invoke(new Object[] { new Long(aContextId), new Long(aProcessId),
            new Integer(aEventType), aProcessName, aLocationPath, aFaultName, serializedXmlDetails, cal });

      if (resp instanceof RemoteException)
         throw (RemoteException) resp;
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandler#processEventHandler(long, long, java.lang.String, int, java.lang.String, java.lang.String, javax.xml.namespace.QName, java.util.Date)
    */
   public boolean processEventHandler(long aContextId, long aProcessId, String aPath, int aEventType,
         String aFaultName, String aText, QName aName, Date aTimestamp) throws RemoteException
   {
      if (super.cachedEndpoint == null)
         throw new NoEndPointException();

      Call call = createCall();
      call.setOperation(sOperations[PROCESS_EVENT_ID]);
      call.setUseSOAPAction(true);
      call.setSOAPActionURI(""); //$NON-NLS-1$
      call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
      call.setOperationName(new QName("urn:AeRemoteDebugServices", "processEventHandler")); //$NON-NLS-1$ //$NON-NLS-2$

      setRequestHeaders(call);
      setAttachments(call);
      if ( aText == null )
         aText = "" ; //$NON-NLS-1$
      Calendar cal = Calendar.getInstance();
      cal.setTime(aTimestamp);
      Object resp = call.invoke(new Object[] { new Long(aContextId), new Long(aProcessId), aPath,
            new Integer(aEventType), aFaultName, aText, aName, cal });

      if (resp instanceof RemoteException)
         throw (RemoteException) resp;

      try
      {
         extractAttachments(call);
         return ((Boolean) resp).booleanValue();
      }
      catch (Exception _exception)
      {
         return ((Boolean) JavaUtils.convert(resp, boolean.class)).booleanValue();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandler#processInfoEventHandler(long, long, java.lang.String, int, java.lang.String, java.lang.String, java.util.Date)
    */
   public void processInfoEventHandler(long aContextId, long aProcessId, String aPath, int aEventType, String aFaultName, String aText, Date aTimestamp) throws RemoteException
   {
      if (super.cachedEndpoint == null)
         throw new NoEndPointException();

      Call call = createCall();
      call.setOperation(sOperations[PROCESS_INFO_EVENT_ID]);
      call.setUseSOAPAction(true);
      call.setSOAPActionURI(""); //$NON-NLS-1$
      call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
      call.setOperationName(new QName("urn:AeRemoteDebugServices", "processInfoEventHandler")); //$NON-NLS-1$ //$NON-NLS-2$

      setRequestHeaders(call);
      setAttachments(call);
      if ( aText == null )
         aText = "" ; //$NON-NLS-1$
      Calendar cal = Calendar.getInstance();
      cal.setTime(aTimestamp);
      Object resp = call.invoke(new Object[] { new Long(aContextId), new Long(aProcessId), aPath,
            new Integer(aEventType), aFaultName, aText, cal });

      if (resp instanceof RemoteException)
         throw (RemoteException) resp;
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.client.IAeEventHandler#breakpointEventHandler(long, long, java.lang.String, javax.xml.namespace.QName, java.util.Date)
    */
   public void breakpointEventHandler(long aContextId, long aProcessId, String aPath, QName aProcessName, Date aTimestamp) throws RemoteException
   {
      if (super.cachedEndpoint == null)
         throw new NoEndPointException();

      Call call = createCall();
      call.setOperation(sOperations[BREAKPOINT_EVENT_ID]);
      call.setUseSOAPAction(true);
      call.setSOAPActionURI(""); //$NON-NLS-1$
      call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
      call.setOperationName(new QName("urn:AeRemoteDebugServices", "breakpointEventHandler")); //$NON-NLS-1$ //$NON-NLS-2$

      setRequestHeaders(call);
      setAttachments(call);
      Calendar cal = Calendar.getInstance();
      cal.setTime(aTimestamp);
      Object resp = call.invoke(new Object[] { new Long(aContextId), new Long(aProcessId), aPath,
            aProcessName, cal });

      if (resp instanceof RemoteException)
         throw (RemoteException) resp;
   }
}