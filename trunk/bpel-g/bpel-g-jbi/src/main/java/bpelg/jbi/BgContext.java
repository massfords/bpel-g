package bpelg.jbi;

import javax.jbi.component.ComponentContext;

/**
 * Singleton context for all things JBI and bpel-g
 * 
 * @author markford
 */
public class BgContext {
	private static final BgContext sInstance = new BgContext();
//	private 

	private ComponentContext mComponentContext;
	
	private BgContext() {
		
	}
	
	public static synchronized BgContext getInstance() {
		return sInstance;
	}
	
	protected ComponentContext getComponentContext() {
		return mComponentContext;
	}

	protected void setComponentContext(ComponentContext aComponentContext) {
		mComponentContext = aComponentContext;
	}
}
