// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeTypedSet.java,v 1.4 2007/05/08 18:46:57 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Base class for typed sets.
 */
abstract public class AeTypedSet implements Set, Serializable
{
   /** The underlying physical representation. */
   protected final Set mSet;

   /**
    * Default constructor.
    */
   public AeTypedSet()
   {
      this(new HashSet());
   }

   /**
    * Constructor.
    *
    * @param aSet The <code>Set</code> to use.
    */
   public AeTypedSet(Set aSet)
   {
      mSet = aSet;
   }

   /**
    * Returns the underlying <code>Set</code> object for synchronization
    * purposes.
    */
   public Object synchObject()
   {
      return mSet;
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return mSet.toString();
   }

   /*======================================================================
    * java.util.Set methods
    *======================================================================
    */

   /**
    * @see java.util.Set#add(java.lang.Object)
    */
   public boolean add(Object aObject)
   {
      return mSet.add(aObject);
   }

   /**
    * @see java.util.Set#addAll(java.util.Collection)
    */
   public boolean addAll(Collection aCollection)
   {
      return mSet.addAll(aCollection);
   }

   /**
    * @see java.util.Set#clear()
    */
   public void clear()
   {
      mSet.clear();
   }

   /**
    * @see java.util.Set#contains(java.lang.Object)
    */
   public boolean contains(Object aObject)
   {
      return mSet.contains(aObject);
   }

   /**
    * @see java.util.Set#containsAll(java.util.Collection)
    */
   public boolean containsAll(Collection aCollection)
   {
      return mSet.containsAll(aCollection);
   }

   /**
    * @see java.util.Set#isEmpty()
    */
   public boolean isEmpty()
   {
      return mSet.isEmpty();
   }

   /**
    * @see java.util.Set#iterator()
    */
   public Iterator iterator()
   {
      return mSet.iterator();
   }

   /**
    * @see java.util.Set#remove(java.lang.Object)
    */
   public boolean remove(Object aObject)
   {
      return mSet.remove(aObject);
   }

   /**
    * @see java.util.Set#removeAll(java.util.Collection)
    */
   public boolean removeAll(Collection aCollection)
   {
      return mSet.removeAll(aCollection);
   }

   /**
    * @see java.util.Set#retainAll(java.util.Collection)
    */
   public boolean retainAll(Collection aCollection)
   {
      return mSet.retainAll(aCollection);
   }

   /**
    * @see java.util.Set#size()
    */
   public int size()
   {
      return mSet.size();
   }

   /**
    * @see java.util.Set#toArray()
    */
   public Object[] toArray()
   {
      return mSet.toArray();
   }

   /**
    * @see java.util.Set#toArray(java.lang.Object[])
    */
   public Object[] toArray(Object[] aArray)
   {
      return mSet.toArray(aArray);
   }
}
