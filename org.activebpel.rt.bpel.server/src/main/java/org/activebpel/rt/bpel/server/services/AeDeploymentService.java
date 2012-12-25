package org.activebpel.rt.bpel.server.services;

import bpelg.services.deploy.AeDeployer;
import bpelg.services.deploy.MissingResourcesException;
import bpelg.services.deploy.UnhandledException;
import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.MessageType;
import bpelg.services.deploy.types.Msg;
import bpelg.services.deploy.types.UndeploymentRequest;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.bpr.AeTempFileUploadHandler;
import org.activebpel.rt.bpel.server.deploy.scanner.IAeDeploymentFileHandler;
import org.activebpel.rt.bpel.server.logging.DeploymentLogger;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;

public class AeDeploymentService implements AeDeployer {

    @Inject
	private IAeDeploymentFileHandler mDeploymentHandler;
	
	private static Log sLog = LogFactory.getLog(AeDeploymentService.class);

	@Override
	public DeploymentResponse deploy(String aName, byte[] aArchive) throws UnhandledException, MissingResourcesException {
        DeploymentResponse response = new DeploymentResponse();
        IAeDeploymentLogger logger = new DeploymentLogger();
        try {
			AeTempFileUploadHandler.handleUpload( aName, new ByteArrayInputStream(aArchive), logger );
		} catch (AeException e) {
//			sLog.error(e);
            response.withMsg(new Msg().withType(MessageType.ERROR).withValue(e.getMessage()));
		}
        response.withDeploymentInfo(logger.getDeploymentInfos());
        response.setDeploymentContainerId(aName);
        response.withMsg(logger.getContainerMessages());
        return response;
	}

	@Override
	public boolean undeploy(UndeploymentRequest aName) {
		return getDeploymentHandler().undeploy(aName);
	}

	public IAeDeploymentFileHandler getDeploymentHandler() {
		return mDeploymentHandler;
	}

	public void setDeploymentHandler(IAeDeploymentFileHandler aDeploymentHandler) {
		mDeploymentHandler = aDeploymentHandler;
	}
}
