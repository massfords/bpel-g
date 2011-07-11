//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/AeExistDataSource.java,v 1.8 2008/02/17 21:49:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.exist;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource;
import org.activebpel.rt.exist.AeMessages;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeFileUtil;
import org.activebpel.rt.util.AeUTF8Util;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.exist.xmldb.DatabaseInstanceManager;
import org.w3c.dom.Document;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

/**
 * The Exist data source.
 */
public class AeExistDataSource implements IAeExistDataSource
{
   public static IAeXMLDBDataSource MAIN = null;

   /** The URL to the Exist installation. */
   private String mURL;
   /** The name of the collection within the database. */
   private String mCollectionName;
   /** The data source username (optional). */
   private String mUsername;
   /** The data source password (optional). */
   private String mPassword;
   /** Flag indicating if the DB is embedded. */
   private boolean mEmbedded;
   /** Flag idicating if an embedded DB should be initialized on start. */
   private boolean mInitializeEmbedded = true;
   /** The path to the filesystem where the embedded DB lives. */
   private String mDatabasePath;
   /** The location of the eXist database. */
   private File mDBLocation;
   /** The size of the connection pool. */
   private int mConnectionPoolSize;
   /** The root eXist collection. */
   private Collection mRootCollection;
   /** The eXist collection to store all our XML resources. */
   private Collection mCollection;
   /** The connection pool. */
   private AeExistConnectionPool mConnectionPool;
   /** Flag indicating whether the data source has been destroyed. */
   private boolean mDestroyed = false;

   /**
    * Constructs a data source.  Uses information in the engine configuration map
    * to initialize its state.
    *
    * @param aConfig The engine configuration map for this data source.
    */
   public AeExistDataSource(Map aConfig) throws AeException
   {
      try
      {
         String url = (String) aConfig.get(URL);
         String collection = (String) aConfig.get(COLLECTION);
         String username = (String) aConfig.get(IAeEngineConfiguration.DS_USERNAME_ENTRY);
         String password = (String) aConfig.get(IAeEngineConfiguration.DS_PASSWORD_ENTRY);
         String embedded = (String) aConfig.get(EMBEDDED);
         String initEmbedded = (String) aConfig.get(INITIALIZE_EMBEDDED);
         String dbLocation = (String) aConfig.get(DB_LOCATION);
         String connectionPoolSize = (String) aConfig.get(CONNECTION_POOL_SIZE);
         int poolSize = 1;

         if (!url.endsWith("/")) //$NON-NLS-1$
         {
            url = url + "/"; //$NON-NLS-1$
         }

         if (AeUtil.notNullOrEmpty(connectionPoolSize))
         {
            poolSize = Integer.parseInt(connectionPoolSize);
         }

         setURL(url);
         setCollectionName(collection);
         setUsername(username);
         setPassword(password);
         setEmbedded("true".equals(embedded)); //$NON-NLS-1$
         if (AeUtil.notNullOrEmpty(initEmbedded))
            setInitializeEmbedded("true".equals(initEmbedded)); //$NON-NLS-1$
         setDatabasePath(dbLocation);
         setConnectionPoolSize(poolSize);

         initDB();
      }
      catch (Exception e)
      {
         throw new AeException(AeMessages.getString("AeExistDataSource.ErrorCreatingExistDataSource"), e); //$NON-NLS-1$
      }
   }

   /**
    * Initialize the database.
    */
   protected void initDB() throws Exception
   {
      if (isEmbedded())
      {
         setDBLocation(new File(getDatabasePath()));

         // Set up system properties used by eXist
         System.setProperty("exist.initdb", "true"); //$NON-NLS-1$ //$NON-NLS-2$
         System.setProperty("exist.home", getDBLocation().getAbsolutePath()); //$NON-NLS-1$

         initializeDatabaseLocation();
      }
      
      // initialize driver
      Class cl = Class.forName("org.exist.xmldb.DatabaseImpl"); //$NON-NLS-1$
      Database database = (Database) cl.newInstance();
      DatabaseManager.registerDatabase(database);

      Collection root = DatabaseManager.getCollection("xmldb:exist:///db", "admin", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      Collection activeBPELCollection = getExistCollection(getCollectionName(), root);

      setRootCollection(root);
      setCollection(activeBPELCollection);

      // TODO need a commons-pooling based connection pool class in xmldb
      setConnectionPool(new AeExistConnectionPool(getConnectionPoolSize(), getCollection()));
   }

   /**
    * Initializes the DB location by making sure the directory exists.  If it
    * does not exist, then it is created and populated with the proper structure.
    */
   protected void initializeDatabaseLocation() throws Exception
   {
      if (isEmbedded() && isInitializeEmbedded())
         AeFileUtil.recursivelyDelete(getDBLocation());

      File configXmlFile = new File(getDBLocation(), "conf.xml"); //$NON-NLS-1$
      if (!getDBLocation().exists() || !configXmlFile.exists())
      {
         getDBLocation().mkdirs();
         new File(getDBLocation(), "data").mkdir(); //$NON-NLS-1$
         new File(getDBLocation(), "journal").mkdir(); //$NON-NLS-1$

         createConfigXml(configXmlFile);
      }
   }

   /**
    * Creates the config.xml file in the database location.
    *
    * @param aFile
    */
   protected void createConfigXml(File aFile) throws Exception
   {
      InputStream is = AeExistDataSource.class.getResourceAsStream("exist-conf.xml"); //$NON-NLS-1$
      Document doc = new AeXMLParserBase(true, false).loadDocument(is, null);
      AeXMLParserBase.saveDocument(doc, new FileWriter(aFile));
   }

   /**
    * Gets the named collection (creating it if it does not yet exist).
    *
    * @param aCollectionName
    * @param aRootCollection
    * @throws XMLDBException
    */
   protected Collection getExistCollection(String aCollectionName, Collection aRootCollection) throws Exception
   {
      Collection activeBPELCollection = DatabaseManager.getCollection("xmldb:exist:///db/" + aCollectionName, "admin", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      if (activeBPELCollection == null)
      {
         // collection does not exist: get root collection and create
         // for simplicity, we assume that the new collection is a
         // direct child of the root collection, e.g. /db/test.
         CollectionManagementService mgtService = (CollectionManagementService) aRootCollection.getService(
               "CollectionManagementService", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         activeBPELCollection = createCollection(aCollectionName, mgtService);

         seedDatabase(activeBPELCollection);
      }
      return activeBPELCollection;
   }

   /**
    * Creates a collection with the given name.
    *
    * @param aCollectionName
    * @param aManagementService
    * @throws XMLDBException
    */
   protected Collection createCollection(String aCollectionName, CollectionManagementService aManagementService) throws XMLDBException
   {
      return aManagementService.createCollection(aCollectionName);
   }

   /**
    * Seed the database with the license and
    *
    * @param aActiveBPELCollection
    */
   protected void seedDatabase(Collection aActiveBPELCollection) throws Exception
   {
      AeExistConnection connection = new AeExistConnection(true, aActiveBPELCollection);
      // First, store the collection config file.
      String collectionConfigXML = loadCollectionConfiguration();
      connection.configureCollection(collectionConfigXML);

      // First insert counter doc. This doc is needed by all IAeExistConnection::insertDocument(..) methods.
      connection.insertRawDocument("<AeResourceRoot><AeCounters/></AeResourceRoot>"); //$NON-NLS-1$
      connection.insertDocument("<AeMetaInfo name=\"Version\">2.1.0.3</AeMetaInfo>"); //$NON-NLS-1$
   }

   /**
    * Loads the collection.xconf file as a resource into a String and 
    * returns it.
    */
   protected String loadCollectionConfiguration() throws Exception
   {
      InputStream is = IAeExistDataSource.class.getResourceAsStream("collection.xconf"); //$NON-NLS-1$
      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte [] bytes = new byte[256];
         int count = 0;
         while ( (count = is.read(bytes)) != -1 )
            baos.write(bytes, 0, count);
         bytes = baos.toByteArray();
         return AeUTF8Util.newString(bytes, 0, bytes.length);
      }
      finally
      {
         AeCloser.close(is);
      }
   }

   /**
    * Public method used to destroy the data source when shutting down.
    */
   public void destroy()
   {
      try
      {
         // shut down the database
         DatabaseInstanceManager manager = (DatabaseInstanceManager) getRootCollection().getService(
               "DatabaseInstanceManager", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
         manager.shutdown();
         setDestroyed(true);
      }
      catch (XMLDBException ex)
      {
         AeException.logError(ex);
      }
   }

   /**
    * @see java.lang.Object#finalize()
    */
   protected void finalize() throws Throwable
   {
      // clean up
      if (!isDestroyed())
         destroy();
   }

   /**
    * Gets a new Exist connection.
    *
    */
   public IAeXMLDBConnection getNewConnection() throws AeXMLDBException
   {
      return getNewConnection(true);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistDataSource#getNewConnection(boolean)
    */
   public IAeXMLDBConnection getNewConnection(boolean aAutoCommit) throws AeXMLDBException
   {
      try
      {
         return getConnectionPool().checkoutConnection(aAutoCommit);
      }
      catch (Throwable t)
      {
         throw new AeXMLDBException(AeMessages.getString("AeExistDataSource.ErrorGettingExistConnection"), t); //$NON-NLS-1$
      }
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource#getNativeDataSource()
    */
   public Object getNativeDataSource()
   {
      return this;
   }

   /**
    * @return Returns the password.
    */
   protected String getPassword()
   {
      return mPassword;
   }

   /**
    * @param aPassword The password to set.
    */
   protected void setPassword(String aPassword)
   {
      mPassword = aPassword;
   }

   /**
    * @return Returns the username.
    */
   protected String getUsername()
   {
      return mUsername;
   }

   /**
    * @param aUsername The username to set.
    */
   protected void setUsername(String aUsername)
   {
      mUsername = aUsername;
   }

   /**
    * @return Returns the uRL.
    */
   protected String getURL()
   {
      return mURL;
   }

   /**
    * @param aUrl The uRL to set.
    */
   protected void setURL(String aUrl)
   {
      mURL = aUrl;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.exist.IAeExistDataSource#getCollectionName()
    */
   public String getCollectionName()
   {
      return mCollectionName;
   }

   /**
    * @param aCollection The collection to set.
    */
   protected void setCollectionName(String aCollection)
   {
      mCollectionName = aCollection;
   }

   /**
    * @return Returns the databasePath.
    */
   protected String getDatabasePath()
   {
      return mDatabasePath;
   }

   /**
    * @param aDatabasePath the databasePath to set
    */
   protected void setDatabasePath(String aDatabasePath)
   {
      mDatabasePath = aDatabasePath;
   }

   /**
    * @return Returns the embedded.
    */
   protected boolean isEmbedded()
   {
      return mEmbedded;
   }

   /**
    * @param aEmbedded the embedded to set
    */
   protected void setEmbedded(boolean aEmbedded)
   {
      mEmbedded = aEmbedded;
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
    * @return Returns the dBLocation.
    */
   protected File getDBLocation()
   {
      return mDBLocation;
   }

   /**
    * @param aLocation the dBLocation to set
    */
   protected void setDBLocation(File aLocation)
   {
      mDBLocation = aLocation;
   }

   /**
    * @return Returns the rootCollection.
    */
   protected Collection getRootCollection()
   {
      return mRootCollection;
   }

   /**
    * @param aRootCollection the rootCollection to set
    */
   protected void setRootCollection(Collection aRootCollection)
   {
      mRootCollection = aRootCollection;
   }

   /**
    * @return Returns the connectionPool.
    */
   protected AeExistConnectionPool getConnectionPool()
   {
      return mConnectionPool;
   }

   /**
    * @param aConnectionPool the connectionPool to set
    */
   protected void setConnectionPool(AeExistConnectionPool aConnectionPool)
   {
      mConnectionPool = aConnectionPool;
   }

   /**
    * @return Returns the initializeEmbedded.
    */
   protected boolean isInitializeEmbedded()
   {
      return mInitializeEmbedded;
   }

   /**
    * @param aInitializeEmbedded the initializeEmbedded to set
    */
   protected void setInitializeEmbedded(boolean aInitializeEmbedded)
   {
      mInitializeEmbedded = aInitializeEmbedded;
   }

   /**
    * @return Returns the destroyed.
    */
   protected boolean isDestroyed()
   {
      return mDestroyed;
   }

   /**
    * @param aDestroyed the destroyed to set
    */
   protected void setDestroyed(boolean aDestroyed)
   {
      mDestroyed = aDestroyed;
   }

   /**
    * @return Returns the connectionPoolSize.
    */
   protected int getConnectionPoolSize()
   {
      return mConnectionPoolSize;
   }

   /**
    * @param aConnectionPoolSize the connectionPoolSize to set
    */
   protected void setConnectionPoolSize(int aConnectionPoolSize)
   {
      mConnectionPoolSize = aConnectionPoolSize;
   }
}
