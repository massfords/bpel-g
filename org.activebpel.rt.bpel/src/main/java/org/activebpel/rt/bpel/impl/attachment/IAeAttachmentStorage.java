// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/attachment/IAeAttachmentStorage.java,v 1.4.4.1 2008/04/21 16:09:42 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.attachment;

import java.io.InputStream;
import java.util.Map;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;

/**
 * Attachment storage interface.
 */
public interface IAeAttachmentStorage
{
   /**
    * Associates the attachments in the given group with the given process.
    *
    * @param aGroupId
    * @param aProcessId
    */
   public void associateProcess(long aGroupId, long aProcessId) throws AeBusinessProcessException;
   
   /**
    * Cleans up unassociated attachments.
    * <p>
    * This can only be done at engine startup. 
    * </p>
    */
   public void cleanup() throws AeBusinessProcessException;
   
   /**
    * Creates a new attachment group and returns its id.
    *
    * @param aPlan
    * @return attachment group id
    */
   public long createAttachmentGroup(IAeProcessPlan aPlan) throws AeBusinessProcessException;

   /**
    * Returns the binary content of an attachment as an <code>InputStream</code>.
    * 
    * @param aAttachmentId
    * @return the binary stream
    */
   public InputStream getContent(long aAttachmentId) throws AeBusinessProcessException;

   /**
    * Returns an attachment's headers.
    *
    * @param aAttachmentId
    * @return <code>Map</code> of header name/value pairs
    */
   public Map getHeaders(long aAttachmentId) throws AeBusinessProcessException;
   
   /**
    * Stores an attachment (headers and content) and returns the attachment id.
    *
    * @param aGroupId
    * @param aInputStream
    * @param aHeaders
    * @return attachment id
    */
   public long storeAttachment(long aGroupId, InputStream aInputStream, Map aHeaders) throws AeBusinessProcessException;
   
   /**
    * Removes an attachment.
    * 
    * @param aAttachmentId
    */
   public void removeAttachment(long aAttachmentId) throws AeBusinessProcessException;
}
