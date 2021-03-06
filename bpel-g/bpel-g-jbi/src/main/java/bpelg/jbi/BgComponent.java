package bpelg.jbi;

import javax.jbi.component.Component;
import javax.jbi.component.ComponentLifeCycle;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.servicedesc.ServiceEndpoint;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

import bpelg.jbi.su.BgServiceUnitManager;

/**
 * Core JBI component
 * 
 * @author markford
 */
public class BgComponent implements Component {

    /** provides hooks for the lifecycle methods */
    private ComponentLifeCycle mLifecycle;
    /** handles deployments of service units */
    private BgServiceUnitManager mServiceUnitManager;
    
    public BgComponent() {
        mLifecycle = new BgComponentLifeCycle();
        mServiceUnitManager = new BgServiceUnitManager();
    }
    
	@Override
	public ComponentLifeCycle getLifeCycle() {
	    return mLifecycle;
	}

	@Override
	public Document getServiceDescription(ServiceEndpoint aEndpoint) {
	    return BgContext.getInstance().getServiceDescription(aEndpoint);
	}

	@Override
	public BgServiceUnitManager getServiceUnitManager() {
		return mServiceUnitManager;
	}

	@Override
	public boolean isExchangeWithConsumerOkay(ServiceEndpoint aEndpoint,
			MessageExchange aExchange) {
		return true;
	}

	@Override
	public boolean isExchangeWithProviderOkay(ServiceEndpoint aEndpoint,
			MessageExchange aExchange) {
		return true;
	}

	@Override
	public ServiceEndpoint resolveEndpointReference(DocumentFragment aEpr) {
		return null;
	}
}
