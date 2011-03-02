// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/list/AeAlarmFilterManager.java,v 1.9 2008/03/10 18:30:24 PJayanetti Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.queue.AeAlarm;
import org.activebpel.rt.util.AeUtil;

/**
 * Provides filtering capability for the in-memory alarm manager listing.
 */
public class AeAlarmFilterManager
{

   /** Default comparator for sorting the list of alarms. */
   private static final AeAlarmComparator SORTER = 
      new AeAlarmComparator();

   /**
    * Returns a filtered list of alarms.
    * @param aEngine the bpel engine.
    * @param aFilter Determines the selection criteria.
    * @param aAlarms All of the available alarms on the queue.
    * @return The filtered results.
    */
   public static AeAlarmListResult filter( IAeBusinessProcessEngineInternal aEngine, 
                                             AeAlarmFilter aFilter, List aAlarms )
   {
      List matches = new ArrayList();
      // totalRows is the number of records that matched the filter query.
      int totalRows = 0;
      
      if( aAlarms != null && !aAlarms.isEmpty() )
      {
         AeAlarm[] recs = (AeAlarm[]) aAlarms.toArray( 
            new AeAlarm[aAlarms.size()]);
            
         for( int i = 0; i < recs.length; i++ )
         {
            AeAlarm alarm = recs[i];
            try
            {
               QName processQName = aEngine.getProcessManager().getProcessQName(alarm.getProcessId());
               AeAlarmExt alarmExt = new AeAlarmExt(alarm.getProcessId(), alarm.getPathId(), alarm.getGroupId(), alarm.getAlarmId() , alarm.getDeadline(), processQName);
               if( accepts(aFilter, alarmExt) )                  
               {                  
                  totalRows++;
                  // add to 'matches' list if  [startPos]  <= [current matched record number] < [endPos]
                  // where startPos = aFilter.getListStart(),  endPos = startPos + aFilter.getMaxReturn() 
                  if( aFilter.isWithinRange(totalRows) )
                  {
                     matches.add(alarmExt);
                  }
               }
            }
            catch (AeBusinessProcessException e)
            {
               AeException.logError(e, AeMessages.getString("AeAlarmFilterManager.ERROR_0") + alarm.getProcessId()); //$NON-NLS-1$
            }
         }
      }
      
      if( ! matches.isEmpty() )
      {
         sort( matches );
      }
      
      return new AeAlarmListResult(totalRows, matches);
   }
   
   /**
    * Returns true if the message alarm meets the filter criteria.
    * @param aFilter The selection criteria.
    * @param aAlarm A queued message alarm.
    */
   protected static boolean accepts( AeAlarmFilter aFilter, AeAlarmExt aAlarm )
   {
      return isPIDMatch( aFilter, aAlarm ) &&
              isProcessNameMatch(aFilter, aAlarm) &&
              isDeadlineMatch(aFilter, aAlarm);
   }
   
   /**
    * Match that the alarm matches the deadline between dates if filled in.
    * @param aFilter The filter to match the start and end between from
    * @param aAlarm The alarm to test.
    * @return True if match of deadline with filter.
    */
   private static boolean isDeadlineMatch(AeAlarmFilter aFilter, AeAlarmExt aAlarm)
   {
      if(aFilter.getAlarmFilterStart() != null)
         if(aAlarm.getDeadline().getTime() < aFilter.getAlarmFilterStart().getTime())
            return false;
      if(aFilter.getAlarmFilterEnd() != null)
         if(aAlarm.getDeadline().getTime() > aFilter.getAlarmFilterEnd().getTime())
            return false;
      return true;
   }

   /**
    * Match that the alarm matches the proess name in filter if filled in.
    * @param aFilter The filter to match the process name
    * @param aAlarm The alarm to test.
    * @return True if the process name matches the passed name.
    */
   private static boolean isProcessNameMatch(AeAlarmFilter aFilter, AeAlarmExt aAlarm)
   {
      if(aFilter.getProcessName() != null)
      {
         if(! AeUtil.isNullOrEmpty(aFilter.getProcessName().getLocalPart()))
            return AeUtil.compareObjects(aFilter.getProcessName().getLocalPart(), aAlarm.getProcessName());
         if(! AeUtil.isNullOrEmpty(aFilter.getProcessName().getNamespaceURI()))
            return AeUtil.compareObjects(aFilter.getProcessName().getNamespaceURI(), aAlarm.getProcessQName().getNamespaceURI());
      }
      
      return true;
   }

   /**
    * Returns true if there is no process id specified in the filter or
    * if the process id in the filter mathes the receive's process id.
    * @param aFilter The selection criteria.
    * @param aAlarm A queued message alarm.
    */
   static boolean isPIDMatch( AeAlarmFilter aFilter, AeAlarmExt aAlarm )
   {
      if( !aFilter.isNullProcessId() )
      {
         return aFilter.getProcessId() == aAlarm.getProcessId();
      }
      else
      {
         return true;
      }
   }
   
   /**
    * Sorts the matching queued alarm.
    * @param aMatches
    */
   protected static void sort( List aMatches )
   {
      Collections.sort( aMatches, SORTER );
   }
   
   /**
    * Comparator impl compares on process id.
    */
   protected static class AeAlarmComparator implements Comparator
   {
      /**
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      public int compare(Object a1, Object a2)
      {
         AeAlarm alarmOne = (AeAlarm)a1;
         AeAlarm alarmTwo = (AeAlarm)a2;
         
         int match = (int) (alarmOne.getProcessId() - alarmTwo.getProcessId());

         return match;
      }
   }
}
