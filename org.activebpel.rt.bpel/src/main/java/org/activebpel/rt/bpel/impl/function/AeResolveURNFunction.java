//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeResolveURNFunction.java,v 1.4 2007/09/04 15:51:33 jbik Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the 
//proprietary property of Active Endpoints, Inc.  Viewing or use of 
//this information is prohibited without the express written consent of 
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT 
//is strictly forbidden. Copyright (c) 2002-2007 All rights reserved. 
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function; 

import java.util.List;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.function.AeFunctionCallException;
import org.activebpel.rt.bpel.function.IAeFunctionExecutionContext;
import org.activebpel.rt.bpel.urn.IAeURNResolver;

/**
 * Implements the abx:resolveURN() custom fuction.
 */
public class AeResolveURNFunction extends AeAbstractBpelFunction
{
   /** The name of the function implemented */
   public static final String FUNCTION_NAME = "resolveURN"; //$NON-NLS-1$

   /**
    * Constructor
    */
   public AeResolveURNFunction()
   {
      super(FUNCTION_NAME); 
   }
   
   /**
    * @see org.activebpel.rt.bpel.function.IAeFunction#call(org.activebpel.rt.bpel.function.IAeFunctionExecutionContext, java.util.List)
    */
   public Object call(IAeFunctionExecutionContext aContext, List aArgs) throws AeFunctionCallException
   {
      if(aArgs.size() != 1 )
         throw new AeFunctionCallException(AeMessages.getString("AeResolveURNFunction.ERROR_INCORRECT_ARGS_NUMBER")); //$NON-NLS-1$
      
      IAeURNResolver resolver = aContext.getAbstractBpelObject().getProcess().getEngine().getURNResolver();
      return resolver.getURL(getStringArg(aArgs,0));
   }
}
 