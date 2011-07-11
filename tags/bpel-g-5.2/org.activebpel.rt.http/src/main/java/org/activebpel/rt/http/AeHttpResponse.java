//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpResponse.java,v 1.6.2.1 2008/04/21 16:15:52 ppatruni Exp $
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.util.AeAutoCloseBlobInputStream;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A normalized representation of the httpClient response
 * @see <a href="http://jakarta.apache.org/httpcomponents/httpclient-3.x/">Apache HTTPClient</a>
 */
public class AeHttpResponse
{
   /** the HTTP response status code */
   private int mHttpStatusCode;

   private HttpMethod mMethod;

   /** response body XML DOM object */
   private Document mDoc;

   /** textual response body value */
   private String mText;

   /**
    * non xml content, the file is associated with a temp file which allows the http connection to be
    * released.
    */
   private AeAutoCloseBlobInputStream mAttachment;
   
   /**
    * Policy override properties derived from the endpoint policies or other handlers
    * @see AeRestInvokeHandler
    * @see AeAxisHTTPHandler
    */
   private Map mOverrideProperties;

   /**
    * Constructor
    * @param aHttpStatusCode
    * @param aMethod
    */
   public AeHttpResponse(int aHttpStatusCode, HttpMethod aMethod,  Map aOverrideProperties)
   {
      mHttpStatusCode = aHttpStatusCode;
      mMethod = aMethod;
      mOverrideProperties = aOverrideProperties;
   }

   /**
    * @return the httpStatusCode
    */
   public int getHttpStatusCode()
   {
      return mHttpStatusCode;
   }

   /**
    * @param aHttpStatusCode the httpStatusCode to set
    */
   public void setHttpStatusCode(int aHttpStatusCode)
   {
      mHttpStatusCode = aHttpStatusCode;
   }

   /**
    * @return the method
    */
   private HttpMethod getMethod()
   {
      return mMethod;
   }

   /**
    * @return the http response headers
    */
   public Map getHeaders()
   {
      return getResponseHeaders(getMethod(), null);
   }

   /**
    * @return the http response footers
    */
   public Map getFooters()
   {
      return getResponseFooters(getMethod(), null);
   }

   /**
    * @return the response body as a DOM document
    */
   public Document getResponseDoc()
   {
      return mDoc;
   }

   /**
    * @return the response body as text
    */
   public String getText()
   {
      return mText;
   }

   /**
    * @return the attachment
    */
   public InputStream getAttachment()
   {
      return mAttachment;
   }

   /**
    * @return the attachment mime headers
    */
   public Map getAttachmentMimeHeaders()
   {
      Map mimes = new HashMap();
      mimes.put(AeMimeUtil.CONTENT_TYPE_ATTRIBUTE, getContentType());
      mimes.put(AeMimeUtil.CONTENT_ID_ATTRIBUTE, getContentId());

      return mimes;
   }

   /**
    * Consume the http response
    * @throws AeException
    */
   public void consume() throws AeException
   {
      String contentMime = getContentType();
      boolean isXml = false;

      if ( AeMimeUtil.isXmlText(contentMime,getXmlMimes()) )
      {
         try
         {
            mDoc = AeXmlUtil.getDocumentBuilder().parse(getMethod().getResponseBodyAsStream());
            if ( mDoc != null )
               isXml = true;
         }
         catch (SAXException ex)
         {
            try
            {
               // parse error,
               throw new AeException((AeMessages.format("AeHttpResponse.ERROR_NotXml", new Object[] { getMethod().getName(), getMethod().getURI(), //$NON-NLS-1$
                     getMethod().getParams() })));
            }
            catch (URIException ex1)
            {
               throw new AeException(ex1);
            }
         }
         catch (IOException ex)
         {
            throw new AeException(ex);
         }
      }

      if ( !isXml )
      {
         // any non xml response becomes an attachment
         try
         {
            if ( AeMimeUtil.isText(contentMime) )
               mText = getMethod().getResponseBodyAsString();
            else
               // any non textual response becomes an attachment
               mAttachment = new AeAutoCloseBlobInputStream(getMethod().getResponseBodyAsStream(), AeHttpConfig.TEMP_FILE_PREFIX,
                     AeHttpConfig.TEMP_FILE_POSTFIX);
         }
         catch (Exception ex)
         {
            throw new AeException(ex);
         }
      }

      if ( !isOK() )
      {
         // Http returned an error response, convert response to an AeHttpException
         String info;
         if ( isXml )
         {
            info = AeXMLParserBase.documentToString(mDoc, true);
         }
         else
         {
            if ( AeMimeUtil.isText(contentMime) )
               info = getText();
            else
               // consume attachment stream as exception info
               info = AeHttpUtil.stream2String(mAttachment);
         }
         throw new AeHttpException(getHttpStatusCode(), getMethod().getName(), getStatusMessage(), info);
      }
   }
 

   /**
    * Release the http connection, close the attachment stream and remove the temp file
    */
   public void close()
   {
      if ( getMethod() != null )
         getMethod().releaseConnection();
   }

   /**
    * <p>
    * Convenience method to create a map of {@link HttpMethod} http response headers.
    * </p>
    * @param aMethod
    * @param aFilter
    */
   public Map getResponseHeaders(HttpMethod aMethod, Set aFilter)
   {
      return convertHeaders2Map(aMethod.getResponseHeaders(), aFilter);
   }

   /**
    * <p>
    * Convenience method to create a map of {@link HttpMethod} http response footers.
    * </p>
    * @param aMethod
    * @param aFilter
    */
   public Map getResponseFooters(HttpMethod aMethod, Set aFilter)
   {
      return convertHeaders2Map(aMethod.getResponseFooters(), aFilter);
   }

   /**
    * convert a Header[] to a map. Filters out members of the passed filter Set
    * @param aMethod
    * @param aFilter
    * @param aHeaders
    * @return
    */
   private Map convertHeaders2Map(Header[] aHeaders, Set aFilter)
   {
      Map responseMap = new HashMap();
      for (int i = 0; i < aHeaders.length; i++)
      {
         String headerName = aHeaders[i].getName();
         if ( aFilter == null || !aFilter.contains(headerName.toLowerCase()) )
            AeHttpUtil.addToMap(responseMap, AeMimeUtil.formMimeKey(headerName), getResponseHeader(getMethod(), headerName), false);

      }
      return responseMap;
   }

   /**
    * @return the content mime type, or null when there is non
    */
   protected String getContentType()
   {
      // use case when there is no mime in the response- assume text/xml;charset=UTF-8
      String header = getResponseHeader(getMethod(), AeMimeUtil.CONTENT_TYPE_ATTRIBUTE);
      return AeUtil.isNullOrEmpty(header) ? AeMimeUtil.XML_UTF8_MIME : header;
   }

   /**
    * @return the charset to use for decoding
    */
   protected String getCharset()
   {
      HeaderElement[] elem = getHeaderElements(getHeader(AeMimeUtil.CONTENT_TYPE_ATTRIBUTE, getMethod()));
      return elem == null ? null : elem[0].getValue();
   }

   /**
    * @return the content mime type, or null when there is non
    */
   protected String getContentId()
   {
      String header = getResponseHeader(getMethod(), AeMimeUtil.CONTENT_ID_ATTRIBUTE);
      return AeUtil.isNullOrEmpty(header) ? AeHttpConfig.DEFAULT_ATTACHMENT_ID : header;
   }

   /**
    * Convenience method to extract a response header from apache commons HttpClient method
    * @param aMethod
    * @param aHeaderName
    * @see HttpMethod
    */
   public static String getResponseHeader(HttpMethod aMethod, String aHeaderName)
   {

      HeaderElement[] elem = getHeaderElements(getHeader(aHeaderName, aMethod));

      if ( elem == null )
         return null;

      StringBuffer buffer = new StringBuffer();
      for (int i = 0; i < elem.length; i++)
      {
         if ( i > 0 )
            buffer.append(","); //$NON-NLS-1$

         buffer.append(elem[i].getName());

         if ( elem[i].getValue() != null )
            buffer.append("=").append(elem[i].getValue()); //$NON-NLS-1$

         NameValuePair[] nvp = elem[i].getParameters();
         if ( nvp != null )
         {
            for (int j = 0; j < nvp.length; j++)
            {
               buffer.append(";").append(nvp[i].getName()); //$NON-NLS-1$

               if ( nvp[i].getValue() != null )
                  buffer.append("=").append(nvp[i].getValue()); //$NON-NLS-1$
            }
         }
      }
      return buffer.toString();
   }

   /**
    * @param aMethod
    * @return the http client response header elements
    */
   private static HeaderElement[] getHeaderElements(Header aHeader)
   {
      return aHeader == null ? null : aHeader.getElements();
   }

   /**
    * @param aMethod
    * @return the named header
    */
   private static Header getHeader(String aHeaderName, HttpMethod aMethod)
   {
      Header[] header = aMethod.getResponseHeaders(aHeaderName);
      return header.length == 0 ? null : header[0];
   }

   /**
    * @return true when http response was successful
    */
   public boolean isOK()
   {
      return getHttpStatusCode() > 199 && getHttpStatusCode() < 300;
   }
   
   /**
    * @return <code>true</code> when http response is a redirect
    */
   public boolean isRedirect()
   {
      // TODO (JB) check for significant redirect codes
      return getHttpStatusCode() > 300 && getHttpStatusCode() < 399;
   }

   /**
    * returns the http status message
    * @throws AeException
    */
   private String getStatusMessage() throws AeException
   {
      String statusMessage = null;
      if ( getHttpStatusCode() == HttpStatus.SC_NOT_IMPLEMENTED )
      {
         try
         {
            statusMessage = AeMessages.format(
                  "AeHttpResponse.ERROR_MethodNotsupported", new Object[] { new Integer(getHttpStatusCode()), getMethod().getName(), getMethod().getURI() }); //$NON-NLS-1$
         }
         catch (URIException ex)
         {
            throw new AeException(ex);
         }
      }
      if ( getHttpStatusCode() != HttpStatus.SC_OK )
         statusMessage = HttpStatus.getStatusText(getHttpStatusCode());

      return statusMessage;
   }
   
   /**
    * @return the overrideProperties
    */
   public Map getOverrideProperties()
   {
      if ( mOverrideProperties == null )
         mOverrideProperties = Collections.EMPTY_MAP;
      return mOverrideProperties;
   }
   
   /**
    * Returns the set of xml mime definitions of an http policy
    * @return
    */
   public Set getXmlMimes()
   {
      Object xmlmimes = getOverrideProperties().get(IAePolicyConstants.TAG_HTTP_XML_TYPES);
      return xmlmimes == null ? Collections.EMPTY_SET : (Set)xmlmimes;
   }
}
