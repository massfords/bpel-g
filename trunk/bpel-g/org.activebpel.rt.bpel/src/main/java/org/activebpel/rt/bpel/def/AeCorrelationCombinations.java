package org.activebpel.rt.bpel.def;

import org.activebpel.rt.util.AeCombinations;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.*;


/**
 * Maintains a collection of the different correlation set combinations that exists for a single partnerlink
 * and operation. 
 */
public class AeCorrelationCombinations implements Serializable
{
   private static final long serialVersionUID = -2634821001957232378L;
   /** the different correlationSets that are used by activities with this plink and operation */
   private Collection<Set<AeCorrelationSetDef>> mCorrelationSetCombinations = new HashSet<Set<AeCorrelationSetDef>>();
   /** provides a quick way of knowing which sets in our collection contain join style correlations */
   private Set<Set<AeCorrelationSetDef>> mJoins = new HashSet<Set<AeCorrelationSetDef>>();
   /** flag that gets set to true if at least one IMA uses a correlationSet that is initiated at the time the IMA executes */
   private boolean mInitiated;
   /** the max number of correlationSets on a single activity that were join style */
   private int mJoinCount;
   
   /** wrapper object for the correlated properties and style */
   private AeCorrelatedProperties mCorrelatedProperties;
   
   // The comparator is used to sort each collection of correlation sets by size
   // to ensure that we attempt to match against the most properties in the collection first
   private static final Comparator<Set<QName>> COMPARATOR = new Comparator<Set<QName>>()
   {
      /**
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      public int compare(Set<QName> aOne, Set<QName> aTwo)
      {
         // i want descending order, so flip the comparison
         return aTwo.size() - aOne.size();
      }
   };

   /**
    * Adds the correlationSetDefs that are used for a given IMA. If this set of correlationSetDefs contains
    * one or more join style correlationSetDefs then we'll set a flag on the class to indicate
    * that we need to produce multiple combinations to handle the possibility of dispatching to an 
    * activity with an uninitialized correlationSet.
    * @param aSetOfCorrelationSetDefs
    */
   public void add(Set<AeCorrelationSetDef> aSetOfCorrelationSetDefs)
   {
      boolean added = getCorrelationSetsColl().add(aSetOfCorrelationSetDefs);
      if (added)
      {
         int count = 0;
          for (AeCorrelationSetDef corrSetDef : aSetOfCorrelationSetDefs) {
              if (corrSetDef.isJoinStyle()) {
                  count++;
              } else {
                  setInitiated(true);
              }
          }
         setJoinCount(Math.max(getJoinCount(), count));
         
         if (count > 0)
         {
            getJoins().add(aSetOfCorrelationSetDefs);
         }
      }
   }
   
   /**
    * Getter for the property combinations
    * @param aMaxCombinations
    */
   public AeCorrelatedProperties getPropertyCombinations(int aMaxCombinations)
   {
      AeCorrelatedProperties props = getCorrelatedProperties();
      if (props != null && (props.getMaxCombinations() >= aMaxCombinations || aMaxCombinations >= getJoinCount()))
      {
         return props;
      }
      else
      {
         synchronized(this)
         {
            Collection<Set<QName>> coll = createPropertyCombinations(aMaxCombinations);
            props = new AeCorrelatedProperties(coll, aMaxCombinations);
            setCorrelatedProperties(props);
            return props;
         }
      }
   }
   
   /**
    * Getter for the join count
    */
   protected int getJoinCount()
   {
      return mJoinCount;
   }
   
   /**
    * Setter for the join count
    * @param aCount
    */
   protected void setJoinCount(int aCount)
   {
      mJoinCount = aCount;
   }

   /**
    * Getter for the initiated flag. True means that at least on IMA for this plink and operation is using
    * an initiated correlation set.
    */
   protected boolean isInitiated()
   {
      return mInitiated;
   }
   
   /**
    * Setter for the initiated flag
    * @param aFlag
    */
   protected void setInitiated(boolean aFlag)
   {
      mInitiated = aFlag;
   }
   
   /**
    * Getter for the collection that maintains the correlationSet combinations
    */
   protected Collection<Set<AeCorrelationSetDef>> getCorrelationSetsColl()
   {
      return mCorrelationSetCombinations;
   }
   
   /**
    * Returns true if the join style flag is set
    */
   protected boolean isJoinStyle()
   {
      return mJoinCount > 0;
   }
   
   /**
    * Getter for the correlated properties
    */
   protected AeCorrelatedProperties getCorrelatedProperties()
   {
      return mCorrelatedProperties;
   }
   
   /**
    * Setter for the correlated properties
    * @param aProps
    */
   protected void setCorrelatedProperties(AeCorrelatedProperties aProps)
   {
      mCorrelatedProperties = aProps;
   }
   
   /**
    * Creates the collection of property combinations that can be used for the plink and operation
    * @param aMaxCombinations
    */
   protected Collection<Set<QName>> createPropertyCombinations(int aMaxCombinations)
   {
      Collection<Set<QName>> coll;
      if (isJoinStyle())
      {
         if (getJoinCount() < aMaxCombinations)
         {
            coll = createJoinStyleCombinations();
         }
         else
         {
            coll = createInitiatedCombinations();
         }
      }
      else
      {
         coll = createInitiatedCombinations();
      }
      LinkedList<Set<QName>> list = new LinkedList<Set<QName>>(coll);
      Collections.sort(list, COMPARATOR);
      return list;
   }
   
   /**
    * The initiated combinations includes all of the properties for the correlationSets that should already
    * be initiated at the time the message receiver executed. CorrelationSets that are set to initiate="yes"
    * and have a single point of initiation do not have their properties included.
    * @return SortedSet - a set of sets. The contained sets have all of the properties needed to compute the
    *                     correlation hash. The set is sorted by the number of properties in descending order. 
    */
   private Set<Set<QName>> createInitiatedCombinations()
   {
	  Set<Set<QName>> set = new HashSet<Set<QName>>();
      addInitiatedCorrelationSetProperties(set, getInitiatedIterator());
      return set;
   }

   /**
    * Walks the iterator and adds all of the properties for each of the correlationSets
    * to the set passed in.
    * @param aSet
    * @param aIter - iteration over a set of sets. The inner sets contain AeCorrelationSetDefs
    */
   protected void addInitiatedCorrelationSetProperties(Set<Set<QName>> aSet, Iterator<Set<AeCorrelationSetDef>> aIter)
   {
      while (aIter.hasNext())
      {
         Set<QName> set = new HashSet<QName>();
         Set<AeCorrelationSetDef> setOfCorrelationSetDefs = aIter.next();
          for (AeCorrelationSetDef corrSetDef : setOfCorrelationSetDefs) {
              set.addAll(corrSetDef.getProperties());
          }
         aSet.add(set);
      }
   }

   /**
    * The join style combinations includes multiple combinations for a single IMA which accounts for
    * the possibility that a correlationSet may or may not have been initiated at the time of the object's
    * execution.
    */
   private Collection<Set<QName>> createJoinStyleCombinations()
   {
	  Set<Set<QName>> combinationSet = new HashSet<Set<QName>>();
      for (Iterator<Set<AeCorrelationSetDef>> iter = getJoinsIterator(); iter.hasNext();)
      {
         // this set will contain at least one correlationSet that is a join style
    	 Set<AeCorrelationSetDef> setOfCorrelationSetDefs = iter.next();
         
         // divide the corr sets into the initiated style and join style
         List<AeCorrelationSetDef> initiatedList = new LinkedList<AeCorrelationSetDef>();
         List<AeCorrelationSetDef> joinList = new LinkedList<AeCorrelationSetDef>();
          for (AeCorrelationSetDef corrSetDef : setOfCorrelationSetDefs) {
              if (corrSetDef.isJoinStyle()) {
                  joinList.add(corrSetDef);
              } else {
                  initiatedList.add(corrSetDef);
              }
          }
         
         // get all of the initiated props since they'll be the same for each combination
         Set<QName> initiatedProps = new HashSet<QName>();
          for (AeCorrelationSetDef corrSetDef : initiatedList) {
              initiatedProps.addAll(corrSetDef.getProperties());
          }
         
         // add the combination which covers none of the join style sets being initiated
         if (!initiatedProps.isEmpty())
         {
            combinationSet.add(initiatedProps);
         }
         
         // get all combinations of the array
         for(Iterator<List<AeCorrelationSetDef>> combinationsIter = AeCombinations.createAllCombinations(joinList); combinationsIter.hasNext();)
         {
            HashSet<QName> set = new HashSet<QName>();
            set.addAll(initiatedProps);
            
            List<AeCorrelationSetDef> next = combinationsIter.next();
            for (AeCorrelationSetDef corrSetDef : next)
            {
               set.addAll(corrSetDef.getProperties());
            }
            
            combinationSet.add(set);
         }
      }
      
      addInitiatedCorrelationSetProperties(combinationSet, getInitiatedIterator());
      return combinationSet;
   }
   
   /**
    * Returns an iterator over the sets that contain "join" style correlation sets
    */
   protected Iterator<Set<AeCorrelationSetDef>> getJoinsIterator()
   {
      return mJoins.iterator(); 
   }
   
   /**
    * Returns an iterator over the sets that contain the initiated correlation sets
    */
   protected Iterator<Set<AeCorrelationSetDef>> getInitiatedIterator()
   {
      HashSet<Set<AeCorrelationSetDef>> set = new HashSet<Set<AeCorrelationSetDef>>(getCorrelationSetsColl());
      set.removeAll(getJoins());
      return set.iterator();
   }
   
   /**
    * Getter for the joins set
    */
   protected Set<Set<AeCorrelationSetDef>> getJoins()
   {
      return mJoins;
   }
   
   /**
    * Wrapper object for the sets of properties that should be used to match an inbound receive. 
    */
   public class AeCorrelatedProperties implements Serializable
   {
      private static final long serialVersionUID = 1121786637037439950L;
      /** All of the correlationSets used by this plink and operation are already initiated at the time of the IMA's execution */
      public static final int INITIATED                                            = 0;
      /** The combinations of correlationSets include a mix of initiated and join style */
      public static final int INITIATED_AND_JOIN                                   = 1;
      /** The combinations of correlationSets are all join style */
      public static final int JOIN                                                 = 2;
      /** The combinations of correlationSets include a mix of initiated and join style but there are too many join styles to make the querying efficient */
      public static final int INITIATED_AND_JOIN_OVER_MAX                          = 3;
      /** The combinations of correlationSets are all join style but are over the max number of combinations allowed. */
      public static final int JOIN_OVER_MAX                                        = 4;

      /** coll of properties */
      private Collection<Set<QName>> mCollection;
      /** the max number of join style operations to allow on a single IMA before abandoning the correlated match hash strategy in favor of brute force */
      private int mMaxCombinations;
      
      /**
       * Ctor
       * @param aCollection
       * @param aMaxCombinations
       */
      public AeCorrelatedProperties(Collection<Set<QName>> aCollection, int aMaxCombinations)
      {
         mCollection = aCollection;
         mMaxCombinations = aMaxCombinations;
      }

      /**
       * @return Returns the collection.
       */
      public Collection<Set<QName>> getCollection()
      {
         return mCollection;
      }

      /**
       * returns one of the constants above which describes the different correlationSets used for this plink and operation
       */
      public int getStyle()
      {
         boolean underLimit = getJoinCount() <= getMaxCombinations();
         
         if (!isJoinStyle())
            return INITIATED;
         else if (isInitiated() && underLimit)
            return INITIATED_AND_JOIN;
         else if (!isInitiated() && underLimit)
            return JOIN;
         else if (isInitiated() && !underLimit)
            return INITIATED_AND_JOIN_OVER_MAX;
         else
            return JOIN_OVER_MAX;
      }
      
      /**
       * Returns the max number of combinations allowed. 
       */
      public int getMaxCombinations()
      {
         return mMaxCombinations;
      }

   }
}