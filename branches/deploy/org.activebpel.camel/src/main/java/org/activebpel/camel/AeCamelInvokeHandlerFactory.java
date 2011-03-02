package org.activebpel.camel;

import javax.xml.transform.dom.DOMSource;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.impl.queue.AeInvoke;
import org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.invoke.AeInvokeResponse;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultCamelContext;
import org.w3c.dom.Node;

public class AeCamelInvokeHandlerFactory implements IAeInvokeHandlerFactory, IAeInvokeHandler {

	private DefaultCamelContext mContext = new DefaultCamelContext();
	
	@Override
	public IAeInvokeHandler createInvokeHandler(IAeInvoke aInvoke)
			throws AeBusinessProcessException {
		return this;
	}

	@Override
	public String getQueryData(IAeInvoke aInvoke) {
		return null;
	}

	@Override
	public IAeWebServiceResponse handleInvoke(IAeInvoke aInvoke,
			String aQueryData) {
		AeInvokeResponse response = new AeInvokeResponse();
		
		AeInvoke invoke = (AeInvoke) aInvoke;
		String address = invoke.getPartnerReference().getAddress();
		Endpoint ep = mContext.getEndpoint(address);
		try {
			Producer p = ep.createProducer();
			Exchange ex = p.createExchange();
			ex.getIn().setBody(new DOMSource((Node) invoke.getInputMessageData().getMessageData().values().iterator().next()));
			p.process(ex);
		} catch (Exception e) {
			// FIXME need better strategy here
			throw new RuntimeException(e);
		}
		return response;
	}
}
