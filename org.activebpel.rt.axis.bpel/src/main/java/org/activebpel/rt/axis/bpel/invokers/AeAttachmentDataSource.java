// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.axis.bpel/src/org/activebpel/rt/axis/bpel/invokers/AeAttachmentDataSource.java,v 1.2 2007/05/24 00:39:50 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.axis.bpel.invokers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.activebpel.wsio.IAeWebServiceAttachment;

/**
 * Provides a datasource for WSIO attachments 
 * @see javax.activation.DataHandler
 */
public class AeAttachmentDataSource implements DataSource
{
   /** WSIO attachments being sourced */
   private IAeWebServiceAttachment mAeWebServiceAttachmentData;
  
   /**
    * Constructor 
    * @param aAeWebServiceAttachmentData WSIO attachments
    */
   public AeAttachmentDataSource(IAeWebServiceAttachment aAeWebServiceAttachmentData)
   {
      mAeWebServiceAttachmentData = aAeWebServiceAttachmentData;
   }

   /**
    * @see javax.activation.DataSource#getContentType()
    */
   public String getContentType()
   {
      return mAeWebServiceAttachmentData.getMimeType();
   }

   /**
    * @see javax.activation.DataSource#getInputStream()
    */
   public InputStream getInputStream() throws IOException
   {
      return mAeWebServiceAttachmentData.getContent();
   }

   /**
    * @see javax.activation.DataSource#getName()
    */
   public String getName()
   {
      return mAeWebServiceAttachmentData.getContentId();
   }

   /**
    * Overrides method to return <code>null</code>.
    * 
    * @see javax.activation.DataSource#getOutputStream()
    */
   public OutputStream getOutputStream() throws IOException
   {
      return null;
   }
}
