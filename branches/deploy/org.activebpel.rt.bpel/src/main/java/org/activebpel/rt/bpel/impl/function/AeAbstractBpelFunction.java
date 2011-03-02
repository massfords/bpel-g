//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeAbstractBpelFunction.java,v 1.13 2008/02/27 23:38:13 mford Exp $
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

import javax.xml.namespace.QName;

import org.activebpel.rt.bpel.AeBusinessProcessException;
import org.activebpel.rt.bpel.IAeVariable;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.AeFunctionExceptions;
import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.impl.AeAbstractBpelObject;
import org.activebpel.rt.util.AeUtil;
import org.activebpel.rt.wsdl.def.IAePropertyAlias;

/**
 * Base class for standard <code>IAeFunction</code> impls.
 */
public abstract class AeAbstractBpelFunction extends AeFunctionExceptions implements IAeFunction
{ 
   /** name of the function - used for reporting errors */
   private String mFunctionName;
   
   /**
    * Ctor
    * @param aFunctionName
    */
   protected AeAbstractBpelFunction(String aFunctionName)
   {
      setFunctionName(aFunctionName);
   }
   
   /**
    * Constructor.
    */
   protected AeAbstractBpelFunction()
   {
   }

   /**
    * Delegates the call to the parent.
    *
    * @see org.activebpel.rt.bpel.impl.IAeBpelObject#findVariable(java.lang.String)
    */
   public IAeVariable getVariable(AeAbstractBpelObject aBpelObj, String aName)
   {
      return aBpelObj.findVariable(aName);
   }

   /**
    * Gets the message property from the wsdl
    *
    * @param aBpelObj
    * @param aPropertyAliasType
    * @param aName
    * @param aPropName
    * @throws AeBusinessProcessException
    */
   protected IAePropertyAlias getPropertyAlias(AeAbstractBpelObject aBpelObj, int aPropertyAliasType, QName aName, QName aPropName) throws AeBusinessProcessException
   {
      return aBpelObj.getProcess().getProcessDefinition().getPropertyAliasOrThrow(aPropertyAliasType, aName, aPropName);
   }

   /**
    * Return true if <code>IAeMutableConfig</code> settings for engine
    * allow empty query selection.
    */
   protected boolean isSelectionFailureDisabled(AeAbstractBpelObject aBpelObj)
   {
      return aBpelObj.getProcess().isDisableSelectionFailure();
   }
   

   /**
    * @return the functionName
    */
   protected String getFunctionName()
   {
      return mFunctionName;
   }

   /**
    * @param aFunctionName the functionName to set
    */
   protected void setFunctionName(String aFunctionName)
   {
      mFunctionName = aFunctionName;
   }
   
   /**
    * Validates that an argument is a String
    * @param aArgs
    * @param aArgIndex
    *
    * @throws AeFunctionCallException 
    */
   public  String getStringArg(List aArgs, int aArgIndex) throws AeFunctionCallException
   {
      Object argObj = aArgs.get(aArgIndex);
      if (!(argObj instanceof String))
      {
         Object[] args = { getFunctionName(), new Integer(aArgIndex+1)};
         throwFunctionException(EXPECT_STRING_ARGUMENT, args);
      }
      return (String)aArgs.get(aArgIndex);
   }
   
  /**
   * Validates that an argument is a positive integer or the equivalent.
   * @param aArgs
   * @param aArgIndex
   *
   * @throws AeFunctionCallException
   */
   public int getPositiveIntArg(List aArgs, int aArgIndex) throws AeFunctionCallException
   {
      Object argObj = aArgs.get(aArgIndex);
     
      if (argObj instanceof Number)
         return ((Number)argObj).intValue();

      int number = AeUtil.parseInt(argObj.toString(),-1);
      if (number < 0)
      {
         Object[] args = { getFunctionName(), new Integer(aArgIndex+1)};
         throwFunctionException(EXPECT_POSITIVE_INT_ARGUMENT, args);
      }
      return number;
   }
   
   /**
    * Validates that an argument is a long or the equivalent.
    * @param aArgs
    * @param aArgIndex
    *
    * @throws AeFunctionCallException
    */
    public long getLongArg(List aArgs, int aArgIndex) throws AeFunctionCallException
    {
       Object argObj = aArgs.get(aArgIndex);
       
       if (argObj instanceof Number)
          return ((Number)argObj).longValue();
      
       long number = AeUtil.parseLong(argObj.toString(),-1);
       if (number < 0)
       {
          Object[] args = { getFunctionName(), new Integer(aArgIndex+1)};
          throwFunctionException(EXPECT_LONG_ARGUMENT, args);
       }
       return number;
    }

    /**
     * Validates that an argument is a boolean
     * @param aArgs
     * @param aArgIndex
     *
     * @throws AeFunctionCallException
     */
     public boolean getBooleanArg(List aArgs, int aArgIndex) throws AeFunctionCallException
     {
        Object argObj = aArgs.get(aArgIndex);
        
        if (argObj instanceof Boolean)
           return ((Boolean)argObj).booleanValue();
       
        return AeUtil.toBoolean(argObj.toString());
     }
}
