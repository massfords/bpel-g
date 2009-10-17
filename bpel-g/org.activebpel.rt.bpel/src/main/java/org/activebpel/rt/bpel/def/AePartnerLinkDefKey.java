// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def;


/**
 * This key identifies a single partner link in the Def layer.
 */
public class AePartnerLinkDefKey implements Comparable
{
   /** The partner link name. */
   private String mPartnerLinkName;
   /** The partner link id. */
   private int mPartnerLinkId;

   /**
    * Constructs a key with all of the component parts.
    *
    * @param aPartnerLinkName
    * @param aPartnerLinkId
    */
   public AePartnerLinkDefKey(String aPartnerLinkName, int aPartnerLinkId)
   {
      super();
      setPartnerLinkName(aPartnerLinkName);
      setPartnerLinkId(aPartnerLinkId);
   }

   /**
    * Constructs a key from the given partner link.
    *
    * @param aPartnerLinkDef
    */
   public AePartnerLinkDefKey(AePartnerLinkDef aPartnerLinkDef)
   {
      this(aPartnerLinkDef.getName(), aPartnerLinkDef.getLocationId());
   }

   /**
    * @return Returns the partnerLinkId.
    */
   public int getPartnerLinkId()
   {
      return mPartnerLinkId;
   }

   /**
    * @param aPartnerLinkId The partnerLinkId to set.
    */
   protected void setPartnerLinkId(int aPartnerLinkId)
   {
      mPartnerLinkId = aPartnerLinkId;
   }

   /**
    * @return Returns the partnerLinkName.
    */
   public String getPartnerLinkName()
   {
      return mPartnerLinkName;
   }

   /**
    * @param aPartnerLinkName The partnerLinkName to set.
    */
   protected void setPartnerLinkName(String aPartnerLinkName)
   {
      mPartnerLinkName = aPartnerLinkName;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObj)
   {
      if (aObj instanceof AePartnerLinkDefKey)
      {
         AePartnerLinkDefKey other = (AePartnerLinkDefKey) aObj;
         // Legacy issue here - the partner link id may be -1 (if it came out of an old DB, for example).  In
         // that case, we'll test for equality based on the partner link name only.
         if (getPartnerLinkId() == -1 || other.getPartnerLinkId() == -1)
            return getPartnerLinkName().equals(other.getPartnerLinkName());
         else
            return getPartnerLinkId() == other.getPartnerLinkId();
      }
      return false;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      // Use the partner link name as the hash code for legacy 
      // reasons.  Partner Link Def keys that came from old DB
      // versions may not have a valid partner link id.
      return getPartnerLinkName().hashCode();
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuffer buff = new StringBuffer();
      synchronized(buff)
      {
         buff.append("Name:     "); //$NON-NLS-1$
         buff.append(getPartnerLinkName());
         buff.append("\n"); //$NON-NLS-1$
         buff.append("Id:       "); //$NON-NLS-1$
         buff.append(getPartnerLinkId());
      }
      return buff.toString();
   }

   /**
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Object aObj)
   {
      if (aObj instanceof AePartnerLinkDefKey)
      {
         AePartnerLinkDefKey other = (AePartnerLinkDefKey) aObj;
         // Legacy issue here - the partner link id may be -1 (if it came out of an old DB, for example).  In
         // that case, we'll do the comparison based on the partner link name only.
         if (getPartnerLinkId() == -1 || other.getPartnerLinkId() == -1)
            return getPartnerLinkName().compareTo(other.getPartnerLinkName());
         else
            return new Integer(getPartnerLinkId()).compareTo(new Integer(other.getPartnerLinkId()));
      }
      return -1;
   }
}
