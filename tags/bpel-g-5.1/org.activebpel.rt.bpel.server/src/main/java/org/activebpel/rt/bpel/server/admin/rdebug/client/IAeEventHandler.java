// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/rdebug/client/IAeEventHandler.java,v 1.7 2007/01/25 16:55:12 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.rdebug.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

/**
 * Interface which defines the operations exposed by the remote debugger.
 */
public interface IAeEventHandler extends Remote
{
   /**
    * Event handler callbacl for engine alerts such as process suspending or faulting
    * 
    * @param aContextId the context id used to locate the callback
    * @param aProcessId the id of the process affected
    * @param aEventType the event type
    * @param aProcessName the name of the process
    * @param aLocationPath location path of the activity causing the fault/suspension
    * @param aFaultName the name of the fault that caused the alert
    * @param aDetails optional details for the event
    * @param aTimestamp the event's timestamp
    * @throws RemoteException
    */
   public void engineAlertHandler(long aContextId, long aProcessId, int aEventType, QName aProcessName,
         String aLocationPath, QName aFaultName, Document aDetails, Date aTimestamp) throws RemoteException;

   /**
    * Event handler callback for engine events, such as process creation and termination.
    * 
    * @param aContextId the context id used to locate the callback
    * @param aProcessId the id of the process affected
    * @param aEventType the event type
    * @param aProcessName the name of the process
    * @param aTimestamp the event's timestamp
    * @return true return indicates to suspend the associated process.
    */
   public boolean engineEventHandler(long aContextId, long aProcessId, int aEventType, QName aProcessName,
         Date aTimestamp) throws RemoteException;

   /**
    * Event handler callback for process events, such as process state and info.
    * 
    * @param aContextId the context id used to locate the callback
    * @param aProcessId the id of the process affected
    * @param aPath path of node affected by the event
    * @param aEventType the event type
    * @param aFaultName an optional fault name if fault event
    * @param aText optional text
    * @param aName The process' QName.
    * @param aTimestamp the event's timestamp
    * @return True if the process needs to be suspended, False otherwise
    */
   public boolean processEventHandler(long aContextId, long aProcessId, String aPath, int aEventType,
         String aFaultName, String aText, QName aName, Date aTimestamp) throws RemoteException;

   /**
    * Event handler callback for process info events, such as process state and info.
    * 
    * @param aContextId the context id used to locate the callback
    * @param aProcessId the id of the process affected
    * @param aPath path of node affected by the event
    * @param aEventType the event type
    * @param aFaultName an optional fault name if fault event
    * @param aText optional text
    * @param aTimestamp the event's timestamp
    */
   public void processInfoEventHandler(long aContextId, long aProcessId, String aPath, int aEventType,
         String aFaultName, String aText, Date aTimestamp) throws RemoteException;

   /**
    * Event handler callback for breakpoint events.
    * 
    * @param aContextId the context id used to locate the callback
    * @param aProcessId the id of the process affected
    * @param aPath the node path where the breakpoint occurred.
    * @param aProcessName the name of the process
    * @param aTimestamp the event's timestamp
    */
   public void breakpointEventHandler(long aContextId, long aProcessId, String aPath, QName aProcessName,
         Date aTimestamp) throws RemoteException;
}