//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeBPWSDefVisitorFactory.java,v 1.9 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors; 

import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.io.readers.def.AeCommonSpecStrategyMatcher;
import org.activebpel.rt.bpel.def.io.readers.def.IAeCopyOperationStrategyMatcher;
import org.activebpel.rt.bpel.def.validation.AeBPWSDefToValidationVisitor;
import org.activebpel.rt.bpel.def.validation.IAeValidationContext;
import org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio.AeBPWSMessageDataStrategyMatcher;
import org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio.AeMessageDataStrategyVisitor;
import org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio.IAeMessageDataStrategyMatcher;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeDynamicScopeParent;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;

/**
 * Factory for creating def visitors for BPEL4WS 1.1 defs  
 */
public class AeBPWSDefVisitorFactory extends AeDefVisitorFactory
{
   /** matcher for 1.1 from/to strategies */
   private static final IAeCopyOperationStrategyMatcher BPEL4WS_STRATEGY_MATCHER = new AeCommonSpecStrategyMatcher();
   
   /** matcher for 1.1 invoke/reply message data producer strategies */
   private static final IAeMessageDataStrategyMatcher BPEL4WS_MESSAGE_DATA_STRATEGY_MATCHER = new AeBPWSMessageDataStrategyMatcher();

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createPathSegmentBuilder()
    */
   public IAePathSegmentBuilder createPathSegmentBuilder()
   {
      return new AeBPWSDefPathSegmentVisitor();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createImplVisitor(org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal, org.activebpel.rt.bpel.impl.IAeDynamicScopeParent)
    */
   public IAeDefToImplVisitor createImplVisitor(IAeBusinessProcessInternal aProcess, IAeDynamicScopeParent aParent)
   {
      return new AeDefToBPWSImplVisitor(aProcess, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createImplVisitor(long, org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal, org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public IAeDefToImplVisitor createImplVisitor(long aPid, IAeBusinessProcessEngineInternal aEngine, IAeProcessPlan aPlan)
   {
      return new AeDefToBPWSImplVisitor(aPid, aEngine, aPlan);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createImplicitVariableVisitor()
    */
   public IAeDefVisitor createImplicitVariableVisitor()
   {
      return new AeImplicitVariableVisitor();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createPropertyAliasInlineVisitor(org.activebpel.rt.wsdl.IAeContextWSDLProvider, org.activebpel.rt.bpel.IAeExpressionLanguageFactory)
    */
   public IAeDefVisitor createPropertyAliasInlineVisitor(IAeContextWSDLProvider aProvider, IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      return new AeInlinePropertyAliasVisitor(aProvider, aExpressionLanguageFactory);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createResourceLockingVisitor(org.activebpel.rt.bpel.IAeExpressionLanguageFactory)
    */
   public IAeDefVisitor createResourceLockingVisitor(IAeExpressionLanguageFactory aExpression)
   {
      return new AeDefVariableUsageVisitor(aExpression);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createValidationVisitor(org.activebpel.rt.bpel.def.validation.IAeValidationContext)
    */
   public IAeDefVisitor createValidationVisitor(IAeValidationContext aContext)
   {
      return new AeBPWSDefToValidationVisitor(aContext);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createCopyOperationStrategyVisitor(org.activebpel.rt.bpel.IAeExpressionLanguageFactory)
    */
   public IAeDefVisitor createCopyOperationStrategyVisitor(IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      return new AeCopyOperationStrategyVisitor(BPEL4WS_STRATEGY_MATCHER, aExpressionLanguageFactory);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createMessageDataStrategyVisitor()
    */
   public IAeDefVisitor createMessageDataStrategyVisitor()
   {
      return new AeMessageDataStrategyVisitor(BPEL4WS_MESSAGE_DATA_STRATEGY_MATCHER);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createMessagePartsMapVisitor(org.activebpel.rt.wsdl.IAeContextWSDLProvider)
    */
   public IAeDefMessagePartsMapVisitor createMessagePartsMapVisitor(IAeContextWSDLProvider aWSDLProvider)
   {
      return new AeBPWSDefMessagePartsMapVisitor(aWSDLProvider);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createMessageExchangeVisitor()
    */
   public IAeDefVisitor createMessageExchangeVisitor()
   {
      // TODO (MF) rename this to a base class and create a BPWS version
      return new AeMessageExchangeVisitor();
   }

   /**
    * Overrides method to 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createAssignExtensionVisitor()
    */
   public IAeDefVisitor createAssignExtensionVisitor()
   {
      return new AeNoOpAssignExtObjVisitor();
   }
}
 