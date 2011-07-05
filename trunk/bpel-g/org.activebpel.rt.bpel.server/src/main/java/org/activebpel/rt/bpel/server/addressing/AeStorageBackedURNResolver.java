//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/addressing/AeStorageBackedURNResolver.java,v 1.2.4.1 2008/04/28 21:55:25 vvelusamy Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.addressing;

import java.util.HashMap;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeStorageFactory;
import org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage;
import org.activebpel.rt.bpel.urn.AeURNResolver;
import org.activebpel.rt.util.AeUtil;

/**
 * Keeps all of the mappings in memory.
 */
public class AeStorageBackedURNResolver extends AeURNResolver implements
		IAeStorageBackedURNResolver {
	IAeStorageFactory mStorageFactory;

	public void init() throws AeStorageException {
        getMap().putAll(getStorage().getMappings());
	}

	/**
	 * @see org.activebpel.rt.bpel.urn.AeURNResolver#addMapping(java.lang.String,
	 *      java.lang.String)
	 */
	public synchronized void addMapping(String aURN, String aURL) {
		try {
			getStorage().addMapping(AeUtil.normalizeURN(aURN), aURL);
			super.addMapping(aURN, aURL);
		} catch (AeStorageException e) {
			e.logError();
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.urn.AeURNResolver#removeMappings(java.lang.String[])
	 */
	public synchronized void removeMappings(String[] aURNArray) {
		try {
			for (int i = 0; i < aURNArray.length; i++) {
				aURNArray[0] = AeUtil.normalizeURN(aURNArray[0]);
			}
			getStorage().removeMappings(aURNArray);
			super.removeMappings(aURNArray);
		} catch (AeStorageException e) {
			e.logError();
		}
	}

	/**
	 * @see org.activebpel.rt.bpel.server.addressing.IAeStorageBackedURNResolver#reload()
	 */
	public synchronized void reload() {
		try {
			setMap(new HashMap<String,String>(getStorage().getMappings()));
		} catch (AeStorageException e) {
			e.logError();
		}
	}

	/**
	 * Getter for the storage.
	 */
	public IAeURNStorage getStorage() {
		return mStorageFactory.getURNStorage();
	}

	/**
	 * Setter for the storage factory.
	 */
	public void setStorageFactory(IAeStorageFactory aFactory) {
		mStorageFactory = aFactory;
	}
}
