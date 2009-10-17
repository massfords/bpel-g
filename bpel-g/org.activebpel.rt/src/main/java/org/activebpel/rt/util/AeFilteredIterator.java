//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeFilteredIterator.java,v 1.2 2006/06/26 16:46:46 mford Exp $
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

/**
 * Allows base class for filtering contents of an iterator
 */
public abstract class AeFilteredIterator implements Iterator
{
   /** The iter that we're delegating to */
   private Iterator mDelegate;
   /** The next object to return */
   private Object mNext;
   /** Indicates if we have a next object. */
   private Boolean mHasNext;
   
   /**
    * Creates the filtered iterator the delegate iterator.
    * @param aDelegate
    */
   public AeFilteredIterator(Iterator aDelegate)
   {
      mDelegate = aDelegate;
   }

   /**
    * @see java.util.Iterator#hasNext()
    */
   public boolean hasNext()
   {
      checkForNext();
      
      return getHasNext().booleanValue();
   }

   /**
    * If we don't know if there's a next object, then read for it 
    * and record the object value. This will also set the hasNext Boolean
    * to indicate if we found a value since a null is an acceptable value.
    */
   protected void checkForNext()
   {
      if (getHasNext() == null)
      {
         setNext(readNextElement());
      }
   }

   /**
    * @see java.util.Iterator#next()
    */
   public Object next()
   {
      checkForNext();
      
      if (!getHasNext().booleanValue())
      {
         throw new NoSuchElementException();
      }
      
      Object next = getNext();
      setNext(null);
      setHasNext(null);
      return next;
   }
   
   /**
    * Reads the next element from the delegate iterator that is
    * accepted by the filter
    */
   protected Object readNextElement()
   {
      setHasNext(Boolean.FALSE);
      
      Object accepted = null;
      while(getDelegate().hasNext() && getHasNext().equals(Boolean.FALSE))
      {
         Object next = getDelegate().next();
         if (accept(next))
         {
            accepted = next;
            setHasNext(Boolean.TRUE);
         }
      }
      
      return accepted;
   }
   
   /**
    * Abstract accept method that is implemented by subclasses to provide the
    * type of filtering needed.
    * @param aObject
    */
   protected abstract boolean accept(Object aObject);

   /**
    * @see java.util.Iterator#remove()
    */
   public void remove()
   {
      throw new UnsupportedOperationException();
   }
   
   /**
    * @return Returns the next obj or null
    */
   protected Object getNext()
   {
      return mNext;
   }
   
   /**
    * @param aNext The next obj or null
    */
   protected void setNext(Object aNext)
   {
      mNext = aNext;
   }

   /**
    * @return Returns the delegate.
    */
   protected Iterator getDelegate()
   {
      return mDelegate;
   }

   /**
    * @param aDelegate The delegate to set.
    */
   protected void setDelegate(Iterator aDelegate)
   {
      mDelegate = aDelegate;
   }
   
   /**
    * Setter for the hasNext flag
    * @param aBoolean
    */
   protected void setHasNext(Boolean aBoolean)
   {
      mHasNext = aBoolean;
   }
   
   /**
    * Getter for the hasNext flag
    */
   protected Boolean getHasNext()
   {
      return mHasNext;
   }
}
 