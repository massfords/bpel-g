package bpelg.jbi;

import java.io.File;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.component.ComponentLifeCycle;
import javax.management.ObjectName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileInfo;
import org.activebpel.rt.bpel.server.deploy.scanner.IAeDeploymentFileHandler;
import org.activebpel.rt.bpel.server.engine.AeEngineLifecycleWrapper;
import org.activebpel.rt.bpel.server.engine.IAeEngineLifecycleWrapper;
import org.activebpel.rt.bpel.server.logging.AeCommonsLoggingImpl;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bpelg.jbi.exchange.BgReceiver;

/**
 * Receives the lifecycle events from the JBI container. These events are translated
 * into the corresponding events for the bpel-g engine.
 * 
 * @author markford
 */
public class BgComponentLifeCycle implements ComponentLifeCycle {
	
	private IAeEngineLifecycleWrapper mEngineLifecycle;
	private BgReceiver mReceiver;
	
	private static final Log sLog = LogFactory.getLog(BgComponentLifeCycle.class);
	
	public BgComponentLifeCycle() {
	}

	@Override
	public void init(ComponentContext aContext) throws JBIException {
	    sLog.trace("initializing bpelg component");
		try {
			BgContext context = BgContext.getInstance();
			context.setComponentContext(aContext);
			
			// we don't need a file handler since deployments should be managed by the JBI container
			IAeDeploymentFileHandler noop = new IAeDeploymentFileHandler() {

                @Override
                public void handleDeployment(File aFile, String aBprName, IAeDeploymentLogger aLogger)
                        throws AeException {
                }

                @Override
                public void handleInitialDeployments() {
                }

                @Override
                public void startScanning() {
                }

                @Override
                public void stopScanning() {
                }
			    
			};
			
			AeDeploymentFileInfo.setInstallDir(new File(aContext.getInstallRoot()));
            AeDeploymentFileInfo.setConfigFileName("aeEngineConfig.xml");
            AeDeploymentFileInfo.setDeploymentDirectory("bpr");
            AeDeploymentFileInfo.setStagingDirectory("bpr/work");
			

			mEngineLifecycle = new AeEngineLifecycleWrapper(
					new AeCommonsLoggingImpl(BgComponentLifeCycle.class), 
					noop, 0);
			
			mEngineLifecycle.init();
			
			mReceiver = new BgReceiver();
		} catch (Exception e) {
			throw new JBIException("Excepton initializing component", e);
		}
	}

    @Override
    public void start() throws JBIException {
        sLog.trace("starting bpelg component");
        try {
            mEngineLifecycle.start();
        } catch (AeException e) {
            throw new JBIException("Exception during start of engine", e);
        } finally {
            mReceiver.start();
        }
    }

    @Override
    public void stop() throws JBIException {
        sLog.trace("stopping bpelg component");
        try {
            mEngineLifecycle.stop();
        } catch (AeException e) {
            throw new JBIException("Exception during start of engine", e);
        } finally {
            mReceiver.cease();
        }
    }

	@Override
	public void shutDown() throws JBIException {
        sLog.trace("shutting down bpelg component");
        try {
            mEngineLifecycle.shutdown();
        } catch (AeException e) {
            throw new JBIException("Exception during start of engine", e);
        } finally {
            mReceiver = null;
        }
	}

	@Override
    public ObjectName getExtensionMBeanName() {
        return null;
    }
}
