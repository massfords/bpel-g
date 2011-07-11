//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeCopyOperationStrategyVisitor.java,v 1.3.4.1 2008/04/21 16:09:44 ppatruni Exp $
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
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeVarDef;
import org.activebpel.rt.bpel.def.io.readers.def.AeSpecStrategyKey;
import org.activebpel.rt.bpel.def.io.readers.def.IAeCopyOperationStrategyMatcher;
import org.activebpel.rt.bpel.def.io.readers.def.IAeFromStrategyNames;
import org.activebpel.rt.bpel.def.io.readers.def.IAeToStrategyNames;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.util.AeUtil;

/**
 * Visits the process def and adds the strategy hint to the copy operation defs:
 * &lt;from&gt; and &lt;to&gt;.
 */
public class AeCopyOperationStrategyVisitor extends AeAbstractDefVisitor implements IAeFromStrategyNames, IAeToStrategyNames
{
   /** used to match the def to a prescribed strategy */
   private IAeCopyOperationStrategyMatcher mStrategyMatcher;
   /** The expression language factory to use. */
   private IAeExpressionLanguageFactory mExpressionLanguageFactory;
   
   /**
    * Constructor.
    * 
    * @param aMatcher
    * @param aExpressionLanguageFactory
    */
   public AeCopyOperationStrategyVisitor(IAeCopyOperationStrategyMatcher aMatcher,
         IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      mStrategyMatcher = aMatcher;
      setExpressionLanguageFactory(aExpressionLanguageFactory);
      setTraversalVisitor( new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * Creates the appropriate impl object to model the &lt;from&gt;
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   public void visit(AeFromDef aDef)
   {
      AeVariableDef variableDef = findVariable(aDef);
      AeSpecStrategyKey strategy = mStrategyMatcher.getStrategy(aDef, variableDef);
      aDef.setStrategyKey(strategy);
   }
   
   /**
    * Finds the variable in scope.
    * 
    * @param aVarDef
    */
   protected AeVariableDef findVariable(AeVarDef aVarDef)
   {
      AeVariableDef def = null;
      
      // walk the parents and inspect each scope def to see if it declares this variable
      
      if (AeUtil.notNullOrEmpty(aVarDef.getVariable()))
      {
         def = AeDefUtil.getVariableByName(aVarDef.getVariable(), aVarDef);
      }
      
      return def;
   }
   
   /**
    * Creates the appropriate impl object to model the &lt;to&gt;
    * 
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
    */
   public void visit(AeToDef aDef)
   {
      AeVariableDef variableDef = findVariable(aDef);
      AeSpecStrategyKey strategy = mStrategyMatcher.getStrategy(aDef, variableDef, getExpressionLanguageFactory());
      aDef.setStrategyKey(strategy);
   }

   /**
    * @return Returns the expressionLanguageFactory.
    */
   protected IAeExpressionLanguageFactory getExpressionLanguageFactory()
   {
      return mExpressionLanguageFactory;
   }

   /**
    * @param aExpressionLanguageFactory The expressionLanguageFactory to set.
    */
   protected void setExpressionLanguageFactory(IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      mExpressionLanguageFactory = aExpressionLanguageFactory;
   }
}
