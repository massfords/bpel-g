// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeInboundReceivesBean.java,v 1.3 2005/01/14 16:30:35 twinkler Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpeladmin.war.AeEngineManagementFactory;

import bpelg.services.queue.types.GetInboundMessages;
import bpelg.services.queue.types.InboundMessage;
import bpelg.services.queue.types.InboundMessages;

/**
 * Top level listing of unmatched inbound queued receives.
 */
public class AeInboundReceivesBean {
    /**
     * Unmatched inbound queued receives
     */
    protected final List<List<InboundMessage>> mDetails;
    /**
     * Current row index.
     */
    protected int mCurrentIndex;

    /**
     * Default constructor. Intializes the unmatched inbound queued receives
     * list.
     */
    public AeInboundReceivesBean() {
        mDetails = toListOfLists(AeEngineManagementFactory.getQueueManager()
                .getInboundMessages(new GetInboundMessages()));
    }

    /**
     * Returns true if the row details are empty.
     */
    public boolean isEmpty() {
        return mDetails.isEmpty();
    }

    /**
     * Maps the partner link, port type, operation key to one or more
     * AeQueuedReceiveDetail objects.
     *
     * @param aDetails
     */
    private static List<List<InboundMessage>> toListOfLists(
            InboundMessages aDetails) {
        Map<String, List<InboundMessage>> recs = new LinkedHashMap<>();

        for (InboundMessage im : aDetails.getInboundMessage()) {
            addToMap(recs, im);
        }
        return new ArrayList<>(recs.values());
    }

    /**
     * Convenience method for adding details to the map.
     *
     * @param aHashMap
     * @param aDetail
     */
    private static void addToMap(Map<String, List<InboundMessage>> aHashMap,
                                 InboundMessage aDetail) {
        String key = makeKey(aDetail);
        List<InboundMessage> matches = aHashMap.get(key);
        if (matches == null) {
            matches = new ArrayList<>();
            aHashMap.put(key, matches);
        }
        matches.add(aDetail);
    }

    /**
     * Create a key based on the partner link, port type and operation.
     *
     * @param aDetail
     */
    protected static String makeKey(InboundMessage aDetail) {
        return aDetail.getPartnerLinkName() + ":" + aDetail.getPortType() + //$NON-NLS-1$
                ":" + aDetail.getOperation(); //$NON-NLS-1$
    }

    /**
     * Indexed accessor for the queued receive detail.
     *
     * @param aIndex
     */
    public InboundMessage getDetail(int aIndex) {
        mCurrentIndex = aIndex;
        return mDetails.get(aIndex).get(0);
    }

    /**
     * Returns the number of queued receives for the current row.
     */
    public int getQueuedReceiveCount() {
        return mDetails.get(mCurrentIndex).size();
    }

    /**
     * Creates a unique key to identify this row.
     */
    public String getIdentifier() {
        List<InboundMessage> detailList = mDetails.get(mCurrentIndex);
        return makeKey(detailList.get(0));
    }

    /**
     * Returns the number of details rows available.
     */
    public int getDetailSize() {
        return mDetails.size();
    }
}
