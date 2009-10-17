// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/validation/process/AeBPWSProcessValidator.java,v 1.6 2008/03/20 16:01:32 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2006 All rights reserved.
/////////////////////////////////////////////////////////////////////////////

package org.activebpel.rt.bpel.def.validation.process;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.activity.AeActivityBreakDef;
import org.activebpel.rt.bpel.def.activity.AeActivityContinueDef;
import org.activebpel.rt.bpel.def.activity.AeActivityForEachDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityScopeDef;
import org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef;
import org.activebpel.rt.bpel.def.validation.IAeValidationContext;
import org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor;
import org.activebpel.rt.bpel.def.visitors.AeDefTraverser;
import org.activebpel.rt.bpel.def.visitors.AeTraversalVisitor;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.xml.def.AeBaseXmlDef;
import org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor;

/**
 * A BPWS version of the process validator.
 */
public class AeBPWSProcessValidator extends AeProcessValidator
{
   /**
    * C'tor.
    * 
    * @param aValidationContext
    * @param aProcessDef
    */
   public AeBPWSProcessValidator(IAeValidationContext aValidationContext, AeProcessDef aProcessDef)
   {
      super(aValidationContext, aProcessDef);
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.validation.process.AeProcessValidator#validate()
    */
   public void validate()
   {
      super.validate();
      
      if (getProcessDef().hasCompensationHandlerActivity() && !getProcessDef().getEnableInstanceCompensation())
      {
         getReporter().reportProblem( BPWS_COMPENSATION_HANDLER_NOT_ENABLED_CODE,
               WARN_COMPENSATION_HANDLER_NOT_ENABLED, null, getProcessDef() );
      }

      AeBPWSExtensionsVisitor visitor = new AeBPWSExtensionsVisitor();
      getProcessDef().accept(visitor);
      if (visitor.isExtensionActivitiesUsed())
      {
         getReporter().reportProblem(BPWS_BPEL_1_1_EXT_ACTIVITY_USED_CODE,
               AeMessages.getString("AeBPWSProcessValidator.BPEL11ExtensionActivitiesWarning"), null, //$NON-NLS-1$
               getDefinition());
      }
   }
   
   /**
    * A visitor that finds Ae extensions to BPEL 1.1.
    */
   protected class AeBPWSExtensionsVisitor extends AeAbstractDefVisitor
   {
      /**
       * Keeps traversing the def until we find an extension.
       */
      protected class AeTraverseWhileNotFound extends AeDefTraverser
      {
         /**
          * @see org.activebpel.rt.bpel.def.visitors.AeDefTraverser#callAccept(org.activebpel.rt.xml.def.AeBaseXmlDef, org.activebpel.rt.xml.def.visitors.IAeBaseXmlDefVisitor)
          */
         protected void callAccept(AeBaseXmlDef aDef, IAeBaseXmlDefVisitor aVisitor)
         {
            if (!isExtensionActivitiesUsed())
               super.callAccept(aDef, aVisitor);
         }

      }

      /** Flag indicating whether extensions were found. */
      private boolean mExtensionActivitiesUsed;
      
      /**
       * Default c'tor.
       */
      public AeBPWSExtensionsVisitor()
      {
         setTraversalVisitor( new AeTraversalVisitor(new AeTraverseWhileNotFound(), this));
      }
      
      /**
       * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
       */
      public void visit(AeActivityReceiveDef aDef)
      {
         if (AeUtil.notNullOrEmpty(aDef.getMessageExchange()))
         {
            setExtensionActivitiesUsed(true);
         }
         super.visit(aDef);
      }
      
      /**
       * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
       */
      public void visit(AeActivityReplyDef aDef)
      {
         if (AeUtil.notNullOrEmpty(aDef.getMessageExchange()))
         {
            setExtensionActivitiesUsed(true);
         }
         super.visit(aDef);
      }
      
      /**
       * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityScopeDef)
       */
      public void visit(AeActivityScopeDef aDef)
      {
         if (aDef.getMessageExchangesDef() != null && aDef.getMessageExchangesDef().getMessageExchangeValues().size() > 0)
         {
            setExtensionActivitiesUsed(true);
         }
         super.visit(aDef);
      }
      
      /**
       * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityForEachDef)
       */
      public void visit(AeActivityForEachDef aDef)
      {
         setExtensionActivitiesUsed(true);
         super.visit(aDef);
      }
      
      /**
       * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityBreakDef)
       */
      public void visit(AeActivityBreakDef aDef)
      {
         setExtensionActivitiesUsed(true);
         super.visit(aDef);
      }
      
      /**
       * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityContinueDef)
       */
      public void visit(AeActivityContinueDef aDef)
      {
         setExtensionActivitiesUsed(true);
         super.visit(aDef);
      }
      
      /**
       * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivitySuspendDef)
       */
      public void visit(AeActivitySuspendDef aDef)
      {
         setExtensionActivitiesUsed(true);
         super.visit(aDef);
      }

      /**
       * @return Returns the extensionActivitiesUsed.
       */
      protected boolean isExtensionActivitiesUsed()
      {
         return mExtensionActivitiesUsed;
      }

      /**
       * @param aExtensionActivitiesUsed The extensionActivitiesUsed to set.
       */
      protected void setExtensionActivitiesUsed(boolean aExtensionActivitiesUsed)
      {
         mExtensionActivitiesUsed = aExtensionActivitiesUsed;
      }
   }
}
