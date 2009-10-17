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

import org.activebpel.rt.bpel.impl.AePartnerLinkOpImplKey;


/**
 * This class combines a partner link and an operation into a key that can be used in maps and
 * the like.  It is also convenient to use when passing partner link:operation pairs around.
 */
public class AePartnerLinkOpKey extends AePartnerLinkDefKey
{
   /** The operation. */
   private String mOperation;
   
   /**
    * Constructs the partner link op key from all of the necessary parts.
    * 
    * @param aPartnerLinkName
    * @param aPartnerLinkId
    * @param aOperation
    */
   public AePartnerLinkOpKey(String aPartnerLinkName, int aPartnerLinkId, String aOperation)
   {
      super(aPartnerLinkName, aPartnerLinkId);
      setOperation(aOperation);
   }
   
   /**
    * Constructs the partner link op key from the partner link def and the operation.
    * 
    * @param aPartnerLinkDef
    * @param aOperation
    */
   public AePartnerLinkOpKey(AePartnerLinkDef aPartnerLinkDef, String aOperation)
   {
      super(aPartnerLinkDef);
      setOperation(aOperation);
   }

   /**
    * Constructs the key from the partner link and operation.
    * 
    * @param aPartnerLinkKey
    * @param aOperation
    */
   public AePartnerLinkOpKey(AePartnerLinkDefKey aPartnerLinkKey, String aOperation)
   {
      super(aPartnerLinkKey.getPartnerLinkName(), aPartnerLinkKey.getPartnerLinkId());
      setOperation(aOperation);
   }

   /**
    * @return Returns the operation.
    */
   public String getOperation()
   {
      return mOperation;
   }

   /**
    * @param aOperation The operation to set.
    */
   protected void setOperation(String aOperation)
   {
      mOperation = aOperation;
   }
   
   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return super.hashCode() ^ getOperation().hashCode();
   }
   
   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObj)
   {
      if (aObj instanceof AePartnerLinkOpKey)
      {
         AePartnerLinkOpKey other = (AePartnerLinkOpKey) aObj;
         return super.equals(other) && getOperation().equals(other.getOperation());
      }

      return super.equals(aObj);
   }

   /**
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Object aObj)
   {
      int rval = super.compareTo(aObj);

      if (aObj instanceof AePartnerLinkOpImplKey && rval == 0)
      {
         AePartnerLinkOpImplKey other = (AePartnerLinkOpImplKey) aObj;
         rval = getOperation().compareTo(other.getOperation());
      }

      return rval;
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuffer buff = new StringBuffer();
      synchronized(buff)
      {
         buff.append(super.toString());
         buff.append("\n"); //$NON-NLS-1$
         buff.append("Operation: "); //$NON-NLS-1$
         buff.append(getOperation());
      }
      return buff.toString();
   }
}
