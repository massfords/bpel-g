package bpelg.jbi.su;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jbi.component.ServiceUnitManager;
import javax.jbi.management.DeploymentException;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.list.AeProcessFilter;
import org.activebpel.rt.bpel.impl.list.AeProcessInstanceDetail;
import org.activebpel.rt.bpel.impl.list.AeProcessListResult;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.admin.IAeEngineAdministration;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandler;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandlerFactory;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.validate.AeDeploymentFileValidator;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.AeCommonsLoggingImpl;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bpelg.packaging.ode.BgDeploymentContainer;

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
    
    private static final Log sLog = LogFactory.getLog(BgServiceUnitManager.class);
    
    /** Using this to track service units which have been deployed to the bpel database */
    private static final Set<String> sDeployed = new HashSet();

    /* 
     * Called when a new service unit has been installed - either through the hotdeploy directory or 
     * via the JMX interface.
     * 
     * @see javax.jbi.component.ServiceUnitManager#deploy(java.lang.String, java.lang.String)
     */
    @Override
    public String deploy(String aServiceUnitName, String aServiceUnitRootPath)
            throws DeploymentException {
        String message = null;
        try {
            boolean success = deployToBPELDB(aServiceUnitRootPath);
            
            if (success) {
                message = "SUCCESS";
            } else { 
                message = "ERROR";
            }
        } catch (Throwable t) {
            sLog.error(
                AeMessages.format("AeDeploymentFileHandler.ERROR_2", new Object[] { aServiceUnitName }), t); //$NON-NLS-1$
            message = stacktraceToString(t);
        }
        return MessageFormat.format(DEPLOY_XML, "deploy", message);
    }
    
    /* 
     * Called when the service unit is undeployed. This will also terminate any running processes with this
     * definition and will also purge them from the database.
     * 
     * @see javax.jbi.component.ServiceUnitManager#undeploy(java.lang.String, java.lang.String)
     */
    @Override
    public String undeploy(String aServiceUnitName, String aServiceUnitRootPath)
            throws DeploymentException {
        
        String message = null;
        
        try {
    
            IAeDeploymentContainer deployContainer = createDeploymentContainer(new File(aServiceUnitRootPath));
            IAeDeploymentHandler handler = AeEngineFactory.getBean(IAeDeploymentHandlerFactory.class).newInstance(new AeCommonsLoggingImpl(sLog));
            handler.undeploy(deployContainer);
            terminateUndeployedInstances(deployContainer);
            purgeUndeployedInstances(deployContainer);
            
            message = "SUCCESS";
        } catch(Throwable t) {
            message = stacktraceToString(t);
        }
        
        return MessageFormat.format(DEPLOY_XML, "undeploy", message);
    }

    private void terminateUndeployedInstances(IAeDeploymentContainer deployContainer) {
        // delete any instances we've left behind
        IAeEngineAdministration admin = AeEngineFactory.getEngineAdministration();
        for (Iterator iter = deployContainer.getPddResources().iterator(); iter.hasNext();) {
            try {
            String pddName = (String) iter.next();
            IAeDeploymentSource source = deployContainer.getDeploymentSource(pddName);
            AeProcessFilter filter = new AeProcessFilter();
            filter.setProcessName(source.getProcessName());
            AeProcessListResult result = admin.getProcessList(filter);
            for (AeProcessInstanceDetail detail : result.getRowDetails()) {
                if (detail.getEnded() != null) {
                    try {
                        AeEngineFactory.getEngine().terminateProcess(detail.getProcessId());
                    } catch (AeBusinessProcessException e) {
                        sLog.error("Error terminating process being undeployed", e);
                    }
                }
            }
            } catch (AeException e) {
                sLog.error("Error getting list of processes to terminate during undeployment", e);
            }
        }
    }

    private void purgeUndeployedInstances(IAeDeploymentContainer deployContainer) {
        // delete any instances we've left behind
        IAeEngineAdministration admin = AeEngineFactory.getEngineAdministration();
        for (Iterator iter = deployContainer.getPddResources().iterator(); iter.hasNext();) {
            String pddName = (String) iter.next();
            try {
                IAeDeploymentSource source = deployContainer.getDeploymentSource(pddName);
                QName processName = source.getProcessName();
                AeProcessFilter filter = new AeProcessFilter();
                filter.setProcessName(processName);
                admin.removeProcesses(filter);
                AeEngineFactory.getBean(IAeDeploymentProvider.class).removeDeploymentPlan(processName);
            } catch (AeException e) {
                sLog.error("Error purging plan for undeployed processes", e); //$NON-NLS-1$
            }
        }
    }

    /* 
     * Part of the standard lifecycle for the service unit. This method is called after the service unit
     * is deployed and also upon startup for service units that have been previously deployed. In the case
     * of the former, nothing happens since we've already done the deployment to the bpel db. In the case
     * of the latter, we deploy to the bpel db.
     * 
     * @see javax.jbi.component.ServiceUnitManager#init(java.lang.String, java.lang.String)
     */
    @Override
    public void init(String aServiceUnitName, String aServiceUnitRootPath)
            throws DeploymentException {
        if (sDeployed.contains(aServiceUnitRootPath)) {
            return;
        }
        sLog.debug("Initializing previously deployed service unit:" + aServiceUnitName + "/" + aServiceUnitRootPath);
        try {
            deployToBPELDB(aServiceUnitRootPath);
        } catch (Exception e) {
            throw new DeploymentException(e);
        }
    }

    @Override
    public void shutDown(String aServiceUnitName) throws DeploymentException {
        sLog.debug("shutdown (no-op):" + aServiceUnitName);
    }

    @Override
    public void start(String aServiceUnitName) throws DeploymentException {
        sLog.debug("start (no-op):" + aServiceUnitName);
    }

    @Override
    public void stop(String aServiceUnitName) throws DeploymentException {
        sLog.debug("stop (no-op):" + aServiceUnitName);
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
    
    protected boolean deployToBPELDB(String aServiceUnitRootPath) throws Exception {
        IAeDeploymentLogger logger = new BgDeploymentLogger();

        IAeDeploymentContainer deployContainer = createDeploymentContainer(new File(aServiceUnitRootPath));

        AeDeploymentFileValidator.validateFileType(deployContainer, true, logger);

        // If the file type is valid, then use the deployment handler to
        // deploy the BPR.
        if (!logger.hasErrors()) {
            IAeDeploymentHandler handler = AeEngineFactory.getBean(IAeDeploymentHandlerFactory.class).newInstance(new AeCommonsLoggingImpl(sLog));
            handler.deploy(deployContainer, logger);
            sDeployed.add(aServiceUnitRootPath);
            return true;
        } else {
            sLog.info(AeMessages.format("AeDeploymentFileHandler.1", //$NON-NLS-1$
                    new Object[] { deployContainer.getShortName() }));
            return false;
        }
    }
}
