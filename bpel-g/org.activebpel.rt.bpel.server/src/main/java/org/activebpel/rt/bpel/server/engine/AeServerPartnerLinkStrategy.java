package org.activebpel.rt.bpel.server.engine;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.IAePartnerLink;
import org.activebpel.rt.bpel.impl.IAeEnginePartnerLinkStrategy;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.bpel.server.IAeProcessDeployment;
import org.activebpel.rt.bpel.server.addressing.IAePartnerAddressing;
import org.activebpel.rt.bpel.server.deploy.AeProcessDeploymentFactory;
import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * Implements the partner link strategy for the server business process
 * engine.
 */
public class AeServerPartnerLinkStrategy implements IAeEnginePartnerLinkStrategy {
    /**
     * @see org.activebpel.rt.bpel.server.addressing.IAePartnerAddressing#getMyRoleEndpoint(org.activebpel.rt.bpel.server.IAeProcessDeployment, org.activebpel.rt.bpel.def.AePartnerLinkDef, javax.xml.namespace.QName, java.lang.String)
     */
    public void initPartnerLink(IAePartnerLink aPartnerLink, IAeProcessPlan aPlan) throws AeBusinessProcessException {
        IAeProcessDeployment deployment = AeProcessDeploymentFactory.getDeploymentForPlan(aPlan);
        IAeEndpointReference partnerRef = deployment.getPartnerEndpointRef(aPartnerLink.getDefinition().getLocationPath());
        if (partnerRef != null) {
            aPartnerLink.getPartnerReference().setReferenceData(partnerRef);
        }
        // get the myRole endpoint
        IAePartnerAddressing addr = AeEngineFactory.getBean(IAePartnerAddressing.class);
        IAeEndpointReference myRef = addr.getMyRoleEndpoint(deployment, aPartnerLink.getDefinition(), aPlan.getProcessDef().getQName(), aPartnerLink.getConversationId());
        if (myRef != null) {
            aPartnerLink.getMyReference().setReferenceData(myRef);
        }
    }

    /**
     * @see org.activebpel.rt.bpel.impl.IAeEnginePartnerLinkStrategy#updatePartnerLink(org.activebpel.rt.bpel.impl.AePartnerLink, org.activebpel.rt.bpel.impl.IAeProcessPlan, org.activebpel.wsio.receive.IAeMessageContext)
     */
    public void updatePartnerLink(IAePartnerLink aPartnerLink, IAeProcessPlan aProcessPlan, IAeMessageContext aMessageContext) throws AeBusinessProcessException {
        IAeProcessDeployment dd = AeProcessDeploymentFactory.getDeploymentForPlan(aProcessPlan);
        dd.updatePartnerLink(aPartnerLink, aMessageContext);
    }
}