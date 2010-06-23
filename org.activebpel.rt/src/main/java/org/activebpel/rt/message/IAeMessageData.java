// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/message/IAeMessageData.java,v 1.8 2007/06/28 21:57:33 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.message;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.attachment.IAeAttachmentContainer;

/**
 * Interface describing the message data associated with a variable of type message.
 */
public interface IAeMessageData extends Cloneable
{
   /**
    * Returns flag indicating if the message data is dirty.
    */
   public boolean isDirty();

   /**
    * Clears the flag indicating that the data is dirty.
    */
   public void clearDirty();

   /**
    * Returns the type of message this data is representing. 
    */
   public QName getMessageType();

   /**
    * Returns an iterator over the part names for which we are storing data.
    */
   public Iterator getPartNames();

   /**
    * Returns the data associated with a passed part. Null if none.
    * @param aPartName The part name to get data for.
    * @return The data associated with the passed part name. Can be null.
    */
   public Object getData(String aPartName);
    
   /**
    * Sets the data associated with a passed part. Data can be null.
    * @param aPartName The part to set the data for.
    * @param aData The data to which the part is set.
    */
   public void setData(String aPartName, Object aData);
   
   /**
    * Makes a deep copy of the part data 
    */
   public Object clone();

   /**
    * Returns the number of parts in this message data
    */
   public int getPartCount();
   
   /**
    * Returns true if there are attachments associated with this MessageData object.
    */
   public boolean hasAttachments();
   
   /**
    * Returns an attachment container associated with the message, if any.
    * @return the  attachment container
    * @see org.activebpel.rt.attachment#IAeAttachmentContainer
    */
   public IAeAttachmentContainer getAttachmentContainer();
   
   /**
    * associates an optional attachment container to the message.
    * @param aAttachmentContainer
    */
   public void setAttachmentContainer(IAeAttachmentContainer aAttachmentContainer);
}
