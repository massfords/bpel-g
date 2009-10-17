// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeDeployConstants.java,v 1.10 2007/12/07 15:58:09 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Constants related to process deployment
 */
public class AeDeployConstants
{
   /** binding for policy services */
   public static final String BIND_POLICY = "POLICY"; //$NON-NLS-1$
   /** binding for external services */
   public static final String BIND_EXTERNAL = "EXTERNAL"; //$NON-NLS-1$
   /** binding for rpc style services */
   public static final String BIND_RPC = "RPC"; //$NON-NLS-1$
   /** binding for rpc literal style services */
   public static final String BIND_RPC_LIT = "RPC-LIT"; //$NON-NLS-1$
   /** binding for msg style services */
   public static final String BIND_MSG = "MSG"; //$NON-NLS-1$
   
   /** Process disposition Maintain old version.*/
   public static final String MAINTAIN = "maintain"; //$NON-NLS-1$
   /** Process disposition Terminate. */
   public static final String TERMINATE = "terminate"; //$NON-NLS-1$
   /** Process disposition Migrate old version. */
   public static final String MIGRATE = "migrate"; //$NON-NLS-1$
   /** Process disposition Migrate and suspend old version. */
   public static final String MIGRATE_AND_SUSPEND = "migrateAndSuspend"; //$NON-NLS-1$
   
   /** Version element from pdd */
   public static final String VERSION_EL = "version"; //$NON-NLS-1$
   
   /** Version id attribute from pdd */
   public static final String VERSIONID_ATTR = "id"; //$NON-NLS-1$
   /** expiration date attribute from pdd */
   public static final String EXPDATE_ATTR = "expirationDate"; //$NON-NLS-1$
   /** expiration date attribute from pdd */
   public static final String PROCESS_RETENTION_DAYS_ATTR = "processRetentionDays"; //$NON-NLS-1$
   /** process group attribute from pdd */
   public static final String PROCESS_GROUP = "processGroup"; //$NON-NLS-1$
   /** effective date attribute from pdd */
   public static final String EFFDATE_ATTR = "effectiveDate"; //$NON-NLS-1$
   /** process disposition attribute from pdd */
   public static final String PROCESSDISP_ATTR = "runningProcessDisposition";  //$NON-NLS-1$
   /** process persistence type attribute from pdd */
   public static final String PERSISTENCE_TYPE_ATTR = "persistenceType"; //$NON-NLS-1$
   /** process transaction type attribute from pdd */
   public static final String TRANSACTION_TYPE_ATTR = "transactionType"; //$NON-NLS-1$

   /** Process persistence type: default. */
   public static final String PERSISTENCE_TYPE_DEFAULT = "default"; //$NON-NLS-1$
   /** Process persistence type: full. */
   public static final String PERSISTENCE_TYPE_FULL = "full"; //$NON-NLS-1$
   /** Process persistence type: none (never persist). */
   public static final String PERSISTENCE_TYPE_NONE = "none"; //$NON-NLS-1$

   /** Process suspend on uncaught fault or invoke recovery: default. */
   public static final String SUSPEND_TYPE_DEFAULT = "default"; //$NON-NLS-1$
   /** Process suspend on uncaught fault or invoke recovery: true. */
   public static final String SUSPEND_TYPE_TRUE = "true"; //$NON-NLS-1$
   /** Process suspend on uncaught fault or invoke recovery: false. */
   public static final String SUSPEND_TYPE_FALSE = "false"; //$NON-NLS-1$
   
   /** Process transaction type: bean. */
   public static final String TRANSACTION_TYPE_BEAN = "bean"; //$NON-NLS-1$
   /** Process transaction type: container. */
   public static final String TRANSACTION_TYPE_CONTAINER = "container"; //$NON-NLS-1$
   
   /** Exception management type: defer to engine setting. */
   public static final String EXCEPTION_MANAGEMENT_TYPE_ENGINE = "engine"; //$NON-NLS-1$
   /** Exception management type: suspend the process (override any engine setting). */
   public static final String EXCEPTION_MANAGEMENT_TYPE_SUSPEND = "suspend"; //$NON-NLS-1$
   /** Exception management type: normal behavior  (override any engine setting). */
   public static final String EXCEPTION_MANAGEMENT_TYPE_NORMAL = "normal"; //$NON-NLS-1$
   
   /** Invoke recovery type: defer to engine setting. */
   public static final String INVOKE_RECOVERY_TYPE_ENGINE = "engine"; //$NON-NLS-1$
   /** Invoke recovery type: suspend the process (override any engine setting). */
   public static final String INVOKE_RECOVERY_TYPE_SUSPEND = "suspend"; //$NON-NLS-1$
   /** Invoke recovery type: normal behavior  (override any engine setting). */
   public static final String INVOKE_RECOVERY_TYPE_NORMAL = "normal"; //$NON-NLS-1$

   /** Represents a pdd. Used in places such as file name extensions, namespace prefixes */
   public static final String PDD = "pdd"; //$NON-NLS-1$
   
   /** Supported Pdd namespaces (in the pdd editor) */
   public static final Collection PDD_SUPPORTED_NAMESPACES = new ArrayList();
   static
   {
      PDD_SUPPORTED_NAMESPACES.add("http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd"); //$NON-NLS-1$
   }
   
   /** Latest supported, and preferred namespace for the pdd schema */
   public static final String PDD_PREFERRED_NAMESPACE = "http://schemas.active-endpoints.com/pdd/2006/08/pdd.xsd"; //$NON-NLS-1$
}
