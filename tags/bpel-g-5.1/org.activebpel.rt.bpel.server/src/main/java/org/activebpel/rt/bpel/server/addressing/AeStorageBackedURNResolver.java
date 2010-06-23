//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/AeStorageBackedURNResolver.java,v 1.2.4.1 2008/04/28 21:55:25 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing; 

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.storage.AePersistentStoreFactory;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage;
import org.activebpel.rt.bpel.urn.AeURNResolver;
import org.activebpel.rt.util.AeUtil;

/**
 * Keeps all of the mappings in memory.
 * 
 * TODO Note - this class uses the IAeURNStorage interface directly when it should not.  The reason is that it can be constructed with a config map - see the constructor for details.
 */
public class AeStorageBackedURNResolver extends AeURNResolver implements IAeStorageBackedURNResolver
{
   /** storage contains the values for the urn addressing */
   private IAeURNStorage mStorage;

   /**
    * Standard manager constructor takes a map with config options.
    * 
    * @param aConfigMap Map must contain entry for the storage class.
    * @throws AeException
    */
   public AeStorageBackedURNResolver(Map aConfigMap) throws AeException
   {
      Map storageMap = (Map) aConfigMap.get("Storage"); //$NON-NLS-1$
      if (storageMap != null)
      {
         setStorage( (IAeURNStorage) AeEngineFactory.createConfigSpecificClass(storageMap) );
      }
      else
      {
         setStorage( AePersistentStoreFactory.getInstance().getURNStorage() );
      }
      getMap().putAll(getStorage().getMappings());
   }

   /**
    * Protected ctor for testing.
    * 
    * @throws AeStorageException
    */
   protected AeStorageBackedURNResolver(IAeURNStorage aStorage) throws AeStorageException
   {
      setStorage(aStorage);
      getMap().putAll(getStorage().getMappings());
   }
   
   /**
    * protected constructor for subclasses. 
    */
   protected AeStorageBackedURNResolver()
   {
   }
   
   /**
    * @see org.activebpel.rt.bpel.urn.AeURNResolver#addMapping(java.lang.String, java.lang.String)
    */
   public synchronized void addMapping(String aURN, String aURL)
   {
      try
      {
         getStorage().addMapping(AeUtil.normalizeURN(aURN), aURL);
         super.addMapping(aURN, aURL);
      }
      catch (AeStorageException e)
      {
         e.logError();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.urn.AeURNResolver#removeMappings(java.lang.String[])
    */
   public synchronized void removeMappings(String[] aURNArray)
   {
      try
      {
         for(int i = 0; i < aURNArray.length; i++)
         {
            aURNArray[0] = AeUtil.normalizeURN(aURNArray[0]);
         }
         getStorage().removeMappings(aURNArray);
         super.removeMappings(aURNArray);
      }
      catch (AeStorageException e)
      {
         e.logError();
      }
   }
   
   /**
    * @see org.activebpel.rt.bpel.server.addressing.IAeStorageBackedURNResolver#reload()
    */
   public synchronized void reload()
   {
      try
      {
         setMap(new HashMap(getStorage().getMappings()));
      }
      catch (AeStorageException e)
      {
         e.logError();
      }
   }
   
   /**
    * Getter for the storage.
    */
   protected IAeURNStorage getStorage()
   {
      return mStorage;
   }
   
   /**
    * Setter for the storage.
    * 
    * @param aStorage
    */
   protected void setStorage(IAeURNStorage aStorage)
   {
      mStorage = aStorage;
   }
}
 