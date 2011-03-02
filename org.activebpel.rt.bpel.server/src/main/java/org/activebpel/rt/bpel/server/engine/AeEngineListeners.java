package org.activebpel.rt.bpel.server.engine;

import java.util.Collections;
import java.util.List;

import org.activebpel.rt.bpel.IAeBusinessProcessEngine;
import org.activebpel.rt.bpel.IAeEngineListener;

public class AeEngineListeners {
	private List<IAeEngineListener> mListeners = Collections.EMPTY_LIST;
	private IAeBusinessProcessEngine mEngine;
	
	public void init() {
		if (getEngine() != null && getListeners() != null) {
			for(IAeEngineListener l : getListeners()) {
				getEngine().addEngineListener(l);
			}
		}
	}

	public List<IAeEngineListener> getListeners() {
		return mListeners;
	}

	public void setListeners(List<IAeEngineListener> aListeners) {
		mListeners = aListeners;
	}

	public IAeBusinessProcessEngine getEngine() {
		return mEngine;
	}

	public void setEngine(IAeBusinessProcessEngine aEngine) {
		mEngine = aEngine;
	}
	
}
