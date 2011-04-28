package org.activebpel.rt.axis.bpel.web;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileInfo;
import org.activebpel.rt.util.AeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AeEngineFactoryInitializer implements ServletContextListener {

	// ///////////////////////////////////////////////////////////////////////////
	// Init param keys
	// ///////////////////////////////////////////////////////////////////////////
	/** Deployment directory init param key. */
	private static final String BPR_DIR_PARAM = "deployment.directory"; //$NON-NLS-1$
	/** Staging directory init param. */
	private static final String STAGING_DIR_PARAM = "staging.directory"; //$NON-NLS-1$
	/** Servlet home init param key. */
	private static final String SERVLET_HOME_PARAM = "servlet.home"; //$NON-NLS-1$

	// ///////////////////////////////////////////////////////////////////////////
	// Default values for init params
	// ///////////////////////////////////////////////////////////////////////////

	/** the default servlet home */
	private static final String DEFAULT_HOME = "catalina.home"; //$NON-NLS-1$

	/** Default staging directory: work (relative to deployment directory). */
	private static final String DEFAULT_STAGING_DIR = "work"; //$NON-NLS-1$

	/**
	 * Default deployment directory: bpr (relative to server.home init param or
	 * catalina.home if none is specified).
	 */
	private static final String DEFAULT_DEPLOYMENT_DIR = "bpr"; //$NON-NLS-1$


	// ///////////////////////////////////////////////////////////////////////////
	// Member data
	// ///////////////////////////////////////////////////////////////////////////
	/** for deployment logging purposes */
	protected static final Log log = LogFactory
			.getLog(AeEngineFactoryInitializer.class);

	@Override
	public void contextInitialized(ServletContextEvent aEvent) {
		try {
			initFileUtil(aEvent.getServletContext());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent aEvent) {
	}

	/**
	 * Initialize the static contents of the <code>AeActiveBpelFileUtil</code>
	 * class.
	 * 
	 * @param aConfig
	 * @throws AeException
	 */
	protected void initFileUtil(ServletContext aConfig) {
		File servletHome = initServletHome(aConfig);
		File deploymentDir = getFile(servletHome, BPR_DIR_PARAM,
				DEFAULT_DEPLOYMENT_DIR, aConfig);
		File stagingDir = getFile(servletHome, STAGING_DIR_PARAM,
				DEFAULT_STAGING_DIR, aConfig);

		AeDeploymentFileInfo.setDeploymentDirectory(deploymentDir.getPath());
		AeDeploymentFileInfo.setStagingDirectory(stagingDir.getPath());
	}

	/**
	 * Initialize the server home from the "servlet.home" init param. Defaults
	 * to "catalina.home" if none is specified. This value is used as an
	 * environment property lookup key.
	 * 
	 * @param aContext
	 * @throws AeException
	 *             Thrown if there is no corresponding environment property for
	 *             the servlet.home param.
	 */
	protected File initServletHome(ServletContext aContext) {
		// extract the server home value (defaults to "catalina.home"
		// if none is specified
		String servletHomePath = aContext.getInitParameter(SERVLET_HOME_PARAM);
		if (AeUtil.isNullOrEmpty(servletHomePath)) {
			servletHomePath = System.getProperty(DEFAULT_HOME);
		} else {
			servletHomePath = AeUtil.replaceAntStyleParams(servletHomePath,
					System.getProperties());
		}
		File servletHome = new File(servletHomePath);
		return servletHome;
	}

	/**
	 * Convenience method for creating files.
	 * 
	 * @param aServletHome
	 *            Any files that are not absolute will be resolved relative to
	 *            this file.
	 * @param aParamKey
	 *            The init param key.
	 * @param aDefaultPath
	 *            The default value if no init param is specified.
	 * @param aConfig
	 *            The <code>ServletConfig</code>.
	 */
	protected File getFile(File aServletHome, String aParamKey,
			String aDefaultPath, ServletContext aConfig) {
		String filePath = aConfig.getInitParameter(aParamKey);
		if (AeUtil.isNullOrEmpty(filePath)) {
			filePath = aDefaultPath;
		} else {
			filePath = AeUtil.replaceAntStyleParams(filePath,
					System.getProperties());
		}
		File file = new File(filePath);
		if (!file.isAbsolute()) {
			file = new File(aServletHome, filePath);
		}
		return file;
	}
}
