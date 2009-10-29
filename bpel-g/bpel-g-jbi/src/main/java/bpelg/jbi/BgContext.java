package bpelg.jbi;

import javax.jbi.component.ComponentContext;

import bpelg.jbi.exchange.BgMessageExchangeProcessor;
import bpelg.jbi.exchange.IBgMessageExchangeProcessor;

/**
 * Singleton context for all things JBI and bpel-g
 * 
 * @author markford
 */
public class BgContext {
	private static final BgContext sInstance = new BgContext();

	private ComponentContext mComponentContext;
	private IBgMessageExchangeProcessor mMessageExchangeProcessor;
	
	private BgContext() {
	    setMessageExchangeProcessor(new BgMessageExchangeProcessor());
	}
	
	public static synchronized BgContext getInstance() {
		return sInstance;
	}
	
	public ComponentContext getComponentContext() {
		return mComponentContext;
	}

	protected void setComponentContext(ComponentContext aComponentContext) {
		mComponentContext = aComponentContext;
	}

    public IBgMessageExchangeProcessor getMessageExchangeProcessor() {
        return mMessageExchangeProcessor;
    }

    protected void setMessageExchangeProcessor(IBgMessageExchangeProcessor aMessageExchangeProcessor) {
        mMessageExchangeProcessor = aMessageExchangeProcessor;
    }
}
