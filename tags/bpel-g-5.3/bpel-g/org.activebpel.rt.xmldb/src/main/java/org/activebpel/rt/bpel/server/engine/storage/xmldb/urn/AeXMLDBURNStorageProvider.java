// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/urn/AeXMLDBURNStorageProvider.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb.urn;

import java.util.LinkedHashMap;
import java.util.Map;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeAbstractXMLDBStorageProvider;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.urn.handlers.AeURNMappingResponseHandler;

/**
 * A XMLDB implementation of a URN storage provider.
 */
public class AeXMLDBURNStorageProvider extends AeAbstractXMLDBStorageProvider implements
      IAeURNStorageProvider
{
   /** The prefix into the xmldb config that this storage object uses. */
   protected static final String CONFIG_PREFIX = "URNStorage"; //$NON-NLS-1$
   /** A response handler that returns a urn->url Map. */
   private static final AeURNMappingResponseHandler URN_MAPPING_HANDLER = new AeURNMappingResponseHandler();

   /**
    * Constructs the XMLDB URN storage with the given XMLDB config.
    * 
    * @param aConfig
    * @param aStorageImpl
    */
   public AeXMLDBURNStorageProvider(AeXMLDBConfig aConfig, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, CONFIG_PREFIX, aStorageImpl);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider#getMappings()
    */
   public Map getMappings() throws AeStorageException
   {
      return (Map) query(IAeURNConfigKeys.GET_MAPPINGS, URN_MAPPING_HANDLER);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider#addMapping(java.lang.String, java.lang.String, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public void addMapping(String aURN, String aURL, IAeStorageConnection aConnection) throws AeStorageException
   {
      LinkedHashMap params = new LinkedHashMap(2);
      params.put(IAeURNElements.URN, aURN);
      params.put(IAeURNElements.URL, aURL);
      insertDocument(IAeURNConfigKeys.INSERT_MAPPING, params, getXMLDBConnection(aConnection));
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider#removeMapping(java.lang.String, org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection)
    */
   public void removeMapping(String aURN, IAeStorageConnection aConnection) throws AeStorageException
   {
      updateDocuments(IAeURNConfigKeys.DELETE_MAPPING, new Object[] { aURN }, getXMLDBConnection(aConnection));
   }
}
