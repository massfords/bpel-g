package org.activebpel.rt.bpel.server.deploy.bpr;

import bpelg.services.deploy.types.pdd.Pdd;

public class AePddResource {
    private String mName;
    private Pdd mPdd;

    public AePddResource(String aName, Pdd aPdd) {
        setName(aName);
        setPdd(aPdd);
    }

    public String getName() {
        return mName;
    }

    public void setName(String aName) {
        mName = aName;
    }

    public Pdd getPdd() {
        return mPdd;
    }

    public void setPdd(Pdd aPdd) {
        mPdd = aPdd;
    }
}
