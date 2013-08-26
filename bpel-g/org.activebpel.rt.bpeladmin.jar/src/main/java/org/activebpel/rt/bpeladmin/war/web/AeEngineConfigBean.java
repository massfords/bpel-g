// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/web/AeEngineConfigBean.java,v 1.26 2008/02/26 02:00:25 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war.web;

import bpelg.services.processes.types.GetProcessDeployments;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AePreferences;
import org.activebpel.rt.bpel.AeProcessEventType;
import org.activebpel.rt.bpel.server.admin.AeBuildInfo;
import org.activebpel.rt.bpeladmin.war.AeBuildNumber;
import org.activebpel.rt.bpeladmin.war.AeEngineManagementFactory;
import org.activebpel.rt.bpeladmin.war.AeMessages;

import java.util.*;
import java.util.prefs.BackingStoreException;

/**
 * Bean for driving display of home page.
 */
public class AeEngineConfigBean extends AeAbstractAdminBean {
    /**
     * Build info information.
     */
    protected final AeBuildInfo[] mBuildInfos;

    // internal state
    private final Set<AeProcessEventType> mNewFilter = new HashSet<>();
    private boolean mAllowEmptyQuery;
    private boolean mValidateMessages;
    private boolean mCreateXPath;
    private boolean mReplaceResources;

    /**
     * Default constructor.
     */
    public AeEngineConfigBean() {
        mBuildInfos = getBuildInfo();
    }

    /**
     * Gets the array of build info objects.
     */
    protected AeBuildInfo[] getBuildInfo() {
        List<AeBuildInfo> list = new ArrayList<>();
        AeBuildInfo[] infoArray = getAdmin().getBuildInfo();
        for (AeBuildInfo bi : infoArray) {
            list.add(bi);
        }

        addAdminBuildInfo(list);

        return list.toArray(new AeBuildInfo[list.size()]);
    }

    /**
     * Creates an additional build info object for the administration console.
     */
    protected void addAdminBuildInfo(List<AeBuildInfo> aList) {
        try {
            aList.add(AeBuildInfo.createBuildInfoFor(AeBuildNumber.class, AeMessages.getString("AeEngineConfigBean.Admin"))); //$NON-NLS-1$
        } catch (AeException ae) {
        }
    }

    /**
     * Setter for the create XPath property.
     *
     * @param aValue
     */
    public void setAllowCreateXPath(boolean aValue) {
        mCreateXPath = aValue;
    }

    /**
     * Getter for create XPath property.
     */
    public boolean isAllowCreateXPath() {
        return getAdmin().isAllowCreateXPath();
    }

    /**
     * Setter for allow empty query property.
     *
     * @param aValue
     */
    public void setAllowEmptyQuery(boolean aValue) {
        mAllowEmptyQuery = aValue;
    }

    /**
     * Getter for allow empty query property.
     */
    public boolean isAllowEmptyQuery() {
        return getAdmin().isAllowEmptyQuerySelection();
    }

    /**
     * Setter for validate service messages property.
     *
     * @param aValue
     */
    public void setValidateServiceMessages(boolean aValue) {
        mValidateMessages = aValue;
    }

    /**
     * Getter for validate services message property.
     */
    public boolean isValidateServiceMessages() {
        return getAdmin().isValidateServiceMessages();
    }

    /**
     * Setter for time out value.
     *
     * @param aTimeout time in seconds
     */
    public void setUnmatchedCorrelatedReceiveTimeout(long aTimeout) {
        getAdmin().setUnmatchedCorrelatedReceiveTimeoutMillis(aTimeout);
    }

    /**
     * Setter for web service timeouts, applies to message invokes.
     *
     * @param aTimeout
     */
    public void setWebServiceInvokeTimeout(int aTimeout) {
        getAdmin().setWebServiceInvokeTimeout(aTimeout);
    }

    /**
     * Setter for web service timeouts, applies to messages sent to the engine..
     *
     * @param aTimeout
     */
    public void setWebServiceReceiveTimeout(int aTimeout) {
        getAdmin().setWebServiceReceiveTimeout(aTimeout);
    }

    /**
     * Getter for time out value.
     */
    public long getUnmatchedCorrelatedReceiveTimeout() {
        return getAdmin().getUnmatchedCorrelatedReceiveTimeoutMillis();
    }

    /**
     * Getter for the invoke timeout value
     */
    public int getWebServiceInvokeTimeout() {
        return getAdmin().getWebServiceInvokeTimeout();
    }

    /**
     * Getter for the receive timeout value
     */
    public int getWebServiceReceiveTimeout() {
        return getAdmin().getWebServiceReceiveTimeout();
    }

    /**
     * Setter for work manager thread pool min.
     *
     * @param aValue
     */
    public void setThreadPoolMin(int aValue) {
        // Only try to set this value if we are using internal WrokManager
        if (isInternalWorkManager())
            getAdmin().setThreadPoolMin(aValue);
    }

    /**
     * Getter for work manager thread pool min.
     */
    public int getThreadPoolMin() {
        return getAdmin().getThreadPoolMin();
    }

    /**
     * Setter for work manager thread pool max.
     *
     * @param aValue
     */
    public void setThreadPoolMax(int aValue) {
        // Only try to set this value if we are using internal WrokManager
        if (isInternalWorkManager())
            getAdmin().setThreadPoolMax(aValue);
    }

    /**
     * Getter for work manager thread pool max.
     */
    public int getThreadPoolMax() {
        return getAdmin().getThreadPoolMax();
    }

    /**
     * Setter for process work count.
     *
     * @param aValue
     */
    public void setProcessWorkCount(int aValue) {
        getAdmin().setProcessWorkCount(aValue);
    }

    /**
     * Getter for process work count.
     */
    public int getProcessWorkCount() {
        return getAdmin().getProcessWorkCount();
    }

    /**
     * Sets max work count for the Alarm child work manager.
     *
     * @param aValue
     */
    public void setAlarmMaxWorkCount(int aValue) {
        // Anything less than 0 is the same as 0.
        int maxWorkCount = (aValue < 0) ? 0 : aValue;

        getAdmin().setAlarmMaxWorkCount(maxWorkCount);
    }

    /**
     * Returns current configuration max work count for the Alarm child work
     * manager.
     *
     * @return max work count for the Alarm child work manager
     */
    public int getAlarmMaxWorkCount() {
        return getAdmin().getAlarmMaxWorkCount();
    }

    /**
     * Indicates that all updates have taken place if the
     * the given value is set to true.
     *
     * @param aValue Flag to signal ok to do engine config updates.
     */
    public void setFinished(boolean aValue) {
        if (aValue) {
            getAdmin().setAllowCreateXPath(mCreateXPath);
            AePreferences.setLoggingEvents(mNewFilter);
            getAdmin().setAllowEmptyQuerySelection(mAllowEmptyQuery);
            getAdmin().setValidateServiceMessages(mValidateMessages);
            getAdmin().setResourceReplaceEnabled(mReplaceResources);
            try {
                AePreferences.root().flush();
            } catch (BackingStoreException e) {
            }
        }
    }

    public boolean isLogReadyToExecute() {
        return AePreferences.isLogReadyToExecute();
    }

    public boolean isLogExecuting() {
        return AePreferences.isLogExecuting();
    }

    public boolean isLogExecuteComplete() {
        return AePreferences.isLogExecuteComplete();
    }

    public boolean isLogExecuteFault() {
        return AePreferences.isLogExecuteFault();
    }

    public boolean isLogLinkStatus() {
        return AePreferences.isLogLinkStatus();
    }

    public boolean isLogDeadPathStatus() {
        return AePreferences.isLogDeadPathStatus();
    }

    public boolean isLogTerminated() {
        return AePreferences.isLogTerminated();
    }

    public boolean isLogSuspended() {
        return AePreferences.isLogSuspended();
    }

    public boolean isLogFaulting() {
        return AePreferences.isLogFaulting();
    }

    public void setLogReadyToExecute(boolean aFlag) {
        if (aFlag)
            mNewFilter.add(AeProcessEventType.ReadyToExecute);
    }

    public void setLogExecuting(boolean aFlag) {
        if (aFlag)
            mNewFilter.add(AeProcessEventType.Executing);
    }

    public void setLogExecuteComplete(boolean aFlag) {
        if (aFlag)
            mNewFilter.add(AeProcessEventType.ExecuteComplete);
    }

    public void setLogExecuteFault(boolean aFlag) {
        if (aFlag)
            mNewFilter.add(AeProcessEventType.ExecuteFault);
    }

    public void setLogLinkStatus(boolean aFlag) {
        if (aFlag)
            mNewFilter.add(AeProcessEventType.LinkStatus);
    }

    public void setLogDeadPathStatus(boolean aFlag) {
        if (aFlag)
            mNewFilter.add(AeProcessEventType.DeadPathStatus);
    }

    public void setLogTerminated(boolean aFlag) {
        if (aFlag)
            mNewFilter.add(AeProcessEventType.Terminated);
    }

    public void setLogSuspended(boolean aFlag) {
        if (aFlag)
            mNewFilter.add(AeProcessEventType.Suspended);
    }

    public void setLogFaulting(boolean aFlag) {
        if (aFlag)
            mNewFilter.add(AeProcessEventType.Faulting);
    }

    /**
     * Returns the number of deployed processes.
     */
    public int getDeployedProcessesSize() {
        return AeEngineManagementFactory.getProcessManager().getProcessDeployments(new GetProcessDeployments()).getProcessDeployment().size();
    }

    /**
     * Returns the engine start date (output will be formatted according to
     * the date pattern property or date.toString() if none is specified).
     */
    public Date getStartDate() {
        return getAdmin().getStartDate();
    }

    /**
     * Indexed accessor for build infos.
     *
     * @param aIndex
     */
    public AeBuildInfo getBuildInfo(int aIndex) {
        return mBuildInfos[aIndex];
    }

    /**
     * Returns the build info array size.
     */
    public int getBuildInfoSize() {
        if (mBuildInfos == null) {
            return 0;
        }
        return mBuildInfos.length;
    }

    /**
     * Setter for the resource cache max.
     *
     * @param aMax
     */
    public void setResourceCacheMax(int aMax) {
        getAdmin().setCatalogCacheSize(aMax);
    }

    /**
     * Getter for the resource cache max.
     */
    public int getResourceCacheMax() {
        return getAdmin().getCatalogCacheSize();
    }

    /**
     * Return true if resource replace flag is set to true.
     */
    public boolean isResourceReplaceEnabled() {
        return getAdmin().isResourceReplaceEnabled();
    }

    /**
     * Setter for resource replacement flag.
     *
     * @param aFlag
     */
    public void setResourceReplaceEnabled(boolean aFlag) {
        mReplaceResources = aFlag;
    }

    /**
     * Returns True if using internal WorkManager or False if using server implementation.
     */
    public boolean isInternalWorkManager() {
        return getAdmin().isInternalWorkManager();
    }

}