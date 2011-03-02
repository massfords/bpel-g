package org.activebpel.rt.bpel.def.faults;

/**
 * interface for the fault matching rules
 */
public interface IAeMatch
{
   /**
    * Returns true if this match is the best match.
    */
   public boolean isBestMatch();

   /**
    * Return true if this match is better than the given match
    * @param aMatch
    */
   public boolean isBetterMatchThan(IAeMatch aMatch);

   /**
    * @return Returns the priority of this match.
    */
   public int getPriority();

   /**
    * @return Returns the substituion group level of this match.
    */
   public int getSGLevel();

   /**
    * Sets the substituon group level of this match.
    */
   public void setSGLevel(int sgLevel);

}