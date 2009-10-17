// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/AeSQLURNStorageProvider.java,v 1.3 2007/01/30 22:32:46 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.sql;

import java.util.Map;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.sql.handlers.AeURNMappingHandler;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * A SQL implementation of a URN storage provider.
 */
public class AeSQLURNStorageProvider extends AeAbstractSQLStorageProvider implements IAeURNStorageProvider
{
   /** The SQL statement prefix for all SQL statements used in this class. */
   protected static final String SQLSTATEMENT_PREFIX = "URNStorage."; //$NON-NLS-1$

   /** resultset handler used to read the urn mappings into a map */
   private static final ResultSetHandler URN_MAPPING_HANDLER = new AeURNMappingHandler();

   /**
    * Creates the SQL URN storage delegate with the given sql config.
    * 
    * @param aConfig
    */
   public AeSQLURNStorageProvider(AeSQLConfig aConfig)
   {
      super(SQLSTATEMENT_PREFIX, aConfig);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider#getMappings()
    */
   public Map getMappings() throws AeStorageException
   {
      return (Map) query(IAeURNSQLKeys.SQL_GET_MAPPINGS, new Object[0], URN_MAPPING_HANDLER);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider#addMapping(java.lang.String, java.lang.String, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public void addMapping(String aURN, String aURL, IAeStorageConnection aConnection) throws AeStorageException
   {
      Object[] args = new Object[] { aURN, aURL.toCharArray()};
      update(getSQLConnection(aConnection), IAeURNSQLKeys.SQL_INSERT_MAPPING, args);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider#removeMapping(java.lang.String, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public void removeMapping(String aURN, IAeStorageConnection aConnection) throws AeStorageException
   {
      update(getSQLConnection(aConnection), IAeURNSQLKeys.SQL_DELETE_MAPPING, new Object[] { aURN });
   }
}
