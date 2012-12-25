//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/support/AeWSBPELCorrelationSet.java,v 1.2 2006/10/05 21:15:31 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.support; 

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.impl.AeCorrelationViolationException;
import org.activebpel.rt.bpel.impl.activity.AeActivityScopeImpl;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.message.IAeMessageData;
import org.activebpel.rt.util.AeUtil;

/**
 * Extension of the correlation set impl object that implments the rules for initiating/validating
 * a correlation set for WS-BPEL 2.0 
 */
public class AeWSBPELCorrelationSet extends AeCorrelationSet
{

   /**
    * Ctor
    * @param aDef
    * @param aParent
    */
   public AeWSBPELCorrelationSet(AeCorrelationSetDef aDef, AeActivityScopeImpl aParent)
   {
      super(aDef, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.support.AeCorrelationSet#initiateOrValidate(org.activebpel.rt.message.IAeMessageData, org.activebpel.rt.message.AeMessagePartsMap, java.lang.String)
    */
   public void initiateOrValidate(IAeMessageData aMessageData, AeMessagePartsMap aMessagePartsMap, String aInitiateValue) throws AeBusinessProcessException
   {
      if (AeCorrelationDef.INITIATE_YES.equals(aInitiateValue))
      {
         if (isInitialized())
         {
            throw new AeCorrelationViolationException(getBPELNamespace(), AeCorrelationViolationException.ALREADY_INITIALIZED );
         }
         else
         {
            initiate(aMessageData, aMessagePartsMap);
         }
      }
      else if (AeCorrelationDef.INITIATE_NO.equals(aInitiateValue) || AeUtil.isNullOrEmpty(aInitiateValue))
      {
         // Note: the validate method will throw if the correlationSet is uninitialized
         validate(aMessageData, aMessagePartsMap);
      }
      else if (AeCorrelationDef.INITIATE_JOIN.equals(aInitiateValue))
      {
         if (isInitialized())
         {
            validate(aMessageData, aMessagePartsMap);
         }
         else
         {
            initiate(aMessageData, aMessagePartsMap);
         }
      }
   }
}
 