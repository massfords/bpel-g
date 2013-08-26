package org.activebpel.rt.bpel.server.admin.jmx;

import java.beans.ConstructorProperties;
import java.util.List;

import bpelg.services.processes.types.ProcessInstanceDetail;

public class AeProcessListResultBean {
    private int mTotalCount;
    private boolean mCompleteCount;
    private List<ProcessInstanceDetail> mResults;

    @ConstructorProperties({"totalCount", "results", "completeCount"})
    public AeProcessListResultBean(int aTotalCount, List<ProcessInstanceDetail> aResults,
                                   boolean aCompleteCount) {
        super();
        mTotalCount = aTotalCount;
        mResults = aResults;
        mCompleteCount = aCompleteCount;
    }

    public void setTotalCount(int totalCount) {
        mTotalCount = totalCount;
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    public void setCompleteCount(boolean completeCount) {
        mCompleteCount = completeCount;
    }

    public boolean isCompleteCount() {
        return mCompleteCount;
    }

    public void setResults(List<ProcessInstanceDetail> results) {
        mResults = results;
    }

    public List<ProcessInstanceDetail> getResults() {
        return mResults;
    }
}
