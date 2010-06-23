//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/AeAbstractXMLDBStorageConnectionProvider.java,v 1.2 2008/02/17 21:59:05 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb;


/**
 * A base class for a XMLDB storage connection delegate.
 */
public abstract class AeAbstractXMLDBStorageConnectionProvider extends AeAbstractXMLDBStorageProvider
{
   /** <code>true</code> to respect container-managed transaction boundaries. */
   private boolean mContainerManaged;
   /** The shared XMLDB <code>Connection</code>. */
   private IAeXMLDBConnection mSharedConnection;

   /**
    * Constructs a xmldb process state connection from the given config.
    * 
    * @param aConfig
    * @param aPrefix
    * @param aContainerManaged
    * @param aStorageImpl
    */
   public AeAbstractXMLDBStorageConnectionProvider(AeXMLDBConfig aConfig, String aPrefix,
         boolean aContainerManaged, IAeXMLDBStorageImpl aStorageImpl)
   {
      super(aConfig, aPrefix, aStorageImpl);
      setContainerManaged(aContainerManaged);
   }

   /**
    * Returns the XMLDB <code>IAeXMLDBConnection</code> to use.
    * 
    * @throws AeXMLDBException
    */
   protected IAeXMLDBConnection getConnection() throws AeXMLDBException
   {
      if (getSharedConnection() == null)
      {
         if (isContainerManaged())
         {
            // Container managed persistence is not supported.
            // TODO this needs to be supported (feature forthcoming)
            throw new UnsupportedOperationException();
         }
         else
         { 
            setSharedConnection(getTransactionManagerConnection(true));
         }
      }

      return getSharedConnection();
   }

   /**
    * Closes the connection.
    */
   public void close() throws AeXMLDBException
   {
      if (getSharedConnection() != null)
      {
         getSharedConnection().close();
         setSharedConnection(null);
      }
   }

   /**
    * Commits the transaction.
    */
   public void commit() throws AeXMLDBException
   {
      if (!isContainerManaged())
      {
         getConnection().commit();
      }
   }
   
   /**
    * Rolls back the transaction.
    */
   public void rollback() throws AeXMLDBException
   {
      if (!isContainerManaged())
      {
         getConnection().rollback();
      }
   }

   /**
    * @return Returns the containerManaged.
    */
   protected boolean isContainerManaged()
   {
      return mContainerManaged;
   }

   /**
    * @param aContainerManaged The containerManaged to set.
    */
   protected void setContainerManaged(boolean aContainerManaged)
   {
      mContainerManaged = aContainerManaged;
   }

   /**
    * @return Returns the sharedConnection.
    */
   protected IAeXMLDBConnection getSharedConnection()
   {
      return mSharedConnection;
   }

   /**
    * @param aSharedConnection The sharedConnection to set.
    */
   protected void setSharedConnection(IAeXMLDBConnection aSharedConnection)
   {
      mSharedConnection = aSharedConnection;
   }
}
