// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeSequenceIterator.java,v 1.3 2004/07/08 13:09:44 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents the logical concatenation of multiple <code>java.util.Iterator</code>
 * objects as well as individually added objects.     
 */
public class AeSequenceIterator implements Iterator
{
   /** Holds onto the iterators that we're joining */
   private List mIters = new ArrayList();
   
   /** Offset used to track where we are in the list of iterators */
   private int mOffset = 0;
   
   /**
    * Convenience method for joining two Iterators together when you don't want
    * the bother of having to check for null or the worry from creating a new
    * empty iterator.
    * @param aIterOne can be null
    * @param aIterTwo can be null
    * @return Iterator, possibly the Collections.EMPTY_SET.iterator() if both are
    *         null/empty. 
    */
   public static Iterator join(Iterator aIterOne, Iterator aIterTwo)
   {
      if (isNullOrEmpty(aIterOne) && isNullOrEmpty(aIterTwo))
      {
         return Collections.EMPTY_SET.iterator();
      }
      
      if (isNullOrEmpty(aIterOne))
      {
         return aIterTwo;
      }
      
      if (isNullOrEmpty(aIterTwo))
      {
         return aIterOne;
      }
      
      return new AeSequenceIterator(aIterOne, aIterTwo);
   }
   
   /**
    * Returns true if the Iterator passed in is either null or empty.
    * @param aIter
    */
   private static boolean isNullOrEmpty(Iterator aIter)
   {
      return aIter == null || !aIter.hasNext();
   }
   
   
   /**
    * Default constructor that creates an empty iterator. Useful if you want to
    * add more than two Iterators together.
    */
   public AeSequenceIterator()
   {
   }
   
   /**
    * Constructor that accepts two iterators for joining together.
    * @param aIteratorOne can be null
    * @param aIteratorTwo can be null
    */
   public AeSequenceIterator(Iterator aIteratorOne, Iterator aIteratorTwo)
   {
      this((Object)aIteratorOne, (Object)aIteratorTwo);
   }
   
   /**
    * Constructor that accepts two objects for joining together. If these objects
    * are Iterators then they'll be added using the logic in add(Iterator)
    * @param aObjectOne
    * @param aObjectTwo
    */
   public AeSequenceIterator(Object aObjectOne, Object aObjectTwo)
   {
      add(aObjectOne);
      add(aObjectTwo);
   }
   
   /**
    * Adds the iterator to our collection if it has elements in it.
    * @param aIterator can be null
    */
   public void add(Iterator aIterator)
   {
      if ( ! isNullOrEmpty(aIterator) )
      {
         mIters.add(aIterator);
      }
   }
   
   /**
    * Adds a single object to our collection. If this object is an instanceof
    * Iterator then we'll apply the logic for adding an Iterator, otherwise
    * this object gets appended to our sequence if it's not null.
    * @param aObject if null, will not be added to our collection
    */
   public void add(Object aObject)
   {
      if (aObject instanceof Iterator)
      {
         add((Iterator)aObject);
      }
      else if (aObject != null)
      {
         add(Collections.singleton(aObject).iterator());
      }
   }

   /**
    * @see java.util.Iterator#hasNext()
    */
   public boolean hasNext()
   {
      Iterator it = getCurrentIterator();
      if (it == null)
      {
         return false;
      }
      
      return it.hasNext();
   }
   
   /**
    * Gets the first non-empty Iterator in our collection. If there are no
    * Iterators left then we return null;
    * @return Iterator or null if there are none left.
    */
   private Iterator getCurrentIterator()
   {
      Iterator found = null;
      while (mOffset < mIters.size())
      {
         Iterator it = (Iterator) mIters.get(mOffset);
         if (it.hasNext())
         {
            found = it;
            break;
         }
         mOffset++;
      }
      return found;
   }

   /**
    * @see java.util.Iterator#next()
    */
   public Object next()
   {
      Iterator it = getCurrentIterator();
      if (it == null)
      {
         throw new NoSuchElementException();
      }
      
      return it.next();
   }

   /**
    * @see java.util.Iterator#remove()
    */
   public void remove()
   {
      Iterator it = getCurrentIterator();
      if (it == null)
      {
         throw new NoSuchElementException();
      }
      
      it.remove();
   }
   
   /**
    * Returns the object to its initial empty state. 
    */
   public void reset()
   {
      mIters.clear();
      mOffset = 0;
   }

}
