//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeCopyStrategyFactory.java,v 1.13 2006/12/14 15:10:37 ewittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.impl.AeSelectionFailureException;

/**
 * Factory used to create IAeCopyStrategy instances. These instances are used to implement the logic of
 * the given copy operation.
 */
public class AeCopyStrategyFactory
{
   /** The strategy factory for bpws. */
   public static IAeCopyStrategyFactory mBPWSFactory = new AeBPWSCopyStrategyFactoryImpl();
   /** The strategy factory for ws-bpel. */
   public static IAeCopyStrategyFactory mWSBPELFactory = new AeWSBPELCopyStrategyFactoryImpl();
   
   /**
    * Gets the strategy factory for the given bpel namespace.
    * 
    * @param aBPELNamespace
    */
   public static IAeCopyStrategyFactory getFactory(String aBPELNamespace) throws AeBusinessProcessException
   {
      if (IAeBPELConstants.BPWS_NAMESPACE_URI.equals(aBPELNamespace))
      {
         return mBPWSFactory;
      }
      else if (IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI.equals(aBPELNamespace))
      {
         return mWSBPELFactory;
      }
      else
      {
         throw new AeBusinessProcessException(AeMessages.format("AeCopyStrategyFactory.MissingCopyStrategyFactoryError", aBPELNamespace)); //$NON-NLS-1$
      }
   }

   /**
    * Factory method for getting the strategy based on the data type
    *
    * @param aCopyOperation
    * @param aFromData
    * @param aToData
    * @throws AeSelectionFailureException
    */
   public static IAeCopyStrategy createStrategy(IAeCopyOperation aCopyOperation, Object aFromData,
         Object aToData) throws AeBusinessProcessException
   {
      return getFactory(aCopyOperation.getContext().getBPELNamespace()).createStrategy(aCopyOperation, aFromData, aToData);
   }
}
