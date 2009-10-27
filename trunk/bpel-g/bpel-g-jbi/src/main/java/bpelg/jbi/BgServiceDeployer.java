package bpelg.jbi;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeServiceDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.IAeWebServicesDeployer;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

public class BgServiceDeployer implements IAeWebServicesDeployer {

    @Override
    public void deployToWebServiceContainer(IAeDeploymentContainer aContainer, ClassLoader aLoader) throws AeException {
        /*
         * Need to activate an endpoint with the JBI context for each BPEL
         * that's deployed
         */
        ComponentContext context = BgContext.getInstance().getComponentContext();

        try {

            for (IAeServiceDeploymentInfo serviceInfo : aContainer.getServiceDeploymentInfo()) {

                QName processName = serviceInfo.getProcessQName();

                IAeProcessDeployment deployment = AeEngineFactory.getDeploymentProvider().findCurrentDeployment(processName);
                AeProcessDef processDef = deployment.getProcessDef();

                AePartnerLinkDef plinkDef = processDef.findPartnerLink(serviceInfo.getPartnerLinkDefKey());

                QName service = plinkDef.getMyRolePortType();
                String endpoint = serviceInfo.getServiceName();
                // ODE has a simple deploy file where the user specifies the
                // service qname and port name for each endpoint
                // AE has a PDD with the equivalent of a port name (called
                // service) but nothing for the service qname
                // options are:
                // - use process qname for service
                // - use wsdl port type for service
                @SuppressWarnings("unused")
                ServiceEndpoint serviceEndpoint = context.activateEndpoint(service, endpoint);
                
                // FIXME need to store this service endpoint so I can undeploy it later
            }

        } catch (JBIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void undeployFromWebServiceContainer(IAeDeploymentContainer aContainer) throws AeException {
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
