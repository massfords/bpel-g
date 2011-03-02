//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/attachment/AeAbstractAttachmentFunction.java,v 1.8 2008/02/14 22:06:32 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function.attachment; 

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.activebpel.rt.attachment.IAeAttachmentItem;
import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.bpel.impl.function.AeAbstractBpelFunction;
import org.activebpel.rt.bpel.impl.visitors.AeInScopeVariableFindingVisitor;

/**
 * Base class for attachment functions
 */
public abstract class AeAbstractAttachmentFunction extends AeAbstractBpelFunction
{
   /**
    * Ctor
    * @param aFunctionName
    */
   public AeAbstractAttachmentFunction(String aFunctionName)
   {
      super(aFunctionName);
   }

   /**
    * Gets the attachment item for the given variable and item number
    * @param aContext
    * @param aVariableName
    * @param aItemNumber
    * @throws AeFunctionCallException
    */
   protected IAeAttachmentItem getAttachment(IAeFunctionExecutionContext aContext, String aVariableName, int aItemNumber) throws AeFunctionCallException
   {
      IAeVariable variable = getVariable(aContext.getAbstractBpelObject(),  aVariableName);
      
      int offset = getItemIndex(aItemNumber);
      
      if ( offset >= variable.getAttachmentData().size() || offset < 0 )
      {
         // return the item number not the index  for human readability
         Object[] args = { getFunctionName(),  new Integer(offset+1), new Integer(offset), variable.getName() };
         throwFunctionException(INVALID_ATTACHMENT_INDEX, args);
      }

      Object itemObject = variable.getAttachmentData().get(offset);
     
      if ( itemObject == null )
         throwFunctionException(NULL_RESULT_ERROR, getFunctionName());
      
      if ( !(itemObject instanceof IAeAttachmentItem) )
         throwFunctionException(INVALID_ATTACHMENT_OBJECT_TYPE, getFunctionName());

      return (IAeAttachmentItem)itemObject;
      
     
   }
   
   /**
    * Gets the attachment index from the passed itemNumber with validation.
    * @param aItemNumber
    * @return itemIndex 
    * @throws AeFunctionCallException
    */
   protected int getItemIndex(int aItemNumber) throws AeFunctionCallException
   {
      // Req 160 requires itemNumbers to start from one, so the here it is converted to the index that starts from zero
      return aItemNumber - 1;
   }
   
   /**
    * Resolves the given String, which must be a space separated list
    * of variable names, to a List of IAeVariable instances.  Throws a
    * function exception if a variable is not found.
    * 
    * @param aContext
    * @param aVariableNames
    * @return collection of variables
    */
   protected Collection resolveVariables(IAeFunctionExecutionContext aContext, String aVariableNames) throws AeFunctionCallException
   {
      return resolveVariables(aContext, aVariableNames, null);
   }
   
   /**
    * Resolves the given String, which must be a space separated list
    * of variable names, to a List of IAeVariable instances.  Throws a
    * function exception if a variable is not found.
    * 
    * @param aContext
    * @param aVariableNames
    * @param aToVariableName
    * @return collection of variables
    */
   protected Collection resolveVariables(IAeFunctionExecutionContext aContext, String aVariableNames,
         String aToVariableName) throws AeFunctionCallException
   {
      List variables = new ArrayList();
      
      for (StringTokenizer tokenizer = new StringTokenizer(aVariableNames); tokenizer.hasMoreTokens(); )
      {
         String variableName = tokenizer.nextToken();
         // If * is one of the variable names on the list, then simply return
         // all of the variables in-scope.
         if ("*".equals(variableName)) //$NON-NLS-1$
            return resolveAllVariablesInScope(aContext, aToVariableName);
         IAeVariable variable = getVariable(aContext.getAbstractBpelObject(), variableName);
         if (variable == null)
            throwFunctionException(UNRESOLVED_VARIABLE, variableName);
         variables.add(variable);
      }
      
      return variables;
   }
   
   /**
    * Returns a list of all variables currently in-scope.
    * 
    * @param aContext
    * @throws AeFunctionCallException
    */
   protected Collection resolveAllVariablesInScope(IAeFunctionExecutionContext aContext,
         String aToVariableName) throws AeFunctionCallException
   {
      AeAbstractBpelObject bpelObj = aContext.getAbstractBpelObject();
      AeInScopeVariableFindingVisitor visitor = new AeInScopeVariableFindingVisitor(Collections.singleton(aToVariableName));
      try
      {
         bpelObj.accept(visitor);
      }
      catch (AeBusinessProcessException ex)
      {
         throw new AeFunctionCallException(ex);
      }
      
      return visitor.getVariables();
   }
}
 