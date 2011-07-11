package org.activebpel.rt.bpel.server.admin.jmx;

import java.beans.ConstructorProperties;

public class AeProcessLogPart {
    
    public static final int PART_SIZE = 1024 * 8;
    
    private String mLog;
    private int mPart;
    private boolean mMoreAvailable;
    
    public AeProcessLogPart() {
        
    }
    
    @ConstructorProperties({"log", "part", "moreAvailable"})
    public AeProcessLogPart(String aLog, int aPart, boolean aMoreAvailable) {
        super();
        setLog(aLog);
        setPart(aPart);
        setMoreAvailable(aMoreAvailable);
    }

    public void setLog(String aLog) {
        mLog = aLog;
    }

    public String getLog() {
        return mLog;
    }

    public void setPart(int aPart) {
        mPart = aPart;
    }

    public int getPart() {
        return mPart;
    }

    public boolean isMoreAvailable() {
        return mMoreAvailable;
    }

    public void setMoreAvailable(boolean aMoreAvailable) {
        mMoreAvailable = aMoreAvailable;
    }
}
