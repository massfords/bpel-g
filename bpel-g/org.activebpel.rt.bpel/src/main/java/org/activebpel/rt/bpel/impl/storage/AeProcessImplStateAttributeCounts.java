// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/storage/AeProcessImplStateAttributeCounts.java,v 1.2 2005/02/01 19:52:59 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.storage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Counts occurrences of attribute values used for persistence by attribute
 * name. The counts can be used to determine which values should be default
 * values in <code>AeProcessImplStateAttributeDefaults</code>.
 */
public abstract class AeProcessImplStateAttributeCounts
{
   private static final Integer ONE = Integer.valueOf(1);

   /** <code>true</code> to enable counts. */
//   private static final boolean ENABLED = false;

   /** Maximum number of distinct values to track. */
   private static final int MAX_VALUES = 4;

   /** Singleton instance. */
   private static final AeProcessImplStateAttributeCounts sCounts = /*
      ENABLED ? (AeProcessImplStateAttributeCounts) new AeRealCounts() :*/ new AeFakeCounts();

   /**
    * Private base constructor for singleton instance.
    */
   private AeProcessImplStateAttributeCounts()
   {
   }

   /**
    * Returns singleton instance.
    */
   public static AeProcessImplStateAttributeCounts getCounts()
   {
      return sCounts;
   }

   /**
    * Increments the count for a specified attribute value.
    */
   public abstract void incrementCount(String aName, String aValue);

   /**
    * Prints the counts.
    */
   public abstract void printCounts();

   /**
    * Implements fake, do-nothing counts.
    */
   protected static class AeFakeCounts extends AeProcessImplStateAttributeCounts
   {
      /**
       * @see org.activebpel.rt.bpel.impl.storage.AeProcessImplStateAttributeCounts#incrementCount(java.lang.String, java.lang.String)
       */
      public void incrementCount(String aName, String aValue)
      {
      }

      /**
       * @see org.activebpel.rt.bpel.impl.storage.AeProcessImplStateAttributeCounts#printCounts()
       */
      public void printCounts()
      {
      }
   }

   /**
    * Implements real counts.
    */
   protected static class AeRealCounts extends AeProcessImplStateAttributeCounts
   {
      /** Maps attribute names to the map of values for that name. */
      private final Map<String,Map<String, Integer>> mNamesMap = new HashMap<String,Map<String, Integer>>();

      /**
       * @see org.activebpel.rt.bpel.impl.storage.AeProcessImplStateAttributeCounts#incrementCount(java.lang.String, java.lang.String)
       */
      public void incrementCount(String aName, String aValue)
      {
         Map<String, Integer> valuesMap;

         synchronized (mNamesMap)
         {
            // Get the values for the name.
            valuesMap = mNamesMap.get(aName);

            if (valuesMap == null)
            {
               // Haven't seen this name yet. Create a new values map.
               valuesMap = new HashMap<String, Integer>();
               mNamesMap.put(aName, valuesMap);
            }
         }

         synchronized (valuesMap)
         {
            // Get the old count.
            Integer count = valuesMap.get(aValue);

            if (count != null)
            {
               // Store the new count.
               valuesMap.put(aValue, Integer.valueOf(count.intValue() + 1));
            }
            else if (valuesMap.size() < MAX_VALUES)
            {
               // Start a new count.
               valuesMap.put(aValue, ONE);
            }
         }
      }

      /**
       * @see org.activebpel.rt.bpel.impl.storage.AeProcessImplStateAttributeCounts#printCounts()
       */
      public void printCounts()
      {
         Map<String,Map<String, Integer>> sortedNamesMap;

         synchronized (mNamesMap)
         {
            sortedNamesMap = new TreeMap<String,Map<String, Integer>>(mNamesMap);
         }

          for (Entry<String, Map<String, Integer>> entry : sortedNamesMap.entrySet()) {
              printCounts(entry.getKey(), entry.getValue());
          }
      }

      /**
       * Prints the counts for the specified name from the specified
       * <code>Map</code> of values to counts.
       */
      public void printCounts(String aName, Map<String, Integer> aValuesMap)
      {
    	 Map<String, Integer> sortedValuesMap = new TreeMap<String, Integer>(new AeReverseCountComparator(aValuesMap));

         synchronized (aValuesMap)
         {
            sortedValuesMap.putAll(aValuesMap);
         }

          for (Entry<String, Integer> entry : sortedValuesMap.entrySet()) {
              System.out.println("count[" + aName + "][" + entry.getKey() + "] = " + entry.getValue().toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          }
      }

      /**
       * Implements a <code>Comparator</code> that compares values in reverse
       * order of associated counts.
       */
      protected static class AeReverseCountComparator implements Comparator<String>
      {
         /** <code>Map</code> from values to counts. */
         private final Map<String, Integer> mValuesMap;

         /**
          * Constructs reverse count comparator with the specified
          * <code>Map</code> from values to counts.
          *
          * @param aValuesMap
          */
         public AeReverseCountComparator(Map<String, Integer> aValuesMap)
         {
            mValuesMap = aValuesMap;
         }

         /**
          * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
          */
         public int compare(String o1, String o2)
         {
            synchronized (mValuesMap)
            {
               int count1 = mValuesMap.get(o1).intValue();
               int count2 = mValuesMap.get(o2).intValue();
               return count2 - count1;
            }
         }
      }
   }
}
