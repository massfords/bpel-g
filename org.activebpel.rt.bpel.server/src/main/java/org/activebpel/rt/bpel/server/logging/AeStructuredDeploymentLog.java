//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AeStructuredDeploymentLog.java,v 1.5 2007/04/20 14:43:58 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bpelg.services.deploy.types.DeploymentResponse;
import bpelg.services.deploy.types.DeploymentResponse.DeploymentInfo;

/**
 * The structured deployment log breaks up the log messages and creates a
 * structure of deployment info objects from them.
 */
public class AeStructuredDeploymentLog extends AeDeploymentLog {
	/** A map to hold the deployment info for each PDD. */
	protected Map<String,DeploymentInfo> mDeployInfoMap = new LinkedHashMap<String,DeploymentInfo>();
	/** A string buffer to hold the log messages. */
	private StringBuffer mBuffer = new StringBuffer();
	/**
	 * A global string buffer to hold messages that aren't associated with a
	 * single PDD.
	 */
	private StringBuffer mGlobalBuffer = new StringBuffer();
	private static Log sLog = LogFactory.getLog(AeStructuredDeploymentLog.class);

	/**
	 * Overrides method to create a new deployment info object each time a new
	 * PDD is encountered.
	 * 
	 * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger#setPddName(java.lang.String)
	 */
	public void setPddName(String aPddName) {
		super.setPddName(aPddName);

		createDeploymentInfoForPdd(aPddName);
	}

	/**
	 * Overrides method to set the 'deployed' flag on the deployment info object
	 * for the current PDD.
	 * 
	 * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger#processDeploymentFinished(boolean)
	 */
	public void processDeploymentFinished(boolean aBool) {
		// Call super.processDeploymentFinished() after this logic so that the
		// pddName is still valid.
		DeploymentInfo depInfo = mDeployInfoMap.get(getPddName());
		depInfo.setDeployed(aBool);
		depInfo.setLog(mBuffer.toString());
		mBuffer = new StringBuffer();

		super.processDeploymentFinished(aBool);
	}

	/**
	 * @see org.activebpel.rt.bpel.server.logging.AeDeploymentLog#writeMessage(java.lang.String)
	 */
	protected void writeMessage(String aMessage) {
		sLog.error(aMessage);
		StringBuffer buff = mBuffer;
		if (mPddName == null) {
			buff = mGlobalBuffer;
		}
		buff.append(aMessage);
		buff.append("\n"); //$NON-NLS-1$
	}

	/**
	 * Nothing to be done on close - all of the logging is done in memory
	 * (within the deployment info objects).
	 * 
	 * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger#close()
	 */
	public void close() {
	}

	/**
	 * Returns the names of all PDDs deployed (or attempted to be deployed).
	 */
	public Set getPddNames() {
		return mDeployInfoMap.keySet();
	}

	/**
	 * Gets a list of the deployment info objects.
	 */
	public List<DeploymentInfo> getDeploymentInfoList() {
		return new ArrayList<DeploymentInfo>(mDeployInfoMap.values());
	}

	/**
	 * Returns the deployment info for the given pdd name.
	 * 
	 * @param aPddName
	 */
	public DeploymentInfo getDeploymentInfo(String aPddName) {
		return mDeployInfoMap.get(getPddName());
	}

	/**
	 * Returns a summary of the deployment in the form of an instance of
	 * <code>IAeDeploymentSummary</code>.
	 */
	public DeploymentResponse getDeploymentSummary() {
		DeploymentResponse response = new DeploymentResponse().withDeploymentInfo(getDeploymentInfoList());
		if (getGlobalMessages().length() > 0) {
			response.setMessage(getGlobalMessages());
		}
		return response;
	}

	/**
	 * Returns the global messages as a string.
	 */
	public String getGlobalMessages() {
		return mGlobalBuffer.toString();
	}

	/**
	 * Overrides method to increment the number of errors found for the current
	 * PDD deployment.
	 * 
	 * @see org.activebpel.rt.bpel.server.logging.AeDeploymentLog#incrementNumErrors()
	 */
	protected void incrementNumErrors() {
		super.incrementNumErrors();
		DeploymentInfo depInfo = mDeployInfoMap.get(getPddName());
		if (depInfo != null)
			depInfo.setNumberOfErrors(depInfo.getNumberOfErrors()+1);
	}

	/**
	 * Overrides method to increment the number of warnings found for the
	 * current PDD deployment.
	 * 
	 * @see org.activebpel.rt.bpel.server.logging.AeDeploymentLog#incrementNumWarnings()
	 */
	protected void incrementNumWarnings() {
		super.incrementNumWarnings();
		DeploymentInfo depInfo = mDeployInfoMap.get(getPddName());
		if (depInfo != null)
			depInfo.setNumberOfWarnings(depInfo.getNumberOfWarnings()+1);
	}

	/**
	 * Puts a new deployment info object into the map for the given pdd name.
	 * 
	 * @param aPddName
	 */
	protected void createDeploymentInfoForPdd(String aPddName) {
		if (aPddName != null) {
			mDeployInfoMap.put(aPddName, new DeploymentInfo().withName(aPddName));
		}
	}
}
