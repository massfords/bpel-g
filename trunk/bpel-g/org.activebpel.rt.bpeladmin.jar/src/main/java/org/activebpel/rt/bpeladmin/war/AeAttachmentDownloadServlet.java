// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/AeAttachmentDownloadServlet.java,v 1.3 2007/09/28 19:53:10 mford Exp $
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeBlobInputStream;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUtil;

/**
 * Responsible for downloading the contents of an attachment and streaming it to a client.
 */
public class AeAttachmentDownloadServlet extends HttpServlet
{
   public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream"; //$NON-NLS-1$
   private static final String HANDLER_CLASS_PARAMETER = "handler.class"; //$NON-NLS-1$

   /**
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse)
    */
   protected void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException,
         IOException
   {
      doPost(aRequest, aResponse);
   }

   /**
    * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse)
    */
   protected void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse) throws ServletException,
         IOException
   {
      long attachmentItemId;
      String mimeType;
      String fileName;

      try
      {
         attachmentItemId = Long.parseLong(aRequest.getParameter("id")); //$NON-NLS-1$
         mimeType = aRequest.getParameter("type"); //$NON-NLS-1$
         fileName = aRequest.getParameter("file"); //$NON-NLS-1$
      }
      catch (Exception e)
      {
         AeException.logError(e);
         aResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, AeMessages.getString("AeAttachmentDownloadServlet.1")); //$NON-NLS-1$
         return;
      }

      // Multipart MIME types do not download properly in Firefox; if it's
      // multipart, then change the MIME type to force proper download.
      if (AeUtil.isNullOrEmpty(mimeType) || mimeType.startsWith("multipart/")) //$NON-NLS-1$
      {
         mimeType = DEFAULT_CONTENT_TYPE;
      }

      if (AeUtil.isNullOrEmpty(fileName))
      {
         fileName = "attachment_" + attachmentItemId + ".bin"; //$NON-NLS-1$ //$NON-NLS-2$
      }

      try
      {
         IAeAttachmentDownloadHandler handler = createAttachmentDownloadHandler();
         InputStream content = AeEngineFactory.getEngine().getAttachmentManager().deserialize(attachmentItemId);

         try
         {
            int size = -1;
            
            if (content instanceof AeBlobInputStream)
            {
               size = ((AeBlobInputStream) content).length();
            }

            handler.handleAttachment(aRequest, aResponse, content, mimeType, fileName, size);
         }
         finally
         {
            // Make sure we close the attachment stream in order to delete its
            // temporary file.
            AeCloser.close(content);
         }
      }
      catch (Exception e)
      {
         AeException.logError(e);
         aResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      }
   }

   /**
    * Constructs the attachment download handler.
    */
   protected IAeAttachmentDownloadHandler createAttachmentDownloadHandler() throws Exception
   {
      IAeAttachmentDownloadHandler handler;
      
      // getInitParameter() is implemented by GenericServlet, which is one of
      // our superclasses.
      String className = getInitParameter(HANDLER_CLASS_PARAMETER);

      if (AeUtil.isNullOrEmpty(className))
      {
         handler = new AeAttachmentDownloadHandler();
      }
      else
      {
         handler = (IAeAttachmentDownloadHandler) Class.forName(className).newInstance();
      }

      return handler;
   }
}
