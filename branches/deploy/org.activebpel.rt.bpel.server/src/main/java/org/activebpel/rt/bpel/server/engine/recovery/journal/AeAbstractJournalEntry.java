// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/journal/AeAbstractJournalEntry.java,v 1.4 2008/04/03 21:55:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery.journal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.AeBusinessProcessPropertyIO;
import org.activebpel.rt.bpel.impl.IAeImplStateNames;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implements abstract base class for journal entries.
 */
public abstract class AeAbstractJournalEntry implements IAeJournalEntry, IAeImplStateNames
{
   /** The entry type. */
   private final int mEntryType;

   /** The location id of the activity corresponding to the journal entry. */
   private final int mLocationId;

   /** <code>true</code> if and only if the item has been deserialized. */
   private boolean mItemDeserialized;

   /** Serialized representation of the entry data; may be <code>null</code>. */
   private AeFastDocument mItemSerialization;

   /** <code>true</code> if and only if the entry has been serialized by {@link #internalSerialize(AeTypeMapping)}. */
   private boolean mItemSerialized;

   /** The journal entry id. */
   private long mJournalId;

   /** The entry's document from persistent storage; may be <code>null</code>. */
   private Document mStorageDocument;

   /**
    * Constructs journal entry to persist to storage.
    */
   protected AeAbstractJournalEntry(int aEntryType, int aLocationId)
   {
      mEntryType = aEntryType;
      mLocationId = aLocationId;
      mItemDeserialized = true;
   }

   /**
    * Constructs journal entry to restore from storage.
    */
   protected AeAbstractJournalEntry(int aEntryType, int aLocationId, long aJournalId, Document aStorageDocument)
   {
      mEntryType = aEntryType;
      mLocationId = aLocationId;
      mJournalId = aJournalId;
      mStorageDocument = aStorageDocument;
   }

   /**
    * Deserializes journal entry from its storage document.
    */
   protected void deserialize() throws AeBusinessProcessException
   {
      if (!mItemDeserialized)
      {
         internalDeserialize(getStorageDocument());
         mItemDeserialized = true;
      }
   }

   /**
    * Deserializes process properties from the given parent element.
    */
   protected Map deserializeProcessProperties(Element aParentElement)
   {
      NodeList nodes = aParentElement.getChildNodes();
      Map properties = new HashMap();

      for (int i = nodes.getLength(); --i >= 0; )
      {
         Node node = nodes.item(i);

         if ((node instanceof Element) && STATE_PROCESSPROPERTY.equals(node.getNodeName()))
         {
            AeBusinessProcessPropertyIO.extractBusinessProcessProperty((Element) node, properties);
         }
      }

      return properties;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#getEntryType()
    */
   public int getEntryType()
   {
      return mEntryType;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#getLocationId()
    */
   public int getLocationId()
   {
      return mLocationId;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#getJournalId()
    */
   public long getJournalId()
   {
      return mJournalId;
   }

   /**
    * Returns item's document from persistent storage.
    */
   protected Document getStorageDocument()
   {
      return mStorageDocument;
   }

   /**
    * Deserializes the entry data.
    */
   protected abstract void internalDeserialize(Document aStorageDocument) throws AeBusinessProcessException;

   /**
    * Serializes the entry data.
    */
   protected abstract AeFastDocument internalSerialize(AeTypeMapping aTypeMapping) throws AeBusinessProcessException;

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#serialize(org.activebpel.rt.xml.schema.AeTypeMapping)
    */
   public AeFastDocument serialize(AeTypeMapping aTypeMapping) throws AeBusinessProcessException
   {
      if (!mItemSerialized)
      {
         mItemSerialization = internalSerialize(aTypeMapping);
         mItemSerialized = true;
      }

      return mItemSerialization;
   }

   /**
    * Serializes process properties to the given parent element.
    */
   protected void serializeProcessProperties(AeFastElement aParentElement, Map aProcessProperties)
   {
      if (aProcessProperties != null)
      {
         for (Iterator i = aProcessProperties.entrySet().iterator(); i.hasNext(); )
         {
            Map.Entry entry = (Map.Entry) i.next();
            String name = (String) entry.getKey();
            String value = AeUtil.getSafeString((String) entry.getValue());

            AeFastElement element = AeBusinessProcessPropertyIO.getBusinessProcessPropertyElement(name, value);
            aParentElement.appendChild(element);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.recovery.journal.IAeJournalEntry#canDispatch(int)
    */
   public boolean canDispatch(int aProcessState)
   {
      return aProcessState != IAeBusinessProcess.PROCESS_COMPLETE && aProcessState != IAeBusinessProcess.PROCESS_FAULTED;
   }
}
