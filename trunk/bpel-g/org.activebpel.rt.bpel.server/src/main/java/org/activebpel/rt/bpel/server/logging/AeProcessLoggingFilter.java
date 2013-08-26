package org.activebpel.rt.bpel.server.logging;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.activebpel.rt.bpel.AeProcessEventType;
import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;

public class AeProcessLoggingFilter implements IAeLoggingFilter {

    private final Set<AeProcessEventType> mProcessEventIds = new CopyOnWriteArraySet<>();

    @Override
    public boolean accept(IAeProcessEvent aEvent) {
        return mProcessEventIds.contains(aEvent.getEventType());
    }

    @Override
    public boolean accept(IAeProcessInfoEvent aInfoEvent) {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return !mProcessEventIds.isEmpty();
    }

    @Override
    public Set<AeProcessEventType> getEnabledEventTypes() {
        return Collections.unmodifiableSet(mProcessEventIds);
    }

    @Override
    public void setEnabledEventTypes(Set<AeProcessEventType> aEnabledEvents) {
        mProcessEventIds.clear();
        mProcessEventIds.addAll(aEnabledEvents);
    }

}
