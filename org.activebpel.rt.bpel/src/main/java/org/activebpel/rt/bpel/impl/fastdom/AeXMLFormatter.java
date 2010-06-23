// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/fastdom/AeXMLFormatter.java,v 1.10 2008/03/20 19:13:59 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.fastdom;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Node;

/**
 * Formats an <code>AeFastNode</code> object to an XML stream.
 */
public class AeXMLFormatter implements IAeVisitor
{

   private static final String XML_ENCODING_PLACEHOLDER = "!ENCODING!"; //$NON-NLS-1$
   private static final String XML_DECLARATION_TEMPLATE = "<?xml version=\"1.0\" encoding=\"" + XML_ENCODING_PLACEHOLDER + "\"?>"; //$NON-NLS-1$ //$NON-NLS-2$

   /** Number of spaces per indent. */
   private static final int SPACES_PER_INDENT = 2;

   /** The name of the character encoding to use. */
   private final String mEncoding;

   /** The XML output writer. */
   private AeIndentingWriter mWriter;

   /** <code>true</code> if and only if an <code>IOException</code> occurred. */
   private boolean mHasFailed = false;
   /** controls whether the xml decl processing instruction is written when serialized */
   private boolean mIncludeDeclaration = true;
   

   /**
    * Constructs an XML formatter with the default UTF-8 character encoding.
    */
   public AeXMLFormatter()
   {
      this( AeUTF8Util.UTF8_ENCODING );
   }

   /**
    * Constructs an XML formatter that will use the specified character
    * encoding.
    *
    * @param aEncoding
    */
   public AeXMLFormatter(String aEncoding)
   {
      mEncoding = aEncoding;
   }

   /**
    * Escapes special attribute characters in a string that will appear as an
    * attribute value.
    */
   protected String escapeAttributeEntities(String aText)
   {
      return escapeEntities(aText, true);
   }

   /**
    * Escapes special element characters in a string that will appear in the
    * main body of an XML document.
    */
   protected String escapeElementEntities(String aText)
   {
      return escapeEntities(aText, false);
   }

   /**
    * Escapes special characters in the specified string. If
    * <code>aIsAttribute</code> is <code>true</code>, then this will perform
    * processing appropriate for attribute values.
    */
   protected String escapeEntities(String aText, boolean aIsAttribute)
   {
      return AeUtil.escapeXMLEntities(aText, aIsAttribute);
   }

   /**
    * Format the specified node to the specified output stream.
    *
    * @param aNode
    * @param aStream
    */
   public void format(AeFastNode aNode, OutputStream aStream) throws UnsupportedEncodingException
   {
      format(aNode, new OutputStreamWriter(aStream, getEncoding()));
   }

   /**
    * Format the specified node to the specified character writer. If you call
    * this method directly with your own <code>Writer</code>, you are
    * responsible for providing the appropriate character encoding description
    * in the constructor.
    *
    * @param aNode
    * @param aWriter
    */
   public void format(AeFastNode aNode, Writer aWriter)
   {
      mWriter = new AeIndentingWriter(aWriter);

      try
      {
         aNode.accept(this);
      }
      finally
      {
         try
         {
            mWriter.close();
         }
         catch (IOException e)
         {
            logIOException(e);
         }
      }

      // At this point, the caller could check hasFailed(), but we don't expect
      // I/O exceptions to occur on a character stream that's basically writing
      // to memory.
   }

   /**
    * Returns the XML declaration with the specified encoding.
    *
    * @param aEncoding
    */
   protected String getDeclaration(String aEncoding)
   {
      return XML_DECLARATION_TEMPLATE.replaceFirst(XML_ENCODING_PLACEHOLDER, aEncoding);
   }

   /**
    * Returns the name of the character encoding to use.
    */
   protected String getEncoding()
   {
      return mEncoding;
   }

   /**
    * Return <code>true</code> if and only if an <code>IOException</code>
    * occurred.
    */
   public boolean hasFailed()
   {
      return mHasFailed;
   }

   /**
    * Log an <code>IOException</code> and remember that it occurred.
    *
    * @param aException
    */
   protected void logIOException(IOException aException)
   {
      if (!mHasFailed)
      {
         // Report exception just once. Besides, we don't expect I/O exceptions
         // to occur on a character stream that's basically writing to memory.
         mHasFailed = true;
         AeException.logError(aException, "I/O exception"); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastAttribute)
    */
   public void visit(AeFastAttribute aAttribute)
   {
      try
      {
         mWriter.write(' ');
         mWriter.write(aAttribute.getName());
         mWriter.write("=\""); //$NON-NLS-1$
         mWriter.write(escapeAttributeEntities(aAttribute.getValue()));
         mWriter.write('\"');
      }
      catch (IOException e)
      {
         logIOException(e);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastDocument)
    */
   public void visit(AeFastDocument aDocument)
   {
      AeFastElement element = aDocument.getRootElement();
      if (element != null)
      {
         if (isIncludeDeclaration())
         {
            try
            {
               mWriter.writeln(getDeclaration(getEncoding()));
            }
            catch (IOException e)
            {
               logIOException(e);
            }
         }

         visit(element);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastElement)
    */
   public void visit(AeFastElement aElement)
   {
      try
      {
         // Start every element on a fresh line.
         mWriter.writeln();

         // Start the element tag.
         mWriter.indentIfFreshLine();
         mWriter.write('<');
         mWriter.write(aElement.getName());

         for (Iterator i = aElement.getAttributes().iterator(); i.hasNext(); )
         {
            visit((AeFastAttribute) i.next());
         }

         List children = aElement.getChildNodes();
         if (children.isEmpty())
         {
            // If there are no children, then close the element immediately.
            mWriter.writeln("/>"); //$NON-NLS-1$
         }
         else
         {
            // Otherwise, close the opening element tag, and then format the
            // element's children.
            mWriter.write('>');
            mWriter.incrementIndent();

            try
            {
               for (Iterator i = children.iterator(); i.hasNext(); )
               {
                  ((AeFastNode) i.next()).accept(this);
               }
            }
            finally
            {
               mWriter.decrementIndent();
               mWriter.indentIfFreshLine();
               mWriter.write("</"); //$NON-NLS-1$
               mWriter.write(aElement.getName());
               mWriter.writeln('>');
            }
         }
      }
      catch (IOException e)
      {
         logIOException(e);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeFastText)
    */
   public void visit(AeFastText aText)
   {
      try
      {
         mWriter.indentIfFreshLine();
         mWriter.write(escapeElementEntities(aText.getValue()));
      }
      catch (IOException e)
      {
         logIOException(e);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.fastdom.IAeVisitor#visit(org.activebpel.rt.bpel.impl.fastdom.AeForeignNode)
    */
   public void visit(AeForeignNode aForeignNode)
   {
      Node node = aForeignNode.getNode();

      if (node != null)
      {
         try
         {
            // Don't add newlines around text nodes.
            if (node.getNodeType() != Node.TEXT_NODE)
            {
               mWriter.writeln();
            }

            mWriter.indentIfFreshLine();
            mWriter.write(AeXMLParserBase.documentToString(node));

            // Don't add newlines around text nodes.
            if (node.getNodeType() != Node.TEXT_NODE)
            {
               mWriter.writeln();
            }
         }
         catch (IOException e)
         {
            logIOException(e);
         }
      }
   }

   /**
    * Wraps another <code>Writer</code> with methods to write new lines with
    * indenting white space at the beginning of each new line.
    */
   protected static class AeIndentingWriter extends Writer
   {
      /** The underlying character stream. */
      private final Writer mOut;

      /** The current indent level. */
      private int mIndent;

      /** <code>true</code> if and only if the writer is at the start of a fresh line. */
      private boolean mIsFreshLine = true;

      /** Character sequence to use to separate lines. */
      private final String mLineSeparator = System.getProperty("line.separator"); //$NON-NLS-1$

      /** Holds spaces for indentation. */
      private char[] mSpaces = new char[0];

      /**
       * Constructs an <code>AeIndentingPrintWriter</code> that wraps the
       * specified character writer.
       *
       * @param aOut
       */
      public AeIndentingWriter(Writer aOut)
      {
         mOut = aOut;
      }

      /**
       * @see java.io.Writer#close()
       */
      public void close() throws IOException
      {
         mOut.close();
      }

      /**
       * Decrements the current index.
       */
      public void decrementIndent()
      {
         if (--mIndent < 0)
         {
            throw new RuntimeException(AeMessages.getString("AeXMLFormatter.ERROR_12")); //$NON-NLS-1$
         }
      }

      /**
       * @see java.io.Writer#flush()
       */
      public void flush() throws IOException
      {
         mOut.flush();
      }

      /**
       * Returns number of spaces for the specified indent level.
       */
      protected int getIndentLength(int aIndent)
      {
         return aIndent * SPACES_PER_INDENT;
      }

      /**
       * Returns a character array containing at least the specified number of
       * spaces.
       *
       * @param aMinLength
       */
      protected char[] getSpaces(int aMinLength)
      {
         if (mSpaces.length < aMinLength)
         {
            mSpaces = new char[aMinLength << 1];

            for (int i = mSpaces.length; --i >= 0; )
            {
               mSpaces[i] = ' ';
            }
         }

         return mSpaces;
      }

      /**
       * Increment the current indent level.
       */
      public void incrementIndent()
      {
         ++mIndent;
      }

      /**
       * Output indentation only if this writer is at the start of a fresh
       * line.
       */
      public void indentIfFreshLine() throws IOException
      {
         if (mIsFreshLine && (mIndent > 0))
         {
            int length = getIndentLength(mIndent);
            mOut.write(getSpaces(length), 0, length);
            mIsFreshLine = false;
         }
      }

      /**
       * @see java.io.Writer#write(int)
       */
      public void write(int c) throws IOException
      {
         // Implement this for efficiency, as the base class version calls
         // write(char[], int, int) through a synchronized block and checks to
         // allocate a buffer.
         mOut.write(c);
         mIsFreshLine = false;
      }

      /**
       * @see java.io.Writer#write(char[], int, int)
       */
      public void write(char[] cbuf, int off, int len) throws IOException
      {
         if (len > 0)
         {
            mOut.write(cbuf, off, len);
            mIsFreshLine = false;
         }
      }

      /**
       * Writes a new line only if this writer is not already at the start of a
       * fresh line. This eliminates unnecessary blank lines.
       */
      public void writeln() throws IOException
      {
         if (!mIsFreshLine)
         {
            write(mLineSeparator);
            mIsFreshLine = true;
         }
      }

      /**
       * Writes the specified character followed by a new line.
       *
       * @param aChar
       */
      public void writeln(int aChar) throws IOException
      {
         write(aChar);
         writeln();
      }

      /**
       * Writes the specified string followed by a new line.
       *
       * @param aString
       */
      public void writeln(String aString) throws IOException
      {
         write(aString);
         writeln();
      }
   }

   /**
    * @return the includeDeclaration
    */
   public boolean isIncludeDeclaration()
   {
      return mIncludeDeclaration;
   }

   /**
    * @param aIncludeDeclaration the includeDeclaration to set
    */
   public void setIncludeDeclaration(boolean aIncludeDeclaration)
   {
      mIncludeDeclaration = aIncludeDeclaration;
   }
}
