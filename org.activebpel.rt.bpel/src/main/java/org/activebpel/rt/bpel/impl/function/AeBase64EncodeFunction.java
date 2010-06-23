//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeBase64EncodeFunction.java,v 1.4 2008/02/15 17:42:50 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function; 

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.activebpel.rt.base64.Base64;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;

/**
 * Implements the abx:base64Encode(stringValue,[charSet]) custom function.
 * <p/>
 * <em>Parameters:</em>
 * <ul>
 * <li>stringValue string to be encoded</li>
 * <li>charSet optional named charset. When omitted, the default charset used is UTF-8</li>
 * </ul>
 */
public class AeBase64EncodeFunction extends AeAbstractBpelFunction
{
   /** Default character set encoding */
   public final static String DEFAULT_CHAR_SET = "UTF-8"; //$NON-NLS-1$
   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "base64Encode"; //$NON-NLS-1$

   /**
    * Constructor
    */
   public AeBase64EncodeFunction()
   {
      super(FUNCTION_NAME);
   }
  
   /**
    * @see org.activebpel.rt.bpel.function.IAeFunction#call(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, java.util.List)
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
   {
      if(aArgs.size() < 1 || aArgs.size() > 2)
         throwFunctionException(INVALID_PARAMS, FUNCTION_NAME);
     
      try
      {
         return (String)Base64.encodeBytes(getStringArg(aArgs,0).getBytes(aArgs.size() == 2 ? getStringArg(aArgs,1) : DEFAULT_CHAR_SET));
      }
      catch (UnsupportedEncodingException ex)
      {
        throw new AeFunctionCallException(ex);
      }
   }
}