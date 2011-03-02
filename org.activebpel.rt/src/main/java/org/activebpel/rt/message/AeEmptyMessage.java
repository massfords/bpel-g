// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/message/AeEmptyMessage.java,v 1.12 2007/06/28 21:57:33 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.message;

import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeMessages;
import org.activebpel.rt.attachment.AeAttachmentContainer;
import org.activebpel.rt.attachment.IAeAttachmentContainer;

/**
 * Container for a message that doesn't contain any parts. 
 */
public class AeEmptyMessage implements IAeMessageData
{
   /** The type of message */
   private QName mType;
   
   /** place holder for optional attachments */
   private IAeAttachmentContainer mAttachmentContainer;
   
   /**
    * Constructor
    * @param aType
    */
   public AeEmptyMessage(QName aType)
   {
      mType = aType;
   }

   /**
    * @see org.activebpel.rt.message.IAeMessageData#isDirty()
    */
   public boolean isDirty()
   {
      return false;
   }

   /**
    * @see org.activebpel.rt.message.IAeMessageData#clearDirty()
    */
   public void clearDirty()
   {
   }

   /**
    * @see org.activebpel.rt.message.IAeMessageData#getMessageType()
    */
   public QName getMessageType()
   {
      return mType;
   }

   /**
    * @see org.activebpel.rt.message.IAeMessageData#getPartNames()
    */
   public Iterator getPartNames()
   {
      return Collections.EMPTY_SET.iterator();
   }

   /**
    * @see org.activebpel.rt.message.IAeMessageData#getData(java.lang.String)
    */
   public Object getData(String aPartName)
   {
      throw new UnsupportedOperationException(AeMessages.getString("AeEmptyMessage.ERROR_0")); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.message.IAeMessageData#setData(java.lang.String, java.lang.Object)
    */
   public void setData(String aPartName, Object aData)
   {
      throw new UnsupportedOperationException(AeMessages.getString("AeEmptyMessage.ERROR_0")); //$NON-NLS-1$
   }

   /**
    * @see java.lang.Object#clone()
    */
   public Object clone()
   {
      try
      {
         AeEmptyMessage copy = (AeEmptyMessage) super.clone();
         
         if (mAttachmentContainer != null)
         {
            copy.mAttachmentContainer = new AeAttachmentContainer(mAttachmentContainer);
         }

         return copy;
      }
      catch (CloneNotSupportedException e)
      {
         throw new InternalError("Unexpected error during clone: " + e.getLocalizedMessage()); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.message.IAeMessageData#getPartCount()
    */
   public int getPartCount()
   {
      return 0;
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
}