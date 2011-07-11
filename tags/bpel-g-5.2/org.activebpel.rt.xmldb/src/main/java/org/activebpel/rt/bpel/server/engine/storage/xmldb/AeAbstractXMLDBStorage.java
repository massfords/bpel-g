//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeAbstractXMLDBStorage.java,v 1.3 2008/02/17 21:59:05 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.impl.fastdom.AeFastDocument;
import org.activebpel.rt.bpel.impl.fastdom.AeFastElement;
import org.activebpel.rt.bpel.impl.fastdom.AeFastText;
import org.activebpel.rt.bpel.impl.fastdom.AeForeignNode;
import org.activebpel.rt.bpel.impl.fastdom.AeXMLFormatter;
import org.activebpel.rt.bpel.impl.list.IAeListingFilter;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.schema.AeSchemaDateTime;
import org.activebpel.rt.xmldb.AeMessages;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A base class for all XMLDB storage objects.  This class provides convenience methods for querying,
 * deleting, updating, and inserting document instances.
 */
public abstract class AeAbstractXMLDBStorage extends AeXMLDBObject implements IAeXMLDBStorage
{
   /** A regexp pattern that will escape braces in a string. */
   private static final Pattern ESCAPE_BRACE_PATTERN = Pattern.compile("([{}])"); //$NON-NLS-1$

   /**
    * Creates a xmldb storage object from the config and config prefix.
    *
    * @param aConfig
    * @param aPrefix
    */
   public AeAbstractXMLDBStorage(AeXMLDBConfig aConfig, String aPrefix, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, aPrefix, aStorageImpl);
   }

   /**
    * Serializes a Fast Document to a xml string.
    *
    * @param aDocument
    * @throws AeXMLDBException
    */
   protected String serializeFastDocument(AeFastDocument aDocument) throws AeXMLDBException
   {
      if (aDocument == null)
      {
         return ""; //$NON-NLS-1$
      }
      else
      {
         // Fixup any existing default namespaces.
         AeXMLDBDefaultNSVisitor visitor = new AeXMLDBDefaultNSVisitor();
         aDocument.accept(visitor);

         StringWriter writer = new StringWriter();
         new AeXMLFormatter().format(aDocument.getRootElement(), writer);
         String rval = writer.getBuffer().toString();
         // The following line replaces { with {{ and } with }}.  It does this because the serialized
         // process document is a part of an XQuery, and the { and } chars are special in that language.
         return ESCAPE_BRACE_PATTERN.matcher(rval).replaceAll("$1$1"); //$NON-NLS-1$
      }
   }

   /**
    * Runs an XQuery and returns the result (some kind of Object).  The XQuery that is run comes
    * from the supplied query builder.  The return result is created by the supplied response handler.
    * If no results are found, the return value is determined by the handler.
    *
    * @param aBuilder
    * @param aResponseHandler
    * @throws AeXMLDBException
    */
   protected Object query(AeXMLDBQueryBuilder aBuilder, IAeXMLDBResponseHandler aResponseHandler) throws AeXMLDBException
   {
      try
      {
         String xquery = aBuilder.buildQuery();
         setCurrentStatementName(aBuilder.getKey());

         return queryInternal(xquery, aResponseHandler);
      }
      catch (AeXMLDBException ex)
      {
         throw ex;
      }
      catch (AeException ex)
      {
         throw new AeXMLDBException(ex);
      }
   }

   /**
    * Queries the XMLDB by creating a query builder and using it to generate
    * an XQuery.  The query builder to use is created by
    *
    * @param aXQueryKey
    * @param aFilter
    * @param aResponseHandler
    * @throws AeXMLDBException
    */
   protected Object query(String aXQueryKey, IAeListingFilter aFilter, IAeXMLDBResponseHandler aResponseHandler) throws AeXMLDBException
   {
      String builderClassname = getXQueryStatement(aXQueryKey);

      try
      {
         Class clazz = Class.forName(builderClassname);
         Constructor ctor = clazz.getConstructor(new Class[] { IAeListingFilter.class, AeXMLDBConfig.class,
               IAeXMLDBStorageImpl.class });
         AeXMLDBQueryBuilder builder = (AeXMLDBQueryBuilder) 
               ctor.newInstance(new Object[] { aFilter, getXMLDBConfig(), getStorageImpl() });
         return query(builder, aResponseHandler);
      }
      catch (Exception ex)
      {
         String msg = MessageFormat.format(AeMessages.getString("AeAbstractXMLDBStorage.ErrorCreatingBuilder"), new Object[] { builderClassname }); //$NON-NLS-1$
         throw new AeXMLDBException(msg, ex);
      }
   }

   /**
    * Runs an XQuery and returns the result (some kind of Object).  The XQuery that is run comes
    * from the xmldb config object.  The key to the xquery statement is passed as a param to
    * this method.  The return result is created by the supplied response handler.  If no results
    * are found, the return value is determined by the handler.
    *
    * @param aXQueryKey
    * @param aResponseHandler
    * @throws AeXMLDBException
    */
   protected Object query(String aXQueryKey, IAeXMLDBResponseHandler aResponseHandler) throws AeXMLDBException
   {
      String xquery = getXQueryStatement(aXQueryKey);

      return queryInternal(xquery, aResponseHandler);
   }

   /**
    * Runs an XQuery and returns the result (some kind of Object).  The XQuery that is run comes
    * from the xmldb config object.  The key to the xquery statement is passed as a param to
    * this method.  The return result is created by the supplied response handler.  If no results
    * are found, the return value is determined by the handler.
    *
    * @param aXQueryKey
    * @param aResponseHandler
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected Object query(String aXQueryKey, IAeXMLDBResponseHandler aResponseHandler,
         IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String xquery = getXQueryStatement(aXQueryKey);

      return queryInternal(xquery, aResponseHandler, aConnection);
   }

   /**
    * Runs an XQuery and returns the result (some kind of Object).  The XQuery that is run comes
    * from the xmldb config object.  The key to the xquery statement is passed as a param to
    * this method.  The return result is created by the supplied response handler.  If no results
    * are found, the return value is determined by the handler.
    *
    * @param aXQueryKey
    * @param aParams
    * @param aResponseHandler
    * @throws AeXMLDBException
    */
   protected Object query(String aXQueryKey, Object [] aParams, IAeXMLDBResponseHandler aResponseHandler) throws AeXMLDBException
   {
      String xqueryPattern = getXQueryStatement(aXQueryKey);
      String xquery = formatStatement(xqueryPattern, aParams);

      return queryInternal(xquery, aResponseHandler);
   }

   /**
    * Runs an XQuery and returns the result (some kind of Object).  The XQuery that is run comes
    * from the xmldb config object.  The key to the xquery statement is passed as a param to
    * this method.  The return result is created by the supplied response handler.  If no results
    * are found, the return value is determined by the handler.
    *
    * @param aXQueryKey
    * @param aParams
    * @param aResponseHandler
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected Object query(String aXQueryKey, Object[] aParams, IAeXMLDBResponseHandler aResponseHandler,
         IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String xqueryPattern = getXQueryStatement(aXQueryKey);
      String xquery = formatStatement(xqueryPattern, aParams);

      return queryInternal(xquery, aResponseHandler, aConnection);
   }

   /**
    * Runs the given XQuery and returns the result (some kind of Object).  The return result is created
    * by the supplied response handler.  If no results are found, the return value is determined by the handler.
    *
    * @param aXQuery
    * @param aResponseHandler
    * @throws AeXMLDBException
    */
   private Object queryInternal(String aXQuery, IAeXMLDBResponseHandler aResponseHandler) throws AeXMLDBException
   {
      IAeXMLDBConnection connection = getNewConnection();

      try
      {
         return queryInternal(aXQuery, aResponseHandler, connection);
      }
      catch (Exception t)
      {
         throw new AeXMLDBException(AeMessages.getString("AeAbstractXMLDBStorage.FAILED_TO_QUERY_XMLDB_DB_ERROR") + aXQuery, t); //$NON-NLS-1$
      }
      finally
      {
         connection.close();
      }
   }

   /**
    * Runs the given XQuery and returns the result (some kind of Object).  The return result is created
    * by the supplied response handler.  If no results are found, the return value is determined by the handler.
    *
    * @param aXQuery
    * @param aResponseHandler
    * @param aConnection
    * @throws AeXMLDBException
    */
   private Object queryInternal(String aXQuery, IAeXMLDBResponseHandler aResponseHandler, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      return xquery(aXQuery, aResponseHandler, aConnection);
   }

   /**
    * Inserts a document instance into the XMLDB database.  This version of the method takes an XML
    * 'pattern' which is a Java MessageFormat formatted string.  This string, along with the included
    * parameters, will become valid XML when formatted.
    *
    * @param aXMLPatternKey a key into the xmldb config
    * @param aParams
    * @throws AeXMLDBException
    */
   protected long insertDocument(String aXMLPatternKey, Object [] aParams) throws AeXMLDBException
   {
      String xmlPattern = getXQueryStatement(aXMLPatternKey);
      String xml = formatStatement(xmlPattern, aParams);
      return insertDocumentInternal(xml);
   }

   /**
    * Creates a doc instance given the doc type key and parameter map.
    *
    * TODO It would be nice if this returned a Reader so we didn't have to serialize to String before handing off to XMLDB...
    *
    * @param aDocTypeKey
    * @param aParams
    * @throws AeXMLDBException
    */
   protected String createDocInstance(String aDocTypeKey, Map aParams) throws AeXMLDBException
   {
      String docType = getXQueryStatement(aDocTypeKey);
      AeFastElement rootElem = new AeFastElement(docType);

      for (Iterator iter = aParams.keySet().iterator(); iter.hasNext(); )
      {
         String elemName = (String) iter.next();
         Object elemValue = aParams.get(elemName);
         if (elemValue != null)
         {
            AeFastElement elem = createDocInstanceElement(elemName, elemValue);
            rootElem.appendChild(elem);
         }
      }

      StringWriter writer = new StringWriter();
      AeXMLFormatter formatter = new AeXMLFormatter();
      formatter.format(rootElem, writer);

      return writer.getBuffer().toString();
   }

   /**
    * Inserts a document instance into the XMLDB database.  This version of the method takes a key
    * into the XMLDB Config.  The key should resolve to a String which will be the name of the
    * document instance (the doc type) and thus the name of the root XML element.  The map of parameters
    * should be String->Object.  The keys of the map will be the names of Elements in the doc instance
    * and the values will be the Element values.  If order is important a LinkedHashMap should be used
    * when calling this method.  Different Java types for the values of the parameters will be handled
    * appropriately (QName will be handled/inserted as an AeQName, for example).
    *
    * @param aDocTypeKey
    * @param aParams
    * @throws AeXMLDBException
    */
   protected long insertDocument(String aDocTypeKey, LinkedHashMap aParams) throws AeXMLDBException
   {
      String xml = createDocInstance(aDocTypeKey, aParams);
      return insertDocumentInternal(xml);
   }

   /**
    * Inserts a document instance into the XMLDB database.  This version of the method takes a key
    * into the XMLDB Config.  The key should resolve to a String which will be the name of the
    * document instance (the doc type) and thus the name of the root XML element.  The map of parameters
    * should be String->Object.  The keys of the map will be the names of Elements in the doc instance
    * and the values will be the Element values.  If order is important a LinkedHashMap should be used
    * when calling this method.  Different Java types for the values of the parameters will be handled
    * appropriately (QName will be handled/inserted as an AeQName, for example).
    *
    * @param aDocTypeKey
    * @param aParams
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected long insertDocument(String aDocTypeKey, LinkedHashMap aParams, IAeXMLDBConnection aConnection)
         throws AeXMLDBException
   {
      String xml = createDocInstance(aDocTypeKey, aParams);
      return insertDocumentInternal(xml, aConnection);
   }

   /**
    * Creates the content element to be inserted into a document instance with the given Object content.
    * This method will return a different Element depending on the type of the Object passed.  For example,
    * it will create an Element of type AeQName if the Object passed in is a QName.
    *
    * @param aElementName
    * @param aValue
    */
   protected AeFastElement createDocInstanceElement(String aElementName, Object aValue)
   {
      AeFastElement elem = new AeFastElement(aElementName);
      if (aValue instanceof IAeXMLDBNull)
      {
         String val = ((IAeXMLDBNull) aValue).getValue();
         elem.setAttribute(IAeCommonElements.AE_NULL_ATTR, "true"); //$NON-NLS-1$
         if (val != null)
            elem.appendChild(new AeFastText(val));
      }
      else if (aValue instanceof QName)
      {
         QName qname = (QName) aValue;

         AeFastElement ns = new AeFastElement(IAeCommonElements.NAMESPACE);
         ns.appendChild(new AeFastText(qname.getNamespaceURI()));
         AeFastElement lp = new AeFastElement(IAeCommonElements.LOCAL_PART);
         lp.appendChild(new AeFastText(qname.getLocalPart()));

         elem.appendChild(ns);
         elem.appendChild(lp);
      }
      else if (aValue instanceof AeFastDocument)
      {
         AeFastDocument doc = (AeFastDocument) aValue;
         elem.appendChild(doc.getRootElement().detach());
      }
      else if (aValue instanceof Node)
      {
         Node doc = (Node) aValue;
         elem.appendChild(new AeForeignNode(doc));
      }
      else if (aValue instanceof Date)
      {
         elem.appendChild(new AeFastText(new AeSchemaDateTime((Date) aValue).toString()));
      }
      else
      {
         elem.appendChild(new AeFastText(aValue.toString()));
      }
      return elem;
   }

   /**
    * Inserts a document instance into the XMLDB database.  This version of the method takes an XML
    * 'pattern' which is a Java MessageFormat formatted string.  This string, along with the included
    * parameters, will become valid XML when formatted.  This version of the method also includes the
    * XMLDB connection that should be used to do the insert.
    *
    * @param aXMLPatternKey a key into the xmldb config
    * @param aParams
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected long insertDocument(String aXMLPatternKey, Object [] aParams, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String xmlPattern = getXQueryStatement(aXMLPatternKey);
      String xml = formatStatement(xmlPattern, aParams);
      return insertDocumentInternal(xml, aConnection);
   }

   /**
    * Inserts a document instance into the XMLDB database.  Returns the ino:id of the inserted
    * document.  This ID is an auto-increment value that can be used to refer to or delete the
    * inserted document.
    *
    * @param aElem
    * @throws AeXMLDBException
    */
   protected long insertDocument(Element aElem) throws AeXMLDBException
   {
      String xml = AeXmlUtil.serialize(aElem);
      return insertDocumentInternal(xml);
   }

   /**
    * Inserts a non-XML document instance into the XMLDB database.  Returns the
    * ino:id of the inserted document.
    *
    * @param aStream
    * @throws AeXMLDBException
    */
   protected long insertNonXMLDocument(InputStream aStream) throws AeXMLDBException
   {
      return insertNonXMLDocumentInternal(aStream);
   }

   /**
    * Inserts a non-XML document instance into the XMLDB database.  Returns the
    * ino:id of the inserted document.  Uses the given xmldb connection to do
    * the insert.
    *
    * @param aStream
    * @throws AeXMLDBException
    */
   protected long insertNonXMLDocument(InputStream aStream, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      return insertNonXMLDocumentInternal(aStream, aConnection);
   }

   /**
    * Inserts an xml document instance into the XMLDB database.
    *
    * @param aXMLKey
    * @throws AeXMLDBException
    */
   protected long insertDocument(String aXMLKey) throws AeXMLDBException
   {
      String xml = getXQueryStatement(aXMLKey);
      return insertDocumentInternal(xml);
   }

   /**
    * Inserts a document instance into the XMLDB database.  Returns the ino:id of the inserted
    * document.  This ID is an auto-increment value that can be used to refer to or delete the
    * inserted document.  Uses the given connection when inserting the document.
    *
    * @param aElem
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected long insertDocument(Element aElem, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String xml = AeXmlUtil.serialize(aElem);
      return insertDocumentInternal(xml, aConnection);
   }

   /**
    * Inserts an xml document instance into the XMLDB database.
    *
    * @param aXMLKey
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected long insertDocument(String aXMLKey, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String xml = getXQueryStatement(aXMLKey);
      return insertDocumentInternal(xml, aConnection);
   }

   /**
    * Inserts an xml document instance into the XMLDB database.
    *
    * @param aXML
    * @throws AeXMLDBException
    */
   private long insertDocumentInternal(String aXML) throws AeXMLDBException
   {
      IAeXMLDBConnection conn = getNewConnection();
      try
      {
         return insertDocumentInternal(aXML, conn);
      }
      catch (AeXMLDBException te)
      {
         throw te;
      }
      finally
      {
         conn.close();
      }
   }

   /**
    * Inserts a non-XML document instance into the XMLDB database.
    *
    * @param aStream
    * @throws AeXMLDBException
    */
   private long insertNonXMLDocumentInternal(InputStream aStream) throws AeXMLDBException
   {
      IAeXMLDBConnection conn = getNewConnection();
      try
      {
         return insertNonXMLDocumentInternal(aStream, conn);
      }
      catch (AeXMLDBException te)
      {
         throw te;
      }
      finally
      {
         conn.close();
      }
   }

   /**
    * Inserts an xml document instance into the XMLDB database.  Gets the XML content from
    * the given Reader.
    *
    * todo Make this private once it is used - it's protected to avoid a warning...
    *
    * @param aXMLReader
    * @throws AeXMLDBException
    */
   protected long insertDocumentInternal(Reader aXMLReader) throws AeXMLDBException
   {
      IAeXMLDBConnection conn = getNewConnection();
      try
      {
         return insertDocumentInternal(aXMLReader, conn);
      }
      catch (AeXMLDBException te)
      {
         throw te;
      }
      finally
      {
         conn.close();
      }
   }

   /**
    * Inserts an xml document instance into the XMLDB database.  Returns the XMLDB ino:id of the
    * newly inserted document instance.
    *
    * @param aXML
    * @param aConnection
    * @throws AeXMLDBException
    */
   private long insertDocumentInternal(String aXML, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      // Put the XML content into a StringReader
      StringReader stringReader = new StringReader(aXML);
      return insertDocumentInternal(stringReader, aConnection);
   }

   /**
    * Inserts an xml document instance into the XMLDB database.  Returns the XMLDB ino:id of the
    * newly inserted document instance.
    *
    * @param aXMLReader
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected long insertDocumentInternal(Reader aXMLReader, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      return getStorageImpl().insertDocument(aXMLReader, aConnection);
   }

   /**
    * Inserts a non-XML document instance into the XMLDB database. Returns the
    * XMLDB ino:id of the newly inserted document instance.
    *
    * @param aInputStream
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected long insertNonXMLDocumentInternal(InputStream aInputStream, IAeXMLDBConnection aConnection)
         throws AeXMLDBException
   {
      return getStorageImpl().insertNonXMLDocument(aInputStream, aConnection);
   }

   /**
    * Retrieves the non-XML document instance with the given ino:id.
    *
    * @param aDocumentId
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected InputStream retrieveNonXMLDocumentInternal(long aDocumentId, IAeXMLDBConnection aConnection)
         throws AeXMLDBException
   {
      return getStorageImpl().retrieveNonXMLDocument(aDocumentId, aConnection);
   }

   /**
    * Deletes some documents that are selected using a query builder.  The query builder uses a filter
    * to generate an XQuery that is used to delete the documents.
    *
    * @param aBuilder
    * @throws AeXMLDBException
    */
   protected int deleteDocuments(AeXMLDBQueryBuilder aBuilder) throws AeXMLDBException
   {
      IAeXMLDBConnection conn = getNewConnection();
      try
      {
         return deleteDocuments(aBuilder, conn);
      }
      catch (AeXMLDBException te)
      {
         throw te;
      }
      finally
      {
         conn.close();
      }
   }

   /**
    * Deletes some documents that are selected using a query builder.  The query builder uses a filter
    * to generate an XQuery that is used to delete the documents.  This version uses the included
    * <code>IAeXMLDBConnection</code> to execute the query.
    *
    * @param aBuilder
    * @param aConnection
    * @throws AeXMLDBException
    * @throws AeXMLDBException
    */
   protected int deleteDocuments(AeXMLDBQueryBuilder aBuilder, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      try
      {
         String xquery = aBuilder.buildDeleteQuery();
         setCurrentStatementName(aBuilder.getKey());

         return deleteDocumentsInternal(xquery, aBuilder.getDeletedDocumentType(), aConnection);
      }
      catch (AeXMLDBException te)
      {
         throw te;
      }
      catch (AeException ex)
      {
         throw new AeXMLDBException(ex);
      }
   }

   /**
    * Deletes a set of documents based on the given query (looked up in the xmldb config based
    * on the supplied key).  Returns the number of documents deleted.
    *
    * @param aQueryKey
    * @throws AeXMLDBException
    */
   protected int deleteDocuments(String aQueryKey) throws AeXMLDBException
   {
      return deleteDocuments(aQueryKey, (String) null);
   }

   /**
    * Deletes a set of documents based on the given query (looked up in the xmldb config based
    * on the supplied key).  Returns the number of documents (of the given type) deleted.
    *
    * @param aQueryKey
    * @param aDeleteDocType
    * @throws AeXMLDBException
    */
   protected int deleteDocuments(String aQueryKey, String aDeleteDocType) throws AeXMLDBException
   {
      String query = getXQueryStatement(aQueryKey);
      return deleteDocumentsInternal(query, aDeleteDocType);
   }

   /**
    * Deletes a set of documents based on the given query (looked up in the xmldb config based
    * on the supplied key).  Returns the number of documents deleted.
    *
    * @param aQueryKey
    * @throws AeXMLDBException
    */
   protected int deleteDocuments(String aQueryKey, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      return deleteDocuments(aQueryKey, (String) null, aConnection);
   }

   /**
    * Deletes a set of documents based on the given query (looked up in the xmldb config based
    * on the supplied key).  Returns the number of documents deleted.
    *
    * @param aQueryKey
    * @param aDeleteDocType
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected int deleteDocuments(String aQueryKey, String aDeleteDocType, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String query = getXQueryStatement(aQueryKey);
      return deleteDocumentsInternal(query, aDeleteDocType, aConnection);
   }

   /**
    * Deletes a set of documents based on the given query (looked up in the xmldb config based
    * on the supplied key).  Returns the number of documents deleted.
    *
    * @param aQueryKey
    * @param aParams
    * @throws AeXMLDBException
    */
   protected int deleteDocuments(String aQueryKey, Object [] aParams) throws AeXMLDBException
   {
      return deleteDocuments(aQueryKey, aParams, (String) null);
   }

   /**
    * Deletes a set of documents based on the given query (looked up in the xmldb config based
    * on the supplied key).  Returns the number of documents (of the given type) deleted.
    *
    * @param aQueryKey
    * @param aParams
    * @param aDeleteDocType
    * @throws AeXMLDBException
    */
   protected int deleteDocuments(String aQueryKey, Object [] aParams, String aDeleteDocType) throws AeXMLDBException
   {
      String queryPattern = getXQueryStatement(aQueryKey);
      String query = formatStatement(queryPattern, aParams);
      return deleteDocumentsInternal(query, aDeleteDocType);
   }

   /**
    * Deletes a set of documents based on the given query (looked up in the xmldb config based
    * on the supplied key).  Returns the number of documents deleted.
    *
    * @param aQueryKey
    * @param aParams
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected int deleteDocuments(String aQueryKey, Object[] aParams, IAeXMLDBConnection aConnection)
         throws AeXMLDBException
   {
      return deleteDocuments(aQueryKey, aParams, (String) null, aConnection);
   }

   /**
    * Deletes a set of documents based on the given query (looked up in the xmldb config based
    * on the supplied key).  Returns the number of documents deleted.
    *
    * @param aQueryKey
    * @param aParams
    * @param aDeleteDocType
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected int deleteDocuments(String aQueryKey, Object[] aParams, String aDeleteDocType,
         IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String queryPattern = getXQueryStatement(aQueryKey);
      String query = formatStatement(queryPattern, aParams);
      return deleteDocumentsInternal(query, aDeleteDocType, aConnection);
   }

   /**
    * Deletes a set of documents based on the given query.  Returns true if at least one document was deleted.
    *
    * @param aXQuery
    * @param aDeleteDocType
    * @throws AeXMLDBException
    */
   private int deleteDocumentsInternal(String aXQuery, String aDeleteDocType) throws AeXMLDBException
   {
      IAeXMLDBConnection conn = getNewConnection();
      try
      {
         return deleteDocumentsInternal(aXQuery, aDeleteDocType, conn);
      }
      catch (AeXMLDBException te)
      {
         throw te;
      }
      finally
      {
         conn.close();
      }
   }

   /**
    * Deletes a set of documents based on the given query.  Returns the number of documents deleted.  If
    * the <code>aDeleteDocType</code> param is not null, then the return value reflects the total number of
    * doc instances of that type that were deleted.
    *
    * @param aXQuery
    * @param aDeleteDocType
    * @param aConnection
    * @throws AeXMLDBException
    */
   private int deleteDocumentsInternal(String aXQuery, String aDeleteDocType, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      return getStorageImpl().deleteDocuments(aXQuery, aDeleteDocType, aConnection);
   }

   /**
    * Deletes the non-XML document with the given ino:id.
    *
    * @param aDocumentId
    */
   protected void deleteNonXMLDocument(long aDocumentId) throws AeXMLDBException
   {
      IAeXMLDBConnection conn = getNewConnection();
      try
      {
         deleteNonXMLDocument(aDocumentId, conn);
      }
      catch (AeXMLDBException te)
      {
         throw te;
      }
      finally
      {
         conn.close();
      }
   }

   /**
    * Deletes the non-XML document with the given ino:id.  Uses the given
    * connection.
    *
    * @param aDocumentId
    * @param aConnection
    */
   protected void deleteNonXMLDocument(long aDocumentId, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      deleteNonXMLDocumentInternal(aDocumentId, aConnection);
   }

   /**
    * Deletes the non-XML document instance with the given ino:id.
    *
    * @param aDocumentId
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected void deleteNonXMLDocumentInternal(long aDocumentId, IAeXMLDBConnection aConnection)
         throws AeXMLDBException
   {
      getStorageImpl().deleteNonXMLDocument(aDocumentId, aConnection);
   }

   /**
    * Updates zero or more documents in the database.  The XQuery statement is retrieved from the
    * XMLDB config using the given config key.
    *
    * @param aXQueryKey
    * @throws AeXMLDBException
    */
   protected int updateDocuments(String aXQueryKey) throws AeXMLDBException
   {
      String xquery = getXQueryStatement(aXQueryKey);

      return updateDocumentsInternal(xquery);
   }

   /**
    * Updates zero or more documents in the database.  The XQuery statement is retrieved from the
    * XMLDB config using the given config key.  This method takes an additional parameter for the
    * XMLDB connection.
    *
    * @param aXQueryKey
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected int updateDocuments(String aXQueryKey, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String xquery = getXQueryStatement(aXQueryKey);

      return updateDocumentsInternal(xquery, aConnection);
   }

   /**
    * Updates zero or more documents in the database.  The XQuery statement is retrieved from the
    * XMLDB config using the given config key and then formatted with the given params.
    *
    * @param aXQueryPatternKey
    * @param aParams
    * @throws AeXMLDBException
    */
   protected int updateDocuments(String aXQueryPatternKey, Object [] aParams) throws AeXMLDBException
   {
      String xqueryPattern = getXQueryStatement(aXQueryPatternKey);
      String query = formatStatement(xqueryPattern, aParams);

      return updateDocumentsInternal(query);
   }

   /**
    * Updates zero or more documents in the database.  The XQuery statement is retrieved from the
    * XMLDB config using the given config key and then formatted with the given params.  In addition,
    * this method takes an additional parameter for the XMLDB connection.
    *
    * @param aXQueryPatternKey
    * @param aParams
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected int updateDocuments(String aXQueryPatternKey, Object [] aParams, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String xqueryPattern = getXQueryStatement(aXQueryPatternKey);
      String query = formatStatement(xqueryPattern, aParams);

      return updateDocumentsInternal(query, aConnection);
   }

   /**
    * Updates zero or more documents in the database using the given XQuery.
    *
    * @param aXQuery
    * @throws AeXMLDBException
    */
   private int updateDocumentsInternal(String aXQuery) throws AeXMLDBException
   {
      IAeXMLDBConnection conn = getNewConnection();
      try
      {
         return updateDocumentsInternal(aXQuery, conn);
      }
      finally
      {
         conn.close();
      }
   }

   /**
    * Updates zero or more documents in the database using the given XQuery.  This version uses the
    * connection object passed to it.
    *
    * @param aXQuery
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected int updateDocumentsInternal(String aXQuery, IAeXMLDBConnection aConnection)
         throws AeXMLDBException
   {
      return getStorageImpl().updateDocuments(aXQuery, aConnection);
   }

   /**
    * Performs an XQuery.  The return value is determined by the response handler.
    *
    * @param aXQuery
    * @param aResponseHandler
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected Object xquery(String aXQuery, IAeXMLDBResponseHandler aResponseHandler, IAeXMLDBConnection aConnection)
         throws AeXMLDBException
   {
      return getStorageImpl().xquery(aXQuery, aResponseHandler, aConnection);
   }

   /**
    * Retrieves the non-XML content with the given ino:id.
    *
    * @param aDocumentId
    * @throws AeXMLDBException
    */
   protected InputStream retrieveNonXMLDocument(long aDocumentId)
         throws AeXMLDBException
   {
      IAeXMLDBConnection conn = getNewConnection();
      try
      {
         return retrieveNonXMLDocumentInternal(aDocumentId, conn);
      }
      catch (AeXMLDBException te)
      {
         throw te;
      }
      finally
      {
         conn.close();
      }
   }

   /**
    * Retrieves the non-XML content with the given ino:id.
    *
    * @param aDocumentId
    * @param aConnection
    * @throws AeXMLDBException
    */
   protected InputStream retrieveNonXMLDocument(long aDocumentId, IAeXMLDBConnection aConnection)
         throws AeXMLDBException
   {
      return retrieveNonXMLDocumentInternal(aDocumentId, aConnection);
   }
}
