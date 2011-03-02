// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.def.AePartnerLinkOpKey;

/**
 * This class implements a partner link impl key.  It extends the partner link def key and simply
 * adds the instance specific information (instance based location and id).
 */
public class AePartnerLinkOpImplKey extends AePartnerLinkOpKey implements Comparable
{
   /** The location path of the partner link */
   private String mPartnerLinkLocationPath;

   /**
    * Constructs the partner link impl key from the partner link.  This constructor uses the 
    * def information as the instance info (with the assumption that this particular partner
    * link has no specific instance info).
    * 
    * @param aPartnerLink
    * @param aOperation
    */
   public AePartnerLinkOpImplKey(AePartnerLink aPartnerLink, String aOperation)
   {
      super(aPartnerLink.getDefinition(), aOperation);

      setPartnerLinkLocationPath(aPartnerLink.getLocationPath());
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObj)
   {
      if (aObj instanceof AePartnerLinkOpImplKey)
      {
         return compareTo(aObj) == 0;
      }
      else
      {
         return super.equals(aObj);
      }
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return getPartnerLinkLocationPath().hashCode() ^ getOperation().hashCode();
   }

   /**
    * @see org.activebpel.rt.bpel.def.AePartnerLinkDefKey#toString()
    */
   public String toString()
   {
      StringBuffer buff = new StringBuffer();
      synchronized(buff)
      {
         buff.append(super.toString());
         buff.append("\nLoc Path:  "); //$NON-NLS-1$
         buff.append(getPartnerLinkLocationPath());
      }
      return buff.toString();
   }

   /**
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Object aObj)
   {
      if (aObj instanceof AePartnerLinkOpImplKey)
      {
         AePartnerLinkOpImplKey other = (AePartnerLinkOpImplKey) aObj;
         int rval = getPartnerLinkLocationPath().compareTo(other.getPartnerLinkLocationPath());
         if (rval == 0)
            rval = getOperation().compareTo(other.getOperation());

         return rval;
      }
      return super.compareTo(aObj);
   }

   /**
    * @return Returns the locationPath of the partner link.
    */
   public String getPartnerLinkLocationPath()
   {
      return mPartnerLinkLocationPath;
   }

   /**
    * @param aLocationPath The locationPath of the partner link 
    */
   public void setPartnerLinkLocationPath(String aLocationPath)
   {
      mPartnerLinkLocationPath = aLocationPath;
   }
}
