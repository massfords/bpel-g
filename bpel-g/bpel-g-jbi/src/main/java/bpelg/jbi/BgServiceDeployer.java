package bpelg.jbi;

import java.util.Collection;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDefKey;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentSource;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bpelg.jbi.su.ode.BgDeploymentContainer;
import bpelg.jbi.su.ode.BgPddInfo.BgPlink;

/**
 * This class activates JBI endpoints for each of the myrole partnerlink services
 * that are exposed by a process. 
 * 
 * @author markford
 */
public class BgServiceDeployer implements IAeWebServicesDeployer {
    
    private static final Log sLog = LogFactory.getLog(BgServiceDeployer.class);

    @Override
    public void deployToWebServiceContainer(IAeDeploymentContainer aContainer, ClassLoader aLoader) throws AeException {
        
        sLog.debug("deployToWebServiceContainer " + aContainer.getBprFileName());
        
        /*
         * Need to activate an endpoint with the JBI context for each BPEL
         * that's deployed
         */
        ComponentContext context = BgContext.getInstance().getComponentContext();

        try {

            for (IAeServiceDeploymentInfo serviceInfo : aContainer.getServiceDeploymentInfo()) {

                QName processName = serviceInfo.getProcessQName();

                sLog.debug("deploying process: " + processName);

                IAeProcessDeployment deployment = AeEngineFactory.getDeploymentProvider().findCurrentDeployment(processName);
                AeProcessDef processDef = deployment.getProcessDef();

                AePartnerLinkDefKey partnerLinkDefKey = serviceInfo.getPartnerLinkDefKey();
                AePartnerLinkDef plinkDef = processDef.findPartnerLink(partnerLinkDefKey);
                
                BgDeploymentContainer container = (BgDeploymentContainer) aContainer;
                BgPlink plink = container.getPlink(processName, serviceInfo.getPartnerLinkName());
                
                QName service = plink.myService;
                String endpoint = plink.myEndpoint;
                

//                QName service = plinkDef.getMyRolePortType();
//                String endpoint = serviceInfo.getServiceName();
                // ODE has a simple deploy file where the user specifies the
                // service qname and port name for each endpoint
                // AE has a PDD with the equivalent of a port name (called
                // service) but nothing for the service qname
                // options are:
                // - use process qname for service
                // - use wsdl port type for service
                sLog.debug("activating endpoint: " + service + " " + endpoint);
                BgBpelService bpelService = new BgBpelService(processName, partnerLinkDefKey, service, endpoint, plinkDef.getMyRolePortType());
                BgContext.getInstance().addService(deployment, bpelService);

                ServiceEndpoint serviceEndpoint = context.activateEndpoint(service, endpoint);
                
                bpelService.setServiceEndpoint(serviceEndpoint);
                
            }

        } catch (Exception e) {
            throw new AeException(e);
        }
    }

    @Override
    public void undeployFromWebServiceContainer(IAeDeploymentContainer aContainer) throws AeException {
        sLog.debug("undeploying container: " + aContainer.getBprFileName());
        ComponentContext context = BgContext.getInstance().getComponentContext();
        Collection<String> pdds = aContainer.getPddResources();
        for(String pdd : pdds) {
            IAeDeploymentSource source = aContainer.getDeploymentSource(pdd);
            QName processName = source.getProcessName();
            sLog.debug("undeploying process: " + processName);
            Collection<BgBpelService> services = BgContext.getInstance().removeServicesByProcessName(processName);
            
            for(BgBpelService service : services) {
                try {
                    context.deactivateEndpoint(service.getServiceEndpoint());
                } catch (JBIException e) {
                    sLog.error("Exception during deactivation of the endpoint", e);
                }
            }
        }
    }

    @Override
    public void undeployFromWebServiceContainer(IAeServiceDeploymentInfo aService) throws AeException {
        throw new UnsupportedOperationException(
                "Single undeployments not supported. Need to undeploy the whole container");
    }

    @Override
    public void deployToWebServiceContainer(IAeServiceDeploymentInfo aService, ClassLoader aLoader) throws AeException {
        throw new UnsupportedOperationException("Single deployments not supported. Need to deploy the whole container");
    }
}
