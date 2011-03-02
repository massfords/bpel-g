//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.xmldb/src/org/activebpel/rt/bpel/server/engine/storage/xmldb/process/AeXMLDBLogStreamReader.java,v 1.1 2007/08/17 00:40:56 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.storage.xmldb.process;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeAbstractXMLDBStorage;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBConfig;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.AeXMLDBException;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.IAeXMLDBStorageImpl;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler;
import org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler;
import org.activebpel.rt.util.AeCloser;
import org.w3c.dom.Element;

/**
 * This class is responsible for putting a Reader facade on the process log for a given
 * process.  The process log resides in the DB broken up into several different doc instances
 * which must be read sequentially.
 * 
 * TODO (head) share code with SQL log stream reader
 */
public class AeXMLDBLogStreamReader extends Reader
{
   /** A XMLDB response handler that converts the response to a list of Longs. */
   protected static final IAeXMLDBResponseHandler LOG_ID_LIST_HANDLER = new AeXMLDBListResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBListResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement)
         {
            return getLongFromElement(aElement, IAeProcessElements.LOG_ID);
         }
      };

   /** A XMLDB response handler that converts the response to a Reader. */
   protected static final IAeXMLDBResponseHandler LOG_READER_HANDLER = new AeXMLDBSingleObjectResponseHandler()
      {
         /**
          * @see org.activebpel.rt.bpel.server.engine.storage.xmldb.handlers.AeXMLDBSingleObjectResponseHandler#handleElement(org.w3c.dom.Element)
          */
         protected Object handleElement(Element aElement) throws AeXMLDBException
         {
            String logStr = getStringFromElement(aElement, IAeProcessElements.PROCESS_LOG);
            return new StringReader(logStr);
         }
      };

   /** The log accessor. */
   private AeXMLDBLogAccessor mLogAccessor;
   /** Iterator over the list of IDs for all log doc instances in the DB for this process. */
   private Iterator mLogIDs;
   /** A Reader over the current log entry. */
   private Reader mCurrentReader;

   /**
    * Constructs the log reader from the xmldb config and the process id of the process whose
    * log we are accessing.
    * 
    * @param aConfig
    * @param aProcessId
    */
   public AeXMLDBLogStreamReader(AeXMLDBConfig aConfig, long aProcessId, IAeXMLDBStorageImpl aStorageImpl)
         throws AeXMLDBException
   {
      setLogAccessor(new AeXMLDBLogAccessor(aConfig, aProcessId, aStorageImpl));

      setLogIDs(getLogAccessor().getLogIdList().iterator());
   }

   /**
    * @see java.io.Reader#read(char[], int, int)
    */
   public int read(char[] aCbuf, int aOff, int aLen) throws IOException
   {
      int rval = -1;

      prepareReader();

      while (getCurrentReader() != null && (rval = getCurrentReader().read(aCbuf, aOff, aLen)) == -1)
      {
         closeCurrentReader();
         prepareReader();
      }

      return rval;
   }
   
   /**
    * Closes the current reader and sets it to null.
    */
   protected void closeCurrentReader()
   {
      AeCloser.close(getCurrentReader());
      setCurrentReader(null);
   }

   /**
    * Prepares the 'current' reader if necessary.
    * 
    * @throws IOException
    */
   protected void prepareReader() throws IOException
   {
      if (getCurrentReader() == null && getLogIDs().hasNext())
      {
         try
         {
            setCurrentReader(getLogAccessor().getReader((Long) getLogIDs().next()));
         }
         catch (AeXMLDBException ex)
         {
            throw new IOException(ex.getLocalizedMessage());
         }
      }
   }
   
   /**
    * @see java.io.Reader#close()
    */
   public void close() throws IOException
   {
      closeCurrentReader();
   }

   /**
    * @return Returns the logAccessor.
    */
   protected AeXMLDBLogAccessor getLogAccessor()
   {
      return mLogAccessor;
   }

   /**
    * @param aLogAccessor The logAccessor to set.
    */
   protected void setLogAccessor(AeXMLDBLogAccessor aLogAccessor)
   {
      mLogAccessor = aLogAccessor;
   }

   /**
    * @return Returns the logIDs.
    */
   protected Iterator getLogIDs()
   {
      return mLogIDs;
   }
   
   /**
    * @param aLogIDs The logIDs to set.
    */
   protected void setLogIDs(Iterator aLogIDs)
   {
      mLogIDs = aLogIDs;
   }
   
   /**
    * @return Returns the currentReader.
    */
   protected Reader getCurrentReader()
   {
      return mCurrentReader;
   }
   
   /**
    * @param aCurrentReader The currentReader to set.
    */
   protected void setCurrentReader(Reader aCurrentReader)
   {
      mCurrentReader = aCurrentReader;
   }


   /**
    * An internal accessor class that is necessary because we can't inherit from both Reader
    * AND abstract XMLDB storage.  If only Reader were an interface. . .
    */
   protected class AeXMLDBLogAccessor extends AeAbstractXMLDBStorage
   {
      /** The process ID of the process for which to access logs. */
      private Long mProcessId;

      /**
       * Constructs the accessor with the given xmldb config.
       * 
       * @param aConfig
       * @param aProcessId
       * @param aStorageImpl
       */
      public AeXMLDBLogAccessor(AeXMLDBConfig aConfig, long aProcessId, IAeXMLDBStorageImpl aStorageImpl)
      {
         super(aConfig, AeXMLDBProcessStateStorageProvider.CONFIG_PREFIX, aStorageImpl);
         setProcessId(new Long(aProcessId));
      }

      /**
       * Gets the list of the IDs for all of the process log instances in the
       * database for this process.
       * 
       * @throws AeXMLDBException
       */
      public List getLogIdList() throws AeXMLDBException
      {
         Object [] params = { getProcessId() };
         return (List) query(IAeProcessConfigKeys.GET_LOG_IDS, params, LOG_ID_LIST_HANDLER);
      }

      /**
       * 
       * @param aLogID
       * @throws AeXMLDBException
       */
      public Reader getReader(Long aLogID) throws AeXMLDBException
      {
         Object [] params = { aLogID };
         return (Reader) query(IAeProcessConfigKeys.GET_LOG, params, LOG_READER_HANDLER);
      }
      
      /**
       * @return Returns the processId.
       */
      protected Long getProcessId()
      {
         return mProcessId;
      }

      /**
       * @param aProcessId The processId to set.
       */
      protected void setProcessId(Long aProcessId)
      {
         mProcessId = aProcessId;
      }
   }
}
