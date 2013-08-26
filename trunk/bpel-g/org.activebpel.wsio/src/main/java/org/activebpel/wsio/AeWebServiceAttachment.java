// $Header: /Development/AEDevelopment/projects/org.activebpel.wsio/src/org/activebpel/wsio/AeWebServiceAttachment.java,v 1.4 2008/02/08 19:22:49 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.wsio;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Reference implementation of <code>IAeWebServiceAttachment</code>.
 */
public class AeWebServiceAttachment implements IAeWebServiceAttachment {
    /**
     * Attachment Mime headers. stored as Strings. key=mimeId
     */
    protected final Map<String, String> mMimeHeaders;

    /**
     * Attachment Data Content
     */
    final InputStream mDataContent;

    /**
     * Constructor.
     *
     * @param aAttachmentData
     * @param aMimeHeaders
     */
    public AeWebServiceAttachment(InputStream aAttachmentData, Map<String, String> aMimeHeaders) {
        mDataContent = aAttachmentData;
        mMimeHeaders = new HashMap<>(aMimeHeaders);
        addTimeStamp();
    }

    /**
     * @see org.activebpel.wsio.IAeWebServiceAttachment#getContent()
     */
    public InputStream getContent() {
        return mDataContent;
    }

    /**
     * @see org.activebpel.wsio.IAeWebServiceAttachment#getMimeHeaders()
     */
    public Map<String, String> getMimeHeaders() {
        return mMimeHeaders;
    }

    /**
     * @see org.activebpel.wsio.IAeWebServiceAttachment#getMimeType()
     */
    public String getMimeType() {
        return mMimeHeaders.get(AE_CONTENT_TYPE_MIME);
    }

    /**
     * @see org.activebpel.wsio.IAeWebServiceAttachment#getLocation()
     */
    public String getLocation() {
        return mMimeHeaders.get(AE_CONTENT_LOCATION_MIME);
    }

    /**
     * @see org.activebpel.wsio.IAeWebServiceAttachment#getContentId()
     */
    public String getContentId() {
        return mMimeHeaders.get(AE_CONTENT_ID_MIME);
    }

    /**
     * Adds create time stamp header to attachment headers map if it is not already present
     */
    private void addTimeStamp() {
        if (!getMimeHeaders().containsKey(ATTACHED_AT)) {
            getMimeHeaders().put(ATTACHED_AT, String.valueOf(System.currentTimeMillis()));
        }
    }
}
