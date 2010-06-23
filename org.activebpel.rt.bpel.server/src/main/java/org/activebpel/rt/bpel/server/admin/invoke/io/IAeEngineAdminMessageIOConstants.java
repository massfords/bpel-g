//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/invoke/io/IAeEngineAdminMessageIOConstants.java,v 1.1 2007/12/19 21:12:29 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.invoke.io;

import org.activebpel.rt.bpel.server.admin.rdebug.server.IAeRemoteDebugConstants;

/**
 * WSDL and Schema namespace constants.
 */
public interface IAeEngineAdminMessageIOConstants
{
   /** Namespace for engine administration wsdl ns*/
   public final static String ENGINE_ADMIN_WSDL_NS = IAeRemoteDebugConstants.ENGINE_ADMIN_NS;
   
   /** Namespace for engine administration schema type*/
   public final static String ENGINE_ADMIN_SCHEMA_NS = "http://schemas.active-endpoints.com/activebpeladmin/2007/01/activebpeladmin.xsd"; //$NON-NLS-1$

   /** Namespace for engine administration internal wsdl*/
   public final static String ENGINE_ADMININTERNAL_WSDL_NS = "http://docs.active-endpoints/wsdl/activebpeladmin/2007/12/activebpeladmininternal.wsdl"; //$NON-NLS-1$
   
   /** Namespace for engine administration internal schema type*/
   public final static String ENGINE_ADMININTERNAL_SCHEMA_NS = "http://schemas.active-endpoints.com/activebpeladmin/2007/12/activebpeladmininternal.xsd"; //$NON-NLS-1$

}
