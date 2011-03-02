/**
 * IAeActiveBpelEventHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 20, 2006 (01:34:32 EST) WSDL2Java emitter.
 */

package org.activebpel.rt.axis.bpel.eventhandler;

public interface IAeActiveBpelEventHandler extends java.rmi.Remote {
    public org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerOutput engineEventHandler(org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineEventHandlerInput input) throws java.rmi.RemoteException;
    public void engineAlertHandler(org.activebpel.rt.axis.bpel.eventhandler.types.AesEngineAlertHandlerInput input) throws java.rmi.RemoteException;
    public org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerOutput processEventHandler(org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessEventHandlerInput input) throws java.rmi.RemoteException;
    public void processInfoEventHandler(org.activebpel.rt.axis.bpel.eventhandler.types.AesProcessInfoEventHandlerInput input) throws java.rmi.RemoteException;
    public void breakpointEventHandler(org.activebpel.rt.axis.bpel.eventhandler.types.AesBreakpointEventHandlerInput input) throws java.rmi.RemoteException;
}
