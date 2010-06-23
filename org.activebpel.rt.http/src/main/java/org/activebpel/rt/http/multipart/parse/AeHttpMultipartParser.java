//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/multipart/parse/AeHttpMultipartParser.java,v 1.3.4.1 2008/04/21 16:15:52 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http.multipart.parse;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.internet.MimeMultipart;

import org.activebpel.rt.AeException;
import org.activebpel.rt.http.AeHttpServletRequest;
import org.activebpel.rt.http.AeHttpUtil;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.AeWebServiceAttachment;

/**
 * Http Request multipart request parser. This class provides {@link AeHttpServletRequest} with Multipart
 * parsing capabilities.
 * <p>
 * Multipart sub types supported include are (but not limited to):</p>
 * <ul>
 * <li><code>multipart/form-data</code></li>
 * <li><code>multipart/mixed</code></li>
 * <li><code>multipart/related</code></li>
 * <li><code>multipart/digest</code></li>
 * </ul>
 * <p>
 * This class uses the parsing capabilities of javax.mail.internet.MimeMultipart to get the contents of the
 * multipart requests. This requires the {@link javax.activation.DataHandler JavaBeans Activation Framework}
 * be available as well.
 * </p>
 * <p>
 * To handle multiple values per parameter key, including multiple file uploads under the same form name .
 * AeOpenTreeMap is used to handle multiple values per key.
 * </p>
 * @see AeHttpServletRequest
 * @see AeServletRequestDataSource
 * @see AeOpenTreeMap
 * @see javax.servlet.ServletRequest
 * @see javax.servlet.http.HttpServletRequest
 * @see javax.mail.internet.MimeMultipart
 */
public final class AeHttpMultipartParser
{
   /** the HttpServletRequest data source to be parsed */
   private AeServletRequestDataSource mDataSource;

   /** the parsed parts. */
   private MimeMultipart mAllParts = null;

   /**
    * Map of all the values uploaded in the multipart form. Only set if <code>multiPart</code> is
    * <code>true</code>.
    */
   private AeOpenTreeMap mParams;

   /** List of all the attachments derived from the multipart parser */
   private ArrayList mAttachments;

   /** The disposition header be filtered out of file related attachments */
   private static final Set sExcludeDisposition = AeUtil.toSet(new String[] { AeMimeUtil.CONTENT_DISPOSITION_ATTRIBUTE.toLowerCase() });

   /**
    * Constructor
    * @param aDataSource
    * @throws AeException
    */
   public AeHttpMultipartParser(AeServletRequestDataSource aDataSource) throws AeException
   {
      mDataSource = aDataSource;
      parse();
   }

   /**
    * @return the params
    */
   public AeOpenTreeMap getParams()
   {
      if ( mParams == null )
         mParams = new AeOpenTreeMap(new TreeSet());
      return mParams;
   }

   /**
    * @return the attachments
    */
   public ArrayList getAttachments()
   {
      if ( mAttachments == null )
         mAttachments = new ArrayList();
      return mAttachments;
   }

   /**
    * @throws AeException
    */
   public void parse() throws AeException
   {
      try
      {
         mAllParts = new MimeMultipart(mDataSource);
         if ( AeMimeUtil.isMultipartForm(mDataSource.getContentType()) )
            extractFormValues();
         else
            extractAttachments();
      }
      catch (Exception ex)
      {
         throw new AeException(ex);
      }

   }

   /**
    * @param aName
    */
   public String getParameter(String aName)
   {
      Iterator it = (Iterator)getParams().get(aName);
      if ( it == null || !it.hasNext() )
         return null;
      return it.next().toString();
   }

   /**
    * @param aName
    */
   public String[] getParameterValues(String aName)
   {
      List lst = new ArrayList();
      for (Iterator itr = (Iterator)getParams().get(aName); itr != null && itr.hasNext();)
      {
         lst.add(itr.next());
      }
      return (String[])lst.toArray(new String[lst.size()]);
   }

   /**
    * Returns an <code>Iterator</code> of <code>String</code>s of the names (keys) mapping to attachment
    * that were parsed.
    * @return iteration of the names mapping to attachment Content-Id
    */
   public Iterator getAttachmentNames()
   {
      return AeHttpUtil.getAttachmentNames(getAttachments());
   }

   /**
    * Convert body part headers to a map
    * @param aBodyPartHeaders
    * @return a map of normalized headers
    */
   private Map getBodyPartHeaders(Enumeration aBodyPartHeaders)
   {
      return getBodyPartHeaders(aBodyPartHeaders, null);
   }

   /**
    * Convert body part headers to a map, filter out header in the passed filter set
    * @param aBodyPartHeaders
    * @param aFilter the set of headers to be filtered out
    * @return
    */
   private Map getBodyPartHeaders(Enumeration aBodyPartHeaders, Set aFilter)
   {
      Map attachmentHeaders = new HashMap();
      while (aBodyPartHeaders.hasMoreElements())
      {
         Header header = (Header)aBodyPartHeaders.nextElement();
         attachmentHeaders.put(header.getName(), header.getValue());
      }

      return AeHttpUtil.normalizeHeaders(attachmentHeaders, aFilter);
   }
   
   /**
    * Extracts attachments form body parts
    * @throws AeException
    */
   private void extractAttachments() throws AeException
   {
      try
      {
         for (int i = 0; i < mAllParts.getCount(); ++i)
         {
            BodyPart bodyPart = mAllParts.getBodyPart(i);
            getAttachments().add(new AeWebServiceAttachment(bodyPart.getInputStream(), getBodyPartHeaders(bodyPart.getAllHeaders())));
         }
      }
      catch (Exception ex)
      {
         throw new AeException(ex);
      }
   }

   /**
    * Extracts form fields form multipart/form-data parts that conform with 
    * <a href="http://www.faqs.org/rfcs/rfc2388.html">RFC2388</a>
    * <p>
    * Form fields are converted to parameters and attachments, as follows:
    * </p>
    * <ul>
    * <li><b>Parameter</b>
    * <ol>
    * <li>A form field is treated as a parameter when the <code>Content-Disposition</code> header of the
    * part has a <code>name</code> attribute but no <code>filename</code> attribute.</li>
    * <li>The <code>Part</code> headers are copied verbose as parameter headers.</li>
    * </ol>
    * </li>
    * <li><b>Attachment</b>
    * <ol>
    * <li>A form field is treated as an attachment when the <code>Content-Disposition</code> header of the
    * part has both <code>name</code> and <code>filename</code> attributes.</li>
    * <li>All the <code>Part</code> headers, excluding the <code>Content-Disposition</code> header are
    * copied verbose as attachment headers.</li>
    * <li><code><i>Content-Disposition Transformations</i></code>
    * <ul>
    * <li>The <code>name</code> attribute is added as the attachment <code>Content-Id</code> header.</li>
    * <li>The <code>filename</code> attribute is added as the attachment <code>Content-Location</code>
    * header.</li>
    * <li>The body part length is added as the attachment <code>Content-Length</code> header.</li>
    * </ul>
    * </li>
    * </ol>
    * </li>
    * </ul>
    * @throws AeException
    */
   private void extractFormValues() throws AeException
   {
      try
      {
         int numElements = mAllParts.getCount();

         BodyPart bodyPart = null;
         String[] headervalues = null;
         String contentdisposition = null;

         Map dispositionFields = null;
         String paramName = null;
         String value = null;
         String fileName = null;

         int size = 0;

         for (int i = 0; i < numElements; ++i)
         {
            bodyPart = mAllParts.getBodyPart(i);

            headervalues = bodyPart.getHeader(AeMimeUtil.CONTENT_DISPOSITION_ATTRIBUTE);
            contentdisposition = (headervalues.length > 0) ? headervalues[0] : ""; //$NON-NLS-1$
            dispositionFields = AeHttpUtil.parseDispositionFields(contentdisposition);

            paramName = (String)dispositionFields.get("name"); //$NON-NLS-1$
            if ( bodyPart.getFileName() == null )
            {
               // No check for null because getContent can never return null.
               value = bodyPart.getContent().toString();

               // TODO (JB) HTTP add headers to a param according to new aeREST.xsd paramType
               getParams().put(paramName, value);
            }
            else
            {
               fileName = bodyPart.getFileName();
               size = bodyPart.getSize();
               Map headers = getBodyPartHeaders(bodyPart.getAllHeaders(), sExcludeDisposition);
               headers.put(AeMimeUtil.CONTENT_ID_ATTRIBUTE, paramName);
               headers.put(AeMimeUtil.CONTENT_LOCATION_ATTRIBUTE, fileName);
               headers.put(AeMimeUtil.CONTENT_LENGTH_ATTRIBUTE, Integer.toString(size));
               getAttachments().add(new AeWebServiceAttachment(bodyPart.getInputStream(), headers));
            }
         }
      }
      catch (Exception ex)
      {
         throw new AeException(ex);
      }
   }

}
