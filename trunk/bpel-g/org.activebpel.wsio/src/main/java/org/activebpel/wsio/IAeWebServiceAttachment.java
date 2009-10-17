//$Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/IAeWebServiceAttachment.java,v 1.4 2008/02/13 07:06:41 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio;

import java.io.InputStream;
import java.util.Map;

/**
 * WSIO Attachment item interface
 */
public interface IAeWebServiceAttachment 
{
   
   /** key content type Mime header */
   public static final String AE_CONTENT_TYPE_MIME = "Content-Type"; //$NON-NLS-1$

   /** content location type Mime header */
   public static final String AE_CONTENT_LOCATION_MIME = "Content-Location"; //$NON-NLS-1$
   
   /** content id Mime header */
   public static final String AE_CONTENT_ID_MIME = "Content-Id"; //$NON-NLS-1$

   /** Attachment creation timestamp constatnt header */
   public static final String ATTACHED_AT = "Attachment-Created-At"; //$NON-NLS-1$
   
   /** Optional principal or username who added the attachment */
   public static final String AE_ATTACHED_BY = "Attached-By"; //$NON-NLS-1$
   
   /**
    * Returns the type of attachment this data is representing. 
    */
   public String getMimeType();
   
   /**
    *returns the mime header that represents the content location identification
    */
   public String getLocation();
   
   /**
    *returns the mime header that represents the content identifier
    */
   public String getContentId();
   
   /**
    * returns all mime headers associated with attachment
    */
   public Map getMimeHeaders();
   
   /**
    * Get the attachment data.
    */
   public InputStream getContent();
}
