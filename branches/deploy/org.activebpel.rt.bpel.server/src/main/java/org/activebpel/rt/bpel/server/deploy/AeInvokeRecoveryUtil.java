// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel.server/src/org/activebpel/rt/bpel/server/deploy/AeInvokeRecoveryUtil.java,v 1.2 2007/09/28 19:48:52 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2007 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.server.deploy;

import org.activebpel.rt.bpel.server.engine.AeEngineFactory;
import org.activebpel.rt.util.AeUtil;
import org.w3c.dom.Document;

/**
 * Utility class for invoke recovery.
 */
public class AeInvokeRecoveryUtil
{
   /**
    * Extracts the {@link AeInvokeRecoveryType} from the process deployment
    * descriptor (.pdd) document.
    *
    * @param aPddDoc
    */
   public static AeInvokeRecoveryType getInvokeRecoveryType(Document aPddDoc)
   {
      String suspendProcess = aPddDoc.getDocumentElement().getAttribute(IAePddXmlConstants.ATT_SUSPEND_PROCESS_ON_INVOKE_RECOVERY);
      AeInvokeRecoveryType result;

      if (AeUtil.isNullOrEmpty(suspendProcess))
      {
         result = AeInvokeRecoveryType.ENGINE;
      }
      else if (AeUtil.toBoolean(suspendProcess))
      {
         result = AeInvokeRecoveryType.SUSPEND;
      }
      else
      {
         result = AeInvokeRecoveryType.NORMAL;
      }

      return result;
   }

   /**
    * Returns <code>true</code> if and only if the given
    * {@link AeInvokeRecoveryType} requires that the process should be suspended
    * if there is an invoke pending during recovery.
    *
    * @param aInvokeRecoveryType
    */
   public static boolean isSuspendOnInvokeRecoveryEnabled(AeInvokeRecoveryType aInvokeRecoveryType)
   {
      boolean result;
      
      if (aInvokeRecoveryType == AeInvokeRecoveryType.ENGINE)
      {
         // Take the setting from the engine configuration.
         result = AeEngineFactory.getEngineConfig().isSuspendProcessOnInvokeRecovery();
      }
      else
      {
         result = aInvokeRecoveryType == AeInvokeRecoveryType.SUSPEND; 
      }
   
      return result;
   }
}
