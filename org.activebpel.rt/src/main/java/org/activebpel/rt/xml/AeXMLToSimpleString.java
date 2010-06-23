// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/AeXMLToSimpleString.java,v 1.7 2006/06/26 16:46:44 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.util.AeUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class extends the SAX DefaultHandler and is used to extract the 
 * core XML from an input stream.
 * todo This now sorts the attributes to ensure different parsers produce the same simple string, v1.0 didn't sort, 
 *      so awf needs this newer version when running debug.
 */
public class AeXMLToSimpleString extends DefaultHandler
{
   /** Flag indicating if whitespace should be trimmed when parsing */
   private boolean mTrimWhitespace;
   
   /** The buffer which holds the output of the parse */
   private StringBuffer mOutput;
   
   /** The map of namespace declarations */
   private TreeMap mNamespaces = new TreeMap();
   
   /**
    * This method takes as input a stream which represents an XML document, and
    * extracts the core tags, attributes and data. All unnecessary whitespace and 
    * comments are removed, based on flag passed in. This method is useful in 
    * preparing XML documents for comparison. 
    * 
    * @param aInput the input stream representing an XML document
    * @param aTrimWhitespace True to trim whitespace False to preserve
    */
   public static String extractCoreXML(InputStream aInput, boolean aTrimWhitespace)
   {
      return extractCoreXML(new InputSource(aInput), aTrimWhitespace);
   }
   
   /**
    * This method takes as input a inputsource which represents an XML document, and
    * extracts the core tags, attributes and data. All unnecessary whitespace and 
    * comments are removed, based on flag passed in. This method is useful in 
    * preparing XML documents for comparison. 
    * 
    * @param aInputSource the input stream representing an XML document
    * @param aTrimWhitespace True to trim whitespace False to preserve
    */
   public static String extractCoreXML(InputSource aInputSource, boolean aTrimWhitespace)
   {
      try
      {
         SAXParserFactory factory = SAXParserFactory.newInstance();
         factory.setNamespaceAware(true);

         AeXMLToSimpleString handler = new AeXMLToSimpleString(aTrimWhitespace);
         SAXParser parser = factory.newSAXParser();
         parser.parse(aInputSource, handler);
         
         return handler.getOutput();
      }
      catch (Exception e)
      {
         AeException.logError(e, AeMessages.getString("AeXMLToSimpleString.ERROR_0")); //$NON-NLS-1$
         return "";       //$NON-NLS-1$
      }
      
   }

   /**
    * Constructor which takes as input a flag indicating if whitespace should
    * be trimmed from the document during parse. 
    * @param aTrimWhitespace True to trim whitespace, False otherwise
    */
   private AeXMLToSimpleString(boolean aTrimWhitespace)
   {
      mTrimWhitespace = aTrimWhitespace;
   }

   /**
    * Returns the ouput result of the parse.
    */   
   public String getOutput()
   {
      return (mOutput == null ? "" : mOutput.toString()); //$NON-NLS-1$
   }

   /**
    * Creates the output buffer at beginning of parse.
    * @see org.xml.sax.ContentHandler#startDocument()
    */
   public void startDocument() throws SAXException
   {
      mOutput = new StringBuffer(512);
   }

   /**
    * Handles creation of element tag and any attributes as well as namespace declarations
    * it contains.  Note the attributes and namespaces are sorted to ensure the comparisons
    * are successful.
    * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
    */
   public void startElement(String aURI, String aLocalName, String aQName, Attributes aAttrs) throws SAXException
   {
      mOutput.append("<").append(aLocalName); //$NON-NLS-1$
      
      // Sort the attribute declarations and add to the element
      TreeMap map = new TreeMap();
      for (int i=0, len = aAttrs.getLength(); i < len; i++)
         map.put(aAttrs.getQName(i), aAttrs.getValue(i));
      for(Iterator iter= map.entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry)iter.next();
         mOutput.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }

      // Add the sorted namespace declarations we may have collected, then reset the namespaces
      for (Iterator iter=mNamespaces.entrySet().iterator(); iter.hasNext(); )
      {
         Map.Entry entry = (Map.Entry)iter.next();
         mOutput.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
      mNamespaces.clear();

      mOutput.append(">"); //$NON-NLS-1$
   }

   /**
    * Handles creation of end element tag.
    * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
    */
   public void endElement (String aURI, String aLocalName, String aQName) throws SAXException
   {
      mOutput.append("<").append(aLocalName).append(">"); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Collects the prefix mapping which is used in the next element declaration.
    * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
    */
   public void startPrefixMapping (String aPrefix, String aURI) throws SAXException
   {
      String name = "xmlns"; //$NON-NLS-1$
      if (! AeUtil.isNullOrEmpty(aPrefix))
         name += (":" + aPrefix); //$NON-NLS-1$
      mNamespaces.put(name, aURI);
   }

   /**
    * Handles output of text data contained within the document.
    * @see org.xml.sax.ContentHandler#characters(char[], int, int)
    */
   public void characters (char aChars[], int aStart, int aLength) throws SAXException
   {
      // Trim the whitespace before appending
      if (mTrimWhitespace)
         mOutput.append(new String(aChars, aStart, aLength).trim());
      else
         mOutput.append(aChars, aStart, aLength);
   }
} 
