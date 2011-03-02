package bpelg.jbi.invoke;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;

public class BgInvokeHandlerFactory implements IAeInvokeHandlerFactory {

    @Override
    public IAeInvokeHandler createInvokeHandler(IAeInvoke aInvoke)
            throws AeBusinessProcessException {
        return new BgInvokeHandler();
    }

    @Override
    public String getQueryData(IAeInvoke aInvoke) {
        return null;
    }

}
