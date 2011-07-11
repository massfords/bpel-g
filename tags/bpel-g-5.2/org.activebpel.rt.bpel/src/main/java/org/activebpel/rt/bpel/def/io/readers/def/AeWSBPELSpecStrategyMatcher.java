//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/io/readers/def/AeWSBPELSpecStrategyMatcher.java,v 1.12 2008/01/25 21:01:19 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.io.readers.def; 

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeBPELConstants;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.IAeFromSpecExtension;
import org.activebpel.rt.bpel.def.activity.support.IAeToSpecExtension;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.expr.def.AeExpressionAnalyzerContext;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer.AeExpressionToSpecDetails;
import org.activebpel.rt.xml.def.AeBaseDefNamespaceContext;

/**
 * Impl of the strategy matcher that adds additional patterns for WS-BPEL 
 */
public class AeWSBPELSpecStrategyMatcher extends AeCommonSpecStrategyMatcher
{
   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeCommonSpecStrategyMatcher#initFromMap()
    */
   protected void initFromMap()
   {
      super.initFromMap();

      // simple|complex variable / property
      AeFromSpec fromSpec = new AeFromSpec();
      fromSpec = new AeFromSpec();
      fromSpec.set(AeFromSpec.VARIABLE_TYPE);
      fromSpec.set(AeFromSpec.PROPERTY);
      add(fromSpec, IAeFromStrategyKeys.KEY_FROM_PROPERTY_TYPE);
      
      // element variable / property
      fromSpec = new AeFromSpec();
      fromSpec.set(AeFromSpec.VARIABLE_ELEMENT);
      fromSpec.set(AeFromSpec.PROPERTY);
      add(fromSpec, IAeFromStrategyKeys.KEY_FROM_PROPERTY_ELEMENT);

      // variable / simple|complex type / query
      fromSpec = new AeFromSpec();
      fromSpec.set(AeFromSpec.VARIABLE_TYPE);
      fromSpec.set(AeFromSpec.QUERY);
      add(fromSpec, IAeFromStrategyKeys.KEY_FROM_VARIABLE_TYPE_QUERY);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeCommonSpecStrategyMatcher#initToMap()
    */
   protected void initToMap()
   {
      super.initToMap();

      // simple|complex variable / property
      AeToSpec toSpec = new AeToSpec();
      toSpec.set(AeToSpec.VARIABLE_TYPE);
      toSpec.set(AeToSpec.PROPERTY);
      add(toSpec, IAeToStrategyKeys.KEY_TO_PROPERTY_TYPE);
      
      // element variable / property
      toSpec = new AeToSpec();
      toSpec.set(AeToSpec.VARIABLE_ELEMENT);
      toSpec.set(AeToSpec.PROPERTY);
      add(toSpec, IAeToStrategyKeys.KEY_TO_PROPERTY_ELEMENT);

      // expression
      toSpec = new AeToSpec();
      toSpec.set(AeToSpec.EXPRESSION);
      add(toSpec, IAeToStrategyKeys.KEY_TO_EXPRESSION);

      // variable / simple|complex type / query
      toSpec = new AeToSpec();
      toSpec.set(AeToSpec.VARIABLE_TYPE);
      toSpec.set(AeToSpec.QUERY);
      add(toSpec, IAeToStrategyKeys.KEY_TO_VARIABLE_TYPE_QUERY);
   }

   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeCommonSpecStrategyMatcher#getStrategy(org.activebpel.rt.bpel.def.activity.support.AeToDef, org.activebpel.rt.bpel.def.AeVariableDef, org.activebpel.rt.bpel.IAeExpressionLanguageFactory)
    */
   public AeSpecStrategyKey getStrategy(AeToDef aToDef, AeVariableDef aVarDef, IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      AeSpecStrategyKey key;

      // Give extensions a crack at determining the to-spec strategy
      if (aToDef.getAdapterFromAttributes(IAeToSpecExtension.class) != null)
      {
         key = new AeSpecStrategyKey(IAeToStrategyNames.TO_EXTENSION);
      }
      else
      {
         key = super.getStrategy(aToDef, aVarDef, aExpressionLanguageFactory);
         if (key == IAeToStrategyKeys.KEY_TO_EXPRESSION)
         {
            key = getExpressionStrategy(aToDef, aExpressionLanguageFactory);
         }
      }

      return key;
   }
   
   
   /**
    * @see org.activebpel.rt.bpel.def.io.readers.def.AeCommonSpecStrategyMatcher#getStrategy(org.activebpel.rt.bpel.def.activity.support.AeFromDef, org.activebpel.rt.bpel.def.AeVariableDef)
    */
   public AeSpecStrategyKey getStrategy(AeFromDef aFromDef, AeVariableDef aVarDef)
   {
      AeSpecStrategyKey key;
      // Given extensions a crack at creating a from-spec first.
      if (aFromDef.getAdapterFromAttributes(IAeFromSpecExtension.class) != null)
      {
         key = new AeSpecStrategyKey(IAeFromStrategyNames.FROM_EXTENSION);
      }
      else
      {
         key = super.getStrategy(aFromDef, aVarDef);
      }
      return key;
   }

   /**
    * Determines the proper strategy to use for an Expression to-spec.  This method will parse the expression
    * to determine its form.  The forms (in XPath) could be:
    * 
    * $varName  (simple type, element)  Note: $varName syntax is illegal if the variable is a message
    * $varName.partName  (message part)
    * $varName/query/to/data  (element with query)
    * $varName.partName/query/to/data  (message part with query)
    * 
    * @param aToDef
    * @param aExpressionLanguageFactory
    */
   protected AeSpecStrategyKey getExpressionStrategy(AeToDef aToDef, IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      AeSpecStrategyKey strategy = null;
      try
      {
         String expressionLanguage = AeDefUtil.getExpressionLanguage(aToDef);
         IAeExpressionAnalyzer analyzer = aExpressionLanguageFactory.createExpressionAnalyzer(IAeBPELConstants.WSBPEL_2_0_NAMESPACE_URI, expressionLanguage);
         IAeExpressionAnalyzerContext analyzerContext = new AeExpressionAnalyzerContext(new AeBaseDefNamespaceContext(aToDef));
         AeExpressionToSpecDetails toSpecDetails = analyzer.parseExpressionToSpec(analyzerContext, aToDef.getExpression());
         if (toSpecDetails != null)
            strategy = getStrategyFromExprToSpecDetails(aToDef, toSpecDetails, aExpressionLanguageFactory);
      }
      catch (AeException ex)
      {
         AeException.logError(ex);
      }

      if (strategy == null)
      {
         // Default to simply using the ToExpression strategy.  It won't work, but it will suppress
         // the "invalid to-spec" validation error.  We want to suppress that error because
         // the to-spec format is valid, but the query being used isn't.  We will catch and
         // report problems with the query itself elsewhere during validation (and prevent
         // deployment).
         strategy = IAeToStrategyKeys.KEY_TO_EXPRESSION;
      }
      
      return strategy;
   }
   
   /**
    * Creates a AeSpecStrategyKey from the result of parsing the to-spec expression into its
    * component parts (variable name, part name, query).
    * 
    * @param aToDef
    * @param aToSpecDetails
    * @param aExpressionLanguageFactory
    */
   protected AeSpecStrategyKey getStrategyFromExprToSpecDetails(AeToDef aToDef,
         AeExpressionToSpecDetails aToSpecDetails, IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      AeSpecStrategyKey key = null;
      
      String varName = aToSpecDetails.getVariableName();
      AeVariableDef varDef = (AeVariableDef) AeDefUtil.getVariableByName(varName, aToDef);
      String partName = aToSpecDetails.getPartName();
      String query = aToSpecDetails.getQuery();
      String queryLanguage = aToSpecDetails.getQueryLanguage();
      String strategyName = getStrategyName(varDef, partName, query);
      if (strategyName != null)
      {
         key = new AeExpressionSpecStrategyKey(strategyName, varName, partName, queryLanguage, query);
      }
      return key;
   }

   /**
    * Gets the proper strategy name given the variable, part name, and query.
    * 
    * @param aVariableDef
    * @param aPartName
    * @param aQuery
    */
   protected String getStrategyName(AeVariableDef aVariableDef, String aPartName, String aQuery)
   {
      String strategyName = null;
      if (aVariableDef == null)
      {
         strategyName = IAeToStrategyNames.TO_VARIABLE_MESSAGE;
         if (aPartName != null)
         {
            strategyName = IAeToStrategyNames.TO_VARIABLE_MESSAGE_PART;
            if (aQuery != null)
            {
               strategyName = IAeToStrategyNames.TO_VARIABLE_MESSAGE_PART_QUERY;
            }
         }
         else if (aQuery != null)
         {
            strategyName = IAeToStrategyNames.TO_VARIABLE_ELEMENT_QUERY;
         }
      }
      else if (aVariableDef.isMessageType())
      {
         // There MUST be a part name, or the format is invalid.
         strategyName = null;
         if (aPartName != null)
         {
            strategyName = IAeToStrategyNames.TO_VARIABLE_MESSAGE_PART;
            if (aQuery != null)
            {
               strategyName = IAeToStrategyNames.TO_VARIABLE_MESSAGE_PART_QUERY;
            }
         }
      }
      else if (aVariableDef.isElement())
      {
         strategyName = IAeToStrategyNames.TO_VARIABLE_ELEMENT;
         if (aQuery != null)
         {
            strategyName = IAeToStrategyNames.TO_VARIABLE_ELEMENT_QUERY;
         }
      }
      else if (aVariableDef.isType())
      {
         strategyName = IAeToStrategyNames.TO_VARIABLE_TYPE;
         if (aQuery != null)
         {
            strategyName = IAeToStrategyNames.TO_VARIABLE_TYPE_QUERY;
         }
      }
      return strategyName;
   }
}
