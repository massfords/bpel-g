// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/catalog/resource/IAeResourceCache.java,v 1.2 2006/08/04 17:57:53 ckeller Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.catalog.resource;

import net.sf.ehcache.Statistics;
import bpelg.services.deploy.types.pdd.ReferenceType;


/**
 * Resource cache interface.
 */
public interface IAeResourceCache {
    /**
     * Locate a specific object via a key.
     *
     * @param aKey
     */
    public Object getResource(ReferenceType aKey) throws AeResourceException;

    /**
     * Remove the resource from the cache.
     *
     * @param aKey
     */
    public boolean removeResource(ReferenceType aKey);

    /**
     * Replace any existing entries mapped to the given key with the new object.
     *
     * @param aKey
     * @param aObject
     */
    public void updateResource(ReferenceType aKey, Object aObject);

    /**
     * Clear entries out of the cache.
     */
    public void clear();

    public Statistics getStatistics();
}
