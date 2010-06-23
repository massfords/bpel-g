// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBDebugConnection.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import org.activebpel.rt.AeException;


/**
 * Debug connection - used in development to find connections that
 * aren't being closed properly.
 */
public class AeXMLDBDebugConnection extends AeXMLDBDelegatingConnection
{
   /** Number of open connections found when this object is garbage collected. */
   private static int sOpenConnCount = 0;
   /** Indicates if the connection was closed. */
   private boolean mClosedByClient = false;

   /**
    * C'tor.
    *
    * @param aConnection
    */
   public AeXMLDBDebugConnection(IAeXMLDBConnection aConnection)
   {
      super(aConnection);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBDelegatingConnection#close()
    */
   public void close()
   {
      setClosedByClient(true);
      super.close();
   }

   /**
    * @return Returns the closedByClient.
    */
   protected boolean isClosedByClient()
   {
      return mClosedByClient;
   }

   /**
    * @param aClosedByClient The closedByClient to set.
    */
   protected void setClosedByClient(boolean aClosedByClient)
   {
      mClosedByClient = aClosedByClient;
   }

   /**
    * @see java.lang.Object#finalize()
    */
   protected void finalize() throws Throwable
   {
      if (!isClosedByClient())
      {
         sOpenConnCount++;
         AeException
               .logWarning("*** XMLDB CONN FINALIZE HAS OPEN CONNECTION *** TotalOpen=" + sOpenConnCount); //$NON-NLS-1$
      }
   }
}
