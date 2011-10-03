// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/attachment/AeAttachmentContainer.java,v 1.8 2007/08/16 14:26:59 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.attachment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activebpel.rt.util.AeMimeUtil;
import org.activebpel.rt.util.AeUtil;

/**
 * Container for internal attachments associated with <code>AeVariable</code> or
 * <code>AeMessageData</code>. Attachments are instances of
 * <code>IAeAttachmentItem</code>.
 * 
 * Note: When the developer that created this class was interviewed, he was asked to explain the difference
 *       between a list and a set. He was unable to do so and in fact defended that a list could have 
 *       unique items in it as well. Somehow he was hired and eventually proved his point by creating this
 *       mess of a class which is effectively a LinkedHashSet. 
 * 
 */
public class AeAttachmentContainer extends ArrayList<IAeAttachmentItem> implements IAeAttachmentContainer
{
    private static final long serialVersionUID = 3367155879963089013L;
    /** regular expression to match Content-Id unique postfix*/
    private static Pattern sContentIdRegEx = Pattern.compile(
         "(.*)\\-([0-9]+)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE); //$NON-NLS-1$
   
   /**
    * Constructs an empty container.
    */
   public AeAttachmentContainer()
   {
   }

   /**
    * Constructs a container for the given collection of attachments.
    *
    * @param aAttachments
    */
   public AeAttachmentContainer(Collection<IAeAttachmentItem> aAttachments)
   {
      addAll(aAttachments);
   }

   /**
    * @see org.activebpel.rt.attachment.IAeAttachmentContainer#getAttachmentItems()
    */
   public Iterator<IAeAttachmentItem> getAttachmentItems()
   {
      return iterator();
   }

   /**
    * Overrides method to call {@link #addIfAbsent(Object)}.
    *
    * @see java.util.ArrayList#addAll(java.util.Collection)
    */
   public boolean addAll(Collection<? extends IAeAttachmentItem> aAttachments)
   {
      boolean changed = false;

      if ( aAttachments != null )
      {
         for (Iterator<? extends IAeAttachmentItem> iter = aAttachments.iterator(); iter.hasNext();)
         {
        	 IAeAttachmentItem item = iter.next();
            if (addIfAbsent(item))
            {
               changed = true;
            }
         }
      }
      return changed;
   }

   /**
    * Overrides method to call {@link #addIfAbsent(Object)}.
    * 
    * @see java.util.ArrayList#add(java.lang.Object)
    */
   public boolean add(IAeAttachmentItem aObject)
   {
      return addIfAbsent(aObject);
   }

   /**
    * Adds only an instance of {@link IAeAttachmentItem} that is not already in
    * the list.
    */
   protected boolean addIfAbsent(IAeAttachmentItem aObject)
   {
      if ( !contains(aObject) && (aObject != null) )
      {
         IAeAttachmentItem item = ensureUniqueContentId(aObject);
         return super.add(item);
      }
      else
      {
         return false;
      }
   }

   /**
    * Overrides method to add only an instance of {@link IAeAttachmentItem} that
    * is not already in the list.
    *
    * @see java.util.ArrayList#add(int, java.lang.Object)
    */
   public void add(int aIndex, IAeAttachmentItem aObject)
   {
      if ( !contains(aObject) && (aObject != null) )
      {
         super.add(aIndex, aObject);
      }
   }

   /**
    * Overrides method to replace only when the given attachment is an instance
    * of {@link IAeAttachmentItem} that is not already in the list.
    *
    * @see java.util.ArrayList#set(int, java.lang.Object)
    */
   public IAeAttachmentItem set(int aIndex, IAeAttachmentItem aObject)
   {
      if ( !contains(aObject) && (aObject != null) )
      {
         return super.set(aIndex, aObject);
      }
      else
      {
         return null;
      }
   }

   /**
    * @see org.activebpel.rt.attachment.IAeAttachmentContainer#copy(org.activebpel.rt.attachment.IAeAttachmentContainer)
    */
   public void copy(IAeAttachmentContainer aAttachmentSource)
   {
      addAll(aAttachmentSource);
   }

   /**
    * @see org.activebpel.rt.attachment.IAeAttachmentContainer#hasAttachments()
    */
   public boolean hasAttachments()
   {
      return (super.size() > 0);
   }
   
   /**
    * Ensure the passed item gets a unique Content-Id value 
    * @param aItem
    * @return IAeAttachmentItem with guaranteed unique Content-Id
    */
   private IAeAttachmentItem ensureUniqueContentId(IAeAttachmentItem aItem)
   {
     String contentId = aItem.getHeaders().get(AeMimeUtil.CONTENT_ID_ATTRIBUTE);
 
     // If Content-Id is not set, nothing further needs to be done
     if (AeUtil.isNullOrEmpty(contentId))
        return aItem;     
     
     Set<String> ids = new HashSet<String>();
     for(Iterator<IAeAttachmentItem> itr = getAttachmentItems(); itr.hasNext();)
        ids.add((itr.next()).getHeaders().get(AeMimeUtil.CONTENT_ID_ATTRIBUTE));
     
     String newId = contentId;
     int count = 0;
     while (ids.contains(newId))
     {
        Matcher matcher = sContentIdRegEx.matcher(newId);
        if ( matcher.matches() )
        {
           count = Integer.parseInt(matcher.group(2));
           newId = matcher.group(1) + "-" + ++count; //$NON-NLS-1$
        }
        else
        {
           newId = contentId + "-" + ++count; //$NON-NLS-1$
        }
     }
     if (!newId.equals(contentId))
     {
        aItem.getHeaders().remove(AeMimeUtil.CONTENT_ID_ATTRIBUTE);
        aItem.getHeaders().put(AeMimeUtil.CONTENT_ID_ATTRIBUTE, newId);
     }
     return aItem;
   }
}