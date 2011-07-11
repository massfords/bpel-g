package org.activebpel.rt.bpel.server.deploy.scanner;

import java.util.Map;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.deploy.AeNewDeploymentInfo;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainer;
import org.activebpel.rt.bpel.server.deploy.IAeDeploymentContainerFactory;
import org.activebpel.rt.util.AeFileUtil;

public class AeExtensionBasedDeploymentContainerFactory implements IAeDeploymentContainerFactory {

    Map<String, IAeDeploymentContainerFactory> mFactoryMap;

    @Override
    public IAeDeploymentContainer createDeploymentContainer(AeNewDeploymentInfo aInfo)
            throws AeException {
        return getFactory(aInfo).createDeploymentContainer(aInfo);
    }

    @Override
    public IAeDeploymentContainer createUndeploymentContainer(AeNewDeploymentInfo aInfo)
            throws AeException {
        return getFactory(aInfo).createUndeploymentContainer(aInfo);
    }

    protected IAeDeploymentContainerFactory getFactory(AeNewDeploymentInfo aInfo)
            throws AeException {
        String name = aInfo.getURL().toString();
        String ext = AeFileUtil.getExtension(name);
        IAeDeploymentContainerFactory dc = getFactoryMap().get(ext);
        if (dc == null)
            throw new AeException("extension not supported");
        return dc;
    }

    public Map<String, IAeDeploymentContainerFactory> getFactoryMap() {
        return mFactoryMap;
    }

    public void setFactoryMap(Map<String, IAeDeploymentContainerFactory> aFactoryMap) {
        mFactoryMap = aFactoryMap;
    }
}
