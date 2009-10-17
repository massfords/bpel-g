// $Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/def/visitors/AeInlinePropertyAliasVisitor.java,v 1.21 2008/01/25 21:01:18 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//               PROPRIETARY RIGHTS STATEMENT
// The contents of this file represent confidential information that is the
// proprietary property of Active Endpoints, Inc.  Viewing or use of
// this information is prohibited without the express written consent of
// Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
// is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.def.visitors;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.activebpel.rt.AeException;
import org.activebpel.rt.bpel.AeWSDLDefHelper;
import org.activebpel.rt.bpel.IAeExpressionLanguageFactory;
import org.activebpel.rt.bpel.def.AeBaseDef;
import org.activebpel.rt.bpel.def.AeCorrelationSetDef;
import org.activebpel.rt.bpel.def.AeProcessDef;
import org.activebpel.rt.bpel.def.AeVariableDef;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef;
import org.activebpel.rt.bpel.def.activity.AeActivityReplyDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWaitDef;
import org.activebpel.rt.bpel.def.activity.AeActivityWhileDef;
import org.activebpel.rt.bpel.def.activity.support.AeCorrelationDef;
import org.activebpel.rt.bpel.def.activity.support.AeElseIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeFromDef;
import org.activebpel.rt.bpel.def.activity.support.AeIfDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnEventDef;
import org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef;
import org.activebpel.rt.bpel.def.activity.support.AeSourceDef;
import org.activebpel.rt.bpel.def.activity.support.AeToDef;
import org.activebpel.rt.bpel.def.activity.support.AeVarDef;
import org.activebpel.rt.bpel.def.util.AeDefUtil;
import org.activebpel.rt.bpel.def.util.AeVariableProperty;
import org.activebpel.rt.expr.def.AeExpressionAnalyzerContext;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzerContext;
import org.activebpel.rt.message.AeMessagePartsMap;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.IAeProperty;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.activebpel.rt.xml.def.AeBaseDefNamespaceContext;

/**
 * Visits the def to inline usage of property alias's where possible. This will
 * reduce the overhead of having to search for these prop aliases at runtime.
 */
public class AeInlinePropertyAliasVisitor extends AeAbstractDefVisitor
{
   /** The WSDL provider set during visitor creation. */
   private IAeContextWSDLProvider mWSDLProvider;
   /** The process def that we're visiting */
   private AeProcessDef mProcessDef;
   /** The expression language factory. */
   private IAeExpressionLanguageFactory mExpressionLanguageFactory;

   /**
    * Constructor for the visitor
    * @param aProvider
    * @param aExpressionLanguageFactory
    */
   protected AeInlinePropertyAliasVisitor(IAeContextWSDLProvider aProvider, IAeExpressionLanguageFactory aExpressionLanguageFactory)
   {
      mWSDLProvider = aProvider;
      setExpressionLanguageFactory(aExpressionLanguageFactory);
      setTraversalVisitor(new AeTraversalVisitor(new AeDefTraverser(), this));
   }

   /**
    * Finds any propertyAliases needed to resolve the correlationSets used in either
    * the input and output variables of an invoke.
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityInvokeDef)
    */
   public void visit(AeActivityInvokeDef aDef)
   {
      for ( Iterator iter = aDef.getCorrelationList() ; iter.hasNext() ; )
      {
         AeCorrelationDef corrDef = (AeCorrelationDef)iter.next();
         AeCorrelationSetDef corrSetDef = AeDefUtil.findCorrSetByName( corrDef.getCorrelationSetName(), aDef );
         if ( corrDef.isRequestDataUsedForCorrelation())
            walkCorrelationSet( corrSetDef, aDef.getProducerMessagePartsMap());
         if ( corrDef.isResponseDataUsedForCorrelation())
            walkCorrelationSet( corrSetDef, aDef.getConsumerMessagePartsMap());
      }

      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReceiveDef)
    */
   public void visit(AeActivityReceiveDef aDef)
   {
      walkCorrelationIterator(aDef, aDef.getConsumerMessagePartsMap(), aDef.getCorrelationList());
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityReplyDef)
    */
   public void visit(AeActivityReplyDef aDef)
   {
      walkCorrelationIterator(aDef, aDef.getProducerMessagePartsMap(), aDef.getCorrelationList());
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnMessageDef)
    */
   public void visit(AeOnMessageDef aDef)
   {
      walkCorrelationIterator(aDef, aDef.getConsumerMessagePartsMap(), aDef.getCorrelationDefs());
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.AeAbstractDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnEventDef)
    */
   public void visit(AeOnEventDef aDef)
   {
      walkCorrelationIterator(aDef.getContext(), aDef.getConsumerMessagePartsMap(), aDef.getCorrelationDefs());
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.AeProcessDef)
    */
   public void visit(AeProcessDef aDef)
   {
      mProcessDef = aDef;
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeFromDef)
    */
   public void visit(AeFromDef aDef)
   {
      visitFromDef(aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeToDef)
    */
   public void visit(AeToDef aDef)
   {
      visitToDef(aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWaitDef)
    */
   public void visit(AeActivityWaitDef aDef)
   {
      extractPropAliasFromExpression(aDef.getForDef(), aDef);
      extractPropAliasFromExpression(aDef.getUntilDef(), aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.AeActivityWhileDef)
    */
   public void visit(AeActivityWhileDef aDef)
   {
      extractPropAliasFromExpression(aDef.getConditionDef(), aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeOnAlarmDef)
    */
   public void visit(AeOnAlarmDef aDef)
   {
      extractPropAliasFromExpression(aDef.getForDef(), aDef);
      extractPropAliasFromExpression(aDef.getUntilDef(), aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeSourceDef)
    */
   public void visit(AeSourceDef aDef)
   {
      extractPropAliasFromExpression(aDef.getTransitionConditionDef(), aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeElseIfDef)
    */
   public void visit(AeElseIfDef aDef)
   {
      extractPropAliasFromExpression(aDef.getConditionDef(), aDef);
      super.visit(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.def.visitors.IAeDefVisitor#visit(org.activebpel.rt.bpel.def.activity.support.AeIfDef)
    */
   public void visit(AeIfDef aDef)
   {
      visit((AeElseIfDef) aDef);
   }

   /**
    * Walks the iter of correlation usages looking for property aliases.
    * @param aDef The activity that's providing the context for this search
    * @param aMessagePartsMap The message parts map
    * @param aCorrelationIter Iter of correlation defs
    */
   protected void walkCorrelationIterator(AeBaseDef aDef, AeMessagePartsMap aMessagePartsMap, Iterator aCorrelationIter)
   {
      while ( aCorrelationIter.hasNext() )
      {
         AeCorrelationDef corrDef = (AeCorrelationDef)aCorrelationIter.next();
         AeCorrelationSetDef corrSetDef = AeDefUtil.findCorrSetByName( corrDef.getCorrelationSetName(), aDef );
         walkCorrelationSet( corrSetDef, aMessagePartsMap );
      }
   }

   /**
    * Finds all of the property aliases for the correlation set's properties and
    * the given message parts map.
    * @param aCorrSetDef
    * @param aMessagePartsMap
    */
   protected void walkCorrelationSet(AeCorrelationSetDef aCorrSetDef, AeMessagePartsMap aMessagePartsMap)
   {
      for ( Iterator iter = aCorrSetDef.getPropertiesList() ; iter.hasNext() ; )
      {
         QName propName = (QName)iter.next();
         cacheCorrelationPropertyAlias(aMessagePartsMap, propName);
      }
   }
   
   /**
    * Caches a property alias on the process def if it's found.
    * @param aVarDef
    * @param aPropName
    */
   protected void cachePropertyAlias(AeVariableDef aVarDef, QName aPropName)
   {
      int type;      
      QName typeName;
      
      if ( aVarDef.isMessageType() )
      {
         type = IAePropertyAlias.MESSAGE_TYPE;   
         typeName = aVarDef.getMessageType();
      }
      else if ( aVarDef.isElement() )
      {
         type = IAePropertyAlias.ELEMENT_TYPE;    
         typeName = aVarDef.getElement();
      }
      else if ( aVarDef.isType() )
      {
         type = IAePropertyAlias.TYPE;      
         typeName = aVarDef.getType();
      }
      else
         return;
         
      cachePropertyAlias(type, typeName, aPropName);
   }
   
   /**
    * Looks up a property alias for the message and caches it.
    * @param aMessagePartsMap
    * @param aPropName
    * @return true if cached.
    */
   protected boolean cacheCorrelationPropertyAlias(AeMessagePartsMap aMessagePartsMap, QName aPropName)
   {
      return cachePropertyAlias(IAePropertyAlias.MESSAGE_TYPE, aMessagePartsMap.getMessageType(), aPropName);
   }

   /**
    * Caches a property alias on the process def if it's found.
    *
    * @param aType
    * @param aTypeName
    * @param aPropName
    */
   protected boolean cachePropertyAlias(int aType, QName aTypeName, QName aPropName)
   {
      IAePropertyAlias alias = AeWSDLDefHelper.getPropertyAlias(getWSDLProvider(), aTypeName, aType, aPropName );
      
      if (alias != null)
      {
         mProcessDef.cachePropertyAlias(alias);
         IAeProperty prop = AeWSDLDefHelper.getProperty(getWSDLProvider(), alias.getPropertyName());
         // Check if property is null, i.e. this property alias is referencing a non-existent property in the WSDL.
         if ( prop != null )
         {
            mProcessDef.cachePropertyType(alias.getPropertyName(), prop.getTypeName());
         }
      }
      return alias != null;
   }
   
   /**
    * Getter for the wsdl provider.
    */
   protected IAeContextWSDLProvider getWSDLProvider()
   {
      return mWSDLProvider;
   }

   /**
    * Visits the assign var def in order to cache property aliases.
    *
    * @param aDef
    */
   protected void visitVarDef(AeVarDef aDef)
   {
      if (!AeUtil.isNullOrEmpty(aDef.getVariable()) && !AeUtil.isNullOrEmpty(aDef.getProperty()))
      {
         AeVariableDef varDef = AeDefUtil.getVariableByName(aDef.getVariable(), aDef);
         QName propName = aDef.getProperty();
         cachePropertyAlias(varDef, propName);
      }
   }

   /**
    * Attempts to cache a property alias if it's used in the variable assignment
    * @param aDef
    */
   protected void visitToDef(AeToDef aDef)
   {
      visitVarDef(aDef);

      // TODO (MF) Update AeToDef to be similar to AeFromDef
      // extractPropAliasFromExpression(aDef, aDef);
   }

   /**
    * Attempts to cache a property alias if it's used in the variable assignment
    * @param aDef
    */
   protected void visitFromDef(AeFromDef aDef)
   {
      visitVarDef(aDef);

      extractPropAliasFromExpression(aDef, aDef);
   }


   /**
    * Searches for bpws:getVariableProperty usage within the expression and attempts
    * to inline the property alias.
    * @param aExpressionDef
    * @param aDef
    */
   protected void extractPropAliasFromExpression(IAeExpressionDef aExpressionDef, AeBaseDef aDef)
   {
      if (aExpressionDef == null || AeUtil.isNullOrEmpty(aExpressionDef.getExpression()))
         return;

      try
      {
         String language = AeDefUtil.getExpressionLanguage(aExpressionDef, AeDefUtil.getProcessDef(aDef));
         IAeExpressionAnalyzer analyzer = getExpressionLanguageFactory().createExpressionAnalyzer(aExpressionDef.getBpelNamespace(), language);
         IAeExpressionAnalyzerContext ctx = new AeExpressionAnalyzerContext(new AeBaseDefNamespaceContext(aDef));

         for (Iterator iter = analyzer.getVarPropertyList(ctx, aExpressionDef.getExpression()).iterator(); iter.hasNext();)
         {
            AeVariableProperty varProp = (AeVariableProperty) iter.next();
            AeVariableDef varDef = AeDefUtil.getVariableByName(varProp.getVarName(), aDef);
            cachePropertyAlias(varDef, varProp.getProperty());
         }
      }
      catch (AeException e)
      {
         AeException.logError(e, e.getLocalizedMessage());
      }
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
