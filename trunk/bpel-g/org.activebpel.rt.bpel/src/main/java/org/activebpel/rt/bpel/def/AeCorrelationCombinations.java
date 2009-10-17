package org.activebpel.rt.bpel.def;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.activebpel.rt.util.AeCombinations;


/**
 * Maintains a collection of the different correlation set combinations that exists for a single partnerlink
 * and operation. 
 */
public class AeCorrelationCombinations
{
   /** the different correlationSets that are used by activities with this plink and operation */
   private Collection mCorrelationSetCombinations = new HashSet();
   /** provides a quick way of knowing which sets in our collection contain join style correlations */
   private Set mJoins = new HashSet();
   /** flag that gets set to true if at least one IMA uses a correlationSet that is initiated at the time the IMA executes */
   private boolean mInitiated;
   /** the max number of correlationSets on a single activity that were join style */
   private int mJoinCount;
   
   /** wrapper object for the correlated properties and style */
   private AeCorrelatedProperties mCorrelatedProperties;
   
   // The comparator is used to sort each collection of correlation sets by size
   // to ensure that we attempt to match against the most properties in the collection first
   private static final Comparator COMPARATOR = new Comparator()
   {
      /**
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      public int compare(Object aOne, Object aTwo)
      {
         Set one = (Set) aOne;
         Set two = (Set) aTwo;
         // i want descending order, so flip the comparison
         return two.size() - one.size();
      }
   };

   /**
    * Adds the correlationSetDefs that are used for a given IMA. If this set of correlationSetDefs contains
    * one or more join style correlationSetDefs then we'll set a flag on the class to indicate
    * that we need to produce multiple combinations to handle the possibility of dispatching to an 
    * activity with an uninitialized correlationSet.
    * @param aSetOfCorrelationSetDefs
    */
   public void add(Set aSetOfCorrelationSetDefs)
   {
      boolean added = getCorrelationSetsColl().add(aSetOfCorrelationSetDefs);
      if (added)
      {
         int count = 0;
         for (Iterator iter = aSetOfCorrelationSetDefs.iterator(); iter.hasNext();)
         {
            AeCorrelationSetDef corrSetDef = (AeCorrelationSetDef) iter.next();
            if (corrSetDef.isJoinStyle())
            {
               count++;
            }
            else
            {
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
            Collection coll = createPropertyCombinations(aMaxCombinations);
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
   protected Collection getCorrelationSetsColl()
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
   protected Collection createPropertyCombinations(int aMaxCombinations)
   {
      Collection coll = null;
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
      LinkedList list = new LinkedList(coll);
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
   private Set createInitiatedCombinations()
   {
      Set set = new HashSet();
      addInitiatedCorrelationSetProperties(set, getInitiatedIterator());
      return set;
   }

   /**
    * Walks the iterator and adds all of the properties for each of the correlationSets
    * to the set passed in.
    * @param aSet
    * @param aIter - iteration over a set of sets. The inner sets contain AeCorrelationSetDefs
    */
   protected void addInitiatedCorrelationSetProperties(Set aSet, Iterator aIter)
   {
      while (aIter.hasNext())
      {
         Set set = new HashSet();
         Set setOfCorrelationSetDefs = (Set) aIter.next();
         for (Iterator iterator = setOfCorrelationSetDefs.iterator(); iterator.hasNext();)
         {
            AeCorrelationSetDef corrSetDef = (AeCorrelationSetDef) iterator.next();
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
   private Collection createJoinStyleCombinations()
   {
      Set combinationSet = new HashSet();
      for (Iterator iter = getJoinsIterator(); iter.hasNext();)
      {
         // this set will contain at least one correlationSet that is a join style
         Set setOfCorrelationSetDefs = (Set) iter.next();
         
         // divide the corr sets into the initiated style and join style
         List initiatedList = new LinkedList();
         List joinList = new LinkedList();
         for (Iterator iterator = setOfCorrelationSetDefs.iterator(); iterator.hasNext();)
         {
            AeCorrelationSetDef corrSetDef = (AeCorrelationSetDef) iterator.next();
            if (corrSetDef.isJoinStyle())
            {
               joinList.add(corrSetDef);
            }
            else
            {
               initiatedList.add(corrSetDef);
            }
         }
         
         // convert to array
         AeCorrelationSetDef[] joinCorrSets = new AeCorrelationSetDef[joinList.size()];
         joinList.toArray(joinCorrSets);
         
         // get all of the initiated props since they'll be the same for each combination
         Set initiatedProps = new HashSet();
         for (Iterator iterator = initiatedList.iterator(); iterator.hasNext();)
         {
            AeCorrelationSetDef corrSetDef = (AeCorrelationSetDef) iterator.next();
            initiatedProps.addAll(corrSetDef.getProperties());
         }
         
         // add the combination which covers none of the join style sets being initiated
         if (!initiatedProps.isEmpty())
         {
            combinationSet.add(initiatedProps);
         }
         
         // get all combinations of the array
         for(Iterator combinationsIter = AeCombinations.createAllCombinations(joinCorrSets); combinationsIter.hasNext();)
         {
            HashSet set = new HashSet();
            set.addAll(initiatedProps);
            
            Object[] next = (Object[]) combinationsIter.next();
            for (int i = 0; i < next.length; i++)
            {
               AeCorrelationSetDef corrSetDef = (AeCorrelationSetDef) next[i];
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
   protected Iterator getJoinsIterator()
   {
      return mJoins.iterator(); 
   }
   
   /**
    * Returns an iterator over the sets that contain the initiated correlation sets
    */
   protected Iterator getInitiatedIterator()
   {
      HashSet set = new HashSet(getCorrelationSetsColl());
      set.removeAll(getJoins());
      return set.iterator();
   }
   
   /**
    * Getter for the joins set
    */
   protected Set getJoins()
   {
      return mJoins;
   }
   
   /**
    * Wrapper object for the sets of properties that should be used to match an inbound receive. 
    */
   public class AeCorrelatedProperties
   {
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
      private Collection mCollection;
      /** the max number of join style operations to allow on a single IMA before abandoning the correlated match hash strategy in favor of brute force */
      private int mMaxCombinations;
      
      /**
       * Ctor
       * @param aCollection
       * @param aMaxCombinations
       */
      public AeCorrelatedProperties(Collection aCollection, int aMaxCombinations)
      {
         mCollection = aCollection;
         mMaxCombinations = aMaxCombinations;
      }

      /**
       * @return Returns the collection.
       */
      public Collection getCollection()
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
      
      /**
       * Setter for the max number of combinations. If greater than the current value
       * then any previously created combinations will be cleared.
       * @param aMaxCombinations
       */
      public void setMaxCombinations(int aMaxCombinations)
      {
         mMaxCombinations = aMaxCombinations;
      }
   }
}