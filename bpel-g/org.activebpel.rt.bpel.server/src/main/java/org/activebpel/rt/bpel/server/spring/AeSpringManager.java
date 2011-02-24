package org.activebpel.rt.bpel.server.spring;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.impl.AeManagerAdapter;
import org.springframework.context.support.GenericApplicationContext;

public class AeSpringManager extends AeManagerAdapter {

    private Map<String,GenericApplicationContext> mContextMap = new HashMap();

    @Override
    public void start() throws Exception {
        super.start();
        for(GenericApplicationContext context : mContextMap.values()) { 
            context.start();
        }
    }

    @Override
    public void stop() {
        super.stop();
        for(GenericApplicationContext context : mContextMap.values()) {
            context.stop();
        }
    }
    
    public void setContextMap(Map<String, GenericApplicationContext> aContextMap) {
        mContextMap.putAll(aContextMap);
    }
    
    public void add(String aKey, GenericApplicationContext aContext) {
        mContextMap.put(aKey, aContext);
        aContext.start();
    }
    
    public GenericApplicationContext remove(String aKey) {
    	GenericApplicationContext context = mContextMap.remove(aKey);
        if (context != null) {
            context.stop();
        }
        return context;
    }
}
