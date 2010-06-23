// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/engine/recovery/AeRecoveryEngineFactory.java,v 1.7 2008/03/28 01:46:19 mford Exp $
/////////////////////////////////////////////////////////////////////////////
// PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2005 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.engine.recovery;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;

/**
 * Implements factory object that constructs a recovery engine along with the
 * recovery engine's managers.
 */
public class AeRecoveryEngineFactory
{
   /** Singleton instance. */
   private static AeRecoveryEngineFactory sInstance = new AeRecoveryEngineFactory();

   /**
    * Private constructor for singleton instance.
    */
   private AeRecoveryEngineFactory()
   {
   }

   /**
    * Returns singleton instance.
    */
   public static AeRecoveryEngineFactory getInstance()
   {
      return sInstance;
   }

   /**
    * Constructs a recovery engine.
    */
   public IAeRecoveryEngine newRecoveryEngine()
   {
      return newRecoveryEngine(AeEngineFactory.getEngine());
   }

   /**
    * Constructs a recovery engine using the engine configuration and partner
    * link strategy from the given engine.
    */
   public IAeRecoveryEngine newRecoveryEngine(IAeBusinessProcessEngineInternal aBaseEngine)
   {
      Map customManagers = getCustomManagers(aBaseEngine);
      
      return new AeRecoveryEngine(
         aBaseEngine.getEngineConfiguration(),
         new AeRecoveryQueueManager(),
         new AeRecoveryProcessManager(),
         new AeRecoveryLockManager(),
         aBaseEngine.getAttachmentManager(),
         aBaseEngine.getPartnerLinkStrategy(),
         new AeRecoveryCoordinationManager(aBaseEngine.getCoordinationManager()),
         aBaseEngine.getTransmissionTracker(),
         customManagers,
         aBaseEngine.getEngineId()
      );
   }

   /**
    * Gets the custom managers from the engine.
    * @param aBaseEngine
    */
   private Map getCustomManagers(IAeBusinessProcessEngineInternal aBaseEngine)
   {
      Map customManagers = new HashMap();
      for (Iterator iter = aBaseEngine.getCustomManagerNames(); iter.hasNext();)
      {
         String mgrName = (String) iter.next();
         customManagers.put(mgrName, aBaseEngine.getCustomManager(mgrName));
      }
      return customManagers;
   }
}
