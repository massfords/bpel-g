//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/rdebug/client/IAeEventHandlerConstants.java,v 1.2 2007/01/25 16:55:12 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.rdebug.client;

/**
 * Constants used for administrative web services on both stub and skeltons.
 */
public interface IAeEventHandlerConstants
{
   /** Target namespace for engine administration web service */
   public final static String EVENT_HANDLER_NS = "http://docs.active-endpoints/wsdl/eventhandler/2007/01/eventhandler.wsdl"; //$NON-NLS-1$
   /** Target namespace for engine administration web service */
   public final static String EVENT_HANDLER_SERVICE = "ActiveBpelEventHandler"; //$NON-NLS-1$
   
   // TODO (RN) These constants will go away in next release
   /** Target namespace for RPC style engine administration web service. */ 
   public final static String RPC_EVENT_HANDLER_NS = "urn:AeRemoteDebugServices"; //$NON-NLS-1$
   /** Service name for RPC style engine administration service */ 
   public final static String RPC_EVENT_HANDLER_SERVICE = "BpelEventHandler"; //$NON-NLS-1$
}
