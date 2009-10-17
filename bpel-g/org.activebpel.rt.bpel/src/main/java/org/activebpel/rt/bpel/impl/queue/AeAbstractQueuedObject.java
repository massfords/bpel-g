// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/queue/AeAbstractQueuedObject.java,v 1.4 2006/07/26 21:47:12 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.queue;

import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;
import org.activebpel.rt.util.AeUtil;

/**
 * Base class for objects that get queued in the engine.
 */
abstract public class AeAbstractQueuedObject
{
   /** The partnerLink + operation key. */
   private AePartnerLinkOpKey mPartnerLinkOperationKey;

   /**
    * Full constructor for queue object
    * 
    * @param aPartnerLinkOpKey
    */
   public AeAbstractQueuedObject(AePartnerLinkOpKey aPartnerLinkOpKey)
   {
      setPartnerLinkOperationKey(aPartnerLinkOpKey);
   }

   /**
    * Default Constructor for JavaBean compliance.
    */
   public AeAbstractQueuedObject()
   {
   }

   /**
    * @return The business operation in the queue.
    */
   public String getOperation()
   {
      return getPartnerLinkOperationKey().getOperation();
   }
   
   /**
    * Convenience method for returning the partner link name.
    */
   public String getPartnerLinkName()
   {
      return getPartnerLinkOperationKey().getPartnerLinkName();
   }

   /**
    * Gets the partnerlink:operation key for this queued object.
    */
   public AePartnerLinkOpKey getPartnerLinkOperationKey()
   {
      return mPartnerLinkOperationKey;
   }

   /**
    * Overides the base to check for specific equality of queue entries.
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObject)
   {
      if(aObject instanceof AeAbstractQueuedObject)
      {
         AeAbstractQueuedObject other = (AeAbstractQueuedObject)aObject;

         return AeUtil.compareObjects(getPartnerLinkOperationKey(), other.getPartnerLinkOperationKey());
      }

      return super.equals(aObject);
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return hashCode(getPartnerLinkOperationKey());
   }

   /**
    * Returns <code>aObject.hashCode()</code> or <code>0</code> if
    * <code>aObject</code> is <code>null</code>.
    *
    * @param aObject
    */
   protected int hashCode(Object aObject)
   {
      return (aObject == null) ? 0 : aObject.hashCode();
   }

   /**
    * @param aPartnerLinkOperationKey The partnerLinkOperationKey to set.
    */
   protected void setPartnerLinkOperationKey(AePartnerLinkOpKey aPartnerLinkOperationKey)
   {
      mPartnerLinkOperationKey = aPartnerLinkOperationKey;
   }
}
