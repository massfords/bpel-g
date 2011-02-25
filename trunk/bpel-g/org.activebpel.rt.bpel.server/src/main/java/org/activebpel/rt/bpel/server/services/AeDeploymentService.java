package org.activebpel.rt.bpel.server.services;

import java.io.ByteArrayInputStream;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.bpr.AeTempFileUploadHandler;
import org.activebpel.rt.bpel.server.logging.AeStructuredDeploymentLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bpelg.services.deploy.AeDeployer;
import bpelg.services.deploy.types.DeploymentResponse;

public class AeDeploymentService implements AeDeployer {
	
	private static Log sLog = LogFactory.getLog(AeDeploymentService.class);

	@Override
	public DeploymentResponse deploy(String aName, byte[] aArchive) {
        AeStructuredDeploymentLog logger = new AeStructuredDeploymentLog();
        try {
			AeTempFileUploadHandler.handleUpload( aName, new ByteArrayInputStream(aArchive), logger );
		} catch (AeException e) {
			sLog.error(e);
		}
        DeploymentResponse response = logger.getDeploymentSummary();
        return response;
	}

	@Override
	public boolean undeploy(String aName) {
		return false;
	}
}
