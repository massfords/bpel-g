//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeSafelyViewableSet.java,v 1.1 2006/12/14 18:47:59 rnaylor Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implements a {@link java.util.Collection} wrapper that maintains a safely
 * readable view of an underlying base collection. All methods that modify the
 * collection are synchronized on the base collection, but all methods that
 * merely examine the collection delegate to a copy of the base collection.
 */
public class AeSafelyViewableSet implements Set
{
   /** The base set. */
   private final Set mBaseSet;

   /** Internal safe view of the base set. */
   private Set mSafeView;

   /**
    * Constructs a safely viewable wrapper for the specified base collection.
    */
   public AeSafelyViewableSet(Set aBaseSet)
   {
      mBaseSet = aBaseSet;

      synchronized (mBaseSet)
      {
         createSafeView();
      }
   }

   /**
    * Creates the internal safe view for the base collection.
    */
   protected void createSafeView()
   {
      // Create a view that preserves the base collection's iteration order
      // and is efficient for contains().
      mSafeView = new LinkedHashSet(mBaseSet);
   }

   /**
    * @see java.util.Collection#add(java.lang.Object)
    */
   public boolean add(Object aObject)
   {
      synchronized (mBaseSet)
      {
         boolean changed = mBaseSet.add(aObject);

         if (changed)
         {
            createSafeView();
         }

         return changed;
      }
   }

   /**
    * @see java.util.Collection#addAll(java.util.Collection)
    */
   public boolean addAll(Collection aCollection)
   {
      synchronized (mBaseSet)
      {
         boolean changed = mBaseSet.addAll(aCollection);

         if (changed)
         {
            createSafeView();
         }

         return changed;
      }
   }

   /**
    * @see java.util.Collection#clear()
    */
   public void clear()
   {
      synchronized (mBaseSet)
      {
         mBaseSet.clear();

         createSafeView();
      }
   }

   /**
    * @see java.util.Collection#contains(java.lang.Object)
    */
   public boolean contains(Object aObject)
   {
      return mSafeView.contains(aObject);
   }

   /**
    * @see java.util.Collection#containsAll(java.util.Collection)
    */
   public boolean containsAll(Collection aCollection)
   {
      return mSafeView.containsAll(aCollection);
   }

   /**
    * @see java.util.Collection#isEmpty()
    */
   public boolean isEmpty()
   {
      return mSafeView.isEmpty();
   }

   /**
    * @see java.util.Collection#iterator()
    */
   public Iterator iterator()
   {
      return mSafeView.iterator();
   }

   /**
    * @see java.util.Collection#remove(java.lang.Object)
    */
   public boolean remove(Object aObject)
   {
      synchronized (mBaseSet)
      {
         boolean changed = mBaseSet.remove(aObject);

         if (changed)
         {
            createSafeView();
         }

         return changed;
      }
   }

   /**
    * @see java.util.Collection#removeAll(java.util.Collection)
    */
   public boolean removeAll(Collection aCollection)
   {
      synchronized (mBaseSet)
      {
         boolean changed = mBaseSet.removeAll(aCollection);

         if (changed)
         {
            createSafeView();
         }

         return changed;
      }
   }

   /**
    * @see java.util.Collection#retainAll(java.util.Collection)
    */
   public boolean retainAll(Collection aCollection)
   {
      synchronized (mBaseSet)
      {
         boolean changed = mBaseSet.retainAll(aCollection);

         if (changed)
         {
            createSafeView();
         }

         return changed;
      }
   }

   /**
    * @see java.util.Collection#size()
    */
   public int size()
   {
      return mSafeView.size();
   }

   /**
    * @see java.util.Collection#toArray()
    */
   public Object[] toArray()
   {
      return mSafeView.toArray();
   }

   /**
    * @see java.util.Collection#toArray(java.lang.Object[])
    */
   public Object[] toArray(Object[] aArray)
   {
      return mSafeView.toArray(aArray);
   }
}
