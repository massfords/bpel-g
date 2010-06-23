// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeEvent.java,v 1.1 2006/10/20 14:41:25 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.impl;

import java.util.Date;

import org.activebpel.rt.bpel.IAeEvent;

/**
 * A base class for all AE Events.
 */
public abstract class AeEvent implements IAeEvent
{
   /** The event's timestamp. */
   private Date mTimestamp;
   
   /**
    * Default c'tor.
    */
   public AeEvent()
   {
      setTimestamp(new Date());
   }
   
   /**
    * Creates the event with the given event timestamp.
    * 
    * @param aTimestamp
    */
   public AeEvent(Date aTimestamp)
   {
      setTimestamp(aTimestamp);
   }
   
   /**
    * @see org.activebpel.rt.bpel.IAeEvent#getTimestamp()
    */
   public Date getTimestamp()
   {
      return mTimestamp;
   }
   
   /**
    * @param aTimestamp The timestamp to set.
    */
   protected void setTimestamp(Date aTimestamp)
   {
      mTimestamp = aTimestamp;
   }
}
