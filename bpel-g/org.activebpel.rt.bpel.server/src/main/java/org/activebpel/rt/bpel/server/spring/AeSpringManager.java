package org.activebpel.rt.bpel.server.spring;

import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.impl.AeManagerAdapter;
import org.springframework.context.support.StaticApplicationContext;

public class AeSpringManager extends AeManagerAdapter {

    private Map<String,StaticApplicationContext> mContextMap = new HashMap();

    @Override
    public void start() throws Exception {
        super.start();
        for(StaticApplicationContext context : mContextMap.values()) {
            context.start();
        }
    }

    @Override
    public void stop() {
        super.stop();
        for(StaticApplicationContext context : mContextMap.values()) {
            context.close();
        }
    }
    
    public void setContextMap(Map<String, StaticApplicationContext> aContextMap) {
        mContextMap.putAll(aContextMap);
    }
    
    public void add(String aKey, StaticApplicationContext aContext) {
        mContextMap.put(aKey, aContext);
        aContext.start();
    }
    
    public void remove(String aKey) {
        StaticApplicationContext context = mContextMap.remove(aKey);
        if (context != null) {
            context.close();
        }
    }
}
