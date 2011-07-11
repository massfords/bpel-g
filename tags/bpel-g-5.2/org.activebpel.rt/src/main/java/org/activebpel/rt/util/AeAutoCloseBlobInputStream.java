//$Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeAutoCloseBlobInputStream.java,v 1.2 2008/02/08 17:54:15 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;

/**
 * Closes an underlying stream as soon as the end of the stream is reached,
 */
public class AeAutoCloseBlobInputStream extends AeBlobInputStream
{

   /**
    * Constructor
    * @param aInputStream
    * @param aFilePrefix
    * @param aFileSuffix
    * @throws AeException
    * @throws FileNotFoundException
    */
   public AeAutoCloseBlobInputStream(InputStream aInputStream, String aFilePrefix, String aFileSuffix) throws AeException, FileNotFoundException
   {
      super(aInputStream, aFilePrefix, aFileSuffix);

   }
   
   /**
    * Constructor
    * @param aInputStream the stream for which the length can be returned
    * @throws AeException
    */
   public AeAutoCloseBlobInputStream(InputStream aInputStream) throws AeException, FileNotFoundException
   {
      super(aInputStream);
   }

   /**
    * True if this stream is open. Assume that the underlying stream is open until we get an EOF indication.
    */
   private boolean mStreamOpen = true;

   /** True if the stream closed itself. */
   private boolean mSelfClosed = false;

   /**
    * Reads the next byte of data from the input stream.
    * @throws IOException when there is an error reading
    * @return the character read, or -1 for EOF
    */
   public int read() throws IOException
   {
      int l = -1;

      if ( isReadAllowed() )
      {
         // underlying stream not closed, go ahead and read.
         l = super.read();
         checkClose(l);
      }

      return l;
   }

   /**
    * Reads up to <code>len</code> bytes of data from the stream.
    * @param b a <code>byte</code> array to read data into
    * @param off an offset within the array to store data
    * @param len the maximum number of bytes to read
    * @return the number of bytes read or -1 for EOF
    * @throws IOException if there are errors reading
    */
   public int read(byte[] b, int off, int len) throws IOException
   {
      int l = -1;

      if ( isReadAllowed() )
      {
         l = super.read(b, off, len);
         checkClose(l);
      }
      return l;
   }

   /**
    * Reads some number of bytes from the input stream and stores them into the buffer array b.
    * @param b a <code>byte</code> array to read data into
    * @return the number of bytes read or -1 for EOF
    * @throws IOException if there are errors reading
    */
   public int read(byte[] b) throws IOException
   {
      int l = -1;

      if ( isReadAllowed() )
      {
         l = super.read(b);
         checkClose(l);
      }
      return l;
   }

   /**
    * Close the underlying stream should the end of the stream arrive.
    * @param readResult The result of the read operation to check.
    * @throws IOException If an IO problem occurs.
    */
   private void checkClose(int readResult) throws IOException
   {
      if ( readResult == -1 )
      {
         if ( mStreamOpen )
         {
            super.close();
            mStreamOpen = false;
         }
      }
   }

   /**
    * See whether a read of the underlying stream should be allowed, and if not, check to see whether our
    * stream has already been closed!
    * @return <code>true</code> if it is still OK to read from the stream.
    * @throws IOException If an IO problem occurs.
    */
   private boolean isReadAllowed() throws IOException
   {
      if ( !mStreamOpen && mSelfClosed )
      {
         throw new IOException(AeMessages.getString("AeAutoCloseBlobInputStream.ERROR_ReadClosedStream")); //$NON-NLS-1$
      }
      return mStreamOpen;
   }
}
