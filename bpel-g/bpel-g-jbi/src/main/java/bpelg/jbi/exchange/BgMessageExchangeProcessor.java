package bpelg.jbi.exchange;

import javax.jbi.messaging.ExchangeStatus;
import javax.jbi.messaging.Fault;
import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.messaging.NormalizedMessage;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.AeMessageContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import bpelg.jbi.BgBpelService;
import bpelg.jbi.BgContext;
import bpelg.jbi.BgMessageExchangePattern;

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
                Document data = getData(aMex);
                AeMessageContext context = new AeMessageContext();
                context.setProcessName(bpelService.getProcessName());
                context.setPartnerLink(bpelService.getPartnerLinkDefKey().getPartnerLinkName());
                
                // no durable replies for the moment
                IAeWebServiceResponse response = AeEngineFactory.getEngine().queueReceiveData(context, data);
                if (isTwoWay(aMex)) {
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
                    BgContext.getInstance().getComponentContext().getDeliveryChannel().send(aMex);
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
                } else if (aMex.getStatus() == ExchangeStatus.ACTIVE)
                    aMex.setStatus(ExchangeStatus.DONE);
                
                BgContext.getInstance().getComponentContext().getDeliveryChannel().send(aMex);
            }
        } else {
            // unexpected role, fault
        }
    }
    
    protected Document getData(MessageExchange aMex) throws Exception {
        Source source = null;
        if (isOneWay(aMex)) {
            InOnly inOnly = (InOnly) aMex;
            source = inOnly.getInMessage().getContent();
        } else {
            InOut inOut = (InOut) aMex;
            source = inOut.getInMessage().getContent();
        }
        return toDocument(source);
    }

    protected boolean isOneWay(MessageExchange aMex) {
        return aMex.getPattern().equals(BgMessageExchangePattern.IN_ONLY);
    }
    
    protected boolean isTwoWay(MessageExchange aMex) {
        return aMex.getPattern().equals(BgMessageExchangePattern.IN_OUT);
    }

    protected Document toDocument(Source aSource) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMResult result = new DOMResult();
        t.transform(aSource, result);
        return (Document) result.getNode();
    }

}
