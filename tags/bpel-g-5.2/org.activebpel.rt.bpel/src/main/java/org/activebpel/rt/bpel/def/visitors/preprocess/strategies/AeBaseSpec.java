//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/preprocess/strategies/AeBaseSpec.java,v 1.1 2006/08/18 22:20:35 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors.preprocess.strategies; 

import java.util.BitSet;

/**
 * Provides a wrapper around a bit set for use in analyzing def objects and determining
 * the runtime strategy for some feature (i.e. copying data, receiving message data, producing message data..etc)
 */
public class AeBaseSpec
{
   /** used to record the bit flags */
   protected BitSet mBits = new BitSet();

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object aObject)
   {
      if (aObject instanceof AeBaseSpec)
      {
         AeBaseSpec other = (AeBaseSpec) aObject;
         return mBits.equals(other.mBits);
      }
      return false;
   }

   /**
    * Setter for the given bit
    * @param aConstant
    */
   public void set(int aConstant)
   {
      mBits.set(aConstant);
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   public int hashCode()
   {
      return mBits.hashCode();
   }
}
