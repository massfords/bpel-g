//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/admin/rdebug/server/AeAttachmentAttribute.java,v 1.2 2008/02/17 21:38:47 mford Exp $
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
 * JavaBean for holding some data for a single attachment attribute definition.
 */
public class AeAttachmentAttribute implements Serializable
{

   /** name of the attachment attribute */
   private String mAttributeName;

   /** value of the attachment attribute */
   private String mAttributeValue;

   /**
    * 
    * Constructor
    */
   public AeAttachmentAttribute() {
   }

   /**
    * Gets the attributeName value for this AeAttachmentAttribute.
    * 
    * @return attributeName
    */
   public String getAttributeName() {
       return mAttributeName;
   }


   /**
    * Sets the attributeName value for this AeAttachmentAttribute.
    * 
    * @param aAttributeName
    */
   public void setAttributeName(String aAttributeName) {
       mAttributeName = aAttributeName;
   }


   /**
    * Gets the attributeValue value for this AeAttachmentAttribute.
    * 
    * @return attributeValue
    */
   public String getAttributeValue() {
       return mAttributeValue;
   }


   /**
    * Sets the attributeValue value for this AeAttachmentAttribute.
    * 
    * @param aAttributeValue
    */
   public void setAttributeValue(String aAttributeValue) {
      mAttributeValue = aAttributeValue;
   }

}
