package bpelg.jbi;

import javax.jbi.JBIException;
import javax.jbi.component.ComponentContext;
import javax.jbi.component.ComponentLifeCycle;
import javax.management.ObjectName;

public class BgComponentLifeCycle implements ComponentLifeCycle {

	@Override
	public ObjectName getExtensionMBeanName() {
		// FIXME not sure I need this
		return null;
	}

	@Override
	public void init(ComponentContext aContext) throws JBIException {
		// FIXME keep context in singleton
		// FIXME init engine within context obj
	}

	@Override
	public void shutDown() throws JBIException {
		// FIXME shut down the engine
	}

	@Override
	public void start() throws JBIException {
		// FIXME start the engine
		// FIXME start polling the delivery channel looking for messages
		// Need class that reads the exchange and routes messages into the engine
		// FIXME need instance of IAeDurableReplyInfo that can route the reply back onto the bus
	}

	@Override
	public void stop() throws JBIException {
		// FIXME stop polling
		// FIXME stop engine
	}
}
