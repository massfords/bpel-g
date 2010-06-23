// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/rdebug/server/AeRemoteDebugSkeleton.java,v 1.30 2007/09/28 19:51:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.rdebug.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.server.admin.rdebug.server.AeAddAttachmentResponse;
import org.activebpel.rt.bpel.server.admin.rdebug.server.AeBreakpointList;
import org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.wsio.AeWebServiceAttachment;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.wsdl.Skeleton;

/**
 * The server side class which receives remote debug commands from the client stub.
 * Note: Please if operations are modified please keep the engineAdmin.wsdl file up to date.
 */
public class AeRemoteDebugSkeleton implements IAeBpelAdmin, Skeleton
{
   private IAeBpelAdmin mAdmin;
   private static Map sOperations = new Hashtable();
   private static Collection sOperationsList = new ArrayList();

   // QName type declarations used for creating parameter descriptors
   private static final QName sLong   = new QName("http://www.w3.org/2001/XMLSchema", "long"); //$NON-NLS-1$ //$NON-NLS-2$
   private static final QName sString = new QName("http://www.w3.org/2001/XMLSchema", "string"); //$NON-NLS-1$ //$NON-NLS-2$
   private static final QName sBoolean = new QName("http://www.w3.org/2001/XMLSchema", "boolean"); //$NON-NLS-1$ //$NON-NLS-2$

   private static boolean sDeprecationWarningIssued = false;

   /**
    * Returns List of OperationDesc objects with this name
    */
   public static List getOperationDescByName(String methodName)
   {
      return (List) sOperations.get(methodName);
   }

   /**
    * Returns Collection of OperationDescs
    */
   public static Collection getOperationDescs()
   {
      return sOperationsList;
   }

   static
   {
      ParameterDesc[] params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };

      OperationDesc operation = new OperationDesc("suspendProcess", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "suspendProcess")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("suspendProcess") == null) //$NON-NLS-1$
         sOperations.put("suspendProcess", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("suspendProcess")).add(operation); //$NON-NLS-1$
      FaultDesc fault = new FaultDesc();
      fault.setName("AeBusinessProcessException"); //$NON-NLS-1$
      fault.setQName(new QName("urn:AeAdminServices", "fault")); //$NON-NLS-1$ //$NON-NLS-2$
      fault.setClassName("org.activebpel.rt.bpel.AeBusinessProcessException"); //$NON-NLS-1$
      fault.setXmlType(new QName("http://bpel.rt.activebpel.org", "AeBusinessProcessException")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.addFault(fault);


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("resumeProcess", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "resumeProcess")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("resumeProcess") == null) //$NON-NLS-1$
         sOperations.put("resumeProcess", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("resumeProcess")).add(operation); //$NON-NLS-1$
      fault = new FaultDesc();
      fault.setName("AeBusinessProcessException"); //$NON-NLS-1$
      fault.setQName(new QName("urn:AeAdminServices", "fault")); //$NON-NLS-1$ //$NON-NLS-2$
      fault.setClassName("org.activebpel.rt.bpel.AeBusinessProcessException"); //$NON-NLS-1$
      fault.setXmlType(new QName("http://bpel.rt.activebpel.org", "AeBusinessProcessException")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.addFault(fault);


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"),      ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aLocation"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("resumeProcessObject", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "resumeProcessObject")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("resumeProcessObject") == null) //$NON-NLS-1$
         sOperations.put("resumeProcessObject", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("resumeProcessObject")).add(operation); //$NON-NLS-1$
      fault = new FaultDesc();
      fault.setName("AeBusinessProcessException"); //$NON-NLS-1$
      fault.setQName(new QName("urn:AeAdminServices", "fault")); //$NON-NLS-1$ //$NON-NLS-2$
      fault.setClassName("org.activebpel.rt.bpel.AeBusinessProcessException"); //$NON-NLS-1$
      fault.setXmlType(new QName("http://bpel.rt.activebpel.org", "AeBusinessProcessException")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.addFault(fault);


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("restartProcess", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "restartProcess")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("restartProcess") == null) //$NON-NLS-1$
         sOperations.put("restartProcess", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("restartProcess")).add(operation); //$NON-NLS-1$
      fault = new FaultDesc();
      fault.setName("AeBusinessProcessException"); //$NON-NLS-1$
      fault.setQName(new QName("urn:AeAdminServices", "fault")); //$NON-NLS-1$ //$NON-NLS-2$
      fault.setClassName("org.activebpel.rt.bpel.AeBusinessProcessException"); //$NON-NLS-1$
      fault.setXmlType(new QName("http://bpel.rt.activebpel.org", "AeBusinessProcessException")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.addFault(fault);


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("terminateProcess", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "terminateProcess")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("terminateProcess") == null) //$NON-NLS-1$
         sOperations.put("terminateProcess", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("terminateProcess")).add(operation); //$NON-NLS-1$
      fault = new FaultDesc();
      fault.setName("AeBusinessProcessException"); //$NON-NLS-1$
      fault.setQName(new QName("urn:AeAdminServices", "fault")); //$NON-NLS-1$ //$NON-NLS-2$
      fault.setClassName("org.activebpel.rt.bpel.AeBusinessProcessException"); //$NON-NLS-1$
      fault.setXmlType(new QName("http://bpel.rt.activebpel.org", "AeBusinessProcessException")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.addFault(fault);


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aContextId"),   ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aEndpointURL"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("addEngineListener", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "addEngineListener")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("addEngineListener") == null) //$NON-NLS-1$
         sOperations.put("addEngineListener", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("addEngineListener")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aContextId"),   ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aEndpointURL"), ParameterDesc.IN, sString, String.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aBreakpointList"), ParameterDesc.IN, new QName("http://impl.bpel.rt.activebpel.org", "AeBreakpointList"), AeBreakpointList.class, false, false)  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      operation = new OperationDesc("addBreakpointListener", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "addBreakpointListener")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("addBreakpointListener") == null) //$NON-NLS-1$
         sOperations.put("addBreakpointListener", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("addBreakpointListener")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aContextId"),   ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aEndpointURL"), ParameterDesc.IN, sString, String.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aBreakpointList"), ParameterDesc.IN, new QName("http://impl.bpel.rt.activebpel.org", "AeBreakpointList"), AeBreakpointList.class, false, false)  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      operation = new OperationDesc("updateBreakpointList", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "updateBreakpointList")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("updateBreakpointList") == null) //$NON-NLS-1$
         sOperations.put("updateBreakpointList", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("updateBreakpointList")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aContextId"),   ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aEndpointURL"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("removeEngineListener", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "removeEngineListener")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("removeEngineListener") == null) //$NON-NLS-1$
         sOperations.put("removeEngineListener", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("removeEngineListener")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aContextId"),   ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aEndpointURL"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("removeBreakpointListener", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "removeBreakpointListener")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("removeBreakpointListener") == null) //$NON-NLS-1$
         sOperations.put("removeBreakpointListener", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("removeBreakpointListener")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aContextId"),   ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aPid"),         ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aEndpointURL"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("addProcessListener", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "addProcessListener")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("addProcessListener") == null) //$NON-NLS-1$
         sOperations.put("addProcessListener", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("addProcessListener")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aContextId"),   ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aEndpointURL"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("removeProcessListener", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "removeProcessListener")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("removeProcessListener") == null) //$NON-NLS-1$
         sOperations.put("removeProcessListener", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("removeProcessListener")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aFilter"), ParameterDesc.IN, new QName("http://impl.bpel.rt.activebpel.org", "AeProcessFilter"), AeProcessFilter.class, false, false)  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      };
      operation = new OperationDesc("getProcessList", params, new QName("", "getProcessListReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://impl.bpel.rt.activebpel.org", "AeProcessListResult")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeAdminServices", "getProcessList")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("getProcessList") == null) //$NON-NLS-1$
         sOperations.put("getProcessList", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("getProcessList")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("getProcessDetail", params, new QName("", "getProcessDetailReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://impl.bpel.rt.activebpel.org", "AeProcessInstanceDetail")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeAdminServices", "getProcessDetail")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("getProcessDetail") == null) //$NON-NLS-1$
         sOperations.put("getProcessDetail", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("getProcessDetail")).add(operation); //$NON-NLS-1$

      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("getProcessState", params, new QName("", "getProcessStateReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeAdminServices", "getProcessState")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("getProcessState") == null) //$NON-NLS-1$
         sOperations.put("getProcessState", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("getProcessState")).add(operation); //$NON-NLS-1$

      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"),          ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aVariablePath"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("getVariable", params, new QName("", "getVariableReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeAdminServices", "getVariable")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("getVariable") == null) //$NON-NLS-1$
         sOperations.put("getVariable", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("getVariable")).add(operation); //$NON-NLS-1$
      
      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"),          ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aVariablePath"), ParameterDesc.IN, sString, String.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aVariableData"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("setVariable", params, new QName("", "setVariableReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeAdminServices", "setVariable")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("setVariable") == null) //$NON-NLS-1$
         sOperations.put("setVariable", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("setVariable")).add(operation); //$NON-NLS-1$
      
      /////////////////////////////////
      // deprectated interface methods
      /////////////////////////////////
      
      params = new ParameterDesc[]
	      {
	         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
	      };
      operation = new OperationDesc("getProcessStateStr", params, new QName("", "getProcessStateReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeAdminServices", "getProcessStateStr")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("getProcessStateStr") == null) //$NON-NLS-1$
         sOperations.put("getProcessStateStr", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("getProcessStateStr")).add(operation); //$NON-NLS-1$

      params = new ParameterDesc[]
	      {
	         new ParameterDesc(new QName("", "aPid"),          ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
	         new ParameterDesc(new QName("", "aVariablePath"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
	      };
      operation = new OperationDesc("getVariableStr", params, new QName("", "getVariableReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeAdminServices", "getVariableStr")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("getVariableStr") == null) //$NON-NLS-1$
         sOperations.put("getVariableStr", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("getVariableStr")).add(operation); //$NON-NLS-1$

      /////////////////////////////////////
      // end deprectated interface methods
      /////////////////////////////////////
      
      params = new ParameterDesc [] 
      {
         new ParameterDesc(new QName("", "aProcessName"), ParameterDesc.IN, sLong, Long.class, false, false)  //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("getProcessDigest", params, new QName("", "getProcessDigestReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "base64Binary")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeEngineServices", "getProcessDigest")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);

      if (sOperations.get("getProcessDigest") == null)  //$NON-NLS-1$
          sOperations.put("getProcessDigest", new ArrayList()); //$NON-NLS-1$

      ((List)sOperations.get("getProcessDigest")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc [] 
      {
         new ParameterDesc(new QName("", "aProcessName"), ParameterDesc.IN, sLong, Long.class, false, false)  //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("getProcessDef", params, new QName("", "getProcessDefReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeEngineServices", "getProcessDef")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("getProcessDef") == null)  //$NON-NLS-1$
          sOperations.put("getProcessDef", new ArrayList()); //$NON-NLS-1$

      ((List)sOperations.get("getProcessDef")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc [] 
      {
         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false)  //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("getProcessLog", params, new QName("", "getProcessLogReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeEngineServices", "getProcessLog")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("getProcessLog") == null) //$NON-NLS-1$
          sOperations.put("getProcessLog", new ArrayList()); //$NON-NLS-1$
      
      ((List)sOperations.get("getProcessLog")).add(operation); //$NON-NLS-1$


      params = new ParameterDesc [] {};
      operation = new OperationDesc("getAPIVersion", params, new QName("", "getAPIVersionReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeEngineServices", "getAPIVersion")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("getAPIVersion") == null) //$NON-NLS-1$
          sOperations.put("getAPIVersion", new ArrayList()); //$NON-NLS-1$
      
      ((List)sOperations.get("getAPIVersion")).add(operation); //$NON-NLS-1$

      params = new ParameterDesc []
      {
            new ParameterDesc(new QName("", "aBprFilename"), ParameterDesc.IN, sString, String.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
            new ParameterDesc(new QName("", "aBase64File"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("deployBpr", params, new QName("", "deployBprReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeEngineServices", "deployBpr")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("deployBpr") == null) //$NON-NLS-1$
          sOperations.put("deployBpr", new ArrayList()); //$NON-NLS-1$
      
      ((List)sOperations.get("deployBpr")).add(operation); //$NON-NLS-1$

      params = new ParameterDesc [] {};
      operation = new OperationDesc("getConfiguration", params, new QName("", "getConfiguration")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(new QName("http://www.w3.org/2001/XMLSchema", "string")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setElementQName(new QName("urn:AeEngineServices", "getConfiguration")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("getConfiguration") == null) //$NON-NLS-1$
          sOperations.put("getConfiguration", new ArrayList()); //$NON-NLS-1$
       
      ((List)sOperations.get("getConfiguration")).add(operation); //$NON-NLS-1$

      params = new ParameterDesc []
       {
             new ParameterDesc(new QName("", "aXmlString"), ParameterDesc.IN, sString, String.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
       };
      operation = new OperationDesc("setConfiguration", params, new QName("", "setConfiguration")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setElementQName(new QName("urn:AeEngineServices", "setConfiguration")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("setConfiguration") == null) //$NON-NLS-1$
          sOperations.put("setConfiguration", new ArrayList()); //$NON-NLS-1$
       
      ((List)sOperations.get("setConfiguration")).add(operation); //$NON-NLS-1$
   
      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aIsPartnerRole"), ParameterDesc.IN, sBoolean, boolean.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aLocationPath"), ParameterDesc.IN, sString, String.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aData"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("setPartnerLinkData", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "setPartnerLinkData")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("setPartnerLinkData") == null) //$NON-NLS-1$
         sOperations.put("setPartnerLinkData", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("setPartnerLinkData")).add(operation); //$NON-NLS-1$

      params = new ParameterDesc[]
      {
         new ParameterDesc(new QName("", "aPid"), ParameterDesc.IN, sLong, long.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aLocationPath"), ParameterDesc.IN, sString, String.class, false, false), //$NON-NLS-1$ //$NON-NLS-2$
         new ParameterDesc(new QName("", "aData"), ParameterDesc.IN, sString, String.class, false, false) //$NON-NLS-1$ //$NON-NLS-2$
      };
      operation = new OperationDesc("setCorrelationSetData", params, null); //$NON-NLS-1$
      operation.setElementQName(new QName("urn:AeAdminServices", "setCorrelationSetData")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("setCorrelationSetData") == null) //$NON-NLS-1$
         sOperations.put("setCorrelationSetData", new ArrayList()); //$NON-NLS-1$

      ((List) sOperations.get("setCorrelationSetData")).add(operation); //$NON-NLS-1$
      
      params = new ParameterDesc [] {};
      operation = new OperationDesc("isInternalWorkManager", params, new QName("", "isInternalWorkManagerReturn")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      operation.setReturnType(sBoolean);
      operation.setElementQName(new QName("urn:AeEngineServices", "isInternalWorkManager")); //$NON-NLS-1$ //$NON-NLS-2$
      operation.setSoapAction(""); //$NON-NLS-1$
      sOperationsList.add(operation);
      if (sOperations.get("isInternalWorkManager") == null) //$NON-NLS-1$
          sOperations.put("isInternalWorkManager", new ArrayList()); //$NON-NLS-1$
      
      ((List)sOperations.get("isInternalWorkManager")).add(operation); //$NON-NLS-1$
   }

   /**
    * Default constructor for the remote debug skelton.
    */
   public AeRemoteDebugSkeleton()
   {
      mAdmin = AeEngineFactory.getRemoteDebugImpl();
   }

   /**
    * Constructor which takes as input an engine implementation.
    * @param aEngine the engine to be used by the skeleton
    */
   public AeRemoteDebugSkeleton(IAeBpelAdmin aEngine)
   {
      mAdmin = aEngine;
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getConfiguration()
    */
   public String getConfiguration() throws RemoteException, AeException
   {
      return getAdmin().getConfiguration();
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#setConfiguration(java.lang.String)
    */
   public void setConfiguration(String aXmlString) throws RemoteException,
         AeException
   {
      getAdmin().setConfiguration(aXmlString);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#suspendProcess(long)
    */
   public void suspendProcess(long aPid) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().suspendProcess(aPid);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#resumeProcess(long)
    */
   public void resumeProcess(long aPid) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().resumeProcess(aPid);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#resumeProcessObject(long, java.lang.String)
    */
   public void resumeProcessObject(long aPid, String aLocation) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().resumeProcessObject(aPid, aLocation);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#restartProcess(long)
    */
   public void restartProcess(long aPid) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().restartProcess(aPid);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#terminateProcess(long)
    */
   public void terminateProcess(long aPid) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().terminateProcess(aPid);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#addEngineListener(long, java.lang.String)
    */
   public void addEngineListener(long aContextId, String aEndpointURL) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().addEngineListener(aContextId, aEndpointURL);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#addBreakpointListener(long, java.lang.String, org.activebpel.rt.bpel.server.admin.rdebug.server.AeBreakpointList)
    */
   public void addBreakpointListener(long aContextId, String aEndpointURL, AeBreakpointList aBreakpointList) 
      throws RemoteException, AeBusinessProcessException
   {
      getAdmin().addBreakpointListener(aContextId, aEndpointURL, aBreakpointList);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#removeBreakpointListener(long, java.lang.String)
    */
   public void removeBreakpointListener(long aContextId, String aEndpointURL) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().removeBreakpointListener(aContextId, aEndpointURL);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#updateBreakpointList(long, java.lang.String, org.activebpel.rt.bpel.server.admin.rdebug.server.AeBreakpointList)
    */
   public void updateBreakpointList(long aContextId, String aEndpointURL, AeBreakpointList aBreakpointList ) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().updateBreakpointList(aContextId, aEndpointURL, aBreakpointList);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#removeEngineListener(long, java.lang.String)
    */
   public void removeEngineListener(long aContextId, String aEndpointURL) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().removeEngineListener(aContextId, aEndpointURL);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#addProcessListener(long, long, java.lang.String)
    */
   public void addProcessListener(long aContextId, long aPid, String aEndpointURL) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().addProcessListener(aContextId, aPid, aEndpointURL);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#removeProcessListener(long, long, java.lang.String)
    */
   public void removeProcessListener(long aContextId, long aPid, String aEndpointURL) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().removeProcessListener(aContextId, aPid, aEndpointURL);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessList(org.activebpel.rt.bpel.impl.list.AeProcessFilter)
    */
   public AeProcessListResult getProcessList(AeProcessFilter aFilter) throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().getProcessList(aFilter);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessDetail(long)
    */
   public AeProcessInstanceDetail getProcessDetail(long aPid) throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().getProcessDetail(aPid);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessState(long)
    */
   public String getProcessState(long aPid) throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().getProcessState(aPid);
   }

   /**
    * @deprecated Use getProcessState. 
    */
   public String getProcessStateStr(long aPid) throws RemoteException, AeBusinessProcessException
   {
      return getProcessState(aPid);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessDigest(long)
    */
   public byte [] getProcessDigest(long aProcessId) throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().getProcessDigest(aProcessId); 
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessDef(long)
    */
   public String getProcessDef(long aProcessId) throws RemoteException, AeBusinessProcessException
   { 
      return getAdmin().getProcessDef(aProcessId); 
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getProcessLog(long)
    */
   public String getProcessLog(long aPid) throws RemoteException, AeBusinessProcessException
   { 
      return getAdmin().getProcessLog(aPid); 
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getVariable(long, java.lang.String)
    */
   public String getVariable(long aPid, String aVariablePath) throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().getVariable(aPid, aVariablePath);
   }

   /**
    * @deprecated Use getVariable. 
    */
   public String getVariableStr(long aPid, String aVariablePath) throws RemoteException, AeBusinessProcessException
   {
      return getVariable(aPid, aVariablePath);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#setVariable(long, java.lang.String, java.lang.String)
    */
   public String setVariable(long aPid, String aVariablePath, String aVariableData) throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().setVariable(aPid, aVariablePath, aVariableData);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#addAttachment(long, java.lang.String, org.activebpel.wsio.AeWebServiceAttachment)
    */
   public AeAddAttachmentResponse addAttachment(long aPid, String aVariablePath, AeWebServiceAttachment aAttachments) throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().addAttachment(aPid, aVariablePath, aAttachments);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#removeAttachments(long, java.lang.String, int[])
    */
   public String removeAttachments(long aPid, String aVariablePath, int[] aAttachmentItemNumbers) throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().removeAttachments(aPid, aVariablePath, aAttachmentItemNumbers);
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#getAPIVersion()
    */
   public String getAPIVersion() throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().getAPIVersion();
   }

   /**
    * @throws RemoteException
    * @throws AeBusinessProcessException
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#deployBpr(java.lang.String, java.lang.String)
    */
   public String deployBpr(String aBprFilename, String aBase64File) throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().deployBpr(aBprFilename, aBase64File);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#setPartnerLinkData(long, boolean, java.lang.String, java.lang.String)
    */
   public void setPartnerLinkData(long aPid, boolean aIsPartnerRole, String aLocationPath, String aData) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().setPartnerLinkData(aPid, aIsPartnerRole, aLocationPath, aData);
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#setCorrelationSetData(long, java.lang.String, java.lang.String)
    */
   public void setCorrelationSetData(long aPid, String aLocationPath, String aData) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().setCorrelationSetData(aPid, aLocationPath, aData);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#retryActivity(long, java.lang.String, boolean)
    */
   public void retryActivity(long aPid, String aLocationPath, boolean aAtScope) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().retryActivity(aPid, aLocationPath, aAtScope);
   }

   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#completeActivity(long, java.lang.String)
    */
   public void completeActivity(long aPid, String aLocationPath) throws RemoteException, AeBusinessProcessException
   {
      getAdmin().completeActivity(aPid, aLocationPath);
   }
 
   /**
    * @see org.activebpel.rt.bpel.server.admin.rdebug.server.IAeBpelAdmin#isInternalWorkManager()
    */
   public boolean isInternalWorkManager() throws RemoteException, AeBusinessProcessException
   {
      return getAdmin().isInternalWorkManager();
   }
   
   /**
    * Getter for the BPEL Admin object.
    * @return Returns the admin.
    */
   public IAeBpelAdmin getAdmin()
   {
      if (! sDeprecationWarningIssued)
      {
         sDeprecationWarningIssued = true;
         String msg = "\n********************************************************************************" + //$NON-NLS-1$
                      "\nApplication is currently using deprecated API's to communicate with ActiveBpel  " + //$NON-NLS-1$
                      "\nengine administration. This API will not be supported in future releases." + //$NON-NLS-1$
                      "\n********************************************************************************"; //$NON-NLS-1$

         AeException.logWarning(msg);
      }
      
      return mAdmin;
   }
   
 }