//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/IAeProcessLogEntry.java,v 1.2 2005/04/13 17:26:47 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging; 

/**
 * Provides an interface for a data structure that contains a segment of logging
 * statements from a process log. These statements are accumulated in memory for
 * the process until such time as the process is persisted. At this time, the 
 * log entry will be extracted from the logger and written to the underlying
 * storage. 
 */
public interface IAeProcessLogEntry
{
   /**
    * Gets the contents of this log entry.
    */
   public String getLog();
   
   /**
    * Gets the number of lines in the entry
    */
   public int getLineCount();
   
   /**
    * Gets the process id for this entry
    */
   public long getProcessId();

   /**
    * Clears the contents of this log entry from the log.
    */
   public void clearFromLog();
}
 