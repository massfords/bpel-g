// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/triggers/AeUniqueKeyConstraintTrigger.java,v 1.1 2007/10/26 19:33:27 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.exist.triggers;

import org.exist.collections.triggers.FilteringTrigger;
import org.exist.collections.triggers.TriggerException;
import org.exist.dom.DocumentImpl;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * An eXist database trigger that is fired when a document is stored.   This
 * trigger enforces unique key constraints on documents being inserted (or
 * updated) in the eXist database.
 */
public class AeUniqueKeyConstraintTrigger extends FilteringTrigger
{
   /** The current doc type being processed. */
   private String mCurrentDocType;
   
   /**
    * @see org.exist.collections.triggers.DocumentTrigger#prepare(int, org.exist.storage.DBBroker, org.exist.storage.txn.Txn, org.exist.xmldb.XmldbURI, org.exist.dom.DocumentImpl)
    */
   public void prepare(int aEvent, DBBroker aBroker, Txn aTransaction, XmldbURI aDocumentPath,
         DocumentImpl aExistingDocument) throws TriggerException
   {
      System.out.println("Prepare trigger!"); //$NON-NLS-1$
   }
   
   /**
    * @see org.exist.collections.triggers.FilteringTrigger#startDocument()
    */
   public void startDocument() throws SAXException
   {
      setCurrentDocType(null);
      super.startDocument();
   }
   
   /**
    * @see org.exist.collections.triggers.FilteringTrigger#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
   public void startElement(String aNamespaceURI, String aLocalName, String aQname, Attributes attributes) throws SAXException
   {
      if (isValidating() && getCurrentDocType() == null && !"AeResourceRoot".equals(aLocalName)) //$NON-NLS-1$
      {
         setCurrentDocType(aLocalName);
      }
      super.startElement(aNamespaceURI, aLocalName, aQname, attributes);
   }

   /**
    * @return Returns the currentDocType.
    */
   protected String getCurrentDocType()
   {
      return mCurrentDocType;
   }

   /**
    * @param aCurrentDocType the currentDocType to set
    */
   protected void setCurrentDocType(String aCurrentDocType)
   {
      mCurrentDocType = aCurrentDocType;
   }
}
