// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeBlobInputStream.java,v 1.5 2007/12/17 19:45:06 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.activebpel.rt.AeException;

/**
 * Implementation of a known length stream. This implementation creates a temporary file that contains the
 * content of the stream passed in the constructor. The file length is the length of the stream. The close
 * method removes the temporary file.
 */
public class AeBlobInputStream extends FileInputStream
{
   /** Default temporary file name prefix. */
   public static final String DEFAULT_TEMP_STREAM_FILE_PREFIX = "aeblob"; //$NON-NLS-1$

   /** The temporary file name prefix for WSIO outbound binary streams. */
   public static final String WSIO_BINARY_STREAM_FILE_PREFIX = "aewsio"; //$NON-NLS-1$

   /** Default temporary file name suffix. */
   public static final String DEFAULT_TEMP_STREAM_FILE_SUFFIX = ".bin"; //$NON-NLS-1$

   /**
    * File holding the contents of the stream.
    */
   private File mBlobFile;
   
   /**
    * Constructor
    * @param aInputStream the stream for which the length can be returned
    * @throws AeException
    */
   public AeBlobInputStream(InputStream aInputStream) throws AeException, FileNotFoundException
   {
      this(aInputStream, DEFAULT_TEMP_STREAM_FILE_PREFIX, DEFAULT_TEMP_STREAM_FILE_SUFFIX);
   }

   /**
    * Constructor allows setting the prefix and suffix of the temporary file name
    * @param aInputStream
    * @param aFilePrefix
    * @param aFileSuffix
    * @throws AeException
    */
   public AeBlobInputStream(InputStream aInputStream, String aFilePrefix, String aFileSuffix) throws AeException, FileNotFoundException
   {
      this(AeUtil.createTempFile(aInputStream, aFilePrefix, aFileSuffix));
   }

   /**
    * Constructs an input stream on the given file without automatically
    * deleting the file when the stream is closed.
    *
    * @param aBlobFile
    * @throws FileNotFoundException
    */
   public AeBlobInputStream(File aBlobFile) throws FileNotFoundException
   {
      super(aBlobFile);

      mBlobFile = aBlobFile;
   }

   /**
    * Overrides method to delete temporary file.
    * 
    * @see java.io.FilterInputStream#close()
    */
   public void close() throws IOException 
   {
      try
      {
         super.close();
      }
      finally
      {
         if ( mBlobFile != null )
         {
            mBlobFile.delete();
            mBlobFile = null;
         }
      }
   }

   /**
    * Returns the <code>File</code> object corresponding to the underlying file.
    */
   public File getBlobFile()
   {
      return mBlobFile;
   }

   /**
    * Returns the length of the underlying file.
    */
   public int length()
   {
      return (int) getBlobFile().length();
   }
}
