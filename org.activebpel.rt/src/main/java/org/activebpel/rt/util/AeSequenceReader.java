// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeSequenceReader.java,v 1.3 2005/02/08 15:27:13 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.activebpel.rt.AeMessages;

/**
 * Joins two readers together much like <code>java.io.SequenceInputStream</code> 
 */
public class AeSequenceReader extends Reader
{
   /** our readers */
   private Reader[] mReaders;
   /** offset into readers array */
   private int mOffset;
   
   /**
    * Constructor accepts two readers to join together.
    * @param aFirst
    * @param aSecond
    */
   public AeSequenceReader(Reader aFirst, Reader aSecond)
   {
      if (aFirst== null || aSecond == null)
      {
         throw new IllegalArgumentException(AeMessages.getString("AeSequenceReader.ERROR_0")); //$NON-NLS-1$
      }
      mReaders = new Reader[2];
      mReaders[0] = aFirst;
      mReaders[1] = aSecond;
   }
   
   /**
    * Constructor accepts mutliple readers to join together
    * @param aIterOfReaders
    */
   public AeSequenceReader(Iterator aIterOfReaders)
   {
      List list = new LinkedList();
      while(aIterOfReaders.hasNext())
      {
         Reader reader = (Reader)aIterOfReaders.next();
         if (reader == null)
         {
            throw new IllegalArgumentException(AeMessages.getString("AeSequenceReader.ERROR_0")); //$NON-NLS-1$
         }
         list.add(reader);
      }
      mReaders = new Reader[list.size()];
      list.toArray(mReaders);
   }
   
   /**
    * @see java.io.Reader#read(char[], int, int)
    */
   public int read(char[] cbuf, int off, int len) throws IOException
   {
      try
      {
         int result = -1;
         while( hasMoreReaders() && (result = getDelegate().read(cbuf, off, len)) == -1)
            prepNextReader();
         return result;
      }
      catch (IOException e)
      {
         close();
         throw e;
      }
   }

   /**
    * Closes the current reader and moves onto the next
    */
   private void prepNextReader()
   {
      AeCloser.close(getDelegate());
      // null out the reader to help gc
      mReaders[mOffset] = null;
      // move onto the next one
      mOffset++;
   }

   /**
    * Returns the current reader
    */
   private Reader getDelegate()
   {
      return mReaders[mOffset];
   }

   /**
    * Returns true if there are more readers to read.
    */
   private boolean hasMoreReaders()
   {
      return mOffset < mReaders.length && getDelegate() != null;
   }

   /**
    * @see java.io.Reader#close()
    */
   public void close() throws IOException
   {
      for (int i = 0; i < mReaders.length; i++)
      {
         AeCloser.close(mReaders[i]);
      }
   }

}
