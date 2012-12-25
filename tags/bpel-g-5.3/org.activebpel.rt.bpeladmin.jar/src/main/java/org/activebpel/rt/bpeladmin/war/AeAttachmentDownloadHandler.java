// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/AeAttachmentDownloadHandler.java,v 1.2 2007/09/28 19:53:10 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeFileUtil;
import org.activebpel.rt.util.AeUtil;

/**
 * Implements the default attachment download handler for
 * {@link AeAttachmentDownloadServlet}.
 */
public class AeAttachmentDownloadHandler implements IAeAttachmentDownloadHandler
{
   /**
    * Default constructor.
    */
   public AeAttachmentDownloadHandler()
   {
   }

   /**
    * Overrides method to copy the attachment contents to the HTTP response
    * stream.
    *
    * @see org.activebpel.rt.bpeladmin.war.IAeAttachmentDownloadHandler#handleAttachment(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.io.InputStream, java.lang.String, java.lang.String, int)
    */
   public void handleAttachment(HttpServletRequest aRequest, HttpServletResponse aResponse,
      InputStream aAttachmentContent, String aMimeType, String aFileName, int aAttachmentSize) throws IOException
   {
      aResponse.setContentType(AeUtil.isNullOrEmpty(aMimeType) ? AeAttachmentDownloadServlet.DEFAULT_CONTENT_TYPE : aMimeType);
      aResponse.setHeader("Content-disposition", "attachment;filename=" + aFileName); //$NON-NLS-1$ //$NON-NLS-2$

      if (aAttachmentSize > 0)
      {
         aResponse.setContentLength(aAttachmentSize);
      }

      OutputStream out = aResponse.getOutputStream();

      try
      {
         AeFileUtil.copy(aAttachmentContent, out);
         out.flush();
      }
      finally
      {
         AeCloser.close(out);
      }
   }
}
