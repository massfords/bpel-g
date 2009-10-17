//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/AeHttpPartFactory.java,v 1.3 2008/02/17 21:54:15 MFord Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.IAeWebServiceAttachment;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.PartBase;

/**
 * 
 */
public class AeHttpPartFactory
{

   public final static String DEFAULT_NAME = "payload"; //$NON-NLS-1$

   public final static String DEFAULT_ATTACHMENT_NAME = "attachment"; //$NON-NLS-1$

   /**
    * @param aWsioAttachment
    * @param aIsFormData
    * @throws AeException
    */
   public static PartBase createPart(IAeWebServiceAttachment aWsioAttachment, boolean aIsFormData) throws AeException
   {
      String name = (String)aWsioAttachment.getContentId();
      name = AeUtil.isNullOrEmpty(name) ? DEFAULT_ATTACHMENT_NAME : name;
      if ( aIsFormData )
      {
         // multipart/form-data is a special case that has a disposition header to describe a file. This is
         // directly supported by HttpClint
         AeAttachmentPartSource attachmentPartSource = new AeAttachmentPartSource(AeMimeUtil.getFileName(aWsioAttachment.getMimeHeaders(), name),
               aWsioAttachment.getContent(), AeUtil.getBigNumeric(((String)aWsioAttachment.getMimeHeaders().get(AeMimeUtil.AE_SIZE_ATTRIBUTE))));
         return new FilePart(name, attachmentPartSource, aWsioAttachment.getMimeType(), AeUTF8Util.UTF8_ENCODING);
      }
      else
         // all other multipart sub types are treated as attachments
         return new AeHttpAttachmentPart(name, aWsioAttachment);

   }
   
   /**
    * Constructor for multipart/form-data simple text fields
    * @param aFieldName
    * @param aContent
    * @throws AeException
    */
   public static PartBase createPart(String aFieldName, String aContent) throws AeException
   {
      String name = AeUtil.isNullOrEmpty(aFieldName) ? DEFAULT_NAME : aFieldName;
      return new AeHttpStringPart(name , aContent);
   }
  
   /**
    * @param aHeaders
    * @param aContentType
    * @param aContent
    * @param aIsFormData
    * @throws AeException
    */
   public static PartBase createPart(Map aHeaders, String aContentType, String aContent, boolean aIsFormData) throws AeException
   {
      String name = (String)aHeaders.get(AeMimeUtil.CONTENT_ID_ATTRIBUTE);
      name = AeUtil.isNullOrEmpty(name) ? DEFAULT_NAME : name;

      if ( aIsFormData )
      {
         // multipart/form-data is a special case that has a disposition header to describe the content as a
         // form parameter, and is directly supported by HttpClient.
         Map tmap = new HashMap();
         tmap.put(AeMimeUtil.CONTENT_TYPE_ATTRIBUTE, aContentType);
         String fileName = AeMimeUtil.getFileName(tmap,name,"txt"); //$NON-NLS-1$
         return new FilePart(name, new ByteArrayPartSource(fileName,aContent.getBytes()),aContentType, AeUTF8Util.UTF8_ENCODING);
      }
      else
         return new AeHttpStringPart(name, aHeaders == null ? Collections.EMPTY_MAP : aHeaders, aContentType, aContent);

   }
}
