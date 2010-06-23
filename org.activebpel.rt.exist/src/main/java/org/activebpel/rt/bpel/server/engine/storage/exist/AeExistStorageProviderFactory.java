//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/AeExistStorageProviderFactory.java,v 1.2 2007/08/17 00:59:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.exist;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBStorageProviderFactory;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;


/**
 * A storage factory that creates Exist versions of the store objects.
 */
public class AeExistStorageProviderFactory extends AeXMLDBStorageProviderFactory
{
   /**
    * Default constructor.
    */
   public AeExistStorageProviderFactory(Map aConfig) throws AeException
   {
      super(aConfig);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBStorageProviderFactory#createXMLDBConfig(java.util.Map)
    */
   protected AeXMLDBConfig createXMLDBConfig(Map aOverrideMap)
   {
      return new AeExistConfig(aOverrideMap);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBStorageProviderFactory#setDataSource(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource)
    */
   protected void setDataSource(IAeXMLDBDataSource aDataSource)
   {
      AeExistDataSource.MAIN = aDataSource;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBStorageProviderFactory#createStorageImpl(org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBDataSource)
    */
   protected IAeXMLDBStorageImpl createStorageImpl(IAeXMLDBDataSource aDataSource)
   {
      return new AeExistStorageImpl(aDataSource);
   }
}
