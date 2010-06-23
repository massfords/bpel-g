//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeCombinations.java,v 1.2 2006/07/06 14:52:14 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util; 

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.activebpel.rt.AeMessages;


/**
 * Returns an iterator with the possible non-repeating subset combinations of
 * an array of objects. 
 */
public class AeCombinations implements Iterator
{
   /** array of objects from which to extract combinations */
   private Object[] mObjects;
   /** number of objects wanted in each combination */
   private int[] mOffsets;
   /** true if the iterator has more elements to return */
   private boolean mHasNext = true;
   
   /**
    * Creates an array of all of the non-repeating combinations of the 
    * objects within the array. The total number of iterations will be
    * (2 ^ aObjects.length) - 1
    * @param aObjects
    */
   public static Iterator createAllCombinations(Object[] aObjects)
   {
      AeSequenceIterator seqIter = new AeSequenceIterator();
      for(int i=aObjects.length; i>0; i--)
      {
         seqIter.add(new AeCombinations(aObjects, i));
      }
      return seqIter;
   }
   
   /**
    * Ctor - initializes the iterator
    * @param aObjects - array of objects from which to extract combinations
    * @param aCount - number of objects wanted in each combination
    */
   public AeCombinations(Object[] aObjects, int aCount)
   {
      if (aCount > aObjects.length || aCount == 0)
      {
         Object[] args = {new Integer(aCount), new Integer(aObjects.length)};
         throw new IllegalArgumentException(AeMessages.format("AeCombinations.IllegalCount", args)); //$NON-NLS-1$
      }
      mObjects = aObjects;
      mOffsets = new int[aCount];
      for (int i = 0; i < mOffsets.length; i++)
      {
         mOffsets[i] = i;
      }
   }
   
   /**
    * Getter for the objects array
    */
   protected Object[] getObjects()
   {
      return mObjects;
   }
   
   /**
    * Getter for the offsets array
    */
   protected int[] getOffsets()
   {
      return mOffsets;
   }

   /**
    * @see java.util.Iterator#hasNext()
    */
   public boolean hasNext()
   {
      return mHasNext;
   }
   
   /**
    * Setter for the hasNext flag of iteration
    * @param aFlag
    */
   protected void setHasNext(boolean aFlag)
   {
      mHasNext = aFlag;
   }
   
   /**
    * @see java.util.Iterator#next()
    */
   public Object next()
   {
      if (!mHasNext)
      {
         throw new NoSuchElementException();
      }
      
      Object[] next = getNext();
      
      setHasNext(prepareNextIteration());
      
      return next;
   }
   
   /**
    * Creates a combination of the array values using the current offsets.
    */
   protected Object[] getNext()
   {
      Object[] next = new Object[getOffsets().length];
      for (int i = 0; i < getOffsets().length; i++)
      {
         next[i] = getObjects()[getOffsets()[i]];
      }
      return next;
   }
   
   /**
    * Returns true if the next iteration can be prepared. False means that no more
    * unique combinations can be produced from this array.
    * 
    * The search for combinations begins with initializing the offet array to 0..(count-1).
    * This will always be the first iteration returned from the combinations. The algorithm
    * then proceeds to increment the right-most value in the combination that can be incremented
    * w/o going over out of bounds of the src array. All subsequent offsets increment by 1 
    * following the incremented value. All iterations are complete when there are no more 
    * offsets that can be incremented w/o going out of bounds.
    * 
    *  For example:
    *  
    *  Given the set of 5 values with desired subset of 3 values, the following combinations 
    *  will be returned:
    *  
    *  0 1 2
    *  0 1 3
    *  0 1 4
    *  0 2 3
    *  0 2 4
    *  0 3 4
    *  1 2 3
    *  1 2 4
    *  1 3 4
    *  2 3 4
    */
   protected boolean prepareNextIteration()
   {
      int offsetToIncrement = calculateOffsetToIncrement();
      if (offsetToIncrement != -1)
      {
         int[] offsets = getOffsets();
         offsets[offsetToIncrement] = offsets[offsetToIncrement] + 1;
         for (int i = offsetToIncrement+1; i < offsets.length; i++)
         {
            offsets[i] = offsets[i-1] + 1; 
         }
      }
      return offsetToIncrement != -1;
   }
   
   /**
    * Returns the offset that should be incremented or -1 if there are no
    * further iterations
    */
   private int calculateOffsetToIncrement()
   {
      int[] offsets = getOffsets();
      for (int i = offsets.length - 1; i >= 0; i--)
      {
         if (offsets[i] < (getObjects().length - offsets.length + i))
            return i;
      }
      return -1;
   }
    
   /**
    * @see java.util.Iterator#remove()
    */
   public void remove()
   {
      throw new UnsupportedOperationException();
   }
}
 