package bpelg.jbi.exchange;

import javax.jbi.messaging.InOnly;
import javax.jbi.messaging.InOut;
import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;
import javax.jbi.servicedesc.ServiceEndpoint;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.AeMessageContext;
import org.w3c.dom.Document;

import bpelg.jbi.BgBpelService;
import bpelg.jbi.BgContext;
import bpelg.jbi.BgMessageExchangePattern;

public class BgMessageExchangeProcessor implements IBgMessageExchangeProcessor {

    @Override
    public void onJbiMessageExchange(MessageExchange aMex) throws MessagingException {
        if (aMex.getRole().equals(javax.jbi.messaging.MessageExchange.Role.CONSUMER)) {
            // FIXME call queueInvokeData / queueInvokeFault
        } else if (aMex.getRole().equals(javax.jbi.messaging.MessageExchange.Role.PROVIDER)) {
            
            // FIXME call queueReceiveData
            // use mex.getEndpoint() as a key to get the processName + partnerLink we're targeting
            
            try {
                ServiceEndpoint serviceEndpoint = aMex.getEndpoint();
                BgBpelService bpelService = BgContext.getInstance().getBpelService(serviceEndpoint);
                Document data = getData(aMex);
                AeMessageContext context = new AeMessageContext();
                context.setProcessName(bpelService.getProcessName());
                context.setPartnerLink(bpelService.getPartnerLinkDefKey().getPartnerLinkName());
                IAeWebServiceResponse response = AeEngineFactory.getEngine().queueReceiveData(context, data);
                
                // FIXME handle reply with fault or data
                // FIXME mark exchange as done.
            } catch (Exception e) {
                // FIXME handle exception
            }
        } else {
            // unexpected role, fault
        }
    }
    
    protected Document getData(MessageExchange aMex) throws Exception {
        Source source = null;
        if (aMex.getPattern().equals(BgMessageExchangePattern.IN_ONLY)) {
            InOnly inOnly = (InOnly) aMex;
            source = inOnly.getInMessage().getContent();
        } else {
            InOut inOut = (InOut) aMex;
            source = inOut.getInMessage().getContent();
        }
        return toDocument(source);
    }
    
    protected Document toDocument(Source aSource) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMResult result = new DOMResult();
        t.transform(aSource, result);
        return (Document) result.getNode();
    }

}
