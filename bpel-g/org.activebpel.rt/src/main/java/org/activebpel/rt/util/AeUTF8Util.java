//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeUTF8Util.java,v 1.2 2007/10/23 12:28:29 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.activebpel.rt.AeException;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Document;

/**
 * Utility class for supporting UTF-8 encoding.
 */
public class AeUTF8Util
{
   /** UTF-8 encoding string. */
   public static final String UTF8_ENCODING = "UTF-8"; //$NON-NLS-1$

   /**
    * Convert <code>Document</code> to <code>InputStream</code> backed by a
    * UTF-8 encoded byte array.
    * @param aDoc
    * @throws AeException
    */
   public static InputStream getInputStream( Document aDoc )
   throws AeException
   {
      String docString = AeXMLParserBase.documentToString( aDoc, true );
      return getInputStream( docString );
   }

   /**
    * Convert <code>String</code> to <code>InputStream</code> backed by a
    * UTF-8 encoded byte array.
    * @param aString
    * @throws AeException
    */
   public static InputStream getInputStream( String aString )
   throws AeException
   {
      return new ByteArrayInputStream( getBytes( aString ) );
   }

   /**
    * Convert <code>Document</code> to byte[] using the UTF-8 charset.
    * @param aDoc
    * @throws AeException
    */
   public static byte[] getBytes( Document aDoc ) throws AeException
   {
      return getBytes( AeXMLParserBase.documentToString(aDoc, true) );
   }

   /**
    * Encodes this <tt>String</tt> into a sequence of bytes using the
    * UTF-8 charset, returning the result as a new byte array.
    * @param aString
    * @throws AeException
    */
   public static byte[] getBytes( String aString ) throws AeException
   {
      try
      {
         return aString.getBytes( getUTF8Encoding() );
      }
      catch( UnsupportedEncodingException io )
      {
         throw new AeException( io );
      }
   }

   /**
     * Constructs a new String by decoding the specified subarray of
     * bytes using the UTF-8 charset.
    * @param aBytes
    * @param aOffSet
    * @param aLength
    * @throws AeException
    */
   public static String newString( byte[] aBytes, int aOffSet, int aLength  )
   throws AeException
   {
      try
      {
         return new String( aBytes, aOffSet, aLength, getUTF8Encoding() );
      }
      catch( UnsupportedEncodingException io )
      {
         throw new AeException( io );
      }
   }

   /**
    * Translates a string into <code>application/x-www-form-urlencoded</code>
    * format using the UTF-8 encoding scheme. This method uses the
    * @param aString
    * @throws UnsupportedEncodingException
    */
   public static String urlEncode( String aString ) throws UnsupportedEncodingException
   {
      return URLEncoder.encode( aString, getUTF8Encoding() );
   }

   /**
    * Decodes a <code>application/x-www-form-urlencoded</code> string using the UTF-8
    * encoding scheme.
    * @param aString
    * @throws UnsupportedEncodingException
    */
   public static String urlDecode( String aString ) throws UnsupportedEncodingException
   {
      return URLDecoder.decode( aString, getUTF8Encoding() );
   }

   /**
    * @return Return the UTF-8 encoding string.
    */
   private static String getUTF8Encoding()
   {
      return UTF8_ENCODING;
   }
}
