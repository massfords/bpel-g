//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/AeCopyOperationContext.java,v 1.20 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import java.util.Set;

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.function.AeFunctionExecutionContext;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.impl.activity.assign.to.AeFromQueryVariableContext;
import org.activebpel.rt.bpel.impl.activity.assign.to.AeToQueryVariableContext;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionTypeConverter;
import org.activebpel.rt.bpel.impl.expr.xpath.AeXPathExpressionTypeConverter;
import org.activebpel.rt.bpel.impl.expr.xpath.AeXPathFunctionContext;
import org.activebpel.rt.bpel.impl.expr.xpath.AeXPathNamespaceContext;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.activebpel.rt.xml.schema.AeTypeMapping;
import org.jaxen.FunctionContext;
import org.jaxen.NamespaceContext;
import org.jaxen.VariableContext;

/**
 * The context for the copy operation.
 */
public class AeCopyOperationContext implements IAeCopyOperationContext
{
   /** The base object we will be delegating through */
   protected AeAbstractBpelObject mContextBase;
   
   /**
    * Constructor
    * @param aContextBase
    */
   public AeCopyOperationContext(AeAbstractBpelObject aContextBase)
   {
      mContextBase = aContextBase;
   }
   
   /**
    * Returns the base context object we are delegating through.
    */
   protected AeAbstractBpelObject getContextBase()
   {
      return mContextBase;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getTypeMapping()
    */
   public AeTypeMapping getTypeMapping()
   {
      return getContextBase().getProcess().getEngine().getTypeMapping();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getVariable(java.lang.String)
    */
   public IAeVariable getVariable(String aName)
   {
      return getContextBase().findVariable(aName);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getVariableForUpdate(java.lang.String, java.lang.String)
    */
   public IAeVariable getVariableForUpdate(String aName, String aPartName)
   {
      // Base implementation does not need to maintain rollback history
      IAeVariable variable = getVariable(aName);
      variable.initializeForAssign(aPartName);
      return variable;
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#executeExpression(org.activebpel.rt.bpel.def.IAeExpressionDef)
    */
   public Object executeExpression(IAeExpressionDef aDef) throws AeBusinessProcessException
   {
      return getContextBase().executeExpression(aDef);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getPartnerLink(java.lang.String)
    */
   public AePartnerLink getPartnerLink(String aName)
   {
      return getContextBase().findPartnerLink(aName);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getPartnerLinkForUpdate(java.lang.String)
    */
   public AePartnerLink getPartnerLinkForUpdate(String aName)
   {
      // Base implementation does not need to maintain rollback history
      return getPartnerLink(aName);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getPropertyAlias(int, javax.xml.namespace.QName, javax.xml.namespace.QName)
    */
   public IAePropertyAlias getPropertyAlias(int aPropertyAliasType, QName aName, QName aPropName) throws AeBusinessProcessException
   {
      return getContextBase().getProcess().getProcessDefinition().getPropertyAliasOrThrow(aPropertyAliasType, aName, aPropName);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#executeQuery(java.lang.String, java.lang.Object, boolean)
    */
   public Object executeQuery(String aQueryExpression, Object aContext, boolean aToQuery)
         throws AeBusinessProcessException
   {
      return executeQuery(aQueryExpression, aContext, this, aToQuery);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#executeQuery(java.lang.String, java.lang.Object, org.activebpel.rt.xml.IAeNamespaceContext, boolean)
    */
   public Object executeQuery(String aQueryExpression, Object aContext,
         IAeNamespaceContext aNamespaceContext, boolean aToQuery) throws AeBusinessProcessException
   {
      AeXPathHelper xpathHelper = AeXPathHelper.getInstance(getBPELNamespace());
      IAeFunctionExecutionContext functionExecCtx = createFunctionExecutionContext(aContext, xpathHelper);
      FunctionContext functionContext = new AeXPathFunctionContext(functionExecCtx);
      VariableContext variableContext = aToQuery ? new AeToQueryVariableContext(this) : new AeFromQueryVariableContext(this);
      NamespaceContext namespaceContext = new AeXPathNamespaceContext(aNamespaceContext);
      try
      {
         return xpathHelper.executeXPathExpression(aQueryExpression, aContext, functionContext,
               variableContext, namespaceContext);
      }
      catch (AeExpressionException ex)
      {
         throw ex.getWrappedException();
      }
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#isEmptyQuerySelectionAllowed()
    */
   public boolean isEmptyQuerySelectionAllowed()
   {
      return getContextBase().getProcess().isDisableSelectionFailure();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#isCreateXPathAllowed()
    */
   public boolean isCreateXPathAllowed()
   {
      return getContextBase().getProcess().isAllowCreateTargetXPath();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#createFunctionExecutionContext(java.lang.Object, org.activebpel.rt.bpel.xpath.AeXPathHelper)
    */
   public IAeFunctionExecutionContext createFunctionExecutionContext(Object aContext, AeXPathHelper aXPathHelper)
   {
      IAeExpressionTypeConverter typeConverter = new AeXPathExpressionTypeConverter(aXPathHelper);
      return new AeFunctionExecutionContext(getContextBase(), aContext, getContextBase(), getContextBase(), typeConverter);
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getBPELNamespace()
    */
   public String getBPELNamespace()
   {
      return getContextBase().getBPELNamespace();
   }

   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolvePrefixToNamespace(java.lang.String)
    */
   public String resolvePrefixToNamespace(String aPrefix)
   {
      return getContextBase().resolvePrefixToNamespace(aPrefix);
   }

   /**
    * @see org.activebpel.rt.xml.IAeNamespaceContext#resolveNamespaceToPrefixes(java.lang.String)
    */
   public Set resolveNamespaceToPrefixes(String aNamespace)
   {
      return getContextBase().resolveNamespaceToPrefixes(aNamespace);
   }
   
   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getContextWSDLProvider()
    */
   public IAeContextWSDLProvider getContextWSDLProvider()
   {
      return getContextBase().getProcess().getProcessPlan();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getProcess()
    */
   public IAeBusinessProcessInternal getProcess()
   {
      return getContextBase().getProcess();
   }

   /**
    * @see org.activebpel.rt.bpel.impl.activity.assign.IAeCopyOperationContext#getBpelObject()
    */
   public IAeBpelObject getBpelObject()
   {
      return getContextBase();
   }
}