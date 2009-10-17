//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeSafelyViewableMap.java,v 1.1 2006/02/14 16:20:54 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implements a {@link java.util.Map} wrapper that maintains a safely
 * readable view of an underlying base map. All methods that modify the
 * map are synchronized on the base map, but all methods that
 * merely examine the map delegate to a copy of the base map.
 */
public class AeSafelyViewableMap implements Map
{
   /** The base map. */
   private final Map mBaseMap;

   /** Internal safe view of the base map. */
   private Map mSafeView;

   /**
    * Constructs a safely viewable wrapper for the specified base map.
    * Note: this pattern does not support the LRU aspect of LinkedHashMap,
    * since all reads are done from a copy of the Map.  
    */
   public AeSafelyViewableMap(Map aBaseMap)
   {
      mBaseMap = aBaseMap;

      synchronized (mBaseMap)
      {
         createSafeView();
      }
   }

   /**
    * Creates the internal safe view for the base map.
    */
   protected void createSafeView()
   {
      // Create a view that preserves the base map's iteration order based on baseMap type
      if (mBaseMap instanceof LinkedHashMap)
         mSafeView = new LinkedHashMap(mBaseMap);
      else
         mSafeView = new HashMap(mBaseMap);
   }

   /**
    * @see java.util.Map#containsKey(java.lang.Object)
    */
   public boolean containsKey(Object aKey)
   {
      return mSafeView.containsKey(aKey);
   }

   /**
    * @see java.util.Map#containsValue(java.lang.Object)
    */
   public boolean containsValue(Object aValue)
   {
      return mSafeView.containsValue(aValue);
   }

   /**
    * @see java.util.Map#values()
    */
   public Collection values()
   {
      return mSafeView.values();
   }

   /**
    * @see java.util.Map#entrySet()
    */
   public Set entrySet()
   {
      return mSafeView.entrySet();
   }

   /**
    * @see java.util.Map#keySet()
    */
   public Set keySet()
   {
      return mSafeView.keySet();
   }

   /**
    * @see java.util.Map#get(java.lang.Object)
    */
   public Object get(Object aKey)
   {
      return mSafeView.get(aKey);
   }

   /**
    * @see java.util.Map#put(java.lang.Object, java.lang.Object)
    */
   public Object put(Object aKey, Object aValue)
   {
      synchronized (mBaseMap)
      {
         Object prevValue = mBaseMap.put(aKey, aValue);
         if (! AeUtil.compareObjects(aValue, prevValue))
            createSafeView();

         return prevValue;
      }
   }

   /**
    * @see java.util.Map#putAll(java.util.Map)
    */
   public void putAll(Map aMap)
   {
      synchronized (mBaseMap)
      {
         mBaseMap.putAll(aMap);
         createSafeView();
      }
   }

   /**
    * @see java.util.Map#size()
    */
   public int size()
   {
      return mSafeView.size();
   }

   /**
    * @see java.util.Map#clear()
    */
   public void clear()
   {
      synchronized (mBaseMap)
      {
         mBaseMap.clear();
         createSafeView();
      }
   }

   /**
    * @see java.util.Map#isEmpty()
    */
   public boolean isEmpty()
   {
      return mSafeView.isEmpty();
   }

   /**
    * @see java.util.Map#remove(java.lang.Object)
    */
   public Object remove(Object aKey)
   {
      synchronized (mBaseMap)
      {
         Object prevValue = mBaseMap.remove(aKey);
         createSafeView();

         return prevValue;
      }
   }
}