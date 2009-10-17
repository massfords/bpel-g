// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/util/AeUnsynchronizedCharArrayWriter.java,v 1.2 2006/07/14 21:44:42 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.util;

import java.io.CharArrayWriter;

/**
 * Extends <code>CharArrayWriter</code> with unsynchronized write methods.
 */
public class AeUnsynchronizedCharArrayWriter extends CharArrayWriter
{
   private static final int DEFAULT_CAPACITY = 8192;

   /**
    * Default constructor.
    */
   public AeUnsynchronizedCharArrayWriter()
   {
      super(DEFAULT_CAPACITY);
   }

   /**
    * Ensures that <code>mBuffer</code> has at least the specified capacity.
    *
    * @param aCapacity
    */
   protected void ensureCapacity(int aCapacity)
   {
      if (aCapacity > buf.length)
      {
         char[] newBuffer = new char[aCapacity + buf.length];
         System.arraycopy(buf, 0, newBuffer, 0, count);
         buf = newBuffer;
      }
   }

   /**
    * @see java.io.Writer#write(int)
    */
   public void write(int c)
   {
      int newCount = count + 1;
      ensureCapacity(newCount);
      buf[count] = (char) c;
      count = newCount;
   }

   /**
    * @see java.io.Writer#write(char[], int, int)
    */
   public void write(char[] cbuf, int off, int len)
   {
      int newCount = count + len;
      ensureCapacity(newCount);
      System.arraycopy(cbuf, off, buf, count, len);
      count = newCount;
   }

   /**
    * @see java.io.Writer#write(java.lang.String, int, int)
    */
   public void write(String str, int off, int len)
   {
      int newCount = count + len;
      ensureCapacity(newCount);
      str.getChars(off, off + len, buf, count);
      count = newCount;
   }
   
   /**
    * @see java.io.Writer#write(java.lang.String)
    */
   public void write(String aStr)
   {
      write(aStr, 0, aStr.length());
   }
}
