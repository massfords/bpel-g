//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/queue/handlers/AeAlarmListResponseHandler.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.handlers;

import java.util.Date;

import org.activebpel.rt.bpel.server.engine.storage.AePersistedAlarm;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.IAeQueueElements;
import org.w3c.dom.Element;


/**
 * A Query Handler that converts a TResponse to a List of AeXMLDBAlarm objects.
 */
public class AeAlarmListResponseHandler extends AeXMLDBListResponseHandler
{
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement)
   {
      long procId = getLongFromElement(aElement, IAeQueueElements.PROCESS_ID).longValue();
      int locId = getIntFromElement(aElement, IAeQueueElements.LOCATION_PATH_ID).intValue();
      int groupId = getIntFromElement(aElement, IAeQueueElements.GROUP_ID).intValue();
      int alarmId = getIntFromElement(aElement, IAeQueueElements.ALARM_ID).intValue();
      Date deadline = getDateTimeFromElement(aElement, IAeQueueElements.DEADLINE);
      
      return new AePersistedAlarm(procId, locId, deadline, groupId, alarmId);
   }
}