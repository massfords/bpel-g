//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/handlers/AeJournalEntriesResponseHandler.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process.handlers;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.recovery.journal.AeJournalEntryFactory;
import org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntryFactory;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.journal.IAeJournalElements;
import org.activebpel.rt.xmldb.AeMessages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A response handler that returns a List of IAeJournalEntry objects.
 */
public class AeJournalEntriesResponseHandler extends AeXMLDBListResponseHandler
{
   /**
    * Overrides method to return an instance of IAeJournalEntry.
    * 
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement) throws AeXMLDBException
   {
      IAeJournalEntryFactory factory = AeJournalEntryFactory.getInstance();

      long journalId = getLongFromElement(aElement, IAeJournalElements.JOURNAL_ID).longValue();
      int entryType = getIntFromElement(aElement, IAeJournalElements.ENTRY_TYPE).intValue();
      int locationId = getIntFromElement(aElement, IAeJournalElements.LOCATION_PATH_ID).intValue();
      Document document = getDocumentFromElement(aElement, IAeJournalElements.ENTRY_DOCUMENT);

      try
      {
         return factory.newJournalEntry(entryType, locationId, journalId, document); 
      }
      catch (AeException e)
      {
         throw new AeXMLDBException(AeMessages.getString("AeJournalEntriesResponseHandler.ERROR_CREATING_JOURNAL_ENTRY"), e); //$NON-NLS-1$
      }
   }

}
