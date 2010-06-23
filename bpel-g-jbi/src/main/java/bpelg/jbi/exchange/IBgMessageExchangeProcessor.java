package bpelg.jbi.exchange;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;

/**
 * Handles the routing of messages between the NMR and the BPEL engine.
 * 
 * Inbound traffic: - messages targeting an inbound message activity like a
 * receive, onMessage, or onEvent - replies to a synchrous invoke
 * 
 * Outbound traffic: - message from an invoke activity which may result in a an
 * inbound message as well if the invoke was request-response - message from a
 * reply activity which corresponds to a previously received inbound message
 * 
 * @author mford
 */
public interface IBgMessageExchangeProcessor {

    // FIXME create one class for each of the scenarios above
    // target the queueReceiveData, queueInvokeResponse/Fault
    // integration with invoke handler factory
    // integration with reply receiver / durable reply factory
    
    public void onJbiMessageExchange(MessageExchange mex) throws MessagingException;

}
