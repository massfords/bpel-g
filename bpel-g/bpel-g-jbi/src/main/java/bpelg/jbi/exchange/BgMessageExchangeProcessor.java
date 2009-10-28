package bpelg.jbi.exchange;

import javax.jbi.messaging.MessageExchange;
import javax.jbi.messaging.MessagingException;

public class BgMessageExchangeProcessor implements IBgMessageExchangeProcessor {

    @Override
    public void onJbiMessageExchange(MessageExchange mex) throws MessagingException {
        if (mex.getRole().equals(javax.jbi.messaging.MessageExchange.Role.CONSUMER)) {
            // FIXME call queueInvokeData / queueInvokeFault
        } else if (mex.getRole().equals(javax.jbi.messaging.MessageExchange.Role.PROVIDER)) {
            // FIXME call queueReceiveData
            // use mex.getEndpoint() as a key to get the processName + partnerLink we're targeting
        } else {
            // unexpected role, fault
        }
    }

}
