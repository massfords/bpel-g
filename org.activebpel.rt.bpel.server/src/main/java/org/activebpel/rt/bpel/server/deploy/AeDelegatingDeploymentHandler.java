package org.activebpel.rt.bpel.server.deploy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger;

public class AeDelegatingDeploymentHandler implements IAeDeploymentHandler {
    
    private List<IAeDeploymentHandler> mHandlers;

    @Override
    public void deploy(IAeDeploymentContainer aContainer, IAeDeploymentLogger aLogger)
            throws AeException {
        for(IAeDeploymentHandler handler : getHandlers())
            handler.deploy(aContainer, aLogger);
    }

    @Override
    public void undeploy(IAeDeploymentContainer aContainer) throws AeException {
        for(IAeDeploymentHandler handler : getUndeploymentHandlers())
            handler.undeploy(aContainer);
    }

    public List<IAeDeploymentHandler> getHandlers() {
        return mHandlers;
    }

    public void setHandlers(List<IAeDeploymentHandler> aHandlers) {
        mHandlers = aHandlers;
    }
    
    public List<IAeDeploymentHandler> getUndeploymentHandlers() {
        List<IAeDeploymentHandler> l = new ArrayList<IAeDeploymentHandler>(getHandlers());
        Collections.reverse(l);
        return l;
    }

}
