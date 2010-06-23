// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/xml/AeXMLParserBase.java,v 1.48 2008/01/29 23:00:17 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.xml.WSDLLocator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.activebpel.rt.AeException;
import org.activebpel.rt.AeMessages;
import org.activebpel.rt.IAeConstants;
import org.activebpel.rt.schemas.AeStandardSchemas;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeSchemaUtil;
import org.exolab.castor.xml.schema.Schema;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class is a base for all those implementing a parse routine. This class
 * explicitly forces the use of the Xerces parser.
 */
public class AeXMLParserBase
{
   /** JAXP Schema language definition. */
   protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage"; //$NON-NLS-1$

   /** JAXP Schema source definition. */
   protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource"; //$NON-NLS-1$

   /** Extension property for setting indent levels for 1.5 vm. */
   protected static final String INDENT_AMOUNT_XALAN = "{http://xml.apache.org/xalan}indent-amount"; //$NON-NLS-1$

   /** Extension property for setting indent levels for 1.4 vm. */
   protected static final String INDENT_AMOUNT_XSLT = "{http://xml.apache.org/xslt}indent-amount"; //$NON-NLS-1$

   /** The error handler which is installed for this parser */
   protected AeXMLParserErrorHandler mErrorHandler;

   /** Flag indicating that the parser should be namespace aware */
   private boolean mNamespaceAware = true;

   /** Flag indicating that the parser should enable validation */
   private boolean mValidating = true;

   /** Cache transformer factory. */
   private static TransformerFactory sTransformerFactory = null;

   /** WSDL locator to use to locate schema resources. */
   private WSDLLocator mWSDLLocator;
   
   /** map of features to enable / disable on the parser */
   private Map mFeatures = new HashMap();

   /**
    * Default constructor
    */
   public AeXMLParserBase()
   {
   }

   /**
    * Constructor.
    *
    * @param aNamespaceAware
    * @param aValidating
    */
   public AeXMLParserBase(boolean aNamespaceAware, boolean aValidating)
   {
      setNamespaceAware(aNamespaceAware);
      setValidating(aValidating);
   }

   /**
    * Constructor which accepts an error handler to use during parsing.
    * @param aHandler the handler to be used during parse.
    */
   public AeXMLParserBase(AeXMLParserErrorHandler aHandler)
   {
      setErrorHandler(aHandler);
   }


   /**
    * This method will load the Document for the given xml string with settings
    * defined by the concrete class. An iterator of Schema objects can optionally
    * be passed if schema validation is to be performed.
    *
    * @param aXmlString the xml string.
    * @param aSchemas an Iterator of Schema objects or null if not performing
    *         schema validation.
    * @return the document loaded or null if an error has occurred during parse
    * @throws AeException
    */
   public Document loadDocumentFromString(String aXmlString, Iterator aSchemas) throws AeException
   {
      // The WebLogic 9.2 XML parser emits an error message, "[Fatal Error]
      // :-1:-1: Premature end of file," whenever it parses an empty string, so
      // don't give it an empty string.
      if (AeUtil.isNullOrEmpty(aXmlString))
      {
         throw new AeException(AeMessages.getString(AeMessages.getString("AeXMLParserBase.ERROR_PrematureEndOfFile"))); //$NON-NLS-1$
      }

      Reader reader = new StringReader(aXmlString);
      return loadDocument(reader, aSchemas);
   }

   /**
    * This method will load the Document for the given filename with settings
    * defined by the concrete class. An iterator of Schema objects can optionally
    * be passed if schema validation is to be performed.
    *
    * @param aFilename the file to be loaded
    * @param aSchemas an Iterator of Schema objects or null if not performing
    *         schema validation.
    * @return the document loaded or null if an error has occurred during parse
    * @throws AeException
    */
   public Document loadDocument(String aFilename, Iterator aSchemas) throws AeException
   {
      Document document = null;
      try
      {
         InputSource source = new InputSource(new FileInputStream(aFilename));
         source.setSystemId(aFilename);
         document = loadDocument(source, aSchemas);
      }
      catch (FileNotFoundException e)
      {
         String msg = AeMessages.getString("AeXMLParserBase.ERROR_2") + aFilename; //$NON-NLS-1$
         throw new AeException(msg, e);
      }

      return document;
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
      Document document = null;
      ClassLoader previousClassLoader = null;
      try
      {
         previousClassLoader = Thread.currentThread().getContextClassLoader();
         // Set class loader to that which loaded us, to ensure we load the xerces parser
         Thread.currentThread().setContextClassLoader(AeXMLParserBase.class.getClassLoader());

         // Create factory for parsing and turn on validation and namespaces
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(isNamespaceAware());
         resetParseWarnings();

         List schemas = AeUtil.toList(aSchemas);
         List schemaInputStreams = isValidating() ? serializeSchemas(schemas) : Collections.EMPTY_LIST;

         // Don't validate if we have no schemas to validate against.
         if (!schemaInputStreams.isEmpty())
         {
            // Only set the validating flag if it would be possible to validate.
            factory.setValidating(isValidating());

            // Instruct the factory which schema we will be validating against
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, IAeConstants.W3C_XML_SCHEMA);
            // Set the the schema list for the parser to use during validation.
            factory.setAttribute(JAXP_SCHEMA_SOURCE, schemaInputStreams.toArray());
         }
         
         // set the features on the builder
         for (Iterator iter = getFeatures().entrySet().iterator(); iter.hasNext();)
         {
            Map.Entry entry = (Map.Entry) iter.next();
            factory.setAttribute((String) entry.getKey(), entry.getValue());
         }

         // Create the document builder and parse the filename
         DocumentBuilder db = factory.newDocumentBuilder();
         db.setEntityResolver(new AeXMLParserSchemaHandler(getWSDLLocator(), schemas));
         db.setErrorHandler(getErrorHandler());
         document = db.parse(aInput);
      }
      catch (IllegalArgumentException e)
      {
         throw new AeException(AeMessages.getString("AeXMLParserBase.ERROR_3"), e); //$NON-NLS-1$
      }
      catch(Exception e)
      {
         throw new AeException(AeMessages.getString("AeXMLParserBase.ERROR_4"), e); //$NON-NLS-1$
      }
      finally
      {
         if( previousClassLoader != null )
         {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
         }
      }

      return document;
   }

   /**
    * This method will load the Document for the given input stream with settings
    * defined by the concrete class. An iterator of Schema objects can optionally
    * be passed if schema validation is to be performed.
    *
    * @param aInput the input stream to load the document from. This stream
    *         gets closed as part of this call.
    * @param aSchemas an Iterator of Schema objects or null if not performing
    *         schema validation.
    * @return the document loaded or null if an error has occurred during parse
    * @throws AeException
    */
   public Document loadDocument(InputStream aInput, Iterator aSchemas) throws AeException
   {
      try
      {
         return loadDocument(new InputSource(aInput), aSchemas);
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
    * @param aInput the input stream to load the document from. This stream
    *         gets closed as part of this call.
    * @param aSchemas an Iterator of Schema objects or null if not performing
    *         schema validation.
    * @return the document loaded or null if an error has occurred during parse
    * @throws AeException
    */
   public Document loadDocument(Reader aInput, Iterator aSchemas) throws AeException
   {
      try
      {
         return loadDocument(new InputSource(aInput), aSchemas);
      }
      finally
      {
         AeCloser.close(aInput);
      }
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
      ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         // Set class loader to that which loaded us, to ensure we load the xerces parser
         Thread.currentThread().setContextClassLoader(AeXMLParserBase.class.getClassLoader());

         // Create factory for parsing and turn on validation and namespaces
         SAXParserFactory factory = SAXParserFactory.newInstance();
         factory.setNamespaceAware(isNamespaceAware());
         factory.setValidating(true);
         resetParseWarnings();

         // Create the SAX parser and set any schemas which were passed to us
         SAXParser parser = factory.newSAXParser();

         List schemas = AeUtil.toList(aSchemas);
         List schemaInputStreams = isValidating() ? serializeSchemas(schemas) : Collections.EMPTY_LIST;

         // Note: we always set the JAXP schema related properties, even
         // if there are no schemas in the list.  I haven't tracked down
         // exactly why this is needed yet, but at first glance it appears
         // that the parser tries to do DTD validation (with a null DTD)
         // if we don't set the properties.

         // Instruct the factory which schema we will be validating against
         parser.setProperty(JAXP_SCHEMA_LANGUAGE, IAeConstants.W3C_XML_SCHEMA);
         // Set the the schema list for the parser to use during validation.
         parser.setProperty(JAXP_SCHEMA_SOURCE, schemaInputStreams.toArray());

         // set the features on the factory
         for (Iterator iter = getFeatures().entrySet().iterator(); iter.hasNext();)
         {
            Map.Entry entry = (Map.Entry) iter.next();
            Boolean bool = (Boolean) entry.getValue();
            factory.setFeature((String) entry.getKey(), bool.booleanValue());
         }

         // Transform the document into an input source and parse it looking for errors
         StringReader reader = new StringReader(documentToString(aDocument));
         parser.parse(
               new InputSource(reader),
               new AeXMLParserSchemaHandler(getWSDLLocator(), getErrorHandler(), schemas));
      }
      catch (IllegalArgumentException e)
      {
         throw new AeException(AeMessages.getString("AeXMLParserBase.ERROR_3"), e); //$NON-NLS-1$
      }
      catch(Exception e)
      {
         throw new AeException(AeMessages.getString("AeXMLParserBase.ERROR_4"), e); //$NON-NLS-1$
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(previousClassLoader);
      }
   }

   /**
    * This method is used to create an empty document as a starting node. If an
    * error occurs null will be returned.
    */
   public Document createDocument()
   {
      try
      {
         return AeXmlUtil.getDocumentBuilder(isNamespaceAware(), isValidating()).newDocument();
      }
      catch(Exception e)
      {
         AeException.logError(e, AeMessages.getString("AeXMLParserBase.ERROR_7")); //$NON-NLS-1$
      }

      return null;
   }

   /**
    * Writes the node to the writer
    * @param aNode
    * @param aWriter
    * @throws IOException
    */
   public static void writeNode(Node aNode, Writer aWriter) throws IOException
   {
      String s = documentToString(aNode, true);
      aWriter.write(s);
   }

   /**
    * This method will return the String representation of a document.
    * @param aNode the node to be converted.
    */
   public static String documentToString(Node aNode)
   {
      return documentToString(aNode, false);
   }

   /**
    * This method will return the String representation of a document.
    * @param aNode the node to be converted.
    * @param aIndentFlag true if you want pretty printing
    */
   public static String documentToString(Node aNode, boolean aIndentFlag)
   {
      return documentToString(aNode, aIndentFlag, true);
   }
   
   /**
    * This method will return the String representation of a document.
    * @param aNode the node to be converted.
    * @param aIndentFlag true if you want pretty printing
    * @param aOmitXmlDecl false if you want an xml declaration in the returned xml
    */
   public static String documentToString(Node aNode, boolean aIndentFlag, boolean aOmitXmlDecl)
   {
      StringWriter writer = new StringWriter();

      // Fix for defect 2065, "Attempting to view the data of a complex or
      // simple schema type variable on the Admin console displays 'Variable not
      // initialized'":
      //
      // If aNode is just a Text node, then the Transformer writes just an empty
      // string. I don't know if this is new behavior, but the easy workaround
      // is to get the string from the Text node directly.
      if (aNode.getNodeType() == Node.TEXT_NODE)
      {
         writer.write(aNode.getNodeValue());
      }
      else
      {
         try
         {
            Transformer transformer = getTransformerFactory().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, aOmitXmlDecl? "yes" : "no" ); //$NON-NLS-1$ //$NON-NLS-2$
            transformer.setOutputProperty(OutputKeys.INDENT, aIndentFlag? "yes" : "no" ); //$NON-NLS-1$ //$NON-NLS-2$
            transformer.setOutputProperty(INDENT_AMOUNT_XSLT, "3"); //$NON-NLS-1$
            transformer.setOutputProperty(INDENT_AMOUNT_XALAN, "3"); //$NON-NLS-1$
            transformer.transform(new DOMSource(aNode), new StreamResult(writer));
         }
         catch (Exception e)
         {
            AeException.logError(e, AeMessages.getString("AeXMLParserBase.ERROR_13")); //$NON-NLS-1$
         }
      }

      return writer.toString();
   }

   /**
    * This method is used to save a document to a specified output stream.
    *
    * @param aDocument the DOM we will be writing
    * @param aOutput the output stream we will be writing to
    * @throws AeException
    */
   public static void saveDocument(Document aDocument, StreamResult aOutput) throws AeException
   {
      if (aOutput != null && aDocument != null)
      {
         try
         {
            DOMSource source = new DOMSource(aDocument);
            Transformer transformer = getTransformerFactory().newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
            transformer.setOutputProperty(INDENT_AMOUNT_XSLT, "3"); //$NON-NLS-1$
            transformer.setOutputProperty(INDENT_AMOUNT_XALAN, "3"); //$NON-NLS-1$
            transformer.transform(source, aOutput);
         }
         catch(Exception e)
         {
            throw new AeException(AeMessages.getString("AeXMLParserBase.ERROR_18"), e); //$NON-NLS-1$
         }
      }
   }

   /**
    * This method is used to save a document to a specified output stream.
    *
    * @param aDocument the DOM we will be writing
    * @param aOutput the output stream we will be writing to
    * @throws AeException
    */
   public static void saveDocument(Document aDocument, Writer aOutput) throws AeException
   {
      saveDocument(aDocument, new StreamResult(aOutput));
   }

   /**
    * Sets the error handler to be used during parse operations.
    * @param aErrorHandler the error handler to be set
    */
   public void setErrorHandler(AeXMLParserErrorHandler aErrorHandler)
   {
      if (aErrorHandler instanceof DefaultHandler)
         mErrorHandler = aErrorHandler;
   }

   /**
    * Method which provides and instance of the error handler used during parse.
    * If one does not exist when it is called for, a default one will be created.
    */
   public AeXMLParserErrorHandler getErrorHandler()
   {
      if (mErrorHandler == null)
         mErrorHandler = new AeXMLParserErrorHandler();

      return mErrorHandler;
   }

   /**
    * Returns a boolean flag indicating if any warnings were generated during parse.
    */
   public boolean hasParseWarnings()
   {
      return getErrorHandler().hasParseWarnings();
   }

   /**
    * Resets the flag which indicates that parse warnings have occurred.
    */
   public void resetParseWarnings()
   {
      getErrorHandler().resetParseWarnings();
   }

   /**
    * Returns flag indicating if the parser is namespace aware.
    */
   public boolean isNamespaceAware()
   {
      return mNamespaceAware;
   }

   /**
    * Sets flag indicating if the parser is namespace aware.
    */
   public void setNamespaceAware(boolean aFlag)
   {
      mNamespaceAware = aFlag;
   }

   /**
    * Returns flag indicating if the parser is validating.
    */
   public boolean isValidating()
   {
      return mValidating;
   }

   /**
    * Sets flag indicating if the parser has validation enabled.
    */
   public void setValidating(boolean aFlag)
   {
      mValidating = aFlag;
   }

   /**
    * @return The cached transformer factory
    */
   public static TransformerFactory getTransformerFactory()
   {
      if(sTransformerFactory == null)
      {
         sTransformerFactory = loadTransformerFactory();
         // required for indenting in 1.5 vm see http://forum.java.sun.com/thread.jspa?threadID=562510&start=0 and http://dev.eclipse.org/newslists/news.eclipse.technology.xsd/msg01616.html
         try { sTransformerFactory.setAttribute("indent-number", new Integer(3)); } catch (Exception ex) { /* ignore for 1.4 vm */ } //$NON-NLS-1$
      }
      return sTransformerFactory;
   }

   /**
    * Loads the transformer factory.  Uses the AE system property if it is set, otherwise it
    * defaults to the typical TransformerFactory.newInstance() call.
    */
   protected static TransformerFactory loadTransformerFactory()
   {
      String transformerImpl = System.getProperty(IAeConstants.SYSTEM_PROPERTY_TRANSFORMER_FACTORY_IMPL);
      if (AeUtil.notNullOrEmpty(transformerImpl))
      {
         try
         {
            return (TransformerFactory) Class.forName(transformerImpl).newInstance();
         }
         catch (Throwable ex)
         {
            AeException.logError(ex);
         }
      }

      return TransformerFactory.newInstance();
   }

   /**
    * Converts list of Castor Schema objects to InputStreams so they can be set
    * on the factory/parser and used by the jaxp provider to validate the
    * document. Also supports already-serialized schemas (Strings or
    * InputStreams).
    * 
    * @param aSchemas - list of Castor schema objects
    * @throws AeException
    */
   protected List serializeSchemas(List aSchemas) throws AeException
   {
      if (AeUtil.isNullOrEmpty(aSchemas))
         return Collections.EMPTY_LIST;

      // Build a list of all schemas being referenced by this variable
      List schemaStreamList = new ArrayList();
      for (Iterator it=aSchemas.iterator(); it.hasNext();)
      {
         Object schemaObj = it.next();
         // stream for the serialized schema
         InputStream input = null;
         int index = schemaStreamList.size();

         if (schemaObj instanceof InputStream)
         {
            input = (InputStream) schemaObj;
         }
         else if (schemaObj instanceof String)
         {
            input = AeUTF8Util.getInputStream((String) schemaObj);
         }
         else if (schemaObj instanceof Schema)
         {
            Schema schema = (Schema) schemaObj;

            // Special handling here for the Schema schema. Castor doesn't load the
            // Schema schema so we use a stripped down version to avoid the parsing
            // errors. This stripped down version can't be passed to the jaxp
            // validator or we risk not being able to validate schemas that
            // reference types or elements defined in the Schema schema.
            // <xs:documentation/> is one example.
            // The workaround is to keep a copy of the full XMLSchema file in our
            // classpath and then return a stream to that file instead of
            // serializing the one we have loaded into Castor.
            if (IAeConstants.W3C_XML_SCHEMA.equals(schema.getTargetNamespace()))
            {
               input = AeXMLParserBase.class.getResourceAsStream("XMLSchema-full.xsd"); //$NON-NLS-1$
            }
            else
            {
               // See if they're serializing one of our standard schemas and use
               // the one we have cached in its serialized form if possible.
               input = AeStandardSchemas.getStandardSchema(schema.getTargetNamespace());
               if (input != null && IAeConstants.W3C_XML_NAMESPACE.equals(schema.getTargetNamespace()))
                  index = 0;
            }
   
            if (input == null)
            {
               // If we got here then we have to serialize the Castor Schema object
               // to a stream so it can be consumed by the jaxp validator.
               String schemaStr = AeSchemaUtil.serializeSchema(schema, false);
               input = AeUTF8Util.getInputStream(schemaStr);
            }
         }

         if (input != null)
            schemaStreamList.add(index, input);
      }

      return schemaStreamList;
   }

   /**
    * Getter for WSDL locator to use to locate schema resources.
    */
   public WSDLLocator getWSDLLocator()
   {
      return mWSDLLocator;
   }

   /**
    * Setter for WSDL locator to use to locate schema resources.
    */
   public void setWSDLLocator(WSDLLocator aWSDLLocator)
   {
      mWSDLLocator = aWSDLLocator;
   }
   
   /**
    * Getter for the features map
    */
   protected Map getFeatures()
   {
      return mFeatures;
   }
   
   /**
    * Sets the feature in the map
    * @param aFeatureName
    * @param aValue
    */
   public void setFeature(String aFeatureName, boolean aValue)
   {
      getFeatures().put(aFeatureName, new Boolean(aValue));
   }
   
   /**
    * Removes the feature from the map
    * @param aFeatureName
    */
   public void removeFeature(String aFeatureName)
   {
      getFeatures().remove(aFeatureName);
   }
   
   /**
    * Returns true if the feature is set
    * @param aFeatureName
    */
   public Boolean getFeature(String aFeatureName)
   {
      Boolean bool = (Boolean) getFeatures().get(aFeatureName);
      return bool;
   }
}
