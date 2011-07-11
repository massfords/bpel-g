//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/log/AeNullXMLDBPerformanceLogger.java,v 1.1 2007/08/17 00:40:55 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.log;

/**
 * A no-op logger.
 */
public class AeNullXMLDBPerformanceLogger implements IAeXMLDBPerformanceLogger
{
   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.log.IAeXMLDBPerformanceLogger#logXQueryTime(java.lang.String, long, long)
    */
   public void logXQueryTime(String aQuery, long aQueryTime, long aHandlerTime)
   {
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.log.IAeXMLDBPerformanceLogger#logInsertTime(java.lang.String, long)
    */
   public void logInsertTime(String aInsert, long aInsertTime)
   {
   }
}
