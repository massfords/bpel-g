//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAePddXmlConstants.java,v 1.14 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

/**
 * Interface for the pdd xml constants.
 */
public interface IAePddXmlConstants
{
   // PDD XML constants.
   public static final String ATT_PROCESS_LOCATION   = "location"; //$NON-NLS-1$
   public static final String ATT_MYROLE_SERVICE     = "service"; //$NON-NLS-1$
   public static final String ATT_NAME               = "name"; //$NON-NLS-1$
   public static final String ATT_LOCATION           = "location"; //$NON-NLS-1$
   public static final String ATT_PROCESS_GROUP      = "processGroup"; //$NON-NLS-1$
   public static final String ATT_PROCESS_RETENTION_DAYS = "processRetentionDays"; //$NON-NLS-1$
   public static final String ATT_CUSTOM_INVOKER     = "customInvokerUri"; //$NON-NLS-1$
   public static final String ATT_INVOKE_HANDLER     = "invokeHandler"; //$NON-NLS-1$
   public static final String ATT_ENDPOINT_REF       = "endpointReference"; //$NON-NLS-1$
   public static final String ATT_PERSISTENCE_TYPE   = "persistenceType"; //$NON-NLS-1$
   public static final String ATT_TRANSACTION_TYPE   = "transactionType"; //$NON-NLS-1$
   public static final String ATT_SERVICE            = "service"; //$NON-NLS-1$
   public static final String ATT_BINDING            = "binding"; //$NON-NLS-1$
   public static final String ATT_ALLOWED_ROLES      = "allowedRoles"; //$NON-NLS-1$
   public static final String ATT_SUSPEND_PROCESS_ON_UNCAUGHT_FAULT = "suspendProcessOnUncaughtFault"; //$NON-NLS-1$
   public static final String ATT_SUSPEND_PROCESS_ON_INVOKE_RECOVERY = "suspendProcessOnInvokeRecovery"; //$NON-NLS-1$
   public static final String ATT_TYPE_URI           = "typeURI"; //$NON-NLS-1$
   public static final String ATT_PORT_NAME          = "PortName"; //$NON-NLS-1$
   public static final String ATT_EFFECTIVE_DATE     = "effectiveDate"; //$NON-NLS-1$

   public static final String TAG_MYROLE             = "myRole"; //$NON-NLS-1$
   public static final String TAG_PARTNER_LINK       = "partnerLink"; //$NON-NLS-1$
   public static final String TAG_PARTNER_ROLE       = "partnerRole"; //$NON-NLS-1$
   public static final String TAG_ENDPOINT_REF       = "EndpointReference"; //$NON-NLS-1$
   public static final String TAG_ADDRESS            = "Address"; //$NON-NLS-1$
   public static final String TAG_SERVICE_NAME       = "ServiceName"; //$NON-NLS-1$
   public static final String TAG_VERSION            = "version"; //$NON-NLS-1$
   
   public static final String TAG_WSDL_REFERENCES    = "wsdlReferences"; //$NON-NLS-1$
   public static final String TAG_REFERENCES         = "references"; //$NON-NLS-1$
   public static final String TAG_WSDL               = "wsdl"; //$NON-NLS-1$
   public static final String TAG_SCHEMA             = "schema"; //$NON-NLS-1$
   public static final String TAG_OTHER              = "other"; //$NON-NLS-1$
}
