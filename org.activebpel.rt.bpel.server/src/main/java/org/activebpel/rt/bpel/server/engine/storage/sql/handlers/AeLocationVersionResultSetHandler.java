// $Header$
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.sql.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.activebpel.rt.bpel.server.engine.storage.AeLocationVersionSet;
import org.activebpel.rt.bpel.server.engine.storage.IAeLocationVersionSet;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * A SQL result set handler that returns a set of location id/version number tuples.
 */
public class AeLocationVersionResultSetHandler implements ResultSetHandler
{
   /**
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public Object handle(ResultSet rs) throws SQLException
   {
      IAeLocationVersionSet set = new AeLocationVersionSet();

      while (rs.next())
      {
         long locationId = rs.getLong(1);

         if (rs.wasNull())
         {
            continue;
         }

         int versionNumber = rs.getInt(2);

         if (rs.wasNull())
         {
            continue;
         }

         set.add(locationId, versionNumber);
      }

      return set;
   }
}
