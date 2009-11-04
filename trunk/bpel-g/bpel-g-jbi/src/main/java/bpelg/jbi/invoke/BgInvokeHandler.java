package bpelg.jbi.invoke;

import javax.jbi.component.ComponentContext;
import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessageExchangeFactory;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.IAeEndpointReference;
import org.activebpel.rt.bpel.impl.queue.AeInvoke;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.AeBPELExtendedWSDLDef;
import org.activebpel.rt.wsdl.def.AeFaultMatcher;
import org.activebpel.wsio.AeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.invoke.AeInvokeResponse;
import org.activebpel.wsio.invoke.IAeInvoke;
import org.activebpel.wsio.invoke.IAeInvokeHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bpelg.jbi.BgContext;
import bpelg.jbi.BgMessageExchangePattern;
import bpelg.jbi.util.BgJbiUtil;

/**
 * The default JBI invoke handler. Converts the payload for a bpel:invoke into a normalized message and delivers that message to the intended service.
 * 
 * @author markford
 */
public class BgInvokeHandler implements IAeInvokeHandler {
    
    private static final Log sLog = LogFactory.getLog(BgInvokeHandler.class);
    
    private IAeInvoke mInvokeContext;
    private AeInvokeResponse mResponse = new AeInvokeResponse();

    @Override
    public IAeWebServiceResponse handleInvoke(IAeInvoke aInvoke, String aQueryData) {
        
        mInvokeContext = aInvoke;

        IAeEndpointReference epr = ((AeInvoke)aInvoke).getPartnerReference();
        QName serviceName = epr.getServiceName();
        String endpoint = epr.getServicePort();
        
        sLog.debug("invoking service/endpoint:" + serviceName + " " + endpoint);

        ComponentContext componentContext = BgContext.getInstance().getComponentContext();
        ServiceEndpoint serviceEndpoint = componentContext.getEndpoint(serviceName, endpoint);

        MessageExchange jbiMex;
        try {
            MessageExchangeFactory mexf = componentContext.getDeliveryChannel().createExchangeFactory(serviceEndpoint);

            QName opname = new QName(serviceName.getNamespaceURI(), aInvoke.getOperation());

            jbiMex = mexf.createExchange(aInvoke.isOneWay() ? BgMessageExchangePattern.IN_ONLY : BgMessageExchangePattern.IN_OUT);
            jbiMex.setEndpoint(serviceEndpoint);
            jbiMex.setService(serviceName);
            jbiMex.setOperation(opname);
            
            NormalizedMessage nmsg = jbiMex.createMessage();
            Document doc = (Document) aInvoke.getInputMessageData().getMessageData().values().iterator().next();
            nmsg.setContent(new DOMSource(doc));
            jbiMex.setMessage(nmsg, "in");
            
            sLog.debug("Sending exchange on channel synchronously");
            componentContext.getDeliveryChannel().sendSync(jbiMex);
            onJbiMessageExchange(jbiMex);

        } catch (MessagingException e) {
            sLog.error(e);
        }
        
        return mResponse;
    }
    
    private void onJbiMessageExchange(MessageExchange jbiMex) throws MessagingException {
        
        if (!jbiMex.getPattern().equals(BgMessageExchangePattern.IN_ONLY) &&
            !jbiMex.getPattern().equals(BgMessageExchangePattern.IN_OUT)) {
            sLog.error("JBI MessageExchange " + jbiMex.getExchangeId() + " is of an unsupported pattern " + jbiMex.getPattern());
            return;
        }
        if (jbiMex.getStatus() == ExchangeStatus.ACTIVE) {
            if (jbiMex.getPattern().equals(BgMessageExchangePattern.IN_OUT)) {
                sLog.debug("Extracting standard response from synchronous invoke");
                try {
                    PortType portType = getPortType();
                    String opName = mInvokeContext.getOperation();
                    Operation operation = portType.getOperation(opName, null, null);
                    Message message = operation.getOutput().getMessage();
                    String partName = (String) message.getParts().keySet().iterator().next();

                    Document data = BgJbiUtil.getData(jbiMex, "out");
                    AeWebServiceMessageData msgData = new AeWebServiceMessageData(message.getQName());
                    msgData.setData(partName, data);
                    mResponse.setMessageData(msgData);
                    jbiMex.setStatus(ExchangeStatus.DONE);
                } catch (Exception e) {
                    sLog.error("Exception handling invoke response", e);
                    jbiMex.setStatus(ExchangeStatus.ERROR);
                }
            }
            sLog.debug("Signalling exchange as complete");
            BgContext.getInstance().getComponentContext().getDeliveryChannel().send(jbiMex);
        } else if (jbiMex.getStatus() == ExchangeStatus.ERROR) {
            sLog.debug("Extracting error from exchange");
            try {
                PortType portType = getPortType();
                String opName = mInvokeContext.getOperation();
                Operation operation = portType.getOperation(opName, null, null);
                
                Document data = BgJbiUtil.getData(jbiMex, "out");
                setFaultOnResponse(mInvokeContext.getPortType(), operation, data.getDocumentElement());
                jbiMex.setStatus(ExchangeStatus.DONE);
            } catch (Exception e) {
                sLog.error("Exception handling invoke response", e);
                jbiMex.setStatus(ExchangeStatus.ERROR);
            }
        } else if (jbiMex.getStatus() == ExchangeStatus.DONE) {
            // no op
            sLog.debug("exchange marked as done, ignoring");
        } else {
            sLog.error("Unexpected status " + jbiMex.getStatus() + " for JBI message exchange: "
                    + jbiMex.getExchangeId());
        }
    }

    /**
     * Extracts the wsdl PortType for the invoke we're supporting
     * @throws AeBusinessProcessException
     */
    private PortType getPortType() throws AeBusinessProcessException {
        IAeContextWSDLProvider wsdlProvider = AeEngineFactory.getDeploymentProvider().findDeploymentPlan(
                -1, mInvokeContext.getProcessName());
        AeBPELExtendedWSDLDef def = AeWSDLDefHelper.getWSDLDefinitionForPortType(
                wsdlProvider, mInvokeContext.getPortType());
        PortType portType = def.getPortType(mInvokeContext.getPortType());
        return portType;
    }
    
    /**
     * Maps a wsdl fault to the invoke response 
     * 
     * @param aPortType
     * @param aOper
     * @param firstDetailElement
     */
    private void setFaultOnResponse(QName aPortType, Operation aOper, Element firstDetailElement) {
        AeFaultMatcher faultMatcher = new AeFaultMatcher(aPortType, aOper, null, firstDetailElement);
        Fault wsdlFault = faultMatcher.getWsdlFault();
        QName faultName = faultMatcher.getFaultName();
        if (faultName == null) {
            faultName = new QName("urn:bpel-g", "failure");
        }

        AeWebServiceMessageData data = null;
        if (wsdlFault != null) {
            // if we have a wsdl fault, then the faultName is the QName of the
            // wsdl fault
            // and the data is extracted from the firstDetailElement
            data = new AeWebServiceMessageData(wsdlFault.getMessage().getQName());
            String partName = (String) wsdlFault.getMessage().getParts().keySet().iterator().next();
            Document doc = AeXmlUtil.newDocument();
            Element details = (Element) doc.importNode(firstDetailElement, true);
            doc.appendChild(details);
            data.setData(partName, doc);
        }

        mResponse.setFaultData(faultName, data);
    }

}
