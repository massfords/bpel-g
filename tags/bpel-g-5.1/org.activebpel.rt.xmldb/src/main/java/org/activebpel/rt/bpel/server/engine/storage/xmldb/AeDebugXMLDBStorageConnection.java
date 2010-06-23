// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeDebugXMLDBStorageConnection.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb;

import org.activebpel.rt.bpel.server.engine.storage.AeStorageException;

/**
 * A version of the XMLDB DB connection that will throw an exception on 'close' if neither
 * commit nor rollback have been called.
 */
public class AeDebugXMLDBStorageConnection extends AeXMLDBStorageConnection
{
   /** Flag indicating if the connection is 'complete' (ie either commit or rollback has been called). */
   private boolean mComplete;

   /**
    * Default constructor.
    *
    * @param aConnection
    */
   protected AeDebugXMLDBStorageConnection(IAeXMLDBConnection aConnection)
   {
      super(aConnection);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBStorageConnection#close()
    */
   public void close()
   {
      super.close();

      if (!isComplete())
         throw new RuntimeException("ERROR: XMLDB connection was closed without first calling commit or rollback!"); //$NON-NLS-1$
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBStorageConnection#commit()
    */
   public void commit() throws AeStorageException
   {
      setComplete(true);
      super.commit();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBStorageConnection#rollback()
    */
   public void rollback() throws AeStorageException
   {
      setComplete(true);
      super.rollback();
   }

   /**
    * @return Returns the complete.
    */
   protected boolean isComplete()
   {
      return mComplete;
   }

   /**
    * @param aComplete The complete to set.
    */
   protected void setComplete(boolean aComplete)
   {
      mComplete = aComplete;
   }
}
