//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/AeUnstructuredDeploymentLog.java,v 1.1 2004/12/10 15:59:24 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging;

import java.io.PrintWriter;

/**
 * Provides an unstructured deployment log base class.  The unstructured deployment
 * logger simply writes all log messages to a PrintWriter (or the class can be
 * extended to provide some other implementation of the <code>writeMessage</code>
 * method.
 */
public class AeUnstructuredDeploymentLog extends AeDeploymentLog
{
   /** records the log as we're going. */
   protected PrintWriter mWriter;

   /**
    * Default constructor.
    */
   public AeUnstructuredDeploymentLog()
   {
   }
   
   /**
    * Creates the logger that sends its output to the provided writer.
    * @param aWriter 
    */
   public AeUnstructuredDeploymentLog(PrintWriter aWriter)
   {
      setWriter(aWriter);
   }

   /**
    * @see org.activebpel.rt.bpel.server.logging.IAeDeploymentLogger#close()
    */
   public void close()
   {
      if (getWriter() != null)
         getWriter().close();
   }
   
   /**
    * Getter for the writer.
    */
   protected PrintWriter getWriter()
   {
      return mWriter;
   }
   
   /**
    * Setter for the writer
    * @param aWriter
    */
   protected void setWriter(PrintWriter aWriter)
   {
      mWriter = aWriter;
   }

   /**
    * Writes the message to the buffer and the writer if it's present.
    * @param aMessage
    */
   protected void writeMessage(String aMessage)
   {
      if (getWriter() != null)
         getWriter().println(aMessage);
   }

}
