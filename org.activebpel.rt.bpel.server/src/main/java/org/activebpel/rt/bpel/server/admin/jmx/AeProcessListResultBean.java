package org.activebpel.rt.bpel.server.admin.jmx;

import java.beans.ConstructorProperties;
import java.util.List;

import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;

public class AeProcessListResultBean {
    private int mTotalCount;
    private boolean mCompleteCount;
    private List<AeProcessInstanceDetail> mResults;

    @ConstructorProperties({"totalCount", "results", "completeCount"})
    public AeProcessListResultBean(int aTotalCount, List<AeProcessInstanceDetail> aResults,
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
    public void setResults(List<AeProcessInstanceDetail> results) {
        mResults = results;
    }
    public List<AeProcessInstanceDetail> getResults() {
        return mResults;
    }
}
