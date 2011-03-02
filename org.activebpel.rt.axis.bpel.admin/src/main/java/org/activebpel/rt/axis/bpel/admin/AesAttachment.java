//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel.admin/src/org/activebpel/rt/axis/bpel/admin/AesAttachment.java,v 1.3 2008/02/17 21:30:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.admin;

import java.io.File;
import java.util.Map;

/**
 * Wrapper for an attachment added to a process variable on a remote engine
 */
public class AesAttachment
{
   /** Attachment header attributes */
   private Map mAttributes;

   /** File with attachment content */
   private File mSource;

   /**
    * Constructor
    * @param aAttachmentAttributes
    * @param aAttachmentSource
    */
   public AesAttachment(Map aAttachmentAttributes, File aAttachmentSource)
   {
      mAttributes = aAttachmentAttributes;
      mSource = aAttachmentSource;
   }

   /** returns the file with the attachment contents */
   public File getSource()
   {
      return mSource;
   }

   /** return attachment attribute Map */
   public Map getAttributes()
   {
      return mAttributes;
   }
}
