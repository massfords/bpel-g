//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/rdebug/server/AeAddAttachmentResponse.java,v 1.2 2008/02/17 21:38:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.rdebug.server;


/**
 * Java bean to hold the response of an add attachment request 
 */
public class AeAddAttachmentResponse
{
   private long mAttachmentId;

   private AeAttachmentAttributeList mAttachmentAttributes;
   
   
   /**
    * No-arg constructor
    */
   public AeAddAttachmentResponse()
   {     
   }
   
   /**
    * Gets the attachmentId value for this AeAddAttachmentResponse.
    * 
    * @return mAttachmentId
    */
   public long getAttachmentId() {
       return mAttachmentId;
   }

   /**
    * Sets the attachmentId value for this AeAddAttachmentResponse.
    * 
    * @param aAttachmentId
    */
   public void setAttachmentId(long aAttachmentId) {
      mAttachmentId = aAttachmentId;
   }

   /**
    * Gets the attachmentAttributes value for this AeAddAttachmentResponse.
    * 
    * @return attachmentAttributes 
    */
   public AeAttachmentAttributeList getAttachmentAttributes() {
       return mAttachmentAttributes;
   }

   /**
    * Sets the attachmentAttributes value for this AeAddAttachmentResponse.
    * 
    * @param aAttachmentAttributes 
    */
   public void setAttachmentAttributes(AeAttachmentAttributeList aAttachmentAttributes) {
       mAttachmentAttributes = aAttachmentAttributes;
   }


}
