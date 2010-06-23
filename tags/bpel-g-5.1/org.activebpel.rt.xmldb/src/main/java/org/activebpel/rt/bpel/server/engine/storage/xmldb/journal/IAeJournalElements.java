//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/journal/IAeJournalElements.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
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
 * Constants that define the Journal element names.
 */
public interface IAeJournalElements
{
   public static final String JOURNAL_ID          = "JournalID"; //$NON-NLS-1$
   public static final String PROCESS_ID          = "ProcessID"; //$NON-NLS-1$
   public static final String LOCATION_PATH_ID    = "LocationPathID"; //$NON-NLS-1$
   public static final String ENTRY_TYPE          = "EntryType"; //$NON-NLS-1$
   public static final String ENTRY_DOCUMENT      = "EntryDocument"; //$NON-NLS-1$
}
