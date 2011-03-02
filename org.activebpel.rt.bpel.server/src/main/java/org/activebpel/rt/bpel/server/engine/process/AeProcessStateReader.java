// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/process/AeProcessStateReader.java,v 1.5 2006/11/01 17:08:31 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.process;

import java.util.Iterator;
import java.util.Set;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.storage.IAeProcessSnapshot;
import org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateConnection;
import org.activebpel.rt.bpel.server.engine.storage.IAeProcessStateStorage;
import org.w3c.dom.Document;

/**
 * Reads process state from persistent storage.
 */
public class AeProcessStateReader implements IAeProcessStateReader
{
   /** The process manager that owns this process state reader. */
   private final IAePersistentProcessManager mProcessManager;

   /**
    * Constructs the process state reader for the given process manager.
    *
    * @param aProcessManager
    */
   public AeProcessStateReader(IAePersistentProcessManager aProcessManager)
   {
      mProcessManager = aProcessManager;
   }

   /**
    * Returns the engine for this process state writer.
    */
   protected IAeBusinessProcessEngineInternal getEngine()
   {
      return getProcessManager().getEngine();
   }

   /**
    * Returns the process manager that owns this process state writer.
    */
   protected IAePersistentProcessManager getProcessManager()
   {
      return mProcessManager;
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.IAePersistentProcessManager#getStorage()
    */
   public IAeProcessStateStorage getStorage()
   {
      return getProcessManager().getStorage();
   }

   /**
    * @see org.activebpel.rt.bpel.server.engine.process.IAeProcessStateReader#readProcess(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public void readProcess(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      long processId = aProcess.getProcessId();
      IAeProcessStateConnection connection = getStorage().getConnection(processId, false);

      try
      {
         restoreState(aProcess, connection);

         // restoreState() shouldn't make any changes, but either commit or
         // rollback is required on the connection.
         connection.commit();
      }
      catch (AeBusinessProcessException bpe)
      {
         connection.rollback();
         throw bpe;
      }
      catch (RuntimeException re)
      {
         connection.rollback();
         throw re;
      }
      finally
      {
         getStorage().releaseConnection(connection);
      }
   }

   /**
    * Restores process state and variables from the specified storage.
    *
    * @param aProcess
    * @param aConnection
    */
   protected void restoreState(IAeBusinessProcess aProcess, IAeProcessStateConnection aConnection) throws AeBusinessProcessException
   {
      Document document = aConnection.getProcessDocument();
      if (document != null)
      {
         // Restore process state.
         aProcess.setProcessData(document);

         // Get a process snapshot.
         IAeProcessSnapshot snapshot = aProcess.getProcessSnapshot();

         // Iterate through all live variable location paths.
         for (Iterator i = snapshot.getVariableLocationPaths().iterator(); i.hasNext(); )
         {
            String locationPath = (String) i.next();
            int locationId = aProcess.getLocationId(locationPath);
            Set versionNumbers = snapshot.getVariableVersionNumbers(locationPath);

            // Iterate through all version numbers for this location path.
            for (Iterator j = versionNumbers.iterator(); j.hasNext(); )
            {
               int versionNumber = ((Number) j.next()).intValue();
               Document variableDocument = aConnection.getVariableDocument(locationId, versionNumber);

               if (variableDocument != null)
               {
                  snapshot.setVariableData(locationPath, versionNumber, variableDocument);
               }
            }
         }
      }
   }
}
