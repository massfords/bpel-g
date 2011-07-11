//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeWSBPELDefVisitorFactory.java,v 1.9 2008/01/11 19:31:16 dvilaverde Exp $
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
import org.activebpel.rt.bpel.def.io.readers.def.AeWSBPELSpecStrategyMatcher;
import org.activebpel.rt.bpel.def.io.readers.def.IAeCopyOperationStrategyMatcher;
import org.activebpel.rt.bpel.def.validation.AeWSBPELDefToValidationVisitor;
import org.activebpel.rt.bpel.def.validation.IAeValidationContext;
import org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio.AeMessageDataStrategyVisitor;
import org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio.AeWSBPELMessageDataProducerStrategyMatcher;
import org.activebpel.rt.bpel.def.visitors.preprocess.strategies.wsio.IAeMessageDataStrategyMatcher;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeDynamicScopeParent;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;
import org.activebpel.rt.xml.def.io.IAeExtensionRegistry;

/**
 * Factory for creating def visitors for WS-BPEL 2.0 defs
 */
public class AeWSBPELDefVisitorFactory extends AeDefVisitorFactory implements IAeExtensionAwareDefVisitorFactory
{
   /** matcher for 2.0 from/to strategies */
   private static final IAeCopyOperationStrategyMatcher WSBPEL_STRATEGY_MATCHER = new AeWSBPELSpecStrategyMatcher();
   
   /** matcher for 2.0 invoke/reply message data producer strategies */
   private static final IAeMessageDataStrategyMatcher WSBPEL_MESSAGE_DATA_STRATEGY_MATCHER = new AeWSBPELMessageDataProducerStrategyMatcher();
   
   /** extension registry */
   private IAeExtensionRegistry mExtensionRegistry;
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createPathSegmentBuilder()
    */
   public IAePathSegmentBuilder createPathSegmentBuilder()
   {
      return new AeWSBPELDefPathSegmentVisitor();
   }
   
   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createImplVisitor(org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal, org.activebpel.rt.bpel.impl.IAeDynamicScopeParent)
    */
   public IAeDefToImplVisitor createImplVisitor(IAeBusinessProcessInternal aProcess, IAeDynamicScopeParent aParent)
   {
      return new AeDefToWSBPELImplVisitor(aProcess, aParent);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createImplVisitor(long, org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal, org.activebpel.rt.bpel.impl.IAeProcessPlan)
    */
   public IAeDefToImplVisitor createImplVisitor(long aPid, IAeBusinessProcessEngineInternal aEngine, IAeProcessPlan aPlan)
   {
      return new AeDefToWSBPELImplVisitor(aPid, aEngine, aPlan);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createImplicitVariableVisitor()
    */
   public IAeDefVisitor createImplicitVariableVisitor()
   {
      return new AeWSBPELImplicitVariableVisitor();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createPropertyAliasInlineVisitor(org.activebpel.rt.wsdl.IAeContextWSDLProvider, org.activebpel.rt.bpel.IAeExpressionLanguageFactory)
    */
   public IAeDefVisitor createPropertyAliasInlineVisitor(IAeContextWSDLProvider aProvider, IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      return new AeWSBPELInlinePropertyAliasVisitor(aProvider, aExpressionLanguageFactory);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createResourceLockingVisitor(org.activebpel.rt.bpel.IAeExpressionLanguageFactory)
    */
   public IAeDefVisitor createResourceLockingVisitor(IAeExpressionLanguageFactory aExpression)
   {
      return new AeWSBPELDefVariableUsageVisitor(aExpression);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createValidationVisitor(org.activebpel.rt.bpel.def.validation.IAeValidationContext)
    */
   public IAeDefVisitor createValidationVisitor(IAeValidationContext aContext)
   {
      return new AeWSBPELDefToValidationVisitor(aContext);
   }


   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createCopyOperationStrategyVisitor(org.activebpel.rt.bpel.IAeExpressionLanguageFactory)
    */
   public IAeDefVisitor createCopyOperationStrategyVisitor(IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      return new AeCopyOperationStrategyVisitor(WSBPEL_STRATEGY_MATCHER, aExpressionLanguageFactory);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createMessageDataStrategyVisitor()
    */
   public IAeDefVisitor createMessageDataStrategyVisitor()
   {
      return new AeMessageDataStrategyVisitor(WSBPEL_MESSAGE_DATA_STRATEGY_MATCHER);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createMessagePartsMapVisitor(org.activebpel.rt.wsdl.IAeContextWSDLProvider)
    */
   public IAeDefMessagePartsMapVisitor createMessagePartsMapVisitor(IAeContextWSDLProvider aWSDLProvider)
   {
      return new AeWSBPELDefMessagePartsMapVisitor(aWSDLProvider);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createMessageExchangeVisitor()
    */
   public IAeDefVisitor createMessageExchangeVisitor()
   {
      return new AeWSBPELMessageExchangeVisitor();
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitorFactory#createAssignExtensionVisitor()
    */
   public IAeDefVisitor createAssignExtensionVisitor()
   {
      return new AeWSBPELAssignExtObjVisitor(mExtensionRegistry);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeExtensionAwareDefVisitorFactory#setExtensionRegistry(org.activebpel.rt.xml.def.io.IAeExtensionRegistry)
    */
   public void setExtensionRegistry(IAeExtensionRegistry aExtensionRegistry)
   {
      mExtensionRegistry = aExtensionRegistry;
   }
}
 