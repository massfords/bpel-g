// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpeladmin.war/src/org/activebpel/rt/bpeladmin/war/AeProcessLogDumpServlet.java,v 1.5 2005/04/13 17:19:53 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpeladmin.war;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activebpel.rt.bpel.server.admin.jmx.AeProcessLogPart;
import org.activebpel.rt.bpel.server.admin.jmx.IAeEngineManagementMXBean;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.bpel.server.engine.IAeProcessLogger;
import org.activebpel.rt.util.AeCloser;

/**
 * Responsible for dumping the contents of a process's log file and streaming it
 * to a client.
 */
public class AeProcessLogDumpServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 6697153830648576282L;

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest aRequest, HttpServletResponse aResponse)
            throws ServletException, IOException {
        doPost(aRequest, aResponse);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest aRequest, HttpServletResponse aResponse)
            throws ServletException, IOException {
        long pid;

        try {
            pid = Long.parseLong(aRequest.getParameter("pid")); //$NON-NLS-1$
        } catch (Exception e) {
            aResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    AeMessages.getString("AeProcessLogDumpServlet.1")); //$NON-NLS-1$
            return;
        }

        streamLog(aResponse, pid);
    }

    /**
     * Gets the log from the server and streams it to the response.
     * 
     * @param aResponse
     * @param aPid
     * @throws IOException
     */
    private void streamLog(HttpServletResponse aResponse, long aPid) throws IOException {
        PrintWriter out = null;
        try {
            IAeProcessLogger logger = AeEngineFactory.getBean(IAeProcessLogger.class);
            out = setupStream(aPid, aResponse);
            if (logger != null) {
                Reader reader = logger.getFullLog(aPid);

                consume(reader, out);
            } else {
                IAeEngineManagementMXBean bean = AeEngineManagementFactory.getBean();
                for (int part = 0; true; part++) {
                    AeProcessLogPart logPart = bean.getProcessLogPart(aPid, part);
                    if (!out.checkError() && logPart.getLog() != null) {
                        out.write(logPart.getLog());
                    } else {
                        break;
                    }
                }
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            aResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        } finally {
            AeCloser.close(out);
        }
    }

    /**
     * Consumes the inputstream, writing its contents to the outputstream.
     * 
     * @param aReader
     * @param aWriter
     * @throws IOException
     */
    private void consume(Reader aReader, PrintWriter aWriter) throws IOException {
        char[] buff = new char[1024 * 4];
        int read;
        try {
            while ((read = aReader.read(buff)) != -1) {
                aWriter.write(buff, 0, read);

                // This is the only way to detect whether an error has occurred
                // on a
                // PrintWriter. Stop copying the log to the output if an error
                // has
                // occurred--e.g., the user cancelled saving the log.
                if (aWriter.checkError()) {
                    break;
                }
            }
        } finally {
            AeCloser.close(aReader);
        }
    }

    /**
     * Sets up the headers needed for the outputstream to do the streaming and
     * returns the outputstream.
     * 
     * @param aPid
     * @param aResponse
     */
    private PrintWriter setupStream(long aPid, HttpServletResponse aResponse) throws IOException {
        aResponse.setContentType("application/octet-stream"); //$NON-NLS-1$
        aResponse.setHeader("Content-disposition", "attachment;filename=process_" + aPid + ".log"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return aResponse.getWriter();
    }
}
