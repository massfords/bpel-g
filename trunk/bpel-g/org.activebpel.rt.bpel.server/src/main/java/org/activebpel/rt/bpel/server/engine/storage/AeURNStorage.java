// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/AeURNStorage.java,v 1.3 2006/06/05 20:45:41 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage;

import org.activebpel.rt.bpel.server.engine.storage.providers.IAeStorageConnection;
import org.activebpel.rt.bpel.server.engine.storage.providers.IAeURNStorageProvider;

import java.util.Map;

/**
 * A delegating implementation of a URN storage.  This class delegates all of the database
 * calls to an instance of IAeURNStorageProvider.  The purpose of this class is to encapsulate
 * storage 'logic' so that it can be shared across multiple storage implementations (such as SQL
 * and Tamino).
 */
public class AeURNStorage extends AeAbstractStorage implements IAeURNStorage {
    /**
     * Convenience method to get the storage provider cast to a URN storage provider.
     */
    protected IAeURNStorageProvider getURNStorageProvider() {
        return (IAeURNStorageProvider) getProvider();
    }


    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#getMappings()
     */
    public Map<String, String> getMappings() throws AeStorageException {
        return getURNStorageProvider().getMappings();
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#addMapping(java.lang.String, java.lang.String)
     */
    public void addMapping(String aURN, String aURL) throws AeStorageException {

        try (IAeStorageConnection connection = getCommitControlDBConnection()) {
            getURNStorageProvider().removeMapping(aURN, connection);
            getURNStorageProvider().addMapping(aURN, aURL, connection);

            connection.commit();
        }
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#removeMappings(java.lang.String[])
     */
    public void removeMappings(String[] aURNArray) throws AeStorageException {
        try (IAeStorageConnection connection = getCommitControlDBConnection()) {
            for (String arr : aURNArray) {
                getURNStorageProvider().removeMapping(arr, connection);
            }
            connection.commit();
        }
    }
}
