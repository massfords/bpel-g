//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/AeAbstractServerEngine.java,v 1.3 2008/03/28 01:46:20 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine; 

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeBusinessProcess;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.coord.IAeCoordinating;
import org.activebpel.rt.bpel.coord.IAeCoordinationContext;
import org.activebpel.rt.bpel.impl.AeBusinessProcessEngine;
import org.activebpel.rt.bpel.impl.IAeAttachmentManager;
import org.activebpel.rt.bpel.impl.IAeLockManager;
import org.activebpel.rt.bpel.impl.IAeProcessCoordination;
import org.activebpel.rt.bpel.impl.IAeProcessManager;
import org.activebpel.rt.bpel.impl.IAeQueueManager;
import org.activebpel.rt.bpel.server.catalog.resource.AeResourceKey;
import org.activebpel.rt.bpel.server.coord.subprocess.AeServerProcessCoordination;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.wsio.receive.IAeMessageContext;

/**
 * Base class for engines that run within a server environment. 
 */
public abstract class AeAbstractServerEngine extends AeBusinessProcessEngine
{
   /**
    * Ctor
    * @param aEngineConfiguration
    * @param aQueueManager
    * @param aProcessManager
    * @param aLockManager
    * @param aAttachmentManager
    */
   public AeAbstractServerEngine(IAeEngineConfiguration aEngineConfiguration, IAeQueueManager aQueueManager, IAeProcessManager aProcessManager, IAeLockManager aLockManager, IAeAttachmentManager aAttachmentManager)
   {
      super(aEngineConfiguration, aQueueManager, aProcessManager, aLockManager,
            aAttachmentManager);
   }

   /**
    * Returns handler for responsible for process coordination operations.
    */
   public final IAeProcessCoordination getProcessCoordination()
   {
      if (mProcessCoordination == null)
      {
         mProcessCoordination = new AeServerProcessCoordination(this);
      }
      return mProcessCoordination;
   }

   /**
    * Returns the coordination context from the message context's business properties map.
    * @param aContext receive message context
    * @return coordination context if available or null otherwise.
    */
   protected final IAeCoordinationContext getCoordinationContext(IAeMessageContext aContext)
   {
      IAeCoordinationContext ctx = null;
      String coordId = (String)aContext.getBusinessProcessProperties().get(IAeCoordinating.WSCOORD_ID);
      if ( AeUtil.notNullOrEmpty(coordId) )
      {
         try
         {
            // For now, (subprocess req 112) use the context already created during the subprocess invoke.
            ctx = getCoordinationManager().getContext(coordId);
         }
         catch (Exception e)
         {
            // ignore.
         }
      }
      return ctx;
   }

   /**
    * Overrides method to notify coordination manager after removing the process from the process manager.
    * @see org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineCallback#processEnded(org.activebpel.rt.bpel.IAeBusinessProcess)
    */
   public final void processEnded(IAeBusinessProcess aProcess) throws AeBusinessProcessException
   {
      // call the base class - which removes the process.
      super.processEnded(aProcess);
      try
      {
         // Coordination notifications:
         // Callback on coordination manager iff process was participating
         // in one or more coordinated activities as a coordinator or as a participant.
         if (aProcess.isCoordinating())
         {
            getCoordinationManager().onProcessCompleted(aProcess.getProcessId(), aProcess.getFault(), aProcess.isNormalCompletion());
         }
      }
      catch(Throwable t)
      {
         AeBusinessProcessException bpe = new AeBusinessProcessException(t.getMessage(), t);
         throw bpe;
      }
   }

   /**
    * Overrides to pull resource from catalog
    * @see org.activebpel.rt.bpel.impl.AeBusinessProcessEngine#loadResourceInternal(java.lang.String, java.lang.String)
    */
   protected final Object loadResourceInternal(String aLocation, String aTypeURI) throws AeException
   {
      return AeEngineFactory.getCatalog().getResourceCache().getResource(new AeResourceKey(aLocation, aTypeURI));
   }
}
 