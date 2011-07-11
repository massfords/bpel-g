// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/IAeAttachmentDownloadHandler.java,v 1.2 2007/09/28 19:53:10 mford Exp $
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Defines the interface for handling an attachment for
 * {@link AeAttachmentDownloadServlet}.
 */
public interface IAeAttachmentDownloadHandler
{
   /**
    * Handles the attachment represented by the given attachment stream by
    * producing an appropriate HTTP response stream.
    *
    * @param aRequest
    * @param aResponse
    * @param aAttachmentContent
    * @param aMimeType
    * @param aFileName
    * @param aAttachmentSize attachment size in bytes or <code>-1</code> if the attachment size is unknown
    * @throws IOException
    */
   public void handleAttachment(HttpServletRequest aRequest,
      HttpServletResponse aResponse,
      InputStream aAttachmentContent,
      String aMimeType,
      String aFileName,
      int aAttachmentSize) throws IOException;
}
