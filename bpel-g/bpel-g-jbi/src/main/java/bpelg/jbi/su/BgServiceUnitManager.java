package bpelg.jbi.su;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;

import javax.jbi.component.ServiceUnitManager;
import javax.jbi.management.DeploymentException;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.deploy.AeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentHandler;
import org.activebpel.rt.bpel.server.deploy.bpr.AeBpr;
import org.activebpel.rt.bpel.server.deploy.bpr.AeUnpackedBprContext;
import org.activebpel.rt.bpel.server.deploy.bpr.IAeBpr;
import org.activebpel.rt.bpel.server.deploy.validate.AeDeploymentFileValidator;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.logging.AeCommonsLoggingImpl;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;
import org.activebpel.rt.bpel.server.logging.IAeLogWrapper;

public class BgServiceUnitManager implements ServiceUnitManager {

    private static final String DEPLOY_XML = 
        "<component-task-result xmlns=''http://java.sun.com/xml/ns/jbi/management-message''> " + 
    		"           <component-name>bpel-g</component-name>" + 
    		"           <component-task-result-details>" + 
    		"              <task-result-details>" + 
    		"                   <task-id>deploy</task-id> " + 
    		"                   <task-result>{0}</task-result>" + 
    		"              </task-result-details> " + 
    		"           </component-task-result-details>" + 
    		"          </component-task-result>"; 
    
    private IAeLogWrapper mLog = new AeCommonsLoggingImpl(BgServiceUnitManager.class);

    @Override
    public String deploy(String aServiceUnitName, String aServiceUnitRootPath)
            throws DeploymentException {
        String message = null;
        try {
            URL url = new File(aServiceUnitRootPath).toURI().toURL();
            IAeDeploymentLogger logger = new BgDeploymentLogger();

            IAeDeploymentContainer deployContainer = createDeploymentContainer(url);

            // If the logger is null, used the factory to create a new one.

            AeDeploymentFileValidator.validateFileType(deployContainer, true, logger);

            // If the file type is valid, then use the deployment handler to
            // deploy the BPR.
            if (!logger.hasErrors()) {
                IAeDeploymentHandler handler = AeEngineFactory.getDeploymentHandlerFactory().newInstance(mLog);
                handler.deploy(deployContainer, logger);
            } else { 
                mLog.logInfo(MessageFormat.format(AeMessages.getString("AeDeploymentFileHandler.1"), //$NON-NLS-1$
                        new Object[] { deployContainer.getShortName() }));
                message = "SUCCESS";
            }
        } catch (Throwable t) {
            mLog.logError(
                    MessageFormat.format(
                            AeMessages.getString("AeDeploymentFileHandler.ERROR_2"), new Object[] { aServiceUnitName }), t); //$NON-NLS-1$
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.flush();
            pw.close();
            message = sw.toString();
        }
        return MessageFormat.format(DEPLOY_XML, message);
    }
    
    public IAeDeploymentContainer createDeploymentContainer( URL aFileURL )
    throws AeException
    {
       ClassLoader current = Thread.currentThread().getContextClassLoader();
       ClassLoader bprResourceClassLoader = URLClassLoader.newInstance( new URL[]{ aFileURL }, current ); 

       String urlString = aFileURL.toString();
       String shortName = urlString.substring( urlString.lastIndexOf('/')+1 );
       AeUnpackedBprContext context = new AeUnpackedBprContext(aFileURL, bprResourceClassLoader, shortName );
       IAeBpr file = AeBpr.createUnpackedBpr( context );
       return new AeDeploymentContainer( context, file, aFileURL );
    }
    

    @Override
    public void init(String aServiceUnitName, String aServiceUnitRootPath)
            throws DeploymentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutDown(String aServiceUnitName) throws DeploymentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void start(String aServiceUnitName) throws DeploymentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop(String aServiceUnitName) throws DeploymentException {
        // TODO Auto-generated method stub

    }

    @Override
    public String undeploy(String aServiceUnitName, String aServiceUnitRootPath)
            throws DeploymentException {
        // TODO Auto-generated method stub
        return null;
    }
}
