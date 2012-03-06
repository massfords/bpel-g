package org.activebpel.camel;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.impl.queue.AeInvoke;
import org.activebpel.rt.bpel.server.IAeDeploymentProvider;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.IAeInvokeHandlerFactory;
import org.activebpel.rt.bpel.server.spring.AeSpringManager;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.invoke.AeInvokeResponse;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;
import org.apache.camel.*;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.BeansException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.inject.Inject;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

public class AeCamelInvokeHandlerFactory implements IAeInvokeHandlerFactory, IAeInvokeHandler {

	private DefaultCamelContext mContext = new DefaultCamelContext();
    @Inject
	private AeSpringManager mSpringManager;
	
	public AeCamelInvokeHandlerFactory() {
	    try {
	        mContext.setLazyLoadTypeConverters(true);
            mContext.start();
        } catch (Exception e) {
            throw new RuntimeException();
        }
	}
	
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
		Endpoint ep = locate(aInvoke.getProcessName()).getEndpoint(address);
		try {
			Producer p = ep.createProducer();
			Exchange ex = p.createExchange();
			ex.getIn().setBody(new DOMSource((Node) invoke.getInputMessageData().getMessageData().values().iterator().next()));
			p.process(ex);
			
			if (!invoke.isOneWay()) {
	            AeWebServiceMessageData data = new AeWebServiceMessageData();
	            AeBPELExtendedWSDLDef def = findWsdlByPortType(aInvoke);
	            PortType pt = def.getPortType(aInvoke.getPortType());
	            Operation op = pt.getOperation(aInvoke.getOperation(), null, null);
	            data.setName(op.getOutput().getMessage().getQName());
    	        Message m = ex.getOut();
    	        String body = m.getBody(String.class);
	            AeXMLParserBase parser = new AeXMLParserBase();
	            Document payload = parser.loadDocumentFromString(body, null);
	            data.setData(op.getOutput().getMessage().getParts().keySet().iterator().next().toString(), payload);
                response.setMessageData(data);
			}
			
		} catch (Exception e) {
			// FIXME need better strategy here
			throw new RuntimeException(e);
		}
		return response;
	}
	
	protected AeBPELExtendedWSDLDef findWsdlByPortType(IAeInvoke aInvoke) throws AeBusinessProcessException {
        IAeContextWSDLProvider wsdlProvider = AeEngineFactory.getBean(
                IAeDeploymentProvider.class).findDeploymentPlan(
                aInvoke.getProcessId(), aInvoke.getProcessName());
        return AeWSDLDefHelper.getWSDLDefinitionForPortType(
                wsdlProvider, aInvoke.getPortType());
	}
	
	private CamelContext locate(QName aProcessName) {
	    try {
            return getSpringManager().getBean(aProcessName, CamelContext.class);
        } catch (BeansException e) {
            return mContext;
        }
	}

    public AeSpringManager getSpringManager() {
        return mSpringManager;
    }

    public void setSpringManager(AeSpringManager aSpringManager) {
        this.mSpringManager = aSpringManager;
    }
}
