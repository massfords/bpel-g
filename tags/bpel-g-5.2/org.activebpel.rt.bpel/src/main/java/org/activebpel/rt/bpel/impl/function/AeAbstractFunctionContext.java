//$Header: /Development/AEDevelopment/projects/org.activebpel.rt.bpel/src/org/activebpel/rt/bpel/impl/function/AeAbstractFunctionContext.java,v 1.1 2005/06/08 12:50:30 EWittmann Exp $
/////////////////////////////////////////////////////////////////////////////
//PROPRIETARY RIGHTS STATEMENT
//The contents of this file represent confidential information that is the
//proprietary property of Active Endpoints, Inc.  Viewing or use of
//this information is prohibited without the express written consent of
//Active Endpoints, Inc. Removal of this PROPRIETARY RIGHTS STATEMENT
//is strictly forbidden. Copyright (c) 2002-2004 All rights reserved.
/////////////////////////////////////////////////////////////////////////////
package org.activebpel.rt.bpel.impl.function;

import org.activebpel.rt.bpel.AeMessages;
import org.activebpel.rt.bpel.function.IAeFunctionContext;



/**
 * Base class for implementing <code>FunctionContext</code> impls that 
 * implement the <code>IAeFunctionContext</code> interface.
 */
public abstract class AeAbstractFunctionContext implements IAeFunctionContext
{
   // common error messages
   public static final String NO_FUNCTION_FOUND_ERROR = AeMessages.getString("AeAbstractFunctionContext.0"); //$NON-NLS-1$

   /**
    * Convenience method for no function found error message.
    * @param aLocalName
    */
   protected String formatFunctionNotFoundErrorMsg( String aLocalName )
   {
      return org.activebpel.rt.bpel.AeMessages.format(NO_FUNCTION_FOUND_ERROR, aLocalName );      
   }
}
