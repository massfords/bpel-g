// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/message/AeMessageData.java,v 1.16 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.message;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.attachment.AeAttachmentContainer;
import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;

/**
 * Manages data interaction for a WSDL message.
 * Internal normalized message structure used by all engines. These messages are not publicly exposed.
 * For public message API see org.activebpel.wsio.IAeWebServiceMessageData
 */
public class AeMessageData implements IAeMessageData, Externalizable
{
   /** The variable message type for which we are storing data */
   private QName mMsgType;
   /** Holds on to the part data, the name of the part is its key. */
   private Map mPartData = new HashMap();
   /** Flag to track if the message data is dirty */
   private boolean mDirty;
   /** place holder for optional attachments */
   private IAeAttachmentContainer mAttachmentContainer;

   /**
    * C'tor for serialization.
    */
   public AeMessageData()
   {
   }
   
   /**
    * Constructor.
    *
    * @param aQName
    * @param aPartData
    */
   public AeMessageData( QName aQName, Map aPartData )
   {
      this( aQName );
      mPartData.putAll( aPartData );
   }

   /**
    * Constructor which takes the QName of the message as input.
    * @param aMsgType the qualified name of the message this data element represents.
    */
   public AeMessageData(QName aMsgType)
   {
      mMsgType = aMsgType;
   }

   /**
    * Returns the type of message this data is representing.
    */
   public QName getMessageType()
   {
      return mMsgType;
   }

   /**
    * Returns flag indicating if the message data is dirty.
    */
   public boolean isDirty()
   {
      // TODO (MF) only called by win32, perhaps those classes could use a subclass and remove this method?
      return mDirty;
   }

   /**
    * Clears the flag indicating that the data is dirty.
    */
   public void clearDirty()
   {
      mDirty = false;
   }

   /**
    * Allows inheriting classes ability to set dirty flag.
    * @param aDirty True for dirty and False if not dirty
    */
   protected void setDirty(boolean aDirty)
   {
      mDirty = aDirty;
   }

   /**
    * Returns an iterator over the part names for which we are storing data.
    */
   public Iterator getPartNames()
   {
      return mPartData.keySet().iterator();
   }

   /**
    * Returns the data associated with a passed part. Null if none.
    * @param aPartName The part name to get data for.
    * @return The data associated with the passed part name. Can be null.
    */
   public Object getData(String aPartName)
   {
      return mPartData.get(aPartName);
   }

   /**
    * Sets the data associated with a passed part. Data can be null.
    * @param aPartName The part to set the data for.
    * @param aData The data to which the part is set.
    */
   public void setData(String aPartName, Object aData)
   {
      mPartData.put(aPartName, aData);
      mDirty = true;
   }

   /**
    * @see java.lang.Object#clone()
    */
   public Object clone()
   {
      try
      {
         AeMessageData copy = (AeMessageData) super.clone();
         copy.mPartData = new HashMap(mPartData);
         
         // walk the map and deep clone any Nodes
         for (Iterator iter = copy.mPartData.entrySet().iterator(); iter.hasNext();)
         {
            Map.Entry entry = (Map.Entry) iter.next();
            if (entry.getValue() instanceof Document)
            {
               Document doc = (Document) entry.getValue();
               entry.setValue(AeXmlUtil.cloneElement(doc.getDocumentElement()).getOwnerDocument());
            }
         }
         
         // clone attachments
         if (hasAttachments())
            copy.setAttachmentContainer(new AeAttachmentContainer(getAttachmentContainer()));
         
         return copy;
      }
      catch(CloneNotSupportedException e)
      {
         throw new InternalError("unexpected error during clone:" + e.getLocalizedMessage()); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.message.IAeMessageData#getPartCount()
    */
   public int getPartCount()
   {
      return mPartData.size();
   }

   /**
    * @see org.activebpel.rt.message.IAeMessageData#hasAttachments()
    */
   public boolean hasAttachments()
   {
      return mAttachmentContainer != null && ! mAttachmentContainer.isEmpty();
   }
   
   /**
    * @see org.activebpel.rt.message.IAeMessageData#getAttachmentContainer()
    */
   public IAeAttachmentContainer getAttachmentContainer()
   {
      if (mAttachmentContainer == null)
         mAttachmentContainer = new AeAttachmentContainer();
      
      return mAttachmentContainer;
   }

   /**
    * @see org.activebpel.rt.message.IAeMessageData#setAttachmentContainer(org.activebpel.rt.attachment.IAeAttachmentContainer)
    */
   public void setAttachmentContainer(IAeAttachmentContainer aAttachmentContainer)
   {
      mAttachmentContainer = aAttachmentContainer;
   }

   /**
    * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
    */
   public void writeExternal(ObjectOutput aOut) throws IOException
   {
      aOut.writeObject(getMessageType());
      aOut.writeBoolean(isDirty());

      aOut.writeInt(mPartData.size());
      for (Iterator iter = mPartData.entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry) iter.next();
         aOut.writeObject(entry.getKey());
         if (entry.getValue() instanceof Document)
         {
            Document doc = (Document) entry.getValue();
            String serializedDoc = AeXmlUtil.serialize(doc.getDocumentElement());
            aOut.writeBoolean(true); // true - it is a complex (DOM) part
            aOut.writeObject(serializedDoc);
         }
         else
         {
            Object value = entry.getValue();
            aOut.writeBoolean(false); // false - simple object
            aOut.writeObject(value);
         }
      }
   }

   /**
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   public void readExternal(ObjectInput aIn) throws IOException, ClassNotFoundException
   {
      mMsgType = (QName) aIn.readObject();
      mDirty = aIn.readBoolean();

      mPartData = new HashMap();
      int numParts = aIn.readInt();
      for (int i = 0; i < numParts; i++)
      {
         String partName = (String) aIn.readObject();
         boolean isDom = aIn.readBoolean();
         Object value = aIn.readObject();
         if (isDom)
         {
            String serializedPartData = (String) value;
            value = parsePartData(serializedPartData);
         }
         mPartData.put(partName, value);
      }
   }

   /**
    * Parse the given part data.
    * 
    * @param aSerializedPartData
    */
   private Object parsePartData(String aSerializedPartData)
   {
      try
      {
         return new AeXMLParserBase(true, false).loadDocumentFromString(aSerializedPartData, null);
      }
      catch (AeException ex)
      {
         // Will not happen
         AeException.logError(ex);
      }
      return null;
   }
}