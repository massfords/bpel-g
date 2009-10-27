package bpelg.jbi.su;

import org.activebpel.rt.bpel.server.logging.AeDeploymentLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BgDeploymentLogger extends AeDeploymentLog {
    
    private static final Log sLog = LogFactory.getLog(BgDeploymentLogger.class);
    
    @Override
    protected void writeMessage(String aMessage) {
        sLog.debug(aMessage);
    }

    @Override
    public void close() {
    }

}
