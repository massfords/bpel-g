// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/storage/sql/handlers/AeAttachmentItemResultSetHandler.java,v 1.1 2007/05/08 19:21:19 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.sql.handlers;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.activebpel.rt.bpel.server.engine.storage.attachment.AeAttachmentItemEntry;
import org.activebpel.rt.bpel.server.engine.storage.sql.AeDbUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.w3c.dom.Document;

/**
 * Implements a <code>ResultSetHandler</code> that returns the first row of a
 * <code>ResultSet</code> as a <code>AeAttachmentItemEntry</code>.
 */
public class AeAttachmentItemResultSetHandler implements ResultSetHandler
{
   /**
    * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
    */
   public  Object handle(ResultSet rs) throws SQLException
   {
      if (rs.next())
      {
         long attachmentGroupId = rs.getLong(1);
         long attachmentItemId = rs.getLong(2);

         Clob clob = rs.getClob(3);
         if (rs.wasNull())
         {
            clob = null;
         }

         Document document = (clob == null) ? null : AeDbUtils.getDocument(clob);
       
         return new AeAttachmentItemEntry(attachmentGroupId, attachmentItemId, document); 
      }

      return null;
   }
}
