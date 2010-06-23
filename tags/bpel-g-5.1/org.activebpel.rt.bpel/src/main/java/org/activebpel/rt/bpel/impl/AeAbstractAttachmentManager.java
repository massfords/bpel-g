// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeAbstractAttachmentManager.java,v 1.7 2007/07/26 20:50:23 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.attachment.AeAttachmentContainer;
import org.activebpel.rt.attachment.IAeAttachmentContainer;
import org.activebpel.rt.attachment.IAeAttachmentItem;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.attachment.AeStoredAttachmentItem;
import org.activebpel.rt.bpel.impl.attachment.AeStreamAttachmentItem;
import org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage;
import org.activebpel.rt.util.AeBlobInputStream;
import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.wsio.AeWebServiceAttachment;
import org.activebpel.wsio.IAeWebServiceAttachment;

/**
 * Abstract base class for attachment managers.
 */
public abstract class AeAbstractAttachmentManager extends AeManagerAdapter implements IAeAttachmentManager
{
   /** A flag indicating whether we are in debug mode. */
   private boolean mDebug = false;

   /**
    * Constructs the attachment manager with the given engine configuration.
    * @param aConfig The engine configuration for this manager.
    */
   public AeAbstractAttachmentManager(Map aConfig)
   {
      mDebug = Boolean.valueOf((String)aConfig.get("Debug")).booleanValue(); //$NON-NLS-1$

      // Pass attachment conversion to the data converter
      AeDataConverter.setAttachmentManager(this);
   }

   /**
    * Returns the {@link IAeAttachmentStorage} instance that stores attachments
    * for this manager.
    */
   abstract protected IAeAttachmentStorage getStorage() throws AeBusinessProcessException;

   /**
    * Writes out a debug message to standard out.
    * @param aMessage The debug message.
    */
   protected void debug(String aMessage)
   {
      if ( mDebug )
      {
         System.out.println("[AttachmentManager] " + aMessage); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeManager#create()
    */
   public void create() throws Exception
   {
      // Ensure that the storage has been initialized at this point
      getStorage();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeManagerAdapter#prepareToStart()
    */
   public void prepareToStart() throws Exception
   {
      // Clean up unassociated attachments.
      getStorage().cleanup();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAttachmentManager#associateProcess(org.activebpel.rt.attachment.IAeAttachmentContainer,
    *      long)
    */
   public void associateProcess(IAeAttachmentContainer aContainer, long aProcessId)
         throws AeBusinessProcessException
   {
      if ( (aContainer != null) && aContainer.hasAttachments() )
      {
         if ( !isStored(aContainer) )
         {
            throw new IllegalArgumentException(AeMessages
                  .getString("AeAbstractAttachmentManager.ERROR_AssociateProcess")); //$NON-NLS-1$
         }

         setProcessId(aContainer, aProcessId);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.AeAbstractAttachmentManager#bpel2wsio(org.activebpel.rt.attachment.IAeAttachmentContainer)
    */
   public List bpel2wsio(IAeAttachmentContainer aBpelContainer) throws AeBusinessProcessException
   {
      List wsAttachments = new ArrayList(aBpelContainer.size());

      // An AeAttachmentContainer can have 0..n attachment parts
      for (Iterator attachmentItr = aBpelContainer.getAttachmentItems(); attachmentItr.hasNext();)
      {
         IAeAttachmentItem attachment = (IAeAttachmentItem)attachmentItr.next();
         InputStream content = deserialize(attachment.getAttachmentId());
         Map headers = attachment.getHeaders();

         // create a wsio attachment with headers and content
         wsAttachments.add(new AeWebServiceAttachment(content, headers));
      }

      return wsAttachments;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAttachmentManager#deserialize(long)
    */
   public InputStream deserialize(long aAttachmentId) throws AeBusinessProcessException
   {
      return getStorage().getContent(aAttachmentId);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.IAeAttachmentManager#removeAttachment(long)
    */
   public void removeAttachment(long aAttachmentId) throws AeBusinessProcessException
   {
      getStorage().removeAttachment(aAttachmentId);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAttachmentManager#storeAttachments(org.activebpel.rt.attachment.IAeAttachmentContainer,
    *      org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public void storeAttachments(IAeAttachmentContainer aContainer, IAeProcessPlan aPlan)
         throws AeBusinessProcessException
   {
      storeAttachments(aContainer, aPlan, IAeBusinessProcess.NULL_PROCESS_ID);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAttachmentManager#storeAttachments(org.activebpel.rt.attachment.IAeAttachmentContainer,
    *      org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public void storeAttachments(IAeAttachmentContainer aContainer, IAeProcessPlan aPlan, long aProcessId)
         throws AeBusinessProcessException
   {
      if ( (aContainer != null) && aContainer.hasAttachments() )
      {
         storeAttachmentContainer(aContainer, aPlan);

         if ( aProcessId != IAeBusinessProcess.NULL_PROCESS_ID )
         {
            setProcessId(aContainer, aProcessId);
         }
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeAttachmentManager#wsio2bpel(java.util.List)
    */
   public IAeAttachmentContainer wsio2bpel(List aWsioAttachments) throws AeBusinessProcessException
   {
      if ( aWsioAttachments == null || aWsioAttachments.size() == 0 )
      {
         // Don't activate logic when there are no attachments
         return null;
      }

      IAeAttachmentContainer container = new AeAttachmentContainer();
      try
      {
         for (Iterator i = aWsioAttachments.iterator(); i.hasNext();)
         {
            IAeWebServiceAttachment wsAttachment = (IAeWebServiceAttachment)i.next();
            AeBlobInputStream content = new AeBlobInputStream(wsAttachment.getContent());

            Map headers = wsAttachment.getMimeHeaders();
            // Add attribute content size as an attribute of the attachment
            headers.put(AeMimeUtil.AE_SIZE_ATTRIBUTE, String.valueOf(content.length()));

            container.add(new AeStreamAttachmentItem(content, headers));
         }
      }
      catch (FileNotFoundException ex)
      {
         throw new AeBusinessProcessException(ex.getLocalizedMessage());
      }
      catch (AeException ex)
      {
         throw new AeBusinessProcessException(ex.getLocalizedMessage());
      }

      return container;
   }

   /**
    * Returns the group id from the first attachment in the given container.
    * @param aContainer
    * @throws IndexOutOfBoundsException if the container is empty.
    * @throws IllegalStateException if the container contains attachments from multiple groups.
    */
   protected long getGroupId(IAeAttachmentContainer aContainer)
   {
      long groupId = ((AeStoredAttachmentItem)aContainer.get(0)).getGroupId();

      // Validate that all the attachments belong to the same group.
      for (Iterator i = aContainer.iterator(); i.hasNext();)
      {
         AeStoredAttachmentItem attachment = (AeStoredAttachmentItem)i.next();
         if ( attachment.getGroupId() != groupId )
         {
            throw new IllegalStateException(AeMessages
                  .getString("AeAbstractAttachmentManager.ERROR_MultipleGroups")); //$NON-NLS-1$
         }
      }

      return groupId;
   }

   /**
    * Returns the process id from the first attachment in the given container.
    * @throws IndexOutOfBoundsException if the container is empty.
    * @throws IllegalStateException if the container contains attachments from multiple processes.
    */
   protected long getProcessId(IAeAttachmentContainer aContainer)
   {
      long processId = ((IAeAttachmentItem)aContainer.get(0)).getProcessId();

      // Validate that all the attachments belong to the same process.
      for (Iterator i = aContainer.iterator(); i.hasNext();)
      {
         IAeAttachmentItem attachment = (IAeAttachmentItem)i.next();
         if ( attachment.getProcessId() != processId )
         {
            throw new IllegalStateException(AeMessages
                  .getString("AeAbstractAttachmentManager.ERROR_MultipleProcesses")); //$NON-NLS-1$
         }
      }

      return processId;
   }

   /**
    * Returns <code>true</code> if and only if all the attachments in the given container are stored.
    * @param aContainer
    */
   protected boolean isStored(IAeAttachmentContainer aContainer)
   {
      for (Iterator i = aContainer.iterator(); i.hasNext();)
      {
         if ( !(i.next() instanceof AeStoredAttachmentItem) )
         {
            return false;
         }
      }

      return true;
   }

   /**
    * Sets the process id associated with the container and all of its attachments.
    */
   protected void setProcessId(IAeAttachmentContainer aContainer, long aProcessId)
         throws AeBusinessProcessException
   {
      // Associate to the process in storage.
      long groupId = getGroupId(aContainer);
      getStorage().associateProcess(groupId, aProcessId);

      // Set the associated process id for all attachments in the container.
      for (Iterator i = aContainer.iterator(); i.hasNext();)
      {
         AeStoredAttachmentItem attachment = (AeStoredAttachmentItem)i.next();
         attachment.setProcessId(aProcessId);
      }
   }

   /**
    * Stores an attachment.
    * @param aGroupId
    * @param aInputStream
    * @param aHeaders
    * @throws AeBusinessProcessException
    */
   protected IAeAttachmentItem storeAttachment(long aGroupId, InputStream aInputStream, Map aHeaders)
         throws AeBusinessProcessException
   {
      long attachmentId = getStorage().storeAttachment(aGroupId, aInputStream, aHeaders);

      AeStoredAttachmentItem attachment = new AeStoredAttachmentItem();
      attachment.setAttachmentId(attachmentId);
      attachment.setGroupId(aGroupId);
      attachment.setHeaders(aHeaders);

      return attachment;
   }

   /**
    * Stores the attachments in the given container possibly in a way determined by the given process plan.
    * @param aContainer
    * @param aPlan
    * @throws AeBusinessProcessException
    */
   protected void storeAttachmentContainer(IAeAttachmentContainer aContainer, IAeProcessPlan aPlan)
         throws AeBusinessProcessException
   {
      long groupId = getStorage().createAttachmentGroup(aPlan);
      List storedAttachments = new ArrayList(aContainer.size());

      for (Iterator i = aContainer.iterator(); i.hasNext();)
      {
         IAeAttachmentItem attachment = (IAeAttachmentItem)i.next();
         InputStream content;

         if ( attachment instanceof AeStreamAttachmentItem )
         {
            // Not yet stored.
            content = ((AeStreamAttachmentItem)attachment).getContent();
         }
         else
         {
            // Already stored for a different process
            // TODO: (JB) future enhancements should optimize the movement of attachments between process
            // boundaries
            content = deserialize(attachment.getAttachmentId());
         }

         Map headers = attachment.getHeaders();

         IAeAttachmentItem storedAttachment = storeAttachment(groupId, content, headers);
         storedAttachments.add(storedAttachment);
      }

      aContainer.clear();
      aContainer.addAll(storedAttachments);
   }
}
