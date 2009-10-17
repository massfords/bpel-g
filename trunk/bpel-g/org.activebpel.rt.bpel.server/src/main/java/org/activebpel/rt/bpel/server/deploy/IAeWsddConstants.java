// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/IAeWsddConstants.java,v 1.5 2006/06/26 18:28:21 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.IAeConstants;

/**
 * Some constants for creating a wsdd deployment document.
 */
public interface IAeWsddConstants extends IAeConstants
{
   
   
   /** providers namespace */    
   public static final String PROVIDER_NAMESPACE_URI = "http://xml.apache.org/axis/wsdd/providers/java"; //$NON-NLS-1$

   public static final String NAME_RPC_BINDING     = "BPEL-RPC"; //$NON-NLS-1$
   public static final String NAME_RPC_LIT_BINDING = "BPEL-RPC-LIT"; //$NON-NLS-1$
   public static final String NAME_MSG_BINDING     = "BPEL-MSG"; //$NON-NLS-1$
   public static final String NAME_POLICY_BINDING     = "BPEL-POLICY"; //$NON-NLS-1$   
   
   /** wsdd namespace */
   public static final String WSDD_NAMESPACE_URI = 
      "http://xml.apache.org/axis/wsdd/"; //$NON-NLS-1$
   
   // tags for generating wsdd xml   
   public static final String TAG_PROCESS_NS = "processNamespace"; //$NON-NLS-1$
   public static final String TAG_PROCESS_NAME = "processName";  //$NON-NLS-1$
   public static final String TAG_PARTNER_LINK = "partnerLink"; //$NON-NLS-1$
   public static final String TAG_PARTNER_LINK_ID = "partnerLinkId"; //$NON-NLS-1$
   public static final String TAG_ALLOWED_ROLES = "allowedRoles";  //$NON-NLS-1$
   public static final String TAG_DEPLOYMENT = "deployment"; //$NON-NLS-1$
   public static final String TAG_RPC_BINDING = "proc:" + NAME_RPC_BINDING; //$NON-NLS-1$
   public static final String TAG_RPC_LIT_BINDING = "proc:" + NAME_RPC_LIT_BINDING; //$NON-NLS-1$
   public static final String TAG_MSG_BINDING = "proc:" + NAME_MSG_BINDING; //$NON-NLS-1$
   public static final String TAG_POLICY_BINDING = "proc:" + NAME_POLICY_BINDING; //$NON-NLS-1$   
   public static final String TAG_SERVICE = "service"; //$NON-NLS-1$
   public static final String TAG_PARAMETER = "parameter"; //$NON-NLS-1$
   public static final String TAG_NAME = "name"; //$NON-NLS-1$
   public static final String TAG_VALUE = "value"; //$NON-NLS-1$
   public static final String TAG_PROVIDER = "provider"; //$NON-NLS-1$
   public static final String TAG_REQUEST_FLOW = "requestFlow"; //$NON-NLS-1$
   public static final String TAG_RESPONSE_FLOW = "responseFlow"; //$NON-NLS-1$
   public static final String TAG_HANDLER = "handler"; //$NON-NLS-1$
   public static final String TAG_GLOBAL_CONFIG = "globalConfiguration"; //$NON-NLS-1$
   
   
}
