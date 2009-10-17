//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/rdebug/server/IAeRemoteDebugConstants.java,v 1.2 2007/01/25 16:55:12 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.rdebug.server;

/**
 * Constants used for administrative web services on both stub and skeltons.
 */
public interface IAeRemoteDebugConstants
{
   /** Marker node path attribute. */
   public static final String NODE_PATH = "node_path" ; // $NON-NLS-1$ //$NON-NLS-1$
   
   /** Marker process QName attribute. */
   public static final String PROCESS_QNAME_NAMESPACE = "process_qname_namespace" ; // $NON-NLS-1$ //$NON-NLS-1$
   
   /** Marker process QName attribute. */
   public static final String PROCESS_QNAME_LOCAL_PART = "process_qname_local_part" ; // $NON-NLS-1$ //$NON-NLS-1$

   /** Target namespace for engine administration web service */
   public final static String ENGINE_ADMIN_NS = "http://docs.active-endpoints/wsdl/activebpeladmin/2007/01/activebpeladmin.wsdl"; //$NON-NLS-1$
   /** Service name for engine administration service */
   public final static String ENGINE_ADMIN_SERVICE = "ActiveBpelAdmin"; //$NON-NLS-1$

   // TODO (RN) These constants will go away in next release
   /** Target namespace for RPC style engine administration web service. */ 
   public final static String RPC_ENGINE_ADMIN_NS = "urn:AeAdminServices"; //$NON-NLS-1$
   /** Service name for RPC style engine administration service. */ 
   public final static String RPC_ENGINE_ADMIN_SERVICE = "BpelEngineAdmin"; //$NON-NLS-1$
}