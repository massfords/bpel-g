// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/scanner/AeDirectoryScanner.java,v 1.9.26.1 2008/04/21 16:12:45 ppatruni Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy.scanner;

import bpelg.services.deploy.MissingResourcesException;
import bpelg.services.deploy.UnhandledException;
import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.util.AeCloser;
import org.activebpel.rt.util.AeFileUtil;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Watches over a given directory and listens for changes.
 */
public class AeDirectoryScanner {
    /**
     * The directory to watch.
     */
    private final File mScanDir;
    /**
     * The scan interval.
     */
    private long mScanInterval;
    /**
     * Indicates if the scan should continue.
     */
    private boolean mOkToScan;
    /**
     * Thread that does the scanning
     */
    private Thread mScannerThread;
    /**
     * Snapshot of the last scan.
     */
    private Map<String, Long> mDeployments;
    /**
     * Filter for specifying file types
     */
    private FilenameFilter mFilter;
    /**
     * Listeners for directory changes
     */
    private ArrayList<IAeScannerListener> mListeners;
    /**
     * Delay interval before scanning starts.
     */
    private long mInitialDelay;
    /**
     * Comparator to control order in which files are listed.
     */
    private Comparator<String> mComparator;
    /**
     * A set of manually added files that should be skipped the next time a scan
     * happens.
     */
    private final Object mDeploymentMutex = new Object();

    /**
     * Constructor.
     *
     * @param aScanDir      The directory to watch over.
     * @param aScanInterval The scan interval. Set to -1 for single scan only.
     * @param aFilter       A FilenameFilter to select a subset of files. Pass in null to
     *                      include all files.
     */
    public AeDirectoryScanner(File aScanDir, long aScanInterval,
                              FilenameFilter aFilter, Comparator<String> aComparator) {
        mScanDir = aScanDir;
        if (!mScanDir.isDirectory()) {
            throw new IllegalArgumentException(aScanDir
                    + AeMessages.getString("AeDirectoryScanner.ERROR_0")); //$NON-NLS-1$
        }
        mScanInterval = aScanInterval;
        mFilter = aFilter;
        mComparator = aComparator;
        mListeners = new ArrayList<>();
        setDeployments(new HashMap<String, Long>());
    }

    /**
     * Prime the watch list with any matching files and return an array of
     * <code>File</code> objects that have been registered with the scanner.
     */
    protected File[] prime() {
        mDeployments = new HashMap<>();
        File scanDir = getScanDir();
        if (!scanDir.isDirectory()) {
            scanDir.mkdirs();
        }

        File[] files = getScanDir().listFiles(getFilter());
        if (files != null) {
            for (File file : files) {
                mDeployments.put(file.getName(),
                        file.lastModified());
            }
        }
        return files;
    }

    /**
     * Starts the scanning process.
     */
    public void start() {
        mScannerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(getInitialDelay());
                    setOkToScan(true);
                } catch (InterruptedException e) {
                    // not much we can do here
                }
                scanImpl();
            }
        }, "AeDeploymentScanner-Thread"); //$NON-NLS-1$
        mScannerThread.start();
    }

    /**
     * The scan impl code. NOTE: If a file has been modified/replaced, two
     * separate scan events will be fired - a removal event AND an addition
     * event.
     */
    protected void scanImpl() {
        while (isOkToScan()) {
            if (getScanInterval() < 0)
                setOkToScan(false);

            try {
                synchronized (mDeploymentMutex) {
                    String[] fileNames = getFileList();

                    if (fileNames != null) {
                        Map<String, Long> currentDeployments = new HashMap<>();
                        for (String fileName : fileNames) {
                            boolean isNewDeployment = true;
                            File bprFile = new File(getScanDir(), fileName);

                            Long lastModified = getDeployments().get(
                                    fileName);
                            if (lastModified != null) {
                                getDeployments().remove(fileName);

                                if (bprFile.lastModified() != lastModified)
                                    fireRemoveEvent(bprFile.toURI().toURL(),
                                            null);
                                else
                                    isNewDeployment = false;
                            }

                            currentDeployments.put(fileName, bprFile.lastModified());

                            if (isNewDeployment) {
                                // attempt to avoid notifying listeners if they
                                // won't be able to acquire
                                // the read lock to the file - remove from the
                                // current deployment list so we can
                                // try again on the next scan cycle.
                                if (cannotOpen(bprFile))
                                    currentDeployments.remove(fileName);
                                else
                                    fireAddEvent(bprFile.toURI().toURL(), null);
                            }

                            bprFile = null;
                        }

                        // if there are deployments left from previous check
                        // remove deployment
                        // and save the new set of deployments
                        removeRemainingDeployments();
                        setDeployments(currentDeployments);
                    }
                }
            } catch (Exception e) {
                AeException.logError(e,
                        AeMessages.getString("AeDirectoryScanner.ERROR_2")); //$NON-NLS-1$
            } finally {
                if (getScanInterval() > 0) {
                    try {
                        Thread.sleep(getScanInterval());
                    } catch (InterruptedException ie) {
                        // Thread was interrupted - most likely for shutdown.
                    }
                }
            }
        }
    }

    /**
     * Attempt to avoid issue where scanner listeners could not get read lock.
     *
     * @param aFile
     */
    protected boolean cannotOpen(File aFile) {
        InputStream in = null;
        try {
            in = new FileInputStream(aFile);
            return false;
        } catch (IOException io) {
            return true;
        } finally {
            AeCloser.close(in);
        }
    }

    /**
     * Return the filenames of all selected files in the scan directory.
     */
    protected String[] getFileList() {
        String[] files = null;

        if (getFilter() != null) {
            files = getScanDir().list(getFilter());
        } else {
            files = getScanDir().list();
        }

        if (files != null && getFileSorter() != null) {
            List<String> fileList = Arrays.asList(files);
            Collections.sort(fileList, getFileSorter());
            files = fileList.toArray(new String[fileList.size()]);
        }
        return files;
    }

    /**
     * Accessor for the file sorter.
     *
     * @return The comparator or null if none has been set.
     */
    protected Comparator<String> getFileSorter() {
        return mComparator;
    }

    /**
     * Convenience method for firing remove scan events for any entries not
     * found on the latest scan.
     *
     * @throws MalformedURLException
     */
    protected void removeRemainingDeployments() throws MalformedURLException, UnhandledException, MissingResourcesException {
        if (!getDeployments().isEmpty()) {
            for (String s : getDeployments().keySet()) {
                fireRemoveEvent(new File(getScanDir(), s).toURI().toURL(), null);
            }
        }
    }

    /**
     * Stop the scanning process.
     */
    public void stop() {
        mOkToScan = false;
        // Now interrupt the thread so it wakes up immediately and falls out of
        // the loop.
        if (mScannerThread != null) {
            mScannerThread.interrupt();
        }
    }

    /**
     * Indicates if scan should continue looping.
     */
    protected boolean isOkToScan() {
        return mOkToScan;
    }

    /**
     * Accessor for the scan directory.
     */
    protected File getScanDir() {
        return mScanDir;
    }

    /**
     * Adds a file url string to the list of manually added files. This list of
     * files will be skipped next time the scanner scans.
     *
     * @param aFile
     * @param aFileName
     * @param aUserData
     * @throws AeException
     */
    protected void addDeploymentFile(File aFile, String aFileName,
                                     Object aUserData) throws UnhandledException, MissingResourcesException {
        File destFile = new File(getScanDir(), aFileName);
        synchronized (mDeploymentMutex) {

            try {
                // First, undeploy the BPR if it already exists.
                if (getDeployments().get(destFile.getName()) != null) {
                    fireRemoveEvent(destFile.toURI().toURL(), null);
                }

                // Now, copy the new file into the scanned dir.
                AeFileUtil.copyFile(aFile, destFile);

                // Now update my internal state.
                getDeployments().put(destFile.getName(),
                        destFile.lastModified());

                // Now fire an add event.
                fireAddEvent(destFile.toURI().toURL(), aUserData);
            } catch (IOException e) {
                throw new UnhandledException(e.getMessage(), e);
            }
        }
    }

    /**
     * Setter for scan flag.
     *
     * @param aFlag Indicates if scan should continue looping.
     */
    protected void setOkToScan(boolean aFlag) {
        mOkToScan = aFlag;
    }

    /**
     * Accessor for the scan interval.
     */
    protected long getScanInterval() {
        return mScanInterval;
    }

    /**
     * Setter for map of watched files.
     *
     * @param aMap
     */
    protected void setDeployments(Map<String, Long> aMap) {
        mDeployments = aMap;
    }

    /**
     * Accessor for scan directory contents that are being watched.
     */
    protected Map<String, Long> getDeployments() {
        return mDeployments;
    }

    /**
     * Accessor for the FilenameFilter.
     */
    protected FilenameFilter getFilter() {
        return mFilter;
    }

    /**
     * Add a scanner listener.
     *
     * @param aListener
     */
    public void addListener(IAeScannerListener aListener) {
        synchronized (mListeners) {
            if (!mListeners.contains(aListener)) {
                mListeners.add(aListener);
            }
        }
    }

    /**
     * Remove a scanner listener.
     *
     * @param aListener
     */
    public void removeListener(IAeScannerListener aListener) {
        synchronized (mListeners) {
            mListeners.remove(aListener);
        }
    }

    /**
     * Fire a remove event.
     *
     * @param aURL
     * @param aUserData
     */
    protected void fireRemoveEvent(URL aURL, Object aUserData) throws UnhandledException, MissingResourcesException {
        fireEvent(aURL, AeScanEvent.REMOVAL, aUserData);
    }

    /**
     * Fire an add event.
     *
     * @param aURL
     * @param aUserData
     */
    protected void fireAddEvent(URL aURL, Object aUserData) throws UnhandledException, MissingResourcesException {
        fireEvent(aURL, AeScanEvent.ADDITION, aUserData);
    }

    /**
     * Convenience method for firing events to listeners.
     *
     * @param aURL      The file url.
     * @param aType     The event type.
     * @param aUserData
     */
    @SuppressWarnings("unchecked")
    protected void fireEvent(URL aURL, int aType, Object aUserData) throws UnhandledException, MissingResourcesException {
        List<IAeScannerListener> recipients = null;
        synchronized (mListeners) {
            if (!mListeners.isEmpty()) {
                recipients = (ArrayList<IAeScannerListener>) mListeners.clone();
            }
        }

        if (recipients != null) {
            AeScanEvent event = new AeScanEvent(aURL, aType, aUserData);
            for (IAeScannerListener recipient : recipients) {
                recipient.onChange(event);
            }
        }
    }

    /**
     * Setter for the initial delay.
     *
     * @param aTimeInMillis
     */
    public void setInitialDelay(long aTimeInMillis) {
        mInitialDelay = aTimeInMillis;
    }

    /**
     * Accessor for initial delay.
     */
    public long getInitialDelay() {
        return mInitialDelay;
    }
}
