// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeXMLDBDelegatingConnection.java,v 1.1 2007/08/17 00:40:54 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.server.engine.storage.xmldb;

/**
 * Base class for delegating connections.
 */
public abstract class AeXMLDBDelegatingConnection implements IAeXMLDBConnection
{
   /** The connection to delegate to. */
   private IAeXMLDBConnection mDelegate;

   /**
    * C'tor.
    *
    * @param mDelegate
    */
   public AeXMLDBDelegatingConnection(IAeXMLDBConnection mDelegate)
   {
      setDelegate(mDelegate);
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection#close()
    */
   public void close()
   {
      getDelegate().close();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection#commit()
    */
   public void commit() throws AeXMLDBException
   {
      getDelegate().commit();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection#getNativeConnection()
    */
   public Object getNativeConnection()
   {
      return getDelegate().getNativeConnection();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBConnection#rollback()
    */
   public void rollback() throws AeXMLDBException
   {
      getDelegate().rollback();
   }

   /**
    * @return Returns the delegate.
    */
   protected IAeXMLDBConnection getDelegate()
   {
      return mDelegate;
   }

   /**
    * @param aDelegate the delegate to set
    */
   protected void setDelegate(IAeXMLDBConnection aDelegate)
   {
      mDelegate = aDelegate;
   }
}
