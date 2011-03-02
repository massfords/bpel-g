//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/activity/assign/IAeCopyOperationContext.java,v 1.13 2008/01/11 19:31:16 dvilaverde Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2006 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.activity.assign; 

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.def.IAeExpressionDef;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AePartnerLink;
import org.activebpel.rt.bpel.impl.IAeBpelObject;
import org.activebpel.rt.bpel.impl.IAeBusinessProcessInternal;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;
import org.activebpel.rt.wsdl.IAeContextWSDLProvider;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;
import org.activebpel.rt.xml.IAeNamespaceContext;
import org.activebpel.rt.xml.schema.AeTypeMapping;

/**
 * The context for the copy operation.
 */
public interface IAeCopyOperationContext extends IAeNamespaceContext
{
   /**
    * Returns the type mapping helper for the engine for schema to java and back.
    */
   public AeTypeMapping getTypeMapping();
   
   /**
    * Getter for the variable
    * @param aName
    */
   public IAeVariable getVariable(String aName);
   
   /**
    * Getter for a variable that will be updated
    * @param aName - Name of the variable being updated
    * @param aPartName - Name of the part which will be assigned to or null if 
    *                    it't not a message variable
    */
   public IAeVariable getVariableForUpdate(String aName, String aPartName);
   
   /**
    * Executes the expression
    * @param aDef
    * @throws AeBusinessProcessException
    */
   public Object executeExpression(IAeExpressionDef aDef) throws AeBusinessProcessException;
   
   /**
    * Getter for the partner link
    * @param aName
    */
   public AePartnerLink getPartnerLink(String aName);
   
   /**
    * Getter for the partner link that will be updated
    * @param aName
    */
   public AePartnerLink getPartnerLinkForUpdate(String aName);
   
   /**
    * Getter for the property alias.
    * @param aPropertyAliasType
    * @param aName
    * @param aPropName
    * @throws AeBusinessProcessException
    */
   public IAePropertyAlias getPropertyAlias(int aPropertyAliasType, QName aName, QName aPropName) throws AeBusinessProcessException;
   
   /**
    * Executes the query using the provided context and the default namespace context
    * @param aQueryExpression
    * @param aContext
    * @param aToQuery
    * @throws AeBusinessProcessException
    */
   public Object executeQuery(String aQueryExpression, Object aContext, boolean aToQuery)
         throws AeBusinessProcessException;
   
   /**
    * Executes the query using the provided context and namespace context
    * @param aQueryExpression
    * @param aContext
    * @param aNamespaceContext
    * @param aToQuery
    * @throws AeBusinessProcessException
    */
   public Object executeQuery(String aQueryExpression, Object aContext,
         IAeNamespaceContext aNamespaceContext, boolean aToQuery) throws AeBusinessProcessException;

   /**
    * Returns true if empty query selections are allowed.
    */
   public boolean isEmptyQuerySelectionAllowed();
   
   /**
    * Returns true if the create xpath extension is enabled
    */
   public boolean isCreateXPathAllowed();
   
   /**
    * Creates a function context for use in executing functions within a XPath <strong>query</strong>.  Note
    * that this method is not used (and not to be used) when executing a BPEL expression, as it
    * assumes XPath.
    * 
    * @param aContext
    */
   public IAeFunctionExecutionContext createFunctionExecutionContext(Object aContext, AeXPathHelper aXPathHelper);
   
   /**
    * Gets the BPEL namespace
    */
   public String getBPELNamespace();
   
   
   /**
    * Getter for the context wsdl provider
    */
   public IAeContextWSDLProvider getContextWSDLProvider();
   
   /**
    * Getter for the process
    */
   public IAeBusinessProcessInternal getProcess();
   
   /**
    * Gets a reference to the enclosing BPEL object for this copy operation.
    */
   public IAeBpelObject getBpelObject();
   
}
