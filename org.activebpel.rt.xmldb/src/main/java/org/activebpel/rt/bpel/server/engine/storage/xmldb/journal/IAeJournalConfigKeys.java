//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/journal/IAeJournalConfigKeys.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.journal;

/**
 * An interface that simply holds some static constants which are the names of keys in the XMLDB config
 * object.
 */
public interface IAeJournalConfigKeys
{
   public static final String INSERT_JOURNAL_ENTRY    = "InsertJournalEntry"; //$NON-NLS-1$
   public static final String DELETE_JOURNAL_ENTRY    = "DeleteJournalEntry"; //$NON-NLS-1$
}
