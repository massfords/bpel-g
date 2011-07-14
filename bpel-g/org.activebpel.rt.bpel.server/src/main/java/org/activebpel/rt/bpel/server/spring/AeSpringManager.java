package org.activebpel.rt.bpel.server.spring;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.AeManagerAdapter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Manager that keeps the references to Spring ApplicationContexts that have been deployed as part
 * of a BPR or ODE packaging. 
 * 
 * @author mford
 */
public class AeSpringManager extends AeManagerAdapter {

    /** map of contexts */
    private Map<String,GenericApplicationContext> mContextMap = new ConcurrentHashMap<String, GenericApplicationContext>();
    /** map of process qnames to their contexts */
    private Map<QName,GenericApplicationContext> mQNameContextMap = new ConcurrentHashMap<QName, GenericApplicationContext>();
    /** map of keys to sets */
    private Map<String,Set<QName>> mNameMap = new ConcurrentHashMap<String, Set<QName>>();

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
    public void add(Set<QName> aProcessSet, String aKey, GenericApplicationContext aContext) {
        // record the key to context
        mContextMap.put(aKey, aContext);
        // record the key to the process set
        mNameMap.put(aKey, aProcessSet);
        // record all of the individual process qnames
        for(QName name : aProcessSet) {
            mQNameContextMap.put(name, aContext);
        }
        aContext.start();
    }
    
    /**
     * Remove a context from the map
     * @param aKey
     */
    public GenericApplicationContext remove(String aKey) {
        // remove the context
    	GenericApplicationContext context = mContextMap.remove(aKey);
    	// remove the process names
        Set<QName> names = mNameMap.get(aKey);
        if (names != null) {
            for(QName name : names) {
                mQNameContextMap.remove(name);
            }
        }
        // remove the name set
        mNameMap.remove(aKey);
        if (context != null) {
            context.stop();
        }
        return context;
    }
    
    public <T> T getBean(QName aProcessName, Class<T> aClass) throws BeansException {
        ApplicationContext ac = mContextMap.get(aProcessName);
        return ac.getBean(aClass);
    }

    public <T> T getBean(QName aProcessName, Class<T> aClass, String aId) throws BeansException {
        ApplicationContext ac = mContextMap.get(aProcessName);
        return ac.getBean(aId, aClass);
    }
}
