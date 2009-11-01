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
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.AeMessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import bpelg.jbi.BgBpelService;
import bpelg.jbi.BgContext;
import bpelg.jbi.util.BgJbiUtil;

public class BgMessageExchangeProcessor implements IBgMessageExchangeProcessor {

    @Override
    public void onJbiMessageExchange(MessageExchange aMex) throws MessagingException {
        if (aMex.getRole().equals(javax.jbi.messaging.MessageExchange.Role.CONSUMER)) {
            // FIXME call queueInvokeData / queueInvokeFault
        } else if (aMex.getRole().equals(javax.jbi.messaging.MessageExchange.Role.PROVIDER)) {
            
            if (aMex.getStatus() != ExchangeStatus.ACTIVE) {
                // We can forget about the exchange.
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
                
                // no durable replies for the moment
                IAeWebServiceResponse response = AeEngineFactory.getEngine().queueReceiveData(context, data);
                if (BgJbiUtil.isTwoWay(aMex)) {
                    InOut exchange = (InOut) aMex;
                    DOMSource responseData = new DOMSource((Node) response.getMessageData().getMessageData().values().iterator().next());
                    if (response.isFaultResponse()) {
                        Fault fault = exchange.createFault();
                        fault.setContent(responseData);
                    } else {
                        NormalizedMessage responseMessage = exchange.createMessage();
                        responseMessage.setContent(responseData);
                        exchange.setOutMessage(responseMessage);
                    }
                } else {
                    // nothing to do with one-way
                }
            } catch (Exception e) {
                error = e;
            }
            finally {
                if (error != null) {
                    aMex.setError(error);
                    aMex.setStatus(ExchangeStatus.ERROR);
                }                
                BgContext.getInstance().getComponentContext().getDeliveryChannel().send(aMex);
            }
        } else {
            // unexpected role, fault
        }
    }
    

}
