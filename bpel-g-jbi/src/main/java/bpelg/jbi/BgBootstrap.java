package bpelg.jbi;

import javax.jbi.JBIException;
import javax.jbi.component.Bootstrap;
import javax.jbi.component.InstallationContext;
import javax.management.ObjectName;

public class BgBootstrap implements Bootstrap {

    @Override
    public void cleanUp() throws JBIException {
    }

    @Override
    public ObjectName getExtensionMBeanName() {
        return null;
    }

    @Override
    public void init(InstallationContext aInstallContext) throws JBIException {
    }

    @Override
    public void onInstall() throws JBIException {
    }

    @Override
    public void onUninstall() throws JBIException {
    }

}
