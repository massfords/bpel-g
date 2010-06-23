//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/queue/handlers/AeFilteredAlarmListResponseHandler.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
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

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.impl.list.AeAlarmExt;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBFilteredListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.queue.IAeQueueElements;
import org.w3c.dom.Element;


/**
 * A Query Handler that converts a TResponse to a List of AeXMLDBAlarm objects (filtered).
 */
public class AeFilteredAlarmListResponseHandler extends AeXMLDBFilteredListResponseHandler
{
   /**
    * Creates a query handler.
    */
   public AeFilteredAlarmListResponseHandler()
   {
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
    */
   protected Object handleElement(Element aElement) throws AeXMLDBException
   {
      try
      {
         long procId = getLongFromElement(aElement, IAeQueueElements.PROCESS_ID).longValue();
         int locPathId = getIntFromElement(aElement, IAeQueueElements.LOCATION_PATH_ID).intValue();
         int groupId = getIntFromElement(aElement, IAeQueueElements.GROUP_ID).intValue();
         Date deadline = getDateTimeFromElement(aElement, IAeQueueElements.DEADLINE);
         QName procName = getQNameFromElement(aElement, IAeQueueElements.PROCESS_NAME);
         int alarmId = getIntFromElement(aElement, IAeQueueElements.ALARM_ID).intValue();
         return new AeAlarmExt(procId, locPathId, groupId, alarmId, deadline, procName);
      }
      catch (Exception e)
      {
         throw new AeXMLDBException(e);
      }
   }
}
