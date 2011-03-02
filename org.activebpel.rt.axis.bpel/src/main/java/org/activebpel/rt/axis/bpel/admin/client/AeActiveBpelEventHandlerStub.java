// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/admin/client/AeActiveBpelEventHandlerStub.java,v 1.2 2007/01/25 16:57:21 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.admin.client;

public class AeActiveBpelEventHandlerStub extends org.apache.axis.client.Stub implements org.activebpel.rt.axis.bpel.eventhandler.IAeActiveBpelEventHandler {
   private java.util.Vector cachedSerClasses = new java.util.Vector();
   private java.util.Vector cachedSerQNames = new java.util.Vector();
   private java.util.Vector cachedSerFactories = new java.util.Vector();
   private java.util.Vector cachedDeserFactories = new java.util.Vector();

   static org.apache.axis.description.OperationDesc [] _operations;

   static {
       _operations = new org.apache.axis.description.OperationDesc[5];
       _initOperationDesc1();
   }

   private static void _initOperationDesc1(){
       org.apache.axis.description.OperationDesc oper;
       org.apache.axis.description.ParameterDesc param;
       oper = new org.apache.axis.description.OperationDesc();
       oper.setName("engineEventHandler"); //$NON-NLS-1$
       param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", "AesEngineEventHandlerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesEngineEventHandlerInput"), org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerInput.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
       oper.addParameter(param);
       oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesEngineEventHandlerOutput")); //$NON-NLS-1$ //$NON-NLS-2$
       oper.setReturnClass(org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerOutput.class);
       oper.setReturnQName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", "AesEngineEventHandlerOutput")); //$NON-NLS-1$ //$NON-NLS-2$
       oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
       oper.setUse(org.apache.axis.constants.Use.LITERAL);
       _operations[0] = oper;

       oper = new org.apache.axis.description.OperationDesc();
       oper.setName("engineAlertHandler"); //$NON-NLS-1$
       param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", "AesEngineAlertHandlerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesEngineAlertHandlerInput"), org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineAlertHandlerInput.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
       oper.addParameter(param);
       oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
       oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
       oper.setUse(org.apache.axis.constants.Use.LITERAL);
       _operations[1] = oper;

       oper = new org.apache.axis.description.OperationDesc();
       oper.setName("processEventHandler"); //$NON-NLS-1$
       param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", "AesProcessEventHandlerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesProcessEventHandlerInput"), org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerInput.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
       oper.addParameter(param);
       oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesProcessEventHandlerOutput")); //$NON-NLS-1$ //$NON-NLS-2$
       oper.setReturnClass(org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerOutput.class);
       oper.setReturnQName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", "AesProcessEventHandlerOutput")); //$NON-NLS-1$ //$NON-NLS-2$
       oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
       oper.setUse(org.apache.axis.constants.Use.LITERAL);
       _operations[2] = oper;

       oper = new org.apache.axis.description.OperationDesc();
       oper.setName("processInfoEventHandler"); //$NON-NLS-1$
       param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", "AesProcessInfoEventHandlerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesProcessInfoEventHandlerInput"), org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessInfoEventHandlerInput.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
       oper.addParameter(param);
       oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
       oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
       oper.setUse(org.apache.axis.constants.Use.LITERAL);
       _operations[3] = oper;

       oper = new org.apache.axis.description.OperationDesc();
       oper.setName("breakpointEventHandler"); //$NON-NLS-1$
       param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", "AesBreakpointEventHandlerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesBreakpointEventHandlerInput"), org.activebpel.rt.axis.bpel.eventhandler.types.AesBreakpointEventHandlerInput.class, false, false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
       oper.addParameter(param);
       oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
       oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
       oper.setUse(org.apache.axis.constants.Use.LITERAL);
       _operations[4] = oper;

   }

   public AeActiveBpelEventHandlerStub() throws org.apache.axis.AxisFault {
        this(null);
   }

   public AeActiveBpelEventHandlerStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        this(service);
        super.cachedEndpoint = endpointURL;
   }

   public AeActiveBpelEventHandlerStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
       if (service == null) {
           super.service = new org.apache.axis.client.Service();
       } else {
           super.service = service;
       }
       ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2"); //$NON-NLS-1$
           java.lang.Class cls;
           javax.xml.namespace.QName qName;
           java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
           java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
           qName = new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesBreakpointEventHandlerInput"); //$NON-NLS-1$ //$NON-NLS-2$
           cachedSerQNames.add(qName);
           cls = org.activebpel.rt.axis.bpel.eventhandler.types.AesBreakpointEventHandlerInput.class;
           cachedSerClasses.add(cls);
           cachedSerFactories.add(beansf);
           cachedDeserFactories.add(beandf);

           qName = new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesEngineAlertHandlerInput"); //$NON-NLS-1$ //$NON-NLS-2$
           cachedSerQNames.add(qName);
           cls = org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineAlertHandlerInput.class;
           cachedSerClasses.add(cls);
           cachedSerFactories.add(beansf);
           cachedDeserFactories.add(beandf);

           qName = new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesEngineEventHandlerInput"); //$NON-NLS-1$ //$NON-NLS-2$
           cachedSerQNames.add(qName);
           cls = org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerInput.class;
           cachedSerClasses.add(cls);
           cachedSerFactories.add(beansf);
           cachedDeserFactories.add(beandf);

           qName = new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesEngineEventHandlerOutput"); //$NON-NLS-1$ //$NON-NLS-2$
           cachedSerQNames.add(qName);
           cls = org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerOutput.class;
           cachedSerClasses.add(cls);
           cachedSerFactories.add(beansf);
           cachedDeserFactories.add(beandf);

           qName = new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesProcessEventHandlerInput"); //$NON-NLS-1$ //$NON-NLS-2$
           cachedSerQNames.add(qName);
           cls = org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerInput.class;
           cachedSerClasses.add(cls);
           cachedSerFactories.add(beansf);
           cachedDeserFactories.add(beandf);

           qName = new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesProcessEventHandlerOutput"); //$NON-NLS-1$ //$NON-NLS-2$
           cachedSerQNames.add(qName);
           cls = org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerOutput.class;
           cachedSerClasses.add(cls);
           cachedSerFactories.add(beansf);
           cachedDeserFactories.add(beandf);

           qName = new javax.xml.namespace.QName("http://schemas.active-endpoints.com/eventhandler/2007/01/eventhandler.xsd", ">AesProcessInfoEventHandlerInput"); //$NON-NLS-1$ //$NON-NLS-2$
           cachedSerQNames.add(qName);
           cls = org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessInfoEventHandlerInput.class;
           cachedSerClasses.add(cls);
           cachedSerFactories.add(beansf);
           cachedDeserFactories.add(beandf);

   }

   protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
       try {
           org.apache.axis.client.Call _call = super._createCall();
           if (super.maintainSessionSet) {
               _call.setMaintainSession(super.maintainSession);
           }
           if (super.cachedUsername != null) {
               _call.setUsername(super.cachedUsername);
           }
           if (super.cachedPassword != null) {
               _call.setPassword(super.cachedPassword);
           }
           if (super.cachedEndpoint != null) {
               _call.setTargetEndpointAddress(super.cachedEndpoint);
           }
           if (super.cachedTimeout != null) {
               _call.setTimeout(super.cachedTimeout);
           }
           if (super.cachedPortName != null) {
               _call.setPortName(super.cachedPortName);
           }
           java.util.Enumeration keys = super.cachedProperties.keys();
           while (keys.hasMoreElements()) {
               java.lang.String key = (java.lang.String) keys.nextElement();
               _call.setProperty(key, super.cachedProperties.get(key));
           }
           // All the type mapping information is registered
           // when the first call is made.
           // The type mapping information is actually registered in
           // the TypeMappingRegistry of the service, which
           // is the reason why registration is only needed for the first call.
           synchronized (this) {
               if (firstCall()) {
                   // must set encoding style before registering serializers
                   _call.setEncodingStyle(null);
                   for (int i = 0; i < cachedSerFactories.size(); ++i) {
                       java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                       javax.xml.namespace.QName qName =
                               (javax.xml.namespace.QName) cachedSerQNames.get(i);
                       java.lang.Object x = cachedSerFactories.get(i);
                       if (x instanceof Class) {
                           java.lang.Class sf = (java.lang.Class)
                                cachedSerFactories.get(i);
                           java.lang.Class df = (java.lang.Class)
                                cachedDeserFactories.get(i);
                           _call.registerTypeMapping(cls, qName, sf, df, false);
                       }
                       else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                           org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                cachedSerFactories.get(i);
                           org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                cachedDeserFactories.get(i);
                           _call.registerTypeMapping(cls, qName, sf, df, false);
                       }
                   }
               }
           }
           return _call;
       }
       catch (java.lang.Throwable _t) {
           throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t); //$NON-NLS-1$
       }
   }

   public org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerOutput engineEventHandler(org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerInput input) throws java.rmi.RemoteException {
       if (super.cachedEndpoint == null) {
           throw new org.apache.axis.NoEndPointException();
       }
       org.apache.axis.client.Call _call = createCall();
       _call.setOperation(_operations[0]);
       _call.setUseSOAPAction(true);
       _call.setSOAPActionURI(""); //$NON-NLS-1$
       _call.setEncodingStyle(null);
       _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
       _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
       _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
       _call.setOperationName(new javax.xml.namespace.QName("", "engineEventHandler")); //$NON-NLS-1$ //$NON-NLS-2$

       setRequestHeaders(_call);
       setAttachments(_call);
try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {input});

       if (_resp instanceof java.rmi.RemoteException) {
           throw (java.rmi.RemoteException)_resp;
       }
       else {
           extractAttachments(_call);
           try {
               return (org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerOutput) _resp;
           } catch (java.lang.Exception _exception) {
               return (org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerOutput) org.apache.axis.utils.JavaUtils.convert(_resp, org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerOutput.class);
           }
       }
 } catch (org.apache.axis.AxisFault axisFaultException) {
 throw axisFaultException;
}
   }

   public void engineAlertHandler(org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineAlertHandlerInput input) throws java.rmi.RemoteException {
       if (super.cachedEndpoint == null) {
           throw new org.apache.axis.NoEndPointException();
       }
       org.apache.axis.client.Call _call = createCall();
       _call.setOperation(_operations[1]);
       _call.setUseSOAPAction(true);
       _call.setSOAPActionURI(""); //$NON-NLS-1$
       _call.setEncodingStyle(null);
       _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
       _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
       _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
       _call.setOperationName(new javax.xml.namespace.QName("", "engineAlertHandler")); //$NON-NLS-1$ //$NON-NLS-2$

       setRequestHeaders(_call);
       setAttachments(_call);
       _call.invokeOneWay(new java.lang.Object[] {input});

   }

   public org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerOutput processEventHandler(org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerInput input) throws java.rmi.RemoteException {
       if (super.cachedEndpoint == null) {
           throw new org.apache.axis.NoEndPointException();
       }
       org.apache.axis.client.Call _call = createCall();
       _call.setOperation(_operations[2]);
       _call.setUseSOAPAction(true);
       _call.setSOAPActionURI(""); //$NON-NLS-1$
       _call.setEncodingStyle(null);
       _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
       _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
       _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
       _call.setOperationName(new javax.xml.namespace.QName("", "processEventHandler")); //$NON-NLS-1$ //$NON-NLS-2$

       setRequestHeaders(_call);
       setAttachments(_call);
try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {input});

       if (_resp instanceof java.rmi.RemoteException) {
           throw (java.rmi.RemoteException)_resp;
       }
       else {
           extractAttachments(_call);
           try {
               return (org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerOutput) _resp;
           } catch (java.lang.Exception _exception) {
               return (org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerOutput) org.apache.axis.utils.JavaUtils.convert(_resp, org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerOutput.class);
           }
       }
 } catch (org.apache.axis.AxisFault axisFaultException) {
 throw axisFaultException;
}
   }

   public void processInfoEventHandler(org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessInfoEventHandlerInput input) throws java.rmi.RemoteException {
       if (super.cachedEndpoint == null) {
           throw new org.apache.axis.NoEndPointException();
       }
       org.apache.axis.client.Call _call = createCall();
       _call.setOperation(_operations[3]);
       _call.setUseSOAPAction(true);
       _call.setSOAPActionURI(""); //$NON-NLS-1$
       _call.setEncodingStyle(null);
       _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
       _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
       _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
       _call.setOperationName(new javax.xml.namespace.QName("", "processInfoEventHandler")); //$NON-NLS-1$ //$NON-NLS-2$

       setRequestHeaders(_call);
       setAttachments(_call);
       _call.invokeOneWay(new java.lang.Object[] {input});

   }

   public void breakpointEventHandler(org.activebpel.rt.axis.bpel.eventhandler.types.AesBreakpointEventHandlerInput input) throws java.rmi.RemoteException {
       if (super.cachedEndpoint == null) {
           throw new org.apache.axis.NoEndPointException();
       }
       org.apache.axis.client.Call _call = createCall();
       _call.setOperation(_operations[4]);
       _call.setUseSOAPAction(true);
       _call.setSOAPActionURI(""); //$NON-NLS-1$
       _call.setEncodingStyle(null);
       _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
       _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
       _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
       _call.setOperationName(new javax.xml.namespace.QName("", "breakpointEventHandler")); //$NON-NLS-1$ //$NON-NLS-2$

       setRequestHeaders(_call);
       setAttachments(_call);
       _call.invokeOneWay(new java.lang.Object[] {input});

   }
}