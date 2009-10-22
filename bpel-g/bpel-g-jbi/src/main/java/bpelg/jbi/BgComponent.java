package bpelg.jbi;

import javax.jbi.component.Component;
import javax.jbi.component.ComponentLifeCycle;
import javax.jbi.component.ServiceUnitManager;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.servicedesc.ServiceEndpoint;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

public class BgComponent implements Component {

	@Override
	public ComponentLifeCycle getLifeCycle() {
		// FIXME Need a lifecycle object that starts/stops the engine
		return null;
	}

	@Override
	public Document getServiceDescription(ServiceEndpoint aEndpoint) {
		// FIXME - return WSDL doc for the given service endpoint
		// - use IAeDeploymentProvider to get IAeProcessDeployment
		// - walk the context WSDL looking for the WSDL def that provides the port type for this endpoint
		return null;
	}

	@Override
	public ServiceUnitManager getServiceUnitManager() {
		// FIXME Need a class that can adapt su packaging to what's needed by IAeDeploymentHandler
		// Not sure there's a comparable notion of start or stop for AE deployments. The process is
		// active as soon as it is deployed
		return null;
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
