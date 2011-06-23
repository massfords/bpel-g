package org.activebpel.camel;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

public class AeBpelEndpoint extends DefaultEndpoint {
	
	private static final Set<String> sCommands = new HashSet<String>();
	static {
		sCommands.add("invoke");
		sCommands.add("undeploy");
	}
	// default to invoke
	private String mCommand = "invoke";
	
	public AeBpelEndpoint(String aUri, AeBpelComponent aComponent) {
		super(aUri, aComponent);
	}

	@Override
	public Consumer createConsumer(Processor aArg0) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Producer createProducer() throws Exception {
		if (mCommand.equals("invoke")) {
			return new AeBpelInvokeProducer(this);
		} else if (mCommand.equals("undeploy")) {
			return new AeUndeployProducer(this);
		} else {
			throw new IllegalArgumentException("Unknown command type: " + mCommand );
		}
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public String getCommand() {
		return mCommand;
	}

	public void setCommand(String aCommand) {
		if (!sCommands.contains(aCommand)) {
			throw new IllegalArgumentException("Unknown command type: " + mCommand );
		}
		mCommand = aCommand;
	}
}
