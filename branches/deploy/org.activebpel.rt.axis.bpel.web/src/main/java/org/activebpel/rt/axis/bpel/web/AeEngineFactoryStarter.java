package org.activebpel.rt.axis.bpel.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.scanner.IAeDeploymentFileHandler;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.timer.AeAbstractTimerWork;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import commonj.timers.TimerListener;
import commonj.timers.TimerManager;

public class AeEngineFactoryStarter implements ServletContextListener {

//	/** Initial scan delay init param config. */
//	private static final String SCAN_DELAY_PARAM = "scan.delay"; //$NON-NLS-1$
//	/** Default amount of time to wait before scanning starts. */
//	private static final long DEFAULT_DELAY = 15000;
//	/** The default scan interval */

	private IAeDeploymentFileHandler mFileHandler;
	/** for deployment logging purposes */
	protected static final Log log = LogFactory
			.getLog(AeEngineFactoryStarter.class);

	@Override
	public void contextInitialized(ServletContextEvent aEvent) {
		
		ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(aEvent.getServletContext());
		mFileHandler = ac.getBean(IAeDeploymentFileHandler.class);
		AeEngineFactory.setApplicationContext(ac);
		AeEngineFactory.getEngine().setEngineConfiguration(AeEngineFactory.getEngineConfig());
		
//		long delay = getLongValue(aEvent.getServletContext(), SCAN_DELAY_PARAM,
//				DEFAULT_DELAY);


		doStart();

//		if (delay <= 0) {
//			doStart();
//		} else {
//			// We need to schedule the start after the preset delay interval
//			TimerListener timerWork = new AeAbstractTimerWork() {
//				public void run() {
//					doStart();
//				}
//			};
//
//			AeEngineFactory.getBean(TimerManager.class).schedule(timerWork,delay);
//		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent aEvent) {
		mFileHandler.stopScanning();
		try {
			AeEngineFactory.getEngine().stop();
		} catch (AeBusinessProcessException e1) {
			e1.printStackTrace();
		}
		try {
			AeEngineFactory.getEngine().shutDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mFileHandler = null;
	}

	/**
	 * Start the server if the storage is ready.
	 */
	protected void doStart() {
		boolean startScanning = true;

		// if the initial deployments fail then
		// something is wrong with the bpr dir
		try {
			mFileHandler.handleInitialDeployments();
		} catch (Throwable t) {
			startScanning = false;
			AeException.logError(t, AeMessages
					.getString("AeEngineLifecycleWrapper.ERROR_3")); //$NON-NLS-1$
		}

		startBpelEngine();

		// don't scan if initial deployments failed
		if (startScanning) {
			mFileHandler.startScanning();
		}
	}

	/**
	 * Start the BPEL engine.
	 */
	protected void startBpelEngine() {
		try {
			AeEngineFactory.getEngine().start();
		} catch (AeException ae) {
			ae.logError();
		}
	}

//	/**
//	 * Return the long value for an init param.
//	 * 
//	 * @param aConfig
//	 * @param aParamName
//	 * @param aDefaultValue
//	 */
//	protected long getLongValue(ServletContext aConfig, String aParamName,
//			long aDefaultValue) {
//		long retVal = aDefaultValue;
//		String longValue = aConfig.getInitParameter(aParamName);
//		if (!AeUtil.isNullOrEmpty(longValue)) {
//			try {
//				retVal = Long.parseLong(longValue);
//			} catch (NumberFormatException nfe) {
//
//			}
//		}
//		return retVal;
//	}
}
