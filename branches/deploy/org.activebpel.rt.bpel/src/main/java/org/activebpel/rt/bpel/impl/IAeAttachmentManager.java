// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeAttachmentManager.java,v 1.6 2007/12/26 17:35:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.io.InputStream;
import java.util.List;

import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.wsio.IAeWebServiceMessageData;

/**
 * A generic attachment manager that provides a means of serializing and converting attachments formats. 
 */
public interface IAeAttachmentManager extends IAeManager
{
   /**
    * Associates the attachments in the given attachment container with the
    * given process.
    *
    * @param aContainer
    * @param aProcessId
    */
   public void associateProcess(IAeAttachmentContainer aContainer, long aProcessId) throws AeBusinessProcessException;
   
   /**
    * Convert attachment items from {@link IAeAttachmentContainer} to {@link IAeWebServiceMessageData}
    * format.
    *
    * @param aBpelContainer
    * @return list of wsio attachments in IAeWebServiceAttachment format
    */
   public List bpel2wsio(IAeAttachmentContainer aBpelContainer) throws AeBusinessProcessException;

   /**
    * Convert attachment items from {@link IAeWebServiceMessageData} to {@link IAeAttachmentContainer}
    * format.
    *
    * @param aWsioAttachments
    * @return IAeAttachmenContainer
    */
   public IAeAttachmentContainer wsio2bpel(List aWsioAttachments) throws AeBusinessProcessException;

   /**
    * Deserializes a stored attachment to a stream.
    *
    * @param aAttachmentId
    * @return attachment binary input stream
    */
   public InputStream deserialize(long aAttachmentId) throws AeBusinessProcessException;

   /**
    * Notifies the attachment manager that the engine has finished populating a
    * synchronous response for the given process.
    *
    * @param aProcessId
    */
   public void responseFilled(long aProcessId);

   /**
    * Notifies the attachment manager that the engine is waiting to populate a
    * synchronous response for the given process. Since the response may include
    * attachments, the attachment manager must hold onto any attachments for
    * the process until the engine calls {@link #responseFilled(long)}.
    *
    * @param aProcessId
    */
   public void responsePending(long aProcessId);

   /**
    * Stores the attachments in the given container.
    *
    * @param aContainer
    * @param aPlan May determine whether the attachments are stored locally or persistently
    * @throws AeBusinessProcessException
    */
   public void storeAttachments(IAeAttachmentContainer aContainer, IAeProcessPlan aPlan) throws AeBusinessProcessException;

   /**
    * Stores the attachments in the given container and associates the
    * attachments with the given process (unless <code>aProcessId</code> is
    * {@link IAeBusinessProcess#NULL_PROCESS_ID}).
    *
    * @param aContainer
    * @param aPlan May determine whether the attachments are stored locally or persistently
    * @param aProcessId
    * @throws AeBusinessProcessException
    */
   public void storeAttachments(IAeAttachmentContainer aContainer, IAeProcessPlan aPlan, long aProcessId) throws AeBusinessProcessException;
   
   /**
    * Removes an attachment.
    * @param aAttachmentId
    * @throws AeBusinessProcessException
    */
   public void removeAttachment(long aAttachmentId) throws AeBusinessProcessException;
}
