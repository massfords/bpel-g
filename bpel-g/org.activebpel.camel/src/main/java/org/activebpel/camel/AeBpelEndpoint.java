package org.activebpel.camel;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

public class AeBpelEndpoint extends DefaultEndpoint {
	
	public AeBpelEndpoint(String aUri, AeBpelComponent aComponent) {
		super(aUri, aComponent);
	}

	@Override
	public Consumer createConsumer(Processor aArg0) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Producer createProducer() throws Exception {
		return new AeBpelProducer(this);
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
