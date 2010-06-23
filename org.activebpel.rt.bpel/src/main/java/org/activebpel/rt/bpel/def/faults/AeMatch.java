// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/faults/AeMatch.java,v 1.2 2006/09/28 16:01:01 tzhang Exp $
// ///////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc. Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
// ///////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.faults;

/**
 * Definition for bpel fault handler and fault data match.
 */
public class AeMatch implements IAeMatch
{
   /** true if this curren match is the best match */
   private boolean mBestMatch;

   /** the priority order of this match. */
   private int mPriority;

   /** the substition group level of this match. */
   private int mSGLevel;

   /**
    * Default constructor
    */
   public AeMatch(boolean aBestMatch, int aPriority)
   {
      mBestMatch = aBestMatch;
      mPriority = aPriority;
   }

   /**
    * Returns true if this match is the best match.
    */
   public boolean isBestMatch()
   {
      return mBestMatch;
   }

   /**
    * Return true if this match is better than the given match. Only when two matches has the identical
    * priority, then the substitution group level will be tested for better match. 
    * @param aMatch
    */
   public boolean isBetterMatchThan(IAeMatch aMatch)
   {
      if ( this.mPriority < aMatch.getPriority()
            || (this.mPriority == aMatch.getPriority() && this.mSGLevel < aMatch.getSGLevel()) )
         return true;
      return false;
   }

   /**
    * @return Returns the priority.
    */
   public int getPriority()
   {
      return mPriority;
   }

   /**
    * @return Returns the substituion group level of this match.
    */
   public int getSGLevel()
   {
      return mSGLevel;
   }

   /**
    * Sets the substituon group level of this match.
    */
   public void setSGLevel(int aSGLevel)
   {
      mSGLevel = aSGLevel;
   }

}
