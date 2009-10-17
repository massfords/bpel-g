// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/AeExistConnection.java,v 1.6 2008/02/17 21:49:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.exist;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBResponseHandler;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.exist.xmldb.IndexQueryService;
import org.exist.xmldb.XQueryService;
import org.w3c.dom.Element;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 * An Exist database connection.
 */
public class AeExistConnection implements IAeExistConnection
{
   /** regexp pattern used to find the doc type in an XML string. */
   private static Pattern sDocTypePattern = Pattern.compile("<(" + AeXmlUtil.NCNAME_PATTERN + ")"); //$NON-NLS-1$ //$NON-NLS-2$

   /** Auto-commit flag. */
   private boolean mAutoCommit;
   /** The collection. */
   private Collection mCollection;
   /** Parser to use when querying the DB. */
   private AeXMLParserBase mParser = new AeXMLParserBase(true, false);
   /** The number of operations performed on this connection since it was opened. */
   private int mNumOperations = 0;

   /**
    * C'tor.
    *
    * @param aAutoCommit
    */
   public AeExistConnection(boolean aAutoCommit, Collection aCollection)
   {
      setAutoCommit(aAutoCommit);
      setCollection(aCollection);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection#getNativeConnection()
    */
   public Object getNativeConnection()
   {
      return this;
   }

   protected long getNextCounterValue(String aType) throws AeXMLDBException
   {
      String query = ""; //$NON-NLS-1$
      query += "let $parent := /AeResourceRoot/AeCounters\n"; //$NON-NLS-1$
      query += "let $counter := /AeResourceRoot/AeCounters/Counter[@name=\"{0}\"] \n"; //$NON-NLS-1$
      query += "return ( if (not(empty($counter))) then \n"; //$NON-NLS-1$
      query += "  ( update replace $counter with\n"; //$NON-NLS-1$
      query += "    <Counter name=\"{0}\">'{number(string($counter)) + 1}'</Counter>,\n"; //$NON-NLS-1$
      query += "  $counter)\n"; //$NON-NLS-1$
      query += "  else (update insert <Counter name=\"{0}\">1</Counter> into $parent ,\n"; //$NON-NLS-1$
      query += "   <Counter name=\"{0}\">1</Counter>)\n"; //$NON-NLS-1$
      query += " )"; //$NON-NLS-1$

      query = MessageFormat.format(query, aType);
      Integer count = (Integer) xquery(query, AeXMLDBResponseHandler.INTEGER_RESPONSE_HANDLER);
      return count.longValue();
   }
   
   /**
    * Called to configure the collection.
    * 
    * @param aConfigurationXML
    * @throws AeXMLDBException
    */
   protected void configureCollection(String aConfigurationXML) throws AeXMLDBException
   {
      try
      {
         IndexQueryService service = (IndexQueryService) getCollection().getService("IndexQueryService", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         service.configureCollection(aConfigurationXML);
      }
      catch (XMLDBException ex)
      {
         throw new AeXMLDBException(ex);
      }
   }
   

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#insertDocument(java.lang.String)
    */
   public long insertDocument(String aXMLContent) throws AeXMLDBException
   {
      mNumOperations++;

      try
      {
         String docType = getDocType(aXMLContent);
         long counter = getNextCounterValue(docType);
         String xml = "<AeResourceRoot aeid=\"{0,number,#}\">{1}</AeResourceRoot>"; //$NON-NLS-1$
         xml = MessageFormat.format(xml, new Object[] { new Long(counter), aXMLContent });
         
         // create new XMLResource; an id will be assigned to the new resource
         XMLResource document = (XMLResource) getCollection().createResource(null, "XMLResource"); //$NON-NLS-1$
         document.setContent(xml);
         getCollection().storeResource(document);

         return counter;
      }
      catch (AeException ex)
      {
         throw new AeXMLDBException(ex);
      }
      catch (XMLDBException ex)
      {
         throw new AeXMLDBException(ex);
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#xquery(java.lang.String, org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler)
    */
   public Object xquery(String aQuery, IAeXMLDBResponseHandler aResponseHandler) throws AeXMLDBException
   {
      List results = new ArrayList();

      try
      {
         XQueryService service = (XQueryService) getCollection().getService("XQueryService", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         service.setProperty("indent", "yes"); //$NON-NLS-1$ //$NON-NLS-2$

         ResourceSet result = service.query(aQuery);
         for (ResourceIterator iter = result.getIterator(); iter.hasMoreResources(); )
         {
            Resource r = iter.nextResource();
            Object content = r.getContent();
            content = getParser().loadDocumentFromString((String) content, null).getDocumentElement();
            results.add(content);
         }
      }
      catch (Exception ex)
      {
         throw new AeXMLDBException(ex);
      }

      if (aResponseHandler != null)
         return aResponseHandler.handleResponse(new AeExistXQueryResponse(results));
      else
         return null;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#updateDocuments(java.lang.String)
    */
   public int updateDocuments(String aQuery) throws AeXMLDBException
   {
      mNumOperations++;

      Integer count = (Integer) xquery(aQuery, AeXMLDBResponseHandler.INTEGER_RESPONSE_HANDLER);
      return count.intValue();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#deleteDocuments(java.lang.String)
    */
   public int deleteDocuments(String aQuery) throws AeXMLDBException
   {
      mNumOperations++;

      Integer count = (Integer) xquery(aQuery, AeXMLDBResponseHandler.INTEGER_RESPONSE_HANDLER);
      return count.intValue();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#close()
    */
   public void close()
   {
      if (mNumOperations > 1 && isAutoCommit())
      {
         throw new RuntimeException("Too many operations performed using this auto-commit eXist connection."); //$NON-NLS-1$
      }
      mNumOperations = 0;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#commit()
    */
   public void commit() throws AeXMLDBException
   {
      if (isAutoCommit())
      {
         throw new UnsupportedOperationException("Commit not supported for auto-commit connections."); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistConnection#rollback()
    */
   public void rollback() throws AeXMLDBException
   {
      if (isAutoCommit())
      {
         throw new UnsupportedOperationException("Rollback not supported for auto-commit connections."); //$NON-NLS-1$
      }
   }

   /**
    * Gets the doc type of the given xml content.
    *
    * @param aContent
    */
   private String getDocType(String aXMLContent) throws AeException
   {
      Matcher matcher = sDocTypePattern.matcher(aXMLContent);
      if (matcher.find())
      {
         String docType = matcher.group(1);
         return docType;
      }
      throw new AeException("Could not find doc type from XML."); //$NON-NLS-1$
   }

   /**
    * @return Returns the autoCommit.
    */
   public boolean isAutoCommit()
   {
      return mAutoCommit;
   }

   /**
    * @param aAutoCommit the autoCommit to set
    */
   protected void setAutoCommit(boolean aAutoCommit)
   {
      mAutoCommit = aAutoCommit;
   }

   /**
    * @return Returns the collection.
    */
   protected Collection getCollection()
   {
      return mCollection;
   }

   /**
    * @param aCollection the collection to set
    */
   protected void setCollection(Collection aCollection)
   {
      mCollection = aCollection;
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

   /**
    * Inserts a raw element into the database.
    *
    * @param aElement
    */
   public void insertRawDocument(Element aElement) throws AeXMLDBException
   {
      String xml = AeXmlUtil.serialize(aElement);
      insertRawDocument(xml);
   }

   /**
    * Inserts a raw element into the database.
    *
    * @param aXmlContent
    */
   public void insertRawDocument(String aXmlContent) throws AeXMLDBException
   {
      try
      {
         // create new XMLResource; an id will be assigned to the new resource
         XMLResource document = (XMLResource) getCollection().createResource(null, "XMLResource"); //$NON-NLS-1$
         document.setContent(aXmlContent);
         getCollection().storeResource(document);
      }
      catch (XMLDBException ex)
      {
         throw new AeXMLDBException(ex);
      }
   }

}
