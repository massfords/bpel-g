// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeStateChangeDetail.java,v 1.4 2004/10/16 16:47:28 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

/**
 * Provides additional information regarding the object's state change. This is
 * currently only used to convey the fault name during an object's entering into
 * the faulted state. In all other cases, we're simply using the NONE static field. 
 */
public class AeStateChangeDetail implements IAeStateChangeDetail
{
   /** constant used to report no additional information regarding the state change */
   public static final IAeStateChangeDetail NONE = new AeStateChangeDetail(null);
   
   /** The name of the fault */
   private String mFaultName;
   
   /** Additional info associated with the fault. */
   private String mAdditionalInfo;
   
   /**
    * Creates a detail object with just the fault name
    * @param aFaultName
    */
   public AeStateChangeDetail(String aFaultName)
   {
      mFaultName = aFaultName;
   }
   
   /**
    * Creates a detail object with the fault name and additional info
    * @param aFaultName
    * @param aInfo
    */
   public AeStateChangeDetail(String aFaultName, String aInfo)
   {
      mFaultName = aFaultName;
      mAdditionalInfo = aInfo ;
   }
   
   /**
    * Return additional information regarding a state change.
    * @see org.activebpel.rt.bpel.impl.IAeStateChangeDetail#getAdditionalInfo()
    */
   public String getAdditionalInfo()
   {
      return mAdditionalInfo;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.IAeStateChangeDetail#getFaultName()
    */
   public String getFaultName()
   {
      return mFaultName;
   }
}
