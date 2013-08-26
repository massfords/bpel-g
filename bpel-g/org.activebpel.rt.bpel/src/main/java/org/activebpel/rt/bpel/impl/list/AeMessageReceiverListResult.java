// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeMessageReceiverListResult.java,v 1.2 2006/06/26 16:50:48 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.impl.queue.AeMessageReceiver;

/**
 * Wraps a listing of queued message receivers.
 */
public class AeMessageReceiverListResult implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -956966022024432317L;
    /**
     * Total rows that matched selection criteria.  This number may be greater than the number of results in this listing.
     */
    protected final int mTotalRows;
    /**
     * The matching message receivers.
     */
    protected final AeMessageReceiver[] mResults;
    /**
     * Mapping of process ids to location paths.
     */
    protected final Map<Integer, String> mLocationIdtoLocationPathMap = new HashMap<>();

    /**
     * Constructor.
     *
     * @param aTotalRows Total rows that matched selection criteria.  This number may be greater than the number of results in this listing.
     * @param aReceivers The matching message receivers.
     */
    @ConstructorProperties({"totalRows", "results"})
    public AeMessageReceiverListResult(int aTotalRows, AeMessageReceiver[] aReceivers) {
        mTotalRows = aTotalRows;
        mResults = aReceivers;
    }

    /**
     * Accessor for total rows.
     */
    public int getTotalRows() {
        return mTotalRows;
    }

    /**
     * Accessor for message receivers.
     */
    public AeMessageReceiver[] getResults() {
        return mResults;
    }

    /**
     * Add a location id to location path mapping.
     *
     * @param aLocationId The location path id.
     * @param aLocation   The location xpath.
     */
    public void addPathMapping(int aLocationId, String aLocation) {
        mLocationIdtoLocationPathMap.put(aLocationId, aLocation);
    }

    /**
     * Returns the matching location path for this process id.
     *
     * @param aLocationId
     */
    public String getLocationPath(int aLocationId) {
        return mLocationIdtoLocationPathMap.get(aLocationId);
    }

    /**
     * Returns true if there are no queued message receivers.
     */
    public boolean isEmpty() {
        return mResults == null || mResults.length == 0;
    }
}
