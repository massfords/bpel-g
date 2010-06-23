// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeProcessInfoEvent.java,v 1.8 2006/10/20 14:41:24 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.activebpel.rt.bpel.IAeProcessInfoEvent;
import org.activebpel.rt.util.AeStaticConstantsMap;
import org.activebpel.rt.util.AeUtil;

/**
 * Implementation of bpel process information events.
 */
public class AeProcessInfoEvent extends AeBaseProcessEvent implements IAeProcessInfoEvent
{
   /** Maps names and values of static constants declared in {@link IAeProcessInfoEvent}. **/
   private static final AeStaticConstantsMap mIAeProcessInfoEventConstantsMap = new AeStaticConstantsMap(IAeProcessInfoEvent.class);

   /**
    * Constructor with all members specified.
    * @param aPID The process ID of the event.
    * @param aPath The path of the object trigerring the event.
    * @param aEventID The event id of the event.
    * @param aFault The associated Fault, or empty.
    * @param aInfo Extra info to register with the event.
    */
   public AeProcessInfoEvent(long aPID, String aPath, int aEventID, String aFault, String aInfo)
   {
      super(aPID, aPath, aEventID, aFault, aInfo);
   }

   /**
    * Constructor with all members specified (including timestamp).
    * @param aPID The process ID of the event.
    * @param aPath The path of the object trigerring the event.
    * @param aEventID The event id of the event.
    * @param aFault The associated Fault, or empty.
    * @param aInfo Extra info to register with the event.
    * @param aTimestamp The event timestamp
    */
   public AeProcessInfoEvent(long aPID, String aPath, int aEventID, String aFault, String aInfo, Date aTimestamp)
   {
      super(aPID, aPath, aEventID, aFault, aInfo, aTimestamp);
   }

   /**
    * Constructor with no Fault or Ancillary Info.
    * @param aPID The process ID of the event.
    * @param aPath The path of the object trigerring the event.
    * @param aEventID The event id of the event.
    */
   public AeProcessInfoEvent(long aPID, String aPath, int aEventID )
   {
      this(aPID, aPath, aEventID, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * Returns the name of the specified event id.
    */
   protected static String getEventIdName(int aEventId)
   {
      String name = mIAeProcessInfoEventConstantsMap.getName(new Integer(aEventId));

      // Use the name if we have it; otherwise, show the value itself.
      return (name != null) ? name : String.valueOf(aEventId);
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      Map map = new LinkedHashMap(); // LinkedHashMap to preserve order of insertions for toString()

      map.put("pid", String.valueOf(getPID())); //$NON-NLS-1$
      map.put("eventid", getEventIdName(getEventID())); //$NON-NLS-1$
      map.put("path", getNodePath()); //$NON-NLS-1$

      if (!AeUtil.isNullOrEmpty(getFaultName()))
      {
         map.put("fault", getFaultName()); //$NON-NLS-1$
      }

      if (!AeUtil.isNullOrEmpty(getAncillaryInfo()))
      {
         map.put("info", getAncillaryInfo()); //$NON-NLS-1$
      }

      return "AeProcessInfoEvent" + map.toString(); //$NON-NLS-1$
   }
}
