// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/AeProcessEvent.java,v 1.7 2006/10/20 14:41:25 EWittmann Exp $
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

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.IAeProcessEvent;
import org.activebpel.rt.util.AeStaticConstantsMap;
import org.activebpel.rt.util.AeUtil;

/**
 * Implementation of bpel process events.
 */
public class AeProcessEvent extends AeBaseProcessEvent implements IAeProcessEvent
{
   /** Maps names and values of static constants declared in {@link IAeProcessEvent}. **/
   private static final AeStaticConstantsMap mIAeProcessEventConstantsMap = new AeStaticConstantsMap(IAeProcessEvent.class);

   /** The process' QName. */
   private QName mName; 

   /**
    * Constructor with all members specified.
    * @param aPID The process ID of the event.
    * @param aPath The path of the object trigerring the event.
    * @param aEventID The event id of the event.
    * @param aFault The Fault associated with this event, or empty.
    * @param aInfo Extra info to register with the event.
    * @param aName The process' QName.
    */
   public AeProcessEvent(long aPID, String aPath, int aEventID, String aFault, String aInfo, QName aName)
   {
      super(aPID, aPath, aEventID, aFault, aInfo);
      mName = aName;
   }
   
   /**
    * Constructor with all members specified (including timestamp).
    * 
    * @param aPID
    * @param aPath
    * @param aEventID
    * @param aFault
    * @param aInfo
    * @param aName
    * @param aTimestamp
    */
   public AeProcessEvent(long aPID, String aPath, int aEventID, String aFault, String aInfo, QName aName, Date aTimestamp)
   {
      super(aPID, aPath, aEventID, aFault, aInfo, aTimestamp);
      mName = aName;
   }

   /**
    * Constructor with no Fault or Ancillary Info.
    * @param aPID The process ID of the event.
    * @param aPath The path of the object trigerring the event.
    * @param aEventID The event id of the event.
    * @param aName The process' QName.
    */
   public AeProcessEvent(long aPID, String aPath, int aEventID, QName aName)
   {
      this( aPID, aPath, aEventID, "", "", aName ); //$NON-NLS-1$ //$NON-NLS-2$
   }

   /**
    * @see org.activebpel.rt.bpel.IAeProcessEvent#getQName()
    */
   public QName getQName()
   {
      return mName;
   }

   /**
    * Returns the name of the specified event id.
    */
   protected static String getEventIdName(int aEventId)
   {
      String name = mIAeProcessEventConstantsMap.getName(new Integer(aEventId));

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

      map.put("qname", getQName()); //$NON-NLS-1$

      return "AeProcessEvent" + map.toString(); //$NON-NLS-1$
   }
}
