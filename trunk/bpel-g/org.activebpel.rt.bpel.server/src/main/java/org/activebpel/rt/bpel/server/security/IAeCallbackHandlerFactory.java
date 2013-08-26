package org.activebpel.rt.bpel.server.security;

import javax.security.auth.callback.CallbackHandler;

public interface IAeCallbackHandlerFactory {
    public CallbackHandler create(String aUsername, String aPassword);
}
