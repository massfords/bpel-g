//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeCloneUtil.java,v 1.2 2007/11/13 16:55:23 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.activebpel.rt.AeException;

/**
 * Utility class to help with clone operations
 */
public class AeCloneUtil
{
   // Empty signature/args used during reflection 
   private static final Class[] EMPTY_SIGNATURE = new Class[0];
   private static final Object[] EMPTY_ARGS = new Object[0];
   
   /**
    * Private ctor to prevent instantiations
    */
   private AeCloneUtil()
   {
   }

   /**
    * Utility method to deep clone a List. All items of the list must be {@link Cloneable}
    * @param aList the list of items to be cloned
    */
   @SuppressWarnings("unchecked")
   public static <T> List<T> deepClone(List<T> aList) throws CloneNotSupportedException
   {
      if (aList == null)
         return null;
      
      try
      {
         Constructor ctor = aList.getClass().getConstructor(EMPTY_SIGNATURE);
         List<T> clonedList = (List<T>) ctor.newInstance(EMPTY_ARGS);
         
         for (Iterator<T> iter=aList.iterator(); iter.hasNext();)
         {
            T obj = iter.next();
            if (obj instanceof Cloneable)
            {
               Method method = obj.getClass().getMethod("clone", EMPTY_SIGNATURE); //$NON-NLS-1$
               obj = (T) method.invoke(obj, EMPTY_ARGS);
            }
            
            clonedList.add(obj);
         }
         
         return clonedList;
      }
      catch(Exception e)
      {
         // This should never happen if clone tree is setup properly
         AeException.logError(e);
         throw new CloneNotSupportedException(e.getLocalizedMessage());
      }
   }

   /**
    * Utility method to deep clone a Set. All items of the list must be {@link Cloneable}
    * @param aSet the {@link Set} of items to be cloned
    */
   @SuppressWarnings("unchecked")
   public static <T> Set<T> deepClone(Set<T> aSet) throws CloneNotSupportedException
   {
      if (aSet == null)
         return null;
      
      try
      {
         Constructor ctor = aSet.getClass().getConstructor(EMPTY_SIGNATURE);
         Set<T> clonedSet = (Set<T>)ctor.newInstance(EMPTY_ARGS);
         
         for (Iterator<T> iter=aSet.iterator(); iter.hasNext();)
         {
            T obj = iter.next();
            if (obj instanceof Cloneable)
            {
               Method method = obj.getClass().getMethod("clone", EMPTY_SIGNATURE); //$NON-NLS-1$
               obj = (T) method.invoke(obj, EMPTY_ARGS);
            }
            clonedSet.add(obj);
         }
         
         return clonedSet;
      }
      catch(Exception e)
      {
         // This should never happen if clone tree is setup properly
         AeException.logError(e);
         throw new CloneNotSupportedException(e.getLocalizedMessage());
      }
   }
   
   /**
    * Utility method to deep clone a Map. All items of the list must be {@link Cloneable}
    * @param aMap the map of items to be cloned
    */
   @SuppressWarnings("unchecked")
   public static <K,V> Map<K,V> deepClone(Map<K,V> aMap) throws CloneNotSupportedException
   {
      if (aMap == null)
         return null;
      
      try
      {
         Constructor ctor = aMap.getClass().getConstructor(EMPTY_SIGNATURE);
         Map<K,V> clonedMap = (Map<K,V>)ctor.newInstance(EMPTY_ARGS);
         
         for (Iterator<Entry<K, V>> iter=aMap.entrySet().iterator(); iter.hasNext();)
         {
            Map.Entry<K,V> entry = iter.next();
            K key = entry.getKey();
            if (key instanceof Cloneable)
            {
               Method keyMethod = entry.getKey().getClass().getMethod("clone", EMPTY_SIGNATURE); //$NON-NLS-1$
               key = (K)keyMethod.invoke(entry.getKey(), EMPTY_ARGS);
            }
            
            V value = entry.getValue();
            if (value instanceof Cloneable)
            {
               Method valueMethod = entry.getValue().getClass().getMethod("clone", EMPTY_SIGNATURE); //$NON-NLS-1$
               value = (V)valueMethod.invoke(entry.getValue(), EMPTY_ARGS);
            }
            
            clonedMap.put(key, value);
         }
         
         return clonedMap;
      }
      catch(Exception e)
      {
         // This should never happen if clone tree is setup properly
         AeException.logError(e);
         throw new CloneNotSupportedException(e.getLocalizedMessage());
      }
   }
}