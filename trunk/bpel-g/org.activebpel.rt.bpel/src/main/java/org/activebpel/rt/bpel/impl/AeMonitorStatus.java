package org.activebpel.rt.bpel.impl;

public enum AeMonitorStatus {
    Normal(0),
    Warning(1),
    Error(2);

    private final int code;

    AeMonitorStatus(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }
}
