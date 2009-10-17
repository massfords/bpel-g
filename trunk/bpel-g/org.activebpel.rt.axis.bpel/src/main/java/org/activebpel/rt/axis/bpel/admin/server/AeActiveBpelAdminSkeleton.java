/**
 * AeActiveBpelAdminSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.admin.server;

public class AeActiveBpelAdminSkeleton implements org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin, org.apache.axis.wsdl.Skeleton {
   private org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin impl;
   private static java.util.Map _myOperations = new java.util.Hashtable();
   private static java.util.Collection _myOperationsList = new java.util.ArrayList();

   /**
    * Returns List of OperationDesc objects with this name
    */
   public static java.util.List getOperationDescByName(java.lang.String methodName) {
      return (java.util.List)_myOperations.get(methodName);
   }

   /**
    * Returns Collection of OperationDescs
    */
   public static java.util.Collection getOperationDescs() {
      return _myOperationsList;
   }

   static {
      org.apache.axis.description.OperationDesc _oper;
      org.apache.axis.description.FaultDesc _fault;
      org.apache.axis.description.ParameterDesc [] _params;
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getConfigurationInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesVoidType"), org.activebpel.rt.axis.bpel.admin.types.AesVoidType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("getConfiguration", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getConfigurationOutput")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesConfigurationType"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "GetConfiguration"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("getConfiguration") == null) { //$NON-NLS-1$
         _myOperations.put("getConfiguration", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("getConfiguration")).add(_oper); //$NON-NLS-1$
      _fault = new org.apache.axis.description.FaultDesc();
      _fault.setName("AdminFault"); //$NON-NLS-1$
      _fault.setQName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "adminFaultElement"));  //$NON-NLS-1$//$NON-NLS-2$
      _fault.setClassName("org.activebpel.rt.axis.bpel.admin.types.AdminFault"); //$NON-NLS-1$
      _fault.setXmlType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AdminFault")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.addFault(_fault);
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "setConfigurationInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesConfigurationType"), org.activebpel.rt.axis.bpel.admin.types.AesConfigurationType.class, false, false),   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("setConfiguration", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "SetConfiguration")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("setConfiguration") == null) { //$NON-NLS-1$
         _myOperations.put("setConfiguration", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("setConfiguration")).add(_oper); //$NON-NLS-1$
      _fault = new org.apache.axis.description.FaultDesc();
      _fault.setName("AdminFault"); //$NON-NLS-1$
      _fault.setQName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "adminFaultElement"));  //$NON-NLS-1$//$NON-NLS-2$
      _fault.setClassName("org.activebpel.rt.axis.bpel.admin.types.AdminFault"); //$NON-NLS-1$
      _fault.setXmlType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AdminFault"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.addFault(_fault);
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "suspendProcessInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("suspendProcess", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "SuspendProcess"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("suspendProcess") == null) { //$NON-NLS-1$
         _myOperations.put("suspendProcess", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("suspendProcess")).add(_oper); //$NON-NLS-1$
      _fault = new org.apache.axis.description.FaultDesc();
      _fault.setName("AdminFault"); //$NON-NLS-1$
      _fault.setQName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "adminFaultElement"));  //$NON-NLS-1$//$NON-NLS-2$
      _fault.setClassName("org.activebpel.rt.axis.bpel.admin.types.AdminFault"); //$NON-NLS-1$
      _fault.setXmlType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AdminFault")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.addFault(_fault);
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "resumeProcessInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessType.class, false, false),   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("resumeProcess", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "ResumeProcess")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("resumeProcess") == null) { //$NON-NLS-1$
         _myOperations.put("resumeProcess", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("resumeProcess")).add(_oper); //$NON-NLS-1$
      _fault = new org.apache.axis.description.FaultDesc();
      _fault.setName("AdminFault"); //$NON-NLS-1$
      _fault.setQName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "adminFaultElement")); //$NON-NLS-1$ //$NON-NLS-2$
      _fault.setClassName("org.activebpel.rt.axis.bpel.admin.types.AdminFault"); //$NON-NLS-1$
      _fault.setXmlType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AdminFault")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.addFault(_fault);
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "resumeProcessObjectInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessObjectType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessObjectType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("resumeProcessObject", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "ResumeProcessObject")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("resumeProcessObject") == null) { //$NON-NLS-1$
         _myOperations.put("resumeProcessObject", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("resumeProcessObject")).add(_oper); //$NON-NLS-1$
      _fault = new org.apache.axis.description.FaultDesc();
      _fault.setName("AdminFault"); //$NON-NLS-1$
      _fault.setQName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "adminFaultElement")); //$NON-NLS-1$ //$NON-NLS-2$
      _fault.setClassName("org.activebpel.rt.axis.bpel.admin.types.AdminFault"); //$NON-NLS-1$
      _fault.setXmlType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AdminFault")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.addFault(_fault);
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "restartProcessInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessType.class, false, false),   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        };
        _oper = new org.apache.axis.description.OperationDesc("restartProcess", _params, null); //$NON-NLS-1$
        _oper.setElementQName(new javax.xml.namespace.QName("", "RestartProcess")); //$NON-NLS-1$ //$NON-NLS-2$
        _oper.setSoapAction(""); //$NON-NLS-1$
        _myOperationsList.add(_oper);
        if (_myOperations.get("restartProcess") == null) { //$NON-NLS-1$
            _myOperations.put("restartProcess", new java.util.ArrayList()); //$NON-NLS-1$
        }
        ((java.util.List)_myOperations.get("restartProcess")).add(_oper); //$NON-NLS-1$
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("AdminFault"); //$NON-NLS-1$
        _fault.setQName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "adminFaultElement")); //$NON-NLS-1$ //$NON-NLS-2$
        _fault.setClassName("org.activebpel.rt.axis.bpel.admin.types.AdminFault"); //$NON-NLS-1$
        _fault.setXmlType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AdminFault")); //$NON-NLS-1$ //$NON-NLS-2$
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "terminateProcessInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("terminateProcess", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "TerminateProcess")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("terminateProcess") == null) { //$NON-NLS-1$
         _myOperations.put("terminateProcess", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("terminateProcess")).add(_oper); //$NON-NLS-1$
      _fault = new org.apache.axis.description.FaultDesc();
      _fault.setName("AdminFault"); //$NON-NLS-1$
      _fault.setQName(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "adminFaultElement"));  //$NON-NLS-1$//$NON-NLS-2$
      _fault.setClassName("org.activebpel.rt.axis.bpel.admin.types.AdminFault"); //$NON-NLS-1$
      _fault.setXmlType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AdminFault"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.addFault(_fault);
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "addEngineListenerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesEngineRequestType"), org.activebpel.rt.axis.bpel.admin.types.AesEngineRequestType.class, false, false),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("addEngineListener", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "AddEngineListener")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("addEngineListener") == null) { //$NON-NLS-1$
         _myOperations.put("addEngineListener", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("addEngineListener")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "addBreakpointListenerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesBreakpointRequestType"), org.activebpel.rt.axis.bpel.admin.types.AesBreakpointRequestType.class, false, false),   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("addBreakpointListener", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "AddBreakpointListener")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("addBreakpointListener") == null) { //$NON-NLS-1$
         _myOperations.put("addBreakpointListener", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("addBreakpointListener")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "updateBreakpointListenerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesBreakpointRequestType"), org.activebpel.rt.axis.bpel.admin.types.AesBreakpointRequestType.class, false, false),   //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("updateBreakpointList", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "UpdateBreakpointList")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("updateBreakpointList") == null) { //$NON-NLS-1$
         _myOperations.put("updateBreakpointList", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("updateBreakpointList")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "removeEngineListenerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesEngineRequestType"), org.activebpel.rt.axis.bpel.admin.types.AesEngineRequestType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("removeEngineListener", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "RemoveEngineListener")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("removeEngineListener") == null) { //$NON-NLS-1$
         _myOperations.put("removeEngineListener", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("removeEngineListener")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "removeBreakpointListenerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesRemoveBreakpointRequestType"), org.activebpel.rt.axis.bpel.admin.types.AesRemoveBreakpointRequestType.class, false, false),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("removeBreakpointListener", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "RemoveBreakpointListener")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("removeBreakpointListener") == null) { //$NON-NLS-1$
         _myOperations.put("removeBreakpointListener", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("removeBreakpointListener")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "addProcessListenerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessRequestType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessRequestType.class, false, false),   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("addProcessListener", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "AddProcessListener")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("addProcessListener") == null) { //$NON-NLS-1$
         _myOperations.put("addProcessListener", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("addProcessListener")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "removeProcessListenerInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessRequestType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessRequestType.class, false, false),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("removeProcessListener", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "RemoveProcessListener")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("removeProcessListener") == null) { //$NON-NLS-1$
         _myOperations.put("removeProcessListener", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("removeProcessListener")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getVariableDataInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesGetVariableDataType"), org.activebpel.rt.axis.bpel.admin.types.AesGetVariableDataType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("getVariable", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getVariableDataOutput"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesStringResponseType"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "GetVariable"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("getVariable") == null) { //$NON-NLS-1$
         _myOperations.put("getVariable", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("getVariable")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "setVariableDataInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesSetVariableDataType"), org.activebpel.rt.axis.bpel.admin.types.AesSetVariableDataType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("setVariable", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "setVariableDataOutput"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesStringResponseType"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "SetVariable"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("setVariable") == null) { //$NON-NLS-1$
         _myOperations.put("setVariable", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("setVariable")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "addAttachmentDataInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesAddAttachmentDataType"), org.activebpel.rt.axis.bpel.admin.types.AesAddAttachmentDataType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
            //  new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "attachment"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), byte[].class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("addAttachment", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "addAttachmentDataOutput"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesAddAttachmentResponseType"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "AddAttachment"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("addAttachment") == null) {//$NON-NLS-1$
         _myOperations.put("addAttachment", new java.util.ArrayList());//$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("addAttachment")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "removeAttachmentDataInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesRemoveAttachmentDataType"), org.activebpel.rt.axis.bpel.admin.types.AesRemoveAttachmentDataType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("removeAttachments", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "removeAttachmentDataOutput"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesStringResponseType"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "RemoveAttachments"));  //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("removeAttachments") == null) { //$NON-NLS-1$
         _myOperations.put("removeAttachments", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("removeAttachments")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessListInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessFilterType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessFilterType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("getProcessList", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessListOutput"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessListType"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "GetProcessList")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("getProcessList") == null) { //$NON-NLS-1$
         _myOperations.put("getProcessList", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("getProcessList")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessDetailInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("getProcessDetail", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessDetailOutput"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessDetailType"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "GetProcessDetail")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("getProcessDetail") == null) { //$NON-NLS-1$
         _myOperations.put("getProcessDetail", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("getProcessDetail")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessStateInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessType.class, false, false),   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("getProcessState", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessStateOutput"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesStringResponseType")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "GetProcessState")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("getProcessState") == null) { //$NON-NLS-1$
         _myOperations.put("getProcessState", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("getProcessState")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessDigestInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("getProcessDigest", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessDigestOutput"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesDigestType"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "GetProcessDigest")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("getProcessDigest") == null) { //$NON-NLS-1$
         _myOperations.put("getProcessDigest", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("getProcessDigest")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessDefInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("getProcessDef", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessDefOutput")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesStringResponseType")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "GetProcessDef")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("getProcessDef") == null) { //$NON-NLS-1$
         _myOperations.put("getProcessDef", new java.util.ArrayList()); //$NON-NLS-1$
      } 
      ((java.util.List)_myOperations.get("getProcessDef")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessLogInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesProcessType"), org.activebpel.rt.axis.bpel.admin.types.AesProcessType.class, false, false),   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("getProcessLog", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getProcessLogOutput")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesStringResponseType")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "GetProcessLog"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("getProcessLog") == null) { //$NON-NLS-1$
         _myOperations.put("getProcessLog", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("getProcessLog")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getAPIVersionInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesVoidType"), org.activebpel.rt.axis.bpel.admin.types.AesVoidType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("getAPIVersion", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "getAPIVersionOutput"));   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesStringResponseType"));  //$NON-NLS-1$//$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "GetAPIVersion")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("getAPIVersion") == null) { //$NON-NLS-1$
         _myOperations.put("getAPIVersion", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("getAPIVersion")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "deployBprInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesDeployBprType"), org.activebpel.rt.axis.bpel.admin.types.AesDeployBprType.class, false, false),  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("deployBpr", _params, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "deployBprOutput")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesStringResponseType")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setElementQName(new javax.xml.namespace.QName("", "DeployBpr")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("deployBpr") == null) { //$NON-NLS-1$
         _myOperations.put("deployBpr", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("deployBpr")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "setPartnerLinkDataInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesSetPartnerLinkType"), org.activebpel.rt.axis.bpel.admin.types.AesSetPartnerLinkType.class, false, false),     //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("setPartnerLinkData", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "SetPartnerLinkData")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("setPartnerLinkData") == null) { //$NON-NLS-1$
         _myOperations.put("setPartnerLinkData", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("setPartnerLinkData")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "setCorrelationDataInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesSetCorrelationType"), org.activebpel.rt.axis.bpel.admin.types.AesSetCorrelationType.class, false, false),    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("setCorrelationSetData", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "SetCorrelationSetData")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("setCorrelationSetData") == null) { //$NON-NLS-1$
         _myOperations.put("setCorrelationSetData", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("setCorrelationSetData")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "retryActivityInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesRetryActivityType"), org.activebpel.rt.axis.bpel.admin.types.AesRetryActivityType.class, false, false),   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("retryActivity", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "RetryActivity")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("retryActivity") == null) { //$NON-NLS-1$
         _myOperations.put("retryActivity", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("retryActivity")).add(_oper); //$NON-NLS-1$
      _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "completeActivityInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd", "AesCompleteActivityType"), org.activebpel.rt.axis.bpel.admin.types.AesCompleteActivityType.class, false, false),   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
      };
      _oper = new org.apache.axis.description.OperationDesc("completeActivity", _params, null); //$NON-NLS-1$
      _oper.setElementQName(new javax.xml.namespace.QName("", "CompleteActivity")); //$NON-NLS-1$ //$NON-NLS-2$
      _oper.setSoapAction(""); //$NON-NLS-1$
      _myOperationsList.add(_oper);
      if (_myOperations.get("completeActivity") == null) { //$NON-NLS-1$
         _myOperations.put("completeActivity", new java.util.ArrayList()); //$NON-NLS-1$
      }
      ((java.util.List)_myOperations.get("completeActivity")).add(_oper); //$NON-NLS-1$
   }

   public AeActiveBpelAdminSkeleton() {
      this.impl = new AeActiveBpelAdminImpl();
   }

   public AeActiveBpelAdminSkeleton(org.activebpel.rt.axis.bpel.admin.IAeActiveBpelAdmin impl) {
      this.impl = impl;
   }
   public org.activebpel.rt.axis.bpel.admin.types.AesConfigurationType getConfiguration(org.activebpel.rt.axis.bpel.admin.types.AesVoidType input) throws java.rmi.RemoteException, org.activebpel.rt.axis.bpel.admin.types.AdminFault
   {
      org.activebpel.rt.axis.bpel.admin.types.AesConfigurationType ret = impl.getConfiguration(input);
      return ret;
   }

   public void setConfiguration(org.activebpel.rt.axis.bpel.admin.types.AesConfigurationType input) throws java.rmi.RemoteException, org.activebpel.rt.axis.bpel.admin.types.AdminFault
   {
      impl.setConfiguration(input);
   }

   public void suspendProcess(org.activebpel.rt.axis.bpel.admin.types.AesProcessType input) throws java.rmi.RemoteException, org.activebpel.rt.axis.bpel.admin.types.AdminFault
   {
      impl.suspendProcess(input);
   }

   public void resumeProcess(org.activebpel.rt.axis.bpel.admin.types.AesProcessType input) throws java.rmi.RemoteException, org.activebpel.rt.axis.bpel.admin.types.AdminFault
   {
      impl.resumeProcess(input);
   }

   public void resumeProcessObject(org.activebpel.rt.axis.bpel.admin.types.AesProcessObjectType input) throws java.rmi.RemoteException, org.activebpel.rt.axis.bpel.admin.types.AdminFault
   {
      impl.resumeProcessObject(input);
   }

    public void restartProcess(org.activebpel.rt.axis.bpel.admin.types.AesProcessType input) throws java.rmi.RemoteException, org.activebpel.rt.axis.bpel.admin.types.AdminFault
    {
        impl.restartProcess(input);
    }

   public void terminateProcess(org.activebpel.rt.axis.bpel.admin.types.AesProcessType input) throws java.rmi.RemoteException, org.activebpel.rt.axis.bpel.admin.types.AdminFault
   {
      impl.terminateProcess(input);
   }

   public void addEngineListener(org.activebpel.rt.axis.bpel.admin.types.AesEngineRequestType input) throws java.rmi.RemoteException
   {
      impl.addEngineListener(input);
   }

   public void addBreakpointListener(org.activebpel.rt.axis.bpel.admin.types.AesBreakpointRequestType input) throws java.rmi.RemoteException
   {
      impl.addBreakpointListener(input);
   }

   public void updateBreakpointList(org.activebpel.rt.axis.bpel.admin.types.AesBreakpointRequestType input) throws java.rmi.RemoteException
   {
      impl.updateBreakpointList(input);
   }

   public void removeEngineListener(org.activebpel.rt.axis.bpel.admin.types.AesEngineRequestType input) throws java.rmi.RemoteException
   {
      impl.removeEngineListener(input);
   }

   public void removeBreakpointListener(org.activebpel.rt.axis.bpel.admin.types.AesRemoveBreakpointRequestType input) throws java.rmi.RemoteException
   {
      impl.removeBreakpointListener(input);
   }

   public void addProcessListener(org.activebpel.rt.axis.bpel.admin.types.AesProcessRequestType input) throws java.rmi.RemoteException
   {
      impl.addProcessListener(input);
   }

   public void removeProcessListener(org.activebpel.rt.axis.bpel.admin.types.AesProcessRequestType input) throws java.rmi.RemoteException
   {
      impl.removeProcessListener(input);
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType getVariable(org.activebpel.rt.axis.bpel.admin.types.AesGetVariableDataType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType ret = impl.getVariable(input);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType setVariable(org.activebpel.rt.axis.bpel.admin.types.AesSetVariableDataType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType ret = impl.setVariable(input);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesAddAttachmentResponseType addAttachment(org.activebpel.rt.axis.bpel.admin.types.AesAddAttachmentDataType input, byte[] attachment) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesAddAttachmentResponseType ret = impl.addAttachment(input, attachment);
      return ret;
   }

   /**
    * We are receiving attachments out of band. The attachment content will
    * not be part of the message body so Axis requires a single argument method
    * like this.
    * @param input
    * @throws java.rmi.RemoteException
    */
   public org.activebpel.rt.axis.bpel.admin.types.AesAddAttachmentResponseType addAttachment(org.activebpel.rt.axis.bpel.admin.types.AesAddAttachmentDataType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesAddAttachmentResponseType ret = impl.addAttachment(input, null);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType removeAttachments(org.activebpel.rt.axis.bpel.admin.types.AesRemoveAttachmentDataType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType ret = impl.removeAttachments(input);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesProcessListType getProcessList(org.activebpel.rt.axis.bpel.admin.types.AesProcessFilterType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesProcessListType ret = impl.getProcessList(input);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesProcessDetailType getProcessDetail(org.activebpel.rt.axis.bpel.admin.types.AesProcessType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesProcessDetailType ret = impl.getProcessDetail(input);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType getProcessState(org.activebpel.rt.axis.bpel.admin.types.AesProcessType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType ret = impl.getProcessState(input);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesDigestType getProcessDigest(org.activebpel.rt.axis.bpel.admin.types.AesProcessType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesDigestType ret = impl.getProcessDigest(input);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType getProcessDef(org.activebpel.rt.axis.bpel.admin.types.AesProcessType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType ret = impl.getProcessDef(input);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType getProcessLog(org.activebpel.rt.axis.bpel.admin.types.AesProcessType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType ret = impl.getProcessLog(input);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType getAPIVersion(org.activebpel.rt.axis.bpel.admin.types.AesVoidType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType ret = impl.getAPIVersion(input);
      return ret;
   }

   public org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType deployBpr(org.activebpel.rt.axis.bpel.admin.types.AesDeployBprType input) throws java.rmi.RemoteException
   {
      org.activebpel.rt.axis.bpel.admin.types.AesStringResponseType ret = impl.deployBpr(input);
      return ret;
   }

   public void setPartnerLinkData(org.activebpel.rt.axis.bpel.admin.types.AesSetPartnerLinkType input) throws java.rmi.RemoteException
   {
      impl.setPartnerLinkData(input);
   }

   public void setCorrelationSetData(org.activebpel.rt.axis.bpel.admin.types.AesSetCorrelationType input) throws java.rmi.RemoteException
   {
      impl.setCorrelationSetData(input);
   }

   public void retryActivity(org.activebpel.rt.axis.bpel.admin.types.AesRetryActivityType input) throws java.rmi.RemoteException
   {
      impl.retryActivity(input);
   }

   public void completeActivity(org.activebpel.rt.axis.bpel.admin.types.AesCompleteActivityType input) throws java.rmi.RemoteException
   {
      impl.completeActivity(input);
   }

}
