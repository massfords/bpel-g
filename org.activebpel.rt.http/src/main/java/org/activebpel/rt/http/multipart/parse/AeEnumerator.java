//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.http/src/org/activebpel/rt/http/multipart/parse/AeEnumerator.java,v 1.1.4.1 2008/04/21 16:15:52 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.http.multipart.parse;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.activebpel.rt.http.AeMessages;

/**
 * Utility class that makes it easy to interchange Enumeration to iterator and Iterator to Enumeration.
 * <p>Construct with an Iterator then Enumerate over it.</p>
 * <p>OR</p>
 * <p>Construct with an Enumeration then Iterate over it.</p>
 * 
 */
public class AeEnumerator implements Enumeration, Iterator
{

   /** The Iterator to be Enumerated */
   Iterator mIterator = null;

   /** The Enumeration to be iterated */
   Enumeration mEnumeration = null;

   /**
    * Constructor
    * @param aIterator
    */
   public AeEnumerator(Iterator aIterator)
   {
      super();
      mIterator = aIterator;
   }

   /**
    * Constructor
    * @param aEnumeration
    */
   public AeEnumerator(Enumeration aEnumeration)
   {
      super();
      mEnumeration = aEnumeration;
   }

   /**
    * 
    * @see java.util.Iterator#hasNext()
    */
   public boolean hasNext()
   {
      return (mIterator == null) ? (mEnumeration == null) ? false : mEnumeration.hasMoreElements() : mIterator.hasNext();
   }

   /**
    * 
    * @see java.util.Iterator#next()
    */
   public Object next() throws NoSuchElementException
   {
      if ( mIterator == null && mEnumeration == null )
         throw new NoSuchElementException(AeMessages.getString("Iterator is null.")); //$NON-NLS-1$
      return (mIterator == null) ? mEnumeration.nextElement() : mIterator.next();
   }

   public void remove() throws UnsupportedOperationException, IllegalStateException
   {
      if ( mIterator == null )
         throw new IllegalStateException(AeMessages.getString("Iterator is null.")); //$NON-NLS-1$
      mIterator.remove();
   }

   /**
    * 
    * @see java.util.Enumeration#hasMoreElements()
    */
   public boolean hasMoreElements()
   {
      return hasNext();
   }

   /**
    * 
    * @see java.util.Enumeration#nextElement()
    */
   public Object nextElement() throws NoSuchElementException
   {
      return next();
   }
}
