//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/multipart/AeHttpMultipartRequestEntity.java,v 1.2.4.1 2008/04/21 16:15:52 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http.multipart;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeMimeUtil;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.EncodingUtil;

/**
 * Implements a request entity suitable for an HTTP multipart POST method using the apache commons http
 * client.
 * <p>
 * Extends the functionality provided by
 * {@link org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity MultipartRequestEntity} to
 * support sub types other than <code>form-data</code>, For example <code>multipart/related</code> posts used by 
 * <a href="http://code.google.com/apis/documents/developers_guide_protocol.html">Google Documents List Data API</a>
 * see <a href="http://code.google.com/apis/documents/developers_guide_protocol.html#UploadingWMetadata">Uploading with metadata</a>
 * </p>
 * The HTTP multipart POST method is defined in section 3.3 of <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC1867</a>:
 */
public class AeHttpMultipartRequestEntity extends MultipartRequestEntity
{

   private String mMultipartContentType;


   /**
    * Constructor
    * @param aParts
    * @param aParams
    * @param aMultipartContentType
    * @throws AeException
    */
   public AeHttpMultipartRequestEntity(Part[] aParts, HttpMethodParams aParams, String aMultipartContentType) throws AeException
   {
      super(aParts, aParams);
      mMultipartContentType = AeMimeUtil.formMultipartMime(aMultipartContentType);
   }

   /**
    * @see org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity#getContentType()
    */
   public String getContentType()
   {
      StringBuffer buffer = new StringBuffer(mMultipartContentType);
      buffer.append("; boundary="); //$NON-NLS-1$
      buffer.append(EncodingUtil.getAsciiString(getMultipartBoundary()));
      return buffer.toString();
   }
}
