package org.activebpel.rt.bpel;

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

public class AePreferences {
    public static Preferences root() {
        return Preferences.systemRoot().node("org/activebpel/bpelg");
    }

    public static Preferences workManager() {
        return root().node("work-manager");
    }

    public static Preferences childWorkManagers() {
        return root().node("child-work-managers");
    }

    public static Preferences logging() {
        return root().node("logging");
    }

    public static Preferences catalog() {
        return root().node("catalog");
    }

    public static Preferences execution() {
        return root().node("execution");
    }

    public static Preferences messaging() {
        return root().node("messaging");
    }

    public static Preferences processes() {
        return root().node("processes");
    }

    public static Preferences logEvents() {
        final Preferences logging = logging();
        return logging.node("enabledEvents");
    }

    public static int getProcessCount() {
        return processes().getInt("processCount", 50);
    }

    public static void setProcessesCount(int aCount) {
        processes().putInt("processCount", aCount);
    }

    public static long getReleaseLagMillis() {
        return processes().getLong("releaseLag", 10000);
    }

    public static void setReleaseLagMillis(long aMillis) {
        processes().putLong("releaseLong", aMillis);
    }

    public static boolean isRestartEnabled() {
        return processes().getBoolean("restartEnabled", false);
    }

    public static void setRestartEnabled(boolean aRestart) {
        processes().putBoolean("restartEnabled", aRestart);
    }

    public static boolean isAllowCreateXpath() {
        return execution().getBoolean("allowCreateXpath", false);
    }

    public static void setAllowCreateXpath(boolean aFlag) {
        execution().putBoolean("allowCreateXpath", aFlag);
    }

    public static boolean isAllowEmptyQuerySelection() {
        return execution().getBoolean("allowEmptyQuerySelection", false);
    }

    public static void setAllowEmptyQuerySelection(boolean aFlag) {
        execution().putBoolean("allowEmptyQuerySelection", aFlag);
    }

    public static long getWhileLoopAlarmDelayMillis() {
        return execution().getLong("whileLoopAlarmDelay", 100);
    }

    public static void setWhileLoopAlarmDelayMillis(long aMillis) {
        execution().putLong("whileLoopAlarmDelay", aMillis);
    }

    public static int getWhileLoopIterations() {
        return execution().getInt("whileLoopIterations", 1000);
    }

    public static void setWhileLoopIterations(int aCount) {
        execution().putInt("whileLoopIterations", aCount);
    }


    public static int getReceiveTimeout() {
        return messaging().getInt("receiveTimeout", 600);
    }

    public static void setReceiveTimeout(int aTimeout) {
        messaging().putInt("receiveTimeout", aTimeout);
    }

    public static int getSendTimeout() {
        return messaging().getInt("sendTimeout", 600);
    }

    public static void setSendTimeout(int aTimeout) {
        messaging().putInt("sendTimeout", aTimeout);
    }

    public static boolean isValidateServiceMessages() {
        return messaging().getBoolean("validateServiceMessages", true);
    }

    public static void setValidateServiceMessages(boolean aFlag) {
        messaging().putBoolean("validateServiceMessages", aFlag);
    }

    public static boolean isAllowedRolesEnforced() {
        return messaging().getBoolean("allowedRolesEnforced", true);
    }

    public static void setAllowedRolesEnforced(boolean aFlag) {
        messaging().putBoolean("allowedRolesEnforced", aFlag);
    }

    public static String getSharedSecret() {
        return root().get("SharedSecret", "terces");
    }

    public static int getResourceCacheSize() {
        return catalog().getInt("cacheSize", 50);
    }

    public static void setResourceCacheSize(int aSize) {
        catalog().putInt("cacheSize", aSize);
    }

    public static boolean isResourceReplaceEnabled() {
        return catalog().getBoolean("resourceReplaceEnabled", false);
    }

    public static void setResourceReplaceEnabled(boolean aFlag) {
        catalog().putBoolean("resourceReplaceEnabled", aFlag);
    }

    public static Set<AeProcessEventType> getEnabledLogEvents() {
        Set<AeProcessEventType> enabled = new HashSet<>();
        if (isLogDeadPathStatus())
            enabled.add(AeProcessEventType.DeadPathStatus);
        if (isLogExecuteComplete())
            enabled.add(AeProcessEventType.ExecuteComplete);
        if (isLogExecuteFault())
            enabled.add(AeProcessEventType.ExecuteFault);
        if (isLogExecuting())
            enabled.add(AeProcessEventType.Executing);
        if (isLogFaulting())
            enabled.add(AeProcessEventType.Faulting);
        if (isLogLinkStatus())
            enabled.add(AeProcessEventType.LinkStatus);
        if (isLogMigrated())
            enabled.add(AeProcessEventType.Migrated);
        if (isLogReadyToExecute())
            enabled.add(AeProcessEventType.ReadyToExecute);
        if (isLogSuspended())
            enabled.add(AeProcessEventType.Suspended);
        if (isLogTerminated())
            enabled.add(AeProcessEventType.Terminated);
        return enabled;
    }

    public static void setLoggingEvents(Set<AeProcessEventType> enabled) {
        setLogDeadPathStatus(enabled.contains(AeProcessEventType.DeadPathStatus));
        setLogExecuteComplete(enabled.contains(AeProcessEventType.ExecuteComplete));
        setLogExecuteFault(enabled.contains(AeProcessEventType.ExecuteFault));
        setLogExecuting(enabled.contains(AeProcessEventType.Executing));
        setLogFaulting(enabled.contains(AeProcessEventType.Faulting));
        setLogLinkStatus(enabled.contains(AeProcessEventType.LinkStatus));
        setLogMigrated(enabled.contains(AeProcessEventType.Migrated));
        setLogReadyToExecute(enabled.contains(AeProcessEventType.ReadyToExecute));
        setLogSuspended(enabled.contains(AeProcessEventType.Suspended));
        setLogTerminated(enabled.contains(AeProcessEventType.Terminated));
    }

    public static boolean isLogReadyToExecute() {
        return logEvents().getBoolean("readyToExecute", false);
    }

    public static void setLogReadyToExecute(boolean aFlag) {
        logEvents().putBoolean("readyToExecute", aFlag);
    }

    public static boolean isLogExecuting() {
        return logEvents().getBoolean("executing", false);
    }

    public static void setLogExecuting(boolean aFlag) {
        logEvents().putBoolean("executing", aFlag);
    }

    public static boolean isLogExecuteComplete() {
        return logEvents().getBoolean("executeComplete", false);
    }

    public static void setLogExecuteComplete(boolean aFlag) {
        logEvents().putBoolean("executeComplete", aFlag);
    }

    public static boolean isLogExecuteFault() {
        return logEvents().getBoolean("executeFault", false);
    }

    public static void setLogExecuteFault(boolean aFlag) {
        logEvents().putBoolean("executeFault", aFlag);
    }

    public static boolean isLogLinkStatus() {
        return logEvents().getBoolean("linkStatus", false);
    }

    public static void setLogLinkStatus(boolean aFlag) {
        logEvents().putBoolean("linkStatus", aFlag);
    }

    public static boolean isLogDeadPathStatus() {
        return logEvents().getBoolean("deadPathStatus", false);
    }

    public static void setLogDeadPathStatus(boolean aFlag) {
        logEvents().putBoolean("deadPathStatus", aFlag);
    }

    public static boolean isLogTerminated() {
        return logEvents().getBoolean("terminated", false);
    }

    public static void setLogTerminated(boolean aFlag) {
        logEvents().putBoolean("terminated", aFlag);
    }

    public static boolean isLogMigrated() {
        return logEvents().getBoolean("migrated", false);
    }

    public static void setLogMigrated(boolean aFlag) {
        logEvents().putBoolean("migrated", aFlag);
    }

    public static boolean isLogSuspended() {
        return logEvents().getBoolean("suspended", false);
    }

    public static void setLogSuspended(boolean aFlag) {
        logEvents().putBoolean("suspended", aFlag);
    }

    public static boolean isLogFaulting() {
        return logEvents().getBoolean("faulting", false);
    }

    public static void setLogFaulting(boolean aFlag) {
        logEvents().putBoolean("faulting", aFlag);
    }


    public static int getProcessWorkCount() {
        return workManager().getInt("processWorkCount", 10);
    }

    public static void setProcessWorkCount(int aCount) {
        workManager().putInt("processWorkCount", aCount);
    }

    public static int getThreadPoolMin() {
        return workManager().getInt("threadPoolMin", 10);
    }

    public static void setThreadPoolMin(int aMin) {
        workManager().putInt("threadPoolMin", aMin);
    }

    public static int getThreadPoolMax() {
        return workManager().getInt("threadPoolMax", 50);
    }

    public static void setThreadPoolMax(int aMax) {
        workManager().putInt("threadPoolMax", aMax);
    }

    public static int getAlarmMaxCount() {
        return childWorkManagers().getInt("Alarm", 5);
    }

    public static void setAlarmMaxCount(int aCount) {
        childWorkManagers().putInt("Alarm", aCount);
    }

    public static long getUnmatchedCorrelatedReceiveTimeoutMillis() {
        return messaging().getLong("unmatchedCorrelatedReceiveTimeout", 30000);
    }

    public static void setUnmatchedCorrelatedReceiveTimeoutMillis(long aTimeout) {
        messaging().putLong("unmatchedCorrelatedReceiveTimeout", aTimeout);
    }

    public static boolean isSuspendProcessOnUncaughtFault() {
        return execution().getBoolean("suspendOnUncaughtFault", false);
    }

    public static void setSuspendProcessOnUncaughtFault(boolean aFlag) {
        execution().putBoolean("suspendOnUncaughtFault", aFlag);
    }

    public static boolean isSuspendProcessOnInvokeRecovery() {
        return execution().getBoolean("suspendOnInvokeRecovery", false);
    }

    public static void setSuspendProcessOnInvokeRecovery(boolean aFlag) {
        execution().putBoolean("suspendOnInvokeRecovery", aFlag);
    }

    public static String getLoggingDirectory() {
        return logging().get("directory", System.getProperty("java.io.tmpdir") + "/bpel-g");
    }

    public static void setLoggingDirectory(String aDirectory) {
        logging().put("directory", aDirectory);
    }

    public static int getLoggingLinesHead() {
        return logging().getInt("headLines", 100);
    }

    public static void setLoggingLinesHead(int aLines) {
        logging().putInt("headLines", aLines);
    }

    public static int getLoggingLinesTail() {
        return logging().getInt("tailLines", 500);
    }

    public static void setLoggingLinesTail(int aLines) {
        logging().putInt("tailLines", aLines);
    }

    public static int getMaxCorrelationCombinations() {
        return messaging().getInt("maxCombinations", 10);
    }

    public static void setMaxCorrelationCombinations(int aMax) {
        messaging().putInt("maxCombinations", aMax);
    }
}
