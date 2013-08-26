package org.activebpel.rt.bpel.server.logging;

import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.MessageType;
import bpelg.services.deploy.types.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author markford
 *         Date: 4/14/12
 */
public class DeploymentLogger implements IAeDeploymentLogger {

    private final Map<String, DeploymentResponse.DeploymentInfo> infos = new LinkedHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(DeploymentLogger.class);
    private String currentPdd;
    private boolean hasErrors = false;
    private boolean hasWarnings = false;
    private final List<Msg> messages = new ArrayList<>();

    @Override
    public void setContainerName(String containerName) {
        log.debug("Deploying processes and wsdl in {}", containerName);
    }

    @Override
    public void setPddName(String pddName) {
        hasErrors = false;
        hasWarnings = false;
        currentPdd = pddName;
        log.debug("Processing deployment descriptor {}", currentPdd);
        infos.put(currentPdd, new DeploymentResponse.DeploymentInfo().withLog(new DeploymentResponse.DeploymentInfo.Log()));
        current().withName(pddName);
    }

    @Override
    public void processDeploymentFinished(boolean success) {

        if (current() != null) {
            current().withDeployed(success && current().getNumberOfErrors() == 0);
        }
        if (success) {
            log.debug("Deployment successful");
        } else {
            log.debug("Deployment failed");
        }
    }

    @Override
    public Collection<DeploymentResponse.DeploymentInfo> getDeploymentInfos() {
        return infos.values();
    }

    @Override
    public void addContainerMessage(Msg msg) {
        messages.add(msg);
    }

    @Override
    public Collection<Msg> getContainerMessages() {
        return messages;
    }

    @Override
    public void addInfo(String infoCode, Object[] args, Object node) {
        addMessage(infoCode, args, MessageType.INFO);
    }

    @Override
    public void addError(String errorCode, Object[] args, Object node) {
        hasErrors = true;
        addMessage(errorCode, args, MessageType.ERROR);
        current().withNumberOfErrors(current().getNumberOfErrors() + 1);
    }

    @Override
    public void addWarning(String warnCode, Object[] args, Object node) {
        hasWarnings = true;
        addMessage(warnCode, args, MessageType.WARNING);
        current().withNumberOfWarnings(current().getNumberOfWarnings() + 1);
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }

    @Override
    public boolean hasWarnings() {
        return hasWarnings;
    }

    private void addMessage(String infoCode, Object[] args, MessageType type) {
        String message = MessageFormat.format(infoCode, args);

        switch (type) {
            case ERROR:
                log.error(message);
                break;
            case INFO:
                log.info(message);
                break;
            case WARNING:
                log.warn(message);
                break;
        }

        DeploymentResponse.DeploymentInfo info = current();
        info.getLog().withMsg(
                new Msg()
                        .withType(type)
                        .withValue(message)
        );
    }

    private DeploymentResponse.DeploymentInfo current() {
        String key = currentPdd;
        if (currentPdd == null) {
            key = "_container";
        }
        DeploymentResponse.DeploymentInfo info = infos.get(key);
        if (info == null) {
            info = new DeploymentResponse.DeploymentInfo();
            infos.put(key, info);
        }
        return info;
    }
}
