// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/AeXIncludeAwareXMLParser.java,v 1.2 2008/02/17 21:09:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;

import org.activebpel.rt.AeException;
import org.activebpel.rt.util.AeCloser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * This class will setup an XInclude aware parser.
 */
public class AeXIncludeAwareXMLParser
{
   /** an xml parser to delegate calls to. */
   private AeXMLParserBase mParser = null;
   
   /**
    * Default Constructor. Sets up a default XInclude aware parser
    */
   public AeXIncludeAwareXMLParser()
   {
      AeXMLParserBase parser = new AeXMLParserBase();
      parser.setFeature(IAeXercesFeatures.XINCLUDE_FEATURE, true);
      setParser(parser);
   }

   /**
    * Constructor. creates a xinclude aware parser with an error handler to use during parsing.
    * @param aHandler
    */
   public AeXIncludeAwareXMLParser(AeXMLParserErrorHandler aHandler)
   {
      AeXMLParserBase parser = new AeXMLParserBase(aHandler);
      parser.setFeature(IAeXercesFeatures.XINCLUDE_FEATURE, true);
      setParser(parser);
   }

   /**
    * Constructor. creates a XInclude aware parser configured with namespace awareness and validation
    * @param aNamespaceAware
    * @param aValidating
    */
   public AeXIncludeAwareXMLParser(boolean aNamespaceAware, boolean aValidating)
   {
      AeXMLParserBase parser = new AeXMLParserBase(aNamespaceAware, aValidating);
      parser.setFeature(IAeXercesFeatures.XINCLUDE_FEATURE, true);
      setParser(parser);
   }
   
   /**
    * Sets the error handler to be used during parse operations.
    * @param aErrorHandler the error handler to be set
    */
   public void setErrorHandler(AeXMLParserErrorHandler aErrorHandler)
   {
      getParser().setErrorHandler(aErrorHandler);
   }
   
   /**
    * This method will validate the Document looking for parse errors. An iterator of
    * Schema objects can optionally be passed if schema validation is to be performed.
    *
    * @param aDocument the document to be validated
    * @param aSchemas an Iterator of Schema objects or null if not performing
    *         schema validation.
    * @throws AeException if an error occurs parsing the document
    */
   public void validateDocument(Document aDocument, Iterator aSchemas) throws AeException
   {
      getParser().validateDocument(aDocument, aSchemas);
   }

   /**
    * Take a <code>InputStream</code> and uri to properly setup the 
    * <code>InputSource</code> before sending it along to the <code>AeXMLParserBase</code>. 
    * An iterator of Schema objects can optionally be passed if schema validation is to be performed.
    * 
    * @param aInput
    * @param aResourceURI
    * @param aSchemas
    * @throws AeException
    */
   public Document loadDocument(InputStream aInput, String aResourceURI, Iterator aSchemas) throws AeException
   {
      try
      {
         InputSource source = new InputSource(aInput);
         source.setSystemId(aResourceURI);
         return loadDocument(source, aSchemas);
      }
      finally
      {
         AeCloser.close(aInput);
      }
   }
   
   /**
    * Take a <code>Reader</code> and uri to properly setup the 
    * <code>InputSource</code> before sending it along to the <code>AeXMLParserBase</code>. 
    * An iterator of Schema objects can optionally be passed if schema validation is to be performed.
    * 
    * @param aInput
    * @param aResourceURI
    * @param aSchemas
    * @throws AeException
    */
   public Document loadDocument(Reader aInput, String aResourceURI, Iterator aSchemas) throws AeException
   {
      try
      {
         InputSource source = new InputSource(aInput);
         source.setSystemId(aResourceURI);
         return loadDocument(source, aSchemas);
      }
      finally
      {
         AeCloser.close(aInput);
      }
   }
   
   /**
    * This method will load the Document for the given input stream with settings
    * defined by the concrete class. An iterator of Schema objects can optionally
    * be passed if schema validation is to be performed.
    *
    * @param aInput the input stream to load the document from.
    * @param aSchemas an Iterator of Schema objects or null if not performing
    *         schema validation.
    * @return the document loaded or null if an error has occurred during parse
    * @throws AeException
    */
   public Document loadDocument(InputSource aInput, Iterator aSchemas) throws AeException
   {
      return getParser().loadDocument(aInput, aSchemas);
   }

   /**
    * @return Returns the parser.
    */
   protected AeXMLParserBase getParser()
   {
      return mParser;
   }

   /**
    * @param aParser the parser to set
    */
   protected void setParser(AeXMLParserBase aParser)
   {
      mParser = aParser;
   }
}
