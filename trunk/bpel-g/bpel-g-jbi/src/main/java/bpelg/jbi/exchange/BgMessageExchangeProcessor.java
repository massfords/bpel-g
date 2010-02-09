package bpelg.jbi.exchange;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.transform.dom.DOMSource;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeXmlUtil;
import org.activebpel.wsio.IAeWebServiceMessageData;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.AeMessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import bpelg.jbi.BgBpelService;
import bpelg.jbi.BgContext;
import bpelg.jbi.util.BgJbiUtil;

public class BgMessageExchangeProcessor implements IBgMessageExchangeProcessor {
    
    private static final Log sLog = LogFactory.getLog(BgMessageExchangeProcessor.class);

    @Override
    public void onJbiMessageExchange(MessageExchange aMex) throws MessagingException {
        if (aMex.getRole().equals(javax.jbi.messaging.MessageExchange.Role.CONSUMER)) {
            // FIXME call queueInvokeData / queueInvokeFault
            sLog.debug("onJbiMessageExchange - consumer role not implemented due to synchronous style invokes");
        } else if (aMex.getRole().equals(javax.jbi.messaging.MessageExchange.Role.PROVIDER)) {
            
            if (aMex.getStatus() != ExchangeStatus.ACTIVE) {
                // We can forget about the exchange.
                sLog.debug("onJbiMessageExchange - provider role not active, ignore");
                return;
            }

            Exception error = null;
            
            try {
                ServiceEndpoint serviceEndpoint = aMex.getEndpoint();
                BgBpelService bpelService = BgContext.getInstance().getBpelService(serviceEndpoint);
                Document data = BgJbiUtil.getData(aMex, "in");
                AeMessageContext context = new AeMessageContext();
                context.setProcessName(bpelService.getProcessName());
                context.setPartnerLink(bpelService.getPartnerLinkDefKey().getPartnerLinkName());
                
                if (sLog.isDebugEnabled())
                    sLog.debug("onJbiMessageExchange - making synchronous call into bpel service: " + bpelService);

                // no durable replies for the moment
                IAeWebServiceResponse response = AeEngineFactory.getEngine().queueReceiveData(context, data);
                if (BgJbiUtil.isTwoWay(aMex)) {
                    InOut exchange = (InOut) aMex;
                    IAeWebServiceMessageData messageData = response.getMessageData();
                    DOMSource responseData = messageData != null ? new DOMSource((Node) messageData.getMessageData().values().iterator().next()) : null;
                    if (response.isFaultResponse()) {
                        sLog.debug("onJbiMessageExchange - received fault response");
                        if (responseData == null) {
                            Document faultDoc = AeXmlUtil.newDocument();
                            Element root = AeXmlUtil.addElementNS(faultDoc, "urn:bpel-g:faults", "bpelFault");
                            AeXmlUtil.addElementNSQName(root, root.getNamespaceURI(), "bg", "errorCode", response.getErrorCode(), false);
                            AeXmlUtil.addElementNS(root, root.getNamespaceURI(), "errorString", response.getErrorString());
                            AeXmlUtil.addElementNS(root, root.getNamespaceURI(), "errorDetail", response.getErrorDetail());
                            responseData = new DOMSource(root);
                        }
                        Fault fault = exchange.createFault();
                        fault.setContent(responseData);
                        exchange.setFault(fault);
                    } else {
                        sLog.debug("onJbiMessageExchange - received standard response");
                        NormalizedMessage responseMessage = exchange.createMessage();
                        responseMessage.setContent(responseData);
                        exchange.setOutMessage(responseMessage);
                    }
                } else {
                    // nothing to do with one-way
                    sLog.debug("onJbiMessageExchange - one way dispatch completed");
                    aMex.setStatus(ExchangeStatus.DONE);
                }
            } catch (Exception e) {
                error = e;
            }
            finally {
                if (error != null) {
                    aMex.setError(error);
                    aMex.setStatus(ExchangeStatus.ERROR);
                }                
                sLog.trace("onJbiMessageExchange - sending exchange back on delivery channel");
                BgContext.getInstance().getComponentContext().getDeliveryChannel().send(aMex);
            }
        } else {
            // unexpected role, fault
            sLog.error("onJbiMessageExchange - unexpected role:" + aMex.getRole());
        }
    }
}
