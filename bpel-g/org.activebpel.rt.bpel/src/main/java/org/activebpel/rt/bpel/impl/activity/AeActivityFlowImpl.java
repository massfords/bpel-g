// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/AeActivityFlowImpl.java,v 1.20 2007/11/21 03:22:16 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeActivity;
import org.activebpel.rt.bpel.def.activity.AeActivityFlowDef;
import org.activebpel.rt.bpel.impl.IAeActivityParent;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.activity.support.AeLink;
import org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor;

import java.util.*;

/**
 * Implementation of the bpel flow activity.
 */
public class AeActivityFlowImpl extends AeActivityImpl implements IAeActivityParent {
    /**
     * maps link names to link objects
     */
    private Map<String, AeLink> mLinks;

    /**
     * list of child activities.
     */
    private final List<IAeActivity> mChildActivities = new ArrayList<>();

    /**
     * default constructor for activity
     */
    public AeActivityFlowImpl(AeActivityFlowDef aActivityDef, IAeActivityParent aParent) {
        super(aActivityDef, aParent);
    }

    /**
     * @see org.activebpel.rt.bpel.impl.visitors.IAeVisitable#accept(org.activebpel.rt.bpel.impl.visitors.IAeImplVisitor)
     */
    public void accept(IAeImplVisitor aVisitor) throws AeBusinessProcessException {
        aVisitor.visit(this);
    }

    /**
     * @see org.activebpel.rt.bpel.impl.IAeActivityParent#addActivity(org.activebpel.rt.bpel.IAeActivity)
     */
    public void addActivity(IAeActivity aActivity) {
        mChildActivities.add(aActivity);
    }

    /**
     * Gets the link by name or returns null if not found.
     *
     * @param aLinkName
     */
    public AeLink getLink(String aLinkName) {
        return getLinkMap().get(aLinkName);
    }

    /**
     * Adds the link to the flow.
     *
     * @param aLink
     */
    public void addLink(AeLink aLink) {
        getLinkMap().put(aLink.getName(), aLink);
    }

    /**
     * Getter for the link map.
     */
    protected Map<String, AeLink> getLinkMap() {
        if (mLinks == null) {
            setLinkMap(new HashMap<String, AeLink>());
        }
        return mLinks;
    }

    /**
     * Setter for the link map.
     *
     * @param aMap
     */
    protected void setLinkMap(Map<String, AeLink> aMap) {
        mLinks = aMap;
    }

    /**
     * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#execute()
     */
    public void execute() throws AeBusinessProcessException {
        super.execute();
        // clear the link status so they will be recomputed (in case we are looping)
        for (AeLink link : getLinkMap().values()) {
            (link).clearStatus();
        }

        // schedule all activities to be run when their links are known
        for (IAeActivity mChildActivity : mChildActivities) {
            getProcess().queueObjectToExecute(mChildActivity);
        }
    }

    /**
     * Handles a child completion by either queing the next activity in list to
     * execute or setting ourselves as complete if it is the last child activity.
     *
     * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#childComplete(org.activebpel.rt.bpel.impl.IAeBpelObject)
     */
    public void childComplete(IAeBpelObject aChild) throws AeBusinessProcessException {
        // removed check for final state since this is being done in the process
        if (childrenAreDone()) {
            objectCompleted();
        }
    }

    /**
     * @see org.activebpel.rt.bpel.impl.IAeBpelObject#getChildrenForStateChange()
     */
    public Iterator<? extends IAeBpelObject> getChildrenForStateChange() {
        return mChildActivities.iterator();
    }

    /**
     * @see org.activebpel.rt.bpel.impl.IAeExecutableBpelObject#terminate()
     */
    public void terminate() throws AeBusinessProcessException {
        if (!isTerminating()) {
            setTerminating(true);
            if (mLinks != null) {
                for (AeLink link : mLinks.values()) {
                    link.setStatus(false);
                }
            }
            super.terminate();
        }
    }
}
