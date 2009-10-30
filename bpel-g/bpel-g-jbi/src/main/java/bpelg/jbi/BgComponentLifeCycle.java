package bpelg.jbi;

import java.io.File;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.component.ComponentLifeCycle;
import javax.management.ObjectName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileHandler;
import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileInfo;
import org.activebpel.rt.bpel.server.engine.AeEngineLifecycleWrapper;
import org.activebpel.rt.bpel.server.engine.IAeEngineLifecycleWrapper;
import org.activebpel.rt.bpel.server.logging.AeCommonsLoggingImpl;

import bpelg.jbi.exchange.BgReceiver;

/**
 * Receives the lifecycle events from the JBI container. These events are translated
 * into the corresponding events for the bpel-g engine.
 * 
 * @author markford
 */
public class BgComponentLifeCycle implements ComponentLifeCycle {
	
	private static final long DEFAULT_SCAN_DELAY_MILLIS = 15000;
	private static final long DEFAULT_SCAN_INTERVAL_MILLIS = 10000;
	private IAeEngineLifecycleWrapper mEngineLifecycle;
	private BgReceiver mReceiver;
	
	public BgComponentLifeCycle() {
	}

	@Override
	public void init(ComponentContext aContext) throws JBIException {
		try {
			BgContext context = BgContext.getInstance();
			context.setComponentContext(aContext);

			// ugly, but need to init some nested class for file paths
			AeDeploymentFileInfo.setInstallDir(new File(aContext.getInstallRoot()));
            AeDeploymentFileInfo.setConfigFileName("aeEngineConfig.xml");
            AeDeploymentFileInfo.setDeploymentDirectory("bpr");
            AeDeploymentFileInfo.setStagingDirectory("bpr/work");
            
			AeDeploymentFileHandler fileDeployer = new AeDeploymentFileHandler(
					new AeCommonsLoggingImpl(AeDeploymentFileHandler.class),
					DEFAULT_SCAN_INTERVAL_MILLIS);
			
			mEngineLifecycle = new AeEngineLifecycleWrapper(
					new AeCommonsLoggingImpl(BgComponentLifeCycle.class), 
					fileDeployer, DEFAULT_SCAN_DELAY_MILLIS);
			
			mEngineLifecycle.init();
			
			mReceiver = new BgReceiver();
		} catch (Exception e) {
			throw new JBIException("Excepton initializing component", e);
		}
	}

    @Override
    public void start() throws JBIException {
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
        // FIXME not sure I need this
        return null;
    }
}
