package org.activebpel.rt.bpel.server.spring;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.impl.AeManagerAdapter;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Manager that keeps the references to Spring ApplicationContexts that have been deployed as part
 * of a BPR or ODE packaging. 
 * 
 * @author mford
 */
public class AeSpringManager extends AeManagerAdapter {

    /** map of contexts */
    private Map<String,GenericApplicationContext> mContextMap = new HashMap<String, GenericApplicationContext>();

    /* start all of the contexts upon manager start
     * @see org.activebpel.rt.bpel.impl.AeManagerAdapter#start()
     */
    @Override
    public void start() throws Exception {
        super.start();
        for(GenericApplicationContext context : mContextMap.values()) { 
            context.start();
        }
    }

    /* stop all of the contexts upon manager stop
     * @see org.activebpel.rt.bpel.impl.AeManagerAdapter#stop()
     */
    @Override
    public void stop() {
        super.stop();
        for(GenericApplicationContext context : mContextMap.values()) {
            context.stop();
        }
    }
    
    /** Add a new context to our map
     * @param aKey
     * @param aContext
     */
    public void add(String aKey, GenericApplicationContext aContext) {
        mContextMap.put(aKey, aContext);
        aContext.start();
    }
    
    /**
     * Remove a context from the map
     * @param aKey
     */
    public GenericApplicationContext remove(String aKey) {
    	GenericApplicationContext context = mContextMap.remove(aKey);
        if (context != null) {
            context.stop();
        }
        return context;
    }
}
