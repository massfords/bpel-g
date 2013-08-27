//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/invoke/AeFileBasedURNStorage.java,v 1.3 2005/12/03 01:06:07 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.invoke;

import org.activebpel.rt.bpel.server.deploy.scanner.AeDeploymentFileInfo;
import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;
import org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage;
import org.activebpel.rt.util.AeUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * File based implementation of <code>IAeURNStorage</code>. The file is kept up
 * to date with changes.
 */
public class AeFileBasedURNStorage implements IAeURNStorage {
    // TODO (MF) we should watch the file for changes like we do for the aeEngineConfig.xml file.

    /**
     * source for the props
     */
    private File mFile;

    /**
     * Map constructor required for construction through engine factory
     */
    public AeFileBasedURNStorage(Map aMap) {
        String filename = (String) aMap.get("File"); //$NON-NLS-1$
        if (AeUtil.isNullOrEmpty(filename)) {
            filename = "urn.properties"; //$NON-NLS-1$
        }
        File file = new File(AeDeploymentFileInfo.getDeploymentDirectory(), filename);
        setFile(file);
    }

    /**
     * Creates the storage with the file as the src for the properties.
     *
     * @param aFile
     */
    public AeFileBasedURNStorage(File aFile) {
        mFile = aFile;
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#getMappings()
     */
    public synchronized Map<String, String> getMappings() throws AeStorageException {
        // Defect #1225 fix: alway return a properties object instead of an immutable collection
        // (java.util.Collections.EMPTY_MAP) since it does not implement Map::put(key,value).
        // The AeFileBaseURNStorage::addMapping(..) method invokes a put() on the map return
        // by this method - hence the map implementation must be mutable.

        Properties props = new Properties();
        if (getFile() != null && getFile().exists() && getFile().isFile()) {
            try (InputStream in = new FileInputStream(getFile())) {
                props.load(in);
            } catch (Exception e) {
                throw new AeStorageException(e);
            }
        }
        Map<String, String> ret = new HashMap<>();
        for (Entry e : props.entrySet()) {
            ret.put(e.getKey().toString(), e.getValue().toString());
        }
        return ret;
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#addMapping(java.lang.String, java.lang.String)
     */
    public synchronized void addMapping(String aURN, String aURL) throws AeStorageException {
        Map<String, String> map = getMappings();
        map.put(aURN, aURL);
        saveMappings(map);
    }

    /**
     * @see org.activebpel.rt.bpel.server.engine.storage.IAeURNStorage#removeMappings(java.lang.String[])
     */
    public synchronized void removeMappings(String[] aURNArray) throws AeStorageException {
        Map<String, String> map = getMappings();
        for (String arr : aURNArray) {
            map.remove(arr);
        }
        saveMappings(map);
    }

    /**
     * Saves the mappings to the file.
     *
     * @param aMap
     */
    protected synchronized void saveMappings(Map<String, String> aMap) throws AeStorageException {
        Properties props = new Properties();
        props.putAll(aMap);

        try (OutputStream out = new FileOutputStream(getFile())) {
            props.store(out, ""); //$NON-NLS-1$
        } catch (Exception e) {
            throw new AeStorageException(e);
        }
    }

    /**
     * Getter for the file.
     */
    protected File getFile() {
        return mFile;
    }

    /**
     * Setter for the file
     *
     * @param aFile
     */
    protected void setFile(File aFile) {
        mFile = aFile;
    }
}
 