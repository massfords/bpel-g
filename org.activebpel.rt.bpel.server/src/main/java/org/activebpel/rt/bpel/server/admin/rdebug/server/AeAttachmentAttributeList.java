//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/rdebug/server/AeAttachmentAttributeList.java,v 1.2 2008/02/17 21:38:47 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.admin.rdebug.server;

import java.io.Serializable;

/**
 * Wrapper for the list of attachment attributes sent by the designer during remote debugging.
 */
public class AeAttachmentAttributeList implements Serializable
{
   
   /** The attributes associated with the attachment. */
   private AeAttachmentAttribute[] mAttachmentAttribute;
   
   /**
    * No-arg constructor
    */
   public AeAttachmentAttributeList()
   {     
   }
   
   /**
    * Gets the attributeName value for this AeAttachmentAttributeList.
    * 
    * @return attributeName
    */
   public AeAttachmentAttribute[] getAttributeName() {
       return mAttachmentAttribute;
   }

   /**
    * Sets the attributeName value for this AesAttachmentAttributeList.
    * 
    * @param attributeName
    */
   public void setAttributeName(AeAttachmentAttribute[] attributeName) {
      mAttachmentAttribute = attributeName;
   }

   /**
    * Returns the indexed attributeName value of this AesAttachmentAttributeList.
    * @param i
    *
    */
   public AeAttachmentAttribute getAttributeName(int i) {
       return mAttachmentAttribute[i];
   }

   /**
    * Sets the indexed attributeName value for this AesAttachmentAttributeList.
    * @param i
    * @param aAttachmentAttribute
    */
   public void setAttributeName(int i, AeAttachmentAttribute aAttachmentAttribute) {
      mAttachmentAttribute[i] = aAttachmentAttribute;
   }
}
