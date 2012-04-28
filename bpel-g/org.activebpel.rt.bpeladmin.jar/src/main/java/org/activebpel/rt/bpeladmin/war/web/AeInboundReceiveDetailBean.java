// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeInboundReceiveDetailBean.java,v 1.1 2004/08/25 20:53:02 PCollins Exp $
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
import java.util.List;

import org.activebpel.rt.bpeladmin.war.AeEngineManagementFactory;

import bpelg.services.queue.types.GetInboundMessages;
import bpelg.services.queue.types.InboundMessage;
import bpelg.services.queue.types.InboundMessages;

/**
 * This bean controls the display of queued receives - either unmatched or
 * message receivers depending on how the bean is configured.
 */
public class AeInboundReceiveDetailBean extends AeAbstractAdminBean {
	/** The key identifying the receives to examine. */
	private String mKey;
	private final List<InboundMessage> mDetails = new ArrayList<InboundMessage>();;

	/**
	 * Default constructor.
	 */
	public AeInboundReceiveDetailBean() {
	}

	/**
	 * Setter for the unique key value.
	 * 
	 * @param aKey
	 *            Uniquely identifies a queued item (via partner link, port type
	 *            and operation).
	 */
	public void setKey(String aKey) {
		mKey = aKey;
		
		InboundMessages ims = AeEngineManagementFactory.getQueueManager().getInboundMessages(new GetInboundMessages());
		for(InboundMessage im : ims.getInboundMessage()) {
			if (AeInboundReceivesBean.makeKey(im).equals(mKey)) {
				mDetails.add(im);
			}
		}
	}

	/**
	 * Returns true if there are no queue details.
	 */
	public boolean isEmpty() {
		return mDetails == null || mDetails.size() == 0;
	}

	/**
	 * Getter for the partner link name.
	 */
	public String getPartnerLinkName() {
		return getDetail(0).getPartnerLinkName();
	}

	/**
	 * Getter for the port type qname as a string.
	 */
	public String getPortTypeAsString() {
		return getDetail(0).getPortType().toString();
	}

	/**
	 * Getter for the operation.
	 */
	public String getOperation() {
		return getDetail(0).getOperation();
	}

	/**
	 * Indexed accessor for the detail row.
	 * 
	 * @param aIndex
	 */
	public InboundMessage getDetail(int aIndex) {
		return mDetails.get(aIndex);
	}

	/**
	 * Returns the size of the detail rows.
	 */
	public int getDetailSize() {
		if (mDetails == null) {
			return 0;
		}
		return mDetails.size();
	}
}
