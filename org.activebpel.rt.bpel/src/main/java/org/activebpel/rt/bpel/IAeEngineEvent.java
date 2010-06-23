// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/IAeEngineEvent.java,v 1.16 2007/09/28 19:37:28 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel;

import javax.xml.namespace.QName;

/**
 * Interface for engine events 
 */
public interface IAeEngineEvent extends IAeEvent
{
   /** Process was created. */
   public static final int PROCESS_CREATED = 0 ;
   /** Process has been terminated. */
   public static final int PROCESS_TERMINATED = 1 ;
   /** Process has been suspended. */
   public static final int PROCESS_SUSPENDED = 2 ;
   /** Process has been resumed. */
   public static final int PROCESS_RESUMED = 3 ;
   /** Process has started executing. */
   public static final int PROCESS_STARTED = 4 ;
   /** Process was recreated. */
   public static final int PROCESS_RECREATED = 5 ;

   /**
    * Returns the event id for the engine event.
    */
   public int getEventID();
   
   /**
    * Returns the process id for the engine event.
    */
   public long getPID();
   
   /**
    * Returns the namespace qualified name of the process this event represents.
    */
   public QName getProcessName();
}
