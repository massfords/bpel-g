package org.activebpel.rt.bpel.server.security;

import javax.security.auth.callback.CallbackHandler;

public class AeDefaultCallbackHandlerFactory implements
		IAeCallbackHandlerFactory {

	@Override
	public CallbackHandler create(String aUsername, String aPassword) {
		AeCallbackHandler cb = new AeCallbackHandler();
		cb.setUser(aUsername);
		cb.setPassword(aPassword);
		return cb;
	}

}
