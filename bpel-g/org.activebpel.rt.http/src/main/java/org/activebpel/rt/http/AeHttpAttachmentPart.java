//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpAttachmentPart.java,v 1.2 2008/02/14 22:33:52 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.IAeWebServiceAttachment;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.util.EncodingUtil;

/**
 * 
 */
public class AeHttpAttachmentPart extends PartBase
{

   /** Default transfer encoding of file attachments. */
   public static final String BINARY_TRANSFER_ENCODING = "binary"; //$NON-NLS-1$

   private Map mHeaders;

   private InputStream mContent;

   private long mLengthOfData = 0;

   /**
    * Constructor
    * @param aWsioAttachment
    * @throws AeException
    */
   public AeHttpAttachmentPart(String aName, IAeWebServiceAttachment aWsioAttachment) throws AeException
   {

      super(aName, (String)aWsioAttachment.getMimeType(), parseCharset((String)aWsioAttachment.getMimeType()), BINARY_TRANSFER_ENCODING);
      mLengthOfData = AeUtil.getBigNumeric(((String)aWsioAttachment.getMimeHeaders().get(AeMimeUtil.AE_SIZE_ATTRIBUTE)));
      mHeaders = aWsioAttachment.getMimeHeaders();
      mContent = aWsioAttachment.getContent();

   }

   /**
    * @see org.apache.commons.httpclient.methods.multipart.Part#lengthOfData()
    */
   protected long lengthOfData() throws IOException
   {
      return mLengthOfData;
      // return mSource.getLength();
   }

   /**
    * @see org.apache.commons.httpclient.methods.multipart.Part#sendData(java.io.OutputStream)
    */
   protected void sendData(OutputStream aOut) throws IOException
   {
      if ( lengthOfData() == 0 )
      {

         // this attachment contains no data, so there is nothing to send.
         // we don't want to create a zero length buffer as this will
         // cause an infinite loop when reading.
         return;
      }

      byte[] tmp = new byte[4096];
      InputStream instream = mContent;
      try
      {
         int len;
         while ((len = instream.read(tmp)) >= 0)
         {
            aOut.write(tmp, 0, len);
         }
      }
      finally
      {
         AeCloser.close(instream);
      }
   }

   /**
    * Disposition for an attachment is used to write out the attachment headers excluding the Content-Type
    * header.
    * @see org.apache.commons.httpclient.methods.multipart.Part#sendDispositionHeader(java.io.OutputStream)
    */
   protected void sendDispositionHeader(OutputStream out) throws IOException
   {
      String dispositionHeader = createHeadersAsDisposition();
      if ( AeUtil.notNullOrEmpty(dispositionHeader) )
         out.write(EncodingUtil.getAsciiBytes(dispositionHeader));
   }

   /**
    * @return the attachment headers
    */
   private Map getHeaders()
   {
      if ( mHeaders == null )
         mHeaders = new LinkedHashMap();
      return mHeaders;
   }

   /**
    * Create headers as disposition, this is a bit tricky and in effect, extends the HttpClient multipart
    * mechanism.
    * @return the headers as a disposition header.
    */
   private String createHeadersAsDisposition()
   {
      StringBuffer buff = new StringBuffer();
      boolean first = true;
      for (Iterator iterator = getHeaders().entrySet().iterator(); iterator.hasNext();)
      {
         Map.Entry header = (Map.Entry)iterator.next();
         String hName = (String)header.getKey();
         if ( !hName.equalsIgnoreCase(AeMimeUtil.CONTENT_TYPE_ATTRIBUTE) )
         {
            if ( first )
               first = false;
            else
               buff.append(CRLF);
            buff.append(hName).append(": ").append((String)header.getValue()); //$NON-NLS-1$/
         }
      }
      return buff.toString();
   }

   /**
    * Convenience method to extract the Charset=... from a content type header. strips enclosing quotes, for
    * example <code>charset="utf-8"</code> and <code>charset=utf-8</code> both yield <code>utf-8</code>
    * @param aContentType the content type line without the key <code>Content_Type:</code>
    * @return the charset string example: utf-8 or <code>null</code> when there is no charset set in the
    *         passed content type line
    */
   protected static String parseCharset(String aContentType)
   {
      if ( AeUtil.isNullOrEmpty(aContentType) )
         return null;

      StringTokenizer outer = new StringTokenizer(aContentType, ";", false); //$NON-NLS-1$
      StringTokenizer inner = null;
      String element = null;
      String charset = null;
      int startIndex = 0;
      int stopIndex = 0;
      while (outer.hasMoreTokens())
      {
         element = outer.nextToken();
         inner = new StringTokenizer(element, "=", false); //$NON-NLS-1$
         if ( inner.countTokens() == 2 )
         {
            if ( inner.nextToken().trim().equalsIgnoreCase("charset") ) //$NON-NLS-1$
            {
               charset = inner.nextToken();
               // strip quotes
               if ( charset.startsWith(QUOTE) )
               {
                  startIndex = 1;
                  stopIndex = (charset.length() > 0) ? charset.length() - 1 : 0;
               }
               else
               {
                  startIndex = 0;
                  stopIndex = charset.length();
               }

               if ( startIndex != 0 || stopIndex != charset.length() )
               {
                  charset = charset.substring(startIndex, stopIndex);
               }
               return charset;
            }
         }
      }
      return null;
   }

}
