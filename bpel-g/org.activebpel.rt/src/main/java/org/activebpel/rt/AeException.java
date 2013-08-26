// $Header: /Development/AEDevelopment/projects/org.activebpel.rt/src/org/activebpel/rt/AeException.java,v 1.19 2007/03/16 12:00:04 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt;

import org.activebpel.rt.util.AeLoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Describes the standard exception
 */
public class AeException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -7891249123313611358L;

    /**
     * Flag indicating if stack trace enabled, on by default.
     */
    public static boolean sStackTraceOn = true;

    /**
     * Message associated with this exception - for serialization.
     */
    private String mInfo;

    /**
     * The logger to use for messages.
     */
    private static final Logger sLogger = AeLoggerFactory.createLogger("AeException"); //$NON-NLS-1$

    /**
     * Construct a new runtime exception.
     */
    public AeException() {
        super();
    }

    /**
     * Construct a new runtime exception with the passed info string.
     *
     * @see java.lang.Throwable#Throwable(String)
     */
    public AeException(String aInfo) {
        super(aInfo);
        setInfo(aInfo);
    }

    /**
     * Construct with a root exception (used primarily for rethrowing an underlying exception).
     *
     * @param aRootCause
     */
    public AeException(Throwable aRootCause) {
        super(aRootCause);
        setInfo(aRootCause.getLocalizedMessage());
    }

    /**
     * Construct with a root exception (used prinarily for rethrowing an underlying exception).
     *
     * @param aInfo      Informational message for the exception
     * @param aRootCause Root cause of the exception
     */
    public AeException(String aInfo, Throwable aRootCause) {
        super(aInfo, aRootCause);
    }

    /**
     * Utility method to write info to the console.
     *
     * @param aInfo Informational message to be displayed.
     */
    public static void info(String aInfo) {
        sLogger.info(aInfo);
    }

    /**
     * Log an error
     *
     * @param aThrowable Throwable who's stack trace is to be logged
     * @param aSummary   Informational message to be displayed.
     */
    public static void logError(Throwable aThrowable, String aSummary) {
        StringWriter sw = new StringWriter();
        //noinspection IOResourceOpenedButNotSafelyClosed
        PrintWriter pw = new PrintWriter(sw);

        // Collect everything into a single string before we log it to the logger.
        printError(pw, aThrowable, aSummary);

        // Log the collected error information.
        sLogger.severe(sw.toString());
    }

    /**
     * Logs an error including nested causes and in the case of SQLExceptions, the next SQL exception in the chain.
     *
     * @param aThrowable
     */
    public static void logError(Throwable aThrowable) {
        logError(aThrowable, null);
    }

    /**
     * Prints an exception stack trace to the given output stream.
     */
    public static void printError(PrintWriter aOut, Throwable aThrowable) {
        printError(aOut, aThrowable, null, true);
    }

    /**
     * Called by {@link #logError(Throwable, String)} to print an error message
     * and/or exception to the given output stream.
     */
    private static void printError(PrintWriter aOut, Throwable aThrowable, String aSummary) {
        printError(aOut, aThrowable, aSummary, sStackTraceOn);
    }

    /**
     * Prints an error message and/or exception stack trace to the given output
     * stream.
     */
    private static void printError(PrintWriter aOut, Throwable aThrowable, String aSummary, boolean aStackTraceOn) {
        if (aSummary != null) {
            aOut.println(aSummary);
        }

        if (aThrowable != null) {
            if (aStackTraceOn) {
                aThrowable.printStackTrace(aOut);
            } else {
                aOut.println(aThrowable.getLocalizedMessage());

                printCausedByMessages(aOut, aThrowable);
            }

            if (aThrowable instanceof SQLException) {
                printError(aOut, ((SQLException) aThrowable).getNextException(), null, aStackTraceOn);
            }
        }
    }

    /**
     * Called by {@link #printError(PrintWriter, Throwable, String)} to recursively
     * print "Caused by: " messages for nested causes.
     */
    private static void printCausedByMessages(PrintWriter aOut, Throwable aThrowable) {
        Throwable cause = aThrowable.getCause();

        if ((cause != null) && (cause != aThrowable)) {
            aOut.print(AeMessages.getString("AeException.2")); //$NON-NLS-1$
            aOut.println(cause.getLocalizedMessage());

            printCausedByMessages(aOut, cause);
        }
    }

    /**
     * Log a warning
     *
     * @param aSummary Informational message to be displayed.
     */
    public static void logWarning(String aSummary) {
        sLogger.warning(aSummary);
    }

    /**
     * Logs this exception error.
     */
    public void logError() {
        logError(this);
    }

    /**
     * @return Returns the info for the exception.
     */
    public String getInfo() {
        return mInfo;
    }

    /**
     * @param aInfo The info to set.
     */
    public void setInfo(String aInfo) {
        mInfo = aInfo;
    }
}
