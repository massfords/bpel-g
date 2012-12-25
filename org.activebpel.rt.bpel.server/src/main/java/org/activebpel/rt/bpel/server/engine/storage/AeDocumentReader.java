// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeDocumentReader.java,v 1.2 2004/09/07 22:13:43 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage;

import java.io.IOException;
import java.io.Reader;

import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.w3c.dom.Document;

/**
 * Abstract class that delivers an <code>AeFastDocument</code> or standard XML
 * <code>Document</code> as a fixed-length character stream (Java
 * <code>Reader</code>). Subclasses implement the <code>createReader</code> and
 * <code>getLength</code> methods.
 */
public abstract class AeDocumentReader extends Reader
{
   /** The underlying character stream that serializes the XML document. */
   private Reader mReader;

   /**
    * Creates the underlying character stream.
    *
    * @return Reader
    */
   protected abstract Reader createReader();

   /**
    * Returns a character stream that serializes the specified
    * <code>AeFastDocument</code>.
    *
    * @param aDocument The document to serialize.
    * @return AeDocumentReader
    */
   public static AeDocumentReader getDocumentReader(AeFastDocument aDocument)
   {
      return new AeCharArrayDocumentReader(aDocument);
   }

   /**
    * Returns a character stream that serializes the specified standard XML
    * document.
    *
    * @param aDocument The document to serialize.
    * @return AeDocumentReader
    */
   public static AeDocumentReader getDocumentReader(Document aDocument)
   {
      return new AeCharArrayDocumentReader(aDocument);
   }

   /**
    * Returns the underlying character stream.
    */
   public Reader getReader()
   {
      if (mReader == null)
      {
         mReader = createReader();
      }

      return mReader;
   }

   /**
    * Returns the length of the serialized document.
    *
    * @return int
    */
   public abstract int getLength();

   /*======================================================================
    * java.io.Reader methods
    *======================================================================
    */

   /**
    * @see java.io.Reader#close()
    */
   public void close() throws IOException
   {
      getReader().close();
   }

   /**
    * @see java.io.Reader#read()
    */
   public int read() throws IOException
   {
      return getReader().read();
   }

   /**
    * @see java.io.Reader#read(char[])
    */
   public int read(char[] aBuffer) throws IOException
   {
      return getReader().read(aBuffer);
   }

   /**
    * @see java.io.Reader#read(char[], int, int)
    */
   public int read(char[] aBuffer, int aOffset, int aLength) throws IOException
   {
      return getReader().read(aBuffer, aOffset, aLength);
   }

   /**
    * @see java.io.Reader#skip(long)
    */
   public long skip(long aCount) throws IOException
   {
      return getReader().skip(aCount);
   }
}
