package org.activebpel.camel;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import bpelg.services.deploy.AeDeployer;
import bpelg.services.deploy.types.UndeploymentRequest;

public class AeUndeployProducer extends DefaultProducer {

	public AeUndeployProducer(Endpoint aEndpoint) {
		super(aEndpoint);
	}

	@Override
	public void process(Exchange aExchange) throws Exception {
		AeDeployer deployer = AeEngineFactory.getBean(AeDeployer.class);
		String containerId = aExchange.getIn().getHeader("containerId", String.class);
		deployer.undeploy(new UndeploymentRequest().withDeploymentContainerId(containerId));
	}
}
