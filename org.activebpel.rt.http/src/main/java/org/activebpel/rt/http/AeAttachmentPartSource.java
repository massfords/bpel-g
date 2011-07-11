//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeAttachmentPartSource.java,v 1.2 2008/02/17 21:54:15 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.methods.multipart.PartSource;

/**
 * Simple Httpclient PartSource implementation to support attachments in Multipart http Posts
 */
public class AeAttachmentPartSource implements PartSource
{

   /** the part content */
   private InputStream mInputStream;

   /** the part length */
   private long mLength;

   /** the part  name */
   private String mAttachmentName;

   /**
    * Constructor
    * @param aAttachmentName
    * @param aInputStream
    * @param aLength
    */
   public AeAttachmentPartSource(String aAttachmentName, InputStream aInputStream, long aLength)
   {
      mInputStream = aInputStream;
      mLength = aLength;
      mAttachmentName = aAttachmentName;
   }

   /**
    * @see org.apache.commons.httpclient.methods.multipart.PartSource#createInputStream()
    */
   public InputStream createInputStream() throws IOException
   {
      return mInputStream;
   }

   /**
    * @see org.apache.commons.httpclient.methods.multipart.PartSource#getFileName()
    */
   public String getFileName()
   {
      return (mAttachmentName == null) ? "noname" : mAttachmentName; //$NON-NLS-1$
   }

   /**
    * @see org.apache.commons.httpclient.methods.multipart.PartSource#getLength()
    */
   public long getLength()
   {
      return mLength;
   }

}
