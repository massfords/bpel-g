//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/upload/AeNewAttachmentUploader.java,v 1.3 2008/02/17 21:43:07 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web.upload;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpeladmin.war.AeMessages;
import org.activebpel.rt.bpeladmin.war.web.processview.attachment.AeAttachmentBeanBase;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;

/**
 * Attachment uploader for BpelAdmin.
 */
@SuppressWarnings("deprecation")
public class AeNewAttachmentUploader extends AeAttachmentBeanBase implements IAeFileUploader {

    /**
     * Container for any custom query string params
     */
    private Map mParams;

    /**
     * the attachment file name
     */
    private String mFileName;

    /**
     * The qualified attachment file path
     */
    private String mFilePath;

    /**
     * xml reperesentation of the attachment attributes
     * <p/>
     * <pre>
     * &lt;attributes&gt;
     *    &lt;attribute name=&quot;Content-Id&quot; value=&quot;Some value&quot;\&gt;
     *    &lt;attribute name=&quot;Content-Location&quot; value=&quot;Some location&quot;\&gt;
     * &lt;/attributes&gt;
     * </pre>
     */
    private String mAttributeXml;

    /**
     * attachment content mime type
     */
    private String mContentType;

    /**
     * Content stream of the attachment file
     */
    private InputStream mContent;

    /**
     * Set any custom params on the uploader.
     */
    public void configure(Map aParams) {
        mParams = aParams;
    }

    /**
     * @see org.activebpel.rt.bpeladmin.war.web.upload.IAeFileUploader#isMultiPartContent(javax.servlet.http.HttpServletRequest)
     */
    public boolean isMultiPartContent(HttpServletRequest aRequest) {
        return FileUpload.isMultipartContent(aRequest);
    }

    /**
     * @see org.activebpel.rt.bpeladmin.war.web.upload.IAeFileUploader#uploadFile(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void uploadFile(HttpServletRequest aRequest, HttpServletResponse aResponse) {
        try {
            FileUploadBase upload = createFileUpload();

            // Iterate through all of the request items.
            List items = upload.parseRequest(aRequest);
            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                // If it's not a Form Field, then it's an upload.
                if (!item.isFormField()) {
                    if (item.getSize() <= 0) {
                        throw new AeException(AeMessages.format("AeNewAttachmentUploader.InvalidFile", new Object[]{getFilePath()})); //$NON-NLS-1$
                    }

                    mFileName = item.getName();
                    mContentType = AeMimeUtil.getContentType(mFileName, item.getContentType());
                    mContent = item.getInputStream();

                } else if (item.isFormField()) {
                    if ("pid".equals(item.getFieldName())) //$NON-NLS-1$
                    {
                        setPid(item.getString());
                    } else if ("path".equals(item.getFieldName())) //$NON-NLS-1$
                    {
                        setPath(item.getString());
                    } else if ("attributeXml".equals(item.getFieldName())) //$NON-NLS-1$
                    {
                        mAttributeXml = item.getString();
                    } else if ("filePath".equals(item.getFieldName())) //$NON-NLS-1$
                    {
                        mFilePath = item.getString();
                    }
                }
            }
        } catch (Throwable t) {
            setError(t);
        }

    }

    /**
     * Creates an instance of <code>org.apache.commons.fileupload.DiskFileUpload</code>.
     */
    protected FileUploadBase createFileUpload() {
        DiskFileUpload diskUpload = new DiskFileUpload();

        if (hasParams()) {
            Integer thresholdSize = getThresholdSize();
            if (thresholdSize != null) {
                diskUpload.setSizeThreshold(thresholdSize);
            }

            Long maxSize = getMaxUploadSize();
            if (maxSize != null) {
                diskUpload.setSizeMax(maxSize);
            }
        }
        return diskUpload;
    }

    /**
     * Return the threshold size (in bytes) or null if none was configured.
     */
    protected Integer getThresholdSize() {
        Integer value = null;
        String stringValue = (String) getParams().get(THRESHOLD_BYTES);
        if (AeUtil.notNullOrEmpty(stringValue)) {
            value = new Integer(stringValue);
        }
        return value;
    }

    /**
     * Return the max upload size or null if none was configured.
     */
    protected Long getMaxUploadSize() {
        Long value = null;
        String stringValue = (String) getParams().get(MAX_SIZE);
        if (AeUtil.notNullOrEmpty(stringValue)) {
            value = new Long(stringValue);
        }
        return value;
    }

    /**
     * Return true if the uploader has been configured with custom params.
     */
    protected boolean hasParams() {
        return getParams() != null;
    }

    /**
     * @return Returns the params.
     */
    protected Map getParams() {
        return mParams;
    }

    /**
     * @return xml reperesentation of the attachment attributes
     */
    public String getAttributeXml() {
        return mAttributeXml;
    }

    /**
     * @return content type of the attachment
     */
    public String getContentType() {
        return mContentType;
    }

    /**
     * @return InputStream of the attachment
     */
    public InputStream getContent() {
        return mContent;
    }

    /**
     * @return xml reperesentation of the attachment attributes
     */
    public String getFileName() {
        return mFileName == null ? "unknown" : mFileName; //$NON-NLS-1$
    }

    /**
     * @return xml reperesentation of the attachment attributes
     */
    public String getFilePath() {
        return mFilePath == null ? "unknown" : mFilePath; //$NON-NLS-1$
    }
}
