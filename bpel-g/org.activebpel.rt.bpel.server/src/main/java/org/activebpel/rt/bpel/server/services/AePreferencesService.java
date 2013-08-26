package org.activebpel.rt.bpel.server.services;

import bpelg.services.preferences.types.GetPreferencesRequest;
import bpelg.services.preferences.types.PreferencesType;
import bpelg.services.preferences.types.PreferencesType.*;
import bpelg.services.preferences.types.PreferencesType.Logging.EnabledEvents;
import org.activebpel.rt.bpel.AePreferences;

import java.math.BigInteger;

public class AePreferencesService implements bpelg.services.preferences.AePreferences {

    @Override
    public void setPreferences(PreferencesType aBody) {
        AePreferences.setAlarmMaxCount(aBody.getChildWorkManagers().getAlarm().intValue());
        AePreferences.setAllowCreateXpath(aBody.getExecution().isAllowCreateXpath());
        AePreferences.setAllowedRolesEnforced(aBody.getMessaging().isAllowedRulesEnforced());
        AePreferences.setAllowEmptyQuerySelection(aBody.getExecution().isAllowEmptyQuerySelection());
        AePreferences.setLogDeadPathStatus(aBody.getLogging().getEnabledEvents().isDeadPathStatus());
        AePreferences.setLogExecuteComplete(aBody.getLogging().getEnabledEvents().isExecuteComplete());
        AePreferences.setLogExecuteFault(aBody.getLogging().getEnabledEvents().isExecuteFault());
        AePreferences.setLogExecuting(aBody.getLogging().getEnabledEvents().isExecuting());
        AePreferences.setLogFaulting(aBody.getLogging().getEnabledEvents().isFaulting());
        AePreferences.setLoggingDirectory(aBody.getLogging().getDirectory());
        AePreferences.setLoggingLinesHead(aBody.getLogging().getHeadLines().intValue());
        AePreferences.setMaxCorrelationCombinations(aBody.getMessaging().getMaxCorrelationCombinations().intValue());
        AePreferences.setProcessesCount(aBody.getProcesses().getProcessCount().intValue());
        AePreferences.setProcessWorkCount(aBody.getWorkManager().getProcessWorkCount().intValue());
        AePreferences.setReceiveTimeout(aBody.getMessaging().getReceiveTimeout().intValue());
        AePreferences.setReleaseLagMillis(aBody.getProcesses().getReleaseLag());
        AePreferences.setResourceCacheSize(aBody.getCatalog().getCacheSize().intValue());
        AePreferences.setResourceReplaceEnabled(aBody.getCatalog().isResourceReplaceEnabled());
        AePreferences.setRestartEnabled(aBody.getProcesses().isRestartEnabled());
        AePreferences.setSendTimeout(aBody.getMessaging().getSendTimeout().intValue());
        AePreferences.setSuspendProcessOnInvokeRecovery(aBody.getExecution().isSuspendOnInvokeRecovery());
        AePreferences.setSuspendProcessOnUncaughtFault(aBody.getExecution().isSuspendOnUncaughtFaults());
        AePreferences.setThreadPoolMax(aBody.getWorkManager().getThreadPoolMax().intValue());
        AePreferences.setThreadPoolMin(aBody.getWorkManager().getThreadPoolMin().intValue());
        AePreferences.setUnmatchedCorrelatedReceiveTimeoutMillis(aBody.getMessaging().getUnmatchedCorrelatedReceiveTimeout().intValue());
        AePreferences.setWhileLoopAlarmDelayMillis(aBody.getExecution().getWhileLoopAlarmDelay());
        AePreferences.setWhileLoopIterations(aBody.getExecution().getWhileLoopIterations().intValue());
    }

    @Override
    public PreferencesType getPreferences(GetPreferencesRequest aBody) {
        return new PreferencesType()
                .withCatalog(new Catalog()
                        .withCacheSize(bi(AePreferences.getResourceCacheSize()))
                        .withResourceReplaceEnabled(AePreferences.isResourceReplaceEnabled())
                )
                .withChildWorkManagers(new ChildWorkManagers().withAlarm(bi(AePreferences.getAlarmMaxCount())))
                .withExecution(new Execution()
                        .withAllowCreateXpath(AePreferences.isAllowCreateXpath())
                        .withAllowEmptyQuerySelection(AePreferences.isAllowEmptyQuerySelection())
                        .withWhileLoopAlarmDelay(AePreferences.getWhileLoopAlarmDelayMillis())
                        .withWhileLoopIterations(bi(AePreferences.getWhileLoopIterations())))
                .withLogging(new Logging()
                        .withDirectory(AePreferences.getLoggingDirectory())
                        .withHeadLines(bi(AePreferences.getLoggingLinesHead()))
                        .withTailLines(bi(AePreferences.getLoggingLinesTail()))
                        .withEnabledEvents(new EnabledEvents()
                                .withDeadPathStatus(AePreferences.isLogDeadPathStatus())
                                .withExecuteComplete(AePreferences.isLogExecuteComplete())
                                .withExecuteFault(AePreferences.isLogExecuteFault())
                                .withExecuting(AePreferences.isLogExecuting())
                                .withFaulting(AePreferences.isLogFaulting())
                                .withLinkStatus(AePreferences.isLogFaulting())
                                .withMigrated(AePreferences.isLogFaulting())
                                .withReadyToExecute(AePreferences.isLogReadyToExecute())
                                .withSuspended(AePreferences.isLogSuspended())
                                .withTerminated(AePreferences.isLogTerminated())))
                .withMessaging(new Messaging()
                        .withAllowedRulesEnforced(AePreferences.isAllowedRolesEnforced())
                        .withMaxCorrelationCombinations(bi(AePreferences.getMaxCorrelationCombinations()))
                        .withReceiveTimeout(bi(AePreferences.getReceiveTimeout()))
                        .withSendTimeout(bi(AePreferences.getSendTimeout()))
                        .withValidateServiceMessages(AePreferences.isValidateServiceMessages())
                        .withUnmatchedCorrelatedReceiveTimeout(new BigInteger("" + AePreferences.getUnmatchedCorrelatedReceiveTimeoutMillis()))
                )
                .withProcesses(new Processes()
                        .withProcessCount(bi(AePreferences.getProcessCount()))
                        .withReleaseLag(AePreferences.getReleaseLagMillis())
                        .withRestartEnabled(AePreferences.isRestartEnabled()))
                .withWorkManager(new WorkManager()
                        .withProcessWorkCount(bi(AePreferences.getProcessWorkCount()))
                        .withThreadPoolMin(bi(AePreferences.getThreadPoolMin()))
                        .withThreadPoolMax(bi(AePreferences.getThreadPoolMax())));
    }

    private BigInteger bi(int aValue) {
        return new BigInteger(aValue + "");
    }

}
