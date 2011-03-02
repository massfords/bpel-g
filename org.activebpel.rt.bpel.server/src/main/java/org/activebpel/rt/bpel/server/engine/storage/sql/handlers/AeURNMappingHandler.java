//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeURNMappingHandler.java,v 1.2 2006/01/03 20:34:57 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.activebpel.rt.bpel.server.engine.storage.sql.AeDbUtils;
import org.activebpel.rt.bpel.server.engine.storage.sql.IAeURNColumns;
import org.apache.commons.dbutils.ResultSetHandler;


/**
 * Walks the result set building a map of urn to url values. 
 */
public class AeURNMappingHandler implements ResultSetHandler
{
   /**
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public Object handle(ResultSet aRs) throws SQLException
   {
      Map map = new HashMap();
      while(aRs.next())
      {
         String urn = aRs.getString(IAeURNColumns.COL_URN);
         String url = AeDbUtils.getString(aRs.getClob(IAeURNColumns.COL_URL));
         map.put(urn, url);
      }
      return map;
   }
}