// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/attachments/IAeAttachmentConfigKeys.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb.attachments;

/**
 * Contains xmldb config keys for looking up xquery statements.
 */
public interface IAeAttachmentConfigKeys
{
   public static final String INSERT_ATTACHMENT_GROUP = "InsertAttachmentGroup"; //$NON-NLS-1$
   public static final String ATTACH_PROCESS = "AttachProcess"; //$NON-NLS-1$
   public static final String INSERT_ATTACHMENT = "InsertAttachment"; //$NON-NLS-1$
   public static final String QUERY_ATTACHMENT_CONTENT_ID = "QueryAttachmentContentId"; //$NON-NLS-1$
   public static final String QUERY_ATTACHMENT_HEADERS = "QueryAttachmentHeaders"; //$NON-NLS-1$
   public static final String CLEANUP_ATTACHMENTS = "CleanupAttachments"; //$NON-NLS-1$
}
