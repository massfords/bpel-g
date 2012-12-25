//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpStringPart.java,v 1.3.4.1 2008/04/21 16:15:52 ppatruni Exp $
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
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.util.EncodingUtil;

/**
 * Implements a String parameter for a multipart post with textual content. The default content type being
 * <code>text/xml</code>
 */
public class AeHttpStringPart extends PartBase
{

   /** Default transfer encoding of string parameters */
   public static final String DEFAULT_TRANSFER_ENCODING = "8bit"; //$NON-NLS-1$

   /** Contents of this StringPart. */
   private byte[] mContent;

   /** The String value of this part. */
   private String mValue;

   /** part headers , always null for <code>multipart/form-data</code> */
   private Map mHeaders;

   private boolean mHasDisposition;

   /** The set of headers to be optionally included in the string part */
   private static final Set sIncludes = AeUtil.toSet(new String[] { AeMimeUtil.CONTENT_ID_ATTRIBUTE.toLowerCase(),
         AeMimeUtil.CONTENT_TYPE_ATTRIBUTE.toLowerCase(), AeMimeUtil.CONTENT_LOCATION_ATTRIBUTE.toLowerCase(),
         AeMimeUtil.CONTENT_LENGTH_ATTRIBUTE.toLowerCase() });

   /**
    * Constructor.
    * @param aName The name of the part
    * @param aValue the string to post
    * @throws AeException
    */
   public AeHttpStringPart(String aName, String aValue) throws AeException
   {
      // TODO (JB) HTTP why is contentType fixed to AeMimeUtil.XML_MIME??
      this(aName, aValue, AeMimeUtil.XML_MIME, AeUTF8Util.UTF8_ENCODING, DEFAULT_TRANSFER_ENCODING);
   }

   /**
    * Constructor
    * @param aName
    * @param aHeaders
    * @param aContentType
    * @param aValue
    * @throws AeException
    */
   public AeHttpStringPart(String aName, Map aHeaders, String aContentType, String aValue) throws AeException
   {
      // TODO (JB) HTTP create AeMimeUtil.getCharset(aContentType) that defaults to AeUTF8Util.UTF8_ENCODING
      this(aName, aValue, AeMimeUtil.getMime(aContentType), AeUTF8Util.UTF8_ENCODING, DEFAULT_TRANSFER_ENCODING);
      mHeaders = aHeaders;
   }

   /**
    * Constructor
    * @param aName
    * @param aValue
    * @param aContentType
    * @param aCharset
    * @param aTransferEncoding
    * @throws AeException
    */
   public AeHttpStringPart(String aName, String aValue, String aContentType, String aCharset, String aTransferEncoding) throws AeException
   {
      super(aName, aContentType, aCharset, aTransferEncoding);
      if ( aValue == null )
      {
         throw new AeException(AeMessages.getString("AeHttpStringPart.ERROR_IllegalArgument")); //$NON-NLS-1$
      }
      if ( aValue.indexOf(0) != -1 )
      {
         // See RFC 2048, 2.8. "8bit Data"
         throw new AeException(AeMessages.getString("AeHttpStringPart.ERROR_NullValue")); //$NON-NLS-1$
      }
      if ( !(AeMimeUtil.isTextual(aContentType)) )
      {
         throw new AeException(AeMessages.format("AeHttpStringPart.ERROR_InvalidContentType", aContentType)); //$NON-NLS-1$
      }
      mValue = aValue;
   }

   /**
    * Return the length of the data.
    * @return The length of the data.
    * @throws IOException If an IO problem occurs
    * @see org.apache.commons.httpclient.methods.multipart.Part#lengthOfData()
    */
   protected long lengthOfData() throws IOException
   {
      return getContent().length;
   }

   /**
    * <p>
    * Disposition for <code>multipart/form-data</code> is the default behavior, sub types that are not
    * <code>multipart/form-data</code> have override logic that supplements the disposition with relevant
    * headers.
    * </p>
    * NOTE:headers are passed for all multipart sub types except for <code>multipart/form-data</code>
    * @see org.apache.commons.httpclient.methods.multipart.Part#sendDispositionHeader(java.io.OutputStream)
    */
   protected void sendDispositionHeader(OutputStream out) throws IOException
   {
      mHasDisposition = true;
      if ( mHeaders == null )
         // use default Httpclient implementation
         super.sendDispositionHeader(out);
      else
      {
         String dispositionHeader = createHeadersAsDispositon();
         if ( AeUtil.notNullOrEmpty(dispositionHeader) )
            out.write(EncodingUtil.getAsciiBytes(dispositionHeader));
         else
            mHasDisposition = false;
      }
   }

   /**
    * NOTE: The super class version assumes there is always a disposition, so we work around that in order not
    * to introduce a superfluous CRLF which breaks the multipart parser
    * @see org.apache.commons.httpclient.methods.multipart.Part#sendContentTypeHeader(java.io.OutputStream)
    */
   protected void sendContentTypeHeader(OutputStream out) throws IOException
   {
      String contentType = getContentType();
      if ( contentType != null )
      {
         if ( mHasDisposition )
            out.write(CRLF_BYTES);
         out.write(CONTENT_TYPE_BYTES);
         out.write(EncodingUtil.getAsciiBytes(contentType));
         String charSet = getCharSet();
         if ( charSet != null )
         {
            out.write(CHARSET_BYTES);
            out.write(EncodingUtil.getAsciiBytes(charSet));
         }
      }
   }

   /**
    * @see org.apache.commons.httpclient.methods.multipart.Part#sendData(java.io.OutputStream)
    */
   protected void sendData(OutputStream aOut) throws IOException
   {
      aOut.write(getContent());
   }

   /**
    * Gets the content in bytes. Bytes are lazily created to allow the charset to be changed after the part is
    * created.
    * @return the content in bytes
    */
   private byte[] getContent()
   {
      if ( mContent == null )
      {
         mContent = EncodingUtil.getBytes(mValue, getCharSet());
      }
      return mContent;
   }

   private String createHeadersAsDispositon()
   {
      StringBuffer buff = new StringBuffer();
      boolean first = true;
      for (Iterator iterator = mHeaders.entrySet().iterator(); iterator.hasNext();)
      {
         Map.Entry header = (Map.Entry)iterator.next();
         String hName = (String)header.getKey();
         if ( sIncludes.contains(hName.toLowerCase()) )
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

}
