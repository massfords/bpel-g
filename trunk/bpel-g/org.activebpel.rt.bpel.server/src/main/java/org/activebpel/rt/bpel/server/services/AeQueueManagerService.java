package org.activebpel.rt.bpel.server.services;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.AeUnmatchedReceive;
import org.activebpel.rt.bpel.impl.IAeQueueManager;
import org.w3c.dom.Document;

import bpelg.services.queue.AeQueueManager;
import bpelg.services.queue.types.CorrelationProperties;
import bpelg.services.queue.types.CorrelationProperties.Property;
import bpelg.services.queue.types.GetInboundMessages;
import bpelg.services.queue.types.InboundMessage;
import bpelg.services.queue.types.InboundMessages;
import bpelg.services.queue.types.MessageRoutingDetails.Message;
import bpelg.services.queue.types.MessageRoutingDetails.Message.Part;

public class AeQueueManagerService implements AeQueueManager {
	
	private IAeQueueManager mQueueManager;

	@Override
	public InboundMessages getInboundMessages(GetInboundMessages aBody) {
		InboundMessages im = new InboundMessages();
		for(Iterator<AeUnmatchedReceive> iter = getQueueManager().getUnmatchedReceivesIterator(); iter.hasNext();) {
			AeUnmatchedReceive ur = iter.next();
			InboundMessage md = new InboundMessage()
				.withOperation(ur.getInboundReceive().getOperation())
				.withPartnerLinkName(ur.getInboundReceive().getPartnerLinkName())
				.withPortType(ur.getInboundReceive().getPortType())
				.withId(ur.getInboundReceive().getQueueId());
			CorrelationProperties cp = new CorrelationProperties();
			for(Entry<QName,Object> entry : ur.getInboundReceive().getCorrelation().entrySet()) {
				cp.withProperty(new Property().withProperty(entry.getKey())
							.withValue(entry.getValue().toString()));
			}
			if (!cp.getProperty().isEmpty())
				md.withCorrelationProperties(cp);
			Message m = new Message();
			for(Iterator<String> it=ur.getInboundReceive().getMessageData().getPartNames(); iter.hasNext();) {
				String partName = it.next();
				Part part = new Part();
				m.withPart(part.withName(partName));
				Object data = ur.getInboundReceive().getMessageData().getData(partName);
				if (data instanceof Document) {
					part.withContent(((Document) data).getDocumentElement());
				} else {
					part.withContent(String.valueOf(data));
				}
			}
			md.withMessage(m);
			
			im.withInboundMessage(md);
		}
		return im;
	}

	public IAeQueueManager getQueueManager() {
		return mQueueManager;
	}

	public void setQueueManager(IAeQueueManager aQueueManager) {
		mQueueManager = aQueueManager;
	}

}
