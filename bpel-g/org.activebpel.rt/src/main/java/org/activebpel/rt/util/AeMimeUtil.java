//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeMimeUtil.java,v 1.19.2.1 2008/04/21 16:05:13 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activebpel.rt.AeException;
import org.xml.sax.InputSource;

/**
 * Utility methods for working with mime attributes
 */
public class AeMimeUtil
{
   /** key content type attribute */
   public static final String CONTENT_TYPE_ATTRIBUTE = "Content-Type"; //$NON-NLS-1$

   /** content location type attribute */
   public static final String CONTENT_LOCATION_ATTRIBUTE = "Content-Location"; //$NON-NLS-1$

   /** content id attribute */
   public static final String CONTENT_ID_ATTRIBUTE = "Content-Id"; //$NON-NLS-1$

   /** content id attribute */
   public static final String USER_AGENT_ATTRIBUTE = "User-Agent"; //$NON-NLS-1$

   /** content length attribute */
   public static final String CONTENT_LENGTH_ATTRIBUTE = "Content-Length"; //$NON-NLS-1$
   
   /** content length attribute */
   public static final String CONTENT_DISPOSITION_ATTRIBUTE = "Content-Disposition"; //$NON-NLS-1$
   
   /** redirect location attribute */
   public static final String REDIRECT_LOCATION_ATTRIBUTE = "Location"; //$NON-NLS-1$

   /** content encoding attribute */
   public static final String CONTENT_ENCODING_ATTRIBUTE = "Content-Transfer-Encoding"; //$NON-NLS-1$
 
   /** Ae extension length attribute */
   public static final String AE_SIZE_ATTRIBUTE = "X-Size"; //$NON-NLS-1$

   /** Default Content-Id for attachments added during remote debug */
   public static final String AE_DEFAULT_REMOTE_CONTENT_ID = "remote-debug-1"; //$NON-NLS-1$

   /** Default Content-Id for attachments added from the admin console */
   public static final String AE_DEFAULT_ADMIN_CONTENT_ID = "bpel-admin-1"; //$NON-NLS-1$

   /**
    * Default Content-Id for attachments added from an internal process expression function, the process id is
    * appended by the engine for identification.
    */
   public static final String AE_DEFAULT_INLINE_CONTENT_ID = "bpel-inline-"; //$NON-NLS-1$

   /** default download file name */
   public static final String DEFAULT_FILE_NAME = "download"; //$NON-NLS-1$

   /** default download file name Extension */
   public static final String DEFAULT_FILE_NAME_EXT = "bin"; //$NON-NLS-1$

   /** Standard mime type for xml with UTF-8 encoding */
   public static final String XML_UTF8_MIME = "text/xml; charset=UTF-8"; //$NON-NLS-1$

   /** Standard mime type for xml */
   public static final String XML_MIME = "text/xml"; //$NON-NLS-1$

   /** Standard mime type for html */
   public static final String HTML_MIME = "text/html"; //$NON-NLS-1$

   /** Standard mime type for plain text */
   public static final String PLAIN_TEXT_MIME = "text/plain"; //$NON-NLS-1$

   /** standard mime type for http POST with form properties */
   public static final String FORM_ENCODED_MIME = "application/x-www-form-urlencoded"; //$NON-NLS-1$

   /** default mime type for binary content */
   public static final String DEFAULT_BINARY_MIME = "application/octet-stream"; //$NON-NLS-1$

   /** default mime type for Atom syndicate content */
   public static final String ATOM_XML_MIME = "application/atom+xml"; //$NON-NLS-1$

   /** multipart mime identifier */
   public static final String MULTIPART_MIME_ID = "multipart/"; //$NON-NLS-1$

   /** default mime type for multipart, mixed content */
   public static final String MULTIPART_MIXED_MIME = "multipart/mixed"; //$NON-NLS-1$

   /** mime type for multipart form content */
   public static final String MULTIPART_FORM_MIME = "multipart/form-data"; //$NON-NLS-1$

   /** mime type for multipart related content */
   public static final String MULTIPART_RELATED_MIME = "multipart/related"; //$NON-NLS-1$

   /** mime type for multipart digest content */
   public static final String MULTIPART_DIGEST_MIME = "multipart/digest"; //$NON-NLS-1$

   /** mime type for multipart alternative content */
   public static final String MULTIPART_ALTERNATIVE_MIME = "multipart/alternative"; //$NON-NLS-1$

   /** mime type for multipart parallel content */
   public static final String MULTIPART_PARALLEL_MIME = "multipart/parallel"; //$NON-NLS-1$

   /** Map of default content-type to file extensions. */
   private static Map sMimeToExtension;
   static
   {
      sMimeToExtension = new HashMap();
      sMimeToExtension.put("video/x-msvideo", "avi"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/octet-stream", "bin"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("image/bmp", "bmp"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/x-msdownload", "dll"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/msword", "doc"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("image/gif", "gif"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/x-gzip", "gz"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("text/html", "html");//$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("image/x-icon", "ico"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/java", "java");//$NON-NLS-1$ //$NON-NLS-2$ 
      sMimeToExtension.put("image/jpg", "jpg"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("image/jpeg", "jpeg");//$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/javascript", "js"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/jsp", "jsp"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("video/x-la-asf", "lsf"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/x-msaccess", "mdb"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("audio/mid", "mid"); //$NON-NLS-1$ //$NON-NLS-2$ 
      sMimeToExtension.put("application/x-msmoney", "mny"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("video/quicktime", "mov"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("video/mpeg", "mp2"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("audio/mpeg", "mp3"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("video/mpeg", "mpg"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/vnd.ms-project", "mpp"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/pdf", "pdf"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("image/png", "png"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/vnd.ms-powerpoint", "ppt"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/postscript", "ps"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("audio/x-pn-realaudio", "ram"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/rtf", "rtf"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("image/tiff", "tiff");//$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("text/plain", "txt"); //$NON-NLS-1$ //$NON-NLS-2$   
      sMimeToExtension.put("text/x-vcard", "vcf"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("audio/wav", "wav"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/x-msmetafile", "wmf"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/vnd.ms-excel", "xls"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("text/xml", "xml"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/x-compress", "z"); //$NON-NLS-1$ //$NON-NLS-2$
      sMimeToExtension.put("application/zip", "zip"); //$NON-NLS-1$ //$NON-NLS-2$

   }

   /** The extension map of well known Mime types */
   protected static Map sExtensionToMime;

   static
   {
      sExtensionToMime = new HashMap();

      sExtensionToMime.put("bas", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("java", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("ini", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("sql", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("unl", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("c", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("properties", "text/plain"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("bpel", "text/xml"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("vbpel", "text/xml"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("htm", "text/html"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("dot", "application/msword"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("xla", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("xlc", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("xlm", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("xlt", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("xlw", "application/vnd.ms-excel"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("class", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("exe", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("lha", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("lzh", "application/octet-stream"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("pps", "application/vnd.ms-powerpoint"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("eps", "application/postscript"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("ai", "application/postscript"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("jpe", "image/jpeg"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("mpa", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("mpe", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("mpeg", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("mpg", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("mpv2", "video/mpeg"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("lsx", "video/x-la-asf"); //$NON-NLS-1$ //$NON-NLS-2$
      sExtensionToMime.put("rmi", "audio/mid"); //$NON-NLS-1$ //$NON-NLS-2$

      for (Iterator iter = sMimeToExtension.entrySet().iterator(); iter.hasNext();)
      {
         Map.Entry entry = (Map.Entry)iter.next();
         sExtensionToMime.put(entry.getValue(), entry.getKey());
      }
   }

   /**
    * Regular expression to match a valid Content-Id per RFC 2046
    */
   private static Pattern sContentIdRegEx = Pattern.compile("<([^\\s]*)@([^\\s]*)>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE); //$NON-NLS-1$

   /** Regular expression to match a valid file name */
   private static Pattern sNameRegEx = Pattern.compile("([a-zA-Z0-9_\\-\\. ]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE); //$NON-NLS-1$

   /** regular expression to match a filename with an extension */
   private static Pattern sNameExtRegEx = Pattern.compile("(.*)\\.([a-z][A-Z0-9_\\-]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE); //$NON-NLS-1$

   /** regular expression to match a filename ending with . */
   private static Pattern sEndDotRegEx = Pattern.compile("(.*)\\.", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE); //$NON-NLS-1$

   /**
    * Return a reasonable file name for downloading the attachment
    * @param aHeaders map of name/value mime headers
    * @return filename with extension
    */
   public static String getFileName(Map aHeaders)
   {
      return getFileName(aHeaders, DEFAULT_FILE_NAME, DEFAULT_FILE_NAME_EXT);
   }

   /**
    * Return a reasonable file name for downloading the attachment
    * @param aHeaders map of name/value mime headers
    * @param aDefaultFileName fallback default filename
    * @return filename with extension
    */
   public static String getFileName(Map aHeaders, String aDefaultFileName)
   {
      return getFileName(aHeaders, aDefaultFileName, null);
   }

   /**
    * Return a reasonable file name for downloading the attachment
    * @param aHeaders map of name/value mime headers
    * @param aDefaultFileName fallback default filename
    * @param aDefaultExt fallback default filename extension
    * @return filename with extension
    */
   public static String getFileName(Map aHeaders, String aDefaultFileName, String aDefaultExt)
   {
      String fileName = null;
      String ext = null;

      // Work out a reasonable filename
      Matcher matcher;

      String match = (String)aHeaders.get(CONTENT_LOCATION_ATTRIBUTE);
      if ( !AeUtil.isNullOrEmpty(match) )
      {
         if ( sNameRegEx.matcher(match).matches() )
         {
            fileName = match;
         }
      }

      match = (String)aHeaders.get(CONTENT_ID_ATTRIBUTE);
      if ( !AeUtil.isNullOrEmpty(match) && AeUtil.isNullOrEmpty(fileName) )
      {
         matcher = sContentIdRegEx.matcher(match);
         if ( matcher.matches() )
         {
            fileName = matcher.group(1);
         }
         if ( sNameRegEx.matcher(match).matches() )
         {
            fileName = match;
         }
      }

      if ( sMimeToExtension.get((String)aHeaders.get(CONTENT_TYPE_ATTRIBUTE)) != null )
      {
         ext = (String)sMimeToExtension.get((String)aHeaders.get(CONTENT_TYPE_ATTRIBUTE));
      }

      if ( AeUtil.isNullOrEmpty(fileName) )
      {
         // resort to default
         fileName = (aDefaultFileName == null) ? DEFAULT_FILE_NAME : aDefaultFileName;
      }

      if ( AeUtil.isNullOrEmpty(ext) )
      {
         // resort to default
         ext = (aDefaultExt == null) ? DEFAULT_FILE_NAME_EXT : aDefaultExt;
      }

      matcher = sNameExtRegEx.matcher(fileName);
      if ( matcher.matches() )
      {
         if ( ext.equals(matcher.group(2)) )
         {
            fileName = matcher.group(1);
         }
      }

      matcher = sEndDotRegEx.matcher(fileName);
      if ( matcher.matches() )
      {
         fileName = matcher.group(1);
      }

      return fileName + "." + ext; //$NON-NLS-1$
   }

   /**
    * Returns the length of the given file.
    * @param aFilename
    */
   public static long getContentLength(String aFilename)
   {
      return new File(aFilename).length();
   }

   /**
    * Given a filename, return the appropriate content type
    * @param aFilename
    */
   public static String getContentType(String aFilename)
   {
      return getContentType(aFilename, "unknown"); //$NON-NLS-1$
   }

   /**
    * Given a filename, return the appropriate content type. If the type is not found, then the default value
    * is returned.
    * @param aFilename file name
    * @param aDefaultType default content type.
    */
   public static String getContentType(String aFilename, String aDefaultType)
   {
      String ext = AeFileUtil.getExtension(aFilename);
      return getContentTypeFromExtension(ext, aDefaultType);
   }

   /**
    * Given a file extension, returns the appropriate content type. If the type is not found, then the default
    * value is returned.
    * @param aExtension file name extension
    * @param aDefaultType default content type.
    */
   public static String getContentTypeFromExtension(String aExtension, String aDefaultType)
   {
      String contentType = (String)sExtensionToMime.get(AeUtil.getSafeString(aExtension).toLowerCase());
      if ( AeUtil.notNullOrEmpty(contentType) )
      {
         return contentType;
      }
      else if ( AeUtil.notNullOrEmpty(aDefaultType) )
      {
         return aDefaultType;
      }
      else
      {
         // default is binary stream.
         return "application/octet-stream"; //$NON-NLS-1$
      }
   }

   /**
    * Returns the mime type from the map of mime headers
    * @param aHeaders
    * @return mime type
    */
   public static String getMimeType(Map aHeaders)
   {
      return (aHeaders == null) ? null : (String)aHeaders.get(CONTENT_TYPE_ATTRIBUTE);
   }

   /**
    * Helper method that returns the mime type as the first token of a ';' delimited string
    * @param aContentType
    * @return the mime type
    * @throws AeException
    */
   public static String getMime(String aContentType) throws AeException
   {
      String str;
      try
      {
         BufferedReader reader = new BufferedReader(new StringReader(aContentType));

         while ((str = reader.readLine()) != null)
         {
            if ( str.length() > 0 )
            {
               StringTokenizer st = new StringTokenizer(str, ";"); //$NON-NLS-1$
               return st.nextToken();
            }
         }
      }
      catch (IOException ex)
      {
         throw new AeException(ex);
      }
      return ""; //$NON-NLS-1$
   }

   /**
    * Returns a best guess content type mime for the passed content type. When not a valid textual mime the
    * content itself is used to guess a mime type. By default <code>text/plain</code> is assumed. When the
    * content starts with <code>&lt;</code> and ends with <code>&gt;</code> <code>text/xml</code> is
    * assumed.
    * @param aContentType
    * @param aContent
    */
   public static String getSafeTextMime(String aContentType, String aContent)
   {
      if ( isTextual(aContentType) )
         return aContentType;
      else if ( contentIsXml(aContent) )
         return XML_MIME;
      return PLAIN_TEXT_MIME;
   }

   /**
    * Convenience method to check if the passed string content is valid xml
    * @param aContent the string to be evaluated
    * @return <code>true</code> for xml content; otherwise return false
    */
   public static boolean contentIsXml(String aContent)
   {
      if ( AeUtil.isNullOrEmpty(aContent) )
         return false;

      boolean isXml = true;
      try
      {
         AeXmlUtil.getDocumentBuilder().parse(new InputSource(new StringReader(aContent)));
      }
      catch (Exception ex)
      {
         isXml = false;
      }
      return isXml;
   }

   /**
    * Convenience method to check for xml content
    * @param aMime
    * @return true if text/xml or other recognized mime types ; otherwise returns false
    */
   public static boolean isXmlText(String aMime)
   {
      Set implicitMembers = new TreeSet();
      implicitMembers.add(AeMimeUtil.ATOM_XML_MIME);
      return isXmlText(aMime,implicitMembers);
       
   }
   
   /**
    * Convenience method to check for xml content
    * @param aMime
    * @return <code>true</code> if mime recognized and xml mime type ; otherwise returns <code>false</code>
    */
   public static boolean isXmlText(String aMime, Set aMemberMimeTypes)
   {
      // TODO (JB) for backwards compatibility - to be remove eventually when policy mime type dialog completed
      if (aMemberMimeTypes.size() == 0)
         aMemberMimeTypes = new TreeSet();
      aMemberMimeTypes.add(AeMimeUtil.ATOM_XML_MIME);
      
      if ( AeUtil.notNullOrEmpty(aMime) && (aMime.trim().startsWith(AeMimeUtil.XML_MIME) || isMember(aMime,aMemberMimeTypes)) )
         return true;
      else
         return false;
   }
   
   /**
    * @param aMime the mime type to be checked
    * @param aMembers a set of mime types
    * @return  <code>true</code> if mime type passed is a member of the passed mime set ; otherwise returns <code>false</code>
    */
   public static boolean isMember(String aMime, Set aMembers)
   {
      if(AeUtil.notNullOrEmpty(aMime))
      {
         for (Iterator iterator = aMembers.iterator(); iterator.hasNext();)
         {
            if(aMime.trim().startsWith((String)iterator.next()))
               return true;
         }
      }
      return false;
   }

   /**
    * <p>
    * Convenience method to check for textual content. Note text/html is not considered textual because the
    * ability to parse html is not guaranteed
    * </p>
    * <p>
    * <i>Note: html cannot be treated as simple text for a dom parser but on the other hand cannot be assumed
    * to be valid xml either </i>
    * </p>
    * @param aMime
    * @return true if considered text ; otherwise returns false
    */
   public static boolean isText(String aMime)
   {
      if ( AeUtil.notNullOrEmpty(aMime) && (aMime.trim().startsWith("text/") && !aMime.trim().startsWith(HTML_MIME)) ) //$NON-NLS-1$
         return true;
      return false;
   }
   
   /**
    * Convenience method to check for textual content. combines the logic of isText and isXmlText
    * @param aMime
    * @return true if considered text ; otherwise returns false
    */
   public static boolean isTextual(String aMime)
   {
      return isXmlText(aMime) || isText(aMime);
   }

   /**
    * Convenience method to check for multipart content
    * @param aMime
    * @return true if considered multipart ; otherwise returns false
    */
   public static boolean isMultipart(String aMime)
   {
      if ( aMime != null && aMime.toLowerCase().startsWith(AeMimeUtil.MULTIPART_MIME_ID) )
         return true;
      return false;
   }

   /**
    * Convenience method to check for multipart/form-data content
    * @param aMime
    * @return true if considered multipart ; otherwise returns false
    */
   public static boolean isMultipartForm(String aMime)
   {
      if ( AeUtil.notNullOrEmpty(aMime) && aMime.toLowerCase().startsWith(AeMimeUtil.MULTIPART_FORM_MIME) )
         return true;
      else
         return false;
   }

   /**
    * Ensures correct capitalization for significant headers
    * @param hName
    * @return the capitalized mime name
    */
   public static String formMimeKey(String hName)
   {
      String hKey = hName;
      if ( CONTENT_TYPE_ATTRIBUTE.equalsIgnoreCase(hName) )
         hKey = CONTENT_TYPE_ATTRIBUTE;
      else if ( CONTENT_ID_ATTRIBUTE.equalsIgnoreCase(hName) )
         hKey = CONTENT_ID_ATTRIBUTE;
      else if ( CONTENT_LOCATION_ATTRIBUTE.equalsIgnoreCase(hName) )
         hKey = CONTENT_LOCATION_ATTRIBUTE;
      else if ( CONTENT_LENGTH_ATTRIBUTE.equalsIgnoreCase(hName) )
         hKey = CONTENT_LENGTH_ATTRIBUTE;
      else if ( USER_AGENT_ATTRIBUTE.equalsIgnoreCase(hName) )
         hKey = USER_AGENT_ATTRIBUTE;
      else if ( CONTENT_ENCODING_ATTRIBUTE.equalsIgnoreCase(hName) )
         hKey = CONTENT_ENCODING_ATTRIBUTE;
      return hKey;
   }

   /**
    * returns a supported multipart content type
    * @param aContentType
    * @return the multipart mime as is if it is a supported mime; otherwise returns the default
    *         <code>multipart/mixed</code> type see <a href="http://www.faqs.org/rfcs/rfc1521.html">RFC1521
    *         Section 7.2.6</a>
    */
   public static String formMultipartMime(String aContentType)
   {
      if ( AeUtil.isNullOrEmpty(aContentType) )
         return MULTIPART_MIXED_MIME;
      if ( aContentType.equalsIgnoreCase(MULTIPART_FORM_MIME) )
         return MULTIPART_FORM_MIME;
      if ( aContentType.equalsIgnoreCase(MULTIPART_RELATED_MIME) || aContentType.startsWith(ATOM_XML_MIME) )
         return MULTIPART_RELATED_MIME;
      if ( aContentType.equalsIgnoreCase(MULTIPART_DIGEST_MIME) )
         return MULTIPART_DIGEST_MIME;
      if ( aContentType.equalsIgnoreCase(MULTIPART_ALTERNATIVE_MIME) )
         return MULTIPART_ALTERNATIVE_MIME;
      if ( aContentType.equalsIgnoreCase(MULTIPART_PARALLEL_MIME) )
         return MULTIPART_PARALLEL_MIME;

      return MULTIPART_MIXED_MIME;
   }

}