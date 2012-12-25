//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeGetVariableDataFunction.java,v 1.10 2008/02/17 21:37:09 mford Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function;

import java.util.List;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.impl.AeBpelException;
import org.activebpel.rt.bpel.impl.AeSelectionFailureException;
import org.activebpel.rt.bpel.impl.AeUninitializedVariableException;
import org.activebpel.rt.bpel.impl.expr.AeExpressionException;
import org.activebpel.rt.bpel.impl.expr.xpath.AeXPathFunctionContext;
import org.activebpel.rt.bpel.impl.expr.xpath.AeXPathNamespaceContext;
import org.activebpel.rt.bpel.xpath.AeXPathHelper;


/**
 * Class representing the function used by expression evaluators to handle 
 * the BPEL getVariableData() function call.
 * 
 * Notes:  
 *   1) This is a BPWS implementation of getVariableData().  
 *   2) It should throw a selectionFailureFault if the 3rd param does not evaluate to a 
 *      single node (and if the allowEmptySelection option is off).
 */
public class AeGetVariableDataFunction extends AeAbstractBpelFunction implements IAeFunction
{
   public static final String FUNCTION_NAME = "getVariableData";//$NON-NLS-1$
   // error message constants 
   private static final String INVALID_ARGS            = AeMessages.getString( "AeAbstractBpelObject.ERROR_27" ); //$NON-NLS-1$
   private static final String ERROR_EVALUATING_QUERY  = AeMessages.getString( "AeAbstractBpelObject.ERROR_28" ); //$NON-NLS-1$

   /**
    * Constructor.
    */
   public AeGetVariableDataFunction()
   {
      super(FUNCTION_NAME);
   }
   
   /**
    * Execution of XPath function. 
    * @see org.jaxen.Function#call(org.jaxen.Context, java.util.List)
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
   {
      String bpelNamespace = aContext.getBpelNamespace();
      Object result = null;
      
      // Validate that we have the proper number of arguments
      int numArgs = aArgs.size();
      if (numArgs < 1 || numArgs > 3)
         throw new AeFunctionCallException( INVALID_ARGS );
      
      // Get the variable object from the first function argument
      String variableName = getStringArg(aArgs,0);
      IAeVariable variable = getVariable(aContext.getAbstractBpelObject(), variableName);
      if (variable != null)
      {
         try
         {
            if (variable.hasData())
            {
               // Is the variable a message variable?
               if (variable.isMessageType())
               {
                  result = getMessageData(aContext, aArgs, variable);
               }
               // Is the variable a simple type?
               else if (variable.isType())
               {
                  result = getTypeData(aContext, aArgs, variable);
               }
               // Must be a element type
               else
               {
                  result = getElementData(aContext, aArgs, variable);
               }
            }
            // Then it must be an uninitialized variable.
            else
            {
               throwUninitializedVariableFault(aContext, variableName);
            }
         }
         catch (AeBpelException ex)
         {
            throw new AeExpressionException(ex);
         }
      }

      // Make sure the result is not null (this is probably unnecessary)
      if (result == null)
         throw new AeFunctionCallException(ERROR_EVALUATING_QUERY, new NullPointerException());

      // If the result is a list it must return exactly one node
      if (result instanceof List)
      {
         // ****************************************************************************************
         // Note: This check will turn off selectionFailures for both empty selection AND 
         // multi-selection.  That's probably not what we want, but we let it slide (legacy issue).
         // ****************************************************************************************
         
         // if selection failure is turned off then don't test for error
         if( !isSelectionFailureDisabled( aContext.getAbstractBpelObject()) )
         {
            // Make sure we have selected only one node, otherwise create the appropriate fault
            List list = (List) result;
            if (list.isEmpty() || list.size() > 1)
            {
               throw new AeExpressionException(new AeSelectionFailureException(bpelNamespace, list.size()));
            }
         }
      }

      return result;
   }
   
   /**
    * Gets the data from the message.
    * 
    * @param aContext
    * @param aArgs
    * @throws AeFunctionCallException
    */
   protected Object getMessageData(IAeFunctionExecutionContext aContext, List aArgs, IAeVariable aVariable)
         throws AeFunctionCallException, AeBpelException
   {
      Object rval = null;

      int numArgs = aArgs.size();
      if (numArgs < 2)
         throw new AeFunctionCallException(AeMessages.format("AeGetVariableDataFunction.MESSAGE_VARIABLE_WITHOUT_PART_ERROR", aVariable.getName())); //$NON-NLS-1$
      
      String variableName = aVariable.getName();
      String partName = getStringArg(aArgs,1);

      // Get the data based on the message part name
      rval = aVariable.getMessageData().getData(partName);

      // The part must exist.
      if (rval == null)
         throwUninitializedVariableFault(aContext, variableName);
      
      // If we got data for the message part and we have a query arg
      // we now need to select the proper node.
      if (numArgs > 2)
      {
         String query = getStringArg(aArgs,2);
         Object xpathContext = rval;
         AeXPathFunctionContext functionContext = new AeXPathFunctionContext(aContext);
         AeXPathNamespaceContext nsContext = new AeXPathNamespaceContext(aContext.getAbstractBpelObject());
         
         AeXPathHelper xpathHelper = AeXPathHelper.getInstance(aContext.getBpelNamespace());
         rval = xpathHelper.executeXPathExpression(query, xpathContext, functionContext, nsContext);
      }
      
      return rval;
   }
   
   /**
    * Gets the data from the Element.
    * 
    * @param aContext
    * @param aArgs
    * @param aVariable
    * @throws AeBpelException
    * @throws AeFunctionCallException 
    */
   protected Object getElementData(IAeFunctionExecutionContext aContext, List aArgs, IAeVariable aVariable)
         throws AeBpelException, AeFunctionCallException
   {
      Object rval = aVariable.getElementData();

      // if the second argument is used it represents a query against the element data so evaluate
      int numArgs = aArgs.size();
      if (rval != null && numArgs > 1)
      {
         String query = getStringArg(aArgs,1);
         Object xpathContext = rval;
         AeXPathNamespaceContext nsContext = new AeXPathNamespaceContext(aContext.getAbstractBpelObject());
         AeXPathFunctionContext functionContext = new AeXPathFunctionContext(aContext);

         AeXPathHelper xpathHelper = AeXPathHelper.getInstance(aContext.getBpelNamespace());
         rval = xpathHelper.executeXPathExpression(query, xpathContext, functionContext, nsContext);
      }
      
      return rval;
   }
   
   /**
    * Gets the data from the variable when the variable is a type.
    * 
    * @param aContext
    * @param aArgs
    * @param aVariable
    */
   protected Object getTypeData(IAeFunctionExecutionContext aContext, List aArgs, IAeVariable aVariable)
         throws AeUninitializedVariableException, AeFunctionCallException
   {
      if (aArgs.size() > 1)
         throw new AeFunctionCallException(AeMessages.format("AeGetVariableDataFunction.TOO_MANY_PARAMS_TO_GETVARDATA_ERROR", aVariable.getName())); //$NON-NLS-1$

      return aVariable.getTypeData();
   }

   /**
    * Throws an uninitialized variable exception for the given variable.
    * 
    * @param aContext
    * @param aVariableName
    */
   protected void throwUninitializedVariableFault(IAeFunctionExecutionContext aContext, String aVariableName) throws AeExpressionException
   {
      throw new AeExpressionException(
            new AeBpelException(
                  AeMessages.format("AeGetVariableDataFunction.VARIABLE_NOT_INITIALIZED_ERROR", aVariableName), aContext.getFaultFactory().getUninitializedVariable())); //$NON-NLS-1$
   }
}
