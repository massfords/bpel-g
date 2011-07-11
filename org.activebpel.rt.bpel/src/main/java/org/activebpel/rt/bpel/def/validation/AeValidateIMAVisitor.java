// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/AeValidateIMAVisitor.java,v 1.1 2008/03/15 22:13:09 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2008 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation;

import org.activebpel.rt.bpel.def.AeActivityDef;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AePartnerLinkDef;
import org.activebpel.rt.bpel.def.activity.AeActivityPickDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.visitors.AeAbstractEntryPointVisitor;

/**
 * Visitor to check the IMA of a Process exists for a given plink and operation
 */
public class AeValidateIMAVisitor extends AeAbstractEntryPointVisitor
{
   /** Operation name */
   private String mOperation;
   /** Partner link */
   private AePartnerLinkDef mPartnerLink;
   /** a flag which when set to true, indicates if an IMA was found */
   private boolean mIMAFoundFlag = false;

   /**
    *
    * @param aOperation
    * @param aPartnerLink
    */
   public AeValidateIMAVisitor(String aOperation, AePartnerLinkDef aPartnerLink)
   {
      mOperation = aOperation;
      mPartnerLink = aPartnerLink;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractEntryPointVisitor#processEntryPoint(org.activebpel.rt.bpel.def.activity.IAeReceiveActivityDef)
    */
   protected void processEntryPoint(IAeReceiveActivityDef aDef)
   {
      AeBaseDef plinkContextDef = (AeBaseDef) aDef;
      // If this is an onEvent, then the onEvent's child scope should be the
      // context for the partner link lookup (i.e. peek down to find it)
      if (aDef instanceof AeOnEventDef)
      {
         AeOnEventDef onEventDef = (AeOnEventDef) aDef;
         AeActivityDef onEventActivityDef = onEventDef.getActivityDef();
         if (onEventActivityDef instanceof AeActivityScopeDef)
         {
            plinkContextDef = onEventActivityDef;
         }
      }
      AePartnerLinkDef referencedPartnerLinkDef = AeDefUtil.findPartnerLinkDef(plinkContextDef, aDef.getPartnerLink());
      
      if( aDef.getOperation().equals(mOperation) && referencedPartnerLinkDef == mPartnerLink)
      {
         mIMAFoundFlag = true;
      }
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractEntryPointVisitor#accept(org.activebpel.rt.bpel.def.activity.AeActivityPickDef)
    */
   protected boolean accept(AeActivityPickDef aDef)
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractEntryPointVisitor#accept(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   protected boolean accept(AeActivityReceiveDef aDef)
   {
      return true;
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractEntryPointVisitor#accept(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   protected boolean accept(AeOnEventDef aDef)
   {
      return true;
   }

   /**
    * Returns true if this visitor found an IMA
    *
    * @return
    */
   public boolean foundIMA()
   {
      return mIMAFoundFlag;
   }
}
