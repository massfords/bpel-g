// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/work/factory/AeDefaultWorkManagerFactory.java,v 1.1 2007/07/31 20:54:25 KRoe Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.work.factory;

import commonj.work.WorkManager;

import java.util.Map;

import javax.naming.InitialContext;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.config.IAeEngineConfiguration;
import org.activebpel.rt.bpel.server.AeMessages;
import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.work.AeWorkManager;
import org.activebpel.work.input.AeDefaultInputMessageWorkManager;
import org.activebpel.work.input.IAeInputMessageWorkManager;

/**
 * Implements a work manager factory that looks up the CommonJ
 * <code>WorkManager</code> in JNDI and constructs an
 * {@link IAeInputMessageWorkManager} that delegates to the engine factory's
 * process work manager.
 */
public class AeDefaultWorkManagerFactory implements IAeWorkManagerFactory
{
   /** The CommonJ <code>WorkManager</code> for scheduling CommonJ <code>Work</code> items. */
   private WorkManager mWorkManager = null;

   /** <code>true</code> if and only if {@link #mWorkManager} is our internal <code>WorkManager</code> implementation. */
   private boolean mIsInternalWorkManager = false;

   /** The default input message work manager. */
   private final IAeInputMessageWorkManager mDefaultInputMessageWorkManager = new AeDefaultInputMessageWorkManager(); 

   /**
    * @see org.activebpel.work.factory.IAeWorkManagerFactory#init(java.util.Map)
    */
   public void init(Map aConfigMap) throws AeException
   {
      if (!AeUtil.isNullOrEmpty(aConfigMap))
      {
         String location = (String) aConfigMap.get(IAeEngineConfiguration.WM_JNDI_NAME_ENTRY);
         if (!AeUtil.isNullOrEmpty(location))
         {
            try
            {
               // Look up the work manager from the JNDI location specified in
               // engine config.
               InitialContext ic = new InitialContext();
               mWorkManager = (WorkManager) ic.lookup(location);
               AeException.info(AeMessages.format("AeDefaultWorkManagerFactory.INFO_WorkManagerLocation", location)); //$NON-NLS-1$
            }
            catch (Exception e)
            {
               AeException.info(AeMessages.format("AeDefaultWorkManagerFactory.ERROR_WorkManagerLocation", location)); //$NON-NLS-1$
            }
         }
      }

      // If the work manager location was not specified or was invalid, then
      // construct our internal implementation.
      if (mWorkManager == null)
      {
         AeException.info(AeMessages.getString("AeDefaultWorkManagerFactory.INFO_DefaultWorkManager")); //$NON-NLS-1$
         mWorkManager = new AeWorkManager();
         mIsInternalWorkManager = true;
      }
   }

   /**
    * @see org.activebpel.work.factory.IAeWorkManagerFactory#getWorkManager()
    */
   public WorkManager getWorkManager()
   {
      return mWorkManager;
   }

   /**
    * @see org.activebpel.work.factory.IAeWorkManagerFactory#isInternalWorkManager()
    */
   public boolean isInternalWorkManager()
   {
      return mIsInternalWorkManager;
   }

   /**
    * Overrides method to return an {@link IAeInputMessageWorkManager} that
    * delegates to {@link AeEngineFactory#schedule(long, commonj.work.Work)}.
    *
    * @see org.activebpel.work.factory.IAeWorkManagerFactory#getInputMessageWorkManager()
    */
   public IAeInputMessageWorkManager getInputMessageWorkManager()
   {
      return mDefaultInputMessageWorkManager;
   }
}
