// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/attachments/IAeAttachmentElements.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
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
 * The names of the xmldb attachment table elements.
 */
public interface IAeAttachmentElements
{
   public static final String PROCESS_ID = "ProcessID"; //$NON-NLS-1$
   public static final String ATTACHMENT_GROUP_ID = "AttachmentGroupID"; //$NON-NLS-1$
   public static final String ATTACHMENT_HEADER = "AttachmentHeader"; //$NON-NLS-1$
   public static final String ATTACHMENT_CONTENT_ID = "AttachmentContentID"; //$NON-NLS-1$
}
