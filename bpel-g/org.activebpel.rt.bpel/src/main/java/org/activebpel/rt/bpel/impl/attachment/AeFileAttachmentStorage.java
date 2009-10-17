// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/attachment/AeFileAttachmentStorage.java,v 1.4 2008/02/17 21:37:11 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.attachment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.util.AeLongMap;
import org.activebpel.rt.util.AeUtil;

/**
 * Implements an attachment storage that stores the contents of attachments in
 * temporary files.
 */
public class AeFileAttachmentStorage implements IAeAttachmentStorage
{
   /** Counter for attachment ids. */
   private AeLongCounter mAttachmentIdCounter = new AeLongCounter();

   /** Maps attachment ids to {@link AeAttachmentInfo} instances. */
   private AeLongMap mAttachmentInfoMap = new AeLongMap(Collections.synchronizedMap(new HashMap()));

   /** Counter for attachment group ids. */
   private AeLongCounter mGroupIdCounter = new AeLongCounter();

   /** Maps attachment group ids to lists of {@link AeAttachmentInfo} instances. */
   private AeLongMap mGroupInfosMap = new AeLongMap(Collections.synchronizedMap(new HashMap()));

   /** Maps process ids to lists of {@link AeAttachmentInfo} instances. */
   private AeLongMap mProcessInfosMap = new AeLongMap(Collections.synchronizedMap(new HashMap()));

   /**
    * Overrides method to move the {@link AeAttachmentInfo} instances associated
    * with an attachment group to the list of {@link AeAttachmentInfo} instances
    * associated with a process.
    *
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#associateProcess(long, long)
    */
   public void associateProcess(long aAttachmentGroupId, long aProcessId) throws AeBusinessProcessException
   {
      // Remove the group's entry from the group map.
      List groupInfos = (List) getGroupInfosMap().remove(aAttachmentGroupId);
      if (groupInfos != null)
      {
         List processInfos = getProcessInfos(aProcessId);
   
         synchronized (processInfos)
         {
            // Add the group's attachments to the process's attachments.
            processInfos.addAll(groupInfos);
         }
      }
   }

   /**
    * Overrides method to remove any pre-existing temporary attachment files
    * from previous engine executions.
    * 
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#cleanup()
    */
   public void cleanup() throws AeBusinessProcessException
   {
      // Get a listing of temporary attachment files.
      File[] tempFiles = AeAttachmentFile.listAttachmentFiles();

      // Construct the set of files to delete.
      Set deleteFiles = new HashSet(Arrays.asList(tempFiles));

      // Don't delete files that we are actively managing.
      for (Iterator i = getAttachmentInfoMap().values().iterator(); i.hasNext(); )
      {
         AeAttachmentInfo info = (AeAttachmentInfo) i.next();
         deleteFiles.remove(info.mAttachmentFile);
      }

      // Delete any remaining temporary attachment files.
      for (Iterator i = deleteFiles.iterator(); i.hasNext(); )
      {
         ((File) i.next()).delete();
      }
   }

   /**
    * Overrides method to create a new attachment group represented by an entry
    * in the attachment groups map.
    * 
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#createAttachmentGroup(org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public long createAttachmentGroup(IAeProcessPlan aPlan) throws AeBusinessProcessException
   {
      long groupId = getNextGroupId();

      getGroupInfosMap().put(groupId, new ArrayList());

      return groupId;
   }

   /**
    * Overrides method to return a new input stream on the temporary file for
    * the given attachment.
    *
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#getContent(long)
    */
   public InputStream getContent(long aAttachmentId) throws AeBusinessProcessException
   {
      AeAttachmentInfo info = getAttachmentInfo(aAttachmentId);

      try
      {
         return new AeAttachmentFileInputStream(info.mAttachmentFile);
      }
      catch (FileNotFoundException e)
      {
         throw new AeBusinessProcessException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * Overrides method to return the headers for the given attachment.
    * 
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#getHeaders(long)
    */
   public Map getHeaders(long aAttachmentId) throws AeBusinessProcessException
   {
      AeAttachmentInfo info = getAttachmentInfo(aAttachmentId);
      return info.mHeaders;
   }

   /**
    * Removes the attachments associated with the given process, including the
    * temporary files for those attachments.
    *
    * @param aProcessId
    */
   public void removeProcess(long aProcessId)
   {
      // Remove the process's entry from the process map.
      List processInfos = (List) getProcessInfosMap().remove(aProcessId);
      if (processInfos != null)
      {
         // Remove the attachments from the attachment map.
         synchronized (getAttachmentInfoMap().synchObject())
         {
            for (Iterator i = processInfos.iterator(); i.hasNext(); )
            {
               AeAttachmentInfo info = (AeAttachmentInfo) i.next();
               getAttachmentInfoMap().remove(info.mAttachmentId);
            }
         }
         
         // Delete the associated temporary files. Do this outside of the
         // synchronized block to avoid blocking other threads during these
         // relatively expensive I/O operations.
         for (Iterator i = processInfos.iterator(); i.hasNext(); )
         {
            AeAttachmentInfo info = (AeAttachmentInfo) i.next();
            info.mAttachmentFile.delete();
         }
      }
   }

   /**
    * Overrides method to store an attachment stream into a temporary file and
    * to create an {@link AeAttachmentInfo} instance associated with the given
    * group.
    * 
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#storeAttachment(long, java.io.InputStream, java.util.Map)
    */
   public long storeAttachment(long aAttachmentGroupId, InputStream aInputStream, Map aHeaders) throws AeBusinessProcessException
   {
      // Verify that the specified group exists.
      List groupInfos = (List) getGroupInfosMap().get(aAttachmentGroupId);
      if (groupInfos == null)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeFileAttachmentStorage.ERROR_IllegalGroupId", String.valueOf(aAttachmentGroupId))); //$NON-NLS-1$
      }

      long attachmentId = getNextAttachmentId();
      AeAttachmentFile attachmentFile = new AeAttachmentFile(aInputStream);
      AeAttachmentInfo info = new AeAttachmentInfo(attachmentId, attachmentFile, aHeaders);

      // Save the attachment info indexed by attachment id.
      getAttachmentInfoMap().put(attachmentId, info);

      // Add the attachment info to the group as well.
      groupInfos.add(info);

      return attachmentId;
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.attachment.IAeAttachmentStorage#removeAttachment(long)
    */
   public void removeAttachment(long aAttachmentId) throws AeBusinessProcessException
   {
      AeAttachmentInfo info = getAttachmentInfo(aAttachmentId);
      info.mAttachmentFile.delete();
      getAttachmentInfoMap().remove(aAttachmentId);
   }

   /**
    * Returns the {@link AeAttachmentInfo} instance for the given attachment.
    *
    * @param aAttachmentId
    * @throws AeBusinessProcessException
    */
   protected AeAttachmentInfo getAttachmentInfo(long aAttachmentId) throws AeBusinessProcessException
   {
      AeAttachmentInfo info = (AeAttachmentInfo) getAttachmentInfoMap().get(aAttachmentId);
      if (info == null)
      {
         throw new AeBusinessProcessException(AeMessages.format("AeFileAttachmentStorage.ERROR_IllegalAttachmentId", String.valueOf(aAttachmentId))); //$NON-NLS-1$
      }

      return info;
   }

   /**
    * Returns the map from attachment ids to {@link AeAttachmentInfo} instances.
    */
   protected AeLongMap getAttachmentInfoMap()
   {
      return mAttachmentInfoMap;
   }

   /**
    * Returns the map from group ids to lists of {@link AeAttachmentInfo}
    * instances.
    */
   protected AeLongMap getGroupInfosMap()
   {
      return mGroupInfosMap;
   }

   /**
    * Returns the next attachment id.
    */
   protected long getNextAttachmentId()
   {
      return mAttachmentIdCounter.getNextValue();
   }

   /**
    * Returns the next attachment group id.
    */
   protected long getNextGroupId()
   {
      return mGroupIdCounter.getNextValue();
   }

   /**
    * Returns the map from process ids to lists of {@link AeAttachmentInfo}
    * instances.
    */
   protected AeLongMap getProcessInfosMap()
   {
      return mProcessInfosMap;
   }

   /**
    * Returns the list of {@link AeAttachmentInfo} instances associated with the
    * given process.
    *
    * @param aProcessId
    */
   protected List getProcessInfos(long aProcessId)
   {
      synchronized (getProcessInfosMap().synchObject())
      {
         List processInfos = (List) getProcessInfosMap().get(aProcessId);
         if (processInfos == null)
         {
            // If an entry for the process doesn't exist yet, then create one.
            processInfos = new ArrayList();
            getProcessInfosMap().put(aProcessId, processInfos);
         }

         return processInfos;
      }
   }

   /**
    * Convenience method that calls {@link AeUtil#listTempFiles(String, String)}
    * and converts <code>AeException</code> to
    * <code>AeBusinessProcessException</code>.
    * @param aPrefix
    * @param aSuffix
    */
   protected static File[] listTempFiles(String aPrefix, String aSuffix) throws AeBusinessProcessException
   {
      try
      {
         return AeUtil.listTempFiles(aPrefix, aSuffix);
      }
      catch (AeException e)
      {
         throw new AeBusinessProcessException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * Defines a simple struct of attachment information.
    */
   protected static class AeAttachmentInfo
   {
      public final long mAttachmentId;
      public final AeAttachmentFile mAttachmentFile;
      public final Map mHeaders;

      public AeAttachmentInfo(long aAttachmentId, AeAttachmentFile aAttachmentFile, Map aHeaders)
      {
         mAttachmentId = aAttachmentId;
         mAttachmentFile = aAttachmentFile;
         mHeaders = aHeaders;
      }
   }

   /**
    * Implements a simple counter.
    */
   protected static class AeLongCounter
   {
      /** Next available counter value. */
      private long mNextValue = 1L;

      /**
       * Default constructor.
       */
      public AeLongCounter()
      {
      }
      
      /**
       * Returns next available counter value.
       */
      public synchronized long getNextValue()
      {
         return mNextValue++;
      }
   }
}
