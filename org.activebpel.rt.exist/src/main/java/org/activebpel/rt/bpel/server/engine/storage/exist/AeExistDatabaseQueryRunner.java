// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.exist/src/org/activebpel/rt/bpel/server/engine/storage/exist/AeExistDatabaseQueryRunner.java,v 1.1 2007/08/23 21:26:53 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.exist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBResponseHandler;
import org.activebpel.rt.xml.AeXMLParserBase;
import org.w3c.dom.Element;

/**
 * Queries a previously created eXist database.
 */
public class AeExistDatabaseQueryRunner
{
   /**
    * Main.
    * 
    * Arg1 - query to run
    * Arg2 - location/directory of the eXist database files
    * 
    * @param aArgs
    */
   public static void main(String [] aArgs)
   {
      String query = "/AeResourceRoot/AeMetaInfo"; //$NON-NLS-1$
      String dbLocation =  "C:\\AEDevelopment\\projects\\test.org.activebpel.tasks\\test_data\\engine-test\\_tmpExistDB"; //$NON-NLS-1$

      if (aArgs.length > 0)
      {
         query = aArgs[0];
         dbLocation = aArgs[1];
      }

      AeExistDataSource dataSource = null;
      try
      {
         Map map = new HashMap();
         map.put("URL", "xmldb:exist:///db"); //$NON-NLS-1$ //$NON-NLS-2$
         map.put("Collection", "ActiveBPEL_collection"); //$NON-NLS-1$ //$NON-NLS-2$
         map.put("Username", "admin"); //$NON-NLS-1$ //$NON-NLS-2$
         map.put("Password", ""); //$NON-NLS-1$ //$NON-NLS-2$
         map.put("Embedded", "true"); //$NON-NLS-1$ //$NON-NLS-2$
         map.put("InitEmbedded", "false"); //$NON-NLS-1$ //$NON-NLS-2$
         map.put("DBLocation", dbLocation); //$NON-NLS-1$
         dataSource = new AeExistDataSource(map);
         
         IAeExistConnection conn = (IAeExistConnection) dataSource.getNewConnection();
         List elements = (List) conn.xquery(query, AeXMLDBResponseHandler.ELEMENT_LIST_RESPONSE_HANDLER);
         for (Iterator iter = elements.iterator(); iter.hasNext(); )
         {
            Element elem = (Element) iter.next();
            System.out.println(AeXMLParserBase.documentToString(elem, true));
            System.out.println("==="); //$NON-NLS-1$
         }

         System.out.println("Result set count: " + elements.size()); //$NON-NLS-1$
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
      finally
      {
         if (dataSource != null)
            dataSource.destroy();
         System.out.println("Done. (DataSource destroyed)"); //$NON-NLS-1$
      }
   }
}
