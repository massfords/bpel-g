//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpRequest.java,v 1.5.2.1 2008/04/21 16:15:52 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.IAePolicyConstants;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.http.multipart.AeHttpMultipartRequestEntity;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.IAeWebServiceAttachment;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

/**
 * A wrapper for http requests. The class is tailored for use of the apache commons HttpClient API
 * @see AeHttpSendHandlerBase
 * @see #createPostRequest(PostMethod) - for more details on the handling of multi-part mime messages
 */
public class AeHttpRequest
{
   /** The http request method name */
   private String mMethodName;

   private String mURI;

   /** xml or textual data content to be delivered */
   private String mPayloadContent;

   /** the Content-Type mime of the payload for textual type content */
   private String mPayloadContentType;

   /** attachments to be delivered, null indicates no attachments */
   private List mAttachments;

   /**
    * Override properties derived from the endpoint policies or other handlers
    * @see AeRestInvokeHandler
    * @see AeAxisHTTPHandler
    */
   private Map mOverrideProperties;

   /**
    * name value pair of url parameters, parameter name is the key. Value is a list to support duplicate
    * parameter names e.g:
    * <code>http://myHost.com/active-bpel/services/REST/someService?param=apple&amp;param=pear</code>. A
    * boolean value has a null value
    */
   private Map mUrlParams;

   /**
    * name value pair of http header parameters, parameter name is the key. A boolean parameter has a null
    * value
    */
   private Map mHeaders;

   /**
    * Constructor
    */
   public AeHttpRequest(String aMethod, Map aOverrideProperties)
   {
      mMethodName = aMethod;
      mOverrideProperties = aOverrideProperties;
   }

   /**
    * @return the methodName
    */
   public String getMethodName()
   {
      return mMethodName;
   }

   /**
    * @return the uRI
    */
   public String getURI()
   {
      return mURI;
   }

   /**
    * @param aUri the uRI to set
    */
   public void setURI(String aUri)
   {
      mURI = aUri;
   }

   /**
    * @return the urlParams
    */
   public Map getUrlParams()
   {
      if ( mUrlParams == null )
         mUrlParams = new LinkedHashMap();
      return mUrlParams;
   }

   /**
    * add a url parameter
    * @param aName
    * @param aValue
    */
   public void addUrlParam(String aName, String aValue)
   {
      if ( mUrlParams == null )
         mUrlParams = new LinkedHashMap();
      mUrlParams.put(aName, aValue);
   }

   /**
    * @param aUrlParams the urlParams to set
    */
   public void setUrlParams(Map aUrlParams)
   {
      mUrlParams = aUrlParams;
   }

   /**
    * return a single header by name
    * @param aName the name of the header
    * @return the header value, can be null when not found
    */
   public String getHeader(String aName)
   {
      return (String)getHeaders().get(aName);
   }

   /**
    * @return the headers
    */
   public Map getHeaders()
   {
      if ( mHeaders == null )
         mHeaders = new HashMap();
      return mHeaders;
   }

   /**
    * add a http header parameter
    * @param aName
    * @param aValue
    */
   public void addHeader(String aName, String aValue)
   {
      getHeaders().put(aName, aValue);
   }

   /**
    * @param aHeaders the headers to set
    */
   public void setHeaders(Map aHeaders)
   {
      mHeaders = AeHttpUtil.normalizeHeaders(aHeaders);
   }

   /**
    * @return the payloadContent
    */
   private String getPayloadContent()
   {
      return mPayloadContent;
   }

   /**
    * @return the payloadContentType
    */
   private String getPayloadContentType()
   {
      return AeUtil.getSafeString(mPayloadContentType);
   }

   /**
    * @param aPayloadContentType the Content-Type of the payload
    * @param aPayloadContent the payloadContent and its' content type to set
    */
   public void setPayload(String aPayloadContentType, String aPayloadContent)
   {
      mPayloadContentType = AeMimeUtil.getSafeTextMime(aPayloadContentType, aPayloadContent);
      mPayloadContent = aPayloadContent;
   }

   /**
    * @param aAttachments the attachments to set
    */
   public void setAttachments(List aAttachments)
   {
      mAttachments = aAttachments;
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
    * Creates a single body (normal) http request and sets the method parameters
    * @return the request to be delivered
    * @throws AeException
    */
   public RequestEntity createRequestBody() throws AeException
   {
      RequestEntity requestEntity = null;
      try
      {
         // Single part body payload
         if ( getPayloadContent() != null )
         {
            requestEntity = new StringRequestEntity(getPayloadContent(), AeMimeUtil.getMime(getPayloadContentType()), AeUTF8Util.UTF8_ENCODING);
         }
         else if ( mAttachments != null )
         {
            IAeWebServiceAttachment attachment = (IAeWebServiceAttachment)mAttachments.iterator().next();
            // add mime headers as http headers
            for (Iterator mimeItr = attachment.getMimeHeaders().entrySet().iterator(); mimeItr.hasNext();)
            {
               Map.Entry mime = (Map.Entry)mimeItr.next();
               addHeader((String)mime.getKey(), (String)mime.getValue());
            }

            requestEntity = new InputStreamRequestEntity(attachment.getContent(), AeUtil.getBigNumeric((String)attachment.getMimeHeaders().get(
                  AeMimeUtil.AE_SIZE_ATTRIBUTE)), AeMimeUtil.getMimeType(attachment.getMimeHeaders()));
         }

      }
      catch (Throwable t)
      {
         throw new AeException(t);
      }
      return requestEntity;
   }

   /**
    * Creates the textual or binary body content of the request.
    * <p>
    * If request contains attachment data then these attachments will either become the payload or get
    * transmitted as part of a multi-part mime message.</p>
    * <p>
    * The exact rules for attachments and explicit multi-part mime messages are as follows:
    * <ol>
    * <li><b>Implicit Multipart</b><br/><br/>
    * <ol>
    * <li><b>Payload with single or multiple attachment</b> - The message is transmitted as a multi-part
    * mime with the first part being the payload and the following part(s) being the attachment(s).</li>
    * <br/><br/>
    * <li><b>No payload with single attachment</b> - The single attachment is transmitted as the payload and
    * the content type of the response is set to match the content type of the attachment. i.e. sending a
    * message with an image/jpg attachment and no payload data will result in the content type of the message
    * being image/jpg.</li>
    * <br/><br/>
    * <li><b>No payload with multiple attachments</b> - The attachments are transmitted as a multi-part mime
    * message.</li>
    * </ol>
    * </li>
    * <li><b>Explicit Form Data Multipart</b> <br/><br/> A header <code>Content-Type</code> with the
    * value <code>multipart/form-data</code> in the <code>RESTRequest</code> message, results in an
    * explicit multi-part mime message. <br/><br/>
    * <ol>
    * <li>Any <code>RESTRequest</code> parameters are delivered as <code>plain/text</code> parts with a
    * disposition header format: <br/><br/> <b>Content-Disposition: form-data; name="</b><i>param-name</i><b>"</b>.
    * <br/><br/> </li>
    * <li> The optional payload is delivered as a File part with a disposition header format: <br/><br/>
    * <b>Content-Disposition: form-data; name="</b><i>param-name</i><b>"; filename="</b><i>file-name</i><b>"</b>.
    * <br/><br/> </li>
    * <li> Optional attachments are transmitted as File parts with a disposition header format: <br/><br/>
    * <b>Content-Disposition: form-data; name="</b><i>param-name</i><b>"; filename="</b><i>file-name</i><b>"</b>.
    * <br/><br/></li>
    * </ol>
    * </li>
    * </ol>
    * </p>
    * @return the httpclient request entity
    * @throws AeException
    */
   public RequestEntity createPostRequest(PostMethod aMethod) throws AeException
   {
      if ( isMultipart() )
      {
         // Multipart body
         return createMultipartBody(aMethod);
      }
      else
      {
         // Normal POST

         // set parameters
         for (Iterator iterator = getUrlParams().entrySet().iterator(); iterator.hasNext();)
         {
            Map.Entry param = (Map.Entry)iterator.next();
            String name = (String)param.getKey();
            List valueList = (List)param.getValue();
            for (Iterator valueItr = valueList.iterator(); valueItr.hasNext();)
            {
               String value = (String)valueItr.next();
               aMethod.addParameter(name, value);
            }
         }

         // set body
         return createRequestBody();
      }
   }

   /**
    * Creates a Multipart body from the payload and attachments
    * @return
    * @throws AeException
    */
   private RequestEntity createMultipartBody(HttpMethod aMethod) throws AeException
   {
      String multipartContentType = AeMimeUtil.formMultipartMime(AeMimeUtil.getMimeType(getHeaders()));

      Part[] parts;
      if ( AeMimeUtil.isMultipartForm(multipartContentType) )
      {
         parts = createFormParts();
      }
      else
      {
         int partCount = mAttachments == null ? 0 : mAttachments.size() + ((getPayloadContent() == null) ? 0 : 1);
         parts = new Part[partCount];

         int atPart = 0;
         if ( getPayloadContent() != null )
            parts[atPart++] = AeHttpPartFactory.createPart(getHeaders(), getPayloadContentType(), getPayloadContent(), false);

         if ( mAttachments != null )
         {
            for (Iterator attachItr = mAttachments.iterator(); attachItr.hasNext();)
            {
               IAeWebServiceAttachment attachment = (IAeWebServiceAttachment)attachItr.next();
               parts[atPart++] = AeHttpPartFactory.createPart(attachment, false);
            }
         }
      }

      // Ensure that the multipart does not get clobbered by removing the content type from the headers
      // after
      // setting the method.
      // Httpclient uses the EntityEnclosingMethod to determine the Content-Type. It will ignore the entity
      // content type and not generate the boundary if the method content type is set directly.
      // So, as a safe guard the Content-Type header is removed.
      getHeaders().remove(AeMimeUtil.CONTENT_TYPE_ATTRIBUTE);

      return new AeHttpMultipartRequestEntity(parts, aMethod.getParams(), multipartContentType);
   }

   /**
    * Creates the parts for a Form data multipart post
    * @return
    * @throws AeException
    */
   private Part[] createFormParts() throws AeException
   {
      List parts = new ArrayList();

      // first create form field parts for the parameters.
      for (Iterator iterator = getUrlParams().entrySet().iterator(); iterator.hasNext();)
      {
         Map.Entry param = (Map.Entry)iterator.next();
         String name = (String)param.getKey();
         if ( AeUtil.notNullOrEmpty(name) )
         {
            // create multiple parts for multiple values
            List valueList = (List)param.getValue();
            for (Iterator valueItr = valueList.iterator(); valueItr.hasNext();)
            {
               parts.add(AeHttpPartFactory.createPart(name, valueItr.next().toString()));
            }
         }
      }

      if ( getPayloadContent() != null )
         parts.add(AeHttpPartFactory.createPart(getHeaders(), getPayloadContentType(), getPayloadContent(), true));

      if ( mAttachments != null )
      {
         for (Iterator attachItr = mAttachments.iterator(); attachItr.hasNext();)
         {
            IAeWebServiceAttachment attachment = (IAeWebServiceAttachment)attachItr.next();
            parts.add(AeHttpPartFactory.createPart(attachment, true));
         }
      }

      return (Part[])parts.toArray(new Part[parts.size()]);
   }

   /**
    * @return <code>true</code> if multipart body; otherwise return <code>false</code>
    */
   private boolean isMultipart()
   {
      if ( AeMimeUtil.isMultipart(AeMimeUtil.getMimeType(getHeaders())) )
         return true;
      if ( getPayloadContent() == null && mAttachments == null )
         return false;

      if ( (getPayloadContent() == null && mAttachments != null && mAttachments.size() == 1) || (getPayloadContent() != null && mAttachments == null) )
         return false;
      else
         return true;
   }

   /**
    * Sends the request to the http manager for delivery
    * @return the response data for the http request
    * @throws AeException
    */
   public AeHttpResponse send() throws AeException
   {
      return getHttpServiceManager().send(this);
   }

   /**
    * Gets the http service manager.
    * @throws AeException
    * @see IAeHttpServiceManager
    * @see AeHttpServiceManager
    */
   public static IAeHttpServiceManager getHttpServiceManager() throws AeException
   {
      return (IAeHttpServiceManager)AeEngineFactory.getEngine().getCustomManager(AeHttpConfig.HTTP_SERVICE_MANAGER_ENTRY);
   }

   /**
    * Returns the redirect policy assertion.
    * @return <code>true</code> - redirect always with GET method, <code>false</code> - redirect with
    *         original method (default)
    */
   public boolean isRedirectWithGET()
   {
      Object withGet = getOverrideProperties().get(IAePolicyConstants.ATTR_HTTP_REDIRECT_WITH_GET);
      return withGet == null ? false : ((Boolean)withGet).booleanValue();
      
   }
   
   /**
    * Creates a get method redirect request
    * @param aRedirectLocation
    * @return the redirection AehttpRequest object
    * @throws URIException 
    */
   public AeHttpRequest createGETRedirectRequest(URI aRedirectLocation) throws URIException
   {
     
      AeHttpRequest redirectRequest = new AeHttpRequest(AeHttpConfig.GET_ENTRY,getOverrideProperties());
      redirectRequest.setURI(aRedirectLocation.getEscapedPath());
      redirectRequest.setUrlParams(AeHttpUtil.createQueryStringMap(aRedirectLocation.getQuery()));
      return redirectRequest;
   }

   /**
    * @return true if request method is GET
    */
   public boolean isGet()
   {
      return mMethodName.equals(AeHttpConfig.GET_ENTRY);
   }

   /**
    * @return true if request method is PUT
    */
   public boolean isPut()
   {
      return mMethodName.equals(AeHttpConfig.PUT_ENTRY);
   }

   /**
    * @return true if request method is POST
    */
   public boolean isPost()
   {
      return mMethodName.equals(AeHttpConfig.POST_ENTRY);
   }

   /**
    * @return true if request method is DELETE
    */
   public boolean isDelete()
   {
      return mMethodName.equals(AeHttpConfig.DELETE_ENTRY);
   }

   /**
    * @return true if request method is TRACE
    */
   public boolean isTrace()
   {
      return mMethodName.equals(AeHttpConfig.TRACE_ENTRY);
   }

   /**
    * @return true if request method is OPTIONS
    */
   public boolean isOptions()
   {
      return mMethodName.equals(AeHttpConfig.OPTIONS_ENTRY);
   }

}
