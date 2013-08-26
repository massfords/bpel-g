package org.activebpel.camel;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.activebpel.wsio.IAeWebServiceResponse;
import org.activebpel.wsio.receive.AeMessageContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.w3c.dom.Document;

public class AeBpelInvokeProducer extends DefaultProducer {

    public AeBpelInvokeProducer(Endpoint aEndpoint) {
        super(aEndpoint);
    }

    @Override
    public void process(Exchange aExchange) throws Exception {
        Message in = aExchange.getIn();
        String xml = in.getBody(String.class);
        Document doc = new AeXMLParserBase().loadDocumentFromString(xml, null);
        AeMessageContext context = new AeMessageContext();
        context.setPartnerLink(in.getHeader("partnerLink", String.class));
        String ns = in.getHeader("processNamespace", String.class);
        String lp = in.getHeader("processLocalPart", String.class);
        context.setProcessName(new QName(ns, lp));
        // invoke the engine
        IAeWebServiceResponse response = AeEngineFactory.getEngine().queueReceiveData(context, doc);
        // check for a single part response
        if (response.getMessageData() != null && response.getMessageData().getMessageData().size() == 1) {
            // I don't care about multi-part messages but if they're going to be supported then I need
            // to give some thought to that here.
            Document responseDoc = (Document) response.getMessageData().getMessageData().values().iterator().next();
            // Don't bother serializing the Document. Leave it up to the processing route to decide whether
            // the payload needs to be converted somehow. No point converting it to a string here if the next
            // processor in the chain wants it as a Document.
            aExchange.getIn().setBody(responseDoc);
        }
    }
}
