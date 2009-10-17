// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeAlarmExt.java,v 1.4 2006/09/18 17:55:34 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002, 2003, 2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.util.Date;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.queue.AeAlarm;

/**
 * Extended alarm info contains process name.
 */
public class AeAlarmExt extends AeAlarm
{
   /** The process name */
   private QName mProcessQName;
   /** Location translation for xpath if provided. */
   protected String mLocation;
   
   /**
    * Extended alarm info for list returns.
    * @param aPID The process id
    * @param aPathId the location path id
    * @param aGroupId the group id of the alarm
    * @param aAlarmId alarm execution reference id.
    * @param aDeadline the deadline for alarm
    * @param aProcessQName the name of owning process
    */
   public AeAlarmExt(long aPID, int aPathId, int aGroupId, int aAlarmId, Date aDeadline, QName aProcessQName)
   {
      super(aPID, aPathId, aGroupId, aAlarmId, aDeadline);
      mProcessQName = aProcessQName;
   }

   /**
    * Returns the process name associated with this alarm.
    */
   public String getProcessName()
   {
      return mProcessQName.getLocalPart();
   }

   /**
    * Returns the process qname associated with this alarm.
    */
   public QName getProcessQName()
   {
      return mProcessQName;
   }

   /**
    * Returns the location or id of location if not translated.
    */
   public String getLocation()
   {
      if(mLocation == null)
         return Integer.toString(getPathId());
      return mLocation;
      
   }

   /**
    * Sets the translated location to a path in bpel.
    */
   public void setLocation(String aLocation)
   {
      mLocation = aLocation;
      
   }
}
