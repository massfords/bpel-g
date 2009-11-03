package bpelg.jbi.su;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import javax.jbi.component.ServiceUnitManager;
import javax.jbi.management.DeploymentException;

import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandler;
import org.activebpel.rt.bpel.server.deploy.validate.AeDeploymentFileValidator;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.AeCommonsLoggingImpl;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.bpel.server.logging.IAeLogWrapper;

import bpelg.jbi.su.ode.BgDeploymentContainer;

/**
 * Handles the deployment and undeployment of service units
 * 
 * @author mford
 */
public class BgServiceUnitManager implements ServiceUnitManager {

    private static final String DEPLOY_XML = 
        "<component-task-result xmlns=''http://java.sun.com/xml/ns/jbi/management-message''> " + 
    		"           <component-name>bpel-g</component-name>" + 
    		"           <component-task-result-details>" + 
    		"              <task-result-details>" + 
    		"                   <task-id>{0}</task-id> " + 
    		"                   <task-result>{1}</task-result>" + 
    		"              </task-result-details> " + 
    		"           </component-task-result-details>" + 
    		"          </component-task-result>"; 
    
    private static final IAeLogWrapper mLog = new AeCommonsLoggingImpl(BgServiceUnitManager.class);

    @Override
    public String deploy(String aServiceUnitName, String aServiceUnitRootPath)
            throws DeploymentException {
        String message = null;
        try {
            IAeDeploymentLogger logger = new BgDeploymentLogger();

            IAeDeploymentContainer deployContainer = createDeploymentContainer(new File(aServiceUnitRootPath));

            AeDeploymentFileValidator.validateFileType(deployContainer, true, logger);

            // If the file type is valid, then use the deployment handler to
            // deploy the BPR.
            if (!logger.hasErrors()) {
                IAeDeploymentHandler handler = AeEngineFactory.getDeploymentHandlerFactory().newInstance(mLog);
                handler.deploy(deployContainer, logger);
                message = "SUCCESS";
            } else { 
                mLog.logInfo(MessageFormat.format(AeMessages.getString("AeDeploymentFileHandler.1"), //$NON-NLS-1$
                        new Object[] { deployContainer.getShortName() }));
                message = "ERROR";
            }
        } catch (Throwable t) {
            mLog.logError(
                    MessageFormat.format(
                            AeMessages.getString("AeDeploymentFileHandler.ERROR_2"), new Object[] { aServiceUnitName }), t); //$NON-NLS-1$
            message = stacktraceToString(t);
        }
        return MessageFormat.format(DEPLOY_XML, "deploy", message);
    }
    
    @Override
    public String undeploy(String aServiceUnitName, String aServiceUnitRootPath)
            throws DeploymentException {
        
        String message = null;
        
        try {
    
            IAeDeploymentContainer deployContainer = createDeploymentContainer(new File(aServiceUnitRootPath));
            IAeDeploymentHandler handler = AeEngineFactory.getDeploymentHandlerFactory().newInstance(mLog);
            handler.undeploy(deployContainer);
            message = "SUCCESS";
        } catch(Throwable t) {
            message = stacktraceToString(t);
        }
        
        return MessageFormat.format(DEPLOY_XML, "undeploy", message);
    }

    @Override
    public void init(String aServiceUnitName, String aServiceUnitRootPath)
            throws DeploymentException {
        // FIXME the open source engine doesn't have hooks for init'ing, starting, stopping, or shutting down processes. 
    }

    @Override
    public void shutDown(String aServiceUnitName) throws DeploymentException {
        // FIXME the open source engine doesn't have hooks for init'ing, starting, stopping, or shutting down processes. 
    }

    @Override
    public void start(String aServiceUnitName) throws DeploymentException {
        // FIXME the open source engine doesn't have hooks for init'ing, starting, stopping, or shutting down processes. 
    }

    @Override
    public void stop(String aServiceUnitName) throws DeploymentException {
        // FIXME the open source engine doesn't have hooks for init'ing, starting, stopping, or shutting down processes. 
    }

    protected IAeDeploymentContainer createDeploymentContainer( File aFile )
    throws Exception
    {
        return new BgDeploymentContainer(aFile);
    }

    private String stacktraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        pw.close();
        return sw.toString();
    }
    
}
