// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/AeExistStorageImpl.java,v 1.2 2007/08/31 14:20:41 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.exist;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.MessageFormat;

import org.activebpel.rt.base64.Base64;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBResponseHandler;
import org.activebpel.rt.exist.AeMessages;

/**
 * An eXist version of the storage impl interface.
 */
public class AeExistStorageImpl implements IAeXMLDBStorageImpl
{
   /** The data source. */
   private IAeXMLDBDataSource mDataSource;

   /**
    * C'tor.
    *
    * @param aDataSource
    */
   public AeExistStorageImpl(IAeXMLDBDataSource aDataSource)
   {
      setDataSource(aDataSource);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl#deleteDocuments(java.lang.String, java.lang.String, org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection)
    */
   public int deleteDocuments(String aQuery, String aDeleteDocType, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      IAeExistConnection nativeConn = (IAeExistConnection) aConnection.getNativeConnection();
      return nativeConn.deleteDocuments(aQuery);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl#deleteNonXMLDocument(long, org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection)
    */
   public void deleteNonXMLDocument(long aDocumentId, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String xquery = 
            "let $nonXMLDocs := (/AeResourceRoot[@aeid = ''{0,number,#}'']/AeNonXMLContent)\n" +  //$NON-NLS-1$
            "return (update delete $nonXMLDocs, <Count>'{ count($nonXMLDocs) }'</Count>)"; //$NON-NLS-1$
      xquery = MessageFormat.format(xquery, new Object[] { new Long(aDocumentId) });
      deleteDocuments(xquery, null, aConnection);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl#getXMLDBDataSource()
    */
   public IAeXMLDBDataSource getXMLDBDataSource()
   {
      return getDataSource();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl#insertDocument(java.io.Reader, org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection)
    */
   public long insertDocument(Reader aReader, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      BufferedReader reader = new BufferedReader(aReader);
      StringBuffer buffer = new StringBuffer();
      String str = null;
      try
      {
         synchronized (buffer)
         {
            while ( (str = reader.readLine()) != null)
            {
               buffer.append(str);
               buffer.append("\n"); //$NON-NLS-1$
            }
         }
      }
      catch (IOException ex)
      {
         throw new AeXMLDBException(ex);
      }

      IAeExistConnection nativeConn = (IAeExistConnection) aConnection.getNativeConnection();
      return nativeConn.insertDocument(buffer.toString());
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl#insertNonXMLDocument(java.io.InputStream, org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection)
    */
   public long insertNonXMLDocument(InputStream aInputStream, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer;
      try
      {
         buffer = new byte[2048];
         int count;
         while ( (count = aInputStream.read(buffer)) > 0 )
            baos.write(buffer, 0, count);
      }
      catch (IOException ex)
      {
         throw new AeXMLDBException(ex);
      }
      
      String nonXmlContent = Base64.encodeBytes(baos.toByteArray());
      String b64Content = "<AeNonXMLContent>" + nonXmlContent + "</AeNonXMLContent>";  //$NON-NLS-1$//$NON-NLS-2$
      
      IAeExistConnection nativeConn = (IAeExistConnection) aConnection.getNativeConnection();
      return nativeConn.insertDocument(b64Content);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl#retrieveNonXMLDocument(long, org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection)
    */
   public InputStream retrieveNonXMLDocument(long aDocumentId, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      String xquery = "/AeResourceRoot[@aeid = ''{0,number,#}'']/AeNonXMLContent"; //$NON-NLS-1$
      xquery = MessageFormat.format(xquery, new Object[] { new Long(aDocumentId) });
      String b64Content = (String) xquery(xquery, AeXMLDBResponseHandler.STRING_RESPONSE_HANDLER, aConnection);
      byte [] content = Base64.decode(b64Content);
      return new ByteArrayInputStream(content);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl#updateDocuments(java.lang.String, org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection)
    */
   public int updateDocuments(String aQuery, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      IAeExistConnection nativeConn = (IAeExistConnection) aConnection.getNativeConnection();
      return nativeConn.updateDocuments(aQuery);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl#xquery(java.lang.String, org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler, org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection)
    */
   public Object xquery(String aQuery, IAeXMLDBResponseHandler aResponseHandler, IAeXMLDBConnection aConnection) throws AeXMLDBException
   {
      ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         // Set class loader to that which loaded us, to ensure we load the xerces parser
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

         IAeExistConnection nativeConn = (IAeExistConnection) aConnection.getNativeConnection();
         return nativeConn.xquery(aQuery, aResponseHandler);
      }
      catch (Throwable t)
      {
         throw new AeXMLDBException(AeMessages.getString("AeAbstractExistStorage.ErrorQueryingExistDB"), t); //$NON-NLS-1$
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(previousClassLoader);
      }
   }

   /**
    * @return Returns the dataSource.
    */
   protected IAeXMLDBDataSource getDataSource()
   {
      return mDataSource;
   }

   /**
    * @param aDataSource the dataSource to set
    */
   protected void setDataSource(IAeXMLDBDataSource aDataSource)
   {
      mDataSource = aDataSource;
   }
}
