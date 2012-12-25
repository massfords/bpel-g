//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/IAeDefVisitorFactory.java,v 1.10.4.1 2008/04/21 16:09:44 ppatruni Exp $
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
import org.activebpel.rt.bpel.def.validation.IAeValidationContext;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessEngineInternal;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.IAeDynamicScopeParent;
import org.activebpel.rt.bpel.impl.IAeProcessPlan;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.xml.def.IAePathSegmentBuilder;
import org.activebpel.rt.xml.def.visitors.IAeDefPathVisitor;

/**
 * Factory for the def visitors that have different implementations depending 
 * on the version of BPEL we're dealing with
 */
public interface IAeDefVisitorFactory
{
   /**
    * Creates the def path visitor. 
    */
   public IAeDefPathVisitor createDefPathVisitor();
   
   /**
    * Creates a visitor to use for getting the value to use for a def's segment of the location path
    */
   public IAePathSegmentBuilder createPathSegmentBuilder();
   
   /**
    * Creates a def to impl visitor for creating a new process
    * @param aPid
    * @param aEngine
    * @param aPlan
    */
   public IAeDefToImplVisitor createImplVisitor(long aPid, IAeBusinessProcessEngineInternal aEngine, IAeProcessPlan aPlan);
   
   /**
    * Creates a def to impl visitor for creating new impls for a running process. This is used for the dynamic impls created
    * for a parallel forEach, onEvent, and onAlarm in WS-BPEL 2.0
    * @param aProcess
    * @param aParent
    */
   public IAeDefToImplVisitor createImplVisitor(IAeBusinessProcessInternal aProcess, IAeDynamicScopeParent aParent);
   
   /**
    * Creates a visitor that handles the creation of def objects to model implicit variables created by some bpel objects.
    * One example is the serial and parallel forEach which manifest their counterName attribute as an unsignedInt variable
    * within their child scope. 
    */
   public IAeDefVisitor createImplicitVariableVisitor();
   
   /**
    * Creates a visitor that handles the setting of extension object in extension element, attribute and activity def objects.
    */
   public IAeDefVisitor createAssignExtensionVisitor();
   
   /**
    * Creates a def visitor that searches for property aliases usages and caches the alias on the def for fast access at runtime. 
    * @param aProvider
    * @param aExpressionLanguageFactory
    */
   public IAeDefVisitor createPropertyAliasInlineVisitor(IAeContextWSDLProvider aProvider, IAeExpressionLanguageFactory aExpressionLanguageFactory);
   
   /**
    * Creates a def visitor that identifies all of the resources (variables/partnerLinks) that an activity needs to lock with an exclusive or shared
    * lock based on whether the activity is nested within an isolated scope and the type of operation performed by the activity. 
    * @param aExpression
    */
   public IAeDefVisitor createResourceLockingVisitor(IAeExpressionLanguageFactory aExpression);
   
   /**
    * Creates a def visitor that validates the def objects against the rules for the appropriate BPEL version.
    * @param aContext
    */
   public IAeDefVisitor createValidationVisitor(IAeValidationContext aContext);

   /**
    * <p>
    * Creates a def visitor that adds the strategy hint to the copy operation
    * defs for &lt;from&gt; and &lt;to&gt; assigns.
    * </p>
    * <p>
    * This visitor should run after any implicit variables visitor since it
    * depends on the implicit variables having been created in order to validate
    * the copy operations.
    * </p>
    * @param aExpressionLanguageFactory
    */
   public IAeDefVisitor createCopyOperationStrategyVisitor(IAeExpressionLanguageFactory aExpressionLanguageFactory);
   
   /**
    * <p>
    * Creates a def visitor that adds the strategy hint to the wsio activities
    * </p>
    * <p>
    * This visitor should run after any implicit variables visitor since it
    * depends on the implicit variables having been created in order to determine
    * the type of the variable (messageType or element), if used.
    * </p>
    */
   public IAeDefVisitor createMessageDataStrategyVisitor();
   
   /**
    * Creates a def visitor that inlines all of the message parts information for 
    * the wsio activities.
    * @param aWSDLProvider - used to lookup the WSDL messages
    */
   public IAeDefMessagePartsMapVisitor createMessagePartsMapVisitor(IAeContextWSDLProvider aWSDLProvider);
   
   /**
    * Creates a def visitor that records the message exchange value for all of the create instances plus 
    * ensures that each of the "root" scopes provide an implicit message exchange value
    */
   public IAeDefVisitor createMessageExchangeVisitor();
   
}
 