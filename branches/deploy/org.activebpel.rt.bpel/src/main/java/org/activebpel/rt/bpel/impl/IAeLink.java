// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/IAeLink.java,v 1.3 2004/07/08 13:09:58 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import org.activebpel.rt.bpel.IAeActivity;

/**
 * Interface for Bpel Link object implementations. 
 */
public interface IAeLink
{
   /**
    * Returns a bpel path unqiue within the process for event processing.
    */
   public String getLocationPath();
   
   /**
    * Returns true if the status of this link is known.
    */
   public boolean isStatusKnown();
   
   /**
    * Returns the status. If the status of this link is unknown then it'll 
    * generate an exception since the spec dictates that you don't evaluate join
    * conditions until the status of all inbound links is known.
    * @return true if transition condition met, false if not, exception if not known
    */
   public boolean getStatus();
   
   /**
    * Getter for the target activity.
    */
   public IAeActivity getTargetActivity();
   
   /**
    * Getter for the source activity.
    */
   public IAeActivity getSourceActivity();
}
