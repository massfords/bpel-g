//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpServletRequest.java,v 1.4.4.1 2008/04/21 16:15:52 ppatruni Exp $
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.activebpel.rt.AeException;
import org.activebpel.rt.http.multipart.parse.AeEnumerator;
import org.activebpel.rt.http.multipart.parse.AeHttpMultipartParser;
import org.activebpel.rt.http.multipart.parse.AeOpenTreeMap;
import org.activebpel.rt.http.multipart.parse.AeServletRequestDataSource;
import org.activebpel.rt.util.AeAutoCloseBlobInputStream;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.wsio.AeWebServiceAttachment;

/**
 * Extends JavaSoft's Http servlet request to handle multipart requests
 * <p>
 * JavaSoft's Servlet 2.x Specification and API does not incorporate the functionality to handle multipart
 * MIME client requests <code>multipart/*</code> nicely.
 * </p>
 * This class allows most multipart request types to be handled in the same manner that normal requests are
 * handled.
 * <p>
 * <b>NOTES:</b>
 * <ol>
 * <li><i>HttpServletRequestWrapper overrides:</i>
 * <dl>
 * <dt>getContentType()</dt>
 * <dd>Returns the content type two ways to work around WebSphere oddities.</dd>
 * <dt>getParameter(String)</dt>
 * <dd>Multipart aware.</dd>
 * <dt>getParamtersMap()</dt>
 * <dd>Multipart aware.</dd>
 * <dt>getParameterNames()</dt>
 * <dd>Multipart aware.</dd>
 * <dt>getParameterValues(String)</dt>
 * <dd>Multipart aware.</dd>
 * </dl>
 * </li>
 * <li><i>Enhanced functionality methods:</i>
 * <dl>
 * <dt>isMultipart()</dt>
 * <dd>boolean indicator <code>true</code> when the request is Multipart.</dd>
 * <dt>getAttachments()</dt>
 * <dd>returns a list of WSIO attachments parsed from a Multipart request, one attachment per part.</dd>
 * <dt>getAttachment(String)</dt>
 * <dd>returns a WSIO attachment by name from the parsed Multipart request.The attachment name corresponds
 * with the attachment <code>Content-Type</code> header value.</dd>>
 * <dt>getAttachmentNames()</dt>
 * <dd>returns a Set of WSIO attachment names parsed from a Multipart request, one attachment per part. The
 * attachment name is the value of the attachment <code>Content-Type</code> header.</dd>
 * <dt>getParser()</dt>
 * <dd>Provides access to the multipart parser.</dd>
 * <dt>createRequestHeaderMap(Set)</dt>
 * <dd>Creates a map of {@link HttpServletRequest} headers. filters out headers that are members of the
 * passed <code>Set</code>.</dd>
 * <dt>dumpHttpRequestInfo()</dt>
 * <dd>Convenience method for debugging purposes. dumps AeHttpServletRequest information.</dd>
 * </dl>
 * </li>
 * </ol>
 * </p>
 * @see AeHttpMultipartParser
 * @see HttpServletRequestWrapper
 */
public class AeHttpServletRequest extends HttpServletRequestWrapper
{

   /** <code>true</code> if the request is a Multipart request */
   private boolean mIsMultipart;

   /** the Multipart parser */
   private AeHttpMultipartParser mParser;

   /** holder of a normal request request information (not Multipart) */
   private AeWebServiceAttachment mNormalBody;

   /**
    * Constructor
    * @param aRequest
    * @throws IOException
    * @throws AeException
    */
   public AeHttpServletRequest(HttpServletRequest aRequest) throws AeException, IOException
   {
      super(aRequest);

      // IE handles redirects using the previous request's content type so we need to
      // ignore the Content-Type on requests with a Content-Length less than zero.
      if ( AeMimeUtil.isMultipart(getContentType()) && getContentLength() > -1 )
      {
         mIsMultipart = true;
         mParser = new AeHttpMultipartParser(new AeServletRequestDataSource(getContentType(), getRemoteAddr(), getInputStream()));
      }
      else
         mIsMultipart = false;
   }

   /**
    * Returns whether the HttpServletRequest was a multipart posting or not.
    * @return <code>true</code> if the request was multipart encoded, <code>false</code> otherwise
    */
   public boolean isMultipart()
   {
      return mIsMultipart;
   }

   /**
    * @see javax.servlet.ServletRequestWrapper#getParameterMap()
    */
   public Map getParameterMap()
   {
      return isMultipart() ? toArrayValues((AeOpenTreeMap)getParser().getParams()) : super.getParameterMap();
   }

   /**
    * Converts the Set of values to an object array of values
    * @param aMap
    * @return the map where values are an object array
    */
   protected static Map toArrayValues(AeOpenTreeMap aMap)
   {
      Map m = new LinkedHashMap();
      if ( aMap == null )
         return m;
      for (Iterator itr = aMap.keySet().iterator(); itr.hasNext();)
      {
         String key = (String)itr.next();
         Set v = (Set)aMap.getNode(key);
         m.put(key, v.toArray());
      }
      return m;
   }

   /**
    * @see javax.servlet.ServletRequestWrapper#getParameterNames()
    */
   public Enumeration getParameterNames()
   {
      return isMultipart() ? new AeEnumerator(getParser().getParams().keySet().iterator()) : super.getParameterNames();
   }

   /**
    * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
    */
   public String getParameter(String aName)
   {
      return isMultipart() ? getParser().getParameter(aName) : super.getParameter(aName);
   }

   /**
    * @see javax.servlet.ServletRequestWrapper#getParameterValues(java.lang.String)
    */
   public String[] getParameterValues(String aName)
   {
      return isMultipart() ? getParser().getParameterValues(aName) : super.getParameterValues(aName);
   }

   /**
    * Returns a <code>AeMultipartFileInfo</code> providing access to the file and information about the
    * file. Hopefully. If there is more than one file referenced by <code>name</code>, then the "first" one
    * is returned.
    * @return <code>AeMultipartFileInfo</code> providing access to the file and information about the file;
    *         <code>null</code> if no files are found or the ServletRequest is not multipart.
    * @throws AeException
    * @throws AeException
    */
   public AeWebServiceAttachment getAttachment(String aName) throws AeException
   {
      if ( !isMultipart() )
         return getNormalBody();
      for (Iterator attItr = getAttachments().iterator(); attItr.hasNext();)
      {
         AeWebServiceAttachment attachment = (AeWebServiceAttachment)attItr.next();
         if ( attachment.getContentId().equals(aName) )
            return attachment;
      }
      return null;
   }

   /**
    * Returns an <code>Iterator</code> of attachment names parsed out of a multipart request
    * @return <code>Iterator</code> of attachment names.
    */
   public Iterator getAttachmentNames()
   {
      return isMultipart() ? getParser().getAttachmentNames() : Collections.EMPTY_SET.iterator();
   }

   /**
    * Returns an <code>Iterator</code> of attachments parsed out of a multipart request
    * @return the attachments list
    */
   public ArrayList getAttachments()
   {
      return isMultipart() ? getParser().getAttachments() : (ArrayList)Collections.EMPTY_LIST;
   }

   /**
    * @return the multipart parser object, returns <code>null</code> when the request is not Multipart
    */
   protected AeHttpMultipartParser getParser()
   {
      return mParser;
   }

   /**
    * Creates an attachment from the request body.
    * @return the request body as an attachment or, return null when there is no body content
    * @throws AeException
    */
   public AeWebServiceAttachment getNormalBody() throws AeException
   {
      if ( mNormalBody == null && getContentLength() > 0 )
      {
         // create an attachment with headers and content
         try
         {
            mNormalBody = new AeWebServiceAttachment(new AeAutoCloseBlobInputStream(getInputStream(), AeHttpConfig.TEMP_FILE_PREFIX,
                  AeHttpConfig.TEMP_FILE_POSTFIX), createRequestHeaderMap(null));
         }
         catch (Exception ex)
         {
            throw new AeException(ex);
         }
      }
      return mNormalBody;
   }

   /**
    * Returns the content type two ways to work around WebSphere oddities
    * @see javax.servlet.ServletRequestWrapper#getContentType()
    */
   public String getContentType()
   {
      String type = null;
      String type1 = getHeader(AeMimeUtil.CONTENT_TYPE_ATTRIBUTE);
      String type2 = super.getContentType();

      // If one value is null, choose the other value
      if ( type1 == null && type2 != null )
         type = type2;
      else if ( type2 == null && type1 != null )
         type = type1;

      // If neither value is null, choose the longer value
      else if ( type1 != null && type2 != null )
         type = (type1.length() > type2.length() ? type1 : type2);

      return type;
   }

   /**
    * Convenience method to create a map of {@link HttpServletRequest} headers.
    * <p>
    * Some headers, such as <code>Accept-Language</code> can be sent by clients as several headers each with
    * a different value rather than sending the header as a comma separated list. This method converts
    * duplicate headers to a single header with comma separated values.
    * </p>
    * @param aFilter
    * @return the request map
    */
   public Map createRequestHeaderMap(Set aFilter)
   {
      Enumeration httpHeaderNames = getHeaderNames();

      // Convert the headers of the request to a Map
      if ( isMultipart() )
      {
         // for multipart filter out some headers that are not relevant due to current aeREST.xsd message
         // structure
         // TODO: (JB) REST modify aeREST schema to support multipart nicely.
         aFilter.add(AeMimeUtil.CONTENT_LENGTH_ATTRIBUTE.toLowerCase());
      }

      Map headerMap = new HashMap();
      while (httpHeaderNames.hasMoreElements())
      {
         String hName = (String)httpHeaderNames.nextElement();
         if ( aFilter == null || !aFilter.contains(hName.toLowerCase()) )
         {
         // Convert to comma separated value. Some headers, such as <code>Accept-Language</code> can be sent
         // by clients as several headers each
         // with a different value rather than sending the header as a comma separated list.
         Enumeration values = getHeaders(hName);
         boolean separator = false;
         StringBuffer hValue = new StringBuffer();
         while (values.hasMoreElements())
         {
            hValue.append((String)values.nextElement());
            if ( separator )
               hValue.append(","); //$NON-NLS-1$
            separator = true;

         }
        
            AeHttpUtil.addToMap(headerMap, AeMimeUtil.formMimeKey(hName), hValue.toString(), false);
         }
      }

      return headerMap;
   }

   /**
    * Convenience method for debugging purposes. dumps AeHttpServletRequest information
    */
   public void dumpHttpRequestInfo()
   {
      StringBuffer params = new StringBuffer("\nParams:\n"); //$NON-NLS-1$
      Enumeration httpParams = getParameterNames();
      while (httpParams.hasMoreElements())
      {
         String name = (String)httpParams.nextElement();
         params.append(name).append("="); //$NON-NLS-1$
         String[] value = (String[])getParameterValues(name);
         for (int i = 0; i < value.length; i++)
         {
            if ( i > 0 )
               params.append(","); //$NON-NLS-1$
            params.append(((String)value[i]));
         }
         params.append("\n"); //$NON-NLS-1$
      }

      params.append("Headers:\n"); //$NON-NLS-1$
      Enumeration hdrs = getHeaderNames();
      while (hdrs.hasMoreElements())
      {
         String hName = (String)hdrs.nextElement();
         params.append(hName).append(": ").append(getHeader(hName)).append("\n"); //$NON-NLS-1$//$NON-NLS-2$
      }

      AeException.logWarning("\nprotocol=" + getProtocol() + "\nmethod=" + getMethod() + "\nserver name=" + getServerName() + "\nport=" //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
            + getServerPort() + "\nquery string=" + getQueryString() + "\nremote addr=" + getRemoteAddr() + "\nremote host=" //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
            + getRemoteHost() + "\npath info=" + getPathInfo() + "\npath translated=" + getPathTranslated() + "\ncontext=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            + getContextPath() + "\nuri=" + getRequestURI() + "\nscheme=" + getScheme() + "\nchar encoding=" + getCharacterEncoding() //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            + "\nurl=" + getRequestURL() + params.toString()); //$NON-NLS-1$
   }
}
