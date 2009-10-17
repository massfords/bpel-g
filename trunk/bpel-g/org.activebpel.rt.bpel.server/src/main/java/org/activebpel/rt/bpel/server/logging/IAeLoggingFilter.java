//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/logging/IAeLoggingFilter.java,v 1.1 2007/02/16 14:05:22 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.logging; 

import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.bpel.IAeProcessInfoEvent;

/**
 * Interface used to filter log events
 */
public interface IAeLoggingFilter
{
   /**
    * Returns true if the process event should be logged
    * @param aEvent
    */
   public boolean accept(IAeProcessEvent aEvent);
   
   /**
    * Returns true if the process info event should be logged
    * @param aInfoEvent
    */
   public boolean accept(IAeProcessInfoEvent aInfoEvent);
   
   /**
    * Returns true if the filter will accept one or more process info events. 
    * If false, then there's no reason to listen for process events.
    */
   public boolean isEnabled();
   
   /**
    * Returns the name of the filter
    */
   public String getName();
}
 