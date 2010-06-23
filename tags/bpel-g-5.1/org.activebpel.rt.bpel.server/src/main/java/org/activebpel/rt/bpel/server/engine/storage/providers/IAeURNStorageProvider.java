// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.providers;

import java.util.Map;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;

/**
 * A URN storage delegate. This interface defines methods that the delegating URN storage will call in order
 * to store/read date in the underlying database.
 */
public interface IAeURNStorageProvider extends IAeStorageProvider
{
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#getMappings()
    */
   public Map getMappings() throws AeStorageException;
   
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#addMapping(java.lang.String, java.lang.String)
    */
   public void addMapping(String aURN, String aURL, IAeStorageConnection aConnection) throws AeStorageException;

   /**
    * Removes a single URN mapping from the database.
    * 
    * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#removeMappings(java.lang.String[])
    */
   public void removeMapping(String aURN, IAeStorageConnection aConnection) throws AeStorageException;
}
